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
 * <p> Tests related to ddms:category elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class CategoryTest extends AbstractBaseTestCase {

	private static final String TEST_QUALIFIER = "http://metadata.dod.mil/mdr/artifiact/MET/severeWeatherCode_enum/xml";
	private static final String TEST_CODE = "T";
	private static final String TEST_LABEL = "TORNADO";

	/**
	 * Constructor
	 */
	public CategoryTest() {
		super("category.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<Category> getFixtureList() {
		try {
			List<Category> categories = new ArrayList<Category>();
			categories.add(new Category("urn:buri:ddmsence:categories", "DDMS", "DDMS", null));
			return (categories);
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
	private Category getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Category component = null;
		try {
			component = new Category(element);
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
	private Category getInstance(Category.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Category component = null;
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
	private Category.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Category component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Category.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "category.qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, "category.code", TEST_CODE));
		text.append(buildOutput(isHTML, "category.label", TEST_LABEL));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, "category.classification", "U"));
			text.append(buildOutput(isHTML, "category.ownerProducer", "USA"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:category ").append(getXmlnsDDMS()).append(" ");
		if (version.isAtLeast("4.0.1")) {
			xml.append(getXmlnsISM()).append(" ");
		}
		xml.append("ddms:qualifier=\"").append(TEST_QUALIFIER).append("\" ");
		xml.append("ddms:code=\"").append(TEST_CODE).append("\" ");
		xml.append("ddms:label=\"").append(TEST_LABEL).append("\"");
		if (version.isAtLeast("4.0.1")) {
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		}
		xml.append(" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Category.getName(version));
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
			Element element = Util.buildDDMSElement(Category.getName(version), null);
			Util.addDDMSAttribute(element, "label", TEST_LABEL);
			Category elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			getInstance(new Category.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing label
			Category.Builder builder = getBaseBuilder();
			builder.setLabel(null);
			getInstance(builder, "label attribute must exist.");	

			// Qualifier not URI
			builder = getBaseBuilder();
			builder.setQualifier(INVALID_URI);
			getInstance(builder, "Invalid URI");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Category component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}
	
	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Category elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Category builderComponent = new Category.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			Category.Builder builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));	

			builder = getBaseBuilder();
			builder.setCode(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setLabel(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 3.1
		Category.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("3.1");
		getInstance(builder, "Security attributes must not be applied");
		
		// No attributes in 2.0
		DDMSVersion.setCurrentVersion("2.0");
		ExtensibleAttributes attributes = ExtensibleAttributesTest.getFixture();
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "xs:anyAttribute must not be applied");

		DDMSVersion version = DDMSVersion.setCurrentVersion("3.0");

		// Using ddms:qualifier as the extension (data)
		List<Attribute> extAttributes = new ArrayList<Attribute>();
		extAttributes.add(new Attribute("ddms:qualifier", version.getNamespace(), "dog"));
		attributes = new ExtensibleAttributes(extAttributes);
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "The extensible attribute with the name, ddms:qualifier");
		
		// Using ddms:code as the extension (data)
		extAttributes = new ArrayList<Attribute>();
		extAttributes.add(new Attribute("ddms:code", version.getNamespace(), "dog"));
		attributes = new ExtensibleAttributes(extAttributes);
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "The extensible attribute with the name, ddms:code");

		// Using ddms:label as the extension (data)
		extAttributes = new ArrayList<Attribute>();
		extAttributes.add(new Attribute("ddms:label", version.getNamespace(), "dog"));
		attributes = new ExtensibleAttributes(extAttributes);
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, "The extensible attribute with the name, ddms:label");
		
		// Using icism:classification as the extension (data)
		extAttributes = new ArrayList<Attribute>();
		extAttributes.add(new Attribute("ism:classification", version.getIsmNamespace(), "U"));
		attributes = new ExtensibleAttributes(extAttributes);
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
		getInstance(builder, SUCCESS);		
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Category elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Category.Builder builder = new Category.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setLabel(TEST_LABEL);
			assertFalse(builder.isEmpty());
		}
	}
}
