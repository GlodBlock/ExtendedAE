---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME精密エクスポートバス
    icon: extendedae:precise_export_bus
categories:
- extended devices
item_ids:
- extendedae:precise_export_bus
---

# ME精密エクスポートバス

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_precise_export_bus.snbt"></ImportStructure>
</GameScene>

ME精密エクスポートバスは、指定した数のアイテム/液体またはその他を出力します。出力先の容量が出力全体を越している場合のみ出力されます。

## 例

![GUI](../pic/pre_bus_gui1.png)

これは、1回の動作で3個の丸石を出力することを意味します。ネットワーク内の丸石の数が3個未満になると、出力が停止します。

![GUI](../pic/pre_bus_gui2.png)

また、出力した量全てが出力先に収まりきらない場合も、出力が停止します。この場合だと、チェストには3個の丸石が入るスペースがないため、出力はされません。
