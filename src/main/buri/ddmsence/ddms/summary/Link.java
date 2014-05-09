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
import buri.ddmsence.ddms.resource.RevisionRecall;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.xlink.XLinkAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:link.
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
 *  	{@child.info ism:classification|0..1|00011}
 * 		{@child.info ism:ownerProducer|0..*|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * 		{@child.info xlink:&lt;<i>xlinkAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule xlink:type must exist, and must be set to "locator".|Error|11111}
 * 		{@ddms.rule xlink:href must exist.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule Warnings from any XLink attributes are claimed by this component.|Warning|11111}
 * 		<p>When a Link is used by {@link RelatedResource}, it cannot have security attributes. When a Link is used by
 * 		{@link RevisionRecall}, it must have security attributes. This is validated in the parent class.</p>
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Link extends AbstractBaseComponent {

	private XLinkAttributes _xlinkAttributes = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String FIXED_TYPE = "locator";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Link(Element element) throws InvalidDDMSException {
		try {
			_xlinkAttributes = new XLinkAttributes(element);
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
	 * @param xlinkAttributes the xlink attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Link(XLinkAttributes xlinkAttributes) throws InvalidDDMSException {
		this(xlinkAttributes, null);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param xlinkAttributes the xlink attributes
	 * @param securityAttributes attributes, which are only allowed on links within a ddms:revisionRecall
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Link(XLinkAttributes xlinkAttributes, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(Link.getName(DDMSVersion.getCurrentVersion()), null);
			_xlinkAttributes = XLinkAttributes.getNonNullInstance(xlinkAttributes);
			_xlinkAttributes.addTo(element);
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
		Util.requireDDMSQName(getXOMElement(), Link.getName(getDDMSVersion()));
		Util.requireDDMSValue("type attribute", getXLinkAttributes().getType());
		if (!getXLinkAttributes().getType().equals(FIXED_TYPE))
			throw new InvalidDDMSException("The type attribute must have a fixed value of \"" + FIXED_TYPE + "\".");
		Util.requireDDMSValue("href attribute", getXLinkAttributes().getHref());
		if (!getDDMSVersion().isAtLeast("4.0.1") && !getSecurityAttributes().isEmpty()) {
			throw new InvalidDDMSException(
				"Security attributes must not be applied to this component until DDMS 4.0.1 or later.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (getXLinkAttributes() != null)
			addWarnings(getXLinkAttributes().getValidationWarnings(), true);
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(getXLinkAttributes().getOutput(isHTML, localPrefix));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Link))
			return (false);
		Link test = (Link) obj;
		return (getXLinkAttributes().equals(test.getXLinkAttributes()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getXLinkAttributes().hashCode();
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
		return ("link");
	}

	/**
	 * Accessor for the XLink Attributes. Will always be non-null, even if it has no values set.
	 */
	public XLinkAttributes getXLinkAttributes() {
		return (_xlinkAttributes);
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
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 4325950371570699184L;
		private XLinkAttributes.Builder _xlinkAttributes;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Link link) {
			setXLinkAttributes(new XLinkAttributes.Builder(link.getXLinkAttributes()));
			setSecurityAttributes(new SecurityAttributes.Builder(link.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Link commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			if (getSecurityAttributes().isEmpty())
				return new Link(getXLinkAttributes().commit());
			return (new Link(getXLinkAttributes().commit(), getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getXLinkAttributes().isEmpty() && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the XLink Attributes
		 */
		public XLinkAttributes.Builder getXLinkAttributes() {
			if (_xlinkAttributes == null)
				_xlinkAttributes = new XLinkAttributes.Builder();
			return _xlinkAttributes;
		}

		/**
		 * Builder accessor for the XLink Attributes
		 */
		public void setXLinkAttributes(XLinkAttributes.Builder xlinkAttributes) {
			_xlinkAttributes = xlinkAttributes;
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