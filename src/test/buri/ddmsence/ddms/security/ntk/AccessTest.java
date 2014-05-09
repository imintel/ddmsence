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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ntk:Access elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class AccessTest extends AbstractBaseTestCase {

	private static final Boolean TEST_EXTERNAL = Boolean.TRUE;

	/**
	 * Constructor
	 */
	public AccessTest() {
		super("access.xml");
		removeSupportedVersions("2.0 3.0 3.1 5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Access getFixture() {
		try {
			if ("4.1".equals(DDMSVersion.getCurrentVersion().getVersion()))
				return (new Access(IndividualTest.getFixtureList(), null, null, SecurityAttributesTest.getFixture()));
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
	private Access getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Access component = null;
		try {
			component = new Access(element);
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
	private Access getInstance(Access.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Access component = null;
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
	private Access.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Access component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Access.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(IndividualTest.getFixture().getOutput(isHTML, "access.individualList.", ""));
		text.append(GroupTest.getFixture().getOutput(isHTML, "access.groupList.", ""));
		text.append(ProfileListTest.getFixture().getOutput(isHTML, "access.", ""));
		if (version.isAtLeast("4.1")) {
			text.append(buildOutput(isHTML, "access.externalReference", String.valueOf(TEST_EXTERNAL)));
		}
		text.append(buildOutput(isHTML, "access.classification", "U"));
		text.append(buildOutput(isHTML, "access.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ntk:Access ").append(getXmlnsNTK()).append(" ").append(getXmlnsISM()).append(" ");
		if (version.isAtLeast("4.1")) {
			xml.append("ntk:externalReference=\"true\" ");
		}
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
		xml.append("\t<ntk:AccessIndividualList>\n");
		xml.append("\t\t<ntk:AccessIndividual ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
		xml.append("\t\t\t<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>\n");
		xml.append("\t\t\t<ntk:AccessIndividualValue ism:classification=\"U\" ism:ownerProducer=\"USA\">user_2321889:Doe_John_H</ntk:AccessIndividualValue>\n");
		xml.append("\t\t</ntk:AccessIndividual>\n");
		xml.append("\t</ntk:AccessIndividualList>\n");
		xml.append("\t<ntk:AccessGroupList>\n");
		xml.append("\t\t<ntk:AccessGroup ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
		xml.append("\t\t\t<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>\n");
		xml.append("\t\t\t<ntk:AccessGroupValue ism:classification=\"U\" ism:ownerProducer=\"USA\">WISE/RODCA</ntk:AccessGroupValue>\n");
		xml.append("\t\t</ntk:AccessGroup>\n");
		xml.append("\t</ntk:AccessGroupList>\n");
		xml.append("\t<ntk:AccessProfileList ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
		xml.append("\t\t<ntk:AccessProfile ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
		xml.append("\t\t\t<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>\n");
		xml.append("\t\t\t<ntk:AccessProfileValue ism:classification=\"U\" ism:ownerProducer=\"USA\" ntk:vocabulary=\"vocabulary\">profile</ntk:AccessProfileValue>\n");
		xml.append("\t\t</ntk:AccessProfile>\n");
		xml.append("\t</ntk:AccessProfileList>\n");
		xml.append("</ntk:Access>");

		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_NTK_PREFIX,
				Access.getName(version));
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
			Element element = Util.buildElement(ntkPrefix, Access.getName(version), version.getNtkNamespace(), null);
			SecurityAttributesTest.getFixture().addTo(element);
			Access elementComponent = getInstance(element, SUCCESS);
			
			// No optional fields
			getInstance(new Access.Builder(elementComponent), SUCCESS);
						
			// Null list params
			try {
				new Access(null, null, null, SecurityAttributesTest.getFixture());
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing security attributes
			Access.Builder builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			Access component = getInstance(getValidElement(sVersion), SUCCESS);

			if (!version.isAtLeast("4.1")) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());				
			}
			else {
				// 4.1 ntk:externalReference used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ntk:externalReference attribute in this DDMS component";
				String locator = "ntk:Access";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}

			// Completely Empty
			Access.Builder builder = new Access.Builder();
			builder.getSecurityAttributes().setClassification("U");
			builder.getSecurityAttributes().setOwnerProducers(Util.getXsListAsList("USA"));
			component = builder.commit();
			assertEquals(1, component.getValidationWarnings().size());
			String text = "An ntk:Access element was found with no";
			String locator = "ntk:Access";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Access elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Access builderComponent = new Access.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Access.Builder builder = getBaseBuilder();
			builder.getIndividuals().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getGroups().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setProfileList(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.setExternalReference(Boolean.FALSE);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}
	
	public void testVersionSpecific() {
		// Pre-4.0.1 test is implicit, since NTK namespace did not exist.
		// Post-4.1 test is handled in MetacardInfoTest.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Access elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Access.Builder builder = new Access.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.getIndividuals().get(0);
			assertTrue(builder.isEmpty());
			
			builder.getGroups().get(1).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Access.Builder builder = new Access.Builder();
			assertNotNull(builder.getIndividuals().get(1));
			assertNotNull(builder.getGroups().get(1));
		}
	}
	
	public void testDeprecatedConstructor() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Access component = new Access(IndividualTest.getFixtureList(), null, null,
				SecurityAttributesTest.getFixture());
			assertNull(component.isExternalReference());
		}
	}
}