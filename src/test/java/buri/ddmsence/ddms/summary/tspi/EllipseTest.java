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
 * <p> Tests related to tspi:Ellipse elements </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class EllipseTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public EllipseTest() {
		super("ellipse.xml");
		removeSupportedVersions("2.0 3.0 3.1 4.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Ellipse getFixture() {
		try {
			Ellipse.Builder builder = new Ellipse.Builder();
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
	private Ellipse getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Ellipse component = null;
		try {
			component = new Ellipse(element);
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
	private Ellipse.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Ellipse component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Ellipse.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "shapeType", "Ellipse"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private static String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<tspi:Ellipse ");
		xml.append("xmlns:tspi=\"").append(DDMSVersion.getCurrentVersion().getTspiNamespace()).append("\" ");
		xml.append("xmlns:tspi-core=\"http://metadata.ces.mil/mdr/ns/GSIP/tspi/2.0/core\" ");
		xml.append("xmlns:gml=\"").append(DDMSVersion.getCurrentVersion().getGmlNamespace()).append("\" ");
		xml.append("gml:id=\"EllipseMinimalExample\" srsName=\"http://metadata.ces.mil/mdr/ns/GSIP/crs/WGS84E_2D\">");
		xml.append("<gml:pos>53.81 -2.10</gml:pos>");
		xml.append("<tspi-core:semiMajorLength uom=\"http://metadata.ces.mil/mdr/ns/GSIP/uom/length/kilometre\">7.5</tspi-core:semiMajorLength>");
		xml.append("<tspi-core:semiMinorLength uom=\"http://metadata.ces.mil/mdr/ns/GSIP/uom/length/kilometre\">5</tspi-core:semiMinorLength>");
		xml.append("<tspi-core:orientation uom=\"http://metadata.ces.mil/mdr/ns/GSIP/uom/planeAngle/arcDegree\">-67.5</tspi-core:orientation>");
		xml.append("</tspi:Ellipse>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_TSPI_PREFIX,
				Ellipse.getName(version));
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
			Ellipse component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Ellipse elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Ellipse builderComponent = new Ellipse.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			assertFalse(elementComponent.equals(Integer.valueOf(1)));
			
			// Different values in each field
			Ellipse.Builder builder = getBaseBuilder();
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

			Ellipse elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Ellipse.Builder builder = new Ellipse.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setXml("<hello />");
			assertFalse(builder.isEmpty());
		}
	}
}
