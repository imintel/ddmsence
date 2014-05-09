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
package buri.ddmsence;

import java.io.Serializable;

import nu.xom.Element;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Subtitle;
import buri.ddmsence.ddms.resource.Title;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.Description;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Base class for DDMS elements which consist of simple child text, possibly decorated with attributes, such as
 * {@link Description}, {@link Title}, and {@link Subtitle}.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p> Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set
 * before the component is used. </p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:classification|1|11111}
 * 		{@child.info ism:ownerProducer|1..*|11111}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public abstract class AbstractSimpleString extends AbstractBaseComponent {

	private SecurityAttributes _securityAttributes = null;

	/**
	 * Base constructor which works from a XOM element.
	 * 
	 * @param element the XOM element
	 * @param validateNow true if the component should be validated here
	 */
	protected AbstractSimpleString(Element element, boolean validateNow) throws InvalidDDMSException {
		try {
			_securityAttributes = new SecurityAttributes(element);
			setXOMElement(element, validateNow);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param name the name of the element without a prefix
	 * @param value the value of the element's child text
	 * @param attributes the security attributes
	 * @param validateNow true if the component should be validated here
	 */
	protected AbstractSimpleString(String name, String value, SecurityAttributes attributes, boolean validateNow)
		throws InvalidDDMSException {
		this(PropertyReader.getPrefix("ddms"), DDMSVersion.getCurrentVersion().getNamespace(), name, value, attributes,
			validateNow);
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param prefix the XML prefix
	 * @param namespace the namespace of the element
	 * @param name the name of the element without a prefix
	 * @param value the value of the element's child text
	 * @param attributes the security attributes
	 * @param validateNow true if the component should be validated here
	 */
	protected AbstractSimpleString(String prefix, String namespace, String name, String value,
		SecurityAttributes attributes, boolean validateNow) throws InvalidDDMSException {
		try {
			Element element = Util.buildElement(prefix, name, namespace, value);
			_securityAttributes = SecurityAttributes.getNonNullInstance(attributes);
			_securityAttributes.addTo(element);
			setXOMElement(element, validateNow);
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
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof AbstractSimpleString))
			return (false);
		AbstractSimpleString test = (AbstractSimpleString) obj;
		return (getValue().equals(test.getValue()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getValue().hashCode();
		return (result);
	}

	/**
	 * Accessor for the child text of the description. The underlying XOM method which retrieves the child text returns
	 * an empty string if not found.
	 */
	public String getValue() {
		return (getXOMElement().getValue());
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null even if the attributes are not set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}

	/**
	 * Abstract Builder for this DDMS component.
	 * 
	 * <p>Builders which are based upon this abstract class should implement the commit() method, returning the
	 * appropriate concrete object type.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static abstract class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7824644958681123708L;
		private String _value;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		protected Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		protected Builder(AbstractSimpleString simpleString) {
			setValue(simpleString.getValue());
			setSecurityAttributes(new SecurityAttributes.Builder(simpleString.getSecurityAttributes()));
		}

		/**
		 * Helper method to determine if any values have been entered for this producer.
		 * 
		 * @return true if all values are empty
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getValue()) && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the child text.
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the child text.
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
	}
}