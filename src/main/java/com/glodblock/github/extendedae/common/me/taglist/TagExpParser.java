package com.glodblock.github.extendedae.common.me.taglist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import appeng.api.stacks.AEKey;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Parses and evaluates tag query expressions with support for logical operators
 * (&, |, !, ^), parentheses, and wildcards (*).
 * <p>
 * The parser uses the Shunting-Yard algorithm to convert infix expressions
 * to Reverse Polish Notation (RPN), allowing for correct operator precedence
 * and associativity. The RPN expression is then evaluated against a set of tags.
 * <p>
 * Example: "(minecraft:logs & !minecraft:planks) | forge:ores/*"
 */
public final class TagExpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

    // Cache compiled predicates for efficiency.
    private final static LoadingCache<String, Predicate<Set<String>>> COMPILED_EXPRESSION_CACHE = CacheBuilder.newBuilder()
            .maximumSize(512) // Sensible cache size limit
            .build(new CacheLoader<>() {
                @Override
                public @NotNull Predicate<Set<String>> load(@NotNull String key) {
                    return compileInternal(key);
                }
            });

    // --- Public API ---

    /**
     * Compiles a tag expression string into a reusable predicate.
     * The predicate accepts a set of tag strings (e.g., ["minecraft:logs", "forge:ores/coal"])
     * and returns true if the tags match the expression.
     * <p>
     * Compiled expressions are cached.
     *
     * @param expression The tag expression string (e.g., "tag1 & (tag2 | tag3)")
     * @return A predicate for evaluating the expression against tag sets. Returns a predicate
     * that always evaluates to `true` if the expression is empty or whitespace.
     */
    public static Predicate<Set<String>> compile(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            // An empty expression matches everything (or could be interpreted as matching nothing,
            // but matching everything is often more useful for filters where empty means "no filter").
            // Let's define empty as matching *nothing* for consistency with how filters usually work.
            // If you want "match all", use "*".
            return tags -> false;
        }
        return COMPILED_EXPRESSION_CACHE.getUnchecked(expression);
    }

    /**
     * Evaluates a pre-compiled expression predicate against the tags of a given AEKey's primary object (Item or Fluid).
     *
     * @param predicate The compiled expression predicate obtained from {@link #compile(String)}.
     * @param key       The AEKey representing the item or fluid.
     * @return True if the key's tags match the expression, false otherwise.
     */
    @SuppressWarnings("deprecation") // Holder::tags is deprecated but necessary here.
    public static boolean evaluate(Predicate<Set<String>> predicate, AEKey key) {
        Object primaryKey = key.getPrimaryKey();
        Holder<?> holder = null;
        if (primaryKey instanceof Item item) {
            holder = item.builtInRegistryHolder();
        } else if (primaryKey instanceof Fluid fluid) {
            holder = fluid.builtInRegistryHolder();
        }

        if (holder != null) {
            // Convert TagKey objects to their string representation for matching.
            Set<String> tagStrings = holder.tags()
                    .map(tagKey -> tagKey.location().toString())
                    .collect(Collectors.toSet());
            return predicate.test(tagStrings);
        }

        return false; // Cannot evaluate if not an Item or Fluid with tags.
    }


    // --- Internal Implementation ---

    private static Predicate<Set<String>> compileInternal(String expression) {
        try {
            List<Token> tokens = tokenize(expression);
            Queue<Token> rpn = convertToRPN(tokens);
            // Return a lambda that captures the RPN queue and evaluates it.
            return actualTags -> evaluateRPN(rpn, actualTags);
        } catch (IllegalArgumentException e) {
            // Log error or handle gracefully? For now, return a predicate that always fails.
            System.err.println("Failed to parse tag expression: '" + expression + "' - " + e.getMessage());
            return tags -> false; // Expression is invalid, so it matches nothing.
        }
    }

    // --- Tokenizer ---

    private enum TokenType { TAG, OPERATOR, LPAREN, RPAREN }

    private record Token(TokenType type, String value, Operator op) {
        Token(TokenType type, String value) { this(type, value, null); } // For TAG, LPAREN, RPAREN
        Token(Operator op) { this(TokenType.OPERATOR, op.symbol, op); } // For OPERATOR
    }

    // Define operators with precedence and associativity.
    // Higher precedence value means it binds tighter.
    // NOT has highest, then AND, then XOR, then OR.
    private enum Operator {
        NOT("!", 3, true),   // Right associative (though usually unary)
        AND("&", 2, false),  // Left associative
        XOR("^", 1, false),  // Left associative
        OR("|", 0, false);   // Left associative

        final String symbol;
        final int precedence;
        final boolean rightAssociative;

        Operator(String symbol, int precedence, boolean rightAssociative) {
            this.symbol = symbol;
            this.precedence = precedence;
            this.rightAssociative = rightAssociative;
        }

        static Operator fromSymbol(char symbol) {
            for (Operator op : values()) {
                if (op.symbol.charAt(0) == symbol) {
                    return op;
                }
            }
            return null;
        }
    }

    /**
     * Converts an infix expression string into a list of tokens.
     * Handles tags (including wildcards), operators (&, |, !, ^), and parentheses.
     * Ignores whitespace.
     */
    private static List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentTag = new StringBuilder();
        boolean expectingOperand = true; // Start expects an operand (tag or '(' or '!')

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                continue; // Skip whitespace
            }

            Operator op = Operator.fromSymbol(c);

            if (c == '(') {
                if (!expectingOperand) {
                    throw new IllegalArgumentException("Unexpected '(' at position " + i + ". Expected operator or ')'.");
                }
                flushTag(currentTag, tokens); // Previous tag finished
                tokens.add(new Token(TokenType.LPAREN, "("));
                expectingOperand = true; // After '(', expect an operand or '!'
            } else if (c == ')') {
                if (expectingOperand && !tokens.isEmpty() && tokens.get(tokens.size()-1).type != TokenType.LPAREN) {
                    // Check if the state allows ')' (must follow an operand)
                    throw new IllegalArgumentException("Unexpected ')' at position " + i + ". Expected operand or '('.");
                 }
                flushTag(currentTag, tokens); // Finish any tag before ')'
                tokens.add(new Token(TokenType.RPAREN, ")"));
                expectingOperand = false; // After ')', expect an operator or end of expression
            } else if (op != null) {
                // Handle unary NOT vs binary operators
                if (op == Operator.NOT && expectingOperand) {
                    // Unary NOT operator
                     flushTag(currentTag, tokens); // Ensure no tag is being built
                     tokens.add(new Token(op));
                     // Still expecting an operand after '!'
                     expectingOperand = true;
                } else if (op != Operator.NOT && !expectingOperand) {
                    // Binary AND, OR, XOR operator
                    flushTag(currentTag, tokens); // Finish tag before operator
                    tokens.add(new Token(op));
                    expectingOperand = true; // Expect operand after binary operator
                 } else {
                     // Operator in wrong place (e.g., "tag1 && tag2", "tag1 | | tag2", or starting with binary op)
                     throw new IllegalArgumentException("Unexpected operator '" + c + "' at position " + i + ".");
                 }
            } else {
                 // Part of a tag name (including namespace, path, '*', ':')
                 if (!expectingOperand) {
                     throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + i + ". Expected operator or ')'.");
                 }
                currentTag.append(c);
            }
        }

        flushTag(currentTag, tokens); // Flush any remaining tag

        // Check if the expression is valid
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty.");
        }
        
        // If the last token is a tag, we're good. If it's an operator (especially binary op), that's invalid.
        if (expectingOperand && 
            tokens.get(tokens.size()-1).type != TokenType.TAG && 
            tokens.get(tokens.size()-1).type != TokenType.RPAREN) {
            throw new IllegalArgumentException("Expression ended unexpectedly. Expected operand after last token.");
        }

        return tokens;
    }

    // Helper to add a tag token if buffer is not empty
    private static void flushTag(StringBuilder currentTag, List<Token> tokens) {
        if (!currentTag.isEmpty()) {
            tokens.add(new Token(TokenType.TAG, currentTag.toString()));
            currentTag.setLength(0); // Clear buffer
            // after a tag, we expect an operator or ')'
           // expectingOperand = false; // This state change is handled in the main loop logic now
        }
    }


    // --- Shunting-Yard Algorithm ---

    /**
     * Converts a list of infix tokens to a queue of postfix (RPN) tokens.
     * Uses the Shunting-Yard algorithm.
     *
     * This algorithm processes tokens one by one. Operands (tags) are added directly
     * to the output queue. Operators are pushed onto a temporary stack, considering
     * precedence rules. Lower precedence operators on the stack are popped to the output
     * before pushing a higher precedence operator. Parentheses are used to manage scope,
     * ensuring operators within them are evaluated first by popping them off the stack
     * when a closing parenthesis is encountered.
     */
    private static Queue<Token> convertToRPN(List<Token> tokens) {
        Queue<Token> outputQueue = new LinkedList<>();
        Deque<Token> operatorStack = new ArrayDeque<>(); // Use Deque as stack

        for (Token token : tokens) {
            switch (token.type) {
                case TAG:
                    outputQueue.offer(token);
                    break;
                case OPERATOR:
                    // Handle operator precedence and associativity
                    while (!operatorStack.isEmpty() && operatorStack.peek().type == TokenType.OPERATOR) {
                        Token topOpToken = operatorStack.peek();
                        Operator currentOp = token.op;
                        Operator topOp = topOpToken.op;

                        // Check precedence and associativity
                        if ((!currentOp.rightAssociative && currentOp.precedence <= topOp.precedence) ||
                            (currentOp.rightAssociative && currentOp.precedence < topOp.precedence)) {
                            outputQueue.offer(operatorStack.pop());
                        } else {
                            break; // Stop popping
                        }
                    }
                    operatorStack.push(token);
                    break;
                case LPAREN:
                    operatorStack.push(token);
                    break;
                case RPAREN:
                    // Pop operators until matching LPAREN is found
                    boolean foundParen = false;
                    while (!operatorStack.isEmpty()) {
                        Token topToken = operatorStack.peek();
                        if (topToken.type == TokenType.LPAREN) {
                            operatorStack.pop(); // Discard LPAREN
                            foundParen = true;
                            break;
                        } else {
                            outputQueue.offer(operatorStack.pop());
                        }
                    }
                    if (!foundParen) {
                        throw new IllegalArgumentException("Mismatched parentheses: Closing parenthesis without matching opening parenthesis.");
                    }
                    break;
            }
        }

        // Pop any remaining operators from the stack to the output queue
        while (!operatorStack.isEmpty()) {
            Token topToken = operatorStack.peek();
            if (topToken.type == TokenType.LPAREN) {
                throw new IllegalArgumentException("Mismatched parentheses: Opening parenthesis without matching closing parenthesis.");
            }
             if (topToken.type == TokenType.OPERATOR) {
                outputQueue.offer(operatorStack.pop());
             } else {
                 // Should not happen if tokenization and previous logic is correct
                 throw new IllegalStateException("Unexpected token type on operator stack: " + topToken.type);
             }
        }

        return outputQueue;
    }

    // --- RPN Evaluator ---

    /**
     * Evaluates a queue of RPN tokens against a set of actual tags.
     *
     * @param rpnQueue   The RPN token queue.
     * @param actualTags The set of tag strings the item/fluid actually has.
     * @return True if the expression matches the tags, false otherwise.
     */
    private static boolean evaluateRPN(Queue<Token> rpnQueue, Set<String> actualTags) {
        Deque<Boolean> valueStack = new ArrayDeque<>();
        // Create a copy to not consume the original queue if it needs to be reused
        Queue<Token> queueCopy = new LinkedList<>(rpnQueue);

        while (!queueCopy.isEmpty()) {
            Token token = queueCopy.poll();

            if (token.type == TokenType.TAG) {
                // Check if any actual tag matches the pattern in the token
                boolean match = actualTags.stream().anyMatch(tag -> matchesWildcard(token.value, tag));
                valueStack.push(match);
            } else if (token.type == TokenType.OPERATOR) {
                Operator op = token.op;
                try {
                    if (op == Operator.NOT) {
                        if (valueStack.isEmpty()) throw new IllegalArgumentException("Invalid expression: NOT operator requires one operand.");
                        boolean operand = valueStack.pop();
                        valueStack.push(!operand);
                    } else {
                        // Binary operators (AND, OR, XOR)
                         if (valueStack.size() < 2) throw new IllegalArgumentException("Invalid expression: Binary operator '" + op.symbol + "' requires two operands.");
                        boolean right = valueStack.pop();
                        boolean left = valueStack.pop();
                        switch (op) {
                            case AND: valueStack.push(left && right); break;
                            case OR:  valueStack.push(left || right); break;
                            case XOR: valueStack.push(left ^ right); break;
                            default: throw new IllegalStateException("Unexpected binary operator: " + op); // Should not happen
                        }
                    }
                } catch (NoSuchElementException e) {
                    // This catches errors if pop() is called on an empty stack
                    throw new IllegalArgumentException("Invalid RPN expression: Not enough operands for operator '" + op.symbol + "'.");
                }
            } else {
                 // LPAREN/RPAREN should not be in the RPN queue
                 throw new IllegalStateException("Unexpected token type in RPN queue: " + token.type);
            }
        }

        // The final result should be the only value left on the stack
        if (valueStack.size() == 1) {
            return valueStack.pop();
        } else {
            // If stack is empty or has multiple values, the expression was malformed
             if (valueStack.isEmpty() && rpnQueue.isEmpty()) return false; // Empty expression evaluates to false
            throw new IllegalArgumentException("Invalid RPN expression: Evaluation finished with " + valueStack.size() + " values on the stack (expected 1).");
        }
    }

    // --- Wildcard Matching ---

    /**
     * Checks if a pattern string matches a text string, allowing for simple wildcards.
     * A single asterisk (*) matches any sequence of characters in the text.
     */
    private static boolean matchesWildcard(@NotNull String pattern, @NotNull String text) {
        // Fast path for exact match or simple wildcard
        if (pattern.equals("*") || pattern.equals(text)) {
            return true;
        }

        // Escape regex special chars except * which we convert to .*
        String regex = pattern
                .replace(".", "\\.")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("?", "\\?")
                .replace("+", "\\+")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("|", "\\|")
                .replace("*", ".*");

        return text.matches(regex);
    }
}
