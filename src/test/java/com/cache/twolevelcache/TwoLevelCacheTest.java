package com.cache.twolevelcache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TwoLevelCacheTest {
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String VALUE3 = "value3";

	private TwoLevelCache<Integer, String> twoLevelCache;

	@BeforeEach
	public void init() throws IOException {
		twoLevelCache = new TwoLevelCache<>(1, 1);
	}

	@AfterEach
	public void clear() throws IOException {
		twoLevelCache.clear();
	}

	@Test
	public void shouldPutGetAndRemoveObjectTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertEquals(1, twoLevelCache.getSize());

		twoLevelCache.remove(0);
		assertNull(twoLevelCache.get(0));
	}

	@Test
	public void shouldRemoveObjectFromFirstLevelTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		twoLevelCache.put(1, VALUE2);

		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));

		twoLevelCache.remove(0);

		assertNull(twoLevelCache.getFirstLevelCache().get(0));
		assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));
	}

	@Test
	public void shouldRemoveObjectFromSecondLevelTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		twoLevelCache.put(1, VALUE2);

		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(1));

		twoLevelCache.remove(1);

		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertNull(twoLevelCache.getSecondLevelCache().get(1));
	}

	@Test
	public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertNull(twoLevelCache.get(111));
	}

	@Test
	public void shouldRemoveDuplicatedObjectFromSecondLevelWhenFirstLevelHasEmptyPlaceTest() throws IOException {
		assertTrue(twoLevelCache.getFirstLevelCache().hasEmptyPlace());

		twoLevelCache.getSecondLevelCache().put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.getSecondLevelCache().get(0));

		twoLevelCache.put(0, VALUE1);

		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
	}

	@Test
	public void shouldPutObjectIntoCacheWhenFirstLevelHasEmptyPlaceTest() throws IOException {
		assertTrue(twoLevelCache.getFirstLevelCache().hasEmptyPlace());
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
	}

	@Test
	public void shouldPutObjectIntoCacheWhenObjectExistsInFirstLevelCacheTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertEquals(1, twoLevelCache.getFirstLevelCache().getSize());

		twoLevelCache.put(0, VALUE2);

		assertEquals(VALUE2, twoLevelCache.get(0));
		assertEquals(VALUE2, twoLevelCache.getFirstLevelCache().get(0));
		assertEquals(1, twoLevelCache.getFirstLevelCache().getSize());
	}

	@Test
	public void shouldPutObjectIntoCacheWhenSecondLevelHasEmptyPlaceTest() throws IOException {
		IntStream.range(0, 1).forEach(i -> {
			try {
				twoLevelCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		assertFalse(twoLevelCache.getFirstLevelCache().hasEmptyPlace());
		assertTrue(twoLevelCache.getSecondLevelCache().hasEmptyPlace());

		twoLevelCache.put(2, VALUE2);

		assertEquals(VALUE2, twoLevelCache.get(2));
		assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(2));
	}

	@Test
	public void shouldPutObjectIntoCacheWhenObjectExistsInSecondLevelTest() throws IOException {
		IntStream.range(0, 1).forEach(i -> {
			try {
				twoLevelCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		assertFalse(twoLevelCache.getFirstLevelCache().hasEmptyPlace());

		twoLevelCache.put(2, VALUE2);

		assertEquals(VALUE2, twoLevelCache.get(2));
		assertEquals(VALUE2, twoLevelCache.getSecondLevelCache().get(2));
		assertEquals(1, twoLevelCache.getSecondLevelCache().getSize());

		twoLevelCache.put(2, VALUE3);

		assertEquals(VALUE3, twoLevelCache.get(2));
		assertEquals(VALUE3, twoLevelCache.getSecondLevelCache().get(2));
		assertEquals(1, twoLevelCache.getSecondLevelCache().getSize());
	}

	@Test
	public void shouldPutObjectIntoCacheWhenObjectShouldBeReplacedTest() throws IOException {
		IntStream.range(0, 2).forEach(i -> {
			try {
				twoLevelCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		assertFalse(twoLevelCache.hasEmptyPlace());
		assertFalse(twoLevelCache.getStrategy().isObjectPresent(3));

		twoLevelCache.put(3, VALUE3);

		assertEquals(twoLevelCache.get(3), VALUE3);
		assertTrue(twoLevelCache.getStrategy().isObjectPresent(3));
	}

	@Test
	public void shouldgetSizeTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		assertEquals(1, twoLevelCache.getSize());

		twoLevelCache.put(1, VALUE2);
		assertEquals(2, twoLevelCache.getSize());
	}

	@Test
	public void isObjectPresentTest() throws IOException {
		assertFalse(twoLevelCache.isObjectPresent(0));

		twoLevelCache.put(0, VALUE1);
		assertTrue(twoLevelCache.isObjectPresent(0));
	}

	@Test
	public void isEmptyPlaceTest() throws IOException {
		assertFalse(twoLevelCache.isObjectPresent(0));
		twoLevelCache.put(0, VALUE1);
		assertTrue(twoLevelCache.hasEmptyPlace());

		twoLevelCache.put(1, VALUE2);
		assertFalse(twoLevelCache.hasEmptyPlace());
	}

	@Test
	public void shouldclearTest() throws IOException {
		twoLevelCache.put(0, VALUE1);
		twoLevelCache.put(1, VALUE2);

		assertEquals(2, twoLevelCache.getSize());
		assertTrue(twoLevelCache.getStrategy().isObjectPresent(0));
		assertTrue(twoLevelCache.getStrategy().isObjectPresent(1));

		twoLevelCache.clear();

		assertEquals(0, twoLevelCache.getSize());
		assertFalse(twoLevelCache.getStrategy().isObjectPresent(0));
		assertFalse(twoLevelCache.getStrategy().isObjectPresent(1));
	}

	@Test
	public void shouldUseLRUStrategyTest() throws IOException {
		twoLevelCache = new TwoLevelCache<>(1, 1, "LRU");
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
	}

	@Test
	public void shouldUseMRUStrategyTest() throws IOException {
		twoLevelCache = new TwoLevelCache<>(1, 1, "MRU");
		twoLevelCache.put(0, VALUE1);
		assertEquals(VALUE1, twoLevelCache.get(0));
		assertEquals(VALUE1, twoLevelCache.getFirstLevelCache().get(0));
		assertFalse(twoLevelCache.getSecondLevelCache().isObjectPresent(0));
	}
}