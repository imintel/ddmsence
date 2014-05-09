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
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:virtualCoverage.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * {@table.header History}
 * 		<p>The network and protocol attributes moved into the virt namespace in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 *		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:address|0..1|11110}
 * 		{@child.info virt:address|0..1|00001}
 * 		{@child.info ddms:protocol|0..1|11110}
 * 		{@child.info virt:protocol|0..1|00001}
 * 		{@child.info virt:network|0..1|00001}
 * 		{@child.info ntk:access|0..1|00001}	
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If the address attribute is present, the protocol attribute must exist.|Error|11111}
 * 		{@ddms.rule virt:network must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule ntk:access must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * {@table.footer}
 * 
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class VirtualCoverage extends AbstractBaseComponent {

	private SecurityAttributes _securityAttributes = null;

	private static final String ADDRESS_NAME = "address";
	private static final String PROTOCOL_NAME = "protocol";
	private static final String ACCESS_NAME = "access";
	private static final String NETWORK_NAME = "network";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public VirtualCoverage(Element element) throws InvalidDDMSException {
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
	 * @deprecated A new constructor was added for DDMS 5.0 to support ntk:access and virt:network. This constructor is
	 *             preserved for backwards compatibility, but may disappear in the next major release.
	 * 
	 * @param address the virtual address
	 * @param protocol the network protocol
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public VirtualCoverage(String address, String protocol, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		this(address, protocol, null, null, securityAttributes);
	}
	
	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param address the virtual address
	 * @param protocol the network protocol
	 * @param access an NTK portion access pattern
	 * @param network a VIRT network name
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public VirtualCoverage(String address, String protocol, String access, String network, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildDDMSElement(VirtualCoverage.getName(version), null);
			if (version.isAtLeast("5.0")) {
				String ntkPrefix = PropertyReader.getPrefix("ntk");
				String ntkNamespace = version.getNtkNamespace();
				String virtPrefix = PropertyReader.getPrefix("virt");
				String virtNamespace = version.getVirtNamespace();
				Util.addAttribute(element, virtPrefix, ADDRESS_NAME, virtNamespace, address);
				Util.addAttribute(element, virtPrefix, PROTOCOL_NAME, virtNamespace, protocol);
				Util.addAttribute(element, ntkPrefix, ACCESS_NAME, ntkNamespace, access);
				Util.addAttribute(element, virtPrefix, NETWORK_NAME, virtNamespace, network);
			}
			else {
				Util.addDDMSAttribute(element, ADDRESS_NAME, address);
				Util.addDDMSAttribute(element, PROTOCOL_NAME, protocol);
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
		Util.requireDDMSQName(getXOMElement(), VirtualCoverage.getName(getDDMSVersion()));
		if (!Util.isEmpty(getAddress()))
			Util.requireDDMSValue(PROTOCOL_NAME, getProtocol());
		if (!getDDMSVersion().isAtLeast("5.0")) {
			// Checks for ntk:access and virt:network are implicit in schema validation and data constructor.
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
		if (Util.isEmpty(getAddress()) && Util.isEmpty(getProtocol()) && Util.isEmpty(getAccess())
			&& Util.isEmpty(getNetwork()))
			addWarning("A completely empty ddms:virtualCoverage element was found.");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + ADDRESS_NAME, getAddress()));
		text.append(buildOutput(isHTML, localPrefix + PROTOCOL_NAME, getProtocol()));
		text.append(buildOutput(isHTML, localPrefix + ACCESS_NAME, getAccess()));
		text.append(buildOutput(isHTML, localPrefix + NETWORK_NAME, getNetwork()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof VirtualCoverage))
			return (false);
		VirtualCoverage test = (VirtualCoverage) obj;
		return (getAddress().equals(test.getAddress()) 
			&& getProtocol().equals(test.getProtocol())
			&& getAccess().equals(test.getAccess())
			&& getNetwork().equals(test.getNetwork()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getAddress().hashCode();
		result = 7 * result + getProtocol().hashCode();
		result = 7 * result + getAccess().hashCode();
		result = 7 * result + getNetwork().hashCode();
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
		return ("virtualCoverage");
	}

	/**
	 * Accessor for the address attribute
	 */
	public String getAddress() {
		if (getDDMSVersion().isAtLeast("5.0"))
			return (getAttributeValue(ADDRESS_NAME, getDDMSVersion().getVirtNamespace()));
		return (getAttributeValue(ADDRESS_NAME));
	}

	/**
	 * Accessor for the protocol attribute (optional, should be used if address is supplied)
	 */
	public String getProtocol() {
		if (getDDMSVersion().isAtLeast("5.0"))
			return (getAttributeValue(PROTOCOL_NAME, getDDMSVersion().getVirtNamespace()));
		return (getAttributeValue(PROTOCOL_NAME));
	}

	/**
	 * Accessor for the access attribute
	 */
	public String getAccess() {
		return (getAttributeValue(ACCESS_NAME, getDDMSVersion().getNtkNamespace()));
	}
	
	/**
	 * Accessor for the network attribute
	 */
	public String getNetwork() {
		return (getAttributeValue(NETWORK_NAME, getDDMSVersion().getVirtNamespace()));
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
		private static final long serialVersionUID = 2986952678400201045L;
		private String _access;
		private String _address;
		private String _protocol;
		private String _network;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(VirtualCoverage coverage) {
			setAddress(coverage.getAddress());
			setProtocol(coverage.getProtocol());
			setAccess(coverage.getAccess());
			setNetwork(coverage.getNetwork());
			setSecurityAttributes(new SecurityAttributes.Builder(coverage.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public VirtualCoverage commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new VirtualCoverage(getAddress(), getProtocol(), getAccess(), getNetwork(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getAddress()) && Util.isEmpty(getProtocol()) && Util.isEmpty(getAccess())
				&& Util.isEmpty(getNetwork()) && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the address attribute
		 */
		public String getAddress() {
			return _address;
		}

		/**
		 * Builder accessor for the address attribute
		 */
		public void setAddress(String address) {
			_address = address;
		}

		/**
		 * Builder accessor for the protocol attribute
		 */
		public String getProtocol() {
			return _protocol;
		}

		/**
		 * Builder accessor for the protocol attribute
		 */
		public void setProtocol(String protocol) {
			_protocol = protocol;
		}
		
		/**
		 * Builder accessor for the access attribute
		 */
		public String getAccess() {
			return _access;
		}

		/**
		 * Builder accessor for the access attribute
		 */
		public void setAccess(String access) {
			_access = access;
		}

		/**
		 * Builder accessor for the network attribute
		 */
		public String getNetwork() {
			return _network;
		}

		/**
		 * Builder accessor for the network attribute
		 */
		public void setNetwork(String network) {
			_network = network;
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