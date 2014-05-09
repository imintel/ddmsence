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
import buri.ddmsence.ddms.summary.xlink.XLinkAttributes;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to DDMS 2.0, 3.0, 3.1 ddms:RelatedResources elements or DDMS 4.0.1 ddms:relatedResource elements
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class RelatedResourceTest extends AbstractBaseTestCase {

	private static final String TEST_RELATIONSHIP = "http://purl.org/dc/terms/references";
	private static final String TEST_DIRECTION = "outbound";
	private static final String TEST_QUALIFIER = "http://purl.org/dc/terms/URI";
	private static final String TEST_VALUE = "http://en.wikipedia.org/wiki/Tank";

	/**
	 * Constructor
	 */
	public RelatedResourceTest() {
		super("relatedResources.xml");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static RelatedResource getFixture() {
		try {
			List<Link> links = new ArrayList<Link>();
			links.add(new Link(new XLinkAttributes("http://en.wikipedia.org/wiki/Tank", "role", null, null)));
			return (new RelatedResource(links, "http://purl.org/dc/terms/references", "outbound",
				"http://purl.org/dc/terms/URI", "http://en.wikipedia.org/wiki/Tank", null));
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
	private RelatedResource getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RelatedResource component = null;
		try {
			component = new RelatedResource(element);
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
	private RelatedResource getInstance(RelatedResource.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RelatedResource component = null;
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
	private RelatedResource.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		RelatedResource component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new RelatedResource.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		String prefix = DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? "relatedResource."
			: "relatedResources.RelatedResource.";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, prefix + "relationship", TEST_RELATIONSHIP));
		text.append(buildOutput(isHTML, prefix + "direction", TEST_DIRECTION));
		text.append(buildOutput(isHTML, prefix + "qualifier", TEST_QUALIFIER));
		text.append(buildOutput(isHTML, prefix + "value", TEST_VALUE));
		text.append(buildOutput(isHTML, prefix + "link.type", "locator"));
		text.append(buildOutput(isHTML, prefix + "link.href", TEST_VALUE));
		text.append(buildOutput(isHTML, prefix + "link.role", "tank"));
		text.append(buildOutput(isHTML, prefix + "link.title", "Tank Page"));
		text.append(buildOutput(isHTML, prefix + "link.label", "tank"));
		text.append(buildOutput(isHTML, prefix + "classification", "U"));
		text.append(buildOutput(isHTML, prefix + "ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		if (version.isAtLeast("4.0.1")) {
			xml.append("<ddms:relatedResource ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
			xml.append("ddms:relationship=\"http://purl.org/dc/terms/references\" ddms:direction=\"outbound\" ");
			xml.append("ddms:qualifier=\"http://purl.org/dc/terms/URI\" ddms:value=\"http://en.wikipedia.org/wiki/Tank\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ddms:link xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"locator\" ");
			xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" />");
			xml.append("</ddms:relatedResource>");
		}
		else {
			xml.append("<ddms:relatedResources ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
			xml.append("ddms:relationship=\"http://purl.org/dc/terms/references\" ddms:direction=\"outbound\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ddms:RelatedResource ddms:qualifier=\"http://purl.org/dc/terms/URI\" ");
			xml.append("ddms:value=\"http://en.wikipedia.org/wiki/Tank\">");
			xml.append("<ddms:link xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"locator\" ");
			xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" />");
			xml.append("</ddms:RelatedResource>");
			xml.append("</ddms:relatedResources>");
		}
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				RelatedResource.getName(version));
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
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = Util.buildDDMSElement(RelatedResource.getName(version), null);
			Util.addDDMSAttribute(element, "relationship", TEST_RELATIONSHIP);
			Element innerElement = version.isAtLeast("4.0.1") ? element
				: Util.buildDDMSElement("RelatedResource", null);
			if (!version.isAtLeast("4.0.1"))
				element.appendChild(innerElement);
			Util.addDDMSAttribute(innerElement, "qualifier", TEST_QUALIFIER);
			Util.addDDMSAttribute(innerElement, "value", TEST_VALUE);
			innerElement.appendChild(new Element(LinkTest.getFixtureElement()));
			RelatedResource elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields
			RelatedResource.Builder builder = new RelatedResource.Builder(elementComponent);
			getInstance(builder, SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// Missing relationship
			RelatedResource.Builder builder = getBaseBuilder();
			builder.setRelationship(null);
			getInstance(builder, "relationship attribute must exist.");
			
			// Invalid URI relationship
			builder = getBaseBuilder();
			builder.setRelationship(INVALID_URI);
			getInstance(builder, "Invalid URI");
			
			// Invalid direction
			builder = getBaseBuilder();
			builder.setDirection("veeringLeft");
			getInstance(builder, "The direction attribute must be");
			
			// Missing qualifier
			builder = getBaseBuilder();
			builder.setQualifier(null);
			getInstance(builder, "qualifier attribute must exist.");
			
			// Invalid URI qualifier
			builder = getBaseBuilder();
			builder.setQualifier(INVALID_URI);
			getInstance(builder, "Invalid URI");
			
			// Missing value
			builder = getBaseBuilder();
			builder.setValue(null);
			getInstance(builder, "value attribute must exist.");
			
			// Missing link
			builder = getBaseBuilder();
			builder.getLinks().clear();
			getInstance(builder, "At least 1 link must exist.");
			
			// Null link list
			try {
				new RelatedResource(null,  TEST_RELATIONSHIP, TEST_DIRECTION, TEST_QUALIFIER, TEST_VALUE, null);
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least 1 link");
			}
			
			// Security Attributes on Link
			builder = getBaseBuilder();
			builder.getLinks().get(0).getSecurityAttributes().setClassification("U");
			getInstance(builder, "Security attributes must not be applied");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			RelatedResource component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());

			// Pre-DDMS 4.0.1, too many relatedResource children
			if (!version.isAtLeast("4.0.1")) {
				Element element = new Element(getValidElement(sVersion));
				Element child = Util.buildDDMSElement("RelatedResource", null);
				child.addAttribute(Util.buildDDMSAttribute("qualifier", "ignoreMe"));
				child.addAttribute(Util.buildDDMSAttribute("value", "ignoreMe"));
				child.appendChild(new Element(LinkTest.getFixtureElement()));
				element.appendChild(child);
				component = getInstance(element, SUCCESS);
				assertEquals(1, component.getValidationWarnings().size());
				String text = "A ddms:RelatedResources element contains more than 1";
				String locator = "ddms:relatedResources";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			RelatedResource elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			RelatedResource builderComponent = new RelatedResource.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());
			
			// Different values in each field	
			RelatedResource.Builder builder = getBaseBuilder();
			builder.setRelationship(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setDirection("inbound");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			XLinkAttributes attr = XLinkAttributesTest.getLocatorFixture();
			builder.getLinks().get(1).setXLinkAttributes(new XLinkAttributes.Builder(attr));
			assertFalse(elementComponent.equals(builder.commit()));			
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RelatedResource elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RelatedResource.Builder builder = new RelatedResource.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setQualifier(TEST_QUALIFIER);
			assertFalse(builder.isEmpty());
			
			// Skip empty Links
			builder = new RelatedResource.Builder();
			builder.setDirection(TEST_DIRECTION);
			builder.setRelationship(TEST_RELATIONSHIP);
			builder.setQualifier(TEST_QUALIFIER);
			builder.setValue(TEST_VALUE);
			Link.Builder emptyBuilder = new Link.Builder();
			Link.Builder fullBuilder = new Link.Builder();
			fullBuilder.getXLinkAttributes().setType("locator");
			fullBuilder.getXLinkAttributes().setHref("http://ddmsence.urizone.net/");
			fullBuilder.getXLinkAttributes().setRole("role");
			builder.getLinks().add(emptyBuilder);
			builder.getLinks().add(fullBuilder);
			assertEquals(1, builder.commit().getLinks().size());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			RelatedResource.Builder builder = new RelatedResource.Builder();
			assertNotNull(builder.getLinks().get(1));
		}
	}
}
