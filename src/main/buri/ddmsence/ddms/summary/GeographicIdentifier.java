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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:geographicIdentifier.
 *  <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 *  	<p>Additional content rules apply to country codes starting in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:name|0..*|11111}
 * 		{@child.info ddms:region|0..*|11111}
 * 		{@child.info ddms:countryCode|0..1|11111}
 * 		{@child.info ddms:subDivisionCode|0..1|00011}
 * 		{@child.info ddms:facilityIdentifier|0..1|11111}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.		
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule This component must contain at least 1 child element.|Error|11111}
 * 		{@ddms.rule If ddms:facilityIdentifier is used, no other child elements must be used.|Error|11111}
 *		{@ddms.rule The codespace of ddms:countryCode must have the correct GENC format.|Error|00001}
 *		{@ddms.rule The code of ddms:countryCode must be 3 uppercase alpha characters when the codespace requires a trigraph.|Error|00001}
 *		{@ddms.rule The code of ddms:countryCode must be 2 uppercase alpha characters when the codespace requires a digraph.|Error|00001}
 *		{@ddms.rule The code of ddms:countryCode must be 3 numerals when the codespace requires numbers.|Error|00001}
 *		{@ddms.rule No lookup of GENC country codes is performed against the NGA registry.|Warning|00001}
 *		<p>See {@link #validateGencCountryCode(CountryCode)} for additional information about country code validation.</p>
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class GeographicIdentifier extends AbstractBaseComponent {

	private List<String> _names = null;
	private List<String> _regions = null;
	private CountryCode _countryCode = null;
	private SubDivisionCode _subDivisionCode = null;
	private FacilityIdentifier _facilityIdentifier = null;

	private static final String NAME_NAME = "name";
	private static final String REGION_NAME = "region";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public GeographicIdentifier(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			DDMSVersion version = getDDMSVersion();
			_names = Util.getDDMSChildValues(element, NAME_NAME);
			_regions = Util.getDDMSChildValues(element, REGION_NAME);
			Element countryCodeElement = element.getFirstChildElement(CountryCode.getName(version), getNamespace());
			if (countryCodeElement != null)
				_countryCode = new CountryCode(countryCodeElement);
			Element subDivisionCodeElement = element.getFirstChildElement(SubDivisionCode.getName(version),
				getNamespace());
			if (subDivisionCodeElement != null)
				_subDivisionCode = new SubDivisionCode(subDivisionCodeElement);
			Element facilityIdentifierElement = element.getFirstChildElement(FacilityIdentifier.getName(version),
				getNamespace());
			if (facilityIdentifierElement != null)
				_facilityIdentifier = new FacilityIdentifier(facilityIdentifierElement);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data. Note that the facilityIdentifier component cannot be used
	 * with the components in this constructor.
	 * 
	 * @param names the names
	 * @param regions the region names
	 * @param countryCode the country code
	 * @param subDivisionCode the subdivision code
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public GeographicIdentifier(List<String> names, List<String> regions, CountryCode countryCode,
		SubDivisionCode subDivisionCode) throws InvalidDDMSException {
		try {
			if (names == null)
				names = Collections.emptyList();
			if (regions == null)
				regions = Collections.emptyList();
			Element element = Util.buildDDMSElement(GeographicIdentifier.getName(DDMSVersion.getCurrentVersion()), null);
			for (String name : names)
				element.appendChild(Util.buildDDMSElement(NAME_NAME, name));
			for (String region : regions)
				element.appendChild(Util.buildDDMSElement(REGION_NAME, region));
			if (countryCode != null)
				element.appendChild(countryCode.getXOMElementCopy());
			if (subDivisionCode != null)
				element.appendChild(subDivisionCode.getXOMElementCopy());
			_names = names;
			_regions = regions;
			_countryCode = countryCode;
			_subDivisionCode = subDivisionCode;
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param facilityIdentifier the facility identifier (required in this constructor)
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public GeographicIdentifier(FacilityIdentifier facilityIdentifier) throws InvalidDDMSException {
		Element element = Util.buildDDMSElement(GeographicIdentifier.getName(DDMSVersion.getCurrentVersion()), null);
		if (facilityIdentifier != null)
			element.appendChild(facilityIdentifier.getXOMElementCopy());
		_names = Collections.emptyList();
		_regions = Collections.emptyList();
		_facilityIdentifier = facilityIdentifier;
		setXOMElement(element, true);
	}

	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), GeographicIdentifier.getName(getDDMSVersion()));
		if (getNames().isEmpty() && getRegions().isEmpty() && getCountryCode() == null && getSubDivisionCode() == null
			&& getFacilityIdentifier() == null) {
			throw new InvalidDDMSException(
				"At least 1 of name, region, countryCode, subDivisionCode, or facilityIdentifier must exist.");
		}
		if (hasFacilityIdentifier()) {
			if (!getNames().isEmpty() || !getRegions().isEmpty() || getCountryCode() != null
				|| getSubDivisionCode() != null)
				throw new InvalidDDMSException("facilityIdentifier must not be used in tandem with other components.");
		}
		if (getDDMSVersion().isAtLeast("5.0") && getCountryCode() != null) {
			validateGencCountryCode(getCountryCode());
		}
		super.validate();
	}
	
	/**
	 * Validates the syntax of the codespace/code value in a DDMS 5.0 (or later) country code.
	 * 
	 * <p>
	 * The codespace will fall into one of 4 flavours:
	 * <ul>
	 * <li>URL: <code>http://api.nsgreg.nga.mil/geo-political/GENC/2/ed1</code></li>
	 * <li>URN: <code>urn:us:gov:dod:nga:def:geo-political:GENC:3:ed1</code></li>
	 * <li>URNBased: <code>geo-political:GENC:3:ed1</code></li>
	 * <li>URNBasedShort: <code>ge:GENC:3:ed1</code></li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * This method converts all codespaces into a URNBased version for comparison and then confirms that the code value
	 * is correct for the provided codespace.
	 * </p>
	 * 
	 * @param countryCode the country code to validate
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	protected static void validateGencCountryCode(CountryCode countryCode) throws InvalidDDMSException {
		String codespace = countryCode.getQualifier()
			.replaceFirst("^http://api.nsgreg.nga.mil/geo-political/", "geo-political/")
			.replaceFirst("^urn:us:gov:dod:nga:def:geo-political:", "geo-political/")
			.replaceFirst("^ge:", "geo-political:")
			.replaceAll("/", ":");
		String code = countryCode.getValue();
		if (!codespace.matches("^geo-political:GENC:[23n]:ed\\d$"))
			throw new InvalidDDMSException(
				"ddms:countryCode must use a geo-political URN or URL, as specified in the DDMS Schematron file.");
		else if (codespace.contains("GENC:3:") && !code.matches("^[A-Z]{3}$"))
			throw new InvalidDDMSException(
				"A GENC country code in a 3-alpha codespace (e.g. geo-political:GENC:3:ed1) must consist of exactly 3 uppercase alpha characters.");
		else if (codespace.contains("GENC:2:") && !code.matches("^[A-Z]{2}$"))
			throw new InvalidDDMSException(
				"A GENC country code in a 2-alpha codespace (e.g. geo-political:GENC:2:ed1) must consist of exactly 2 uppercase alpha characters.");
		else if (codespace.contains("GENC:n:") && !code.matches("^[0-9]{3}$"))
			throw new InvalidDDMSException(
				"A GENC country code in a numeric codespace (e.g. geo-political:GENC:n:ed1) must consist of exactly 3 numerals.");
	}
	
	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (getDDMSVersion().isAtLeast("5.0") && getCountryCode() != null) {
			addWarning("The ddms:countryCode is syntactically correct, "
				+ "but was not looked up in the NGA registry at http://api.nsgreg.nga.mil/.");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix + NAME_NAME, getNames()));
		text.append(buildOutput(isHTML, localPrefix + REGION_NAME, getRegions()));
		if (getCountryCode() != null)
			text.append(getCountryCode().getOutput(isHTML, localPrefix, ""));
		if (getSubDivisionCode() != null)
			text.append(getSubDivisionCode().getOutput(isHTML, localPrefix, ""));
		if (hasFacilityIdentifier())
			text.append(getFacilityIdentifier().getOutput(isHTML, localPrefix, ""));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getCountryCode());
		list.add(getSubDivisionCode());
		list.add(getFacilityIdentifier());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof GeographicIdentifier))
			return (false);
		GeographicIdentifier test = (GeographicIdentifier) obj;
		return (Util.listEquals(getNames(), test.getNames())
			&& Util.listEquals(getRegions(), test.getRegions()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getNames().hashCode();
		result = 7 * result + getRegions().hashCode();
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
		return ("geographicIdentifier");
	}

	/**
	 * Accessor for the names
	 */
	public List<String> getNames() {
		return (Collections.unmodifiableList(_names));
	}

	/**
	 * Accessor for the regions
	 */
	public List<String> getRegions() {
		return (Collections.unmodifiableList(_regions));
	}

	/**
	 * Accessor for the country code. May return null if no code was used.
	 */
	public CountryCode getCountryCode() {
		return (_countryCode);
	}

	/**
	 * Accessor for the subdivision code. May return null if no code was used.
	 */
	public SubDivisionCode getSubDivisionCode() {
		return (_subDivisionCode);
	}

	/**
	 * Accessor for the facility identifier. May return null if no identifier was used.
	 */
	public FacilityIdentifier getFacilityIdentifier() {
		return (_facilityIdentifier);
	}

	/**
	 * Accessor for whether this geographic identifier is using a facility identifier.
	 */
	public boolean hasFacilityIdentifier() {
		return (getFacilityIdentifier() != null);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -6626896938484051916L;
		private List<String> _names = null;
		private List<String> _regions = null;
		private CountryCode.Builder _countryCode = null;
		private SubDivisionCode.Builder _subDivisionCode = null;
		private FacilityIdentifier.Builder _facilityIdentifier = null;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(GeographicIdentifier identifier) {
			if (identifier.hasFacilityIdentifier())
				setFacilityIdentifier(new FacilityIdentifier.Builder(identifier.getFacilityIdentifier()));
			else {
				setNames(identifier.getNames());
				setRegions(identifier.getRegions());
				if (identifier.getCountryCode() != null)
					setCountryCode(new CountryCode.Builder(identifier.getCountryCode()));
				if (identifier.getSubDivisionCode() != null)
					setSubDivisionCode(new SubDivisionCode.Builder(identifier.getSubDivisionCode()));
			}
		}

		/**
		 * @see IBuilder#commit()
		 */
		public GeographicIdentifier commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			FacilityIdentifier identifier = getFacilityIdentifier().commit();
			if (identifier != null)
				return (new GeographicIdentifier(identifier));
			return (new GeographicIdentifier(getNames(), getRegions(), getCountryCode().commit(),
				getSubDivisionCode().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.containsOnlyEmptyValues(getNames())
				&& Util.containsOnlyEmptyValues(getRegions())
				&& getCountryCode().isEmpty()
				&& getSubDivisionCode().isEmpty()
				&& getFacilityIdentifier().isEmpty());
		}

		/**
		 * Builder accessor for the names
		 */
		public List<String> getNames() {
			if (_names == null)
				_names = new LazyList(String.class);
			return _names;
		}

		/**
		 * Builder accessor for the names
		 */
		public void setNames(List<String> names) {
			_names = new LazyList(names, String.class);
		}

		/**
		 * Builder accessor for the regions
		 */
		public List<String> getRegions() {
			if (_regions == null)
				_regions = new LazyList(String.class);
			return _regions;
		}

		/**
		 * Builder accessor for the regions
		 */
		public void setRegions(List<String> regions) {
			_regions = new LazyList(regions, String.class);
		}

		/**
		 * Builder accessor for the country code
		 */
		public CountryCode.Builder getCountryCode() {
			if (_countryCode == null) {
				_countryCode = new CountryCode.Builder();
			}
			return _countryCode;
		}

		/**
		 * Builder accessor for the country code
		 */
		public void setCountryCode(CountryCode.Builder countryCode) {
			_countryCode = countryCode;
		}

		/**
		 * Builder accessor for the subdivision code
		 */
		public SubDivisionCode.Builder getSubDivisionCode() {
			if (_subDivisionCode == null) {
				_subDivisionCode = new SubDivisionCode.Builder();
			}
			return _subDivisionCode;
		}

		/**
		 * Builder accessor for the subdivision code
		 */
		public void setSubDivisionCode(SubDivisionCode.Builder subDivisionCode) {
			_subDivisionCode = subDivisionCode;
		}

		/**
		 * Builder accessor for the facility identifier
		 */
		public FacilityIdentifier.Builder getFacilityIdentifier() {
			if (_facilityIdentifier == null)
				_facilityIdentifier = new FacilityIdentifier.Builder();
			return _facilityIdentifier;
		}

		/**
		 * Builder accessor for the facility identifier
		 */
		public void setFacilityIdentifier(FacilityIdentifier.Builder facilityIdentifier) {
			_facilityIdentifier = facilityIdentifier;
		}
	}
}