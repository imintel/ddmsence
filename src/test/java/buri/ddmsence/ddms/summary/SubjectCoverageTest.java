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
 * <p> Tests related to ddms:subjectCoverage elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class SubjectCoverageTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public SubjectCoverageTest() throws InvalidDDMSException {
		super("subjectCoverage.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param order an order value for a nonstate actor inside
	 */
	public static SubjectCoverage getFixture(int order) {
		try {
			List<NonStateActor> actors = new ArrayList<NonStateActor>();
			actors.add(NonStateActorTest.getFixture(order));
			return (new SubjectCoverage(KeywordTest.getFixtureList(), null, null,
				DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? actors : null, null));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static SubjectCoverage getFixture() {
		try {
			List<Keyword> keywords = new ArrayList<Keyword>();
			keywords.add(new Keyword("DDMSence", null));
			return (new SubjectCoverage(keywords, null, null, null, null));
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
	private SubjectCoverage getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SubjectCoverage component = null;
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
				SecurityAttributesTest.getFixture().addTo(element);
			component = new SubjectCoverage(element);
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
	private SubjectCoverage getInstance(SubjectCoverage.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		SubjectCoverage component = null;
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
	private SubjectCoverage.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		SubjectCoverage component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new SubjectCoverage.Builder(component));
	}

	/**
	 * Helper method to manage the deprecated Subject wrapper element
	 * 
	 * @param innerElement the element containing the guts of this component
	 * @return the element itself in DDMS 4.0.1 or later, or the element wrapped in another element
	 */
	private Element wrapInnerElement(Element innerElement) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String name = SubjectCoverage.getName(version);
		if (version.isAtLeast("4.0.1")) {
			innerElement.setLocalName(name);
			return (innerElement);
		}
		Element element = Util.buildDDMSElement(name, null);
		element.appendChild(innerElement);
		return (element);
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String prefix = version.isAtLeast("4.0.1") ? "subjectCoverage." : "subjectCoverage.Subject.";
		StringBuffer text = new StringBuffer();
		for (Keyword keyword : KeywordTest.getFixtureList())
			text.append(keyword.getOutput(isHTML, prefix, ""));
		for (Category category : CategoryTest.getFixtureList())
			text.append(category.getOutput(isHTML, prefix, ""));

		if (version.isAtLeast("4.0.1")) {
			for (ProductionMetric metric : ProductionMetricTest.getFixtureList())
				text.append(metric.getOutput(isHTML, prefix, ""));
			for (NonStateActor actor : NonStateActorTest.getFixtureList())
				text.append(actor.getOutput(isHTML, prefix, ""));
		}
		if (version.isAtLeast("3.0")) {
			text.append(SecurityAttributesTest.getFixture().getOutput(isHTML, prefix));
		}
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:subjectCoverage ").append(getXmlnsDDMS());
		if (version.isAtLeast("3.0")) {
			xml.append(" ").append(getXmlnsISM()).append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		}
		xml.append(">\n");
		if (version.isAtLeast("4.0.1")) {
			xml.append("\t<ddms:keyword ddms:value=\"DDMSence\" />\n");
			xml.append("\t<ddms:keyword ddms:value=\"Uri\" />\n");
			xml.append("\t<ddms:category ddms:qualifier=\"urn:buri:ddmsence:categories\" ddms:code=\"DDMS\" ");
			xml.append("ddms:label=\"DDMS\" />\n");
			xml.append("\t<ddms:productionMetric ddms:subject=\"FOOD\" ddms:coverage=\"AFG\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\" />\n");
			xml.append("\t<ddms:nonStateActor ism:classification=\"U\" ism:ownerProducer=\"USA\" ddms:order=\"1\"");
			if (version.isAtLeast("4.1")) {
				xml.append(" ddms:qualifier=\"urn:sample\"");
			}
			xml.append(">Laotian Monks</ddms:nonStateActor>\n");
		}
		else {
			xml.append("\t<ddms:Subject>\n");
			xml.append("\t\t<ddms:keyword ddms:value=\"DDMSence\" />\n");
			xml.append("\t\t<ddms:keyword ddms:value=\"Uri\" />\n");
			xml.append("\t\t<ddms:category ddms:qualifier=\"urn:buri:ddmsence:categories\" ddms:code=\"DDMS\" ");
			xml.append("ddms:label=\"DDMS\" />\n");
			xml.append("\t</ddms:Subject>\n");
		}
		xml.append("</ddms:subjectCoverage>");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				SubjectCoverage.getName(version));
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
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element subjectElement = Util.buildDDMSElement("Subject", null);
			subjectElement.appendChild(KeywordTest.getFixtureList().get(0).getXOMElementCopy());
			SubjectCoverage elementComponent = getInstance(wrapInnerElement(subjectElement), SUCCESS);
			
			// Data-based, No optional fields
			getInstance(new SubjectCoverage.Builder(elementComponent), SUCCESS);
		}
	}


	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No keywords or categories
			Element subjectElement = Util.buildDDMSElement("Subject", null);
			getInstance(wrapInnerElement(subjectElement), "At least 1 keyword or category must exist.");
			
			// Null lists
			try {
				new SubjectCoverage(null, null, null, null, SecurityAttributesTest.getFixture());
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least 1 keyword or category");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			SubjectCoverage component = getInstance(getValidElement(sVersion), SUCCESS);

			if (!"4.1".equals(sVersion)) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// 4.1 ddms:qualifier element used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ddms:qualifier attribute in this DDMS component";
				String locator = "ddms:subjectCoverage/ddms:nonStateActor";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}

			// Identical keywords
			SubjectCoverage.Builder builder = getBaseBuilder();
			builder.getNonStateActors().clear();
			builder.getKeywords().add(builder.getKeywords().get(0));
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			String text = "1 or more keywords have the same value.";
			String locator = version.isAtLeast("4.0.1") ? "ddms:subjectCoverage" : "ddms:subjectCoverage/ddms:Subject";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

			// Identical categories
			builder = getBaseBuilder();
			builder.getNonStateActors().clear();
			builder.getCategories().add(builder.getCategories().get(0));
			component = getInstance(builder, SUCCESS);
			assertEquals(1, component.getValidationWarnings().size());
			text = "1 or more categories have the same value.";
			locator = version.isAtLeast("4.0.1") ? "ddms:subjectCoverage" : "ddms:subjectCoverage/ddms:Subject";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

			// Identical productionMetrics
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.getNonStateActors().clear();
				builder.getProductionMetrics().add(builder.getProductionMetrics().get(0));
				component = getInstance(builder, SUCCESS);
				assertEquals(1, component.getValidationWarnings().size());
				text = "1 or more productionMetrics have the same value.";
				locator = "ddms:subjectCoverage";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			SubjectCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			SubjectCoverage builderComponent = new SubjectCoverage.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field	
			SubjectCoverage.Builder builder = getBaseBuilder();
			builder.getKeywords().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getCategories().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder();
				builder.getProductionMetrics().clear();
				assertFalse(elementComponent.equals(builder.commit()));
				
				builder = getBaseBuilder();
				builder.getNonStateActors().clear();
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No security attributes in DDMS 2.0
		DDMSVersion.setCurrentVersion("3.1");
		SubjectCoverage.Builder builder = getBaseBuilder();
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "Security attributes must not be applied");
	}
		
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			SubjectCoverage elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			SubjectCoverage.Builder builder = new SubjectCoverage.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
			
			// Skip empty Keywords
			builder = new SubjectCoverage.Builder();
			Keyword.Builder emptyBuilder = new Keyword.Builder();
			Keyword.Builder fullBuilder = new Keyword.Builder();
			fullBuilder.setValue("keyword");
			builder.getKeywords().add(emptyBuilder);
			builder.getKeywords().add(fullBuilder);
			assertEquals(1, builder.commit().getKeywords().size());

			// Skip empty Categories
			builder = new SubjectCoverage.Builder();
			Category.Builder emptyCategoryBuilder = new Category.Builder();
			Category.Builder fullCategoryBuilder = new Category.Builder();
			fullCategoryBuilder.setLabel("label");
			builder.getCategories().add(emptyCategoryBuilder);
			builder.getCategories().add(fullCategoryBuilder);
			assertEquals(1, builder.commit().getCategories().size());

			if (version.isAtLeast("4.0.1")) {
				// Skip empty metrics
				builder = new SubjectCoverage.Builder();
				ProductionMetric.Builder emptyProductionMetricBuilder = new ProductionMetric.Builder();
				ProductionMetric.Builder fullProductionMetricBuilder = new ProductionMetric.Builder();
				fullProductionMetricBuilder.setSubject("FOOD");
				fullProductionMetricBuilder.setCoverage("AFG");
				builder.getKeywords().get(0).setValue("test");
				builder.getProductionMetrics().add(emptyProductionMetricBuilder);
				builder.getProductionMetrics().add(fullProductionMetricBuilder);
				assertEquals(1, builder.commit().getProductionMetrics().size());

				// Skip empty actors
				builder = new SubjectCoverage.Builder();
				NonStateActor.Builder emptyNonStateActorBuilder = new NonStateActor.Builder();
				NonStateActor.Builder fullNonStateActorBuilder = new NonStateActor.Builder();
				fullNonStateActorBuilder.setValue("Laotian Monks");
				builder.getKeywords().get(0).setValue("test");
				builder.getNonStateActors().add(emptyNonStateActorBuilder);
				builder.getNonStateActors().add(fullNonStateActorBuilder);
				assertEquals(1, builder.commit().getNonStateActors().size());
			}
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			SubjectCoverage.Builder builder = new SubjectCoverage.Builder();
			assertNotNull(builder.getKeywords().get(1));
			assertNotNull(builder.getCategories().get(1));
			assertNotNull(builder.getProductionMetrics().get(1));
		}
	}
}
