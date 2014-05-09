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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.ApproximableDate;
import buri.ddmsence.ddms.ApproximableDateTest;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:temporalCoverage elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class TemporalCoverageTest extends AbstractBaseTestCase {

	private static final String TEST_NAME = "My Time Period";
	private static final String TEST_START = "1979-09-15";
	private static final String TEST_END = "Not Applicable";

	/**
	 * Constructor
	 */
	public TemporalCoverageTest() {
		super("temporalCoverage.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static TemporalCoverage getFixture() {
		try {
			return (new TemporalCoverage(null, "1979-09-15", "Not Applicable", null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Generates an approximableDate for testing
	 */
	private ApproximableDate.Builder getTestApproximableStart() throws InvalidDDMSException {
		Element element = ApproximableDateTest.getFixtureElement("approximableStart", true);
		ApproximableDate date = new ApproximableDate(element);
		return (new ApproximableDate.Builder(date));
	}

	/**
	 * Generates an approximableDate for testing
	 */
	private ApproximableDate.Builder getTestApproximableEnd() throws InvalidDDMSException {
		Element element = ApproximableDateTest.getFixtureElement("approximableEnd", true);
		ApproximableDate date = new ApproximableDate(element);
		return (new ApproximableDate.Builder(date));
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param element the element to build from
	 * @return a valid object
	 */
	private TemporalCoverage getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TemporalCoverage component = null;
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
				SecurityAttributesTest.getFixture().addTo(element);
			component = new TemporalCoverage(element);
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
	private TemporalCoverage getInstance(TemporalCoverage.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TemporalCoverage component = null;
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
	private TemporalCoverage.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		TemporalCoverage component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new TemporalCoverage.Builder(component));
	}

	/**
	 * Helper method to manage the deprecated TimePeriod wrapper element
	 * 
	 * @param innerElement the element containing the guts of this component
	 * @return the element itself in DDMS 4.0.1 or later, or the element wrapped in another element
	 */
	private Element wrapInnerElement(Element innerElement) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String name = TemporalCoverage.getName(version);
		if (version.isAtLeast("4.0.1")) {
			innerElement.setLocalName(name);
			return (innerElement);
		}
		Element element = Util.buildDDMSElement(name, null);
		element.appendChild(innerElement);
		return (element);
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML, boolean isApproximable) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = "temporalCoverage.";
		if (!version.isAtLeast("4.0.1"))
			prefix += "TimePeriod.";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, prefix + "name", TEST_NAME));
		if (isApproximable) {
			ApproximableDate start = new ApproximableDate(ApproximableDateTest.getFixtureElement("approximableStart",
				true));
			ApproximableDate end = new ApproximableDate(ApproximableDateTest.getFixtureElement("approximableEnd", true));
			text.append(start.getOutput(isHTML, prefix, ""));
			text.append(end.getOutput(isHTML, prefix, ""));
		}
		else {
			text.append(buildOutput(isHTML, prefix + "start", TEST_START));
			text.append(buildOutput(isHTML, prefix + "end", TEST_END));
		}
		if (version.isAtLeast("3.0"))
			text.append(SecurityAttributesTest.getFixture().getOutput(isHTML, prefix));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput(boolean isApproximable) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:temporalCoverage ").append(getXmlnsDDMS());
		if (version.isAtLeast("3.0"))
			xml.append(" ").append(getXmlnsISM()).append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		xml.append(">");
		if (!version.isAtLeast("4.0.1"))
			xml.append("<ddms:TimePeriod>");
		xml.append("<ddms:name>").append(TEST_NAME).append("</ddms:name>");
		if (isApproximable) {
			xml.append("<ddms:approximableStart>");
			xml.append("<ddms:description>description</ddms:description>");
			xml.append("<ddms:approximableDate ddms:approximation=\"1st qtr\">2012</ddms:approximableDate>");
			xml.append("<ddms:searchableDate><ddms:start>2012-01</ddms:start>");
			xml.append("<ddms:end>2012-03-31</ddms:end></ddms:searchableDate>");
			xml.append("</ddms:approximableStart>");
			xml.append("<ddms:approximableEnd>");
			xml.append("<ddms:description>description</ddms:description>");
			xml.append("<ddms:approximableDate ddms:approximation=\"1st qtr\">2012</ddms:approximableDate>");
			xml.append("<ddms:searchableDate><ddms:start>2012-01</ddms:start>");
			xml.append("<ddms:end>2012-03-31</ddms:end></ddms:searchableDate>");
			xml.append("</ddms:approximableEnd>");
		}
		else {
			xml.append("<ddms:start>").append(TEST_START).append("</ddms:start>");
			xml.append("<ddms:end>").append(TEST_END).append("</ddms:end>");
		}
		if (!version.isAtLeast("4.0.1"))
			xml.append("</ddms:TimePeriod>");
		xml.append("</ddms:temporalCoverage>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				TemporalCoverage.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, exact-exact
			getInstance(getValidElement(sVersion), SUCCESS);
			
			// Data-based via Builder, exact-exact
			getBaseBuilder();

			if (version.isAtLeast("4.1")) {
				// Element-based, approx-approx
				Element periodElement = Util.buildDDMSElement("TimePeriod", null);
				periodElement.appendChild(ApproximableDateTest.getFixtureElement("approximableStart", false));
				periodElement.appendChild(ApproximableDateTest.getFixtureElement("approximableEnd", false));
				TemporalCoverage elementComponent = getInstance(wrapInnerElement(periodElement), SUCCESS);
				
				// Data-based, approx-approx
				getInstance(new TemporalCoverage.Builder(elementComponent), SUCCESS);
				
				// approx-exact
				TemporalCoverage.Builder builder = new TemporalCoverage.Builder(elementComponent);
				builder.setStartString(null);
				builder.setApproximableStart(getTestApproximableStart());
				getInstance(builder, SUCCESS);
				
				// exact-approx
				builder = new TemporalCoverage.Builder(elementComponent);
				builder.setEndString(null);
				builder.setApproximableEnd(getTestApproximableEnd());
				getInstance(builder, SUCCESS);
			}
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element periodElement = Util.buildDDMSElement("TimePeriod", null);
			periodElement.appendChild(Util.buildDDMSElement("start", TEST_START));
			periodElement.appendChild(Util.buildDDMSElement("end", TEST_END));
			TemporalCoverage elementComponent = getInstance(wrapInnerElement(periodElement), SUCCESS);
	
			// Data-based, No optional fields
			getInstance(new TemporalCoverage.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Wrong date format (using xs:gDay here)
			TemporalCoverage.Builder builder = getBaseBuilder();
			builder.setStartString("---31");
			getInstance(builder, "The date datatype must be one of");
			
			if (version.isAtLeast("4.1")) {
				// start and approx start
				builder = getBaseBuilder();
				builder.setApproximableStart(getTestApproximableStart());
				getInstance(builder, "Only 1 of start or approximableStart");

				// end and approx end
				builder = getBaseBuilder();
				builder.setApproximableEnd(getTestApproximableEnd());
				getInstance(builder, "Only 1 of end or approximableEnd");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// No warnings, exact-exact
			TemporalCoverage component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Empty name element
			Element periodElement = Util.buildDDMSElement("TimePeriod", null);
			periodElement.appendChild(Util.buildDDMSElement("name", null));
			periodElement.appendChild(Util.buildDDMSElement("start", TEST_START));
			periodElement.appendChild(Util.buildDDMSElement("end", TEST_END));
			component = getInstance(wrapInnerElement(periodElement), SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A ddms:name element was found with no value.";
			String locator = version.isAtLeast("4.0.1") ? "ddms:temporalCoverage"
				: "ddms:temporalCoverage/ddms:TimePeriod";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

			// 4.1 ddms:approximableStart/End element used
			if ("4.1".equals(sVersion)) {
				TemporalCoverage.Builder builder = getBaseBuilder();
				builder.setStartString(null);
				builder.setEndString(null);
				builder.setApproximableStart(getTestApproximableStart());
				builder.setApproximableEnd(getTestApproximableEnd());
				component = getInstance(builder, SUCCESS);
				assertEquals(1, component.getValidationWarnings().size());
				text = "The ddms:approximableStart or ddms:approximableEnd element";
				locator = "ddms:temporalCoverage";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality, exact-exact
			TemporalCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			TemporalCoverage builderComponent = new TemporalCoverage.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			if (version.isAtLeast("4.1")) {
				// Base equality, approx-approx
				Element periodElement = Util.buildDDMSElement("TimePeriod", null);
				periodElement.appendChild(ApproximableDateTest.getFixtureElement("approximableStart", true));
				periodElement.appendChild(ApproximableDateTest.getFixtureElement("approximableEnd", true));
				elementComponent = getInstance(wrapInnerElement(periodElement), SUCCESS);
				builderComponent = new TemporalCoverage.Builder(elementComponent).commit();
				assertEquals(elementComponent, builderComponent);
				assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			}
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			TemporalCoverage.Builder builder = getBaseBuilder();
			builder.setTimePeriodName("");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setStartString("Not Applicable");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setEndString("2050");
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("4.1")) {
				builder = getBaseBuilder();
				builder.setStartString(null);
				builder.setApproximableStart(getTestApproximableStart());
				assertFalse(elementComponent.equals(builder.commit()));
				
				builder = getBaseBuilder();
				builder.setEndString(null);
				builder.setApproximableEnd(getTestApproximableEnd());
				assertFalse(elementComponent.equals(builder.commit()));				
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 2.0
		DDMSVersion.setCurrentVersion("3.1");
		TemporalCoverage.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "Security attributes must not be applied");
		
		// No approximable before 4.1
		DDMSVersion.setCurrentVersion("4.1");
		builder = getBaseBuilder();
		builder.setStartString(null);
		builder.setApproximableStart(getTestApproximableStart());
		DDMSVersion.setCurrentVersion("3.0");
		
		getInstance(builder, "The approximableStart element must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			TemporalCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true, false), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false, false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(false), elementComponent.toXML());
			
			if (version.isAtLeast("4.1")) {
				TemporalCoverage.Builder builder = getBaseBuilder();
				builder.setStartString(null);
				builder.setApproximableStart(getTestApproximableStart());
				builder.setEndString(null);
				builder.setApproximableEnd(getTestApproximableEnd());
				TemporalCoverage builderComponent = getInstance(builder, SUCCESS);
				assertEquals(getExpectedOutput(true, true), builderComponent.toHTML());
				assertEquals(getExpectedOutput(false, true), builderComponent.toText());
				assertEquals(getExpectedXMLOutput(true), builderComponent.toXML());	
			}
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			TemporalCoverage.Builder builder = new TemporalCoverage.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setStartString(TEST_START);
			assertFalse(builder.isEmpty());
		}
	}
	
	public void testDefaultValues() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Element periodElement = Util.buildDDMSElement("TimePeriod", null);
			periodElement.appendChild(Util.buildDDMSElement("start", ""));
			periodElement.appendChild(Util.buildDDMSElement("end", ""));
			TemporalCoverage component = getInstance(wrapInnerElement(periodElement), SUCCESS);
			assertEquals("Unknown", component.getStartString());
			assertEquals("Unknown", component.getEndString());
						
			component = new TemporalCoverage("", "", "", null);
			assertEquals("Unknown", component.getTimePeriodName());
			assertEquals("Unknown", component.getStartString());
			assertEquals("Unknown", component.getEndString());
		}
	}

	public void testDeprecatedAccessors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Element periodElement = Util.buildDDMSElement("TimePeriod", null);
			periodElement.appendChild(Util.buildDDMSElement("start", ""));
			periodElement.appendChild(Util.buildDDMSElement("end", ""));
			TemporalCoverage component = getInstance(wrapInnerElement(periodElement), SUCCESS);
			assertNull(component.getStart());
			assertNull(component.getEnd());

			component = new TemporalCoverage("", TEST_START, TEST_START, null);
			assertEquals(TEST_START, component.getStart().toXMLFormat());
			assertEquals(TEST_START, component.getEnd().toXMLFormat());
		}
	}
}