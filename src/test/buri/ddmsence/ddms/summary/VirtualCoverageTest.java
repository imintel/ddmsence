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
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:virtualCoverage elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class VirtualCoverageTest extends AbstractBaseTestCase {

	private static final String TEST_ADDRESS = "123.456.789.0";
	private static final String TEST_PROTOCOL = "IP";
	private static final String TEST_ACCESS = "namespace1|key1^value1|key2^value1|key2^value2";
	private static final String TEST_NETWORK = "NIPRNet";

	/**
	 * Constructor
	 */
	public VirtualCoverageTest() {
		super("virtualCoverage.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static VirtualCoverage getFixture() {
		try {
			return (new VirtualCoverage("123.456.789.0", "IP", null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param element the element to build from
	 * @return a valid object
	 */
	private VirtualCoverage getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		VirtualCoverage component = null;
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
				SecurityAttributesTest.getFixture().addTo(element);
			component = new VirtualCoverage(element);
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
	private VirtualCoverage getInstance(VirtualCoverage.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		VirtualCoverage component = null;
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
	private VirtualCoverage.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		VirtualCoverage component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new VirtualCoverage.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "virtualCoverage.address", TEST_ADDRESS));
		text.append(buildOutput(isHTML, "virtualCoverage.protocol", TEST_PROTOCOL));
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			text.append(buildOutput(isHTML, "virtualCoverage.access", TEST_ACCESS));
			text.append(buildOutput(isHTML, "virtualCoverage.network", TEST_NETWORK));
		}
		if (DDMSVersion.getCurrentVersion().isAtLeast("3.0")) {
			text.append(buildOutput(isHTML, "virtualCoverage.classification", "U"));
			text.append(buildOutput(isHTML, "virtualCoverage.ownerProducer", "USA"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:virtualCoverage ").append(getXmlnsDDMS());
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			xml.append(" ").append(getXmlnsVirt());
			xml.append(" ").append(getXmlnsNTK());
		}
		if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
			xml.append(" ").append(getXmlnsISM());
		String prefix = DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "virt" : "ddms";
		xml.append(" ").append(prefix).append(":address=\"").append(TEST_ADDRESS).append("\" ");
		xml.append(prefix).append(":protocol=\"").append(TEST_PROTOCOL).append("\"");
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append(" ntk:access=\"namespace1|key1^value1|key2^value1|key2^value2\" virt:network=\"NIPRNet\"");
		if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		xml.append(" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				VirtualCoverage.getName(version));
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

			// No optional fields
			Element element = Util.buildDDMSElement(VirtualCoverage.getName(version), null);
			VirtualCoverage elementComponent = getInstance(element, SUCCESS);

			// No optional fields
			getInstance(new VirtualCoverage.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// address without protocol
			VirtualCoverage.Builder builder = getBaseBuilder();
			builder.setProtocol(null);
			getInstance(builder, "protocol must exist.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			VirtualCoverage component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Completely empty
			Element element = Util.buildDDMSElement(VirtualCoverage.getName(version), null);
			component = getInstance(element, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "A completely empty ddms:virtualCoverage element was found.";
			String locator = "ddms:virtualCoverage";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			VirtualCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			VirtualCoverage builderComponent = new VirtualCoverage.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			VirtualCoverage.Builder builder = getBaseBuilder();
			builder.setAddress(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setProtocol(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.setAccess(null);
				assertFalse(elementComponent.equals(builder.commit()));
			
				builder = getBaseBuilder();
				builder.setNetwork("SIPRNet");
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 2.0
		DDMSVersion.setCurrentVersion("3.1");
		VirtualCoverage.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "Security attributes must not be applied");
		
		// Check for access and network attributes implicit in constructor (ignored)
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			VirtualCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			VirtualCoverage.Builder builder = new VirtualCoverage.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setAddress(TEST_ADDRESS);
			assertFalse(builder.isEmpty());
		}
	}
}
