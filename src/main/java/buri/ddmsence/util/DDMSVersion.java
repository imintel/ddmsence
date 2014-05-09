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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import buri.ddmsence.ddms.UnsupportedVersionException;
import buri.ddmsence.ddms.security.ism.ISMVocabulary;

/**
 * Manages the supported versions of DDMS.
 * 
 * <p>
 * This class is the extension point for supporting new DDMS versions in the future. DDMSVersion maintains a static
 * currentVersion variable which can be set at runtime. All DDMS component constructors which build components from
 * scratch can then call <code>DDMSVersion.getCurrentVersion()</code> to access various details such as schema
 * locations and namespace URIs. If no currentVersion has been set, a default will be used, which maps to
 * <code>buri.ddmsence.ddms.defaultVersion</code> in the properties file. This defaults to 4.1 right now.</p>
 * 
 * <p>
 * The ddmsence.properties file has a property, <code>ddms.supportedVersions</code> which can be a comma-separated list
 * of version
 * numbers. Each of these token values then has a set of properties which identify the namespace and schema locations
 * for each DDMS version:
 * </p>
 * 
 * <li><code>&lt;versionNumber&gt;.ddms.xmlNamespace</code>: i.e. "urn:us:mil:ces:metadata:ddms:5"</li>
 * <li><code>&lt;versionNumber&gt;.ddms.xsdLocation</code>: i.e. "/schemas/5.0/DDMS/ddms.xsd"</li>
 * <li><code>&lt;versionNumber&gt;.gml.xmlNamespace</code>: i.e. "http://www.opengis.net/gml/3.2"</li>
 * <li><code>&lt;versionNumber&gt;.gml.xsdLocation</code>: i.e. "/schemas/5.0/DDMS/gml.xsd"</li>
 * <li><code>&lt;versionNumber&gt;.ism.cveLocation</code>: i.e. "/schemas/5.0/ISM/CVE/"</li>
 * <li><code>&lt;versionNumber&gt;.ism.xmlNamespace</code>: i.e. "urn:us:gov:ic:ism"</li>
 * <li><code>&lt;versionNumber&gt;.ntk.xmlNamespace</code>: i.e. "urn:us:gov:ic:ntk"</li>
 * <li><code>&lt;versionNumber&gt;.ntk.xsdLocation</code>: i.e. "/schemas/5.0/NTK/IC-NTK.xsd"</li>
 * <li><code>&lt;versionNumber&gt;.tspi.xmlNamespace</code>: i.e. "http://metadata.ces.mil/mdr/ns/GSIP/tspi/2.0"</li>
 * <li><code>&lt;versionNumber&gt;.tspi.xsdLocation</code>: i.e. "/schemas/5.0/tspi/2.0.0/tspi.xsd"</li>
 * <li><code>&lt;versionNumber&gt;.virt.xmlNamespace</code>: i.e. "urn:us:gov:ic:virt"</li>
 * <li><code>&lt;versionNumber&gt;.xlink.xmlNamespace</code>: i.e. "http://www.w3.org/1999/xlink"</li>
 * 
 * <p>
 * The format of an xsdLocation should generally follow
 * <code>/schemas/&lt;versionNumber&gt;/schemaLocationInDataDirectory</code>.
 * </p>
 * 
 * <p><u>Version-specific Notes:</u></p>
 * 
 * <p>Because DDMS 3.0.1 is syntactically identical to DDMS 3.0, requests for version 3.0.1
 * will simply alias to DDMS 3.0. DDMS 3.0.1 is not set up as a separate batch of schemas and namespaces,
 * since none of the technical artifacts changed (3.0.1 was a documentation release).
 * </p>
 * 
 * <p>Because DDMS 4.1 uses the same XML namespace as DDMS 4.0, resolving the XML namespace to a version
 * will always return 4.1 (because it is newer). 4.0.1 is now an alias for 4.1, and warnings will appear when
 * using new 4.1 components.</p>
 * 
 * <p>
 * This class is intended for use in a single-threaded environment.
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class DDMSVersion {

	private String _version;
	private String _namespace;
	private String _schema;

	private String _gmlNamespace;
	private String _gmlSchema;
	private String _ismCveLocation;
	private String _ismNamespace;
	private String _ntkNamespace;
	private String _ntkSchema;
	private String _tspiNamespace;
	private String _tspiSchema;
	private String _virtNamespace;
	private String _xlinkNamespace;

	private static DDMSVersion _currentVersion;

	private static final Map<String, DDMSVersion> VERSIONS_TO_DETAILS = new TreeMap<String, DDMSVersion>();
	static {
		for (String version : PropertyReader.getListProperty("ddms.supportedVersions")) {
			VERSIONS_TO_DETAILS.put(version, new DDMSVersion(version));
		}
		_currentVersion = getVersionFor(PropertyReader.getProperty("ddms.defaultVersion"));
	}

	/**
	 * Private to prevent instantiation
	 * 
	 * @param version the number as shown in ddms.supportedVersions.
	 */
	private DDMSVersion(String version) {
		int index = getSupportedVersionsProperty().indexOf(version);
		_version = version;
		_namespace = getSupportedDDMSNamespacesProperty().get(index);
		_schema = PropertyReader.getProperty(version + ".ddms.xsdLocation");
		_gmlNamespace = PropertyReader.getProperty(version + ".gml.xmlNamespace");
		_gmlSchema = PropertyReader.getProperty(version + ".gml.xsdLocation");
		_ismCveLocation = PropertyReader.getProperty(version + ".ism.cveLocation");
		_ismNamespace = PropertyReader.getProperty(version + ".ism.xmlNamespace");
		_ntkNamespace = PropertyReader.getProperty(version + ".ntk.xmlNamespace");
		_ntkSchema = PropertyReader.getProperty(version + ".ntk.xsdLocation");
		_tspiNamespace = PropertyReader.getProperty(version + ".tspi.xmlNamespace");
		_tspiSchema = PropertyReader.getProperty(version + ".tspi.xsdLocation");
		_virtNamespace = PropertyReader.getProperty(version + ".virt.xmlNamespace");
		_xlinkNamespace = PropertyReader.getProperty(version + ".xlink.xmlNamespace");
	}

	/**
	 * Convenience method to check if a DDMS version number is equal to or higher that some
	 * test number. An example of where this might be used is to determine the capitalization
	 * of element names, many of which changed in DDMS 4.0.1.
	 * 
	 * @param version the version number to check
	 * @return true if the version is equal to or greater than the test version
	 */
	public boolean isAtLeast(String version) {
		version = aliasVersion(version);
		if (!getSupportedVersionsProperty().contains(version))
			throw new UnsupportedVersionException(version);
		int index = getSupportedVersionsProperty().indexOf(this.getVersion());
		int testIndex = getSupportedVersionsProperty().indexOf(version);
		return (index >= testIndex);
	}

	/**
	 * Returns a list of supported DDMS versions
	 * 
	 * @return List of string version numbers
	 */
	public static List<String> getSupportedVersions() {
		return Collections.unmodifiableList(getSupportedVersionsProperty());
	}

	/**
	 * Private accessor for the property containing the supported versions list
	 * 
	 * @return List of String version numbers
	 */
	private static List<String> getSupportedVersionsProperty() {
		return (PropertyReader.getListProperty("ddms.supportedVersions"));
	}

	/**
	 * Private accessor for the property containing the supported DDMS XML namespace list
	 * 
	 * @return List of String version numbers
	 */
	private static List<String> getSupportedDDMSNamespacesProperty() {
		List<String> supportedNamespaces = new ArrayList<String>();
		for (String version : getSupportedVersionsProperty()) {
			supportedNamespaces.add(PropertyReader.getProperty(version + ".ddms.xmlNamespace"));
		}
		return (supportedNamespaces);
	}

	/**
	 * Checks if an XML namespace is included in the list of supported XML namespaces for DDMS
	 * 
	 * @param xmlNamespace the namespace to test
	 * @return true if the namespace is supported
	 */
	public static boolean isSupportedDDMSNamespace(String xmlNamespace) {
		return (getSupportedDDMSNamespacesProperty().contains(xmlNamespace));
	}

	/**
	 * Returns the DDMSVersion instance mapped to a particular version number.
	 * 
	 * @param version a version number
	 * @return the instance
	 * @throws UnsupportedVersionException if the version number is not supported
	 */
	public static DDMSVersion getVersionFor(String version) {
		version = aliasVersion(version);
		if (!getSupportedVersionsProperty().contains(version))
			throw new UnsupportedVersionException(version);
		return (VERSIONS_TO_DETAILS.get(version));
	}

	/**
	 * Returns the DDMSVersion instance mapped to a particular XML namespace. If the
	 * namespace is shared by multiple versions of DDMS, the most recent will be
	 * returned.
	 * 
	 * @param namespace the XML namespace
	 * @return the instance
	 * @throws UnsupportedVersionException if the version number is not supported
	 */
	public static DDMSVersion getVersionForNamespace(String namespace) {
		List<DDMSVersion> versions = new ArrayList<DDMSVersion>(VERSIONS_TO_DETAILS.values());
		Collections.reverse(versions);
		for (DDMSVersion version : versions) {
			if (version.getNamespace().equals(namespace) || version.getIsmNamespace().equals(namespace)
				|| version.getNtkNamespace().equals(namespace) || version.getGmlNamespace().equals(namespace)
				|| version.getTspiNamespace().equals(namespace) || version.getVirtNamespace().equals(namespace)
				|| version.getXlinkNamespace().equals(namespace)) {
				return (version);
			}
		}
		throw new UnsupportedVersionException("for XML namespace " + namespace);
	}

	/**
	 * Sets the currentVersion which will be used for by DDMS component constructors to determine the namespace and
	 * schema to use. Also updates the ISMVersion on the ISMVocabulary class, which is used to determine
	 * which set of IC CVEs to validate with.
	 * 
	 * @param version the new version, which must be supported by DDMSence
	 * @return the version which was just set, as a full-fledged DDMSVersion object
	 * @throws UnsupportedVersionException if the version is not supported
	 */
	public static synchronized DDMSVersion setCurrentVersion(String version) {
		version = aliasVersion(version);
		if (!getSupportedVersionsProperty().contains(version))
			throw new UnsupportedVersionException(version);
		_currentVersion = getVersionFor(version);
		ISMVocabulary.setDDMSVersion(getCurrentVersion());
		return (getCurrentVersion());
	}

	/**
	 * Treats version 3.0.1 of DDMS as an alias for DDMS 3.0, and treats version 4.0.1 as an alias for DDMS 4.1.
	 * 3.0.1 is syntactically identical, and has the same namespaces and schemas. 4.0.1 shares the same
	 * XML namespace as 4.1.
	 * 
	 * @param version the raw version
	 * @return the aliased version
	 */
	private static String aliasVersion(String version) {
		if ("3.0.1".equals(version))
			return ("3.0");
		if ("4.0.1".equals(version))
			return ("4.1");
		return (version);
	}

	/**
	 * Accessor for the current version. If not set, returns the default from the properties file.
	 */
	public static DDMSVersion getCurrentVersion() {
		return (_currentVersion);
	}

	/**
	 * Resets the current version to the default value.
	 */
	public static void clearCurrentVersion() {
		setCurrentVersion(PropertyReader.getProperty("ddms.defaultVersion"));
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return (getVersion());
	}

	/**
	 * Accessor for the version number
	 */
	public String getVersion() {
		return _version;
	}

	/**
	 * Accessor for the DDMS namespace
	 */
	public String getNamespace() {
		return _namespace;
	}

	/**
	 * Accessor for the DDMS schema location
	 */
	public String getSchema() {
		return _schema;
	}

	/**
	 * Accessor for the gml namespace
	 */
	public String getGmlNamespace() {
		return _gmlNamespace;
	}

	/**
	 * Accessor for the gml schema location
	 */
	public String getGmlSchema() {
		return _gmlSchema;
	}

	/**
	 * Accessor for the ISM CVE location
	 */
	public String getIsmCveLocation() {
		return _ismCveLocation;
	}

	/**
	 * Accessor for the ISM namespace
	 */
	public String getIsmNamespace() {
		return _ismNamespace;
	}

	/**
	 * Accessor for the NTK namespace
	 */
	public String getNtkNamespace() {
		return _ntkNamespace;
	}

	/**
	 * Accessor for the NTK schema location
	 */
	public String getNtkSchema() {
		return _ntkSchema;
	}

	/**
	 * Accessor for the tspi namespace
	 */
	public String getTspiNamespace() {
		return _tspiNamespace;
	}
	
	/**
	 * Accessor for the tspi schema location
	 */
	public String getTspiSchema() {
		return _tspiSchema;
	}
	
	/**
	 * Accessor for the virt namespace
	 */
	public String getVirtNamespace() {
		return _virtNamespace;
	}
	
	/**
	 * Accessor for the xlink namespace
	 */
	public String getXlinkNamespace() {
		return _xlinkNamespace;
	}
}
