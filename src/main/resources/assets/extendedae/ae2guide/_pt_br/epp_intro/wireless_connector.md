---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Conector Sem Fio ME
    icon: extendedae:wireless_connect
categories:
- extended devices
item_ids:
- extendedae:wireless_connect
- extendedae:wireless_tool
---

# Conector Sem Fio ME

<Row gap="20">
<BlockImage id="extendedae:wireless_connect" scale="6"></BlockImage>
<ItemImage id="extendedae:wireless_tool" scale="6"></ItemImage>
</Row>

O Conector Sem Fio ME pode vincular duas redes como o <ItemLink id="ae2:quantum_link" />, mas com distâncias limitadas e não pode 
cruzar dimensões. O Conector Sem Fio ME suporta apenas conexões um-para-um, você precisa usar o <ItemLink id="extendedae:wireless_hub" /> 
se quiser conexões muitos-para-muitos.

## Vincular os Conectores Sem Fio

Clique nos dois Conectores Sem Fio que você deseja vincular com o Kit de Configuração Sem Fio ME, então você pode vinculá-los.

Agachar + Clique para limpar a configuração atual do Kit de Configuração Sem Fio ME.

O Conector Sem Fio ME mudará sua textura quando um vínculo for estabelecido com sucesso.

Conectores Sem Fio ME não vinculados

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_off.snbt"></ImportStructure>
</GameScene>

Conectores Sem Fio ME vinculados

<GameScene zoom="5" background="transparent">
  <ImportStructure src="../structure/wireless_connector_on.snbt"></ImportStructure>
</GameScene>

## Cor

Conectores Sem Fio podem ser coloridos como cabos e conectam apenas o cabo/conectores com a mesma cor.

Você precisa de um <ItemLink id="ae2:color_applicator" /> para colorir o conector.

Então você pode configurar seus conectores sem fio assim:

<GameScene zoom="3" background="transparent" interactive={true}>
  <ImportStructure src="../structure/wireless_connector_setup.snbt"></ImportStructure>
</GameScene>

## Uso de Energia

O Conector Sem Fio ME custa mais energia quando eles estão mais distantes. Sua curva de custo-distância não é linear, então o custo de energia 
pode ficar muito alto se eles estiverem muito distantes.

Você pode usar <ItemLink id="ae2:energy_card" /> para economizar energia, cada cartão pode reduzir 10% do custo de energia.