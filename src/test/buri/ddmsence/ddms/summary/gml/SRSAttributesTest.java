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
package buri.ddmsence.ddms.summary.gml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to the SRS attributes on gml: elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class SRSAttributesTest extends AbstractBaseTestCase {

	protected static final String TEST_SRS_NAME = "http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D";
	protected static final Integer TEST_SRS_DIMENSION = 10;
	protected static final List<String> TEST_AXIS_LABELS = new ArrayList<String>();
	static {
		TEST_AXIS_LABELS.add("A");
		TEST_AXIS_LABELS.add("B");
		TEST_AXIS_LABELS.add("C");
	}
	protected static final List<String> TEST_UOM_LABELS = new ArrayList<String>();
	static {
		TEST_UOM_LABELS.add("Meter");
		TEST_UOM_LABELS.add("Meter");
		TEST_UOM_LABELS.add("Meter");
	}

	/**
	 * Constructor
	 */
	public SRSAttributesTest() {
		super(null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static SRSAttributes getFixture() {
		try {
			return (new SRSAttributes(TEST_SRS_NAME, TEST_SRS_DIMENSION, TEST_AXIS_LABELS, TEST_UOM_LABELS));
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
	private SRSAttributes getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SRSAttributes attributes = null;
		try {
			attributes = new SRSAttributes(element);
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
	private SRSAttributes getInstance(SRSAttributes.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SRSAttributes component = null;
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
	private SRSAttributes.Builder getBaseBuilder() {
		return (new SRSAttributes.Builder(getFixture()));
	}
	
	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "srsName", TEST_SRS_NAME));
		text.append(buildOutput(isHTML, "srsDimension", String.valueOf(TEST_SRS_DIMENSION)));
		text.append(buildOutput(isHTML, "axisLabels", Util.getXsList(TEST_AXIS_LABELS)));
		text.append(buildOutput(isHTML, "uomLabels", Util.getXsList(TEST_UOM_LABELS)));
		return (text.toString());
	}

	/**
	 * Helper method to add srs attributes to a XOM element. The element is not validated.
	 * 
	 * @param element element
	 * @param srsName the srsName (optional)
	 * @param srsDimension the srsDimension (optional)
	 * @param axisLabels the axis labels (optional, but should be omitted if no srsName is set)
	 * @param uomLabels the labels for UOM (required when axisLabels is set)
	 */
	private void addAttributes(Element element, String srsName, Integer srsDimension, String axisLabels,
		String uomLabels) {
		Util.addAttribute(element, SRSAttributes.NO_PREFIX, "srsName", SRSAttributes.NO_NAMESPACE, srsName);
		if (srsDimension != null) {
			Util.addAttribute(element, SRSAttributes.NO_PREFIX, "srsDimension", SRSAttributes.NO_NAMESPACE,
				String.valueOf(srsDimension));
		}
		Util.addAttribute(element, SRSAttributes.NO_PREFIX, "axisLabels", SRSAttributes.NO_NAMESPACE, axisLabels);
		Util.addAttribute(element, SRSAttributes.NO_PREFIX, "uomLabels", SRSAttributes.NO_NAMESPACE, uomLabels);
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Position.getName(version),
				version.getGmlNamespace(), null);
			addAttributes(element, TEST_SRS_NAME, TEST_SRS_DIMENSION, Util.getXsList(TEST_AXIS_LABELS),
				Util.getXsList(TEST_UOM_LABELS));
			getInstance(element, SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}

	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, no optional fields
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Position.getName(version),
				version.getGmlNamespace(), null);
			SRSAttributes elementAttributes = getInstance(element, SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new SRSAttributes.Builder(elementAttributes), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// srsName not a URI
			SRSAttributes.Builder builder = getBaseBuilder();
			builder.setSrsName(INVALID_URI);
			getInstance(builder, "Invalid URI");

			// axisLabels without srsName
			builder = getBaseBuilder();
			builder.setSrsName(null);
			getInstance(builder, "The axisLabels attribute must only be used");

			// uomLabels without axisLabels
			builder = getBaseBuilder();
			builder.setAxisLabels(null);
			getInstance(builder, "The uomLabels attribute must only be used");

			// Non-NCNames in axisLabels
			builder = getBaseBuilder();
			builder.getAxisLabels().add("1TEST");
			getInstance(builder, "\"1TEST\" is not a valid NCName.");

			// Non-NCNames in uomLabels
			builder = getBaseBuilder();
			builder.getUomLabels().add("TEST:TEST");
			getInstance(builder, "\"TEST:TEST\" is not a valid NCName.");
			
			// Dimension is a positive integer
			builder = getBaseBuilder();
			builder.setSrsDimension(Integer.valueOf(-1));
			getInstance(builder, "The srsDimension must be a positive integer.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Position.getName(version),
				version.getGmlNamespace(), null);
			addAttributes(element, TEST_SRS_NAME, TEST_SRS_DIMENSION, Util.getXsList(TEST_AXIS_LABELS),
				Util.getXsList(TEST_UOM_LABELS));
			SRSAttributes component = getInstance(element, SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			SRSAttributes elementAttributes = getFixture();
			SRSAttributes builderAttributes = new SRSAttributes.Builder(elementAttributes).commit();
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementAttributes.equals(wrongComponent));
			
			// Different values in each field
			SRSAttributes.Builder builder = getBaseBuilder();
			builder.setSrsName(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setSrsDimension(null);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getAxisLabels().add("NewLabel");
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setUomLabels(null);
			assertFalse(elementAttributes.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SRSAttributes attributes = getFixture();
			assertEquals(getExpectedOutput(true), attributes.getOutput(true, ""));
			assertEquals(getExpectedOutput(false), attributes.getOutput(false, ""));
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SRSAttributes.Builder builder = new SRSAttributes.Builder();
			assertNotNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.setSrsName(TEST_SRS_NAME);
			assertFalse(builder.isEmpty());

			builder = new SRSAttributes.Builder();
			builder.setSrsDimension(TEST_SRS_DIMENSION);
			assertFalse(builder.isEmpty());

			builder = new SRSAttributes.Builder();
			builder.getUomLabels().add(null);
			builder.getUomLabels().add("label");
			assertFalse(builder.isEmpty());

			builder = new SRSAttributes.Builder();
			builder.getAxisLabels().add(null);
			builder.getAxisLabels().add("label");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			SRSAttributes.Builder builder = new SRSAttributes.Builder();
			assertNotNull(builder.getUomLabels().get(1));
			assertNotNull(builder.getAxisLabels().get(1));
		}
	}
	
	public void testAddTo() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			SRSAttributes component = getFixture();

			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), "sample", version.getGmlNamespace(),
				null);
			component.addTo(element);
			SRSAttributes output = new SRSAttributes(element);
			assertEquals(component, output);
		}
	}

	public void testGetNonNull() throws InvalidDDMSException {
		SRSAttributes component = new SRSAttributes(null, null, null, null);
		SRSAttributes output = SRSAttributes.getNonNullInstance(null);
		assertEquals(component, output);

		output = SRSAttributes.getNonNullInstance(getFixture());
		assertEquals(getFixture(), output);
	}
}