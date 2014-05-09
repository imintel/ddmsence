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
package buri.ddmsence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import nu.xom.Element;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.util.DDMSReader;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Base class for DDMSence test cases.
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public abstract class AbstractBaseTestCase extends TestCase {

	private String _type;
	private List<String> _supportedVersions = new ArrayList<String>(DDMSVersion.getSupportedVersions());

	private static Map<String, Element> _elementMap = new HashMap<String, Element>();

	protected static final String TEST_ID = "IDValue";

	protected static final String SUCCESS = "";
	protected static final String INVALID_URI = ":::::";
	protected static final String DIFFERENT_VALUE = "Different";
	protected static final String WRONG_NAME_MESSAGE = "Unexpected namespace URI and local name encountered:";
	protected static final String DEFAULT_DDMS_PREFIX = PropertyReader.getPrefix("ddms");
	protected static final String DEFAULT_GML_PREFIX = PropertyReader.getPrefix("gml");
	protected static final String DEFAULT_ISM_PREFIX = PropertyReader.getPrefix("ism");
	protected static final String DEFAULT_NTK_PREFIX = PropertyReader.getPrefix("ntk");
	protected static final String DEFAULT_TSPI_PREFIX = PropertyReader.getPrefix("tspi");

	/**
	 * Resets the in-use version of DDMS.
	 */
	protected void setUp() throws Exception {
		DDMSVersion.clearCurrentVersion();
	}

	/**
	 * Resets the in-use version of DDMS.
	 */
	protected void tearDown() throws Exception {
		DDMSVersion.clearCurrentVersion();
		PropertyReader.setProperty("output.indexLevel", "0");
	}

	/**
	 * Constructor
	 * 
	 * @param validDocumentFile the filename to load. One of these will be loaded for each supporting version.
	 */
	public AbstractBaseTestCase(String validDocumentFile) {
		_type = validDocumentFile;
		if (validDocumentFile == null)
			return;
		try {
			for (String sVersion : getSupportedVersions()) {
				if (getValidElement(sVersion) == null) {
					DDMSReader reader = new DDMSReader(DDMSVersion.getVersionFor(sVersion));
					File file = new File(PropertyReader.getProperty("test.unit.data") + sVersion, validDocumentFile);
					if (file.exists()) {
						Element element = reader.getElement(file);
						synchronized (_elementMap) {
							_elementMap.put(getType() + ":" + sVersion, element);
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot run tests without valid DDMSReader and valid unit test object.", e);
		}
	}

	/**
	 * Convenience method to fail a test if the wrong error message comes back. This addresses cases where a test is no
	 * longer failing for the reason we expect it to be failing.
	 * 
	 * @param e the exception
	 * @param message the beginning of the expected message (enough to confirm its accuracy).
	 */
	protected void expectMessage(Exception e, String message) {
		if (!e.getMessage().startsWith(message)) {
			System.out.println(DDMSVersion.getCurrentVersion() + ": " + e.getMessage());
			fail("Test failed for the wrong reason.");
		}
	}

	/**
	 * Convenience method to create a XOM element which is not a valid DDMS component because of an incorrect name.
	 */
	protected static Element getWrongNameElementFixture() {
		return (Util.buildDDMSElement("wrongName", null));
	}

	/**
	 * Should be called after a successful constructor execution. If the constructor was expected to fail, but it
	 * succeeded, the test will fail.
	 * 
	 * @param expectFailure true if the constructor was expected to fail.
	 */
	protected static void checkConstructorSuccess(boolean expectFailure) {
		if (expectFailure)
			fail("Constructor allowed invalid data.");
	}

	/**
	 * Should be called after a failed constructor execution. If the constructor was expected to succeed, but it failed,
	 * the test will fail.
	 * 
	 * @param expectFailure true if the constructor was expected to fail.
	 * @param exception the exception that occurred
	 */
	protected static void checkConstructorFailure(boolean expectFailure, InvalidDDMSException exception) {
		if (!expectFailure)
			fail("Constructor failed on valid data: " + exception.getMessage());
	}

	/**
	 * Helper method to confirm that a warning message is correct.
	 * 
	 * @param text the text of the message
	 * @param locator the locator text of the message
	 * @param message the ValidationMessage to test
	 */
	protected void assertWarningEquality(String text, String locator, ValidationMessage message) {
		if (locator != "")
			locator = "/" + locator;
		assertTrue(ValidationMessage.WARNING_TYPE.equals(message.getType()));
		assertTrue(locator.equals(message.getLocator()));
		assertTrue(message.getText().startsWith(text));
	}

	/**
	 * Helper method to confirm that a warning message is correct.
	 * 
	 * @param text the text of the message
	 * @param locator the locator text of the message
	 * @param message the ValidationMessage to test
	 */
	protected void assertErrorEquality(String text, String locator, ValidationMessage message) {
		if (locator != "")
			locator = "/" + locator;
		assertTrue(ValidationMessage.ERROR_TYPE.equals(message.getType()));
		assertTrue(locator.equals(message.getLocator()));
		assertTrue(message.getText().startsWith(text));
	}

	/**
	 * Shared method for testing the name and namespace of a created component.
	 * 
	 * @param component the component to test
	 * @param prefix the expected XML prefix
	 * @param name the expected XML local name
	 */
	protected void assertNameAndNamespace(IDDMSComponent component, String prefix, String name) {
		assertEquals(name, component.getName());
		assertEquals(prefix, component.getPrefix());
		assertEquals(prefix + ":" + name, component.getQualifiedName());
	}

	/**
	 * Convenience method to build a meta tag for HTML output or a text line for Text output.
	 * 
	 * @param isHTML true for HTML, false for Text
	 * @param name the name value of the meta tag (will be escaped in HTML)
	 * @param content the content value of the meta tag (will be escaped in HTML)
	 * @return a string containing the output
	 */
	public static String buildOutput(boolean isHTML, String name, String content) {
		StringBuffer tag = new StringBuffer();
		tag.append(isHTML ? "<meta name=\"" : "");
		tag.append(isHTML ? Util.xmlEscape(name) : name);
		tag.append(isHTML ? "\" content=\"" : ": ");
		tag.append(isHTML ? Util.xmlEscape(content) : content);
		tag.append(isHTML ? "\" />\n" : "\n");
		return (tag.toString());
	}

	/**
	 * Returns a namespace declaration for DDMS
	 */
	protected static String getXmlnsDDMS() {
		return ("xmlns:ddms=\"" + DDMSVersion.getCurrentVersion().getNamespace() + "\"");
	}

	/**
	 * Returns a namespace declaration for ISM
	 */
	protected static String getXmlnsISM() {
		return ("xmlns:ism=\"" + DDMSVersion.getCurrentVersion().getIsmNamespace() + "\"");
	}

	/**
	 * Returns a namespace declaration for GML
	 */
	protected static String getXmlnsGML() {
		return ("xmlns:gml=\"" + DDMSVersion.getCurrentVersion().getGmlNamespace() + "\"");
	}

	/**
	 * Returns a namespace declaration for NTK
	 */
	protected static String getXmlnsNTK() {
		return ("xmlns:ntk=\"" + DDMSVersion.getCurrentVersion().getNtkNamespace() + "\"");
	}

	/**
	 * Returns a namespace declaration for virt
	 */
	protected static String getXmlnsVirt() {
		return ("xmlns:virt=\"" + DDMSVersion.getCurrentVersion().getVirtNamespace() + "\"");
	}

	/**
	 * Accessor for a valid DDMS XOM Element constructed from the root element of an XML file, which can be used in
	 * testing as a "correct base case".
	 */
	public Element getValidElement(String version) {
		return (_elementMap.get(getType() + ":" + version));
	}

	/**
	 * Removes specific versions, so that components will only be tested in supported versions.
	 * 
	 * @param xsList an xs:list containing the unsupported version numbers
	 */
	protected void removeSupportedVersions(String xsList) {
		List<String> unsupportedVersions = Util.getXsListAsList(xsList);
		getSupportedVersions().removeAll(unsupportedVersions);
	}

	/**
	 * Accessor for the local identifier for the type of component being tested
	 */
	private String getType() {
		return (_type);
	}

	/**
	 * Accessor for the supported versions for this specific component
	 */
	protected List<String> getSupportedVersions() {
		return (_supportedVersions);
	}
}
