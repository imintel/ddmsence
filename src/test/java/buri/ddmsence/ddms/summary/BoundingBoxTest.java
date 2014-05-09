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
 * <p> Tests related to ddms:boundingBox elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class BoundingBoxTest extends AbstractBaseTestCase {

	private static final double TEST_WEST = 12.3;
	private static final double TEST_EAST = 23.4;
	private static final double TEST_SOUTH = 34.5;
	private static final double TEST_NORTH = 45.6;

	/**
	 * Constructor
	 */
	public BoundingBoxTest() {
		super("boundingBox.xml");
		removeSupportedVersions("5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static BoundingBox getFixture() {
		try {
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				return (new BoundingBox(1.1, 2.2, 3.3, 4.4));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Helper method to get the name of the westbound longitude element, which changed in DDMS 4.0.1.
	 */
	private String getTestWestBLName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "westBL" : "WestBL");
	}

	/**
	 * Helper method to get the name of the eastbound longitude element, which changed in DDMS 4.0.1.
	 */
	private String getTestEastBLName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "eastBL" : "EastBL");
	}

	/**
	 * Helper method to get the name of the southbound latitude element, which changed in DDMS 4.0.1.
	 */
	private String getTestSouthBLName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "southBL" : "SouthBL");
	}

	/**
	 * Helper method to get the name of the northbound latitude element, which changed in DDMS 4.0.1.
	 */
	private String getTestNorthBLName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "northBL" : "NorthBL");
	}
	
	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private BoundingBox getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		BoundingBox component = null;
		try {
			component = new BoundingBox(element);
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
	private BoundingBox getInstance(BoundingBox.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		BoundingBox component = null;
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
	private BoundingBox.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		BoundingBox component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new BoundingBox.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "boundingBox." + getTestWestBLName(), String.valueOf(TEST_WEST)));
		text.append(buildOutput(isHTML, "boundingBox." + getTestEastBLName(), String.valueOf(TEST_EAST)));
		text.append(buildOutput(isHTML, "boundingBox." + getTestSouthBLName(), String.valueOf(TEST_SOUTH)));
		text.append(buildOutput(isHTML, "boundingBox." + getTestNorthBLName(), String.valueOf(TEST_NORTH)));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:boundingBox ").append(getXmlnsDDMS()).append(">\n\t");
		xml.append("<ddms:").append(getTestWestBLName()).append(">").append(TEST_WEST);
		xml.append("</ddms:").append(getTestWestBLName()).append(">\n\t");
		xml.append("<ddms:").append(getTestEastBLName()).append(">").append(TEST_EAST);
		xml.append("</ddms:").append(getTestEastBLName()).append(">\n\t");
		xml.append("<ddms:").append(getTestSouthBLName()).append(">").append(TEST_SOUTH);
		xml.append("</ddms:").append(getTestSouthBLName()).append(">\n\t");
		xml.append("<ddms:").append(getTestNorthBLName()).append(">").append(TEST_NORTH);
		xml.append("</ddms:").append(getTestNorthBLName()).append(">\n");
		xml.append("</ddms:boundingBox>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				BoundingBox.getName(version));
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
		// No tests.
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing values
			BoundingBox.Builder builder = getBaseBuilder();
			builder.setWestBL(null);
			getInstance(builder, "A ddms:boundingBox must have two latitude");

			// Longitude too small
			builder = getBaseBuilder();
			builder.setWestBL(Double.valueOf("-181"));
			getInstance(builder, "A longitude value must be between");

			// Longitude too big
			builder = getBaseBuilder();
			builder.setWestBL(Double.valueOf("181"));
			getInstance(builder, "A longitude value must be between");

			// Latitude too small
			builder = getBaseBuilder();
			builder.setSouthBL(Double.valueOf("-91"));
			getInstance(builder, "A latitude value must be between");

			// Latitude too big
			builder = getBaseBuilder();
			builder.setSouthBL(Double.valueOf("91"));
			getInstance(builder, "A latitude value must be between");

			// Issue #65
			builder = getBaseBuilder();
			builder.setNorthBL(Double.valueOf("-91"));
			getInstance(builder, "A latitude value must be between");
			builder = getBaseBuilder();
			builder.setNorthBL(Double.valueOf("91"));
			getInstance(builder, "A latitude value must be between");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			BoundingBox component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			BoundingBox elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			BoundingBox builderComponent = new BoundingBox.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Double equality
			BoundingBox component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(component.getWestBL(), Double.valueOf(TEST_WEST));
			assertEquals(component.getEastBL(), Double.valueOf(TEST_EAST));
			assertEquals(component.getSouthBL(), Double.valueOf(TEST_SOUTH));
			assertEquals(component.getNorthBL(), Double.valueOf(TEST_NORTH));
			
			// Different values in each field
			BoundingBox.Builder builder = getBaseBuilder();
			builder.setWestBL(Double.valueOf(10));
			assertFalse(elementComponent.equals(builder.commit()));
		
			builder = getBaseBuilder();
			builder.setEastBL(Double.valueOf(10));
			assertFalse(elementComponent.equals(builder.commit()));
						
			builder = getBaseBuilder();
			builder.setNorthBL(Double.valueOf(10));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setSouthBL(Double.valueOf(10));
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// Not in 5.0
		DDMSVersion.setCurrentVersion("4.1");
		BoundingBox.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("5.0");
		getInstance(builder, "The boundingBox element must not be used");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			BoundingBox elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			BoundingBox.Builder builder = new BoundingBox.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setWestBL(TEST_WEST);
			assertFalse(builder.isEmpty());
		}
	}
}
