---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Bus de almacenamiento ME de etiquetas
    icon: extendedae:tag_storage_bus
categories:
- extended devices
item_ids:
- extendedae:tag_storage_bus
---

# Bus de almacenamiento ME de etiquetas

<GameScene zoom="8" background="transparent">
  <ImportStructure src="../structure/cable_tag_storage_bus.snbt"></ImportStructure>
</GameScene>

El bus de almacenamiento ME de etiquetas es un <ItemLink id="ae2:storage_bus" /> que se puede filtrar por etiquetas de objetos o fluidos y admite algunos operadores lógicos básicos.

Aquí hay algunos ejemplos:

- Solo aceptar mena cruda

c:raw_materials/*

- Aceptar todos los lingotes y gemas

c:ingots/* | c:gems/*

