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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:geospatialCoverage.
 *  *  <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 *  	<p>Before DDMS 4.0.1, a geospatialCoverage element contains a nested GeospatialExtent element. Because
 * 		DDMS does not decorate this element with any special attributes, it is not implemented as a Java object.
 * 		Starting in DDMS 4.0.1, the GeospatialExtent wrapper has been removed.</p>
 * 		<p>The introduction of TSPI deprecated the boundingBox and verticalExtent components in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:geographicIdentifier|0..1|11111}
 * 		{@child.info ddms:boundingBox|0..1|11110}
 * 		{@child.info ddms:boundingGeometry|0..1|11111}
 * 		{@child.info ddms:postalAddress|0..1|11111}
 * 		{@child.info ddms:verticalExtent|0..1|11110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:precedence|0..1|00011}
 * 		{@child.info ddms:order|0..1|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule This component must contain at least 1 child element.|Error|11111}
 * 		{@ddms.rule Only 1 of each type of child element must be used.|Error|11111}
 * 		{@ddms.rule If a ddms:geographicIdentifier is used and contains a ddms:facilityIdentifier, no other child elements must be used.|Error|11111}
 *	 	{@ddms.rule ddms:order must not be used before the DDMS version in which it was introduced.|Error|11111}
 *		{@ddms.rule ddms:precedence must not be used before the DDMS version in which it was introduced.|Error|11111}
 *		{@ddms.rule If set, ddms:precedence must be a valid token.|Error|11111}
 *		{@ddms.rule If ddms:precedence exists, this component must contain a ddms:geographicIdentifier with a ddms:countryCode.|Error|11111}
 *		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		<p>Does not validate the value of the order attribute (this is done at the Resource level).</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class GeospatialCoverage extends AbstractBaseComponent {

	private GeographicIdentifier _geographicIdentifier = null;
	private BoundingBox _boundingBox = null;
	private BoundingGeometry _boundingGeometry = null;
	private PostalAddress _postalAddress = null;
	private VerticalExtent _verticalExtent = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String GEOSPATIAL_EXTENT_NAME = "GeospatialExtent";
	private static final String PRECEDENCE_NAME = "precedence";
	private static final String ORDER_NAME = "order";

	private static final List<String> VALID_PRECEDENCE_VALUES = new ArrayList<String>();
	static {
		VALID_PRECEDENCE_VALUES.add("Primary");
		VALID_PRECEDENCE_VALUES.add("Secondary");
	}

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public GeospatialCoverage(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("geographicIdentifier element", element);
			setXOMElement(element, false);
			Element extElement = getExtentElement();
			if (extElement != null) {
				DDMSVersion version = DDMSVersion.getVersionForNamespace(getNamespace());
				Element geographicIdentifierElement = extElement.getFirstChildElement(
					GeographicIdentifier.getName(version), getNamespace());
				if (geographicIdentifierElement != null)
					_geographicIdentifier = new GeographicIdentifier(geographicIdentifierElement);
				Element boundingBoxElement = extElement.getFirstChildElement(BoundingBox.getName(version),
					getNamespace());
				if (boundingBoxElement != null)
					_boundingBox = new BoundingBox(boundingBoxElement);
				Element boundingGeometryElement = extElement.getFirstChildElement(BoundingGeometry.getName(version),
					getNamespace());
				if (boundingGeometryElement != null)
					_boundingGeometry = new BoundingGeometry(boundingGeometryElement);
				Element postalAddressElement = extElement.getFirstChildElement(PostalAddress.getName(version),
					getNamespace());
				if (postalAddressElement != null)
					_postalAddress = new PostalAddress(postalAddressElement);
				Element verticalExtentElement = extElement.getFirstChildElement(VerticalExtent.getName(version),
					getNamespace());
				if (verticalExtentElement != null)
					_verticalExtent = new VerticalExtent(verticalExtentElement);
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
	 * @param geographicIdentifier an identifier
	 * @param boundingBox a bounding box
	 * @param boundingGeometry a set of bounding geometry
	 * @param postalAddress an address
	 * @param verticalExtent an extent
	 * @param precedence the precedence attribute
	 * @param order the order attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public GeospatialCoverage(GeographicIdentifier geographicIdentifier, BoundingBox boundingBox,
		BoundingGeometry boundingGeometry, PostalAddress postalAddress, VerticalExtent verticalExtent,
		String precedence, Integer order, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			Element coverageElement = Util.buildDDMSElement(
				GeospatialCoverage.getName(DDMSVersion.getCurrentVersion()), null);

			Element element = DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? coverageElement
				: Util.buildDDMSElement(GEOSPATIAL_EXTENT_NAME, null);
			if (geographicIdentifier != null)
				element.appendChild(geographicIdentifier.getXOMElementCopy());
			if (boundingBox != null)
				element.appendChild(boundingBox.getXOMElementCopy());
			if (boundingGeometry != null)
				element.appendChild(boundingGeometry.getXOMElementCopy());
			if (postalAddress != null)
				element.appendChild(postalAddress.getXOMElementCopy());
			if (verticalExtent != null)
				element.appendChild(verticalExtent.getXOMElementCopy());
			Util.addDDMSAttribute(coverageElement, PRECEDENCE_NAME, precedence);
			if (order != null)
				Util.addDDMSAttribute(coverageElement, ORDER_NAME, order.toString());

			if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				coverageElement.appendChild(element);

			_geographicIdentifier = geographicIdentifier;
			_boundingBox = boundingBox;
			_boundingGeometry = boundingGeometry;
			_postalAddress = postalAddress;
			_verticalExtent = verticalExtent;
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(coverageElement);
			setXOMElement(coverageElement, true);
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
		Util.requireDDMSQName(getXOMElement(), GeospatialCoverage.getName(getDDMSVersion()));
		Element extElement = getExtentElement();
		Util.requireDDMSValue("GeospatialExtent element", extElement);
		int validComponents = 0;
		if (getGeographicIdentifier() != null)
			validComponents++;
		if (getBoundingBox() != null)
			validComponents++;
		if (getBoundingGeometry() != null)
			validComponents++;
		if (getPostalAddress() != null)
			validComponents++;
		if (getVerticalExtent() != null)
			validComponents++;
		if (validComponents == 0) {
			throw new InvalidDDMSException("At least 1 of geographicIdentifier, boundingBox, boundingGeometry, "
				+ "postalAddress, or verticalExtent must be used.");
		}
		Util.requireBoundedChildCount(extElement, GeographicIdentifier.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(extElement, BoundingBox.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(extElement, BoundingGeometry.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(extElement, PostalAddress.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(extElement, VerticalExtent.getName(getDDMSVersion()), 0, 1);
		if (hasFacilityIdentifier() && validComponents > 1) {
			throw new InvalidDDMSException("A geographicIdentifier containing a facilityIdentifier must not be used in "
				+ "tandem with any other coverage elements.");
		}
		if (!getDDMSVersion().isAtLeast("4.0.1")) {
			if (getOrder() != null) {
				throw new InvalidDDMSException("The ddms:order attribute must not be used until DDMS 4.0.1 or later.");
			}
			if (!Util.isEmpty(getPrecedence())) {
				throw new InvalidDDMSException(
					"The ddms:precedence attribute must not be used until DDMS 4.0.1 or later.");
			}
		}
		if (!Util.isEmpty(getPrecedence())) {
			if (!VALID_PRECEDENCE_VALUES.contains(getPrecedence())) {
				throw new InvalidDDMSException("The ddms:precedence attribute must have a value from: "
					+ VALID_PRECEDENCE_VALUES);
			}
			if (getGeographicIdentifier() == null || getGeographicIdentifier().getCountryCode() == null) {
				throw new InvalidDDMSException("The ddms:precedence attribute must only be applied to a "
					+ "geospatialCoverage containing a country code.");
			}
		}
		if (!getDDMSVersion().isAtLeast("3.0") && !getSecurityAttributes().isEmpty()) {
			throw new InvalidDDMSException(
				"Security attributes must not be applied to this component until DDMS 3.0 or later.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getLocatorSuffix()
	 */
	protected String getLocatorSuffix() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "" : ValidationMessage.ELEMENT_PREFIX
			+ getXOMElement().getNamespacePrefix() + ":" + GEOSPATIAL_EXTENT_NAME);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		if (!getDDMSVersion().isAtLeast("4.0.1"))
			localPrefix += GEOSPATIAL_EXTENT_NAME + ".";
		StringBuffer text = new StringBuffer();
		if (getGeographicIdentifier() != null)
			text.append(getGeographicIdentifier().getOutput(isHTML, localPrefix, ""));
		if (getBoundingBox() != null)
			text.append(getBoundingBox().getOutput(isHTML, localPrefix, ""));
		if (getBoundingGeometry() != null)
			text.append(getBoundingGeometry().getOutput(isHTML, localPrefix, ""));
		if (getPostalAddress() != null)
			text.append(getPostalAddress().getOutput(isHTML, localPrefix, ""));
		if (getVerticalExtent() != null)
			text.append(getVerticalExtent().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix + PRECEDENCE_NAME, getPrecedence()));
		if (getOrder() != null)
			text.append(buildOutput(isHTML, localPrefix + ORDER_NAME, String.valueOf(getOrder())));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getBoundingBox());
		list.add(getBoundingGeometry());
		list.add(getGeographicIdentifier());
		list.add(getPostalAddress());
		list.add(getVerticalExtent());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof GeospatialCoverage))
			return (false);
		GeospatialCoverage test = (GeospatialCoverage) obj;
		return (getPrecedence().equals(test.getPrecedence())
			&& Util.nullEquals(getOrder(), test.getOrder()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getPrecedence().hashCode();
		if (getOrder() != null)
			result = 7 * result + getOrder().hashCode();
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
		return ("geospatialCoverage");
	}

	/**
	 * Accessor for the element which contains the child elementsnt. Before DDMS 4.0.1, this is a wrapper element called
	 * ddms:GeospatialExtent. Starting in DDMS 4.0.1, it is the ddms:geospatialCoverage element itself.
	 */
	private Element getExtentElement() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? getXOMElement() : getChild(GEOSPATIAL_EXTENT_NAME));
	}

	/**
	 * Accessor for the precedence attribute.
	 */
	public String getPrecedence() {
		return (getAttributeValue(PRECEDENCE_NAME));
	}

	/**
	 * Accessor for the order attribute.
	 */
	public Integer getOrder() {
		String order = getAttributeValue(ORDER_NAME);
		return (Util.isEmpty(order) ? null : Integer.valueOf(order));
	}

	/**
	 * Accessor for whether this geospatialCoverage is using a facility identifier.
	 */
	public boolean hasFacilityIdentifier() {
		return (getGeographicIdentifier() != null && getGeographicIdentifier().hasFacilityIdentifier());
	}

	/**
	 * Accessor for the geographicIdentifier. May return null if not used.
	 */
	public GeographicIdentifier getGeographicIdentifier() {
		return _geographicIdentifier;
	}

	/**
	 * Accessor for the boundingBox. May return null if not used.
	 */
	public BoundingBox getBoundingBox() {
		return _boundingBox;
	}

	/**
	 * Accessor for the boundingGeometry. May return null if not used.
	 */
	public BoundingGeometry getBoundingGeometry() {
		return _boundingGeometry;
	}

	/**
	 * Accessor for the postalAddress. May return null if not used.
	 */
	public PostalAddress getPostalAddress() {
		return (_postalAddress);
	}

	/**
	 * Accessor for the verticalExtent. May return null if not used.
	 */
	public VerticalExtent getVerticalExtent() {
		return (_verticalExtent);
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
		private static final long serialVersionUID = 2895705456552847432L;
		private BoundingBox.Builder _boundingBox;
		private BoundingGeometry.Builder _boundingGeometry;
		private GeographicIdentifier.Builder _geographicIdentifier;
		private PostalAddress.Builder _postalAddress;
		private VerticalExtent.Builder _verticalExtent;
		private String _precedence;
		private Integer _order;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(GeospatialCoverage coverage) {
			if (coverage.getBoundingBox() != null)
				setBoundingBox(new BoundingBox.Builder(coverage.getBoundingBox()));
			if (coverage.getBoundingGeometry() != null)
				setBoundingGeometry(new BoundingGeometry.Builder(coverage.getBoundingGeometry()));
			if (coverage.getGeographicIdentifier() != null)
				setGeographicIdentifier(new GeographicIdentifier.Builder(coverage.getGeographicIdentifier()));
			if (coverage.getPostalAddress() != null)
				setPostalAddress(new PostalAddress.Builder(coverage.getPostalAddress()));
			if (coverage.getVerticalExtent() != null)
				setVerticalExtent(new VerticalExtent.Builder(coverage.getVerticalExtent()));
			setPrecedence(coverage.getPrecedence());
			setOrder(coverage.getOrder());
			setSecurityAttributes(new SecurityAttributes.Builder(coverage.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public GeospatialCoverage commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new GeospatialCoverage(getGeographicIdentifier().commit(),
				getBoundingBox().commit(), getBoundingGeometry().commit(), getPostalAddress().commit(),
				getVerticalExtent().commit(), getPrecedence(), getOrder(), getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getGeographicIdentifier().isEmpty()
				&& getBoundingBox().isEmpty()
				&& getBoundingGeometry().isEmpty()
				&& getPostalAddress().isEmpty()
				&& getVerticalExtent().isEmpty()
				&& Util.isEmpty(getPrecedence())
				&& getOrder() == null
				&& getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the boundingBox
		 */
		public BoundingBox.Builder getBoundingBox() {
			if (_boundingBox == null)
				_boundingBox = new BoundingBox.Builder();
			return _boundingBox;
		}

		/**
		 * Builder accessor for the boundingBox
		 */
		public void setBoundingBox(BoundingBox.Builder boundingBox) {
			_boundingBox = boundingBox;
		}

		/**
		 * Builder accessor for the boundingGeometry
		 */
		public BoundingGeometry.Builder getBoundingGeometry() {
			if (_boundingGeometry == null)
				_boundingGeometry = new BoundingGeometry.Builder();
			return _boundingGeometry;
		}

		/**
		 * Builder accessor for the boundingGeometry
		 */
		public void setBoundingGeometry(BoundingGeometry.Builder boundingGeometry) {
			_boundingGeometry = boundingGeometry;
		}

		/**
		 * Builder accessor for the geographicIdentifier
		 */
		public GeographicIdentifier.Builder getGeographicIdentifier() {
			if (_geographicIdentifier == null)
				_geographicIdentifier = new GeographicIdentifier.Builder();
			return _geographicIdentifier;
		}

		/**
		 * Builder accessor for the geographicIdentifier
		 */
		public void setGeographicIdentifier(GeographicIdentifier.Builder geographicIdentifier) {
			_geographicIdentifier = geographicIdentifier;
		}

		/**
		 * Builder accessor for the postalAddress
		 */
		public PostalAddress.Builder getPostalAddress() {
			if (_postalAddress == null)
				_postalAddress = new PostalAddress.Builder();
			return _postalAddress;
		}

		/**
		 * Builder accessor for the postalAddress
		 */
		public void setPostalAddress(PostalAddress.Builder postalAddress) {
			_postalAddress = postalAddress;
		}

		/**
		 * Builder accessor for the verticalExtent
		 */
		public VerticalExtent.Builder getVerticalExtent() {
			if (_verticalExtent == null)
				_verticalExtent = new VerticalExtent.Builder();
			return _verticalExtent;
		}

		/**
		 * Builder accessor for the verticalExtent
		 */
		public void setVerticalExtent(VerticalExtent.Builder verticalExtent) {
			_verticalExtent = verticalExtent;
		}

		/**
		 * Builder accessor for the precedence
		 */
		public String getPrecedence() {
			return _precedence;
		}

		/**
		 * Builder accessor for the precedence
		 */
		public void setPrecedence(String precedence) {
			_precedence = precedence;
		}

		/**
		 * Builder accessor for the order
		 */
		public Integer getOrder() {
			return _order;
		}

		/**
		 * Builder accessor for the order
		 */
		public void setOrder(Integer order) {
			_order = order;
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