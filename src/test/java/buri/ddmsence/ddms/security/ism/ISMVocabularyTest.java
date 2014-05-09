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
package buri.ddmsence.ddms.security.ism;

import buri.ddmsence.AbstractBaseTestCase;
import buri.ddmsence.util.DDMSVersion;

/**
 * <p> Tests related to the ISM Controlled Vocabularies </p>
 * 
 * @author Brian Uri!
 * @since 0.9.d
 */
public class ISMVocabularyTest extends AbstractBaseTestCase {

	/**
	 * Constructor
	 */
	public ISMVocabularyTest() {
		super(null);
	}

	public void testBadKey() {
		try {
			ISMVocabulary.getEnumerationTokens("unknownKey");
			fail("Allowed invalid key.");
		}
		catch (IllegalArgumentException e) {
			expectMessage(e, "No controlled vocabulary could be found");
		}
	}

	public void testEnumerationTokens() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			ISMVocabulary.setDDMSVersion(version);
			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_ALL_CLASSIFICATIONS, "C"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_ALL_CLASSIFICATIONS, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_OWNER_PRODUCERS, "AUS"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_OWNER_PRODUCERS, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_SCI_CONTROLS, "HCS"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_SCI_CONTROLS, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "FOUO"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_FGI_SOURCE_OPEN, "ABW"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_FGI_SOURCE_OPEN, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_FGI_SOURCE_PROTECTED, "ABW"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_FGI_SOURCE_PROTECTED, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_RELEASABLE_TO, "ABW"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_RELEASABLE_TO, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_NON_IC_MARKINGS, "DS"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_NON_IC_MARKINGS, "unknown"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DECLASS_EXCEPTION, "25X1"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_DECLASS_EXCEPTION, "unknown"));

			if (!version.isAtLeast("3.1")) {
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_TYPE_EXEMPTED_SOURCE, "X1"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_TYPE_EXEMPTED_SOURCE, "unknown"));
			}

			if (version.isAtLeast("3.1")) {
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_ATOMIC_ENERGY_MARKINGS, "RD"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_ATOMIC_ENERGY_MARKINGS, "unknown"));

				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_COMPLIES_WITH, "DoD5230.24"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_COMPLIES_WITH, "unknown"));

				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISPLAY_ONLY_TO, "ABW"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISPLAY_ONLY_TO, "unknown"));

				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_NON_US_CONTROLS, "ATOMAL"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_NON_US_CONTROLS, "unknown"));
			}

			if (version.isAtLeast("4.0.1")) {
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_NOTICE_TYPE, "DoD-Dist-B"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_NOTICE_TYPE, "unknown"));

				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_POC_TYPE, "DoD-Dist-B"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_POC_TYPE, "unknown"));
			}
		}
	}

	public void testEnumerationPatterns() {
		for (String sVersion : getSupportedVersions()) {
			DDMSVersion version = DDMSVersion.setCurrentVersion(sVersion);
			ISMVocabulary.setDDMSVersion(version);
			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_SCI_CONTROLS, "SI-G-ABCD"));
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_SCI_CONTROLS, "SI-G-ABCDE"));

			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_SAR_IDENTIFIER, "SAR-ABC"));
			assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_SAR_IDENTIFIER, "SAR-AB"));
			
			StringBuffer b = new StringBuffer("SAR-");
			for (int i = 0; i < 11; i++) {
				b.append("0123456789");
			}
			assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_SAR_IDENTIFIER, b.toString()));

			if (!version.isAtLeast("3.1")) {
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "RD-SG-1"));
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "RD-SG-12"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "RD-SG-100"));

				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "FRD-SG-1"));
				assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "FRD-SG-12"));
				assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_DISSEMINATION_CONTROLS, "FRD-SG-100"));
			}
		}
	}

	public void testIsUSMarking() {
		ISMVocabulary.setDDMSVersion(DDMSVersion.getVersionFor("2.0"));
		assertTrue(ISMVocabulary.enumContains(ISMVocabulary.CVE_US_CLASSIFICATIONS, "TS"));
		assertFalse(ISMVocabulary.enumContains(ISMVocabulary.CVE_US_CLASSIFICATIONS, "CTS"));
	}

	public void testInvalidMessage() {
		assertEquals("Dog is not a valid enumeration token for this attribute, as specified in Cat.",
			ISMVocabulary.getInvalidMessage("Cat", "Dog"));
	}
}
