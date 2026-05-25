---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: MEデバイスアップグレード
    icon: extendedae:pattern_provider_upgrade
categories:
- extended items
item_ids:
- extendedae:pattern_provider_upgrade
- extendedae:interface_upgrade
- extendedae:io_bus_upgrade
- extendedae:pattern_terminal_upgrade
- extendedae:drive_upgrade
---

# MEデバイスアップグレード

これらのアップグレードにより、通常のMEデバイスを壊すことなく拡張版に置き換えることができます。

<Row>
<ItemImage id="extendedae:pattern_provider_upgrade" scale="4"></ItemImage>
<ItemImage id="extendedae:interface_upgrade" scale="4"></ItemImage>
<ItemImage id="extendedae:io_bus_upgrade" scale="4"></ItemImage>
<ItemImage id="extendedae:pattern_terminal_upgrade" scale="4"></ItemImage>
<ItemImage id="extendedae:drive_upgrade" scale="4"></ItemImage>
</Row>

これらのデバイスをスニーク+右クリックすると、拡張版に切り替わります。デバイスの設定とインベントリはすべて保持されます。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/upgrade_show_1.snbt"></ImportStructure>
  <BoxAnnotation color="#ffffff" min="1 0 0" max="4 1 1">
        通常のパターンプロバイダー。パターンプロバイダーアップグレードでアップグレードできます。
        <ItemImage id="extendedae:pattern_provider_upgrade" scale="2"></ItemImage>
  </BoxAnnotation>
</GameScene>
<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/upgrade_show_2.snbt"></ImportStructure>
  <BoxAnnotation color="#ffffff" min="1 0 0" max="4 1 1">
        拡張型パターンプロバイダーは、元のパターンプロバイダーからのすべての設定とパターンインベントリを保持します。
  </BoxAnnotation>
</GameScene>

## アップグレード一覧

|                                      アップグレード                                      |                           通常デバイス                            |                                  拡張型デバイス                                  |
|:---------------------------------------------------------------------------------:|:------------------------------------------------------------------:|:---------------------------------------------------------------------------------:|
| <ItemImage id="extendedae:pattern_provider_upgrade" scale="3"></ItemImage> |    <ItemImage id="ae2:pattern_provider" scale="3"></ItemImage>     |   <ItemImage id="extendedae:ex_pattern_provider" scale="3"></ItemImage>    |
| <ItemImage id="extendedae:pattern_provider_upgrade" scale="3"></ItemImage> | <ItemImage id="ae2:cable_pattern_provider" scale="3"></ItemImage>  | <ItemImage id="extendedae:ex_pattern_provider_part" scale="3"></ItemImage> |
|    <ItemImage id="extendedae:interface_upgrade" scale="3"></ItemImage>     |        <ItemImage id="ae2:interface" scale="3"></ItemImage>        |       <ItemImage id="extendedae:ex_interface" scale="3"></ItemImage>       |
|    <ItemImage id="extendedae:interface_upgrade" scale="3"></ItemImage>     |     <ItemImage id="ae2:cable_interface" scale="3"></ItemImage>     |    <ItemImage id="extendedae:ex_interface_part" scale="3"></ItemImage>     |
|      <ItemImage id="extendedae:io_bus_upgrade" scale="3"></ItemImage>      |       <ItemImage id="ae2:import_bus" scale="3"></ItemImage>        |    <ItemImage id="extendedae:ex_import_bus_part" scale="3"></ItemImage>    |
|      <ItemImage id="extendedae:io_bus_upgrade" scale="3"></ItemImage>      |       <ItemImage id="ae2:export_bus" scale="3"></ItemImage>        |    <ItemImage id="extendedae:ex_export_bus_part" scale="3"></ItemImage>    |
| <ItemImage id="extendedae:pattern_terminal_upgrade" scale="3"></ItemImage> | <ItemImage id="ae2:pattern_access_terminal" scale="3"></ItemImage> |  <ItemImage id="extendedae:ex_pattern_access_part" scale="3"></ItemImage>  |
|      <ItemImage id="extendedae:drive_upgrade" scale="3"></ItemImage>       |          <ItemImage id="ae2:drive" scale="3"></ItemImage>          |         <ItemImage id="extendedae:ex_drive" scale="3"></ItemImage>         |
