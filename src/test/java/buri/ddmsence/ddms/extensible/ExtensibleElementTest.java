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
package buri.ddmsence.ddms.extensible;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to extensible layer elements </p>
 * 
 * @author Brian Uri!
 * @since 1.1.0
 */
public class ExtensibleElementTest extends AbstractBaseTestCase {

	private static final String TEST_NAME = "extension";
	private static final String TEST_PREFIX = "ddmsence";
	private static final String TEST_NAMESPACE = "http://ddmsence.urizone.net/";

	/**
	 * Constructor
	 */
	public ExtensibleElementTest() {
		super(null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		return (Util.buildElement(TEST_PREFIX, TEST_NAME, TEST_NAMESPACE, "This is an extensible element."));
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private ExtensibleElement getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ExtensibleElement component = null;
		try {
			component = new ExtensibleElement(element);
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (component);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param builder the builder to commit
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private ExtensibleElement getInstance(ExtensibleElement.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ExtensibleElement component = null;
		try {
			component = builder.commit();
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (component);
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddmsence:extension xmlns:ddmsence=\"http://ddmsence.urizone.net/\">");
		xml.append("This is an extensible element.</ddmsence:extension>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), TEST_PREFIX, TEST_NAME);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(), SUCCESS);

			// Data-based via Builder
			ExtensibleElement.Builder builder = new ExtensibleElement.Builder();
			builder.setXml(getExpectedXMLOutput());
			getInstance(builder, SUCCESS);
		}
	}

	public void testConstructorsMinimal() {
		// No tests.
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Using the DDMS namespace
			Element element = Util.buildDDMSElement("name", null);
			getInstance(element, "Extensible elements must not be defined in the DDMS namespace.");

			// Bad XML
			ExtensibleElement.Builder builder = new ExtensibleElement.Builder();
			builder.setXml("This is not XML.");
			getInstance(builder, "Could not create");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ExtensibleElement component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ExtensibleElement elementComponent = getInstance(getFixtureElement(), SUCCESS);
			ExtensibleElement builderComponent = new ExtensibleElement.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Element element = Util.buildElement(TEST_PREFIX, "newName", TEST_NAMESPACE,
				"This is an extensible element.");
			ExtensibleElement.Builder builder = new ExtensibleElement.Builder();
			builder.setXml(element.toXML());
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ExtensibleElement elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals("", elementComponent.toHTML());
			assertEquals("", elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ExtensibleElement.Builder builder = new ExtensibleElement.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setXml("<test/>");
			assertFalse(builder.isEmpty());
		}
	}
}
