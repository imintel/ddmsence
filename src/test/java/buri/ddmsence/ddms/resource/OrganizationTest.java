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
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.ddms.extensible.ExtensibleAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:organization elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class OrganizationTest extends AbstractBaseTestCase {

	private static final List<String> TEST_NAMES = new ArrayList<String>();
	private static final List<String> TEST_PHONES = new ArrayList<String>();
	private static final List<String> TEST_EMAILS = new ArrayList<String>();
	static {
		TEST_NAMES.add("DISA");
		TEST_NAMES.add("PEO-GES");
		TEST_PHONES.add("703-882-1000");
		TEST_PHONES.add("703-885-1000");
		TEST_EMAILS.add("ddms@fgm.com");
	}

	/**
	 * Constructor
	 */
	public OrganizationTest() {
		super("organization.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Organization getFixture() {
		try {
			return (new Organization(Util.getXsListAsList("DISA"), null, null, null, null, null));
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
	private Organization getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Organization component = null;
		try {
			component = new Organization(element);
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
	private Organization getInstance(Organization.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Organization component = null;
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
	private Organization.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Organization component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Organization.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "entityType", Organization.getName(version)));
		for (String name : TEST_NAMES)
			text.append(buildOutput(isHTML, "name", name));
		for (String phone : TEST_PHONES)
			text.append(buildOutput(isHTML, "phone", phone));
		for (String email : TEST_EMAILS)
			text.append(buildOutput(isHTML, "email", email));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, "subOrganization", "sub1"));
			text.append(buildOutput(isHTML, "subOrganization.classification", "U"));
			text.append(buildOutput(isHTML, "subOrganization.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, "subOrganization", "sub2"));
			text.append(buildOutput(isHTML, "subOrganization.classification", "U"));
			text.append(buildOutput(isHTML, "subOrganization.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, "acronym", "DISA"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:").append(Organization.getName(version)).append(" ").append(getXmlnsDDMS());
		if (version.isAtLeast("4.0.1"))
			xml.append(" ddms:acronym=\"DISA\"");
		xml.append(">\n");
		for (String name : TEST_NAMES)
			xml.append("\t<ddms:name>").append(name).append("</ddms:name>\n");
		for (String phone : TEST_PHONES)
			xml.append("\t<ddms:phone>").append(phone).append("</ddms:phone>\n");
		for (String email : TEST_EMAILS)
			xml.append("\t<ddms:email>").append(email).append("</ddms:email>\n");
		if (version.isAtLeast("4.0.1")) {
			xml.append("\t<ddms:subOrganization ").append(getXmlnsISM());
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">sub1</ddms:subOrganization>\n");
			xml.append("\t<ddms:subOrganization ").append(getXmlnsISM());
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">sub2</ddms:subOrganization>\n");
		}
		xml.append("</ddms:").append(Organization.getName(version)).append(">");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Organization.getName(version));
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
			Element element = Util.buildDDMSElement(Organization.getName(version), null);
			element.appendChild(Util.buildDDMSElement("name", TEST_NAMES.get(0)));
			Organization elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields
			getInstance(new Organization.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// Missing name
			Organization.Builder builder = getBaseBuilder();
			builder.getNames().clear();
			getInstance(builder, "At least 1 name element must");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Organization component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Organization elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Organization builderComponent = new Organization.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field	
			Organization.Builder builder = getBaseBuilder();
			builder.getNames().set(0, "Ellen");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getPhones().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getEmails().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.getSubOrganizations().clear();
				assertFalse(elementComponent.equals(builder.commit()));
				
				builder = getBaseBuilder();
				builder.setAcronym(DIFFERENT_VALUE);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// acronym before 4.1
		DDMSVersion.setCurrentVersion("3.1");
		Organization.Builder builder = getBaseBuilder();
		builder.setAcronym("test");
		getInstance(builder, "An organization must not have an acronym");
		
		// extensible attributes in 5.0
		ExtensibleAttributes attr = ExtensibleAttributesTest.getFixture();
		DDMSVersion.setCurrentVersion("5.0");
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attr));
		getInstance(builder, "ddms:organization must not have");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Organization elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			Organization.Builder builder = new Organization.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			// List Emptiness
			if (version.isAtLeast("4.0.1")) {
				assertTrue(builder.isEmpty());
				builder.getSubOrganizations().get(0);
				assertTrue(builder.isEmpty());
				builder.getSubOrganizations().get(0).setValue("TEST");
				assertFalse(builder.isEmpty());
			}

			builder.setNames(TEST_NAMES);
			assertFalse(builder.isEmpty());

		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Organization.Builder builder = new Organization.Builder();
			assertNotNull(builder.getNames().get(1));
			assertNotNull(builder.getPhones().get(1));
			assertNotNull(builder.getEmails().get(1));
		}
	}
		
	public void testIndexLevelsObjectLists() throws InvalidDDMSException {
		List<String> names = Util.getXsListAsList("DISA");
		Organization org = new Organization(names, null, null, SubOrganizationTest.getFixtureList(), null, null);

		PropertyReader.setProperty("output.indexLevel", "0");
		assertEquals("entityType: organization\nname: DISA\n"
			+ "subOrganization: sub1\nsubOrganization.classification: U\nsubOrganization.ownerProducer: USA\n"
			+ "subOrganization: sub2\nsubOrganization.classification: U\nsubOrganization.ownerProducer: USA\n",
			org.toText());

		PropertyReader.setProperty("output.indexLevel", "1");
		assertEquals(
			"entityType: organization\nname: DISA\n"
				+ "subOrganization[1]: sub1\nsubOrganization[1].classification: U\nsubOrganization[1].ownerProducer: USA\n"
				+ "subOrganization[2]: sub2\nsubOrganization[2].classification: U\nsubOrganization[2].ownerProducer: USA\n",
			org.toText());

		PropertyReader.setProperty("output.indexLevel", "2");
		assertEquals(
			"entityType: organization\nname[1]: DISA\n"
				+ "subOrganization[1]: sub1\nsubOrganization[1].classification: U\nsubOrganization[1].ownerProducer: USA\n"
				+ "subOrganization[2]: sub2\nsubOrganization[2].classification: U\nsubOrganization[2].ownerProducer: USA\n",
			org.toText());
	}
	
	public void testConstructorChaining() throws InvalidDDMSException {
		Organization org = new Organization(TEST_NAMES, null, null, null, null);
		Organization orgFull = new Organization(TEST_NAMES, null, null, null, null, null);
		assertTrue(org.equals(orgFull));
	}
}
