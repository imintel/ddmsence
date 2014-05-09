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
package buri.ddmsence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.UnsupportedVersionException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.ddms.extensible.ExtensibleElement;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Top-level base class for all DDMS elements and attributes modeled as Java objects.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p>Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set before
 * the component is used. It is assumed that after the constructor on a component has been called, the component will be
 * well-formed and valid.</p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None. Extending classes may have additional elements.
 * {@table.footer}
 * {@table.header Attributes}
 * 		None. Extending classes may have additional attributes.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The component has a name.|Error|11111}
 * 		{@ddms.rule All child components use the same version of DDMS as this component.|Error|11111}
 * 		{@ddms.rule Warnings from any child components are claimed by this component.|Warning|11111}
 * 		{@ddms.rule Warnings from any security attributes are claimed by this component.|Warning|11111}
 * {@table.footer}
 * 	
 * @author Brian Uri!
 * @since 0.9.b
 */
public abstract class AbstractBaseComponent implements IDDMSComponent {

	private List<ValidationMessage> _warnings = null;
	private Element _element = null;

	/**
	 * Empty constructor
	 */
	protected AbstractBaseComponent() throws InvalidDDMSException {}

	/**
	 * Base constructor
	 * 
	 * @param element the XOM element representing this component
	 */
	protected AbstractBaseComponent(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Will return an empty string if the element is not set, but this cannot occur after instantiation.
	 * 
	 * @see IDDMSComponent#getPrefix()
	 */
	public String getPrefix() {
		return (getXOMElement() == null ? "" : getXOMElement().getNamespacePrefix());
	}

	/**
	 * Will return an empty string if the element is not set, but this cannot occur after instantiation.
	 * 
	 * @see IDDMSComponent#getName()
	 */
	public String getName() {
		return (getXOMElement() == null ? "" : getXOMElement().getLocalName());
	}

	/**
	 * Will return an empty string if the element is not set, but this cannot occur after instantiation.
	 * 
	 * @see IDDMSComponent#getNamespace()
	 */
	public String getNamespace() {
		return (getXOMElement() == null ? "" : getXOMElement().getNamespaceURI());
	}

	/**
	 * Will return an empty string if the element is not set, but this cannot occur after instantiation.
	 * 
	 * @see IDDMSComponent#getQualifiedName()
	 */
	public String getQualifiedName() {
		return (getXOMElement() == null ? "" : getXOMElement().getQualifiedName());
	}

	/**
	 * The base implementation of a DDMS component assumes that there are no security attributes. Components with
	 * attributes should override this.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (null);
	}

	/**
	 * @see IDDMSComponent#getValidationWarnings()
	 */
	public List<ValidationMessage> getValidationWarnings() {
		return (Collections.unmodifiableList(getWarnings()));
	}

	/**
	 * Base case for validation. This method can be overridden for more in-depth validation. It is always assumed that
	 * the subcomponents of a component are already valid.
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSValue("name", getName());
		for (IDDMSComponent nested : getNestedComponents()) {
			if (nested instanceof ExtensibleElement || nested == null)
				continue;
			Util.requireCompatibleVersion(this, nested);
		}
		validateWarnings();
	}

	/**
	 * Base case for warnings. This method can be overridden for more in-depth validation. It is always assumed that the
	 * subcomponents of a component are already valid.
	 */
	protected void validateWarnings() {
		for (IDDMSComponent nested : getNestedComponents()) {
			if (nested == null)
				continue;
			addWarnings(nested.getValidationWarnings(), false);
		}
		if (getSecurityAttributes() != null)
			addWarnings(getSecurityAttributes().getValidationWarnings(), true);
	}

	/**
	 * Adds a warning about a component which is used in a valid manner, but may cause issues with systems that only
	 * process an earlier DDMS version with the same namespace.
	 * 
	 * <p>DDMS 4.0 and 4.1 share the same XML namespace, so it is impossible to tell which DDMS version is employed from
	 * the XML namespace alone. If an XML instance of a metacard employs a new DDMS 4.1 construct (like the new
	 * ntk:Access element in a ddms:metacardInfo element), that XML instance will fail to work in a DDMS 4.0 system.</p>
	 * 
	 * @param component a text description of the component that is being warned about
	 */
	protected void addDdms40Warning(String component) {
		addWarning("The " + component
			+ " in this DDMS component was introduced in DDMS 4.1, and will prevent this XML instance"
			+ " from being understood by DDMS 4.0.1 systems.");
	}

	/**
	 * @see IDDMSComponent#toHTML()
	 */
	public String toHTML() {
		return (getOutput(true, "", ""));
	}

	/**
	 * @see IDDMSComponent#toText()
	 */
	public String toText() {
		return (getOutput(false, "", ""));
	}

	/**
	 * Renders this component as HTML or Text, with an optional prefix to nest it.
	 * 
	 * @param isHTML true for HTML, false for Text.
	 * @param prefix an optional prefix to put on each name.
	 * @param suffix an optional suffix to append to each name, such as an index.
	 * 
	 * @return the HTML or Text representation of this component
	 */
	public abstract String getOutput(boolean isHTML, String prefix, String suffix);

	/**
	 * Accessor for a collection of nested components. A list such as this is useful for bulk actions, such as checking
	 * emptiness, equality, generating hash codes, or applying mass validation.
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		return (Collections.EMPTY_LIST);
	}

	/**
	 * Convenience method to build a meta tag for HTML output or a text line for Text output.
	 * 
	 * @param isHTML true for HTML, false for Text
	 * @param name the name of the name-value pairing (will be escaped in HTML)
	 * @param content the value of the name-value pairing (will be escaped in HTML)
	 * @return a string containing the output
	 */
	public static String buildOutput(boolean isHTML, String name, String content) {
		if (Util.isEmpty(content))
			return ("");
		StringBuffer tag = new StringBuffer();
		tag.append(isHTML ? "<meta name=\"" : "");
		tag.append(isHTML ? Util.xmlEscape(name) : name);
		tag.append(isHTML ? "\" content=\"" : ": ");
		tag.append(isHTML ? Util.xmlEscape(content) : content);
		tag.append(isHTML ? "\" />\n" : "\n");
		return (tag.toString());
	}

	/**
	 * Convenience method to build a meta tag for HTML output or a text line for Text output for a list of multiple DDMS
	 * components.
	 * 
	 * @param isHTML true for HTML, false for Text
	 * @param prefix the first part of the name in the name-value pairing (will be escaped in HTML)
	 * @param contents a list of the values (will be escaped in HTML)
	 * @return a string containing the output
	 */
	protected String buildOutput(boolean isHTML, String prefix, List<?> contents) {
		StringBuffer values = new StringBuffer();
		for (int i = 0; i < contents.size(); i++) {
			Object object = contents.get(i);
			if (object instanceof AbstractBaseComponent) {
				AbstractBaseComponent component = (AbstractBaseComponent) object;
				values.append(component.getOutput(isHTML, prefix, buildIndex(i, contents.size())));
			}
			else if (object instanceof String)
				values.append(buildOutput(isHTML, prefix + buildIndex(i, contents.size()), (String) object));
			else
				values.append(buildOutput(isHTML, prefix + buildIndex(i, contents.size()), String.valueOf(object)));
		}
		return (values.toString());
	}

	/**
	 * Convenience method to construct a naming prefix for use in HTML/Text output
	 * 
	 * @param prefix an optional first part to the prefix
	 * @param token an optional second part to the prefix
	 * @param suffix an optional third part to the prefix
	 * @return a String containing the concatenated values
	 */
	protected String buildPrefix(String prefix, String token, String suffix) {
		return (Util.getNonNullString(prefix) + Util.getNonNullString(token) + Util.getNonNullString(suffix));
	}

	/**
	 * Constructs a braced 1-based index to differentiate multiples in HTML/Text output, based on the 0-based list index
	 * of the item, and the <code>output.indexLevel</code> configurable property. When this property is 0, indices are
	 * never shown. At 1, indices are shown when needed, but hidden when there is only 1 item to display. At 2, indices
	 * are always shown. If the property is set to something else, it defaults to 0.
	 * 
	 * @param index the 0-based index of an item in a list
	 * @param total the total number of items in that list
	 * @return a String containing the index text, if applicable
	 */
	protected String buildIndex(int index, int total) {
		if (total < 1)
			throw new IllegalArgumentException("The total must be at least 1.");
		if (index < 0 || index >= total)
			throw new IllegalArgumentException("The index is not properly bounded between 0 and " + (total - 1));

		String indexLevel = PropertyReader.getProperty("output.indexLevel");
		if ("2".equals(indexLevel))
			return ("[" + (index + 1) + "]");
		if ("1".equals(indexLevel) && (total > 1))
			return ("[" + (index + 1) + "]");
		return ("");
	}

	/**
	 * Will return an empty string if the name is not set, but this cannot occur after instantiation.
	 * 
	 * @see IDDMSComponent#toXML()
	 */
	public String toXML() {
		return (getXOMElement() == null ? "" : getXOMElement().toXML());
	}

	/**
	 * Convenience method to look up an attribute which is in the same namespace as the enclosing element
	 * 
	 * @param name the local name of the attribute
	 * @return attribute value, or an empty string if it does not exist
	 */
	protected String getAttributeValue(String name) {
		return (getAttributeValue(name, getNamespace()));
	}

	/**
	 * Convenience method to look up an attribute
	 * 
	 * @param name the local name of the attribute
	 * @param namespaceURI the namespace of the attribute
	 * @return attribute value, or an empty string if it does not exist
	 */
	protected String getAttributeValue(String name, String namespaceURI) {
		Util.requireValue("name", name);
		String attrValue = getXOMElement().getAttributeValue(name, Util.getNonNullString(namespaceURI));
		return (Util.getNonNullString(attrValue));
	}

	/**
	 * Convenience method to get the first child element with a given name in the same namespace as the parent element
	 * 
	 * @param name the local name to search for
	 * @return the element, or null if it does not exist
	 */
	protected Element getChild(String name) {
		Util.requireValue("name", name);
		return (getXOMElement().getFirstChildElement(name, getNamespace()));
	}

	/**
	 * Convenience method to convert one of the lat/lon fields into a Double. Returns null if the field does not exist.
	 * 
	 * @param element the parent element
	 * @param name the local name of the child
	 * @return a Double, or null if it cannot be created
	 */
	protected static Double getChildTextAsDouble(Element element, String name) {
		Util.requireValue("element", element);
		Util.requireValue("name", name);
		Element childElement = element.getFirstChildElement(name, element.getNamespaceURI());
		if (childElement == null)
			return (null);
		return (Double.valueOf(childElement.getValue()));
	}

	/**
	 * Helper method to validate that a specific version of DDMS (or higher) is being used.
	 * 
	 * @param version the inclusive threshold version
	 * @throws InvalidDDMSException if the version is not high enough
	 */
	protected void requireAtLeastVersion(String version) throws InvalidDDMSException {
		if (!getDDMSVersion().isAtLeast(version))
			throw new InvalidDDMSException("The " + getName() + " element must not be used until DDMS " + version
				+ " or later.");
	}
	
	/**
	 * Helper method to validate that a specific version of DDMS (or lower) is being used.
	 * 
	 * @param version the inclusive threshold version
	 * @throws InvalidDDMSException if the version is too high enough
	 */
	protected void requireAtMostVersion(String version) throws InvalidDDMSException {
		DDMSVersion ceiling = DDMSVersion.getVersionFor(version);
		if (!ceiling.isAtLeast(getDDMSVersion().getVersion()))
			throw new InvalidDDMSException("The " + getName() + " element must not be used after DDMS " + version
				+ ".");
	}

	/**
	 * Returns the most recent compatible DDMSVersion for this component, based on the XML Namespace. Depends on the XOM
	 * Element being set. For DDMS versions that share the same namespace (4.0.1 and 4.1), the newer version is always
	 * returned.
	 * 
	 * @return a version
	 * @throws UnsupportedVersionException if the XML namespace is not one of the supported DDMS namespaces.
	 */
	protected DDMSVersion getDDMSVersion() {
		return (DDMSVersion.getVersionForNamespace(getNamespace()));
	}

	/**
	 * Test for logical equality.
	 * 
	 * <p> The base case tests against the name value and namespaceURI, as well as any child components classified as
	 * "nested components" and any security attributes. Extending classes may require additional rules for equality.
	 * This case automatically includes any nested components or security attributes.</p>
	 * 
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return (true);
		if (!(obj instanceof AbstractBaseComponent) || !(getClass().equals(obj.getClass())))
			return (false);
		AbstractBaseComponent test = (AbstractBaseComponent) obj;
		return (getName().equals(test.getName()) && getNamespace().equals(test.getNamespace())
			&& Util.listEquals(getNestedComponents(), test.getNestedComponents()) && Util.nullEquals(
			getSecurityAttributes(), test.getSecurityAttributes()));
	}

	/**
	 * Returns a hashcode for the component.
	 * 
	 * <p>This automatically includes any nested components or security attributes.</p>
	 * 
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = getName().hashCode();
		result = 7 * result + getNamespace().hashCode();
		for (IDDMSComponent nested : getNestedComponents()) {
			if (nested == null)
				continue;
			result = 7 * result + nested.hashCode();
		}
		if (getSecurityAttributes() != null)
			result = 7 * result + getSecurityAttributes().hashCode();
		return (result);
	}

	/**
	 * Returns the XML representation of the component
	 * 
	 * @see Object#toString()
	 */
	public String toString() {
		return (toXML());
	}

	/**
	 * Can be overridden to change the locator string used in warnings and errors.
	 * 
	 * <p>For components such as Format, there are wrapper elements that are not implemented as Java objects. These
	 * elements should be included in the XPath string used to identify the source of the error.</p>
	 * 
	 * <p>For example, if a ddms:extent element has a warning and the ddms:format element reports it, the locator
	 * information should be "/ddms:format/ddms:Media/ddms:extent" and not the default of "/ddms:format/ddms:extent"</p>
	 * 
	 * @return an empty string, unless overridden.
	 */
	protected String getLocatorSuffix() {
		return ("");
	}

	/**
	 * Convenience method to create a warning and add it to the list of validation warnings.
	 * 
	 * @param text the description text
	 */
	protected void addWarning(String text) {
		getWarnings().add(ValidationMessage.newWarning(text, getQualifiedName() + getLocatorSuffix()));
	}

	/**
	 * Convenience method to add multiple warnings to the list of validation warnings.
	 * 
	 * <p>Child locator information will be prefixed with the parent (this) locator information. This does not overwrite
	 * the original warning -- it creates a new copy.</p>
	 * 
	 * @param warnings the list of validation messages to add
	 * @param forAttributes if true, the locator suffix is not used, because the attributes will be for the topmost
	 *        element (for example, warnings for gml:Polygon's security attributes should not end up with a locator of
	 *        /gml:Polygon/gml:exterior/gml:LinearRing).
	 */
	protected void addWarnings(List<ValidationMessage> warnings, boolean forAttributes) {
		for (ValidationMessage warning : warnings) {
			String newLocator = getQualifiedName() + (forAttributes ? "" : getLocatorSuffix()) + warning.getLocator();
			getWarnings().add(ValidationMessage.newWarning(warning.getText(), newLocator));
		}
	}

	/**
	 * Accessor for the list of validation warnings.
	 * 
	 * <p>This is the private copy that should be manipulated during validation. Lazy initialization.</p>
	 * 
	 * @return an editable list of warnings
	 */
	private List<ValidationMessage> getWarnings() {
		if (_warnings == null)
			_warnings = new ArrayList<ValidationMessage>();
		return (_warnings);
	}

	/**
	 * Accessor for the XOM element representing this component
	 */
	protected Element getXOMElement() {
		return _element;
	}

	/**
	 * Accessor for a copy of the underlying XOM element
	 */
	public Element getXOMElementCopy() {
		return (new Element(_element));
	}

	/**
	 * Accessor for the XOM element representing this component. When the element is set, the component is validated
	 * again with <code>validate</code>.
	 * 
	 * @param element the XOM element to use
	 * @param validateNow whether to validate the component immediately after setting
	 */
	protected void setXOMElement(Element element, boolean validateNow) throws InvalidDDMSException {
		Util.requireDDMSValue("XOM Element", element);
		_element = element;
		if (validateNow)
			validate();
	}
}
