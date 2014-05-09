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
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:subtitle elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class SubtitleTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "Version 0.1";

	/**
	 * Constructor
	 */
	public SubtitleTest() {
		super("subtitle.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Subtitle getFixture() {
		try {
			return (new Subtitle("Version 0.1", SecurityAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Subtitle getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Subtitle component = null;
		try {
			component = new Subtitle(element);
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
	private Subtitle getInstance(Subtitle.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Subtitle component = null;
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
	private Subtitle.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Subtitle component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Subtitle.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "subtitle", TEST_VALUE));
		text.append(buildOutput(isHTML, "subtitle.classification", "U"));
		text.append(buildOutput(isHTML, "subtitle.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:subtitle ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(
			" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append(TEST_VALUE).append("</ddms:subtitle>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Subtitle.getName(version));
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

	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = Util.buildDDMSElement(Subtitle.getName(version), null);
			SecurityAttributesTest.getFixture().addTo(element);
			getInstance(element, SUCCESS);

			// Data-based, No optional fields
			Subtitle.Builder builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, SUCCESS);
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Bad security attributes
			Subtitle.Builder builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Subtitle component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Completely empty
			Subtitle.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A ddms:subtitle element was found with no";
			String locator = "ddms:subtitle";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Subtitle elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Subtitle builderComponent = new Subtitle.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Subtitle.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Subtitle elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Subtitle.Builder builder = new Subtitle.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
