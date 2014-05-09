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
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:person elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class PersonTest extends AbstractBaseTestCase {

	private static final String TEST_SURNAME = "Uri";
	private static final String TEST_USERID = "123";
	private static final List<String> TEST_NAMES = new ArrayList<String>();
	private static final List<String> TEST_PHONES = new ArrayList<String>();
	private static final List<String> TEST_EMAILS = new ArrayList<String>();
	private static final List<String> TEST_AFFILIATIONS = new ArrayList<String>();
	static {
		TEST_NAMES.add("Brian");
		TEST_NAMES.add("BU");
		TEST_PHONES.add("703-885-1000");
		TEST_EMAILS.add("ddms@fgm.com");
		TEST_AFFILIATIONS.add("DISA");
	}

	/**
	 * Constructor
	 */
	public PersonTest() {
		super("person.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Person getFixture() {
		try {
			return (new Person(Util.getXsListAsList("Brian"), TEST_SURNAME, null, null, null, null, null));
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
	private Person getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Person component = null;
		try {
			component = new Person(element);
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
	private Person getInstance(Person.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Person component = null;
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
	private Person.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Person component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Person.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "entityType", Person.getName(version)));
		for (String name : TEST_NAMES)
			text.append(buildOutput(isHTML, "name", name));
		for (String phone : TEST_PHONES)
			text.append(buildOutput(isHTML, "phone", phone));
		for (String email : TEST_EMAILS)
			text.append(buildOutput(isHTML, "email", email));
		text.append(buildOutput(isHTML, "surname", TEST_SURNAME));
		text.append(buildOutput(isHTML, "userID", TEST_USERID));
		for (String affiliation : TEST_AFFILIATIONS)
			text.append(buildOutput(isHTML, "affiliation", affiliation));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:").append(Person.getName(version)).append(" ").append(getXmlnsDDMS()).append(">\n");
		for (String name : TEST_NAMES)
			xml.append("\t<ddms:name>").append(name).append("</ddms:name>\n");
		xml.append("\t<ddms:surname>").append(TEST_SURNAME).append("</ddms:surname>\n");
		if (version.isAtLeast("4.0.1")) {
			for (String phone : TEST_PHONES)
				xml.append("\t<ddms:phone>").append(phone).append("</ddms:phone>\n");
			for (String email : TEST_EMAILS)
				xml.append("\t<ddms:email>").append(email).append("</ddms:email>\n");
			xml.append("\t<ddms:userID>").append(TEST_USERID).append("</ddms:userID>\n");
			for (String affiliation : TEST_AFFILIATIONS)
				xml.append("\t<ddms:affiliation>").append(affiliation).append("</ddms:affiliation>\n");
		}
		else {
			xml.append("\t<ddms:userID>").append(TEST_USERID).append("</ddms:userID>\n");
			for (String affiliation : TEST_AFFILIATIONS)
				xml.append("\t<ddms:affiliation>").append(affiliation).append("</ddms:affiliation>\n");
			for (String phone : TEST_PHONES)
				xml.append("\t<ddms:phone>").append(phone).append("</ddms:phone>\n");
			for (String email : TEST_EMAILS)
				xml.append("\t<ddms:email>").append(email).append("</ddms:email>\n");
		}
		xml.append("</ddms:").append(Person.getName(version)).append(">");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Person.getName(version));
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
			Element element = Util.buildDDMSElement(Person.getName(version), null);
			element.appendChild(Util.buildDDMSElement("surname", TEST_SURNAME));
			element.appendChild(Util.buildDDMSElement("name", TEST_NAMES.get(0)));
			Person elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields
			getInstance(new Person.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			// Missing name
			Person.Builder builder = getBaseBuilder();
			builder.getNames().clear();
			getInstance(builder, "At least 1 name element must");
			
			// Missing surname
			builder = getBaseBuilder();
			builder.setSurname(null);
			getInstance(builder, "surname must exist");
			
			// Too many affiliations
			if (!version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.getAffiliations().add("ODNI");
				getInstance(builder, "No more than 1 affiliation");
			}
			
			// Null name list
			try {
				new Person(null, null, null, null, null, null);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "surname must exist");
			}
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Person component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Person elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Person builderComponent = new Person.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field	
			Person.Builder builder = getBaseBuilder();
			builder.getNames().set(0, "Ellen");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getPhones().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getEmails().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getAffiliations().clear();
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {	
		// extensible attributes in 4.0.1
		ExtensibleAttributes attr = ExtensibleAttributesTest.getFixture();
		DDMSVersion.setCurrentVersion("4.0.1");
		Person.Builder builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attr));
		getInstance(builder, "ddms:person must not have");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Person elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Person.Builder builder = new Person.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setNames(TEST_NAMES);
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Person.Builder builder = new Person.Builder();
			assertNotNull(builder.getNames().get(1));
			assertNotNull(builder.getPhones().get(1));
			assertNotNull(builder.getEmails().get(1));
		}
	}
	
	public void testConstructorChaining() throws InvalidDDMSException {
		Person person = new Person(TEST_NAMES, TEST_SURNAME, null, null, null, null);
		Person personFull = new Person(TEST_NAMES, TEST_SURNAME, null, null, null, null, null);
		assertTrue(person.equals(personFull));
	}
}
