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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractRoleEntity;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:organization.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		The name of this element was made lowercase in DDMS 4.0.1.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:name|1..*|11111}
 * 		{@child.info ddms:phone|0..*|11111}
 * 		{@child.info ddms:email|0..*|11111}
 * 		{@child.info ddms:subOrganization|0..*|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:acronym|0..1|00011}
 * 		{@child.info any:&lt;<i>extensibleAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least 1 ddms:name must exist.|Error|11111}
 * 		{@ddms.rule ddms:acronym must not be used before the DDMS version in which it was introduced.|Error|11111}
 *		{@ddms.rule Extensible attributes must not be used after the DDMS version in which they were removed.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Organization extends AbstractRoleEntity {

	private List<SubOrganization> _subOrganizations = null;

	private static final String ACRONYM_NAME = "acronym";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Organization(Element element) throws InvalidDDMSException {
		super(element, false);
		try {
			String namespace = element.getNamespaceURI();
			Elements components = element.getChildElements(SubOrganization.getName(getDDMSVersion()), namespace);
			_subOrganizations = new ArrayList<SubOrganization>();
			for (int i = 0; i < components.size(); i++)
				_subOrganizations.add(new SubOrganization(components.get(i)));
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @param names an ordered list of names
	 * @param phones an ordered list of phone numbers
	 * @param emails an ordered list of email addresses
	 * @param subOrganizations an ordered list of suborganizations
	 * @param acronym the organization's acronym
	 */
	public Organization(List<String> names, List<String> phones, List<String> emails,
		List<SubOrganization> subOrganizations, String acronym) throws InvalidDDMSException {
		this(names, phones, emails, subOrganizations, acronym, null);
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @param names an ordered list of names
	 * @param phones an ordered list of phone numbers
	 * @param emails an ordered list of email addresses
	 * @param subOrganizations an ordered list of suborganizations
	 * @param acronym the organization's acronym
	 * @param extensions extensible attributes
	 */
	public Organization(List<String> names, List<String> phones, List<String> emails,
		List<SubOrganization> subOrganizations, String acronym, ExtensibleAttributes extensions)
		throws InvalidDDMSException {
		super(Organization.getName(DDMSVersion.getCurrentVersion()), names, phones, emails, extensions);
		try {
			if (subOrganizations == null)
				subOrganizations = Collections.emptyList();
			Util.addDDMSAttribute(getXOMElement(), ACRONYM_NAME, acronym);
			for (SubOrganization subOrganization : subOrganizations)
				getXOMElement().appendChild(subOrganization.getXOMElementCopy());
			_subOrganizations = subOrganizations;
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * @see AbstractRoleEntity#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), Organization.getName(getDDMSVersion()));
		if (!getDDMSVersion().isAtLeast("4.0.1") && !Util.isEmpty(getAcronym()))
			throw new InvalidDDMSException("An organization must not have an acronym until DDMS 4.0.1 or later.");
		if (getDDMSVersion().isAtLeast("5.0") && !getExtensibleAttributes().isEmpty())
			throw new InvalidDDMSException("ddms:" + getName() + " must not have extensible attributes after DDMS 4.1.");
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "", suffix);
		StringBuffer text = new StringBuffer(super.getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix, getSubOrganizations()));
		text.append(buildOutput(isHTML, localPrefix + ACRONYM_NAME, getAcronym()));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getSubOrganizations());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Organization))
			return (false);
		Organization test = (Organization) obj;
		return (getAcronym().equals(test.getAcronym()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getAcronym().hashCode();
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
		return (version.isAtLeast("4.0.1") ? "organization" : "Organization");
	}

	/**
	 * Accessor for the suborganizations
	 */
	public List<SubOrganization> getSubOrganizations() {
		return (Collections.unmodifiableList(_subOrganizations));
	}

	/**
	 * Accessor for the acronym
	 */
	public String getAcronym() {
		return (getAttributeValue(ACRONYM_NAME));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder extends AbstractRoleEntity.Builder {
		private static final long serialVersionUID = 4565840434345629470L;
		private List<SubOrganization.Builder> _subOrganizations;
		private String _acronym;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Organization organization) {
			super(organization);
			for (SubOrganization subOrg : organization.getSubOrganizations())
				getSubOrganizations().add(new SubOrganization.Builder(subOrg));
			setAcronym(organization.getAcronym());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Organization commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<SubOrganization> subOrgs = new ArrayList<SubOrganization>();
			for (IBuilder builder : getSubOrganizations()) {
				SubOrganization component = (SubOrganization) builder.commit();
				if (component != null)
					subOrgs.add(component);
			}
			return (new Organization(getNames(), getPhones(), getEmails(), subOrgs, getAcronym(),
				getExtensibleAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getSubOrganizations())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (super.isEmpty()
				&& !hasValueInList
				&& Util.isEmpty(getAcronym()));
		}

		/**
		 * Builder accessor for suborganizations
		 */
		public List<SubOrganization.Builder> getSubOrganizations() {
			if (_subOrganizations == null)
				_subOrganizations = new LazyList(SubOrganization.Builder.class);
			return _subOrganizations;
		}

		/**
		 * Builder accessor for the acronym
		 */
		public String getAcronym() {
			return _acronym;
		}

		/**
		 * Builder accessor for the acronym
		 */
		public void setAcronym(String acronym) {
			_acronym = acronym;
		}
	}
}