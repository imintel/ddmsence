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
package buri.ddmsence.ddms.summary.tspi;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to tspi:Polygon elements </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class PolygonTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public PolygonTest() {
		super("polygon.xml");
		removeSupportedVersions("2.0 3.0 3.1 4.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Polygon getFixture() {
		try {
			Polygon.Builder builder = new Polygon.Builder();
			builder.setXml(getExpectedXMLOutput());
			return (builder.commit());
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
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "shapeType", "Polygon"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private static String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<tspi:Polygon ");
		xml.append("xmlns:tspi=\"").append(DDMSVersion.getCurrentVersion().getTspiNamespace()).append("\" ");
		xml.append("xmlns:gml=\"").append(DDMSVersion.getCurrentVersion().getGmlNamespace()).append("\" ");
		xml.append("gml:id=\"PolygonMinimalExample\" srsName=\"http://metadata.ces.mil/mdr/ns/GSIP/crs/WGS84E_2D\">");
		xml.append("<gml:exterior>");
		xml.append("<gml:LinearRing>");
		xml.append("<gml:pos>51.0667 -1.8</gml:pos>");
		xml.append("<gml:pos>52.0 -1.75</gml:pos>");
		xml.append("<gml:pos>52.75 -1.2</gml:pos>");
		xml.append("<gml:pos>51.25 -1.4667</gml:pos>");
		xml.append("<gml:pos>51.0667 -1.8</gml:pos>");
		xml.append("</gml:LinearRing>");
		xml.append("</gml:exterior>");
		xml.append("</tspi:Polygon>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_TSPI_PREFIX,
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
		// No tests.
	}

	public void testValidationErrors() throws InvalidDDMSException {
		// Invalid XML case is implicit in Util.commitXml() test.
	}

	public void testValidationWarnings() throws InvalidDDMSException {
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
			assertFalse(elementComponent.equals(Integer.valueOf(1)));
			
			// Different values in each field
			Polygon.Builder builder = getBaseBuilder();
			String xml = getExpectedXMLOutput();
			xml = xml.replace("MinimalExample", "Example");
			builder.setXml(xml);
			assertFalse(elementComponent.equals(builder.commit()));		
		}
	}

	public void testVersionSpecific() {
		// Pre-5.0 test is implicit, since TSPI namespace did not exist.
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
			
			builder.setXml("<hello />");
			assertFalse(builder.isEmpty());
		}
	}
}
