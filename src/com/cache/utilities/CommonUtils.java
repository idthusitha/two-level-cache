package com.cache.utilities;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Scanner;


/**
 * 
 * @author Thusitha Dissanayaka
 * 
 */
public class CommonUtils {	
	private CommonUtils() {
	}

	public static Properties properties = null;
	private static CommonUtils instance = null;

	/**
	 * @return CommonUtils
	 */
	public static CommonUtils getInstance() {
		if (instance == null) {
			instance = new CommonUtils();
		}
		return instance;
	}


	public Properties getProperties() {

		Properties properties = null;
		try {
			java.util.Locale myLocale = java.util.Locale.getDefault();
			PropertyResourceBundle resource = (PropertyResourceBundle) PropertyResourceBundle.getBundle("application", myLocale);
			properties = convertResourceBundleToProperties(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;

	}

	private Properties convertResourceBundleToProperties(ResourceBundle resource) {
		Properties properties = new Properties();

		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}

		return properties;
	}

	public String getFile(String fileName) {
		StringBuilder result = new StringBuilder("");

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public int getNumber(String number) {
		int numeric = 0;
		try {
			numeric = Integer.parseInt(number);
		} catch (Exception e) {
			numeric = 0;
		}
		return numeric;
	}
}
