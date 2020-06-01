package com.cache.twolevelcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemoryCacheTest {
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";

	private MemoryCache<Integer, String> memoryCache;

	@BeforeEach
	public void init() {
		memoryCache = new MemoryCache<>(3);
	}

	@AfterEach
	public void clear() {
		memoryCache.clear();
	}

	@Test
	public void shouldPutGetAndRemoveObjectTest() {
		memoryCache.put(0, VALUE1);
		assertEquals(VALUE1, memoryCache.get(0));
		assertEquals(1, memoryCache.getSize());

		memoryCache.remove(0);
		assertNull(memoryCache.get(0));
	}

	@Test
	public void shouldNotGetObjectFromCacheIfNotExistsTest() {
		memoryCache.put(0, VALUE1);
		assertEquals(VALUE1, memoryCache.get(0));
		assertNull(memoryCache.get(111));
	}

	@Test
	public void shouldNotRemoveObjectFromCacheIfNotExistsTest() {
		memoryCache.put(0, VALUE1);
		assertEquals(VALUE1, memoryCache.get(0));
		assertEquals(1, memoryCache.getSize());

		memoryCache.remove(5);
		assertEquals(VALUE1, memoryCache.get(0));
	}

	@Test
	public void shouldgetSizeTest() {
		memoryCache.put(0, VALUE1);
		assertEquals(1, memoryCache.getSize());

		memoryCache.put(1, VALUE2);
		assertEquals(2, memoryCache.getSize());
	}

	@Test
	public void isObjectPresentTest() {
		assertFalse(memoryCache.isObjectPresent(0));

		memoryCache.put(0, VALUE1);
		assertTrue(memoryCache.isObjectPresent(0));
	}

	@Test
	public void isEmptyPlaceTest() {
		memoryCache = new MemoryCache<>(5);

		IntStream.range(0, 4).forEach(i -> memoryCache.put(i, "String " + i));

		assertTrue(memoryCache.hasEmptyPlace());
		memoryCache.put(5, "String");
		assertFalse(memoryCache.hasEmptyPlace());
	}

	@Test
	public void shouldclearTest() {
		IntStream.range(0, 3).forEach(i -> memoryCache.put(i, "String " + i));

		assertEquals(3, memoryCache.getSize());
		memoryCache.clear();
		assertEquals(0, memoryCache.getSize());
	}
}