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
package buri.ddmsence.samples.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple data class representing a distribution of keys.
 * 
 * <p>
 * This class is used in the Escape application to keep track of how often
 * keywords and mimeTypes are used.
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class Distribution {

	private Map<String, Integer> _map = new HashMap<String, Integer>();

	/**
	 * Empty constructor
	 */
	public Distribution() {}

	/**
	 * Increments the count for a particular key.
	 * 
	 * @param key the key
	 */
	public void incrementCount(String key) {
		Integer count = getCount(key);
		getMap().put(key, new Integer(count + 1));
	}

	/**
	 * Accessor for the keys
	 */
	public Set<String> getKeys() {
		return (getMap().keySet());
	}

	/**
	 * Accessor for a count
	 */
	public Integer getCount(String key) {
		Integer count = getMap().get(key);
		if (count == null)
			count = new Integer(0);
		return (count);
	}

	/**
	 * Accessor for the map
	 */
	private Map<String, Integer> getMap() {
		return (_map);
	}
}
