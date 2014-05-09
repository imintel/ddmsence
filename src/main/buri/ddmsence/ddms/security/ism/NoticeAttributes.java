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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nu.xom.Element;
import buri.ddmsence.AbstractAttributeGroup;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.Resource;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Attribute group for the ISM notice markings used on a {@link Resource} and {@link Notice}.
 * <br /><br />
 * {@ddms.versions 00010}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		<p>This class was introduced to support ISM notices in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:noticeType|0..1|00010}
 * 		{@child.info ism:noticeReason|0..1|00010}
 * 		{@child.info ism:noticeDate|0..1|00010}
 * 		{@child.info ism:unregisteredNoticeType|0..1|00010}
 * 		{@child.info ism:externalNotice|0..1|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Attribute group must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule If set, ism:noticeType must be a valid token.|Error|11111}
 * 		{@ddms.rule If set, ism:noticeReason must be shorter than 2048 characters.|Error|11111}
 * 		{@ddms.rule If set, ism:unregisteredNoticeType must be shorter than 2048 characters.|Error|11111}
 * 		{@ddms.rule If set, ism:noticeDate must adhere to a valid date format.|Error|11111}
 * 		<p>Does NOT do any validation on the constraints described in the DES ISM specification.</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class NoticeAttributes extends AbstractAttributeGroup {
	private String _noticeType = null;
	private String _noticeReason = null;
	private XMLGregorianCalendar _noticeDate = null;
	private String _unregisteredNoticeType = null;
	private Boolean _externalNotice = null;

	/** Attribute name */
	public static final String NOTICE_TYPE_NAME = "noticeType";

	/** Attribute name */
	public static final String NOTICE_REASON_NAME = "noticeReason";

	/** Attribute name */
	public static final String NOTICE_DATE_NAME = "noticeDate";

	/** Attribute name */
	public static final String UNREGISTERED_NOTICE_TYPE_NAME = "unregisteredNoticeType";

	/** Attribute name */
	public static final String EXTERNAL_NOTICE_NAME = "externalNotice";

	/** Maximum length of reason and unregistered notice type attributes. */
	public static final int MAX_LENGTH = 2048;

	private static final Set<String> ALL_NAMES = new HashSet<String>();
	static {
		ALL_NAMES.add(NOTICE_TYPE_NAME);
		ALL_NAMES.add(NOTICE_REASON_NAME);
		ALL_NAMES.add(NOTICE_DATE_NAME);
		ALL_NAMES.add(UNREGISTERED_NOTICE_TYPE_NAME);
		ALL_NAMES.add(EXTERNAL_NOTICE_NAME);
	}

	/** A set of all SecurityAttribute names which should not be converted into ExtensibleAttributes */
	public static final Set<String> NON_EXTENSIBLE_NAMES = Collections.unmodifiableSet(ALL_NAMES);

	/**
	 * Returns a non-null instance of notice attributes. If the instance passed in is not null, it will be returned.
	 * 
	 * @param noticeAttributes the attributes to return by default
	 * @return a non-null attributes instance
	 * @throws InvalidDDMSException if there are problems creating the empty attributes instance
	 */
	public static NoticeAttributes getNonNullInstance(NoticeAttributes noticeAttributes) throws InvalidDDMSException {
		return (noticeAttributes == null ? new NoticeAttributes(null, null, null, null) : noticeAttributes);
	}

	/**
	 * Base constructor
	 * 
	 * @param element the XOM element which is decorated with these attributes.
	 */
	public NoticeAttributes(Element element) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getVersionForNamespace(element.getNamespaceURI());
		setNamespace(version.getIsmNamespace());
		_noticeType = element.getAttributeValue(NOTICE_TYPE_NAME, getNamespace());
		_noticeReason = element.getAttributeValue(NOTICE_REASON_NAME, getNamespace());
		_unregisteredNoticeType = element.getAttributeValue(UNREGISTERED_NOTICE_TYPE_NAME, getNamespace());
		String noticeDate = element.getAttributeValue(NOTICE_DATE_NAME, getNamespace());
		if (!Util.isEmpty(noticeDate))
			_noticeDate = getFactory().newXMLGregorianCalendar(noticeDate);
		String external = element.getAttributeValue(EXTERNAL_NOTICE_NAME, getNamespace());
		if (!Util.isEmpty(external))
			_externalNotice = Boolean.valueOf(external);
		validate(DDMSVersion.getVersionForNamespace(element.getNamespaceURI()));
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @deprecated A new constructor was added for DDMS 4.1 to support ism:externalNotice. This constructor is preserved
	 *             for backwards compatibility, but may disappear in the next major release.
	 * 
	 * @param noticeType the notice type (with a value from the CVE)
	 * @param noticeReason the reason associated with a notice
	 * @param noticeDate the date associated with a notice
	 * @param unregisteredNoticeType a notice type not in the CVE
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NoticeAttributes(String noticeType, String noticeReason, String noticeDate, String unregisteredNoticeType)
		throws InvalidDDMSException {
		this(noticeType, noticeReason, noticeDate, unregisteredNoticeType, null);
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param noticeType the notice type (with a value from the CVE)
	 * @param noticeReason the reason associated with a notice
	 * @param noticeDate the date associated with a notice
	 * @param unregisteredNoticeType a notice type not in the CVE
	 * @param externalNotice true if this notice is for an external resource
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NoticeAttributes(String noticeType, String noticeReason, String noticeDate, String unregisteredNoticeType,
		Boolean externalNotice) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		setNamespace(version.getIsmNamespace());
		_noticeType = noticeType;
		_noticeReason = noticeReason;
		_unregisteredNoticeType = unregisteredNoticeType;
		if (!Util.isEmpty(noticeDate)) {
			try {
				_noticeDate = getFactory().newXMLGregorianCalendar(noticeDate);
			}
			catch (IllegalArgumentException e) {
				throw new InvalidDDMSException("The ism:noticeDate attribute must adhere to a valid date format.");
			}
		}
		_externalNotice = externalNotice;
		validate(version);
	}

	/**
	 * Convenience method to add these attributes onto an existing XOM Element
	 * 
	 * @param element the element to decorate
	 */
	public void addTo(Element element) throws InvalidDDMSException {
		DDMSVersion elementVersion = DDMSVersion.getVersionForNamespace(element.getNamespaceURI());
		validateCompatibleVersion(elementVersion);
		String icNamespace = elementVersion.getIsmNamespace();
		String icPrefix = PropertyReader.getPrefix("ism");

		Util.addAttribute(element, icPrefix, NOTICE_TYPE_NAME, icNamespace, getNoticeType());
		Util.addAttribute(element, icPrefix, NOTICE_REASON_NAME, icNamespace, getNoticeReason());
		if (getNoticeDate() != null)
			Util.addAttribute(element, icPrefix, NOTICE_DATE_NAME, icNamespace, getNoticeDate().toXMLFormat());
		Util.addAttribute(element, icPrefix, UNREGISTERED_NOTICE_TYPE_NAME, icNamespace, getUnregisteredNoticeType());
		if (isExternalReference() != null)
			Util.addAttribute(element, icPrefix, EXTERNAL_NOTICE_NAME, icNamespace,
				String.valueOf(isExternalReference()));
	}

	/**
	 * Checks if any attributes have been set.
	 * 
	 * @return true if no attributes have values, false otherwise
	 */
	public boolean isEmpty() {
		return (Util.isEmpty(getNoticeType()) 
			&& Util.isEmpty(getNoticeReason())
			&& Util.isEmpty(getUnregisteredNoticeType()) 
			&& getNoticeDate() == null
			&& isExternalReference() == null);
	}

	/**
	 * Compares the DDMS version of these attributes to another DDMS version
	 * 
	 * @param newParentVersion the version to test
	 * @throws InvalidDDMSException if the versions do not match
	 */
	protected void validateCompatibleVersion(DDMSVersion newParentVersion) throws InvalidDDMSException {
		if (!newParentVersion.getIsmNamespace().equals(getNamespace()))
			throw new InvalidDDMSException(INCOMPATIBLE_VERSION_MESSAGE);
	}

	/**
	 * Validates the attribute group. Where appropriate the {@link ISMVocabulary} enumerations are validated.
	 * 
	 * @param version the DDMS version to validate against. This cannot be stored in the attribute group because some
	 *        DDMSVersions have the same attribute XML namespace (e.g. XLink, ISM, NTK, GML after DDMS 2.0).
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	protected void validate(DDMSVersion version) throws InvalidDDMSException {
		if (!version.isAtLeast("4.0.1") && !isEmpty())
			throw new InvalidDDMSException("Notice attributes must not be used until DDMS 4.0.1 or later.");
		if (!Util.isEmpty(getNoticeType()))
			ISMVocabulary.validateEnumeration(ISMVocabulary.CVE_NOTICE_TYPE, getNoticeType());
		if (!Util.isEmpty(getNoticeReason()) && getNoticeReason().length() > MAX_LENGTH)
			throw new InvalidDDMSException("The noticeReason attribute must be shorter than " + MAX_LENGTH
				+ " characters.");
		if (!Util.isEmpty(getUnregisteredNoticeType()) && getUnregisteredNoticeType().length() > MAX_LENGTH)
			throw new InvalidDDMSException("The unregisteredNoticeType attribute must be shorter than " + MAX_LENGTH
				+ " characters.");
		if (getNoticeDate() != null && !getNoticeDate().getXMLSchemaType().equals(DatatypeConstants.DATE))
			throw new InvalidDDMSException("The noticeDate attribute must be in the xs:date format (YYYY-MM-DD).");
		super.validate(version);
	}

	/**
	 * @see AbstractAttributeGroup#getOutput(boolean, String)
	 */
	public String getOutput(boolean isHTML, String prefix) {
		String localPrefix = Util.getNonNullString(prefix);
		StringBuffer text = new StringBuffer();
		text.append(Resource.buildOutput(isHTML, localPrefix + NOTICE_TYPE_NAME, getNoticeType()));
		text.append(Resource.buildOutput(isHTML, localPrefix + NOTICE_REASON_NAME, getNoticeReason()));
		if (getNoticeDate() != null) {
			text.append(Resource.buildOutput(isHTML, localPrefix + NOTICE_DATE_NAME, getNoticeDate().toXMLFormat()));
		}
		text.append(Resource.buildOutput(isHTML, localPrefix + UNREGISTERED_NOTICE_TYPE_NAME,
			getUnregisteredNoticeType()));
		if (isExternalReference() != null) {
			text.append(Resource.buildOutput(isHTML, localPrefix + EXTERNAL_NOTICE_NAME,
				String.valueOf(isExternalReference())));
		}

		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof NoticeAttributes))
			return (false);
		NoticeAttributes test = (NoticeAttributes) obj;
		return (getNoticeType().equals(test.getNoticeType())
			&& getNoticeReason().equals(test.getNoticeReason())
			&& getUnregisteredNoticeType().equals(test.getUnregisteredNoticeType())
			&& Util.nullEquals(getNoticeDate(), test.getNoticeDate())
			&& Util.nullEquals(isExternalReference(), test.isExternalReference()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = 0;
		result = 7 * result + getNoticeType().hashCode();
		result = 7 * result + getNoticeReason().hashCode();
		result = 7 * result + getUnregisteredNoticeType().hashCode();
		if (getNoticeDate() != null)
			result = 7 * result + getNoticeDate().hashCode();
		if (isExternalReference() != null)
			result = 7 * result + isExternalReference().hashCode();
		return (result);
	}

	/**
	 * Accessor for the noticeType attribute.
	 */
	public String getNoticeType() {
		return (Util.getNonNullString(_noticeType));
	}

	/**
	 * Accessor for the noticeReason attribute.
	 */
	public String getNoticeReason() {
		return (Util.getNonNullString(_noticeReason));
	}

	/**
	 * Accessor for the unregisteredNoticeType attribute.
	 */
	public String getUnregisteredNoticeType() {
		return (Util.getNonNullString(_unregisteredNoticeType));
	}

	/**
	 * Accessor for the externalNotice attribute. This may be null before DDMS 4.1.
	 */
	public Boolean isExternalReference() {
		return (_externalNotice);
	}

	/**
	 * Accessor for the noticeDate attribute. May return null if not set.
	 */
	public XMLGregorianCalendar getNoticeDate() {
		return (_noticeDate == null ? null : getFactory().newXMLGregorianCalendar(_noticeDate.toXMLFormat()));
	}

	/**
	 * Accesor for the datatype factory
	 */
	private static DatatypeFactory getFactory() {
		return (Util.getDataTypeFactory());
	}

	/**
	 * Builder for these attributes.
	 * 
	 * <p>This class does not implement the IBuilder interface, because the behavior of commit() is at odds with the
	 * standard commit() method. As an attribute group, an empty attribute group will always be returned instead of
	 * null.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder implements Serializable {
		private static final long serialVersionUID = 279072341662308051L;
		private Map<String, String> _stringAttributes = new HashMap<String, String>();
		private Boolean _externalNotice;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(NoticeAttributes attributes) {
			setNoticeType(attributes.getNoticeType());
			setNoticeReason(attributes.getNoticeReason());
			if (attributes.getNoticeDate() != null)
				setNoticeDate(attributes.getNoticeDate().toXMLFormat());
			setUnregisteredNoticeType(attributes.getUnregisteredNoticeType());
			setExternalNotice(attributes.isExternalReference());
		}

		/**
		 * Finalizes the data gathered for this builder instance. Will always return an empty instance instead of
		 * a null one.
		 * 
		 * @throws InvalidDDMSException if any required information is missing or malformed
		 */
		public NoticeAttributes commit() throws InvalidDDMSException {
			return (new NoticeAttributes(getNoticeType(), getNoticeReason(), getNoticeDate(),
				getUnregisteredNoticeType(), getExternalNotice()));
		}

		/**
		 * Checks if any values have been provided for this Builder.
		 * 
		 * @return true if every field is empty
		 */
		public boolean isEmpty() {
			boolean isEmpty = true;
			for (String value : getStringAttributes().values()) {
				isEmpty = isEmpty && Util.isEmpty(value);
			}
			return (isEmpty && getExternalNotice() == null);
		}

		/**
		 * Builder accessor for the noticeType attribute
		 */
		public String getNoticeType() {
			return (getStringAttributes().get(NOTICE_TYPE_NAME));
		}

		/**
		 * Builder accessor for the noticeType attribute
		 */
		public void setNoticeType(String noticeType) {
			getStringAttributes().put(NOTICE_TYPE_NAME, noticeType);
		}

		/**
		 * Builder accessor for the noticeReason attribute
		 */
		public String getNoticeReason() {
			return (getStringAttributes().get(NOTICE_REASON_NAME));
		}

		/**
		 * Builder accessor for the noticeReason attribute
		 */
		public void setNoticeReason(String noticeReason) {
			getStringAttributes().put(NOTICE_REASON_NAME, noticeReason);
		}

		/**
		 * Builder accessor for the noticeDate attribute
		 */
		public String getNoticeDate() {
			return (getStringAttributes().get(NOTICE_DATE_NAME));
		}

		/**
		 * Builder accessor for the noticeDate attribute
		 */
		public void setNoticeDate(String noticeDate) {
			getStringAttributes().put(NOTICE_DATE_NAME, noticeDate);
		}

		/**
		 * Builder accessor for the unregisteredNoticeType attribute
		 */
		public String getUnregisteredNoticeType() {
			return (getStringAttributes().get(UNREGISTERED_NOTICE_TYPE_NAME));
		}

		/**
		 * Builder accessor for the unregisteredNoticeType attribute
		 */
		public void setUnregisteredNoticeType(String unregisteredNoticeType) {
			getStringAttributes().put(UNREGISTERED_NOTICE_TYPE_NAME, unregisteredNoticeType);
		}

		/**
		 * Builder accessor for the externalNotice attribute
		 */
		public Boolean getExternalNotice() {
			return (_externalNotice);
		}

		/**
		 * Builder accessor for the externalNotice attribute
		 */
		public void setExternalNotice(Boolean externalNotice) {
			_externalNotice = externalNotice;
		}

		/**
		 * Accessor for the map of attribute names to string values
		 */
		private Map<String, String> getStringAttributes() {
			return (_stringAttributes);
		}
	}
}