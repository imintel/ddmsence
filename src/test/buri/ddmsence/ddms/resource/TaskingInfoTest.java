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

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.ddms.summary.DescriptionTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:taskingInfo elements </p>
 * 
 * <p> Because a ddms:taskingInfo is a local component, we cannot load a valid document from a unit test data file. We
 * have to build the well-formed Element ourselves. </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class TaskingInfoTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public TaskingInfoTest() {
		super(null);
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Element getFixtureElement() {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(TaskingInfo.getName(version), null);
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ddms"), version.getNamespace());
			element.addNamespaceDeclaration(PropertyReader.getPrefix("ism"), version.getIsmNamespace());
			SecurityAttributesTest.getFixture().addTo(element);
			element.appendChild(RequesterInfoTest.getFixtureElement(true));
			element.appendChild(AddresseeTest.getFixtureElement(true));
			element.appendChild(DescriptionTest.getFixture().getXOMElementCopy());
			element.appendChild(TaskIDTest.getFixtureElementNoNetwork());
			return (element);
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<TaskingInfo> getFixtureList() {
		try {
			List<TaskingInfo> infos = new ArrayList<TaskingInfo>();
			infos.add(new TaskingInfo(getFixtureElement()));
			return (infos);
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
	private TaskingInfo getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TaskingInfo component = null;
		try {
			component = new TaskingInfo(element);
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
	private TaskingInfo getInstance(TaskingInfo.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		TaskingInfo component = null;
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
	private TaskingInfo.Builder getBaseBuilder() {
		TaskingInfo component = getInstance(getFixtureElement(), SUCCESS);
		return (new TaskingInfo.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, "taskingInfo.requesterInfo.entityType", "organization"));
		text.append(buildOutput(isHTML, "taskingInfo.requesterInfo.name", "DISA"));
		text.append(buildOutput(isHTML, "taskingInfo.requesterInfo.classification", "U"));
		text.append(buildOutput(isHTML, "taskingInfo.requesterInfo.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "taskingInfo.addressee.entityType", "organization"));
		text.append(buildOutput(isHTML, "taskingInfo.addressee.name", "DISA"));
		text.append(buildOutput(isHTML, "taskingInfo.addressee.classification", "U"));
		text.append(buildOutput(isHTML, "taskingInfo.addressee.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "taskingInfo.description", "A transformation service."));
		text.append(buildOutput(isHTML, "taskingInfo.description.classification", "U"));
		text.append(buildOutput(isHTML, "taskingInfo.description.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID", "Task #12345"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.taskingSystem", "MDR"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.type", "simple"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.href", "http://en.wikipedia.org/wiki/Tank"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.role", "tank"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.title", "Tank Page"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.arcrole", "arcrole"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.show", "new"));
		text.append(buildOutput(isHTML, "taskingInfo.taskID.actuate", "onLoad"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:taskingInfo ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:requesterInfo ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization>");
		xml.append("</ddms:requesterInfo>");
		xml.append("<ddms:addressee ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization>");
		xml.append("</ddms:addressee>");
		xml.append("<ddms:description ism:classification=\"U\" ism:ownerProducer=\"USA\">A transformation service.</ddms:description>");
		xml.append("<ddms:taskID ");
		xml.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		xml.append("ddms:taskingSystem=\"MDR\" ");
		xml.append("xlink:type=\"simple\" ");
		xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:arcrole=\"arcrole\" ");
		xml.append("xlink:show=\"new\" xlink:actuate=\"onLoad\">Task #12345</ddms:taskID>");
		xml.append("</ddms:taskingInfo>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getFixtureElement(), SUCCESS), DEFAULT_DDMS_PREFIX,
				TaskingInfo.getName(version));
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
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields
			Element element = Util.buildDDMSElement(TaskingInfo.getName(version), null);
			SecurityAttributesTest.getFixture().addTo(element);
			element.appendChild(TaskIDTest.getFixture().getXOMElementCopy());
			TaskingInfo elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields
			getInstance(new TaskingInfo.Builder(elementComponent), SUCCESS);
			
			// Null list parameters
			try {
				new TaskingInfo(null, null, null, TaskIDTest.getFixture(), SecurityAttributesTest.getFixture());
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}
	
	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing taskID
			TaskingInfo.Builder builder = getBaseBuilder();
			builder.setTaskID(null);
			getInstance(builder, "taskID must exist.");

			// Missing security attributes
			builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			TaskingInfo component = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			TaskingInfo elementComponent = getInstance(getFixtureElement(), SUCCESS);
			TaskingInfo builderComponent = new TaskingInfo.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field
			TaskingInfo.Builder builder = getBaseBuilder();
			builder.getRequesterInfos().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getAddressees().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setDescription(null);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getTaskID().setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));			
		}
	}
	
	public void testVersionSpecific() throws InvalidDDMSException {
		// Implicit because TaskID cannot be instantiated before 4.0.1.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			TaskingInfo elementComponent = getInstance(getFixtureElement(), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}
	
	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			TaskingInfo.Builder builder = new TaskingInfo.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getRequesterInfos().get(1).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
			
			builder = new TaskingInfo.Builder();
			builder.getAddressees().get(1).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
		}
	}
}
