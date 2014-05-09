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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:geographicIdentifier elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class GeographicIdentifierTest extends AbstractBaseTestCase {

	private static final List<String> TEST_NAMES = new ArrayList<String>();
	static {
		TEST_NAMES.add("The White House");
	}

	private static final List<String> TEST_REGIONS = new ArrayList<String>();
	static {
		TEST_REGIONS.add("Mid-Atlantic States");
	}

	/**
	 * Constructor
	 */
	public GeographicIdentifierTest() throws InvalidDDMSException {
		super("geographicIdentifier.xml");
	}

	/**
	 * Returns a fixture object for testing. This object will be based on a country code.
	 */
	public static GeographicIdentifier getCountryCodeBasedFixture() throws InvalidDDMSException {
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				return new GeographicIdentifier(null, null, new CountryCode(
					"urn:us:gov:dod:nga:def:geo-political:GENC:3:ed1", "USA"), null);
			}
			return new GeographicIdentifier(null, null, new CountryCode(
				"urn:us:gov:ic:cvenum:irm:coverage:iso3166:trigraph:v1", "LAO"), null);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing. This object will be based on a facility ID
	 */
	public static GeographicIdentifier getFacIdBasedFixture() throws InvalidDDMSException {
		try {
			return (new GeographicIdentifier(FacilityIdentifierTest.getFixture()));
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
	private GeographicIdentifier getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		GeographicIdentifier component = null;
		try {
			component = new GeographicIdentifier(element);
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
	private GeographicIdentifier getInstance(GeographicIdentifier.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		GeographicIdentifier component = null;
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
	private GeographicIdentifier.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		GeographicIdentifier component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new GeographicIdentifier.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "geographicIdentifier.name", TEST_NAMES.get(0)));
		text.append(buildOutput(isHTML, "geographicIdentifier.region", TEST_REGIONS.get(0)));
		text.append(CountryCodeTest.getFixture().getOutput(isHTML, "geographicIdentifier.", ""));
		if (version.isAtLeast("4.0.1"))
			text.append(SubDivisionCodeTest.getFixture().getOutput(isHTML, "geographicIdentifier.", ""));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:geographicIdentifier ").append(getXmlnsDDMS()).append(">\n\t");
		xml.append("<ddms:name>The White House</ddms:name>\n\t");
		xml.append("<ddms:region>Mid-Atlantic States</ddms:region>\n\t");
		if (version.isAtLeast("5.0")) {
			xml.append("<ddms:countryCode ddms:").append(CountryCodeTest.getTestQualifierName());
			xml.append("=\"http://api.nsgreg.nga.mil/geo-political/GENC/2/ed1\" ");
			xml.append("ddms:").append(CountryCodeTest.getTestValueName()).append("=\"US\" />\n");
		}
		else {
			xml.append("<ddms:countryCode ddms:").append(CountryCodeTest.getTestQualifierName());
			xml.append("=\"ISO-3166\" ddms:").append(CountryCodeTest.getTestValueName()).append("=\"USA\" />\n");
		}
		if (version.isAtLeast("4.0.1")) {
			xml.append("\t<ddms:subDivisionCode ddms:").append(CountryCodeTest.getTestQualifierName());
			xml.append("=\"ISO-3166\" ddms:").append(CountryCodeTest.getTestValueName()).append("=\"USA\" />\n");
		}
		xml.append("</ddms:geographicIdentifier>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				GeographicIdentifier.getName(version));
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
			String geoIdName = GeographicIdentifier.getName(version);

			// No optional fields
			Element element = Util.buildDDMSElement(geoIdName, null);
			element.appendChild(Util.buildDDMSElement("name", TEST_NAMES.get(0)));
			GeographicIdentifier elementComponent = getInstance(element, SUCCESS);

			getInstance(new GeographicIdentifier.Builder(elementComponent), SUCCESS);
			
			element = Util.buildDDMSElement(geoIdName, null);
			element.appendChild(Util.buildDDMSElement("region", TEST_REGIONS.get(0)));
			elementComponent = getInstance(element, SUCCESS);

			getInstance(new GeographicIdentifier.Builder(elementComponent), SUCCESS);
			
			element = Util.buildDDMSElement(geoIdName, null);
			element.appendChild(CountryCodeTest.getFixture().getXOMElementCopy());
			elementComponent = getInstance(element, SUCCESS);

			getInstance(new GeographicIdentifier.Builder(elementComponent), SUCCESS);
			
			if (version.isAtLeast("4.0.1")) {
				element = Util.buildDDMSElement(geoIdName, null);
				element.appendChild(SubDivisionCodeTest.getFixture().getXOMElementCopy());
				elementComponent = getInstance(element, SUCCESS);
				
				getInstance(new GeographicIdentifier.Builder(elementComponent), SUCCESS);
			}

			element = Util.buildDDMSElement(geoIdName, null);
			element.appendChild(FacilityIdentifierTest.getFixture().getXOMElementCopy());
			elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new GeographicIdentifier.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String geoIdName = GeographicIdentifier.getName(version);
			
			// Element-based, At least 1 name, region, countryCode, or facilityIdentifier must exist.
			Element element = Util.buildDDMSElement(geoIdName, null);
			getInstance(element, "At least 1 of ");

			// Data-based, At least 1 name, region, countryCode, or facilityIdentifier must exist.
			try {
				new GeographicIdentifier(null,  null, null, null);
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least 1 of ");
			}
			
			// facilityIdentifier must be alone
			element = Util.buildDDMSElement(geoIdName, null);
			element.appendChild(CountryCodeTest.getFixture().getXOMElementCopy());
			element.appendChild(FacilityIdentifierTest.getFixture().getXOMElementCopy());
			getInstance(element, "facilityIdentifier must not be used");
		}
	}
	
	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			GeographicIdentifier component = getInstance(getValidElement(sVersion), SUCCESS);

			if (!version.isAtLeast("5.0")) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// No NGA search
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ddms:countryCode is syntactically correct";
				String locator = "ddms:geographicIdentifier";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality, non-facility
			GeographicIdentifier elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			GeographicIdentifier builderComponent = new GeographicIdentifier.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field	
			GeographicIdentifier.Builder builder = getBaseBuilder();
			builder.getNames().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getRegions().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setCountryCode(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.setSubDivisionCode(null);
				assertFalse(elementComponent.equals(builder.commit()));
			}
			
			GeographicIdentifier dataComponent = getFacIdBasedFixture();
			assertFalse(elementComponent.equals(dataComponent));
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// No subdivisonCode before 4.0.1
		GeographicIdentifier.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("3.1");
		getInstance(builder, "The subDivisionCode element must not ");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// non-facility
			GeographicIdentifier elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
			
			// facility
			GeographicIdentifier component = getFacIdBasedFixture();
			StringBuffer facIdOutput = new StringBuffer();
			facIdOutput.append("<meta name=\"geographicIdentifier.facilityIdentifier.beNumber\" content=\"1234DD56789\" />\n");
			facIdOutput.append("<meta name=\"geographicIdentifier.facilityIdentifier.osuffix\" content=\"DD123\" />\n");
			assertEquals(facIdOutput.toString(), component.toHTML());
			facIdOutput = new StringBuffer();
			facIdOutput.append("geographicIdentifier.facilityIdentifier.beNumber: 1234DD56789\n");
			facIdOutput.append("geographicIdentifier.facilityIdentifier.osuffix: DD123\n");
			assertEquals(facIdOutput.toString(), component.toText());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			GeographicIdentifier.Builder builder = new GeographicIdentifier.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setNames(TEST_NAMES);
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			GeographicIdentifier.Builder builder = new GeographicIdentifier.Builder();
			assertNotNull(builder.getNames().get(1));
			assertNotNull(builder.getRegions().get(1));
		}
	}
	
	public void testGencCountryCodeSuccess() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("5.0");
		GeographicIdentifier.validateGencCountryCode(new CountryCode(
			"http://api.nsgreg.nga.mil/geo-political/GENC/2/ed1", "US"));
		GeographicIdentifier.validateGencCountryCode(new CountryCode("urn:us:gov:dod:nga:def:geo-political:GENC:3:ed1",
			"USA"));
		GeographicIdentifier.validateGencCountryCode(new CountryCode("geo-political:GENC:3:ed1", "USA"));
		GeographicIdentifier.validateGencCountryCode(new CountryCode("ge:GENC:n:ed1", "123"));
	}

	public void testGencCountryCodeFailure() {
		DDMSVersion.setCurrentVersion("5.0");
		try {
			GeographicIdentifier.validateGencCountryCode(new CountryCode("ISO-3166", "US"));
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "ddms:countryCode must use a geo-political");
		}
		try {
			GeographicIdentifier.validateGencCountryCode(new CountryCode(
				"urn:us:gov:dod:nga:def:geo-political:GENC:3:ed1", "US"));
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "A GENC country code in a 3-alpha codespace");
		}
		try {
			GeographicIdentifier.validateGencCountryCode(new CountryCode(
				"urn:us:gov:dod:nga:def:geo-political:GENC:2:ed1", "USA"));
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "A GENC country code in a 2-alpha codespace");
		}
		try {
			GeographicIdentifier.validateGencCountryCode(new CountryCode(
				"urn:us:gov:dod:nga:def:geo-political:GENC:n:ed1", "USA"));
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "A GENC country code in a numeric");
		}
	}
}
