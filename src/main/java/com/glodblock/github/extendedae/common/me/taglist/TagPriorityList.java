package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TagPriorityList implements IPartitionList {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

    private final Set<TagKey<?>> whiteSet;
    private final Set<TagKey<?>> blackSet;
    private final String tagExpression;
    private final TagExpParser.Expression parsedExpression;
    
    // Cache isn't fast enough here, so we use a map
    private final Reference2BooleanMap<Object> memory = new Reference2BooleanOpenHashMap<>();

    public TagPriorityList(Set<TagKey<?>> whiteKeys, Set<TagKey<?>> blackKeys, String tagExp) {
        this.whiteSet = whiteKeys;
        this.blackSet = blackKeys;
        this.tagExpression = tagExp;
        this.parsedExpression = TagExpParser.parseExpression(tagExp);
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Created TagPriorityList with expression: '{}'", tagExp);
            LOGGER.info("White tags ({}): {}", whiteKeys.size(), formatTags(whiteKeys));
            LOGGER.info("Black tags ({}): {}", blackKeys.size(), formatTags(blackKeys));
            LOGGER.info("Expression parsed as: {}", parsedExpression.getClass().getSimpleName());
        }
    }

    @Override
    public boolean isListed(AEKey input) {
        Object key = input.getPrimaryKey();
        boolean result = this.memory.computeIfAbsent(key, this::eval);
        
        if (DEBUG_ENABLED) {
            LOGGER.info("isListed check for item {} => {}", input, result ? "MATCHED" : "REJECTED");
        }
        
        return result;
    }

    @Override
    public boolean isEmpty() {
        return this.tagExpression.isEmpty();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }

    @SuppressWarnings("deprecation")
    private boolean eval(@NotNull Object obj) {
        if (DEBUG_ENABLED) {
            LOGGER.info("Evaluating object: {}", obj);
        }
        
        // First check if we have an empty taglist or a taglist with only operators that should match nothing
        if (parsedExpression instanceof TagExpParser.EmptyExpression) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Empty expression, returning false immediately");
            }
            return false;
        }
        
        Holder<?> refer = null;
        if (obj instanceof Item item) {
            refer = item.builtInRegistryHolder();
            if (DEBUG_ENABLED) {
                LOGGER.info("Object is an Item: {}", item.getDescriptionId());
            }
        } else if (obj instanceof Fluid fluid) {
            refer = fluid.builtInRegistryHolder();
            if (DEBUG_ENABLED) {
                LOGGER.info("Object is a Fluid: {}", fluid.toString());
            }
        }
        
        if (refer == null) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Object is neither Item nor Fluid, returning false");
            }
            return false;
        }
        
        // Collect the object's tags for logging
        Set<TagKey<?>> objectTags = new HashSet<>();
        refer.tags().forEach(objectTags::add);
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Object has {} tags: {}", objectTags.size(), formatTags(objectTags));
        }
        
        // Special case: Detect if we're dealing with an AND expression with direct tag expressions
        if (parsedExpression instanceof TagExpParser.AndExpression andExpression) {
            // Extract the required tags from the left and right expressions
            Set<String> requiredTags = andExpression.getRequiredTags();
            
            if (DEBUG_ENABLED) {
                LOGGER.info("AND expression - checking for required tags: {}", requiredTags);
            }
            
            // Check if the item has ALL the required tags
            for (String tagStr : requiredTags) {
                // Skip special tags like wildcard
                if (tagStr.equals("*")) continue;
                
                boolean found = false;
                for (TagKey<?> itemTag : objectTags) {
                    if (itemTag.location().toString().equals(tagStr)) {
                        found = true;
                        if (DEBUG_ENABLED) {
                            LOGGER.info("Found required tag: {}", tagStr);
                        }
                        break;
                    }
                }
                
                if (!found) {
                    if (DEBUG_ENABLED) {
                        LOGGER.info("Missing required tag {}, rejecting item", tagStr);
                    }
                    return false;
                }
            }
            
            // If we reach here, the item has all required tags
            if (DEBUG_ENABLED) {
                LOGGER.info("Item has all required tags, PASSED");
            }
            
            // Still need to check blacklist
            if (!blackSet.isEmpty()) {
                boolean noneMatch = refer.tags().noneMatch(blackSet::contains);
                if (DEBUG_ENABLED) {
                    if (noneMatch) {
                        LOGGER.info("Object PASSED blacklist check - no blacklisted tags");
                    } else {
                        // Log which blacklist tags matched
                        Set<TagKey<?>> matchedBlackTags = new HashSet<>();
                        refer.tags().filter(blackSet::contains).forEach(matchedBlackTags::add);
                        LOGGER.info("Object FAILED blacklist check. Matched blacklist tags: {}", formatTags(matchedBlackTags));
                    }
                }
                return noneMatch;
            }
            
            return true;
        }
        
        // For non-AND expressions, use the original whitelist/blacklist mechanism
        
        // Check whitelist
        boolean pass = true;
        if (!whiteSet.isEmpty()) {
            boolean anyMatch = refer.tags().anyMatch(whiteSet::contains);
            pass = anyMatch;
            
            if (DEBUG_ENABLED) {
                if (pass) {
                    LOGGER.info("Object PASSED whitelist check");
                    // Log which tags matched
                    Set<TagKey<?>> matchedTags = new HashSet<>();
                    refer.tags().filter(whiteSet::contains).forEach(matchedTags::add);
                    LOGGER.info("Matched whitelist tags: {}", formatTags(matchedTags));
                } else {
                    LOGGER.info("Object FAILED whitelist check - no matching tags");
                }
            }
        } else if (tagExpression.isEmpty()) {
            if (DEBUG_ENABLED) {
                LOGGER.info("Whitelist is empty, default pass");
            }
        } else {
            if (DEBUG_ENABLED) {
                LOGGER.info("Empty whitelist with non-empty expression, checking operators");
            }
            
            // If we have a non-empty expression with operators and empty whitelist, return false
            if (tagExpression.contains("&") || tagExpression.contains("|") || tagExpression.contains("^")) {
                if (DEBUG_ENABLED) {
                    LOGGER.info("Expression has operators but whitelist is empty, returning false");
                }
                return false;
            }
        }
        
        // If passes whitelist check, then check blacklist
        if (pass) {
            if (!blackSet.isEmpty()) {
                boolean noneMatch = refer.tags().noneMatch(blackSet::contains);
                
                if (DEBUG_ENABLED) {
                    if (noneMatch) {
                        LOGGER.info("Object PASSED blacklist check - no blacklisted tags");
                    } else {
                        // Log which blacklist tags matched
                        Set<TagKey<?>> matchedBlackTags = new HashSet<>();
                        refer.tags().filter(blackSet::contains).forEach(matchedBlackTags::add);
                        LOGGER.info("Object FAILED blacklist check. Matched blacklist tags: {}", formatTags(matchedBlackTags));
                    }
                }
                
                return noneMatch;
            }
            
            if (DEBUG_ENABLED) {
                LOGGER.info("No blacklist, item PASSED filter");
            }
            return true;
        }
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Item FAILED filter");
        }
        return false;
    }
    
    private String formatTags(Set<TagKey<?>> tags) {
        return tags.stream()
                .map(tag -> tag.location().toString())
                .collect(Collectors.joining(", "));
    }
}
