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
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:resourceManagement.
 *  * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:recordsManagementInfo|0..1|00011}
 * 		{@child.info ddms:revisionRecall|0..1|00011}
 * 		{@child.info ddms:taskingInfo|0..*|00011}
 * 		{@child.info ddms:processingInfo|0..*|00011}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class ResourceManagement extends AbstractBaseComponent {

	private RecordsManagementInfo _recordsManagementInfo = null;
	private RevisionRecall _revisionRecall = null;
	private List<TaskingInfo> _taskingInfos = null;
	private List<ProcessingInfo> _processingInfos = null;
	private SecurityAttributes _securityAttributes = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ResourceManagement(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element recordsManagementInfo = element.getFirstChildElement(
				RecordsManagementInfo.getName(getDDMSVersion()), getNamespace());
			if (recordsManagementInfo != null)
				_recordsManagementInfo = new RecordsManagementInfo(recordsManagementInfo);
			Element revisionRecall = element.getFirstChildElement(RevisionRecall.getName(getDDMSVersion()),
				getNamespace());
			if (revisionRecall != null)
				_revisionRecall = new RevisionRecall(revisionRecall);
			_taskingInfos = new ArrayList<TaskingInfo>();
			Elements taskingInfos = element.getChildElements(TaskingInfo.getName(getDDMSVersion()), getNamespace());
			for (int i = 0; i < taskingInfos.size(); i++) {
				_taskingInfos.add(new TaskingInfo(taskingInfos.get(i)));
			}
			_processingInfos = new ArrayList<ProcessingInfo>();
			Elements processingInfos = element.getChildElements(ProcessingInfo.getName(getDDMSVersion()),
				getNamespace());
			for (int i = 0; i < processingInfos.size(); i++) {
				_processingInfos.add(new ProcessingInfo(processingInfos.get(i)));
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
	 * @param recordsManagementInfo records management info
	 * @param revisionRecall information about revision recalls
	 * @param taskingInfos list of tasking info
	 * @param processingInfos list of processing info
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ResourceManagement(RecordsManagementInfo recordsManagementInfo, RevisionRecall revisionRecall,
		List<TaskingInfo> taskingInfos, List<ProcessingInfo> processingInfos, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			if (taskingInfos == null)
				taskingInfos = Collections.emptyList();
			if (processingInfos == null)
				processingInfos = Collections.emptyList();

			Element element = Util.buildDDMSElement(ResourceManagement.getName(DDMSVersion.getCurrentVersion()), null);
			setXOMElement(element, false);
			if (recordsManagementInfo != null)
				element.appendChild(recordsManagementInfo.getXOMElementCopy());
			if (revisionRecall != null)
				element.appendChild(revisionRecall.getXOMElementCopy());
			for (TaskingInfo info : taskingInfos)
				element.appendChild(info.getXOMElementCopy());
			for (ProcessingInfo info : processingInfos)
				element.appendChild(info.getXOMElementCopy());

			_recordsManagementInfo = recordsManagementInfo;
			_revisionRecall = revisionRecall;
			_taskingInfos = taskingInfos;
			_processingInfos = processingInfos;
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
		Util.requireDDMSQName(getXOMElement(), ResourceManagement.getName(getDDMSVersion()));
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (getRecordsManagementInfo() != null)
			text.append(getRecordsManagementInfo().getOutput(isHTML, localPrefix, ""));
		if (getRevisionRecall() != null)
			text.append(getRevisionRecall().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix, getTaskingInfos()));
		text.append(buildOutput(isHTML, localPrefix, getProcessingInfos()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getRecordsManagementInfo());
		list.add(getRevisionRecall());
		list.addAll(getTaskingInfos());
		list.addAll(getProcessingInfos());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ResourceManagement))
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
		return ("resourceManagement");
	}

	/**
	 * Accessor for the recordsManagementInfo
	 */
	public RecordsManagementInfo getRecordsManagementInfo() {
		return _recordsManagementInfo;
	}

	/**
	 * Accessor for the revisionRecall
	 */
	public RevisionRecall getRevisionRecall() {
		return _revisionRecall;
	}

	/**
	 * Accessor for the tasking information
	 */
	public List<TaskingInfo> getTaskingInfos() {
		return (Collections.unmodifiableList(_taskingInfos));
	}

	/**
	 * Accessor for the processing information
	 */
	public List<ProcessingInfo> getProcessingInfos() {
		return (Collections.unmodifiableList(_processingInfos));
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
		private RecordsManagementInfo.Builder _recordsManagementInfo;
		private RevisionRecall.Builder _revisionRecall;
		private List<TaskingInfo.Builder> _taskingInfos;
		private List<ProcessingInfo.Builder> _processingInfos;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ResourceManagement resourceManagement) {
			if (resourceManagement.getRecordsManagementInfo() != null)
				setRecordsManagementInfo(new RecordsManagementInfo.Builder(
					resourceManagement.getRecordsManagementInfo()));
			if (resourceManagement.getRevisionRecall() != null)
				setRevisionRecall(new RevisionRecall.Builder(resourceManagement.getRevisionRecall()));
			for (TaskingInfo info : resourceManagement.getTaskingInfos())
				getTaskingInfos().add(new TaskingInfo.Builder(info));
			for (ProcessingInfo info : resourceManagement.getProcessingInfos())
				getProcessingInfos().add(new ProcessingInfo.Builder(info));
			setSecurityAttributes(new SecurityAttributes.Builder(resourceManagement.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ResourceManagement commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<TaskingInfo> taskingInfos = new ArrayList<TaskingInfo>();
			for (TaskingInfo.Builder builder : getTaskingInfos()) {
				TaskingInfo info = builder.commit();
				if (info != null)
					taskingInfos.add(info);
			}
			List<ProcessingInfo> processingInfos = new ArrayList<ProcessingInfo>();
			for (ProcessingInfo.Builder builder : getProcessingInfos()) {
				ProcessingInfo point = builder.commit();
				if (point != null)
					processingInfos.add(point);
			}
			return (new ResourceManagement(getRecordsManagementInfo().commit(), getRevisionRecall().commit(),
				taskingInfos, processingInfos, getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getProcessingInfos()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			for (IBuilder builder : getTaskingInfos()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (!hasValueInList && getRecordsManagementInfo().isEmpty() && getRevisionRecall().isEmpty() && getSecurityAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the recordsManagementInfo
		 */
		public RecordsManagementInfo.Builder getRecordsManagementInfo() {
			if (_recordsManagementInfo == null)
				_recordsManagementInfo = new RecordsManagementInfo.Builder();
			return _recordsManagementInfo;
		}

		/**
		 * Builder accessor for the recordsManagementInfo
		 */
		public void setRecordsManagementInfo(RecordsManagementInfo.Builder recordsManagementInfo) {
			_recordsManagementInfo = recordsManagementInfo;
		}

		/**
		 * Builder accessor for the revisionRecall
		 */
		public RevisionRecall.Builder getRevisionRecall() {
			if (_revisionRecall == null)
				_revisionRecall = new RevisionRecall.Builder();
			return _revisionRecall;
		}

		/**
		 * Builder accessor for the revisionRecall
		 */
		public void setRevisionRecall(RevisionRecall.Builder revisionRecall) {
			_revisionRecall = revisionRecall;
		}

		/**
		 * Builder accessor for the taskingInfos
		 */
		public List<TaskingInfo.Builder> getTaskingInfos() {
			if (_taskingInfos == null)
				_taskingInfos = new LazyList(TaskingInfo.Builder.class);
			return _taskingInfos;
		}

		/**
		 * Builder accessor for the processingInfos
		 */
		public List<ProcessingInfo.Builder> getProcessingInfos() {
			if (_processingInfos == null)
				_processingInfos = new LazyList(ProcessingInfo.Builder.class);
			return _processingInfos;
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