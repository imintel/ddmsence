/* Copyright 2010 - 2013 by Brian Uri!
   
   This file is part of DDMSence.
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 3.0 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
   GNU Lesser General Public License for more processingInfo.
   
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
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:processingInfo elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ProcessingInfoTest extends AbstractBaseTestCase {

	private static final String TEST_VALUE = "XSLT Transformation to convert DDMS 2.0 to DDMS 3.1.";
	private static final String TEST_DATE_PROCESSED = "2011-08-19";

	/**
	 * Constructor
	 */
	public ProcessingInfoTest() {
		super("processingInfo.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static ProcessingInfo getFixture() {
		try {
			return (new ProcessingInfo(TEST_VALUE, TEST_DATE_PROCESSED, SecurityAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<ProcessingInfo> getFixtureList() {
		List<ProcessingInfo> infos = new ArrayList<ProcessingInfo>();
		infos.add(getFixture());
		return (infos);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private ProcessingInfo getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProcessingInfo component = null;
		try {
			component = new ProcessingInfo(element);
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
	private ProcessingInfo getInstance(ProcessingInfo.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProcessingInfo component = null;
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
	private ProcessingInfo.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		ProcessingInfo component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new ProcessingInfo.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "processingInfo", TEST_VALUE));
		text.append(buildOutput(isHTML, "processingInfo.dateProcessed", TEST_DATE_PROCESSED));
		text.append(buildOutput(isHTML, "processingInfo.classification", "U"));
		text.append(buildOutput(isHTML, "processingInfo.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:processingInfo ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM());
		xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\" ");
		xml.append("ddms:dateProcessed=\"").append(TEST_DATE_PROCESSED).append("\">");
		xml.append(TEST_VALUE).append("</ddms:processingInfo>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				ProcessingInfo.getName(version));
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
			Element element = Util.buildDDMSElement(ProcessingInfo.getName(version), null);
			Util.addDDMSAttribute(element, "dateProcessed", TEST_DATE_PROCESSED);
			SecurityAttributesTest.getFixture().addTo(element);
			ProcessingInfo elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			ProcessingInfo.Builder builder = new ProcessingInfo.Builder(elementComponent);
			getInstance(builder, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing date
			ProcessingInfo.Builder builder = getBaseBuilder();
			builder.setDateProcessed(null);
			getInstance(builder, "dateProcessed must exist.");

			// Wrong date format (using xs:gDay here)
			builder = getBaseBuilder();
			builder.setDateProcessed("---31");
			getInstance(builder, "The date datatype must be one of");

			// Invalid date format
			builder = getBaseBuilder();
			builder.setDateProcessed("soon");
			getInstance(builder, "The date datatype must be one of");

			// Bad security attributes
			builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ProcessingInfo component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ProcessingInfo elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			ProcessingInfo builderComponent = new ProcessingInfo.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			ProcessingInfo.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setDateProcessed("2011");
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("4.1");
		ProcessingInfo.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The processingInfo element must not be used");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProcessingInfo elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProcessingInfo.Builder builder = new ProcessingInfo.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}

	public void testDeprecatedAccessors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			ProcessingInfo component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(TEST_DATE_PROCESSED, component.getDateProcessed().toXMLFormat());

			// Not compatible with XMLGregorianCalendar
			if (version.isAtLeast("4.1")) {
				component = new ProcessingInfo(TEST_VALUE, "2012-01-01T01:02Z", SecurityAttributesTest.getFixture());
				assertNull(component.getDateProcessed());
			}
		}
	}
}
