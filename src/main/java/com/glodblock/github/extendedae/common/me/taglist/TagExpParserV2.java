package com.glodblock.github.extendedae.common.me.taglist;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagExpParserV2 {

    private final static LoadingCache<String, Predicate<Input>> COMPILED_EXPRESSION_CACHE = CacheBuilder.newBuilder()
            .maximumSize(512) // Sensible cache size limit
            .build(new CacheLoader<>() {
                @Override
                public @NotNull Predicate<Input> load(@NotNull String key) {
                    return compileInternal(key);
                }
            });

    public static Predicate<List<String>> compile(String expression, boolean whitelist) {
        var exp = COMPILED_EXPRESSION_CACHE.getUnchecked(expression);
        return s -> exp.test(new Input(s, whitelist));
    }

    @SuppressWarnings("deprecation")
    public static boolean evaluate(Predicate<List<String>> expression, Object key) {
        Holder<?> holder = null;
        Holder<?> assocateHolder = null;
        if (key instanceof Item item) {
            holder = item.builtInRegistryHolder();
            if (item instanceof BlockItem block) {
                assocateHolder = block.getBlock().builtInRegistryHolder();
            }
        } else if (key instanceof Fluid fluid) {
            holder = fluid.builtInRegistryHolder();
        }
        if (holder != null) {
            List<String> tagStrings = Stream.concat(holder.tags(), assocateHolder == null ? Stream.empty() : assocateHolder.tags())
                    .map(tagKey -> tagKey.location().toString())
                    .collect(Collectors.toList());
            holder.unwrapKey().ifPresent(resourceKey -> tagStrings.add(resourceKey.location().toString()));
            return expression.test(tagStrings);
        }
        return false;
    }

    private static Predicate<Input> compileInternal(String pattern) {
        pattern = removeExtraSyb(pattern.trim());
        var tokens = tokenize(pattern);
        if (tokens.isEmpty()) {
            return i -> false;
        }
        var rpn = convertToRPN(tokens);
        if (rpn.isEmpty()) {
            return i -> false;
        }
        return input -> {
            int evalCode = eval(input.input(), rpn);
            if (evalCode == 0) {
                return false;
            } else {
                if (input.whitelist()) {
                    return evalCode == 1;
                } else {
                    return evalCode == -1;
                }
            }
        };
    }

    // 1: true, -1: false, 0: invalid expression
    private static int eval(List<String> input, List<Token> rpn) {
        Stack<BooleanSupplier> stats = new Stack<>();
        for (var exp : rpn) {
            if (exp.kind() == Kind.TAG) {
                stats.push(() -> checkTag(input, exp.content));
            } else if (exp.kind() == Kind.BI_OP) {
                if (stats.size() < 2) {
                    if (EAEConfig.debugMode) {
                        ExtendedAE.LOGGER.error("Missing tag token for operator {} in {}, at least 2.", exp, rpn);
                    }
                    return 0;
                } else {
                    var s1 = stats.pop();
                    var s2 = stats.pop();
                    var op = exp.op();
                    if (op == Op.OR) {
                        if (s1.getAsBoolean() || s2.getAsBoolean()) {
                            stats.push(() -> true);
                        } else {
                            stats.push(() -> false);
                        }
                    } else if (op == Op.AND) {
                        if (s1.getAsBoolean() && s2.getAsBoolean()) {
                            stats.push(() -> true);
                        } else {
                            stats.push(() -> false);
                        }
                    } else if (op == Op.XOR) {
                        if (s1.getAsBoolean() ^ s2.getAsBoolean()) {
                            stats.push(() -> true);
                        } else {
                            stats.push(() -> false);
                        }
                    } else {
                        if (EAEConfig.debugMode) {
                            ExtendedAE.LOGGER.error("Unknown operator token {} in {}", exp, rpn);
                        }
                        return 0;
                    }
                }
            } else if (exp.kind() == Kind.UNI_OP) {
                if (stats.isEmpty()) {
                    if (EAEConfig.debugMode) {
                        ExtendedAE.LOGGER.error("Missing tag token for operator {} in {}, at least 1.", exp, rpn);
                    }
                    return 0;
                } else {
                    var s1 = stats.pop();
                    stats.push(() -> !s1.getAsBoolean());
                }
            } else {
                if (EAEConfig.debugMode) {
                    ExtendedAE.LOGGER.error("Unknown token {} in {}", exp, rpn);
                }
                return 0;
            }
        }
        if (stats.size() != 1) {
            if (EAEConfig.debugMode) {
                ExtendedAE.LOGGER.error("RPN expression {}'s operators and tags don't match", rpn);
            }
            return 0;
        } else {
            return stats.pop().getAsBoolean() ? 1 : -1;
        }
    }

    private static String removeExtraSyb(String expression) {
        return expression.replace("&&", "&").replace("||", "|");
    }

    private static List<Token> convertToRPN(List<Token> tokens) {
        var previous = new Token(Kind.START, null, null);
        List<Token> rpn = new ArrayList<>(tokens.size());
        Stack<Token> ops = new Stack<>();
        for (var token : tokens) {
            if (isValidToken(previous.kind(), token.kind())) {
                previous = token;
                var kind = token.kind();
                if (kind == Kind.TAG) {
                    rpn.add(token);
                } else if (kind == Kind.BI_OP || kind == Kind.UNI_OP) {
                    while (true) {
                        if (ops.isEmpty()) {
                            ops.push(token);
                            break;
                        } else {
                            var top = ops.peek();
                            var topOp = top.op();
                            // A parenthesis
                            if (topOp == null) {
                                ops.push(token);
                                break;
                            } else {
                                if (topOp.priority < token.op().priority || (token.op().rightAss && topOp.priority == token.op().priority)) {
                                    ops.push(token);
                                    break;
                                } else {
                                    rpn.add(ops.pop());
                                }
                            }
                        }
                    }
                } else if (kind == Kind.LP) {
                    ops.push(token);
                } else if (kind == Kind.RP) {
                    while (true) {
                        if (ops.isEmpty()) {
                            if (EAEConfig.debugMode) {
                                ExtendedAE.LOGGER.error("Find unmatch ')' in {}", tokens);
                            }
                            return List.of();
                        }
                        var top = ops.pop();
                        if (top.kind() == Kind.LP) {
                            break;
                        } else {
                            rpn.add(top);
                        }
                    }
                } else if (kind == Kind.END) {
                    while (!ops.isEmpty()) {
                        var top = ops.pop();
                        if (top.kind() == Kind.LP) {
                            if (EAEConfig.debugMode) {
                                ExtendedAE.LOGGER.error("Find unmatch '(' in {}", tokens);
                            }
                            return List.of();
                        } else {
                            rpn.add(top);
                        }
                    }
                } else {
                    if (EAEConfig.debugMode) {
                        ExtendedAE.LOGGER.error("Find unknown token: {} in {}", token, tokens);
                    }
                    return List.of();
                }
            } else {
                if (EAEConfig.debugMode) {
                    ExtendedAE.LOGGER.error("Find invalid token: {} after token: {}", token, previous);
                }
                return List.of();
            }
        }
        return rpn;
    }

    enum Kind {
        UNI_OP, BI_OP, TAG, LP, RP, START, END
    }

    enum Op {
        NOT(3, true), AND(2, false), OR(0, false), XOR(1, false);

        final int priority;
        final boolean rightAss;

        Op(int priority, boolean rightAss) {
            this.priority = priority;
            this.rightAss = rightAss;
        }

    }

    private static List<Token> tokenize(String input) {
        input = input + " ";
        List<Token> tokens = new ArrayList<>();
        StringBuilder tagBuilder = new StringBuilder();
        for (int x = 0; x < input.length(); x++) {
            char c = input.charAt(x);
            if (isValidTagChar(c)) {
                tagBuilder.append(c);
                continue;
            } else {
                if (!tagBuilder.isEmpty()) {
                    var result = Token.parse(tagBuilder.toString());
                    if (result.ok()) {
                        tokens.add(result.data);
                    } else {
                        if (EAEConfig.debugMode) {
                            ExtendedAE.LOGGER.error("Failed to parse tag expression: '{}' - {}", input, result.message);
                        }
                        return List.of();
                    }
                    tagBuilder = new StringBuilder();
                }
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '&' || c == '|' || c == '^' || c == '!' || c == '(' || c == ')') {
                var result = Token.parse(c);
                if (result.ok()) {
                    tokens.add(result.data);
                } else {
                    if (EAEConfig.debugMode) {
                        ExtendedAE.LOGGER.error("Failed to parse operator expression: '{}' - {}", input, result.message);
                    }
                    return List.of();
                }
            }
        }
        tokens.add(new Token(Kind.END, null, null));
        return tokens;
    }

    private static boolean isValidToken(Kind current, Kind next) {
        return switch (current) {
            case UNI_OP, BI_OP, LP -> next == Kind.UNI_OP || next == Kind.LP || next == Kind.TAG;
            case TAG, RP -> next == Kind.BI_OP || next == Kind.RP || next == Kind.END;
            case START -> next == Kind.UNI_OP || next == Kind.TAG || next == Kind.LP || next == Kind.END;
            case END -> false;
        };
    }

    record Token(Kind kind, Op op, String content) {

        static TryResult<Token> parse(char c) {
            return parse(String.valueOf(c));
        }

        static TryResult<Token> parse(String str) {
            return switch (str) {
                case "&" -> TryResult.success(new Token(Kind.BI_OP, Op.AND, null));
                case "|" -> TryResult.success(new Token(Kind.BI_OP, Op.OR, null));
                case "^" -> TryResult.success(new Token(Kind.BI_OP, Op.XOR, null));
                case "!" -> TryResult.success(new Token(Kind.UNI_OP, Op.NOT, null));
                case "(" -> TryResult.success(new Token(Kind.LP, null, null));
                case ")" -> TryResult.success(new Token(Kind.RP, null, null));
                default -> {
                    if (isValidTagString(str)) {
                        yield TryResult.success(new Token(Kind.TAG, null, str));
                    } else {
                        yield TryResult.fail("Invalid tag: " + str);
                    }
                }
            };
        }

    }

    private static boolean isValidTagString(String str) {
        for (int x = 0; x < str.length(); x++) {
            if (!isValidTagChar(str.charAt(x))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidTagChar(char ch) {
        return Identifier.validPathChar(ch) || ch == ':' || ch == '*';
    }

    private static boolean checkTag(List<String> input, String pattern) {
        return input.stream().anyMatch(s -> {
            if (pattern.equals("*") || pattern.equals(s)) {
                return true;
            }
            return s.matches(pattern.replace("*", ".*"));
        });
    }

    record TryResult<T>(boolean ok, T data, String message) {

        static <T> TryResult<T> success(T data) {
            return new TryResult<>(true, data, null);
        }

        static <T> TryResult<T> fail(String error) {
            return new TryResult<>(false, null, error);
        }

    }

    record Input(List<String> input, boolean whitelist) {}

}
