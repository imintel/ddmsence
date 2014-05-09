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
package buri.ddmsence.ddms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.util.Util;

/**
 * Base class for DDMS elements which are an approximable date, such as ddms:dates/ddms:acquiredOn.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p>
 * The structure of this class diverges from the usual DDMSence approach of selecting which DDMS components are
 * implemented as Java classes. The ApproximableDateType, introduced in DDMS 4.1, is directly reused in three locations
 * in the DDMS schema, so it is implemented as a final class rather than an Abstract class. It contains one wrapper
 * element, ddms:searchableDate, which is not implemented as a Java class.
 * </p>
 * 
 * <p>
 * This type also contains one element, ddms:approximableDate, which should be implemented as a Java class, since it
 * contains an attribute. To simplify the class structure, this element and its attribute are collapsed into this Java
 * class.
 * </p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:description|0..1|00011}
 * 		{@child.info ddms:approximableDate|0..1|00011}
 * 		{@child.info ddms:searchableDate/ddms:start|0..1|00011}
 * 		{@child.info ddms:searchableDate/ddms:end|0..1|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:approximableDate/@ddms:approximation|0..1|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If set, the ddms:approximableDate must adhere to a valid date format.|Error|11111}
 * 		{@ddms.rule If set, the ddms:approximableDate/@ddms:approximation is a valid token.|Error|11111}
 * 		{@ddms.rule If set, the ddms:searchableDate/ddms:start must adhere to a valid date format.|Error|11111}
 * 		{@ddms.rule If set, the ddms:searchableDate/ddms:end must adhere to a valid date format.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.1.0
 */
public final class ApproximableDate extends AbstractBaseComponent {

	private static final String DESCRIPTION_NAME = "description";
	private static final String APPROXIMABLE_DATE_NAME = "approximableDate";
	private static final String APPROXIMATION_NAME = "approximation";
	private static final String SEARCHABLE_DATE_NAME = "searchableDate";
	private static final String START_NAME = "start";
	private static final String END_NAME = "end";

	private static Set<String> APPROXIMATION_TYPES = new HashSet<String>();
	static {
		APPROXIMATION_TYPES.add("1st qtr");
		APPROXIMATION_TYPES.add("2nd qtr");
		APPROXIMATION_TYPES.add("3rd qtr");
		APPROXIMATION_TYPES.add("4th qtr");
		APPROXIMATION_TYPES.add("circa");
		APPROXIMATION_TYPES.add("early");
		APPROXIMATION_TYPES.add("mid");
		APPROXIMATION_TYPES.add("late");
	}

	private static Set<String> NAME_TYPES = new HashSet<String>();
	static {
		NAME_TYPES.add("acquiredOn");
		NAME_TYPES.add("approximableStart");
		NAME_TYPES.add("approximableEnd");
	}

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ApproximableDate(Element element) throws InvalidDDMSException {
		super.setXOMElement(element, true);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param name the name of the element
	 * @param description the description of this approximable date
	 * @param approximableDate the value of the approximable date
	 * @param approximation an attribute that decorates the date
	 * @param searchableStartDate the lower bound for this approximable date
	 * @param searchableEndDate the upper bound for this approximable date
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ApproximableDate(String name, String description, String approximableDate, String approximation,
		String searchableStartDate, String searchableEndDate) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(name, null);
			setXOMElement(element, false);

			if (!Util.isEmpty(description)) {
				Util.addDDMSChildElement(getXOMElement(), DESCRIPTION_NAME, description);
			}
			if (!Util.isEmpty(approximableDate) || Util.isEmpty(approximation)) {
				Element approximableElment = Util.buildDDMSElement(APPROXIMABLE_DATE_NAME, approximableDate);
				Util.addDDMSAttribute(approximableElment, APPROXIMATION_NAME, approximation);
				getXOMElement().appendChild(approximableElment);
			}
			if (!Util.isEmpty(searchableStartDate) || Util.isEmpty(searchableEndDate)) {
				Element searchableElement = Util.buildDDMSElement(SEARCHABLE_DATE_NAME, null);
				Util.addDDMSChildElement(searchableElement, START_NAME, searchableStartDate);
				Util.addDDMSChildElement(searchableElement, END_NAME, searchableEndDate);
				getXOMElement().appendChild(searchableElement);
			}
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Validates an approximation against the allowed values.
	 * 
	 * @param approximation the value to test
	 * @throws InvalidDDMSException if the value is null, empty or invalid.
	 */
	public static void validateApproximation(String approximation) throws InvalidDDMSException {
		Util.requireDDMSValue("approximation", approximation);
		if (!APPROXIMATION_TYPES.contains(approximation))
			throw new InvalidDDMSException("The approximation must be one of " + APPROXIMATION_TYPES);
	}

	/**
	 * Validates an element name against the allowed values.
	 * 
	 * @param name the value to test
	 * @throws InvalidDDMSException if the value is null, empty or invalid.
	 */
	public static void validateElementName(String name) throws InvalidDDMSException {
		Util.requireDDMSValue("name", name);
		if (!NAME_TYPES.contains(name))
			throw new InvalidDDMSException("The element name must be one of " + NAME_TYPES);
	}
	 
	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtLeastVersion("4.1");
		validateElementName(getName());
		if (!Util.isEmpty(getApproximableDateString()))
			Util.requireDDMSDateFormat(getApproximableDateString(), getNamespace());
		if (!Util.isEmpty(getApproximation())) {
			validateApproximation(getApproximation());
		}
		if (!Util.isEmpty(getSearchableStartString()))
			Util.requireDDMSDateFormat(getSearchableStartString(), getNamespace());
		if (!Util.isEmpty(getSearchableEndString()))
			Util.requireDDMSDateFormat(getSearchableEndString(), getNamespace());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (Util.isEmpty(getDescription())
			&& Util.isEmpty(getApproximableDateString())
			&& Util.isEmpty(getApproximation())
			&& Util.isEmpty(getSearchableStartString())
			&& Util.isEmpty(getSearchableEndString())) {
			addWarning("A completely empty " + getQualifiedName() + " element was found.");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + "." + DESCRIPTION_NAME, getDescription()));
		text.append(buildOutput(isHTML, localPrefix + "." + APPROXIMABLE_DATE_NAME, getApproximableDateString()));
		text.append(buildOutput(isHTML, localPrefix + "." + APPROXIMABLE_DATE_NAME + "." + APPROXIMATION_NAME,
			getApproximation()));
		text.append(buildOutput(isHTML, localPrefix + "." + SEARCHABLE_DATE_NAME + "." + START_NAME,
			getSearchableStartString()));
		text.append(buildOutput(isHTML, localPrefix + "." + SEARCHABLE_DATE_NAME + "." + END_NAME,
			getSearchableEndString()));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ApproximableDate))
			return (false);
		ApproximableDate test = (ApproximableDate) obj;
		return (getDescription().equals(test.getDescription())
			&& getApproximableDateString().equals(test.getApproximableDateString())
			&& getApproximation().equals(test.getApproximation())
			&& getSearchableStartString().equals(test.getSearchableStartString())
			&& getSearchableEndString().equals(test.getSearchableEndString()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getDescription().hashCode();
		result = 7 * result + getApproximableDateString().hashCode();
		result = 7 * result + getApproximation().hashCode();
		result = 7 * result + getSearchableStartString().hashCode();
		result = 7 * result + getSearchableEndString().hashCode();
		return (result);
	}

	/**
	 * Accessor for the description.
	 */
	public String getDescription() {
		Element descriptionElement = getChild(DESCRIPTION_NAME);
		return (descriptionElement == null ? "" : Util.getNonNullString(descriptionElement.getValue()));
	}

	/**
	 * Accessor for the approximableDate.
	 */
	public String getApproximableDateString() {
		Element dateElement = getChild(APPROXIMABLE_DATE_NAME);
		return (dateElement == null ? "" : Util.getNonNullString(dateElement.getValue()));
	}

	/**
	 * Accessor for the value of the approximation attribute
	 */
	public String getApproximation() {
		String approximation = null;
		Element approximableDateElement = getChild(APPROXIMABLE_DATE_NAME);
		if (approximableDateElement != null) {
			approximation = approximableDateElement.getAttributeValue(APPROXIMATION_NAME, getNamespace());
		}
		return (Util.getNonNullString(approximation));
	}

	/**
	 * Accessor for the searchableStart date
	 */
	public String getSearchableStartString() {
		String date = "";
		Element dateElement = getChild(SEARCHABLE_DATE_NAME);
		if (dateElement != null) {
			Element startElement = dateElement.getFirstChildElement(START_NAME, getNamespace());
			if (startElement != null)
				date = Util.getNonNullString(startElement.getValue());
		}
		return (date);
	}

	/**
	 * Accessor for the searchableEnd date
	 */
	public String getSearchableEndString() {
		String date = "";
		Element dateElement = getChild(SEARCHABLE_DATE_NAME);
		if (dateElement != null) {
			Element endElement = dateElement.getFirstChildElement(END_NAME, getNamespace());
			if (endElement != null)
				date = Util.getNonNullString(endElement.getValue());
		}
		return (date);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.1.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -7348511606867959470L;
		private String _name;
		private String _description;
		private String _approximableDate;
		private String _approximation;
		private String _searchableStart;
		private String _searchableEnd;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ApproximableDate approximableDate) {
			setName(approximableDate.getName());
			setDescription(approximableDate.getDescription());
			setApproximableDate(approximableDate.getApproximableDateString());
			setApproximation(approximableDate.getApproximation());
			setSearchableStart(approximableDate.getSearchableStartString());
			setSearchableEnd(approximableDate.getSearchableEndString());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ApproximableDate commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new ApproximableDate(getName(), getDescription(), getApproximableDate(),
				getApproximation(), getSearchableStart(), getSearchableEnd()));
		}

		/**
		 * Does not include the element name.
		 * 
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getDescription()) && Util.isEmpty(getApproximableDate())
				&& Util.isEmpty(getApproximation()) && Util.isEmpty(getSearchableStart())
				&& Util.isEmpty(getSearchableEnd()));
		}

		/**
		 * Builder accessor for the name of the element
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Builder accessor for the name of the element
		 */
		public void setName(String name) {
			_name = name;
		}

		/**
		 * Builder accessor for the description
		 */
		public String getDescription() {
			return _description;
		}

		/**
		 * Builder accessor for the description
		 */
		public void setDescription(String description) {
			_description = description;
		}

		/**
		 * Builder accessor for the approximableDate
		 */
		public String getApproximableDate() {
			return _approximableDate;
		}

		/**
		 * Builder accessor for the approximableDate
		 */
		public void setApproximableDate(String approximableDate) {
			_approximableDate = approximableDate;
		}

		/**
		 * Builder accessor for the approximation
		 */
		public String getApproximation() {
			return _approximation;
		}

		/**
		 * Builder accessor for the approximation
		 */
		public void setApproximation(String approximation) {
			_approximation = approximation;
		}

		/**
		 * Builder accessor for the searchableStart
		 */
		public String getSearchableStart() {
			return _searchableStart;
		}

		/**
		 * Builder accessor for the searchableStart
		 */
		public void setSearchableStart(String searchableStart) {
			_searchableStart = searchableStart;
		}

		/**
		 * Builder accessor for the searchableEnd
		 */
		public String getSearchableEnd() {
			return _searchableEnd;
		}

		/**
		 * Builder accessor for the searchableEnd
		 */
		public void setSearchableEnd(String searchableEnd) {
			_searchableEnd = searchableEnd;
		}
	}
}