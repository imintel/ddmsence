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
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:recordKeeper elements </p>
 * 
 * <p> Because a ddms:recordKeeper is a local component, we cannot load a valid document from a unit test data file. We
 * have to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class RecordKeeperTest extends AbstractBaseTestCase {

	private static final String TEST_ID = "#289-99202.9";
	private static final String TEST_NAME = "DISA";

	/**
	 * Constructor
	 */
	public RecordKeeperTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = Util.buildDDMSElement(RecordKeeper.getName(version), null);
		element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
		Element idElement = Util.buildDDMSElement("recordKeeperID", TEST_ID);
		element.appendChild(idElement);
		element.appendChild(OrganizationTest.getFixture().getXOMElementCopy());
		return (element);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static RecordKeeper getFixture() {
		try {
			return (new RecordKeeper(getFixtureElement()));
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
	private RecordKeeper getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RecordKeeper component = null;
		try {
			component = new RecordKeeper(element);
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
	private RecordKeeper getInstance(RecordKeeper.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RecordKeeper component = null;
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
	private RecordKeeper.Builder getBaseBuilder() {
		RecordKeeper component = getInstance(getFixtureElement(), SUCCESS);
		return (new RecordKeeper.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "recordKeeper.recordKeeperID", TEST_ID));
		text.append(buildOutput(isHTML, "recordKeeper.entityType", "organization"));
		text.append(buildOutput(isHTML, "recordKeeper.name", TEST_NAME));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:recordKeeper ").append(getXmlnsDDMS()).append(">");
		xml.append("<ddms:recordKeeperID>").append(TEST_ID).append("</ddms:recordKeeperID>");
		xml.append("<ddms:organization>");
		xml.append("<ddms:name>").append(TEST_NAME).append("</ddms:name>");
		xml.append("</ddms:organization>");
		xml.append("</ddms:recordKeeper>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				RecordKeeper.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}

	public void testConstructorsMinimal() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing recordKeeperID
			RecordKeeper.Builder builder = getBaseBuilder();
			builder.setRecordKeeperID(null);			
			getInstance(builder, "record keeper ID must exist.");
			
			// Missing organization
			builder = getBaseBuilder();
			builder.setOrganization(null);			
			getInstance(builder, "organization must exist.");
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			RecordKeeper component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			RecordKeeper elementComponent = getInstance(getFixtureElement(), SUCCESS);
			RecordKeeper builderComponent = new RecordKeeper.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			RecordKeeper.Builder builder = getBaseBuilder();
			builder.setRecordKeeperID(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getOrganization().setAcronym("DARPA");
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(getFixtureElement(), "The recordKeeper element must not ");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RecordKeeper elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RecordKeeper.Builder builder = new RecordKeeper.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setRecordKeeperID(TEST_ID);
			assertFalse(builder.isEmpty());
		}
	}
}
