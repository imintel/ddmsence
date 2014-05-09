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

import java.io.Serializable;

import nu.xom.Element;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;
import buri.ddmsence.util.Util;

/**
 * Base class for NTK elements which consist of simple child text decorated with NTK attributes, and security
 * attributes.
 * <br /><br />
 * {@ddms.versions 00010}
 * 
 * <p> Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set
 * before the component is used. </p>
 * 
 * {@table.header History}
 * 		<p>This abstract class was introduced to support NTK Access components in DDMS 4.1. Those components are
 * 		no longer a part of DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ntk:id|0..1|00010}
 * 		{@child.info nkt:IDReference|0..1|00010}
 * 		{@child.info nkt:qualifier|0..1|00010}
 * 		{@child.info ism:classification|1|00010}
 * 		{@child.info ism:ownerProducer|1..*|00010}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00010}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule If this is an NMTOKEN-based string, and the child text is not empty, the child text is an NMTOKEN.|Error|11111}
 * 		{@ddms.rule ism:classification must exist.|Error|11111}
 * 		{@ddms.rule ism:ownerProducer must exist.|Error|11111}
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 2.0.0
 */
public abstract class AbstractNtkString extends AbstractBaseComponent {
	
	private boolean _tokenBased = false;
	private SecurityAttributes _securityAttributes = null;
		
	private static final String ID_NAME = "id";
	private static final String ID_REFERENCE_NAME = "IDReference";
	private static final String QUALIFIER_NAME = "qualifier";
	
	/**
	 * Base constructor which works from a XOM element.
	 * 
	 * @param tokenBased true if the child text is an NMTOKEN, false if it's just a string
	 * @param element the XOM element
	 */
	protected AbstractNtkString(boolean tokenBased, Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			_tokenBased = tokenBased;
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);	
		}
	}
	
	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param tokenBased true if the child text is an NMTOKEN, false if it's just a string
	 * @param name the name of the element without a prefix
	 * @param value the value of the element's child text
	 * @param id the NTK ID
	 * @param idReference a reference to an NTK ID
	 * @param qualifier an NTK qualifier
	 * @param securityAttributes the security attributes
	 * @param validateNow whether to validate immediately
	 */
	protected AbstractNtkString(boolean tokenBased, String name, String value, String id, String idReference,
		String qualifier, SecurityAttributes securityAttributes, boolean validateNow) throws InvalidDDMSException {
		try {
			String ntkPrefix = PropertyReader.getPrefix("ntk");
			String ntkNamespace = DDMSVersion.getCurrentVersion().getNtkNamespace();
			Element element = Util.buildElement(ntkPrefix, name, ntkNamespace, value);
			Util.addAttribute(element, ntkPrefix, ID_NAME, ntkNamespace, id);
			Util.addAttribute(element, ntkPrefix, ID_REFERENCE_NAME, ntkNamespace, idReference);
			Util.addAttribute(element, ntkPrefix, QUALIFIER_NAME, ntkNamespace, qualifier);
			_tokenBased = tokenBased;
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
			setXOMElement(element, validateNow);
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
		requireAtLeastVersion("4.0.1");
		if (isTokenBased())
			Util.requireValidNMToken(getValue());
		getSecurityAttributes().requireClassification();
		super.validate();
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return (false);
		AbstractNtkString test = (AbstractNtkString) obj;
		return (getValue().equals(test.getValue())
			&& getID().equals(test.getID())
			&& getIDReference().equals(test.getIDReference())
			&& getQualifier().equals(test.getQualifier()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getValue().hashCode();
		result = 7 * result + getID().hashCode();
		result = 7 * result + getIDReference().hashCode();
		result = 7 * result + getQualifier().hashCode();
		return (result);
	}
	
	/**
	 * Accessor for the child text of the description. The underlying XOM method which retrieves the child text returns
	 * an empty string if not found.
	 */
	public String getValue() {
		return (getXOMElement().getValue());
	}
	
	/**
	 * Accessor for the ID
	 */
	public String getID() {
		return (getAttributeValue(ID_NAME, getDDMSVersion().getNtkNamespace()));
	}
	
	/**
	 * Accessor for the IDReference
	 */
	public String getIDReference() {
		return (getAttributeValue(ID_REFERENCE_NAME, getDDMSVersion().getNtkNamespace()));
	}
	
	/**
	 * Accessor for the qualifier
	 */
	public String getQualifier() {
		return (getAttributeValue(QUALIFIER_NAME, getDDMSVersion().getNtkNamespace()));
	}
	
	/**
	 * Accessor for the Security Attributes. Will always be non-null even if the attributes are not set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}
	
	/**
	 * Accessor for whether this is an NMTOKEN-based string
	 */
	private boolean isTokenBased() {
		return (_tokenBased);
	}
	
	/**
	 * Abstract Builder for this DDMS component.
	 * 
	 * <p>Builders which are based upon this abstract class should implement the commit() method, returning the
	 * appropriate concrete object type.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static abstract class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7824644958681123708L;
		private String _value;
		private String _id;
		private String _idReference;
		private String _qualifier;
		private SecurityAttributes.Builder _securityAttributes;
		
		
		/**
		 * Empty constructor
		 */
		protected Builder() {}
		
		/**
		 * Constructor which starts from an existing component.
		 */
		protected Builder(AbstractNtkString string) {
			setValue(string.getValue());
			setID(string.getID());
			setIDReference(string.getIDReference());
			setQualifier(string.getQualifier());
			setSecurityAttributes(new SecurityAttributes.Builder(string.getSecurityAttributes()));
		}
		
		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.isEmpty(getValue())
				&& Util.isEmpty(getID())
				&& Util.isEmpty(getIDReference())
				&& Util.isEmpty(getQualifier())
				&& getSecurityAttributes().isEmpty());
		}
		
		/**
		 * Builder accessor for the child text.
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Builder accessor for the child text.
		 */
		public void setValue(String value) {
			_value = value;
		}

		/**
		 * Builder accessor for the id
		 */
		public String getID() {
			return _id;
		}

		/**
		 * Builder accessor for the id
		 */
		public void setID(String id) {
			_id = id;
		}

		/**
		 * Builder accessor for the idReference
		 */
		public String getIDReference() {
			return _idReference;
		}

		/**
		 * Builder accessor for the idReference
		 */
		public void setIDReference(String idReference) {
			_idReference = idReference;
		}

		/**
		 * Builder accessor for the qualifier
		 */
		public String getQualifier() {
			return _qualifier;
		}

		/**
		 * Builder accessor for the qualifier
		 */
		public void setQualifier(String qualifier) {
			_qualifier = qualifier;
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