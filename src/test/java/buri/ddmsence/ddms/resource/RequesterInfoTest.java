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
 * <p> Tests related to ddms:requesterInfo elements </p>
 * 
 * <p> Because a ddms:requesterInfo is a local component, we cannot load a valid document from a unit test data file. We
 * have to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class RequesterInfoTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public RequesterInfoTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param useOrg true to put an organization in, false for a person
	 */
	public static Element getFixtureElement(boolean useOrg) {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(RequesterInfo.getName(version), null);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			element.appendChild(useOrg ? OrganizationTest.getFixture().getXOMElementCopy()
				: PersonTest.getFixture().getXOMElementCopy());
			SecurityAttributesTest.getFixture().addTo(element);
			return (element);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<RequesterInfo> getFixtureList() {
		try {
			List<RequesterInfo> list = new ArrayList<RequesterInfo>();
			list.add(new RequesterInfo(RequesterInfoTest.getFixtureElement(true)));
			return (list);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private RequesterInfo getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RequesterInfo component = null;
		try {
			component = new RequesterInfo(element);
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
	private RequesterInfo getInstance(RequesterInfo.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RequesterInfo component = null;
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
	 * 
	 * @param useOrg true to put an organization in, false for a person
	 */
	private RequesterInfo.Builder getBaseBuilder(boolean useOrg) {
		RequesterInfo component = getInstance(getFixtureElement(useOrg), SUCCESS);
		return (new RequesterInfo.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "requesterInfo.entityType", "organization"));
		text.append(buildOutput(isHTML, "requesterInfo.name", "DISA"));
		text.append(buildOutput(isHTML, "requesterInfo.classification", "U"));
		text.append(buildOutput(isHTML, "requesterInfo.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:requesterInfo ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization>");
		xml.append("</ddms:requesterInfo>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(true), SUCCESS), DEFAULT_DDMS_PREFIX,
				RequesterInfo.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, organization
			getInstance(getFixtureElement(true), SUCCESS);

			// Element-based, person
			getInstance(getFixtureElement(false), SUCCESS);

			// Data-based via Builder, organization
			getBaseBuilder(true);

			// Data-based via Builder, person
			getBaseBuilder(false);
		}
	}

	public void testConstructorsMinimal() {
		// No tests.
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing entity
			RequesterInfo.Builder builder = getBaseBuilder(true);
			builder.setEntityType(null);
			builder.setOrganization(null);
			getInstance(builder, "entity must exist.");

			// Missing security attributes
			builder = getBaseBuilder(true);
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");

			// Wrong entity
			try {
				new RequesterInfo(ServiceTest.getFixture(), SecurityAttributesTest.getFixture());
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "The entity must be a ");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			RequesterInfo component = getInstance(getFixtureElement(true), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			RequesterInfo elementComponent = getInstance(getFixtureElement(true), SUCCESS);
			RequesterInfo builderComponent = new RequesterInfo.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			RequesterInfo.Builder builder = getBaseBuilder(false);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(getFixtureElement(true), "The requesterInfo element must not ");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RequesterInfo elementComponent = getInstance(getFixtureElement(true), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RequesterInfo.Builder builder = new RequesterInfo.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.getPerson().setNames(Util.getXsListAsList("Name"));
			assertFalse(builder.isEmpty());
		}
	}
}
