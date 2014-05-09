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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.ddms.extensible.ExtensibleAttributesTest;
import buri.ddmsence.ddms.extensible.ExtensibleElement;
import buri.ddmsence.ddms.extensible.ExtensibleElementTest;
import buri.ddmsence.ddms.format.Extent;
import buri.ddmsence.ddms.format.FormatTest;
import buri.ddmsence.ddms.metacard.MetacardInfoTest;
import buri.ddmsence.ddms.resource.Contributor;
import buri.ddmsence.ddms.resource.ContributorTest;
import buri.ddmsence.ddms.resource.Creator;
import buri.ddmsence.ddms.resource.CreatorTest;
import buri.ddmsence.ddms.resource.DatesTest;
import buri.ddmsence.ddms.resource.Identifier;
import buri.ddmsence.ddms.resource.IdentifierTest;
import buri.ddmsence.ddms.resource.Language;
import buri.ddmsence.ddms.resource.LanguageTest;
import buri.ddmsence.ddms.resource.Organization;
import buri.ddmsence.ddms.resource.Person;
import buri.ddmsence.ddms.resource.PointOfContact;
import buri.ddmsence.ddms.resource.PointOfContactTest;
import buri.ddmsence.ddms.resource.Publisher;
import buri.ddmsence.ddms.resource.PublisherTest;
import buri.ddmsence.ddms.resource.ResourceManagementTest;
import buri.ddmsence.ddms.resource.RightsTest;
import buri.ddmsence.ddms.resource.Service;
import buri.ddmsence.ddms.resource.Source;
import buri.ddmsence.ddms.resource.SourceTest;
import buri.ddmsence.ddms.resource.Subtitle;
import buri.ddmsence.ddms.resource.SubtitleTest;
import buri.ddmsence.ddms.resource.Title;
import buri.ddmsence.ddms.resource.TitleTest;
import buri.ddmsence.ddms.resource.Type;
import buri.ddmsence.ddms.resource.TypeTest;
import buri.ddmsence.ddms.resource.Unknown;
import buri.ddmsence.ddms.security.SecurityTest;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.ddms.summary.DescriptionTest;
import buri.ddmsence.ddms.summary.GeospatialCoverage;
import buri.ddmsence.ddms.summary.GeospatialCoverageTest;
import buri.ddmsence.ddms.summary.Link;
import buri.ddmsence.ddms.summary.RelatedResource;
import buri.ddmsence.ddms.summary.RelatedResourceTest;
import buri.ddmsence.ddms.summary.SubjectCoverage;
import buri.ddmsence.ddms.summary.SubjectCoverageTest;
import buri.ddmsence.ddms.summary.TemporalCoverage;
import buri.ddmsence.ddms.summary.TemporalCoverageTest;
import buri.ddmsence.ddms.summary.VirtualCoverage;
import buri.ddmsence.ddms.summary.VirtualCoverageTest;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ddms:resource elements </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class ResourceTest extends AbstractBaseTestCase {
	private List<IDDMSComponent> TEST_TOP_LEVEL_COMPONENTS;
	private List<IDDMSComponent> TEST_NO_OPTIONAL_COMPONENTS;

	private static final List<String> TEST_ISM_COMPLIES_WITH = Util.getXsListAsList("DoD5230.24");

	/**
	 * Constructor
	 */
	public ResourceTest() throws InvalidDDMSException {
		super("resource.xml");
	}

	/**
	 * Returns a test resource element value for DDMSVersions that support it.
	 */
	private Boolean getTestResourceElement() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? null : Boolean.TRUE);
	}

	/**
	 * Returns a test resource element value for DDMSVersions that support it.
	 */
	private String getTestResourceElementString() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "" : "true");
	}

	/**
	 * Returns a create date element value for DDMSVersions that support it.
	 */
	private String getTestCreateDate() {
		return (DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? null : "2010-01-21");
	}

	/**
	 * Regenerates all the components needed in a Resource
	 */
	private void createComponents() throws InvalidDDMSException {
		TEST_TOP_LEVEL_COMPONENTS = new ArrayList<IDDMSComponent>();
		TEST_TOP_LEVEL_COMPONENTS.add(SecurityTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(RelatedResourceTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(ResourceManagementTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(GeospatialCoverageTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(TemporalCoverageTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(VirtualCoverageTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(SubjectCoverageTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(FormatTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(PointOfContactTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(ContributorTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(PublisherTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(CreatorTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(TypeTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(SourceTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(RightsTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(DatesTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(LanguageTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(DescriptionTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(SubtitleTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(TitleTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(IdentifierTest.getFixture());
		TEST_TOP_LEVEL_COMPONENTS.add(MetacardInfoTest.getFixture());

		TEST_NO_OPTIONAL_COMPONENTS = new ArrayList<IDDMSComponent>();
		TEST_NO_OPTIONAL_COMPONENTS.add(MetacardInfoTest.getFixture());
		TEST_NO_OPTIONAL_COMPONENTS.add(IdentifierTest.getFixture());
		TEST_NO_OPTIONAL_COMPONENTS.add(TitleTest.getFixture());
		TEST_NO_OPTIONAL_COMPONENTS.add(CreatorTest.getFixture());
		TEST_NO_OPTIONAL_COMPONENTS.add(SubjectCoverageTest.getFixture());
		TEST_NO_OPTIONAL_COMPONENTS.add(SecurityTest.getFixture());
	}

	/**
	 * Creates a stub resource element that is otherwise correct, but leaves resource header attributes off.
	 * 
	 * @return the element
	 * @throws InvalidDDMSException
	 */
	private Element getTestResourceNoHeader() throws InvalidDDMSException {
		Element element = Util.buildDDMSElement(Resource.getName(DDMSVersion.getCurrentVersion()), null);
		if (DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
			element.appendChild(MetacardInfoTest.getFixture().getXOMElementCopy());
		element.appendChild(IdentifierTest.getFixture().getXOMElementCopy());
		element.appendChild(TitleTest.getFixture().getXOMElementCopy());
		element.appendChild(CreatorTest.getFixture().getXOMElementCopy());
		element.appendChild(SubjectCoverageTest.getFixture().getXOMElementCopy());
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			element.appendChild(SecurityTest.getFixture().getXOMElementCopy());
		return (element);
	}

	/**
	 * Creates a stub resource element that is otherwise correct, but leaves resource components out.
	 * 
	 * @return the element
	 * @throws InvalidDDMSException
	 */
	private Element getTestResourceNoBody() throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String ismPrefix = PropertyReader.getPrefix("ism");
		String ismNamespace = version.getIsmNamespace();
		String ntkPrefix = PropertyReader.getPrefix("ntk");
		String ntkNamespace = version.getNtkNamespace();

		Element element = Util.buildDDMSElement(Resource.getName(version), null);
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
			Util.addAttribute(element, ismPrefix, Resource.RESOURCE_ELEMENT_NAME, ismNamespace,
				getTestResourceElementString());
			Util.addAttribute(element, ismPrefix, Resource.CREATE_DATE_NAME, ismNamespace, getTestCreateDate());
			Util.addAttribute(element, ismPrefix, Resource.DES_VERSION_NAME, ismNamespace,
				String.valueOf(getTestIsmDesVersion()));
			if (version.isAtLeast("4.0.1"))
				Util.addAttribute(element, ntkPrefix, Resource.DES_VERSION_NAME, ntkNamespace,
					String.valueOf(getTestNtkDesVersion()));
			SecurityAttributesTest.getFixture().addTo(element);
		}
		return (element);
	}

	/**
	 * Creates a stub resource element that contains a bunch of pre-DDMS 4.0.1 relatedResources in different
	 * configurations.
	 * 
	 * @return the element
	 * @throws InvalidDDMSException
	 */
	private Element getTestResourceMultipleRelated() throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		if (version.isAtLeast("4.0.1"))
			return null;
		String ismPrefix = PropertyReader.getPrefix("ism");
		String ismNamespace = version.getIsmNamespace();

		Element element = Util.buildDDMSElement(Resource.getName(version), null);
		Util.addAttribute(element, ismPrefix, Resource.RESOURCE_ELEMENT_NAME, ismNamespace,
			getTestResourceElementString());
		Util.addAttribute(element, ismPrefix, Resource.CREATE_DATE_NAME, ismNamespace, getTestCreateDate());
		Util.addAttribute(element, ismPrefix, Resource.DES_VERSION_NAME, ismNamespace,
			String.valueOf(getTestIsmDesVersion()));
		SecurityAttributesTest.getFixture().addTo(element);
		element.appendChild(IdentifierTest.getFixture().getXOMElementCopy());
		element.appendChild(TitleTest.getFixture().getXOMElementCopy());
		element.appendChild(CreatorTest.getFixture().getXOMElementCopy());
		element.appendChild(SubjectCoverageTest.getFixture().getXOMElementCopy());

		Link link = new Link(new XLinkAttributes("http://en.wikipedia.org/wiki/Tank", "role", null, null));

		// #1: a ddms:relatedResources containing 1 ddms:RelatedResource
		Element rel1 = Util.buildDDMSElement(RelatedResource.getName(version), null);
		Util.addDDMSAttribute(rel1, "relationship", "http://purl.org/dc/terms/references");
		Element innerElement = Util.buildDDMSElement("RelatedResource", null);
		Util.addDDMSAttribute(innerElement, "qualifier", "http://purl.org/dc/terms/URI");
		Util.addDDMSAttribute(innerElement, "value", "http://en.wikipedia.org/wiki/Tank1");
		innerElement.appendChild(link.getXOMElementCopy());
		rel1.appendChild(innerElement);
		element.appendChild(rel1);

		// #2: a ddms:relatedResources containing 3 ddms:RelatedResources
		Element rel2 = Util.buildDDMSElement(RelatedResource.getName(version), null);
		Util.addDDMSAttribute(rel2, "relationship", "http://purl.org/dc/terms/references");
		Element innerElement1 = Util.buildDDMSElement("RelatedResource", null);
		Util.addDDMSAttribute(innerElement1, "qualifier", "http://purl.org/dc/terms/URI");
		Util.addDDMSAttribute(innerElement1, "value", "http://en.wikipedia.org/wiki/Tank2");
		innerElement1.appendChild(link.getXOMElementCopy());
		Element innerElement2 = Util.buildDDMSElement("RelatedResource", null);
		Util.addDDMSAttribute(innerElement2, "qualifier", "http://purl.org/dc/terms/URI");
		Util.addDDMSAttribute(innerElement2, "value", "http://en.wikipedia.org/wiki/Tank3");
		innerElement2.appendChild(link.getXOMElementCopy());
		Element innerElement3 = Util.buildDDMSElement("RelatedResource", null);
		Util.addDDMSAttribute(innerElement3, "qualifier", "http://purl.org/dc/terms/URI");
		Util.addDDMSAttribute(innerElement3, "value", "http://en.wikipedia.org/wiki/Tank4");
		innerElement3.appendChild(link.getXOMElementCopy());
		rel2.appendChild(innerElement1);
		rel2.appendChild(innerElement2);
		rel2.appendChild(innerElement3);
		element.appendChild(rel2);

		element.appendChild(SecurityTest.getFixture().getXOMElementCopy());
		return (element);
	}

	/**
	 * Returns an acceptable DESVersion for the version of DDMS
	 * 
	 * @return a DESVersion
	 */
	private Integer getTestIsmDesVersion() {
		if (!DDMSVersion.getCurrentVersion().isAtLeast("3.1"))
			return (Integer.valueOf(2));
		if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
			return (Integer.valueOf(5));
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			return (Integer.valueOf(9));
		return (null);
	}

	/**
	 * Returns an acceptable DESVersion for the version of DDMS
	 * 
	 * @return a DESVersion
	 */
	private Integer getTestNtkDesVersion() {
		if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
			return (null);
		if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0"))
			return (Integer.valueOf(7));
		return (null);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * 
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Resource getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Resource component = null;
		try {
			component = new Resource(element);
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
	private Resource getInstance(Resource.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Resource component = null;
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
	private Resource.Builder getBaseBuilder() {
		Resource component = getInstance(getValidElement(DDMSVersion.getCurrentVersion().getVersion()), SUCCESS);
		return (new Resource.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		String resourcePrefix = Resource.getName(version);
		boolean isAtLeast30 = version.isAtLeast("3.0");
		boolean isAtLeast401 = version.isAtLeast("4.0.1");
		boolean is41 = "4.1".equals(version.getVersion());
		boolean isAtLeast50 = version.isAtLeast("5.0");

		StringBuffer text = new StringBuffer();
		if (isAtLeast50) {
			text.append(buildOutput(isHTML, resourcePrefix + ".compliesWith", "DDMSRules"));
		}
		else {
			if (isAtLeast30) {
				text.append(buildOutput(isHTML, resourcePrefix + ".resourceElement", "true"));
				text.append(buildOutput(isHTML, resourcePrefix + ".createDate", "2010-01-21"));
			}
			text.append(buildOutput(isHTML, resourcePrefix + ".ism.DESVersion", String.valueOf(getTestIsmDesVersion())));
			if (isAtLeast401) {
				text.append(buildOutput(isHTML, resourcePrefix + ".ntk.DESVersion",
					String.valueOf(getTestNtkDesVersion())));
			}
			if (isAtLeast30) {
				text.append(buildOutput(isHTML, resourcePrefix + ".classification", "U"));
				text.append(buildOutput(isHTML, resourcePrefix + ".ownerProducer", "USA"));
			}
		}
		if (is41) {
			text.append(buildOutput(isHTML, resourcePrefix + ".noticeType", "DoD-Dist-B"));
			text.append(buildOutput(isHTML, resourcePrefix + ".noticeReason", "noticeReason"));
			text.append(buildOutput(isHTML, resourcePrefix + ".noticeDate", "2011-09-15"));
			text.append(buildOutput(isHTML, resourcePrefix + ".unregisteredNoticeType", "unregisteredNoticeType"));
			text.append(buildOutput(isHTML, resourcePrefix + ".externalNotice", "false"));
		}
		if (isAtLeast401) {
			text.append(buildOutput(isHTML, "metacardInfo.identifier.qualifier", "URI"));
			text.append(buildOutput(isHTML, "metacardInfo.identifier.value", "urn:buri:ddmsence:testIdentifier"));
			text.append(buildOutput(isHTML, "metacardInfo.dates.created", "2003"));
			text.append(buildOutput(isHTML, "metacardInfo.publisher.entityType", "person"));
			text.append(buildOutput(isHTML, "metacardInfo.publisher.name", "Brian"));
			text.append(buildOutput(isHTML, "metacardInfo.publisher.surname", "Uri"));
			text.append(buildOutput(isHTML, "metacardInfo.classification", "U"));
			text.append(buildOutput(isHTML, "metacardInfo.ownerProducer", "USA"));
		}
		text.append(buildOutput(isHTML, "identifier.qualifier", "URI"));
		text.append(buildOutput(isHTML, "identifier.value", "urn:buri:ddmsence:testIdentifier"));
		text.append(buildOutput(isHTML, "title", "DDMSence"));
		text.append(buildOutput(isHTML, "title.classification", "U"));
		text.append(buildOutput(isHTML, "title.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "subtitle", "Version 0.1"));
		text.append(buildOutput(isHTML, "subtitle.classification", "U"));
		text.append(buildOutput(isHTML, "subtitle.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "description", "A transformation service."));
		text.append(buildOutput(isHTML, "description.classification", "U"));
		text.append(buildOutput(isHTML, "description.ownerProducer", "USA"));
		text.append(buildOutput(isHTML, "language.qualifier", "http://purl.org/dc/elements/1.1/language"));
		text.append(buildOutput(isHTML, "language.value", "en"));
		text.append(buildOutput(isHTML, "dates.created", "2003"));
		text.append(buildOutput(isHTML, "rights.privacyAct", "true"));
		text.append(buildOutput(isHTML, "rights.intellectualProperty", "true"));
		text.append(buildOutput(isHTML, "rights.copyright", "true"));
		text.append(buildOutput(isHTML, "source.value", "http://www.xmethods.com"));
		text.append(buildOutput(isHTML, "type.qualifier", "DCMITYPE"));
		text.append(buildOutput(isHTML, "type.value", "http://purl.org/dc/dcmitype/Text"));
		text.append(buildOutput(isHTML, "creator.entityType", Organization.getName(version)));
		text.append(buildOutput(isHTML, "creator.name", "DISA"));
		text.append(buildOutput(isHTML, "publisher.entityType", Person.getName(version)));
		text.append(buildOutput(isHTML, "publisher.name", "Brian"));
		text.append(buildOutput(isHTML, "publisher.surname", "Uri"));
		text.append(buildOutput(isHTML, "contributor.entityType", Service.getName(version)));
		text.append(buildOutput(isHTML, "contributor.name", "https://metadata.dod.mil/ebxmlquery/soap"));
		if (isAtLeast30) {
			text.append(buildOutput(isHTML, "pointOfContact.entityType", Unknown.getName(version)));
			text.append(buildOutput(isHTML, "pointOfContact.name", "UnknownEntity"));
		}
		else {
			text.append(buildOutput(isHTML, "pointOfContact.entityType", Person.getName(version)));
			text.append(buildOutput(isHTML, "pointOfContact.name", "Brian"));
			text.append(buildOutput(isHTML, "pointOfContact.surname", "Uri"));
		}

		String formatPrefix = (isAtLeast401 ? "format." : "format.Media.");
		String subjectPrefix = (isAtLeast401 ? "subjectCoverage." : "subjectCoverage.Subject.");
		String temporalPrefix = (isAtLeast401 ? "temporalCoverage." : "temporalCoverage.TimePeriod.");
		String geospatialPrefix = isAtLeast401 ? "geospatialCoverage." : "geospatialCoverage.GeospatialExtent.";
		String relatedPrefix = (isAtLeast401 ? "relatedResource." : "relatedResources.RelatedResource.");

		text.append(buildOutput(isHTML, formatPrefix + "mimeType", "text/xml"));
		text.append(buildOutput(isHTML, subjectPrefix + "keyword", "DDMSence"));
		text.append(buildOutput(isHTML, "virtualCoverage.address", "123.456.789.0"));
		text.append(buildOutput(isHTML, "virtualCoverage.protocol", "IP"));
		text.append(buildOutput(isHTML, temporalPrefix + "name", "Unknown"));
		text.append(buildOutput(isHTML, temporalPrefix + "start", "1979-09-15"));
		text.append(buildOutput(isHTML, temporalPrefix + "end", "Not Applicable"));
		if (!isAtLeast50) {
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.id", "IDValue"));
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.srsName",
				"http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D"));
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.srsDimension", "10"));
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.axisLabels", "A B C"));
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.uomLabels", "Meter Meter Meter"));
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.Point.pos", "32.1 40.1"));
		}
		else {
			text.append(buildOutput(isHTML, geospatialPrefix + "boundingGeometry.shapeType", "Point"));
		}
		text.append(buildOutput(isHTML, relatedPrefix + "relationship", "http://purl.org/dc/terms/references"));
		text.append(buildOutput(isHTML, relatedPrefix + "direction", "outbound"));
		text.append(buildOutput(isHTML, relatedPrefix + "qualifier", "http://purl.org/dc/terms/URI"));
		text.append(buildOutput(isHTML, relatedPrefix + "value", "http://en.wikipedia.org/wiki/Tank"));
		text.append(buildOutput(isHTML, relatedPrefix + "link.type", "locator"));
		text.append(buildOutput(isHTML, relatedPrefix + "link.href", "http://en.wikipedia.org/wiki/Tank"));
		text.append(buildOutput(isHTML, relatedPrefix + "link.role", "role"));
		
		if (isAtLeast401) {
			text.append(buildOutput(isHTML, "resourceManagement.processingInfo",
				"XSLT Transformation to convert DDMS 2.0 to DDMS 3.1."));
			text.append(buildOutput(isHTML, "resourceManagement.processingInfo.dateProcessed", "2011-08-19"));
			text.append(buildOutput(isHTML, "resourceManagement.processingInfo.classification", "U"));
			text.append(buildOutput(isHTML, "resourceManagement.processingInfo.ownerProducer", "USA"));
			text.append(buildOutput(isHTML, "resourceManagement.classification", "U"));
			text.append(buildOutput(isHTML, "resourceManagement.ownerProducer", "USA"));
		}
		if (!isAtLeast50) {
			if (isAtLeast30)
				text.append(buildOutput(isHTML, "security.excludeFromRollup", "true"));
			text.append(buildOutput(isHTML, "security.classification", "U"));
			text.append(buildOutput(isHTML, "security.ownerProducer", "USA"));
		}
		text.append(buildOutput(isHTML, "extensible.layer", "false"));
		text.append(buildOutput(isHTML, "ddms.generator", "DDMSence " + PropertyReader.getProperty("version")));
		// Output for version will be based upon XML namespace of created resource, not the currently set version.
		text.append(buildOutput(isHTML, "ddms.version",
			DDMSVersion.getVersionForNamespace(version.getNamespace()).getVersion()));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		boolean isAtLeast30 = version.isAtLeast("3.0");
		boolean isAtLeast401 = version.isAtLeast("4.0.1");
		boolean isAtLeast50 = version.isAtLeast("5.0");

		StringBuffer xml = new StringBuffer();
		xml.append("<ddms:").append(Resource.getName(version)).append(" ").append(getXmlnsDDMS());
		if (isAtLeast401)
			xml.append(" ").append(getXmlnsNTK());
		xml.append(" ").append(getXmlnsISM());
		if (isAtLeast50) {
			xml.append(" ").append(getXmlnsVirt());
			xml.append(" ").append("ddms:compliesWith=\"DDMSRules\"");
		}
		else {
			if (isAtLeast401)
				xml.append(" ntk:DESVersion=\"").append(getTestNtkDesVersion()).append("\"");
			if (isAtLeast30)
				xml.append(" ism:resourceElement=\"true\"");
			// Adding DESVersion in DDMS 2.0 allows the namespace declaration to definitely be in the Resource element.
			xml.append(" ism:DESVersion=\"").append(getTestIsmDesVersion()).append("\"");
			if (isAtLeast30)
				xml.append(" ism:createDate=\"2010-01-21\"");
			if ("4.1".equals(version.getVersion())) {
				xml.append(" ism:noticeType=\"DoD-Dist-B\" ism:noticeReason=\"noticeReason\" ism:noticeDate=\"2011-09-15\" ");
				xml.append("ism:unregisteredNoticeType=\"unregisteredNoticeType\"");
				xml.append(" ism:externalNotice=\"false\"");
			}
			if (isAtLeast30)
				xml.append(" ism:classification=\"U\" ism:ownerProducer=\"USA\"");
		}
		xml.append(">\n");
		if (isAtLeast401) {
			xml.append("\t<ddms:metacardInfo ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ddms:identifier ddms:qualifier=\"URI\" ddms:value=\"urn:buri:ddmsence:testIdentifier\" />");
			xml.append("<ddms:dates ddms:created=\"2003\" /><ddms:publisher><ddms:person><ddms:name>Brian</ddms:name>");
			xml.append("<ddms:surname>Uri</ddms:surname></ddms:person></ddms:publisher></ddms:metacardInfo>\n");
		}
		xml.append("\t<ddms:identifier ddms:qualifier=\"URI\" ddms:value=\"urn:buri:ddmsence:testIdentifier\" />\n");
		xml.append("\t<ddms:title ism:classification=\"U\" ism:ownerProducer=\"USA\">DDMSence</ddms:title>\n");
		xml.append("\t<ddms:subtitle ism:classification=\"U\" ism:ownerProducer=\"USA\">Version 0.1</ddms:subtitle>\n");
		xml.append("\t<ddms:description ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("A transformation service.</ddms:description>\n");
		xml.append("\t<ddms:language ddms:qualifier=\"http://purl.org/dc/elements/1.1/language\" ");
		xml.append("ddms:value=\"en\" />\n");
		xml.append("\t<ddms:dates ddms:created=\"2003\" />\n");
		xml.append("\t<ddms:rights ddms:privacyAct=\"true\" ddms:intellectualProperty=\"true\" ");
		xml.append("ddms:copyright=\"true\" />\n");
		xml.append("\t<ddms:source ddms:value=\"http://www.xmethods.com\" />\n");
		xml.append("\t<ddms:type ddms:qualifier=\"DCMITYPE\" ddms:value=\"http://purl.org/dc/dcmitype/Text\" />\n");
		xml.append("\t<ddms:creator>\n");
		xml.append("\t\t<ddms:").append(Organization.getName(version)).append(">\n");
		xml.append("\t\t\t<ddms:name>DISA</ddms:name>\n");
		xml.append("\t\t</ddms:").append(Organization.getName(version)).append(">\t\n");
		xml.append("\t</ddms:creator>\n");
		xml.append("\t<ddms:publisher>\n");
		xml.append("\t\t<ddms:").append(Person.getName(version)).append(">\n");
		xml.append("\t\t\t<ddms:name>Brian</ddms:name>\n");
		xml.append("\t\t\t<ddms:surname>Uri</ddms:surname>\n");
		xml.append("\t\t</ddms:").append(Person.getName(version)).append(">\t\n");
		xml.append("\t</ddms:publisher>\n");
		xml.append("\t<ddms:contributor>\n");
		xml.append("\t\t<ddms:").append(Service.getName(version)).append(">\n");
		xml.append("\t\t\t<ddms:name>https://metadata.dod.mil/ebxmlquery/soap</ddms:name>\n");
		xml.append("\t\t</ddms:").append(Service.getName(version)).append(">\t\n");
		xml.append("\t</ddms:contributor>\n");
		xml.append("\t<ddms:pointOfContact>\n");
		if (isAtLeast30) {
			xml.append("\t\t<ddms:").append(Unknown.getName(version)).append(">\n");
			xml.append("\t\t\t<ddms:name>UnknownEntity</ddms:name>\n");
			xml.append("\t\t</ddms:").append(Unknown.getName(version)).append(">\t\n");
		}
		else {
			xml.append("\t\t<ddms:").append(Person.getName(version)).append(">\n");
			xml.append("\t\t\t<ddms:name>Brian</ddms:name>\n");
			xml.append("\t\t\t<ddms:surname>Uri</ddms:surname>\n");
			xml.append("\t\t</ddms:").append(Person.getName(version)).append(">\n");
		}
		xml.append("\t</ddms:pointOfContact>\n");
		xml.append("\t<ddms:format>\n");
		if (isAtLeast401) {
			xml.append("\t\t<ddms:mimeType>text/xml</ddms:mimeType>\n");
		}
		else {
			xml.append("\t\t<ddms:Media>\n");
			xml.append("\t\t\t<ddms:mimeType>text/xml</ddms:mimeType>\n");
			xml.append("\t\t</ddms:Media>\n");
		}
		xml.append("\t</ddms:format>\n");
		xml.append("\t<ddms:subjectCoverage>\n");
		if (isAtLeast401) {
			xml.append("\t\t<ddms:keyword ddms:value=\"DDMSence\" />\n");
		}
		else {
			xml.append("\t\t<ddms:Subject>\n");
			xml.append("\t\t\t<ddms:keyword ddms:value=\"DDMSence\" />\n");
			xml.append("\t\t</ddms:Subject>\n");
		}
		xml.append("\t</ddms:subjectCoverage>\n");
		String prefix = DDMSVersion.getCurrentVersion().isAtLeast("5.0") ? "virt" : "ddms";
		xml.append("\t<ddms:virtualCoverage ").append(prefix).append(":address=\"123.456.789.0\" ");
		xml.append(prefix).append(":protocol=\"IP\" />\n");
		xml.append("\t<ddms:temporalCoverage>\n");
		if (isAtLeast401) {
			xml.append("\t\t<ddms:start>1979-09-15</ddms:start>\n");
			xml.append("\t\t<ddms:end>Not Applicable</ddms:end>\n");
		}
		else {
			xml.append("\t\t<ddms:TimePeriod>\n");
			xml.append("\t\t\t<ddms:start>1979-09-15</ddms:start>\n");
			xml.append("\t\t\t<ddms:end>Not Applicable</ddms:end>\n");
			xml.append("\t\t</ddms:TimePeriod>\n");
		}
		xml.append("\t</ddms:temporalCoverage>\n");
		xml.append("\t<ddms:geospatialCoverage>\n");
		if (!isAtLeast50) {
			if (isAtLeast401) {
				xml.append("\t\t<ddms:boundingGeometry>\n");
				xml.append("\t\t\t<gml:Point xmlns:gml=\"").append(version.getGmlNamespace()).append("\" ");
				xml.append("gml:id=\"IDValue\" srsName=\"http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D\" ");
				xml.append("srsDimension=\"10\" axisLabels=\"A B C\" uomLabels=\"Meter Meter Meter\">\n");
				xml.append("\t\t\t\t<gml:pos>32.1 40.1</gml:pos>\n");
				xml.append("\t\t\t</gml:Point>\n");
				xml.append("\t\t</ddms:boundingGeometry>\n");
			}
			else {
				xml.append("\t\t<ddms:GeospatialExtent>\n");
				xml.append("\t\t\t<ddms:boundingGeometry>\n");
				xml.append("\t\t\t\t<gml:Point xmlns:gml=\"").append(version.getGmlNamespace()).append("\" ");
				xml.append("gml:id=\"IDValue\" srsName=\"http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D\" ");
				xml.append("srsDimension=\"10\" axisLabels=\"A B C\" uomLabels=\"Meter Meter Meter\">\n");
				xml.append("\t\t\t\t\t<gml:pos>32.1 40.1</gml:pos>\n");
				xml.append("\t\t\t\t</gml:Point>\n");
				xml.append("\t\t\t</ddms:boundingGeometry>\n");
				xml.append("\t\t</ddms:GeospatialExtent>\n");
			}
		}
		else {
			xml.append("\t\t<ddms:boundingGeometry>\n");
			xml.append("\t\t\t<tspi:Point xmlns:tspi=\"").append(version.getTspiNamespace()).append("\" ");
			xml.append("xmlns:gml=\"").append(version.getGmlNamespace()).append("\" ");
			xml.append("gml:id=\"PointMinimalExample\" srsDimension=\"3\" srsName=\"http://metadata.ces.mil/mdr/ns/GSIP/crs/WGS84E_MSL_H\">\n");
			xml.append("\t\t\t\t<gml:pos>32.1 40.1</gml:pos>\n");
			xml.append("\t\t\t</tspi:Point>\n");
			xml.append("\t\t</ddms:boundingGeometry>\n");
		}
		xml.append("\t</ddms:geospatialCoverage>\n");
		if (isAtLeast401) {
			xml.append("\t<ddms:relatedResource ddms:relationship=\"http://purl.org/dc/terms/references\" ");
			xml.append("ddms:direction=\"outbound\" ddms:qualifier=\"http://purl.org/dc/terms/URI\" ");
			xml.append("ddms:value=\"http://en.wikipedia.org/wiki/Tank\">\n");
			xml.append("\t\t<ddms:link xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"locator\" ");
			xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"role\" />\n");
			xml.append("\t</ddms:relatedResource>\n");
		}
		else {
			xml.append("\t<ddms:relatedResources ddms:relationship=\"http://purl.org/dc/terms/references\" ");
			xml.append("ddms:direction=\"outbound\">\n");
			xml.append("\t\t<ddms:RelatedResource ddms:qualifier=\"http://purl.org/dc/terms/URI\" ");
			xml.append("ddms:value=\"http://en.wikipedia.org/wiki/Tank\">\n");
			xml.append("\t\t\t<ddms:link xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"locator\" ");
			xml.append("xlink:href=\"http://en.wikipedia.org/wiki/Tank\" xlink:role=\"role\" />\n");
			xml.append("\t\t</ddms:RelatedResource>\n");
			xml.append("\t</ddms:relatedResources>\n");
		}
		if (isAtLeast401) {
			xml.append("\t<ddms:resourceManagement ism:classification=\"U\" ism:ownerProducer=\"USA\">");
			xml.append("<ddms:processingInfo ism:classification=\"U\" ism:ownerProducer=\"USA\" ");
			xml.append("ddms:dateProcessed=\"2011-08-19\">");
			xml.append("XSLT Transformation to convert DDMS 2.0 to DDMS 3.1.</ddms:processingInfo>");
			xml.append("</ddms:resourceManagement>\n");
		}
		if (!isAtLeast50) {
			xml.append("\t<ddms:security ");
			if (isAtLeast30)
				xml.append("ism:excludeFromRollup=\"true\" ");
			xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\" />\n");
		}
		xml.append("</ddms:").append(Resource.getName(version)).append(">");
		return (xml.toString());
	}

	public void testNameAndNamespace() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_DDMS_PREFIX,
				Resource.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();
			boolean isAtLeast50 = version.isAtLeast("5.0");

			// Element-based
			getInstance(getValidElement(sVersion), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();

			if (version.isAtLeast("4.0.1")) {
				// More than 1 subjectCoverage
				Resource.Builder builder = getBaseBuilder();
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture()));
				getInstance(builder, SUCCESS);

				// Valid orders
				builder = getBaseBuilder();
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture(1)));
				builder.getGeospatialCoverages().add(
					new GeospatialCoverage.Builder(GeospatialCoverageTest.getFixture(2)));
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture(3)));
				getInstance(builder, SUCCESS);
			}
			if (!version.isAtLeast("5.0")) {
				// Element-based, with ExtensibleElement
				try {
					Resource.Builder builder = getBaseBuilder();
					Resource resource = builder.commit();
					Element resourceElement = resource.getXOMElementCopy();
					resourceElement.appendChild(ExtensibleElementTest.getFixtureElement());
					Resource generated = new Resource(resourceElement);
					assertEquals(generated, new Resource.Builder(generated).commit());
				}
				catch (InvalidDDMSException e) {
					checkConstructorFailure(false, e);
				}

				// Data-based via Builder, with ExtensibleElement
				ExtensibleElement component = new ExtensibleElement(ExtensibleElementTest.getFixtureElement());
				Resource.Builder builder = getBaseBuilder();
				builder.getExtensibleElements().get(0).setXml(component.toXML());
				getInstance(builder, SUCCESS);
			}

			// Null in component list
			try {
				List<IDDMSComponent> components = new ArrayList<IDDMSComponent>(TEST_NO_OPTIONAL_COMPONENTS);
				components.add(null);
				SecurityAttributes attributes = isAtLeast50 ? null : SecurityAttributesTest.getFixture();
				new Resource(components, getTestResourceElement(), getTestCreateDate(), null, getTestIsmDesVersion(),
					getTestNtkDesVersion(), attributes, null, null);
			}
			catch (InvalidDDMSException e) {
				checkConstructorFailure(false, e);
			}
		}
	}

	public void testConstructorsMinimal() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			// Element-based, no optional fields
			Element element = getTestResourceNoBody();
			if (version.isAtLeast("4.0.1"))
				element.appendChild(MetacardInfoTest.getFixture().getXOMElementCopy());
			element.appendChild(IdentifierTest.getFixture().getXOMElementCopy());
			element.appendChild(TitleTest.getFixture().getXOMElementCopy());
			element.appendChild(CreatorTest.getFixture().getXOMElementCopy());
			element.appendChild(SubjectCoverageTest.getFixture().getXOMElementCopy());
			if (!version.isAtLeast("5.0"))
				element.appendChild(SecurityTest.getFixture().getXOMElementCopy());
			Resource elementComponent = getInstance(element, SUCCESS);

			// Data-based via Builder, no optional fields
			getInstance(new Resource.Builder(elementComponent), SUCCESS);
		}
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();
			boolean isAtLeast30 = version.isAtLeast("3.0");
			boolean isAtLeast401 = version.isAtLeast("4.0.1");
			boolean isAtLeast50 = version.isAtLeast("5.0");

			// Null component list
			try {
				new Resource(null, null);
			}
			catch (InvalidDDMSException e) {
				String error = isAtLeast401 ? "Exactly 1 metacardInfo" : "At least 1 identifier";
				expectMessage(e, error);
			}

			// Invalid object in component list
			try {
				List<IDDMSComponent> components = new ArrayList<IDDMSComponent>(TEST_NO_OPTIONAL_COMPONENTS);
				components.add(new Extent(null, null));
				new Resource(components, null);
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "extent is not a valid");
			}

			if (isAtLeast30 && !isAtLeast50) {
				// Missing resourceHeader
				Resource.Builder builder = getBaseBuilder();
				builder.setResourceElement(null);
				getInstance(builder, "resourceElement must exist.");

				// Missing createDate
				builder = getBaseBuilder();
				builder.setCreateDate(null);
				getInstance(builder, "createDate must exist.");

				// Invalid createDate
				builder = getBaseBuilder();
				builder.setCreateDate("soon");
				getInstance(builder, "The ism:createDate attribute must adhere to a valid");

				// Almost valid createDate
				builder = getBaseBuilder();
				builder.setCreateDate("---31");
				getInstance(builder, "The createDate must be in the xs:date");

				// Missing ISM DESVersion
				builder = getBaseBuilder();
				builder.setIsmDESVersion(null);
				getInstance(builder, "ism:DESVersion must exist.");
			}

			if (isAtLeast401 && !isAtLeast50) {
				// Missing NTK DESVersion
				Resource.Builder builder = getBaseBuilder();
				builder.setNtkDESVersion(null);
				getInstance(builder, "ntk:DESVersion must exist.");
			}

			// Missing identifier
			Resource.Builder builder = getBaseBuilder();
			builder.getIdentifiers().clear();
			getInstance(builder, "At least 1 identifier");

			// Missing title
			builder = getBaseBuilder();
			builder.getTitles().clear();
			getInstance(builder, "At least 1 title");

			// At least 1 producer
			builder = getBaseBuilder();
			builder.getContributors().clear();
			builder.getCreators().clear();
			builder.getPointOfContacts().clear();
			builder.getPublishers().clear();
			getInstance(builder, "At least 1 producer");

			if (isAtLeast401) {
				// Missing subjectCoverage
				builder = getBaseBuilder();
				builder.getSubjectCoverages().clear();
				getInstance(builder, "At least 1 subjectCoverage");

				// Duplicate orders
				builder = getBaseBuilder();
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture(1)));
				builder.getGeospatialCoverages().add(
					new GeospatialCoverage.Builder(GeospatialCoverageTest.getFixture(1)));
				getInstance(builder, "The ddms:order attributes throughout this resource");

				// Skipped orders
				builder = getBaseBuilder();
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture(1)));
				builder.getGeospatialCoverages().add(
					new GeospatialCoverage.Builder(GeospatialCoverageTest.getFixture(3)));
				getInstance(builder, "The ddms:order attributes throughout this resource");
			}
			else {
				// Missing subjectCoverage
				builder = getBaseBuilder();
				builder.getSubjectCoverages().clear();
				getInstance(builder, "Exactly 1 subjectCoverage");

				// Too many subjectCoverages
				builder = getBaseBuilder();
				builder.getSubjectCoverages().add(new SubjectCoverage.Builder(SubjectCoverageTest.getFixture()));
				getInstance(builder, "Exactly 1 subjectCoverage");
			}
			if (!isAtLeast50) {
				// Missing security
				builder = getBaseBuilder();
				builder.setSecurity(null);
				getInstance(builder, "Exactly 1 security element must exist.");
			}
		}
	}

	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();
			boolean is41 = "4.1".equals(version.getVersion());

			Resource component = getInstance(getValidElement(sVersion), SUCCESS);
			if (!is41) {
				// No warnings
				assertEquals(0, component.getValidationWarnings().size());
			}
			else {
				// 4.1 ism:Notice used
				assertEquals(1, component.getValidationWarnings().size());
				String text = "The ism:externalNotice attribute in this DDMS component";
				String locator = "ddms:resource";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}

			int countIndex = is41 ? 1 : 0;

			// Nested warnings
			Resource.Builder builder = getBaseBuilder();
			builder.getFormat().getExtent().setQualifier("test");
			builder.getFormat().getExtent().setValue(null);
			component = getInstance(builder, SUCCESS);
			assertEquals(countIndex + 1, component.getValidationWarnings().size());
			String resourceName = Resource.getName(version);
			String text = "A qualifier has been set without an accompanying value attribute.";
			String locator = (version.isAtLeast("4.0.1")) ? "ddms:" + resourceName + "/ddms:format/ddms:extent"
				: "ddms:" + resourceName + "/ddms:format/ddms:Media/ddms:extent";
			assertWarningEquality(text, locator, component.getValidationWarnings().get(countIndex));
			if (is41) {
				text = "The ism:externalNotice attribute";
				locator = "ddms:resource";
				assertWarningEquality(text, locator, component.getValidationWarnings().get(0));
			}
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			boolean isAtLeast31 = version.isAtLeast("3.1");
			boolean isAtLeast50 = version.isAtLeast("5.0");

			// Base equality
			Resource elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Resource builderComponent = new Resource.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Resource.Builder builder = getBaseBuilder();
			builder.setDates(null);
			assertFalse(elementComponent.equals(builder.commit()));

			if (!isAtLeast31) {
				// Can only change resourceElement before 3.1.
				builder = getBaseBuilder();
				builder.setResourceElement(Boolean.FALSE);
				assertFalse(elementComponent.equals(builder.commit()));

				// Can only change DESVersion before 3.1
				builder = getBaseBuilder();
				builder.setIsmDESVersion(Integer.valueOf(1));
				assertFalse(elementComponent.equals(builder.commit()));
			}
			if (!isAtLeast50) {
				// Different createDate
				builder = getBaseBuilder();
				builder.setCreateDate("1999-10-10");
				assertFalse(elementComponent.equals(builder.commit()));
			}
		}
	}

	public void testVersionSpecific() throws InvalidDDMSException {
		// compliesWith before 3.1
		DDMSVersion.setCurrentVersion("3.0");
		Resource.Builder builder = getBaseBuilder();
		builder.setCompliesWiths(TEST_ISM_COMPLIES_WITH);
		getInstance(builder, "The compliesWith attribute must not");

		// ISM/NTK in DDMS 5.0
		DDMSVersion.setCurrentVersion("5.0");
		builder = getBaseBuilder();
		builder.setCreateDate("2012-01-01");
		getInstance(builder, "The resource must not have ISM");

		// Extensible Elements in DDMS 5.0
		DDMSVersion.setCurrentVersion("5.0");
		builder = getBaseBuilder();
		builder.getExtensibleElements().get(0).setXml(ExtensibleElementTest.getFixtureElement().toXML());
		getInstance(builder, "The resource must not have extensible");

		// Extensible Attributes in DDMS 5.0
		DDMSVersion.setCurrentVersion("5.0");
		builder = getBaseBuilder();
		builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(ExtensibleAttributesTest.getFixture()));
		getInstance(builder, "The resource must not have extensible");
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			Resource elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());

			if (!version.isAtLeast("5.0")) {
				// ExtensibleElements
				ExtensibleElement component = new ExtensibleElement(ExtensibleElementTest.getFixtureElement());
				Resource.Builder builder = getBaseBuilder();
				builder.getExtensibleElements().get(0).setXml(component.toXML());
				Resource builderComponent = builder.commit();
				assertTrue(builderComponent.toHTML().indexOf(buildOutput(true, "extensible.layer", "true")) != -1);
				assertTrue(builderComponent.toText().indexOf(buildOutput(false, "extensible.layer", "true")) != -1);
			}
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Resource.Builder builder = new Resource.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.getIdentifiers().add(new Identifier.Builder());
			assertTrue(builder.isEmpty());
			builder.getTitles().add(new Title.Builder());
			assertTrue(builder.isEmpty());
			builder.getSubtitles().add(new Subtitle.Builder());
			assertTrue(builder.isEmpty());
			builder.getLanguages().add(new Language.Builder());
			assertTrue(builder.isEmpty());
			builder.getSources().add(new Source.Builder());
			assertTrue(builder.isEmpty());
			builder.getTypes().add(new Type.Builder());
			assertTrue(builder.isEmpty());
			builder.getCreators().add(new Creator.Builder());
			assertTrue(builder.isEmpty());
			builder.getContributors().add(new Contributor.Builder());
			assertTrue(builder.isEmpty());
			builder.getPublishers().add(new Publisher.Builder());
			assertTrue(builder.isEmpty());
			builder.getPointOfContacts().add(new PointOfContact.Builder());
			assertTrue(builder.isEmpty());
			assertEquals(4, builder.getProducers().size());
			builder.getVirtualCoverages().add(new VirtualCoverage.Builder());
			assertTrue(builder.isEmpty());
			builder.getTemporalCoverages().add(new TemporalCoverage.Builder());
			assertTrue(builder.isEmpty());
			builder.getGeospatialCoverages().add(new GeospatialCoverage.Builder());
			assertTrue(builder.isEmpty());
			builder.getRelatedResources().add(new RelatedResource.Builder());
			assertTrue(builder.isEmpty());
			builder.getExtensibleElements().add(new ExtensibleElement.Builder());
			assertTrue(builder.isEmpty());
			builder.getExtensibleElements().get(0).setXml("InvalidXml");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Resource.Builder builder = new Resource.Builder();
			assertNotNull(builder.getIdentifiers().get(1));
			assertNotNull(builder.getTitles().get(1));
			assertNotNull(builder.getSubtitles().get(1));
			assertNotNull(builder.getLanguages().get(1));
			assertNotNull(builder.getSources().get(1));
			assertNotNull(builder.getTypes().get(1));
			assertNotNull(builder.getCreators().get(1));
			assertNotNull(builder.getContributors().get(1));
			assertNotNull(builder.getPublishers().get(1));
			assertNotNull(builder.getPointOfContacts().get(1));
			assertNotNull(builder.getVirtualCoverages().get(1));
			assertNotNull(builder.getTemporalCoverages().get(1));
			assertNotNull(builder.getGeospatialCoverages().get(1));
			assertNotNull(builder.getRelatedResources().get(1));
			assertNotNull(builder.getExtensibleElements().get(1));
		}
	}

	public void testBuilderSerialization() throws Exception {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Resource component = getInstance(getValidElement(sVersion), SUCCESS);

			Resource.Builder builder = new Resource.Builder(component);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(builder);
			oos.close();
			byte[] serialized = out.toByteArray();
			assertTrue(serialized.length > 0);

			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
			Resource.Builder unserializedBuilder = (Resource.Builder) ois.readObject();
			assertEquals(component, unserializedBuilder.commit());
		}
	}

	public void testRelatedResourcesMediation() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();
			if (version.isAtLeast("4.0.1"))
				continue;

			Element element = getTestResourceMultipleRelated();
			Resource resource = getInstance(element, SUCCESS);
			assertEquals(4, resource.getRelatedResources().size());
			assertEquals("http://en.wikipedia.org/wiki/Tank1", resource.getRelatedResources().get(0).getValue());
			assertEquals("http://en.wikipedia.org/wiki/Tank2", resource.getRelatedResources().get(1).getValue());
			assertEquals("http://en.wikipedia.org/wiki/Tank3", resource.getRelatedResources().get(2).getValue());
			assertEquals("http://en.wikipedia.org/wiki/Tank4", resource.getRelatedResources().get(3).getValue());
		}
	}

	public void testConstructorChaining() throws InvalidDDMSException {
		// DDMS 2.0
		DDMSVersion.setCurrentVersion("2.0");
		createComponents();
		Resource resource = new Resource(TEST_TOP_LEVEL_COMPONENTS, null);
		Resource fullResource = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, null, null, null, null, null);
		assertEquals(resource, fullResource);

		// DDMS 3.0
		DDMSVersion.setCurrentVersion("3.0");
		createComponents();
		resource = new Resource(TEST_TOP_LEVEL_COMPONENTS, getTestResourceElement(), getTestCreateDate(),
			getTestIsmDesVersion(), SecurityAttributesTest.getFixture(), null);
		fullResource = new Resource(TEST_TOP_LEVEL_COMPONENTS, getTestResourceElement(), getTestCreateDate(), null,
			getTestIsmDesVersion(), null, SecurityAttributesTest.getFixture(), null, null);
		assertEquals(resource, fullResource);

		// DDMS 3.1
		DDMSVersion.setCurrentVersion("3.1");
		createComponents();
		resource = new Resource(TEST_TOP_LEVEL_COMPONENTS, getTestResourceElement(), getTestCreateDate(),
			TEST_ISM_COMPLIES_WITH, getTestIsmDesVersion(), SecurityAttributesTest.getFixture(), null);
		fullResource = new Resource(TEST_TOP_LEVEL_COMPONENTS, getTestResourceElement(), getTestCreateDate(),
			TEST_ISM_COMPLIES_WITH, getTestIsmDesVersion(), null, SecurityAttributesTest.getFixture(), null, null);
		assertEquals(resource, fullResource);
	}

	public void testLoad30Commit20() throws InvalidDDMSException {
		// Direct mapping works
		DDMSVersion.setCurrentVersion("3.0");
		Resource.Builder builder = getBaseBuilder();
		getInstance(builder, SUCCESS);

		// Transform back to 2.0 fails on 3.0-specific fields
		DDMSVersion.setCurrentVersion("2.0");
		getInstance(builder, "The Unknown element must not");

		// Wiping of 3.0-specific fields works
		builder.getPointOfContacts().clear();
		getInstance(builder, SUCCESS);
	}

	public void testLoad20Commit30() throws InvalidDDMSException {
		// Direct mapping works
		DDMSVersion.setCurrentVersion("2.0");
		Resource.Builder builder = getBaseBuilder();
		getInstance(builder, SUCCESS);

		// Transform up to 3.0 fails on 3.0-specific fields
		DDMSVersion.setCurrentVersion("3.0");
		getInstance(builder, "resourceElement must exist");

		// Adding 3.0-specific fields works
		builder.setResourceElement(getTestResourceElement());
		builder.setCreateDate(getTestCreateDate());
		builder.setIsmDESVersion(getTestIsmDesVersion());
		builder.getSecurityAttributes().setClassification("U");
		builder.getSecurityAttributes().setOwnerProducers(Util.getXsListAsList("USA"));
		getInstance(builder, SUCCESS);
	}

	public void testLoad30Commit31() throws InvalidDDMSException {
		// Direct mapping works
		DDMSVersion.setCurrentVersion("3.0");
		Resource.Builder builder = getBaseBuilder();
		getInstance(builder, SUCCESS);

		// Transform up to 3.1 fails on 3.0-specific fields
		DDMSVersion.setCurrentVersion("3.1");
		getInstance(builder, "nu.xom.ValidityException: cvc-attribute.4");

		// Adding 3.1-specific fields works
		builder.setIsmDESVersion(Integer.valueOf(5));
		getInstance(builder, SUCCESS);
	}

	public void testLoad31Commit41() throws InvalidDDMSException {
		// Direct mapping works
		DDMSVersion.setCurrentVersion("3.1");
		Resource.Builder builder = getBaseBuilder();
		getInstance(builder, SUCCESS);

		// Transform up to 4.1 fails on 3.1-specific fields
		DDMSVersion.setCurrentVersion("4.0.1");
		getInstance(builder, "Exactly 1 metacardInfo");

		// Adding 4.1-specific fields works
		builder.setNtkDESVersion(Integer.valueOf(7));
		builder.setIsmDESVersion(Integer.valueOf(9));
		builder.getMetacardInfo().getIdentifiers().get(0).setQualifier("qualifier");
		builder.getMetacardInfo().getIdentifiers().get(0).setValue("value");
		builder.getMetacardInfo().getDates().setCreated("2011-09-25");
		builder.getMetacardInfo().getPublishers().get(0).setEntityType("organization");
		builder.getMetacardInfo().getPublishers().get(0).getOrganization().setNames(Util.getXsListAsList("DISA"));
		getInstance(builder, SUCCESS);
	}

	public void testLoad41Commit50() throws InvalidDDMSException {
		// Direct mapping works
		DDMSVersion.setCurrentVersion("4.1");
		Resource.Builder builder = getBaseBuilder();
		getInstance(builder, SUCCESS);

		// Transform up to 5.0 fails on 4.1-specific fields
		DDMSVersion.setCurrentVersion("5.0");
		getInstance(builder, "At least 1 TSPI");

		// Adding 5.0-specific fields works
		builder.setResourceElement(null);
		builder.setCreateDate(null);
		builder.setIsmDESVersion(null);
		builder.setNtkDESVersion(null);
		builder.setCompliesWiths(null);
		builder.setSecurityAttributes(null);
		builder.setNoticeAttributes(null);
		builder.setSecurity(null);
		builder.getGeospatialCoverages().clear();
		getInstance(builder, SUCCESS);
	}

	public void testExtensibleAttributes() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();

			// Extensible attribute added
			ExtensibleAttributes attr = ExtensibleAttributesTest.getFixture();
			if (!version.isAtLeast("5.0")) {
				Resource.Builder builder = getBaseBuilder();
				builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attr));
				getInstance(builder, SUCCESS);
			}
		}
	}

	public void test20ExtensibleSizes() throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.setCurrentVersion("2.0");
		createComponents();
		String ismPrefix = PropertyReader.getPrefix("ism");

		// Base Case
		Resource component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null);
		assertNull(component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(0, component.getExtensibleAttributes().getAttributes().size());

		// ism:DESVersion in element
		Element element = getTestResourceNoHeader();
		Util.addAttribute(element, ismPrefix, Resource.DES_VERSION_NAME, version.getIsmNamespace(),
			String.valueOf(getTestIsmDesVersion()));
		component = new Resource(element);
		assertEquals(getTestIsmDesVersion(), component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(0, component.getExtensibleAttributes().getAttributes().size());

		// ism:classification in element
		element = getTestResourceNoHeader();
		Util.addAttribute(element, ismPrefix, SecurityAttributes.CLASSIFICATION_NAME, version.getIsmNamespace(), "U");
		component = new Resource(element);
		assertFalse(component.getSecurityAttributes().isEmpty());
		assertEquals(0, component.getExtensibleAttributes().getAttributes().size());

		// ddmsence:confidence in element
		element = getTestResourceNoHeader();
		Util.addAttribute(element, "ddmsence", "confidence", "http://ddmsence.urizone.net/", "95");
		component = new Resource(element);
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(1, component.getExtensibleAttributes().getAttributes().size());

		// This can be a parameter or an extensible.
		Attribute icAttribute = new Attribute("ism:DESVersion", version.getIsmNamespace(), "2");
		// This can be a securityAttribute or an extensible.
		Attribute secAttribute = new Attribute("ism:classification", version.getIsmNamespace(), "U");
		// This can be an extensible.
		Attribute uniqueAttribute = new Attribute("ddmsence:confidence", "http://ddmsence.urizone.net/", "95");
		List<Attribute> exAttr = new ArrayList<Attribute>();

		// icAttribute as parameter, uniqueAttribute as extensibleAttribute
		exAttr.clear();
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, getTestIsmDesVersion(),
			getTestNtkDesVersion(), null, null, new ExtensibleAttributes(exAttr));
		assertEquals(getTestIsmDesVersion(), component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(1, component.getExtensibleAttributes().getAttributes().size());

		// icAttribute and uniqueAttribute as extensibleAttributes
		exAttr.clear();
		exAttr.add(new Attribute(icAttribute));
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, new ExtensibleAttributes(exAttr));
		assertNull(component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(2, component.getExtensibleAttributes().getAttributes().size());

		// secAttribute as securityAttribute, uniqueAttribute as extensibleAttribute
		exAttr.clear();
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, null, null,
			SecurityAttributesTest.getFixture(), null, new ExtensibleAttributes(exAttr));
		assertNull(component.getIsmDESVersion());
		assertFalse(component.getSecurityAttributes().isEmpty());
		assertEquals(1, component.getExtensibleAttributes().getAttributes().size());

		// secAttribute and uniqueAttribute as extensibleAttribute
		exAttr.clear();
		exAttr.add(new Attribute(secAttribute));
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, new ExtensibleAttributes(exAttr));
		assertNull(component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(2, component.getExtensibleAttributes().getAttributes().size());

		// icAttribute as parameter, secAttribute as securityAttribute, uniqueAttribute as extensibleAttribute
		exAttr.clear();
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, getTestIsmDesVersion(),
			getTestNtkDesVersion(), SecurityAttributesTest.getFixture(), null, new ExtensibleAttributes(exAttr));
		assertEquals(getTestIsmDesVersion(), component.getIsmDESVersion());
		assertFalse(component.getSecurityAttributes().isEmpty());
		assertEquals(1, component.getExtensibleAttributes().getAttributes().size());

		// icAttribute as parameter, secAttribute and uniqueAttribute as extensibleAttributes
		exAttr.clear();
		exAttr.add(new Attribute(secAttribute));
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, getTestIsmDesVersion(),
			getTestNtkDesVersion(), null, null, new ExtensibleAttributes(exAttr));
		assertEquals(getTestIsmDesVersion(), component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(2, component.getExtensibleAttributes().getAttributes().size());

		// secAttribute as securityAttribute, icAttribute and uniqueAttribute as extensibleAttributes
		exAttr.clear();
		exAttr.add(new Attribute(icAttribute));
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, null, null, null, null, null,
			SecurityAttributesTest.getFixture(), null, new ExtensibleAttributes(exAttr));
		assertNull(component.getIsmDESVersion());
		assertFalse(component.getSecurityAttributes().isEmpty());
		assertEquals(2, component.getExtensibleAttributes().getAttributes().size());

		// all three as extensibleAttributes
		exAttr.clear();
		exAttr.add(new Attribute(icAttribute));
		exAttr.add(new Attribute(secAttribute));
		exAttr.add(new Attribute(uniqueAttribute));
		component = new Resource(TEST_TOP_LEVEL_COMPONENTS, new ExtensibleAttributes(exAttr));
		assertNull(component.getIsmDESVersion());
		assertTrue(component.getSecurityAttributes().isEmpty());
		assertEquals(3, component.getExtensibleAttributes().getAttributes().size());
	}

	public void testExtensibleDuplicates() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			createComponents();

			if (version.isAtLeast("5.0"))
				continue;

			// IsmDESVersion in parameter AND extensible.
			List<Attribute> exAttr = new ArrayList<Attribute>();
			exAttr.add(new Attribute("ism:DESVersion", version.getIsmNamespace(), "2"));
			ExtensibleAttributes attributes = new ExtensibleAttributes(exAttr);
			Resource.Builder builder = getBaseBuilder();
			builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
			getInstance(builder, "The extensible attribute with the name, ism:DESVersion");

			if (version.isAtLeast("4.0.1")) {
				// NtkDESVersion in parameter AND extensible.
				exAttr = new ArrayList<Attribute>();
				exAttr.add(new Attribute("ntk:DESVersion", version.getNtkNamespace(), "2"));
				attributes = new ExtensibleAttributes(exAttr);
				builder = getBaseBuilder();
				builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
				getInstance(builder, "The extensible attribute with the name, ntk:DESVersion");
			}

			// classification in securityAttributes AND extensible.
			exAttr = new ArrayList<Attribute>();
			exAttr.add(new Attribute("ism:classification", version.getIsmNamespace(), "S"));
			attributes = new ExtensibleAttributes(exAttr);
			builder = getBaseBuilder();
			builder.getSecurityAttributes().setClassification("U");
			builder.setExtensibleAttributes(new ExtensibleAttributes.Builder(attributes));
			getInstance(builder, "The extensible attribute with the name, ism:classification");
		}
	}

	public void testExtensibleCardinality() throws InvalidDDMSException {
		DDMSVersion.setCurrentVersion("2.0");
		createComponents();

		// Too many in DDMS 2.0
		Resource.Builder builder = getBaseBuilder();
		builder.getExtensibleElements().get(0).setXml(ExtensibleElementTest.getFixtureElement().toXML());
		builder.getExtensibleElements().get(1).setXml(ExtensibleElementTest.getFixtureElement().toXML());
		getInstance(builder, "Only 1 extensible element must exist in DDMS 2.0.");

		// Okay later
		DDMSVersion.setCurrentVersion("3.0");
		builder = getBaseBuilder();
		builder.getExtensibleElements().get(0).setXml(ExtensibleElementTest.getFixtureElement().toXML());
		builder.getExtensibleElements().get(1).setXml(ExtensibleElementTest.getFixtureElement().toXML());
		getInstance(builder, SUCCESS);
	}

	public void test20DeclassManualReviewAttribute() throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.setCurrentVersion("2.0");
		createComponents();
		String ismNamespace = version.getIsmNamespace();

		Element element = getTestResourceNoHeader();
		Util.addAttribute(element, PropertyReader.getPrefix("ism"), SecurityAttributes.DECLASS_MANUAL_REVIEW_NAME,
			ismNamespace, "true");
		SecurityAttributesTest.getFixture().addTo(element);
		Resource resource = getInstance(element, SUCCESS);

		// ism:declassManualReview should not get picked up as an extensible attribute
		assertEquals(0, resource.getExtensibleAttributes().getAttributes().size());
	}
}