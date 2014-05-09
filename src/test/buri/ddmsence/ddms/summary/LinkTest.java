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
import buri.ddmsence.ddms.summary.xlink.XLinkAttributes;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:link elements </p>
 * 
 * <p> Because a ddms:link is a local component, we cannot load a valid document from a unit test data file. We have to
 * build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class LinkTest extends AbstractBaseTestCase {

	private static final String TEST_TYPE = "locator";
	private static final String TEST_HREF = "http://en.wikipedia.org/wiki/Tank";
	private static final String TEST_ROLE = "tank";;

	/**
	 * Constructor
	 */
	public LinkTest() {
		super(null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element linkElement = Util.buildDDMSElement(Link.getName(version), null);
			linkElement.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			XLinkAttributesTest.getLocatorFixture().addTo(linkElement);
			return (linkElement);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param hasSecurity true for security attributes
	 */
	public static List<Link> getLocatorFixtureList(boolean hasSecurity) {
		List<Link> links = new ArrayList<Link>();
		links.add(getLocatorFixture(hasSecurity));
		return (links);
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param hasSecurity true for security attributes
	 */
	public static Link getLocatorFixture(boolean hasSecurity) {
		try {
			return (new Link(XLinkAttributesTest.getLocatorFixture(), hasSecurity ? SecurityAttributesTest.getFixture()
				: null));
		}
		catch (InvalidDDMSException e) {
			e.printStackTrace();
			fail("Could not create fixture.");
		}
		return (null);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param element the element to build from
	 * @return a valid object
	 */
	private Link getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Link component = null;
		try {
			component = new Link(element);
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
	private Link getInstance(Link.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Link component = null;
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
	private Link.Builder getBaseBuilder() {
		Link component = getInstance(getFixtureElement(), SUCCESS);
		return (new Link.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(XLinkAttributesTest.getLocatorFixture().getOutput(isHTML, "link."));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:link ").append(getXmlnsDDMS()).append(" ");
		xml.append("xmlns:xlink=\"").append(version.getXlinkNamespace()).append("\" ");
		xml.append("xlink:type=\"locator\" xlink:href=\"http://en.wikipedia.org/wiki/Tank\" ");
		xml.append("xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" />");
		return (xml.toString());
	}

	/**
	 * Helper method to create a XOM element that can be used to test element constructors
	 * 
	 * @param type the type
	 * @param href the href
	 * @return Element
	 */
	private Element buildComponentElement(String type, String href) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = Util.buildDDMSElement(Link.getName(version), null);
		String xlinkPrefix = PropertyReader.getPrefix("xlink");
		String xlinkNamespace = version.getXlinkNamespace();
		if (type != null)
			element.addAttribute(Util.buildAttribute(xlinkPrefix, "type", xlinkNamespace, type));
		if (href != null)
			element.addAttribute(Util.buildAttribute(xlinkPrefix, "href", xlinkNamespace, href));
		element.addAttribute(Util.buildAttribute(xlinkPrefix, "role", xlinkNamespace, TEST_ROLE));
		return (element);
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				Link.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getFixtureElement(), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = buildComponentElement(TEST_TYPE, TEST_HREF);
			Link elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			getInstance(new Link.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing href
			Element element = buildComponentElement(TEST_TYPE, null);
			getInstance(element, "href attribute must exist.");

			// invalid type
			element = buildComponentElement("simple", TEST_HREF);
			getInstance(element, "The type attribute must have a fixed value");
			
			// Null attributes
			try {
				new Link((XLinkAttributes) null);
				fail("Constructor allowed invalid data.");
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "type attribute must exist.");
			}
			
			// Missing href
			Link.Builder builder = getBaseBuilder();
			builder.getXLinkAttributes().setHref(null);
			getInstance(builder, "href attribute must exist");
			
			// Invalid type
			builder = getBaseBuilder();
			builder.getXLinkAttributes().setType("simple");
			getInstance(builder, "The type attribute must have a fixed value");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// No warnings
			Link component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Link elementComponent = getInstance(getFixtureElement(), SUCCESS);
			Link builderComponent = new Link.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			Link.Builder builder = getBaseBuilder();
			builder.getXLinkAttributes().setHref(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}
	
	public void testVersionSpecific() {
		// No tests.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Link elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Link.Builder builder = new Link.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getXLinkAttributes().setRole(TEST_ROLE);
			assertFalse(builder.isEmpty());
		}
	}
}
