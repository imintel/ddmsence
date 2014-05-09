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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.ITspiShape;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.ddms.summary.gml.PointTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:geospatialCoverage elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class GeospatialCoverageTest extends AbstractBaseTestCase {

	private static final String TEST_PRECEDENCE = "Primary";
	private static final Integer TEST_ORDER = Integer.valueOf(1);

	/**
	 * Constructor
	 */
	public GeospatialCoverageTest() throws InvalidDDMSException {
		super("geospatialCoverage.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param a fixed order value
	 */
	public static GeospatialCoverage getFixture(int order) {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			return (new GeospatialCoverage(null, null, null, PostalAddressTest.getFixture(), null, null,
				version.isAtLeast("4.0.1") ? Integer.valueOf(order) : null, null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static GeospatialCoverage getFixture() {
		try {
			BoundingGeometry geometry = null;
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				geometry = new BoundingGeometry(null, PointTest.getFixtureList());
			else {
				List<ITspiShape> shapes = new ArrayList<ITspiShape>();
				shapes.add(buri.ddmsence.ddms.summary.tspi.PointTest.getFixture());
				geometry = new BoundingGeometry(shapes);
			}
			return (new GeospatialCoverage(null, null, geometry, null, null, null, null, null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private GeospatialCoverage getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		GeospatialCoverage component = null;
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
				SecurityAttributesTest.getFixture().addTo(element);
			component = new GeospatialCoverage(element);
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
	private GeospatialCoverage getInstance(GeospatialCoverage.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		GeospatialCoverage component = null;
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
	private GeospatialCoverage.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		GeospatialCoverage component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new GeospatialCoverage.Builder(component));
	}

	/**
	 * Returns the ISM attributes HTML output, if the DDMS Version supports it.
	 */
	private String getHtmlIcism() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = version.isAtLeast("4.0.1") ? "geospatialCoverage." : "geospatialCoverage.GeospatialExtent.";
		if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
			return (buildOutput(true, prefix + "classification", "U") + buildOutput(true, prefix + "ownerProducer",
				"USA"));
		return ("");
	}

	/**
	 * Returns the ISM attributes Text output, if the DDMS Version supports it.
	 */
	private String getTextIcism() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = version.isAtLeast("4.0.1") ? "geospatialCoverage." : "geospatialCoverage.GeospatialExtent.";
		if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
			return (buildOutput(false, prefix + "classification", "U") + buildOutput(false, prefix + "ownerProducer",
				"USA"));
		return ("");
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = version.isAtLeast("4.0.1") ? "geospatialCoverage." : "geospatialCoverage.GeospatialExtent.";
		StringBuffer text = new StringBuffer();
		text.append(GeographicIdentifierTest.getCountryCodeBasedFixture().getOutput(isHTML, prefix, ""));
		if (version.isAtLeast("4.0.1")) {
			text.append(buildOutput(isHTML, prefix + "precedence", "Primary"));
			text.append(buildOutput(isHTML, prefix + "order", "1"));
		}
		if (version.isAtLeast("3.0"))
			text.append(SecurityAttributesTest.getFixture().getOutput(isHTML, prefix));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:geospatialCoverage ").append(getXmlnsDDMS());
		if (version.isAtLeast("3.0")) {
			xml.append(" ").append(getXmlnsISM()).append(" ");
			if (version.isAtLeast("4.0.1"))
				xml.append("ddms:precedence=\"Primary\" ddms:order=\"1\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		}
		xml.append(">\n\t");
		if (version.isAtLeast("5.0")) {
			xml.append("<ddms:geographicIdentifier>\n\t\t");
			xml.append("<ddms:countryCode ddms:").append(CountryCodeTest.getTestQualifierName());
			xml.append("=\"urn:us:gov:dod:nga:def:geo-political:GENC:3:ed1\" ");
			xml.append("ddms:").append(CountryCodeTest.getTestValueName()).append("=\"USA\" />\n\t");
			xml.append("</ddms:geographicIdentifier>\n");
		}
		else if (version.isAtLeast("4.0.1")) {
			xml.append("<ddms:geographicIdentifier>\n\t\t");
			xml.append("<ddms:countryCode ddms:").append(CountryCodeTest.getTestQualifierName());
			xml.append("=\"urn:us:gov:ic:cvenum:irm:coverage:iso3166:trigraph:v1\" ");
			xml.append("ddms:").append(CountryCodeTest.getTestValueName()).append("=\"LAO\" />\n\t");
			xml.append("</ddms:geographicIdentifier>\n");
		}
		else {
			xml.append("<ddms:GeospatialExtent>\n\t\t<ddms:geographicIdentifier>\n\t\t\t");
			xml.append("<ddms:countryCode ddms:qualifier=\"urn:us:gov:ic:cvenum:irm:coverage:iso3166:trigraph:v1\" ddms:value=\"LAO\" />\n\t\t");
			xml.append("</ddms:geographicIdentifier>\n\t</ddms:GeospatialExtent>\n");
		}
		xml.append("</ddms:geospatialCoverage>");
		return (xml.toString());
	}

	/**
	 * Helper method to create a XOM element that can be used to test element constructors
	 * 
	 * @param component the child of the GeospatialExtent
	 * @return Element
	 */
	private Element buildComponentElement(IDDMSComponent component) {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		if (component != null)
			list.add(component);
		return (buildComponentElement(list));
	}

	/**
	 * Helper method to create a XOM element that can be used to test element constructors
	 * 
	 * @param components the children of the GeospatialExtent
	 * @return Element
	 */
	private Element buildComponentElement(List<IDDMSComponent> components) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = Util.buildDDMSElement(GeospatialCoverage.getName(DDMSVersion.getCurrentVersion()), null);
		Element extElement = version.isAtLeast("4.0.1") ? element : Util.buildDDMSElement("GeospatialExtent", null);
		for (IDDMSComponent component : components) {
			if (component != null)
				extElement.appendChild(component.getXOMElementCopy());
		}
		if (!version.isAtLeast("4.0.1"))
			element.appendChild(extElement);
		return (element);
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				GeospatialCoverage.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, geographicIdentifier
			getInstance(getValidElement(sVersion), SUCCESS);

			// Data-based via Builder, geographicIdentifier
			getBaseBuilder();

			if (!version.isAtLeast("5.0")) {
				// Element-based, boundingBox
				Element element = buildComponentElement(BoundingBoxTest.getFixture());
				GeospatialCoverage elementComponent = getInstance(element, SUCCESS);

				// Data-based via Builder, boundingBox
				getInstance(new GeospatialCoverage.Builder(elementComponent), SUCCESS);

				// Element-based, verticalExtent
				element = buildComponentElement(VerticalExtentTest.getFixture());
				elementComponent = getInstance(element, SUCCESS);

				// Data-based via Builder, verticalExtent
				getInstance(new GeospatialCoverage.Builder(elementComponent), SUCCESS);
			}

			// Element-based, boundingGeometry
			Element element = buildComponentElement(BoundingGeometryTest.getFixture());
			GeospatialCoverage elementComponent = getInstance(element, SUCCESS);

			// Data-based via Builder, boundingGeometry
			getInstance(new GeospatialCoverage.Builder(elementComponent), SUCCESS);

			// Element-based, postalAddress
			element = buildComponentElement(PostalAddressTest.getFixture());
			elementComponent = getInstance(element, SUCCESS);

			// Data-based via Builder, postalAddress
			getInstance(new GeospatialCoverage.Builder(elementComponent), SUCCESS);

			// Element-based, everything
			List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
			list.add(BoundingBoxTest.getFixture());
			list.add(BoundingGeometryTest.getFixture());
			list.add(PostalAddressTest.getFixture());
			list.add(VerticalExtentTest.getFixture());
			element = buildComponentElement(list);
			elementComponent = getInstance(element, SUCCESS);

			// Data-based, everything
			getInstance(new GeospatialCoverage.Builder(elementComponent), SUCCESS);
		}
	}

	public void testConstructorsMinimal() {
		// No tests.
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// At least 1 of geographicIdentifier, boundingBox, boundingGeometry, postalAddress, or verticalExtent
			// must be used.
			Element element = buildComponentElement((IDDMSComponent) null);
			getInstance(element, "At least 1 of ");

			// Too many geographicIdentifier
			List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
			list.add(GeographicIdentifierTest.getCountryCodeBasedFixture());
			list.add(GeographicIdentifierTest.getCountryCodeBasedFixture());
			element = buildComponentElement(list);
			getInstance(element, "No more than 1 geographicIdentifier");

			if (!version.isAtLeast("5.0")) {
				// Too many boundingBox
				list = new ArrayList<IDDMSComponent>();
				list.add(BoundingBoxTest.getFixture());
				list.add(BoundingBoxTest.getFixture());
				element = buildComponentElement(list);
				getInstance(element, "No more than 1 boundingBox");

				// Too many verticalExtent
				list = new ArrayList<IDDMSComponent>();
				list.add(VerticalExtentTest.getFixture());
				list.add(VerticalExtentTest.getFixture());
				element = buildComponentElement(list);
				getInstance(element, "No more than 1 verticalExtent");
			}

			// Too many boundingGeometry
			list = new ArrayList<IDDMSComponent>();
			list.add(BoundingGeometryTest.getFixture());
			list.add(BoundingGeometryTest.getFixture());
			element = buildComponentElement(list);
			getInstance(element, "No more than 1 boundingGeometry");

			// Too many postalAddress
			list = new ArrayList<IDDMSComponent>();
			list.add(PostalAddressTest.getFixture());
			list.add(PostalAddressTest.getFixture());
			element = buildComponentElement(list);
			getInstance(element, "No more than 1 postalAddress");

			// If facilityIdentifier is used, nothing else can.
			list = new ArrayList<IDDMSComponent>();
			list.add(GeographicIdentifierTest.getFacIdBasedFixture());
			list.add(BoundingGeometryTest.getFixture());
			element = buildComponentElement(list);
			getInstance(element, "A geographicIdentifier containing a facilityIdentifier");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			GeospatialCoverage component = getInstance(getValidElement(sVersion), SUCCESS);

			if (!version.isAtLeast("5.0")) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ddms:countryCode is syntactically correct";
				String locator = "ddms:geospatialCoverage/ddms:geographicIdentifier";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality, countryCode
			GeospatialCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			GeospatialCoverage builderComponent = new GeospatialCoverage.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));

			// Different values in each field
			if (version.isAtLeast("4.0.1")) {
				GeospatialCoverage.Builder builder = getBaseBuilder();
				builder.setOrder(Integer.valueOf(2));
				assertFalse(elementComponent.equals(builder.commit()));
			
				builder = getBaseBuilder();
				builder.setPrecedence("Secondary");
				assertFalse(elementComponent.equals(builder.commit()));
			}			
			if (!version.isAtLeast("5.0")) {
				GeospatialCoverage.Builder builder = getBaseBuilder();
				builder.setGeographicIdentifier(null);
				builder.setPrecedence(null);
				builder.setBoundingBox(new BoundingBox.Builder(BoundingBoxTest.getFixture()));
				assertFalse(elementComponent.equals(builder.commit()));

				builder = getBaseBuilder();
				builder.setGeographicIdentifier(null);
				builder.setPrecedence(null);
				builder.setVerticalExtent(new VerticalExtent.Builder(VerticalExtentTest.getFixture()));
				assertFalse(elementComponent.equals(builder.commit()));
			}

			GeospatialCoverage.Builder builder = getBaseBuilder();
			builder.setGeographicIdentifier(null);
			builder.setPrecedence(null);
			builder.setBoundingGeometry(new BoundingGeometry.Builder(BoundingGeometryTest.getFixture()));
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setGeographicIdentifier(null);
			builder.setPrecedence(null);
			builder.setPostalAddress(new PostalAddress.Builder(PostalAddressTest.getFixture()));
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 2.0
		DDMSVersion.setCurrentVersion("3.1");
		GeospatialCoverage.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "Security attributes must not be applied");

		// No precedence before 4.0.1
		DDMSVersion.setCurrentVersion("3.1");
		builder = getBaseBuilder();
		builder.setPrecedence(TEST_PRECEDENCE);
		getInstance(builder, "The ddms:precedence attribute must not be used");

		// No order before 4.0.1
		builder = getBaseBuilder();
		builder.setOrder(TEST_ORDER);
		getInstance(builder, "The ddms:order attribute must not be used");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String prefix = "geospatialCoverage.";
			if (!version.isAtLeast("4.0.1"))
				prefix += "GeospatialExtent.";

			// geographicIdentifier
			GeospatialCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());

			if (!version.isAtLeast("5.0")) {
				// boundingBox
				Element element = buildComponentElement(BoundingBoxTest.getFixture());
				elementComponent = getInstance(element, SUCCESS);
				assertEquals(BoundingBoxTest.getFixture().getOutput(true, prefix, "") + getHtmlIcism(),
					elementComponent.toHTML());
				assertEquals(BoundingBoxTest.getFixture().getOutput(false, prefix, "") + getTextIcism(),
					elementComponent.toText());

				// verticalExtent
				element = buildComponentElement(VerticalExtentTest.getFixture());
				elementComponent = getInstance(element, SUCCESS);
				assertEquals(VerticalExtentTest.getFixture().getOutput(true, prefix, "") + getHtmlIcism(),
					elementComponent.toHTML());
				assertEquals(VerticalExtentTest.getFixture().getOutput(false, prefix, "") + getTextIcism(),
					elementComponent.toText());
			}

			// boundingGeometry
			Element element = buildComponentElement(BoundingGeometryTest.getFixture());
			elementComponent = getInstance(element, SUCCESS);
			assertEquals(BoundingGeometryTest.getFixture().getOutput(true, prefix, "") + getHtmlIcism(),
				elementComponent.toHTML());
			assertEquals(BoundingGeometryTest.getFixture().getOutput(false, prefix, "") + getTextIcism(),
				elementComponent.toText());

			// postalAddress
			element = buildComponentElement(PostalAddressTest.getFixture());
			elementComponent = getInstance(element, SUCCESS);
			assertEquals(PostalAddressTest.getFixture().getOutput(true, prefix, "") + getHtmlIcism(),
				elementComponent.toHTML());
			assertEquals(PostalAddressTest.getFixture().getOutput(false, prefix, "") + getTextIcism(),
				elementComponent.toText());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			GeospatialCoverage.Builder builder = new GeospatialCoverage.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.setOrder(TEST_ORDER);
			assertFalse(builder.isEmpty());
		}
	}

	public void testGetLocatorSuffix() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			GeospatialCoverage component = getInstance(getValidElement(sVersion), SUCCESS);
			String suffix = version.isAtLeast("4.0.1") ? "" : "/ddms:GeospatialExtent";
			assertEquals(suffix, component.getLocatorSuffix());
		}
	}

	public void testPrecedenceRestrictions() {
		DDMSVersion.setCurrentVersion("4.0.1");
		try {
			new GeospatialCoverage(GeographicIdentifierTest.getCountryCodeBasedFixture(), null, null, null, null,
				"Tertiary", null, null);
			fail("Allowed invalid data.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The ddms:precedence attribute must have a value from");
		}
		try {
			new GeospatialCoverage(null, BoundingBoxTest.getFixture(), null, null, null, TEST_PRECEDENCE, null, null);
			fail("Allowed invalid data.");
		}
		catch (InvalidDDMSException e) {
			expectMessage(e, "The ddms:precedence attribute must only be applied");
		}
	}
}
