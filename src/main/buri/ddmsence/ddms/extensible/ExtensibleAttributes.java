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
package buri.ddmsence.ddms.extensible;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import nu.xom.Attribute;
import nu.xom.Element;
import buri.ddmsence.AbstractAttributeGroup;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.Resource;
import buri.ddmsence.ddms.security.ism.NoticeAttributes;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.ddms.summary.Category;
import buri.ddmsence.ddms.summary.Keyword;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Attribute group representing the xs:anyAttribute tag which appears on various DDMS components.
 * <br /><br />
 * {@ddms.versions 11111}
 *  
 * <p>No validation or processing of any kind is performed by DDMSence on extensible attributes, other than the base
 * validation used when loading attributes from an XML file, and a check to confirm that extensible attributes do not
 * collide with existing attributes. This class merely exposes a <code>getAttributes()</code> method which returns a
 * read-only List of XOM Attributes that can be manipulated in business-specific ways.</p>
 * 
 * <p>For example, this ddms:Keyword would have an ExtensibleAttributes instance containing 2 attributes (assuming that
 * the "opensearch" namespace was defined earlier in the file):</p>
 * 
 * <ul><code> &lt;ddms:Keyword ddms:value="xml" opensearch:relevance="95" opensearch:confidence="82" /&gt; </code></ul>
 * 
 * <p>XOM attributes can be created as follows:</p>
 * 
 * <ul><code> Attribute attribute = new Attribute("opensearch:relevance", "http://opensearch.namespace/", "95");<br />
 * Attribute attribute = new Attribute("opensearch:confidence", "http://opensearch.namespace/", "82"); </code></ul>
 * 
 * <p>The DDMS documentation does not provide sample HTML/Text output for extensible attributes, so the following
 * approach is used. In general, the HTML/Text output of extensible attributes will be prefixed with the name of the
 * element being marked. For example:</p>
 * 
 * <ul><code> keyword opensearch:relevance: 95<br /> keyword opensearch:confidence: 82<br />
 * &lt;meta name="subjectCoverage.Subject.keyword.opensearch.relevance" content="95" /&gt;<br />
 * &lt;meta name="subjectCoverage.Subject.keyword.opensearch.confidence" content="82" /&gt;<br />
 * </code></ul>
 * 
 * <p>Details about the XOM Attribute class can be found at:
 * <i>http://www.xom.nu/apidocs/index.html?nu/xom/Attribute.html</i></p>
 * 
 * {@table.header History}
 * 		<p>In DDMS 2.0, this attribute group can only decorate {@link buri.ddmsence.ddms.resource.Organization},
 * 		{@link buri.ddmsence.ddms.resource.Person}, {@link buri.ddmsence.ddms.resource.Service}, or the {@link Resource}.</p>
 * 		<p>In DDMS 3.0 and 3.1, this attribute group can decorate {@link buri.ddmsence.ddms.resource.Organization},
 * 		{@link buri.ddmsence.ddms.resource.Person}, {@link buri.ddmsence.ddms.resource.Service},
 * 		{@link buri.ddmsence.ddms.resource.Unknown}, {@link Keyword}, {@link Category}, or the {@link Resource} itself.</p>
 * 		<p>In DDMS 4.0.1 and 4.1, this attribute group was removed from {@link buri.ddmsence.ddms.resource.Person}.</p>
 * 		<p>In DDMS 5.0, this attribute group was also removed from {@link buri.ddmsence.ddms.resource.Organization} and
 * 		{@link Resource}. It is anticipated that all remaining extension points will be deprecated in the next release.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info any:&lt;<i>extensibleAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		Extensible attributes are not validated by DDMSence.
 * {@table.footer}

 * 
 * @author Brian Uri!
 * @since 1.1.0
 */
public final class ExtensibleAttributes extends AbstractAttributeGroup {

	private List<Attribute> _attributes = null;

	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	private final Set<QName> RESERVED_RESOURCE_NAMES = new HashSet<QName>();

	/**
	 * Returns a non-null instance of extensible attributes. If the instance passed in is not null, it will be returned.
	 * 
	 * @param extensibleAttributes the attributes to return by default
	 * @return a non-null attributes instance
	 * @throws InvalidDDMSException if there are problems creating the empty attributes instance
	 */
	public static ExtensibleAttributes getNonNullInstance(ExtensibleAttributes extensibleAttributes)
		throws InvalidDDMSException {
		return (extensibleAttributes == null ? new ExtensibleAttributes((List<Attribute>) null) : extensibleAttributes);
	}

	/**
	 * Base constructor
	 * 
	 * <p>Will only load attributes from a different namespace than DDMS (##other)
	 * and will also skip any Resource attributes that are reserved.</p>
	 * 
	 * @param element the XOM element which is decorated with these attributes.
	 */
	public ExtensibleAttributes(Element element) throws InvalidDDMSException {
		buildReservedNames(element.getNamespaceURI());
		DDMSVersion version = DDMSVersion.getVersionForNamespace(element.getNamespaceURI());

		_attributes = new ArrayList<Attribute>();
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute attribute = element.getAttribute(i);
			// Skip xsi: attributes.
			if (attribute.getNamespaceURI().equals(XSI_NAMESPACE))
				continue;
			// Skip ddms: attributes.
			if (element.getNamespaceURI().equals(attribute.getNamespaceURI()))
				continue;
			// Skip reserved ISM attributes on Resource and Category
			if (Resource.getName(version).equals(element.getLocalName())
				|| Category.getName(version).equals(element.getLocalName())
				|| Keyword.getName(version).equals(element.getLocalName())) {
				QName testName = new QName(attribute.getNamespaceURI(), attribute.getLocalName(),
					attribute.getNamespacePrefix());
				if (RESERVED_RESOURCE_NAMES.contains(testName))
					continue;
			}
			_attributes.add(attribute);
		}
		validate(version);
	}

	/**
	 * Constructor which builds from raw data. Because the parent is not known at this time, will accept
	 * all attributes. The method, addTo() will confirm that the names do not clash with existing or reserved
	 * names on the element.
	 * 
	 * @param attributes a list of extensible attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ExtensibleAttributes(List<Attribute> attributes) throws InvalidDDMSException {
		if (attributes == null)
			attributes = Collections.emptyList();
		_attributes = new ArrayList<Attribute>(attributes);
		validate(DDMSVersion.getCurrentVersion());
	}

	/**
	 * Compiles lists of attribute names which should be ignored when creating extensible attributes. In most cases,
	 * this is easy to determine, because namespace="##other" forces all extensible attributes to be in a non-DDMS
	 * namespace, so the Resource is the only element that might encounter collisions (it has ISM attributes that
	 * should be ignored).
	 * 
	 * @param parentNamespace the namespace of the element which owns these attributes
	 */
	private void buildReservedNames(String parentNamespace) {
		DDMSVersion version = DDMSVersion.getVersionForNamespace(parentNamespace);
		RESERVED_RESOURCE_NAMES.clear();
		String ismPrefix = PropertyReader.getPrefix("ism");
		String ntkPrefix = PropertyReader.getPrefix("ntk");
		for (String reservedName : Resource.NON_EXTENSIBLE_NAMES) {
			RESERVED_RESOURCE_NAMES.add(new QName(version.getIsmNamespace(), reservedName, ismPrefix));
		}
		for (String reservedName : SecurityAttributes.NON_EXTENSIBLE_NAMES) {
			RESERVED_RESOURCE_NAMES.add(new QName(version.getIsmNamespace(), reservedName, ismPrefix));
		}
		if (version.isAtLeast("4.0.1")) {
			for (String reservedName : NoticeAttributes.NON_EXTENSIBLE_NAMES) {
				RESERVED_RESOURCE_NAMES.add(new QName(version.getIsmNamespace(), reservedName, ismPrefix));
			}
			RESERVED_RESOURCE_NAMES.add(new QName(version.getNtkNamespace(), Resource.DES_VERSION_NAME, ntkPrefix));
		}
	}

	/**
	 * Convenience method to add these attributes onto an existing XOM Element
	 * 
	 * @param element the element to decorate
	 * @throws InvalidDDMSException if the attribute already exists
	 */
	public void addTo(Element element) throws InvalidDDMSException {
		for (Attribute attribute : getAttributes()) {
			if (element.getAttribute(attribute.getLocalName(), attribute.getNamespaceURI()) != null)
				throw new InvalidDDMSException("The extensible attribute with the name, "
					+ attribute.getQualifiedName() + " conflicts with a pre-existing attribute on the element.");
			element.addAttribute(attribute);
		}
	}

	/**
	 * Checks if any attributes have been set.
	 * 
	 * @return true if no attributes have values, false otherwise
	 */
	public boolean isEmpty() {
		return (getAttributes().isEmpty());
	}

	/**
	 * Currently, no further validation is performed.
	 * 
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	protected void validate(DDMSVersion version) throws InvalidDDMSException {
		super.validate(version);
	}

	/**
	 * @see AbstractAttributeGroup#getOutput(boolean, String)
	 */
	public String getOutput(boolean isHTML, String prefix) {
		String localPrefix = Util.getNonNullString(prefix);
		StringBuffer text = new StringBuffer();
		for (Attribute attribute : getAttributes()) {
			text.append(Resource.buildOutput(isHTML, localPrefix + attribute.getNamespacePrefix() + "."
				+ attribute.getLocalName(), attribute.getValue()));
		}
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ExtensibleAttributes))
			return (false);
		ExtensibleAttributes test = (ExtensibleAttributes) obj;
		// XOM Attribute has no logical equality. Must compare by hand.
		if (getAttributes().size() != test.getAttributes().size())
			return (false);
		for (int i = 0; i < getAttributes().size(); i++) {
			Attribute attr1 = getAttributes().get(i);
			Attribute attr2 = test.getAttributes().get(i);
			if (!attr1.getLocalName().equals(attr2.getLocalName())
				|| !attr1.getNamespaceURI().equals(attr2.getNamespaceURI()))
				return (false);
		}
		return (true);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = 0;
		// XOM Attribute has no logical equality. Must calculate by hand.
		for (Attribute attribute : getAttributes()) {
			result = 7 * result + attribute.getLocalName().hashCode();
			result = 7 * result + attribute.getNamespaceURI().hashCode();
		}
		return (result);
	}

	/**
	 * Accessor for the attributes. Returns a copy.
	 */
	public List<Attribute> getAttributes() {
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute attribute : _attributes) {
			attributes.add(new Attribute(attribute));
		}
		return (Collections.unmodifiableList(attributes));
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
	 * @since 1.8.0
	 */
	public static class Builder implements Serializable {
		private static final long serialVersionUID = 1257270526054778197L;
		private List<ExtensibleAttributes.AttributeBuilder> _attributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ExtensibleAttributes attributes) {
			for (Attribute attribute : attributes.getAttributes()) {
				getAttributes().add(new ExtensibleAttributes.AttributeBuilder(attribute));
			}
		}

		/**
		 * Finalizes the data gathered for this builder instance. Will always return an empty instance instead of
		 * a null one.
		 * 
		 * @throws InvalidDDMSException if any required information is missing or malformed
		 */
		public ExtensibleAttributes commit() throws InvalidDDMSException {
			List<Attribute> attributes = new ArrayList<Attribute>();
			for (ExtensibleAttributes.AttributeBuilder builder : getAttributes()) {
				Attribute attr = builder.commit();
				if (attr != null)
					attributes.add(attr);
			}
			return (new ExtensibleAttributes(attributes));
		}

		/**
		 * Checks if any values have been provided for this Builder.
		 * 
		 * @return true if every field is empty
		 */
		public boolean isEmpty() {
			return (getAttributes().isEmpty());
		}

		/**
		 * Builder accessor for the attributes
		 */
		public List<ExtensibleAttributes.AttributeBuilder> getAttributes() {
			if (_attributes == null)
				_attributes = new LazyList(ExtensibleAttributes.AttributeBuilder.class);
			return _attributes;
		}
	}

	/**
	 * Builder for a XOM attribute.
	 * 
	 * <p>This builder is implemented because the XOM attribute does not have a no-arg constructor which can be hooked
	 * into a LazyList. Because the Builder returns a XOM attribute instead of an IDDMSComponent, it does not officially
	 * implement the IBuilder interface.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.9.0
	 */
	public static class AttributeBuilder implements Serializable {
		private static final long serialVersionUID = -5102193614065692204L;
		private String _name;
		private String _uri;
		private String _value;
		private Attribute.Type _type;

		/**
		 * Empty constructor
		 */
		public AttributeBuilder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public AttributeBuilder(Attribute attribute) {
			setName(attribute.getQualifiedName());
			setUri(attribute.getNamespaceURI());
			setValue(attribute.getValue());
			setType(attribute.getType());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Attribute commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Attribute(getName(), getUri(), getValue(), getType()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getName()) && Util.isEmpty(getUri()) && Util.isEmpty(getValue()) && getType() == null);
		}

		/**
		 * Builder accessor for the name
		 */
		public String getName() {
			return _name;
		}

		/**
		 * Builder accessor for the name
		 */
		public void setName(String name) {
			_name = name;
		}

		/**
		 * Builder accessor for the uri
		 */
		public String getUri() {
			return _uri;
		}

		/**
		 * Builder accessor for the uri
		 */
		public void setUri(String uri) {
			_uri = uri;
		}

		/**
		 * Builder accessor for the value
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the value
		 */
		public void setValue(String value) {
			_value = value;
		}

		/**
		 * Builder accessor for the type
		 */
		public Attribute.Type getType() {
			return _type;
		}

		/**
		 * Builder accessor for the type
		 */
		public void setType(Attribute.Type type) {
			_type = type;
		}
	}
}