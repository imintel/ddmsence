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

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of an element which might fulfill the xs:any space in the Extensible Layer.
 * <br /><br />
 * {@ddms.versions 11110}
 * 
 * <p>Extensible elements must live in any other namespace besides the DDMS XML namespace.</p>
 * 
 * <p>No validation or processing of any kind is performed by DDMSence on extensible attributes, other than the base
 * validation used when loading attributes from an XML file. This class merely exposes a
 * <code>getXOMElementCopy()</code>
 * method which returns a XOM Element that can be manipulated in business-specific ways.</p>
 * 
 * <p>XOM elements can be created as follows:</p>
 * 
 * <ul><code>
 * Element element = new Element("ddmsence:extension", "http://ddmsence.urizone.net/");<br />
 * element.appendChild("This will be the child text.");
 * </code></ul>
 * 
 * <p>Because it is impossible to cover all of the HTML/Text output cases for ExtensibleElements, DDMSence
 * will simply print out the existence of extensible elements:</p>
 * <ul><code>
 * Extensible Layer: true<br />
 * &lt;meta name="extensible.layer" content="true" /&gt;<br />
 * </code></ul></p>
 * 
 * <p>Details about the XOM Element class can be found at:
 * <i>http://www.xom.nu/apidocs/index.html?nu/xom/Element.html</i></p>
 *
 * {@table.header History}
 * 		<p>In DDMS 2.0, only one element is allowed on the resource. In DDMS 3.0 - 4.1, zero to many may appear. The
 * 		ExtensibleElement extension point was removed in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info any:&lt;<i>extensibleElements</i>&gt;|0..1|10000}
 * 		{@child.info any:&lt;<i>extensibleElements</i>&gt;|0..*|01110}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule An extensible element must not be in the DDMS namespace.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 1.1.0
 */
public final class ExtensibleElement extends AbstractBaseComponent {

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public ExtensibleElement(Element element) throws InvalidDDMSException {
		super(element);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		if (DDMSVersion.isSupportedDDMSNamespace(getNamespace()))
			throw new InvalidDDMSException("Extensible elements must not be defined in the DDMS namespace.");
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		return ("");
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof ExtensibleElement))
			return (false);
		ExtensibleElement test = (ExtensibleElement) obj;
		return (getXOMElement().toXML().equals(test.getXOMElement().toXML()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getXOMElement().toXML().hashCode();
		return (result);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7276942157278555643L;
		private String _xml;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(ExtensibleElement element) {
			setXml(element.toXML());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public ExtensibleElement commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			return (new ExtensibleElement(Util.commitXml(getXml())));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getXml()));
		}

		/**
		 * Builder accessor for the XML string representing the element.
		 */
		public String getXml() {
			return _xml;
		}

		/**
		 * Builder accessor for the XML string representing the element.
		 */
		public void setXml(String xml) {
			_xml = xml;
		}
	}
}