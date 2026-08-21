---
navigation:
    parent: epp_intro/epp_intro-index.md
    title: Config Modifier
    icon: extendedae:config_modifier
categories:
- extended items
item_ids:
- extendedae:config_modifier
---

# Config Modifier

The Config Modifier is a tool for modifying configuration inventories in bulk.

<ItemImage id="extendedae:config_modifier" scale="4"></ItemImage>

Right-click it to open its GUI.

## Usage

The Config Modifier can quickly modify configuration inventories, such as an interface's configuration inventory, according to its settings. For example, it can set configured item
amounts to the maximum value or remove them all.

Use the Config Modifier on a target device (block or subpart) in the world to modify its configuration.

## Settings

It has two main settings: the **modification mode** and the **modification amount (X)**.

Click the button to change the mode.

![GUI](../pic/cm.png)

- Add/Subtract/Multiply/Divide: Add, subtract, multiply, or divide each configured amount by X.
- Maximize: Set each configured amount to the maximum value.
- Minimize: Set each configured amount to the minimum value.
- Set: Set each configured amount to X.
- Clear: Clear the entire configuration.
