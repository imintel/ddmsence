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
package buri.ddmsence.ddms;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p>
 * Tests related to elements of type ddms:ApproximableDateType (includes ddms:acquiredOn, and the ddms:start / ddms:end
 * values in a ddms:temporalCoverage element.
 * </p>
 * 
 * <p>
 * Because these are local components, we cannot load a valid document from a unit test data file. We have to build the
 * well-formed Element ourselves.
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.1.0
 */
public class ApproximableDateTest extends AbstractBaseTestCase {

	private static final String TEST_NAME = "acquiredOn";
	private static final String TEST_DESCRIPTION = "description";
	private static final String TEST_APPROXIMABLE_DATE = "2012";
	private static final String TEST_APPROXIMATION = "1st qtr";
	private static final String TEST_START_DATE = "2012-01";
	private static final String TEST_END_DATE = "2012-03-31";

	/**
	 * Constructor
	 */
	public ApproximableDateTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param name the element name
	 * @param includeAllFields true to include optional fields
	 */
	public static Element getFixtureElement(String name, boolean includeAllFields) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = Util.buildDDMSElement(name, null);
		element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
		if (includeAllFields) {
			Util.addDDMSChildElement(element, "description", TEST_DESCRIPTION);

			Element approximableElment = Util.buildDDMSElement("approximableDate", TEST_APPROXIMABLE_DATE);
			Util.addDDMSAttribute(approximableElment, "approximation", TEST_APPROXIMATION);
			element.appendChild(approximableElment);

			Element searchableElement = Util.buildDDMSElement("searchableDate", null);
			Util.addDDMSChildElement(searchableElement, "start", TEST_START_DATE);
			Util.addDDMSChildElement(searchableElement, "end", TEST_END_DATE);
			element.appendChild(searchableElement);
		}
		return (element);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private ApproximableDate getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ApproximableDate component = null;
		try {
			component = new ApproximableDate(element);
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
	private ApproximableDate getInstance(ApproximableDate.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ApproximableDate component = null;
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
	private ApproximableDate.Builder getBaseBuilder() {
		ApproximableDate component = getInstance(getFixtureElement(TEST_NAME, true), SUCCESS);
		return (new ApproximableDate.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "acquiredOn.description", TEST_DESCRIPTION));
		text.append(buildOutput(isHTML, "acquiredOn.approximableDate", TEST_APPROXIMABLE_DATE));
		text.append(buildOutput(isHTML, "acquiredOn.approximableDate.approximation", TEST_APPROXIMATION));
		text.append(buildOutput(isHTML, "acquiredOn.searchableDate.start", TEST_START_DATE));
		text.append(buildOutput(isHTML, "acquiredOn.searchableDate.end", TEST_END_DATE));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:acquiredOn ").append(getXmlnsDDMS()).append(">");
		xml.append("<ddms:description>").append(TEST_DESCRIPTION).append("</ddms:description>");
		xml.append("<ddms:approximableDate ddms:approximation=\"").append(TEST_APPROXIMATION).append("\">");
		xml.append(TEST_APPROXIMABLE_DATE).append("</ddms:approximableDate>");
		xml.append("<ddms:searchableDate><ddms:start>").append(TEST_START_DATE).append("</ddms:start>");
		xml.append("<ddms:end>").append(TEST_END_DATE).append("</ddms:end></ddms:searchableDate>");
		xml.append("</ddms:acquiredOn>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(TEST_NAME, true), SUCCESS), DEFAULT_DDMS_PREFIX,
				TEST_NAME);
			getInstance(getWrongNameElementFixture(), "The element name must be one of");
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(TEST_NAME, true), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}

	public void testConstructorsMinimal() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, no optional fields
			getInstance(getFixtureElement(TEST_NAME, false), SUCCESS);

			// Data-based, no optional fields
			try {
				new ApproximableDate(TEST_NAME, null, null, null, null, null);
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Wrong date format: approximableDate
			ApproximableDate.Builder builder = getBaseBuilder();
			builder.setApproximableDate("---31");
			getInstance(builder, "The date datatype");

			// Invalid approximation
			builder = getBaseBuilder();
			builder.setApproximation("almost-nearly");
			getInstance(builder, "The approximation must be");

			// Wrong date format: start
			builder = getBaseBuilder();
			builder.setSearchableStart("---31");
			getInstance(builder, "The date datatype");

			// Wrong date format: end
			builder = getBaseBuilder();
			builder.setSearchableEnd("---31");
			getInstance(builder, "The date datatype");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ApproximableDate component = getInstance(getFixtureElement(TEST_NAME, true), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Completely empty
			component = getInstance(getFixtureElement(TEST_NAME, false), SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A completely empty ddms:acquiredOn";
			String locator = "ddms:acquiredOn";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ApproximableDate elementComponent = getInstance(getFixtureElement(TEST_NAME, true), SUCCESS);
			ApproximableDate builderComponent = new ApproximableDate.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			ApproximableDate.Builder builder = getBaseBuilder();
			builder.setName("approximableStart");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setDescription(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setDescription(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setApproximableDate("2000");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setApproximation("2nd qtr");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setSearchableStart("2000");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setSearchableEnd("2500");
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		ApproximableDate.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The acquiredOn element must not ");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ApproximableDate elementComponent = getInstance(getFixtureElement(TEST_NAME, true), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ApproximableDate.Builder builder = new ApproximableDate.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setDescription(TEST_DESCRIPTION);
			assertFalse(builder.isEmpty());
		}
	}
}
