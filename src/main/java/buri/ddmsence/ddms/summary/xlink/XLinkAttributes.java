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
package buri.ddmsence.ddms.summary.xlink;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Element;
import buri.ddmsence.AbstractAttributeGroup;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.Resource;
import buri.ddmsence.ddms.resource.RevisionRecall;
import buri.ddmsence.ddms.resource.TaskID;
import buri.ddmsence.ddms.summary.Link;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Attribute group for the XLINK attributes.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p>
 * This class only models the subset of attributes and values that are employed by the DDMS specification.
 * Determinations about whether an attribute is optional or required depend on the decorated class
 * ({@link Link}, {@link RevisionRecall}, or {@link TaskID}).
 * </p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes (in a ddms:link)}
 * 		{@child.info xlink:type|0..1|11111}
 * 		{@child.info xlink:href|0..1|11111}
 * 		{@child.info xlink:role|0..1|11111}
 * 		{@child.info xlink:title|0..1|11111}
 * 		{@child.info xlink:label|0..1|11111}
 * {@table.footer}
 * {@table.header Attributes (in a ddms:revisionRecall)}
 * 		{@child.info xlink:type|0..1|00011}
 * 		{@child.info xlink:role|0..1|00011}
 * 		{@child.info xlink:title|0..1|00011}
 * 		{@child.info xlink:label|0..1|00011}
 * {@table.footer}
 * {@table.header Attributes (in a ddms:taskID)}
 * 		{@child.info xlink:type|0..1|00011}
 * 		{@child.info xlink:href|0..1|00011}
 * 		{@child.info xlink:role|0..1|00011}
 * 		{@child.info xlink:title|0..1|00011}
 * 		{@child.info xlink:arcrole|0..1|00011}
 * 		{@child.info xlink:show|0..1|00011}
 * 		{@child.info xlink:actuate|0..1|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule If set, xlink:href must be a valid URI.|Error|11111}
 * 		{@ddms.rule If set, xlink:arcrole must be a valid URI.|Error|11111}
 * 		{@ddms.rule If set, xlink:show must be a valid token.|Error|11111}
 * 		{@ddms.rule If set, xlink:actuate must be a valid token.|Error|11111}
 * 		{@ddms.rule If set, xlink:role must be a valid URI.|Error|00011}
 * 		{@ddms.rule If set, xlink:label msut be a valid NCName.|Error|00011}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class XLinkAttributes extends AbstractAttributeGroup {

	private String _type = null;
	private String _href = null;
	private String _role = null;
	private String _title = null;
	private String _label = null;
	private String _arcrole = null;
	private String _show = null;
	private String _actuate = null;

	private static final String TYPE_NAME = "type";
	private static final String HREF_NAME = "href";
	private static final String ROLE_NAME = "role";
	private static final String TITLE_NAME = "title";
	private static final String LABEL_NAME = "label";
	private static final String ARC_ROLE_NAME = "arcrole";
	private static final String SHOW_NAME = "show";
	private static final String ACTUATE_NAME = "actuate";

	private static final String TYPE_LOCATOR = "locator";
	private static final String TYPE_SIMPLE = "simple";
	private static final String TYPE_RESOURCE = "resource";

	private static Set<String> TYPE_TYPES = new HashSet<String>();
	static {
		TYPE_TYPES.add(TYPE_LOCATOR);
		TYPE_TYPES.add(TYPE_SIMPLE);
		TYPE_TYPES.add(TYPE_RESOURCE);
	}
	private static Set<String> SHOW_TYPES = new HashSet<String>();
	static {
		SHOW_TYPES.add("new");
		SHOW_TYPES.add("replace");
		SHOW_TYPES.add("embed");
		SHOW_TYPES.add("other");
		SHOW_TYPES.add("none");
	}

	private static Set<String> ACTUATE_TYPES = new HashSet<String>();
	static {
		ACTUATE_TYPES.add("onLoad");
		ACTUATE_TYPES.add("onRequest");
		ACTUATE_TYPES.add("other");
		ACTUATE_TYPES.add("none");
	}

	/**
	 * Returns a non-null instance of XLink attributes. If the instance passed in is not null, it will be returned.
	 * 
	 * @param xlinkAttributes the attributes to return by default
	 * @return a non-null attributes instance
	 * @throws InvalidDDMSException if there are problems creating the empty attributes instance
	 */
	public static XLinkAttributes getNonNullInstance(XLinkAttributes xlinkAttributes) throws InvalidDDMSException {
		return (xlinkAttributes == null ? new XLinkAttributes() : xlinkAttributes);
	}

	/**
	 * Base constructor
	 * 
	 * @param element the XOM element which is decorated with these attributes.
	 */
	public XLinkAttributes(Element element) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getVersionForNamespace(element.getNamespaceURI());
		setNamespace(version.getXlinkNamespace());
		_type = element.getAttributeValue(TYPE_NAME, getNamespace());
		_href = element.getAttributeValue(HREF_NAME, getNamespace());
		_role = element.getAttributeValue(ROLE_NAME, getNamespace());
		_title = element.getAttributeValue(TITLE_NAME, getNamespace());
		_label = element.getAttributeValue(LABEL_NAME, getNamespace());
		_arcrole = element.getAttributeValue(ARC_ROLE_NAME, getNamespace());
		_show = element.getAttributeValue(SHOW_NAME, getNamespace());
		_actuate = element.getAttributeValue(ACTUATE_NAME, getNamespace());
		validate(DDMSVersion.getVersionForNamespace(element.getNamespaceURI()));
	}

	/**
	 * Constructor which builds from raw data for an unknown type.
	 * 
	 * @throws InvalidDDMSException
	 */
	public XLinkAttributes() throws InvalidDDMSException {
		setNamespace(DDMSVersion.getCurrentVersion().getXlinkNamespace());
		validate(DDMSVersion.getCurrentVersion());
	}

	/**
	 * Constructor which builds from raw data for a resource link.
	 * 
	 * @param role the role attribute
	 * @param title the link title
	 * @param label the name of the link
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public XLinkAttributes(String role, String title, String label) throws InvalidDDMSException {
		setNamespace(DDMSVersion.getCurrentVersion().getXlinkNamespace());
		_type = TYPE_RESOURCE;
		_role = role;
		_title = title;
		_label = label;
		validate(DDMSVersion.getCurrentVersion());
	}

	/**
	 * Constructor which builds from raw data for a locator link.
	 * 
	 * @param href the link href
	 * @param role the role attribute
	 * @param title the link title
	 * @param label the name of the link
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public XLinkAttributes(String href, String role, String title, String label) throws InvalidDDMSException {
		setNamespace(DDMSVersion.getCurrentVersion().getXlinkNamespace());
		_type = TYPE_LOCATOR;
		_href = href;
		_role = role;
		_title = title;
		_label = label;
		validate(DDMSVersion.getCurrentVersion());
	}

	/**
	 * Constructor which builds from raw data for a simple link.
	 * 
	 * @param href the link href
	 * @param role the role attribute
	 * @param title the link title
	 * @param arcrole the arcrole
	 * @param show the show token
	 * @param actuate the actuate token
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public XLinkAttributes(String href, String role, String title, String arcrole, String show, String actuate)
		throws InvalidDDMSException {
		setNamespace(DDMSVersion.getCurrentVersion().getXlinkNamespace());
		_type = TYPE_SIMPLE;
		_href = href;
		_role = role;
		_title = title;
		_arcrole = arcrole;
		_show = show;
		_actuate = actuate;
		validate(DDMSVersion.getCurrentVersion());
	}

	/**
	 * Convenience method to add these attributes onto an existing XOM Element
	 * 
	 * @param element the element to decorate
	 * @throws InvalidDDMSException if the DDMS version of the element is different
	 */
	public void addTo(Element element) throws InvalidDDMSException {
		DDMSVersion elementVersion = DDMSVersion.getVersionForNamespace(element.getNamespaceURI());
		validateCompatibleVersion(elementVersion);
		String xlinkNamespace = elementVersion.getXlinkNamespace();
		String xlinkPrefix = PropertyReader.getPrefix("xlink");

		Util.addAttribute(element, xlinkPrefix, TYPE_NAME, xlinkNamespace, getType());
		Util.addAttribute(element, xlinkPrefix, HREF_NAME, xlinkNamespace, getHref());
		Util.addAttribute(element, xlinkPrefix, ROLE_NAME, xlinkNamespace, getRole());
		Util.addAttribute(element, xlinkPrefix, TITLE_NAME, xlinkNamespace, getTitle());
		Util.addAttribute(element, xlinkPrefix, LABEL_NAME, xlinkNamespace, getLabel());
		Util.addAttribute(element, xlinkPrefix, ARC_ROLE_NAME, xlinkNamespace, getArcrole());
		Util.addAttribute(element, xlinkPrefix, SHOW_NAME, xlinkNamespace, getShow());
		Util.addAttribute(element, xlinkPrefix, ACTUATE_NAME, xlinkNamespace, getActuate());
	}

	/**
	 * Compares the DDMS version of these attributes to another DDMS version
	 * 
	 * @param newParentVersion the version to test
	 * @throws InvalidDDMSException if the versions do not match
	 */
	protected void validateCompatibleVersion(DDMSVersion newParentVersion) throws InvalidDDMSException {
		// No test yet, because every version of DDMS uses the same version of XLink.
	}

	/**
	 * Validates the attribute group.
	 * 
	 * @param version the DDMS version to validate against. This cannot be stored in the attribute group because some
	 *        DDMSVersions have the same attribute XML namespace (e.g. XLink, ISM, NTK, GML after DDMS 2.0).
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	protected void validate(DDMSVersion version) throws InvalidDDMSException {
		if (!Util.isEmpty(getHref()))
			Util.requireDDMSValidURI(getHref());
		if (!Util.isEmpty(getArcrole()))
			Util.requireDDMSValidURI(getArcrole());
		if (!Util.isEmpty(getShow()) && !SHOW_TYPES.contains(getShow()))
			throw new InvalidDDMSException("The show attribute must be one of " + SHOW_TYPES);
		if (!Util.isEmpty(getActuate()) && !ACTUATE_TYPES.contains(getActuate()))
			throw new InvalidDDMSException("The actuate attribute must be one of " + ACTUATE_TYPES);
		if (version.isAtLeast("4.0.1")) {
			if (!Util.isEmpty(getRole()))
				Util.requireDDMSValidURI(getRole());
			if (!Util.isEmpty(getLabel()))
				Util.requireValidNCName(getLabel());
		}
		super.validate(version);
	}

	/**
	 * @see AbstractAttributeGroup#getOutput(boolean, String)
	 */
	public String getOutput(boolean isHTML, String prefix) {
		String localPrefix = Util.getNonNullString(prefix);
		StringBuffer text = new StringBuffer();
		text.append(Resource.buildOutput(isHTML, localPrefix + TYPE_NAME, getType()));
		text.append(Resource.buildOutput(isHTML, localPrefix + HREF_NAME, getHref()));
		text.append(Resource.buildOutput(isHTML, localPrefix + ROLE_NAME, getRole()));
		text.append(Resource.buildOutput(isHTML, localPrefix + TITLE_NAME, getTitle()));
		text.append(Resource.buildOutput(isHTML, localPrefix + LABEL_NAME, getLabel()));
		text.append(Resource.buildOutput(isHTML, localPrefix + ARC_ROLE_NAME, getArcrole()));
		text.append(Resource.buildOutput(isHTML, localPrefix + SHOW_NAME, getShow()));
		text.append(Resource.buildOutput(isHTML, localPrefix + ACTUATE_NAME, getActuate()));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof XLinkAttributes))
			return (false);
		XLinkAttributes test = (XLinkAttributes) obj;
		return (getType().equals(test.getType())
			&& getHref().equals(test.getHref())
			&& getRole().equals(test.getRole())
			&& getTitle().equals(test.getTitle())
			&& getLabel().equals(test.getLabel())
			&& getArcrole().equals(test.getArcrole())
			&& getShow().equals(test.getShow())
			&& getActuate().equals(test.getActuate()));		
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = 0;
		result = 7 * result + getType().hashCode();
		result = 7 * result + getHref().hashCode();
		result = 7 * result + getRole().hashCode();
		result = 7 * result + getTitle().hashCode();
		result = 7 * result + getLabel().hashCode();
		result = 7 * result + getArcrole().hashCode();
		result = 7 * result + getShow().hashCode();
		result = 7 * result + getActuate().hashCode();
		return (result);
	}

	/**
	 * Accessor for the type
	 */
	public String getType() {
		return (Util.getNonNullString(_type));
	}

	/**
	 * Accessor for the href
	 */
	public String getHref() {
		return (Util.getNonNullString(_href));
	}

	/**
	 * Accessor for the role
	 */
	public String getRole() {
		return (Util.getNonNullString(_role));
	}

	/**
	 * Accessor for the title
	 */
	public String getTitle() {
		return (Util.getNonNullString(_title));
	}

	/**
	 * Accessor for the label
	 */
	public String getLabel() {
		return (Util.getNonNullString(_label));
	}

	/**
	 * Accessor for the arcrole
	 */
	public String getArcrole() {
		return (Util.getNonNullString(_arcrole));
	}

	/**
	 * Accessor for the show
	 */
	public String getShow() {
		return (Util.getNonNullString(_show));
	}

	/**
	 * Accessor for the actuate
	 */
	public String getActuate() {
		return (Util.getNonNullString(_actuate));
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
		private static final long serialVersionUID = 6071979027185230870L;
		private String _type;
		private String _href;
		private String _role;
		private String _title;
		private String _label;
		private String _arcrole;
		private String _show;
		private String _actuate;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(XLinkAttributes attributes) {
			setType(attributes.getType());
			setHref(attributes.getHref());
			setRole(attributes.getRole());
			setTitle(attributes.getTitle());
			setLabel(attributes.getLabel());
			setArcrole(attributes.getArcrole());
			setShow(attributes.getShow());
			setActuate(attributes.getActuate());
		}

		/**
		 * Finalizes the data gathered for this builder instance. Will always return an empty instance instead of a null
		 * one.
		 * 
		 * @throws InvalidDDMSException if any required information is missing or malformed
		 */
		public XLinkAttributes commit() throws InvalidDDMSException {
			if (TYPE_LOCATOR.equals(getType()))
				return (new XLinkAttributes(getHref(), getRole(), getTitle(), getLabel()));
			if (TYPE_SIMPLE.equals(getType()))
				return (new XLinkAttributes(getHref(), getRole(), getTitle(), getArcrole(), getShow(), getActuate()));
			if (TYPE_RESOURCE.equals(getType()))
				return (new XLinkAttributes(getRole(), getTitle(), getLabel()));
			return (new XLinkAttributes());
		}

		/**
		 * Checks if any values have been provided for this Builder.
		 * 
		 * @return true if every field is empty
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getType())
				&& Util.isEmpty(getHref())
				&& Util.isEmpty(getRole())
				&& Util.isEmpty(getTitle())
				&& Util.isEmpty(getLabel())
				&& Util.isEmpty(getArcrole())
				&& Util.isEmpty(getShow())
				&& Util.isEmpty(getActuate()));				
		}

		/**
		 * Builder accessor for the type
		 */
		public String getType() {
			return _type;
		}

		/**
		 * Builder accessor for the type
		 */
		public void setType(String type) {
			_type = type;
		}

		/**
		 * Builder accessor for the href
		 */
		public String getHref() {
			return _href;
		}

		/**
		 * Builder accessor for the href
		 */
		public void setHref(String href) {
			_href = href;
		}

		/**
		 * Builder accessor for the role
		 */
		public String getRole() {
			return _role;
		}

		/**
		 * Builder accessor for the role
		 */
		public void setRole(String role) {
			_role = role;
		}

		/**
		 * Builder accessor for the title
		 */
		public String getTitle() {
			return _title;
		}

		/**
		 * Builder accessor for the title
		 */
		public void setTitle(String title) {
			_title = title;
		}

		/**
		 * Builder accessor for the label
		 */
		public String getLabel() {
			return _label;
		}

		/**
		 * Builder accessor for the label
		 */
		public void setLabel(String label) {
			_label = label;
		}

		/**
		 * Builder accessor for the arcrole
		 */
		public String getArcrole() {
			return _arcrole;
		}

		/**
		 * Builder accessor for the arcrole
		 */
		public void setArcrole(String arcrole) {
			_arcrole = arcrole;
		}

		/**
		 * Builder accessor for the show
		 */
		public String getShow() {
			return _show;
		}

		/**
		 * Builder accessor for the show
		 */
		public void setShow(String show) {
			_show = show;
		}

		/**
		 * Builder accessor for the actuate
		 */
		public String getActuate() {
			return _actuate;
		}

		/**
		 * Builder accessor for the actuate
		 */
		public void setActuate(String actuate) {
			_actuate = actuate;
		}
	}
}