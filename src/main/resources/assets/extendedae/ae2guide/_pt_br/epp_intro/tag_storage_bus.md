---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Ponto de Armazenamento de Tags ME
    icon: extendedae:tag_storage_bus
categories:
- extended devices
item_ids:
- extendedae:tag_storage_bus
---

# Ponto de Armazenamento de Tags ME

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_tag_storage_bus.snbt"></ImportStructure>
</GameScene>

O Ponto de Armazenamento de Tags ME é um <ItemLink id="ae2:storage_bus" /> que pode ser filtrado por tags de itens ou fluidos e suporta alguns operadores lógicos básicos.

Aqui estão alguns exemplos:

- Aceitar apenas minério bruto

c:raw_materials/*

- Aceitar todas as barras e gemas

c:ingots/* | c:gems/*