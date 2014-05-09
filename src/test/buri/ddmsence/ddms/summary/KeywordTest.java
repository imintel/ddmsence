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

import nu.xom.Attribute;
import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.ddms.extensible.ExtensibleAttributesTest;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:keyword elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class KeywordTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "Tornado";

	/**
	 * Constructor
	 */
	public KeywordTest() {
		super("keyword.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<Keyword> getFixtureList() {
		try {
			List<Keyword> keywords = new ArrayList<Keyword>();
			keywords.add(new Keyword("DDMSence", null));
			keywords.add(new Keyword("Uri", null));
			return (keywords);
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
	private Keyword getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Keyword component = null;
		try {
			component = new Keyword(element);
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
	private Keyword getInstance(Keyword.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Keyword component = null;
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
	private Keyword.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Keyword component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Keyword.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "keyword", TEST_VALUE));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, "keyword.classification", "U"));
			text.append(buildOutput(isHTML, "keyword.ownerProducer", "USA"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:keyword ").append(getXmlnsDDMS()).append(" ");
		if (version.isAtLeast("4.0.1"))
			xml.append(getXmlnsISM()).append(" ");
		xml.append("ddms:value=\"").append(TEST_VALUE).append("\"");
		if (version.isAtLeast("4.0.1"))
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		xml.append(" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Keyword.getName(version));
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
		// No tests.
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Missing value
			Element element = Util.buildDDMSElement(Keyword.getName(version), null);
			getInstance(element, "value attribute must exist.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Keyword component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Keyword elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Keyword builderComponent = new Keyword.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			Keyword.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 3.1
		Keyword.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("3.1");
		getInstance(builder, "Security attributes must not be applied");
		
		// No attributes in 2.0
		DDMSVersion.setCurrentVersion("2.0");
		ExtensibleAttributes attributes = ExtensibleAttributesTest.getFixture();
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "xs:anyAttribute must not be applied");
		
		DDMSVersion version = DDMSVersion.setCurrentVersion("3.0");
		Attribute attr = new Attribute("ddms:value", version.getNamespace(), "dog");

		// Using ddms:value as the extension (data)
		List<Attribute> extAttributes = new ArrayList<Attribute>();
		extAttributes.add(attr);
		attributes = new ExtensibleAttributes(extAttributes);
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "The extensible attribute with the name, ddms:value");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Keyword elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Keyword.Builder builder = new Keyword.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
