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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.ddms.extensible.ExtensibleElement;
import buri.ddmsence.ddms.format.Format;
import buri.ddmsence.ddms.metacard.MetacardInfo;
import buri.ddmsence.ddms.resource.Contributor;
import buri.ddmsence.ddms.resource.Creator;
import buri.ddmsence.ddms.resource.Dates;
import buri.ddmsence.ddms.resource.Identifier;
import buri.ddmsence.ddms.resource.Language;
import buri.ddmsence.ddms.resource.PointOfContact;
import buri.ddmsence.ddms.resource.Publisher;
import buri.ddmsence.ddms.resource.ResourceManagement;
import buri.ddmsence.ddms.resource.Rights;
import buri.ddmsence.ddms.resource.Source;
import buri.ddmsence.ddms.resource.Subtitle;
import buri.ddmsence.ddms.resource.Title;
import buri.ddmsence.ddms.resource.Type;
import buri.ddmsence.ddms.security.Security;
import buri.ddmsence.ddms.security.ism.ISMVocabulary;
import buri.ddmsence.ddms.security.ism.NoticeAttributes;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.Description;
import buri.ddmsence.ddms.summary.GeospatialCoverage;
import buri.ddmsence.ddms.summary.NonStateActor;
import buri.ddmsence.ddms.summary.RelatedResource;
import buri.ddmsence.ddms.summary.SubjectCoverage;
import buri.ddmsence.ddms.summary.TemporalCoverage;
import buri.ddmsence.ddms.summary.VirtualCoverage;
import buri.ddmsence.util.DDMSReader;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:resource (the top-level element of a DDMS metacard or assertion).
 * <br />
 * {@ddms.versions 11111}
 * 
 * <p>When generating HTML/Text output for a Resource, additional tags are generated listing the version of DDMSence
 * used to create the record (to help identify formatting bugs), and the version of DDMS. These lines are not required
 * (and may be removed). For example:</p>
 * 
 * <ul><code>
 * ddms.generator: DDMSence 1.0.0<br />
 * ddms.version: 3.0<br /><br />
 * &lt;meta name="ddms.generator" content="DDMSence 1.0.0" /&gt;<br />
 * &lt;meta name="ddms.version" content="3.0" /&gt;<br />
 * </code></ul></p>
 * 
 * {@table.header History}
 * 		<p>Starting in DDMS 3.0, resources have additional ISM attributes which did not exist in 2.0. However, the 2.0 schema
 * 		still allows "any" attributes on the Resource, so the 3.0 attribute values will be loaded if present.</p>
 * 		<p>Starting in DDMS 3.0, the ISM attributes explicitly defined in the schema should appear in the SecurityAttributes,
 * 		not the ExtensibleAttributes. Attempts to load them as ExtensibleAttributes will throw an InvalidDDMSException.
 * 		In DDMS 2.0, there are no ISM attributes explicitly defined in the schema, so you can load them in any way you
 * 		want. It is recommended that you load them as SecurityAttributes anyhow, for consistency with newer DDMS resources.
 * 		Please see the "Power Tips" on the Extensible Layer (on the DDMSence home page) for details.</p>
 * 		<p>The names of this component was made lowercase in DDMS 4.0.1.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:metacardInfo|1|00011}
 * 		{@child.info ddms:identifier|1..*|11111}
 * 		{@child.info ddms:title|1..*|11111}
 * 		{@child.info ddms:subtitle|0..*|11111}
 * 		{@child.info ddms:description|0..1|11111}
 * 		{@child.info ddms:language|0..*|11111}
 * 		{@child.info ddms:dates|0..1|11111}
 * 		{@child.info ddms:rights|0..1|11111}
 * 		{@child.info ddms:source|0..*|11111}
 * 		{@child.info ddms:type|0..*|11111}
 * 		{@child.info ddms:contributor|0..*|11111}
 * 		{@child.info ddms:creator|0..*|11111}
 * 		{@child.info ddms:pointOfContact|0..*|11111}
 * 		{@child.info ddms:publisher|0..*|11111}
 * 		{@child.info ddms:format|0..1|11111}
 * 		{@child.info ddms:subjectCoverage|0..1|11100}
 * 		{@child.info ddms:subjectCoverage|1..*|00011}
 * 		{@child.info ddms:virtualCoverage|0..*|11111}
 * 		{@child.info ddms:temporalCoverage|0..*|11111}
 * 		{@child.info ddms:geospatialCoverage|0..*|11111}
 * 		{@child.info ddms:relatedResource|0..*|11111}
 * 		{@child.info ddms:resourceManagement|0..1|00011}
 * 		{@child.info ddms:security|1|11110}
 * 		{@child.info any:&lt;<i>extensibleElements</i>&gt;|0..1|10000}
 * 		{@child.info any:&lt;<i>extensibleElements</i>&gt;|0..*|01110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:resourceElement|1|01110}
 * 		{@child.info ism:createDate|1|01110}
 * 		{@child.info ism:DESVersion|1|01110}
 * 		{@child.info ntk:DESVersion|1|00010}
 * 		{@child.info ism:compliesWith|0..*|00110}
 * 		{@child.info ddms:compliesWith|0..*|00001}
 *  	{@child.info ism:classification|1|01110}
 * 		{@child.info ism:ownerProducer|1..*|01110}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01110}
 * 		{@child.info ism:&lt;<i>noticeAttributes</i>&gt;|0..*|00010}  
 * 		{@child.info any:&lt;<i>extensibleAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of the element is correct.|Error|11111}
 * 		{@ddms.rule The cardinality all child components is enforced.|Error|11111}
 * 		{@ddms.rule At least 1 of creator, publisher, contributor, or pointOfContact must exist.|Error|11111}
 * 		{@ddms.rule All ddms:order attributes make a complete, consecutive set, starting at 1.|Error|11111}
 * 		{@ddms.rule ism:resourceElement must exist.|Error|01110}
 * 		{@ddms.rule ism:createDate must exist and adheres to a valid date format.|Error|01110}
 * 		{@ddms.rule ism:DESVersion must exist and is a valid Integer.|Error|01111}
 * 		{@ddms.rule ism:classification must exist.|Error|01110}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|01110}
 * 		{@ddms.rule ntk:DESVersion must exist and be a valid Integer.|Error|00010}
 * 		{@ddms.rule ism:compliesWith must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule ism:compliesWith must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule If set, ism:compliesWith must contain valid tokens.|Error|11110}
 * 		{@ddms.rule The resource must not have ISM or NTK attributes.|Error|00001}
 * 		{@ddms.rule The resource must not have extensible elements or attributes.|Error|00001}			
 * 		{@ddms.rule Warnings from any notice attributes are claimed by this component.|Warning|11111}
 * 		{@ddms.rule ism:externalNotice  may cause issues for DDMS 4.0.1 systems.|Warning|00010}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Resource extends AbstractBaseComponent {

	private MetacardInfo _metacardInfo = null;
	private List<Identifier> _identifiers = new ArrayList<Identifier>();
	private List<Title> _titles = new ArrayList<Title>();
	private List<Subtitle> _subtitles = new ArrayList<Subtitle>();
	private Description _description = null;
	private List<Language> _languages = new ArrayList<Language>();
	private Dates _dates = null;
	private Rights _rights = null;
	private List<Source> _sources = new ArrayList<Source>();
	private List<Type> _types = new ArrayList<Type>();
	private List<Creator> _creators = new ArrayList<Creator>();
	private List<Publisher> _publishers = new ArrayList<Publisher>();
	private List<Contributor> _contributors = new ArrayList<Contributor>();
	private List<PointOfContact> _pointOfContacts = new ArrayList<PointOfContact>();
	private Format _format = null;
	private List<SubjectCoverage> _subjectCoverages = new ArrayList<SubjectCoverage>();
	private List<VirtualCoverage> _virtualCoverages = new ArrayList<VirtualCoverage>();
	private List<TemporalCoverage> _temporalCoverages = new ArrayList<TemporalCoverage>();
	private List<GeospatialCoverage> geospatialCoverages = new ArrayList<GeospatialCoverage>();
	private List<RelatedResource> _relatedResources = new ArrayList<RelatedResource>();
	private ResourceManagement _resourceManagement = null;
	private Security _security = null;
	private List<ExtensibleElement> _extensibleElements = new ArrayList<ExtensibleElement>();
	private List<IDDMSComponent> _orderedList = new ArrayList<IDDMSComponent>();

	private XMLGregorianCalendar _createDate = null;
	List<String> _compliesWiths = null;
	private Integer _ismDESVersion = null;
	private Integer _ntkDESVersion = null;
	private NoticeAttributes _noticeAttributes = null;
	private SecurityAttributes _securityAttributes = null;
	private ExtensibleAttributes _extensibleAttributes = null;

	/** The attribute name for resource element flag */
	protected static final String RESOURCE_ELEMENT_NAME = "resourceElement";

	/** The attribute name for create date */
	protected static final String CREATE_DATE_NAME = "createDate";

	/** The attribute name for the compliesWith attribute */
	public static final String COMPLIES_WITH_NAME = "compliesWith";

	/** The attribute name for DES version */
	public static final String DES_VERSION_NAME = "DESVersion";

	private static final Set<String> ALL_IC_ATTRIBUTES = new HashSet<String>();
	static {
		ALL_IC_ATTRIBUTES.add(RESOURCE_ELEMENT_NAME);
		ALL_IC_ATTRIBUTES.add(CREATE_DATE_NAME);
		ALL_IC_ATTRIBUTES.add(DES_VERSION_NAME);
	}

	/** A set of all Resource attribute names which should not be converted into ExtensibleAttributes */
	public static final Set<String> NON_EXTENSIBLE_NAMES = Collections.unmodifiableSet(ALL_IC_ATTRIBUTES);

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * <p>Starting in DDMS 3.0, resources have additional ISM attributes which did not exist in 2.0. However, the 2.0
	 * schema still allows "any" attributes on the Resource, so the 3.0 attribute values will be loaded if present.</p>
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Resource(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			String namespace = element.getNamespaceURI();
			String ismNamespace = getDDMSVersion().getIsmNamespace();

			String createDate = getAttributeValue(CREATE_DATE_NAME, ismNamespace);
			if (!Util.isEmpty(createDate))
				_createDate = getFactory().newXMLGregorianCalendar(createDate);
			String compliesNamespace = getDDMSVersion().isAtLeast("5.0") ? getNamespace() : ismNamespace;
			_compliesWiths = Util.getXsListAsList(getAttributeValue(COMPLIES_WITH_NAME, compliesNamespace));
			String ismDESVersion = element.getAttributeValue(DES_VERSION_NAME, ismNamespace);
			if (!Util.isEmpty(ismDESVersion)) {
				_ismDESVersion = Integer.valueOf(ismDESVersion);
			}
			if (getDDMSVersion().isAtLeast("4.0.1")) {
				String ntkDESVersion = element.getAttributeValue(DES_VERSION_NAME, getDDMSVersion().getNtkNamespace());
				if (!Util.isEmpty(ntkDESVersion)) {
					_ntkDESVersion = Integer.valueOf(ntkDESVersion);
				}
			}
			_noticeAttributes = new NoticeAttributes(element);
			_securityAttributes = new SecurityAttributes(element);
			_extensibleAttributes = new ExtensibleAttributes(element);

			DDMSVersion version = getDDMSVersion();

			// Metacard Set
			Element component = getChild(MetacardInfo.getName(version));
			if (component != null) {
				_metacardInfo = new MetacardInfo(component);
			}
			// Resource Set
			Elements components = element.getChildElements(Identifier.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_identifiers.add(new Identifier(components.get(i)));
			components = element.getChildElements(Title.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_titles.add(new Title(components.get(i)));
			components = element.getChildElements(Subtitle.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_subtitles.add(new Subtitle(components.get(i)));
			component = getChild(Description.getName(version));
			if (component != null)
				_description = new Description(component);
			components = element.getChildElements(Language.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_languages.add(new Language(components.get(i)));
			component = getChild(Dates.getName(version));
			if (component != null)
				_dates = new Dates(component);
			component = getChild(Rights.getName(version));
			if (component != null)
				_rights = new Rights(component);
			components = element.getChildElements(Source.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_sources.add(new Source(components.get(i)));
			components = element.getChildElements(Type.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_types.add(new Type(components.get(i)));
			components = element.getChildElements(Creator.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_creators.add(new Creator(components.get(i)));
			components = element.getChildElements(Publisher.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_publishers.add(new Publisher(components.get(i)));
			components = element.getChildElements(Contributor.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_contributors.add(new Contributor(components.get(i)));
			components = element.getChildElements(PointOfContact.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_pointOfContacts.add(new PointOfContact(components.get(i)));

			// Format Set
			component = getChild(Format.getName(version));
			if (component != null)
				_format = new Format(component);

			// Summary Set
			components = element.getChildElements(SubjectCoverage.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_subjectCoverages.add(new SubjectCoverage(components.get(i)));
			components = element.getChildElements(VirtualCoverage.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_virtualCoverages.add(new VirtualCoverage(components.get(i)));
			components = element.getChildElements(TemporalCoverage.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				_temporalCoverages.add(new TemporalCoverage(components.get(i)));
			components = element.getChildElements(GeospatialCoverage.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				geospatialCoverages.add(new GeospatialCoverage(components.get(i)));
			components = element.getChildElements(RelatedResource.getName(version), namespace);
			for (int i = 0; i < components.size(); i++)
				loadRelatedResource(components.get(i));

			// Resource Set again
			component = getChild(ResourceManagement.getName(version));
			if (component != null)
				_resourceManagement = new ResourceManagement(component);

			// Security Set
			component = getChild(Security.getName(version));
			if (component != null) {
				_security = new Security(component);

				// Extensible Layer

				// We use the security component to locate the extensible layer. If it is null, this resource is going
				// to fail validation anyhow (or we're in DDMS 5.0, which doesn't have an extensible layer, so we skip 
				// the extensible layer.
				int index = 0;
				Elements allElements = element.getChildElements();
				while (allElements.get(index) != component) {
					index++;
				}
				for (int i = index + 1; i < allElements.size(); i++)
					_extensibleElements.add(new ExtensibleElement(allElements.get(i)));
			}
			populatedOrderedList();
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Helper method to convert element-based related resources into components. In DDMS 4.0.1, there is a
	 * one-to-one correlation between the two. In DDMS 2.0, 3.0, or 3.1, the top-level ddms:RelatedResources
	 * element might contain more than 1 ddms:relatedResource. In the latter case, each ddms:relatedResource
	 * must be mediated into a separate RelatedResource instance.
	 * 
	 * @param resource the top-level element
	 */
	private void loadRelatedResource(Element resource) throws InvalidDDMSException {
		Elements children = resource.getChildElements(RelatedResource.OLD_INNER_NAME, getNamespace());
		if (children.size() <= 1) {
			_relatedResources.add(new RelatedResource(resource));
		}
		else {
			for (int i = 0; i < children.size(); i++) {
				Element copy = new Element(resource);
				copy.removeChildren();
				copy.appendChild(new Element(children.get(i)));
				_relatedResources.add(new RelatedResource(copy));
			}
		}
	}

	/**
	 * Constructor for creating a DDMS 2.0 Resource from raw data.
	 * 
	 * <p>This helper constructor merely calls the fully-parameterized version. Attempts to use it with DDMS 3.0
	 * (or higher) components will fail, because some required attributes are missing.</p>
	 * 
	 * @param topLevelComponents a list of top level components
	 * @param extensibleAttributes any extensible attributes
	 */
	public Resource(List<IDDMSComponent> topLevelComponents, ExtensibleAttributes extensibleAttributes)
		throws InvalidDDMSException {
		this(topLevelComponents, null, null, null, null, null, null, null, extensibleAttributes);
	}

	/**
	 * Constructor for creating a DDMS 3.0 Resource from raw data.
	 * 
	 * <p>This helper constructor merely calls the fully-parameterized version. Attempts to use it with DDMS 3.1
	 * (or higher) components will fail, because some required attributes are missing.</p>
	 * 
	 * @param topLevelComponents a list of top level components
	 * @param resourceElement value of the resourceElement attribute
	 * @param createDate the create date as an xs:date (YYYY-MM-DD)
	 * @param ismDESVersion the DES Version as an Integer
	 * @param securityAttributes any security attributes
	 * @param extensibleAttributes any extensible attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed, or if one of the components
	 *         does not belong at the top-level of the Resource.
	 */
	public Resource(List<IDDMSComponent> topLevelComponents, Boolean resourceElement, String createDate,
		Integer ismDESVersion, SecurityAttributes securityAttributes, ExtensibleAttributes extensibleAttributes)
		throws InvalidDDMSException {
		this(topLevelComponents, resourceElement, createDate, null, ismDESVersion, null, securityAttributes, null,
			extensibleAttributes);
	}

	/**
	 * Constructor for creating a DDMS 3.1 Resource from raw data.
	 * 
	 * <p>This helper constructor merely calls the fully-parameterized version. Attempts to use it with DDMS 4.0.1
	 * (or higher) components will fail, because some required attributes are missing.</p>
	 * 
	 * @param topLevelComponents a list of top level components
	 * @param resourceElement value of the resourceElement attribute
	 * @param createDate the create date as an xs:date (YYYY-MM-DD)
	 * @param compliesWiths shows what rulesets this resource complies with
	 * @param ismDESVersion the DES Version as an Integer
	 * @param securityAttributes any security attributes
	 * @param extensibleAttributes any extensible attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed, or if one of the components
	 *         does not belong at the top-level of the Resource.
	 */
	public Resource(List<IDDMSComponent> topLevelComponents, Boolean resourceElement, String createDate,
		List<String> compliesWiths, Integer ismDESVersion, SecurityAttributes securityAttributes,
		ExtensibleAttributes extensibleAttributes) throws InvalidDDMSException {
		this(topLevelComponents, resourceElement, createDate, compliesWiths, ismDESVersion, null, securityAttributes,
			null, extensibleAttributes);
	}

	/**
	 * Constructor for creating a DDMS resource of any version from raw data.
	 * 
	 * <p>The other data-driven constructors call this one.</p>
	 * 
	 * <p> Passing the top-level components in as a list is a compromise between a constructor with over twenty
	 * parameters, and the added complexity of a step-by-step factory/builder approach. If any component is not a
	 * top-level component, an InvalidDDMSException will be thrown. </p>
	 * 
	 * <p> The order of different types of components does not matter here (a security component could be the first
	 * component in the list). However, if multiple instances of the same component type exist in the list (such as
	 * multiple identifier components), those components will be stored and output in the order of the list. If only 1
	 * instance can be supported, the last one in the list will be the one used. </p>
	 * 
	 * <p>Starting in DDMS 3.0, resources have additional ISM attributes which did not exist in 2.0. However, the 2.0
	 * schema still allows "any" attributes on the Resource, so the attribute values will be loaded if present. </p>
	 * 
	 * @param topLevelComponents a list of top level components
	 * @param resourceElement value of the resourceElement attribute
	 * @param createDate the create date as an xs:date (YYYY-MM-DD)
	 * @param compliesWiths shows what rule sets this resource complies with
	 * @param ismDESVersion the DES Version as an Integer
	 * @param ntkDESVersion the DES Version as an Integer
	 * @param securityAttributes any security attributes
	 * @param noticeAttributes any notice attributes
	 * @param extensibleAttributes any extensible attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed, or if one of the components
	 *         does not belong at the top-level of the Resource.
	 */
	public Resource(List<IDDMSComponent> topLevelComponents, Boolean resourceElement, String createDate,
		List<String> compliesWiths, Integer ismDESVersion, Integer ntkDESVersion,
		SecurityAttributes securityAttributes, NoticeAttributes noticeAttributes,
		ExtensibleAttributes extensibleAttributes) throws InvalidDDMSException {
		try {
			if (topLevelComponents == null)
				topLevelComponents = Collections.emptyList();
			if (compliesWiths == null)
				compliesWiths = Collections.emptyList();

			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String ismPrefix = PropertyReader.getPrefix("ism");
			String ismNamespace = version.getIsmNamespace();
			String ntkPrefix = PropertyReader.getPrefix("ntk");
			String ntkNamespace = version.getNtkNamespace();
			String virtPrefix = PropertyReader.getPrefix("virt");
			String virtNamespace = version.getVirtNamespace();
			Element element = Util.buildDDMSElement(Resource.getName(version), null);
			if (!Util.isEmpty(ntkNamespace))
				element.addNamespaceDeclaration(ntkPrefix, ntkNamespace);
			element.addNamespaceDeclaration(ismPrefix, ismNamespace);
			if (!Util.isEmpty(virtNamespace))
				element.addNamespaceDeclaration(virtPrefix, virtNamespace);
			// Attributes
			_compliesWiths = compliesWiths;
			if (!compliesWiths.isEmpty()) {
				if (version.isAtLeast("5.0"))
					Util.addDDMSAttribute(element, COMPLIES_WITH_NAME, Util.getXsList(compliesWiths));
				else
					Util.addAttribute(element, ismPrefix, COMPLIES_WITH_NAME, ismNamespace, Util.getXsList(compliesWiths));
			}
			if (ntkDESVersion != null) {
				_ntkDESVersion = ntkDESVersion;
				Util.addAttribute(element, ntkPrefix, DES_VERSION_NAME, ntkNamespace, ntkDESVersion.toString());
			}
			if (resourceElement != null) {
				Util.addAttribute(element, ismPrefix, RESOURCE_ELEMENT_NAME, ismNamespace,
					String.valueOf(resourceElement));
			}
			if (ismDESVersion != null) {
				_ismDESVersion = ismDESVersion;
				Util.addAttribute(element, ismPrefix, DES_VERSION_NAME, ismNamespace, ismDESVersion.toString());
			}
			if (!Util.isEmpty(createDate)) {
				try {
					_createDate = getFactory().newXMLGregorianCalendar(createDate);
				}
				catch (IllegalArgumentException e) {
					throw new InvalidDDMSException("The ism:createDate attribute must adhere to a valid date format.");
				}
				Util.addAttribute(element, ismPrefix, CREATE_DATE_NAME, version.getIsmNamespace(),
					getCreateDate().toXMLFormat());
			}
			_noticeAttributes = NoticeAttributes.getNonNullInstance(noticeAttributes);
			_noticeAttributes.addTo(element);
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
			_extensibleAttributes = ExtensibleAttributes.getNonNullInstance(extensibleAttributes);
			_extensibleAttributes.addTo(element);

			for (IDDMSComponent component : topLevelComponents) {
				if (component == null)
					continue;

				// Metacard Set
				if (component instanceof MetacardInfo)
					_metacardInfo = (MetacardInfo) component;
				// Resource Set
				else if (component instanceof Identifier)
					_identifiers.add((Identifier) component);
				else if (component instanceof Title)
					_titles.add((Title) component);
				else if (component instanceof Subtitle)
					_subtitles.add((Subtitle) component);
				else if (component instanceof Description)
					_description = (Description) component;
				else if (component instanceof Language)
					_languages.add((Language) component);
				else if (component instanceof Dates)
					_dates = (Dates) component;
				else if (component instanceof Rights)
					_rights = (Rights) component;
				else if (component instanceof Source)
					_sources.add((Source) component);
				else if (component instanceof Type)
					_types.add((Type) component);
				else if (component instanceof Creator)
					_creators.add((Creator) component);
				else if (component instanceof Publisher)
					_publishers.add((Publisher) component);
				else if (component instanceof Contributor)
					_contributors.add((Contributor) component);
				else if (component instanceof PointOfContact)
					_pointOfContacts.add((PointOfContact) component);
				// Format Set
				else if (component instanceof Format)
					_format = (Format) component;
				// Summary Set
				else if (component instanceof SubjectCoverage)
					_subjectCoverages.add((SubjectCoverage) component);
				else if (component instanceof VirtualCoverage)
					_virtualCoverages.add((VirtualCoverage) component);
				else if (component instanceof TemporalCoverage)
					_temporalCoverages.add((TemporalCoverage) component);
				else if (component instanceof GeospatialCoverage)
					geospatialCoverages.add((GeospatialCoverage) component);
				else if (component instanceof RelatedResource)
					_relatedResources.add((RelatedResource) component);
				// Resource Set again
				else if (component instanceof ResourceManagement)
					_resourceManagement = (ResourceManagement) component;
				// Security Set
				else if (component instanceof Security)
					_security = (Security) component;
				// Extensible Layer
				else if (component instanceof ExtensibleElement)
					_extensibleElements.add((ExtensibleElement) component);
				else
					throw new InvalidDDMSException(component.getName()
						+ " is not a valid top-level component in a resource.");
			}
			populatedOrderedList();
			for (IDDMSComponent component : getTopLevelComponents()) {
				element.appendChild(component.getXOMElementCopy());
			}
			setXOMElement(element, true);
			DDMSReader.validateWithSchema(version, toXML());
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Creates an ordered list of all the top-level components in this Resource, for ease of traversal.
	 */
	private void populatedOrderedList() {
		if (getMetacardInfo() != null)
			_orderedList.add(getMetacardInfo());
		_orderedList.addAll(getIdentifiers());
		_orderedList.addAll(getTitles());
		_orderedList.addAll(getSubtitles());
		if (getDescription() != null)
			_orderedList.add(getDescription());
		_orderedList.addAll(getLanguages());
		if (getDates() != null)
			_orderedList.add(getDates());
		if (getRights() != null)
			_orderedList.add(getRights());
		_orderedList.addAll(getSources());
		_orderedList.addAll(getTypes());
		_orderedList.addAll(getCreators());
		_orderedList.addAll(getPublishers());
		_orderedList.addAll(getContributors());
		_orderedList.addAll(getPointOfContacts());
		if (getFormat() != null)
			_orderedList.add(getFormat());
		_orderedList.addAll(getSubjectCoverages());
		_orderedList.addAll(getVirtualCoverages());
		_orderedList.addAll(getTemporalCoverages());
		_orderedList.addAll(getGeospatialCoverages());
		_orderedList.addAll(getRelatedResources());
		if (getResourceManagement() != null)
			_orderedList.add(getResourceManagement());
		if (getSecurity() != null)
			_orderedList.add(getSecurity());
		_orderedList.addAll(getExtensibleElements());
	}

	/**
	 * Performs a Schematron validation of the DDMS Resource, via the ISO Schematron skeleton stylesheets for XSLT1
	 * or XSLT2 processors. This action can only be performed on a DDMS Resource which is already valid according
	 * to the DDMS specification.
	 * 
	 * <p>The informational results of this validation are returned to the caller in a list of ValidationMessages of
	 * type "Warning" for reports and "Error" for failed asserts. These messages do NOT affect the validity of the
	 * underlying object model. The locator on the ValidationMessage will be the location attribute from the
	 * successful-report or failed-assert element.</p>
	 * 
	 * <p>Details about ISO Schematron can be found at: http://www.schematron.com/ </p>
	 * 
	 * @param schematronFile the file containing the ISO Schematron constraints. This file is transformed with the ISO
	 *        Schematron skeleton files.
	 * @return a list of ValidationMessages
	 * @throws XSLException if there are XSL problems transforming with stylesheets
	 * @throws IOException if there are problems reading or parsing the Schematron file
	 */
	public List<ValidationMessage> validateWithSchematron(File schematronFile) throws XSLException, IOException {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		XSLTransform schematronTransform = Util.buildSchematronTransform(schematronFile);
		Nodes nodes = schematronTransform.transform(new Document(getXOMElementCopy()));
		Document doc = XSLTransform.toDocument(nodes);

		XPathContext context = XPathContext.makeNamespaceContext(doc.getRootElement());
		String svrlNamespace = context.lookup("svrl");
		Nodes outputNodes = doc.query("//svrl:failed-assert | //svrl:successful-report", context);
		for (int i = 0; i < outputNodes.size(); i++) {
			if (outputNodes.get(i) instanceof Element) {
				Element outputElement = (Element) outputNodes.get(i);
				boolean isAssert = "failed-assert".equals(outputElement.getLocalName());
				String text = outputElement.getFirstChildElement("text", svrlNamespace).getValue();
				String locator = outputElement.getAttributeValue("location");
				messages.add(isAssert ? ValidationMessage.newError(text, locator) : ValidationMessage.newWarning(text,
					locator));
			}
		}
		return (messages);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		boolean isAtLeast30 = getDDMSVersion().isAtLeast("3.0");
		boolean isAtLeast401 = getDDMSVersion().isAtLeast("4.0.1");
		boolean isAtLeast50 = getDDMSVersion().isAtLeast("5.0");
		
		Util.requireDDMSQName(getXOMElement(), Resource.getName(getDDMSVersion()));
		if (getDDMSVersion().isAtLeast("4.0.1"))
			Util.requireBoundedChildCount(getXOMElement(), MetacardInfo.getName(getDDMSVersion()), 1, 1);
	
		if (getIdentifiers().size() < 1)
			throw new InvalidDDMSException("At least 1 identifier must exist.");
		if (getTitles().size() < 1)
			throw new InvalidDDMSException("At least 1 title must exist.");	
		if (getCreators().size() + getContributors().size() + getPublishers().size() + getPointOfContacts().size() == 0)
			throw new InvalidDDMSException(
				"At least 1 producer (creator, contributor, publisher, or pointOfContact) must exist.");
		Util.requireBoundedChildCount(getXOMElement(), Description.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Dates.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Rights.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Format.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), ResourceManagement.getName(getDDMSVersion()), 0, 1);
		if (isAtLeast401) {
			if (getSubjectCoverages().size() < 1)
				throw new InvalidDDMSException("At least 1 subjectCoverage must exist.");
		}
		else
			Util.requireBoundedChildCount(getXOMElement(), SubjectCoverage.getName(getDDMSVersion()), 1, 1);
		if (!isAtLeast50)
			Util.requireBoundedChildCount(getXOMElement(), Security.getName(getDDMSVersion()), 1, 1);
		if (!isAtLeast30 && getExtensibleElements().size() > 1) {
			throw new InvalidDDMSException("Only 1 extensible element must exist in DDMS 2.0.");
		}
		
		validateOrderAttributes();
		if (isAtLeast30 && !isAtLeast50) {
			Util.requireDDMSValue(RESOURCE_ELEMENT_NAME, isResourceElement());
			Util.requireDDMSValue(CREATE_DATE_NAME, getCreateDate());
			if (!getCreateDate().getXMLSchemaType().equals(DatatypeConstants.DATE))
				throw new InvalidDDMSException("The createDate must be in the xs:date format (YYYY-MM-DD).");
			Util.requireDDMSValue("ism:" + DES_VERSION_NAME, getIsmDESVersion());
			Util.requireDDMSValue("security attributes", getSecurityAttributes());
			getSecurityAttributes().requireClassification();
		}			
		if (isAtLeast401 && !isAtLeast50) {
			Util.requireDDMSValue("ntk:" + DES_VERSION_NAME, getNtkDESVersion());
		}
		
		if (!getDDMSVersion().isAtLeast("3.1") && !getCompliesWiths().isEmpty())
			throw new InvalidDDMSException("The compliesWith attribute must not be used until DDMS 3.1 or later.");
		if (getDDMSVersion().isAtLeast("3.1") && !isAtLeast50) {
			// ism:compliesWith
			for (String with : getCompliesWiths())
				ISMVocabulary.validateEnumeration(ISMVocabulary.CVE_COMPLIES_WITH, with);
		}

		if (isAtLeast50) {
			if (isResourceElement() != null || getCreateDate() != null || getIsmDESVersion() != null || getNtkDESVersion() != null
				|| !getSecurityAttributes().isEmpty() || !getNoticeAttributes().isEmpty())
				throw new InvalidDDMSException("The resource must not have ISM or NTK attributes, starting in DDMS 5.0.");
			if (!getExtensibleAttributes().isEmpty() || !getExtensibleElements().isEmpty())
				throw new InvalidDDMSException("The resource must not have extensible elements or attributes, starting in DDMS 5.0.");
		}

		super.validate();
	}

	/**
	 * Validates the ddms:order attributes on ddms:nonStateActor and ddms:geospatialCoverage elements. All elements
	 * in the document which specify the order attribute should be interpreted as entries in a single, ordered list.
	 * Values must be sequential, starting at 1, and may not contain duplicates.
	 * 
	 * @throws InvalidDDMSException if the orders do not make a unique consecutive list starting at 1.
	 */
	private void validateOrderAttributes() throws InvalidDDMSException {
		List<Integer> orders = new ArrayList<Integer>();
		for (GeospatialCoverage coverage : getGeospatialCoverages()) {
			if (coverage.getOrder() != null)
				orders.add(coverage.getOrder());
		}
		for (SubjectCoverage coverage : getSubjectCoverages()) {
			for (NonStateActor actor : coverage.getNonStateActors()) {
				if (actor.getOrder() != null)
					orders.add(actor.getOrder());
			}
		}
		Collections.sort(orders);
		for (int i = 0; i < orders.size(); i++) {
			Integer expectedValue = Integer.valueOf(i + 1);
			if (!expectedValue.equals(orders.get(i))) {
				throw new InvalidDDMSException("The ddms:order attributes throughout this resource must form "
					+ "a single, ordered list starting from 1.");
			}
		}
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (!getDDMSVersion().isAtLeast("5.0") && !getNoticeAttributes().isEmpty()) {
			addWarnings(getNoticeAttributes().getValidationWarnings(), true);
			if (getNoticeAttributes().isExternalReference() != null)
				addDdms40Warning("ism:externalNotice attribute");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (isResourceElement() != null)
			text.append(buildOutput(isHTML, localPrefix + RESOURCE_ELEMENT_NAME, String.valueOf(isResourceElement())));
		if (getCreateDate() != null)
			text.append(buildOutput(isHTML, localPrefix + CREATE_DATE_NAME, getCreateDate().toXMLFormat()));
		text.append(buildOutput(isHTML, localPrefix + COMPLIES_WITH_NAME, Util.getXsList(getCompliesWiths())));
		if (getIsmDESVersion() != null)
			text.append(buildOutput(isHTML, localPrefix + "ism." + DES_VERSION_NAME, String.valueOf(getIsmDESVersion())));
		if (getNtkDESVersion() != null)
			text.append(buildOutput(isHTML, localPrefix + "ntk." + DES_VERSION_NAME, String.valueOf(getNtkDESVersion())));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		text.append(getNoticeAttributes().getOutput(isHTML, localPrefix));
		text.append(getExtensibleAttributes().getOutput(isHTML, localPrefix));

		// Traverse top-level components, suppressing the resource prefix
		if (getMetacardInfo() != null)
			text.append(getMetacardInfo().getOutput(isHTML, "", ""));
		text.append(buildOutput(isHTML, "", getIdentifiers()));
		text.append(buildOutput(isHTML, "", getTitles()));
		text.append(buildOutput(isHTML, "", getSubtitles()));
		if (getDescription() != null)
			text.append(getDescription().getOutput(isHTML, "", ""));
		text.append(buildOutput(isHTML, "", getLanguages()));
		if (getDates() != null)
			text.append(getDates().getOutput(isHTML, "", ""));
		if (getRights() != null)
			text.append(getRights().getOutput(isHTML, "", ""));
		text.append(buildOutput(isHTML, "", getSources()));
		text.append(buildOutput(isHTML, "", getTypes()));
		text.append(buildOutput(isHTML, "", getCreators()));
		text.append(buildOutput(isHTML, "", getPublishers()));
		text.append(buildOutput(isHTML, "", getContributors()));
		text.append(buildOutput(isHTML, "", getPointOfContacts()));
		if (getFormat() != null)
			text.append(getFormat().getOutput(isHTML, "", ""));
		text.append(buildOutput(isHTML, "", getSubjectCoverages()));
		text.append(buildOutput(isHTML, "", getVirtualCoverages()));
		text.append(buildOutput(isHTML, "", getTemporalCoverages()));
		text.append(buildOutput(isHTML, "", getGeospatialCoverages()));
		text.append(buildOutput(isHTML, "", getRelatedResources()));
		if (getResourceManagement() != null)
			text.append(getResourceManagement().getOutput(isHTML, "", ""));
		if (getSecurity() != null)
			text.append(getSecurity().getOutput(isHTML, "", ""));
		text.append(buildOutput(isHTML, "", getExtensibleElements()));

		text.append(buildOutput(isHTML, "extensible.layer", String.valueOf(!getExtensibleElements().isEmpty())));
		text.append(buildOutput(isHTML, "ddms.generator", "DDMSence " + PropertyReader.getProperty("version")));
		text.append(buildOutput(isHTML, "ddms.version", getDDMSVersion().getVersion()));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Resource))
			return (false);
		Resource test = (Resource) obj;
		return (Util.nullEquals(isResourceElement(), test.isResourceElement())
			&& Util.nullEquals(getCreateDate(), test.getCreateDate())
			&& Util.listEquals(getCompliesWiths(), test.getCompliesWiths())
			&& Util.nullEquals(getIsmDESVersion(), test.getIsmDESVersion())
			&& Util.nullEquals(getNtkDESVersion(), test.getNtkDESVersion())
			&& getNoticeAttributes().equals(test.getNoticeAttributes())
			&& getExtensibleAttributes().equals(test.getExtensibleAttributes()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		if (isResourceElement() != null)
			result = 7 * result + isResourceElement().hashCode();
		if (getCreateDate() != null)
			result = 7 * result + getCreateDate().hashCode();
		result = 7 * result + getCompliesWiths().hashCode();
		if (getIsmDESVersion() != null)
			result = 7 * result + getIsmDESVersion().hashCode();
		if (getNtkDESVersion() != null)
			result = 7 * result + getNtkDESVersion().hashCode();
		result = 7 * result + getNoticeAttributes().hashCode();
		result = 7 * result + getExtensibleAttributes().hashCode();
		return (result);
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return (version.isAtLeast("4.0.1") ? "resource" : "Resource");
	}

	/**
	 * Accessor for the MetacardInfo component
	 */
	public MetacardInfo getMetacardInfo() {
		return (_metacardInfo);
	}

	/**
	 * Accessor for the identifier components. There will always be at least one.
	 */
	public List<Identifier> getIdentifiers() {
		return (Collections.unmodifiableList(_identifiers));
	}

	/**
	 * Accessor for the title components. There will always be at least one.
	 */
	public List<Title> getTitles() {
		return (Collections.unmodifiableList(_titles));
	}

	/**
	 * Accessor for the subtitle components
	 */
	public List<Subtitle> getSubtitles() {
		return (Collections.unmodifiableList(_subtitles));
	}

	/**
	 * Accessor for the description component
	 */
	public Description getDescription() {
		return (_description);
	}

	/**
	 * Accessor for the language components
	 */
	public List<Language> getLanguages() {
		return (Collections.unmodifiableList(_languages));
	}

	/**
	 * Accessor for the dates component. May return null.
	 */
	public Dates getDates() {
		return _dates;
	}

	/**
	 * Accessor for the rights component. May return null.
	 */
	public Rights getRights() {
		return _rights;
	}

	/**
	 * Accessor for the source components
	 */
	public List<Source> getSources() {
		return (Collections.unmodifiableList(_sources));
	}

	/**
	 * Accessor for the type components
	 */
	public List<Type> getTypes() {
		return (Collections.unmodifiableList(_types));
	}

	/**
	 * Accessor for a list of all Creator entities
	 */
	public List<Creator> getCreators() {
		return (Collections.unmodifiableList(_creators));
	}

	/**
	 * Accessor for a list of all Publisher entities
	 */
	public List<Publisher> getPublishers() {
		return (Collections.unmodifiableList(_publishers));
	}

	/**
	 * Accessor for a list of all Contributor entities
	 */
	public List<Contributor> getContributors() {
		return (Collections.unmodifiableList(_contributors));
	}

	/**
	 * Accessor for a list of all PointOfContact entities
	 */
	public List<PointOfContact> getPointOfContacts() {
		return (Collections.unmodifiableList(_pointOfContacts));
	}

	/**
	 * Accessor for the Format component. May return null.
	 */
	public Format getFormat() {
		return (_format);
	}

	/**
	 * Accessor for the subjectCoverage component
	 */
	public List<SubjectCoverage> getSubjectCoverages() {
		return _subjectCoverages;
	}

	/**
	 * Accessor for the virtualCoverage components
	 */
	public List<VirtualCoverage> getVirtualCoverages() {
		return (Collections.unmodifiableList(_virtualCoverages));
	}

	/**
	 * Accessor for the temporalCoverage components
	 */
	public List<TemporalCoverage> getTemporalCoverages() {
		return (Collections.unmodifiableList(_temporalCoverages));
	}

	/**
	 * Accessor for the geospatialCoverage components
	 */
	public List<GeospatialCoverage> getGeospatialCoverages() {
		return (Collections.unmodifiableList(geospatialCoverages));
	}

	/**
	 * Accessor for the RelatedResource components 
	 */
	public List<RelatedResource> getRelatedResources() {
		return (Collections.unmodifiableList(_relatedResources));
	}

	/**
	 * Accessor for the ResourceManagement component. May return null.
	 */
	public ResourceManagement getResourceManagement() {
		return (_resourceManagement);
	}

	/**
	 * Accessor for the security component. May return null.
	 */
	public Security getSecurity() {
		return (_security);
	}

	/**
	 * Accessor for the extensible layer elements.
	 */
	public List<ExtensibleElement> getExtensibleElements() {
		return (Collections.unmodifiableList(_extensibleElements));
	}

	/**
	 * Accessor for the resourceElement attribute. This may be null.
	 */
	public Boolean isResourceElement() {
		String value = getAttributeValue(RESOURCE_ELEMENT_NAME, getDDMSVersion().getIsmNamespace());
		if ("true".equals(value))
			return (Boolean.TRUE);
		if ("false".equals(value))
			return (Boolean.FALSE);
		return (null);
	}

	/**
	 * Accessor for the createDate date. Returns a copy. This may be null.
	 */
	public XMLGregorianCalendar getCreateDate() {
		return (_createDate == null ? null : getFactory().newXMLGregorianCalendar(_createDate.toXMLFormat()));
	}

	/**
	 * Accessor for the ISM compliesWith attribute.
	 */
	public List<String> getCompliesWiths() {
		return (Collections.unmodifiableList(_compliesWiths));
	}

	/**
	 * Accessor for the ISM DESVersion attribute. Because this attribute does not exist before DDMS 3.0, the accessor
	 * will return null for v2.0 Resource elements.
	 */
	public Integer getIsmDESVersion() {
		return (_ismDESVersion);
	}

	/**
	 * Accessor for the NTK DESVersion attribute.
	 */
	public Integer getNtkDESVersion() {
		return (_ntkDESVersion);
	}

	/**
	 * Accessor for an ordered list of the components in this Resource. Components which are missing are not represented
	 * in this list (no null entries).
	 */
	public List<IDDMSComponent> getTopLevelComponents() {
		return (Collections.unmodifiableList(_orderedList));
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		return (getTopLevelComponents());
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null even if the attributes are not set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}

	/**
	 * Accessor for the Notice Attributes. Will always be non-null even if the attributes are not set.
	 */
	public NoticeAttributes getNoticeAttributes() {
		return (_noticeAttributes);
	}

	/**
	 * Accessor for the extensible attributes. Will always be non-null, even if not set.
	 */
	public ExtensibleAttributes getExtensibleAttributes() {
		return (_extensibleAttributes);
	}

	/**
	 * Accesor for the datatype factory
	 */
	private static DatatypeFactory getFactory() {
		return (Util.getDataTypeFactory());
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -8581492714895157280L;
		private MetacardInfo.Builder _metacardInfo;
		private List<Identifier.Builder> _identifiers;
		private List<Title.Builder> _titles;
		private List<Subtitle.Builder> _subtitles;
		private Description.Builder _description;
		private List<Language.Builder> _languages;
		private Dates.Builder _dates;
		private Rights.Builder _rights;
		private List<Source.Builder> _sources;
		private List<Type.Builder> _types;
		private List<Creator.Builder> _creators;
		private List<Contributor.Builder> _contributors;
		private List<Publisher.Builder> _publishers;
		private List<PointOfContact.Builder> _pointOfContacts;
		private Format.Builder _format;
		private List<SubjectCoverage.Builder> _subjectCoverages;
		private List<VirtualCoverage.Builder> _virtualCoverages;
		private List<TemporalCoverage.Builder> _temporalCoverages;
		private List<GeospatialCoverage.Builder> _geospatialCoverages;
		private List<RelatedResource.Builder> _relatedResources;
		private ResourceManagement.Builder _resourceManagement;
		private Security.Builder _security;
		private List<ExtensibleElement.Builder> _extensibleElements;

		private Boolean _resourceElement;
		private String _createDate;
		private List<String> _compliesWiths;
		private Integer _ismDESVersion;
		private Integer _ntkDESVersion;
		private NoticeAttributes.Builder _noticeAttributes;
		private SecurityAttributes.Builder _securityAttributes;
		private ExtensibleAttributes.Builder _extensibleAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Resource resource) {
			for (IDDMSComponent component : resource.getTopLevelComponents()) {
				// Metacard Set
				if (component instanceof MetacardInfo)
					setMetacardInfo(new MetacardInfo.Builder((MetacardInfo) component));
				// Resource Set
				else if (component instanceof Identifier)
					getIdentifiers().add(new Identifier.Builder((Identifier) component));
				else if (component instanceof Title)
					getTitles().add(new Title.Builder((Title) component));
				else if (component instanceof Subtitle)
					getSubtitles().add(new Subtitle.Builder((Subtitle) component));
				else if (component instanceof Description)
					setDescription(new Description.Builder((Description) component));
				else if (component instanceof Language)
					getLanguages().add(new Language.Builder((Language) component));
				else if (component instanceof Dates)
					setDates(new Dates.Builder((Dates) component));
				else if (component instanceof Rights)
					setRights(new Rights.Builder((Rights) component));
				else if (component instanceof Source)
					getSources().add(new Source.Builder((Source) component));
				else if (component instanceof Type)
					getTypes().add(new Type.Builder((Type) component));
				else if (component instanceof Creator)
					getCreators().add(new Creator.Builder((Creator) component));
				else if (component instanceof Contributor)
					getContributors().add(new Contributor.Builder((Contributor) component));
				else if (component instanceof Publisher)
					getPublishers().add(new Publisher.Builder((Publisher) component));
				else if (component instanceof PointOfContact)
					getPointOfContacts().add(new PointOfContact.Builder((PointOfContact) component));

				// Format Set
				else if (component instanceof Format)
					setFormat(new Format.Builder((Format) component));
				// Summary Set
				else if (component instanceof SubjectCoverage)
					getSubjectCoverages().add(new SubjectCoverage.Builder((SubjectCoverage) component));
				else if (component instanceof VirtualCoverage)
					getVirtualCoverages().add(new VirtualCoverage.Builder((VirtualCoverage) component));
				else if (component instanceof TemporalCoverage)
					getTemporalCoverages().add(new TemporalCoverage.Builder((TemporalCoverage) component));
				else if (component instanceof GeospatialCoverage)
					getGeospatialCoverages().add(new GeospatialCoverage.Builder((GeospatialCoverage) component));
				else if (component instanceof RelatedResource)
					getRelatedResources().add(new RelatedResource.Builder((RelatedResource) component));
				// Resource Set again
				else if (component instanceof ResourceManagement)
					setResourceManagement(new ResourceManagement.Builder((ResourceManagement) component));

				// Security Set
				else if (component instanceof Security)
					setSecurity(new Security.Builder((Security) component));
				// Extensible Layer
				else if (component instanceof ExtensibleElement)
					getExtensibleElements().add(new ExtensibleElement.Builder((ExtensibleElement) component));
			}
			if (resource.getCreateDate() != null)
				setCreateDate(resource.getCreateDate().toXMLFormat());
			setResourceElement(resource.isResourceElement());
			setCompliesWiths(resource.getCompliesWiths());
			setIsmDESVersion(resource.getIsmDESVersion());
			setNtkDESVersion(resource.getNtkDESVersion());
			setSecurityAttributes(new SecurityAttributes.Builder(resource.getSecurityAttributes()));
			setNoticeAttributes(new NoticeAttributes.Builder(resource.getNoticeAttributes()));
			setExtensibleAttributes(new ExtensibleAttributes.Builder(resource.getExtensibleAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Resource commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<IDDMSComponent> topLevelComponents = new ArrayList<IDDMSComponent>();
			for (IBuilder builder : getChildBuilders()) {
				IDDMSComponent component = builder.commit();
				if (component != null)
					topLevelComponents.add(component);
			}
			return (new Resource(topLevelComponents, getResourceElement(), getCreateDate(), getCompliesWiths(),
				getIsmDESVersion(), getNtkDESVersion(), getSecurityAttributes().commit(),
				getNoticeAttributes().commit(), getExtensibleAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getChildBuilders())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList
				&& Util.isEmpty(getCreateDate())
				&& getResourceElement() == null
				&& getCompliesWiths().isEmpty()
				&& getIsmDESVersion() == null
				&& getNtkDESVersion() == null
				&& getSecurityAttributes().isEmpty()
				&& getNoticeAttributes().isEmpty()
				&& getExtensibleAttributes().isEmpty());
		}

		/**
		 * Convenience method to get every child Builder in this Builder.
		 * 
		 * @return a list of IBuilders
		 */
		private List<IBuilder> getChildBuilders() {
			List<IBuilder> list = new ArrayList<IBuilder>();
			list.addAll(getIdentifiers());
			list.addAll(getTitles());
			list.addAll(getSubtitles());
			list.addAll(getLanguages());
			list.addAll(getSources());
			list.addAll(getTypes());
			list.addAll(getCreators());
			list.addAll(getContributors());
			list.addAll(getPublishers());
			list.addAll(getPointOfContacts());
			list.addAll(getSubjectCoverages());
			list.addAll(getVirtualCoverages());
			list.addAll(getTemporalCoverages());
			list.addAll(getGeospatialCoverages());
			list.addAll(getRelatedResources());
			list.addAll(getExtensibleElements());
			list.add(getMetacardInfo());
			list.add(getDescription());
			list.add(getDates());
			list.add(getRights());
			list.add(getFormat());
			list.add(getResourceManagement());
			list.add(getSecurity());
			return (list);
		}

		/**
		 * Builder accessor for the metacardInfo
		 */
		public MetacardInfo.Builder getMetacardInfo() {
			if (_metacardInfo == null)
				_metacardInfo = new MetacardInfo.Builder();
			return _metacardInfo;
		}

		/**
		 * Builder accessor for the metacardInfo
		 */
		public void setMetacardInfo(MetacardInfo.Builder metacardInfo) {
			_metacardInfo = metacardInfo;
		}

		/**
		 * Builder accessor for the identifiers
		 */
		public List<Identifier.Builder> getIdentifiers() {
			if (_identifiers == null)
				_identifiers = new LazyList(Identifier.Builder.class);
			return _identifiers;
		}

		/**
		 * Builder accessor for the titles
		 */
		public List<Title.Builder> getTitles() {
			if (_titles == null)
				_titles = new LazyList(Title.Builder.class);
			return _titles;
		}

		/**
		 * Builder accessor for the subtitles
		 */
		public List<Subtitle.Builder> getSubtitles() {
			if (_subtitles == null)
				_subtitles = new LazyList(Subtitle.Builder.class);
			return _subtitles;
		}

		/**
		 * Builder accessor for the description
		 */
		public Description.Builder getDescription() {
			if (_description == null)
				_description = new Description.Builder();
			return _description;
		}

		/**
		 * Builder accessor for the description
		 */
		public void setDescription(Description.Builder description) {
			_description = description;
		}

		/**
		 * Builder accessor for the languages
		 */
		public List<Language.Builder> getLanguages() {
			if (_languages == null)
				_languages = new LazyList(Language.Builder.class);
			return _languages;
		}

		/**
		 * Builder accessor for the dates
		 */
		public Dates.Builder getDates() {
			if (_dates == null)
				_dates = new Dates.Builder();
			return _dates;
		}

		/**
		 * Builder accessor for the dates
		 */
		public void setDates(Dates.Builder dates) {
			_dates = dates;
		}

		/**
		 * Builder accessor for the rights
		 */
		public Rights.Builder getRights() {
			if (_rights == null)
				_rights = new Rights.Builder();
			return _rights;
		}

		/**
		 * Builder accessor for the rights
		 */
		public void setRights(Rights.Builder rights) {
			_rights = rights;
		}

		/**
		 * Builder accessor for the sources
		 */
		public List<Source.Builder> getSources() {
			if (_sources == null)
				_sources = new LazyList(Source.Builder.class);
			return _sources;
		}

		/**
		 * Builder accessor for the types
		 */
		public List<Type.Builder> getTypes() {
			if (_types == null)
				_types = new LazyList(Type.Builder.class);
			return _types;
		}

		/**
		 * Convenience accessor for all of the producers. This list does not grow dynamically.
		 */
		public List<IBuilder> getProducers() {
			List<IBuilder> producers = new ArrayList<IBuilder>();
			producers.addAll(getCreators());
			producers.addAll(getContributors());
			producers.addAll(getPublishers());
			producers.addAll(getPointOfContacts());
			return (producers);
		}

		/**
		 * Builder accessor for creators
		 */
		public List<Creator.Builder> getCreators() {
			if (_creators == null)
				_creators = new LazyList(Creator.Builder.class);
			return _creators;
		}

		/**
		 * Builder accessor for contributors
		 */
		public List<Contributor.Builder> getContributors() {
			if (_contributors == null)
				_contributors = new LazyList(Contributor.Builder.class);
			return _contributors;
		}

		/**
		 * Builder accessor for publishers
		 */
		public List<Publisher.Builder> getPublishers() {
			if (_publishers == null)
				_publishers = new LazyList(Publisher.Builder.class);
			return _publishers;
		}

		/**
		 * Builder accessor for points of contact
		 */
		public List<PointOfContact.Builder> getPointOfContacts() {
			if (_pointOfContacts == null)
				_pointOfContacts = new LazyList(PointOfContact.Builder.class);
			return _pointOfContacts;
		}

		/**
		 * Builder accessor for the format
		 */
		public Format.Builder getFormat() {
			if (_format == null)
				_format = new Format.Builder();
			return _format;
		}

		/**
		 * Builder accessor for the format
		 */
		public void setFormat(Format.Builder format) {
			_format = format;
		}

		/**
		 * Builder accessor for the subjectCoverages
		 */
		public List<SubjectCoverage.Builder> getSubjectCoverages() {
			if (_subjectCoverages == null)
				_subjectCoverages = new LazyList(SubjectCoverage.Builder.class);
			return _subjectCoverages;
		}

		/**
		 * Builder accessor for the virtualCoverages
		 */
		public List<VirtualCoverage.Builder> getVirtualCoverages() {
			if (_virtualCoverages == null)
				_virtualCoverages = new LazyList(VirtualCoverage.Builder.class);
			return _virtualCoverages;
		}

		/**
		 * Builder accessor for the temporalCoverages
		 */
		public List<TemporalCoverage.Builder> getTemporalCoverages() {
			if (_temporalCoverages == null)
				_temporalCoverages = new LazyList(TemporalCoverage.Builder.class);
			return _temporalCoverages;
		}

		/**
		 * Builder accessor for the geospatialCoverages
		 */
		public List<GeospatialCoverage.Builder> getGeospatialCoverages() {
			if (_geospatialCoverages == null)
				_geospatialCoverages = new LazyList(GeospatialCoverage.Builder.class);
			return _geospatialCoverages;
		}

		/**
		 * Builder accessor for the relatedResources
		 */
		public List<RelatedResource.Builder> getRelatedResources() {
			if (_relatedResources == null)
				_relatedResources = new LazyList(RelatedResource.Builder.class);
			return _relatedResources;
		}

		/**
		 * Builder accessor for the resourceManagement
		 */
		public ResourceManagement.Builder getResourceManagement() {
			if (_resourceManagement == null)
				_resourceManagement = new ResourceManagement.Builder();
			return _resourceManagement;
		}

		/**
		 * Builder accessor for the resourceManagement
		 */
		public void setResourceManagement(ResourceManagement.Builder resourceManagement) {
			_resourceManagement = resourceManagement;
		}

		/**
		 * Builder accessor for the security
		 */
		public Security.Builder getSecurity() {
			if (_security == null)
				_security = new Security.Builder();
			return _security;
		}

		/**
		 * Builder accessor for the security
		 */
		public void setSecurity(Security.Builder security) {
			_security = security;
		}

		/**
		 * Builder accessor for the extensibleElements
		 */
		public List<ExtensibleElement.Builder> getExtensibleElements() {
			if (_extensibleElements == null)
				_extensibleElements = new LazyList(ExtensibleElement.Builder.class);
			return _extensibleElements;
		}

		/**
		 * Builder accessor for the createDate attribute
		 */
		public String getCreateDate() {
			return _createDate;
		}

		/**
		 * Builder accessor for the createDate attribute
		 */
		public void setCreateDate(String createDate) {
			_createDate = createDate;
		}

		/**
		 * Accessor for the resourceElement attribute
		 */
		public Boolean getResourceElement() {
			return _resourceElement;
		}

		/**
		 * Accessor for the resourceElement attribute
		 */
		public void setResourceElement(Boolean resourceElement) {
			_resourceElement = resourceElement;
		}

		/**
		 * Builder accessor for the compliesWith attribute
		 */
		public List<String> getCompliesWiths() {
			if (_compliesWiths == null)
				_compliesWiths = new LazyList(String.class);
			return _compliesWiths;
		}

		/**
		 * Builder accessor for the compliesWith attribute
		 */
		public void setCompliesWiths(List<String> compliesWiths) {
			_compliesWiths = new LazyList(compliesWiths, String.class);
		}

		/**
		 * Builder accessor for the NTK DESVersion
		 */
		public Integer getNtkDESVersion() {
			return _ntkDESVersion;
		}

		/**
		 * Builder accessor for the NTK DESVersion
		 */
		public void setNtkDESVersion(Integer desVersion) {
			_ntkDESVersion = desVersion;
		}

		/**
		 * Builder accessor for the ISM DESVersion
		 */
		public Integer getIsmDESVersion() {
			return _ismDESVersion;
		}

		/**
		 * Builder accessor for the ISM DESVersion
		 */
		public void setIsmDESVersion(Integer desVersion) {
			_ismDESVersion = desVersion;
		}

		/**
		 * Builder accessor for the securityAttributes
		 */
		public SecurityAttributes.Builder getSecurityAttributes() {
			if (_securityAttributes == null)
				_securityAttributes = new SecurityAttributes.Builder();
			return _securityAttributes;
		}

		/**
		 * Builder accessor for the securityAttributes
		 */
		public void setSecurityAttributes(SecurityAttributes.Builder securityAttributes) {
			_securityAttributes = securityAttributes;
		}

		/**
		 * Builder accessor for the noticeAttributes
		 */
		public NoticeAttributes.Builder getNoticeAttributes() {
			if (_noticeAttributes == null)
				_noticeAttributes = new NoticeAttributes.Builder();
			return _noticeAttributes;
		}

		/**
		 * Builder accessor for the noticeAttributes
		 */
		public void setNoticeAttributes(NoticeAttributes.Builder noticeAttributes) {
			_noticeAttributes = noticeAttributes;
		}

		/**
		 * Builder accessor for the extensibleAttributes
		 */
		public ExtensibleAttributes.Builder getExtensibleAttributes() {
			if (_extensibleAttributes == null)
				_extensibleAttributes = new ExtensibleAttributes.Builder();
			return _extensibleAttributes;
		}

		/**
		 * Builder accessor for the extensibleAttributes
		 */
		public void setExtensibleAttributes(ExtensibleAttributes.Builder extensibleAttributes) {
			_extensibleAttributes = extensibleAttributes;
		}
	}
}