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
package buri.ddmsence.ddms.security;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.ddms.security.ntk.Access;
import buri.ddmsence.ddms.security.ntk.AccessTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:security elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class SecurityTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public SecurityTest() {
		super("security.xml");
		removeSupportedVersions("5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Security getFixture() {
		try {
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				return (new Security(null, null, SecurityAttributesTest.getFixture()));
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
	private Security getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Security component = null;
		try {
			component = new Security(element);
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
	private Security getInstance(Security.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Security component = null;
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
	private Security.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Security component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Security.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = "security.";
		StringBuffer text = new StringBuffer();
		if (version.isAtLeast("3.0"))
			text.append(buildOutput(isHTML, prefix + "excludeFromRollup", "true"));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeText", "noticeText"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeText.pocType", "DoD-Dist-B"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeText.classification", "U"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeText.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.classification", "U"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeType", "DoD-Dist-B"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeReason", "noticeReason"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.noticeDate", "2011-09-15"));
			text.append(buildOutput(isHTML, prefix + "noticeList.notice.unregisteredNoticeType",
				"unregisteredNoticeType"));
			if (version.isAtLeast("4.1")) {
				text.append(buildOutput(isHTML, prefix + "noticeList.notice.externalNotice", "false"));
			}
			text.append(buildOutput(isHTML, prefix + "noticeList.classification", "U"));
			text.append(buildOutput(isHTML, prefix + "noticeList.ownerProducer", "USA"));
			if (!version.isAtLeast("5.0"))
				text.append(AccessTest.getFixture().getOutput(isHTML, "security.", ""));
		}
		text.append(SecurityAttributesTest.getFixture().getOutput(isHTML, prefix));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:security ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		if (version.isAtLeast("3.0"))
			xml.append("ism:excludeFromRollup=\"true\" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		if (!version.isAtLeast("4.0.1"))
			xml.append(" />");
		else {
			xml.append(">\n");
			xml.append("\t<ddms:noticeList ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
			xml.append("\t\t<ism:Notice ism:noticeType=\"DoD-Dist-B\" ism:noticeReason=\"noticeReason\" ism:noticeDate=\"2011-09-15\" ism:unregisteredNoticeType=\"unregisteredNoticeType\"");
			if (version.isAtLeast("4.1")) {
				xml.append(" ism:externalNotice=\"false\"");
			}
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
			xml.append("\t\t\t<ism:NoticeText ism:classification=\"U\" ism:ownerProducer=\"USA\" ism:pocType=\"DoD-Dist-B\">noticeText</ism:NoticeText>\n");
			xml.append("\t\t</ism:Notice>\n");
			xml.append("\t</ddms:noticeList>\n");
			if (!version.isAtLeast("5.0")) {
				xml.append("\t<ntk:Access xmlns:ntk=\"urn:us:gov:ic:ntk\" ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
				xml.append("\t\t<ntk:AccessIndividualList>\n");
				xml.append("\t\t\t<ntk:AccessIndividual ism:classification=\"U\" ism:ownerProducer=\"USA\">\n");
				xml.append("\t\t\t\t<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>\n");
				xml.append("\t\t\t\t<ntk:AccessIndividualValue ism:classification=\"U\" ism:ownerProducer=\"USA\">");
				xml.append("user_2321889:Doe_John_H</ntk:AccessIndividualValue>\n");
				xml.append("\t\t\t</ntk:AccessIndividual>\n");
				xml.append("\t\t</ntk:AccessIndividualList>\n");
				xml.append("\t</ntk:Access>\n");
			}
			xml.append("</ddms:security>");
		}
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Security.getName(version));
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
			if (version.isAtLeast("4.0.1")) {
				Element element = Util.buildDDMSElement(Security.getName(version), null);
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), "excludeFromRollup",
					version.getIsmNamespace(), "true");
				SecurityAttributesTest.getFixture().addTo(element);
				getInstance(element, SUCCESS);
			}
			
			// Data-based, No optional fields
			Security.Builder builder = new Security.Builder();
			builder.setSecurityAttributes(new SecurityAttributes.Builder(SecurityAttributesTest.getFixture()));
			getInstance(builder, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			if (version.isAtLeast("3.0")) {
				// Missing excludeFromRollup
				Element element = Util.buildDDMSElement(Security.getName(version), null);
				SecurityAttributesTest.getFixture().addTo(element);
				getInstance(element, "The excludeFromRollup attribute");

				// Incorrect excludeFromRollup
				element = Util.buildDDMSElement(Security.getName(version), null);
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), "excludeFromRollup",
					version.getIsmNamespace(), "false");
				getInstance(element, "The excludeFromRollup attribute");

				// Invalid excludeFromRollup
				element = Util.buildDDMSElement(Security.getName(version), null);
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), "excludeFromRollup",
					version.getIsmNamespace(), "aardvark");
				getInstance(element, "The excludeFromRollup attribute");
			}
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			boolean is41 = "4.1".equals(sVersion);
			Security component = getInstance(getValidElement(sVersion), SUCCESS);

			if (!is41) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// 4.1 ism:externalNotice used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ism:externalNotice attribute in this DDMS component";
				String locator = "ddms:security/ddms:noticeList/ism:Notice";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}

			if (is41) {
				// Nested warnings
				Element element = Util.buildDDMSElement(Security.getName(version), null);
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), "excludeFromRollup",
					version.getIsmNamespace(), "true");
				Element accessElement = Util.buildElement(PropertyReader.getPrefix("ntk"), Access.getName(version),
					version.getNtkNamespace(), null);
				SecurityAttributesTest.getFixture().addTo(accessElement);
				element.appendChild(accessElement);
				SecurityAttributesTest.getFixture().addTo(element);
				component = getInstance(element, SUCCESS);
				assertEquals(1, component.getValidationWarnings().size());
				String text = "An ntk:Access element was found with no";
				String locator = "ddms:security/ntk:Access";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}
	
	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Security elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Security builderComponent = new Security.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			if (version.isAtLeast("4.0.1")) {
				Security.Builder builder = getBaseBuilder();
				builder.setAccess(null);
				assertFalse(elementComponent.equals(builder.commit()));
				
				builder = getBaseBuilder();
				builder.setNoticeList(null);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security in 5.0
		DDMSVersion.setCurrentVersion("4.1");
		Security.Builder builder = getBaseBuilder();
		builder.setNoticeList(null);
		DDMSVersion.setCurrentVersion("5.0");
		getInstance(builder, "The security element must not be used");
		
		// No excludeFromRollup in 2.0
		DDMSVersion.setCurrentVersion("2.0");
		String icPrefix = PropertyReader.getPrefix("ism");
		String icNamespace = DDMSVersion.getCurrentVersion().getIsmNamespace();

		Element element = Util.buildDDMSElement("security", null);
		Util.addAttribute(element, icPrefix, "classification", icNamespace, "U");
		Util.addAttribute(element, icPrefix, "ownerProducer", icNamespace, "USA");
		Util.addAttribute(element, icPrefix, "excludeFromRollup", icNamespace, "true");
		getInstance(element, "The excludeFromRollup attribute must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Security elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Security.Builder builder = new Security.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
		}
	}
}
