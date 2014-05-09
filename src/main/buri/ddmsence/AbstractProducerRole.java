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
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.IRoleEntity;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.resource.Organization;
import buri.ddmsence.ddms.resource.Person;
import buri.ddmsence.ddms.resource.Service;
import buri.ddmsence.ddms.resource.Unknown;
import buri.ddmsence.ddms.security.ism.ISMVocabulary;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Base class for DDMS producer elements, such as ddms:creator and ddms:contributor.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p>
 * Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set before
 * the component is used.
 * </p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:organization|0..1|11111}
 * 		{@child.info ddms:person|0..1|11111}
 * 		{@child.info ddms:service|0..1|11111}
 * 		{@child.info ddms:unknown|0..1|01111}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:pocType|0..*|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Exactly 1 producer entity must fill this role.|Error|11111}
 * 		{@ddms.rule ism:pocType must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule If set, ism:pocType contains valid tokens.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public abstract class AbstractProducerRole extends AbstractBaseComponent {

	private IRoleEntity _entity = null;
	private List<String> _pocTypes = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String POC_TYPE_NAME = "pocType";

	/**
	 * Base constructor
	 * 
	 * @param element the XOM element representing this component
	 */
	protected AbstractProducerRole(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			if (element.getChildElements().size() > 0) {
				Element entityElement = element.getChildElements().get(0);
				String entityType = entityElement.getLocalName();
				if (Organization.getName(getDDMSVersion()).equals(entityType))
					_entity = new Organization(entityElement);
				if (Person.getName(getDDMSVersion()).equals(entityType))
					_entity = new Person(entityElement);
				if (Service.getName(getDDMSVersion()).equals(entityType))
					_entity = new Service(entityElement);
				if (Unknown.getName(getDDMSVersion()).equals(entityType))
					_entity = new Unknown(entityElement);
			}
			String pocTypes = element.getAttributeValue(POC_TYPE_NAME, getDDMSVersion().getIsmNamespace());
			_pocTypes = Util.getXsListAsList(pocTypes);
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param producerType the type of producer this producer entity is fulfilling (i.e. creator or contributor)
	 * @param entity the actual entity fulfilling this role
	 * @param pocTypes the pocType attribute
	 * @param securityAttributes any security attributes
	 */
	protected AbstractProducerRole(String producerType, IRoleEntity entity, List<String> pocTypes,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			if (pocTypes == null)
				pocTypes = Collections.emptyList();
			Util.requireDDMSValue("producer type", producerType);
			Util.requireDDMSValue("entity", entity);
			Element element = Util.buildDDMSElement(producerType, null);
			element.appendChild(entity.getXOMElementCopy());
			_entity = entity;
			if (!pocTypes.isEmpty())
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), POC_TYPE_NAME,
					DDMSVersion.getCurrentVersion().getIsmNamespace(), Util.getXsList(pocTypes));
			_pocTypes = pocTypes;
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
		Util.requireDDMSValue("entity", getEntity());
		Util.requireBoundedChildCount(getXOMElement(), Organization.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Person.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Service.getName(getDDMSVersion()), 0, 1);
		Util.requireBoundedChildCount(getXOMElement(), Unknown.getName(getDDMSVersion()), 0, 1);
		if (!getDDMSVersion().isAtLeast("4.0.1") && !getPocTypes().isEmpty()) {
			throw new InvalidDDMSException("This component must not have a pocType until DDMS 4.0.1 or later.");
		}
		for (String pocType : getPocTypes())
			ISMVocabulary.validateEnumeration(ISMVocabulary.CVE_POC_TYPE, pocType);
		super.validate();
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof AbstractProducerRole))
			return (false);
		AbstractProducerRole test = (AbstractProducerRole) obj;
		return (Util.listEquals(getPocTypes(), test.getPocTypes()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getPocTypes().hashCode();
		return (result);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(((AbstractBaseComponent) getEntity()).getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix + POC_TYPE_NAME, Util.getXsList(getPocTypes())));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getEntity());
		return (list);
	}

	/**
	 * Accessor for the entity fulfilling this producer role
	 */
	public IRoleEntity getEntity() {
		return _entity;
	}

	/**
	 * Accessor for the pocType attribute.
	 */
	public List<String> getPocTypes() {
		return (_pocTypes);
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
	 * appropriate
	 * concrete object type.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static abstract class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -1694935853087559491L;

		private String _entityType;
		private Organization.Builder _organization;
		private Person.Builder _person;
		private Service.Builder _service;
		private Unknown.Builder _unknown;
		private List<String> _pocTypes;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Base constructor
		 */
		protected Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		protected Builder(AbstractProducerRole producer) {
			setEntityType(producer.getEntity().getName());
			DDMSVersion version = producer.getDDMSVersion();
			if (Organization.getName(version).equals(getEntityType()))
				setOrganization(new Organization.Builder((Organization) producer.getEntity()));
			if (Person.getName(version).equals(getEntityType()))
				setPerson(new Person.Builder((Person) producer.getEntity()));
			if (Service.getName(version).equals(getEntityType()))
				setService(new Service.Builder((Service) producer.getEntity()));
			if (Unknown.getName(version).equals(getEntityType()))
				setUnknown(new Unknown.Builder((Unknown) producer.getEntity()));
			setPocTypes(producer.getPocTypes());
			setSecurityAttributes(new SecurityAttributes.Builder(producer.getSecurityAttributes()));
		}

		/**
		 * Commits the entity which is active in this builder, based on the entityType.
		 * 
		 * @return the entity
		 */
		protected IRoleEntity commitSelectedEntity() throws InvalidDDMSException {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			if (Organization.getName(version).equalsIgnoreCase(getEntityType())) {
				return (getOrganization().commit());
			}
			if (Person.getName(version).equalsIgnoreCase(getEntityType())) {
				return (getPerson().commit());
			}
			if (Service.getName(version).equalsIgnoreCase(getEntityType())) {
				return (getService().commit());
			}
			return (getUnknown().commit());
		}

		/**
		 * Helper method to determine if any values have been entered for this producer.
		 * 
		 * @return true if all values are empty
		 */
		public boolean isEmpty() {
			return (getOrganization().isEmpty()
				&& getPerson().isEmpty()
				&& getService().isEmpty()
				&& getUnknown().isEmpty()
				&& getPocTypes().isEmpty()
				&& getSecurityAttributes().isEmpty());
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
		 * Builder accessor for the entityType, which determines which of the 4 entity builders are used.
		 */
		public String getEntityType() {
			return _entityType;
		}

		/**
		 * Builder accessor for the entityType, which determines which of the 4 entity builders are used.
		 */
		public void setEntityType(String entityType) {
			_entityType = entityType;
		}

		/**
		 * Builder accessor for the organization builder
		 */
		public Organization.Builder getOrganization() {
			if (_organization == null) {
				_organization = new Organization.Builder();
			}
			return _organization;
		}

		/**
		 * Builder accessor for the organization builder
		 */
		public void setOrganization(Organization.Builder organization) {
			_organization = organization;
		}

		/**
		 * Builder accessor for the person builder
		 */
		public Person.Builder getPerson() {
			if (_person == null) {
				_person = new Person.Builder();
			}
			return _person;
		}

		/**
		 * Builder accessor for the person builder
		 */
		public void setPerson(Person.Builder person) {
			_person = person;
		}

		/**
		 * Builder accessor for the service builder
		 */
		public Service.Builder getService() {
			if (_service == null) {
				_service = new Service.Builder();
			}
			return _service;
		}

		/**
		 * Builder accessor for the service builder
		 */
		public void setService(Service.Builder service) {
			_service = service;
		}

		/**
		 * Builder accessor for the unknown builder
		 */
		public Unknown.Builder getUnknown() {
			if (_unknown == null) {
				_unknown = new Unknown.Builder();
			}
			return _unknown;
		}

		/**
		 * Builder accessor for the unknown builder
		 */
		public void setUnknown(Unknown.Builder unknown) {
			_unknown = unknown;
		}

		/**
		 * Builder accessor for the pocTypes
		 */
		public List<String> getPocTypes() {
			if (_pocTypes == null)
				_pocTypes = new LazyList(String.class);
			return _pocTypes;
		}

		/**
		 * Builder accessor for the pocTypes
		 */
		public void setPocTypes(List<String> pocTypes) {
			_pocTypes = pocTypes;
		}
	}
}