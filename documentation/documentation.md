# MapEngine documentation

I too lazy to write documentation or create images right now. Maybe later.

## /shader command
Server-side command for adding shader to viewport of selected player

```
/shader <targets> reset
/shader <targets> outline <size: int> <color: RRGGBB>
/shader <targets> vignette <size: int> <color: RRGGBB>
/shader <targets> tint <R: 0-200> <G: 0-200> <B: 0-200>
/shader <targets> saturation <R: 0-200> <G: 0-200> <B: 0-200>
/shader <targets> hue <degrees: 0-360>
/shader <targets> contrast <0-200>
/shader <targets> brightness <0-200>
/shader <targets> posterize <0-100>
/shader <targets> pixelize <size: int>
/shader <targets> noise <size: int> <rate: int>
/shader <targets> blur <0-100>
/shader <targets> chromatic <0-100>
/shader <targets> motion <0-100>
```

## /tooltip command
Server-side command for edit tooltip (outline of frame with item name, description and other)

```
/tooltip <targets> reset
/tooltip <targets> <background> <borderStart> <borderEnd>
```

## /interface command
Server-side command to edit interface elements: air, armor, food, health, experience_bar, experience_level, hotbar, gui, announcements, item_name, chat, recipebook

```
/interface <targets> <element> color <RRGGBB>
/interface <targets> <element> opacity <0-100>
/interface <targets> <element> visibility <true|false>
/interface <targets> <element> reset
```

im in discord: ghhggg123456.
