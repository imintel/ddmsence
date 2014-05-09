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
package buri.ddmsence.ddms.metacard;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.ContributorTest;
import buri.ddmsence.ddms.resource.CreatorTest;
import buri.ddmsence.ddms.resource.DatesTest;
import buri.ddmsence.ddms.resource.IdentifierTest;
import buri.ddmsence.ddms.resource.PointOfContactTest;
import buri.ddmsence.ddms.resource.ProcessingInfoTest;
import buri.ddmsence.ddms.resource.PublisherTest;
import buri.ddmsence.ddms.resource.RecordsManagementInfoTest;
import buri.ddmsence.ddms.resource.RevisionRecallTest;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.security.NoticeListTest;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.ddms.security.ntk.Access;
import buri.ddmsence.ddms.security.ntk.AccessTest;
import buri.ddmsence.ddms.security.ntk.IndividualTest;
import buri.ddmsence.ddms.summary.DescriptionTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p>
 * Tests related to ddms:metacardInfo elements
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class MetacardInfoTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public MetacardInfoTest() {
		super("metacardInfo.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static MetacardInfo getFixture() {
		try {
			if (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				return (new MetacardInfo(getChildComponents(true), SecurityAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a list of child components for testing.
	 * 
	 * @boolean onlyRequired true to return a minimal set of required components.
	 */
	private static List<IDDMSComponent> getChildComponents(boolean onlyRequired) {
		List<IDDMSComponent> childComponents = new ArrayList<IDDMSComponent>();
		childComponents.add(IdentifierTest.getFixture());
		childComponents.add(DatesTest.getFixture());
		childComponents.add(PublisherTest.getFixture());
		if (!onlyRequired) {
			childComponents.add(ContributorTest.getFixture());
			childComponents.add(CreatorTest.getFixture());
			childComponents.add(PointOfContactTest.getFixture());
			childComponents.add(DescriptionTest.getFixture());
			childComponents.add(ProcessingInfoTest.getFixture());
			childComponents.add(RevisionRecallTest.getTextFixture());
			childComponents.add(RecordsManagementInfoTest.getFixture());
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				childComponents.add(NoticeListTest.getFixture());
				childComponents.add(AccessTest.getFixture());
			}
		}
		return (childComponents);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private MetacardInfo getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		MetacardInfo component = null;
		try {
			component = new MetacardInfo(element);
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
	private MetacardInfo getInstance(MetacardInfo.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		MetacardInfo component = null;
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
	private MetacardInfo.Builder getBaseBuilder() {
		MetacardInfo component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new MetacardInfo.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		for (IDDMSComponent component : getChildComponents(false))
			text.append(((AbstractBaseComponent) component).getOutput(isHTML, "metacardInfo.", ""));
		text.append(buildOutput(isHTML, "metacardInfo.classification", "U"));
		text.append(buildOutput(isHTML, "metacardInfo.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:metacardInfo ").append(getXmlnsDDMS()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ddms:identifier ddms:qualifier=\"URI\" ddms:value=\"urn:buri:ddmsence:testIdentifier\" />");
		xml.append("<ddms:dates ddms:created=\"2003\" />");
		xml.append("<ddms:publisher><ddms:person><ddms:name>Brian</ddms:name>");
		xml.append("<ddms:surname>Uri</ddms:surname></ddms:person></ddms:publisher>");
		xml.append("<ddms:contributor><ddms:service><ddms:name>https://metadata.dod.mil/ebxmlquery/soap</ddms:name>");
		xml.append("</ddms:service></ddms:contributor>");
		xml.append("<ddms:creator><ddms:organization><ddms:name>DISA</ddms:name></ddms:organization></ddms:creator>");
		xml.append("<ddms:pointOfContact><ddms:unknown><ddms:name>UnknownEntity</ddms:name>");
		xml.append("</ddms:unknown></ddms:pointOfContact>");
		xml.append("<ddms:description ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("A transformation service.</ddms:description>");
		xml.append("<ddms:processingInfo ism:classification=\"U\" ism:ownerProducer=\"USA\" ");
		xml.append("ddms:dateProcessed=\"2011-08-19\">");
		xml.append("XSLT Transformation to convert DDMS 2.0 to DDMS 3.1.</ddms:processingInfo>");
		xml.append("<ddms:revisionRecall xmlns:xlink=\"http://www.w3.org/1999/xlink\" ddms:revisionID=\"1\" ");
		xml.append("ddms:revisionType=\"ADMINISTRATIVE RECALL\" ");
		xml.append("xlink:type=\"resource\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">Description of Recall</ddms:revisionRecall>");
		xml.append("<ddms:recordsManagementInfo ddms:vitalRecordIndicator=\"true\">");
		xml.append("<ddms:recordKeeper><ddms:recordKeeperID>#289-99202.9</ddms:recordKeeperID>");
		xml.append("<ddms:organization><ddms:name>DISA</ddms:name></ddms:organization></ddms:recordKeeper>");
		xml.append("<ddms:applicationSoftware ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("IRM Generator 2L-9</ddms:applicationSoftware>");
		xml.append("</ddms:recordsManagementInfo>");
		if ("4.1".equals(DDMSVersion.getCurrentVersion().getVersion())) {
			xml.append("<ddms:noticeList ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ism:Notice ism:noticeType=\"DoD-Dist-B\" ism:noticeReason=\"noticeReason\" ism:noticeDate=\"2011-09-15\" ");
			xml.append("ism:unregisteredNoticeType=\"unregisteredNoticeType\"");
			xml.append(" ism:externalNotice=\"false\"");
			xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ism:NoticeText ism:classification=\"U\" ism:ownerProducer=\"USA\"");
			xml.append(" ism:pocType=\"DoD-Dist-B\">noticeText</ism:NoticeText>");
			xml.append("</ism:Notice></ddms:noticeList>");
			xml.append("<ntk:Access xmlns:ntk=\"urn:us:gov:ic:ntk\" ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ntk:AccessIndividualList>");
			xml.append("<ntk:AccessIndividual ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>");
			xml.append("<ntk:AccessIndividualValue ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("user_2321889:Doe_John_H</ntk:AccessIndividualValue>");
			xml.append("</ntk:AccessIndividual>");
			xml.append("</ntk:AccessIndividualList>");
			xml.append("</ntk:Access>");
		}
		xml.append("</ddms:metacardInfo>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				MetacardInfo.getName(version));
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
			Element element = Util.buildDDMSElement(MetacardInfo.getName(version), null);
			for (IDDMSComponent component : getChildComponents(true))
				element.appendChild(component.getXOMElementCopy());
			MetacardInfo elementComponent = getInstance(element, SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new MetacardInfo.Builder(elementComponent), SUCCESS);

			// Null in component list
			try {
				List<IDDMSComponent> components = getChildComponents(true);
				components.add(null);
				new MetacardInfo(components, null);
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Null component list
			try {
				new MetacardInfo(null, null);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least one");
			}

			// Invalid object in component list
			try {
				List<IDDMSComponent> components = getChildComponents(true);
				components.add(new Rights(true, true, true));
				new MetacardInfo(components, null);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "rights is not a valid");
			}

			// Missing identifier
			MetacardInfo.Builder builder = getBaseBuilder();
			builder.getIdentifiers().clear();
			getInstance(builder, "At least one");

			// Missing publisher
			builder = getBaseBuilder();
			builder.getPublishers().clear();
			if (version.isAtLeast("5.0")) {
				builder.getContributors().clear();
				builder.getCreators().clear();
				builder.getPointOfContacts().clear();
			}
			getInstance(builder, "At least one");

			// Missing dates
			builder = getBaseBuilder();
			builder.setDates(null);
			getInstance(builder, "Exactly 1 dates");
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			if (!"4.1".equals(sVersion)) {
				// No warnings
				MetacardInfo component = getInstance(getValidElement(sVersion), SUCCESS);
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// ntk:Access used
				MetacardInfo component = getInstance(getValidElement(sVersion), SUCCESS);
				assertEquals(2, component.getValidationWarnings().size());
				String text = "The ntk:Access element in this DDMS component";
				String locator = "ddms:metacardInfo";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));

				// ism:externalNotice used
				text = "The ism:externalNotice attribute in this DDMS component";
				locator = "ddms:metacardInfo/ddms:noticeList/ism:Notice";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(1));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			MetacardInfo elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			MetacardInfo builderComponent = new MetacardInfo.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			MetacardInfo.Builder builder = getBaseBuilder();
			builder.getIdentifiers().get(0).setQualifier(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getDates().setApprovedOn("2013-01-01");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getCreators().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getContributors().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getPointOfContacts().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getPublishers().get(0).getPerson().setNames(Util.getXsListAsList("Ellen"));
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setDescription(null);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.getProcessingInfos().clear();
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setRevisionRecall(null);
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setRecordsManagementInfo(null);
			assertFalse(elementComponent.equals(builder.commit()));

			if (!version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.setNoticeList(null);
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// No Access in DDMS 5.0.
		DDMSVersion.setCurrentVersion("5.0");
		MetacardInfo.Builder builder = getBaseBuilder();
		Access access = new Access(IndividualTest.getFixtureList(), null, null, SecurityAttributesTest.getFixture());
		builder.setAccess(new Access.Builder(access));
		getInstance(builder, "The ntk:Access element");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			MetacardInfo elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			MetacardInfo.Builder builder = new MetacardInfo.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());

			builder.getDates().setApprovedOn("2001");
			assertFalse(builder.isEmpty());
		}
	}
}
