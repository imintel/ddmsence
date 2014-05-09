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
import java.util.HashSet;
import java.util.Set;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:verticalExtent.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p>The optional unitOfMeasure and datum on the minVerticalExtent/maxVerticalExtent child elements MUST match the
 * values on the required attributes of the same name on this element. It does not seem logical to specify these
 * attributes on the parent element and then express the actual values with a different measure. Note that because
 * DDMSence is giving precedence to the top-level unitOfMeasure and datum attributes, those attributes on the
 * children are not displayed in HTML/Text. However, they are still rendered in XML, if present in an existing
 * document.</p>
 * 
 * <p>The above design decision dictates that VerticalDistance (the type behind minVerticalExtent and maxVerticalExtent)
 * does not need to be implemented as a Java class.</p>
 * 
 * {@table.header History}
 * 		<p>The names of the nested elements were made lowercase in DDMS 4.0.1.</p>
 * 		<p>This component is functionally replaced by a TSPI Point in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:minVerticalExtent|1|11110}
 * 		{@child.info ddms:maxVerticalExtent|1|11110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:unitOfMeasure|1|11110}
 * 		{@child.info ddms:datum|1|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:minVerticalExtent must exist.|Error|11111}
 * 		{@ddms.rule ddms:maxVerticalExtent must exist.|Error|11111}
 * 		{@ddms.rule ddms:unitOfMeasure must exist, and must be a valid token.|Error|11111}
 * 		{@ddms.rule ddms:datum must exist, and must be a valid token.|Error|11111}
 * 		{@ddms.rule If a minVerticalExtent has unitOfMeasure or datum set, its values must match the parent attribute values.|Error|11111}
 * 		{@ddms.rule If a maxVerticalExtent has unitOfMeasure or datum set, its values must match the parent attribute values.|Error|11111}
 * 		{@ddms.rule ddms:minVerticalExtent must be less than ddms:maxVerticalExtent.|Error|11111}
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class VerticalExtent extends AbstractBaseComponent {

	private Double _min = null;
	private Double _max = null;

	private static Set<String> VERTICAL_DATUM_TYPES = new HashSet<String>();
	static {
		VERTICAL_DATUM_TYPES.add("MSL");
		VERTICAL_DATUM_TYPES.add("AGL");
		VERTICAL_DATUM_TYPES.add("HAE");
	}

	private static Set<String> LENGTH_MEASURE_TYPES = new HashSet<String>();
	static {
		LENGTH_MEASURE_TYPES.add("Meter");
		LENGTH_MEASURE_TYPES.add("Kilometer");
		LENGTH_MEASURE_TYPES.add("Foot");
		LENGTH_MEASURE_TYPES.add("StatuteMile");
		LENGTH_MEASURE_TYPES.add("NauticalMile");
		LENGTH_MEASURE_TYPES.add("Fathom");
		LENGTH_MEASURE_TYPES.add("Inch");
	}

	private static final String DATUM_NAME = "datum";
	private static final String UOM_NAME = "unitOfMeasure";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public VerticalExtent(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("verticalExtent element", element);
			setXOMElement(element, false);
			_min = getChildTextAsDouble(element, getMinVerticalExtentName());
			_max = getChildTextAsDouble(element, getMaxVerticalExtentName());
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
	 * @param minVerticalExtent the minimum
	 * @param maxVerticalExtent the maximum
	 * @param unitOfMeasure the unit of measure
	 * @param datum the datum
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public VerticalExtent(double minVerticalExtent, double maxVerticalExtent, String unitOfMeasure, String datum)
		throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(VerticalExtent.getName(DDMSVersion.getCurrentVersion()), null);
			setXOMElement(element, false);
			Util.addDDMSAttribute(element, UOM_NAME, unitOfMeasure);
			Util.addDDMSAttribute(element, DATUM_NAME, datum);
			element.appendChild(Util.buildDDMSElement(getMinVerticalExtentName(), String.valueOf(minVerticalExtent)));
			element.appendChild(Util.buildDDMSElement(getMaxVerticalExtentName(), String.valueOf(maxVerticalExtent)));
			_min = Double.valueOf(minVerticalExtent);
			_max = Double.valueOf(maxVerticalExtent);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Validates a vertical datum type against the allowed types.
	 * 
	 * @param datumType the type to test
	 * @throws InvalidDDMSException if the value is null, empty or invalid.
	 */
	public static void validateVerticalDatumType(String datumType) throws InvalidDDMSException {
		Util.requireDDMSValue("vertical datum type", datumType);
		if (!VERTICAL_DATUM_TYPES.contains(datumType))
			throw new InvalidDDMSException("The vertical datum type must be one of " + VERTICAL_DATUM_TYPES);
	}

	/**
	 * Validates a length measure type against the allowed types.
	 * 
	 * @param lengthType the type to test
	 * @throws InvalidDDMSException if the value is null, empty or invalid.
	 */
	public static void validateLengthMeasureType(String lengthType) throws InvalidDDMSException {
		Util.requireDDMSValue("length measure type", lengthType);
		if (!LENGTH_MEASURE_TYPES.contains(lengthType))
			throw new InvalidDDMSException("The length measure type must be one of " + LENGTH_MEASURE_TYPES);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtMostVersion("4.1");
		Util.requireDDMSQName(getXOMElement(), VerticalExtent.getName(getDDMSVersion()));
		Util.requireDDMSValue(getMinVerticalExtentName(), getMinVerticalExtent());
		Util.requireDDMSValue(getMaxVerticalExtentName(), getMaxVerticalExtent());
		Util.requireDDMSValue(UOM_NAME, getUnitOfMeasure());
		Util.requireDDMSValue(DATUM_NAME, getDatum());
		validateLengthMeasureType(getUnitOfMeasure());
		validateVerticalDatumType(getDatum());
		validateInheritedAttributes(getChild(getMinVerticalExtentName()));
		validateInheritedAttributes(getChild(getMaxVerticalExtentName()));
		if (getMaxVerticalExtent().compareTo(getMinVerticalExtent()) < 0)
			throw new InvalidDDMSException("Minimum vertical extent must be less than maximum vertical extent.");
		super.validate();
	}

	/**
	 * Confirms that the unitOfMeasure and datum on minimum and maximum extent elements matches the parent attribute
	 * values. This is an additional level of logic added by DDMSence.
	 * 
	 * @param extentElement
	 * @throws InvalidDDMSException
	 */
	private void validateInheritedAttributes(Element extentElement) throws InvalidDDMSException {
		String unitOfMeasure = extentElement.getAttributeValue(UOM_NAME, extentElement.getNamespaceURI());
		String datum = extentElement.getAttributeValue(DATUM_NAME, extentElement.getNamespaceURI());
		if (!Util.isEmpty(unitOfMeasure) && !unitOfMeasure.equals(getUnitOfMeasure()))
			throw new InvalidDDMSException("The unitOfMeasure on the " + extentElement.getLocalName()
				+ " element must match the unitOfMeasure on the enclosing verticalExtent element.");
		if (!Util.isEmpty(datum) && !datum.equals(getDatum()))
			throw new InvalidDDMSException("The datum on the " + extentElement.getLocalName()
				+ " element must match the datum on the enclosing verticalExtent element.");
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + UOM_NAME, getUnitOfMeasure()));
		text.append(buildOutput(isHTML, localPrefix + DATUM_NAME, getDatum()));
		text.append(buildOutput(isHTML, localPrefix + "minimum", String.valueOf(getMinVerticalExtent())));
		text.append(buildOutput(isHTML, localPrefix + "maximum", String.valueOf(getMaxVerticalExtent())));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof VerticalExtent))
			return (false);
		VerticalExtent test = (VerticalExtent) obj;
		return (getUnitOfMeasure().equals(test.getUnitOfMeasure()) 
			&& getDatum().equals(test.getDatum())
			&& getMinVerticalExtent().equals(test.getMinVerticalExtent()) 
			&& getMaxVerticalExtent().equals(test.getMaxVerticalExtent()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getUnitOfMeasure().hashCode();
		result = 7 * result + getDatum().hashCode();
		result = 7 * result + getMinVerticalExtent().hashCode();
		result = 7 * result + getMaxVerticalExtent().hashCode();
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
		return ("verticalExtent");
	}

	/**
	 * Accessor for the name of the minimum vertical extent element, which changed in DDMS 4.0.1.
	 */
	private String getMinVerticalExtentName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "minVerticalExtent" : "MinVerticalExtent");
	}

	/**
	 * Accessor for the name of the maximum vertical extent element, which changed in DDMS 4.0.1.
	 */
	private String getMaxVerticalExtentName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "maxVerticalExtent" : "MaxVerticalExtent");
	}

	/**
	 * Accessor for the unitOfMeasure attribute
	 */
	public String getUnitOfMeasure() {
		return (getAttributeValue(UOM_NAME));
	}

	/**
	 * Accessor for the vertical datum attribute
	 */
	public String getDatum() {
		return (getAttributeValue(DATUM_NAME));
	}

	/**
	 * Accessor for the minimum extent
	 */
	public Double getMinVerticalExtent() {
		return (_min);
	}

	/**
	 * Accessor for the maximum extent
	 */
	public Double getMaxVerticalExtent() {
		return (_max);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 5188383406608210723L;
		private Double _minVerticalExtent;
		private Double _maxVerticalExtent;
		private String _unitOfMeasure;
		private String _datum;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(VerticalExtent extent) {
			setMinVerticalExtent(extent.getMinVerticalExtent());
			setMaxVerticalExtent(extent.getMaxVerticalExtent());
			setUnitOfMeasure(extent.getUnitOfMeasure());
			setDatum(extent.getDatum());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public VerticalExtent commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			// Check for existence of values before casting to primitives.
			if (getMinVerticalExtent() == null || getMaxVerticalExtent() == null)
				throw new InvalidDDMSException("A ddms:verticalExtent must have a minimum and maximum extent value.");
			return (new VerticalExtent(getMinVerticalExtent().doubleValue(), getMaxVerticalExtent().doubleValue(),
				getUnitOfMeasure(), getDatum()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getMinVerticalExtent() == null && getMaxVerticalExtent() == null
				&& Util.isEmpty(getUnitOfMeasure())
				&& Util.isEmpty(getDatum()));
		}

		/**
		 * Builder accessor for the minimum extent
		 */
		public Double getMinVerticalExtent() {
			return _minVerticalExtent;
		}

		/**
		 * Builder accessor for the minimum extent
		 */
		public void setMinVerticalExtent(Double minVerticalExtent) {
			_minVerticalExtent = minVerticalExtent;
		}

		/**
		 * Builder accessor for the maximum extent
		 */
		public Double getMaxVerticalExtent() {
			return _maxVerticalExtent;
		}

		/**
		 * Builder accessor for the maximum extent
		 */
		public void setMaxVerticalExtent(Double maxVerticalExtent) {
			_maxVerticalExtent = maxVerticalExtent;
		}

		/**
		 * Builder accessor for the unitOfMeasure attribute
		 */
		public String getUnitOfMeasure() {
			return _unitOfMeasure;
		}

		/**
		 * Builder accessor for the unitOfMeasure attribute
		 */
		public void setUnitOfMeasure(String unitOfMeasure) {
			_unitOfMeasure = unitOfMeasure;
		}

		/**
		 * Builder accessor for the vertical datum attribute
		 */
		public String getDatum() {
			return _datum;
		}

		/**
		 * Builder accessor for the vertical datum attribute
		 */
		public void setDatum(String datum) {
			_datum = datum;
		}
	}
}