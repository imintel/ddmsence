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
 * An immutable implementation of ntk:AccessProfileList.
 * <br /><br />
 * {@ddms.versions 00010}
 * 
 * <p>Unlike ntk:AccessIndividualList and ntk:AccessGroupList, this element is implemented in DDMSence because it has
 * security attributes.</p>
 * 
 * {@table.header History}
 * 		<p>This class was introduced to support NTK components in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ntk:AccessProfile|1..*|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least one profile must exist.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class ProfileList extends AbstractBaseComponent {

	private List<Profile> _profiles = null;
	private SecurityAttributes _securityAttributes = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProfileList(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Elements values = element.getChildElements(Profile.getName(getDDMSVersion()), getNamespace());
			_profiles = new ArrayList<Profile>();
			for (int i = 0; i < values.size(); i++) {
				_profiles.add(new Profile(values.get(i)));
			}
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
	 * @param profiles the list of profiles
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProfileList(List<Profile> profiles, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			Element element = Util.buildElement(PropertyReader.getPrefix("ntk"), ProfileList.getName(version),
				version.getNtkNamespace(), null);
			setXOMElement(element, false);
			if (profiles == null)
				profiles = Collections.emptyList();
			for (Profile profile : profiles) {
				getXOMElement().appendChild(profile.getXOMElementCopy());
			}
			_profiles = profiles;
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
		Util.requireQName(getXOMElement(), getNamespace(), ProfileList.getName(getDDMSVersion()));
		if (getProfiles().isEmpty())
			throw new InvalidDDMSException("At least one profile must exist.");
		getSecurityAttributes().requireClassification();

		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "profileList", suffix) + ".";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getProfiles()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getProfiles());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ProfileList))
			return (false);
		return (true);
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("AccessProfileList");
	}

	/**
	 * Accessor for the list of profile values (1-many)
	 */
	public List<Profile> getProfiles() {
		return (Collections.unmodifiableList(_profiles));
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
		private List<Profile.Builder> _profiles;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ProfileList profileList) {
			for (Profile value : profileList.getProfiles())
				getProfiles().add(new Profile.Builder(value));
			setSecurityAttributes(new SecurityAttributes.Builder(profileList.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ProfileList commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Profile> values = new ArrayList<Profile>();
			for (IBuilder builder : getProfiles()) {
				Profile component = (Profile) builder.commit();
				if (component != null)
					values.add(component);
			}
			return (new ProfileList(values, getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getProfiles())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the values
		 */
		public List<Profile.Builder> getProfiles() {
			if (_profiles == null)
				_profiles = new LazyList(Profile.Builder.class);
			return _profiles;
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