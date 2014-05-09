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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:nonStateActor elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class NonStateActorTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "Laotian Monks";
	private static final Integer TEST_ORDER = Integer.valueOf(1);
	private static final String TEST_QUALIFIER = "urn:sample";

	/**
	 * Constructor
	 */
	public NonStateActorTest() {
		super("nonStateActor.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param a fixed order value
	 */
	public static NonStateActor getFixture(int order) {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			return (version.isAtLeast("4.0.1") ? new NonStateActor(TEST_VALUE, Integer.valueOf(order),
				getTestQualifier(), SecurityAttributesTest.getFixture()) : null);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<NonStateActor> getFixtureList() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			List<NonStateActor> actors = new ArrayList<NonStateActor>();
			if (version.isAtLeast("4.0.1"))
				actors.add(new NonStateActor(TEST_VALUE, TEST_ORDER, getTestQualifier(),
					SecurityAttributesTest.getFixture()));
			return (actors);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a dummy value for the qualifier, based upon the current DDMS version.
	 */
	private static String getTestQualifier() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.1") ? TEST_QUALIFIER : null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private NonStateActor getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NonStateActor component = null;
		try {
			component = new NonStateActor(element);
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
	private NonStateActor getInstance(NonStateActor.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NonStateActor component = null;
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
	private NonStateActor.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		NonStateActor component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new NonStateActor.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "nonStateActor.value", TEST_VALUE));
		text.append(buildOutput(isHTML, "nonStateActor.order", String.valueOf(TEST_ORDER)));
		if (version.isAtLeast("4.1"))
			text.append(buildOutput(isHTML, "nonStateActor.qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "nonStateActor.classification", "U"));
		text.append(buildOutput(isHTML, "nonStateActor.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:nonStateActor ").append(getXmlnsDDMS()).append(" ");
		xml.append(getXmlnsISM()).append(" ism:classification=\"U\" ism:ownerProducer=\"USA\" ");
		xml.append("ddms:order=\"").append(TEST_ORDER).append("\"");
		if (version.isAtLeast("4.1"))
			xml.append(" ddms:qualifier=\"").append(TEST_QUALIFIER).append("\"");
		xml.append(">").append(TEST_VALUE).append("</ddms:nonStateActor>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				NonStateActor.getName(version));
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
			Element element = Util.buildDDMSElement(NonStateActor.getName(version), null);
			SecurityAttributesTest.getFixture().addTo(element);
			getInstance(element, SUCCESS);

			// Data-based, No optional fields
			NonStateActor.Builder builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Bad qualifier
			if (version.isAtLeast("4.1")) {
				NonStateActor.Builder builder = getBaseBuilder();
				builder.setQualifier(INVALID_URI);
				getInstance(builder, "Invalid URI");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			boolean is41 = "4.1".equals(sVersion);
			NonStateActor component = getInstance(getValidElement(sVersion), SUCCESS);
			

			if (!is41) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ddms:qualifier attribute in this DDMS component";
				String locator = "ddms:nonStateActor";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}

			int countIndex = is41 ? 1 : 0;
			
			// Completely empty
			NonStateActor.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(countIndex + 1, component.getValidationWarnings().size());
			String text = "A ddms:nonStateActor element was found with no";
			String locator = "ddms:nonStateActor";		
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			NonStateActor elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			NonStateActor builderComponent = new NonStateActor.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			NonStateActor.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setOrder(null);
			assertFalse(elementComponent.equals(builder.commit()));

			if (version.isAtLeast("4.1")) {
				builder = getBaseBuilder();
				builder.setQualifier(DIFFERENT_VALUE);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		NonStateActor.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The nonStateActor element must not be used");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NonStateActor elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NonStateActor.Builder builder = new NonStateActor.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setOrder(TEST_ORDER);
			assertFalse(builder.isEmpty());
		}
	}

	public void testDeprecatedConstructor() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NonStateActor dataComponent = new NonStateActor(TEST_VALUE, TEST_ORDER, SecurityAttributesTest.getFixture());
			assertTrue(Util.isEmpty(dataComponent.getQualifier()));
		}
	}
}
