package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class TagPriorityList implements IPartitionList {

    // Store raw expressions for isEmpty check and potentially for debugging/recompiling.
    private final String rawWhiteListExpression;
    private final String rawBlackListExpression;

    // Compiled predicates for efficient evaluation.
    private final Predicate<List<String>> whiteListPredicate;
    private final Predicate<List<String>> blackListPredicate;

    private final boolean isWhitelistActive;
    private final boolean isBlacklistActive;

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
        this.rawWhiteListExpression = this.removeBlank(whiteListExpression != null ? whiteListExpression : "");
        this.rawBlackListExpression = this.removeBlank(blackListExpression != null ? blackListExpression : "");

        // Compile the expressions using the new parser.
        this.whiteListPredicate = TagExpParserV2.compile(this.rawWhiteListExpression, true);
        this.blackListPredicate = TagExpParserV2.compile(this.rawBlackListExpression, false);

        // Determine if the whitelist should be actively checked.
        // An empty/whitespace-only expression means the whitelist doesn't restrict anything.
        this.isWhitelistActive = !this.rawWhiteListExpression.isBlank();
        this.isBlacklistActive = !this.rawBlackListExpression.isBlank();
    }

    private String removeBlank(String raw) {
        return raw.replaceAll("\\s+", "");
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
        if (this.isWhitelistActive && this.isBlacklistActive) {
            return TagExpParserV2.evaluate(this.whiteListPredicate, input) && TagExpParserV2.evaluate(this.blackListPredicate, input);
        } else if (this.isWhitelistActive) {
            return TagExpParserV2.evaluate(this.whiteListPredicate, input);
        } else if (this.isBlacklistActive) {
            return TagExpParserV2.evaluate(this.blackListPredicate, input);
        } else {
            return true;
        }
    }

}
