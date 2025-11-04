# Integrating Minigame Restrictions into Bronzeman Unleashed

## Overview

This guide explains how to integrate the minigame protection feature from GroupBronzeManMode into Bronzeman Unleashed to prevent unlocking temporary items from minigames like Last Man Standing, Theatre of Blood, Chambers of Xeric, and Barbarian Assault.

---

## Current State Analysis

### Bronzeman Unleashed Architecture

**Item Unlock Entry Points:**
1. **`ItemUnlockService.onItemContainerChanged()`** (line 275)
   - Triggered when items appear in containers (inventory, bank, etc.)
   - Calls `unlockItemsFromItemContainer()` → `unlockItem()`

2. **`ItemUnlockService.onServerNpcLoot()`** (line 288)
   - Triggered when looting NPCs
   - Directly calls `unlockItem()` for each item

**Existing Protection:**
- ✅ Seasonal world check (`isCurrentWorldSupportedForUnlockingItems()`)
- ✅ Container whitelist (`INCLUDED_CONTAINER_IDS`)
- ⚠️ Raids chests are **commented out** (lines 116-118):
  ```java
  // I think we should probably not include these it might ruin the moment
  //    InventoryID.RAIDS_REWARDS, // Chambers of Xeric reward
  //    InventoryID.TOB_CHESTS, // Theater of Blood reward
  //    InventoryID.TOA_CHESTS, // Tombs of Amascut reward
  ```

**What's Missing:**
- ❌ No varbit checks for active minigames
- ❌ No protection against temporary items during minigames

---

## GroupBronzeManMode Implementation

### Current Minigame Check (Lines 950-977)

```java
private boolean isInMinigame()
{
    // Check Last Man Standing
    if (client.getVarbitValue(Varbits.IN_LMS) == 1)
    {
        return true;
    }

    // Check if in a raid (CoX)
    if (client.getVarbitValue(Varbits.IN_RAID) == 1)
    {
        return true;
    }

    // Check Theatre of Blood (1=Party, 2=Inside/Spectator, 3=Dead Spectating)
    int tobState = client.getVarbitValue(Varbits.THEATRE_OF_BLOOD);
    if (tobState == 2 || tobState == 3)
    {
        return true;
    }

    // Check Barbarian Assault
    if (client.getVarbitValue(Varbits.IN_GAME_BA) == 1)
    {
        return true;
    }

    return false;
}
```

**Where It's Called:**
- In `unlockItemContainerItems()` at line 629, before processing any items

---

## Integration Strategy

### Option 1: Add as GameRule (Recommended - Fits Their Architecture)

**Why this approach:**
- ✅ Fits Bronzeman Unleashed's configurable "GameRules" pattern
- ✅ Gives players choice (some might want to allow minigame unlocks)
- ✅ Follows their existing architecture (clean separation of concerns)
- ✅ Can be toggled per-group via Firebase sync

**Steps:**

#### 1. Add New GameRule Field

**File:** `/src/main/java/com.elertan/models/GameRules.java`

Add around line 60 (after PvP section):

```java
// Minigames
@Getter
@Setter
private boolean preventMinigameItemUnlocks;
```

Update constructor (line 66):
```java
public GameRules(Long lastUpdatedByAccountHash, ISOOffsetDateTime lastUpdatedAt,
    boolean onlyForTradeableItems,
    boolean restrictGroundItems,
    boolean preventTradeOutsideGroup,
    boolean preventTradeLockedItems,
    boolean preventGrandExchangeBuyOffers,
    boolean preventPlayedOwnedHouse,
    boolean restrictPlayerVersusPlayerLoot,
    boolean shareAchievementNotifications,
    Integer valuableLootNotificationThreshold,
    String partyPassword,
    boolean preventMinigameItemUnlocks) {  // ADD THIS
    // ... existing assignments ...
    this.preventMinigameItemUnlocks = preventMinigameItemUnlocks;  // ADD THIS
}
```

Update `createWithDefaults()` (line 90):
```java
public static GameRules createWithDefaults(Long lastUpdatedByAccountHash,
    ISOOffsetDateTime lastUpdatedAt) {
    return new GameRules(
        lastUpdatedByAccountHash,
        lastUpdatedAt,
        true,   // onlyForTradeableItems
        true,   // restrictGroundItems
        true,   // preventTradeOutsideGroup
        true,   // preventTradeLockedItems
        true,   // preventGrandExchangeBuyOffers
        true,   // preventPlayedOwnedHouse
        false,  // restrictPlayerVersusPlayerLoot
        true,   // shareAchievementNotifications
        100_000,  // valuableLootNotificationThreshold
        null,   // partyPassword
        true    // preventMinigameItemUnlocks - DEFAULT TO TRUE
    );
}
```

Update `toString()` to include the new field.

---

#### 2. Create Minigame Detection Service

**New File:** `/src/main/java/com.elertan/MinigameDetectionService.java`

```java
package com.elertan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Varbits;

@Slf4j
@Singleton
public class MinigameDetectionService {

    @Inject
    private Client client;

    /**
     * Checks if the player is currently in a minigame where temporary items
     * should not be unlocked.
     *
     * This prevents unlocking items from:
     * - Last Man Standing (LMS)
     * - Chambers of Xeric (CoX) raids
     * - Theatre of Blood (ToB) raids
     * - Barbarian Assault (BA)
     *
     * @return true if in a minigame, false otherwise.
     */
    public boolean isInMinigame() {
        // Check Last Man Standing
        if (client.getVarbitValue(Varbits.IN_LMS) == 1) {
            log.debug("Player is in Last Man Standing");
            return true;
        }

        // Check if in a raid (Chambers of Xeric)
        if (client.getVarbitValue(Varbits.IN_RAID) == 1) {
            log.debug("Player is in Chambers of Xeric raid");
            return true;
        }

        // Check Theatre of Blood
        // States: 0=Not in, 1=In party, 2=Inside/Spectating, 3=Dead spectating
        int tobState = client.getVarbitValue(Varbits.THEATRE_OF_BLOOD);
        if (tobState == 2 || tobState == 3) {
            log.debug("Player is in Theatre of Blood (state: {})", tobState);
            return true;
        }

        // Check Barbarian Assault
        if (client.getVarbitValue(Varbits.IN_GAME_BA) == 1) {
            log.debug("Player is in Barbarian Assault");
            return true;
        }

        // Could add more minigames here:
        // - Tombs of Amascut (ToA) - if varbit exists
        // - Nightmare Zone
        // - Pest Control
        // - Soul Wars
        // etc.

        return false;
    }
}
```

---

#### 3. Update ItemUnlockService

**File:** `/src/main/java/com.elertan/ItemUnlockService.java`

Add injection (around line 150):
```java
@Inject
private MinigameDetectionService minigameDetectionService;
```

Update `unlockItem()` method - add check around line 388 (after the world type check):

```java
// We don't support all world types, for example we don't want unlocks on seasonal modes
try {
    if (!isCurrentWorldSupportedForUnlockingItems()) {
        log.info("Current world is not supported for unlocking items");
        future.complete(null);
        return future;
    }
} catch (Exception ex) {
    future.completeExceptionally(ex);
    return future;
}

// ADD THIS BLOCK:
// Check if we should prevent unlocks during minigames
// We need to check game rules first
gameRulesService
    .waitUntilGameRulesReady(null)
    .whenComplete((__, throwable) -> {
        if (throwable != null) {
            future.completeExceptionally(throwable);
            return;
        }

        GameRules gameRules = gameRulesService.getGameRules();
        if (gameRules.isPreventMinigameItemUnlocks() && minigameDetectionService.isInMinigame()) {
            log.debug("Skipping unlock - player is in a minigame");
            future.complete(null);
            return;
        }

        // Continue with rest of unlock logic...
        continueUnlockItem(initialItemId, droppedByNPCId, future);
    });

return future;
```

**Note:** You'll need to refactor the rest of the unlock logic into a `continueUnlockItem()` method to avoid nesting issues.

---

#### 4. Add UI Toggle in GameRules Editor

**File:** `/src/main/java/com.elertan/panel/components/GameRulesEditor.java`

Add a checkbox/toggle for the new rule in the UI panel where other game rules are configured.

Look for where other rules like `preventGrandExchangeBuyOffers` are added and follow the same pattern.

---

### Option 2: Always-On Protection (Simpler, Less Flexible)

**Why this approach:**
- ✅ Simpler implementation
- ✅ No UI changes needed
- ✅ Prevents edge cases where players forget to enable it
- ❌ Less flexible - some players might want minigame unlocks

**Steps:**

#### 1. Create MinigameDetectionService

Same as Option 1, Step 2.

#### 2. Update ItemUnlockService

**File:** `/src/main/java/com.elertan/ItemUnlockService.java`

Add injection:
```java
@Inject
private MinigameDetectionService minigameDetectionService;
```

Update `unlockItemsFromItemContainer()` method at line 524:

```java
private void unlockItemsFromItemContainer(ItemContainer itemContainer) {
    if (unlockedItemsDataProvider.getState() != UnlockedItemsDataProvider.State.Ready) {
        return;
    }

    if (itemContainer == null) {
        return;
    }

    // ADD THIS CHECK:
    if (minigameDetectionService.isInMinigame()) {
        log.debug("Skipping container unlock - player is in a minigame");
        return;
    }

    for (Item item : itemContainer.getItems()) {
        // ... rest of logic
    }
}
```

Also update `onServerNpcLoot()` at line 288:

```java
public void onServerNpcLoot(ServerNpcLoot event) {
    if (unlockedItemsDataProvider.getState() != UnlockedItemsDataProvider.State.Ready) {
        return;
    }

    // ADD THIS CHECK:
    if (minigameDetectionService.isInMinigame()) {
        log.debug("Skipping NPC loot unlock - player is in a minigame");
        return;
    }

    Collection<ItemStack> itemStack = event.getItems();
    // ... rest of logic
}
```

---

## Additional Minigame Varbits to Consider

Here are other minigames you might want to add protection for:

```java
// Tombs of Amascut (ToA)
// Note: You'll need to find the correct varbit - check RuneLite API

// Nightmare Zone
if (client.getVarbitValue(Varbits.IN_NIGHTMARE_ZONE) == 1) {
    return true;
}

// Pest Control
// Check for being on the Pest Control boat or in the game

// Soul Wars
// Check for being in Soul Wars

// Castle Wars
// Check for being in Castle Wars
```

**Research Required:** Some of these varbits might not exist yet in RuneLite's API. You'd need to:
1. Check `net.runelite.api.Varbits` class for available constants
2. Test in-game to find the correct varbit IDs
3. Submit PRs to RuneLite to add missing varbits

---

## Testing Plan

### Test Cases to Verify:

1. **Last Man Standing**
   - Enter LMS
   - Pick up items during the game
   - Verify items are NOT unlocked
   - Exit LMS
   - Pick up the same items
   - Verify items ARE unlocked

2. **Chambers of Xeric**
   - Enter CoX raid
   - Obtain items during raid (potions, supplies)
   - Verify items are NOT unlocked
   - Complete raid and open chest
   - Check if reward items are unlocked (they should be from chest container)

3. **Theatre of Blood**
   - Enter ToB
   - Obtain items during raid
   - Verify items are NOT unlocked
   - Complete raid
   - Check rewards

4. **Barbarian Assault**
   - Play BA
   - Obtain items during game (e.g., eggs, hammers)
   - Verify items are NOT unlocked
   - Exit BA
   - Normal items should unlock

5. **Normal Gameplay**
   - Ensure regular drops/items still unlock normally
   - Bank items, GE purchases, etc. work as expected

---

## Why Raids Chests Are Commented Out

In the current code (lines 116-118), the developer commented:
```java
// I think we should probably not include these it might ruin the moment
//    InventoryID.RAIDS_REWARDS, // Chambers of Xeric reward
//    InventoryID.TOB_CHESTS, // Theater of Blood reward
//    InventoryID.TOA_CHESTS, // Tombs of Amascut reward
```

**This is intentional and separate from minigame protection:**
- They want raid **rewards** (from chests) to still unlock
- But they DON'T want temporary items **during the raid** to unlock
- The minigame check prevents unlocks during the raid
- But once you complete and open the chest, those items unlock normally

**So both systems work together:**
- `isInMinigame()` = prevents temporary items during raid
- Excluding raid chests from `INCLUDED_CONTAINER_IDS` = prevents instant unlock of rewards (player sees them first)

---

## Recommendation

**Go with Option 1 (GameRule approach)** because:

1. ✅ Fits their existing architecture perfectly
2. ✅ Maintains configurability (their core design principle)
3. ✅ Can be synced across the group via Firebase
4. ✅ Allows edge cases (e.g., testing, fun runs where people WANT minigame unlocks)
5. ✅ Future-proof - easy to add more minigames later
6. ✅ Follows their pattern for other restrictions

The work is a bit more upfront, but it's cleaner and more maintainable long-term.

---

## Files You'll Need to Modify

### Option 1 (GameRule):
1. ✅ `/src/main/java/com.elertan/models/GameRules.java` - Add field
2. ✅ `/src/main/java/com.elertan/MinigameDetectionService.java` - NEW FILE
3. ✅ `/src/main/java/com.elertan/ItemUnlockService.java` - Add check
4. ✅ `/src/main/java/com.elertan/panel/components/GameRulesEditor.java` - Add UI toggle

### Option 2 (Always-On):
1. ✅ `/src/main/java/com.elertan/MinigameDetectionService.java` - NEW FILE
2. ✅ `/src/main/java/com.elertan/ItemUnlockService.java` - Add checks

---

## Dependencies

**No new dependencies needed!**
- All varbits are available in RuneLite API
- `net.runelite.api.Varbits` class already has all the constants we need

---

## Summary

The integration is straightforward because:
1. ✅ All the varbit constants exist in RuneLite
2. ✅ The unlock logic is centralized in `ItemUnlockService`
3. ✅ Their architecture makes it easy to add new checks
4. ✅ No external dependencies required

**Estimated Development Time:**
- Option 1: ~2-3 hours (including UI work)
- Option 2: ~30 minutes

**Testing Time:** ~1-2 hours (need to test each minigame)

---

## Next Steps

1. Decide on Option 1 vs Option 2
2. Create the `MinigameDetectionService` class
3. Update `ItemUnlockService` with the checks
4. If Option 1: Add GameRule field and UI toggle
5. Test thoroughly in each minigame
6. Submit PR to elertan's repository

---

Good luck with the integration! This is a valuable feature that will prevent frustrating bugs for players.
