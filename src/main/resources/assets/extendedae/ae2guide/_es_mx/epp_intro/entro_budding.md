---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Brotador de fluix entroizado
    icon: extendedae:entro_budding_fully
categories:
- entro system
item_ids:
- extendedae:entro_budding_fully
- extendedae:entro_budding_mostly
- extendedae:entro_budding_half
- extendedae:entro_budding_hardly
- extendedae:entro_cluster_small
- extendedae:entro_cluster_medium
- extendedae:entro_cluster_large
- extendedae:entro_cluster
---

# Brotador de fluix entroizado

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../structure/budding_entro.snbt"></ImportStructure>
  <IsometricCamera yaw="195" pitch="30"></IsometricCamera>
</GameScene>

Son la fuente de <ItemLink id="extendedae:entro_crystal" /> y puedes obtener brotadores de fluix entroizados inyectando <ItemLink id="extendedae:entro_seed" /> en <ItemLink id="ae2:fluix_block" />.

El mecanismo de crecimiento de los racimos de entro es similar al del [Cuarzo certus](ae2:items-blocks-machines/budding_certus.md). Sin embargo, el progreso
de degradación de los brotadores de fluix entroizado es inevitable, y finalmente se convertirá en <ItemLink id="ae2:quartz_block" />.
Teóricamente, puedes obtener ~10 de <ItemLink id="extendedae:entro_crystal" /> de un fluix completamente entroizado sin el encantamiento de fortuna.

El brotador de fluix entroizado siempre dejará caer **un** <ItemLink id="extendedae:entro_dust" /> cuando se rompa, independientemente de su estado.
El racimo de entro dejará caer <ItemLink id="extendedae:entro_shard" /> cuando no esté completamente crecido.
