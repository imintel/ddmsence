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
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.security.ntk.Group;
import buri.ddmsence.ddms.security.ntk.Individual;
import buri.ddmsence.ddms.security.ntk.Profile;
import buri.ddmsence.ddms.security.ntk.SystemName;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Base class for NTK elements which describe system access rules for an {@link Individual}, {@link Group}, or 
 * {@link Profile}.
 * <br /><br />
 * {@ddms.versions 00010}
 * 
 * <p> Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set
 * before the component is used. </p>
 * 
 * {@table.header History}
 * 		<p>This abstract class was introduced to support NTK Access components in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ntk:AccessSystemName|1|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule ntk:AccessSystemName must exist.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public abstract class AbstractAccessEntity extends AbstractBaseComponent {

	private SystemName _systemName = null;
	private SecurityAttributes _securityAttributes = null;

	/**
	 * Constructor for creating a component from a XOM Element. Does not validate.
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public AbstractAccessEntity(Element element) throws InvalidDDMSException {
		setXOMElement(element, false);
		Element systemElement = element.getFirstChildElement(SystemName.getName(getDDMSVersion()), getNamespace());
		if (systemElement != null)
			_systemName = new SystemName(systemElement);
		_securityAttributes = new SecurityAttributes(element);
	}

	/**
	 * Constructor for creating a component from raw data. Does not validate.
	 * 
	 * @param systemName the system name
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public AbstractAccessEntity(String name, SystemName systemName, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Element element = Util.buildElement(PropertyReader.getPrefix("ntk"), name, version.getNtkNamespace(), null);
		setXOMElement(element, false);
		if (systemName != null)
			element.appendChild(systemName.getXOMElementCopy());
		_systemName = systemName;
		_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
		_securityAttributes.addTo(element);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSValue("systemName", getSystemName());
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getSystemName());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof AbstractAccessEntity))
			return (false);
		return (true);
	}

	/**
	 * Accessor for the system name
	 */
	public SystemName getSystemName() {
		return _systemName;
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null even if the attributes are not set.
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
	public abstract static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7851044806424206976L;
		private SystemName.Builder _systemName;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(AbstractAccessEntity group) {
			if (group.getSystemName() != null)
				setSystemName(new SystemName.Builder(group.getSystemName()));
			setSecurityAttributes(new SecurityAttributes.Builder(group.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getSystemName().isEmpty() && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the systemName
		 */
		public SystemName.Builder getSystemName() {
			if (_systemName == null)
				_systemName = new SystemName.Builder();
			return _systemName;
		}

		/**
		 * Builder accessor for the systemName
		 */
		public void setSystemName(SystemName.Builder systemName) {
			_systemName = systemName;
		}

		/**
		 * Builder accessor for the securityAttributes
		 */
		public SecurityAttributes.Builder getSecurityAttributes() {
			if (_securityAttributes == null)
				_securityAttributes = new SecurityAttributes.Builder();
			return _securityAttributes;
		}

		/**
		 * Builder accessor for the securityAttributes
		 */
		public void setSecurityAttributes(SecurityAttributes.Builder securityAttributes) {
			_securityAttributes = securityAttributes;
		}
	}
}