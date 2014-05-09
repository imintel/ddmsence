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
package buri.ddmsence.ddms;

/**
 * Identifying interface for a mutable Builder of components
 * 
 * <p>The builder should be used when a DDMS record needs to be built up over time, but validation should not occur
 * until the end. The commit() method attempts to finalize the immutable object based on the values gathered.</p>
 * 
 * <p>The builder approach differs from calling the immutable constructor directly because it treats a Builder instance
 * with no values provided as "no component" instead of "a component with missing values". For example, calling a
 * constructor directly with an empty string for a required parameter might throw an InvalidDDMSException, while calling
 * commit() on a Builder without setting any values would just return null.</p>
 * 
 * @author Brian Uri!
 * @since 1.8.0
 */
public interface IBuilder {

	/**
	 * Finalizes the data gathered for this builder instance. If no values have been provided, a null instance will be
	 * returned instead of a possibly invalid one or an empty one.
	 * 
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public IDDMSComponent commit() throws InvalidDDMSException;

	/**
	 * Checks if any values have been provided for this Builder.
	 * 
	 * @return true if every field is empty
	 */
	public boolean isEmpty();
}
