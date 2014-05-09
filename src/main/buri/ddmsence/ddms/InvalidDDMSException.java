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

import java.net.URISyntaxException;

/**
 * Exception class for attempts to generate invalid DDMS components.
 * 
 * <p> The underlying data is stored as a ValidationMessage, which allows locator information to be set on it. Because
 * InvalidDDMSExceptions are singular (one is thrown) while validation warnings are gathered from subcomponents and
 * merged into a master list, we modify the exception itself when adding parent locator information. </p>
 * 
 * <p>Since a component is not nested in another component at the time of instantiation, it has no parent when a
 * validation exception is thrown. Therefore, the locator info will always consist of the single element whose
 * constructor was called.
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class InvalidDDMSException extends Exception {

	private ValidationMessage _message = null;

	private static final long serialVersionUID = -183915550465140589L;

	/**
	 * @see Exception#Exception(String)
	 */
	public InvalidDDMSException(String message) {
		super(message);
		_message = ValidationMessage.newError(getMessage(), null);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public InvalidDDMSException(Throwable nested) {
		super(nested);
		_message = ValidationMessage.newError(getMessage(), null);
	}

	/**
	 * Handles nested URISyntaxExceptions
	 * 
	 * @param e the exception
	 */
	public InvalidDDMSException(URISyntaxException e) {
		super("Invalid URI (" + e.getMessage() + ")", e);
		_message = ValidationMessage.newError(getMessage(), null);
	}

	/**
	 * Accessor for the underlying ValidationMessage
	 */
	private ValidationMessage getValidationMessage() {
		return _message;
	}

	/**
	 * Accessor for the locator
	 */
	public String getLocator() {
		return getValidationMessage().getLocator();
	}

	/**
	 * Prefixes some string to the beginning of the existing locator.
	 */
	public void setLocator(String locator) {
		getValidationMessage().setLocator(locator);
	}
}
