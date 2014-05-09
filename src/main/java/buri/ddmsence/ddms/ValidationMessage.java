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

import buri.ddmsence.util.Util;

/**
 * Simple class representing a validation message.
 * 
 * <p> DDMS components are validated during instantiation, and a component is either valid or does not exist. These
 * messages are either embedded in a thrown InvalidDDMSException (in which case they are errors), or stored as
 * informational messages on the component itself (warnings). </p>
 * 
 * <p> The locator string provides additional information about the context of the error or warning. This string is
 * implemented as an XPath string right now: if the error was coming from a ddms:identifier component, the locator would
 * be "/ddms:identifier". Should a parent component choose to report the warnings/errors of a child component, the
 * locator string could be expanded with parent information, such as "/ddms:resource/ddms:identifier". </p>
 * 
 * <p> Please note that the XPath string is not intended to drill all the way down to the offending element or
 * attribute. It should merely provide enough context so that the source of the message can be discovered. <p>
 * 
 * @author Brian Uri!
 * @since 0.9.c
 */
public class ValidationMessage {

	private String _type = null;
	private String _text = null;
	private String _locator = null;

	/** Constant type for a warning. */
	public static final String WARNING_TYPE = "Warning";

	/** Constant type for an error. */
	public static final String ERROR_TYPE = "Error";

	/** XPath prefix to separate elements */
	public static final String ELEMENT_PREFIX = "/";

	/**
	 * Private constructor. Use factory methods to instantiate.
	 * 
	 * @param type the type of this message
	 * @param text the description text'
	 * @param locator a locator string, in XPath format. For attributes, use empty string. The parent element will claim
	 *        the attributes.
	 */
	private ValidationMessage(String type, String text, String locator) {
		Util.requireValue("text", text);
		_type = type;
		_text = text;
		setLocator(locator);
	}

	/**
	 * Factory method to create a warning
	 * 
	 * @param text the description text
	 * @param locator a locator string, in XPath format. For attributes, use empty string. The parent element will claim
	 *        the attributes.
	 * @return a new warning message
	 */
	public static ValidationMessage newWarning(String text, String locator) {
		return (new ValidationMessage(WARNING_TYPE, text, locator));
	}

	/**
	 * Factory method to create an error
	 * 
	 * @param text the description text
	 * @param locator a locator string, in XPath format. For attributes, use empty string. The parent element will claim
	 *        the attributes.
	 * @return a new error message
	 */
	public static ValidationMessage newError(String text, String locator) {
		return (new ValidationMessage(ERROR_TYPE, text, locator));
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		StringBuffer text = new StringBuffer();
		text.append(getType()).append(": ").append(getText());
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return (true);
		if (!(obj instanceof ValidationMessage))
			return (false);
		ValidationMessage test = (ValidationMessage) obj;
		return (getType().equals(test.getType()) 
			&& getText().equals(test.getText()) 
			&& getLocator().equals(test.getLocator()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = getType().hashCode();
		result = 7 * result + getText().hashCode();
		result = 7 * result + getLocator().hashCode();
		return (result);
	}

	/**
	 * Accessor for the type
	 */
	public String getType() {
		return _type;
	}

	/**
	 * Accessor for the text
	 */
	public String getText() {
		return _text;
	}

	/**
	 * Accessor for the locator
	 */
	public String getLocator() {
		return _locator;
	}

	/**
	 * Accessor for the locator.
	 * 
	 * <p>If the locator is null, it will be set to an empty string. If not, it will be prefixed with a
	 * forward-slash.</p>
	 */
	public void setLocator(String locator) {
		_locator = (locator == null ? "" : ELEMENT_PREFIX + locator);
	}
}
