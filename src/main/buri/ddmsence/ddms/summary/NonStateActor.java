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
package buri.ddmsence.ddms.summary;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.AbstractSimpleString;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:nonStateActor.
 * <br /><br />
 * {@ddms.versions 00011}
 * 
 * <p></p>
 * 
 * {@table.header History}
 *  	None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ddms:order|0..1|00011}
 * 		{@child.info ddms:qualifier|0..1|00011}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|00011}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule Component must not be used before the DDMS version in which it was introduced.|Error|11111}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule If set, ddms:qualifier must be a valid URI.|Error|11111}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11111}
 * 		{@ddms.rule ddms:qualifier may cause issues for DDMS 4.0.1 systems.|Warning|00010}
 * 		<p>Does not validate the value of the order attribute (this is done at the Resource level).</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public final class NonStateActor extends AbstractSimpleString {

	private static final String ORDER_NAME = "order";
	private static final String QUALIFIER_NAME = "qualifier";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NonStateActor(Element element) throws InvalidDDMSException {
		super(element, true);
	}

	/**
	 * Constructor for creating a component from raw data.
	 * 
	 * @deprecated A new constructor was added for DDMS 4.1 to support ddms:qualifier. This constructor is preserved for
	 *             backwards compatibility, but may disappear in the next major release.
	 * 
	 * @param value the value of the description child text
	 * @param order the order of this actor
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NonStateActor(String value, Integer order, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		this(value, order, null, securityAttributes);
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param value the value of the description child text
	 * @param order the order of this actor
	 * @param qualifier the qualifier
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public NonStateActor(String value, Integer order, String qualifier, SecurityAttributes securityAttributes)
		throws InvalidDDMSException {
		super(NonStateActor.getName(DDMSVersion.getCurrentVersion()), value, securityAttributes, false);
		try {
			if (order != null)
				Util.addDDMSAttribute(getXOMElement(), ORDER_NAME, order.toString());
			if (!Util.isEmpty(qualifier))
				Util.addDDMSAttribute(getXOMElement(), QUALIFIER_NAME, qualifier);
			validate();
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
		// Do not call super.validate(), because securityAttributes are optional.
		requireAtLeastVersion("4.0.1");
		Util.requireDDMSQName(getXOMElement(), NonStateActor.getName(getDDMSVersion()));
		if (!Util.isEmpty(getQualifier())) {
			Util.requireDDMSValidURI(getQualifier());
		}
		validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (Util.isEmpty(getValue()))
			addWarning("A ddms:" + getName() + " element was found with no value.");
		if (!getDDMSVersion().isAtLeast("5.0") && !Util.isEmpty(getQualifier()))
			addDdms40Warning("ddms:qualifier attribute");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + "value", getValue()));
		text.append(buildOutput(isHTML, localPrefix + ORDER_NAME, String.valueOf(getOrder())));
		text.append(buildOutput(isHTML, localPrefix + QUALIFIER_NAME, getQualifier()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof NonStateActor))
			return (false);
		NonStateActor test = (NonStateActor) obj;
		return (getOrder().equals(test.getOrder()) && getQualifier().equals(test.getQualifier()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getOrder().hashCode();
		result = 7 * result + getQualifier().hashCode();
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
		return ("nonStateActor");
	}

	/**
	 * Accessor for the order attribute.
	 */
	public Integer getOrder() {
		String order = getAttributeValue(ORDER_NAME);
		return (Util.isEmpty(order) ? null : Integer.valueOf(order));
	}

	/**
	 * Accessor for the qualifier attribute.
	 */
	public String getQualifier() {
		return (getAttributeValue(QUALIFIER_NAME));
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 2.0.0
	 */
	public static class Builder extends AbstractSimpleString.Builder {
		private static final long serialVersionUID = 7750664735441105296L;
		private Integer _order;
		private String _qualifier;

		/**
		 * Empty constructor
		 */
		public Builder() {
			super();
		}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(NonStateActor actor) {
			super(actor);
			setOrder(actor.getOrder());
			setQualifier(actor.getQualifier());
		}

		/**
		 * @see IBuilder#commit()
		 */
		public NonStateActor commit() throws InvalidDDMSException {
			return (isEmpty() ? null : new NonStateActor(getValue(), getOrder(), getQualifier(),
				getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (super.isEmpty()
				&& getOrder() == null
				&& Util.isEmpty(getQualifier()));
		}

		/**
		 * Builder accessor for the order
		 */
		public Integer getOrder() {
			return _order;
		}

		/**
		 * Builder accessor for the qualifier attribute
		 */
		public String getQualifier() {
			return _qualifier;
		}

		/**
		 * Builder accessor for the qualifier attribute
		 */
		public void setQualifier(String qualifier) {
			_qualifier = qualifier;
		}

		/**
		 * Builder accessor for the order
		 */
		public void setOrder(Integer order) {
			_order = order;
		}
	}
}