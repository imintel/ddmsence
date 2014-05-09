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
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:productionMetric.
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
 * 		{@child.info ddms:subject|1|00011}
 * 		{@child.info ddms:coverage|1|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:subject must exist.|Error|11111}
 * 		{@ddms.rule ddms:coverage must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class ProductionMetric extends AbstractBaseComponent {

	private SecurityAttributes _securityAttributes = null;

	private static final String SUBJECT_NAME = "subject";
	private static final String COVERAGE_NAME = "coverage";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProductionMetric(Element element) throws InvalidDDMSException {
		try {
			_securityAttributes = new SecurityAttributes(element);
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @param subject a method of categorizing the subject of a document in a fashion understandable by DDNI-A
	 * @param coverage a method of categorizing the coverage of a document in a fashion understandable by DDNI-A
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProductionMetric(String subject, String coverage, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(ProductionMetric.getName(DDMSVersion.getCurrentVersion()), null);
			Util.addDDMSAttribute(element, SUBJECT_NAME, subject);
			Util.addDDMSAttribute(element, COVERAGE_NAME, coverage);
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
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSQName(getXOMElement(), ProductionMetric.getName(getDDMSVersion()));
		Util.requireDDMSValue("subject attribute", getSubject());
		Util.requireDDMSValue("coverage attribute", getCoverage());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + SUBJECT_NAME, getSubject()));
		text.append(buildOutput(isHTML, localPrefix + COVERAGE_NAME, getCoverage()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ProductionMetric))
			return (false);
		ProductionMetric test = (ProductionMetric) obj;
		return (getSubject().equals(test.getSubject()) 
			&& getCoverage().equals(test.getCoverage()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getSubject().hashCode();
		result = 7 * result + getCoverage().hashCode();
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
		return ("productionMetric");
	}

	/**
	 * Accessor for the subject attribute.
	 */
	public String getSubject() {
		return (getAttributeValue(SUBJECT_NAME));
	}

	/**
	 * Accessor for the coverage attribute.
	 */
	public String getCoverage() {
		return (getAttributeValue(COVERAGE_NAME));
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
	 * @since 2.0.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -9012648230977148516L;
		private String _subject;
		private String _coverage;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ProductionMetric metric) {
			setSubject(metric.getSubject());
			setCoverage(metric.getCoverage());
			setSecurityAttributes(new SecurityAttributes.Builder(metric.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ProductionMetric commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new ProductionMetric(getSubject(), getCoverage(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getSubject()) && Util.isEmpty(getCoverage()) && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the subject attribute
		 */
		public String getSubject() {
			return _subject;
		}

		/**
		 * Builder accessor for the subject attribute
		 */
		public void setSubject(String subject) {
			_subject = subject;
		}

		/**
		 * Builder accessor for the coverage attribute
		 */
		public String getCoverage() {
			return _coverage;
		}

		/**
		 * Builder accessor for the coverage attribute
		 */
		public void setCoverage(String coverage) {
			_coverage = coverage;
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