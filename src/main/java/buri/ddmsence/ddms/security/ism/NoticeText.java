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
package buri.ddmsence.ddms.security.ism;

import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractSimpleString;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ism:NoticeText.
 *  * <br /><br />
 * {@ddms.versions 00010}
 *  
 *  <p></p>
 *  
 *  {@table.header History}
 * 		<p>This class was introduced to support ISM notices in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:pocType|0..*|00010}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If set, each ism:pocType must be a valid token.|Error|11111}
 * 		{@ddms.rule Warnings from any notice attributes are claimed by this component.|Warning|11111}
 * 		{@ddms.rule This component can be used with no value set.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class NoticeText extends AbstractSimpleString {

	private List<String> _pocTypes = null;

	private static final String POC_TYPE_NAME = "pocType";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NoticeText(Element element) throws InvalidDDMSException {
		super(element, false);
		try {
			String pocTypes = element.getAttributeValue(POC_TYPE_NAME, getDDMSVersion().getIsmNamespace());
			_pocTypes = Util.getXsListAsList(pocTypes);
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
	 * @param value the value of the description child text
	 * @param pocTypes the value of the pocType attribute
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NoticeText(String value, List<String> pocTypes, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(PropertyReader.getPrefix("ism"), DDMSVersion.getCurrentVersion().getIsmNamespace(),
			NoticeText.getName(DDMSVersion.getCurrentVersion()), value, securityAttributes, false);
		try {
			if (pocTypes == null)
				pocTypes = Collections.emptyList();
			if (!pocTypes.isEmpty())
				Util.addAttribute(getXOMElement(), PropertyReader.getPrefix("ism"), POC_TYPE_NAME,
					DDMSVersion.getCurrentVersion().getIsmNamespace(), Util.getXsList(pocTypes));
			_pocTypes = pocTypes;
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
		Util.requireQName(getXOMElement(), getDDMSVersion().getIsmNamespace(), NoticeText.getName(getDDMSVersion()));
		if (getDDMSVersion().isAtLeast("4.0.1")) {
			for (String pocType : getPocTypes())
				ISMVocabulary.validateEnumeration(ISMVocabulary.CVE_POC_TYPE, pocType);
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (Util.isEmpty(getValue()))
			addWarning("An ism:" + getName() + " element was found with no value.");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, "noticeText", suffix);
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getValue()));
		text.append(buildOutput(isHTML, localPrefix + "." + POC_TYPE_NAME, Util.getXsList(getPocTypes())));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix + "."));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof NoticeText))
			return (false);
		NoticeText test = (NoticeText) obj;
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
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("NoticeText");
	}

	/**
	 * Accessor for the pocType attribute.
	 */
	public List<String> getPocTypes() {
		return (_pocTypes);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractSimpleString.Builder {
		private static final long serialVersionUID = 7750664735441105296L;
		private List<String> _pocTypes;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(NoticeText text) {
			super(text);
			setPocTypes(text.getPocTypes());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public NoticeText commit() throws InvalidDDMSException {
			return (isEmpty() && getPocTypes().isEmpty() ? null : new NoticeText(getValue(), getPocTypes(),
				getSecurityAttributes().commit()));
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