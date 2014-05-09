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

import junit.framework.TestCase;
import buri.ddmsence.ddms.resource.Rights;

/**
 * <p> Tests related to ValidationMessages </p>
 * 
 * @author Brian Uri!
 * @since 0.9.c
 */
public class ValidationMessageTest extends TestCase {

	public void testFactory() {
		ValidationMessage message = ValidationMessage.newWarning("Test", "ddms:test");
		assertEquals(ValidationMessage.WARNING_TYPE, message.getType());
		assertEquals("Test", message.getText());
		assertEquals("/ddms:test", message.getLocator());

		message = ValidationMessage.newError("Test", "ddms:test");
		assertEquals(ValidationMessage.ERROR_TYPE, message.getType());
		assertEquals("Test", message.getText());
		assertEquals("/ddms:test", message.getLocator());
	}

	public void testEquality() {
		ValidationMessage message1 = ValidationMessage.newWarning("Test", "ddms:test");
		ValidationMessage message2 = ValidationMessage.newWarning("Test", "ddms:test");
		assertEquals(message1, message1);
		assertEquals(message1, message2);
		assertEquals(message1.hashCode(), message2.hashCode());
		assertEquals(message1.toString(), message2.toString());
	}

	public void testInequalityDifferentValues() throws InvalidDDMSException {
		ValidationMessage message1 = ValidationMessage.newWarning("Test", "ddms:test");
		ValidationMessage message2 = ValidationMessage.newError("Test", "ddms:test");
		assertFalse(message1.equals(message2));

		message2 = ValidationMessage.newWarning("Test2", "ddms:test");
		assertFalse(message1.equals(message2));

		message2 = ValidationMessage.newWarning("Test", "ddms:test2");
		assertFalse(message1.equals(message2));
	}

	public void testInequalityWrongClass() throws InvalidDDMSException {
		ValidationMessage message = ValidationMessage.newWarning("Test", "ddms:test");
		Rights wrongComponent = new Rights(true, true, true);
		assertFalse(message.equals(wrongComponent));
	}

	public void testLocatorEquality() throws InvalidDDMSException {
		InvalidDDMSException e = new InvalidDDMSException("test");
		e.setLocator("test");
		assertEquals("/test", e.getLocator());
	}
}
