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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Client for interacting with JSONBin.io API for group unlock synchronization
 */
@Slf4j
public class JsonBinClient
{
	private static final String JSONBIN_API_BASE = "https://api.jsonbin.io/v3";
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final String apiKey;
	private final String binId;

	public JsonBinClient(String apiKey, String binId)
	{
		this.apiKey = apiKey;
		this.binId = binId;
		this.gson = new Gson();

		this.httpClient = new OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.build();
	}

	/**
	 * Fetch all unlocks from JSONBin
	 */
	public UnlockData fetchUnlocks()
	{
		if (apiKey == null || apiKey.isEmpty() || binId == null || binId.isEmpty())
		{
			log.warn("JSONBin not configured (missing API key or Bin ID)");
			return new UnlockData();
		}

		try
		{
			String url = String.format("%s/b/%s/latest", JSONBIN_API_BASE, binId);

			Request request = new Request.Builder()
				.url(url)
				.header("X-Master-Key", apiKey)
				.get()
				.build();

			try (Response response = httpClient.newCall(request).execute())
			{
				if (!response.isSuccessful())
				{
					log.error("Failed to fetch from JSONBin: {} - {}", response.code(), response.message());
					return new UnlockData();
				}

				String responseBody = response.body().string();
				JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

				// JSONBin returns data in a "record" field
				if (jsonResponse.has("record"))
				{
					UnlockData data = gson.fromJson(jsonResponse.get("record"), UnlockData.class);
					log.info("Fetched {} unlocks from JSONBin", data.unlockedItems.size());
					return data;
				}
				else
				{
					log.warn("No 'record' field in JSONBin response");
					return new UnlockData();
				}
			}
		}
		catch (IOException e)
		{
			log.error("Error fetching from JSONBin", e);
			return new UnlockData();
		}
	}

	/**
	 * Push unlocks to JSONBin
	 */
	public boolean pushUnlocks(UnlockData data)
	{
		if (apiKey == null || apiKey.isEmpty() || binId == null || binId.isEmpty())
		{
			log.warn("JSONBin not configured (missing API key or Bin ID)");
			return false;
		}

		try
		{
			String url = String.format("%s/b/%s", JSONBIN_API_BASE, binId);

			String json = gson.toJson(data);
			RequestBody body = RequestBody.create(JSON, json);

			Request request = new Request.Builder()
				.url(url)
				.header("X-Master-Key", apiKey)
				.header("Content-Type", "application/json")
				.put(body)
				.build();

			try (Response response = httpClient.newCall(request).execute())
			{
				if (response.isSuccessful())
				{
					log.info("Successfully pushed {} unlocks to JSONBin", data.unlockedItems.size());
					return true;
				}
				else
				{
					log.error("Failed to push to JSONBin: {} - {}", response.code(), response.message());
					return false;
				}
			}
		}
		catch (IOException e)
		{
			log.error("Error pushing to JSONBin", e);
			return false;
		}
	}

	/**
	 * Data structure for unlocks stored in JSONBin
	 */
	public static class UnlockData
	{
		public Map<Integer, UnlockEntry> unlockedItems;
		public long lastUpdated;

		public UnlockData()
		{
			this.unlockedItems = new HashMap<>();
			this.lastUpdated = System.currentTimeMillis();
		}

		public void addUnlock(int itemId, String itemName, String playerName)
		{
			if (!unlockedItems.containsKey(itemId))
			{
				unlockedItems.put(itemId, new UnlockEntry(itemName, playerName, System.currentTimeMillis()));
				lastUpdated = System.currentTimeMillis();
			}
		}

		public boolean isUnlocked(int itemId)
		{
			return unlockedItems.containsKey(itemId);
		}

		public Set<Integer> getItemIds()
		{
			return unlockedItems.keySet();
		}
	}

	/**
	 * Individual unlock entry
	 */
	public static class UnlockEntry
	{
		public String itemName;
		public String unlockedBy;
		public long timestamp;

		public UnlockEntry(String itemName, String unlockedBy, long timestamp)
		{
			this.itemName = itemName;
			this.unlockedBy = unlockedBy;
			this.timestamp = timestamp;
		}
	}
}
