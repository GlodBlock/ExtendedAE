---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME無線接続器
    icon: extendedae:wireless_connect
categories:
- extended devices
item_ids:
- extendedae:wireless_connect
- extendedae:wireless_tool
---

# ME無線接続器

<Row gap="20">
<BlockImage id="extendedae:wireless_connect" scale="6"></BlockImage>
<ItemImage id="extendedae:wireless_tool" scale="6"></ItemImage>
</Row>

ME無線接続器は<ItemLink id="ae2:quantum_link" />のように2つの場所をワイヤレスで接続できますが、ディメンションを跨げず、距離制限があります。これ単体では1対1での接続しかできず、<ItemLink id="extendedae:wireless_hub" />と組み合わせることで1体多の接続が可能になります。

## 無線接続器を接続する

ME無線セットアップキットで2つの無線接続器をクリックすると、それらを接続できます。

スニーク右クリックすると、ME無線セットアップキットの現在の設定が消去されます。

正しく接続が行われると、ME無線接続器の見た目が変わります。

未接続のME無線接続器

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

接続されたME無線接続器

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## 色の変更

無線接続器はケーブルと同じように着色でき、同じ色のケーブル/接続器のみと繋がります。

接続器に色を付けるには、<ItemLink id="ae2:color_applicator" />が必要です。

色を使い分けるとこんな感じのことができます:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## 電力消費

ME無線接続器は、距離が離れると電力消費量が増加します。距離に対するコスト曲線は直線ではないため、距離が離れすぎると電力コストが一気に増える可能性があります。

<ItemLink id="ae2:energy_card" />を使用すると電力を節約できます。カード1枚につき電力コストを10%削減できます。

