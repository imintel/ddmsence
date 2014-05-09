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
package buri.ddmsence.ddms.summary;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:countryCode elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class CountryCodeTest extends AbstractBaseTestCase {

	private static final String TEST_QUALIFIER = "ISO-3166";
	private static final String TEST_VALUE = "USA";

	/**
	 * Constructor
	 */
	public CountryCodeTest() {
		super("countryCode.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static CountryCode getFixture() {
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				return (new CountryCode("http://api.nsgreg.nga.mil/geo-political/GENC/2/ed1", "US"));
			return (new CountryCode("ISO-3166", "USA"));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns the name of the qualifier attribute, based on DDMS version
	 */
	public static String getTestQualifierName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "codespace" : "qualifier");
	}

	/**
	 * Returns the name of the value attribute, based on DDMS version
	 */
	public static String getTestValueName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "code" : "value");
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private CountryCode getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		CountryCode component = null;
		try {
			component = new CountryCode(element);
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
	private CountryCode getInstance(CountryCode.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		CountryCode component = null;
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
	private CountryCode.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		CountryCode component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new CountryCode.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "countryCode." + getTestQualifierName(), TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "countryCode." + getTestValueName(), TEST_VALUE));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:countryCode ").append(getXmlnsDDMS()).append(" ");
		xml.append("ddms:").append(getTestQualifierName()).append("=\"").append(TEST_QUALIFIER).append("\" ");
		xml.append("ddms:").append(getTestValueName()).append("=\"").append(TEST_VALUE).append("\" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				CountryCode.getName(version));
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
		// No tests
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing qualifier
			CountryCode.Builder builder = getBaseBuilder();
			builder.setQualifier(null);
			getInstance(builder, getTestQualifierName() + " attribute must exist.");

			// Missing value
			builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, getTestValueName() + " attribute must exist.");
			
			// GENC tests are tested in GeographicIdentifier.
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			CountryCode component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			CountryCode elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			CountryCode builderComponent = new CountryCode.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Different values in each field	
			CountryCode.Builder builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
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

			CountryCode elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			CountryCode.Builder builder = new CountryCode.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
