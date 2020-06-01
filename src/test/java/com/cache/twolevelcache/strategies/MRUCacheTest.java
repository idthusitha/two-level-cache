package com.cache.twolevelcache.strategies;

import com.cache.twolevelcache.TwoLevelCache;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MRUCacheTest {
	private TwoLevelCache<Integer, String> twoLevelCache;

	@AfterEach
	public void clearCache() throws IOException {
		twoLevelCache.clear();
	}

	@Test
	public void shouldMoveObjectFromCacheTest() throws IOException {
		twoLevelCache = new TwoLevelCache<>(2, 2, "MRU");

		// i=3 - Most Recently Used - will be removed
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
	}
}
