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
package buri.ddmsence.ddms.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractQualifierValue;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of the ddms:relatedResource component.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	<p>Before DDMS 4.0.1, ddms:RelatedResources was the top-level component (0-many in a resource) and contained
 * 		1 to many ddms:relatedResource components. Starting in DDMS 4.0.1, the ddms:RelatedResources component was
 * 		removed, and the ddms:relatedResource now contains all of the parent information (relationship and direction).</p>
 * 
 * 		<p>The element-based constructor for this class can automatically handle these cases, and will automatically
 * 		mediate the Text/HTML/XML output:</p>
 * 		<ul>
 * 		<li>A pre-DDMS 4.0.1 ddms:RelatedResources element containing 1 ddms:relatedResource.</li>
 * 		<li>A post-DDMS 4.0.1 ddms:relatedResource element.</li>
 * 		</ul>
 * 		<p>If you have a case where a pre-DDMS 4.0.1 ddms:RelatedResources element contained 5 ddms:relatedResource
 * 		elements, the Resource class will automatically mediate it to create 5 RelatedResource instances. If an
 * 		old-fashioned parent element containing multiple children is loaded in the element-based constructor,
 * 		only the first child will be processed, and a warning will be provided.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:link|1..*|11111}	
 * {@table.footer}
 * {@table.header Attributes}
 *  	{@child.info ddms:relationship|1|11111}
 * 		{@child.info ddms:direction|0..1|11111} 
 *  	{@child.info ddms:qualifier|1|11111}
 * 		{@child.info ddms:value|1|11111}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 *		{@ddms.rule ddms:relationship must exist, and must be a valid URI.|Error|11111}
 *		{@ddms.rule If set, ddms:direction must be a valid token.|Error|11111}
 *		{@ddms.rule ddms:qualifier must exist, and must be a valid URI.|Error|11111}
 *		{@ddms.rule ddms:value must exist.|Error|11111}
 *		{@ddms.rule At least 1 ddms:link exists.|Error|11111}
 *		{@ddms.rule Links must not contain security attributes.|Error|11111}
 * 		{@ddms.rule Parent component should not contain more than 1 ddms:relatedResource.|Warning|11100}
 * 		<p>Does NOT validate that the value is valid against the qualifier's vocabulary.</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class RelatedResource extends AbstractQualifierValue {

	private List<Link> _links = null;
	private SecurityAttributes _securityAttributes = null;

	/** The value for an inbound direction. */
	public static final String INBOUND_DIRECTION = "inbound";

	/** The value for an outbound direction. */
	public static final String OUTBOUND_DIRECTION = "outbound";

	/** The value for an bidirectional direction. */
	public static final String BIDRECTIONAL_DIRECTION = "bidirectional";

	/** The pre-DDMS 4.0.1 name of the nested resource elements */
	public static final String OLD_INNER_NAME = "RelatedResource";

	private static Set<String> RELATIONSHIP_DIRECTIONS = new HashSet<String>();
	static {
		RELATIONSHIP_DIRECTIONS.add(INBOUND_DIRECTION);
		RELATIONSHIP_DIRECTIONS.add(OUTBOUND_DIRECTION);
		RELATIONSHIP_DIRECTIONS.add(BIDRECTIONAL_DIRECTION);
	}

	private static final String RELATIONSHIP_NAME = "relationship";
	private static final String DIRECTION_NAME = "direction";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RelatedResource(Element element) throws InvalidDDMSException {
		// Defaults to qualifier-value over codespace-code.
		try {
			Util.requireDDMSValue("element", element);
			setXOMElement(element, false);
			Element innerElement = getInnerElement();
			if (innerElement != null) {
				_links = new ArrayList<Link>();
				Elements links = innerElement.getChildElements(Link.getName(getDDMSVersion()), getNamespace());
				for (int i = 0; i < links.size(); i++)
					_links.add(new Link(links.get(i)));
			}
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param links the xlinks
	 * @param relationship the relationship attribute
	 * @param direction the relationship direction
	 * @param qualifier the value of the qualifier attribute
	 * @param value the value of the value attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RelatedResource(List<Link> links, String relationship, String direction, String qualifier, String value,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		// Defaults to qualifier-value over codespace-code.
		try {
			if (links == null)
				links = Collections.emptyList();
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(RelatedResource.getName(version), null);
			Util.addDDMSAttribute(element, RELATIONSHIP_NAME, relationship);
			Util.addDDMSAttribute(element, DIRECTION_NAME, direction);
			Element innerElement = (version.isAtLeast("4.0.1") ? element : Util.buildDDMSElement(OLD_INNER_NAME, null));
			Util.addDDMSAttribute(innerElement, getQualifierName(), qualifier);
			Util.addDDMSAttribute(innerElement, getValueName(), value);
			for (Link link : links) {
				innerElement.appendChild(link.getXOMElementCopy());
			}

			if (!version.isAtLeast("4.0.1"))
				element.appendChild(innerElement);
			_links = links;
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
	 * Asserts that a direction is valid.
	 * 
	 * @param direction the string to check
	 * @throws InvalidDDMSException if the value is null, empty or invalid.
	 */
	public static void validateRelationshipDirection(String direction) throws InvalidDDMSException {
		Util.requireDDMSValue("relationship direction", direction);
		if (!RELATIONSHIP_DIRECTIONS.contains(direction))
			throw new InvalidDDMSException("The direction attribute must be one of " + RELATIONSHIP_DIRECTIONS);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), RelatedResource.getName(getDDMSVersion()));
		Util.requireDDMSValue("relationship attribute", getRelationship());
		Util.requireDDMSValidURI(getRelationship());		
		if (!Util.isEmpty(getDirection()))
			validateRelationshipDirection(getDirection());
		Util.requireDDMSValue("qualifier attribute", getQualifier());
		Util.requireDDMSValidURI(getQualifier());
		Util.requireDDMSValue("value attribute", getValue());
		if (getLinks().isEmpty())
			throw new InvalidDDMSException("At least 1 link must exist.");
		for (Link link : getLinks()) {
			if (!link.getSecurityAttributes().isEmpty())
				throw new InvalidDDMSException("Security attributes must not be applied to links in a related resource.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (!getDDMSVersion().isAtLeast("4.0.1")) {
			Elements elements = getXOMElement().getChildElements(OLD_INNER_NAME, getNamespace());
			if (elements.size() > 1)
				addWarning("A ddms:RelatedResources element contains more than 1 ddms:relatedResource. "
					+ "To ensure consistency between versions of DDMS, each ddms:RelatedResources element "
					+ "should contain only 1 ddms:RelatedResource. DDMSence will only process the first child.");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
			localPrefix += "RelatedResource.";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + RELATIONSHIP_NAME, getRelationship()));
		text.append(buildOutput(isHTML, localPrefix + DIRECTION_NAME, getDirection()));
		text.append(buildOutput(isHTML, localPrefix + getQualifierName(), getQualifier()));
		text.append(buildOutput(isHTML, localPrefix + getValueName(), getValue()));
		text.append(buildOutput(isHTML, localPrefix, getLinks()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getLinks());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof RelatedResource))
			return (false);
		RelatedResource test = (RelatedResource) obj;
		return (getRelationship().equals(test.getRelationship()) 
			&& getDirection().equals(test.getDirection()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getRelationship().hashCode();
		result = 7 * result + getDirection().hashCode();
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
		return (version.isAtLeast("4.0.1") ? "relatedResource" : "relatedResources");
	}

	/**
	 * Accessor for the element which contains the links, qualifier and value. Before DDMS 4.0.1,
	 * this is a wrapper element called ddms:RelatedResource. Starting in DDMS 4.0.1, it is the ddms:relatedResource
	 * element itself.
	 */
	private Element getInnerElement() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? getXOMElement() : getChild(OLD_INNER_NAME));
	}

	/**
	 * Accessor for the links (1 to many).
	 * 
	 * @return unmodifiable List
	 */
	public List<Link> getLinks() {
		return (Collections.unmodifiableList(_links));
	}

	/**
	 * Accessor for the relationship attribute
	 */
	public String getRelationship() {
		return (getAttributeValue(RELATIONSHIP_NAME));
	}

	/**
	 * Accessor for the direction attribute (may be empty)
	 */
	public String getDirection() {
		return (getAttributeValue(DIRECTION_NAME));
	}

	/**
	 * Accessor for the value of the qualifier attribute
	 */
	public String getQualifier() {
		Element innerElement = getInnerElement();
		String attrValue = innerElement.getAttributeValue(getQualifierName(), getNamespace());
		return (Util.getNonNullString(attrValue));
	}

	/**
	 * Accessor for the value of the value attribute
	 */
	public String getValue() {
		Element innerElement = getInnerElement();
		String attrValue = innerElement.getAttributeValue(getValueName(), getNamespace());
		return (Util.getNonNullString(attrValue));
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null even if the attributes are not set.
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
	public static class Builder extends AbstractQualifierValue.Builder {
		private static final long serialVersionUID = 5430464017408842022L;
		private String _relationship;
		private String _direction;
		private List<Link.Builder> _links;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(RelatedResource resource) {
			super(resource);
			setRelationship(resource.getRelationship());
			setDirection(resource.getDirection());
			for (Link link : resource.getLinks()) {
				getLinks().add(new Link.Builder(link));
			}
			setSecurityAttributes(new SecurityAttributes.Builder(resource.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public RelatedResource commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Link> links = new ArrayList<Link>();
			for (Link.Builder builder : getLinks()) {
				Link link = builder.commit();
				if (link != null)
					links.add(link);
			}
			return (new RelatedResource(links, getRelationship(), getDirection(), getQualifier(), getValue(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getLinks()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (super.isEmpty() && !hasValueInList
				&& Util.isEmpty(getRelationship())
				&& Util.isEmpty(getDirection())
				&& getSecurityAttributes().isEmpty());
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
		 * Builder accessor for the relationship attribute
		 */
		public String getRelationship() {
			return _relationship;
		}

		/**
		 * Builder accessor for the relationship attribute
		 */
		public void setRelationship(String relationship) {
			_relationship = relationship;
		}

		/**
		 * Builder accessor for the direction attribute
		 */
		public String getDirection() {
			return _direction;
		}

		/**
		 * Builder accessor for the direction attribute
		 */
		public void setDirection(String direction) {
			_direction = direction;
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