# JSONBin.io Setup Guide for Group Bronzeman Mode

This guide will help you set up group synchronization using JSONBin.io cloud storage.

## What is JSONBin.io?

JSONBin.io is a free cloud storage service for JSON data. We use it to store and synchronize your group's unlocked items across all players.

## Step-by-Step Setup

### Step 1: Create a JSONBin Account

1. Go to [https://jsonbin.io](https://jsonbin.io)
2. Click "Sign Up" (top right)
3. Create a free account using your email
4. Verify your email address

### Step 2: Get Your API Key

1. After logging in, click your profile icon (top right)
2. Select "API Keys" from the dropdown
3. You should see your "X-Master-Key" listed
4. Click the copy icon to copy your API key
5. **IMPORTANT:** Keep this key safe! Don't share it publicly.

Example API key format: `$2a$10$abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGH`

### Step 3: Create a Bin for Your Group

1. Click "Create Bin" (top right, green button)
2. In the JSON editor, paste this initial structure:
   ```json
   {
     "unlockedItems": {},
     "lastUpdated": 0
   }
   ```
3. Click "Create" at the bottom
4. After creating, you'll see your Bin ID in the URL bar
5. The URL will look like: `https://jsonbin.io/app/bins/65a1b2c3d4e5f6g7h8i9j0k1`
6. Your Bin ID is the part after `/bins/`: `65a1b2c3d4e5f6g7h8i9j0k1`
7. Copy this Bin ID

### Step 4: Configure Privacy Settings

1. On your bin page, click the "Settings" tab
2. Under "Privacy", select one of these options:
   - **Private** (recommended): Only people with your API key can access
   - **Public** (not recommended for group play): Anyone can read, but only you can write

### Step 5: Configure the Plugin

1. Open RuneLite
2. Click the wrench icon (Configuration)
3. Search for "Another Bronzeman Mode" or "Group Bronzeman"
4. Find the "Group Sync (JSONBin.io)" section
5. Enter your configuration:
   - **Enable group synchronization**: ✅ Check this box
   - **JSONBin API Key**: Paste your X-Master-Key from Step 2
   - **JSONBin Bin ID**: Paste your Bin ID from Step 3
   - **Group name**: Enter a friendly name (e.g., "Iron Bros")
   - **Sync interval**: How often to sync (default: 5 minutes)
   - **Show who unlocked items**: ✅ Check to see who unlocked each item

### Step 6: Share with Your Group

To add more players to your group, share with them:

1. **The Bin ID**: `65a1b2c3d4e5f6g7h8i9j0k1`
2. **The API Key**: `$2a$10$abcdefgh...` (from Step 2)

**SECURITY NOTE:** Anyone with your API key can read AND write to your bin. Only share with trusted group members!

Each group member should:
1. Follow Steps 1-2 to create their own JSONBin account (optional, for their own API keys)
2. OR use the shared API key and Bin ID
3. Configure their plugin with the same Bin ID and API key

## How It Works

Once configured:

- ✅ **Auto-sync**: Every X minutes (configurable), your plugin will check for new unlocks
- ✅ **Push on unlock**: When you unlock a new item, it's queued to push to the cloud
- ✅ **Merge strategy**: Unlocks from all players are combined (union)
- ✅ **Attribution**: Each unlock records who unlocked it
- ✅ **Offline support**: Local unlocks still work if sync fails

## Troubleshooting

### "Failed to fetch from JSONBin"
- Check your API key and Bin ID are correct
- Ensure you have internet connection
- Verify your JSONBin account is active

### "No pending unlocks to push"
- This is normal! It means everything is synced
- New unlocks will automatically queue for pushing

### Items not syncing
- Check "Enable group synchronization" is checked
- Verify all group members use the same Bin ID
- Wait for the sync interval (default 5 minutes)
- Or restart the plugin to force a sync

### Rate Limits
JSONBin free tier allows:
- 10,000 requests per month
- With default 5-minute sync intervals, you can sync 24/7 for the whole month

## Testing Your Setup

1. Enable group sync and configure your API key + Bin ID
2. Unlock a new item in-game (pick up something you've never had)
3. Wait for the sync interval or restart the plugin
4. Check your bin at jsonbin.io - you should see the item in `unlockedItems`
5. Have a group member configure the same Bin ID and restart their plugin
6. They should see your unlock!

## Privacy & Security

- ✅ Your API key is stored locally in RuneLite config
- ✅ JSONBin uses HTTPS encryption
- ⚠️ Anyone with your API key can access your bin
- ⚠️ Don't share your API key publicly (Discord, Twitch, etc.)

## Free Tier Limits

JSONBin.io free tier includes:
- ✅ Unlimited bins
- ✅ 10,000 requests/month
- ✅ 100 KB per bin
- ✅ SSL encryption

This is more than enough for a group of 5-10 players!

## Advanced: Multiple Groups

To play in multiple groups:
1. Create a separate bin for each group
2. Copy the Bin ID for each group
3. Change the "JSONBin Bin ID" in config when switching groups
4. You can use the same API key for all your bins

## Need Help?

- Visit the [GitHub Issues](https://github.com/tugglicious/GroupBronzeManMode/issues)
- Read the [JSONBin.io Documentation](https://jsonbin.io/api-reference)
- Ask in the OSRS community Discord servers

---

**Happy grinding with your group!** 🥉⚒️
