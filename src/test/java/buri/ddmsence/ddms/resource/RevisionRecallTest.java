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
import buri.ddmsence.ddms.summary.LinkTest;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:revisionRecall elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class RevisionRecallTest extends AbstractBaseTestCase {

	private static final Integer TEST_REVISION_ID = Integer.valueOf(1);
	private static final String TEST_REVISION_TYPE = "ADMINISTRATIVE RECALL";
	private static final String TEST_VALUE = "Description of Recall";
	private static final String TEST_OTHER_NETWORK = "PBS";

	/**
	 * Constructor
	 */
	public RevisionRecallTest() {
		super("revisionRecall.xml");
		removeSupportedVersions("2.0 3.0 3.1");
	}

	/**
	 * Returns a fixture object for testing.
	 * 
	 * @param stripNetworks true if the network attributes should be removed.
	 */
	public static Element getTextFixtureElement(boolean stripNetworks) {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = new Element(new RevisionRecallTest().getValidElement(version.getVersion()));
		if (stripNetworks) {
			if (!version.isAtLeast("5.0")) {
				element.removeAttribute(element.getAttribute("network"));
				element.removeAttribute(element.getAttribute("otherNetwork"));
			}
			else {
				element.removeAttribute(element.getAttribute("network", "urn:us:gov:ic:virt"));
			}
		}
		element.removeChildren();
		element.appendChild(TEST_VALUE);
		return (element);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static RevisionRecall getTextFixture() {
		try {
			return (new RevisionRecall(getTextFixtureElement(true)));
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
	private RevisionRecall getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RevisionRecall component = null;
		try {
			component = new RevisionRecall(element);
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
	private RevisionRecall getInstance(RevisionRecall.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		RevisionRecall component = null;
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
	private RevisionRecall.Builder getBaseBuilder() {
		RevisionRecall component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new RevisionRecall.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean hasLinks, boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		if (!hasLinks)
			text.append(buildOutput(isHTML, "revisionRecall", TEST_VALUE));
		text.append(buildOutput(isHTML, "revisionRecall.revisionID", "1"));
		text.append(buildOutput(isHTML, "revisionRecall.revisionType", "ADMINISTRATIVE RECALL"));
		text.append(buildOutput(isHTML, "revisionRecall.network", "NIPRNet"));
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			text.append(buildOutput(isHTML, "revisionRecall.otherNetwork", "PBS"));
		if (hasLinks) {
			text.append(buildOutput(isHTML, "revisionRecall.link.type", "locator"));
			text.append(buildOutput(isHTML, "revisionRecall.link.href", "http://en.wikipedia.org/wiki/Tank"));
			text.append(buildOutput(isHTML, "revisionRecall.link.role", "tank"));
			text.append(buildOutput(isHTML, "revisionRecall.link.title", "Tank Page"));
			text.append(buildOutput(isHTML, "revisionRecall.link.label", "tank"));
			text.append(buildOutput(isHTML, "revisionRecall.link.classification", "U"));
			text.append(buildOutput(isHTML, "revisionRecall.link.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, "revisionRecall.details", "Details"));
			text.append(buildOutput(isHTML, "revisionRecall.details.classification", "U"));
			text.append(buildOutput(isHTML, "revisionRecall.details.ownerProducer", "USA"));
		}
		text.append(XLinkAttributesTest.getResourceFixture().getOutput(isHTML, "revisionRecall."));
		text.append(SecurityAttributesTest.getFixture().getOutput(isHTML, "revisionRecall."));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput(boolean hasLinks) {
		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:revisionRecall ").append(getXmlnsDDMS()).append(" ");
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append(getXmlnsVirt()).append(" ");
		xml.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" ");
		xml.append(getXmlnsISM()).append(" ");
		xml.append("ddms:revisionID=\"1\" ddms:revisionType=\"ADMINISTRATIVE RECALL\" ");
		if (DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append("virt:");
		xml.append("network=\"NIPRNet\" ");
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			xml.append("otherNetwork=\"PBS\" ");
		xml.append("xlink:type=\"resource\" xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");

		if (hasLinks) {
			xml.append("<ddms:link xlink:type=\"locator\" xlink:href=\"http://en.wikipedia.org/wiki/Tank\" ");
			xml.append("xlink:role=\"tank\" xlink:title=\"Tank Page\" xlink:label=\"tank\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\" />");
			xml.append("<ddms:details ism:classification=\"U\" ism:ownerProducer=\"USA\">Details</ddms:details>");
		}
		else
			xml.append(TEST_VALUE);
		xml.append("</ddms:revisionRecall>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				RevisionRecall.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, links & details
			getInstance(getValidElement(sVersion), SUCCESS);
			
			// Data-based via Builder, links & details
			getBaseBuilder();
			
			// Element-based, text
			RevisionRecall elementComponent = getInstance(getTextFixtureElement(false), SUCCESS);
			
			// Data-based, text
			getInstance(new RevisionRecall.Builder(elementComponent), SUCCESS);
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, No optional fields (text)
			Element element = Util.buildDDMSElement(RevisionRecall.getName(version), null);
			Util.addDDMSAttribute(element, "revisionID", TEST_REVISION_ID.toString());
			Util.addDDMSAttribute(element, "revisionType", TEST_REVISION_TYPE);
			SecurityAttributesTest.getFixture().addTo(element);
			RevisionRecall elementComponent = getInstance(element, SUCCESS);

			// Data-based, No optional fields (text)
			getInstance(new RevisionRecall.Builder(elementComponent), SUCCESS);
			
			// Element-based, No optional fields (links & details)
			element.appendChild(LinkTest.getLocatorFixture(true).getXOMElementCopy());
			elementComponent = getInstance(element, SUCCESS);
			
			// Data-based, No optional fields (links & details)
			getInstance(new RevisionRecall.Builder(elementComponent), SUCCESS);
			
			// Null list parameters
			try {
				new RevisionRecall(null, null, TEST_REVISION_ID, TEST_REVISION_TYPE, null, null, null,
					SecurityAttributesTest.getFixture());
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Wrong type of XLinkAttributes (locator)
			RevisionRecall.Builder builder = getBaseBuilder();
			builder.getXLinkAttributes().setType("simple");
			getInstance(builder, "The type attribute must have a fixed value");

			// Both text AND links/details
			builder = getBaseBuilder();
			builder.setValue(TEST_VALUE);
			getInstance(builder, "A ddms:revisionRecall element must not have both");
			
			// Links without security attributes
			builder = getBaseBuilder();
			builder.getLinks().get(0).setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");

			// Missing revisionID
			builder = getBaseBuilder();
			builder.setRevisionID(null);
			getInstance(builder, "revision ID must exist.");

			// Missing revisionType
			builder = getBaseBuilder();
			builder.setRevisionType(null);
			getInstance(builder, "The revisionType attribute must be one of");

			// Invalid revisionType
			builder = getBaseBuilder();
			builder.setRevisionType("MISTAKE");
			getInstance(builder, "The revisionType attribute must be one of");

			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				// Bad network
				builder = getBaseBuilder();
				builder.setNetwork("PBS");
				getInstance(builder, "The network attribute must be one of");
			}
			else {
				// Invalid otherNetwork
				builder = getBaseBuilder();
				builder.setOtherNetwork(TEST_OTHER_NETWORK);				
				getInstance(builder, "The otherNetwork attribute must not ");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			RevisionRecall component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Base equality, links & details
			RevisionRecall elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			RevisionRecall builderComponent = new RevisionRecall.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Wrong class
			Rights wrongComponent = new Rights(true, true, true);
			assertFalse(elementComponent.equals(wrongComponent));
			
			// Different values in each field, links & details
			RevisionRecall.Builder builder = getBaseBuilder();
			builder.getLinks().get(0).getSecurityAttributes().setClassification("S");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getLinks().get(0).getSecurityAttributes().setClassification("S");
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getDetails().clear();
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setRevisionID(Integer.valueOf(2));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.setRevisionType("ADMINISTRATIVE REVISION");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = getBaseBuilder();
			builder.setNetwork("SIPRNet");
			assertFalse(elementComponent.equals(builder.commit()));

			if (!version.isAtLeast("5.0")) {
				builder = getBaseBuilder();
				builder.setOtherNetwork("SIPRNet");
				assertFalse(elementComponent.equals(builder.commit()));
			}
			
			builder = getBaseBuilder();
			builder.setXLinkAttributes(null);
			assertFalse(elementComponent.equals(builder.commit()));
						
			// Base equality, text
			elementComponent = getInstance(getTextFixtureElement(false), SUCCESS);
			builderComponent = new RevisionRecall.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field, text
			builder = new RevisionRecall.Builder(elementComponent);
			builder.setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = new RevisionRecall.Builder(elementComponent);
			builder.setRevisionID(Integer.valueOf(2));
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = new RevisionRecall.Builder(elementComponent);
			builder.setRevisionType("ADMINISTRATIVE REVISION");
			assertFalse(elementComponent.equals(builder.commit()));

			builder = new RevisionRecall.Builder(elementComponent);
			builder.setNetwork("SIPRNet");
			assertFalse(elementComponent.equals(builder.commit()));
			
			if (!version.isAtLeast("5.0")) {
				builder = new RevisionRecall.Builder(elementComponent);
				builder.setOtherNetwork("SIPRNet");
				assertFalse(elementComponent.equals(builder.commit()));
			}
			
			builder = new RevisionRecall.Builder(elementComponent);
			builder.setXLinkAttributes(null);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		RevisionRecall.Builder builder = getBaseBuilder();
		builder.getLinks().clear();
		builder.getDetails().clear();		
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The revisionRecall element must not ");
	}
	
	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// links & details
			RevisionRecall elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true, true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(true, false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(true), elementComponent.toXML());
			
			// text
			elementComponent = getInstance(getTextFixtureElement(false), SUCCESS);
			assertEquals(getExpectedOutput(false, true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false, false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(false), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			RevisionRecall.Builder builder = new RevisionRecall.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			
			builder.getLinks().get(2).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());

			builder = new RevisionRecall.Builder();
			assertTrue(builder.isEmpty());
			
			builder.getDetails().get(2).getSecurityAttributes().setClassification("U");
			assertFalse(builder.isEmpty());
		}
	}
	
	public void testConstructorChaining() throws InvalidDDMSException {
		RevisionRecall recall = new RevisionRecall(null, TEST_REVISION_ID, TEST_REVISION_TYPE, null, null, null,
			SecurityAttributesTest.getFixture());
		RevisionRecall recallFull = new RevisionRecall(null, null, TEST_REVISION_ID, TEST_REVISION_TYPE, null, null,
			null, SecurityAttributesTest.getFixture());
		assertTrue(recall.equals(recallFull));
	}
}
