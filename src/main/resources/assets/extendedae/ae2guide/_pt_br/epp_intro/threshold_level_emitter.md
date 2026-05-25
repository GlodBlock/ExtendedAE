---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Emissor de Nível de Limiar ME
    icon: extendedae:threshold_level_emitter
categories:
- extended devices
item_ids:
- extendedae:threshold_level_emitter
---

# Emissor de Nível de Limiar ME

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_level_emitter.snbt"></ImportStructure>
</GameScene>

Ele funciona como um Reset-Set Latch. Ele desliga o sinal de redstone quando a quantidade de um item na rede é menor que
o limiar inferior e liga quando a quantidade é maior que o limiar superior.

Por exemplo, dado que o limiar inferior está definido como 100 e o limiar superior como 150.

A princípio a rede está vazia, então o emissor não estará ativo.

Conforme a quantidade do item aumenta e ultrapassa 150, o emissor enviará sinal de redstone.

Quando a quantidade diminui e fica menor que 150, o emissor ainda envia sinal.

Por fim, quando a quantidade é menor que 100, o emissor será desligado.