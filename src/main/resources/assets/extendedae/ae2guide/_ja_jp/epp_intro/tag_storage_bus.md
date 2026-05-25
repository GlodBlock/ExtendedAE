---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: MEストレージバス(タグ)
    icon: extendedae:tag_storage_bus
categories:
- extended devices
item_ids:
- extendedae:tag_storage_bus
---

# MEストレージバス(タグ)

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_tag_storage_bus.snbt"></ImportStructure>
</GameScene>

MEストレージバス(タグ)はアイテムまたは液体タグでフィルタリングでき、いくつかの論理演算子をサポートする<ItemLink id="ae2:storage_bus" />です。

いくつか例を挙げます:

- 全ての原石だけを受け入れ

c:raw_materials/*

- 全てのインゴットと宝石を受け入れ

c:ingots/* | c:gems/*

