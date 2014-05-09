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
package buri.ddmsence.ddms.format;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p>
 * Tests related to ddms:extent elements
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class ExtentTest extends AbstractBaseTestCase {

	private static final String TEST_QUALIFIER = "sizeBytes";
	private static final String TEST_VALUE = "75000";

	/**
	 * Constructor
	 */
	public ExtentTest() {
		super("extent.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Extent getFixture() {
		try {
			return (new Extent(TEST_QUALIFIER, TEST_VALUE));
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
	private Extent getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Extent component = null;
		try {
			component = new Extent(element);
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
	private Extent getInstance(Extent.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Extent component = null;
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
	private Extent.Builder getBaseBuilder() {
		Extent component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new Extent.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "extent.qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "extent.value", TEST_VALUE));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:extent ").append(getXmlnsDDMS()).append(" ");
		xml.append("ddms:qualifier=\"").append(TEST_QUALIFIER).append("\" ");
		xml.append("ddms:value=\"").append(TEST_VALUE).append("\" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Extent.getName(version));
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

			// Element-based, no optional fields
			Element element = Util.buildDDMSElement(Extent.getName(version), null);
			Extent component = getInstance(element, SUCCESS);

			// Data-based, no optional fields
			getInstance(new Extent.Builder(component), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing qualifier
			Extent.Builder builder = getBaseBuilder();
			builder.setQualifier(null);
			getInstance(builder, "qualifier attribute must exist.");

			// Qualifier not URI
			builder = getBaseBuilder();
			builder.setQualifier(INVALID_URI);
			getInstance(builder, "Invalid URI");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			final String locator = "ddms:extent";

			// No warnings
			Extent component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Qualifier without value
			Extent.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A qualifier has been set without an accompanying value attribute.";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

			// Completely empty
			Element element = Util.buildDDMSElement(Extent.getName(version), null);
			Extent elementComponent = getInstance(element, SUCCESS);
			assertEquals(1, elementComponent.getValidationWarnings().size());
			text = "A completely empty ddms:extent element was found.";
			assertWarningEquality(text, locator, elementComponent.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Extent elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Extent builderComponent = new Extent.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Extent.Builder builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Extent elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Extent.Builder builder = new Extent.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
