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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:boundingBox.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	<p>The names of the nested elements were made lowercase in DDMS 4.0.1.</p>
 * 		<p>This component is functionally replaced by a TSPI Envelope in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:westBL|1|11110}
 * 		{@child.info ddms:eastBL|1|11110}
 * 		{@child.info ddms:southBL|1|11110}
 * 		{@child.info ddms:northBL|1|11110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:westBL must exist, and must be a valid longitude.|Error|11110}
 * 		{@ddms.rule ddms:eastBL must exist, and must be a valid longitude.|Error|11110}
 * 		{@ddms.rule ddms:southBL must exist, and must be a valid latitude.|Error|11110}
 * 		{@ddms.rule ddms:northBL must exist, and must be a valid latitude.|Error|11110}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class BoundingBox extends AbstractBaseComponent {

	private Double _westBL = null;
	private Double _eastBL = null;
	private Double _southBL = null;
	private Double _northBL = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public BoundingBox(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("boundingBox element", element);
			setXOMElement(element, false);
			_westBL = getChildTextAsDouble(element, getWestBLName());
			_eastBL = getChildTextAsDouble(element, getEastBLName());
			_southBL = getChildTextAsDouble(element, getSouthBLName());
			_northBL = getChildTextAsDouble(element, getNorthBLName());
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
	 * @param westBL the westbound longitude
	 * @param eastBL the eastbound longitude
	 * @param southBL the southbound latitude
	 * @param northBL the northbound latitude
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public BoundingBox(double westBL, double eastBL, double southBL, double northBL) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(BoundingBox.getName(DDMSVersion.getCurrentVersion()), null);
			setXOMElement(element, false);
			element.appendChild(Util.buildDDMSElement(getWestBLName(), String.valueOf(westBL)));
			element.appendChild(Util.buildDDMSElement(getEastBLName(), String.valueOf(eastBL)));
			element.appendChild(Util.buildDDMSElement(getSouthBLName(), String.valueOf(southBL)));
			element.appendChild(Util.buildDDMSElement(getNorthBLName(), String.valueOf(northBL)));
			_westBL = Double.valueOf(westBL);
			_eastBL = Double.valueOf(eastBL);
			_southBL = Double.valueOf(southBL);
			_northBL = Double.valueOf(northBL);
			validate();
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
		requireAtMostVersion("4.1");
		Util.requireDDMSQName(getXOMElement(), BoundingBox.getName(getDDMSVersion()));
		Util.requireDDMSValue("westbound longitude", getWestBL());
		Util.requireDDMSValue("eastbound longitude", getEastBL());
		Util.requireDDMSValue("southbound latitude", getSouthBL());
		Util.requireDDMSValue("northbound latitude", getNorthBL());
		Util.requireValidLongitude(getWestBL());
		Util.requireValidLongitude(getEastBL());
		Util.requireValidLatitude(getSouthBL());
		Util.requireValidLatitude(getNorthBL());	
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + getWestBLName(), String.valueOf(getWestBL())));
		text.append(buildOutput(isHTML, localPrefix + getEastBLName(), String.valueOf(getEastBL())));
		text.append(buildOutput(isHTML, localPrefix + getSouthBLName(), String.valueOf(getSouthBL())));
		text.append(buildOutput(isHTML, localPrefix + getNorthBLName(), String.valueOf(getNorthBL())));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof BoundingBox))
			return (false);
		BoundingBox test = (BoundingBox) obj;
		return (getWestBL().equals(test.getWestBL()) 
			&& getEastBL().equals(test.getEastBL())
			&& getSouthBL().equals(test.getSouthBL()) 
			&& getNorthBL().equals(test.getNorthBL()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getWestBL().hashCode();
		result = 7 * result + getEastBL().hashCode();
		result = 7 * result + getSouthBL().hashCode();
		result = 7 * result + getNorthBL().hashCode();
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
		return ("boundingBox");
	}

	/**
	 * Accessor for the name of the westbound longitude element, which changed in DDMS 4.0.1.
	 */
	private String getWestBLName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "westBL" : "WestBL");
	}

	/**
	 * Accessor for the name of the eastbound longitude element, which changed in DDMS 4.0.1.
	 */
	private String getEastBLName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "eastBL" : "EastBL");
	}

	/**
	 * Accessor for the name of the southbound latitude element, which changed in DDMS 4.0.1.
	 */
	private String getSouthBLName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "southBL" : "SouthBL");
	}

	/**
	 * Accessor for the name of the northbound latitude element, which changed in DDMS 4.0.1.
	 */
	private String getNorthBLName() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "northBL" : "NorthBL");
	}

	/**
	 * Accessor for the westbound longitude.
	 */
	public Double getWestBL() {
		return (_westBL);
	}

	/**
	 * Accessor for the eastbound longitude.
	 */
	public Double getEastBL() {
		return (_eastBL);
	}

	/**
	 * Accessor for the southbound latitude.
	 */
	public Double getSouthBL() {
		return (_southBL);
	}

	/**
	 * Accessor for the northbound latitude.
	 */
	public Double getNorthBL() {
		return (_northBL);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -2364407215439097065L;
		private Double _westBL;
		private Double _eastBL;
		private Double _southBL;
		private Double _northBL;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(BoundingBox box) {
			setWestBL(box.getWestBL());
			setEastBL(box.getEastBL());
			setSouthBL(box.getSouthBL());
			setNorthBL(box.getNorthBL());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public BoundingBox commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			// Check for existence of values before casting to primitives.
			if (getWestBL() == null || getEastBL() == null || getSouthBL() == null || getNorthBL() == null)
				throw new InvalidDDMSException("A ddms:boundingBox must have two latitude and two longitude values.");
			return (new BoundingBox(getWestBL().doubleValue(), getEastBL().doubleValue(), getSouthBL().doubleValue(),
				getNorthBL().doubleValue()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getWestBL() == null && getEastBL() == null && getSouthBL() == null && getNorthBL() == null);
		}

		/**
		 * Builder accessor for the westbound longitude
		 */
		public Double getWestBL() {
			return _westBL;
		}

		/**
		 * Builder accessor for the westbound longitude
		 */
		public void setWestBL(Double westBL) {
			_westBL = westBL;
		}

		/**
		 * Builder accessor for the eastbound longitude
		 */
		public Double getEastBL() {
			return _eastBL;
		}

		/**
		 * Builder accessor for the eastbound longitude
		 */
		public void setEastBL(Double eastBL) {
			_eastBL = eastBL;
		}

		/**
		 * Builder accessor for the southbound latitude
		 */
		public Double getSouthBL() {
			return _southBL;
		}

		/**
		 * Builder accessor for the southbound latitude
		 */
		public void setSouthBL(Double southBL) {
			_southBL = southBL;
		}

		/**
		 * Builder accessor for the northbound latitude
		 */
		public Double getNorthBL() {
			return _northBL;
		}

		/**
		 * Builder accessor for the northbound latitude
		 */
		public void setNorthBL(Double northBL) {
			_northBL = northBL;
		}
	}
}