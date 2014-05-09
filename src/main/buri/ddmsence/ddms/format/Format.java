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
package buri.ddmsence.ddms.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:format.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		Before DDMS 4.0.1, a format element contains a locally defined Media construct.
 * 		This Media construct is a container for the mimeType,extent, and medium of a resource.
 * 		It exists only inside of a ddms:format parent, so it is not implemented as a Java
 * 		object. Starting in DDMS 4.0.1, the Media wrapper has been removed.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:extent|0..1|11111}
 * 		{@child.info ddms:medium|0..1|11111}
 * 		{@child.info ddms:mimeType|1|11111}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule A ddms:mimeType must exist.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class Format extends AbstractBaseComponent {

	private String _mimeType = null;
	private Extent _extent = null;
	private String _medium = null;

	private static final String MEDIA_NAME = "Media";
	private static final String MIME_TYPE_NAME = "mimeType";
	private static final String MEDIUM_NAME = "medium";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Format(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			Element mediaElement = getMediaElement();
			if (mediaElement != null) {
				Element mimeTypeElement = mediaElement.getFirstChildElement(MIME_TYPE_NAME, getNamespace());
				if (mimeTypeElement != null)
					_mimeType = mimeTypeElement.getValue();
				Element extentElement = mediaElement.getFirstChildElement(Extent.getName(getDDMSVersion()),
					getNamespace());
				if (extentElement != null)
					_extent = new Extent(extentElement);
				Element mediumElement = mediaElement.getFirstChildElement(MEDIUM_NAME, getNamespace());
				if (mediumElement != null)
					_medium = mediumElement.getValue();
			}
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
	 * @param mimeType the mimeType element
	 * @param extent the extent element
	 * @param medium the medium element
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Format(String mimeType, Extent extent, String medium) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(Format.getName(DDMSVersion.getCurrentVersion()), null);
			Element mediaElement = DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? element
				: Util.buildDDMSElement(MEDIA_NAME, null);
			Util.addDDMSChildElement(mediaElement, MIME_TYPE_NAME, mimeType);
			if (extent != null)
				mediaElement.appendChild(extent.getXOMElementCopy());
			Util.addDDMSChildElement(mediaElement, MEDIUM_NAME, medium);

			if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				element.appendChild(mediaElement);

			_mimeType = mimeType;
			_extent = extent;
			_medium = medium;
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
		Util.requireDDMSQName(getXOMElement(), Format.getName(getDDMSVersion()));
		Element mediaElement = getMediaElement();
		Util.requireDDMSValue("Media element", mediaElement);
		Util.requireDDMSValue(MIME_TYPE_NAME, getMimeType());
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getLocatorSuffix()
	 */
	protected String getLocatorSuffix() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "" : ValidationMessage.ELEMENT_PREFIX
			+ getXOMElement().getNamespacePrefix() + ":" + MEDIA_NAME);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		if (!getDDMSVersion().isAtLeast("4.0.1"))
			localPrefix += MEDIA_NAME + ".";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + MIME_TYPE_NAME, getMimeType()));
		if (getExtent() != null)
			text.append(getExtent().getOutput(isHTML, localPrefix, ""));
		text.append(buildOutput(isHTML, localPrefix + MEDIUM_NAME, getMedium()));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getExtent());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof Format))
			return (false);
		Format test = (Format) obj;
		boolean isEqual = getMimeType().equals(test.getMimeType()) 
			&& getMedium().equals(test.getMedium());
		return (isEqual);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getMimeType().hashCode();
		result = 7 * result + getMedium().hashCode();
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
		return ("format");
	}

	/**
	 * Accessor for the element which contains the mimeType, medium, and extent. Before DDMS 4.0.1,
	 * this is a wrapper element called ddms:Media. Starting in DDMS 4.0.1, it is the ddms:format
	 * element itself.
	 */
	private Element getMediaElement() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? getXOMElement() : getChild(MEDIA_NAME));
	}

	/**
	 * Accessor for the mimeType element child text. Will return an empty string if not set, but that cannot occur after
	 * instantiation.
	 */
	public String getMimeType() {
		return (Util.getNonNullString(_mimeType));
	}

	/**
	 * Accessor for the extent
	 */
	public Extent getExtent() {
		return (_extent);
	}

	/**
	 * Convenience accessor for the extent qualifier. Returns an empty string if there is not extent.
	 */
	public String getExtentQualifier() {
		return (getExtent() == null ? "" : getExtent().getQualifier());
	}

	/**
	 * Convenience accessor for the extent value. Returns an empty string if there is not extent.
	 */
	public String getExtentValue() {
		return (getExtent() == null ? "" : getExtent().getValue());
	}

	/**
	 * Accessor for the medium element child text
	 */
	public String getMedium() {
		return (Util.getNonNullString(_medium));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7851044806424206976L;
		private String _mimeType;
		private Extent.Builder _extent;
		private String _medium;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Format format) {
			setMimeType(format.getMimeType());
			if (format.getExtent() != null)
				setExtent(new Extent.Builder(format.getExtent()));
			setMedium(format.getMedium());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Format commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Format(getMimeType(), getExtent().commit(), getMedium()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getMimeType()) && Util.isEmpty(getMedium()) && getExtent().isEmpty());
		}

		/**
		 * Builder accessor for the mimeType element child text.
		 */
		public String getMimeType() {
			return _mimeType;
		}

		/**
		 * Builder accessor for the mimeType element child text.
		 */
		public void setMimeType(String mimeType) {
			_mimeType = mimeType;
		}

		/**
		 * Builder accessor for the mediaExtent element.
		 */
		public Extent.Builder getExtent() {
			if (_extent == null)
				_extent = new Extent.Builder();
			return _extent;
		}

		/**
		 * Builder accessor for the mediaExtent element.
		 */
		public void setExtent(Extent.Builder extent) {
			_extent = extent;
		}

		/**
		 * Builder accessor for the medium element child text.
		 */
		public String getMedium() {
			return _medium;
		}

		/**
		 * Builder accessor for the medium element child text.
		 */
		public void setMedium(String medium) {
			_medium = medium;
		}
	}
}