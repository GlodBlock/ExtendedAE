---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Precise Export Bus
    icon: expatternprovider:precise_export_bus
categories:
- extended devices
item_ids:
- expatternprovider:precise_export_bus
---

# ME Precise Export Bus

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_precise_export_bus.snbt"></ImportStructure>
</GameScene>

The ME Precise Export Bus exports specified quantities of items or fluids. It only performs an export when the target container can accept the entire output.

## Modes

### Exact Mode
It always exports the **EXACT** configured quantity of items or fluids per operation. Install acceleration cards when exporting large quantities.

### Batch Mode
It attempts to export the configured quantity X times per operation.

## Example

![GUI](../pic/pre_bus_gui1.png)

This configuration exports 3 cobblestone per operation. The bus stops exporting when the network contains fewer than 3 cobblestone.

![GUI](../pic/pre_bus_gui2.png)

The bus also stops exporting when the target container cannot hold the complete output. In this example, the chest has room for only 2 more cobblestone, so the export cannot proceed.
