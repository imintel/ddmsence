/* Copyright 2010 - 2013 by Brian Uri!
   
   This file is part of DDMSence.
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 3.0 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public 
   License along with DDMSence. If not, see <http://www.gnu.org/licenses/>.

   You can contact the author at ddmsence@urizone.net. The DDMSence
   home page is located at http://ddmsence.urizone.net/
 */
package buri.ddmsence.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Utility class for dealing with the property file.
 * 
 * <p> Properties in DDMSence are found in the <code>ddmsence.properties</code> file. All properties are prefixed with
 * "buri.ddmsence.", so <code>getProperty</code> calls should be performed with just the property suffix. </p>
 * 
 * <p> The Property Reader supports several custom properties, which can be specified at runtime. The complete list of
 * configurable properties can be found on the DDMSence website at:
 * http://ddmsence.urizone.net/documentation.jsp#tips-configuration. </p>
 * 
 * <p> Changing a namespace prefix will affect both components created from scratch and components loaded from XML
 * files. </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class PropertyReader {
	private Properties _properties = new Properties();

	private static final String PROPERTIES_FILE = "ddmsence.properties";
	private static final String PROPERTIES_PREFIX = "buri.ddmsence.";
	private static final String UNDEFINED_PROPERTY = "Undefined Property: ";

	private static final Set<String> CUSTOM_PROPERTIES = new HashSet<String>();
	static {
		CUSTOM_PROPERTIES.add("ddms.prefix");
		CUSTOM_PROPERTIES.add("gml.prefix");
		CUSTOM_PROPERTIES.add("ism.prefix");
		CUSTOM_PROPERTIES.add("ntk.prefix");
		CUSTOM_PROPERTIES.add("output.indexLevel");
		CUSTOM_PROPERTIES.add("sample.data");
		CUSTOM_PROPERTIES.add("tspi.prefix");
		CUSTOM_PROPERTIES.add("virt.prefix");
		CUSTOM_PROPERTIES.add("xlink.prefix");
		CUSTOM_PROPERTIES.add("xml.transform.TransformerFactory");
	};

	private static final PropertyReader INSTANCE = new PropertyReader();

	/**
	 * Private to prevent instantiation
	 */
	private PropertyReader() {
		InputStream is = getLoader().getResourceAsStream(PROPERTIES_FILE);
		try {
			if (is != null) {
				Properties aProperties = new Properties();
				aProperties.load(is);
				is.close();
				_properties.putAll(aProperties);
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Could not load the properties file: " + e.getMessage());
		}
	}

	/**
	 * Convenience method to look up an XML prefix
	 * 
	 * @param key the schema key, such as ddms, ism, or ntk.
	 */
	public static String getPrefix(String key) {
		return (getProperty(key + ".prefix"));
	}

	/**
	 * Locates a property and returns it. Assumes that the property must exist.
	 * 
	 * @param name the simple name of the property, without "buri.ddmsence."
	 * @return the property specified
	 * @throws IllegalArgumentException if the property does not exist.
	 */
	public static String getProperty(String name) {
		String value = INSTANCE.getProperties().getProperty(PROPERTIES_PREFIX + name);
		if (value == null)
			throw new IllegalArgumentException(UNDEFINED_PROPERTY + PROPERTIES_PREFIX + name);
		return (value);
	}

	/**
	 * Attempts to set one of the properties defined as a configurable property.
	 * 
	 * @param name the key of the property, without the "buri.ddmsence." prefix
	 * @param value the new value of the property
	 * @throws IllegalArgumentException if the property is not a valid configurable property.
	 */
	public static void setProperty(String name, String value) {
		if (!CUSTOM_PROPERTIES.contains(name))
			throw new IllegalArgumentException(name + " is not a configurable property.");
		INSTANCE.getProperties().setProperty(PROPERTIES_PREFIX + name, Util.getNonNullString(value).trim());
	}

	/**
	 * Locates a list property and returns it as a List
	 * 
	 * @param name the simple name of the property, without "buri.ddmsence."
	 * @return the property specified
	 * @throws IllegalArgumentException if the property does not exist
	 */
	public static List<String> getListProperty(String name) {
		String value = getProperty(name);
		String[] values = value.split(",");
		List<String> listValues = Arrays.asList(values);
		return (Collections.unmodifiableList(listValues));
	}

	/**
	 * Generate a ClassLoader to be used to load resources
	 * 
	 * @return a ClassLoader
	 */
	private static ClassLoader getLoader() {
		return new FindClassLoader().getClass().getClassLoader();
	}

	/**
	 * Stub to load classes.
	 */
	private static class FindClassLoader {
		public FindClassLoader() {}
	}

	/**
	 * Accessor for properties object.
	 */
	private Properties getProperties() {
		return (_properties);
	}
}
