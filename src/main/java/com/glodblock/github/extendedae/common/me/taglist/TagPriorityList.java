package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TagPriorityList implements IPartitionList {

    // Store raw expressions for isEmpty check and potentially for debugging/recompiling.
    private final String rawWhiteListExpression;
    private final String rawBlackListExpression;

    // Compiled predicates for efficient evaluation.
    private final Predicate<Set<String>> whiteListPredicate;
    private final Predicate<Set<String>> blackListPredicate;

    private final boolean isWhitelistActive;

    // Cache results per AEKey instance for performance.
    // Using AEKey directly handles potential variations within the same item/fluid primary key.
    private final Reference2BooleanMap<Object> memory = new Reference2BooleanOpenHashMap<>();

    /**
     * Creates a tag-based partition list using complex filter expressions.
     *
     * @param whiteListExpression The expression for the whitelist (e.g., "forge:ingots & !forge:ingots/iron").
     *                            If empty or null, the whitelist is inactive.
     * @param blackListExpression The expression for the blacklist (e.g., "minecraft:logs | minecraft:planks").
     */
    public TagPriorityList(String whiteListExpression, String blackListExpression) {
        this.rawWhiteListExpression = whiteListExpression != null ? whiteListExpression : "";
        this.rawBlackListExpression = blackListExpression != null ? blackListExpression : "";

        // Compile the expressions using the new parser.
        this.whiteListPredicate = TagExpParser.compile(this.rawWhiteListExpression);
        this.blackListPredicate = TagExpParser.compile(this.rawBlackListExpression);

        // Determine if the whitelist should be actively checked.
        // An empty/whitespace-only expression means the whitelist doesn't restrict anything.
        this.isWhitelistActive = !this.rawWhiteListExpression.isBlank();
    }

    @Override
    public boolean isListed(AEKey input) {
        // empty filter pass all inputs
        if (this.isEmpty()) {
            return true;
        }
        // computeIfAbsent ensures eval is called only once per key.
        return this.memory.computeIfAbsent(input.getPrimaryKey(), this::eval);
    }

    @Override
    public boolean isEmpty() {
        // The filter is considered empty if neither a whitelist nor a blacklist expression is provided.
        return rawWhiteListExpression.isBlank() && rawBlackListExpression.isBlank();
    }

    @Override
    public Iterable<AEKey> getItems() {
        // This partition list dynamically evaluates tags, it doesn't hold a predefined list of items.
        return List.of();
    }

    /**
     * Evaluates if the given Primary Key matches the filter rules (whitelist/blacklist).
     * This method is called by the caching mechanism in `isListed`.
     *
     * @param input The Primary Key (Item or Fluid key) to evaluate.
     * @return True if the key passes the filter rules, false otherwise.
     */
    private boolean eval(@NotNull Object input) {
        // Evaluate both predicates against the key's tags.
        // TagExpParser.evaluate handles getting the tags from the key.
        final boolean whiteMatches = TagExpParser.evaluate(this.whiteListPredicate, input);
        final boolean blackMatches = TagExpParser.evaluate(this.blackListPredicate, input);

        // Apply standard filter logic:
        // - If whitelist is active, must match whitelist AND NOT match blacklist.
        // - If whitelist is inactive, must NOT match blacklist.
        if (this.isWhitelistActive) {
            return whiteMatches && !blackMatches;
        } else {
            return !blackMatches;
        }
    }

}
