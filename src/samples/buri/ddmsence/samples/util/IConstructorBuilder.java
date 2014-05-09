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

import java.io.IOException;

import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;

/**
 * Simple interface for a builder which takes data from the command line and converts it into a DDMS Component.
 * 
 * <p>
 * This interface is used in the Escort application. It is NOT the same as the {@link IBuilder} interface in the main
 * DDMSence library. This builder works by directly calling the constructor of a component, while the {@link IBuilder}
 * interface is a stateful builder that loads all of the pieces of a component and then commits the changes.
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.d
 */
public interface IConstructorBuilder {

	/**
	 * Takes user input and constructs a DDMS Component
	 * 
	 * @return the valid component
	 * @throws InvalidDDMSException if the component cannot be built from the user input
	 * @throws IOException if there are problems getting user input
	 */
	public IDDMSComponent build() throws InvalidDDMSException, IOException;
}
