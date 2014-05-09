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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractAccessEntity;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ntk:AccessProfile.
 * {@ddms.versions 00010}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		<p>This class was introduced to support NTK components in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ntk:AccessSystemName|1|00010}
 * 		{@child.info ntk:AccessProfileValue|1..*|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ntk:AccessSystemName must exist.|Error|11111}
 * 		{@ddms.rule At least 1 ntk:AccessProfileValue must exist.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class Profile extends AbstractAccessEntity {

	private List<ProfileValue> _profileValues = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Profile(Element element) throws InvalidDDMSException {
		super(element);
		try {
			Elements values = element.getChildElements(ProfileValue.getName(getDDMSVersion()), getNamespace());
			_profileValues = new ArrayList<ProfileValue>();
			for (int i = 0; i < values.size(); i++) {
				_profileValues.add(new ProfileValue(values.get(i)));
			}
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
	 * @param systemName the system name
	 * @param profileValues the list of values
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Profile(SystemName systemName, List<ProfileValue> profileValues, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(Profile.getName(DDMSVersion.getCurrentVersion()), systemName, securityAttributes);
		try {
			if (profileValues == null)
				profileValues = Collections.emptyList();
			for (ProfileValue value : profileValues) {
				getXOMElement().appendChild(value.getXOMElementCopy());
			}
			_profileValues = profileValues;
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
		Util.requireQName(getXOMElement(), getNamespace(), Profile.getName(getDDMSVersion()));
		if (getProfileValues().isEmpty())
			throw new InvalidDDMSException("At least one profile value must exist.");
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "profile", suffix) + ".";
		StringBuffer text = new StringBuffer();
		if (getSystemName() != null)
			text.append(getSystemName().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix, getProfileValues()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = super.getNestedComponents();
		list.addAll(getProfileValues());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Profile))
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
		return ("AccessProfile");
	}

	/**
	 * Accessor for the list of profile values (1-many)
	 */
	public List<ProfileValue> getProfileValues() {
		return (Collections.unmodifiableList(_profileValues));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractAccessEntity.Builder {
		private static final long serialVersionUID = 7851044806424206976L;
		private List<ProfileValue.Builder> _profileValues;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Profile profile) {
			super(profile);
			for (ProfileValue value : profile.getProfileValues())
				getProfileValues().add(new ProfileValue.Builder(value));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Profile commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<ProfileValue> values = new ArrayList<ProfileValue>();
			for (IBuilder builder : getProfileValues()) {
				ProfileValue component = (ProfileValue) builder.commit();
				if (component != null)
					values.add(component);
			}
			return (new Profile(getSystemName().commit(), values, getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getProfileValues())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList && super.isEmpty());
		}

		/**
		 * Builder accessor for the values
		 */
		public List<ProfileValue.Builder> getProfileValues() {
			if (_profileValues == null)
				_profileValues = new LazyList(ProfileValue.Builder.class);
			return _profileValues;
		}
	}
}