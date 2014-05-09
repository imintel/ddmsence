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
 * <p> Tests related to gml:Polygon elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.0
 */
public class PolygonTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public PolygonTest() throws InvalidDDMSException {
		super("polygon.xml");
		removeSupportedVersions("5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<Polygon> getFixtureList() {
		try {
			List<Polygon> polygons = new ArrayList<Polygon>();
			polygons.add(new Polygon(PositionTest.getFixtureList(), SRSAttributesTest.getFixture(), TEST_ID));
			return (polygons);
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
	private Polygon getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Polygon component = null;
		try {
			component = new Polygon(element);
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
	private Polygon getInstance(Polygon.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Polygon component = null;
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
	private Polygon.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Polygon component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Polygon.Builder(component));
	}

	/**
	 * Wraps a list of positions into the nested elements needed for a valid construct
	 * 
	 * @param positions the positions
	 * @return an exterior element containing a LinearRing element containing the positions
	 */
	private Element wrapPositions(List<Position> positions) {
		String gmlNamespace = DDMSVersion.getCurrentVersion().getGmlNamespace();
		Element ringElement = Util.buildElement(PropertyReader.getPrefix("gml"), "LinearRing", gmlNamespace, null);
		for (Position pos : positions) {
			ringElement.appendChild(pos.getXOMElementCopy());
		}
		Element extElement = Util.buildElement(PropertyReader.getPrefix("gml"), "exterior", gmlNamespace, null);
		extElement.appendChild(ringElement);
		return (extElement);
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "Polygon.id", TEST_ID));
		text.append(SRSAttributesTest.getFixture().getOutput(isHTML, "Polygon."));
		for (Position pos : PositionTest.getFixtureList()) {
			text.append(pos.getOutput(isHTML, "Polygon.", ""));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() throws InvalidDDMSException {
		SRSAttributes attr = SRSAttributesTest.getFixture();
		StringBuffer xml = new StringBuffer();
		xml.append("<gml:Polygon ").append(getXmlnsGML()).append(" ");
		xml.append("gml:id=\"").append(TEST_ID).append("\" ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">\n\t");
		xml.append("<gml:exterior>\n\t\t");
		xml.append("<gml:LinearRing>\n\t\t\t");
		xml.append("<gml:pos ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">");
		xml.append(Util.getXsList(PositionTest.TEST_COORDS)).append("</gml:pos>\n\t\t\t");
		xml.append("<gml:pos ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">");
		xml.append(Util.getXsList(PositionTest.TEST_COORDS_2)).append("</gml:pos>\n\t\t\t");
		xml.append("<gml:pos ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">");
		xml.append(Util.getXsList(PositionTest.TEST_COORDS_3)).append("</gml:pos>\n\t\t\t");
		xml.append("<gml:pos ");
		xml.append("srsName=\"").append(attr.getSrsName()).append("\" ");
		xml.append("srsDimension=\"").append(attr.getSrsDimension()).append("\" ");
		xml.append("axisLabels=\"").append(attr.getAxisLabelsAsXsList()).append("\" ");
		xml.append("uomLabels=\"").append(attr.getUomLabelsAsXsList()).append("\">");
		xml.append(Util.getXsList(PositionTest.TEST_COORDS)).append("</gml:pos>\n\t\t");
		xml.append("</gml:LinearRing>\n\t");
		xml.append("</gml:exterior>\n");
		xml.append("</gml:Polygon>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_GML_PREFIX,
				Polygon.getName(version));
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
			String gmlPrefix = PropertyReader.getPrefix("gml");
			String gmlNamespace = version.getGmlNamespace();

			// No optional fields
			Element element = Util.buildElement(gmlPrefix, Polygon.getName(version), gmlNamespace, null);
			Util.addAttribute(element, SRSAttributes.NO_PREFIX, "srsName", SRSAttributes.NO_NAMESPACE,
				SRSAttributesTest.getFixture().getSrsName());
			Util.addAttribute(element, gmlPrefix, "id", gmlNamespace, TEST_ID);
			element.appendChild(wrapPositions(PositionTest.getFixtureList()));
			Polygon elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new Polygon.Builder(elementComponent), SUCCESS);

			// First position matches last position but has extra whitespace.
			element = Util.buildElement(gmlPrefix, Polygon.getName(version), gmlNamespace, null);
			SRSAttributesTest.getFixture().addTo(element);
			Util.addAttribute(element, gmlPrefix, "id", gmlNamespace, TEST_ID);
			List<Position> newPositions = new ArrayList<Position>(PositionTest.getFixtureList());
			newPositions.add(PositionTest.getFixtureList().get(1));
			Element posElement = Util.buildElement(gmlPrefix, Position.getName(version), gmlNamespace,
				"32.1         40.1");
			SRSAttributesTest.getFixture().addTo(posElement);
			Position positionWhitespace = new Position(posElement);
			newPositions.add(positionWhitespace);
			element.appendChild(wrapPositions(newPositions));
			getInstance(element, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing SRS Name
			Polygon.Builder builder = getBaseBuilder();
			builder.getSrsAttributes().setSrsName(null);
			builder.getSrsAttributes().setAxisLabels(null);
			builder.getSrsAttributes().setUomLabels(null);
			getInstance(builder, "srsName must exist.");

			// Polygon SRS Name doesn't match pos SRS Name
			builder = getBaseBuilder();
			builder.getSrsAttributes().setSrsName(DIFFERENT_VALUE);
			getInstance(builder, "The srsName of each position must match");

			// Missing ID
			builder = getBaseBuilder();
			builder.setId(null);
			getInstance(builder, "id must exist.");

			// ID not NCName
			builder = getBaseBuilder();
			builder.setId("1TEST");
			getInstance(builder, "\"1TEST\" is not a valid NCName.");

			// Missing positions
			builder = getBaseBuilder();
			builder.getPositions().clear();
			getInstance(builder, "At least 4 positions");

			// First position doesn't match last position.
			builder = getBaseBuilder();
			builder.getPositions().add(new Position.Builder(PositionTest.getFixtureList().get(1)));
			getInstance(builder, "The first and last position");

			// Not enough positions
			builder = getBaseBuilder();
			builder.getPositions().remove(1);
			getInstance(builder, "At least 4 positions");
			
			// Null coords param
			try {
				new Polygon(null, SRSAttributesTest.getFixture(), TEST_ID);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least 4 positions");
			}
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Polygon component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Polygon elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Polygon builderComponent = new Polygon.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field	
			Polygon.Builder builder = getBaseBuilder();
			builder.getPositions().get(1).getSrsAttributes().setSrsDimension(Integer.valueOf(22));
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

			Polygon elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Polygon.Builder builder = new Polygon.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setId(TEST_ID);
			assertFalse(builder.isEmpty());
			
			// Skip empty Positions
			builder = new Polygon.Builder();
			builder.setId(TEST_ID);
			Position.Builder emptyBuilder = new Position.Builder();
			Position.Builder fullBuilder1 = new Position.Builder();
			fullBuilder1.getCoordinates().get(0).setValue(Double.valueOf(0));
			fullBuilder1.getCoordinates().get(1).setValue(Double.valueOf(0));
			Position.Builder fullBuilder2 = new Position.Builder();
			fullBuilder2.getCoordinates().get(0).setValue(Double.valueOf(0));
			fullBuilder2.getCoordinates().get(1).setValue(Double.valueOf(1));
			Position.Builder fullBuilder3 = new Position.Builder();
			fullBuilder3.getCoordinates().get(0).setValue(Double.valueOf(1));
			fullBuilder3.getCoordinates().get(1).setValue(Double.valueOf(1));
			builder.getPositions().add(emptyBuilder);
			builder.getPositions().add(fullBuilder1);
			builder.getPositions().add(fullBuilder2);
			builder.getPositions().add(fullBuilder3);
			builder.getPositions().add(fullBuilder1);
			builder.setSrsAttributes(new SRSAttributes.Builder(SRSAttributesTest.getFixture()));
			assertEquals(4, builder.commit().getPositions().size());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Polygon.Builder builder = new Polygon.Builder();
			assertNotNull(builder.getPositions().get(1));
		}
	}

	public void testGetLocatorSuffix() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			// Because Positions don't have any ValidationWarnings, no existing code uses this locator method right now.
			Polygon component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals("/gml:exterior/gml:LinearRing", component.getLocatorSuffix());
		}
	}
}
