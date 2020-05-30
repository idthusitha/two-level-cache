package com.cache.twolevelcache.strategies;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.cache.twolevelcache.TwoLevelCache;

import static org.junit.jupiter.api.Assertions.*;

public class LFUCacheTest {
	private TwoLevelCache<Integer, String> twoLevelCache;

	@AfterEach
	public void clearCache() throws IOException {
		twoLevelCache.clear();
	}

	@Test
	public void shouldMoveObjectFromCacheTest() throws IOException {
		twoLevelCache = new TwoLevelCache<>(2, 2, "LFU");

		twoLevelCache.put(0, "String 0");
		twoLevelCache.get(0);
		twoLevelCache.get(0);
		twoLevelCache.put(1, "String 1");
		twoLevelCache.get(1); // Least Frequently Used - will be removed
		twoLevelCache.put(2, "String 2");
		twoLevelCache.get(2);
		twoLevelCache.get(2);
		twoLevelCache.put(3, "String 3");
		twoLevelCache.get(3);
		twoLevelCache.get(3);

		assertTrue(twoLevelCache.isObjectPresent(0));
		assertTrue(twoLevelCache.isObjectPresent(1));
		assertTrue(twoLevelCache.isObjectPresent(2));
		assertTrue(twoLevelCache.isObjectPresent(3));

		twoLevelCache.put(4, "String 4");
		twoLevelCache.get(4);
		twoLevelCache.get(4);

		assertTrue(twoLevelCache.isObjectPresent(0));
		assertFalse(twoLevelCache.isObjectPresent(1)); // Least Frequently Used - has been removed
		assertTrue(twoLevelCache.isObjectPresent(2));
		assertTrue(twoLevelCache.isObjectPresent(3));
		assertTrue(twoLevelCache.isObjectPresent(4));
	}

	@Test
	public void shouldNotRemoveObjectIfNotPresentTest() throws IOException {
		twoLevelCache = new TwoLevelCache<>(1, 1, "LFU");

		twoLevelCache.put(0, "String 0");
		twoLevelCache.put(1, "String 1");

		twoLevelCache.remove(2);

	}
}