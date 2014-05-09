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
package buri.ddmsence.ddms;

import java.util.List;

import nu.xom.Element;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;

/**
 * Interface for a single DDMS element.
 * 
 * <p>
 * All DDMS components should be valid after instantiation and can output themselves in various formats. Implementation
 * classes are divided up into packages based on the five Core sets: Metacard Info, Security, Resource, Summary Content,
 * and Format.
 * </p>
 * 
 * <p>
 * Accessors which return string-based data will always favour an empty string over a null value.
 * </p>
 * 
 * <p>
 * In general, Java classes are implemented for:
 * <ul>
 * <li>Elements which are explicitly declared as DDMS Categories in the DDMS documentation are always implemented
 * (ddms:identifier).</li>
 * <li>Elements which merely enclose important data AND which have no special attributes are never implemented
 * (ddms:Media).</li>
 * <li>Data which can be represented as a simple Java type AND which has no special attributes is represented as a
 * simple Java type (ddms:email).</li>
 * <li>Attributes are generally implemented as properties on an Object. The exceptions to this are the ISM, SRS, and XLink
 * AttributeGroup, which decorates many DDMS components.</li>
 * </ul>
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public interface IDDMSComponent {

	/**
	 * Accessor for the prefix of the component, without a trailing colon
	 * 
	 * @return the prefix
	 */
	public String getPrefix();

	/**
	 * Accessor for the name of the component
	 * 
	 * @return the name, without prefix
	 */
	public String getName();

	/**
	 * Accessor for the XML namespace of the component
	 * 
	 * @return the namespace
	 */
	public String getNamespace();

	/**
	 * Accessor for the name of the component, including the prefix
	 * 
	 * @return the name, with prefix if available
	 */
	public String getQualifiedName();

	/**
	 * Returns any security attributes attached to this component. If the component is not allowed to have attributes,
	 * this will be null. Otherwise, a valid SecurityAttributes object will be returned, even if none of its properties
	 * are set.
	 */
	public SecurityAttributes getSecurityAttributes();

	/**
	 * Returns a list of any warning messages that occurred during validation. Warnings do not prevent a valid component
	 * from being formed. A parent component should also claim the warnings of its child elements.
	 * 
	 * @return a list of warnings
	 */
	public List<ValidationMessage> getValidationWarnings();

	/**
	 * Renders this component as HTML.
	 * 
	 * @return the HTML representation of this component
	 */
	public String toHTML();

	/**
	 * Renders this component as Text.
	 * 
	 * @return the text-based representation of this component
	 */
	public String toText();

	/**
	 * Renders this component as XML.
	 * 
	 * @return the XML representation of this component
	 */
	public String toXML();

	/**
	 * Accessor for a copy of the underlying XOM element. This allows a XOM tree to be built from DDMS data when
	 * traversing a list of IDDMSComponents.
	 */
	public Element getXOMElementCopy();
}
