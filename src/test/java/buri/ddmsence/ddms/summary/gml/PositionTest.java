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
 * <p> Tests related to gml:pos elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class PositionTest extends AbstractBaseTestCase {

	public static final List<Double> TEST_COORDS = new ArrayList<Double>();
	static {
		TEST_COORDS.add(new Double(32.1));
		TEST_COORDS.add(new Double(40.1));
	}
	protected static final String TEST_XS_LIST = Util.getXsList(TEST_COORDS);

	public static final List<Double> TEST_COORDS_2 = new ArrayList<Double>();
	static {
		TEST_COORDS_2.add(new Double(42.1));
		TEST_COORDS_2.add(new Double(40.1));
	}
	public static final List<Double> TEST_COORDS_3 = new ArrayList<Double>();
	static {
		TEST_COORDS_3.add(new Double(42.1));
		TEST_COORDS_3.add(new Double(50.1));
	}

	/**
	 * Constructor
	 */
	public PositionTest() throws InvalidDDMSException {
		super("position.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Position getFixture() {
		try {
			return (new Position(PositionTest.TEST_COORDS, SRSAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing. This list of positions represents a closed polygon.
	 */
	public static List<Position> getFixtureList() {
		try {
			List<Position> positions = new ArrayList<Position>();
			positions.add(new Position(TEST_COORDS, SRSAttributesTest.getFixture()));
			positions.add(new Position(TEST_COORDS_2, SRSAttributesTest.getFixture()));
			positions.add(new Position(TEST_COORDS_3, SRSAttributesTest.getFixture()));
			positions.add(new Position(TEST_COORDS, SRSAttributesTest.getFixture()));
			return (positions);
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
	private Position getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Position component = null;
		try {
			component = new Position(element);
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
	private Position getInstance(Position.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Position component = null;
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
	private Position.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Position component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Position.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "pos", TEST_XS_LIST));
		text.append(SRSAttributesTest.getFixture().getOutput(isHTML, "pos."));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<gml:pos ").append(getXmlnsGML()).append(" ");
		xml.append("srsName=\"").append(SRSAttributesTest.TEST_SRS_NAME).append("\" ");
		xml.append("srsDimension=\"").append(SRSAttributesTest.TEST_SRS_DIMENSION).append("\" ");
		xml.append("axisLabels=\"").append(Util.getXsList(SRSAttributesTest.TEST_AXIS_LABELS)).append("\" ");
		xml.append("uomLabels=\"").append(Util.getXsList(SRSAttributesTest.TEST_UOM_LABELS)).append("\">");
		xml.append(TEST_XS_LIST).append("</gml:pos>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_GML_PREFIX,
				Position.getName(version));
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
			Element element = Util.buildElement(gmlPrefix, Position.getName(version), gmlNamespace, TEST_XS_LIST);
			getInstance(element, SUCCESS);

			// Empty coordinate
			element = Util.buildElement(gmlPrefix, Position.getName(version), gmlNamespace, "25.0    26.0");
			getInstance(element, SUCCESS);

		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// Null coords param
			try {
				new Position(null, null);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "A position must be represented by");
			}
			
			// Missing coordinates
			Position.Builder builder = getBaseBuilder();
			builder.getCoordinates().clear();
			getInstance(builder, "A position must be represented by");

			// At least 2 coordinates
			builder = getBaseBuilder();
			builder.getCoordinates().clear();
			builder.getCoordinates().get(0).setValue(Double.valueOf("25.0"));
			getInstance(builder, "A position must be represented by");

			// No more than 3 coordinates
			builder = getBaseBuilder();
			builder.getCoordinates().get(2).setValue(Double.valueOf("25.0"));
			builder.getCoordinates().get(3).setValue(Double.valueOf("35.0"));
			getInstance(builder, "A position must be represented by");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Position component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String gmlPrefix = PropertyReader.getPrefix("gml");
			String gmlNamespace = version.getGmlNamespace();

			// Base equality
			Position elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Position builderComponent = new Position.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			Position.Builder builder = getBaseBuilder();
			builder.getCoordinates().get(2).setValue(Double.valueOf(100.0));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setSrsAttributes(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			// Whitespace equality
			Position position = new Position(Util.buildElement(gmlPrefix, Position.getName(version), gmlNamespace,
				TEST_XS_LIST));
			Position positionEqual = new Position(Util.buildElement(gmlPrefix, Position.getName(version), gmlNamespace,
				TEST_XS_LIST));
			Position positionEqualWhitespace = new Position(Util.buildElement(gmlPrefix, Position.getName(version),
				gmlNamespace, TEST_XS_LIST + "   "));
			Position positionUnequal2d = new Position(Util.buildElement(gmlPrefix, Position.getName(version),
				gmlNamespace, "32.1 40.0"));
			Position positionUnequal3d = new Position(Util.buildElement(gmlPrefix, Position.getName(version),
				gmlNamespace, TEST_XS_LIST + " 40.0"));
			assertEquals(position, positionEqual);
			assertEquals(position, positionEqualWhitespace);
			assertFalse(position.equals(positionUnequal2d));
			assertFalse(position.equals(positionUnequal3d));
		}
	}
	
	public void testVersionSpecific() {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Position elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Position.Builder builder = new Position.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getCoordinates().get(0).setValue(Double.valueOf(0));
			assertFalse(builder.isEmpty());
			
			// Skip empty Coordinates
			builder = new Position.Builder();
			builder.setSrsAttributes(new SRSAttributes.Builder(SRSAttributesTest.getFixture()));
			builder.getCoordinates().get(0).setValue(null);
			builder.getCoordinates().get(1).setValue(Double.valueOf(0));
			builder.getCoordinates().get(2).setValue(Double.valueOf(1));
			assertEquals(2, builder.commit().getCoordinates().size());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Position.Builder builder = new Position.Builder();
			assertNotNull(builder.getCoordinates().get(1));
		}
	}
}