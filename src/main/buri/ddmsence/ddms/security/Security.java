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
package buri.ddmsence.ddms.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.security.ntk.Access;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:security.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 *  <p></p>
 *  
 *  {@table.header History}
 * 		<p>This class was removed in DDMS 5.0, as part of the migrartion to the IC Trusted Data Format.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:noticeList|0..1|00010}
 * 		{@child.info ntk:Access|0..1|00010}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:excludeFromRollup|1|11110}
 * 		{@child.info ism:classification|1|11110}
 * 		{@child.info ism:ownerProducer|1..*|11110}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11110}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used after the DDMS version in which it was removed.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule ism:excludeFromRollup must not be used before the DDMS version in which it was introduced.|Error|10000}
 * 		{@ddms.rule ism:excludeFromRollup must exist and is set to "true".|Error|01111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Security extends AbstractBaseComponent {

	private NoticeList _noticeList = null;
	private Access _access = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String FIXED_ROLLUP = "true";

	/** Attribute name */
	public static final String EXCLUDE_FROM_ROLLUP_NAME = "excludeFromRollup";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Security(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element noticeListElement = element.getFirstChildElement(NoticeList.getName(getDDMSVersion()),
				getNamespace());
			if (noticeListElement != null)
				_noticeList = new NoticeList(noticeListElement);

			Element accessElement = element.getFirstChildElement(Access.getName(getDDMSVersion()),
				getDDMSVersion().getNtkNamespace());
			if (accessElement != null)
				_access = new Access(accessElement);
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
	 * @param noticeList notice list
	 * @param access NTK access information
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Security(NoticeList noticeList, Access access, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		try {
			DDMSVersion version = DDMSVersion.getCurrentVersion();

			Element element = Util.buildDDMSElement(Security.getName(version), null);
			if (noticeList != null)
				element.appendChild(noticeList.getXOMElementCopy());
			if (access != null)
				element.appendChild(access.getXOMElementCopy());
			if (DDMSVersion.getCurrentVersion().isAtLeast("3.0"))
				Util.addAttribute(element, PropertyReader.getPrefix("ism"), EXCLUDE_FROM_ROLLUP_NAME,
					DDMSVersion.getCurrentVersion().getIsmNamespace(), FIXED_ROLLUP);
			_noticeList = noticeList;
			_access = access;
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			if (securityAttributes != null)
				securityAttributes.addTo(element);
			setXOMElement(element, true);
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
		requireAtMostVersion("4.1");
		Util.requireDDMSQName(getXOMElement(), Security.getName(getDDMSVersion()));
		if (getDDMSVersion().isAtLeast("3.0")) {
			if (getExcludeFromRollup() == null ||!FIXED_ROLLUP.equals(String.valueOf(getExcludeFromRollup())))
				throw new InvalidDDMSException("The excludeFromRollup attribute must have a fixed value of \""
					+ FIXED_ROLLUP + "\".");
		}
		else if (getExcludeFromRollup() != null)
			throw new InvalidDDMSException("The excludeFromRollup attribute must not be used until DDMS 3.0 or later.");
		getSecurityAttributes().requireClassification();
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (getExcludeFromRollup() != null)
			text.append(buildOutput(isHTML, localPrefix + EXCLUDE_FROM_ROLLUP_NAME,
				String.valueOf(getExcludeFromRollup())));
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
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getNoticeList());
		list.add(getAccess());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Security))
			return (false);
		return (true);
		// ExcludeFromRollup is not included in equality or hashCode, because it is fixed at TRUE.
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("security");
	}

	/**
	 * Accessor for the excludeFromRollup attribute. This may be null for DDMS 2.0 components.
	 */
	public Boolean getExcludeFromRollup() {
		String value = getAttributeValue(EXCLUDE_FROM_ROLLUP_NAME, DDMSVersion.getCurrentVersion().getIsmNamespace());
		if ("true".equals(value))
			return (Boolean.TRUE);
		if ("false".equals(value))
			return (Boolean.FALSE);
		return (null);
	}

	/**
	 * Accessor for the NoticeList. May be null.
	 */
	public NoticeList getNoticeList() {
		return (_noticeList);
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
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -7744353774641616270L;
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
		public Builder(Security security) {
			if (security.getNoticeList() != null)
				setNoticeList(new NoticeList.Builder(security.getNoticeList()));
			if (security.getAccess() != null)
				setAccess(new Access.Builder(security.getAccess()));
			setSecurityAttributes(new SecurityAttributes.Builder(security.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Security commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Security(getNoticeList().commit(), getAccess().commit(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (getNoticeList().isEmpty() && getAccess().isEmpty() && getSecurityAttributes().isEmpty());
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