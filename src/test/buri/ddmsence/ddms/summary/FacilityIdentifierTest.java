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
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:facilityIdentifier elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class FacilityIdentifierTest extends AbstractBaseTestCase {
	private static final String TEST_BENUMBER = "1234DD56789";
	private static final String TEST_OSUFFIX = "DD123";

	/**
	 * Constructor
	 */
	public FacilityIdentifierTest() {
		super("facilityIdentifier.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static FacilityIdentifier getFixture() {
		try {
			return (new FacilityIdentifier("1234DD56789", "DD123"));
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
	private FacilityIdentifier getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		FacilityIdentifier component = null;
		try {
			component = new FacilityIdentifier(element);
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
	private FacilityIdentifier getInstance(FacilityIdentifier.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		FacilityIdentifier component = null;
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
	private FacilityIdentifier.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		FacilityIdentifier component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new FacilityIdentifier.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "facilityIdentifier.beNumber", TEST_BENUMBER));
		text.append(buildOutput(isHTML, "facilityIdentifier.osuffix", TEST_OSUFFIX));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:facilityIdentifier ").append(getXmlnsDDMS()).append(" ");
		xml.append("ddms:beNumber=\"").append(TEST_BENUMBER).append("\" ");
		xml.append("ddms:osuffix=\"").append(TEST_OSUFFIX).append("\" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				FacilityIdentifier.getName(version));
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

			// Missing beNumber
			FacilityIdentifier.Builder builder = getBaseBuilder();
			builder.setBeNumber(null);
			getInstance(builder, "beNumber must exist.");

			// Missing osuffix
			builder = getBaseBuilder();
			builder.setOsuffix(null);
			getInstance(builder, "osuffix must exist.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			FacilityIdentifier component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			FacilityIdentifier elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			FacilityIdentifier builderComponent = new FacilityIdentifier.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field	
			FacilityIdentifier.Builder builder = getBaseBuilder();
			builder.setBeNumber(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setOsuffix(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			FacilityIdentifier elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			FacilityIdentifier.Builder builder = new FacilityIdentifier.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setBeNumber(TEST_BENUMBER);
			assertFalse(builder.isEmpty());
		}
	}
}
