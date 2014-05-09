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
 * An immutable implementation of ddms:source.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:qualifier|0..1|11111}
 * 		{@child.info ddms:value|0..1|11111}
 * 		{@child.info ddms:schemaQualifier|0..1|11111}
 * 		{@child.info ddms:schemaHref|0..1|11111}
 * 		{@child.info ddms:value|0..1|11111}
 * 		{@child.info ddms:value|0..1|11111}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If set, ddms:schemaHref must be a valid URI.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Source extends AbstractQualifierValue {

	private SecurityAttributes _securityAttributes = null;

	private static final String SCHEMA_QUALIFIER_NAME = "schemaQualifier";
	private static final String SCHEMA_HREF_NAME = "schemaHref";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Source(Element element) throws InvalidDDMSException {
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
	 * @param qualifier the value of the qualifier attribute
	 * @param value the value of the value attribute
	 * @param schemaQualifier the value of the schemaQualifier attribute
	 * @param schemaHref the value of the schemaHref attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Source(String qualifier, String value, String schemaQualifier, String schemaHref,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		super(Source.getName(DDMSVersion.getCurrentVersion()), qualifier, value, false, true);
		try {
			Element element = getXOMElement();
			Util.addDDMSAttribute(element, SCHEMA_QUALIFIER_NAME, schemaQualifier);
			Util.addDDMSAttribute(element, SCHEMA_HREF_NAME, schemaHref);
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
		Util.requireDDMSQName(getXOMElement(), Source.getName(getDDMSVersion()));
		if (!Util.isEmpty(getSchemaHref())) {
			Util.requireDDMSValidURI(getSchemaHref());
		}
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
		if (Util.isEmpty(getQualifier()) && Util.isEmpty(getValue()) && Util.isEmpty(getSchemaQualifier())
			&& Util.isEmpty(getSchemaHref())) {
			addWarning("A completely empty ddms:source element was found.");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + getQualifierName(), getQualifier()));
		text.append(buildOutput(isHTML, localPrefix + getValueName(), getValue()));
		text.append(buildOutput(isHTML, localPrefix + SCHEMA_QUALIFIER_NAME, getSchemaQualifier()));
		text.append(buildOutput(isHTML, localPrefix + SCHEMA_HREF_NAME, getSchemaHref()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Source))
			return (false);
		Source test = (Source) obj;
		return (getSchemaQualifier().equals(test.getSchemaQualifier()) && getSchemaHref().equals(test.getSchemaHref()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getSchemaQualifier().hashCode();
		result = 7 * result + getSchemaHref().hashCode();
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
		return ("source");
	}

	/**
	 * Accessor for the value of the schema qualifier
	 */
	public String getSchemaQualifier() {
		return (getAttributeValue(SCHEMA_QUALIFIER_NAME));
	}

	/**
	 * Accessor for the value of the schema href
	 */
	public String getSchemaHref() {
		return (getAttributeValue(SCHEMA_HREF_NAME));
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
		private static final long serialVersionUID = -514632949760329348L;
		private String _schemaQualifier;
		private String _schemaHref;
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
		public Builder(Source source) {
			super(source);
			setSchemaQualifier(source.getSchemaQualifier());
			setSchemaHref(source.getSchemaHref());
			setSecurityAttributes(new SecurityAttributes.Builder(source.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Source commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Source(getQualifier(), getValue(), getSchemaQualifier(), getSchemaHref(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (super.isEmpty()
				&& Util.isEmpty(getSchemaQualifier())
				&& Util.isEmpty(getSchemaHref())
				&& getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the schema qualifier
		 */
		public String getSchemaQualifier() {
			return _schemaQualifier;
		}

		/**
		 * Builder accessor for the schema qualifier
		 */
		public void setSchemaQualifier(String schemaQualifier) {
			_schemaQualifier = schemaQualifier;
		}

		/**
		 * Builder accessor for the schema href
		 */
		public String getSchemaHref() {
			return _schemaHref;
		}

		/**
		 * Builder accessor for the schemah ref
		 */
		public void setSchemaHref(String schemaHref) {
			_schemaHref = schemaHref;
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