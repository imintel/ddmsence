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
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:recordsManagementInfo.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:recordKeeper|0..1|00011}
 * 		{@child.info ddms:applicationSoftware|0..1|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:vitalRecordIndicator|0..1|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class RecordsManagementInfo extends AbstractBaseComponent {

	private RecordKeeper _recordKeeper = null;
	private ApplicationSoftware _applicationSoftware = null;

	private static final String VITAL_RECORD_INDICATOR_NAME = "vitalRecordIndicator";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RecordsManagementInfo(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element recordKeeper = element.getFirstChildElement(RecordKeeper.getName(getDDMSVersion()), getNamespace());
			if (recordKeeper != null)
				_recordKeeper = new RecordKeeper(recordKeeper);
			Element applicationSoftware = element.getFirstChildElement(ApplicationSoftware.getName(getDDMSVersion()),
				getNamespace());
			if (applicationSoftware != null)
				_applicationSoftware = new ApplicationSoftware(applicationSoftware);
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
	 * @param recordKeeper the record keeper
	 * @param applicationSoftware the software
	 * @param vitalRecordIndicator whether this is a vital record (defaults to false)
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public RecordsManagementInfo(RecordKeeper recordKeeper, ApplicationSoftware applicationSoftware,
		Boolean vitalRecordIndicator) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(RecordsManagementInfo.getName(DDMSVersion.getCurrentVersion()),
				null);
			setXOMElement(element, false);
			if (recordKeeper != null)
				element.appendChild(recordKeeper.getXOMElementCopy());
			if (applicationSoftware != null)
				element.appendChild(applicationSoftware.getXOMElementCopy());
			Util.addDDMSAttribute(element, VITAL_RECORD_INDICATOR_NAME, String.valueOf(vitalRecordIndicator));
			_recordKeeper = recordKeeper;
			_applicationSoftware = applicationSoftware;
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
		Util.requireDDMSQName(getXOMElement(), RecordsManagementInfo.getName(getDDMSVersion()));
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (getRecordKeeper() != null)
			text.append(getRecordKeeper().getOutput(isHTML, localPrefix, ""));
		if (getApplicationSoftware() != null)
			text.append(getApplicationSoftware().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix + VITAL_RECORD_INDICATOR_NAME,
			String.valueOf(getVitalRecordIndicator())));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getRecordKeeper());
		list.add(getApplicationSoftware());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof RecordsManagementInfo))
			return (false);
		RecordsManagementInfo test = (RecordsManagementInfo) obj;
		return (getVitalRecordIndicator().equals(test.getVitalRecordIndicator()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getVitalRecordIndicator().hashCode();
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
		return ("recordsManagementInfo");
	}

	/**
	 * Accessor for the recordKeeper
	 */
	public RecordKeeper getRecordKeeper() {
		return _recordKeeper;
	}

	/**
	 * Accessor for the applicationSoftware
	 */
	public ApplicationSoftware getApplicationSoftware() {
		return _applicationSoftware;
	}

	/**
	 * Accessor for the vitalRecordIndicator attribute. This defaults to false if not found.
	 */
	public Boolean getVitalRecordIndicator() {
		String value = getAttributeValue(VITAL_RECORD_INDICATOR_NAME, getNamespace());
		if ("true".equals(value))
			return (Boolean.TRUE);
		return (Boolean.FALSE);
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
		private RecordKeeper.Builder _recordKeeper;
		private ApplicationSoftware.Builder _applicationSoftware;
		private Boolean _vitalRecordIndicator;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(RecordsManagementInfo info) {
			if (info.getRecordKeeper() != null)
				setRecordKeeper(new RecordKeeper.Builder(info.getRecordKeeper()));
			if (info.getApplicationSoftware() != null)
				setApplicationSoftware(new ApplicationSoftware.Builder(info.getApplicationSoftware()));
			setVitalRecordIndicator(info.getVitalRecordIndicator());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public RecordsManagementInfo commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new RecordsManagementInfo(getRecordKeeper().commit(),
				getApplicationSoftware().commit(), getVitalRecordIndicator()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getRecordKeeper().isEmpty() && getApplicationSoftware().isEmpty() && getVitalRecordIndicator() == null);
		}

		/**
		 * Builder accessor for the recordKeeper
		 */
		public RecordKeeper.Builder getRecordKeeper() {
			if (_recordKeeper == null)
				_recordKeeper = new RecordKeeper.Builder();
			return _recordKeeper;
		}

		/**
		 * Builder accessor for the recordKeeper
		 */
		public void setRecordKeeper(RecordKeeper.Builder recordKeeper) {
			_recordKeeper = recordKeeper;
		}

		/**
		 * Builder accessor for the applicationSoftware
		 */
		public ApplicationSoftware.Builder getApplicationSoftware() {
			if (_applicationSoftware == null)
				_applicationSoftware = new ApplicationSoftware.Builder();
			return _applicationSoftware;
		}

		/**
		 * Builder accessor for the applicationSoftware
		 */
		public void setApplicationSoftware(ApplicationSoftware.Builder applicationSoftware) {
			_applicationSoftware = applicationSoftware;
		}

		/**
		 * Builder accessor for the vitalRecordIndicator flag
		 */
		public Boolean getVitalRecordIndicator() {
			return _vitalRecordIndicator;
		}

		/**
		 * Builder accessor for the vitalRecordIndicator flag
		 */
		public void setVitalRecordIndicator(Boolean vitalRecordIndicator) {
			_vitalRecordIndicator = vitalRecordIndicator;
		}
	}
}