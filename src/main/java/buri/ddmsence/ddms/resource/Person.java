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

import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractRoleEntity;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.extensible.ExtensibleAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:person.
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
 * 		{@child.info ddms:affiliation|0..1|11110}
 * 		{@child.info ddms:affiliation|0..*|00001}
 * 		{@child.info ddms:surname|1|11111}
 * 		{@child.info ddms:userId|0..1|11111}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info any:&lt;<i>extensibleAttributes</i>&gt;|0..*|11100}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least 1 ddms:name must exist.|Error|11111}
 * 		{@ddms.rule A ddms:surname must exist.|Error|11111}
 * 		{@ddms.rule ddms:affiliation must not have multiple values before DDMS 5.0.|Error|11111}
 *		{@ddms.rule Extensible attributes must not be used after the DDMS version in which they were removed.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Person extends AbstractRoleEntity {

	private List<String> _affiliations = null;

	private static final String AFFILIATION_NAME = "affiliation";
	private static final String USERID_NAME = "userID";
	private static final String SURNAME_NAME = "surname";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Person(Element element) throws InvalidDDMSException {
		super(element, true);
		_affiliations = Util.getDDMSChildValues(element, AFFILIATION_NAME);
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @param names an ordered list of names
	 * @param surname the surname of the person
	 * @param phones an ordered list of phone numbers
	 * @param emails an ordered list of email addresses
	 * @param userID optional unique identifier within an organization
	 * @param affiliations organizational affiliation of the person
	 */
	public Person(List<String> names, String surname, List<String> phones, List<String> emails, String userID,
		List<String> affiliations) throws InvalidDDMSException {
		this(names, surname, phones, emails, userID, affiliations, null);
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @param names an ordered list of names
	 * @param surname the surname of the person
	 * @param phones an ordered list of phone numbers
	 * @param emails an ordered list of email addresses
	 * @param userID optional unique identifier within an organization
	 * @param affiliations organizational affiliations of the person
	 * @param extensions extensible attributes
	 */
	public Person(List<String> names, String surname, List<String> phones, List<String> emails, String userID,
		List<String> affiliations, ExtensibleAttributes extensions) throws InvalidDDMSException {
		super(Person.getName(DDMSVersion.getCurrentVersion()), names, phones, emails, extensions);
		try {
			int insertIndex = (names == null ? 0 : names.size());
			addExtraElements(insertIndex, surname, userID, affiliations);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Inserts additional elements into the existing entity. Because the personType contains a sequence,
	 * additional fields must be inserted among the name, phone, and email elements.
	 * 
	 * @param insertIndex the index of the position after the last names element
	 * @param surname the surname of the person
	 * @param userID optional unique identifier within an organization
	 * @param affiliations organizational affiliations of the person
	 * @throws InvalidDDMSException if the result is an invalid component
	 */
	private void addExtraElements(int insertIndex, String surname, String userID, List<String> affiliations)
		throws InvalidDDMSException {
		if (affiliations == null)
			affiliations = Collections.emptyList();
		Element element = getXOMElement();
		if (getDDMSVersion().isAtLeast("4.0.1")) {
			element.insertChild(Util.buildDDMSElement(SURNAME_NAME, surname), insertIndex);
			if (!Util.isEmpty(userID))
				element.appendChild(Util.buildDDMSElement(USERID_NAME, userID));
			for (String affiliation : affiliations)
				element.appendChild(Util.buildDDMSElement(AFFILIATION_NAME, affiliation));
		}
		else {
			// Inserting in reverse order allow the same index to be reused. Later inserts will "push" the early ones
			// forward.
			for (String affiliation : affiliations)
				element.insertChild(Util.buildDDMSElement(AFFILIATION_NAME, affiliation), insertIndex);
			if (!Util.isEmpty(userID))
				element.insertChild(Util.buildDDMSElement(USERID_NAME, userID), insertIndex);
			element.insertChild(Util.buildDDMSElement(SURNAME_NAME, surname), insertIndex);
		}
		_affiliations = affiliations;
	}

	/**
	 * @see AbstractRoleEntity#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), Person.getName(getDDMSVersion()));
		Util.requireDDMSValue(SURNAME_NAME, getSurname());
		if (!getDDMSVersion().isAtLeast("5.0"))
			Util.requireBoundedChildCount(getXOMElement(), AFFILIATION_NAME, 0, 1);
		if (getDDMSVersion().isAtLeast("4.0.1") && !getExtensibleAttributes().isEmpty())
			throw new InvalidDDMSException("ddms:" + getName() + " must not have extensible attributes after DDMS 3.1.");
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "", suffix);
		StringBuffer text = new StringBuffer(super.getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix + SURNAME_NAME, getSurname()));
		text.append(buildOutput(isHTML, localPrefix + USERID_NAME, getUserID()));
		text.append(buildOutput(isHTML, localPrefix + AFFILIATION_NAME, getAffiliations()));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Person))
			return (false);
		Person test = (Person) obj;
		return (getSurname().equals(test.getSurname()) 
			&& getUserID().equals(test.getUserID()) 
			&& Util.listEquals(getAffiliations(), test.getAffiliations()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getSurname().hashCode();
		result = 7 * result + getUserID().hashCode();
		result = 7 * result + getAffiliations().hashCode();
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
		return (version.isAtLeast("4.0.1") ? "person" : "Person");
	}

	/**
	 * Accessor for the surname of the person
	 */
	public String getSurname() {
		return (Util.getFirstDDMSChildValue(getXOMElement(), SURNAME_NAME));
	}

	/**
	 * Accessor for the userID of the person
	 */
	public String getUserID() {
		return (Util.getFirstDDMSChildValue(getXOMElement(), USERID_NAME));
	}

	/**
	 * Accessor for the affiliations of the person
	 */
	public List<String> getAffiliations() {
		return (Collections.unmodifiableList(_affiliations));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder extends AbstractRoleEntity.Builder {
		private static final long serialVersionUID = -2933889158864177338L;
		private String _surname;
		private String _userID;
		private List<String> _affiliations;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Person person) {
			super(person);
			setSurname(person.getSurname());
			setUserID(person.getUserID());
			setAffiliations(person.getAffiliations());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Person commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Person(getNames(), getSurname(), getPhones(), getEmails(), getUserID(),
				getAffiliations(), getExtensibleAttributes().commit()));
		}

		/**
		 * Helper method to determine if any values have been entered for this Person.
		 * 
		 * @return true if all values are empty
		 */
		public boolean isEmpty() {
			return (super.isEmpty()
				&& Util.isEmpty(getSurname())
				&& Util.isEmpty(getUserID())
				&& Util.containsOnlyEmptyValues(getAffiliations()));
		}

		/**
		 * Builder accessor for the surname
		 */
		public String getSurname() {
			return _surname;
		}

		/**
		 * Builder accessor for the surname
		 */
		public void setSurname(String surname) {
			_surname = surname;
		}

		/**
		 * Builder accessor for the userID
		 */
		public String getUserID() {
			return _userID;
		}

		/**
		 * Builder accessor for the userID
		 */
		public void setUserID(String userID) {
			_userID = userID;
		}

		/**
		 * Builder accessor for the affiliations
		 */
		public List<String> getAffiliations() {
			if (_affiliations == null)
				_affiliations = new LazyList(String.class);
			return _affiliations;
		}

		/**
		 * Builder accessor for the affiliations
		 */
		public void setAffiliations(List<String> affiliations) {
			_affiliations = new LazyList(affiliations, String.class);
		}
	}
}