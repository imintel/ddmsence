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
package buri.ddmsence.ddms.summary.xlink;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to the XLINK attributes on ddms:link and ddms:taskID elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class XLinkAttributesTest extends AbstractBaseTestCase {

	private static final String TEST_HREF = "http://en.wikipedia.org/wiki/Tank";
	private static final String TEST_ROLE = "tank";
	private static final String TEST_TITLE = "Tank Page";
	private static final String TEST_LABEL = "tank";
	private static final String TEST_ARCROLE = "arcrole";
	private static final String TEST_SHOW = "new";
	private static final String TEST_ACTUATE = "onLoad";

	/**
	 * Constructor
	 */
	public XLinkAttributesTest() {
		super(null);
	}

	/**
	 * Returns a fixture object for testing. The type will be "locator".
	 */
	public static XLinkAttributes getLocatorFixture() {
		try {
			return (new XLinkAttributes(TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_LABEL));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing. The type will be "simple".
	 */
	public static XLinkAttributes getSimpleFixture() {
		try {
			return (new XLinkAttributes(TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_ARCROLE, TEST_SHOW, TEST_ACTUATE));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing. The type will be "resource".
	 */
	public static XLinkAttributes getResourceFixture() {
		try {
			return (new XLinkAttributes(TEST_ROLE, TEST_TITLE, TEST_LABEL));
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
	private XLinkAttributes getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		XLinkAttributes attributes = null;
		try {
			attributes = new XLinkAttributes(element);
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (attributes);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param builder the builder to commit
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private XLinkAttributes getInstance(XLinkAttributes.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		XLinkAttributes component = null;
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
	 * Returns a builder, pre-populated with base data from the test attribute.
	 * 
	 * This builder can then be modified to test various conditions.
	 */
	private XLinkAttributes.Builder getBaseBuilder(XLinkAttributes xlinkFixture) {
		return (new XLinkAttributes.Builder(xlinkFixture));
	}
	
	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML, String type) {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "type", type));
		if (!"resource".equals(type))
			text.append(buildOutput(isHTML, "href", TEST_HREF));
		text.append(buildOutput(isHTML, "role", TEST_ROLE));
		text.append(buildOutput(isHTML, "title", TEST_TITLE));
		if (!"simple".equals(type))
			text.append(buildOutput(isHTML, "label", TEST_LABEL));
		if ("simple".equals(type)) {
			text.append(buildOutput(isHTML, "arcrole", TEST_ARCROLE));
			text.append(buildOutput(isHTML, "show", TEST_SHOW));
			text.append(buildOutput(isHTML, "actuate", TEST_ACTUATE));
		}
		return (text.toString());
	}

	/**
	 * Helper method to add attributes to a XOM element. The element is not validated.
	 * 
	 * @param element element
	 * @param href the link href (optional)
	 * @param role the role attribute (optional)
	 * @param title the link title (optional)
	 * @param arcrole the arcrole attribute (optional)
	 * @param show the show token (optional)
	 * @param actuate the actuate token (optional)
	 */
	private void addAttributes(Element element, String href, String role, String title, String arcrole, String show,
		String actuate) {
		String xlinkPrefix = PropertyReader.getPrefix("xlink");
		String xlinkNamespace = DDMSVersion.getCurrentVersion().getXlinkNamespace();
		Util.addAttribute(element, xlinkPrefix, "type", xlinkNamespace, "simple");
		Util.addAttribute(element, xlinkPrefix, "href", xlinkNamespace, href);
		Util.addAttribute(element, xlinkPrefix, "role", xlinkNamespace, role);
		Util.addAttribute(element, xlinkPrefix, "title", xlinkNamespace, title);
		Util.addAttribute(element, xlinkPrefix, "arcrole", xlinkNamespace, arcrole);
		Util.addAttribute(element, xlinkPrefix, "show", xlinkNamespace, show);
		Util.addAttribute(element, xlinkPrefix, "actuate", xlinkNamespace, actuate);
	}

	/**
	 * Helper method to add attributes to a XOM element. The element is not validated.
	 * 
	 * @param element element
	 * @param href the link href (optional)
	 * @param role the role attribute (optional)
	 * @param title the link title (optional)
	 * @param label the name of the link (optional)
	 */
	private void addAttributes(Element element, String href, String role, String title, String label) {
		String xlinkPrefix = PropertyReader.getPrefix("xlink");
		String xlinkNamespace = DDMSVersion.getCurrentVersion().getXlinkNamespace();
		Util.addAttribute(element, xlinkPrefix, "type", xlinkNamespace, "locator");
		Util.addAttribute(element, xlinkPrefix, "href", xlinkNamespace, href);
		Util.addAttribute(element, xlinkPrefix, "role", xlinkNamespace, role);
		Util.addAttribute(element, xlinkPrefix, "title", xlinkNamespace, title);
		Util.addAttribute(element, xlinkPrefix, "label", xlinkNamespace, label);
	}

	/**
	 * Helper method to add attributes to a XOM element. The element is not validated.
	 * 
	 * @param element element
	 * @param href the link href (optional)
	 * @param role the role attribute (optional)
	 * @param title the link title (optional)
	 * @param label the name of the link (optional)
	 */
	private void addAttributes(Element element, String role, String title, String label) {
		String xlinkPrefix = PropertyReader.getPrefix("xlink");
		String xlinkNamespace = DDMSVersion.getCurrentVersion().getXlinkNamespace();
		Util.addAttribute(element, xlinkPrefix, "type", xlinkNamespace, "resource");
		Util.addAttribute(element, xlinkPrefix, "role", xlinkNamespace, role);
		Util.addAttribute(element, xlinkPrefix, "title", xlinkNamespace, title);
		Util.addAttribute(element, xlinkPrefix, "label", xlinkNamespace, label);
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			
			// All fields (locator)
			Element element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_LABEL);
			XLinkAttributes elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new XLinkAttributes.Builder(elementComponent), SUCCESS);

			// All fields (simple)
			element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_ARCROLE, TEST_SHOW, TEST_ACTUATE);
			elementComponent = getInstance(element, SUCCESS);

			getInstance(new XLinkAttributes.Builder(elementComponent), SUCCESS);
			
			// All fields (resource)
			element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_ROLE, TEST_TITLE, TEST_LABEL);
			elementComponent = getInstance(element, SUCCESS);

			getInstance(new XLinkAttributes.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No optional fields (all)
			Element element = Util.buildDDMSElement("link", null);
			XLinkAttributes elementComponent = getInstance(element, SUCCESS);
			
			getInstance(new XLinkAttributes.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// href is not valid URI
			XLinkAttributes.Builder builder = getBaseBuilder(getLocatorFixture());
			builder.setHref(INVALID_URI);
			getInstance(builder, "Invalid URI");

			// role is not valid URI
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder(getLocatorFixture());
				builder.setRole(INVALID_URI);
				getInstance(builder, "Invalid URI");
			}

			// label is not valid NCName
			if (version.isAtLeast("4.0.1")) {
				builder = getBaseBuilder(getResourceFixture());
				builder.setLabel("ddms:prefix& GML");
				getInstance(builder, "\"ddms:prefix& GML\" is not a valid NCName.");
			}

			// invalid arcrole
			builder = getBaseBuilder(getSimpleFixture());
			builder.setArcrole(INVALID_URI);
			getInstance(builder, "Invalid URI");

			// invalid show
			builder = getBaseBuilder(getSimpleFixture());
			builder.setShow("notInTheTokenList");
			getInstance(builder, "The show attribute must be");
			
			// invalid actuate
			builder = getBaseBuilder(getSimpleFixture());
			builder.setActuate("notInTheTokenList");
			getInstance(builder, "The actuate attribute must be");
		}
	}

	public void testValidationWarnings() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Element element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_LABEL);
			XLinkAttributes component = getInstance(element, SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality, locator version
			Element element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_LABEL);
			XLinkAttributes elementAttributes = getInstance(element, SUCCESS);
			XLinkAttributes builderAttributes = getInstance(new XLinkAttributes.Builder(elementAttributes), SUCCESS); 
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());

			// Different values in each field, locator version
			XLinkAttributes.Builder builder = getBaseBuilder(getLocatorFixture());
			builder.setHref(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getLocatorFixture());
			builder.setRole(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getLocatorFixture());
			builder.setTitle(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getLocatorFixture());
			builder.setLabel(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			// Base equality, simple version
			element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_HREF, TEST_ROLE, TEST_TITLE, TEST_ARCROLE, TEST_SHOW, TEST_ACTUATE);
			elementAttributes = getInstance(element, SUCCESS);
			builderAttributes = getInstance(new XLinkAttributes.Builder(elementAttributes), SUCCESS);
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());

			// Different values in each field, simple version
			builder = getBaseBuilder(getSimpleFixture());
			builder.setHref(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getSimpleFixture());
			builder.setRole(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getSimpleFixture());
			builder.setTitle(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getSimpleFixture());
			builder.setArcrole(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getSimpleFixture());
			builder.setShow("embed");
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getSimpleFixture());
			builder.setActuate("none");
			assertFalse(elementAttributes.equals(builder.commit()));
			
			// Base equality, resource version
			element = Util.buildDDMSElement("link", null);
			addAttributes(element, TEST_ROLE, TEST_TITLE, TEST_LABEL);
			elementAttributes = getInstance(element, SUCCESS);
			builderAttributes = getInstance(new XLinkAttributes.Builder(elementAttributes), SUCCESS);
			assertEquals(elementAttributes, builderAttributes);
			assertEquals(elementAttributes.hashCode(), builderAttributes.hashCode());

			// Different values in each field, resource version
			builder = getBaseBuilder(getResourceFixture());
			builder.setRole(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getResourceFixture());
			builder.setTitle(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			builder = getBaseBuilder(getResourceFixture());
			builder.setLabel(DIFFERENT_VALUE);
			assertFalse(elementAttributes.equals(builder.commit()));
			
			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementAttributes.equals(wrongComponent));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			XLinkAttributes attributes = getLocatorFixture();
			assertEquals(getExpectedOutput(true, "locator"), attributes.getOutput(true, ""));
			assertEquals(getExpectedOutput(false, "locator"), attributes.getOutput(false, ""));

			attributes = getSimpleFixture();
			assertEquals(getExpectedOutput(true, "simple"), attributes.getOutput(true, ""));
			assertEquals(getExpectedOutput(false, "simple"), attributes.getOutput(false, ""));

			attributes = getResourceFixture();
			assertEquals(getExpectedOutput(true, "resource"), attributes.getOutput(true, ""));
			assertEquals(getExpectedOutput(false, "resource"), attributes.getOutput(false, ""));
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			XLinkAttributes.Builder builder = new XLinkAttributes.Builder();
			assertNotNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.setLabel(TEST_LABEL);
			assertFalse(builder.isEmpty());

			// An untyped instance
			XLinkAttributes output = builder.commit();
			assertTrue(Util.isEmpty(output.getType()));
		}
	}
	
	public void testAddTo() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			XLinkAttributes component = getLocatorFixture();

			Element element = Util.buildDDMSElement("sample", null);
			component.addTo(element);
			XLinkAttributes output = new XLinkAttributes(element);
			assertEquals(component, output);
		}
	}

	public void testGetNonNull() throws InvalidDDMSException {
		XLinkAttributes component = new XLinkAttributes();
		XLinkAttributes output = XLinkAttributes.getNonNullInstance(null);
		assertEquals(component, output);

		output = XLinkAttributes.getNonNullInstance(getLocatorFixture());
		assertEquals(getLocatorFixture(), output);
	}
}
