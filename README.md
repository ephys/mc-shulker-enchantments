# Shulker Enchantments

[See the CurseForge page for details](https://www.curseforge.com/minecraft/mc-mods/shulker-enchantments)

## For developers

If your mod provides items that keep or have their own inventory in item form (such as Shulker Boxes, Satchels, Backpacks, etc...), 
and you wish for your items to support the enchantments provided by this mod, 
you can do so using the native Tags and the Capabilities systems.

### Tagging

You simply need to tag your item with the `shulker_enchantments:shulker_like` item tag.  
This will tell ShulkerEnchantments that your item accepts the enchantment.

Start by creating a file in `resources` called `data/shulker_enchantments/tags/items/shulker_like.json`, and place the following JSON in it:
```json
{
  "replace": false,
  "values": ["<YOUR ITEM ID>"]
}
```

Where `<YOUR ITEM ID>` is the ID of the item you wish to add support to (eg. `minecraft:shulker_box`).

For more information about tags, see [The Minecraft Tag documentation](https://minecraft.gamepedia.com/Tag) and [Forge's Tag documentation](https://mcforge.readthedocs.io/en/latest/utilities/tags/)

### Capabilities

Lastly, your item's *`ItemStack`s* must have Forge's `CapabilityItemHandler.ITEM_HANDLER_CAPABILITY` capability attached. See [Forge's Capabilities documentation](https://mcforge.readthedocs.io/en/stable/datastorage/capabilities/) to learn more about attaching capabilities to ItemStacks.

If you do not provide this capability, your item's inventory will be impossible to manipulate.
