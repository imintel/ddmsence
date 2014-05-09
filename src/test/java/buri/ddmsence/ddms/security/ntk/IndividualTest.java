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
 * <p> Tests related to ntk:AccessIndividual elements </p>
 * 
 * @author Brian Uri!
 * @since 2.0.0
 */
public class IndividualTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public IndividualTest() {
		super("accessIndividual.xml");
		removeSupportedVersions("2.0 3.0 3.1 5.0");
	}

	/**
	 * Returns a fixture object for testing.
	 */
	public static Individual getFixture() {
		try {
			return (new Individual(SystemNameTest.getFixture(), IndividualValueTest.getFixtureList(),
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
	public static List<Individual> getFixtureList() {
		List<Individual> list = new ArrayList<Individual>();
		list.add(IndividualTest.getFixture());
		return (list);
	}

	/**
	 * Attempts to build a component from a XOM element.
	 * @param element the element to build from
	 * @param message an expected error message. If empty, the constructor is expected to succeed.
	 * 
	 * @return a valid object
	 */
	private Individual getInstance(Element element, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Individual component = null;
		try {
			component = new Individual(element);
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
	private Individual getInstance(Individual.Builder builder, String message) {
		boolean expectFailure = !Util.isEmpty(message);
		Individual component = null;
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
	private Individual.Builder getBaseBuilder() {
		DDMSVersion version = DDMSVersion.getCurrentVersion();
		Individual component = getInstance(getValidElement(version.getVersion()), SUCCESS);
		return (new Individual.Builder(component));
	}

	/**
	 * Returns the expected HTML or Text output for this unit test
	 */
	private String getExpectedOutput(boolean isHTML) throws InvalidDDMSException {
		StringBuffer text = new StringBuffer();
		text.append(SystemNameTest.getFixture().getOutput(isHTML, "individual.", ""));
		text.append(IndividualValueTest.getFixtureList().get(0).getOutput(isHTML, "individual.", ""));
		text.append(buildOutput(isHTML, "individual.classification", "U"));
		text.append(buildOutput(isHTML, "individual.ownerProducer", "USA"));
		return (text.toString());
	}

	/**
	 * Returns the expected XML output for this unit test
	 */
	private String getExpectedXMLOutput() {
		StringBuffer xml = new StringBuffer();
		xml.append("<ntk:AccessIndividual ").append(getXmlnsNTK()).append(" ").append(getXmlnsISM()).append(" ");
		xml.append("ism:classification=\"U\" ism:ownerProducer=\"USA\">");
		xml.append("<ntk:AccessSystemName ism:classification=\"U\" ism:ownerProducer=\"USA\">DIAS</ntk:AccessSystemName>");
		xml.append("<ntk:AccessIndividualValue ism:classification=\"U\" ism:ownerProducer=\"USA\">user_2321889:Doe_John_H</ntk:AccessIndividualValue>");
		xml.append("</ntk:AccessIndividual>");
		return (xml.toString());
	}

	public void testNameAndNamespace() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);

			assertNameAndNamespace(getInstance(getValidElement(sVersion), SUCCESS), DEFAULT_NTK_PREFIX,
				Individual.getName(version));
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
			Individual.Builder builder = getBaseBuilder();
			builder.setSystemName(null);
			getInstance(builder, "systemName must exist.");

			// Missing groupValue
			builder = getBaseBuilder();
			builder.getIndividualValues().clear();
			getInstance(builder, "At least one individual value must exist.");

			// Missing security attributes
			builder = getBaseBuilder();
			builder.setSecurityAttributes(null);
			getInstance(builder, "classification must exist.");
			
			// Null individualValue param
			try {
				new Individual(SystemNameTest.getFixture(), null, SecurityAttributesTest.getFixture());
			}
			catch (InvalidDDMSException e) {
				expectMessage(e, "At least one individual value");
			}
		}
	}
	
	public void testValidationWarnings() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// No warnings
			Individual component = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(0, component.getValidationWarnings().size());
		}
	}

	public void testEquality() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			// Base equality
			Individual elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			Individual builderComponent = new Individual.Builder(elementComponent).commit();
			assertEquals(elementComponent, builderComponent);
			assertEquals(elementComponent.hashCode(), builderComponent.hashCode());

			// Different values in each field
			Individual.Builder builder = getBaseBuilder();
			builder.getSystemName().setValue(DIFFERENT_VALUE);
			assertFalse(elementComponent.equals(builder.commit()));
			
			builder = getBaseBuilder();
			builder.getIndividualValues().get(0).setValue(DIFFERENT_VALUE);
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

			Individual elementComponent = getInstance(getValidElement(sVersion), SUCCESS);
			assertEquals(getExpectedOutput(true), elementComponent.toHTML());
			assertEquals(getExpectedOutput(false), elementComponent.toText());
			assertEquals(getExpectedXMLOutput(), elementComponent.toXML());
		}
	}

	public void testBuilderIsEmpty() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);

			Individual.Builder builder = new Individual.Builder();
			assertNull(builder.commit());
			assertTrue(builder.isEmpty());
			builder.getIndividualValues().get(0);
			assertTrue(builder.isEmpty());
			
			builder.getIndividualValues().get(1).setValue("TEST");
			assertFalse(builder.isEmpty());
		}
	}
	
	public void testBuilderLazyList() throws InvalidDDMSException {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion.setCurrentVersion(sVersion);
			Individual.Builder builder = new Individual.Builder();
			assertNotNull(builder.getIndividualValues().get(1));
		}
	}
}
