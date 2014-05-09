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
import buri.ddmsence.ddms.security.ism.NoticeTest;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:noticeList elements </p>
 * 
 * <p> Because a ddms:noticeList is a local component, we cannot load a valid document from a unit test data file. We
 * have to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class NoticeListTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public NoticeListTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1 5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();

			Element element = Util.buildDDMSElement(NoticeList.getName(version), null);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			SecurityAttributesTest.getFixture().addTo(element);
			element.appendChild(NoticeTest.getFixtureElement());
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
	public static NoticeList getFixture() {
		try {
			if ("4.1".equals(DDMSVersion.getCurrentVersion().getVersion()))
				return (new NoticeList(getFixtureElement()));
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
	private NoticeList getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeList component = null;
		try {
			component = new NoticeList(element);
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
	private NoticeList getInstance(NoticeList.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeList component = null;
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
	private NoticeList.Builder getBaseBuilder() {
		NoticeList component = getInstance(getFixtureElement(), SUCCESS);
		return (new NoticeList.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "noticeList.notice.noticeText", "noticeText"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeText.pocType", "DoD-Dist-B"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeText.classification", "U"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeText.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "noticeList.notice.classification", "U"));
		text.append(buildOutput(isHTML, "noticeList.notice.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeType", "DoD-Dist-B"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeReason", "noticeReason"));
		text.append(buildOutput(isHTML, "noticeList.notice.noticeDate", "2011-09-15"));
		text.append(buildOutput(isHTML, "noticeList.notice.unregisteredNoticeType", "unregisteredNoticeType"));
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.1")) {
			text.append(buildOutput(isHTML, "noticeList.notice.externalNotice", "false"));
		}
		text.append(buildOutput(isHTML, "noticeList.classification", "U"));
		text.append(buildOutput(isHTML, "noticeList.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:noticeList ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ism:Notice ism:noticeType=\"DoD-Dist-B\" ism:noticeReason=\"noticeReason\" ism:noticeDate=\"2011-09-15\" ");
		xml.append("ism:unregisteredNoticeType=\"unregisteredNoticeType\"");
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.1")) {
			xml.append(" ism:externalNotice=\"false\"");
		}
		xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ism:NoticeText ism:classification=\"U\" ism:ownerProducer=\"USA\" ism:pocType=\"DoD-Dist-B\">noticeText</ism:NoticeText>");
		xml.append("</ism:Notice>");
		xml.append("</ddms:noticeList>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				NoticeList.getName(version));
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

	public void testConstructorsMinimal() {
		// No tests.
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No Notices
			Element element = new Element(getFixtureElement());
			element.removeChildren();
			getInstance(element, "At least one ism:Notice");
			
			// No attributes
			NoticeList.Builder builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist");
			
			// Null notice parameter
			try {
				new NoticeList(null,  SecurityAttributesTest.getFixture());
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least one ism:Notice");
			}			
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			NoticeList component = getInstance(getFixtureElement(), SUCCESS);
			if (!version.isAtLeast("4.1")) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// 4.1 ism:externalNotice used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ism:externalNotice attribute in this DDMS component";
				String locator = "ddms:noticeList/ism:Notice";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}
	
	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			NoticeList elementComponent = getInstance(getFixtureElement(), SUCCESS);
			NoticeList builderComponent = new NoticeList.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			NoticeList.Builder builder = getBaseBuilder();
			builder.getNotices().get(0).getNoticeTexts().get(0).setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// Implicit, since 1 Notice must exist and that requires DDMS 4.0.1 or greater.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeList elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeList.Builder builder = new NoticeList.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getNotices().get(1).getNoticeTexts().get(1).setValue("TEST");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			NoticeList.Builder builder = new NoticeList.Builder();
			assertNotNull(builder.getNotices().get(1));
		}
	}
}
