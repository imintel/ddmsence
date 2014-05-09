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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractQualifierValue;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:type.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		<p>Beginning in DDMS 4.0.1, a ddms:type element can contain child text. The intent of this text is to provide 
 * 		further context when the ddms:type element references an IC activity.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:qualifier|0..1|11111}
 * 		{@child.info ddms:value|0..1|11111}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:qualifier must exist if ddms:value is set.|Error|11111}
 * 		{@ddms.rule The child text must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule A ddms:qualifier can be set with no ddms:value.|Warning|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Type extends AbstractQualifierValue {

	private SecurityAttributes _securityAttributes = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Type(Element element) throws InvalidDDMSException {
		// Defaults to qualifier-value over codespace-code.
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
	 * Constructor for creating a component from raw data
	 * 
	 * @param description the child text describing an IC activity, if this component is used to reference an IC
	 *        activity
	 * @param qualifier the value of the qualifier attribute
	 * @param value the value of the value attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Type(String description, String qualifier, String value, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(Type.getName(DDMSVersion.getCurrentVersion()), qualifier, value, false, true);
		try {
			Element element = getXOMElement();
			if (!Util.isEmpty(description))
				element.appendChild(description);
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
		Util.requireDDMSQName(getXOMElement(), Type.getName(getDDMSVersion()));
		if (!Util.isEmpty(getValue()))
			Util.requireDDMSValue("qualifier attribute", getQualifier());
		if (!getDDMSVersion().isAtLeast("4.0.1")) {
			if (!Util.isEmpty(getDescription()))
				throw new InvalidDDMSException(
					"This component must not contain description child text until DDMS 4.0.1 or later.");
			if (!getSecurityAttributes().isEmpty())
				throw new InvalidDDMSException(
					"Security attributes must not be applied to this component until DDMS 4.0.1 or later.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (!Util.isEmpty(getQualifier()) && Util.isEmpty(getValue()))
			addWarning("A qualifier has been set without an accompanying value attribute.");
		if (Util.isEmpty(getQualifier()) && Util.isEmpty(getValue()))
			addWarning("Neither a qualifier nor a value was set on this type.");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + "description", getDescription()));
		text.append(buildOutput(isHTML, localPrefix + getQualifierName(), getQualifier()));
		text.append(buildOutput(isHTML, localPrefix + getValueName(), getValue()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Type))
			return (false);
		Type test = (Type) obj;
		return (getDescription().equals(test.getDescription()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getDescription().hashCode();
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
		return ("type");
	}

	/**
	 * Accessor for the description child text, which provides additional context to the qualifier/value pairing of this
	 * component. The underlying XOM method which retrieves the child text returns an empty string if not found.
	 */
	public String getDescription() {
		return (getXOMElement().getValue());
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
	 * @since 1.8.0
	 */
	public static class Builder extends AbstractQualifierValue.Builder {
		private static final long serialVersionUID = 4388694130954618393L;
		private String _description;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Type type) {
			super(type);
			setDescription(type.getDescription());
			setSecurityAttributes(new SecurityAttributes.Builder(type.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Type commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Type(getDescription(), getQualifier(), getValue(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (super.isEmpty() && Util.isEmpty(getDescription()) && getSecurityAttributes().isEmpty());
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