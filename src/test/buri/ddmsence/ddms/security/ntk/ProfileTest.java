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
package buri.ddmsence.ddms.security.ntk;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.security.ism.SecurityAttributesTest;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

/**
 * <p> Tests related to ntk:AccessProfile elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class ProfileTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public ProfileTest() {
		super("accessProfile.xml");
		removeSupportedVersions("2.0 3.0 3.1 5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Profile getFixture() {
		try {
			return (new Profile(SystemNameTest.getFixture(), ProfileValueTest.getFixtureList(),
				SecurityAttributesTest.getFixture()));
		}
		catch (InvalidDDMSException e) {
			fail("Could not create fixture: " + e.getMessage());
		}
		return (null);
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static List<Profile> getFixtureList() {
		List<Profile> profiles = new ArrayList<Profile>();
		profiles.add(ProfileTest.getFixture());
		return (profiles);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Profile getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Profile component = null;
		try {
			component = new Profile(element);
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (component);
	}

	/**
	 * Helper method to create an object which is expected to be valid.
	 * 
	 * @param builder the builder to commit
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Profile getInstance(Profile.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Profile component = null;
		try {
			component = builder.commit();
			checkConstructorSuccess(expectFailure);
		}
		catch (InvalidDDMSException e) {
			checkConstructorFailure(expectFailure, e);
			expectMessage(e, message);
		}
		return (component);
	}

	/**
	 * Returns a builder, pre-populated with base data from the XML sample.
	 * 
	 * This builder can then be modified to test various conditions.
	 */
	private Profile.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Profile component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Profile.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(SystemNameTest.getFixture().getOutput(isHTML, "profile.", ""));
		text.append(ProfileValueTest.getFixtureList().get(0).getOutput(isHTML, "profile.", ""));
		text.append(buildOutput(isHTML, "profile.classification", "U"));
		text.append(buildOutput(isHTML, "profile.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ntk:AccessProfile ").append(getXmlnsNTK()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>");
		xml.append("<ntk:AccessProfileValue ism:classification=\"U\" ism:ownerProducer=\"USA\" ntk:vocabulary=\"vocabulary\">profile</ntk:AccessProfileValue>");
		xml.append("</ntk:AccessProfile>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_NTK_PREFIX,
				Profile.getName(version));
			getInstance(getWrongNameElementFixture(), WRONG_NAME_MESSAGE);
		}
	}

	public void testConstructors() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Element-based
			getInstance(getValidElement(sVersion), SUCCESS);

			// Data-based via Builder
			getBaseBuilder();
		}
	}
	
	public void testConstructorsMinimal() throws InvalidDDMSException {
		// No tests.
	}

	public void testValidationErrors() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Missing systemName
			Profile.Builder builder = getBaseBuilder();
			builder.setSystemName(null);
			getInstance(builder, "systemName must exist.");

			// Missing groupValue
			builder = getBaseBuilder();
			builder.getProfileValues().clear();
			getInstance(builder, "At least one profile value must exist.");

			// Missing security attributes
			builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
			
			// Null groupValue param
			try {
				new Profile(SystemNameTest.getFixture(), null, SecurityAttributesTest.getFixture());
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least one profile value");
			}
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Profile component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Profile elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Profile builderComponent = new Profile.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Profile.Builder builder = getBaseBuilder();
			builder.getSystemName().setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getProfileValues().get(0).setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
		}
	}

	public void testVersionSpecific() {
		// Pre-4.0.1 test is implicit, since NTK namespace did not exist.
		// Post-4.1 test is handled in MetacardInfoTest.
	}

	public void testOutput() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Profile elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Profile.Builder builder = new Profile.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.getProfileValues().get(0);
			assertTrue(builder.isEmpty());
			
			builder.getProfileValues().get(1).setValue("TEST");
			assertFalse(builder.isEmpty());
		}
	}

	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Profile.Builder builder = new Profile.Builder();
			assertNotNull(builder.getProfileValues().get(1));
		}
	}}
