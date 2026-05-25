---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: MEしきい値レベルエミッター
    icon: extendedae:threshold_level_emitter
categories:
- extended devices
item_ids:
- extendedae:threshold_level_emitter
---

# MEしきい値レベルエミッター

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_level_emitter.snbt"></ImportStructure>
</GameScene>

これはRSラッチのように動作します。ネットワーク内のアイテムの数量が下限しきい値を下回るとレッドストーン信号をオフにし、上限しきい値を超えるとオンにします。

たとえば、下限しきい値を100に設定し、上限しきい値を150に設定します。

最初はネットワークは空なので、エミッターはオンになりません。

アイテムの数量が増えて150を超えると、エミッターはレッドストーン信号を送信します。

数量が減少して150未満になった場合でも、エミッターは信号を送信し続けます。

最後に数量が100未満になると、エミッターはオフになります。
