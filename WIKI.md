# GBM - Group Bronzeman Mode Plugin Wiki

Welcome to the complete guide for the GBM (Group Bronzeman Mode) RuneLite plugin!

## Table of Contents

1. [What is Group Bronzeman Mode?](#what-is-group-bronzeman-mode)
2. [Features](#features)
3. [Installation](#installation)
4. [Getting Started](#getting-started)
5. [Configuration Guide](#configuration-guide)
6. [Group Synchronization Setup](#group-synchronization-setup)
7. [Commands](#commands)
8. [Advanced Features](#advanced-features)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

---

## What is Group Bronzeman Mode?

Group Bronzeman Mode is a custom game mode that sits between a normal account and an Ironman account. The key rule: **You can't buy an item on the Grand Exchange until you've obtained it through self-sufficient means** (drops, shops, skilling, etc.).

**The "Group" twist:** When playing with friends, all players in your group share the same unlock list! When any player unlocks an item, everyone in the group can buy it on the Grand Exchange.

### Game Mode Philosophy

- 🎯 **Encourages exploration** - You must obtain items "the Ironman way" first
- 🤝 **Supports group play** - Share unlocks with your friends
- 📊 **Track progress** - See all your unlocked items in a sortable panel
- 🔒 **Trading restrictions** - Only trade with your group members (optional)
- ☁️ **Cloud sync** - Your group's unlocks are automatically synchronized via JSONBin.io

---

## Features

### Core Features

#### Grand Exchange Restrictions
- **Only unlocked items can be purchased** on the GE
- Locked items appear greyed out and are not clickable
- Search results automatically hide locked items

#### Item Tracking
- **Automatic unlock detection** when you:
  - Receive item drops
  - Buy from shops
  - Skill items (fishing, woodcutting, mining, etc.)
  - Open inventory containers (bank, chests, etc.)
- **Visual unlock notification** appears when you unlock a new item
- **Searchable unlock list** in the side panel with sorting and filtering

#### Screenshots & Notifications
- **Auto-screenshot** when unlocking items (optional)
- **Desktop notifications** for new unlocks (optional)
- **Chat messages** announcing unlocks (optional)

### Group Features

#### Cloud Synchronization (JSONBin.io)
- **Automatic syncing** of unlocks with your group (configurable interval)
- **Real-time sharing** - When anyone unlocks an item, everyone gets it
- **Offline support** - Works offline, syncs when connection restored
- **Unlock attribution** - See which player unlocked each item (optional)

#### Auto Friends Chat Messages
- **Automatic announcement** when you unlock items
- **Message queuing** - Multiple unlocks combine into one message
  - Single: "I unlocked Fire rune for the group!"
  - Multiple: "I unlocked Fire rune, Water rune, Earth rune for the group!"
- **Works with Key Remapping plugin** - Message appears when you press Enter

#### Trading Restrictions
- **Restrict trading to group members only** (optional)
- Configure your group member list
- Prevents accidental trading with non-group players
- Two-layer protection: menu blocking + trade screen validation

#### Character Whitelist
- **Restrict plugin to specific characters** on your Jagex account
- Useful if you have multiple characters but only some are bronzemen
- Prevents non-bronzeman characters from affecting your group
- Automatic detection with retry mechanism (handles slow name loading)

### Chat Cosmetics
- **Bronzeman chat icons** for players on your group member list
- Customize which players get the special icon
- Client-side only (only you see the icons)

---

## Installation

### Prerequisites
- RuneLite client installed
- Java 11 or higher

### Sideloading the Plugin (Custom Version)

1. **Download the Plugin JAR**
   - Build from source or obtain `group-bronzeman-mode-1.2.jar`

2. **Locate Your RuneLite Folder**
   - Windows: `C:\Users\<YourName>\.runelite\`
   - Mac: `~/.runelite/`
   - Linux: `~/.runelite/`

3. **Create Sideloaded Plugins Folder** (if it doesn't exist)
   - Create folder: `.runelite\sideloaded-plugins\`

4. **Copy JAR File**
   - Copy `group-bronzeman-mode-1.2.jar` to the `sideloaded-plugins` folder

5. **Restart RuneLite**
   - Completely close and reopen RuneLite

6. **Enable the Plugin**
   - Click the Plugin Hub icon (puzzle piece 🧩)
   - Search for: **"GBM"** or **"GBM - Group Bronzeman"**
   - Enable the plugin

---

## Getting Started

### First Time Setup

1. **Enable the Plugin**
   - Search for "GBM" in the Plugin Hub
   - Toggle it on

2. **Initial Unlock**
   - On first enable, all items in your inventory and bank are automatically unlocked
   - This ensures you don't lose access to items you already own

3. **Configure Settings** (Optional)
   - Open RuneLite Configuration (⚙️ wrench icon)
   - Scroll to "GBM - Group Bronzeman Mode"
   - Customize your preferences

4. **Set Up Group Sync** (Optional - for group play)
   - See [Group Synchronization Setup](#group-synchronization-setup) section below

### Basic Usage

**Playing Solo:**
- Just play normally! Items unlock automatically as you obtain them
- Check your unlock list anytime by clicking the bronzeman icon in the sidebar

**Playing in a Group:**
- Set up JSONBin.io sync (see below)
- Share your Bin ID with group members
- Everyone's unlocks automatically sync every few minutes

---

## Configuration Guide

### Character Whitelist Section

#### Enable character whitelist
- **Default:** Disabled
- **Purpose:** Restrict the plugin to specific characters on your Jagex account
- **Use case:** You have multiple characters but only some are bronzemen

#### Whitelisted characters
- **Format:** Comma-separated list of character names
- **Example:** `MyIronman, GroupIM, BronzeMan69`
- **Important:** Character names are case-sensitive
- **Behavior:** Non-whitelisted characters will see: "Plugin disabled for this character"

### Bronzeman Names (Group Members)

#### Bronzeman Names
- **Format:** Comma-separated list of group member names
- **Example:** `IronBro, GroupIM, BronzeMan69`
- **Purpose:** This list controls TWO things:
  1. **Chat icons** - These players get a bronzeman icon in chat
  2. **Trading** - If trading restrictions enabled, you can ONLY trade with these players
- **Dynamic:** Can add/remove players mid-session without restarting

### Screenshots Section

#### Take unlock screenshots
- **Default:** Disabled
- **Purpose:** Automatically screenshot when you unlock a new item
- **Location:** RuneLite screenshot folder (usually `.runelite/screenshots/`)

#### Include client frame
- **Default:** Disabled
- **Purpose:** Include the client window frame in screenshots
- **Note:** Only applies if screenshots are enabled

### Notifications Section

#### Send notification on unlock
- **Default:** Disabled
- **Purpose:** Desktop notification when you unlock an item
- **Appearance:** System notification outside the game

#### Send chat message on unlock
- **Default:** Enabled
- **Purpose:** In-game chat message: "You have unlocked a new item: [name]."

#### Auto friends chat message
- **Default:** Disabled
- **Purpose:** Automatically prepare a friends chat message announcing your unlock
- **Format:** "I unlocked [item name] for the group!"
- **Behavior:**
  - Message appears in your chatbox
  - Press Enter to send (or twice with Key Remapping plugin)
  - Multiple unlocks combine: "I unlocked Fire rune, Water rune for the group!"
- **Requirement:** Must be in a friends chat

### Trading Section

#### Allow trading
- **Default:** Disabled (trading restricted)
- **Purpose:** Control who you can trade with
- **When DISABLED:** Can only trade with players in "Bronzeman Names" list
- **When ENABLED:** Can trade with anyone (removes all restrictions)

### Commands Section

#### Enable reset command
- **Default:** Disabled
- **Purpose:** Enables the `!bmreset` command to wipe your unlock list
- **⚠️ WARNING:** This only resets LOCAL unlocks, not the JSONBin cloud storage
- **See:** [Reset Command Warning](#reset-command-warning) section

### Display Section

#### Hide untradeables
- **Default:** Disabled
- **Purpose:** Prevents unlock popups for untradeable items
- **Applies to:** Chat messages, notifications, and overlay graphics

---

## Group Synchronization Setup

### What You'll Need
- Free JSONBin.io account (10,000 requests/month free)
- Internet connection for syncing
- 5 minutes to set up

### Step-by-Step Setup

#### 1. Create JSONBin.io Account

1. Go to https://jsonbin.io
2. Click "Sign Up"
3. Create a free account
4. Verify your email

#### 2. Get Your API Key

1. Log into JSONBin.io
2. Click your profile icon (top-right)
3. Select "API Keys"
4. Copy your **X-Master-Key** (starts with `$2b$...`)
5. Save this somewhere safe - you'll need it

#### 3. Create a Bin (Storage Container)

**Method 1: Via Website (Easiest)**
1. Go to https://jsonbin.io/app/bins
2. Click "Create Bin"
3. Name it something like "OSRS Group Unlocks"
4. In the JSON editor, paste this:
```json
{
  "unlocks": {}
}
```
5. Click "Create"
6. Copy the **Bin ID** (looks like `67a1b2c3d4e5f6g7h8i9j0k1`)

**Method 2: Via API (Advanced)**
1. Use the JSONBin API to create a bin programmatically
2. See [JSONBIN_SETUP_GUIDE.md](JSONBIN_SETUP_GUIDE.md) for details

#### 4. Configure Privacy Settings

**IMPORTANT:** Set your bin to private!

1. Open your bin on JSONBin.io
2. Click "Permissions" or "Settings"
3. Set to **"Private"** - Only you (and people with the Bin ID) can access
4. Save changes

#### 5. Configure Plugin

**In RuneLite:**
1. Open Configuration (⚙️)
2. Find "GBM - Group Bronzeman Mode"
3. Scroll to "Group Synchronization" section
4. Fill in:
   - ✅ **Enable group synchronization:** Check this box
   - 📝 **JSONBin API Key:** Paste your X-Master-Key
   - 📝 **JSONBin Bin ID:** Paste your Bin ID
   - 🏷️ **Group name:** (Optional) e.g., "Iron Squad"
   - ⏱️ **Sync interval:** How often to sync (default: 5 minutes)
   - 👤 **Show who unlocked items:** See attribution (optional)

5. **Save** - The plugin will immediately sync!

#### 6. Share With Your Group

**Give your group members:**
- ✅ The **Bin ID**
- ✅ The **API Key**

**They need to:**
1. Follow steps 1-2 to create their own JSONBin account (optional, for their own keys)
2. OR use your shared API key (easier, but less secure)
3. Configure the plugin with the SAME Bin ID
4. Enable group synchronization

**Security Note:** Anyone with your Bin ID and API key can read/write your group's unlocks. Only share with trusted group members!

### How Syncing Works

#### Automatic Sync
- **Frequency:** Every X minutes (configurable, default 5)
- **What happens:** Plugin fetches new unlocks from JSONBin and merges them into your local list
- **Direction:** Bi-directional (you push your unlocks, you pull others' unlocks)

#### Manual Sync
- **When you unlock an item:** Queued to be pushed on next sync cycle
- **On plugin start:** Immediately syncs from cloud
- **On config change:** Re-initializes sync with new settings

#### Offline Behavior
- **Local unlocks still work** - You don't need internet to play
- **Queued for sync** - Your unlocks are saved locally
- **Automatic catch-up** - When internet returns, syncs automatically

#### Merge Strategy
- **Union merge** - All unlocks from all players are combined
- **No conflicts** - An item is either unlocked or not (no overwriting)
- **Attribution tracking** - Tracks who unlocked each item first (optional display)

### Sync Interval Recommendations

- **1-2 minutes:** Near real-time, uses more API calls
- **5 minutes (default):** Good balance of responsiveness and API usage
- **10-15 minutes:** Conservative, saves API calls
- **30-60 minutes:** Very conservative, suitable for large groups

**API Usage Calculation:**
- Players: 4
- Sync interval: 5 minutes
- Playtime: 3 hours/day average per player
- API calls/day: 4 players × (3 hours × 60 min / 5 min interval) = ~144 calls/day
- Monthly: ~4,300 calls (well within 10,000 free tier limit)

---

## Commands

All commands are typed in the in-game chat.

### !bmunlocks (or !bmcount)
**Purpose:** Display your total unlock count

**Usage:**
```
!bmunlocks
```

**Output:**
```
You have unlocked 1,234 items.
```

### !bmbackup
**Purpose:** Create a backup of your unlock list

**Usage:**
```
!bmbackup
```

**Output:**
```
Successfully backed up file!
```

**Location:** Creates a backup file in your RuneLite plugin folder

### !bmreset
**Purpose:** Reset your unlock list (start fresh)

**Requirements:**
- Must enable "Enable reset command" in config first
- Type the command in chat

**Usage:**
```
!bmreset
```

**Output:**
```
Unlocks successfully reset!
```

**⚠️ WARNING - RESET WITH GROUP SYNC:**

If you have group synchronization enabled, `!bmreset` has important limitations:

**What happens:**
1. ✅ Your LOCAL unlock list is cleared
2. ❌ The JSONBin cloud storage is **NOT** cleared
3. ⏱️ On the next sync (within a few minutes), all unlocks will be pulled back from the cloud
4. 🔄 Your unlock list will be restored to match the group's unlocks

**If you want to fully reset the group's unlocks:**
- You must **manually delete the JSONBin** at https://jsonbin.io
- Create a new bin and update all group members with the new Bin ID
- OR manually edit the bin to remove all unlocks using the JSONBin.io web interface

**Recommendation:** Coordinate with your group before resetting unlocks to avoid confusion.

---

## Advanced Features

### Character Whitelist Details

**Use Case Scenarios:**

**Scenario 1: Multiple Characters on Jagex Account**
- You have 5 characters on your Jagex account
- Only 2 are bronzemen: "IronMain" and "GroupIM"
- Other 3 are normal accounts or different game modes
- **Solution:** Add "IronMain, GroupIM" to whitelist

**Scenario 2: Testing Without Group Impact**
- You want to test something on an alt
- Don't want test account to add unlocks to your group
- **Solution:** Don't add the alt to the whitelist

**How It Works:**
1. **On Login:** Plugin checks if your character name is on the whitelist
2. **If NOT on whitelist:**
   - Plugin disables itself
   - Chat message: "Plugin disabled for this character (not on whitelist)"
   - No unlocks are tracked
   - No group sync occurs
   - GE restrictions don't apply
3. **If on whitelist:**
   - Plugin works normally
   - All features enabled

**Timing & Retry Logic:**
- Plugin checks whitelist when you log in
- If character name isn't loaded yet, retries after 1 second
- If still not loaded, retries after 4 seconds total
- This handles slow login scenarios

**Mid-Session Changes:**
- Can add/remove characters from whitelist without restarting
- Changes take effect on next login
- If you disable plugin and re-enable mid-session, whitelist check runs again

### Message Queuing System

**Problem Solved:** When you unlock multiple items rapidly (e.g., buying a pack of runes), only the last item would be announced.

**Solution:** Message queuing system

**How It Works:**

**Single Unlock:**
```
You unlock: Fire rune
Chatbox shows: "I unlocked Fire rune for the group!"
Press Enter → Sent!
```

**Multiple Unlocks (Before Pressing Enter):**
```
You unlock: Fire rune
Chatbox shows: "I unlocked Fire rune for the group!"

You unlock: Water rune (before pressing Enter)
Chatbox updates: "I unlocked Fire rune, Water rune for the group!"

You unlock: Earth rune (still haven't pressed Enter)
Chatbox updates: "I unlocked Fire rune, Water rune, Earth rune for the group!"

Press Enter → All sent in one message!
```

**Smart Queue Management:**
- Automatically detects when you send the message (chatbox becomes empty)
- Clears the queue after sending
- Next unlock starts a fresh batch
- Works with Key Remapping plugin

**Technical Details:**
- Uses `VarClientStr.CHATBOX_TYPED_TEXT` to read/write chatbox content
- Checks if current message contains "I unlocked" to detect pending messages
- If chatbox is empty or has different text, clears old queue

### Trading Restrictions Details

**How It Works:**

**When "Allow trading" is DISABLED (default):**
- You can **only** trade with players in your "Bronzeman Names (Group Members)" list
- All trade attempts with non-group players are blocked
- Two-layer protection system ensures no bypasses

**Protection Layers:**

**Layer 1: Menu Click Blocking**
- Intercepts all trade-related menu options:
  - "Trade with" (right-click player)
  - "Accept trade" (trade requests from others)
  - Any menu option containing "trade"
- Strips color tags from player names for accurate matching
- Compares against your group member list
- If not on list → trade attempt is blocked
- Chat message: "You can only trade with your group members: [list]"

**Layer 2: Trade Screen Validation** (Currently disabled to prevent script errors)
- Previously detected trade screen opening
- Would auto-close if partner not on list
- Removed due to script compatibility issues
- Layer 1 is sufficient for blocking all trades

**Dynamic Updates:**
- Add/remove players from group list mid-session
- Changes take effect immediately (no restart needed)
- Tested scenarios:
  - ✅ Add player → Can trade immediately
  - ✅ Remove player → Trade blocked immediately
  - ✅ Re-add player → Can trade again

**Edge Cases Handled:**
- Color-coded names (rank indicators, etc.)
- Level indicators in player names
- Special characters
- Case-insensitive matching

---

## Troubleshooting

### Plugin Not Showing Up in RuneLite

**Symptoms:** Can't find "GBM - Group Bronzeman Mode" in Plugin Hub

**Solutions:**

1. **Check JAR Location**
   - Verify `group-bronzeman-mode-1.2.jar` is in `.runelite\sideloaded-plugins\`
   - Check file size (should be 50-60KB)

2. **Check JAR Contents**
   - Open JAR like a ZIP file
   - Verify `runelite-plugin.properties` exists at root level
   - If missing, rebuild with latest code

3. **Try Developer Mode**
   - Close RuneLite
   - Open Command Prompt
   - Navigate to RuneLite folder
   - Run: `RuneLite.exe --developer-mode`

4. **Check for Name Conflicts**
   - Disable any other "Group Bronzeman Mode" plugins
   - Only enable "GBM - Group Bronzeman Mode"

5. **Clear RuneLite Cache**
   - Close RuneLite
   - Delete `.runelite\cache\` folder
   - Restart RuneLite

### Whitelist Not Working

**Symptoms:** Character on whitelist shows "not on whitelist" message

**Solutions:**

1. **Check Character Name Spelling**
   - Names are case-sensitive
   - Check for extra spaces
   - Format: `Character1, Character2, Character3`

2. **Wait for Name to Load**
   - Plugin retries after 1 second, then 4 seconds
   - If you see warning in logs, wait a few seconds

3. **Check Logs**
   - Look for: `"Character 'YourName' is on whitelist. Plugin enabled."`
   - Or: `"Character 'YourName' not on whitelist. Plugin disabled."`

4. **Try Re-logging**
   - Log out and back in
   - Whitelist check runs on every login

### Group Sync Not Working

**Symptoms:** Unlocks not syncing with group

**Common Issues:**

**1. Invalid Credentials**
- **Check:** API Key starts with `$2b$`
- **Check:** Bin ID is the correct format (alphanumeric)
- **Test:** Visit https://jsonbin.io and log in to verify account

**2. Network Issues**
- **Check:** Internet connection working
- **Check:** Firewall not blocking JSONBin.io
- **Test:** Visit https://jsonbin.io in browser

**3. Bin Not Found**
- **Check:** Bin still exists on JSONBin.io
- **Check:** Bin wasn't deleted
- **Solution:** Create new bin, update all group members

**4. API Rate Limit**
- **Check:** Haven't exceeded 10,000 requests/month (free tier)
- **Solution:** Increase sync interval or upgrade JSONBin plan

**5. Whitelist Blocking Sync**
- **Check:** Character is on whitelist
- Non-whitelisted characters don't sync by design

**Debug Steps:**

1. **Check RuneLite Logs**
   - Look for: `"Initializing group sync..."`
   - Look for: `"Synced X new unlocks from your group!"`
   - Look for errors about API keys or network

2. **Test Manual Sync**
   - Disable then re-enable "Enable group synchronization"
   - Watch for sync messages

3. **Verify Bin Contents**
   - Log into JSONBin.io
   - Open your bin
   - Check if `"unlocks": {...}` has data

### Auto Friends Chat Message Not Working

**Symptoms:** Message doesn't appear or doesn't send

**Solutions:**

1. **Not in Friends Chat**
   - Must be in a friends chat for feature to work
   - Plugin checks `client.getFriendsChatManager()`
   - If null, silently skips

2. **Key Remapping Plugin**
   - With Key Remapping: Press Enter TWICE
   - First Enter opens chat (shows message)
   - Second Enter sends message
   - This is expected behavior

3. **Multiple Unlocks**
   - If unlocking multiple items, wait for queue to build
   - Final message shows all items
   - Press Enter when ready

4. **Message Not Clearing**
   - If old message persists, manually clear chatbox
   - Type something else or press Escape
   - Next unlock will start fresh

### Trading Restrictions Not Working

**Symptoms:** Can trade with non-group members

**Solutions:**

1. **Check "Allow Trading" Setting**
   - Must be DISABLED (unchecked) for restrictions to apply
   - If enabled, all trades are allowed

2. **Check Group Members List**
   - Verify player name is spelled correctly
   - Format: `Player1, Player2, Player3`
   - Case-insensitive matching

3. **Update Group List Mid-Session**
   - Add player to list
   - No restart needed
   - Changes apply immediately

4. **Check Logs**
   - Look for: `"Trade attempt with: 'PlayerName'"`
   - Look for: `"Comparing 'PlayerName' with group member 'X'"`
   - Should show: `"Allowing trade with group member"` or `"Blocking trade"`

---

## FAQ

### General Questions

**Q: Can I use this plugin on multiple accounts?**
A: Yes! Use the character whitelist feature to control which characters use the plugin.

**Q: Is this plugin allowed by Jagex?**
A: This is a client-side plugin that doesn't interact with game servers in any prohibited way. It's similar to other RuneLite plugins. However, always check Jagex's current rules.

**Q: Can I play solo without group sync?**
A: Absolutely! Just don't enable "Enable group synchronization" in the config.

**Q: How do I know what items I've unlocked?**
A: Click the bronzeman icon in the RuneLite sidebar to open your unlock panel.

### Group Sync Questions

**Q: Do all group members need to use the same plugin?**
A: Yes, all members should use GBM - Group Bronzeman Mode with the same Bin ID.

**Q: What happens if someone leaves the group?**
A: Their unlocks remain in the shared storage. You can create a new bin if you want to start fresh.

**Q: Can we have more than 5 players?**
A: Yes! The group size is unlimited. Just watch your API call usage with large groups.

**Q: What if two people unlock the same item at the same time?**
A: No problem! The merge strategy handles this. Both unlocks are recorded, and everyone gets the item.

**Q: How much does JSONBin cost?**
A: Free tier includes 10,000 requests/month, which is plenty for most groups. Paid plans available if needed.

**Q: Can I see my group's progress online?**
A: Yes! Log into JSONBin.io and view your bin to see all unlocks.

### Technical Questions

**Q: Where are my unlocks stored locally?**
A: In your RuneLite config folder under `.runelite/` in a file specific to your account.

**Q: What happens if I delete my local unlock file?**
A: If group sync is enabled, your unlocks will be restored from the cloud on next sync. If sync is disabled, you'll lose your progress.

**Q: Can I export my unlock list?**
A: Yes! Use the `!bmbackup` command to create a backup file.

**Q: Does this plugin affect game performance?**
A: No significant impact. Network sync happens in the background at configurable intervals.

**Q: What are the class files named "AnotherBronzemanMode"?**
A: Internal code names kept for compatibility. The plugin displays as "GBM - Group Bronzeman Mode" to users.

### Feature Questions

**Q: Can I customize the unlock notification?**
A: Yes! You can toggle screenshots, desktop notifications, chat messages, and auto friends chat messages independently.

**Q: Does the character whitelist affect group sync?**
A: Yes! Non-whitelisted characters don't sync to prevent them from affecting your group.

**Q: Can I trade with group members even if trading is restricted?**
A: Yes! Add them to your "Bronzeman Names (Group Members)" list. They'll be allowed to trade with you.

**Q: What's the difference between "Bronzeman Names" and "Whitelisted Characters"?**
A:
- **Whitelisted Characters:** YOUR characters (on your Jagex account) that can use the plugin
- **Bronzeman Names:** OTHER PLAYERS (group members) who get chat icons and trading permissions

---

## Credits

### Main Author
- **tugglicious** - GBM - Group Bronzeman Mode implementation and modifications

### Honorable Mentions - Another Bronzeman Mode Creators
- **[CodePanter](https://github.com/codepanter)** - Creator of [Another Bronzeman Mode](https://github.com/CodePanter/another-bronzeman-mode), which this plugin is based on
- **[Sethrem](https://github.com/sethrem)** - Author of the [initial Bronzeman Mode plugin](https://github.com/sethrem/bronzeman)
- **[Dekvall](https://github.com/dekvall)** - Code improvements in their [bronzeman-mode version](https://github.com/dekvall/bronzeman-mode)
- **[Robin Withes](https://github.com/robinwithes)** - Unlock-list backup feature, reset unlocks feature, collection log integration, and search functionality

### Original Concept
- **[GUDI (Mod Ronan)](https://www.youtube.com/watch?v=GFNfa2saOJg)** - First envisioned the Bronzeman mode concept

---

## Support

### Getting Help

- **GitHub Issues:** https://github.com/tugglicious/GroupBronzeManMode/issues
- **Discord:** (If applicable, add your Discord server link)

### Reporting Bugs

When reporting bugs, please include:
1. RuneLite version
2. Plugin version (1.2)
3. Steps to reproduce the issue
4. Screenshots if applicable
5. Relevant log output from RuneLite

### Feature Requests

Have an idea for a new feature? Open an issue on GitHub with the "enhancement" label!

---

## Version History

### Version 1.2 (Current)
- ✨ Added group synchronization via JSONBin.io
- ✨ Added character whitelist feature
- ✨ Added auto friends chat message feature
- ✨ Added message queuing for multiple unlocks
- ✨ Added trading restrictions with group member list
- 🐛 Fixed null player name handling in whitelist check
- 🐛 Fixed color tag handling in player name matching
- 🐛 Fixed whitelist bypass via config changes
- 🐛 Fixed script errors with auto-close trade screen
- 🐛 Fixed missing runelite-plugin.properties in JAR
- 📝 Renamed plugin to "GBM - Group Bronzeman Mode" to avoid conflicts
- 📝 Updated documentation and setup guides

### Version 1.0-1.1 (Previous - Another Bronzeman Mode Base)
- Original Another Bronzeman Mode features
- Unlock tracking and GE restrictions
- Screenshot capabilities
- Chat icons for bronzemen
- Commands (!bmunlocks, !bmreset, !bmbackup)

---

**Thank you for using GBM - Group Bronzeman Mode! Enjoy your adventure! 🎮**
