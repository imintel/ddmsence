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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.Resource;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to the ISM notice attributes </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class NoticeAttributesTest extends AbstractBaseTestCase {

	private static final String TEST_NOTICE_TYPE = "DoD-Dist-B";
	private static final String TEST_NOTICE_REASON = "noticeReason";
	private static final String TEST_NOTICE_DATE = "2011-09-15";
	private static final String TEST_UNREGISTERED_NOTICE_TYPE = "unregisteredNoticeType";

	/**
	 * Constructor
	 */
	public NoticeAttributesTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static NoticeAttributes getFixture() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Boolean externalNotice = version.isAtLeast("4.1") ? Boolean.FALSE : null;
			return (new NoticeAttributes(TEST_NOTICE_TYPE, TEST_NOTICE_REASON, TEST_NOTICE_DATE,
				TEST_UNREGISTERED_NOTICE_TYPE, externalNotice));
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
	private NoticeAttributes getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeAttributes attributes = null;
		try {
			attributes = new NoticeAttributes(element);
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (attributes);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param builder the builder to commit
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private NoticeAttributes getInstance(NoticeAttributes.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		NoticeAttributes component = null;
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
	 * Returns a builder, pre-populated with base data from the test attribute.
	 * 
	 * This builder can then be modified to test various conditions.
	 */
	private NoticeAttributes.Builder getBaseBuilder() {
		return (new NoticeAttributes.Builder(getFixture()));
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			Element element = Util.buildDDMSElement(Resource.getName(version), null);
			getFixture().addTo(element);
			getInstance(element, SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}

	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, no optional fields
			Element element = Util.buildDDMSElement(Resource.getName(version), null);
			NoticeAttributes elementAttributes = getInstance(element, SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new NoticeAttributes.Builder(elementAttributes), SUCCESS);
		}
	}
	
	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// invalid noticeType
			NoticeAttributes.Builder builder = getBaseBuilder();
			builder.setNoticeType("Unknown");
			getInstance(builder, "Unknown is not a valid enumeration token");

			// wrong noticeDate date format
			builder = getBaseBuilder();
			builder.setNoticeDate("2001");
			getInstance(builder, "The noticeDate attribute must be in the xs:date format");

			// invalid noticeDate
			builder = getBaseBuilder();
			builder.setNoticeDate("baboon");
			getInstance(builder, "The ism:noticeDate attribute must adhere to a valid");
						
			StringBuffer longString = new StringBuffer();
			for (int i = 0; i < NoticeAttributes.MAX_LENGTH / 10 + 1; i++) {
				longString.append("0123456789");
			}

			// too long noticeReason
			builder = getBaseBuilder();
			builder.setNoticeReason(longString.toString());
			getInstance(builder, "The noticeReason attribute must be shorter");

			// too long unregisteredNoticeType
			builder = getBaseBuilder();
			builder.setUnregisteredNoticeType(longString.toString());
			getInstance(builder, "The unregisteredNoticeType attribute must be shorter");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Element element = Util.buildDDMSElement(Resource.getName(version), null);
			getFixture().addTo(element);
			NoticeAttributes attr = getInstance(element, SUCCESS);
			assertEquals(0, attr.getValidationWarnings().size());
		}
	}
	
	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			NoticeAttributes elementAttributes = getFixture();
			NoticeAttributes builderAttributes = new NoticeAttributes.Builder(elementAttributes).commit();
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementAttributes.equals(wrongComponent));
			
			// Different values in each field
			NoticeAttributes.Builder builder = getBaseBuilder();
			builder.setNoticeType("DoD-Dist-C");
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setNoticeReason(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setNoticeDate("2011-08-15");
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setUnregisteredNoticeType(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));

			if (version.isAtLeast("4.1")) {
				builder = getBaseBuilder();
				builder.setExternalNotice(null);
				assertFalse(elementAttributes.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No attributes in 2.0.
		DDMSVersion version = DDMSVersion.setCurrentVersion("2.0");
		Element element = Util.buildDDMSElement(Resource.getName(version), null);
		Util.addAttribute(element, PropertyReader.getPrefix("ism"), NoticeAttributes.NOTICE_DATE_NAME,
			version.getIsmNamespace(), "2011-09-15");
		getInstance(element, "Notice attributes must not be used");
		
		// Can't attach to a different version.
		DDMSVersion.setCurrentVersion("4.1");
		NoticeAttributes attr = getFixture();
		version = DDMSVersion.setCurrentVersion("2.0");
		element = Util.buildDDMSElement(Resource.getName(version), null);
		try {
			attr.addTo(element);
			fail("Method allowed invalid data.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The DDMS version of the parent ");
		}
	}
		
	public void testOutput() {
		// Tested by parent components.
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeAttributes.Builder builder = new NoticeAttributes.Builder();
			assertNotNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.setNoticeType("");
			assertTrue(builder.isEmpty());
			
			builder.setNoticeReason(TEST_NOTICE_REASON);
			assertFalse(builder.isEmpty());
		}
	}

	public void testIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeAttributes dataAttributes = new NoticeAttributes(null, null, null, null);
			assertTrue(dataAttributes.isEmpty());
			dataAttributes = new NoticeAttributes(TEST_NOTICE_TYPE, null, null, null);
			assertFalse(dataAttributes.isEmpty());
		}
	}
	
	public void testAddTo() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			NoticeAttributes component = getFixture();

			Element element = Util.buildDDMSElement("sample", null);
			component.addTo(element);
			NoticeAttributes output = new NoticeAttributes(element);
			assertEquals(component, output);
		}
	}

	public void testGetNonNull() throws InvalidDDMSException {
		NoticeAttributes component = new NoticeAttributes(null, null, null, null);
		NoticeAttributes output = NoticeAttributes.getNonNullInstance(null);
		assertEquals(component, output);
		assertTrue(output.isEmpty());

		output = NoticeAttributes.getNonNullInstance(getFixture());
		assertEquals(getFixture(), output);
	}
	
	public void testDeprecatedConstructor() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeAttributes attr = new NoticeAttributes(TEST_NOTICE_TYPE, null, null, null);
			assertNull(attr.isExternalReference());
		}
	}
	
	public void testDateOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			NoticeAttributes.Builder builder = new NoticeAttributes.Builder();
			builder.setNoticeDate("2005-10-10");
			NoticeAttributes dataAttributes = builder.commit();
			assertEquals(buildOutput(true, "noticeDate", "2005-10-10"), dataAttributes.getOutput(true, ""));
			assertEquals(buildOutput(false, "noticeDate", "2005-10-10"), dataAttributes.getOutput(false, ""));
		}
	}
}
