---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Conector ME inalámbrico
    icon: extendedae:wireless_connect
categories:
- extended devices
item_ids:
- extendedae:wireless_connect
- extendedae:wireless_tool
---

# Conector ME inalámbrico

<Row gap="20">
<BlockImage id="extendedae:wireless_connect" scale="6"></BlockImage>
<ItemImage id="extendedae:wireless_tool" scale="6"></ItemImage>
</Row>

El conector ME inalámbrico puede vincular dos redes como un <ItemLink id="ae2:quantum_link" />, pero con distancias limitadas
y no puede cruzar dimensiones. El conector ME inalámbrico solo admite conexiones uno a uno, necesitas usar un <ItemLink id="extendedae:wireless_hub" />
si deseas conexiones muchos a muchos.

## Vincular los conectores inalámbricos

Haz clic en los dos conectores inalámbricos que deseas vincular con el kit de configuración ME inalámbrico, luego puedes vincularlos juntos.

Haz clic agachándote para borrar la configuración actual del kit de configuración ME inalámbrico.

El conector ME inalámbrico cambiará su textura cuando se establezca un enlace con éxito.

Conectores ME inalámbricos no vinculados

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

Conectores ME inalámbricos vinculados

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## Color

Los conectores inalámbricos se pueden colorear como los cables y solo conectan el cable/conectores con el mismo color.

Necesitas un <ItemLink id="ae2:color_applicator" /> para colorear el conector.

Así que puedes configurar tus conectores inalámbricos así:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## Uso de energía

El conector ME inalámbrico cuesta más energía cuando están más separados. Su curva de costo-distancia no es lineal,
por lo que el costo de energía puede ser muy alto si están demasiado lejos.

Puedes usar una <ItemLink id="ae2:energy_card" /> para ahorrar energía, cada tarjeta puede reducir el costo de energía en un 10%.
