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

public class FileSystemCacheTest {
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";

	private FileSystemCache<Integer, String> fileSystemCache;

	@BeforeEach
	public void init() throws IOException {
		fileSystemCache = new FileSystemCache<>();
	}

	@AfterEach
	public void clear() throws IOException {
		fileSystemCache.clear();
	}

	@Test
	public void shouldPutGetAndRemoveObjectTest() throws IOException {
		fileSystemCache.put(0, VALUE1);
		assertEquals(VALUE1, fileSystemCache.get(0));
		assertEquals(1, fileSystemCache.getSize());

		fileSystemCache.remove(0);
		assertNull(fileSystemCache.get(0));
	}

	@Test
	public void shouldNotGetObjectFromCacheIfNotExistsTest() throws IOException {
		fileSystemCache.put(0, VALUE1);
		assertEquals(VALUE1, fileSystemCache.get(0));
		assertNull(fileSystemCache.get(111));
	}

	@Test
	public void shouldNotRemoveObjectFromCacheIfNotExistsTest() throws IOException {
		fileSystemCache.put(0, VALUE1);
		assertEquals(VALUE1, fileSystemCache.get(0));
		assertEquals(1, fileSystemCache.getSize());

		fileSystemCache.remove(5);
		assertEquals(VALUE1, fileSystemCache.get(0));
	}

	@Test
	public void shouldgetSizeTest() throws IOException {
		fileSystemCache.put(0, VALUE1);
		assertEquals(1, fileSystemCache.getSize());

		fileSystemCache.put(1, VALUE2);
		assertEquals(2, fileSystemCache.getSize());
	}

	@Test
	public void isObjectPresentTest() throws IOException {
		assertFalse(fileSystemCache.isObjectPresent(0));

		fileSystemCache.put(0, VALUE1);
		assertTrue(fileSystemCache.isObjectPresent(0));
	}

	@Test
	public void isEmptyPlaceTest() throws IOException {
		fileSystemCache = new FileSystemCache<>(5);

		IntStream.range(0, 4).forEach(i -> {
			try {
				fileSystemCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		assertTrue(fileSystemCache.hasEmptyPlace());
		fileSystemCache.put(5, "String");
		assertFalse(fileSystemCache.hasEmptyPlace());
	}

	@Test
	public void shouldclearTest() throws IOException {
		IntStream.range(0, 3).forEach(i -> {
			try {
				fileSystemCache.put(i, "String " + i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		assertEquals(3, fileSystemCache.getSize());
		fileSystemCache.clear();
		assertEquals(0, fileSystemCache.getSize());
	}
}
