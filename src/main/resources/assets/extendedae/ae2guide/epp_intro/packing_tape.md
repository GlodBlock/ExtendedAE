---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: ME Packing Tape
    icon: extendedae:me_packing_tape
categories:
- extended items
item_ids:
- extendedae:me_packing_tape
- extendedae:package
---

# ME Packing Tape

ME Packing Tape can pack an in-world ME device into a Packaged Device item.

<Row>
<ItemImage id="extendedae:me_packing_tape" scale="4"></ItemImage>
<ItemImage id="extendedae:package" scale="4"></ItemImage>
</Row>

## Packing

Sneak-right-click an ME device with the tape to turn it into a package item. All settings and inventory contents of
the device are preserved. This is useful when moving your ME setup.

ME Packing Tape supports only devices on the list. This device list is configurable.

### Default List

|                                      Device                                       |                                 Name                                  |
|:---------------------------------------------------------------------------------:|:---------------------------------------------------------------------:|
|    <ItemImage id="extendedae:ex_interface_part" scale="3"></ItemImage>     |    <ItemLink id="extendedae:ex_interface_part"></ItemLink>     |
| <ItemImage id="extendedae:ex_pattern_provider_part" scale="3"></ItemImage> | <ItemLink id="extendedae:ex_pattern_provider_part"></ItemLink> |
|       <ItemImage id="extendedae:ex_interface" scale="3"></ItemImage>       |       <ItemLink id="extendedae:ex_interface"></ItemLink>       |
|   <ItemImage id="extendedae:ex_pattern_provider" scale="3"></ItemImage>    |   <ItemLink id="extendedae:ex_pattern_provider"></ItemLink>    |
|            <ItemImage id="ae2:cable_interface" scale="3"></ItemImage>             |            <ItemLink id="ae2:cable_interface"></ItemLink>             |
|         <ItemImage id="ae2:cable_pattern_provider" scale="3"></ItemImage>         |         <ItemLink id="ae2:cable_pattern_provider"></ItemLink>         |
|               <ItemImage id="ae2:interface" scale="3"></ItemImage>                |               <ItemLink id="ae2:interface"></ItemLink>                |
|            <ItemImage id="ae2:pattern_provider" scale="3"></ItemImage>            |            <ItemLink id="ae2:pattern_provider"></ItemLink>            |
|                 <ItemImage id="ae2:drive" scale="3"></ItemImage>                  |                 <ItemLink id="ae2:drive"></ItemLink>                  |

## Unpacking

Right-click with the package item as though placing the original device block or part. The packaged device will then be restored
from the package.
