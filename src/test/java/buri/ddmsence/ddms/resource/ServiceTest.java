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
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:service elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class ServiceTest extends AbstractBaseTestCase {

	private static final List<String> TEST_NAMES = new ArrayList<String>();
	private static final List<String> TEST_PHONES = new ArrayList<String>();
	private static final List<String> TEST_EMAILS = new ArrayList<String>();
	private static final List<String> TEST_AFFILIATIONS = new ArrayList<String>();
	static {
		TEST_NAMES.add("https://metadata.dod.mil/ebxmlquery/soap");
		TEST_PHONES.add("703-882-1000");
		TEST_EMAILS.add("ddms@fgm.com");
		TEST_AFFILIATIONS.add("DISA");
	}

	/**
	 * Constructor
	 */
	public ServiceTest() {
		super("service.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Service getFixture() {
		try {
			return (new Service(Util.getXsListAsList("https://metadata.dod.mil/ebxmlquery/soap"), null, null));
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
	private Service getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Service component = null;
		try {
			component = new Service(element);
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
	private Service getInstance(Service.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Service component = null;
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
	private Service.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Service component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Service.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "entityType", Service.getName(version)));
		for (String name : TEST_NAMES)
			text.append(buildOutput(isHTML, "name", name));
		for (String phone : TEST_PHONES)
			text.append(buildOutput(isHTML, "phone", phone));
		for (String email : TEST_EMAILS)
			text.append(buildOutput(isHTML, "email", email));
		if (version.isAtLeast("5.0")) {
			for (String affiliation : TEST_AFFILIATIONS)
				text.append(buildOutput(isHTML, "affiliation", affiliation));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:").append(Service.getName(version)).append(" ").append(getXmlnsDDMS()).append(">\n");
		for (String name : TEST_NAMES)
			xml.append("\t<ddms:name>").append(name).append("</ddms:name>\n");
		for (String phone : TEST_PHONES)
			xml.append("\t<ddms:phone>").append(phone).append("</ddms:phone>\n");
		for (String email : TEST_EMAILS)
			xml.append("\t<ddms:email>").append(email).append("</ddms:email>\n");
		if (version.isAtLeast("5.0")) {
			for (String affiliation : TEST_AFFILIATIONS)
				xml.append("\t<ddms:affiliation>").append(affiliation).append("</ddms:affiliation>\n");
		}
		xml.append("</ddms:").append(Service.getName(version)).append(">");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Service.getName(version));
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
			Element element = Util.buildDDMSElement(Service.getName(version), null);
			element.appendChild(Util.buildDDMSElement("name", TEST_NAMES.get(0)));
			Service elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields
			getInstance(new Service.Builder(elementComponent), SUCCESS);
			
			// Null affiliation list
			new Service(TEST_NAMES, null, null, null, null);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// Missing name
			Service.Builder builder = getBaseBuilder();
			builder.getNames().clear();
			getInstance(builder, "At least 1 name element must");
		}
	}
	
	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Service component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Service elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Service builderComponent = new Service.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field	
			Service.Builder builder = getBaseBuilder();
			builder.getNames().set(0, "Ellen");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getPhones().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getEmails().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getPhones().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.getAffiliations().add("DISA");
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Service elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Service.Builder builder = new Service.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setNames(TEST_NAMES);
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Service.Builder builder = new Service.Builder();
			assertNotNull(builder.getNames().get(1));
			assertNotNull(builder.getPhones().get(1));
			assertNotNull(builder.getEmails().get(1));
		}
	}
	
	public void testConstructorChaining() throws InvalidDDMSException {
		Service person = new Service(TEST_NAMES, null, null);
		Service personFull = new Service(TEST_NAMES, null, null, null, null);
		assertTrue(person.equals(personFull));
	}
}
