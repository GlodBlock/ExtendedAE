---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: 組立マトリックス
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

# 組立マトリックス

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

組立マトリックスはマルチブロック構造です。<ItemLink id="ae2:molecular_assembler" />と<ItemLink id="ae2:pattern_provider" />を組み合わせて一つで動作するようにしました。
MEネットワーク内に十分な数の<ItemLink id="ae2:crafting_accelerator" />を搭載したCPUがあれば、チャンネル数を節約した上、複数の作業を同時に実行できる最強の分子組立機が爆誕します。

## 構造

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/assembler_matrix.snbt"></ImportStructure>
</GameScene>

マトリックスは辺の長さが3から7までの直方体の形をしています。構造には以下のルールがあります。
- 頂点と辺は組立マトリックス外枠
- 面は組立マトリックス外壁/外壁ガラス
- 内部は組立マトリックスパターン/クラフト/スピードコア

有効な組立マトリックスには一つ以上のパターンコアとクラフトコアが含まれている必要があります。
完全に満たされる必要があり、空気ブロックの部分を残してはいけません。
組立マトリックスが正しく構築され、デバイスオンラインになると、外枠の色が青色に変わります。

## 組立マトリックスコア

組立マトリックスには3種類のコアがあります。

- 組立マトリックスパターンコア

組立マトリックスは、パターンコアからのみパターンを取得します。各パターンコアは、組立マトリックスのパターンスロットを36個増やします。

- 組立マトリックスクラフトコア

組立マトリックスは、受け取ったクラフト要求をクラフトコアに割り当てます。各クラフトコアは、同時に8つものクラフト作業を並列して行えます。

- 組立マトリックススピードコア

これは、組立マトリックス用の<ItemLink id="ae2:speed_card" />です。5つのスピードコアを搭載させると、組立マトリックスを最速で動作させることが可能です。逆に言えば、5つ以上スピードコアを積んでもそれ以上速くはなりません。

## GUI

構築され、オンラインになっているマトリックスを右クリックするとGUIを開けます。

![GUI](../pic/assembler_matrix.png)

パターンを投入したり検索したり、実行中のクラフト作業の数を確認したりできます。
