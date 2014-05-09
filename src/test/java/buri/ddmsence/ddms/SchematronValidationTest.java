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
package buri.ddmsence.ddms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.xslt.XSLException;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.resource.Person;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;

/**
 * <p> Tests related to Schematron validation of Resources </p>
 * 
 * @author Brian Uri!
 * @since 1.3.1
 */
public class SchematronValidationTest extends AbstractBaseTestCase {

	private Map<String, Resource> versionToResourceMap;

	/**
	 * Constructor
	 */
	public SchematronValidationTest() throws InvalidDDMSException {
		super("resource.xml");
		versionToResourceMap = new HashMap<String, Resource>();
		for (String sVersion : getSupportedVersions()) {
			versionToResourceMap.put(sVersion, new Resource(getValidElement(sVersion)));
		}
	}

	public void testSchematronValidationXslt1() throws InvalidDDMSException, IOException, XSLException {
		List<String> supportedXslt1Processors = new ArrayList<String>();
		if (System.getProperty("java.version").indexOf("1.5.0") == -1)
			supportedXslt1Processors.add("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		supportedXslt1Processors.add("net.sf.saxon.TransformerFactoryImpl");
		for (String processor : supportedXslt1Processors) {
			PropertyReader.setProperty("xml.transform.TransformerFactory", processor);
			for (String sVersion : getSupportedVersions()) {
				DDMSVersion version = DDMSVersion.getVersionFor(sVersion);
				Resource resource = versionToResourceMap.get(sVersion);
				String ddmsNamespace = resource.getNamespace();
				String resourceName = Resource.getName(version);
				List<ValidationMessage> messages = resource.validateWithSchematron(new File("data/test/" + sVersion
					+ "/testSchematronXslt1.sch"));
				assertEquals(version.isAtLeast("4.0.1") ? 3 : 2, messages.size());

				String text = "A DDMS Resource must have an unknownElement child.";
				String locator = "/*[local-name()='" + resourceName + "' and namespace-uri()='" + ddmsNamespace + "']";
				assertErrorEquality(text, locator, messages.get(0));

				int originalWarningIndex = version.isAtLeast("4.0.1") ? 2 : 1;

				text = "Members of the Uri family cannot be publishers.";
				locator = "/*[local-name()='" + resourceName + "' and namespace-uri()='" + ddmsNamespace + "']"
					+ "/*[local-name()='publisher' and namespace-uri()='" + ddmsNamespace + "']" + "/*[local-name()='"
					+ Person.getName(version) + "' and namespace-uri()='" + ddmsNamespace + "']"
					+ "/*[local-name()='surname' and namespace-uri()='" + ddmsNamespace + "']";
				assertWarningEquality(text, locator, messages.get(originalWarningIndex));

				if (version.isAtLeast("4.0.1")) {
					text = "Members of the Uri family cannot be publishers.";
					locator = "/*[local-name()='" + resourceName + "' and namespace-uri()='" + ddmsNamespace + "']"
						+ "/*[local-name()='metacardInfo' and namespace-uri()='" + ddmsNamespace + "']"
						+ "/*[local-name()='publisher' and namespace-uri()='" + ddmsNamespace + "']"
						+ "/*[local-name()='" + Person.getName(version) + "' and namespace-uri()='" + ddmsNamespace
						+ "']" + "/*[local-name()='surname' and namespace-uri()='" + ddmsNamespace + "']";
					assertWarningEquality(text, locator, messages.get(1));
				}
			}
		}
	}

	public void testSchematronValidationXslt2() throws InvalidDDMSException, IOException, XSLException {
		String[] supportedXslt1Processors = new String[] { "net.sf.saxon.TransformerFactoryImpl" };
		for (String processor : supportedXslt1Processors) {
			PropertyReader.setProperty("xml.transform.TransformerFactory", processor);
			for (String sVersion : getSupportedVersions()) {
				DDMSVersion version = DDMSVersion.getVersionFor(sVersion);
				
				Resource resource = versionToResourceMap.get(sVersion);
				String ddmsNamespace = resource.getNamespace();
				String gmlNamespace = version.getGmlNamespace();
				String tspiNamespace = version.getTspiNamespace();
				List<ValidationMessage> messages = resource.validateWithSchematron(new File("data/test/" + sVersion
					+ "/testSchematronXslt2.sch"));
				assertEquals(1, messages.size());

				String text = "The second coordinate in a gml:pos element must be 40.2 degrees.";
				String extent = version.isAtLeast("4.0.1") ? "" : "/*:GeospatialExtent[namespace-uri()='"
					+ ddmsNamespace + "'][1]";
				String resourceName = Resource.getName(version);
				String pointNamespace = (!version.isAtLeast("5.0") ? gmlNamespace : tspiNamespace); 
				String locator = "/*:" + resourceName + "[namespace-uri()='" + ddmsNamespace + "'][1]"
					+ "/*:geospatialCoverage[namespace-uri()='" + ddmsNamespace + "'][1]" + extent
					+ "/*:boundingGeometry[namespace-uri()='" + ddmsNamespace + "'][1]"
					+ "/*:Point[namespace-uri()='" + pointNamespace + "'][1]" + "/*:pos[namespace-uri()='" + gmlNamespace + "'][1]";
				assertErrorEquality(text, locator, messages.get(0));
			}
		}
	}

	public void testSchematronValidationInvalid() throws InvalidDDMSException, IOException, XSLException {
		String[] supportedXslt1Processors = new String[] { "net.sf.saxon.TransformerFactoryImpl" };
		for (String processor : supportedXslt1Processors) {
			PropertyReader.setProperty("xml.transform.TransformerFactory", processor);
			for (String sVersion : getSupportedVersions()) {
				Resource resource = versionToResourceMap.get(sVersion);
				try {
					resource.validateWithSchematron(new File("data/test/" + sVersion + "/testSchematronInvalid.sch"));
				}
				catch (IllegalArgumentException e) {
					expectMessage(e, "DDMSence currently only supports Schematron files with a queryBinding attribute");
				}
			}
		}
	}

	// public void testIsmXmlV5SchematronValidation() throws SAXException, InvalidDDMSException, IOException,
	// XSLException {
	// // For this test to work, the ISM.XML V5 distribution must be unpacked in the data directory.
	// File schematronFile = new File("ISM_XML.sch");
	// Resource resource = new DDMSReader().getDDMSResource(new File("data/sample/DDMSence_Example_v3_1.xml"));
	// List<ValidationMessage> messages = resource.validateWithSchematron(schematronFile);
	// for (ValidationMessage message : messages) {
	// System.out.println("Location: " + message.getLocator());
	// System.out.println("Message: " + message.getText());
	// }
	// }
}
