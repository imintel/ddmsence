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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.ApproximableDate;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:temporalCoverage.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p>To avoid confusion between the name of the temporalCoverage element and the name of the specified time period,
 * the latter is referred to as the "time period name".
 * </p>
 * 
 * <p>
 * If not "Not Applicable" or "Unknown", date formats must adhere to one of the DDMS-allowed date formats.
 * </p>
 * 
 * {@table.header History}
 * 		<p>Before DDMS 4.0.1, a temporalCoverage element contains a locally defined TimePeriod construct.
 * 		This TimePeriod construct is a container for the name, start, and end values of a time period.
 * 		It exists only inside of a ddms:temporalCoverage parent, so it is not implemented as a Java object.
 * 		Starting in DDMS 4.0.1, the TimePeriod wrapper has been removed.</p>
 * 		<p>Starting in DDMS 4.1, the start and end dates may optionally be replaced by an approximableStart
 * 		or approximableEnd date.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:name|0..1|11111}
 * 		{@child.info ddms:start|0..1|11111}	
 * 		{@child.info ddms:end|0..1|11111}	
 * 		{@child.info ddms:approximableStart|0..1|00011}
 * 		{@child.info ddms:approximableEnd|0..1|00011}		
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If set, ddms:start must adhere to a valid date format.|Error|11111}
 * 		{@ddms.rule If set, ddms:end must adhere to a valid date format.|Error|11111}
 * 		{@ddms.rule Approximable dates must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule An empty ddms:name will be given a default value.|Warning|11111}
 * 		{@ddms.rule Approximable dates may cause issues for DDMS 4.0.1 systems.|Warning|00010}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class TemporalCoverage extends AbstractBaseComponent {

	private String _name = DEFAULT_VALUE;
	private ApproximableDate _approximableStart = null;
	private ApproximableDate _approximableEnd = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String DEFAULT_VALUE = "Unknown";

	private static Set<String> EXTENDED_DATE_TYPES = new HashSet<String>();
	static {
		EXTENDED_DATE_TYPES.add("Not Applicable");
		EXTENDED_DATE_TYPES.add("Unknown");
	}

	// The name of the TimePeriod element itself
	private static final String TIME_PERIOD_NAME = "TimePeriod";

	// The name of the "name" element nested inside the temporalCoverage element
	private static final String TIME_PERIOD_NAME_NAME = "name";

	private static final String START_NAME = "start";
	private static final String END_NAME = "end";
	private static final String APPROXIMABLE_START_NAME = "approximableStart";
	private static final String APPROXIMABLE_END_NAME = "approximableEnd";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TemporalCoverage(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element periodElement = getTimePeriodElement();
			if (periodElement != null) {
				Element nameElement = periodElement.getFirstChildElement(TIME_PERIOD_NAME_NAME, getNamespace());
				if (nameElement != null && !Util.isEmpty(nameElement.getValue()))
					_name = nameElement.getValue();

				Element approximableStart = element.getFirstChildElement(APPROXIMABLE_START_NAME, getNamespace());
				if (approximableStart != null)
					_approximableStart = new ApproximableDate(approximableStart);
				Element approximableEnd = element.getFirstChildElement(APPROXIMABLE_END_NAME, getNamespace());
				if (approximableEnd != null)
					_approximableEnd = new ApproximableDate(approximableEnd);
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
	 * Constructor for creating a component from raw data, using two exact date values.
	 * 
	 * @param timePeriodName the time period name
	 * @param startString a string representation of the date
	 * @param endString a string representation of the end date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TemporalCoverage(String timePeriodName, String startString, String endString,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(timePeriodName, startString, null, endString, null, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data, using an exact start date
	 * and an approximable end date.
	 * 
	 * @param timePeriodName the time period name
	 * @param startString a string representation of the date
	 * @param approximableEnd the end date, as an approximable date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TemporalCoverage(String timePeriodName, String startString, ApproximableDate approximableEnd,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(timePeriodName, startString, null, null, approximableEnd, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data, using an approximable start date
	 * and an exact end date.
	 * 
	 * @param timePeriodName the time period name
	 * @param approximableStart the start date, as an approximable date
	 * @param endString a string representation of the end date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TemporalCoverage(String timePeriodName, ApproximableDate approximableStart, String endString,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(timePeriodName, null, approximableStart, endString, null, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data, using two approximable dates.
	 * 
	 * @param timePeriodName the time period name (if empty, defaults to Unknown)
	 * @param approximableStart the start date, as an approximable date
	 * @param approximableEnd the end date, as an approximable date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TemporalCoverage(String timePeriodName, ApproximableDate approximableStart,
		ApproximableDate approximableEnd, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(timePeriodName, null, approximableStart, null, approximableEnd, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data, which handles all permutations of exact and
	 * approximable date formats.
	 * 
	 * @param timePeriodName the time period name (if empty, defaults to Unknown)
	 * @param startString a string representation of the date (if empty, defaults to Unknown)
	 * @param approximableStart the start date, as an approximable date
	 * @param endString a string representation of the end date (if empty, defaults to Unknown)
	 * @param approximableEnd the end date, as an approximable date
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	private TemporalCoverage(String timePeriodName, String startString, ApproximableDate approximableStart,
		String endString, ApproximableDate approximableEnd, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(TemporalCoverage.getName(DDMSVersion.getCurrentVersion()), null);
			Element periodElement = DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? element
				: Util.buildDDMSElement(TIME_PERIOD_NAME, null);
			if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				element.appendChild(periodElement);
			if (!Util.isEmpty(timePeriodName))
				_name = timePeriodName;
			Util.addDDMSChildElement(periodElement, TIME_PERIOD_NAME_NAME, timePeriodName);

			if (approximableStart != null) {
				element.appendChild(approximableStart.getXOMElementCopy());
				_approximableStart = approximableStart;
			}
			else {
				startString = (Util.isEmpty(startString) ? DEFAULT_VALUE : startString);
				periodElement.appendChild(Util.buildDDMSElement(START_NAME, startString));
			}

			if (approximableEnd != null) {
				element.appendChild(approximableEnd.getXOMElementCopy());
				_approximableEnd = approximableEnd;
			}
			else {
				endString = (Util.isEmpty(endString) ? DEFAULT_VALUE : endString);
				periodElement.appendChild(Util.buildDDMSElement(END_NAME, endString));
			}

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
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), TemporalCoverage.getName(getDDMSVersion()));
		Element periodElement = getTimePeriodElement();
		Util.requireDDMSValue("TimePeriod element", periodElement);
		if (getApproximableStart() == null) {
			Util.requireDDMSValue("start", getStartString());
			if (!EXTENDED_DATE_TYPES.contains(getStartString()))
				Util.requireDDMSDateFormat(getStartString(), getNamespace());
		}
		if (getApproximableEnd() == null) {
			Util.requireDDMSValue("end", getEndString());
			if (!EXTENDED_DATE_TYPES.contains(getEndString()))
				Util.requireDDMSDateFormat(getEndString(), getNamespace());
		}
		// Check for approximableDates is implicit, since they cannot be instantiated before 4.1.
		if (!getDDMSVersion().isAtLeast("3.0") && !getSecurityAttributes().isEmpty()) {
			throw new InvalidDDMSException(
				"Security attributes must not be applied to this component until DDMS 3.0 or later.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		Element periodElement = getTimePeriodElement();
		Element timePeriodName = periodElement.getFirstChildElement(TIME_PERIOD_NAME_NAME,
			periodElement.getNamespaceURI());
		if (timePeriodName != null && Util.isEmpty(timePeriodName.getValue()))
			addWarning("A ddms:name element was found with no value. Defaulting to \"" + DEFAULT_VALUE + "\".");
		if ("4.1".equals(getDDMSVersion().getVersion())
			&& (getApproximableStart() != null || getApproximableEnd() != null))
			addDdms40Warning("ddms:approximableStart or ddms:approximableEnd element");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getLocatorSuffix()
	 */
	protected String getLocatorSuffix() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "" : ValidationMessage.ELEMENT_PREFIX
			+ getXOMElement().getNamespacePrefix() + ":" + TIME_PERIOD_NAME);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		if (!getDDMSVersion().isAtLeast("4.0.1"))
			localPrefix += TIME_PERIOD_NAME + ".";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + TIME_PERIOD_NAME_NAME, getTimePeriodName()));
		text.append(buildOutput(isHTML, localPrefix + START_NAME, getStartString()));
		text.append(buildOutput(isHTML, localPrefix + END_NAME, getEndString()));
		if (getApproximableStart() != null)
			text.append(getApproximableStart().getOutput(isHTML, localPrefix, ""));
		if (getApproximableEnd() != null)
			text.append(getApproximableEnd().getOutput(isHTML, localPrefix, ""));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getApproximableStart());
		list.add(getApproximableEnd());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof TemporalCoverage))
			return (false);
		TemporalCoverage test = (TemporalCoverage) obj;
		return (getTimePeriodName().equals(test.getTimePeriodName()) 
			&& getStartString().equals(test.getStartString())
			&& getEndString().equals(test.getEndString()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getTimePeriodName().hashCode();
		result = 7 * result + getStartString().hashCode();
		result = 7 * result + getEndString().hashCode();
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
		return ("temporalCoverage");
	}

	/**
	 * Accessor for the element which contains the time period name, start date, and end date. Before DDMS 4.0.1,
	 * this is a wrapper element called ddms:TimePeriod. Starting in DDMS 4.0.1, it is the ddms:temporalCoverage
	 * element itself.
	 */
	private Element getTimePeriodElement() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? getXOMElement() : getChild(TIME_PERIOD_NAME));
	}

	/**
	 * Accessor for the TimePeriod name element child text. Note that the getName() accessor will
	 * return the local name of the temporal coverage element (temporalCoverage).
	 */
	public String getTimePeriodName() {
		return (_name);
	}

	/**
	 * Accessor for the XML calendar representing the start date
	 * 
	 * @deprecated Because DDMS 4.1 added a new allowable date format (ddms:DateHourMinType),
	 *             XMLGregorianCalendar is no longer a sufficient representation. This accessor will return
	 *             null for dates in the new format. Use <code>getStartString()</code> to
	 *             access the raw XML format of the date, "Not Applicable", or "Unknown" values instead.
	 */
	public XMLGregorianCalendar getStart() {
		try {
			return (getFactory().newXMLGregorianCalendar(getStartString()));
		}
		catch (IllegalArgumentException e) {
			return (null);
		}
	}

	/**
	 * Accessor for the start date as a string. If the value of start cannot be represented by an XML calendar, this
	 * will return "Not Applicable" or "Unknown". Use <code>getStart</code> to work with this value as a calendar date.
	 */
	public String getStartString() {
		Element startElement = getTimePeriodElement().getFirstChildElement(START_NAME, getNamespace());
		if (startElement == null)
			return ("");
		String value = startElement.getValue();
		if (Util.isEmpty(value))
			return (DEFAULT_VALUE);
		return (value);
		// return (Util.isEmpty(value) ? DEFAULT_VALUE : value);
	}

	/**
	 * Accessor for the XML calendar representing the end date
	 * 
	 * @deprecated Because DDMS 4.1 added a new allowable date format (ddms:DateHourMinType),
	 *             XMLGregorianCalendar is no longer a sufficient representation. This accessor will return
	 *             null for dates in the new format. Use <code>getEndString()</code> to
	 *             access the raw XML format of the date, "Not Applicable", or "Unknown" values instead.
	 */
	public XMLGregorianCalendar getEnd() {
		try {
			return (getFactory().newXMLGregorianCalendar(getEndString()));
		}
		catch (IllegalArgumentException e) {
			return (null);
		}
	}

	/**
	 * Accessor for the end date as a string. If the value of end cannot be represented by an XML calendar, this will
	 * return "Not Applicable" or "Unknown". Use <code>getEnd</code> to work with this value as a calendar date.
	 */
	public String getEndString() {
		Element endElement = getTimePeriodElement().getFirstChildElement(END_NAME, getNamespace());
		if (endElement == null)
			return ("");
		String value = endElement.getValue();
		return (Util.isEmpty(value) ? DEFAULT_VALUE : value);
	}

	/**
	 * Accessor for the approximableStart date.
	 */
	public ApproximableDate getApproximableStart() {
		return (_approximableStart);
	}

	/**
	 * Accessor for the approximableStart date.
	 */
	public ApproximableDate getApproximableEnd() {
		return (_approximableEnd);
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null, even if it has no values set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
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
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -3187482277963663663L;
		private String _timePeriodName;
		private String _startString;
		private String _endString;
		private ApproximableDate.Builder _approximableStart;
		private ApproximableDate.Builder _approximableEnd;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(TemporalCoverage coverage) {
			setTimePeriodName(coverage.getTimePeriodName());
			setStartString(coverage.getStartString());
			setEndString(coverage.getEndString());
			if (coverage.getApproximableStart() != null)
				setApproximableStart(new ApproximableDate.Builder(coverage.getApproximableStart()));
			if (coverage.getApproximableEnd() != null)
				setApproximableEnd(new ApproximableDate.Builder(coverage.getApproximableEnd()));
			setSecurityAttributes(new SecurityAttributes.Builder(coverage.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public TemporalCoverage commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			if (!getApproximableStart().isEmpty() && !Util.isEmpty(getStartString()))
				throw new InvalidDDMSException("Only 1 of start or approximableStart must be used.");
			if (!getApproximableEnd().isEmpty() && !Util.isEmpty(getEndString()))
				throw new InvalidDDMSException("Only 1 of end or approximableEnd must be used.");
			if (!getApproximableStart().isEmpty() && !getApproximableEnd().isEmpty())
				return (new TemporalCoverage(getTimePeriodName(), getApproximableStart().commit(),
					getApproximableEnd().commit(), getSecurityAttributes().commit()));
			if (!getApproximableStart().isEmpty() && getApproximableEnd().isEmpty())
				return (new TemporalCoverage(getTimePeriodName(), getApproximableStart().commit(),
					getEndString(), getSecurityAttributes().commit()));
			if (getApproximableStart().isEmpty() && !getApproximableEnd().isEmpty())
				return (new TemporalCoverage(getTimePeriodName(), getStartString(),
					getApproximableEnd().commit(), getSecurityAttributes().commit()));
			return (new TemporalCoverage(getTimePeriodName(), getStartString(), getApproximableStart().commit(),
				getEndString(), getApproximableEnd().commit(), getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getTimePeriodName())
				&& Util.isEmpty(getStartString())
				&& Util.isEmpty(getEndString())
				&& getApproximableStart().isEmpty()
				&& getApproximableEnd().isEmpty()
				&& getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the TimePeriod name element child text.
		 */
		public String getTimePeriodName() {
			return _timePeriodName;
		}

		/**
		 * Builder accessor for the TimePeriod name element child text.
		 */
		public void setTimePeriodName(String timePeriodName) {
			_timePeriodName = timePeriodName;
		}

		/**
		 * Builder accessor for the start date as a string.
		 */
		public String getStartString() {
			return _startString;
		}

		/**
		 * Builder accessor for the start date as a string.
		 */
		public void setStartString(String startString) {
			_startString = startString;
		}

		/**
		 * Builder accessor for the end date as a string.
		 */
		public String getEndString() {
			return _endString;
		}

		/**
		 * Builder accessor for the end date as a string.
		 */
		public void setEndString(String endString) {
			_endString = endString;
		}

		/**
		 * Builder accessor for the approximableStart
		 */
		public ApproximableDate.Builder getApproximableStart() {
			if (_approximableStart == null) {
				_approximableStart = new ApproximableDate.Builder();
				_approximableStart.setName(APPROXIMABLE_START_NAME);
			}
			return _approximableStart;
		}

		/**
		 * Builder accessor for the approximableStart
		 */
		public void setApproximableStart(ApproximableDate.Builder approximableStart) {
			_approximableStart = approximableStart;
		}

		/**
		 * Builder accessor for the approximableEnd
		 */
		public ApproximableDate.Builder getApproximableEnd() {
			if (_approximableEnd == null) {
				_approximableEnd = new ApproximableDate.Builder();
				_approximableEnd.setName(APPROXIMABLE_END_NAME);
			}
			return _approximableEnd;
		}

		/**
		 * Builder accessor for the approximableEnd
		 */
		public void setApproximableEnd(ApproximableDate.Builder approximableEnd) {
			_approximableEnd = approximableEnd;
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