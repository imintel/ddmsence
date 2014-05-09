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
package buri.ddmsence.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import net.sf.saxon.om.Name10Checker;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.ITspiAddress;
import buri.ddmsence.ddms.ITspiShape;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.Notice;
import buri.ddmsence.ddms.security.ntk.Access;
import buri.ddmsence.ddms.summary.gml.Point;
import buri.ddmsence.ddms.summary.gml.Polygon;

/**
 * A collection of static utility methods.
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class Util {

	private static XSLTransform _schematronIncludeTransform;
	private static XSLTransform _schematronAbstractTransform;
	private static Map<String, XSLTransform> _schematronSvrlTransforms = new HashMap<String, XSLTransform>();

	private static final String PROP_TRANSFORM_FACTORY = "javax.xml.transform.TransformerFactory";
	private static final LinkedHashMap<String, String> XML_SPECIAL_CHARS = new LinkedHashMap<String, String>();
	static {
		XML_SPECIAL_CHARS.put("&", "&amp;");
		XML_SPECIAL_CHARS.put("\"", "&quot;");
		XML_SPECIAL_CHARS.put("'", "&apos;");
		XML_SPECIAL_CHARS.put("<", "&lt;");
		XML_SPECIAL_CHARS.put(">", "&gt;");
	}

	private static final String DDMS_DATE_HOUR_MIN_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}(Z|[\\-\\+][0-9]{2}:[0-9]{2})?";

	private static Set<QName> DATE_DATATYPES = new HashSet<QName>();
	static {
		DATE_DATATYPES.add(DatatypeConstants.DATE);
		DATE_DATATYPES.add(DatatypeConstants.DATETIME);
		DATE_DATATYPES.add(DatatypeConstants.GYEARMONTH);
		DATE_DATATYPES.add(DatatypeConstants.GYEAR);
	}

	private static DatatypeFactory _factory;
	static {
		try {
			_factory = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Could not load DatatypeFactory for date conversion.", e);
		}
	}

	/**
	 * Private to prevent instantiation.
	 */
	private Util() {}

	/**
	 * Accesor for the datatype factory
	 */
	public static DatatypeFactory getDataTypeFactory() {
		return (_factory);
	}

	/**
	 * Returns an empty string in place of a null one.
	 * 
	 * @param string the string to convert, if null
	 * @return an empty string if the string is null, or the string untouched
	 */
	public static String getNonNullString(String string) {
		return (string == null ? "" : string);
	}

	/**
	 * Helper method to convert an xs:NMTOKENS data type into a List of Strings.
	 * 
	 * <p>
	 * The number of items returned is based on the normalization of the whitespace first. So, an xs:list defined as
	 * "a   b" will return a List of 2 Strings ("a", "b"), and not a List of 4 String ("a", "", "", "b")
	 * </p>
	 * 
	 * @param value the xs:list style String to parse
	 * @return a List (never null)
	 */
	public static List<String> getXsListAsList(String value) {
		if (Util.isEmpty(value))
			return Collections.emptyList();
		String[] tokens = value.split(" ");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++) {
			if (!isEmpty(tokens[i]))
				list.add(tokens[i]);
		}
		return (list);
	}

	/**
	 * Converts a list of objects into a space-delimited xs:list, using the object's toString() implementation
	 * 
	 * @param list the list to convert
	 * @return a space-delimited string, or empty string if the list was empty.
	 */
	public static String getXsList(List<?> list) {
		if (list == null)
			return ("");
		StringBuffer buffer = new StringBuffer();
		for (Object string : list) {
			buffer.append(string).append(" ");
		}
		return (buffer.toString().trim());
	}

	/**
	 * Returns an int value for a boolean, for use in a hashCode function.
	 * 
	 * @param b the boolean
	 * @return 1 for true and 0 for false
	 */
	public static int booleanHashCode(boolean b) {
		return (b ? 1 : 0);
	}

	/**
	 * Checks if a String value is empty. An empty string is defined as one that is null, contains only whitespace, or
	 * has length 0.
	 * 
	 * @param value the value to check.
	 * @return a boolean, true if the value is null or zero-length, false otherwise
	 */
	public static boolean isEmpty(String value) {
		return (value == null || value.trim().length() == 0);
	}

	/**
	 * Checks if all of the entries in a list of Strings is empty or null.
	 * 
	 * @param list the list containing strings
	 * @return true if the list only has null or empty values
	 */
	public static boolean containsOnlyEmptyValues(List<String> list) {
		if (list != null) {
			int emptyCount = 0;
			for (String value : list) {
				if (isEmpty(value))
					emptyCount++;
			}
			return (list.size() == emptyCount);
		}
		return (false);
	}

	/**
	 * Gets the child text of the first child element matching the name in the DDMS namespace.
	 * 
	 * @param parent the parent element
	 * @param name the name of the child element
	 * @return the child text of the first discovered child element
	 */
	public static String getFirstDDMSChildValue(Element parent, String name) {
		Util.requireValue("parent element", parent);
		Util.requireValue("child name", name);
		if (!DDMSVersion.isSupportedDDMSNamespace(parent.getNamespaceURI()))
			throw new IllegalArgumentException("This method should only be called on an element in the DDMS namespace.");
		Element child = parent.getFirstChildElement(name, parent.getNamespaceURI());
		return (child == null ? "" : child.getValue());
	}

	/**
	 * Gets the child text of any child elements in the DDMS namespace and returns them as a list.
	 * 
	 * @param parent the parent element
	 * @param name the name of the child element
	 * @return a List of strings, where each string is child text of matching elements
	 */
	public static List<String> getDDMSChildValues(Element parent, String name) {
		Util.requireValue("parent element", parent);
		Util.requireValue("child name", name);
		if (!DDMSVersion.isSupportedDDMSNamespace(parent.getNamespaceURI()))
			throw new IllegalArgumentException("This method should only be called on an element in the DDMS namespace.");
		List<String> childTexts = new ArrayList<String>();
		Elements childElements = parent.getChildElements(name, parent.getNamespaceURI());
		for (int i = 0; i < childElements.size(); i++) {
			childTexts.add(childElements.get(i).getValue());
		}
		return (childTexts);
	}

	/**
	 * Asserts that a value required for DDMS is not null or empty.
	 * 
	 * @param description a descriptive name of the value
	 * @param value the value to check
	 * @throws InvalidDDMSException if the value is null or empty
	 */
	public static void requireDDMSValue(String description, Object value) throws InvalidDDMSException {
		if (value == null || (value instanceof String && isEmpty((String) value)))
			throw new InvalidDDMSException(description + " must exist.");
	}

	/**
	 * Asserts that a date format is one of the 5 types accepted by DDMS.
	 * 
	 * @param date the date in its raw XML format
	 * @param ddmsNamespace the DDMS namespace of this date (DDM 4.0.1 and earlier only support 4 types).
	 * @throws InvalidDDMSException if the value is invalid. Does nothing if value is null.
	 */
	public static void requireDDMSDateFormat(String date, String ddmsNamespace) throws InvalidDDMSException {
		DDMSVersion version = DDMSVersion.getVersionForNamespace(ddmsNamespace);

		if (version.isAtLeast("4.1") && Pattern.matches(DDMS_DATE_HOUR_MIN_PATTERN, date))
			return;

		boolean isXsdType = false;
		try {
			XMLGregorianCalendar calendar = getDataTypeFactory().newXMLGregorianCalendar(date);
			isXsdType = DATE_DATATYPES.contains(calendar.getXMLSchemaType());
		}
		catch (IllegalArgumentException e) {
			// Fall-through
		}
		if (!isXsdType) {
			String message = "The date datatype must be one of " + DATE_DATATYPES;
			if (version.isAtLeast("4.1"))
				message += " or ddms:DateHourMinType";
			throw new InvalidDDMSException(message);
		}
	}

	/**
	 * Asserts that the qualified name of an element matches the expected name and a supported version of the
	 * DDMS XML namespace
	 * 
	 * @param element the element to check
	 * @param localName the local name to compare to
	 * @throws InvalidDDMSException if the name is incorrect
	 */
	public static void requireDDMSQName(Element element, String localName) throws InvalidDDMSException {
		Util.requireValue("element", element);
		Util.requireValue("local name", localName);
		if (!localName.equals(element.getLocalName())
			|| !DDMSVersion.isSupportedDDMSNamespace(element.getNamespaceURI())) {
			throw new InvalidDDMSException("Unexpected namespace URI and local name encountered: "
				+ element.getQualifiedName());
		}
	}

	/**
	 * Asserts that the qualified name of an element matches the expected name and namespace URI
	 * 
	 * @param element the element to check
	 * @param namespaceURI the namespace to check
	 * @param localName the local name to compare to
	 * @throws IllegalArgumentException if the name is incorrect
	 */
	public static void requireQName(Element element, String namespaceURI, String localName) throws InvalidDDMSException {
		Util.requireValue("element", element);
		Util.requireValue("local name", localName);
		if (namespaceURI == null)
			namespaceURI = "";
		if (!localName.equals(element.getLocalName()) || !namespaceURI.equals(element.getNamespaceURI())) {
			throw new InvalidDDMSException("Unexpected namespace URI and local name encountered: "
				+ element.getQualifiedName());
		}
	}

	/**
	 * Asserts that a value required, for general cases.
	 * 
	 * @param description a descriptive name of the value
	 * @param value the value to check
	 * @throws IllegalArgumentException if the value is null or empty
	 */
	public static void requireValue(String description, Object value) {
		if (value == null || (value instanceof String && isEmpty((String) value)))
			throw new IllegalArgumentException(description + " must exist.");
	}

	/**
	 * Checks that the number of child elements with the given name in the same namespace as the parent are bounded.
	 * 
	 * @param parent the parent element
	 * @param childName the local name of the child
	 * @param lowBound the lowest value the number can be
	 * @param highBound the highest value the number can be
	 * @throws InvalidDDMSException if the number is out of bounds
	 */
	public static void requireBoundedChildCount(Element parent, String childName, int lowBound, int highBound)
		throws InvalidDDMSException {
		Util.requireValue("parent element", parent);
		Util.requireValue("child name", childName);
		int childCount = parent.getChildElements(childName, parent.getNamespaceURI()).size();
		if (!isBounded(childCount, lowBound, highBound)) {
			StringBuffer error = new StringBuffer();
			if (lowBound == highBound) {
				error.append("Exactly ").append(highBound).append(" ").append(childName).append(" element");
				if (highBound != 1)
					error.append("s");
				error.append(" must exist.");
			}
			else if (lowBound == 0) {
				error.append("No more than ").append(highBound).append(" ").append(childName).append(" element");
				if (highBound != 1)
					error.append("s");
				error.append(" must exist.");
			}
			else {
				error.append("The number of ").append(childName).append(" elements must be between ").append(lowBound).append(
					" and ").append(highBound).append(".");
			}
			throw new InvalidDDMSException(error.toString());
		}
	}

	/**
	 * Validates that a list of strings contains NCNames. This method uses the built-in Verifier in XOM by attempting to
	 * create a new Element with the test string as a local name (Local names must be NCNames).
	 * 
	 * @param names a list of names to check
	 * @throws InvalidDDMSException if any name is not an NCName.
	 */
	public static void requireValidNCNames(List<String> names) throws InvalidDDMSException {
		if (names == null)
			names = Collections.emptyList();
		for (String name : names) {
			requireValidNCName(name);
		}
	}

	/**
	 * Validates that a child component has a compatible DDMS version as the parent.
	 * 
	 * @param parent the parent component
	 * @param child the child component
	 * @throws InvalidDDMSException if
	 */
	public static void requireCompatibleVersion(IDDMSComponent parent, IDDMSComponent child)
		throws InvalidDDMSException {
		Util.requireValue("parent", parent);
		Util.requireValue("child", child);
		// Cover acceptable case where parent (e.g. BoundingGeometry) has different XML namespace than child.
		String parentNamespace = parent.getNamespace();
		if (child instanceof Polygon || child instanceof Point) {
			parentNamespace = DDMSVersion.getVersionForNamespace(parentNamespace).getGmlNamespace();
		}
		if (child instanceof Access) {
			parentNamespace = DDMSVersion.getVersionForNamespace(parentNamespace).getNtkNamespace();
		}
		if (child instanceof Notice) {
			parentNamespace = DDMSVersion.getVersionForNamespace(parentNamespace).getIsmNamespace();
		}
		if (child instanceof ITspiAddress || child instanceof ITspiShape) {
			parentNamespace = DDMSVersion.getVersionForNamespace(parentNamespace).getTspiNamespace();
		}
		String childNamespace = child.getNamespace();
		if (!parentNamespace.equals(childNamespace)) {
			throw new InvalidDDMSException("A child component, " + child.getQualifiedName()
				+ ", is using a different version of DDMS from its parent.");
		}
	}

	/**
	 * Validates that a string is an NCName. This method relies on Saxon's library
	 * methods.
	 * 
	 * @param name the name to check
	 * @throws InvalidDDMSException if the name is not an NCName.
	 */
	public static void requireValidNCName(String name) throws InvalidDDMSException {
		if (!(new Name10Checker().isValidNCName(getNonNullString(name))))
			throw new InvalidDDMSException("\"" + name + "\" is not a valid NCName.");
	}

	/**
	 * Validates that a string is an NMTOKEN. This method relies on Saxon's library
	 * methods.
	 * 
	 * @param name the name to check
	 * @throws InvalidDDMSException if the name is not an NMTOKEN.
	 */
	public static void requireValidNMToken(String name) throws InvalidDDMSException {
		if (!(new Name10Checker().isValidNmtoken(getNonNullString(name))))
			throw new InvalidDDMSException("\"" + name + "\" is not a valid NMTOKEN.");
	}

	/**
	 * Checks that a string is a valid URI.
	 * 
	 * @param uri the string to test
	 * @throws InvalidDDMSException if the string cannot be built into a URI
	 */
	public static void requireDDMSValidURI(String uri) throws InvalidDDMSException {
		Util.requireValue("uri", uri);
		try {
			new URI(uri);
		}
		catch (URISyntaxException e) {
			throw new InvalidDDMSException(e);
		}
	}

	/**
	 * Validates a longitude value
	 * 
	 * @param value the value to test
	 * @throws InvalidDDMSException
	 */
	public static void requireValidLongitude(Double value) throws InvalidDDMSException {
		if (value == null || (new Double(-180)).compareTo(value) > 0 || (new Double(180)).compareTo(value) < 0)
			throw new InvalidDDMSException("A longitude value must be between -180 and 180 degrees: " + value);
	}

	/**
	 * Validates a latitude value
	 * 
	 * @param value the value to test
	 * @throws InvalidDDMSException
	 */
	public static void requireValidLatitude(Double value) throws InvalidDDMSException {
		if (value == null || (new Double(-90)).compareTo(value) > 0 || (new Double(90)).compareTo(value) < 0)
			throw new InvalidDDMSException("A latitude value must be between -90 and 90 degrees: " + value);
	}

	/**
	 * Checks that a number is between two values, inclusive
	 * 
	 * @param testCount the number to evaluate
	 * @param lowBound the lowest value the number can be
	 * @param highBound the highest value the number can be
	 * @return true if the number is bounded, false otherwise
	 * @throws IllegalArgumentException if the range is invalid.
	 */
	public static boolean isBounded(int testCount, int lowBound, int highBound) {
		if (lowBound > highBound)
			throw new IllegalArgumentException("Invalid number range: " + lowBound + " to " + highBound);
		return (testCount >= lowBound && testCount <= highBound);
	}

	/**
	 * Checks if two lists of Objects are identical. Returns true if the lists are the same length and each indexed
	 * string also exists at the same index in the other list.
	 * 
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return true if the lists are of equal size and contain the same objects, false otherwise.
	 * @throws IllegalArgumentException if one of the lists is null
	 */
	public static boolean listEquals(List<?> list1, List<?> list2) {
		if (list1 == null || list2 == null)
			throw new IllegalArgumentException("Null lists cannot be compared.");
		if (list1 == list2)
			return (true);
		if (list1.size() != list2.size())
			return (false);
		for (int i = 0; i < list1.size(); i++) {
			Object value1 = list1.get(i);
			Object value2 = list2.get(i);
			if (!nullEquals(value1, value2))
				return (false);
		}
		return (true);
	}

	/**
	 * Checks object equality when the objects could possible be null.
	 * 
	 * @param obj1 the first object
	 * @param obj2 the second object
	 * @return true if both objects are null or obj1 equals obj2, false otherwise
	 */
	public static boolean nullEquals(Object obj1, Object obj2) {
		return (obj1 == null ? obj2 == null : obj1.equals(obj2));
	}

	/**
	 * Replaces XML special characters - '&', '<', '>', '\'', '"'
	 * 
	 * @param input the string to escape.
	 * @return escaped String
	 */
	public static String xmlEscape(String input) {
		if (input != null) {
			for (Iterator<String> iterator = XML_SPECIAL_CHARS.keySet().iterator(); iterator.hasNext();) {
				String pattern = iterator.next();
				input = Pattern.compile(pattern).matcher(input).replaceAll((String) XML_SPECIAL_CHARS.get(pattern));
			}
		}
		return input;
	}

	/**
	 * Capitalizes the first letter of a String. Silently does nothing if the string is null, empty, or not a letter.
	 * 
	 * @param string the string to capitalize
	 * @return the capitalized string
	 */
	public static String capitalize(String string) {
		if (isEmpty(string))
			return (string);
		if (string.length() == 1)
			return (string.toUpperCase());
		return (string.substring(0, 1).toUpperCase() + string.substring(1, string.length()));
	}

	/**
	 * Helper method to add a ddms attribute to an element. Will not add the attribute if the value
	 * is empty or null. This method uses the DDMS namespace defined with DDMSVersion.getCurrentVersion().
	 * 
	 * @param element the element to decorate
	 * @param attributeName the name of the attribute (will be within the DDMS namespace)
	 * @param attributeValue the value of the attribute
	 */
	public static void addDDMSAttribute(Element element, String attributeName, String attributeValue) {
		addAttribute(element, PropertyReader.getPrefix("ddms"), attributeName,
			DDMSVersion.getCurrentVersion().getNamespace(), attributeValue);
	}

	/**
	 * Helper method to add an attribute to an element. Will not add the attribute if the value
	 * is empty or null.
	 * 
	 * @param element the element to decorate
	 * @param prefix the prefix to use (without a trailing colon)
	 * @param attributeName the name of the attribute
	 * @param namespaceURI the namespace this attribute is in
	 * @param attributeValue the value of the attribute
	 */
	public static void addAttribute(Element element, String prefix, String attributeName, String namespaceURI,
		String attributeValue) {
		if (!Util.isEmpty(attributeValue))
			element.addAttribute(Util.buildAttribute(prefix, attributeName, namespaceURI, attributeValue));
	}

	/**
	 * Helper method to add a ddms child element to an element. Will not add if the value
	 * is empty or null.
	 * 
	 * @param element the element to decorate
	 * @param childName the name of the child (will be within the DDMS namespace)
	 * @param childValue the value of the attribute
	 */
	public static void addDDMSChildElement(Element element, String childName, String childValue) {
		if (!Util.isEmpty(childValue))
			element.appendChild(Util.buildDDMSElement(childName, childValue));
	}

	/**
	 * Convenience method to create an element in the default DDMS namespace with some child text.
	 * The resultant element will use the DDMS prefix and have no attributes or children (yet).
	 * 
	 * @param name the local name of the element
	 * @param childText the text of the element
	 */
	public static Element buildDDMSElement(String name, String childText) {
		return (buildElement(PropertyReader.getPrefix("ddms"), name, DDMSVersion.getCurrentVersion().getNamespace(),
			childText));
	}

	/**
	 * Convenience method to create an element in a namespace with some child text.
	 * The resultant element will use a custom prefix and have no attributes or children (yet).
	 * 
	 * @param prefix the prefix to use (without a trailing colon)
	 * @param name the local name of the element
	 * @param namespaceURI the namespace this element is in
	 * @param childText the text of the element
	 */
	public static Element buildElement(String prefix, String name, String namespaceURI, String childText) {
		Util.requireValue("name", name);
		prefix = (Util.isEmpty(prefix) ? "" : prefix + ":");
		Element element = new Element(prefix + name, namespaceURI);
		if (!Util.isEmpty(childText))
			element.appendChild(childText);
		return (element);
	}

	/**
	 * Convenience method to create an attribute in the default DDMS namespace. The resultant attribute will use the
	 * DDMS prefix and have the provided value.
	 * 
	 * @param name the local name of the attribute
	 * @param value the value of the attribute
	 */
	public static Attribute buildDDMSAttribute(String name, String value) {
		return (buildAttribute(PropertyReader.getPrefix("ddms"), name, DDMSVersion.getCurrentVersion().getNamespace(),
			value));
	}

	/**
	 * Convenience method to create an attribute in a namespace.
	 * 
	 * @param prefix the prefix to use (without a trailing colon)
	 * @param name the local name of the attribute
	 * @param namespaceURI the namespace this attribute is in
	 * @param value the value of the attribute
	 */
	public static Attribute buildAttribute(String prefix, String name, String namespaceURI, String value) {
		requireValue("name", name);
		requireValue("value", value);
		prefix = (Util.isEmpty(prefix) ? "" : prefix + ":");
		if (namespaceURI == null)
			namespaceURI = "";
		return (new Attribute(prefix + name, namespaceURI, value));
	}

	/**
	 * Loads a XOM object tree from an input stream. This method does no schema validation.
	 * 
	 * @param inputStream the input stream containing the XML document
	 * @return a XOM Document
	 * @throws IOException if there are problems loading or parsing the input stream
	 */
	public static Document buildXmlDocument(InputStream inputStream) throws IOException {
		Util.requireValue("input stream", inputStream);
		try {
			return (new Builder().build(inputStream));
		}
		catch (ParsingException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Attempts to convert arbitrary XML into an Element.
	 * 
	 * @param xml the XML string
	 * @return a XOM element based upon the XML string
	 * @throws InvalidDDMSException if the element could not be created.
	 */
	public static Element commitXml(String xml) throws InvalidDDMSException {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader(PropertyReader.getProperty("xml.reader.class"));
			nu.xom.Builder builder = new nu.xom.Builder(reader, false);
			Document doc = builder.build(new StringReader(xml));
			return (doc.getRootElement());
		}
		catch (Exception e) {
			throw new InvalidDDMSException("Could not create a valid element from XML string: " + e.getMessage());
		}
	}
	/**
	 * Locates the queryBinding attribute in an ISO Schematron file and returns it.
	 * 
	 * @param schDocument the Schematron file as an XML Document
	 * @return the value of the queryBinding attribute, or "xslt" if undefined.
	 * @throws IOException if there are file-related problems with looking up the attribute
	 */
	public static String getSchematronQueryBinding(Document schDocument) throws IOException {
		Attribute attr = schDocument.getRootElement().getAttribute("queryBinding");
		return (attr == null ? "xslt" : attr.getValue());
	}

	/**
	 * Takes a Schematron file and transforms it with the ISO Schematron skeleton files.
	 * 
	 * <ol>
	 * <li>The schema is preprocessed with iso_dsdl_include.xsl.</li>
	 * <li>The schema is preprocessed with iso_abstract_expand.xsl.</li>
	 * <li>The schema is compiled with iso_svrl_for_xslt1.xsl.</li>
	 * </ol>
	 * 
	 * <p>The XSLTransform instance using the result of the processing is returned. This XSLTransform can then be used
	 * to validate DDMS components.</p>
	 * 
	 * @param schematronFile the Schematron file
	 * @return the XSLTransform instance
	 * @throws IOException if there are file-related problems with preparing the stylesheets
	 * @throws XSLException if stylesheet transformation fails
	 */
	public static XSLTransform buildSchematronTransform(File schematronFile) throws IOException, XSLException {
		String oldFactory = System.getProperty(PROP_TRANSFORM_FACTORY);
		String newFactory = PropertyReader.getProperty("xml.transform.TransformerFactory");
		if (Util.isEmpty(oldFactory) || !newFactory.equals(oldFactory)) {
			clearTransformCaches();
			System.setProperty(PROP_TRANSFORM_FACTORY, newFactory);
		}
		Document schDocument = Util.buildXmlDocument(new FileInputStream(schematronFile));
		String queryBinding = getSchematronQueryBinding(schDocument);

		// long time = new Date().getTime();
		XSLTransform phase1 = getSchematronIncludeTransform();
		// System.out.println((new Date().getTime() - time) + "ms (Include)");

		// time = new Date().getTime();
		XSLTransform phase2 = getSchematronAbstractTransform();
		// System.out.println((new Date().getTime() - time) + "ms (Abstract)");

		// time = new Date().getTime();
		XSLTransform phase3 = getSchematronSvrlTransform(queryBinding);
		// System.out.println((new Date().getTime() - time) + "ms (SVRL)");

		// time = new Date().getTime();
		Nodes nodes = phase3.transform(phase2.transform(phase1.transform(schDocument)));
		// System.out.println((new Date().getTime() - time) + "ms (Base transformation 1, 2, 3)");

		// time = new Date().getTime();
		XSLTransform finalTransform = new XSLTransform(XSLTransform.toDocument(nodes));
		// System.out.println((new Date().getTime() - time) + "ms (Schematron Validation)");

		return (finalTransform);
	}

	/**
	 * Clears any previous instantiated transforms.
	 */
	private synchronized static void clearTransformCaches() {
		_schematronIncludeTransform = null;
		_schematronAbstractTransform = null;
		_schematronSvrlTransforms.clear();
	}

	/**
	 * Lazy instantiation / cached accessor for the first step of Schematron validation.
	 * 
	 * @return the phase one transform
	 */
	private synchronized static XSLTransform getSchematronIncludeTransform() throws IOException, XSLException {
		if (_schematronIncludeTransform == null) {
			InputStream includeStylesheet = getLoader().getResourceAsStream("schematron/iso_dsdl_include.xsl");
			_schematronIncludeTransform = new XSLTransform(Util.buildXmlDocument(includeStylesheet));
		}
		return (_schematronIncludeTransform);
	}

	/**
	 * Lazy instantiation / cached accessor for the second step of Schematron validation.
	 * 
	 * @return the phase two transform
	 */
	private synchronized static XSLTransform getSchematronAbstractTransform() throws IOException, XSLException {
		if (_schematronAbstractTransform == null) {
			InputStream abstractStylesheet = getLoader().getResourceAsStream("schematron/iso_abstract_expand.xsl");
			_schematronAbstractTransform = new XSLTransform(Util.buildXmlDocument(abstractStylesheet));
		}
		return (_schematronAbstractTransform);
	}

	/**
	 * Lazy instantiation / cached accessor for the third step of Schematron validation, using XSLT1 or XSLT2
	 * 
	 * @param queryBinding the queryBinding value of the Schematron file. Currently "xslt" or "xslt2" are supported.
	 * @return the phase three transform
	 * @throws IllegalArgumentException if the queryBinding is unsupported
	 */
	private synchronized static XSLTransform getSchematronSvrlTransform(String queryBinding) throws IOException,
		XSLException {
		String resourceName;
		if ("xslt2".equals(queryBinding))
			resourceName = "schematron/iso_svrl_for_xslt2.xsl";
		else if ("xslt".equals(queryBinding))
			resourceName = "schematron/iso_svrl_for_xslt1.xsl";
		else
			throw new IllegalArgumentException(
				"DDMSence currently only supports Schematron files with a queryBinding attribute of \"xslt\" or \"xslt2\".");
		if (_schematronSvrlTransforms.get(resourceName) == null) {
			try {
				InputStream schematronStylesheet = getLoader().getResourceAsStream(resourceName);
				Document svrlStylesheet = Util.buildXmlDocument(schematronStylesheet);

				// XOM passes the Base URI to Xalan as the SystemId, which cannot be empty.
				URI svrlUri = getLoader().getResource(resourceName).toURI();
				svrlStylesheet.setBaseURI(svrlUri.toString());

				_schematronSvrlTransforms.put(resourceName, new XSLTransform(svrlStylesheet));
			}
			catch (URISyntaxException e) {
				throw new IOException(e.getMessage());
			}
		}
		return (_schematronSvrlTransforms.get(resourceName));
	}

	/**
	 * Generate a ClassLoader to be used to load resources
	 * 
	 * @return a ClassLoader
	 */
	private static ClassLoader getLoader() {
		return new FindClassLoader().getClass().getClassLoader();
	}

	/**
	 * Stub to load classes.
	 */
	private static class FindClassLoader {
		public FindClassLoader() {}
	}
}