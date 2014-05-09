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
import buri.ddmsence.ddms.ITspiShape;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.summary.gml.Point;
import buri.ddmsence.ddms.summary.gml.PointTest;
import buri.ddmsence.ddms.summary.gml.Polygon;
import buri.ddmsence.ddms.summary.gml.PolygonTest;
import buri.ddmsence.ddms.summary.gml.Position;
import buri.ddmsence.ddms.summary.gml.PositionTest;
import buri.ddmsence.ddms.summary.gml.SRSAttributes;
import buri.ddmsence.ddms.summary.gml.SRSAttributesTest;
import buri.ddmsence.ddms.summary.tspi.Circle;
import buri.ddmsence.ddms.summary.tspi.CircleTest;
import buri.ddmsence.ddms.summary.tspi.Ellipse;
import buri.ddmsence.ddms.summary.tspi.EllipseTest;
import buri.ddmsence.ddms.summary.tspi.Envelope;
import buri.ddmsence.ddms.summary.tspi.EnvelopeTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:subjectCoverage elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class BoundingGeometryTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public BoundingGeometryTest() throws InvalidDDMSException {
		super("boundingGeometry.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static BoundingGeometry getFixture() {
		try {
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				return (new BoundingGeometry(null, PointTest.getFixtureList()));
			List<ITspiShape> shapes = new ArrayList<ITspiShape>();
			shapes.add(EnvelopeTest.getFixture());
			return (new BoundingGeometry(shapes));
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
	private BoundingGeometry getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		BoundingGeometry component = null;
		try {
			component = new BoundingGeometry(element);
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
	private BoundingGeometry getInstance(BoundingGeometry.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		BoundingGeometry component = null;
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
	private BoundingGeometry.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		BoundingGeometry component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new BoundingGeometry.Builder(component));
	}

	/**
	 * Helper method to confirm that a TSPI-based builder can translate into an element-constructor version
	 * and back again. Getting equal components at each step does a full walkthrough of constructors
	 * and builder constructors.
	 * 
	 * @param builder the base builder to test
	 */
	private void assertFullConversionEquality(BoundingGeometry.Builder builder) throws InvalidDDMSException {
		BoundingGeometry builderComponent = builder.commit();
		BoundingGeometry elementComponent = new BoundingGeometry(builderComponent.getXOMElementCopy());
		assertEquals(builderComponent, elementComponent);
		
		builderComponent = new BoundingGeometry.Builder(elementComponent).commit();
		assertEquals(elementComponent, builderComponent);
	}
	
	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			text.append(PointTest.getFixtureList().get(0).getOutput(isHTML, "boundingGeometry.", ""));
		}
		else {
			text.append(EnvelopeTest.getFixture().getOutput(isHTML, "boundingGeometry.", ""));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:boundingGeometry ").append(getXmlnsDDMS()).append(">");
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			xml.append("<gml:Point ").append(getXmlnsGML()).append(" ");
			xml.append("gml:id=\"IDValue\" srsName=\"http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D\" ");
			xml.append("srsDimension=\"10\" axisLabels=\"A B C\" uomLabels=\"Meter Meter Meter\">");
			xml.append("<gml:pos>32.1 40.1</gml:pos>");
			xml.append("</gml:Point>");
		}
		else {
			xml.append("<tspi:Envelope ");
			xml.append("xmlns:tspi=\"").append(DDMSVersion.getCurrentVersion().getTspiNamespace()).append("\" ");
			xml.append("xmlns:tspi-core=\"http://metadata.ces.mil/mdr/ns/GSIP/tspi/2.0/core\" ");
			xml.append("xmlns:gml=\"").append(DDMSVersion.getCurrentVersion().getGmlNamespace()).append("\" ");
			xml.append("gml:id=\"EnvelopeMinimalExample\" srsName=\"http://metadata.ces.mil/mdr/ns/GSIP/crs/WGS84E_2D\">");
			xml.append("<tspi-core:lowerCorner>");
			xml.append("<gml:pos>51.0667 -1.8</gml:pos>");
			xml.append("</tspi-core:lowerCorner>");
			xml.append("<tspi-core:upperCorner>");
			xml.append("<gml:pos>52.75 -1.2</gml:pos>");
			xml.append("</tspi-core:upperCorner>");
			xml.append("</tspi:Envelope>");
		}
		xml.append("</ddms:boundingGeometry>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				BoundingGeometry.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, Point or TSPI
			getInstance(getValidElement(sVersion), SUCCESS);

			// Data-based via Builder, Point or TSPI
			getBaseBuilder();
			
			if (!version.isAtLeast("5.0")) {
				// Element-based, Polygon
				Element element = Util.buildDDMSElement(BoundingGeometry.getName(version), null);
				element.appendChild(PolygonTest.getFixtureList().get(0).getXOMElementCopy());
				BoundingGeometry elementComponent = getInstance(element, SUCCESS);
				
				// Data-based via Builder, Polygon
				getInstance(new BoundingGeometry.Builder(elementComponent), SUCCESS);
				
				// Element-based, Both
				element = Util.buildDDMSElement(BoundingGeometry.getName(version), null);
				element.appendChild(PolygonTest.getFixtureList().get(0).getXOMElementCopy());
				element.appendChild(PointTest.getFixtureList().get(0).getXOMElementCopy());
				elementComponent = getInstance(element, SUCCESS);
				
				// Data-based via Builder, Both
				getInstance(new BoundingGeometry.Builder(elementComponent), SUCCESS);
			}
		}
	}
	
	public void testConstructorsMinimal() {
		// No tests.
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			if (!version.isAtLeast("5.0")) {
				// No polygons or points
				Element element = Util.buildDDMSElement(BoundingGeometry.getName(version), null);
				getInstance(element, "At least 1 of ");
				
				// null list parameter
				try {
					new BoundingGeometry(null, null);
					fail("Constructor allowed invalid data.");
				}
				catch (InvalidDDMSException e) {
					expectMessage(e, "At least 1 of");
				}
			}
			else {
				// Missing TSPI address
				try {
					new BoundingGeometry((List<ITspiShape>) null);
					fail("Constructor allowed invalid data.");
				}
				catch (InvalidDDMSException e) {
					expectMessage(e, "At least 1 TSPI shape must exist");
				}
				// Used the old constructor.
				try {
					new BoundingGeometry(PolygonTest.getFixtureList(), null);
					fail("Constructor allowed invalid data.");
				}
				catch (InvalidDDMSException e) {
					expectMessage(e, "boundingGeometry must be defined with TSPI");
				}
			}
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			BoundingGeometry component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			BoundingGeometry elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			BoundingGeometry builderComponent = new BoundingGeometry.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Base equality of various TSPI addresses
			// Conversion both ways: builder-element and element-builder
			if (version.isAtLeast("5.0")) {
				BoundingGeometry.Builder builder = new BoundingGeometry.Builder();
				builder.getCircles().add(new Circle.Builder(CircleTest.getFixture()));
				assertFullConversionEquality(builder);

				builder = new BoundingGeometry.Builder();
				builder.getEllipses().add(new Ellipse.Builder(EllipseTest.getFixture()));
				assertFullConversionEquality(builder);

				builder = new BoundingGeometry.Builder();
				builder.getEnvelopes().add(new Envelope.Builder(EnvelopeTest.getFixture()));
				assertFullConversionEquality(builder);

				builder = new BoundingGeometry.Builder();
				builder.getPoints().add(
					new buri.ddmsence.ddms.summary.tspi.Point.Builder(
						buri.ddmsence.ddms.summary.tspi.PointTest.getFixture()));
				assertFullConversionEquality(builder);

				builder = new BoundingGeometry.Builder();
				builder.getPolygons().add(
					new buri.ddmsence.ddms.summary.tspi.Polygon.Builder(
						buri.ddmsence.ddms.summary.tspi.PolygonTest.getFixture()));
				assertFullConversionEquality(builder);
			}
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			if (!version.isAtLeast("5.0")) {
				Polygon polygon = PolygonTest.getFixtureList().get(0);
				BoundingGeometry.Builder builder = getBaseBuilder();
				builder.getGmlPolygons().add(new Polygon.Builder(polygon));
				assertFalse(elementComponent.equals(builder.commit()));

				builder = getBaseBuilder();
				builder.getGmlPoints().clear();
				builder.getGmlPolygons().add(new Polygon.Builder(polygon));
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests yet.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			BoundingGeometry elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());

			if (!version.isAtLeast("5.0")) {
				Element element = Util.buildDDMSElement(BoundingGeometry.getName(version), null);
				element.appendChild(PolygonTest.getFixtureList().get(0).getXOMElementCopy());
				elementComponent = getInstance(element, SUCCESS);
				assertEquals(PolygonTest.getFixtureList().get(0).getOutput(true, "boundingGeometry.", ""),
					elementComponent.toHTML());
				assertEquals(PolygonTest.getFixtureList().get(0).getOutput(false, "boundingGeometry.", ""),
					elementComponent.toText());
			}
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			BoundingGeometry.Builder builder = new BoundingGeometry.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.getGmlPoints().get(0).setId(TEST_ID);
			assertFalse(builder.isEmpty());
			
			if (!version.isAtLeast("5.0")) {
				// Skip empty Points
				builder = new BoundingGeometry.Builder();
				Point.Builder emptyBuilder = new Point.Builder();
				Point.Builder fullBuilder = new Point.Builder();
				fullBuilder.setSrsAttributes(new SRSAttributes.Builder(SRSAttributesTest.getFixture()));
				fullBuilder.setId(TEST_ID);
				fullBuilder.getPosition().getCoordinates().get(0).setValue(Double.valueOf(0));
				fullBuilder.getPosition().getCoordinates().get(1).setValue(Double.valueOf(0));
				builder.getGmlPoints().add(emptyBuilder);
				builder.getGmlPoints().add(fullBuilder);
				assertEquals(1, builder.commit().getGmlPoints().size());

				// Skip empty Polygons
				builder = new BoundingGeometry.Builder();
				Polygon.Builder emptyPolygonBuilder = new Polygon.Builder();
				Polygon.Builder fullPolygonBuilder = new Polygon.Builder();
				fullPolygonBuilder.setSrsAttributes(new SRSAttributes.Builder(SRSAttributesTest.getFixture()));
				fullPolygonBuilder.setId(TEST_ID);
				fullPolygonBuilder.getPositions().add(new Position.Builder());
				fullPolygonBuilder.getPositions().add(new Position.Builder());
				fullPolygonBuilder.getPositions().add(new Position.Builder());
				fullPolygonBuilder.getPositions().add(new Position.Builder());
				fullPolygonBuilder.getPositions().get(0).getCoordinates().get(0).setValue(
					PositionTest.TEST_COORDS.get(0));
				fullPolygonBuilder.getPositions().get(0).getCoordinates().get(1).setValue(
					PositionTest.TEST_COORDS.get(1));

				fullPolygonBuilder.getPositions().get(1).getCoordinates().get(0).setValue(
					PositionTest.TEST_COORDS_2.get(0));
				fullPolygonBuilder.getPositions().get(1).getCoordinates().get(1).setValue(
					PositionTest.TEST_COORDS_2.get(1));

				fullPolygonBuilder.getPositions().get(2).getCoordinates().get(0).setValue(
					PositionTest.TEST_COORDS_3.get(0));
				fullPolygonBuilder.getPositions().get(2).getCoordinates().get(1).setValue(
					PositionTest.TEST_COORDS_3.get(1));

				fullPolygonBuilder.getPositions().get(3).getCoordinates().get(0).setValue(
					PositionTest.TEST_COORDS.get(0));
				fullPolygonBuilder.getPositions().get(3).getCoordinates().get(1).setValue(
					PositionTest.TEST_COORDS.get(1));

				builder.getGmlPolygons().add(emptyPolygonBuilder);
				builder.getGmlPolygons().add(fullPolygonBuilder);
				assertEquals(1, builder.commit().getGmlPolygons().size());
			}
		}
	}
	
	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			BoundingGeometry.Builder builder = new BoundingGeometry.Builder();
			assertNotNull(builder.getGmlPoints().get(1));
			assertNotNull(builder.getGmlPolygons().get(1));
			assertNotNull(builder.getCircles().get(1));
			assertNotNull(builder.getEllipses().get(1));
			assertNotNull(builder.getEnvelopes().get(1));
			assertNotNull(builder.getPoints().get(1));
			assertNotNull(builder.getPolygons().get(1));
		}
	}
}
