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
package buri.ddmsence.ddms.security.ism;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ism:Notice elements </p>
 * 
 * <p> The valid instance of ism:Notice is generated, rather than relying on the ISM schemas to validate an XML file.
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class NoticeTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public NoticeTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String ismPrefix = PropertyReader.getPrefix("ism");
			String ismNs = version.getIsmNamespace();

			Element element = Util.buildElement(ismPrefix, Notice.getName(version), ismNs, null);
			element.addNamespaceDeclaration(ismPrefix, version.getIsmNamespace());
			NoticeAttributesTest.getFixture().addTo(element);
			SecurityAttributesTest.getFixture().addTo(element);
			element.appendChild(NoticeTextTest.getFixtureElement());
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
	public static List<Notice> getFixtureList() {
		try {
			List<Notice> list = new ArrayList<Notice>();
			list.add(new Notice(NoticeTest.getFixtureElement()));
			return (list);
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
	private Notice getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Notice component = null;
		try {
			component = new Notice(element);
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
	private Notice getInstance(Notice.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Notice component = null;
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
	private Notice.Builder getBaseBuilder() {
		Notice component = getInstance(getFixtureElement(), SUCCESS);
		return (new Notice.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "notice.noticeText", "noticeText"));
		text.append(buildOutput(isHTML, "notice.noticeText.pocType", "DoD-Dist-B"));
		text.append(buildOutput(isHTML, "notice.noticeText.classification", "U"));
		text.append(buildOutput(isHTML, "notice.noticeText.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "notice.classification", "U"));
		text.append(buildOutput(isHTML, "notice.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "notice.noticeType", "DoD-Dist-B"));
		text.append(buildOutput(isHTML, "notice.noticeReason", "noticeReason"));
		text.append(buildOutput(isHTML, "notice.noticeDate", "2011-09-15"));
		text.append(buildOutput(isHTML, "notice.unregisteredNoticeType", "unregisteredNoticeType"));
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.1")) {
			text.append(buildOutput(isHTML, "notice.externalNotice", "false"));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ism:Notice ").append(getXmlnsISM()).append(" ");
		xml.append("ism:noticeType=\"DoD-Dist-B\" ism:noticeReason=\"noticeReason\" ism:noticeDate=\"2011-09-15\" ");
		xml.append("ism:unregisteredNoticeType=\"unregisteredNoticeType\"");
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.1")) {
			xml.append(" ism:externalNotice=\"false\"");
		}
		xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ism:NoticeText ism:classification=\"U\" ism:ownerProducer=\"USA\" ism:pocType=\"DoD-Dist-B\">noticeText</ism:NoticeText></ism:Notice>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_ISM_PREFIX,
				Notice.getName(version));
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
			DDMSVersion.setCurrentVersion(sVersion);

			// No attributes
			new Notice(NoticeTextTest.getFixtureList(), null, null);
		}
	}
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No NoticeTexts
			Notice.Builder builder = getBaseBuilder();
			builder.getNoticeTexts().clear();
			getInstance(builder, "At least one ism:NoticeText");
			
			// null list parameter
			try {
				new Notice(null, SecurityAttributesTest.getFixture(), null);
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

			Notice component = getInstance(getFixtureElement(), SUCCESS);

			if (!version.isAtLeast("4.1")) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// 4.1 ism:Notice used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ism:externalNotice attribute in this DDMS component";
				String locator = "ism:Notice";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Notice elementComponent = getInstance(getFixtureElement(), SUCCESS);
			Notice builderComponent = new Notice.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Notice.Builder builder = getBaseBuilder();
			builder.getNoticeTexts().get(0).setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// Implicit, since 1 NoticeText must exist and that requires DDMS 4.0.1 or greater.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Notice elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Notice.Builder builder = new Notice.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getNoticeTexts().get(1).setValue("TEST");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Notice.Builder builder = new Notice.Builder();
			assertNotNull(builder.getNoticeTexts().get(1));
		}
	}
}
