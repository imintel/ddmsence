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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:subOrganization elements </p>
 * 
 * <p> Because a ddms:subOrganization is a local component, we cannot load a valid document from a unit test data file.
 * We have to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class SubOrganizationTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "PEO-GES";

	/**
	 * Constructor
	 */
	public SubOrganizationTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<SubOrganization> getFixtureList() {
		try {
			if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				return (null);
			List<SubOrganization> subOrgs = new ArrayList<SubOrganization>();
			subOrgs.add(new SubOrganization("sub1", SecurityAttributesTest.getFixture()));
			subOrgs.add(new SubOrganization("sub2", SecurityAttributesTest.getFixture()));
			return (subOrgs);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(SubOrganization.getName(version), TEST_VALUE);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ism"), version.getIsmNamespace());
			SecurityAttributesTest.getFixture().addTo(element);
			return (element);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param element the element to build from
	 * @return a valid object
	 */
	private SubOrganization getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SubOrganization component = null;
		try {
			component = new SubOrganization(element);
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
	private SubOrganization getInstance(SubOrganization.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SubOrganization component = null;
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
	private SubOrganization.Builder getBaseBuilder() {
		SubOrganization component = getInstance(getFixtureElement(), SUCCESS);
		return (new SubOrganization.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "subOrganization", TEST_VALUE));
		text.append(buildOutput(isHTML, "subOrganization.classification", "U"));
		text.append(buildOutput(isHTML, "subOrganization.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:subOrganization ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(
			" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append(TEST_VALUE);
		xml.append("</ddms:subOrganization>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				SubOrganization.getName(version));
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

			// Missing child text
			SubOrganization.Builder builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, "subOrganization value must exist");
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			SubOrganization component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			SubOrganization elementComponent = getInstance(getFixtureElement(), SUCCESS);
			SubOrganization builderComponent = new SubOrganization.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			SubOrganization.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		SubOrganization.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The subOrganization element must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SubOrganization elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SubOrganization.Builder builder = new SubOrganization.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
