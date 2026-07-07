---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Wireless Connector
    icon: expatternprovider:wireless_connect
categories:
- extended devices
item_ids:
- expatternprovider:wireless_connect
- expatternprovider:wireless_tool
---

# ME Wireless Connector

<Row gap="20">
<BlockImage id="expatternprovider:wireless_connect" scale="6"></BlockImage>
<ItemImage id="expatternprovider:wireless_tool" scale="6"></ItemImage>
</Row>

The ME Wireless Connector can link two networks like <ItemLink id="ae2:quantum_link" />, but with limited range and without
cross-dimensional support. The ME Wireless Connector only supports one-to-one connections. Use <ItemLink id="expatternprovider:wireless_hub" />
if you want many-to-many connections.

## Link the Wireless Connectors

Click the two Wireless Connectors that you want to link with the ME Wireless Setup Kit to link them together.

Sneak + Click to clear the ME Wireless Setup Kit's current setting.

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

Wireless Connectors can be colored like cables and only connect to cables/connectors with the same color.

You need a <ItemLink id="ae2:color_applicator" /> to color the connector.

So you can set up your wireless connectors like this:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## Power Usage

ME Wireless Connectors consume more energy when they are farther apart. The cost-distance curve isn't linear, so the power
cost can get very high when they are too far apart.

You can use <ItemLink id="ae2:energy_card" /> to save power. Each card reduces the energy cost by 10%.

