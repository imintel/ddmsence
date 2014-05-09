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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractNtkString;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ntk:AccessProfileValue.
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
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ntk:vocabulary|1|00010}
 * 		{@child.info ntk:id|0..1|00010}
 * 		{@child.info ntk:IDReference|0..1|00010}
 * 		{@child.info ntk:qualifier|0..1|00010}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ntk:vocabulary must exist, and must be a valid NMTOKEN.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class ProfileValue extends AbstractNtkString {

	private static final String VOCABULARY_NAME = "vocabulary";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ProfileValue(Element element) throws InvalidDDMSException {
		super(false, element);
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param value the value of the element's child text
	 * @param vocabulary the lexicon
	 * @param id the NTK ID
	 * @param idReference a reference to an NTK ID
	 * @param qualifier an NTK qualifier
	 * @param securityAttributes the security attributes
	 */
	public ProfileValue(String value, String vocabulary, String id, String idReference, String qualifier,
		SecurityAttributes securityAttributes) throws InvalidDDMSException {
		super(false, ProfileValue.getName(DDMSVersion.getCurrentVersion()), value, id, idReference, qualifier,
			securityAttributes, false);
		try {
			Util.addAttribute(getXOMElement(), PropertyReader.getPrefix("ntk"), VOCABULARY_NAME,
				DDMSVersion.getCurrentVersion().getNtkNamespace(), vocabulary);
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
		Util.requireQName(getXOMElement(), getNamespace(), ProfileValue.getName(getDDMSVersion()));
		Util.requireValidNMToken(getVocabulary());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (Util.isEmpty(getValue()))
			addWarning("A ntk:" + getName() + " element was found with no value.");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "profileValue", suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getValue()));
		text.append(buildOutput(isHTML, localPrefix + ".vocabulary", getVocabulary()));
		text.append(buildOutput(isHTML, localPrefix + ".id", getID()));
		text.append(buildOutput(isHTML, localPrefix + ".idReference", getIDReference()));
		text.append(buildOutput(isHTML, localPrefix + ".qualifier", getQualifier()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * Builder for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("AccessProfileValue");
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ProfileValue))
			return (false);
		ProfileValue test = (ProfileValue) obj;
		return (getVocabulary().equals(test.getVocabulary()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getVocabulary().hashCode();
		return (result);
	}

	/**
	 * Builder for the vocabulary
	 */
	public String getVocabulary() {
		return (getAttributeValue(VOCABULARY_NAME, getDDMSVersion().getNtkNamespace()));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractNtkString.Builder {
		private static final long serialVersionUID = 7750664735441105296L;
		private String _vocabulary;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ProfileValue value) {
			super(value);
			setVocabulary(value.getVocabulary());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ProfileValue commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new ProfileValue(getValue(), getVocabulary(), getID(), getIDReference(),
				getQualifier(), getSecurityAttributes().commit()));
		}

		/**
		 * Builder accessor for the vocabulary
		 */
		public String getVocabulary() {
			return _vocabulary;
		}

		/**
		 * Builder accessor for the vocabulary
		 */
		public void setVocabulary(String vocabulary) {
			_vocabulary = vocabulary;
		}
	}
}