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

import java.io.Serializable;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:rights.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:privacyAct|0..1|11111}
 * 		{@child.info ddms:intellectualProperty|0..1|11111}
 * 		{@child.info ddms:copyright|0..1|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Rights extends AbstractBaseComponent {

	private static final String PRIVACY_ACT_NAME = "privacyAct";
	private static final String INTELLECTUAL_PROPERY_NAME = "intellectualProperty";
	private static final String COPYRIGHT_NAME = "copyright";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Rights(Element element) throws InvalidDDMSException {
		super(element);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param privacyAct the value for the privacyAct attribute
	 * @param intellectualProperty the value for the intellectualProperty attribute
	 * @param copyright the value for the copyright attribute
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Rights(boolean privacyAct, boolean intellectualProperty, boolean copyright) throws InvalidDDMSException {
		Element element = Util.buildDDMSElement(Rights.getName(DDMSVersion.getCurrentVersion()), null);
		Util.addDDMSAttribute(element, PRIVACY_ACT_NAME, Boolean.toString(privacyAct));
		Util.addDDMSAttribute(element, INTELLECTUAL_PROPERY_NAME, Boolean.toString(intellectualProperty));
		Util.addDDMSAttribute(element, COPYRIGHT_NAME, Boolean.toString(copyright));
		setXOMElement(element, true);
		// This cannot actually throw an exception, so locator information is not inserted in a catch statement.
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), Rights.getName(getDDMSVersion()));
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + PRIVACY_ACT_NAME, String.valueOf(getPrivacyAct())));
		text.append(buildOutput(isHTML, localPrefix + INTELLECTUAL_PROPERY_NAME,
			String.valueOf(getIntellectualProperty())));
		text.append(buildOutput(isHTML, localPrefix + COPYRIGHT_NAME, String.valueOf(getCopyright())));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Rights))
			return (false);
		Rights test = (Rights) obj;
		return (getPrivacyAct() == test.getPrivacyAct() 
			&& getIntellectualProperty() == test.getIntellectualProperty() 
			&& getCopyright() == test.getCopyright());
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + Util.booleanHashCode(getPrivacyAct());
		result = 7 * result + Util.booleanHashCode(getIntellectualProperty());
		result = 7 * result + Util.booleanHashCode(getCopyright());
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
		return ("rights");
	}

	/**
	 * Accessor for the privacyAct attribute. The default value is false.
	 */
	public boolean getPrivacyAct() {
		return (Boolean.valueOf(getAttributeValue(PRIVACY_ACT_NAME)));
	}

	/**
	 * Accessor for the intellectualProperty attribute. The default value is false.
	 */
	public boolean getIntellectualProperty() {
		return (Boolean.valueOf(getAttributeValue(INTELLECTUAL_PROPERY_NAME)));
	}

	/**
	 * Accessor for the copyright attribute. The default value is false.
	 */
	public boolean getCopyright() {
		return (Boolean.valueOf(getAttributeValue(COPYRIGHT_NAME)));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -2290965863004046496L;
		private Boolean _privacyAct = null;
		private Boolean _intellectualProperty = null;
		private Boolean _copyright = null;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Rights rights) {
			setPrivacyAct(Boolean.valueOf(rights.getPrivacyAct()));
			setIntellectualProperty(Boolean.valueOf(rights.getIntellectualProperty()));
			setCopyright(Boolean.valueOf(rights.getCopyright()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Rights commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);

			// Handle default values.
			boolean privacyAct = (getPrivacyAct() == null) ? false : getPrivacyAct().booleanValue();
			boolean intellectualProperty = (getIntellectualProperty() == null) ? false
				: getIntellectualProperty().booleanValue();
			boolean copyright = (getCopyright() == null) ? false : getCopyright().booleanValue();
			return (new Rights(privacyAct, intellectualProperty, copyright));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getPrivacyAct() == null && getIntellectualProperty() == null && getCopyright() == null);
		}

		/**
		 * Builder accessor for the privacyAct attribute.
		 */
		public Boolean getPrivacyAct() {
			return _privacyAct;
		}

		/**
		 * Builder accessor for the privacyAct attribute.
		 */
		public void setPrivacyAct(Boolean privacyAct) {
			_privacyAct = privacyAct;
		}

		/**
		 * Builder accessor for the intellectualProperty attribute.
		 */
		public Boolean getIntellectualProperty() {
			return _intellectualProperty;
		}

		/**
		 * Builder accessor for the intellectualProperty attribute.
		 */
		public void setIntellectualProperty(Boolean intellectualProperty) {
			_intellectualProperty = intellectualProperty;
		}

		/**
		 * Builder accessor for the copyright attribute.
		 */
		public Boolean getCopyright() {
			return _copyright;
		}

		/**
		 * Builder accessor for the copyright attribute.
		 */
		public void setCopyright(Boolean copyright) {
			_copyright = copyright;
		}
	}
}