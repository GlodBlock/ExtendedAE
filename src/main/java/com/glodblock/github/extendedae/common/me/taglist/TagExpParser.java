package com.glodblock.github.extendedae.common.me.taglist;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author brachy84
 */
public final class TagExpParser {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

    private final static ReferenceSet<TagKey<?>> TAGS = new ReferenceOpenHashSet<>();
    private static boolean isInit = false;
    private final static LoadingCache<String, Set<TagKey<?>>> CACHE = CacheBuilder.newBuilder().build(
            new CacheLoader<>() {
                @Override
                public @NotNull Set<TagKey<?>> load(@NotNull String key) {
                    return getMatchingOreInternal(key);
                }
            }
    );

    private static void init() {
        TAGS.addAll(BuiltInRegistries.ITEM.getTagNames().toList());
        TAGS.addAll(BuiltInRegistries.FLUID.getTagNames().toList());
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Initialized TagExpParser with {} available tags", TAGS.size());
        }
    }

    public static Set<TagKey<?>> getMatchingOre(String oreExp) {
        oreExp = validateExp(oreExp);
        if (DEBUG_ENABLED) {
            LOGGER.info("Getting matching tags for expression: '{}'", oreExp);
        }
        Set<TagKey<?>> result = CACHE.getUnchecked(oreExp);
        if (DEBUG_ENABLED) {
            LOGGER.info("Found {} matching tags for '{}': {}", 
                result.size(), 
                oreExp, 
                result.stream().map(tag -> tag.location().toString()).limit(20).toList());
            if (result.size() > 20) {
                LOGGER.info("... and {} more tags", result.size() - 20);
            }
        }
        return result;
    }

    private static Set<TagKey<?>> getMatchingOreInternal(String oreExp) {
        if (oreExp == null || oreExp.trim().isEmpty()) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Empty expression, returning empty tag set");
            }
            return Set.of();
        }
        if (!isInit) {
            init();
            isInit = true;
        }
        
        // Handle special cases that should match nothing
        if (oreExp.trim().equals("&") || oreExp.trim().equals("|") || oreExp.trim().equals("^") || 
            oreExp.trim().matches("^\\s*[&|^].*") || oreExp.trim().matches(".*[&|^]\\s*$")) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Expression '{}' only contains operators or has invalid format, returning empty tag set", oreExp);
            }
            return Set.of(); // Expression with only operators or invalid operators should match nothing
        }
        
        Set<TagKey<?>> matchingIds = new HashSet<>();
        Expression expression = parseExpression(oreExp);
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Parsing '{}' resulted in a {} expression", oreExp, expression.getClass().getSimpleName());
        }
        
        // Special handling for AND expressions
        if (expression instanceof AndExpression) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Expression is an AND - returning an empty set as we'll check tags at item evaluation time");
            }
            // For AND expressions, we don't pre-populate the whitelist
            // Instead, we'll check each item against the required tags at evaluation time
            return Set.of();
        }
        
        // For other expressions, use the standard matching logic
        for (var tag : TAGS) {
            String tagStr = tag.location().toString();
            boolean matches = expression.matches(tagStr);
            if (matches) {
                matchingIds.add(tag);
                if (DEBUG_ENABLED && matchingIds.size() <= 10) {
                    LOGGER.debug("Tag '{}' matched expression '{}'", tagStr, oreExp);
                }
            }
        }
        if (DEBUG_ENABLED) {
            LOGGER.info("Expression '{}' matched {} tags", oreExp, matchingIds.size());
        }
        return matchingIds;
    }

    // Parse an expression to create an expression tree
    public static Expression parseExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Empty expression, returning EmptyExpression");
            }
            return new EmptyExpression();
        }
        
        expression = expression.trim();
        
        // Check for empty expression
        if (expression.isEmpty()) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Trimmed expression is empty, returning EmptyExpression");
            }
            return new EmptyExpression();
        }
        
        // Handle operators at the beginning or end
        if (expression.startsWith("&") || expression.startsWith("|") || expression.startsWith("^") ||
            expression.endsWith("&") || expression.endsWith("|") || expression.endsWith("^")) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Expression '{}' has operators at beginning or end, returning EmptyExpression", expression);
            }
            return new EmptyExpression(); // These should match nothing
        }
        
        // Simple expression without operators
        if (!expression.contains("&") && !expression.contains("|") && !expression.contains("^")) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Expression '{}' has no operators, creating simple TagExpression", expression);
            }
            return new TagExpression(expression);
        }
        
        // Split complex expressions by their operators
        if (DEBUG_ENABLED) {
            LOGGER.info("Parsing complex expression: '{}'", expression);
        }
        return parseComplexExpression(expression);
    }
    
    private static Expression parseComplexExpression(String expr) {
        // First, handle parentheses
        if (expr.contains("(")) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Expression '{}' contains parentheses, handling those first", expr);
            }
            return parseParentheses(expr);
        }
        
        // Process AND operator (&) with highest precedence
        if (expr.contains("&")) {
            String[] parts = expr.split("&", 2);
            if (parts.length == 2) {
                String left = parts[0].trim();
                String right = parts[1].trim();
                
                if (DEBUG_ENABLED) {
                    LOGGER.info("Splitting AND expression '{}' into left '{}' and right '{}'", expr, left, right);
                }
                
                if (left.isEmpty() || right.isEmpty()) {
                    if (DEBUG_ENABLED) {
                        LOGGER.info("AND expression has empty part, returning EmptyExpression");
                    }
                    return new EmptyExpression(); // Invalid expression with empty part
                }
                
                Expression leftExp = parseExpression(left);
                Expression rightExp = parseExpression(right);
                if (DEBUG_ENABLED) {
                    LOGGER.info("Created AND expression with {} on left and {} on right", 
                        leftExp.getClass().getSimpleName(), rightExp.getClass().getSimpleName());
                }
                return new AndExpression(leftExp, rightExp);
            }
        }
        
        // Process OR operator (|) with medium precedence
        if (expr.contains("|")) {
            String[] parts = expr.split("\\|", 2);
            if (parts.length == 2) {
                String left = parts[0].trim();
                String right = parts[1].trim();
                
                if (DEBUG_ENABLED) {
                    LOGGER.info("Splitting OR expression '{}' into left '{}' and right '{}'", expr, left, right);
                }
                
                if (left.isEmpty() || right.isEmpty()) {
                    if (DEBUG_ENABLED) {
                        LOGGER.info("OR expression has empty part, returning EmptyExpression");
                    }
                    return new EmptyExpression(); // Invalid expression with empty part
                }
                
                return new OrExpression(parseExpression(left), parseExpression(right));
            }
        }
        
        // Process XOR operator (^) with lowest precedence
        if (expr.contains("^")) {
            String[] parts = expr.split("\\^", 2);
            if (parts.length == 2) {
                String left = parts[0].trim();
                String right = parts[1].trim();
                
                if (DEBUG_ENABLED) {
                    LOGGER.info("Splitting XOR expression '{}' into left '{}' and right '{}'", expr, left, right);
                }
                
                if (left.isEmpty() || right.isEmpty()) {
                    if (DEBUG_ENABLED) {
                        LOGGER.info("XOR expression has empty part, returning EmptyExpression");
                    }
                    return new EmptyExpression(); // Invalid expression with empty part
                }
                
                return new XorExpression(parseExpression(left), parseExpression(right));
            }
        }
        
        // If we reach here, it's a simple tag expression
        if (DEBUG_ENABLED) {
            LOGGER.info("Expression '{}' is a simple tag pattern", expr);
        }
        return new TagExpression(expr);
    }
    
    private static Expression parseParentheses(String expr) {
        // Find the outermost parentheses
        int level = 0;
        int start = -1;
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') {
                if (level == 0) {
                    start = i;
                }
                level++;
            } else if (c == ')') {
                level--;
                if (level == 0 && start != -1) {
                    // Found matching parentheses
                    String before = expr.substring(0, start).trim();
                    String inside = expr.substring(start + 1, i).trim();
                    String after = expr.substring(i + 1).trim();
                    
                    Expression insideExpr = parseExpression(inside);
                    
                    if (before.isEmpty() && after.isEmpty()) {
                        // Just the parentheses
                        return insideExpr;
                    }
                    
                    // Check for operators before/after the parentheses
                    if (!before.isEmpty() && before.endsWith("&")) {
                        String leftPart = before.substring(0, before.length() - 1).trim();
                        Expression leftExpr = parseExpression(leftPart);
                        
                        if (!after.isEmpty()) {
                            // Handle operator after parentheses
                            if (after.startsWith("&")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new AndExpression(new AndExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("|")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new OrExpression(new AndExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("^")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new XorExpression(new AndExpression(leftExpr, insideExpr), rightExpr);
                            } else {
                                // Invalid format
                                return new EmptyExpression();
                            }
                        } else {
                            // Just & before the parentheses
                            return new AndExpression(leftExpr, insideExpr);
                        }
                    } else if (!before.isEmpty() && before.endsWith("|")) {
                        String leftPart = before.substring(0, before.length() - 1).trim();
                        Expression leftExpr = parseExpression(leftPart);
                        
                        if (!after.isEmpty()) {
                            // Handle operator after parentheses
                            if (after.startsWith("&")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new AndExpression(new OrExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("|")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new OrExpression(new OrExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("^")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new XorExpression(new OrExpression(leftExpr, insideExpr), rightExpr);
                            } else {
                                // Invalid format
                                return new EmptyExpression();
                            }
                        } else {
                            // Just | before the parentheses
                            return new OrExpression(leftExpr, insideExpr);
                        }
                    } else if (!before.isEmpty() && before.endsWith("^")) {
                        String leftPart = before.substring(0, before.length() - 1).trim();
                        Expression leftExpr = parseExpression(leftPart);
                        
                        if (!after.isEmpty()) {
                            // Handle operator after parentheses
                            if (after.startsWith("&")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new AndExpression(new XorExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("|")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new OrExpression(new XorExpression(leftExpr, insideExpr), rightExpr);
                            } else if (after.startsWith("^")) {
                                String rightPart = after.substring(1).trim();
                                Expression rightExpr = parseExpression(rightPart);
                                return new XorExpression(new XorExpression(leftExpr, insideExpr), rightExpr);
                            } else {
                                // Invalid format
                                return new EmptyExpression();
                            }
                        } else {
                            // Just ^ before the parentheses
                            return new XorExpression(leftExpr, insideExpr);
                        }
                    } else {
                        // Invalid format or no operator
                        return new EmptyExpression();
                    }
                }
            }
        }
        
        // Unbalanced parentheses
        return new EmptyExpression();
    }

    private static String validateExp(String input) {
        if (input == null || input.trim().isEmpty()) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Validation: input is null or empty");
            }
            return "";
        }
        
        String original = input;
        
        // Normalize spaces
        input = input.trim().replaceAll("\\s+", " ");
        
        // Remove multiple consecutive operators
        input = input.replaceAll("&{2,}", "&");
        input = input.replaceAll("\\|{2,}", "|");
        input = input.replaceAll("\\^{2,}", "^");
        input = input.replaceAll("\\*{2,}", "*");
        
        if (DEBUG_ENABLED && !original.equals(input)) {
            LOGGER.info("Validation: normalized '{}' to '{}'", original, input);
        }
        
        return input;
    }
    
    // Base expression interface
    public interface Expression {
        boolean matches(String tag);
        Set<String> getRequiredTags();
    }
    
    // Empty expression - matches nothing
    public static class EmptyExpression implements Expression {
        @Override
        public boolean matches(String tag) {
            return false;
        }
        
        @Override
        public Set<String> getRequiredTags() {
            return Set.of();
        }
    }
    
    // AND expression
    public static class AndExpression implements Expression {
        private final Expression left;
        private final Expression right;
        private final Set<String> requiredTags;
        
        public AndExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
            
            // Collect all required tags from both expressions
            this.requiredTags = new HashSet<>();
            this.requiredTags.addAll(left.getRequiredTags());
            this.requiredTags.addAll(right.getRequiredTags());
            
            if (DEBUG_ENABLED) {
                LOGGER.info("AND Expression created with required tags: {}", 
                    String.join(", ", requiredTags));
            }
        }
        
        @Override
        public boolean matches(String tag) {
            boolean leftMatch = left.matches(tag);
            boolean rightMatch = right.matches(tag);
            boolean result = leftMatch && rightMatch;
            
            if (DEBUG_ENABLED && tag.contains("tools") || tag.contains("enchantables")) {
                LOGGER.debug("AND match for tag '{}': left={}, right={}, result={}", 
                    tag, leftMatch, rightMatch, result);
            }
            
            return result;
        }
        
        @Override
        public Set<String> getRequiredTags() {
            return requiredTags;
        }
    }
    
    // OR expression
    public static class OrExpression implements Expression {
        private final Expression left;
        private final Expression right;
        
        public OrExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean matches(String tag) {
            boolean leftMatch = left.matches(tag);
            boolean rightMatch = right.matches(tag);
            boolean result = leftMatch || rightMatch;
            
            if (DEBUG_ENABLED) {
                LOGGER.debug("OR match for tag '{}': left={}, right={}, result={}", 
                    tag, leftMatch, rightMatch, result);
            }
            
            return result;
        }
        
        @Override
        public Set<String> getRequiredTags() {
            Set<String> tags = new HashSet<>(left.getRequiredTags());
            tags.addAll(right.getRequiredTags());
            return tags;
        }
    }
    
    // XOR expression
    public static class XorExpression implements Expression {
        private final Expression left;
        private final Expression right;
        
        public XorExpression(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public boolean matches(String tag) {
            boolean leftMatches = left.matches(tag);
            boolean rightMatches = right.matches(tag);
            boolean result = leftMatches != rightMatches;
            
            if (DEBUG_ENABLED) {
                LOGGER.debug("XOR match for tag '{}': left={}, right={}, result={}", 
                    tag, leftMatches, rightMatches, result);
            }
            
            return result;
        }
        
        @Override
        public Set<String> getRequiredTags() {
            Set<String> tags = new HashSet<>(left.getRequiredTags());
            tags.addAll(right.getRequiredTags());
            return tags;
        }
    }
    
    // Tag pattern matcher
    public static class TagExpression implements Expression {
        private final String pattern;
        private final boolean hasWildcard;
        private final Pattern regex;
        
        public TagExpression(String pattern) {
            this.pattern = pattern;
            this.hasWildcard = pattern.contains("*");
            
            if (hasWildcard) {
                // Convert wildcard pattern to regex
                String regexPattern = Pattern.quote(pattern).replace("*", "\\E.*\\Q");
                if (pattern.startsWith("*")) {
                    regexPattern = ".*" + regexPattern.substring(4);
                }
                if (pattern.endsWith("*")) {
                    regexPattern = regexPattern.substring(0, regexPattern.length() - 4) + ".*";
                }
                this.regex = Pattern.compile(regexPattern);
                
                if (DEBUG_ENABLED) {
                    LOGGER.info("Created wildcard pattern: '{}' => regex: '{}'", pattern, regexPattern);
                }
            } else {
                this.regex = null;
                
                if (DEBUG_ENABLED) {
                    LOGGER.info("Created exact match pattern: '{}'", pattern);
                }
            }
        }
        
        @Override
        public boolean matches(String tag) {
            if (pattern.equals("*")) {
                return true;
            }
            
            boolean result;
            if (hasWildcard) {
                result = regex.matcher(tag).matches();
            } else {
                result = tag.equals(pattern);
            }
            
            if (DEBUG_ENABLED && (tag.contains("tools") || tag.contains("enchantables") || result)) {
                LOGGER.debug("Pattern '{}' match for tag '{}': {}", pattern, tag, result);
            }
            
            return result;
        }
        
        @Override
        public Set<String> getRequiredTags() {
            // Only return a concrete tag if this is not a wildcard pattern
            if (!hasWildcard) {
                return Set.of(pattern);
            }
            return Set.of();
        }
    }
}
