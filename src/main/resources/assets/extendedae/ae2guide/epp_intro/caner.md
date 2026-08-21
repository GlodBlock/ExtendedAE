---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Canner
    icon: extendedae:caner
categories:
- extended devices
item_ids:
- extendedae:caner
---

# ME Canner

<BlockImage id="extendedae:caner" scale="8"></BlockImage>

The ME Canner is a machine that cans various resources, including fluids, Mekanism gases, Botania mana, and even energy!

The first slot holds the filling resource, and the second holds the container to be filled.

It needs energy to run, and every operation costs 80 AE.

![GUI](../pic/caner_gui.png)

By default, it only fills fluids. You need to install the corresponding add-on to make it fill other resources.

## Supported Add-ons

- Applied Flux
- Applied Mekanistics
- Applied Botanics Addon

## Autocrafting with the ME Canner

Only the top and bottom sides can accept energy and connect to the network.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_example.snbt"></ImportStructure>
</GameScene>

A simple setup for the ME Canner. The ME Canner automatically ejects the filled item when it receives the ingredients from a <ItemLink id="ae2:pattern_provider" />.

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_auto.snbt"></ImportStructure>
</GameScene>

The pattern must contain only the resource used to fill and the container to be filled. Here are some examples:

Fill a water bucket:

![P1](../pic/fill_water.png)

Charge an Energy Tablet (requires Applied Flux):

![P1](../pic/fill_energy.png)


## Uncanning

The ME Canner can also drain resources from a container in Empty mode. You need to swap the inputs and outputs in the pattern.
