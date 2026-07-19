---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Wireless Connector
    icon: extendedae:wireless_connect
categories:
- extended devices
item_ids:
- extendedae:wireless_connect
- extendedae:wireless_tool
---

# ME Wireless Connector

<Row gap="20">
<BlockImage id="extendedae:wireless_connect" scale="6"></BlockImage>
<ItemImage id="extendedae:wireless_tool" scale="6"></ItemImage>
</Row>

ME Wireless Connector can link two networks like <ItemLink id="ae2:quantum_link" />, but it has limited range and cannot
connect across dimensions. ME Wireless Connector supports only one-to-one connections. Use <ItemLink id="extendedae:wireless_hub" />
if you want many-to-many connections.

## Link the Wireless Connectors

Use the ME Wireless Setup Kit to click the two Wireless Connectors that you want to link.

Sneak + click to clear the ME Wireless Setup Kit's current setting.

ME Wireless Connector changes its texture when a link is successfully established.

Unlinked ME Wireless Connectors

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

Linked ME Wireless Connectors

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## Color

Wireless Connectors can be colored like cables and can connect only to cables/connectors of the same color.

You need a <ItemLink id="ae2:color_applicator" /> to color a connector.

So you can set up your wireless connectors like this:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## Power Usage

ME Wireless Connectors consume more energy the farther apart they are. Because the cost-distance curve is not linear, the power
cost can become very high when they are too far apart.

You can use <ItemLink id="ae2:energy_card" /> to save power; each card reduces the energy cost by 10%.

