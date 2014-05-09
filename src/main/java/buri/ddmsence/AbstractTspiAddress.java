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
import java.util.HashSet;
import java.util.Set;

import nu.xom.Element;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.ITspiAddress;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.Util;

/**
 * Base class for TSPI addresses.
 * <br /><br />
 * {@ddms.versions 00001}
 * 
 * <p> Extensions of this class are generally expected to be immutable, and the underlying XOM element MUST be set
 * before the component is used. </p>
 * 
 * <p>For the initial support of DDMS 5.0 TSPI addresses, the DDMSence component will only return the raw XML of
 * an address. The addressing specification is incredibly complex and multi-layered, and it is unclear how much
 * value full-fledged classes would have. As use cases refine and more organizations adopt DDMS 5.0, the components
 * can be revisited to provide more value-add.</p>
 *  
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info action|0..1|00001}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule If set, action attribute must be a valid token.|Error|11111}
 * 		<p>No additional validation is done on the TSPI address at this time.</p>
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 2.2.0
 */
public abstract class AbstractTspiAddress extends AbstractBaseComponent implements ITspiAddress {
	
	/** The prefix of the action attribute */
	public static final String NO_PREFIX = "";

	/** The namespace of the action attribute */
	public static final String NO_NAMESPACE = "";
	
	/** The value for an add action. */
	public static final String ADD_ACTION = "ADD";
	
	/** The value for a delete action. */
	public static final String DELETE_ACTION = "DELETE";
	
	private static Set<String> ACTIONS = new HashSet<String>();
	static {
		ACTIONS.add(ADD_ACTION);
		ACTIONS.add(DELETE_ACTION);
	}
	
	private static final String ACTION_NAME = "action";
	
	/**
	 * Base constructor which works from a XOM element.
	 * 
	 * @param element the XOM element
	 */
	protected AbstractTspiAddress(Element element) throws InvalidDDMSException {
		super(element);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		requireAtLeastVersion("5.0");
		if (!Util.isEmpty(getAction()) &&!ACTIONS.contains(getAction()))
			throw new InvalidDDMSException("The action attribute must be one of " + ACTIONS);		
		super.validate();
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return (false);
		AbstractTspiAddress test = (AbstractTspiAddress) obj;
		return (getAction().equals(test.getAction())
			&& toXML().equals(test.toXML()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getAction().hashCode();
		result = 7 * result + toXML().hashCode();
		return (result);
	}

	/**
	 * Accessor for the action
	 */
	public String getAction() {
		return (getAttributeValue(ACTION_NAME, NO_NAMESPACE));
	}
		
	/**
	 * Abstract Builder for this DDMS component.
	 * 
	 * <p>Builders which are based upon this abstract class should implement the commit() method, returning the
	 * appropriate concrete object type.</p>
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.2.0
	 */
	public static abstract class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 7824644958681123708L;
		private String _xml;
				
		/**
		 * Empty constructor
		 */
		protected Builder() {}
		
		/**
		 * Constructor which starts from an existing component.
		 */
		protected Builder(AbstractTspiAddress address) {
			setXml(address.toXML());
		}

		/**
		 * Commits the XML string to a XOM element
		 * 
		 * @throws InvalidDDMSException if the XML cannot be converted.
		 */
		protected Element commitXml() throws InvalidDDMSException {
			return (Util.commitXml(getXml()));
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