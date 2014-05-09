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
import buri.ddmsence.ddms.ITspiAddress;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.summary.tspi.GeneralAddressClass;
import buri.ddmsence.ddms.summary.tspi.IntersectionAddress;
import buri.ddmsence.ddms.summary.tspi.LandmarkAddress;
import buri.ddmsence.ddms.summary.tspi.NumberedThoroughfareAddress;
import buri.ddmsence.ddms.summary.tspi.TwoNumberAddressRange;
import buri.ddmsence.ddms.summary.tspi.USPSGeneralDeliveryOffice;
import buri.ddmsence.ddms.summary.tspi.USPSPostalDeliveryBox;
import buri.ddmsence.ddms.summary.tspi.USPSPostalDeliveryRoute;
import buri.ddmsence.ddms.summary.tspi.UnnumberedThoroughfareAddress;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:postalAddress.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 *  	<p>The custom definition of postal address was replaced by TSPI addresses in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:street|0..6|11110}
 * 		{@child.info ddms:city|0..1|11110}
 * 		{@child.info ddms:state|0..1|11110}
 * 		{@child.info ddms:province|0..1|11110}
 * 		{@child.info ddms:postalCode|0..1|11110}
 * 		{@child.info ddms:countryCode|0..1|11110}
 * 		{@child.info tspi:NumberedThoroughfareAddress|0..1|00001} 
 * 		{@child.info tspi:UnnumberedThoroughfareAddress|0..1|00001}
 * 		{@child.info tspi:IntersectionAddress|0..1|00001}
 *  	{@child.info tspi:TwoNumberAddressRange|0..1|00001}
 *   	{@child.info tspi:LandmarkAddress|0..1|00001}
 *   	{@child.info tspi:USPSPostalDeliveryBox|0..1|00001}
 *   	{@child.info tspi:USPSPostalDeliveryRoute|0..1|00001}
 *   	{@child.info tspi:USPSGeneralDeliveryOffice|0..1|00001}
 *   	{@child.info tspi:GeneralAddressClass|0..1|00001}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule Either a ddms:state or a ddms:province must exist, but not both.|Error|11110}
 * 		{@ddms.rule TSPI addresses must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule DDMS postal elements are not used after the DDMS version in which they were removed.|Error|11111}
 * 		{@ddms.rule A TSPI address must exist.|Error|00001}
 * 		{@ddms.rule This component can be used with no values set.|Warning|11110}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class PostalAddress extends AbstractBaseComponent {

	private List<String> _streets = null;
	private String _city = null;
	private String _state = null;
	private String _province = null;
	private String _postalCode = null;
	private CountryCode _countryCode = null;
	private ITspiAddress _tspiAddress = null;
	
	private static final String STREET_NAME = "street";
	private static final String CITY_NAME = "city";
	private static final String STATE_NAME = "state";
	private static final String PROVINCE_NAME = "province";
	private static final String POSTAL_CODE_NAME = "postalCode";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public PostalAddress(Element element) throws InvalidDDMSException {
		try {
			setXOMElement(element, false);
			if (!getDDMSVersion().isAtLeast("5.0")) {
				_streets = Util.getDDMSChildValues(element, STREET_NAME);
				Element cityElement = element.getFirstChildElement(CITY_NAME, getNamespace());
				if (cityElement != null)
					_city = cityElement.getValue();
				Element stateElement = element.getFirstChildElement(STATE_NAME, getNamespace());
				if (stateElement != null)
					_state = stateElement.getValue();
				Element provinceElement = element.getFirstChildElement(PROVINCE_NAME, getNamespace());
				if (provinceElement != null)
					_province = provinceElement.getValue();
				Element postalCodeElement = element.getFirstChildElement(POSTAL_CODE_NAME, getNamespace());
				if (postalCodeElement != null)
					_postalCode = postalCodeElement.getValue();
				Element countryCodeElement = element.getFirstChildElement(CountryCode.getName(getDDMSVersion()),
					getNamespace());
				if (countryCodeElement != null)
					_countryCode = new CountryCode(countryCodeElement);
			}
			else {
				String tspiNamespace = getDDMSVersion().getTspiNamespace();
				Element generalAddressClassElement = element.getFirstChildElement(
					GeneralAddressClass.getName(getDDMSVersion()), tspiNamespace);
				if (generalAddressClassElement != null) {
					_tspiAddress = new GeneralAddressClass(generalAddressClassElement);
				}
				Element intersectionAddressElement = element.getFirstChildElement(
					IntersectionAddress.getName(getDDMSVersion()), tspiNamespace);
				if (intersectionAddressElement != null)
					_tspiAddress = new IntersectionAddress(intersectionAddressElement);
				Element landmarkAddressElement = element.getFirstChildElement(
					LandmarkAddress.getName(getDDMSVersion()), tspiNamespace);
				if (landmarkAddressElement != null)
					_tspiAddress = new LandmarkAddress(landmarkAddressElement);
				Element numberedThoroughfareAddressElement = element.getFirstChildElement(
					NumberedThoroughfareAddress.getName(getDDMSVersion()), tspiNamespace);
				if (numberedThoroughfareAddressElement != null)
					_tspiAddress = new NumberedThoroughfareAddress(numberedThoroughfareAddressElement);
				Element twoNumberAddressRangeElement = element.getFirstChildElement(
					TwoNumberAddressRange.getName(getDDMSVersion()), tspiNamespace);
				if (twoNumberAddressRangeElement != null)
					_tspiAddress = new TwoNumberAddressRange(twoNumberAddressRangeElement);
				Element unnumberedThoroughfareAddressElement = element.getFirstChildElement(
					UnnumberedThoroughfareAddress.getName(getDDMSVersion()), tspiNamespace);
				if (unnumberedThoroughfareAddressElement != null)
					_tspiAddress = new UnnumberedThoroughfareAddress(
						unnumberedThoroughfareAddressElement);
				Element uspsGeneralDeliveryOfficeElement = element.getFirstChildElement(
					USPSGeneralDeliveryOffice.getName(getDDMSVersion()), tspiNamespace);
				if (uspsGeneralDeliveryOfficeElement != null)
					_tspiAddress = new USPSGeneralDeliveryOffice(uspsGeneralDeliveryOfficeElement);
				Element uspsPostalDeliveryBoxElement = element.getFirstChildElement(
					USPSPostalDeliveryBox.getName(getDDMSVersion()), tspiNamespace);
				if (uspsPostalDeliveryBoxElement != null)
					_tspiAddress = new USPSPostalDeliveryBox(uspsPostalDeliveryBoxElement);
				Element uspsPostalDeliveryRouteElement = element.getFirstChildElement(
					USPSPostalDeliveryRoute.getName(getDDMSVersion()), tspiNamespace);
				if (uspsPostalDeliveryRouteElement != null)
					_tspiAddress = new USPSPostalDeliveryRoute(uspsPostalDeliveryRouteElement);
			}
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param streets the street address lines (0-6)
	 * @param city the city
	 * @param stateOrProvince the state or province
	 * @param postalCode the postal code
	 * @param countryCode the country code
	 * @param hasState true if the stateOrProvince is a state, false if it is a province
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public PostalAddress(List<String> streets, String city, String stateOrProvince, String postalCode,
		CountryCode countryCode, boolean hasState) throws InvalidDDMSException {
		try {
			if (streets == null)
				streets = Collections.emptyList();
			Element element = Util.buildDDMSElement(PostalAddress.getName(DDMSVersion.getCurrentVersion()), null);
			for (String street : streets) {
				element.appendChild(Util.buildDDMSElement(STREET_NAME, street));
			}
			Util.addDDMSChildElement(element, CITY_NAME, city);
			if (hasState)
				Util.addDDMSChildElement(element, STATE_NAME, stateOrProvince);
			else
				Util.addDDMSChildElement(element, PROVINCE_NAME, stateOrProvince);
			Util.addDDMSChildElement(element, POSTAL_CODE_NAME, postalCode);
			if (countryCode != null)
				element.appendChild(countryCode.getXOMElementCopy());
			_streets = streets;
			_city = city;
			_state = hasState ? stateOrProvince : "";
			_province = hasState ? "" : stateOrProvince;
			_postalCode = postalCode;
			_countryCode = countryCode;
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
	 * @param address a TSPI address
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public PostalAddress(ITspiAddress address) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(PostalAddress.getName(DDMSVersion.getCurrentVersion()), null);
			if (address != null) {
				element.appendChild(address.getXOMElementCopy());
				if (address instanceof GeneralAddressClass)
					_tspiAddress = (GeneralAddressClass) address;
				if (address instanceof IntersectionAddress)
					_tspiAddress = (IntersectionAddress) address;
				if (address instanceof LandmarkAddress)
					_tspiAddress = (LandmarkAddress) address;
				if (address instanceof NumberedThoroughfareAddress)
					_tspiAddress = (NumberedThoroughfareAddress) address;
				if (address instanceof TwoNumberAddressRange)
					_tspiAddress = (TwoNumberAddressRange) address;
				if (address instanceof UnnumberedThoroughfareAddress)
					_tspiAddress = (UnnumberedThoroughfareAddress) address;
				if (address instanceof USPSGeneralDeliveryOffice)
					_tspiAddress = (USPSGeneralDeliveryOffice) address;
				if (address instanceof USPSPostalDeliveryBox)
					_tspiAddress = (USPSPostalDeliveryBox) address;
				if (address instanceof USPSPostalDeliveryRoute)
					_tspiAddress = (USPSPostalDeliveryRoute) address;		
			}
			setXOMElement(element, true);
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
		Util.requireDDMSQName(getXOMElement(), PostalAddress.getName(getDDMSVersion()));
		if (!getDDMSVersion().isAtLeast("5.0")) {
			Util.requireBoundedChildCount(getXOMElement(), STREET_NAME, 0, 6);
			if (!Util.isEmpty(getState()) && !Util.isEmpty(getProvince())) {
				throw new InvalidDDMSException("Only 1 of state or province must be used.");
			}
			// Check for TSPI components is implicit, since they cannot be instantiated before DDMS 5.0.
		}
		else {
			if (!getStreets().isEmpty() || !Util.isEmpty(getCity()) || !Util.isEmpty(getState())
				|| !Util.isEmpty(getProvince()) || !Util.isEmpty(getPostalCode()) || getCountryCode() != null) {
				throw new InvalidDDMSException("postalAddress must be defined with a TSPI address.");
			}
			Util.requireDDMSValue("TSPI address", getTspiAddress());
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		if (!getDDMSVersion().isAtLeast("5.0") && getStreets().isEmpty() && Util.isEmpty(getCity()) && Util.isEmpty(getState())
			&& Util.isEmpty(getProvince()) && Util.isEmpty(getPostalCode()) && getCountryCode() == null) {
			addWarning("A completely empty ddms:postalAddress element was found.");
		}
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (!getDDMSVersion().isAtLeast("5.0")) {
			text.append(buildOutput(isHTML, localPrefix + STREET_NAME, getStreets()));
			text.append(buildOutput(isHTML, localPrefix + CITY_NAME, getCity()));
			text.append(buildOutput(isHTML, localPrefix + STATE_NAME, getState()));
			text.append(buildOutput(isHTML, localPrefix + PROVINCE_NAME, getProvince()));
			text.append(buildOutput(isHTML, localPrefix + POSTAL_CODE_NAME, getPostalCode()));
			if (getCountryCode() != null)
				text.append(getCountryCode().getOutput(isHTML, localPrefix, ""));
		}
		else {
			text.append(getTspiAddress().getOutput(isHTML, localPrefix, ""));
		}
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.add(getCountryCode());
		list.add(getTspiAddress());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof PostalAddress))
			return (false);
		PostalAddress test = (PostalAddress) obj;
		return (Util.listEquals(getStreets(), test.getStreets()) 
			&& getCity().equals(test.getCity())
			&& getState().equals(test.getState()) 
			&& getProvince().equals(test.getProvince())
			&& getPostalCode().equals(test.getPostalCode()));
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = 7 * result + getStreets().hashCode();
		result = 7 * result + getCity().hashCode();
		result = 7 * result + getState().hashCode();
		result = 7 * result + getProvince().hashCode();
		result = 7 * result + getPostalCode().hashCode();
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
		return ("postalAddress");
	}

	/**
	 * Accessor for the street addresses (max 6)
	 */
	public List<String> getStreets() {
		if (_streets == null)
			_streets = Collections.emptyList();
		return (Collections.unmodifiableList(_streets));
	}

	/**
	 * Accessor for the city
	 */
	public String getCity() {
		return (Util.getNonNullString(_city));
	}

	/**
	 * Accessor for the state
	 */
	public String getState() {
		return (Util.getNonNullString(_state));
	}

	/**
	 * Accessor for the province
	 */
	public String getProvince() {
		return (Util.getNonNullString(_province));
	}

	/**
	 * Accessor for the postalCode
	 */
	public String getPostalCode() {
		return (Util.getNonNullString(_postalCode));
	}

	/**
	 * Accessor for the country code
	 */
	public CountryCode getCountryCode() {
		return (_countryCode);
	}
	
	/**
	 * Accessor for the TSPI address
	 */
	public ITspiAddress getTspiAddress() {
		return (_tspiAddress);
	}
		
	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = 6887962646280796652L;
		private List<String> _streets;
		private String _city;
		private String _state;
		private String _province;
		private String _postalCode;
		private CountryCode.Builder _countryCode;
		private String _addressType;
		private GeneralAddressClass.Builder _generalAddressClass;
		private IntersectionAddress.Builder _intersectionAddress;
		private LandmarkAddress.Builder _landmarkAddress;
		private NumberedThoroughfareAddress.Builder _numberedThoroughfareAddress;
		private TwoNumberAddressRange.Builder _twoNumberAddressRange;
		private UnnumberedThoroughfareAddress.Builder _unnumberedThoroughfareAddress;
		private USPSGeneralDeliveryOffice.Builder _uspsGeneralDeliveryOffice;
		private USPSPostalDeliveryBox.Builder _uspsPostalDeliveryBox;
		private USPSPostalDeliveryRoute.Builder _uspsPostalDeliveryRoute;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(PostalAddress address) {
			DDMSVersion version = address.getDDMSVersion();
			setStreets(address.getStreets());
			setCity(address.getCity());
			setState(address.getState());
			setProvince(address.getProvince());
			setPostalCode(address.getPostalCode());
			if (address.getCountryCode() != null)
				setCountryCode(new CountryCode.Builder(address.getCountryCode()));
			if (address.getTspiAddress() != null) {
				setAddressType(address.getTspiAddress().getName());
				if (GeneralAddressClass.getName(version).equals(getAddressType()))
					setGeneralAddressClass(new GeneralAddressClass.Builder(
						(GeneralAddressClass) address.getTspiAddress()));
				if (IntersectionAddress.getName(version).equals(getAddressType()))
					setIntersectionAddress(new IntersectionAddress.Builder(
						(IntersectionAddress) address.getTspiAddress()));
				if (LandmarkAddress.getName(version).equals(getAddressType()))
					setLandmarkAddress(new LandmarkAddress.Builder((LandmarkAddress) address.getTspiAddress()));
				if (NumberedThoroughfareAddress.getName(version).equals(getAddressType()))
					setNumberedThoroughfareAddress(new NumberedThoroughfareAddress.Builder(
						(NumberedThoroughfareAddress) address.getTspiAddress()));
				if (TwoNumberAddressRange.getName(version).equals(getAddressType()))
					setTwoNumberAddressRange(new TwoNumberAddressRange.Builder(
						(TwoNumberAddressRange) address.getTspiAddress()));
				if (UnnumberedThoroughfareAddress.getName(version).equals(getAddressType()))
					setUnnumberedThoroughfareAddress(new UnnumberedThoroughfareAddress.Builder(
						(UnnumberedThoroughfareAddress) address.getTspiAddress()));
				if (USPSGeneralDeliveryOffice.getName(version).equals(getAddressType()))
					setUSPSGeneralDeliveryOffice(new USPSGeneralDeliveryOffice.Builder(
						(USPSGeneralDeliveryOffice) address.getTspiAddress()));
				if (USPSPostalDeliveryBox.getName(version).equals(getAddressType()))
					setUSPSPostalDeliveryBox(new USPSPostalDeliveryBox.Builder(
						(USPSPostalDeliveryBox) address.getTspiAddress()));
				if (USPSPostalDeliveryRoute.getName(version).equals(getAddressType()))
					setUSPSPostalDeliveryRoute(new USPSPostalDeliveryRoute.Builder(
						(USPSPostalDeliveryRoute) address.getTspiAddress()));
			}
		}

		/**
		 * @see IBuilder#commit()
		 */
		public PostalAddress commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				boolean hasStateAndProvince = (!Util.isEmpty(getState()) && !Util.isEmpty(getProvince()));
				if (hasStateAndProvince)
					throw new InvalidDDMSException("Only 1 of state or province must be used.");
				boolean hasState = !Util.isEmpty(getState());
				String stateOrProvince = hasState ? getState() : getProvince();
				return (new PostalAddress(getStreets(), getCity(), stateOrProvince, getPostalCode(),
					getCountryCode().commit(), hasState));
			}
			else {		
				return (new PostalAddress(commitSelectedAddress()));
			}
		}

		/**
		 * Commits the address which is active in this builder, based on the addressType.
		 * 
		 * @return the address
		 */
		protected ITspiAddress commitSelectedAddress() throws InvalidDDMSException {
			DDMSVersion version = DDMSVersion.getCurrentVersion();
			if (GeneralAddressClass.getName(version).equals(getAddressType()))
				return (getGeneralAddressClass().commit());
			if (IntersectionAddress.getName(version).equals(getAddressType()))
				return (getIntersectionAddress().commit());
			if (LandmarkAddress.getName(version).equals(getAddressType()))
				return (getLandmarkAddress().commit());
			if (NumberedThoroughfareAddress.getName(version).equals(getAddressType()))
				return (getNumberedThoroughfareAddress().commit());
			if (TwoNumberAddressRange.getName(version).equals(getAddressType()))
				return (getTwoNumberAddressRange().commit());
			if (UnnumberedThoroughfareAddress.getName(version).equals(getAddressType()))
				return (getUnnumberedThoroughfareAddress().commit());
			if (USPSGeneralDeliveryOffice.getName(version).equals(getAddressType()))
				return (getUSPSGeneralDeliveryOffice().commit());
			if (USPSPostalDeliveryBox.getName(version).equals(getAddressType()))
				return (getUSPSPostalDeliveryBox().commit());
			if (USPSPostalDeliveryRoute.getName(version).equals(getAddressType()))
				return (getUSPSPostalDeliveryRoute().commit());
			throw new InvalidDDMSException("Unknown address type: " + getAddressType());
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			return (Util.containsOnlyEmptyValues(getStreets()) && Util.isEmpty(getCity()) && Util.isEmpty(getState())
				&& Util.isEmpty(getProvince()) && Util.isEmpty(getPostalCode()) && getCountryCode().isEmpty()
				&& getGeneralAddressClass().isEmpty() && getIntersectionAddress().isEmpty()
				&& getLandmarkAddress().isEmpty() && getNumberedThoroughfareAddress().isEmpty()
				&& getTwoNumberAddressRange().isEmpty() && getUnnumberedThoroughfareAddress().isEmpty()
				&& getUSPSGeneralDeliveryOffice().isEmpty() && getUSPSPostalDeliveryBox().isEmpty() && getUSPSPostalDeliveryRoute().isEmpty());
		}

		/**
		 * Builder accessor for the streets
		 */
		public List<String> getStreets() {
			if (_streets == null)
				_streets = new LazyList(String.class);
			return _streets;
		}

		/**
		 * Builder accessor for the streets
		 */
		public void setStreets(List<String> streets) {
			_streets = new LazyList(streets, String.class);
		}

		/**
		 * Builder accessor for the city
		 */
		public String getCity() {
			return _city;
		}

		/**
		 * Builder accessor for the city
		 */
		public void setCity(String city) {
			_city = city;
		}

		/**
		 * Builder accessor for the state
		 */
		public String getState() {
			return _state;
		}

		/**
		 * Builder accessor for the state
		 */
		public void setState(String state) {
			_state = state;
		}

		/**
		 * Builder accessor for the province
		 */
		public String getProvince() {
			return _province;
		}

		/**
		 * Builder accessor for the province
		 */
		public void setProvince(String province) {
			_province = province;
		}

		/**
		 * Builder accessor for the postalCode
		 */
		public String getPostalCode() {
			return _postalCode;
		}

		/**
		 * Builder accessor for the postalCode
		 */
		public void setPostalCode(String postalCode) {
			_postalCode = postalCode;
		}

		/**
		 * Builder accessor for the countryCode
		 */
		public CountryCode.Builder getCountryCode() {
			if (_countryCode == null) {
				_countryCode = new CountryCode.Builder();
			}
			return _countryCode;
		}

		/**
		 * Builder accessor for the countryCode
		 */
		public void setCountryCode(CountryCode.Builder countryCode) {
			_countryCode = countryCode;
		}

		/**
		 * Builder accessor for the addressType
		 */
		public String getAddressType() {
			return _addressType;
		}

		/**
		 * Builder accessor for the addressType
		 */
		public void setAddressType(String addressType) {
			_addressType = addressType;
		}

		/**
		 * Builder accessor for the generalAddressClass
		 */
		public GeneralAddressClass.Builder getGeneralAddressClass() {
			if (_generalAddressClass == null) {
				_generalAddressClass = new GeneralAddressClass.Builder();
			}
			return _generalAddressClass;
		}

		/**
		 * Builder accessor for the generalAddressClass
		 */
		public void setGeneralAddressClass(GeneralAddressClass.Builder generalAddressClass) {
			_generalAddressClass = generalAddressClass;
		}

		/**
		 * Builder accessor for the intersectionAddress
		 */
		public IntersectionAddress.Builder getIntersectionAddress() {
			if (_intersectionAddress == null) {
				_intersectionAddress = new IntersectionAddress.Builder();
			}
			return _intersectionAddress;
		}

		/**
		 * Builder accessor for the intersectionAddress
		 */
		public void setIntersectionAddress(IntersectionAddress.Builder intersectionAddress) {
			_intersectionAddress = intersectionAddress;
		}

		/**
		 * Builder accessor for the landmarkAddress
		 */
		public LandmarkAddress.Builder getLandmarkAddress() {
			if (_landmarkAddress == null) {
				_landmarkAddress = new LandmarkAddress.Builder();
			}
			return _landmarkAddress;
		}

		/**
		 * Builder accessor for the landmarkAddress
		 */
		public void setLandmarkAddress(LandmarkAddress.Builder landmarkAddress) {
			_landmarkAddress = landmarkAddress;
		}

		/**
		 * Builder accessor for the numberedThoroughfareAddress
		 */
		public NumberedThoroughfareAddress.Builder getNumberedThoroughfareAddress() {
			if (_numberedThoroughfareAddress == null) {
				_numberedThoroughfareAddress = new NumberedThoroughfareAddress.Builder();
			}
			return _numberedThoroughfareAddress;
		}

		/**
		 * Builder accessor for the numberedThoroughfareAddress
		 */
		public void setNumberedThoroughfareAddress(NumberedThoroughfareAddress.Builder numberedThoroughfareAddress) {
			_numberedThoroughfareAddress = numberedThoroughfareAddress;
		}

		/**
		 * Builder accessor for the twoNumberAddressRange
		 */
		public TwoNumberAddressRange.Builder getTwoNumberAddressRange() {
			if (_twoNumberAddressRange == null) {
				_twoNumberAddressRange = new TwoNumberAddressRange.Builder();
			}
			return _twoNumberAddressRange;
		}

		/**
		 * Builder accessor for the twoNumberAddressRange
		 */
		public void setTwoNumberAddressRange(TwoNumberAddressRange.Builder twoNumberAddressRange) {
			_twoNumberAddressRange = twoNumberAddressRange;
		}

		/**
		 * Builder accessor for the unnumberedThoroughfareAddress
		 */
		public UnnumberedThoroughfareAddress.Builder getUnnumberedThoroughfareAddress() {
			if (_unnumberedThoroughfareAddress == null) {
				_unnumberedThoroughfareAddress = new UnnumberedThoroughfareAddress.Builder();
			}
			return _unnumberedThoroughfareAddress;
		}

		/**
		 * Builder accessor for the unnumberedThoroughfareAddress
		 */
		public void setUnnumberedThoroughfareAddress(UnnumberedThoroughfareAddress.Builder unnumberedThoroughfareAddress) {
			_unnumberedThoroughfareAddress = unnumberedThoroughfareAddress;
		}

		/**
		 * Builder accessor for the uspsGeneralDeliveryOffice
		 */
		public USPSGeneralDeliveryOffice.Builder getUSPSGeneralDeliveryOffice() {
			if (_uspsGeneralDeliveryOffice == null) {
				_uspsGeneralDeliveryOffice = new USPSGeneralDeliveryOffice.Builder();
			}
			return _uspsGeneralDeliveryOffice;
		}

		/**
		 * Builder accessor for the uspsGeneralDeliveryOffice
		 */
		public void setUSPSGeneralDeliveryOffice(USPSGeneralDeliveryOffice.Builder uspsGeneralDeliveryOffice) {
			_uspsGeneralDeliveryOffice = uspsGeneralDeliveryOffice;
		}

		/**
		 * Builder accessor for the uspsPostalDeliveryBox
		 */
		public USPSPostalDeliveryBox.Builder getUSPSPostalDeliveryBox() {
			if (_uspsPostalDeliveryBox == null) {
				_uspsPostalDeliveryBox = new USPSPostalDeliveryBox.Builder();
			}
			return _uspsPostalDeliveryBox;
		}

		/**
		 * Builder accessor for the uspsPostalDeliveryBox
		 */
		public void setUSPSPostalDeliveryBox(USPSPostalDeliveryBox.Builder uspsPostalDeliveryBox) {
			_uspsPostalDeliveryBox = uspsPostalDeliveryBox;
		}

		/**
		 * Builder accessor for the uspsPostalDeliveryRoute
		 */
		public USPSPostalDeliveryRoute.Builder getUSPSPostalDeliveryRoute() {
			if (_uspsPostalDeliveryRoute == null) {
				_uspsPostalDeliveryRoute = new USPSPostalDeliveryRoute.Builder();
			}
			return _uspsPostalDeliveryRoute;
		}

		/**
		 * Builder accessor for the uspsPostalDeliveryRoute
		 */
		public void setUSPSPostalDeliveryRoute(USPSPostalDeliveryRoute.Builder uspsPostalDeliveryRoute) {
			_uspsPostalDeliveryRoute = uspsPostalDeliveryRoute;
		}
	}
}