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
 * Tests related to ddms:format elements
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class FormatTest extends AbstractBaseTestCase {

	private static final String TEST_MIME_TYPE = "text/xml";
	private static final String TEST_MEDIUM = "digital";

	/**
	 * Constructor
	 */
	public FormatTest() throws InvalidDDMSException {
		super("format.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Format getFixture() {
		try {
			return (new Format("text/xml", null, null));
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
	private Format getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Format component = null;
		try {
			component = new Format(element);
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
	private Format getInstance(Format.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Format component = null;
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
	private Format.Builder getBaseBuilder() {
		Format component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new Format.Builder(component));
	}

	/**
	 * Helper method to manage the deprecated Media wrapper element
	 * 
	 * @param innerElement the element containing the guts of this component
	 * @return the element itself in DDMS 4.0.1 or later, or the element wrapped in another element
	 */
	private Element wrapInnerElement(Element innerElement) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String name = Format.getName(version);
		if (version.isAtLeast("4.0.1")) {
			innerElement.setLocalName(name);
			return (innerElement);
		}
		Element element = Util.buildDDMSElement(name, null);
		element.appendChild(innerElement);
		return (element);
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = version.isAtLeast("4.0.1") ? "format." : "format.Media.";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, prefix + "mimeType", TEST_MIME_TYPE));
		text.append(ExtentTest.getFixture().getOutput(isHTML, prefix, ""));
		text.append(buildOutput(isHTML, prefix + "medium", TEST_MEDIUM));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:format ").append(getXmlnsDDMS()).append(">\n\t");
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1")) {
			xml.append("<ddms:mimeType>text/xml</ddms:mimeType>\n\t");
			xml.append("<ddms:extent ddms:qualifier=\"sizeBytes\" ddms:value=\"75000\" />\n\t");
			xml.append("<ddms:medium>digital</ddms:medium>\n");
		}
		else {
			xml.append("<ddms:Media>\n\t\t");
			xml.append("<ddms:mimeType>text/xml</ddms:mimeType>\n\t\t");
			xml.append("<ddms:extent ddms:qualifier=\"sizeBytes\" ddms:value=\"75000\" />\n\t\t");
			xml.append("<ddms:medium>digital</ddms:medium>\n\t");
			xml.append("</ddms:Media>\n");
		}
		xml.append("</ddms:format>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Format.getName(version));
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
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, no optional fields
			Element mediaElement = Util.buildDDMSElement("Media", null);
			Util.addDDMSChildElement(mediaElement, "mimeType", "text/html");
			Format elementComponent = getInstance(wrapInnerElement(mediaElement), SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new Format.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing mimeType
			Format.Builder builder = getBaseBuilder();
			builder.setMimeType(null);
			getInstance(builder, "mimeType must exist.");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Format component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Format elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Format builderComponent = new Format.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Format.Builder builder = getBaseBuilder();
			builder.setMimeType(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setExtent(null);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setMedium(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Format elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Format.Builder builder = new Format.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setMimeType(TEST_MIME_TYPE);
			assertFalse(builder.isEmpty());
		}
	}

	public void testExtentAccessors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base case
			Format component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(component.getExtentQualifier(), component.getExtent().getQualifier());
			assertEquals(component.getExtentValue(), component.getExtent().getValue());

			// No Extent
			Format.Builder builder = getBaseBuilder();
			builder.setExtent(null);
			component = builder.commit();
			assertEquals("", component.getExtentQualifier());
			assertEquals("", component.getExtentValue());
		}
	}
}