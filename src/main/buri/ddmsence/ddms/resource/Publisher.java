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
package buri.ddmsence.ddms.resource;

import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractProducerRole;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IRoleEntity;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:publisher.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:organization|0..1|11111}
 * 		{@child.info ddms:person|0..1|11111}
 * 		{@child.info ddms:service|0..1|11111}
 * 		{@child.info ddms:unknown|0..1|01111}
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|11111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule Exactly 1 producer entity must fill this role.|Error|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class Publisher extends AbstractProducerRole {

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public Publisher(Element element) throws InvalidDDMSException {
		super(element);
	}

	/**
	 * Constructor which builds from raw data.
	 * 
	 * @param entity the actual entity fulfilling this role
	 * @param pocTypes the ISM pocType for this producer
	 * @param securityAttributes any security attributes
	 */
	public Publisher(IRoleEntity entity, List<String> pocTypes, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(Publisher.getName(DDMSVersion.getCurrentVersion()), entity, pocTypes, securityAttributes);
	}

	/**
	 * @see AbstractProducerRole#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), Publisher.getName(getDDMSVersion()));
		super.validate();
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		return (super.equals(obj) && (obj instanceof Publisher));
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("publisher");
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractProducerRole.Builder {
		private static final long serialVersionUID = 4565840434345629470L;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(Publisher producer) {
			super(producer);
		}

		/**
		 * @see IBuilder#commit()
		 */
		public Publisher commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new Publisher(commitSelectedEntity(), getPocTypes(),
				getSecurityAttributes().commit()));
		}
	}
}
