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
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:contributor elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ContributorTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public ContributorTest() {
		super("contributor.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Contributor getFixture() {
		try {
			return (new Contributor(ServiceTest.getFixture(), null, null));
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
	private Contributor getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Contributor component = null;
		try {
			SecurityAttributesTest.getFixture().addTo(element);
			component = new Contributor(element);
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
	private Contributor getInstance(Contributor.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Contributor component = null;
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
	private Contributor.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Contributor component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Contributor.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(OrganizationTest.getFixture().getOutput(isHTML, "contributor.", ""));
		if (version.isAtLeast("4.0.1"))
			text.append(buildOutput(isHTML, "contributor.pocType", "DoD-Dist-B"));
		text.append(buildOutput(isHTML, "contributor.classification", "U"));
		text.append(buildOutput(isHTML, "contributor.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:contributor ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM());
		if (version.isAtLeast("4.0.1"))
			xml.append(" ism:pocType=\"DoD-Dist-B\"");
		xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">\n\t<ddms:");
		xml.append(Organization.getName(version)).append(">\n");
		xml.append("\t\t<ddms:name>DISA</ddms:name>\n");
		xml.append("\t</ddms:").append(Organization.getName(version)).append(">\n</ddms:contributor>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Contributor.getName(version));
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
			Element element = Util.buildDDMSElement(Contributor.getName(version), null);
			element.appendChild(OrganizationTest.getFixture().getXOMElementCopy());
			Contributor elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields
			getInstance(new Contributor.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Missing entity
			Contributor.Builder builder = getBaseBuilder();
			builder.setEntityType(null);
			builder.setOrganization(null);
			getInstance(builder, "entity must exist.");

			if (version.isAtLeast("4.0.1")) {
				// Invalid pocType
				builder = getBaseBuilder();
				builder.setPocTypes(Util.getXsListAsList("Unknown"));
				getInstance(builder, "Unknown is not a valid enumeration token");

				// Partial Invalid pocType
				builder = getBaseBuilder();
				builder.setPocTypes(Util.getXsListAsList("DoD-Dist-B Unknown"));
				getInstance(builder, "Unknown is not a valid enumeration token");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Contributor component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Contributor elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Contributor builderComponent = new Contributor.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field	
			Contributor.Builder builder = getBaseBuilder();
			builder.setEntityType(Person.getName(version));
			builder.setPerson(new Person.Builder(PersonTest.getFixture()));
			builder.setOrganization(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.setPocTypes(Util.getXsListAsList("DoD-Dist-C"));
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// pocType before 4.0.1
		DDMSVersion.setCurrentVersion("3.1");
		Contributor.Builder builder = getBaseBuilder();
		builder.setPocTypes(Util.getXsListAsList("DoD-Dist-B"));
		getInstance(builder, "This component must not have a pocType");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Contributor elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Contributor.Builder builder = new Contributor.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setPocTypes(Util.getXsListAsList("DoD-Dist-B"));
			assertFalse(builder.isEmpty());
		}
	}
}
