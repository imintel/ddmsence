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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.Resource;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.Security;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to the ISM attributes </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class SecurityAttributesTest extends AbstractBaseTestCase {

	private static final String TEST_CLASS = "U";
	private static final List<String> TEST_OWNERS = Util.getXsListAsList("USA");

	private static final Map<String, String> TEST_OTHERS_50 = new HashMap<String, String>();
	static {
		TEST_OTHERS_50.put(SecurityAttributes.ATOMIC_ENERGY_MARKINGS_NAME, "RD");
		TEST_OTHERS_50.put(SecurityAttributes.CLASSIFICATION_REASON_NAME, "PQ");
		TEST_OTHERS_50.put(SecurityAttributes.CLASSIFIED_BY_NAME, " MN");
		TEST_OTHERS_50.put(SecurityAttributes.COMPILATION_REASON_NAME, "NO");
		TEST_OTHERS_50.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
		TEST_OTHERS_50.put(SecurityAttributes.DECLASS_EVENT_NAME, "RS");
		TEST_OTHERS_50.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1");
		TEST_OTHERS_50.put(SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME, "OP");
		TEST_OTHERS_50.put(SecurityAttributes.DERIVED_FROM_NAME, "QR");
		TEST_OTHERS_50.put(SecurityAttributes.DISPLAY_ONLY_TO_NAME, "AIA");
		TEST_OTHERS_50.put(SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "FOUO");
		TEST_OTHERS_50.put(SecurityAttributes.FGI_SOURCE_OPEN_NAME, "ABW");
		TEST_OTHERS_50.put(SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "FGI");
		TEST_OTHERS_50.put(SecurityAttributes.NON_IC_MARKINGS_NAME, "DS");
		TEST_OTHERS_50.put(SecurityAttributes.NON_US_CONTROLS_NAME, "ATOMAL");
		TEST_OTHERS_50.put(SecurityAttributes.RELEASABLE_TO_NAME, "AIA");
		TEST_OTHERS_50.put(SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-USA");
		TEST_OTHERS_50.put(SecurityAttributes.SCI_CONTROLS_NAME, "HCS");
	}
	private static final Map<String, String> TEST_OTHERS_41 = new HashMap<String, String>();
	static {
		TEST_OTHERS_41.put(SecurityAttributes.ATOMIC_ENERGY_MARKINGS_NAME, "RD");
		TEST_OTHERS_41.put(SecurityAttributes.CLASSIFICATION_REASON_NAME, "PQ");
		TEST_OTHERS_41.put(SecurityAttributes.CLASSIFIED_BY_NAME, " MN");
		TEST_OTHERS_41.put(SecurityAttributes.COMPILATION_REASON_NAME, "NO");
		TEST_OTHERS_41.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
		TEST_OTHERS_41.put(SecurityAttributes.DECLASS_EVENT_NAME, "RS");
		TEST_OTHERS_41.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1");
		TEST_OTHERS_41.put(SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME, "OP");
		TEST_OTHERS_41.put(SecurityAttributes.DERIVED_FROM_NAME, "QR");
		TEST_OTHERS_41.put(SecurityAttributes.DISPLAY_ONLY_TO_NAME, "AIA");
		TEST_OTHERS_41.put(SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "FOUO");
		TEST_OTHERS_41.put(SecurityAttributes.FGI_SOURCE_OPEN_NAME, "ALA");
		TEST_OTHERS_41.put(SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "FGI");
		TEST_OTHERS_41.put(SecurityAttributes.NON_IC_MARKINGS_NAME, "DS");
		TEST_OTHERS_41.put(SecurityAttributes.NON_US_CONTROLS_NAME, "ATOMAL");
		TEST_OTHERS_41.put(SecurityAttributes.RELEASABLE_TO_NAME, "AIA");
		TEST_OTHERS_41.put(SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-USA");
		TEST_OTHERS_41.put(SecurityAttributes.SCI_CONTROLS_NAME, "HCS");
	}
	private static final Map<String, String> TEST_OTHERS_31 = new HashMap<String, String>();
	static {
		TEST_OTHERS_31.put(SecurityAttributes.ATOMIC_ENERGY_MARKINGS_NAME, "RD");
		TEST_OTHERS_31.put(SecurityAttributes.CLASSIFICATION_REASON_NAME, "PQ");
		TEST_OTHERS_31.put(SecurityAttributes.CLASSIFIED_BY_NAME, " MN");
		TEST_OTHERS_31.put(SecurityAttributes.COMPILATION_REASON_NAME, "NO");
		TEST_OTHERS_31.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
		TEST_OTHERS_31.put(SecurityAttributes.DECLASS_EVENT_NAME, "RS");
		TEST_OTHERS_31.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1");
		TEST_OTHERS_31.put(SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME, "OP");
		TEST_OTHERS_31.put(SecurityAttributes.DERIVED_FROM_NAME, "QR");
		TEST_OTHERS_31.put(SecurityAttributes.DISPLAY_ONLY_TO_NAME, "AIA");
		TEST_OTHERS_31.put(SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "FOUO");
		TEST_OTHERS_31.put(SecurityAttributes.FGI_SOURCE_OPEN_NAME, "ALA");
		TEST_OTHERS_31.put(SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "FGI");
		TEST_OTHERS_31.put(SecurityAttributes.NON_IC_MARKINGS_NAME, "SINFO");
		TEST_OTHERS_31.put(SecurityAttributes.NON_US_CONTROLS_NAME, "ATOMAL");
		TEST_OTHERS_31.put(SecurityAttributes.RELEASABLE_TO_NAME, "AIA");
		TEST_OTHERS_31.put(SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-USA");
		TEST_OTHERS_31.put(SecurityAttributes.SCI_CONTROLS_NAME, "HCS");
	}
	private static final Map<String, String> TEST_OTHERS_30 = new HashMap<String, String>();
	static {
		TEST_OTHERS_30.put(SecurityAttributes.CLASSIFICATION_REASON_NAME, "PQ");
		TEST_OTHERS_30.put(SecurityAttributes.CLASSIFIED_BY_NAME, " MN");
		TEST_OTHERS_30.put(SecurityAttributes.COMPILATION_REASON_NAME, "NO");
		TEST_OTHERS_30.put(SecurityAttributes.DATE_OF_EXEMPTED_SOURCE_NAME, "2005-10-11");
		TEST_OTHERS_30.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
		TEST_OTHERS_30.put(SecurityAttributes.DECLASS_EVENT_NAME, "RS");
		TEST_OTHERS_30.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1");
		TEST_OTHERS_30.put(SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME, "OP");
		TEST_OTHERS_30.put(SecurityAttributes.DERIVED_FROM_NAME, "QR");
		TEST_OTHERS_30.put(SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "FOUO");
		TEST_OTHERS_30.put(SecurityAttributes.FGI_SOURCE_OPEN_NAME, "ALA");
		TEST_OTHERS_30.put(SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "FGI");
		TEST_OTHERS_30.put(SecurityAttributes.NON_IC_MARKINGS_NAME, "SINFO");
		TEST_OTHERS_30.put(SecurityAttributes.RELEASABLE_TO_NAME, "AIA");
		TEST_OTHERS_30.put(SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-USA");
		TEST_OTHERS_30.put(SecurityAttributes.SCI_CONTROLS_NAME, "HCS");
		TEST_OTHERS_30.put(SecurityAttributes.TYPE_OF_EXEMPTED_SOURCE_NAME, "OADR");
	}
	private static final Map<String, String> TEST_OTHERS_20 = new HashMap<String, String>();
	static {
		TEST_OTHERS_20.put(SecurityAttributes.CLASSIFICATION_REASON_NAME, "PQ");
		TEST_OTHERS_20.put(SecurityAttributes.CLASSIFIED_BY_NAME, " MN");
		TEST_OTHERS_20.put(SecurityAttributes.DATE_OF_EXEMPTED_SOURCE_NAME, "2005-10-11");
		TEST_OTHERS_20.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
		TEST_OTHERS_20.put(SecurityAttributes.DECLASS_EVENT_NAME, "RS");
		TEST_OTHERS_20.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1");
		TEST_OTHERS_20.put(SecurityAttributes.DECLASS_MANUAL_REVIEW_NAME, "true");
		TEST_OTHERS_20.put(SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME, "OP");
		TEST_OTHERS_20.put(SecurityAttributes.DERIVED_FROM_NAME, "QR");
		TEST_OTHERS_20.put(SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "FOUO");
		TEST_OTHERS_20.put(SecurityAttributes.FGI_SOURCE_OPEN_NAME, "ALA");
		TEST_OTHERS_20.put(SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "FGI");
		TEST_OTHERS_20.put(SecurityAttributes.NON_IC_MARKINGS_NAME, "SINFO");
		TEST_OTHERS_20.put(SecurityAttributes.RELEASABLE_TO_NAME, "AIA");
		TEST_OTHERS_20.put(SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-USA");
		TEST_OTHERS_20.put(SecurityAttributes.SCI_CONTROLS_NAME, "HCS");
		TEST_OTHERS_20.put(SecurityAttributes.TYPE_OF_EXEMPTED_SOURCE_NAME, "OADR");
	}

	/**
	 * Constructor
	 */
	public SecurityAttributesTest() {
		super(null);
	}

	/**
	 * Resets the validation property.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Returns a fixture object for testing. These attributes will only contain the basic required attributes
	 * (classification and ownerProducer).
	 */
	public static SecurityAttributes getFixture() {
		try {
			return (new SecurityAttributes(TEST_CLASS, TEST_OWNERS, null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing. These attributes will be a full set, including optional attributes.
	 */
	public static SecurityAttributes getFullFixture() {
		try {
			return (new SecurityAttributes(TEST_CLASS, TEST_OWNERS, getOtherAttributes()));
		}
		catch (InvalidDDMSException e) {
			fail(DDMSVersion.getCurrentVersion() + "Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a set of attributes for a specific version of DDMS.
	 * 
	 * @return an attribute group
	 */
	private static Map<String, String> getOtherAttributes() {
		String version = DDMSVersion.getCurrentVersion().getVersion();
		if ("2.0".equals(version))
			return (new HashMap<String, String>(TEST_OTHERS_20));
		if ("3.0".equals(version))
			return (new HashMap<String, String>(TEST_OTHERS_30));
		if ("3.1".equals(version))
			return (new HashMap<String, String>(TEST_OTHERS_31));
		if ("4.1".equals(version))
			return (new HashMap<String, String>(TEST_OTHERS_41));
		return (new HashMap<String, String>(TEST_OTHERS_50));
	}

	/**
	 * Returns a set of attributes for a specific version of DDMS, with a single attribute replaced by a custom value.
	 * 
	 * @param key the key of the attribute to replace
	 * @param value the new value to set for that attribute
	 * @return an attribute group
	 */
	private static Map<String, String> getOtherAttributes(String key, String value) {
		Map<String, String> baseAttributes = new HashMap(getOtherAttributes());
		baseAttributes.put(key, value);
		return (baseAttributes);
	}

	/**
	 * Helper method to confirm that changing a single attribute correctly affects equality of two instances
	 * 
	 * @param expected the base set of attributes
	 * @param key the key of the attribute that will change
	 * @param value the value of the attribute that will change
	 */
	private void assertAttributeChangeEquality(SecurityAttributes expected, String key, String value) {
		Map<String, String> others = getOtherAttributes(key, value);
		assertFalse(expected.equals(getInstance(TEST_CLASS, TEST_OWNERS, others, SUCCESS)));
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private SecurityAttributes getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SecurityAttributes attributes = null;
		try {
			attributes = new SecurityAttributes(element);
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
	 * @param classification the classification level, which must be a legal classification type
	 * @param ownerProducers a list of ownerProducers
	 * @param otherAttributes a name/value mapping of other ISM attributes. The value will be a String value, as it
	 *        appears in XML.
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private SecurityAttributes getInstance(String classification, List<String> ownerProducers, Map<String, String> otherAttributes,
		String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SecurityAttributes attributes = null;
		try {
			attributes = new SecurityAttributes(classification, ownerProducers, otherAttributes);
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
	private SecurityAttributes getInstance(SecurityAttributes.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SecurityAttributes component = null;
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
	private SecurityAttributes.Builder getBaseBuilder() {
		return (new SecurityAttributes.Builder(getFixture()));
	}
	
	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String ismPrefix = PropertyReader.getPrefix("ism");
			String icNamespace = version.getIsmNamespace();
			
			// Element-based
			Element element = Util.buildDDMSElement(Security.getName(version), null);
			Util.addAttribute(element, ismPrefix, Security.EXCLUDE_FROM_ROLLUP_NAME, icNamespace, "true");
			getFullFixture().addTo(element);
			SecurityAttributes elementAttributes = getInstance(element, SUCCESS);

			// Data-based via Builder
			getInstance(new SecurityAttributes.Builder(elementAttributes), SUCCESS);
			
			// Extra fields
			getInstance(TEST_CLASS, TEST_OWNERS, getOtherAttributes("notAnAttribute", "test"), SUCCESS);
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String ismPrefix = PropertyReader.getPrefix("ism");
			String icNamespace = version.getIsmNamespace();
			
			// No optional fields
			Element element = Util.buildDDMSElement(Security.getName(version), null);
			Util.addAttribute(element, ismPrefix, Security.EXCLUDE_FROM_ROLLUP_NAME, icNamespace, "true");
			Util.addAttribute(element, ismPrefix, SecurityAttributes.CLASSIFICATION_NAME, icNamespace, TEST_CLASS);
			Util.addAttribute(element, ismPrefix, SecurityAttributes.OWNER_PRODUCER_NAME, icNamespace,
				Util.getXsList(TEST_OWNERS));
			getInstance(element, SUCCESS);

			// No optional fields
			getInstance(TEST_CLASS, TEST_OWNERS, null, SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			Map<String, String> others = getOtherAttributes();
			
			// wrong declassDate date format
			SecurityAttributes.Builder builder = getBaseBuilder();
			builder.setDeclassDate("2001");
			getInstance(builder, "The declassDate must be in the xs:date format");

			// invalid declassDate date
			builder = getBaseBuilder();
			builder.setDeclassDate("baboon");
			getInstance(builder, "The ism:declassDate attribute must adhere to a valid date format");
			
			// wrong dateOfExemptedSource date format
			builder = getBaseBuilder();
			builder.setDateOfExemptedSource("2001");
			String message = (version.isAtLeast("3.1") ? "The dateOfExemptedSource attribute must only be used in DDMS 2.0 or 3.0."
				: "The dateOfExemptedSource attribute must be in the xs:date format");
			getInstance(builder, message);
			
			// invalid dateOfExemptedSource
			builder = getBaseBuilder();
			builder.setDateOfExemptedSource("baboon");
			getInstance(builder, "The ism:dateOfExemptedSource attribute must adhere to a valid date format");
			
			// Invalid classification
			builder = getBaseBuilder();
			builder.setClassification("ZOO");
			getInstance(builder, "ZOO is not a valid");
			
			// Missing classification
			builder = getBaseBuilder();
			builder.setClassification(null);
			try {
				builder.commit().requireClassification();
				fail("Allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "classification must exist.");
			}
			
			// No ownerProducers
			builder = getBaseBuilder();
			builder.setOwnerProducers(null);
			try {
				builder.commit().requireClassification();
				fail("Allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least 1 ownerProducer must exist.");
			}

			// No non-empty ownerProducers
			List<String> ownerProducers = new ArrayList<String>();
			ownerProducers.add("");
			getInstance(TEST_CLASS, ownerProducers, others, " is not a valid enumeration token");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			SecurityAttributes attr = getFullFixture();
			assertEquals(0, attr.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			SecurityAttributes elementAttributes = getFullFixture();
			SecurityAttributes builderAttributes = new SecurityAttributes.Builder(elementAttributes).commit();
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementAttributes.equals(wrongComponent));

			// Different values in each field
			SecurityAttributes expected = getFullFixture();
			if (version.isAtLeast("3.1"))
				assertAttributeChangeEquality(expected, SecurityAttributes.ATOMIC_ENERGY_MARKINGS_NAME, "FRD");
			assertFalse(expected.equals(getInstance("C", TEST_OWNERS, getOtherAttributes(), SUCCESS))); // Classification
			assertAttributeChangeEquality(expected, SecurityAttributes.CLASSIFIED_BY_NAME, DIFFERENT_VALUE);
			if (version.isAtLeast("3.0"))
				assertAttributeChangeEquality(expected, SecurityAttributes.COMPILATION_REASON_NAME,
					DIFFERENT_VALUE);
			if (!version.isAtLeast("3.1"))
				assertAttributeChangeEquality(expected, SecurityAttributes.DATE_OF_EXEMPTED_SOURCE_NAME,
					"2001-10-10");
			assertAttributeChangeEquality(expected, SecurityAttributes.DECLASS_DATE_NAME, "2001-10-10");
			assertAttributeChangeEquality(expected, SecurityAttributes.DECLASS_EVENT_NAME, DIFFERENT_VALUE);
			assertAttributeChangeEquality(expected, SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X4");
			if (!version.isAtLeast("3.0"))
				assertAttributeChangeEquality(expected, SecurityAttributes.DECLASS_MANUAL_REVIEW_NAME, "false");
			assertAttributeChangeEquality(expected, SecurityAttributes.DERIVATIVELY_CLASSIFIED_BY_NAME,
				DIFFERENT_VALUE);
			assertAttributeChangeEquality(expected, SecurityAttributes.DERIVED_FROM_NAME, DIFFERENT_VALUE);
			if (version.isAtLeast("3.1"))
				assertAttributeChangeEquality(expected, SecurityAttributes.DISPLAY_ONLY_TO_NAME, "USA");
			assertAttributeChangeEquality(expected, SecurityAttributes.DISSEMINATION_CONTROLS_NAME, "EYES");
			assertAttributeChangeEquality(expected, SecurityAttributes.FGI_SOURCE_OPEN_NAME, "BGR");
			assertAttributeChangeEquality(expected, SecurityAttributes.FGI_SOURCE_PROTECTED_NAME, "BGR");
			assertAttributeChangeEquality(expected, SecurityAttributes.NON_IC_MARKINGS_NAME, "SBU");
			if (version.isAtLeast("3.1"))
				assertAttributeChangeEquality(expected, SecurityAttributes.NON_US_CONTROLS_NAME, "BALK");
			assertFalse(expected.equals(getInstance(TEST_CLASS, Util.getXsListAsList("AUS"), getOtherAttributes(),
				SUCCESS))); // OwnerProducer
			assertAttributeChangeEquality(expected, SecurityAttributes.RELEASABLE_TO_NAME, "BGR");
			assertAttributeChangeEquality(expected, SecurityAttributes.SAR_IDENTIFIER_NAME, "SAR-AIA");
			assertAttributeChangeEquality(expected, SecurityAttributes.SCI_CONTROLS_NAME, "TK");
			if (!version.isAtLeast("3.1"))
				assertAttributeChangeEquality(expected, SecurityAttributes.TYPE_OF_EXEMPTED_SOURCE_NAME, "X4");
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// Can't attach to a different version.
		DDMSVersion.setCurrentVersion("3.0");
		SecurityAttributes attr = getFixture();
		DDMSVersion version = DDMSVersion.setCurrentVersion("2.0");
		Element element = Util.buildDDMSElement(Resource.getName(version), null);
		try {
			attr.addTo(element);
			fail("Method allowed invalid data.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The DDMS version of the parent");
		}
	}
	
	public void testOutput() {
		// Tested by parent components.
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SecurityAttributes.Builder builder = new SecurityAttributes.Builder();
			assertNotNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.setAtomicEnergyMarkings(Util.getXsListAsList(""));
			assertTrue(builder.isEmpty());
			builder.setAtomicEnergyMarkings(Util.getXsListAsList("RD FRD"));
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			SecurityAttributes.Builder builder = new SecurityAttributes.Builder();
			assertNotNull(builder.getOwnerProducers().get(1));
			assertNotNull(builder.getSCIcontrols().get(1));
			assertNotNull(builder.getSARIdentifier().get(1));
			assertNotNull(builder.getDisseminationControls().get(1));
			assertNotNull(builder.getFGIsourceOpen().get(1));
			assertNotNull(builder.getFGIsourceProtected().get(1));
			assertNotNull(builder.getReleasableTo().get(1));
			assertNotNull(builder.getNonICmarkings().get(1));

			if (version.isAtLeast("3.1")) {
				assertNotNull(builder.getAtomicEnergyMarkings().get(1));
				assertNotNull(builder.getDisplayOnlyTo().get(1));
				assertNotNull(builder.getNonUSControls().get(1));
			}
		}
	}
	
	public void testIsEmpty() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SecurityAttributes dataAttributes = getInstance(null, null, null, SUCCESS);
			assertTrue(dataAttributes.isEmpty());
			dataAttributes = getInstance(TEST_CLASS, null, null, SUCCESS);
			assertFalse(dataAttributes.isEmpty());
		}
	}
	
	public void testAddTo() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			SecurityAttributes component = getFixture();

			Element element = Util.buildDDMSElement("sample", null);
			component.addTo(element);
			SecurityAttributes output = new SecurityAttributes(element);
			assertEquals(component, output);
		}
	}

	public void testGetNonNull() throws InvalidDDMSException {
		SecurityAttributes component = new SecurityAttributes(null, null, null);
		SecurityAttributes output = SecurityAttributes.getNonNullInstance(null);
		assertEquals(component, output);

		output = SecurityAttributes.getNonNullInstance(getFixture());
		assertEquals(getFixture(), output);
	}

	public void testDateOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			Map<String, String> others = new HashMap<String, String>();
			others.put(SecurityAttributes.DECLASS_DATE_NAME, "2005-10-10");
			SecurityAttributes dataAttributes = getInstance(null, null, others, SUCCESS);
			assertEquals(buildOutput(true, "declassDate", "2005-10-10"), dataAttributes.getOutput(true, ""));
			assertEquals(buildOutput(false, "declassDate", "2005-10-10"), dataAttributes.getOutput(false, ""));

			if (!version.isAtLeast("3.1")) {
				others = new HashMap<String, String>();
				others.put(SecurityAttributes.DATE_OF_EXEMPTED_SOURCE_NAME, "2005-10-10");
				dataAttributes = getInstance(null, null, others, SUCCESS);
				assertEquals(buildOutput(true, "dateOfExemptedSource", "2005-10-10"),
					dataAttributes.getOutput(true, ""));
				assertEquals(buildOutput(false, "dateOfExemptedSource", "2005-10-10"), dataAttributes.getOutput(false,
					""));
			}
		}
	}

	public void testOldClassifications() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		getInstance("NS-S", TEST_OWNERS, null, SUCCESS);
		getInstance("NS-A", TEST_OWNERS, null, SUCCESS);
		DDMSVersion.setCurrentVersion("3.0");
		getInstance("NS-S", TEST_OWNERS, null, "NS-S is not a valid enumeration token");
		getInstance("NS-A", TEST_OWNERS, null, "NS-A is not a valid enumeration token for this attribute");
	}

	public void test30AttributesIn20() throws InvalidDDMSException {
		try {
			DDMSVersion.setCurrentVersion("3.0");
			Map<String, String> others = getOtherAttributes();
			DDMSVersion.setCurrentVersion("2.0");
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed DDMS 3.0 attributes to be used in DDMS 2.0.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The compilationReason attribute must not be used");
		}
	}

	public void test20AttributesIn30() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		Map<String, String> map = getOtherAttributes(SecurityAttributes.DECLASS_MANUAL_REVIEW_NAME, "true");
		try {
			DDMSVersion.setCurrentVersion("3.0");
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
			fail("Allowed DDMS 2.0 attributes to be used in DDMS 3.0.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The declassManualReview attribute must only be used in DDMS 2.0.");
		}

		DDMSVersion.setCurrentVersion("2.0");
		map.remove(SecurityAttributes.COMPILATION_REASON_NAME);
		SecurityAttributes attr = new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
		assertTrue(attr.getOutput(true, "").contains(buildOutput(true, "declassManualReview", "true")));
		assertTrue(attr.getOutput(false, "").contains(buildOutput(false, "declassManualReview", "true")));
	}
	
	public void test30AttributesIn31() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("3.1");
		Map<String, String> others = getOtherAttributes(SecurityAttributes.TYPE_OF_EXEMPTED_SOURCE_NAME, "OADR");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed 3.0 attributes in 3.1.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The typeOfExemptedSource attribute must only be used");
		}

		others = getOtherAttributes(SecurityAttributes.DATE_OF_EXEMPTED_SOURCE_NAME, "2010-01-01");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed 3.0 attributes in 3.1.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The dateOfExemptedSource attribute must only be used");
		}
	}
	
	public void test31AttributesIn30() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("3.0");
		Map<String, String> others = getOtherAttributes(SecurityAttributes.ATOMIC_ENERGY_MARKINGS_NAME, "RD");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed 3.1 attributes in 3.0.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The atomicEnergyMarkings attribute must not be used");
		}

		others = getOtherAttributes(SecurityAttributes.DISPLAY_ONLY_TO_NAME, "AIA");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed 3.1 attributes in 3.0.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The displayOnlyTo attribute must not be used");
		}

		others = getOtherAttributes(SecurityAttributes.NON_US_CONTROLS_NAME, "ATOMAL");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, others);
			fail("Allowed 3.1 attributes in 3.0.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The nonUSControls attribute must not be used");
		}
	}
	
	public void testMultipleDeclassException() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		Map<String, String> map = getOtherAttributes(SecurityAttributes.DECLASS_EXCEPTION_NAME, "25X1 25X2");
		new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
	}

	public void testMultipleTypeExempted() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		Map<String, String> map = getOtherAttributes(SecurityAttributes.TYPE_OF_EXEMPTED_SOURCE_NAME, "X1 X2");
		new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
	}

	public void testDeclassManualReviewHtmlOutput() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		Map<String, String> map = new HashMap<String, String>();
		map.put(SecurityAttributes.DECLASS_MANUAL_REVIEW_NAME, "true");
		SecurityAttributes attributes = new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
		assertEquals(buildOutput(true, "classification", "U") + buildOutput(true, "declassManualReview", "true")
			+ buildOutput(true, "ownerProducer", "USA"), attributes.getOutput(true, ""));
	}

	public void testCVEErrorsByDefault() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(SecurityAttributes.DECLASS_EXCEPTION_NAME, "UnknownValue");
		try {
			new SecurityAttributes(TEST_CLASS, TEST_OWNERS, map);
			fail("Allowed invalid CVE value without throwing an Exception.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "UnknownValue is not a valid enumeration token");
		}
	}

}
