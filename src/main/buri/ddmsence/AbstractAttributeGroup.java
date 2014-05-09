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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;

/**
 * Top-level base class for attribute groups, such as {@link SecurityAttributes}.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p>Extensions of this class are generally expected to be immutable. It is assumed that after the constructor on
 * a component has been called, the component will be well-formed and valid.</p>
 * 
 * {@table.header History}
 * 		None.
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		None.
 * {@table.footer}
 * {@table.header Attributes}
 * 		None. Extending classes will have additional attributes.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		No rules are validated at this level. Extending classes may have additional rules.
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 1.1.0
 */
public abstract class AbstractAttributeGroup {

	private String _namespace = null;
	private List<ValidationMessage> _warnings = null;

	protected final static String INCOMPATIBLE_VERSION_MESSAGE = "The DDMS version of the parent component is incompatible with the XML namespace of these attributes.";

	/**
	 * Empty constructor
	 */
	public AbstractAttributeGroup() {}

	/**
	 * Base validation case for attribute groups.
	 * 
	 * @param version the DDMS version to validate against. This cannot be stored in the attribute group because some
	 *        DDMSVersions have the same attribute XML namespace (e.g. XLink, ISM, NTK, GML after DDMS 2.0).
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	protected void validate(DDMSVersion version) throws InvalidDDMSException {}

	/**
	 * Returns a list of any warning messages that occurred during validation. Warnings do not prevent a valid component
	 * from being formed.
	 * 
	 * @return a list of warnings
	 */
	public List<ValidationMessage> getValidationWarnings() {
		return (Collections.unmodifiableList(getWarnings()));
	}

	/**
	 * Accessor for the list of validation warnings.
	 * 
	 * <p>
	 * This is the private copy that should be manipulated during validation. Lazy initialization.
	 * </p>
	 * 
	 * @return an editable list of warnings
	 */
	protected List<ValidationMessage> getWarnings() {
		if (_warnings == null)
			_warnings = new ArrayList<ValidationMessage>();
		return (_warnings);
	}

	/**
	 * Outputs to HTML or Text with a prefix at the beginning of each meta tag or line.
	 * 
	 * @param isHTML true for HTML, false for Text
	 * @param prefix the prefix to add
	 * @return the HTML or Text output
	 */
	public abstract String getOutput(boolean isHTML, String prefix);

	/**
	 * Accessor for the XML namespace of these attributes
	 */
	protected String getNamespace() {
		return (_namespace);
	}

	/**
	 * Accessor for the XML namespace of these attributes
	 */
	protected void setNamespace(String namespace) {
		_namespace = namespace;
	}
}
