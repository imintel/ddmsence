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
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:rights elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class RightsTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public RightsTest() {
		super("rights.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Rights getFixture() {
		try {
			return (new Rights(true, true, true));
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
	private Rights getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Rights component = null;
		try {
			component = new Rights(element);
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
	private Rights getInstance(Rights.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Rights component = null;
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
	private Rights.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Rights component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Rights.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "rights.privacyAct", "true"));
		text.append(buildOutput(isHTML, "rights.intellectualProperty", "true"));
		text.append(buildOutput(isHTML, "rights.copyright", "false"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:rights ").append(getXmlnsDDMS());
		xml.append(" ddms:privacyAct=\"true\" ddms:intellectualProperty=\"true\" ddms:copyright=\"false\" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Rights.getName(version));
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
			Element element = Util.buildDDMSElement(Rights.getName(version), null);
			Rights elementComponent = getInstance(element, SUCCESS);
						
			// Data-based, No optional fields
			getInstance(new Rights.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testValidationErrors() {
		// No tests.		
	}
	
	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Rights component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Rights elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Rights builderComponent = new Rights.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Language wrongComponent = new Language("qualifier", "value");
			assertFalse(elementComponent.equals(wrongComponent));
						
			// Different values in each field
			Rights.Builder builder = getBaseBuilder();
			builder.setCopyright(true);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setPrivacyAct(false);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setIntellectualProperty(false);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Rights elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Rights.Builder builder = new Rights.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setCopyright(Boolean.TRUE);
			assertFalse(builder.isEmpty());
		}
	}

	public void testDefaultValues() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			Element element = Util.buildDDMSElement(Rights.getName(version), null);
			Rights component = getInstance(element, SUCCESS);
			assertFalse(component.getPrivacyAct());
			assertFalse(component.getIntellectualProperty());
			assertFalse(component.getCopyright());
			
			// Default values (at least 1 value must be explicit to prevent a null commit)
			Rights.Builder builder = new Rights.Builder();
			builder.setPrivacyAct(true);
			assertFalse(builder.commit().getIntellectualProperty());
			assertFalse(builder.commit().getCopyright());
			builder = new Rights.Builder();
			builder.setIntellectualProperty(true);
			assertFalse(builder.commit().getPrivacyAct());
		}
	}
}
