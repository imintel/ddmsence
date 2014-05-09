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
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:productionMetric elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ProductionMetricTest extends AbstractBaseTestCase {

	private static final String TEST_SUBJECT = "FOOD";
	private static final String TEST_COVERAGE = "AFG";

	/**
	 * Constructor
	 */
	public ProductionMetricTest() {
		super("productionMetric.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<ProductionMetric> getFixtureList() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			List<ProductionMetric> metrics = new ArrayList<ProductionMetric>();
			if (version.isAtLeast("4.0.1"))
				metrics.add(new ProductionMetric("FOOD", "AFG", SecurityAttributesTest.getFixture()));
			return (metrics);
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
	private ProductionMetric getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProductionMetric component = null;
		try {
			component = new ProductionMetric(element);
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
	private ProductionMetric getInstance(ProductionMetric.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ProductionMetric component = null;
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
	private ProductionMetric.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		ProductionMetric component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new ProductionMetric.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "productionMetric.subject", TEST_SUBJECT));
		text.append(buildOutput(isHTML, "productionMetric.coverage", TEST_COVERAGE));
		text.append(buildOutput(isHTML, "productionMetric.classification", "U"));
		text.append(buildOutput(isHTML, "productionMetric.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:productionMetric ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ddms:subject=\"").append(TEST_SUBJECT).append("\" ");
		xml.append("ddms:coverage=\"").append(TEST_COVERAGE).append("\" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		xml.append(" />");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				ProductionMetric.getName(version));
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
	
	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing subject
			ProductionMetric.Builder builder = getBaseBuilder();
			builder.setSubject(null);
			getInstance(builder, "subject attribute must exist.");

			// Missing coverage
			builder = getBaseBuilder();
			builder.setCoverage(null);
			getInstance(builder, "coverage attribute must exist.");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ProductionMetric component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ProductionMetric elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			ProductionMetric builderComponent = new ProductionMetric.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			ProductionMetric.Builder builder = getBaseBuilder();
			builder.setSubject(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setCoverage(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		ProductionMetric.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The productionMetric element must not be used");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProductionMetric elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ProductionMetric.Builder builder = new ProductionMetric.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setCoverage(TEST_COVERAGE);
			assertFalse(builder.isEmpty());
		}
	}
}
