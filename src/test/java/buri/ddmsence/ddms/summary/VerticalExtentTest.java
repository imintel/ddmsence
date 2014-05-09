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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:verticalExtent elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class VerticalExtentTest extends AbstractBaseTestCase {

	private static final String TEST_UOM = "Meter";
	private static final String TEST_DATUM = "AGL";
	private static final double TEST_MIN = 0.1;
	private static final double TEST_MAX = 100.1;

	/**
	 * Constructor
	 */
	public VerticalExtentTest() {
		super("verticalExtent.xml");
		removeSupportedVersions("5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static VerticalExtent getFixture() {
		try {
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
				return (new VerticalExtent(1.1, 2.2, "Meter", "HAE"));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Helper method to get the correct-case of the minVerticalExtent eleemnt.
	 */
	private String getTestMinVerticalExtentName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "minVerticalExtent" : "MinVerticalExtent");
	}

	/**
	 * Helper method to get the correct-case of the maxVerticalExtent eleemnt.
	 */
	private String getTestMaxVerticalExtentName() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "maxVerticalExtent" : "MaxVerticalExtent");
	}
	
	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private VerticalExtent getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		VerticalExtent component = null;
		try {
			component = new VerticalExtent(element);
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
	private VerticalExtent getInstance(VerticalExtent.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		VerticalExtent component = null;
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
	private VerticalExtent.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		VerticalExtent component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new VerticalExtent.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "verticalExtent.unitOfMeasure", String.valueOf(TEST_UOM)));
		text.append(buildOutput(isHTML, "verticalExtent.datum", String.valueOf(TEST_DATUM)));
		text.append(buildOutput(isHTML, "verticalExtent.minimum", String.valueOf(TEST_MIN)));
		text.append(buildOutput(isHTML, "verticalExtent.maximum", String.valueOf(TEST_MAX)));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:verticalExtent ").append(getXmlnsDDMS()).append(" ");
		xml.append("ddms:unitOfMeasure=\"").append(TEST_UOM).append("\" ");
		xml.append("ddms:datum=\"").append(TEST_DATUM).append("\">\n\t");
		xml.append("<ddms:").append(getTestMinVerticalExtentName()).append(">").append(TEST_MIN);
		xml.append("</ddms:").append(getTestMinVerticalExtentName()).append(">\n\t");
		xml.append("<ddms:").append(getTestMaxVerticalExtentName()).append(">").append(TEST_MAX);
		xml.append("</ddms:").append(getTestMaxVerticalExtentName()).append(">\n");
		xml.append("</ddms:verticalExtent>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				VerticalExtent.getName(version));
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
	
	public void testConstructorsMinimal() {
		// No tests.
	}


	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			String extentName = VerticalExtent.getName(version);

			// Missing UOM
			VerticalExtent.Builder builder = getBaseBuilder();
			builder.setUnitOfMeasure(null);
			getInstance(builder, "unitOfMeasure must exist.");

			// Invalid UOM
			builder = getBaseBuilder();
			builder.setUnitOfMeasure("furlong");
			getInstance(builder, "The length measure type must be one of");

			// Missing Datum
			builder = getBaseBuilder();
			builder.setDatum(null);
			getInstance(builder, "datum must exist");
			
			// Invalid Datum
			builder = getBaseBuilder();
			builder.setDatum("PDQ");
			getInstance(builder, "The vertical datum type must be one of");

			// Missing MinVerticalExtent
			builder = getBaseBuilder();
			builder.setMinVerticalExtent(null);
			getInstance(builder, "A ddms:verticalExtent must have a minimum and maximum extent value.");

			// Missing MaxVerticalExtent
			builder = getBaseBuilder();
			builder.setMaxVerticalExtent(null);
			getInstance(builder, "A ddms:verticalExtent must have a minimum and maximum extent value.");
			
			// MinVerticalExtent is not less than MaxVerticalExtent
			builder = getBaseBuilder();
			builder.setMinVerticalExtent(TEST_MAX);
			builder.setMaxVerticalExtent(TEST_MIN);
			getInstance(builder, "Minimum vertical extent must be less");
			
			// MinVerticalExtent UOM doesn't match parent
			Element minElement = Util.buildDDMSElement(getTestMinVerticalExtentName(), String.valueOf(TEST_MIN));
			Util.addDDMSAttribute(minElement, "unitOfMeasure", "Inch");
			Element element = Util.buildDDMSElement(extentName, null);
			Util.addDDMSAttribute(element, "unitOfMeasure", TEST_UOM);
			Util.addDDMSAttribute(element, "datum", TEST_DATUM);
			element.appendChild(minElement);
			element.appendChild(Util.buildDDMSElement(getTestMaxVerticalExtentName(), String.valueOf(TEST_MAX)));
			getInstance(element, "The unitOfMeasure on the");

			// MinVerticalExtent Datum doesn't match parent
			minElement = Util.buildDDMSElement(getTestMinVerticalExtentName(), String.valueOf(TEST_MIN));
			Util.addDDMSAttribute(minElement, "datum", "PDQ");
			element = Util.buildDDMSElement(extentName, null);
			Util.addDDMSAttribute(element, "unitOfMeasure", TEST_UOM);
			Util.addDDMSAttribute(element, "datum", TEST_DATUM);
			element.appendChild(minElement);
			element.appendChild(Util.buildDDMSElement(getTestMaxVerticalExtentName(), String.valueOf(TEST_MAX)));
			getInstance(element, "The datum on the");

			// MaxVerticalExtent UOM doesn't match parent
			Element maxElement = Util.buildDDMSElement(getTestMaxVerticalExtentName(), String.valueOf(TEST_MAX));
			Util.addDDMSAttribute(maxElement, "unitOfMeasure", "Inch");
			element = Util.buildDDMSElement(extentName, null);
			Util.addDDMSAttribute(element, "unitOfMeasure", TEST_UOM);
			Util.addDDMSAttribute(element, "datum", TEST_DATUM);
			element.appendChild(maxElement);
			element.appendChild(Util.buildDDMSElement(getTestMinVerticalExtentName(), String.valueOf(TEST_MIN)));
			getInstance(element, "The unitOfMeasure on the");

			// MaxVerticalExtent Datum doesn't match parent
			maxElement = Util.buildDDMSElement(getTestMaxVerticalExtentName(), String.valueOf(TEST_MAX));
			Util.addDDMSAttribute(maxElement, "datum", "PDQ");
			element = Util.buildDDMSElement(extentName, null);
			Util.addDDMSAttribute(element, "unitOfMeasure", TEST_UOM);
			Util.addDDMSAttribute(element, "datum", TEST_DATUM);
			element.appendChild(maxElement);
			element.appendChild(Util.buildDDMSElement(getTestMinVerticalExtentName(), String.valueOf(TEST_MIN)));
			getInstance(element, "The datum on the");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			VerticalExtent component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			VerticalExtent elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			VerticalExtent builderComponent = new VerticalExtent.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));

			// Double equality
			VerticalExtent component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(component.getMinVerticalExtent(), Double.valueOf(TEST_MIN));
			assertEquals(component.getMaxVerticalExtent(), Double.valueOf(TEST_MAX));
			
			// Different values in each field
			VerticalExtent.Builder builder = getBaseBuilder();
			builder.setUnitOfMeasure("Inch");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setUnitOfMeasure("Inch");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setDatum("HAE");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setMinVerticalExtent(Double.valueOf("1"));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setMaxVerticalExtent(Double.valueOf("101"));
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// Not in 5.0
		DDMSVersion.setCurrentVersion("4.1");
		VerticalExtent.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("5.0");
		getInstance(builder, "The verticalExtent element must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			VerticalExtent elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			VerticalExtent.Builder builder = new VerticalExtent.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setDatum(TEST_DATUM);
			assertFalse(builder.isEmpty());
		}
	}
}
