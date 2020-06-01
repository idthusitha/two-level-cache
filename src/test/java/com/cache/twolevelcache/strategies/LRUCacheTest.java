package com.cache.twolevelcache.strategies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.cache.twolevelcache.TwoLevelCache;

public class LRUCacheTest {
	private TwoLevelCache<Integer, String> twoLevelCache;

	@AfterEach
	public void clear() throws IOException {
		twoLevelCache.clear();
	}

	@Test
	public void shouldMoveObjectFromCacheTest() throws Exception {
		twoLevelCache = new TwoLevelCache<>(2, 2, "LRU");

		// i=0 - Least Recently Used - will be removed
		IntStream.range(0, 4).forEach(i -> {
			try {
				twoLevelCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assertTrue(twoLevelCache.isObjectPresent(i));
			twoLevelCache.get(i);
		});

		twoLevelCache.put(4, "String 4");

		assertFalse(twoLevelCache.isObjectPresent(0));
		assertTrue(twoLevelCache.isObjectPresent(1));
		assertTrue(twoLevelCache.isObjectPresent(2));
		assertTrue(twoLevelCache.isObjectPresent(3));
		assertTrue(twoLevelCache.isObjectPresent(4));
	}
}
