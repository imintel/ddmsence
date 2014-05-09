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

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractSimpleString;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:processingInfo.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:dateProcessed|1|00011}
 * 		{@child.info ism:classification|1|00011}
 * 		{@child.info ism:ownerProducer|1..*|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:dateProcessed must exist, and adheres to a valid date format.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class ProcessingInfo extends AbstractSimpleString {

	private static final String DATE_PROCESSED_NAME = "dateProcessed";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProcessingInfo(Element element) throws InvalidDDMSException {
		super(element, true);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param value the value of the child text
	 * @param dateProcessed the processing date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProcessingInfo(String value, String dateProcessed, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(ProcessingInfo.getName(DDMSVersion.getCurrentVersion()), value, securityAttributes, false);
		try {
			Util.addDDMSAttribute(getXOMElement(), DATE_PROCESSED_NAME, dateProcessed);
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
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSQName(getXOMElement(), ProcessingInfo.getName(getDDMSVersion()));
		Util.requireDDMSValue(DATE_PROCESSED_NAME, getDateProcessedString());
		Util.requireDDMSDateFormat(getDateProcessedString(), getNamespace());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getValue()));
		text.append(buildOutput(isHTML, localPrefix + "." + DATE_PROCESSED_NAME, getDateProcessedString()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ProcessingInfo))
			return (false);
		ProcessingInfo test = (ProcessingInfo) obj;
		return (Util.nullEquals(getDateProcessedString(), test.getDateProcessedString()));
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("processingInfo");
	}

	/**
	 * Accessor for the processing date. Returns a copy.
	 * 
	 * @deprecated Because DDMS 4.1 added a new allowable date format (ddms:DateHourMinType),
	 *             XMLGregorianCalendar is no longer a sufficient representation. This accessor will return
	 *             null for dates in the new format. Use <code>getDateProcessedString()</code> to
	 *             access the raw XML format of the date instead.
	 */
	public XMLGregorianCalendar getDateProcessed() {
		try {
			return (getFactory().newXMLGregorianCalendar(getDateProcessedString()));
		}
		catch (IllegalArgumentException e) {
			return (null);
		}
	}

	/**
	 * Accessor for the processing date.
	 */
	public String getDateProcessedString() {
		return (getAttributeValue(DATE_PROCESSED_NAME));
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
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractSimpleString.Builder {
		private static final long serialVersionUID = -7348511606867959470L;
		private String _dateProcessed;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ProcessingInfo info) {
			super(info);
			setDateProcessed(info.getDateProcessedString());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ProcessingInfo commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new ProcessingInfo(getValue(), getDateProcessed(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (super.isEmpty() && Util.isEmpty(getDateProcessed()));
		}

		/**
		 * Builder accessor for the dateProcessed
		 */
		public String getDateProcessed() {
			return _dateProcessed;
		}

		/**
		 * Builder accessor for the dateProcessed
		 */
		public void setDateProcessed(String dateProcessed) {
			_dateProcessed = dateProcessed;
		}
	}
}