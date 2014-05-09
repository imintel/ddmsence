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
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of gml:pos.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 * 		The GML profile was removed in favour of TSPI in DDMS 5.0.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info &lt;<i>srsAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule Each coordinate must be a valid Double.|Error|11111}
 * 		{@ddms.rule The position must be represented by 2 or 3 coordinates (to comply with WGS84E_2D or WGS84E_3D).|Error|11111}
 * 		{@ddms.rule The first coordinate must be a valid latitude.|Error|11111}
 * 		{@ddms.rule The second coordinate must be a valid longitude.|Error|11111}
 * 		{@ddms.rule Warnings from any SRS attributes are claimed by this component.|Warning|11111}
 * 		<p>Does not perform any special validation on the third coordinate (height above ellipsoid).</p>
 * {@table.footer}
 * 
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Position extends AbstractBaseComponent {

	private SRSAttributes _srsAttributes = null;
	private List<Double> _coordinates = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Position(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			List<String> tuple = Util.getXsListAsList(getCoordinatesAsXsList());
			_coordinates = new ArrayList<Double>();
			for (String coordinate : tuple) {
				_coordinates.add(Double.valueOf(coordinate));
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
	 * @param coordinates a list of either 2 or 3 coordinate Double values
	 * @param srsAttributes the attribute group containing srsName, srsDimension, axisLabels, and uomLabels
	 * 
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Position(List<Double> coordinates, SRSAttributes srsAttributes) throws InvalidDDMSException {
		try {
			if (coordinates == null)
				coordinates = Collections.emptyList();
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildElement(PropertyReader.getPrefix("gml"), Position.getName(version),
				version.getGmlNamespace(), Util.getXsList(coordinates));

			_coordinates = coordinates;
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
		Util.requireQName(getXOMElement(), getNamespace(), Position.getName(getDDMSVersion()));
		for (Double coordinate : getCoordinates())
			Util.requireDDMSValue("coordinate", coordinate);
		if (!Util.isBounded(getCoordinates().size(), 2, 3))
			throw new InvalidDDMSException("A position must be represented by either 2 or 3 coordinates.");
		Util.requireValidLatitude(getCoordinates().get(0));
		Util.requireValidLongitude(getCoordinates().get(1));
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
		String localPrefix = buildPrefix(prefix, getName(), suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getCoordinatesAsXsList()));
		text.append(getSRSAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Position))
			return (false);
		Position test = (Position) obj;
		return (getSRSAttributes().equals(test.getSRSAttributes())
			&& getCoordinates().size() == test.getCoordinates().size()
			&& Util.listEquals(getCoordinates(), test.getCoordinates()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getSRSAttributes().hashCode();
		result = 7 * result + getCoordinatesAsXsList().hashCode();
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
		return ("pos");
	}

	/**
	 * Accessor for the SRS Attributes. Will always be non-null, even if the attributes inside are not set.
	 */
	public SRSAttributes getSRSAttributes() {
		return (_srsAttributes);
	}

	/**
	 * Accessor for the coordinates of the position. May return null, but cannot happen after instantiation.
	 */
	public List<Double> getCoordinates() {
		return (Collections.unmodifiableList(_coordinates));
	}

	/**
	 * Accessor for the String representation of the coordinates
	 */
	public String getCoordinatesAsXsList() {
		return (getXOMElement().getValue());
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 33638279863455987L;
		private SRSAttributes.Builder _srsAttributes;
		private List<Position.DoubleBuilder> _coordinates;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Position position) {
			setSrsAttributes(new SRSAttributes.Builder(position.getSRSAttributes()));
			for (Double coord : position.getCoordinates()) {
				getCoordinates().add(new DoubleBuilder(coord));
			}
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Position commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Double> coordinates = new ArrayList<Double>();
			for (Position.DoubleBuilder builder : getCoordinates()) {
				Double coord = builder.commit();
				if (coord != null)
					coordinates.add(coord);
			}
			return (new Position(coordinates, getSrsAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (Position.DoubleBuilder builder : getCoordinates()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (!hasValueInList && getSrsAttributes().isEmpty());
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
		 * Builder accessor for the coordinates of the position
		 */
		public List<Position.DoubleBuilder> getCoordinates() {
			if (_coordinates == null)
				_coordinates = new LazyList(Position.DoubleBuilder.class);
			return _coordinates;
		}
	}

	/**
	 * Builder for a Double
	 * 
	 * <p>This builder is implemented because the Java Double class does not have a no-arg constructor which can be
	 * hooked into a LazyList. Because the Builder returns a Double instead of an IDDMSComponent, it does not officially
	 * implement the IBuilder interface.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.9.0
	 */
	public static class DoubleBuilder implements Serializable {
		private static final long serialVersionUID = -5102193614065692204L;
		private Double _value;

		/**
		 * Empty constructor
		 */
		public DoubleBuilder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public DoubleBuilder(Double value) {
			setValue(value);
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Double commit() throws InvalidDDMSException {
			return (isEmpty() ? null : getValue());
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getValue() == null);
		}

		/**
		 * Builder accessor for the value
		 */
		public Double getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the value
		 */
		public void setValue(Double value) {
			_value = value;
		}
	}
}