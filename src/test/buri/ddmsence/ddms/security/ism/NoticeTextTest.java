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
package buri.ddmsence.ddms.security.ism;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ism:NoticeText elements </p>
 * 
 * <p> The valid instance of ism:NoticeText is generated, rather than relying on the ISM schemas to validate an XML
 * file. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class NoticeTextTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "noticeText";
	private static final List<String> TEST_POC_TYPES = Util.getXsListAsList("DoD-Dist-B");

	/**
	 * Constructor
	 */
	public NoticeTextTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String ismPrefix = PropertyReader.getPrefix("ism");
			String ismNs = version.getIsmNamespace();

			Element element = Util.buildElement(ismPrefix, NoticeText.getName(version), ismNs, TEST_VALUE);
			element.addNamespaceDeclaration(ismPrefix, version.getIsmNamespace());
			SecurityAttributesTest.getFixture().addTo(element);
			Util.addAttribute(element, ismPrefix, "pocType", ismNs, "DoD-Dist-B");
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
	public static List<NoticeText> getFixtureList() {
		try {
			List<NoticeText> list = new ArrayList<NoticeText>();
			list.add(new NoticeText(getFixtureElement()));
			return (list);
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
	private NoticeText getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeText component = null;
		try {
			component = new NoticeText(element);
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
	private NoticeText getInstance(NoticeText.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeText component = null;
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
	private NoticeText.Builder getBaseBuilder() {
		NoticeText component = getInstance(getFixtureElement(), SUCCESS);
		return (new NoticeText.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "noticeText", TEST_VALUE));
		text.append(buildOutput(isHTML, "noticeText.pocType", Util.getXsList(TEST_POC_TYPES)));
		text.append(buildOutput(isHTML, "noticeText.classification", "U"));
		text.append(buildOutput(isHTML, "noticeText.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ism:NoticeText ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\" ism:pocType=\"DoD-Dist-B\"");
		xml.append(">").append(TEST_VALUE).append("</ism:NoticeText>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_ISM_PREFIX,
				NoticeText.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
			
			// Null poc list
			new NoticeText(TEST_VALUE, null, SecurityAttributesTest.getFixture());
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String ismPrefix = PropertyReader.getPrefix("ism");
			
			// Element-based, No optional fields
			Element element = Util.buildElement(ismPrefix, NoticeText.getName(version), version.getIsmNamespace(), null);
			SecurityAttributesTest.getFixture().addTo(element);
			getInstance(element, SUCCESS);

			// Data-based, No optional fields
			NoticeText.Builder builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, SUCCESS);
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Invalid pocType
			NoticeText.Builder builder = getBaseBuilder();
			builder.setPocTypes(Util.getXsListAsList("Unknown"));
			getInstance(builder, "Unknown is not a valid enumeration token");

			// Partial Invalid pocType
			builder = getBaseBuilder();
			builder.setPocTypes(Util.getXsListAsList("DoD-Dist-B Unknown"));
			getInstance(builder, "Unknown is not a valid enumeration token");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			NoticeText component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Completely empty
			NoticeText.Builder builder = getBaseBuilder();
			builder.setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "An ism:NoticeText element was found with no";
			String locator = "ism:NoticeText";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			NoticeText elementComponent = getInstance(getFixtureElement(), SUCCESS);
			NoticeText builderComponent = new NoticeText.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			NoticeText.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setPocTypes(Util.getXsListAsList("DoD-Dist-C"));
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		NoticeText.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The NoticeText element must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeText elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeText.Builder builder = new NoticeText.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			NoticeText.Builder builder = new NoticeText.Builder();
			assertNotNull(builder.getPocTypes().get(1));
		}
	}
}
