---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Threshold Level Emitter
    icon: extendedae:threshold_level_emitter
categories:
- extended devices
item_ids:
- extendedae:threshold_level_emitter
---

# ME Threshold Level Emitter

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_level_emitter.snbt"></ImportStructure>
</GameScene>

It works like a reset-set latch. It turns off its redstone signal when the quantity of an item in the network is less than
the lower threshold and turns it on when the quantity is greater than the upper threshold.

For example, suppose the lower threshold is set to 100 and the upper threshold is set to 150.

At first, the network is empty, so the emitter is inactive.

When the quantity of the item increases above 150, the emitter sends a redstone signal.

When the quantity decreases below 150, the emitter continues to send the signal.

When the quantity falls below 100, the emitter turns off.
