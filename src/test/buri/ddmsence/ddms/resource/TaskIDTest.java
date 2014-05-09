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
import buri.ddmsence.ddms.summary.xlink.XLinkAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:taskID elements </p>
 * 
 * <p> Because a ddms:taskID is a local component, we cannot load a valid document from a unit test data file. We have
 * to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class TaskIDTest extends AbstractBaseTestCase {

	private static final String TEST_TASKING_SYSTEM = "MDR";
	private static final String TEST_VALUE = "Task #12345";
	private static final String TEST_NETWORK = "NIPRNet";
	private static final String TEST_OTHER_NETWORK = "PBS";

	/**
	 * Constructor
	 */
	public TaskIDTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElementNoNetwork() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(TaskID.getName(version), TEST_VALUE);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			element.addNamespaceDeclaration(PropertyReader.getPrefix("xlink"), version.getXlinkNamespace());
			Util.addDDMSAttribute(element, "taskingSystem", TEST_TASKING_SYSTEM);
			XLinkAttributesTest.getSimpleFixture().addTo(element);
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
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String prefix = version.isAtLeast("5.0") ? PropertyReader.getPrefix("virt") : "";
			String namespace = version.isAtLeast("5.0") ? version.getVirtNamespace() : "";

			Element element = Util.buildDDMSElement(TaskID.getName(version), TEST_VALUE);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			if (version.isAtLeast("5.0"))
				element.addNamespaceDeclaration(prefix, namespace);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("xlink"), version.getXlinkNamespace());
			Util.addDDMSAttribute(element, "taskingSystem", TEST_TASKING_SYSTEM);
			Util.addAttribute(element, prefix, "network", namespace, TEST_NETWORK);
			Util.addAttribute(element, "", "otherNetwork", "", getTestOtherNetwork());
			XLinkAttributesTest.getSimpleFixture().addTo(element);
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
	public static TaskID getFixture() {
		try {
			return (new TaskID(TaskIDTest.getFixtureElement()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Gets an otherNetwork value for the version
	 */
	private static String getTestOtherNetwork() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "" : TEST_OTHER_NETWORK);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private TaskID getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TaskID component = null;
		try {
			component = new TaskID(element);
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
	private TaskID getInstance(TaskID.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TaskID component = null;
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
	private TaskID.Builder getBaseBuilder() {
		TaskID component = getInstance(getFixtureElement(), SUCCESS);
		return (new TaskID.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "taskID", TEST_VALUE));
		text.append(buildOutput(isHTML, "taskID.taskingSystem", TEST_TASKING_SYSTEM));
		text.append(buildOutput(isHTML, "taskID.network", TEST_NETWORK));
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			text.append(buildOutput(isHTML, "taskID.otherNetwork", getTestOtherNetwork()));
		}
		text.append(XLinkAttributesTest.getSimpleFixture().getOutput(isHTML, "taskID."));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:taskID ").append(getXmlnsDDMS()).append(" ");
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append(getXmlnsVirt()).append(" ");
		xml.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		xml.append("ddms:taskingSystem=\"MDR\" ");
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append("virt:");
		xml.append("network=\"NIPRNet\" ");
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append("otherNetwork=\"PBS\" ");
		xml.append("xlink:type=\"simple\" ");
		xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"tank\" xlink:title=\"Tank Page\" ");
		xml.append("xlink:arcrole=\"arcrole\" xlink:show=\"new\" xlink:actuate=\"onLoad\">Task #12345</ddms:taskID>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				TaskID.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}
	
	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = Util.buildDDMSElement(TaskID.getName(version), TEST_VALUE);
			TaskID elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			getInstance(new TaskID.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Wrong type of XLinkAttributes
			TaskID.Builder builder = getBaseBuilder();
			builder.getXLinkAttributes().setType("locator");
			getInstance(builder, "The type attribute must have a fixed value");

			// Missing value
			builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, "value must exist.");

			if (!version.isAtLeast("5.0")) {
				// Bad network
				builder = getBaseBuilder();
				builder.setNetwork("PBS");
				getInstance(builder, "The network attribute must be one of");
			}
			else {
				// Invalid otherNetwork
				builder = getBaseBuilder();
				builder.setOtherNetwork(TEST_OTHER_NETWORK);				
				getInstance(builder, "The otherNetwork attribute must not ");
			}
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			TaskID component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			TaskID elementComponent = getInstance(getFixtureElement(), SUCCESS);
			TaskID builderComponent = new TaskID.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			TaskID.Builder builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setTaskingSystem(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setNetwork("SIPRNet");
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (!version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.setOtherNetwork(DIFFERENT_VALUE);
				assertFalse(elementComponent.equals(builder.commit()));
			}
			
			builder = getBaseBuilder();
			builder.setXLinkAttributes(null);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(getFixtureElement(), "The taskID element must not ");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			TaskID elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			TaskID.Builder builder = new TaskID.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setValue(TEST_VALUE);
			assertFalse(builder.isEmpty());
		}
	}
}
