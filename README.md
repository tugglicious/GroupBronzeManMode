# Group Bronzeman Mode RuneLite Plugin

This plugin implements the custom gamemode called 'Group Bronzeman mode', based on Another Bronzeman Mode.

The idea of this gamemode lies somewhere between a 'normal' account and an 'Ironman' account; as you can't buy an item on the Grand Exchange until you have obtained that item through other means, such as getting it as a drop or buying it in a shop.

The plugin enforces this rule by keeping track of all items you acquire, and only allowing you to buy this item.
When the plugin is enabled for the first time it will unlock all items in your inventory, as well as unlock all items in your bank the next time you open it.

## 🆕 Group Synchronization

**NEW FEATURE:** Synchronize your unlocks with other players in your group! When any player in your group unlocks an item, everyone in the group can buy it on the Grand Exchange.

**Setup Guide:** See [JSONBIN_SETUP_GUIDE.md](JSONBIN_SETUP_GUIDE.md) for detailed instructions on setting up group sync with JSONBin.io (free cloud storage).

**How it works:**
- All players share the same JSONBin.io storage (Bin ID)
- Unlocks are automatically synced every few minutes (configurable)
- See who unlocked each item (optional)
- Works offline - syncs when connection is restored

## Features

### Core Features
- Restricts buying items from the Grand Exchange until that item is obtained through self-sufficient methods.
- Will disallow trading players depending on your settings.
- Shows an item unlock graphic every time you obtain an item for the first time.
- Unlocks are handled per account so you can have multiple bronze-men or not effect the status of your bronze-man accounts.
- Can optionally take screenshots of all new item unlocks.
- Supports adding a list of names of other Bronzeman accounts, and will provide them with (client side) Bronzeman chat icons.
- Has settings for sending chat messages and notifications for every item unlock.
- Allows the command '!bmcount' and '!bmunlocks' to get a total number for all unlocked items.
- Has a setting to enable a '!bmreset' command, which deletes all current unlocked items and starts fresh.
- Supports a '!bmbackup' command that makes a backup of the current unlocked items list.

### Group Synchronization Features
- **Cloud sync via JSONBin.io**: Free cloud storage for your group's unlocked items
- **Automatic syncing**: Configurable sync interval (1-60 minutes, default 5)
- **Real-time notifications**: Get notified when your group unlocks new items
- **Unlock attribution**: See which player unlocked each item
- **Merge strategy**: All unlocks from all players are combined (union)
- **Offline support**: Local unlocks still work, syncs when back online
- **Easy setup**: Share one Bin ID and API key with your group

## Screenshots

![Unlocking an item](https://i.imgur.com/odE4nVo.png)
Unlocking an item right after getting off tutorial island.

![Chat icons](https://i.imgur.com/D8Zl6Ss.png)
Talking to fellow Bronzemen with chat icons and everything.

![Grand exchange](https://i.imgur.com/lTd0I6P.png)
This player has only unlocked bronze arrows, so the other items are greyed out and not clickable.

![Unlock list](https://i.imgur.com/348PI3B.png)

You can see all your unlocks in the Another Bronzeman Mode side-panel as a neatly sortable and filterable list of items.
This interface comes with search functionality, as well as the ability to re-lock an item by right clicking it and selecting "Remove".

## Credits

### Main Author
- **tugglicious** - Group Bronzeman Mode implementation and modifications

### Honorable Mentions - Another Bronzeman Mode Creators
- **[CodePanter](https://github.com/codepanter)** - Creator of [Another Bronzeman Mode](https://github.com/CodePanter/another-bronzeman-mode), which this plugin is based on
- **[Sethrem](https://github.com/sethrem)** - Author of the [initial Bronzeman Mode plugin](https://github.com/sethrem/bronzeman)
- **[Dekvall](https://github.com/dekvall)** - Code improvements in their [bronzeman-mode version](https://github.com/dekvall/bronzeman-mode)
- **[Robin Withes](https://github.com/robinwithes)** - Unlock-list backup feature, reset unlocks feature, collection log integration, and search functionality

### Original Concept
- **[GUDI (Mod Ronan)](https://www.youtube.com/watch?v=GFNfa2saOJg)** - First envisioned the Bronzeman mode concept
