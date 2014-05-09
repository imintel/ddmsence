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
package buri.ddmsence.ddms.security.ntk;

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
 * <p> Tests related to ntk:AccessProfileValue elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ProfileValueTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "profile";
	private static final String TEST_VOCABULARY = "vocabulary";
	private static final String TEST_ID = "ID";
	private static final String TEST_ID_REFERENCE = "ID";
	private static final String TEST_QUALIFIER = "qualifier";

	/**
	 * Constructor
	 */
	public ProfileValueTest() {
		super("accessProfileValue.xml");
		removeSupportedVersions("2.0 3.0 3.1 5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param value the value for this component
	 */
	public static ProfileValue getFixture(String value) {
		try {
			return (new ProfileValue(value, TEST_VOCABULARY, null, null, null, SecurityAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<ProfileValue> getFixtureList() {
		List<ProfileValue> list = new ArrayList<ProfileValue>();
		list.add(ProfileValueTest.getFixture("profile"));
		return (list);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private ProfileValue getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProfileValue component = null;
		try {
			component = new ProfileValue(element);
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
	private ProfileValue getInstance(ProfileValue.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProfileValue component = null;
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
	private ProfileValue.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		ProfileValue component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new ProfileValue.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "profileValue", TEST_VALUE));
		text.append(buildOutput(isHTML, "profileValue.vocabulary", TEST_VOCABULARY));
		text.append(buildOutput(isHTML, "profileValue.id", TEST_ID));
		text.append(buildOutput(isHTML, "profileValue.idReference", TEST_ID_REFERENCE));
		text.append(buildOutput(isHTML, "profileValue.qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "profileValue.classification", "U"));
		text.append(buildOutput(isHTML, "profileValue.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ntk:AccessProfileValue ").append(getXmlnsNTK()).append(" ").append(getXmlnsISM());
		xml.append(" ntk:id=\"ID\" ntk:IDReference=\"ID\" ntk:qualifier=\"qualifier\"");
		xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\" ntk:vocabulary=\"vocabulary\">");
		xml.append(TEST_VALUE).append("</ntk:AccessProfileValue>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_NTK_PREFIX,
				ProfileValue.getName(version));
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
			String ntkPrefix = PropertyReader.getPrefix("ntk");

			// No optional fields
			Element element = Util.buildElement(ntkPrefix, ProfileValue.getName(version), version.getNtkNamespace(),
				TEST_VALUE);
			Util.addAttribute(element, ntkPrefix, "vocabulary", version.getNtkNamespace(), TEST_VOCABULARY);
			SecurityAttributesTest.getFixture().addTo(element);
			ProfileValue elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new ProfileValue.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// Missing vocabulary
			ProfileValue.Builder builder = getBaseBuilder();
			builder.setVocabulary(null);
			getInstance(builder, "\"\" is not a valid NMTOKEN.");
			
			// Missing security attributes
			builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ProfileValue component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// No value
			ProfileValue.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = builder.commit();			
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A ntk:AccessProfileValue element was found with no value.";
			String locator = "ntk:AccessProfileValue";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ProfileValue elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			ProfileValue builderComponent = new ProfileValue.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			ProfileValue.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setVocabulary(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setID(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setIDReference(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		// Pre-4.0.1 test is implicit, since NTK namespace did not exist.
		// Post-4.1 test is handled in MetacardInfoTest.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProfileValue elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProfileValue.Builder builder = new ProfileValue.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
