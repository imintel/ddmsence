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
 * <p> Tests related to gml:Point elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class PointTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public PointTest() throws InvalidDDMSException {
		super("point.xml");
		removeSupportedVersions("5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<Point> getFixtureList() {
		try {
			List<Point> points = new ArrayList<Point>();
			points.add(new Point(new Position(PositionTest.TEST_COORDS, null), SRSAttributesTest.getFixture(), TEST_ID));
			return (points);
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
	private Point getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Point component = null;
		try {
			component = new Point(element);
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
	private Point getInstance(Point.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Point component = null;
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
	private Point.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Point component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Point.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "Point.id", TEST_ID));
		text.append(SRSAttributesTest.getFixture().getOutput(isHTML, "Point."));
		text.append(PositionTest.getFixture().getOutput(isHTML, "Point.", ""));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() throws InvalidDDMSException {
		SRSAttributes attr = SRSAttributesTest.getFixture();
		StringBuffer xml = new StringBuffer();
		xml.append("<gml:Point ").append(getXmlnsGML()).append(" ");
		xml.append("gml:id=\"").append(TEST_ID).append("\" ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">\n\t");
		xml.append("<gml:pos ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">");
		xml.append(PositionTest.TEST_XS_LIST).append("</gml:pos>\n");
		xml.append("</gml:Point>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_GML_PREFIX,
				Point.getName(version));
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

			// No optional fields
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Point.getName(version),
				version.getGmlNamespace(), null);
			Util.addAttribute(element, SRSAttributes.NO_PREFIX, "srsName", SRSAttributes.NO_NAMESPACE,
				SRSAttributesTest.getFixture().getSrsName());
			Util.addAttribute(element, PropertyReader.getPrefix("gml"), "id", version.getGmlNamespace(), TEST_ID);
			element.appendChild(PositionTest.getFixture().getXOMElementCopy());
			Point elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new Point.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing SRS Name
			Point.Builder builder = getBaseBuilder();
			builder.getSrsAttributes().setSrsName(null);
			builder.getSrsAttributes().setAxisLabels(null);
			builder.getSrsAttributes().setUomLabels(null);
			getInstance(builder, "srsName must exist.");

			// Point SRS Name doesn't match pos SRS Name
			builder = getBaseBuilder();
			builder.getSrsAttributes().setSrsName(DIFFERENT_VALUE);
			getInstance(builder, "The srsName of the position must match");

			// Missing ID
			builder = getBaseBuilder();
			builder.setId(null);
			getInstance(builder, "id must exist.");

			// ID not NCName
			builder = getBaseBuilder();
			builder.setId("1TEST");
			getInstance(builder, "\"1TEST\" is not a valid NCName.");

			// Missing position
			builder = getBaseBuilder();
			builder.setPosition(null);
			getInstance(builder, "position must exist.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Point component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Point elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Point builderComponent = new Point.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field	
			Point.Builder builder = getBaseBuilder();
			builder.getPosition().getSrsAttributes().setSrsDimension(Integer.valueOf(22));
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getSrsAttributes().setSrsDimension(Integer.valueOf(22));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setId(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Point elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Point.Builder builder = new Point.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setId(TEST_ID);
			assertFalse(builder.isEmpty());
		}
	}
}
