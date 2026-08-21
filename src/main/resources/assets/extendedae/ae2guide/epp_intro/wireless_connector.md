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

The ME Wireless Connector can link two networks like a <ItemLink id="ae2:quantum_link" />, but it has a limited range and cannot
connect across dimensions. It supports only one-to-one connections; use an <ItemLink id="extendedae:wireless_hub" />
for many-to-many connections.

## Linking Wireless Connectors

Click the two Wireless Connectors you want to link with the ME Wireless Setup Kit to connect them.

Sneak-click to clear the ME Wireless Setup Kit's current settings.

The ME Wireless Connector changes its texture when a link is successfully established.

Unlinked ME Wireless Connectors

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

Linked ME Wireless Connectors

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## Color

Wireless Connectors can be colored like cables and connect only to cables and connectors of the same color.

You need a <ItemLink id="ae2:color_applicator" /> to color a connector.

You can set up your Wireless Connectors like this:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## Power Usage

ME Wireless Connectors consume more energy when they are farther apart. The cost-distance curve is not linear, so the power
cost can become very high when they are too far apart.

You can use <ItemLink id="ae2:energy_card" />s to save power. Each card reduces the energy cost by 10%.

