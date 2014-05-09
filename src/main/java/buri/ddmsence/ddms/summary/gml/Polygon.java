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
package buri.ddmsence.ddms.summary.gml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of gml:Polygon.
 * 
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p>
 * A Polygon element contains a nested gml:exterior element, which itself contains a nested gml:LinearRing element.
 * The points which mark the boundaries of the polygon should be provided in counter-clockwise order.
 * Because DDMS does not decorate these elements with any special attributes, they are not implemented as Java objects.
 * </p>
 * 
 *  {@table.header History}
 * 		The GML profile was removed in favour of TSPI in DDMS 5.0.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info gml:pos|4..*|11110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info gml:id|1|11110}
 * 		{@child.info &lt;<i>srsAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule The srsName must exist.|Error|11111}
 * 		{@ddms.rule The gml:id must exist, and must be a valid NCName.|Error|11111}
 * 		{@ddms.rule If a gml:pos has an srsName, it must match the srsName of this Polygon.|Error|11111}
 * 		{@ddms.rule The first and last position coordinates must be identical (a closed polygon).|Error|11111}
 * 		{@ddms.rule Warnings from any SRS attributes are claimed by this component.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Polygon extends AbstractBaseComponent {

	private List<Position> _positions;
	private SRSAttributes _srsAttributes;

	private static final String EXTERIOR_NAME = "exterior";
	private static final String LINEAR_RING_NAME = "LinearRing";
	private static final String ID_NAME = "id";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Polygon(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			_positions = new ArrayList<Position>();
			Element extElement = element.getFirstChildElement(EXTERIOR_NAME, getNamespace());
			if (extElement != null) {
				Element ringElement = extElement.getFirstChildElement(LINEAR_RING_NAME, getNamespace());
				if (ringElement != null) {
					Elements positions = ringElement.getChildElements(Position.getName(getDDMSVersion()),
						getNamespace());
					for (int i = 0; i < positions.size(); i++) {
						_positions.add(new Position(positions.get(i)));
					}
				}
			}
			_srsAttributes = new SRSAttributes(element);
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
	 * @param positions the positions of the Polygon
	 * @param srsAttributes the attribute group containing srsName, srsDimension, axisLabels, and uomLabels
	 * @param id the id value
	 * 
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Polygon(List<Position> positions, SRSAttributes srsAttributes, String id) throws InvalidDDMSException {
		try {
			if (positions == null)
				positions = Collections.emptyList();
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String gmlPrefix = PropertyReader.getPrefix("gml");
			String gmlNamespace = version.getGmlNamespace();
			Element ringElement = Util.buildElement(gmlPrefix, LINEAR_RING_NAME, gmlNamespace, null);
			for (Position pos : positions) {
				ringElement.appendChild(pos.getXOMElementCopy());
			}
			Element extElement = Util.buildElement(gmlPrefix, EXTERIOR_NAME, gmlNamespace, null);
			extElement.appendChild(ringElement);
			Element element = Util.buildElement(gmlPrefix, Polygon.getName(version), gmlNamespace, null);
			element.appendChild(extElement);
			Util.addAttribute(element, gmlPrefix, ID_NAME, gmlNamespace, id);

			_positions = positions;
			_srsAttributes = SRSAttributes.getNonNullInstance(srsAttributes);
			_srsAttributes.addTo(element);
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
		Util.requireQName(getXOMElement(), getNamespace(), Polygon.getName(getDDMSVersion()));
		Util.requireDDMSValue("srsName", getSRSAttributes().getSrsName());
		Util.requireDDMSValue(ID_NAME, getId());
		Util.requireValidNCName(getId());

		Element extElement = getXOMElement().getFirstChildElement(EXTERIOR_NAME, getNamespace());
		Util.requireDDMSValue("exterior element", extElement);
		if (extElement != null) {
			Util.requireDDMSValue("LinearRing element", extElement.getFirstChildElement(LINEAR_RING_NAME,
				getNamespace()));
		}
		List<Position> positions = getPositions();
		for (Position pos : positions) {
			if (pos.getSRSAttributes() != null) {
				String srsName = pos.getSRSAttributes().getSrsName();
				if (!Util.isEmpty(srsName) && !srsName.equals(getSRSAttributes().getSrsName()))
					throw new InvalidDDMSException(
						"The srsName of each position must match the srsName of the Polygon.");
			}
		}
		if (positions.size() < 4)
			throw new InvalidDDMSException("At least 4 positions must exist for a valid Polygon.");
		if (!positions.isEmpty() && !positions.get(0).equals(positions.get(positions.size() - 1))) {
			throw new InvalidDDMSException("The first and last position in the Polygon must be the same.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		addWarnings(getSRSAttributes().getValidationWarnings(), true);
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getLocatorSuffix()
	 */
	protected String getLocatorSuffix() {
		String gmlPrefix = PropertyReader.getPrefix("gml");
		return (ValidationMessage.ELEMENT_PREFIX + gmlPrefix + ":" + EXTERIOR_NAME + ValidationMessage.ELEMENT_PREFIX
			+ gmlPrefix + ":" + LINEAR_RING_NAME);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + ID_NAME, getId()));
		text.append(getSRSAttributes().getOutput(isHTML, localPrefix));
		text.append(buildOutput(isHTML, localPrefix, getPositions()));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getPositions());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Polygon))
			return (false);
		Polygon test = (Polygon) obj;
		return (getSRSAttributes().equals(test.getSRSAttributes())
			&& getId().equals(test.getId()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getSRSAttributes().hashCode();
		result = 7 * result + getId().hashCode();
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
		return ("Polygon");
	}

	/**
	 * Accessor for the SRS Attributes. Will always be non-null.
	 */
	public SRSAttributes getSRSAttributes() {
		return (_srsAttributes);
	}

	/**
	 * Accessor for the ID
	 */
	public String getId() {
		return (getAttributeValue(ID_NAME, getNamespace()));
	}

	/**
	 * Accessor for the coordinates. May return null, but cannot happen after instantiation.
	 */
	public List<Position> getPositions() {
		return (Collections.unmodifiableList(_positions));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -4324741146353401634L;
		private SRSAttributes.Builder _srsAttributes;
		private List<Position.Builder> _positions;
		private String _id;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Polygon polygon) {
			setSrsAttributes(new SRSAttributes.Builder(polygon.getSRSAttributes()));
			for (Position position : polygon.getPositions()) {
				getPositions().add(new Position.Builder(position));
			}
			setId(polygon.getId());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Polygon commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Position> positions = new ArrayList<Position>();
			for (Position.Builder builder : getPositions()) {
				Position position = builder.commit();
				if (position != null)
					positions.add(position);
			}
			return (new Polygon(positions, getSrsAttributes().commit(), getId()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getPositions()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (Util.isEmpty(getId()) && !hasValueInList && getSrsAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the SRS Attributes
		 */
		public SRSAttributes.Builder getSrsAttributes() {
			if (_srsAttributes == null)
				_srsAttributes = new SRSAttributes.Builder();
			return _srsAttributes;
		}

		/**
		 * Builder accessor for the SRS Attributes
		 */
		public void setSrsAttributes(SRSAttributes.Builder srsAttributes) {
			_srsAttributes = srsAttributes;
		}

		/**
		 * Builder accessor for the coordinates
		 */
		public List<Position.Builder> getPositions() {
			if (_positions == null)
				_positions = new LazyList(Position.Builder.class);
			return _positions;
		}

		/**
		 * Accessor for the ID
		 */
		public String getId() {
			return _id;
		}

		/**
		 * Accessor for the ID
		 */
		public void setId(String id) {
			_id = id;
		}
	}
}