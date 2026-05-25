---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME缶詰機
    icon: extendedae:caner
categories:
- extended devices
item_ids:
- extendedae:caner
---

# ME缶詰機

<BlockImage id="extendedae:caner" scale="8"></BlockImage>

ME缶詰機は、液体、Mekanismガス、Botaniaマナ、さらにはエネルギーなどのものを「缶詰め」する機械です！

1つ目のスロットは詰める物のためのスロットで、2つ目のスロットは詰められる容器のためのスロットです。

レシピを実行するためには電力が必要です。動作ごとに80AEかかります。

![GUI](../pic/caner_gui.png)

デフォルトでは液体のみを充填します。他の物質を充填するには、対応するアドオンをインストールする必要があります。

### 対応アドオン:
- Applied Flux
- Applied Mekanistics
- Applied Botanics Addon

## ME缶詰機による自動クラフト

上側と下側のみがエネルギーを受け入れ、ネットワークに接続できます。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_example.snbt"></ImportStructure>
</GameScene>

ME缶詰機の簡単なセットアップ例です。ME缶詰機は<ItemLink id="ae2:pattern_provider" />から材料を受け取ると、自動的に缶詰め作業を行います。

<GameScene zoom="6" background="transparent">
  <ImportStructure src="../structure/caner_auto.snbt"></ImportStructure>
</GameScene>

パターンには、充填する材料と充填する容器のみを含める必要があります。以下に例を示します:

バケツに水を入れる場合:

![P1](../pic/fill_water.png)

エネルギータブレットを充電する場合(別途Applied Mekanisticsの導入が必要):

![P1](../pic/fill_energy.png)


## 排出

ME缶詰機は、空モードで容器から中身を排出することもできます。入力と出力をパターンに従って切り替える必要があります。
