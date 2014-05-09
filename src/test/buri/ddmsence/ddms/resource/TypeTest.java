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
package buri.ddmsence.ddms.resource;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:type elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class TypeTest extends AbstractBaseTestCase {

	private static final String TEST_DESCRIPTION = "Description";
	private static final String TEST_QUALIFIER = "DCMITYPE";
	private static final String TEST_VALUE = "http://purl.org/dc/dcmitype/Text";

	/**
	 * Constructor
	 */
	public TypeTest() {
		super("type.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Type getFixture() {
		try {
			return (new Type(null, "DCMITYPE", "http://purl.org/dc/dcmitype/Text", null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Type getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Type component = null;
		try {
			component = new Type(element);
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
	private Type getInstance(Type.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Type component = null;
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
	 * Returns a builder, pre-populated with base data from the XML sample.
	 * 
	 * This builder can then be modified to test various conditions.
	 */
	private Type.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Type component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Type.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		if (version.isAtLeast("4.0.1"))
			text.append(buildOutput(isHTML, "type.description", TEST_DESCRIPTION));
		text.append(buildOutput(isHTML, "type.qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "type.value", TEST_VALUE));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, "type.classification", "U"));
			text.append(buildOutput(isHTML, "type.ownerProducer", "USA"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:type ").append(getXmlnsDDMS()).append(" ");
		if (version.isAtLeast("4.0.1")) {
			xml.append(getXmlnsISM()).append(" ");
		}
		xml.append("ddms:qualifier=\"").append(TEST_QUALIFIER).append("\" ");
		xml.append("ddms:value=\"").append(TEST_VALUE).append("\"");
		if (version.isAtLeast("4.0.1")) {
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">Description</ddms:type>");
		}
		else
			xml.append(" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Type.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getValidElement(sVersion), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}

	public void testConstructorsMinimal() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = Util.buildDDMSElement(Type.getName(version), null);
			Type elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			Type.Builder builder = new Type.Builder(elementComponent);
			getInstance(builder, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing qualifier
			Type.Builder builder = getBaseBuilder();
			builder.setQualifier(null);
			getInstance(builder, "qualifier attribute must exist");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Type component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Qualifier without value
			Type.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A qualifier has been set without an accompanying value attribute.";
			String locator = "ddms:type";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

			// Completely empty
			Element element = Util.buildDDMSElement(Type.getName(version), null);
			component = getInstance(element, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			text = "Neither a qualifier nor a value was set on this type.";
			locator = "ddms:type";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Type elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Type builderComponent = new Type.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Type.Builder builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.setDescription(DIFFERENT_VALUE);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 3.1
		Type.Builder builder = getBaseBuilder();
		builder.setDescription(null);
		DDMSVersion.setCurrentVersion("3.1");
		getInstance(builder, "Security attributes must not be applied");

		// No description until DDMS 4.0.1
		DDMSVersion.setCurrentVersion("3.1");
		builder = getBaseBuilder();
		builder.setDescription(TEST_DESCRIPTION);
		getInstance(builder, "This component must not contain description");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Type elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Type.Builder builder = new Type.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}