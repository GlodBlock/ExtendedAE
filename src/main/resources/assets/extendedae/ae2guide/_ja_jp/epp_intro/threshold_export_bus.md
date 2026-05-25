---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: MEしきい値エクスポートバス
    icon: extendedae:threshold_export_bus
categories:
- extended devices
item_ids:
- extendedae:threshold_export_bus
---

# MEしきい値エクスポートバス

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_export_bus.snbt"></ImportStructure>
</GameScene>

MEしきい値エクスポートバスは、MEネットワークに保存されているアイテムの数量がしきい値を上回るか下回る場合に機能します。

## 例

![GUI](../pic/thr_bus_gui1.png)

銅インゴットのしきい値は128に設定されているため、ネットワークに保存されている銅インゴットが128を超えると銅が出力されます。

![GUI](../pic/thr_bus_gui2.png)

しきい値は上記と同じですが、モードは「下回ったとき」に設定されています。保存されている銅インゴットが128未満の場合に銅を出力します。
