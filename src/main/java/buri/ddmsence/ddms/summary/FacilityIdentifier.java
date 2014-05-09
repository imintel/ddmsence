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
 * An immutable implementation of ddms:facilityIdentifier.
 *  <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:beNumber|1|11111}
 * 		{@child.info ddms:osuffix|1|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:beNumber must exist.|Error|11111}
 * 		{@ddms.rule ddms:osuffix must exist.|Error|11111}
 * 		<p>Does not validate whether the attributes have logical values.</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class FacilityIdentifier extends AbstractBaseComponent {

	private static final String BE_NUMBER_NAME = "beNumber";
	private static final String OSUFFIX_NAME = "osuffix";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public FacilityIdentifier(Element element) throws InvalidDDMSException {
		super(element);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param beNumber the beNumber
	 * @param osuffix the Osuffix
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public FacilityIdentifier(String beNumber, String osuffix) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(FacilityIdentifier.getName(DDMSVersion.getCurrentVersion()), null);
			Util.addDDMSAttribute(element, BE_NUMBER_NAME, beNumber);
			Util.addDDMSAttribute(element, OSUFFIX_NAME, osuffix);
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
		Util.requireDDMSQName(getXOMElement(), FacilityIdentifier.getName(getDDMSVersion()));
		Util.requireDDMSValue(BE_NUMBER_NAME, getBeNumber());
		Util.requireDDMSValue(OSUFFIX_NAME, getOsuffix());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + BE_NUMBER_NAME, getBeNumber()));
		text.append(buildOutput(isHTML, localPrefix + OSUFFIX_NAME, getOsuffix()));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof FacilityIdentifier))
			return (false);
		FacilityIdentifier test = (FacilityIdentifier) obj;
		return (getBeNumber().equals(test.getBeNumber()) 
			&& getOsuffix().equals(test.getOsuffix()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getBeNumber().hashCode();
		result = 7 * result + getOsuffix().hashCode();
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
		return ("facilityIdentifier");
	}

	/**
	 * Accessor for the beNumber attribute.
	 */
	public String getBeNumber() {
		return (getAttributeValue(BE_NUMBER_NAME));
	}

	/**
	 * Accessor for the osuffix attribute.
	 */
	public String getOsuffix() {
		return (getAttributeValue(OSUFFIX_NAME));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 4781523669271343048L;
		private String _beNumber;
		private String _osuffix;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(FacilityIdentifier facilityIdentifier) {
			setBeNumber(facilityIdentifier.getBeNumber());
			setOsuffix(facilityIdentifier.getOsuffix());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public FacilityIdentifier commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new FacilityIdentifier(getBeNumber(), getOsuffix()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getBeNumber()) && Util.isEmpty(getOsuffix()));
		}

		/**
		 * Builder accessor for the beNumber attribute.
		 */
		public String getBeNumber() {
			return _beNumber;
		}

		/**
		 * Builder accessor for the beNumber attribute.
		 */
		public void setBeNumber(String beNumber) {
			_beNumber = beNumber;
		}

		/**
		 * Builder accessor for the osuffix attribute.
		 */
		public String getOsuffix() {
			return _osuffix;
		}

		/**
		 * Builder accessor for the osuffix attribute.
		 */
		public void setOsuffix(String osuffix) {
			_osuffix = osuffix;
		}
	}
}