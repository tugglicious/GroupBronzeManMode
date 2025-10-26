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

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Manages synchronization of unlocks between local storage and JSONBin cloud storage
 */
@Slf4j
public class GroupSyncManager
{
	private final JsonBinClient jsonBinClient;
	private JsonBinClient.UnlockData remoteUnlocks;
	private final Set<Integer> pendingPush; // Items we've unlocked but haven't pushed yet

	public GroupSyncManager(JsonBinClient jsonBinClient)
	{
		this.jsonBinClient = jsonBinClient;
		this.remoteUnlocks = new JsonBinClient.UnlockData();
		this.pendingPush = new HashSet<>();
	}

	/**
	 * Check if an item is unlocked (in remote data)
	 */
	public boolean isUnlocked(int itemId)
	{
		return remoteUnlocks.isUnlocked(itemId);
	}

	/**
	 * Add an unlock to the pending push queue
	 */
	public void queueUnlock(int itemId, String itemName, String playerName)
	{
		if (!remoteUnlocks.isUnlocked(itemId))
		{
			remoteUnlocks.addUnlock(itemId, itemName, playerName);
			pendingPush.add(itemId);
			log.info("Queued item unlock: {} (ID: {}) by {}", itemName, itemId, playerName);
		}
	}

	/**
	 * Sync from JSONBin - fetch remote data and merge with local
	 * Returns the number of new unlocks discovered
	 */
	public int syncFromRemote()
	{
		if (jsonBinClient == null)
		{
			log.warn("JSONBin client not initialized");
			return 0;
		}

		JsonBinClient.UnlockData fetchedData = jsonBinClient.fetchUnlocks();
		if (fetchedData == null || fetchedData.unlockedItems == null)
		{
			log.warn("Failed to fetch remote unlocks");
			return 0;
		}

		int newUnlocks = 0;
		for (Map.Entry<Integer, JsonBinClient.UnlockEntry> entry : fetchedData.unlockedItems.entrySet())
		{
			int itemId = entry.getKey();
			JsonBinClient.UnlockEntry unlockEntry = entry.getValue();

			if (!remoteUnlocks.isUnlocked(itemId))
			{
				remoteUnlocks.addUnlock(itemId, unlockEntry.itemName, unlockEntry.unlockedBy);
				newUnlocks++;
			}
		}

		if (newUnlocks > 0)
		{
			log.info("Synced {} new unlocks from remote", newUnlocks);
		}

		return newUnlocks;
	}

	/**
	 * Push local unlocks to JSONBin
	 * This will fetch latest remote data first to avoid overwriting others' unlocks
	 */
	public boolean pushToRemote()
	{
		if (jsonBinClient == null)
		{
			log.warn("JSONBin client not initialized");
			return false;
		}

		if (pendingPush.isEmpty())
		{
			log.debug("No pending unlocks to push");
			return true;
		}

		// First sync from remote to avoid overwriting others' unlocks
		JsonBinClient.UnlockData fetchedData = jsonBinClient.fetchUnlocks();
		if (fetchedData != null && fetchedData.unlockedItems != null)
		{
			// Merge remote data into local
			for (Map.Entry<Integer, JsonBinClient.UnlockEntry> entry : fetchedData.unlockedItems.entrySet())
			{
				int itemId = entry.getKey();
				JsonBinClient.UnlockEntry remoteEntry = entry.getValue();

				if (!remoteUnlocks.isUnlocked(itemId))
				{
					remoteUnlocks.addUnlock(itemId, remoteEntry.itemName, remoteEntry.unlockedBy);
				}
			}
		}

		// Now push merged data
		boolean success = jsonBinClient.pushUnlocks(remoteUnlocks);
		if (success)
		{
			pendingPush.clear();
			log.info("Successfully pushed unlocks to remote");
		}

		return success;
	}

	/**
	 * Get all unlocked item IDs
	 */
	public Set<Integer> getUnlockedItems()
	{
		return new HashSet<>(remoteUnlocks.getItemIds());
	}

	/**
	 * Get the unlock entry for an item (includes who unlocked it and when)
	 */
	public JsonBinClient.UnlockEntry getUnlockEntry(int itemId)
	{
		return remoteUnlocks.unlockedItems.get(itemId);
	}

	/**
	 * Get count of unlocked items
	 */
	public int getUnlockCount()
	{
		return remoteUnlocks.unlockedItems.size();
	}

	/**
	 * Check if there are pending unlocks to push
	 */
	public boolean hasPendingPush()
	{
		return !pendingPush.isEmpty();
	}

	/**
	 * Initialize remote unlocks from a local list
	 * Used when migrating from local-only to group sync
	 */
	public void initializeFromLocal(List<Integer> localUnlocks, String playerName)
	{
		for (Integer itemId : localUnlocks)
		{
			if (!remoteUnlocks.isUnlocked(itemId))
			{
				remoteUnlocks.addUnlock(itemId, "Item " + itemId, playerName);
				pendingPush.add(itemId);
			}
		}
		log.info("Initialized {} items from local storage", localUnlocks.size());
	}
}
