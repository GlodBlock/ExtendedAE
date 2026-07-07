---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Threshold Level Emitter
    icon: expatternprovider:threshold_level_emitter
categories:
- extended devices
item_ids:
- expatternprovider:threshold_level_emitter
---

# ME Threshold Level Emitter

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_level_emitter.snbt"></ImportStructure>
</GameScene>

It works like a reset-set latch. It turns off the redstone signal when the quantity of an item in the network is less than
the lower threshold and turns it on when the quantity is greater than the upper threshold.

For example, suppose the lower threshold is set to 100 and the upper threshold is set to 150.

At first the network is empty, so the emitter won't be active.

As the item quantity grows past 150, the emitter will send a redstone signal.

When the quantity declines below 150, the emitter still sends a signal.

Finally, when the quantity falls below 100, the emitter will turn off.
