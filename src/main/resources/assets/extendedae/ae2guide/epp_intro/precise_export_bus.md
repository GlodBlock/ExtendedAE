---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Precise Export Bus
    icon: extendedae:precise_export_bus
categories:
- extended devices
item_ids:
- extendedae:precise_export_bus
---

# ME Precise Export Bus

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_precise_export_bus.snbt"></ImportStructure>
</GameScene>

The ME Precise Export Bus exports items or fluids in specified quantities. It exports only when the container can accept the entire output.

## Example

![GUI](../pic/pre_bus_gui1.png)

This setting exports 3 units of cobblestone per operation. It stops exporting when the network contains fewer than 3 units.

![GUI](../pic/pre_bus_gui2.png)

It also stops exporting when the target container cannot hold the entire output. The chest can hold only 2 more units of cobblestone, so the export bus stops.
