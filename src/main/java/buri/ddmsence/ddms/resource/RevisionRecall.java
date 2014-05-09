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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Text;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.ISMVocabulary;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.Link;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:revisionRecall.
 *  * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p>A revisionRecall element will either contain free child text describing the recall, or a set of link and details
 * elements.</p>
 * 
 * {@table.header History}
 *  	<p>The network and otherNetwork attributes originated from DDMS 4.x's import of IC-COMMON. IC-COMMON was replaced
 *  	by VIRT in DDMS 5.0, dropping otherNetwork, and moving network into the virt namespace.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:link|0..*|00011}
 * 		{@child.info ddms:details|0..*|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:revisionID|1|00011}
 * 		{@child.info ddms:revisionType|1|00011}
 * 		{@child.info network|0..1|00010}
 * 		{@child.info virt:network|0..1|00001}
 * 		{@child.info otherNetwork|0..1|00010}
 * 		{@child.info ism:classification|1|00011}
 * 		{@child.info ism:ownerProducer|1..*|00011}
 * 		{@child.info xlink:&lt;<i>xlinkAttributes</i>&gt;|0..*|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011} 
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule A valid component must not have both non-empty child text and nested elements.|Error|11111}
 * 		{@ddms.rule All ddms:links must have security attributes.|Error|11111}
 * 		{@ddms.rule The revisionID must be a valid Integer.|Error|11111}
 * 		{@ddms.rule The revisionType must be a valid type token.|Error|11111}
 * 		{@ddms.rule If set, xlink:type must have a value of "resource".|Error|11111}
 * 		{@ddms.rule If set, network or virt:network must be a valid network token.|Error|11111}
 * 		{@ddms.rule network and otherNetwork must not be used after the DDMS version in which they were removed.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * 		{@ddms.rule ism:classification must exist on any ddms:links.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist on any ddms:links.|Error|11111}
 * 		{@ddms.rule Warnings from any XLink attributes are claimed by this component.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class RevisionRecall extends AbstractBaseComponent {

	private List<Link> _links = null;
	private List<Details> _details = null;
	private Integer _revisionID = null;
	private XLinkAttributes _xlinkAttributes = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String FIXED_TYPE = "resource";

	/** The prefix of the network attributes */
	public static final String NO_PREFIX = "";

	/** The namespace of the network attributes */
	public static final String NO_NAMESPACE = "";

	private static final String REVISION_ID_NAME = "revisionID";
	private static final String REVISION_TYPE_NAME = "revisionType";
	private static final String NETWORK_NAME = "network";
	private static final String OTHER_NETWORK_NAME = "otherNetwork";

	private static Set<String> REVISION_TYPE_TYPES = new HashSet<String>();
	static {
		REVISION_TYPE_TYPES.add("ADMINISTRATIVE RECALL");
		REVISION_TYPE_TYPES.add("ADMINISTRATIVE REVISION");
		REVISION_TYPE_TYPES.add("SUBSTANTIVE RECALL");
		REVISION_TYPE_TYPES.add("SUBSTANTIVE REVISION");
	}

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RevisionRecall(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			_links = new ArrayList<Link>();
			Elements links = element.getChildElements(Link.getName(getDDMSVersion()), getNamespace());
			for (int i = 0; i < links.size(); i++) {
				_links.add(new Link(links.get(i)));
			}
			_details = new ArrayList<Details>();
			Elements details = element.getChildElements(Details.getName(getDDMSVersion()), getNamespace());
			for (int i = 0; i < details.size(); i++) {
				_details.add(new Details(details.get(i)));
			}
			String revisionID = element.getAttributeValue(REVISION_ID_NAME, getNamespace());
			if (!Util.isEmpty(revisionID)) {
				_revisionID = Integer.valueOf(revisionID);
			}
			_xlinkAttributes = new XLinkAttributes(element);
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data, based on links and details.
	 * 
	 * @param links associated links
	 * @param details associated details
	 * @param revisionID integer ID for this recall
	 * @param revisionType type of revision
	 * @param network the network
	 * @param otherNetwork another network
	 * @param xlinkAttributes simple xlink attributes
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RevisionRecall(List<Link> links, List<Details> details, Integer revisionID, String revisionType,
		String network, String otherNetwork, XLinkAttributes xlinkAttributes, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		this(null, links, details, revisionID, revisionType, network, otherNetwork, xlinkAttributes, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data, based on child text.
	 * 
	 * @param value the child text describing this revision
	 * @param revisionID integer ID for this recall
	 * @param revisionType type of revision
	 * @param network the network
	 * @param otherNetwork another network
	 * @param xlinkAttributes simple xlink attributes
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RevisionRecall(String value, Integer revisionID, String revisionType, String network, String otherNetwork,
		XLinkAttributes xlinkAttributes, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(value, null, null, revisionID, revisionType, network, otherNetwork, xlinkAttributes, securityAttributes);
	}

	/**
	 * Private constructor for creating a component from raw data.
	 * 
	 * @param value the child text describing this revision
	 * @param links associated links
	 * @param details associated details
	 * @param revisionID integer ID for this recall
	 * @param revisionType type of revision
	 * @param network the network
	 * @param otherNetwork another network
	 * @param xlinkAttributes simple xlink attributes
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	private RevisionRecall(String value, List<Link> links, List<Details> details, Integer revisionID,
		String revisionType, String network, String otherNetwork, XLinkAttributes xlinkAttributes,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			if (links == null)
				links = Collections.emptyList();
			if (details == null)
				details = Collections.emptyList();

			Element element = Util.buildDDMSElement(RevisionRecall.getName(DDMSVersion.getCurrentVersion()), value);
			for (Link link : links)
				element.appendChild(link.getXOMElementCopy());
			for (Details detail : details)
				element.appendChild(detail.getXOMElementCopy());
			if (revisionID != null) {
				_revisionID = revisionID;
				Util.addDDMSAttribute(element, REVISION_ID_NAME, revisionID.toString());
			}
			Util.addDDMSAttribute(element, REVISION_TYPE_NAME, revisionType);
			
			if (DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				String virtPrefix = PropertyReader.getPrefix("virt");
				String virtNamespace = DDMSVersion.getCurrentVersion().getVirtNamespace();
				Util.addAttribute(element, virtPrefix, NETWORK_NAME, virtNamespace, network);
			}
			else {
				Util.addAttribute(element, NO_PREFIX, NETWORK_NAME, NO_NAMESPACE, network);
			}
			Util.addAttribute(element, NO_PREFIX, OTHER_NETWORK_NAME, NO_NAMESPACE, otherNetwork);
			_links = links;
			_details = details;
			_xlinkAttributes = XLinkAttributes.getNonNullInstance(xlinkAttributes);
			_xlinkAttributes.addTo(element);
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSQName(getXOMElement(), RevisionRecall.getName(getDDMSVersion()));

		boolean hasChildText = false;
		for (int i = 0; i < getXOMElement().getChildCount(); i++) {
			Node child = getXOMElement().getChild(i);
			hasChildText = (hasChildText || (child instanceof Text && !Util.isEmpty(child.getValue().trim())));
		}
		boolean hasNestedElements = (!getLinks().isEmpty() || !getDetails().isEmpty());
		if (hasChildText && hasNestedElements) {
			throw new InvalidDDMSException(
				"A ddms:revisionRecall element must not have both child text and nested elements.");
		}		
		for (Link link : getLinks()) {
			Util.requireDDMSValue("link security attributes", link.getSecurityAttributes());
			link.getSecurityAttributes().requireClassification();
		}
		
		Util.requireDDMSValue("revision ID", getRevisionID());
		if (!REVISION_TYPE_TYPES.contains(getRevisionType()))
			throw new InvalidDDMSException("The revisionType attribute must be one of " + REVISION_TYPE_TYPES);
		if (!Util.isEmpty(getXLinkAttributes().getType()) && !getXLinkAttributes().getType().equals(FIXED_TYPE))
			throw new InvalidDDMSException("The type attribute must have a fixed value of \"" + FIXED_TYPE + "\".");
		if (!Util.isEmpty(getNetwork()))
			ISMVocabulary.requireValidNetwork(getNetwork());
		if (getDDMSVersion().isAtLeast("5.0")) {
			// Check for network is implicit in schema validation.
			if (!Util.isEmpty(getOtherNetwork()))
				throw new InvalidDDMSException("The otherNetwork attribute must not be used after DDMS 4.1.");
		}
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (getXLinkAttributes() != null)
			addWarnings(getXLinkAttributes().getValidationWarnings(), true);
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		boolean hasNestedElements = (!getLinks().isEmpty() || !getDetails().isEmpty());
		String localPrefix = buildPrefix(prefix, getName(), suffix);
		StringBuffer text = new StringBuffer();
		if (!hasNestedElements)
			text.append(buildOutput(isHTML, localPrefix, getValue()));
		text.append(buildOutput(isHTML, localPrefix + "." + REVISION_ID_NAME, String.valueOf(getRevisionID())));
		text.append(buildOutput(isHTML, localPrefix + "." + REVISION_TYPE_NAME, getRevisionType()));
		text.append(buildOutput(isHTML, localPrefix + "." + NETWORK_NAME, getNetwork()));
		text.append(buildOutput(isHTML, localPrefix + "." + OTHER_NETWORK_NAME, getOtherNetwork()));
		text.append(buildOutput(isHTML, localPrefix + ".", getLinks()));
		text.append(buildOutput(isHTML, localPrefix + ".", getDetails()));
		text.append(getXLinkAttributes().getOutput(isHTML, localPrefix + "."));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getLinks());
		list.addAll(getDetails());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof RevisionRecall))
			return (false);
		RevisionRecall test = (RevisionRecall) obj;
		return (getValue().equals(test.getValue())
			&& Util.nullEquals(getRevisionID(), test.getRevisionID()) 
			&& getRevisionType().equals(test.getRevisionType()) 
			&& getNetwork().equals(test.getNetwork())
			&& getOtherNetwork().equals(test.getOtherNetwork()) 
			&& getXLinkAttributes().equals(test.getXLinkAttributes()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getValue().hashCode();
		result = 7 * result + getRevisionID().hashCode();
		result = 7 * result + getRevisionType().hashCode();
		result = 7 * result + getNetwork().hashCode();
		result = 7 * result + getOtherNetwork().hashCode();
		result = 7 * result + getXLinkAttributes().hashCode();
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
		return ("revisionRecall");
	}

	/**
	 * Accessor for the list of Links.
	 */
	public List<Link> getLinks() {
		return (Collections.unmodifiableList(_links));
	}

	/**
	 * Accessor for the list of Details.
	 */
	public List<Details> getDetails() {
		return (Collections.unmodifiableList(_details));
	}

	/**
	 * Accessor for the value of the child text.
	 */
	public String getValue() {
		return (getXOMElement().getValue());
	}

	/**
	 * Accessor for the revisionID attribute.
	 */
	public Integer getRevisionID() {
		return (_revisionID);
	}

	/**
	 * Accessor for the revisionType attribute.
	 */
	public String getRevisionType() {
		return (getAttributeValue(REVISION_TYPE_NAME));
	}

	/**
	 * Accessor for the network attribute.
	 */
	public String getNetwork() {
		String namespace = getDDMSVersion().isAtLeast("5.0") ? getDDMSVersion().getVirtNamespace() : NO_NAMESPACE;
		return (getAttributeValue(NETWORK_NAME, namespace));
	}

	/**
	 * Accessor for the otherNetwork attribute.
	 */
	public String getOtherNetwork() {
		return (getAttributeValue(OTHER_NETWORK_NAME, NO_NAMESPACE));
	}

	/**
	 * Accessor for the XLink Attributes. Will always be non-null, even if it has no values set.
	 */
	public XLinkAttributes getXLinkAttributes() {
		return (_xlinkAttributes);
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null, even if it has no values set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 4325950371570699184L;
		private String _value;
		private List<Link.Builder> _links;
		private List<Details.Builder> _details;
		private Integer _revisionID;
		private String _revisionType;
		private String _network;
		private String _otherNetwork;
		private XLinkAttributes.Builder _xlinkAttributes;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(RevisionRecall recall) {
			for (Link link : recall.getLinks())
				getLinks().add(new Link.Builder(link));
			for (Details detail : recall.getDetails())
				getDetails().add(new Details.Builder(detail));
			if (recall.getLinks().isEmpty() && recall.getDetails().isEmpty())
				setValue(recall.getValue());
			setRevisionID(recall.getRevisionID());
			setRevisionType(recall.getRevisionType());
			setNetwork(recall.getNetwork());
			setOtherNetwork(recall.getOtherNetwork());
			setXLinkAttributes(new XLinkAttributes.Builder(recall.getXLinkAttributes()));
			setSecurityAttributes(new SecurityAttributes.Builder(recall.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public RevisionRecall commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Link> links = new ArrayList<Link>();
			for (IBuilder builder : getLinks()) {
				Link component = (Link) builder.commit();
				if (component != null)
					links.add(component);
			}
			List<Details> details = new ArrayList<Details>();
			for (IBuilder builder : getDetails()) {
				Details component = (Details) builder.commit();
				if (component != null)
					details.add(component);
			}
			return (new RevisionRecall(getValue(), links, details, getRevisionID(), getRevisionType(), getNetwork(),
				getOtherNetwork(), getXLinkAttributes().commit(), getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getLinks())
				hasValueInList = hasValueInList || !builder.isEmpty();
			for (IBuilder builder : getDetails())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList
				&& Util.isEmpty(getValue())
				&& getRevisionID() == null
				&& Util.isEmpty(getRevisionType())
				&& Util.isEmpty(getNetwork())
				&& Util.isEmpty(getOtherNetwork())
				&& getXLinkAttributes().isEmpty()
				&& getSecurityAttributes().isEmpty());				
		}

		/**
		 * Builder accessor for the value
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the value
		 */
		public void setValue(String value) {
			_value = value;
		}

		/**
		 * Builder accessor for the links
		 */
		public List<Link.Builder> getLinks() {
			if (_links == null)
				_links = new LazyList(Link.Builder.class);
			return _links;
		}

		/**
		 * Builder accessor for the details
		 */
		public List<Details.Builder> getDetails() {
			if (_details == null)
				_details = new LazyList(Details.Builder.class);
			return _details;
		}

		/**
		 * Builder accessor for the revisionID
		 */
		public Integer getRevisionID() {
			return _revisionID;
		}

		/**
		 * Builder accessor for the revisionID
		 */
		public void setRevisionID(Integer revisionID) {
			_revisionID = revisionID;
		}

		/**
		 * Builder accessor for the revisionType
		 */
		public String getRevisionType() {
			return _revisionType;
		}

		/**
		 * Builder accessor for the revisionType
		 */
		public void setRevisionType(String revisionType) {
			_revisionType = revisionType;
		}

		/**
		 * Builder accessor for the network
		 */
		public String getNetwork() {
			return _network;
		}

		/**
		 * Builder accessor for the network
		 */
		public void setNetwork(String network) {
			_network = network;
		}

		/**
		 * Builder accessor for the otherNetwork
		 */
		public String getOtherNetwork() {
			return _otherNetwork;
		}

		/**
		 * Builder accessor for the otherNetwork
		 */
		public void setOtherNetwork(String otherNetwork) {
			_otherNetwork = otherNetwork;
		}

		/**
		 * Builder accessor for the XLink Attributes
		 */
		public XLinkAttributes.Builder getXLinkAttributes() {
			if (_xlinkAttributes == null)
				_xlinkAttributes = new XLinkAttributes.Builder();
			return _xlinkAttributes;
		}

		/**
		 * Builder accessor for the XLink Attributes
		 */
		public void setXLinkAttributes(XLinkAttributes.Builder xlinkAttributes) {
			_xlinkAttributes = xlinkAttributes;
		}

		/**
		 * Builder accessor for the Security Attributes
		 */
		public SecurityAttributes.Builder getSecurityAttributes() {
			if (_securityAttributes == null)
				_securityAttributes = new SecurityAttributes.Builder();
			return _securityAttributes;
		}

		/**
		 * Builder accessor for the Security Attributes
		 */
		public void setSecurityAttributes(SecurityAttributes.Builder securityAttributes) {
			_securityAttributes = securityAttributes;
		}
	}
}