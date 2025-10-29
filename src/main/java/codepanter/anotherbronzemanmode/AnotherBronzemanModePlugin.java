/*
 * Group Bronzeman Mode
 * Copyright (c) 2025, tugglicious <https://github.com/tugglicious>
 *
 * Based on Another Bronzeman Mode
 * Original Copyright (c) 2019, CodePanter <https://github.com/codepanter>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package codepanter.anotherbronzemanmode;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.events.PluginChanged;
import net.runelite.api.ChatMessageType;
import net.runelite.api.widgets.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.Text;
import net.runelite.client.game.WorldService;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.ClientToolbar;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
        name = "GBM - Group Bronzeman Mode",
        description = "GBM - Group Bronzeman Mode - Limits Grand Exchange access to only allow buying items obtained 'the Ironman way'. Supports group sync via JSONBin.io.",
        tags = {"overlay", "bronzeman", "group", "gbm"}
)
public class AnotherBronzemanModePlugin extends Plugin
{
    static final String CONFIG_GROUP = "anotherbronzemanmode";
    private static final String BM_UNLOCKS_STRING = "!bmunlocks";
    private static final String BM_COUNT_STRING = "!bmcount";
    private static final String BM_RESET_STRING = "!bmreset";
    private static final String BM_BACKUP_STRING = "!bmbackup";

    private static final int GE_SEARCH_BUILD_SCRIPT = 751;

    private boolean LOGGING_IN = false;

    static final Set<Integer> OWNED_INVENTORY_IDS = ImmutableSet.of(
            0,    // Reward from fishing trawler.
            93,   // Standard player inventory.
            94,   // Equipment inventory.
            95,   // Bank inventory.
            141,  // Barrows reward chest inventory.
            390,  // Kingdom Of Miscellania reward inventory.
            581,  // Chambers of Xeric chest inventory.
            612,  // Theater of Blood reward chest inventory (Raids 2).
            626); // Seed vault located inside the Farming Guild.

    @Inject
    private Client client;

    @Inject
    private ChatboxPanelManager chatboxPanelManager;

    @Inject
    private Notifier notifier;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WorldService worldService;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private ChatCommandManager chatCommandManager;

    @Inject
    private AnotherBronzemanModeConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    private AnotherBronzemanModePanel panel;

    private NavigationButton navButton;

    @Inject
    private AnotherBronzemanModeOverlay AnotherBronzemanModeOverlay;

    @Inject
    private ScheduledExecutorService executor;

    private List<Integer> unlockedItems;

    // Group sync fields
    private GroupSyncManager groupSyncManager;
    private JsonBinClient jsonBinClient;
    private ScheduledFuture<?> syncTask;

    @Getter
    private BufferedImage unlockImage = null;

    private static final String SCRIPT_EVENT_SET_CHATBOX_INPUT = "setChatboxInput";

    private ChatboxTextInput searchInput;
    private Widget searchButton;
    private Collection<Widget> itemEntries;

    private List<String> namesBronzeman = new ArrayList<>();
    private int bronzemanIconOffset = -1; // offset for bronzeman icon
    private boolean onSeasonalWorld;
    private boolean disabledByWhitelist = false; // Track if plugin is disabled due to character not on whitelist
    private List<String> pendingFCMessages = new ArrayList<>(); // Queue for multiple unlock messages
    private File legacyFile;
    private File legacyFolder;
    private File profileFile;
    private File profileFolder;
    private String profileKey;

    // current version of the plugin
    private String currentVersion;

    @Provides
    AnotherBronzemanModeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AnotherBronzemanModeConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        super.startUp();
        onSeasonalWorld = false;
        updateNamesBronzeman();
        updateScreenshotUnlock();
        loadResources();
        unlockedItems = new ArrayList<>();
        overlayManager.add(AnotherBronzemanModeOverlay);
        chatCommandManager.registerCommand(BM_UNLOCKS_STRING, this::OnUnlocksCountCommand);
        chatCommandManager.registerCommand(BM_COUNT_STRING, this::OnUnlocksCountCommand);
        chatCommandManager.registerCommand(BM_BACKUP_STRING, this::OnUnlocksBackupCommand);

        // get current version of the plugin using properties file generated by build.gradle
        try
        {
            final Properties props = new Properties();
            InputStream is = AnotherBronzemanModePlugin.class.getResourceAsStream("/anotherbronzemanmode_version.txt");
            props.load(is);
            this.currentVersion = props.getProperty("version");
        }
        catch (Exception e)
        {
            log.warn("Could not determine current plugin version", e);
            this.currentVersion = "";
        }

        if (config.resetCommand())
        {
            chatCommandManager.registerCommand(BM_RESET_STRING, this::OnUnlocksResetCommand);
        }

        panel = injector.getInstance(AnotherBronzemanModePanel.class);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/bronzeman_icon.png");

        navButton = NavigationButton.builder()
                .tooltip("GBM - Group Bronzeman Mode")
                .icon(icon)
                .panel(panel)
                .priority(6)
                .build();

        clientToolbar.addNavigation(navButton);

        // Check whitelist if already logged in (plugin enabled mid-session)
        clientThread.invoke(() ->
        {
            if (client.getGameState() == GameState.LOGGED_IN)
            {
                // Try to check whitelist immediately
                boolean nameAvailable = checkCharacterWhitelist();

                // If name wasn't available, schedule retries
                if (!nameAvailable)
                {
                    log.info("Scheduling delayed whitelist check retries...");
                    // Retry after 1 second
                    executor.schedule(() -> clientThread.invoke(() -> {
                        if (!checkCharacterWhitelist() && config.enableWhitelist())
                        {
                            // If still not available after 1s, try again after 3s more
                            executor.schedule(() -> clientThread.invoke(() -> {
                                checkCharacterWhitelist();
                            }), 3, TimeUnit.SECONDS);
                        }
                    }), 1, TimeUnit.SECONDS);
                }

                if (!disabledByWhitelist)
                {
                    onSeasonalWorld = isSeasonalWorld(client.getWorld());
                    // A player can not be a bronzeman on a seasonal world.
                    if (!onSeasonalWorld)
                    {
                        setChatboxName(getNameChatbox());
                    }
                }
            }
        });

        // Initialize group sync if enabled (after whitelist check)
        if (config.enableGroupSync() && !disabledByWhitelist)
        {
            initializeGroupSync();
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        super.shutDown();
        itemEntries = null;
        unlockedItems = null;
        overlayManager.remove(AnotherBronzemanModeOverlay);
        chatCommandManager.unregisterCommand(BM_UNLOCKS_STRING);
        chatCommandManager.unregisterCommand(BM_COUNT_STRING);
        chatCommandManager.unregisterCommand(BM_BACKUP_STRING);
        if (config.resetCommand())
        {
            chatCommandManager.unregisterCommand(BM_RESET_STRING);
        }

        clientToolbar.removeNavigation(navButton);

        // Stop group sync
        stopGroupSync();

        clientThread.invoke(() ->
        {
            // Cleanup is not required after having played on a seasonal world.
            if (client.getGameState() == GameState.LOGGED_IN && !onSeasonalWorld)
            {
                setChatboxName(getNameDefault());
            }
        });
    }

    /** Loads players unlocks on login **/
    @Subscribe
    public void onGameStateChanged(GameStateChanged e)
    {
        if (e.getGameState() == GameState.LOGGING_IN) {
            LOGGING_IN = true; // Set when logging in
        }
        if (e.getGameState() == GameState.LOGGED_IN && LOGGING_IN)
        {
            LOGGING_IN = false; // Makes sure this only happens when having just logged in; not when the state changed from 'LOADING'.

            // Check character whitelist
            boolean nameAvailable = checkCharacterWhitelist();

            // If name wasn't available, schedule retries
            if (!nameAvailable)
            {
                log.info("Player name not loaded at login event, scheduling retries...");
                // Retry after 1 second
                executor.schedule(() -> clientThread.invoke(() -> {
                    if (!checkCharacterWhitelist() && config.enableWhitelist())
                    {
                        // If still not available after 1s, try again after 3s more
                        executor.schedule(() -> clientThread.invoke(() -> {
                            checkCharacterWhitelist();
                        }), 3, TimeUnit.SECONDS);
                    }
                }), 1, TimeUnit.SECONDS);
            }

            if (disabledByWhitelist)
            {
                return; // Exit early - don't load unlocks or enable features
            }

            setupUnlockHistory();
            loadPlayerUnlocks();
            loadResources();
            onSeasonalWorld = isSeasonalWorld(client.getWorld());

            if (!getSavedVersionString().equals(getCurrentVersionString()))
            {
                final String updateDescription = "The unlocked items list has been moved out of the collection log, " +
                        "and into a new side-panel; and now comes with additional sorting and filtering options! " +
                        "To stop this message from appearing every time you log in; " +
                        "try out the new functionality by clicking the 'View Unlocked Items' button in the side-panel!";
                final String message = new ChatMessageBuilder()
                        .append(Color.red, "Group ")
                        .append(Color.orange, "Bronzeman ")
                        .append(Color.red, "Mode update: ")
                        .append(Color.blue, updateDescription)
                        .build();

                chatMessageManager.queue(
                        QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(message)
                                .build());
            }
        }
        if (e.getGameState() == GameState.LOGIN_SCREEN)
        {
            itemEntries = null;
        }
    }

    @Subscribe
    public void onPluginChanged(PluginChanged e)
    {
        if (e.getPlugin() == this && client.getGameState() == GameState.LOGGED_IN)
        {
            setupUnlockHistory();
            loadPlayerUnlocks();
        }
    }

    /** Unlocks all new items that are currently not unlocked **/
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged e)
    {
        if (OWNED_INVENTORY_IDS.contains(e.getContainerId()))
        {
            unlockItemContainerItems(e.getItemContainer());
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (event.getScriptId() == GE_SEARCH_BUILD_SCRIPT) {
            killSearchResults();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (disabledByWhitelist)
        {
            return; // Allow all trading if character not on whitelist
        }

        String menuOption = event.getMenuOption();

        // Catch all trade-related menu options
        if (menuOption.equals("Trade with") ||
            menuOption.equals("Accept trade") ||
            menuOption.contains("trade")) {

            // If "Allow trading" is enabled, allow all trades
            if (config.allowTrading()) {
                return;
            }

            // Get the target player name and remove color tags
            String targetPlayer = Text.removeTags(event.getMenuTarget());

            // Remove any level indicators or extra text (e.g., "PlayerName (level-126)")
            if (targetPlayer.contains("(")) {
                targetPlayer = targetPlayer.substring(0, targetPlayer.indexOf("(")).trim();
            }

            log.info("Trade attempt with: '{}' (raw target: '{}') | Group list has {} members", targetPlayer, event.getMenuTarget(), namesBronzeman != null ? namesBronzeman.size() : 0);

            // Check if target is on the bronzeman names list (group members)
            if (namesBronzeman != null && !namesBronzeman.isEmpty()) {
                for (String groupMember : namesBronzeman) {
                    String cleanGroupMember = groupMember.trim();
                    log.info("Comparing '{}' with group member '{}'", targetPlayer, cleanGroupMember);
                    if (targetPlayer.equalsIgnoreCase(cleanGroupMember)) {
                        log.info("Allowing trade with group member: {}", targetPlayer);
                        // Allow trade with group members
                        return;
                    }
                }
            }

            // Block trade - not a group member
            log.info("Blocking trade with non-group member: '{}'. Current group list: {}", targetPlayer, namesBronzeman);
            event.consume();
            sendChatMessage("You can only trade with your group members: " + config.namesBronzeman());
            return;
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (disabledByWhitelist)
        {
            return;
        }

        // Trade screen widget IDs - We no longer auto-close trade screens here
        // The MenuOptionClicked handler is sufficient for blocking trades
        // Removed auto-close to prevent script errors and game freezing
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent)
    {
        if (scriptCallbackEvent.getEventName().equals(SCRIPT_EVENT_SET_CHATBOX_INPUT) && !onSeasonalWorld)
        {
            setChatboxName(getNameChatbox());
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (client.getGameState() != GameState.LOADING && client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        String name = Text.removeTags(chatMessage.getName());
        switch (chatMessage.getType())
        {
            case PRIVATECHAT:
            case MODPRIVATECHAT:
                // Note this is unable to change icon on PMs if they are not a friend or in friends chat
            case CLAN_CHAT:
            case CLAN_GUEST_CHAT:
            case FRIENDSCHAT:
                if (isChatPlayerOnNormalWorld(name) && isChatPlayerBronzeman(name))
                {
                    addBronzemanIconToMessage(chatMessage);
                }
                break;
            case PUBLICCHAT:
            case MODCHAT:
                if (!onSeasonalWorld && isChatPlayerBronzeman(name))
                {
                    addBronzemanIconToMessage(chatMessage);
                }
                break;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals(CONFIG_GROUP))
        {
            if (event.getKey().equals("namesBronzeman"))
            {
                updateNamesBronzeman();
            }
            else if (event.getKey().equals("screenshotUnlock") || event.getKey().equals("includeFrame"))
            {
                updateScreenshotUnlock();
            }
            else if (event.getKey().equals("resetCommand"))
            {
                if (config.resetCommand())
                {
                    chatCommandManager.registerCommand(BM_RESET_STRING, this::OnUnlocksResetCommand);
                }
                else
                {
                    chatCommandManager.unregisterCommand(BM_RESET_STRING);
                }
            }
            else if (event.getKey().equals("enableGroupSync"))
            {
                if (config.enableGroupSync() && !disabledByWhitelist)
                {
                    initializeGroupSync();
                }
                else
                {
                    stopGroupSync();
                }
            }
            else if (event.getKey().equals("jsonbinApiKey") || event.getKey().equals("jsonbinBinId"))
            {
                // Reinitialize if config changes (only if whitelisted)
                if (config.enableGroupSync() && !disabledByWhitelist)
                {
                    stopGroupSync();
                    initializeGroupSync();
                }
            }
            else if (event.getKey().equals("syncInterval"))
            {
                // Restart sync task with new interval (only if whitelisted)
                if (config.enableGroupSync() && syncTask != null && !disabledByWhitelist)
                {
                    stopGroupSync();
                    initializeGroupSync();
                }
            }
        }
    }

    public void unlockFilter(boolean showUntradeableItems, SortOption sortOption, String search)
    {
        List<ItemObject> filteredItems = new ArrayList<ItemObject>();

        for (Integer itemID : unlockedItems) {
            ItemComposition composition = client.getItemDefinition(itemID);

            boolean tradeable = composition.isTradeable();
            if (!showUntradeableItems && !tradeable) continue;

            String itemName = composition.getMembersName();
            if (!search.isEmpty() && !itemName.toLowerCase().contains(search)) continue;

            AsyncBufferedImage icon = itemManager.getImage(itemID);

            ItemObject item = new ItemObject(itemID, itemName, tradeable, icon);
            filteredItems.add(item);
        }

        if (sortOption.name() == "NEW_TO_OLD")
        {
            Collections.reverse(filteredItems);
        }

        if (sortOption.name() == "ALPHABETICAL_ASC")
        {
            Collections.sort(filteredItems,new Comparator<ItemObject>() {
                @Override
                public int compare(ItemObject i1, ItemObject i2) {
                    return i1.getName().compareToIgnoreCase(i2.getName());
                }
            });
        }

        if (sortOption.name() == "ALPHABETICAL_DESC")
        {
            Collections.sort(filteredItems,new Comparator<ItemObject>() {
                @Override
                public int compare(ItemObject i1, ItemObject i2) {
                    return i2.getName().compareToIgnoreCase(i1.getName());
                }
            });
        }

        panel.displayItems(filteredItems); // Redraw the panel
    }

    /** Unlocks all items in the given item container. **/
    public void unlockItemContainerItems(ItemContainer itemContainer)
    {
        if (disabledByWhitelist)
        {
            return; // Skip if character not on whitelist
        }

        for (Item i : itemContainer.getItems())
        {
            int itemId = i.getId();
            int realItemId = itemManager.canonicalize(itemId);
            ItemComposition itemComposition = itemManager.getItemComposition(itemId);
            int noteId = itemComposition.getNote();
            boolean tradeable = itemComposition.isTradeable();
            if (itemId != realItemId && noteId != 799) continue;  // The 799 signifies that it is a noted item
            if (i.getId() <= 1) continue;
            if (i.getQuantity() <= 0) continue;
            if (!unlockedItems.contains(realItemId))
            {
                queueItemUnlock(realItemId);
				if (config.hideUntradeables() && !tradeable) continue;

                String itemName = client.getItemDefinition(realItemId).getMembersName();

                if (config.sendNotification())
                {
                    notifier.notify("You have unlocked a new item: " + itemName + ".");
                }
                else if (config.sendChatMessage())
                {
                    sendChatMessage("You have unlocked a new item: " + itemName + ".");
                }

                // Auto friends chat message
                if (config.autoFriendsChatMessage())
                {
                    sendFriendsChatMessage(itemName);
                }
            }
        }
    }

    public String getSavedVersionString()
    {
        final String versionStr = configManager.getConfiguration(CONFIG_GROUP, "version");
        return versionStr == null ? "" : versionStr;
    }

    public void setSavedVersionString(final String newVersion)
    {
        configManager.setConfiguration(CONFIG_GROUP, "version", newVersion);
    }

    public String getCurrentVersionString()
    {
        return currentVersion;
    }

    /** Queues a new unlock to be properly displayed **/
    public void queueItemUnlock(int itemId)
    {
        if (disabledByWhitelist)
        {
            return; // Skip if character not on whitelist (failsafe)
        }

        unlockedItems.add(itemId);

        panel.displayItems(new ArrayList<ItemObject>()); // Redraw the panel

		boolean tradeable = itemManager.getItemComposition(itemId).isTradeable();
		if (!(config.hideUntradeables() && !tradeable)) AnotherBronzemanModeOverlay.addItemUnlock(itemId);
        savePlayerUnlocks();// Save after every item to fail-safe logging out

        // Queue for group sync if enabled
        if (config.enableGroupSync() && groupSyncManager != null)
        {
            String itemName = client.getItemDefinition(itemId).getMembersName();
            String playerName = client.getLocalPlayer() != null ? client.getLocalPlayer().getName() : "Unknown";
            groupSyncManager.queueUnlock(itemId, itemName, playerName);
        }
    }

    /** Queues the removal of an unlocked item **/
    public void queueItemDelete(int itemId)
    {
        unlockedItems.remove(Integer.valueOf(itemId));
        savePlayerUnlocks(); // Save after every item to fail-safe logging out
    }

    /** Unlocks default items like a bond to a newly made profile **/
    private void unlockDefaultItems()
    {
        queueItemUnlock(ItemID.COINS_995);
        queueItemUnlock(ItemID.OLD_SCHOOL_BOND);
    }

    public void sendChatMessage(String chatMessage)
    {
        final String message = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append(chatMessage)
            .build();

        chatMessageManager.queue(
            QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(message)
                .build());
    }

    /**
     * Queue a friends chat message for the next unlock
     * Multiple messages will be combined into one
     */
    private void sendFriendsChatMessage(String itemName)
    {
        clientThread.invoke(() -> {
            // Check if player is in a friends chat
            if (client.getFriendsChatManager() == null)
            {
                log.debug("Not in a friends chat, skipping auto-message");
                return;
            }

            // Check if chatbox is empty (user sent the previous message)
            String currentChatboxText = client.getVarcStrValue(VarClientStr.CHATBOX_TYPED_TEXT);
            if (currentChatboxText == null || currentChatboxText.trim().isEmpty() || !currentChatboxText.contains("I unlocked"))
            {
                // Chatbox is empty or doesn't have our message - clear the old queue
                clearPendingFCMessages();
            }

            // Add this item to the pending messages queue
            pendingFCMessages.add(itemName);

            // Combine all pending messages
            String combinedMessage;
            if (pendingFCMessages.size() == 1)
            {
                combinedMessage = "I unlocked " + pendingFCMessages.get(0) + " for the group!";
            }
            else
            {
                // Multiple items: "I unlocked Fire rune, Water rune, Earth rune for the group!"
                combinedMessage = "I unlocked " + String.join(", ", pendingFCMessages) + " for the group!";
            }

            // Set the combined message in the chatbox input field
            // The user just needs to press Enter to send it
            client.setVarcStrValue(VarClientStr.CHATBOX_TYPED_TEXT, combinedMessage);
            log.debug("Set FC message in chatbox (queued {} items): {}", pendingFCMessages.size(), combinedMessage);
        });
    }

    /**
     * Clear the pending friends chat messages queue
     * Call this when the user sends the message or opens chat
     */
    private void clearPendingFCMessages()
    {
        pendingFCMessages.clear();
    }

    public boolean isDeletionConfirmed(final String message, final String title)
    {
        int confirm = JOptionPane.showConfirmDialog(panel,
                message, title, JOptionPane.OK_CANCEL_OPTION);

        return confirm == JOptionPane.YES_OPTION;
    }

    void killSearchResults()
    {
        if (disabledByWhitelist)
        {
            return; // Don't restrict GE if character not on whitelist
        }

        Widget grandExchangeSearchResults = client.getWidget(ComponentID.CHATBOX_GE_SEARCH_RESULTS);

        if (grandExchangeSearchResults == null)
        {
            return;
        }

        Widget[] children = grandExchangeSearchResults.getDynamicChildren();

        if (children == null || children.length < 2 || children.length % 3 != 0)
        {
            return;
        }

        for (int i = 0; i < children.length; i+= 3) {
            if (!unlockedItems.contains(children[i + 2].getItemId()))
            {
                children[i].setHidden(true);
                children[i + 1].setOpacity(70);
                children[i + 2].setOpacity(70);
            }
        }
    }

    /* Saves players unlock JSON to a .txt file every time they unlock a new item */
    private void savePlayerUnlocks()
    {
        try
        {
            PrintWriter w = new PrintWriter(profileFile);
            String json = GSON.toJson(unlockedItems);
            w.println(json);
            w.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /* Loads a players unlock JSON every time they login */
    private void loadPlayerUnlocks()
    {
        unlockedItems.clear();
        try
        {
            String json = new Scanner(profileFile).useDelimiter("\\Z").next();
            unlockedItems.addAll(GSON.fromJson(json, new TypeToken<List<Integer>>(){}.getType()));
            panel.displayItems(new ArrayList<ItemObject>()); // Redraw the panel
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateNamesBronzeman()
    {
        namesBronzeman = Text.fromCSV(config.namesBronzeman());
        log.info("Updated group members list: {} members - {}", namesBronzeman.size(), namesBronzeman);
    }

    private void updateScreenshotUnlock()
    {
        boolean screenshotUnlock = config.screenshotUnlock();
        boolean includeFrame = config.includeFrame();
        AnotherBronzemanModeOverlay.updateScreenshotUnlock(screenshotUnlock, includeFrame);
    }

    /**
     * Adds the Bronzeman Icon in front of player names.
     *
     * @param chatMessage chat message to edit sender name on
     */
    private void addBronzemanIconToMessage(ChatMessage chatMessage)
    {
        String name = chatMessage.getName();
        if (!name.equals(Text.removeTags(name)))
        {
            // If the name has any tags, no bronzeman icon will be added.
            // This makes it so Iron men can't be flagged as bronzeman, but
            // currently also excludes mods.
            return;
        }

        final MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setName(getNameWithIcon(bronzemanIconOffset, name));

        client.refreshChat();
    }

    /**
     * Checks if the world is a Seasonal world (Like leagues and seasonal deadman).
     *
     * @param worldNumber number of the world to check.
     * @return boolean true/false if it is a seasonal world or not.
     */
    private boolean isSeasonalWorld(int worldNumber)
    {
        WorldResult worlds = worldService.getWorlds();
        if (worlds == null)
        {
            return false;
        }

        World world = worlds.findWorld(worldNumber);
        return world != null && world.getTypes().contains(WorldType.SEASONAL);
    }

    /**
     * Checks if the given message was sent by the player
     *
     * @param chatMessage number of the world to check.
     * @return boolean true/false if the message was sent by the player.
     */
    private boolean sentByPlayer(ChatMessage chatMessage)
    {
        MessageNode messageNode = chatMessage.getMessageNode();

        return Text.sanitize(messageNode.getName()).equals(Text.sanitize(client.getLocalPlayer().getName()));
    }

    /**
     * Update the player name in the chatbox input
     */
    private void setChatboxName(String name)
    {
        Widget chatboxInput = client.getWidget(ComponentID.CHATBOX_INPUT);
        if (chatboxInput != null)
        {
            String text = chatboxInput.getText();
            int idx = text.indexOf(':');
            if (idx != -1)
            {
                String newText = name + text.substring(idx);
                chatboxInput.setText(newText);
            }
        }
    }

    /**
     * Gets the bronzeman name, including possible icon, of the local player.
     *
     * @return String of icon + name
     */
    private String getNameChatbox()
    {
        Player player = client.getLocalPlayer();
        if (player != null)
        {
            Widget chatboxInput = client.getWidget(ComponentID.CHATBOX_INPUT);
            String namePlusChannel = player.getName();
            if (chatboxInput != null)
            {
                String text = chatboxInput.getText();
                int idx = text.indexOf(':');
                if (idx != -1)
                {
                    namePlusChannel = text.substring(0,idx);
                }
            }
            return getNameWithIcon(bronzemanIconOffset, namePlusChannel);
        }
        return null;
    }

    /**
     * Gets the default name, including possible icon, of the local player.
     *
     * @return String of icon + name
     */
    private String getNameDefault()
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }

        int iconIndex;
        switch (client.getAccountType())
        {
            case IRONMAN:
                iconIndex = IconID.IRONMAN.getIndex();
                break;
            case HARDCORE_IRONMAN:
                iconIndex = IconID.HARDCORE_IRONMAN.getIndex();
                break;
            case ULTIMATE_IRONMAN:
                iconIndex = IconID.ULTIMATE_IRONMAN.getIndex();
                break;
            default:
                return player.getName();
        }

        return getNameWithIcon(iconIndex, player.getName());
    }

    /**
     * Get a name formatted with icon
     *
     * @param iconIndex index of the icon
     * @param name      name of the player
     * @return String of icon + name
     */
    private static String getNameWithIcon(int iconIndex, String name)
    {
        String icon = "<img=" + iconIndex + ">";
        return icon + name;
    }

    /**
     * Gets a ChatPlayer object from a clean name by searching friends chat and friends list.
     *
     * @param name name of player to find.
     * @return ChatPlayer if found, else null.
     */
    private ChatPlayer getChatPlayerFromName(String name)
    {
        // Search friends chat members first, because if a friend is in the friends chat but their private
        // chat is 'off', then we won't know the world
        FriendsChatManager friendsChatManager = client.getFriendsChatManager();
        if (friendsChatManager != null)
        {
            FriendsChatMember friendsChatMember = friendsChatManager.findByName(name);
            if (friendsChatMember != null)
            {
                return friendsChatMember;
            }
        }

        NameableContainer<Friend> friendContainer = client.getFriendContainer();
        return friendContainer.findByName(name);
    }

    /**
     * Checks if a player name is a friend or friends chat member is a bronzeman.
     *
     * @param name name of player to check.
     * @return boolean true/false.
     */
    private boolean isChatPlayerBronzeman(String name)
    {
        return isChatPlayerOnNormalWorld(name) && (namesBronzeman.contains(name) || namesBronzeman.contains(name.replace('\u00A0', ' ')));
    }

    /**
     * Checks if a player name is a friend or friends chat member on a normal world.
     *
     * @param name name of player to check.
     * @return boolean true/false.
     */
    private boolean isChatPlayerOnNormalWorld(String name)
    {
        ChatPlayer player = getChatPlayerFromName(name);

        if (player == null)
        {
            return true;
        }

        int world = player.getWorld();
        return !isSeasonalWorld(world);
    }

    /**
     * Sets up the profileFile variable, and makes the player file if needed.
     */
    private void setupUnlockHistory()
    {
        profileKey = configManager.getRSProfileKey();

        // If profiles are not being used yet, we continue to use the legacy system.
        if (profileKey == null)
        {
            setupLegacyFile();
        }
        else
        {
            setupProfileFile();
        }
    }

    private void setupLegacyFile()
    {
        profileFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + client.getUsername());
        if (!profileFolder.exists())
        {
            profileFolder.mkdirs();
        }

        profileFile = new File(profileFolder, "another-bronzeman-mode-unlocks.txt");
        if (!profileFile.exists())
        {
            try {
                profileFile.createNewFile();
                unlockDefaultItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupProfileFile()
    {
        legacyFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + client.getUsername());
        legacyFile = new File(legacyFolder, "another-bronzeman-mode-unlocks.txt");

        profileFolder = new File(RuneLite.RUNELITE_DIR, "profiles/" + profileKey);
        if (!profileFolder.exists())
        {
            profileFolder.mkdirs();
        }
        profileFile = new File(profileFolder, "another-bronzeman-mode-unlocks.txt");
        if (!profileFile.exists())
        {
            if (legacyFile.exists())
            {
                try {
                    Files.copy(legacyFile.toPath(), profileFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            else
            {
                try {
                    profileFile.createNewFile();
                    unlockDefaultItems();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void OnUnlocksCountCommand(ChatMessage chatMessage, String message)
    {
        if (!sentByPlayer(chatMessage))
        {
            return;
        }

        final ChatMessageBuilder builder = new ChatMessageBuilder()
            .append(ChatColorType.HIGHLIGHT)
            .append("You have unlocked ")
            .append(ChatColorType.NORMAL)
            .append(Integer.toString(unlockedItems.size()))
            .append(ChatColorType.HIGHLIGHT)
            .append(" items.");

        String response = builder.build();

        MessageNode messageNode = chatMessage.getMessageNode();
        messageNode.setRuneLiteFormatMessage(response);
        client.refreshChat();
    }

    private void OnUnlocksResetCommand(ChatMessage chatMessage, String message)
    {
        if (!sentByPlayer(chatMessage))
        {
            return;
        }
        resetItemUnlocks();
        sendChatMessage("Unlocks successfully reset!");
    }

    private void resetItemUnlocks(){
        try {
            profileFile.delete();
            unlockedItems.clear();
            panel.displayItems(new ArrayList<ItemObject>()); // Redraw the panel
            savePlayerUnlocks();
            unlockDefaultItems();
            unlockItemContainerItems(client.getItemContainer(InventoryID.INVENTORY));
            unlockItemContainerItems(client.getItemContainer(InventoryID.EQUIPMENT));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void OnUnlocksBackupCommand(ChatMessage chatMessage, String message)
    {
        if (!sentByPlayer(chatMessage))
        {
            return;
        }
        backupItemUnlocks();
        sendChatMessage("Successfully backed up file!");
    }

    private void backupItemUnlocks()
    {
        Path originalPath = profileFile.toPath();
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM_WW_HH_mm_ss");
            Files.copy(originalPath, Paths.get(profileFolder.getPath() + "_" + sdf.format(cal.getTime()) + ".backup"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Loads the bronzeman resources into the client.
     */
    private void loadResources()
    {
        final IndexedSprite[] modIcons = client.getModIcons();

        if (bronzemanIconOffset != -1 || modIcons == null)
        {
            return;
        }

        unlockImage = ImageUtil.getResourceStreamFromClass(getClass(), "/item-unlocked.png");
        BufferedImage image = ImageUtil.getResourceStreamFromClass(getClass(), "/bronzeman_icon.png");
        IndexedSprite indexedSprite = ImageUtil.getImageIndexedSprite(image, client);

        bronzemanIconOffset = modIcons.length;

        final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + 1);
        newModIcons[newModIcons.length - 1] = indexedSprite;

        client.setModIcons(newModIcons);
    }

    /**
     * Initialize group synchronization with JSONBin
     */
    private void initializeGroupSync()
    {
        String apiKey = config.jsonbinApiKey();
        String binId = config.jsonbinBinId();

        if (apiKey == null || apiKey.isEmpty() || binId == null || binId.isEmpty())
        {
            log.warn("Group sync enabled but JSONBin credentials not configured");
            return;
        }

        log.info("Initializing group sync with JSONBin");
        jsonBinClient = new JsonBinClient(apiKey, binId);
        groupSyncManager = new GroupSyncManager(jsonBinClient);

        // Initialize with existing local unlocks
        if (unlockedItems != null && !unlockedItems.isEmpty())
        {
            String playerName = client.getLocalPlayer() != null ? client.getLocalPlayer().getName() : "Unknown";
            groupSyncManager.initializeFromLocal(unlockedItems, playerName);
        }

        // Initial sync from remote
        executor.submit(() -> {
            int newUnlocks = groupSyncManager.syncFromRemote();
            if (newUnlocks > 0)
            {
                // Merge remote unlocks into local
                mergeRemoteUnlocks();
                String itemWord = newUnlocks == 1 ? "item" : "items";
                sendChatMessage("Group sync: " + newUnlocks + " new " + itemWord + " unlocked by your group!");
            }
        });

        // Schedule periodic sync
        int intervalMinutes = Math.max(1, Math.min(60, config.syncInterval()));
        syncTask = executor.scheduleWithFixedDelay(
            this::performSync,
            intervalMinutes,
            intervalMinutes,
            TimeUnit.MINUTES
        );

        log.info("Group sync started with {}-minute interval", intervalMinutes);
    }

    /**
     * Stop group synchronization
     */
    private void stopGroupSync()
    {
        if (syncTask != null)
        {
            syncTask.cancel(false);
            syncTask = null;
        }

        // Push any pending unlocks before stopping
        if (groupSyncManager != null && groupSyncManager.hasPendingPush())
        {
            executor.submit(() -> groupSyncManager.pushToRemote());
        }

        groupSyncManager = null;
        jsonBinClient = null;
        log.info("Group sync stopped");
    }

    /**
     * Perform periodic sync (pull from remote, then push local)
     */
    private void performSync()
    {
        if (groupSyncManager == null)
        {
            return;
        }

        try
        {
            // First, pull new unlocks from remote
            int newUnlocks = groupSyncManager.syncFromRemote();
            if (newUnlocks > 0)
            {
                mergeRemoteUnlocks();

                // Always show sync notification when items are unlocked from group
                String itemWord = newUnlocks == 1 ? "item" : "items";
                sendChatMessage("Group sync: " + newUnlocks + " new " + itemWord + " unlocked by your group!");

                // Also send desktop notification if enabled
                if (config.sendNotification())
                {
                    notifier.notify("Group Bronzeman: " + newUnlocks + " new unlocks from your group!");
                }
            }

            // Then, push our pending unlocks
            if (groupSyncManager.hasPendingPush())
            {
                groupSyncManager.pushToRemote();
            }
        }
        catch (Exception e)
        {
            log.error("Error during group sync", e);
        }
    }

    /**
     * Merge remote unlocks into local unlocked items list
     */
    private void mergeRemoteUnlocks()
    {
        if (groupSyncManager == null)
        {
            return;
        }

        Set<Integer> remoteItems = groupSyncManager.getUnlockedItems();
        for (Integer itemId : remoteItems)
        {
            if (!unlockedItems.contains(itemId))
            {
                unlockedItems.add(itemId);

                // Optionally show who unlocked it
                if (config.showWhoUnlocked())
                {
                    JsonBinClient.UnlockEntry entry = groupSyncManager.getUnlockEntry(itemId);
                    if (entry != null)
                    {
                        log.info("Item {} was unlocked by {}", entry.itemName, entry.unlockedBy);
                    }
                }
            }
        }

        // Save updated unlocks
        savePlayerUnlocks();
        panel.displayItems(new ArrayList<ItemObject>()); // Redraw panel
    }

    /**
     * Check if the current character is on the whitelist
     * Sets disabledByWhitelist flag accordingly
     * @return true if player name was available and check completed, false if name not yet loaded
     */
    private boolean checkCharacterWhitelist()
    {
        if (!config.enableWhitelist())
        {
            disabledByWhitelist = false;
            return true; // Check completed (whitelist disabled)
        }

        String whitelistStr = config.whitelistedCharacters();
        if (whitelistStr == null || whitelistStr.isEmpty())
        {
            disabledByWhitelist = false;
            return true; // Check completed (no whitelist configured)
        }

        // Get player name safely - handle null cases
        String playerName = "";
        if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null)
        {
            playerName = client.getLocalPlayer().getName();
        }

        // If player name isn't loaded yet, don't disable the plugin
        // The check will be retried shortly
        if (playerName.isEmpty())
        {
            disabledByWhitelist = false;
            log.warn("Player name not available yet, skipping whitelist check (will retry)");
            return false; // Check incomplete - name not available
        }

        List<String> whitelist = Text.fromCSV(whitelistStr);

        if (whitelist.contains(playerName))
        {
            disabledByWhitelist = false;
            log.info("Character '{}' is on whitelist. Plugin enabled.", playerName);
        }
        else
        {
            disabledByWhitelist = true;
            log.info("Character '{}' not on whitelist. Plugin disabled.", playerName);
            sendChatMessage("Group Bronzeman Mode is disabled for this character (not on whitelist).");
        }

        return true; // Check completed successfully
    }
}
