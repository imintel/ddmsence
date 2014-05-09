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
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.Description;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:taskingInfo.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:requesterInfo|0..*|00011}
 * 		{@child.info ddms:addressee|0..*|00011}
 * 		{@child.info ddms:description|0..1|00011}
 * 		{@child.info ddms:taskID|1|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:classification|1|00011}
 * 		{@child.info ism:ownerProducer|1..*|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ddms:taskID must exist.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class TaskingInfo extends AbstractBaseComponent {

	private List<RequesterInfo> _requesterInfos = null;
	private List<Addressee> _addressees = null;
	private Description _description = null;
	private TaskID _taskID = null;
	private SecurityAttributes _securityAttributes = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public TaskingInfo(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("element", element);
			setXOMElement(element, false);

			_requesterInfos = new ArrayList<RequesterInfo>();
			Elements infos = element.getChildElements(RequesterInfo.getName(getDDMSVersion()), getNamespace());
			for (int i = 0; i < infos.size(); i++) {
				_requesterInfos.add(new RequesterInfo(infos.get(i)));
			}
			_addressees = new ArrayList<Addressee>();
			Elements addressees = element.getChildElements(Addressee.getName(getDDMSVersion()), getNamespace());
			for (int i = 0; i < addressees.size(); i++) {
				_addressees.add(new Addressee(addressees.get(i)));
			}
			Element description = element.getFirstChildElement(Description.getName(getDDMSVersion()), getNamespace());
			if (description != null)
				_description = new Description(description);
			Element taskID = element.getFirstChildElement(TaskID.getName(getDDMSVersion()), getNamespace());
			if (taskID != null)
				_taskID = new TaskID(taskID);
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
	 * @param requesterInfos list of requestors
	 * @param addressees list of addressee
	 * @param description description of tasking
	 * @param taskID taskID for tasking
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException
	 */
	public TaskingInfo(List<RequesterInfo> requesterInfos, List<Addressee> addressees, Description description,
		TaskID taskID, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			if (requesterInfos == null)
				requesterInfos = Collections.emptyList();
			if (addressees == null)
				addressees = Collections.emptyList();

			Element element = Util.buildDDMSElement(TaskingInfo.getName(DDMSVersion.getCurrentVersion()), null);
			setXOMElement(element, false);
			for (RequesterInfo info : requesterInfos)
				element.appendChild(info.getXOMElementCopy());
			for (Addressee addressee : addressees)
				element.appendChild(addressee.getXOMElementCopy());
			if (description != null)
				element.appendChild(description.getXOMElementCopy());
			if (taskID != null)
				element.appendChild(taskID.getXOMElementCopy());

			_requesterInfos = requesterInfos;
			_addressees = addressees;
			_description = description;
			_taskID = taskID;
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
		Util.requireDDMSQName(getXOMElement(), TaskingInfo.getName(getDDMSVersion()));
		Util.requireDDMSValue("taskID", getTaskID());
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getRequesterInfos()));
		text.append(buildOutput(isHTML, localPrefix, getAddressees()));
		if (getDescription() != null)
			text.append(getDescription().getOutput(isHTML, localPrefix, ""));
		text.append(getTaskID().getOutput(isHTML, localPrefix, ""));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getRequesterInfos());
		list.addAll(getAddressees());
		list.add(getDescription());
		list.add(getTaskID());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof TaskingInfo))
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
		return ("taskingInfo");
	}

	/**
	 * Accessor for the requesterInfos
	 */
	public List<RequesterInfo> getRequesterInfos() {
		return _requesterInfos;
	}

	/**
	 * Accessor for the addressees
	 */
	public List<Addressee> getAddressees() {
		return _addressees;
	}

	/**
	 * Accessor for the description
	 */
	public Description getDescription() {
		return _description;
	}

	/**
	 * Accessor for the taskID
	 */
	public TaskID getTaskID() {
		return _taskID;
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
		private List<RequesterInfo.Builder> _requesterInfos;
		private List<Addressee.Builder> _addressees;
		private Description.Builder _description;
		private TaskID.Builder _taskID;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(TaskingInfo info) {
			for (RequesterInfo requester : info.getRequesterInfos())
				getRequesterInfos().add(new RequesterInfo.Builder(requester));
			for (Addressee addressee : info.getAddressees())
				getAddressees().add(new Addressee.Builder(addressee));
			if (info.getDescription() != null)
				setDescription(new Description.Builder(info.getDescription()));
			setTaskID(new TaskID.Builder(info.getTaskID()));
			setSecurityAttributes(new SecurityAttributes.Builder(info.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public TaskingInfo commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<RequesterInfo> requesterInfos = new ArrayList<RequesterInfo>();
			for (IBuilder builder : getRequesterInfos()) {
				RequesterInfo component = (RequesterInfo) builder.commit();
				if (component != null)
					requesterInfos.add(component);
			}
			List<Addressee> addressees = new ArrayList<Addressee>();
			for (IBuilder builder : getAddressees()) {
				Addressee component = (Addressee) builder.commit();
				if (component != null)
					addressees.add(component);
			}
			return (new TaskingInfo(requesterInfos, addressees, getDescription().commit(), getTaskID().commit(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getRequesterInfos())
				hasValueInList = hasValueInList || !builder.isEmpty();
			for (IBuilder builder : getAddressees())
				hasValueInList = hasValueInList || !builder.isEmpty();
			return (!hasValueInList && getDescription().isEmpty() && getTaskID().isEmpty() && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the requesterInfos
		 */
		public List<RequesterInfo.Builder> getRequesterInfos() {
			if (_requesterInfos == null)
				_requesterInfos = new LazyList(RequesterInfo.Builder.class);
			return _requesterInfos;
		}

		/**
		 * Builder accessor for the addressees
		 */
		public List<Addressee.Builder> getAddressees() {
			if (_addressees == null)
				_addressees = new LazyList(Addressee.Builder.class);
			return _addressees;
		}

		/**
		 * Builder accessor for the description
		 */
		public Description.Builder getDescription() {
			if (_description == null)
				_description = new Description.Builder();
			return _description;
		}

		/**
		 * Builder accessor for the description
		 */
		public void setDescription(Description.Builder description) {
			_description = description;
		}

		/**
		 * Builder accessor for the taskID
		 */
		public TaskID.Builder getTaskID() {
			if (_taskID == null)
				_taskID = new TaskID.Builder();
			return _taskID;
		}

		/**
		 * Builder accessor for the taskID
		 */
		public void setTaskID(TaskID.Builder taskID) {
			_taskID = taskID;
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
	}
}