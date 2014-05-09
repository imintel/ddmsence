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
package buri.ddmsence.ddms.security.ntk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ntk:Access.
 * <br /><br />
 * {@ddms.versions 00010}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		<p>This class was introduced to support NTK components in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ntk:AccessIndividualList/ntk:AccessIndividual|0..*|00010}
 * 		{@child.info ntk:AccessGroupList/ntk:AccessGroup|0..*|00010}
 * 		{@child.info ntk:AccessProfileList|0..1|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ntk:externalReference|0..1|00010}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * 		{@ddms.rule ntk:externalReference may cause issues for DDMS 4.0.1 systems.|Warning|00010}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class Access extends AbstractBaseComponent {

	private List<Individual> _individuals = null;
	private List<Group> _groups = null;
	private ProfileList _profileList = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String INDIVIDUAL_LIST_NAME = "AccessIndividualList";
	private static final String GROUP_LIST_NAME = "AccessGroupList";
	private static final String EXTERNAL_REFERENCE_NAME = "externalReference";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Access(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			_individuals = new ArrayList<Individual>();
			Element individualList = element.getFirstChildElement(INDIVIDUAL_LIST_NAME, getNamespace());
			if (individualList != null) {
				Elements individuals = individualList.getChildElements();
				for (int i = 0; i < individuals.size(); i++) {
					_individuals.add(new Individual(individuals.get(i)));
				}
			}
			_groups = new ArrayList<Group>();
			Element groupList = element.getFirstChildElement(GROUP_LIST_NAME, getNamespace());
			if (groupList != null) {
				Elements groups = groupList.getChildElements();
				for (int i = 0; i < groups.size(); i++) {
					_groups.add(new Group(groups.get(i)));
				}
			}
			Element profileList = element.getFirstChildElement(ProfileList.getName(getDDMSVersion()), getNamespace());
			if (profileList != null)
				_profileList = new ProfileList(profileList);
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @deprecated A new constructor was added for DDMS 4.1 to support ntk:externalResource. This constructor is
	 *             preserved for backwards compatibility, but may disappear in the next major release.
	 * 
	 * @param individuals a list of individuals
	 * @param groups a list of groups
	 * @param profileList the profile list
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Access(List<Individual> individuals, List<Group> groups, ProfileList profileList,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		this(individuals, groups, profileList, null, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param individuals a list of individuals
	 * @param groups a list of groups
	 * @param profileList the profile list
	 * @param externalReference a boolean attribute
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Access(List<Individual> individuals, List<Group> groups, ProfileList profileList, Boolean externalReference,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			String ntkPrefix = PropertyReader.getPrefix("ntk");
			String ntkNamespace = version.getNtkNamespace();

			Element element = Util.buildElement(ntkPrefix, Access.getName(version), ntkNamespace, null);
			setXOMElement(element, false);

			if (individuals == null)
				individuals = Collections.emptyList();
			if (!individuals.isEmpty()) {
				Element individualList = Util.buildElement(ntkPrefix, INDIVIDUAL_LIST_NAME, ntkNamespace, null);
				element.appendChild(individualList);
				for (Individual individual : individuals) {
					individualList.appendChild(individual.getXOMElementCopy());
				}
			}
			if (groups == null)
				groups = Collections.emptyList();
			if (!groups.isEmpty()) {
				Element groupList = Util.buildElement(ntkPrefix, GROUP_LIST_NAME, ntkNamespace, null);
				element.appendChild(groupList);
				for (Group group : groups) {
					groupList.appendChild(group.getXOMElementCopy());
				}
			}
			if (profileList != null)
				element.appendChild(profileList.getXOMElementCopy());
			if (externalReference != null) {
				Util.addAttribute(element, ntkPrefix, EXTERNAL_REFERENCE_NAME, ntkNamespace,
					String.valueOf(externalReference));
			}

			_individuals = individuals;
			_groups = groups;
			_profileList = profileList;
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
			validate();
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
		Util.requireQName(getXOMElement(), getNamespace(), Access.getName(getDDMSVersion()));
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (getIndividuals().isEmpty() && getGroups().isEmpty() && getProfileList() == null)
			addWarning("An ntk:Access element was found with no individual, group, or profile information.");
		if (isExternalReference() != null)
			addDdms40Warning("ntk:externalReference attribute");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "access", suffix) + ".";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + "individualList.", getIndividuals()));
		text.append(buildOutput(isHTML, localPrefix + "groupList.", getGroups()));
		if (getProfileList() != null)
			text.append(getProfileList().getOutput(isHTML, localPrefix, ""));
		if (isExternalReference() != null)
			text.append(buildOutput(isHTML, localPrefix + EXTERNAL_REFERENCE_NAME,
				String.valueOf(isExternalReference())));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getIndividuals());
		list.addAll(getGroups());
		list.add(getProfileList());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Access))
			return (false);
		Access test = (Access) obj;
		return (Util.nullEquals(isExternalReference(), test.isExternalReference()));
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("Access");
	}

	/**
	 * Accessor for the individuals
	 */
	public List<Individual> getIndividuals() {
		return (Collections.unmodifiableList(_individuals));
	}

	/**
	 * Accessor for the groups
	 */
	public List<Group> getGroups() {
		return (Collections.unmodifiableList(_groups));
	}

	/**
	 * Accessor for the profileList
	 */
	public ProfileList getProfileList() {
		return _profileList;
	}

	/**
	 * Accessor for the externalReference attribute. This may be null for Access elements before DDMS 4.1.
	 */
	public Boolean isExternalReference() {
		String value = getAttributeValue(EXTERNAL_REFERENCE_NAME, getDDMSVersion().getNtkNamespace());
		if ("true".equals(value))
			return (Boolean.TRUE);
		if ("false".equals(value))
			return (Boolean.FALSE);
		return (null);
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
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7851044806424206976L;
		private List<Individual.Builder> _individuals;
		private List<Group.Builder> _groups;
		private ProfileList.Builder _profileList;
		private Boolean _externalReference;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Access access) {
			for (Individual individual : access.getIndividuals())
				getIndividuals().add(new Individual.Builder(individual));
			for (Group group : access.getGroups())
				getGroups().add(new Group.Builder(group));
			if (access.getProfileList() != null)
				setProfileList(new ProfileList.Builder(access.getProfileList()));
			setExternalReference(access.isExternalReference());
			setSecurityAttributes(new SecurityAttributes.Builder(access.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Access commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Individual> individuals = new ArrayList<Individual>();
			for (IBuilder builder : getIndividuals()) {
				Individual component = (Individual) builder.commit();
				if (component != null)
					individuals.add(component);
			}
			List<Group> groups = new ArrayList<Group>();
			for (IBuilder builder : getGroups()) {
				Group component = (Group) builder.commit();
				if (component != null)
					groups.add(component);
			}

			return (new Access(individuals, groups, getProfileList().commit(), getExternalReference(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getIndividuals())
				hasValueInList = hasValueInList || !builder.isEmpty();
			for (IBuilder builder : getGroups())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList && getProfileList().isEmpty() && getExternalReference() == null && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the individuals
		 */
		public List<Individual.Builder> getIndividuals() {
			if (_individuals == null)
				_individuals = new LazyList(Individual.Builder.class);
			return _individuals;
		}

		/**
		 * Builder accessor for the groups
		 */
		public List<Group.Builder> getGroups() {
			if (_groups == null)
				_groups = new LazyList(Group.Builder.class);
			return _groups;
		}

		/**
		 * Builder accessor for the profileList
		 */
		public ProfileList.Builder getProfileList() {
			if (_profileList == null)
				_profileList = new ProfileList.Builder();
			return _profileList;
		}

		/**
		 * Builder accessor for the profileList
		 */
		public void setProfileList(ProfileList.Builder profileList) {
			_profileList = profileList;
		}

		/**
		 * Accessor for the externalReference attribute
		 */
		public Boolean getExternalReference() {
			return _externalReference;
		}

		/**
		 * Accessor for the externalReference attribute
		 */
		public void setExternalReference(Boolean externalReference) {
			_externalReference = externalReference;
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