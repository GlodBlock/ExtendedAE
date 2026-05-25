---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Emisor de nivel ME de umbral
    icon: extendedae:threshold_level_emitter
categories:
- extended devices
item_ids:
- extendedae:threshold_level_emitter
---

# Emisor de nivel ME de umbral

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_threshold_level_emitter.snbt"></ImportStructure>
</GameScene>

Funciona como un cerrojo de reinicio-establecimiento. Apaga la señal de redstone cuando la cantidad de un objeto en
la red es menor que el umbral inferior y se enciende cuando la cantidad es mayor que el umbral superior.

Por ejemplo, dado que el umbral inferior se establece en 100 y el umbral superior se establece en 150.

Al principio la red está vacía, por lo que el emisor no estará activo.

A medida que la cantidad del objeto aumenta y supera los 150, el emisor enviará una señal de redstone.

Cuando la cantidad disminuye y es menor que 150, el emisor aún envía la señal.

Al final, la cantidad es inferior a 100, el emisor se apagará.
