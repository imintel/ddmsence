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
package buri.ddmsence.ddms.resource;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:resourceManagement elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ResourceManagementTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public ResourceManagementTest() {
		super("resourceManagement.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static ResourceManagement getFixture() {
		try {
			return (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? new ResourceManagement(null, null, null,
				ProcessingInfoTest.getFixtureList(), SecurityAttributesTest.getFixture()) : null);
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
	private ResourceManagement getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ResourceManagement component = null;
		try {
			component = new ResourceManagement(element);
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
	private ResourceManagement getInstance(ResourceManagement.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		ResourceManagement component = null;
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
	private ResourceManagement.Builder getBaseBuilder() {
		ResourceManagement component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new ResourceManagement.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(RecordsManagementInfoTest.getFixture().getOutput(isHTML, "resourceManagement.", ""));
		text.append(RevisionRecallTest.getTextFixture().getOutput(isHTML, "resourceManagement.", ""));
		text.append(TaskingInfoTest.getFixtureList().get(0).getOutput(isHTML, "resourceManagement.", ""));
		text.append(ProcessingInfoTest.getFixtureList().get(0).getOutput(isHTML, "resourceManagement.", ""));
		text.append(buildOutput(isHTML, "resourceManagement.classification", "U"));
		text.append(buildOutput(isHTML, "resourceManagement.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:resourceManagement ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:recordsManagementInfo ddms:vitalRecordIndicator=\"true\">");
		xml.append("<ddms:recordKeeper><ddms:recordKeeperID>#289-99202.9</ddms:recordKeeperID>");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization></ddms:recordKeeper>");
		xml.append("<ddms:applicationSoftware ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("IRM Generator 2L-9</ddms:applicationSoftware>");
		xml.append("</ddms:recordsManagementInfo>");
		xml.append("<ddms:revisionRecall xmlns:xlink=\"http://www.w3.org/1999/xlink\" ddms:revisionID=\"1\" ");
		xml.append("ddms:revisionType=\"ADMINISTRATIVE RECALL\" ");
		xml.append("xlink:type=\"resource\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">Description of Recall</ddms:revisionRecall>");
		xml.append("<ddms:taskingInfo ism:classification=\"U\" ism:ownerProducer=\"USA\"><ddms:requesterInfo ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization></ddms:requesterInfo>");
		xml.append("<ddms:addressee ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization></ddms:addressee>");
		xml.append("<ddms:description ism:classification=\"U\" ism:ownerProducer=\"USA\">A transformation service.");
		xml.append("</ddms:description><ddms:taskID xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		xml.append("ddms:taskingSystem=\"MDR\" xlink:type=\"simple\" ");
		xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"tank\" xlink:title=\"Tank Page\" ");
		xml.append("xlink:arcrole=\"arcrole\" xlink:show=\"new\" xlink:actuate=\"onLoad\">Task #12345</ddms:taskID>");
		xml.append("</ddms:taskingInfo><ddms:processingInfo ism:classification=\"U\" ism:ownerProducer=\"USA\" ");
		xml.append("ddms:dateProcessed=\"2011-08-19\">XSLT Transformation to convert DDMS 2.0 to DDMS 3.1.");
		xml.append("</ddms:processingInfo></ddms:resourceManagement>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				ResourceManagement.getName(version));
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

			// Element-based, no optional fields
			Element element = Util.buildDDMSElement(ResourceManagement.getName(version), null);
			ResourceManagement elementComponent = getInstance(element, SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new ResourceManagement.Builder(elementComponent), SUCCESS);
			
			// Null list parameters
			try {
				new ResourceManagement(null, null, null, null, null);
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		// No tests.
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			ResourceManagement component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			ResourceManagement elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			ResourceManagement builderComponent = new ResourceManagement.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			ResourceManagement.Builder builder = getBaseBuilder();
			builder.setRecordsManagementInfo(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setRevisionRecall(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getTaskingInfos().clear();			
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getProcessingInfos().clear();			
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		ResourceManagement.Builder builder = new ResourceManagement.Builder();
		builder.getSecurityAttributes().setClassification("U");
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The resourceManagement element must not ");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ResourceManagement elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			ResourceManagement.Builder builder = new ResourceManagement.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getTaskingInfos().get(1).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
			
			builder = new ResourceManagement.Builder();
			builder.getProcessingInfos().get(1).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
		}
	}
}
