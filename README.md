# Group Bronzeman Mode RuneLite Plugin

A group-enabled version of Bronzeman Mode allowing players to share unlocked items across their group via cloud synchronization.

## What is Bronzeman Mode?

Bronzeman mode bridges the gap between a 'normal' account and an 'Ironman' account - you can't buy an item on the Grand Exchange until you have obtained that item through self-sufficient means, such as getting it as a drop, buying it in a shop, or crafting it yourself.

The plugin enforces this rule by keeping track of all items you acquire, and only allowing you to buy items you've unlocked.
When the plugin is enabled for the first time, it will unlock all items in your inventory and bank.

## Why Group Bronzeman Mode?

Play Bronzeman with your friends! All unlocks are shared across your group - when anyone unlocks an item, everyone can buy it. Perfect for group progression and cooperative gameplay.

## Installation

### From Plugin Hub (Pending Approval)
1. Open RuneLite
2. Click the **Plugin Hub** icon
3. Search for "**GBM**" or "**Group Bronzeman Mode**"
4. Click **Install**

### Manual Installation (Current Method)
1. Download the latest `.jar` file from the [Releases](../../releases) page
2. Place it in your RuneLite plugins folder:
   - **Windows**: `C:\Users\YourUsername\.runelite\plugins\`
   - **Mac**: `~/.runelite/plugins/`
   - **Linux**: `~/.runelite/plugins/`
3. Restart RuneLite
4. Enable the plugin in the Configuration panel

## 🆕 Group Synchronization

**Synchronize your unlocks with other players in your group!** When any player in your group unlocks an item, everyone in the group can buy it on the Grand Exchange.

### Quick Setup
1. **Create a free JSONBin.io account** at https://jsonbin.io
2. **Get your API key** from your JSONBin dashboard
3. **Create a new bin** and copy the Bin ID
4. **Share the API key and Bin ID** with all group members
5. **Configure the plugin** with these credentials
6. **Start playing!** Unlocks sync automatically every 5 minutes (configurable)

📖 **Detailed Guide:** See [JSONBIN_SETUP_GUIDE.md](JSONBIN_SETUP_GUIDE.md) for step-by-step instructions with screenshots.

### How It Works
- ✅ All players share the same JSONBin.io storage (Bin ID)
- ✅ Unlocks are automatically synced every few minutes (configurable interval)
- ✅ **Prominent colored notifications** appear in-game when your group unlocks items
- ✅ See who unlocked each item (optional setting)
- ✅ Works offline - syncs when connection is restored
- ✅ Free tier is sufficient for most groups

### ⚠️ Important: Reset Command with Group Sync

**WARNING:** The `!bmreset` command **only resets your LOCAL unlocks** - it does NOT clear the JSONBin cloud storage.

**What happens when you use `!bmreset` with group sync enabled:**
1. Your local unlock list is cleared
2. The JSONBin (shared with your group) is **NOT** cleared
3. On the next sync (within a few minutes), all unlocks will be pulled back from the cloud
4. Your unlock list will be restored to match the group's unlocks

**If you want to fully reset the group's unlocks:**
- You must **manually delete the JSONBin** at https://jsonbin.io
- Create a new bin and update all group members with the new Bin ID
- OR manually edit the bin to remove all unlocks using the JSONBin.io web interface

**Recommendation:** Coordinate with your group before resetting unlocks to avoid confusion.

## Features

### Core Bronzeman Features
- ✅ **Grand Exchange Restrictions**: Only buy items you've unlocked through self-sufficient gameplay
- ✅ **Item Unlock Overlay**: Satisfying graphic display when unlocking new items
- ✅ **Per-Account Unlocks**: Multiple characters supported with separate unlock lists
- ✅ **Screenshot Support**: Automatically capture screenshots of item unlocks
- ✅ **Bronzeman Chat Icons**: Visual indicators for fellow Bronzeman players in chat
- ✅ **Unlock Notifications**: Chat messages and desktop notifications for new unlocks
- ✅ **Item Management Panel**: Sortable, searchable, filterable list of all unlocked items
- ✅ **Commands**: `!bmcount`, `!bmunlocks`, `!bmreset`, `!bmbackup`

### Group Synchronization Features
- ✅ **Cloud Sync via JSONBin.io**: Free cloud storage for your group's shared unlocked items
- ✅ **Automatic Syncing**: Configurable sync interval (1-60 minutes, default 5)
- ✅ **Prominent In-Game Notifications**: Colored game messages when group unlocks new items
  - Orange "Group Sync:" prefix with green item counts for visibility
  - Changed from easily-missed console messages to prominent GAMEMESSAGE type
- ✅ **Unlock Attribution**: See which player unlocked each item (optional)
- ✅ **Merge Strategy**: All unlocks from all players are combined automatically
- ✅ **Offline Support**: Local unlocks still work, syncs when connection is restored
- ✅ **Easy Setup**: Share one Bin ID and API key with your entire group

### Trading & Group Restrictions
- ✅ **Group-Only Trading**: Restrict trading to only your group members
- ✅ **Group Member List**: Configure who's in your Bronzeman group
- ✅ **Trading Whitelist**: Optional setting to allow all trading (for flexibility)
- ✅ **Automatic Trade Blocking**: Non-group trade attempts are blocked with helpful messages

### Quality of Life Features
- ✅ **Auto Friends Chat Messages**: Automatically fills chat with unlock messages
  - Smart message queuing - combines multiple unlocks into one message
  - Character limit protection - prevents game lag from long messages
  - Switches to item count format when unlocking many items at once
- ✅ **Character Whitelist**: Enable/disable plugin per character (perfect for Jagex multi-character accounts)
- ✅ **Minigame Protection**: Prevents unlocking temporary items from Last Man Standing, Theatre of Blood, Chambers of Xeric, etc.
- ✅ **Tradeable-Only Mode**: Option to hide/ignore untradeable items to reduce clutter

### Recent Improvements (v1.2)
- 🆕 **Fixed Group Sync Visibility**: Notifications now show as prominent colored messages instead of hidden console text
- 🆕 **Fixed Chat Overflow Lag**: Added smart character limits to prevent game freezing when unlocking many items
- 🆕 **Updated Dependencies**: Full RuneLite 1.11.25 compatibility with updated Gradle verification
- 🆕 **Improved Message Queuing**: Intelligently combines or summarizes multiple unlock messages

## Configuration

### Basic Settings
- **Names Bronzeman**: Comma-separated list of player names in your group (for chat icons and trading restrictions)
- **Send Chat Message**: Display chat message when unlocking items
- **Send Notification**: Desktop notification when unlocking items
- **Screenshot Unlock**: Automatically screenshot item unlocks
- **Hide Untradeables**: Only track tradeable items (reduces clutter)

### Group Sync Settings
- **Enable Group Sync**: Turn on cloud synchronization
- **JSONBin API Key**: Your JSONBin.io API key
- **JSONBin Bin ID**: Your shared bin ID (all group members use the same one)
- **Sync Interval**: How often to sync (1-60 minutes, default: 5)
- **Show Who Unlocked**: Display which player unlocked each item

### Advanced Settings
- **Enable Whitelist**: Only enable plugin for specific characters
- **Whitelisted Characters**: Comma-separated list of character names
- **Allow Trading**: Bypass trading restrictions (for flexibility)
- **Auto Friends Chat Message**: Pre-fill chat with unlock messages
- **Reset Command**: Enable the `!bmreset` command

## Screenshots

![Unlocking an item](https://i.imgur.com/odE4nVo.png)
Unlocking an item right after getting off tutorial island.

![Chat icons](https://i.imgur.com/D8Zl6Ss.png)
Talking to fellow Bronzemen with chat icons and everything.

![Grand exchange](https://i.imgur.com/lTd0I6P.png)
This player has only unlocked bronze arrows, so the other items are greyed out and not clickable.

![Unlock list](https://i.imgur.com/348PI3B.png)

You can see all your unlocks in the Group Bronzeman Mode side-panel as a neatly sortable and filterable list of items.
This interface comes with search functionality, as well as the ability to re-lock an item by right clicking it and selecting "Remove".

## Changelog

### Version 1.2 (Current)
- **Fixed**: Group sync notifications now show as prominent colored GAMEMESSAGE instead of hidden console text
- **Fixed**: Chat overflow causing game lag - added character limits and smart message truncation
- **Fixed**: Updated Gradle dependency verification for RuneLite 1.11.25 compatibility
- **Improved**: Message queuing intelligently combines or summarizes multiple unlock messages
- **Added**: Character whitelist system for Jagex multi-character accounts
- **Added**: Auto friends chat messaging with smart overflow protection
- **Added**: Minigame protection (LMS, ToB, CoX, BA)

### Version 1.1
- **Added**: Group synchronization via JSONBin.io
- **Added**: Trading restrictions (group members only)
- **Added**: Unlock attribution (see who unlocked items)
- **Added**: Configurable sync intervals

### Version 1.0
- Initial release based on Another Bronzeman Mode
- Core Bronzeman functionality
- Item unlock tracking and Grand Exchange restrictions

## Support & Community

- **Issues**: Report bugs on [GitHub Issues](../../issues)
- **Discussions**: Join discussions on [GitHub Discussions](../../discussions)
- **Contributing**: Pull requests welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) if available

## Credits

### Main Author
- **[tugglicious](https://github.com/tugglicious)** - Group Bronzeman Mode implementation, group sync features, and ongoing maintenance

### Based On
- **[CodePanter](https://github.com/codepanter)** - Creator of [Another Bronzeman Mode](https://github.com/CodePanter/another-bronzeman-mode)
- **[Sethrem](https://github.com/sethrem)** - Author of the [original Bronzeman Mode plugin](https://github.com/sethrem/bronzeman)
- **[Dekvall](https://github.com/dekvall)** - Code improvements in [bronzeman-mode](https://github.com/dekvall/bronzeman-mode)
- **[Robin Withes](https://github.com/robinwithes)** - Unlock-list backup, reset, collection log integration, and search

### Original Concept
- **[GUDI (Mod Ronan)](https://www.youtube.com/watch?v=GFNfa2saOJg)** - First envisioned the Bronzeman mode concept

## License

This project is licensed under the BSD 2-Clause License - see the LICENSE file for details.
