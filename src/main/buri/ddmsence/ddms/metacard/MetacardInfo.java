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
package buri.ddmsence.ddms.metacard;

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
import buri.ddmsence.ddms.resource.Contributor;
import buri.ddmsence.ddms.resource.Creator;
import buri.ddmsence.ddms.resource.Dates;
import buri.ddmsence.ddms.resource.Identifier;
import buri.ddmsence.ddms.resource.PointOfContact;
import buri.ddmsence.ddms.resource.ProcessingInfo;
import buri.ddmsence.ddms.resource.Publisher;
import buri.ddmsence.ddms.resource.RecordsManagementInfo;
import buri.ddmsence.ddms.resource.RevisionRecall;
import buri.ddmsence.ddms.security.NoticeList;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.security.ntk.Access;
import buri.ddmsence.ddms.summary.Description;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:metacardInfo.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:identifier|1..*|00011}
 * 		{@child.info ddms:dates|1|00011}
 * 		{@child.info ddms:contributor|0..*|00011}
 * 		{@child.info ddms:creator|0..*|00011}
 * 		{@child.info ddms:pointOfContact|0..*|00011}
 * 		{@child.info ddms:publisher|0..*|00011}
 *  	{@child.info ddms:description|0..1|00011}
 *  	{@child.info ddms:processingInfo|0..*|00011}
 *  	{@child.info ddms:revisionRecall|0..1|00011}
 *  	{@child.info ddms:recordsManagementInfo|0..1|00011}
 *  	{@child.info ddms:noticeList|0..1|00010}
 *  	{@child.info ntk:Access|0..1|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least 1 ddms:identifier must exist.|Error|11111}
 * 		{@ddms.rule A ddms:dates must exist.|Error|11111}
 * 		{@ddms.rule At least 1 ddms:publisher must exist.|Error|11110}
 * 		{@ddms.rule At least 1 producer of any kind must exist.|Error|00001}
 * 		{@ddms.rule ddms:noticeList must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule ddms:noticeList must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule ntk:Access must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule ntk:Access must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule ntk:Access may cause issues for DDMS 4.0.1 systems.|Warning|00010}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class MetacardInfo extends AbstractBaseComponent {

	private List<Identifier> _identifiers = new ArrayList<Identifier>();
	private Dates _dates = null;
	private List<Contributor> _contributors = new ArrayList<Contributor>();
	private List<Creator> _creators = new ArrayList<Creator>();
	private List<PointOfContact> _pointOfContacts = new ArrayList<PointOfContact>();
	private List<Publisher> _publishers = new ArrayList<Publisher>();
	private Description _description = null;
	private List<ProcessingInfo> _processingInfos = new ArrayList<ProcessingInfo>();
	private RevisionRecall _revisionRecall = null;
	private RecordsManagementInfo _recordsManagementInfo = null;
	private NoticeList _noticeList = null;
	private Access _access = null;
	private SecurityAttributes _securityAttributes = null;
	private List<IDDMSComponent> _orderedList = new ArrayList<IDDMSComponent>();

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public MetacardInfo(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			DDMSVersion version = getDDMSVersion();
			_identifiers = new ArrayList<Identifier>();
			Elements components = element.getChildElements(Identifier.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++) {
				_identifiers.add(new Identifier(components.get(i)));
			}
			Element component = element.getFirstChildElement(Dates.getName(version), getNamespace());
			if (component != null)
				_dates = new Dates(component);
			components = element.getChildElements(Creator.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++)
				_creators.add(new Creator(components.get(i)));
			components = element.getChildElements(Publisher.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++)
				_publishers.add(new Publisher(components.get(i)));
			components = element.getChildElements(Contributor.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++)
				_contributors.add(new Contributor(components.get(i)));
			components = element.getChildElements(PointOfContact.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++)
				_pointOfContacts.add(new PointOfContact(components.get(i)));
			component = element.getFirstChildElement(Description.getName(version), getNamespace());
			if (component != null)
				_description = new Description(component);
			components = element.getChildElements(ProcessingInfo.getName(version), getNamespace());
			for (int i = 0; i < components.size(); i++)
				_processingInfos.add(new ProcessingInfo(components.get(i)));
			component = element.getFirstChildElement(RevisionRecall.getName(getDDMSVersion()), getNamespace());
			if (component != null)
				_revisionRecall = new RevisionRecall(component);
			component = element.getFirstChildElement(RecordsManagementInfo.getName(getDDMSVersion()), getNamespace());
			if (component != null)
				_recordsManagementInfo = new RecordsManagementInfo(component);
			component = element.getFirstChildElement(NoticeList.getName(getDDMSVersion()), getNamespace());
			if (component != null)
				_noticeList = new NoticeList(component);
			component = element.getFirstChildElement(Access.getName(getDDMSVersion()),
				getDDMSVersion().getNtkNamespace());
			if (component != null)
				_access = new Access(component);
			_securityAttributes = new SecurityAttributes(element);
			populatedOrderedList();
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
	 * <p>Because there are so many possible components in a MetacardInfo instance, they are passed in as a collection,
	 * similar to the approach used for top-level components in a Resource. If any component does not belong in a
	 * MetacardInfo instance, an InvalidDDMSException will be thrown.</p>
	 * 
	 * <p>The order of different types of components does not matter here. However, if multiple instances of the same
	 * component type exist in the list (such as multiple identifier components), those components will be stored and
	 * output in the order of the list. If only 1 instance can be supported, the last one in the list will be the
	 * one used.</p>
	 * 
	 * @param childComponents any components that belong in this MetacardInfo
	 * @param securityAttributes security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public MetacardInfo(List<IDDMSComponent> childComponents, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			if (childComponents == null)
				childComponents = Collections.emptyList();

			Element element = Util.buildDDMSElement(MetacardInfo.getName(DDMSVersion.getCurrentVersion()), null);
			setXOMElement(element, false);

			for (IDDMSComponent component : childComponents) {
				if (component == null)
					continue;

				if (component instanceof Identifier)
					_identifiers.add((Identifier) component);
				else if (component instanceof Dates)
					_dates = (Dates) component;
				else if (component instanceof Contributor)
					_contributors.add((Contributor) component);
				else if (component instanceof Creator)
					_creators.add((Creator) component);
				else if (component instanceof PointOfContact)
					_pointOfContacts.add((PointOfContact) component);
				else if (component instanceof Publisher)
					_publishers.add((Publisher) component);
				else if (component instanceof Description)
					_description = (Description) component;
				else if (component instanceof ProcessingInfo)
					_processingInfos.add((ProcessingInfo) component);
				else if (component instanceof RevisionRecall)
					_revisionRecall = (RevisionRecall) component;
				else if (component instanceof RecordsManagementInfo)
					_recordsManagementInfo = (RecordsManagementInfo) component;
				else if (component instanceof NoticeList)
					_noticeList = (NoticeList) component;
				else if (component instanceof Access) {
					_access = (Access) component;
				}
				else
					throw new InvalidDDMSException(component.getName()
						+ " is not a valid child component in a metacardInfo element.");
			}
			populatedOrderedList();
			for (IDDMSComponent component : getNestedComponents()) {
				element.appendChild(component.getXOMElementCopy());
			}
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
	 * Creates an ordered list of all the child components in this MetacardInfo, for ease of traversal.
	 */
	private void populatedOrderedList() {
		_orderedList.addAll(getIdentifiers());
		if (getDates() != null)
			_orderedList.add(getDates());
		_orderedList.addAll(getPublishers());
		_orderedList.addAll(getContributors());
		_orderedList.addAll(getCreators());
		_orderedList.addAll(getPointOfContacts());
		if (getDescription() != null)
			_orderedList.add(getDescription());
		_orderedList.addAll(getProcessingInfos());
		if (getRevisionRecall() != null)
			_orderedList.add(getRevisionRecall());
		if (getRecordsManagementInfo() != null)
			_orderedList.add(getRecordsManagementInfo());
		if (getNoticeList() != null)
			_orderedList.add(getNoticeList());
		if (getAccess() != null)
			_orderedList.add(getAccess());
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSQName(getXOMElement(), MetacardInfo.getName(getDDMSVersion()));
		if (getIdentifiers().isEmpty())
			throw new InvalidDDMSException(
				"At least one ddms:identifier must exist within a ddms:metacardInfo element.");
		Util.requireBoundedChildCount(getXOMElement(), Dates.getName(getDDMSVersion()), 1, 1);
		if (!getDDMSVersion().isAtLeast("5.0")) {
			if (getPublishers().isEmpty())
				throw new InvalidDDMSException(
					"At least one ddms:publisher must exist within a ddms:metacardInfo element.");
		}
		else {
			if (getContributors().isEmpty() && getCreators().isEmpty() && getPointOfContacts().isEmpty()
				&& getPublishers().isEmpty())
				throw new InvalidDDMSException("At least one producer must exist within a ddms:metacardInfo element.");
			// NoticeList check is implicit, since the class cannot be instantiated after DDMS 4.1.
			if (getAccess() != null)
				throw new InvalidDDMSException("The ntk:Access element must not be used after DDMS 4.1.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (!getDDMSVersion().isAtLeast("5.0") && getAccess() != null)
			addDdms40Warning("ntk:Access element");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();

		// Traverse child components, suppressing the resource prefix
		text.append(buildOutput(isHTML, localPrefix, getIdentifiers()));
		if (getDates() != null)
			text.append(getDates().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix, getPublishers()));
		text.append(buildOutput(isHTML, localPrefix, getContributors()));
		text.append(buildOutput(isHTML, localPrefix, getCreators()));
		text.append(buildOutput(isHTML, localPrefix, getPointOfContacts()));
		if (getDescription() != null)
			text.append(getDescription().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix, getProcessingInfos()));
		if (getRevisionRecall() != null)
			text.append(getRevisionRecall().getOutput(isHTML, localPrefix, ""));
		if (getRecordsManagementInfo() != null)
			text.append(getRecordsManagementInfo().getOutput(isHTML, localPrefix, ""));
		if (getNoticeList() != null)
			text.append(getNoticeList().getOutput(isHTML, localPrefix, ""));
		if (getAccess() != null)
			text.append(getAccess().getOutput(isHTML, localPrefix, ""));

		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		return (getChildComponents());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof MetacardInfo))
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
		return ("metacardInfo");
	}

	/**
	 * Accessor for an ordered list of the components in this metcardInfo. Components which are missing are not
	 * represented in this list (no null entries).
	 */
	public List<IDDMSComponent> getChildComponents() {
		return (Collections.unmodifiableList(_orderedList));
	}

	/**
	 * Accessor for a list of all identifiers
	 */
	public List<Identifier> getIdentifiers() {
		return (Collections.unmodifiableList(_identifiers));
	}

	/**
	 * Accessor for the dates
	 */
	public Dates getDates() {
		return _dates;
	}

	/**
	 * Accessor for a list of all Contributor entities
	 */
	public List<Contributor> getContributors() {
		return (Collections.unmodifiableList(_contributors));
	}

	/**
	 * Accessor for a list of all Creator entities
	 */
	public List<Creator> getCreators() {
		return (Collections.unmodifiableList(_creators));
	}

	/**
	 * Accessor for a list of all PointOfContact entities
	 */
	public List<PointOfContact> getPointOfContacts() {
		return (Collections.unmodifiableList(_pointOfContacts));
	}

	/**
	 * Accessor for a list of all Publisher entities
	 */
	public List<Publisher> getPublishers() {
		return (Collections.unmodifiableList(_publishers));
	}

	/**
	 * Accessor for the description
	 */
	public Description getDescription() {
		return _description;
	}

	/**
	 * Accessor for the processing information
	 */
	public List<ProcessingInfo> getProcessingInfos() {
		return (Collections.unmodifiableList(_processingInfos));
	}

	/**
	 * Accessor for the revisionRecall
	 */
	public RevisionRecall getRevisionRecall() {
		return _revisionRecall;
	}

	/**
	 * Accessor for the recordsManagementInfo
	 */
	public RecordsManagementInfo getRecordsManagementInfo() {
		return _recordsManagementInfo;
	}

	/**
	 * Accessor for the noticeList
	 */
	public NoticeList getNoticeList() {
		return _noticeList;
	}

	/**
	 * Accessor for the Access. May be null.
	 */
	public Access getAccess() {
		return (_access);
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
		private List<Identifier.Builder> _identifiers;
		private Dates.Builder _dates;
		private List<Contributor.Builder> _contributors;
		private List<Creator.Builder> _creators;
		private List<PointOfContact.Builder> _pointOfContacts;
		private List<Publisher.Builder> _publishers;
		private Description.Builder _description;
		private List<ProcessingInfo.Builder> _processingInfos;
		private RevisionRecall.Builder _revisionRecall;
		private RecordsManagementInfo.Builder _recordsManagementInfo;
		private NoticeList.Builder _noticeList;
		private Access.Builder _access;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(MetacardInfo metacardInfo) {
			for (IDDMSComponent component : metacardInfo.getChildComponents()) {
				if (component instanceof Identifier)
					getIdentifiers().add(new Identifier.Builder((Identifier) component));
				else if (component instanceof Dates)
					setDates(new Dates.Builder((Dates) component));
				else if (component instanceof Creator)
					getCreators().add(new Creator.Builder((Creator) component));
				else if (component instanceof Contributor)
					getContributors().add(new Contributor.Builder((Contributor) component));
				else if (component instanceof Publisher)
					getPublishers().add(new Publisher.Builder((Publisher) component));
				else if (component instanceof PointOfContact)
					getPointOfContacts().add(new PointOfContact.Builder((PointOfContact) component));
				else if (component instanceof Description)
					setDescription(new Description.Builder((Description) component));
				else if (component instanceof ProcessingInfo)
					getProcessingInfos().add(new ProcessingInfo.Builder((ProcessingInfo) component));
				else if (component instanceof RevisionRecall)
					setRevisionRecall(new RevisionRecall.Builder((RevisionRecall) component));
				else if (component instanceof RecordsManagementInfo)
					setRecordsManagementInfo(new RecordsManagementInfo.Builder((RecordsManagementInfo) component));
				else if (component instanceof NoticeList)
					setNoticeList(new NoticeList.Builder((NoticeList) component));
				else if (component instanceof Access)
					setAccess(new Access.Builder((Access) component));
			}
			setSecurityAttributes(new SecurityAttributes.Builder(metacardInfo.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public MetacardInfo commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<IDDMSComponent> childComponents = new ArrayList<IDDMSComponent>();
			for (IBuilder builder : getChildBuilders()) {
				IDDMSComponent component = builder.commit();
				if (component != null) {
					childComponents.add(component);
				}
			}
			return (new MetacardInfo(childComponents, getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getChildBuilders()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (!hasValueInList && getSecurityAttributes().isEmpty());
		}

		/**
		 * Convenience method to get every child Builder in this Builder.
		 * 
		 * @return a list of IBuilders
		 */
		private List<IBuilder> getChildBuilders() {
			List<IBuilder> list = new ArrayList<IBuilder>();
			list.addAll(getIdentifiers());
			list.addAll(getPublishers());
			list.addAll(getContributors());
			list.addAll(getCreators());
			list.addAll(getPointOfContacts());
			list.addAll(getProcessingInfos());
			list.add(getDates());
			list.add(getDescription());
			list.add(getRevisionRecall());
			list.add(getRecordsManagementInfo());
			list.add(getNoticeList());
			list.add(getAccess());
			return (list);
		}

		/**
		 * Builder accessor for the taskingInfos
		 */
		public List<Identifier.Builder> getIdentifiers() {
			if (_identifiers == null)
				_identifiers = new LazyList(Identifier.Builder.class);
			return _identifiers;
		}

		/**
		 * Builder accessor for the dates
		 */
		public Dates.Builder getDates() {
			if (_dates == null)
				_dates = new Dates.Builder();
			return _dates;
		}

		/**
		 * Builder accessor for the dates
		 */
		public void setDates(Dates.Builder dates) {
			_dates = dates;
		}

		/**
		 * Builder accessor for creators
		 */
		public List<Creator.Builder> getCreators() {
			if (_creators == null)
				_creators = new LazyList(Creator.Builder.class);
			return _creators;
		}

		/**
		 * Builder accessor for contributors
		 */
		public List<Contributor.Builder> getContributors() {
			if (_contributors == null)
				_contributors = new LazyList(Contributor.Builder.class);
			return _contributors;
		}

		/**
		 * Builder accessor for publishers
		 */
		public List<Publisher.Builder> getPublishers() {
			if (_publishers == null)
				_publishers = new LazyList(Publisher.Builder.class);
			return _publishers;
		}

		/**
		 * Builder accessor for points of contact
		 */
		public List<PointOfContact.Builder> getPointOfContacts() {
			if (_pointOfContacts == null)
				_pointOfContacts = new LazyList(PointOfContact.Builder.class);
			return _pointOfContacts;
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
		 * Builder accessor for the processingInfos
		 */
		public List<ProcessingInfo.Builder> getProcessingInfos() {
			if (_processingInfos == null)
				_processingInfos = new LazyList(ProcessingInfo.Builder.class);
			return _processingInfos;
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
		 * Builder accessor for the noticeList
		 */
		public NoticeList.Builder getNoticeList() {
			if (_noticeList == null)
				_noticeList = new NoticeList.Builder();
			return _noticeList;
		}

		/**
		 * Builder accessor for the noticeList
		 */
		public void setNoticeList(NoticeList.Builder noticeList) {
			_noticeList = noticeList;
		}

		/**
		 * Builder accessor for the access
		 */
		public Access.Builder getAccess() {
			if (_access == null)
				_access = new Access.Builder();
			return _access;
		}

		/**
		 * Accessor for the access
		 */
		public void setAccess(Access.Builder access) {
			_access = access;
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