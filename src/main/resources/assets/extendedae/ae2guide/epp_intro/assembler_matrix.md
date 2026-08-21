---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Assembler Matrix
    icon: extendedae:assembler_matrix_frame
categories:
- extended devices
item_ids:
- extendedae:assembler_matrix_frame
- extendedae:assembler_matrix_wall
- extendedae:assembler_matrix_glass
- extendedae:assembler_matrix_pattern
- extendedae:assembler_matrix_crafter
- extendedae:assembler_matrix_speed
---

# Assembler Matrix

<Row>
<BlockImage id="extendedae:assembler_matrix_frame" p:formed="true" p:powered="true" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_wall" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_glass" scale="5"></BlockImage>
</Row>
<Row>
<BlockImage id="extendedae:assembler_matrix_pattern" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_crafter" scale="5"></BlockImage>
<BlockImage id="extendedae:assembler_matrix_speed" scale="5"></BlockImage>
</Row>

The Assembler Matrix is a multiblock structure. It is a combination of a <ItemLink id="ae2:molecular_assembler" /> and a <ItemLink id="ae2:pattern_provider" />.
It can run many crafting jobs at the same time (with enough <ItemLink id="ae2:crafting_accelerator" />s in your ME network) and save network channels.

## Structure

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/assembler_matrix.snbt"></ImportStructure>
</GameScene>

It is a rectangular prism with edge lengths between 3 and 7.

- Its edges are composed of Assembler Matrix Frames.
- Its faces are composed of Assembler Matrix Wall or Assembler Matrix Glass blocks.
- Its interior is composed of Assembler Matrix Pattern, Craft, or Speed Cores.

A valid Assembler Matrix must contain at least one Pattern Core and one Craft Core.
It must be completely filled and can't be hollow.
When the Assembler Matrix is correctly formed and powered, the lines on its frames turn blue.

## Assembler Matrix Core

There are three different Assembler Matrix Cores.

- Assembler Matrix Pattern Core

The Assembler Matrix takes patterns only from its Pattern Cores. Each Pattern Core provides 36 pattern slots for the Assembler Matrix.

- Assembler Matrix Craft Core

The Assembler Matrix assigns received crafting jobs to its Craft Cores. Each Craft Core can run eight crafting jobs at the same time.

- Assembler Matrix Speed Core

It acts as the <ItemLink id="ae2:speed_card" /> for the Assembler Matrix. Five Speed Cores allow the Assembler Matrix to run at full speed.
Installing more than five Speed Cores will not provide an additional speed boost.

## GUI

Right-clicking a formed and online Assembler Matrix opens its GUI.

![GUI](../pic/assembler_matrix.png)

You can insert or search for patterns in it and view how many crafting jobs it is running.
