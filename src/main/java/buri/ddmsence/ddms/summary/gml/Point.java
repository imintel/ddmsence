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
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of gml:Point.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 * 		The GML profile was removed in favour of TSPI in DDMS 5.0.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info gml:pos|1|11110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info gml:id|1|11110}
 * 		{@child.info &lt;<i>srsAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule The srsName must exist.|Error|11111}
 * 		{@ddms.rule The gml:id must exist, and must be a valid NCName.|Error|11111}
 * 		{@ddms.rule If the gml:pos has an srsName, it must match the srsName of this Point.|Error|11111}
 * 		{@ddms.rule Warnings from any SRS attributes are claimed by this component.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Point extends AbstractBaseComponent {

	private Position _position = null;
	private SRSAttributes _srsAttributes = null;

	private static final String ID_NAME = "id";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Point(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element posElement = element.getFirstChildElement(Position.getName(getDDMSVersion()), getNamespace());
			if (posElement != null)
				_position = new Position(posElement);
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
	 * @param position the position of the Point
	 * @param srsAttributes the attribute group containing srsName, srsDimension, axisLabels, and uomLabels
	 * @param id the id value
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Point(Position position, SRSAttributes srsAttributes, String id) throws InvalidDDMSException {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Point.getName(version),
				version.getGmlNamespace(), null);
			if (position != null) {
				element.appendChild(position.getXOMElementCopy());
			}
			Util.addAttribute(element, PropertyReader.getPrefix("gml"), ID_NAME,
				DDMSVersion.getCurrentVersion().getGmlNamespace(), id);

			_position = position;
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
		Util.requireQName(getXOMElement(), getNamespace(), Point.getName(getDDMSVersion()));
		Util.requireDDMSValue("srsAttributes", getSRSAttributes());
		Util.requireDDMSValue("srsName", getSRSAttributes().getSrsName());
		Util.requireDDMSValue(ID_NAME, getId());
		Util.requireValidNCName(getId());
		Util.requireDDMSValue("position", getPosition());
		String srsName = getPosition().getSRSAttributes().getSrsName();
		if (!Util.isEmpty(srsName) && !srsName.equals(getSRSAttributes().getSrsName()))
			throw new InvalidDDMSException("The srsName of the position must match the srsName of the Point.");
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
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + ID_NAME, getId()));
		text.append(getSRSAttributes().getOutput(isHTML, localPrefix));
		text.append(getPosition().getOutput(isHTML, localPrefix, ""));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getPosition());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Point))
			return (false);
		Point test = (Point) obj;
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
		return ("Point");
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
	 * Accessor for the coordinates of the position. May return null, but cannot happen after instantiation.
	 */
	public Position getPosition() {
		return (_position);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 4003805386998809149L;
		private SRSAttributes.Builder _srsAttributes;
		private Position.Builder _position;
		private String _id;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Point point) {
			setSrsAttributes(new SRSAttributes.Builder(point.getSRSAttributes()));
			setPosition(new Position.Builder(point.getPosition()));
			setId(point.getId());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Point commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Point(getPosition().commit(), getSrsAttributes().commit(), getId()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getId()) && getPosition().isEmpty() && getSrsAttributes().isEmpty());
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
		 * Builder accessor for the position
		 */
		public Position.Builder getPosition() {
			if (_position == null)
				_position = new Position.Builder();
			return _position;
		}

		/**
		 * Builder accessor for the position
		 */
		public void setPosition(Position.Builder position) {
			_position = position;
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