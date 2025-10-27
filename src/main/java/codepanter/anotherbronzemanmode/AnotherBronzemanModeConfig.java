package codepanter.anotherbronzemanmode;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(AnotherBronzemanModePlugin.CONFIG_GROUP)
public interface AnotherBronzemanModeConfig extends Config
{
    // Character Whitelist Section
    @ConfigSection(
        name = "Character Whitelist",
        description = "Restrict plugin to specific characters on your account",
        position = 0,
        closedByDefault = false
    )
    String whitelistSection = "whitelist";

    @ConfigItem(
        keyName = "enableWhitelist",
        name = "Enable character whitelist",
        description = "Only run the plugin for specific characters. Useful if you have multiple characters but only some are bronzemen.",
        section = whitelistSection,
        position = 0
    )
    default boolean enableWhitelist()
    {
        return false;
    }

    @ConfigItem(
        keyName = "whitelistedCharacters",
        name = "Whitelisted characters",
        description = "Comma-separated list of character names allowed to use this plugin. Example: MyIronman, GroupIM, BronzeMan69",
        section = whitelistSection,
        position = 1
    )
    default String whitelistedCharacters()
    {
        return "";
    }

    @ConfigItem(
            keyName = "namesBronzeman",
            name = "Bronzeman Names (Group Members)",
            position = 2,
            description = "Comma-separated list of your group members. These players will: (1) Have a bronzeman icon in chat, and (2) Be allowed to trade with you. Example: IronBro, GroupIM, BronzeMan69"
    )
    default String namesBronzeman()
    {
        return "";
    }

    @ConfigItem(
            keyName = "screenshotUnlock",
            name = "Screenshot new Unlocks",
            position = 2,
            description = "Take a screenshot whenever a new item is unlocked."
    )
    default boolean screenshotUnlock()
    {
        return false;
    }

    @ConfigItem(
            keyName = "includeFrame",
            name = "Include Client Frame",
            description = "Configures whether or not the client frame is included in screenshots.",
            position = 3
    )
    default boolean includeFrame()
    {
        return false;
    }

    @ConfigItem(
        keyName = "sendNotification",
        name = "Notify on unlock",
        description = "Send a notification when a new item is unlocked.",
        position = 4
    )
    default boolean sendNotification()
    {
        return false;
    }

    @ConfigItem(
        keyName = "sendChatMessage",
        name = "Chat message on unlock",
        description = "Send a chat message when a new item is unlocked.",
        position = 5
    )
    default boolean sendChatMessage()
    {
        return false;
    }

    @ConfigItem(
        keyName = "autoFriendsChatMessage",
        name = "Auto friends chat message",
        description = "Automatically send a message to friends chat when you unlock an item (e.g., 'I unlocked Fire rune for the group!'). Requires being in a friends chat.",
        position = 6
    )
    default boolean autoFriendsChatMessage()
    {
        return false;
    }

    @ConfigItem(
        keyName = "allowTrading",
        name = "Allow trading with everyone",
        description = "Allow trading with ALL players (not just group members). By default, you can only trade with players in 'Bronzeman Names (Group Members)'. Enable this to remove all trading restrictions.",
        position = 7
    )
    default boolean allowTrading()
    {
        return false;
    }

    @ConfigItem(
        keyName = "resetCommand",
        name = "Enable reset command",
        description = "Enables the !bmreset command used for wiping your unlocked items.",
        position = 8
    )
    default boolean resetCommand()
    {
        return false;
    }

    @ConfigItem(
    	keyName = "hideUntradeables",
		name = "Hide untradeable item unlocks",
		description = "Prevents unlock popups for untradeable items and in chat/notifications",
		position = 9
	)
	default boolean hideUntradeables() {
    	return false;
	}

	// Group Sync Section
	@ConfigSection(
		name = "Group Sync (JSONBin.io)",
		description = "Synchronize unlocks with your group using JSONBin.io cloud storage",
		position = 10,
		closedByDefault = true
	)
	String groupSyncSection = "groupSync";

	@ConfigItem(
		keyName = "enableGroupSync",
		name = "Enable group synchronization",
		description = "Share unlocks with other players in your group via JSONBin.io",
		section = groupSyncSection,
		position = 0
	)
	default boolean enableGroupSync()
	{
		return false;
	}

	@ConfigItem(
		keyName = "jsonbinApiKey",
		name = "JSONBin API Key",
		description = "Your JSONBin.io API key (X-Master-Key). Get it from jsonbin.io after creating an account.",
		section = groupSyncSection,
		position = 1
	)
	default String jsonbinApiKey()
	{
		return "";
	}

	@ConfigItem(
		keyName = "jsonbinBinId",
		name = "JSONBin Bin ID",
		description = "Your group's Bin ID. Create a new bin at jsonbin.io and share this ID with your group.",
		section = groupSyncSection,
		position = 2
	)
	default String jsonbinBinId()
	{
		return "";
	}

	@ConfigItem(
		keyName = "groupName",
		name = "Group name",
		description = "Your group's name (optional, for display purposes only)",
		section = groupSyncSection,
		position = 3
	)
	default String groupName()
	{
		return "My Group";
	}

	@ConfigItem(
		keyName = "syncInterval",
		name = "Sync interval (minutes)",
		description = "How often to automatically sync unlocks from JSONBin (1-60 minutes)",
		section = groupSyncSection,
		position = 4
	)
	default int syncInterval()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "showWhoUnlocked",
		name = "Show who unlocked items",
		description = "Display which player unlocked each item in notifications",
		section = groupSyncSection,
		position = 5
	)
	default boolean showWhoUnlocked()
	{
		return true;
	}
}
