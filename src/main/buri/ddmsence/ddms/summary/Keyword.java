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
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:keyword.
 * <br /><br />
 * {@ddms.versions 11111}
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
 * 		{@child.info ddms:value|1|11111}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * 		{@child.info any:&lt;<i>extensibleAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:value must exist.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule Extensible attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Keyword extends AbstractBaseComponent {

	private SecurityAttributes _securityAttributes = null;
	private ExtensibleAttributes _extensibleAttributes = null;

	private static final String VALUE_NAME = "value";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Keyword(Element element) throws InvalidDDMSException {
		try {
			_securityAttributes = new SecurityAttributes(element);
			_extensibleAttributes = new ExtensibleAttributes(element);
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param value the value attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Keyword(String value, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(value, securityAttributes, null);
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * <p>This constructor will throw an InvalidDDMSException if the extensible attributes uses the reserved
	 * attribute "ddms:value".</p>
	 * 
	 * @param value the value attribute
	 * @param securityAttributes any security attributes
	 * @param extensibleAttributes extensible attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Keyword(String value, SecurityAttributes securityAttributes, ExtensibleAttributes extensibleAttributes)
		throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(Keyword.getName(DDMSVersion.getCurrentVersion()), null);
			Util.addDDMSAttribute(element, VALUE_NAME, value);
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
			_extensibleAttributes = ExtensibleAttributes.getNonNullInstance(extensibleAttributes);
			_extensibleAttributes.addTo(element);
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
		Util.requireDDMSQName(getXOMElement(), Keyword.getName(getDDMSVersion()));
		Util.requireDDMSValue("value attribute", getValue());
		if (!getDDMSVersion().isAtLeast("4.0.1") && !getSecurityAttributes().isEmpty()) {
			throw new InvalidDDMSException(
				"Security attributes must not be applied to this component until DDMS 4.0.1 or later.");
		}
		if (!getDDMSVersion().isAtLeast("3.0") && !getExtensibleAttributes().isEmpty())
			throw new InvalidDDMSException("xs:anyAttribute must not be applied to ddms:keyword until DDMS 3.0 or later.");
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getValue()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix + "."));
		text.append(getExtensibleAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Keyword))
			return (false);
		Keyword test = (Keyword) obj;
		return (getValue().equals(test.getValue()) && getExtensibleAttributes().equals(test.getExtensibleAttributes()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getValue().hashCode();
		result = 7 * result + getExtensibleAttributes().hashCode();
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
		return ("keyword");
	}

	/**
	 * Accessor for the value attribute.
	 */
	public String getValue() {
		return (getAttributeValue(VALUE_NAME));
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null, even if it has no values set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}

	/**
	 * Accessor for the extensible attributes. Will always be non-null, even if not set.
	 */
	public ExtensibleAttributes getExtensibleAttributes() {
		return (_extensibleAttributes);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 5165722850252388155L;
		private String _value;
		private SecurityAttributes.Builder _securityAttributes;
		private ExtensibleAttributes.Builder _extensibleAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Keyword keyword) {
			setValue(keyword.getValue());
			setSecurityAttributes(new SecurityAttributes.Builder(keyword.getSecurityAttributes()));
			setExtensibleAttributes(new ExtensibleAttributes.Builder(keyword.getExtensibleAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Keyword commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Keyword(getValue(), getSecurityAttributes().commit(),
				getExtensibleAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getValue())
				&& getSecurityAttributes().isEmpty()
				&& getExtensibleAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the value attribute
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the value attribute
		 */
		public void setValue(String value) {
			_value = value;
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

		/**
		 * Builder accessor for the Extensible Attributes
		 */
		public ExtensibleAttributes.Builder getExtensibleAttributes() {
			if (_extensibleAttributes == null)
				_extensibleAttributes = new ExtensibleAttributes.Builder();
			return _extensibleAttributes;
		}

		/**
		 * Builder accessor for the Extensible Attributes
		 */
		public void setExtensibleAttributes(ExtensibleAttributes.Builder extensibleAttributes) {
			_extensibleAttributes = extensibleAttributes;
		}
	}
}