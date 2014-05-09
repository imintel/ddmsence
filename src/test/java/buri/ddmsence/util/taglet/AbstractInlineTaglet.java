package buri.ddmsence.util.taglet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A base class for custom inline javadoc Taglets.
 * 
 * <p>Default implementation allows tags to appear anywhere in comments.
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public abstract class AbstractInlineTaglet implements Taglet {

	/**
	 * Return the name of this custom tag.
	 */
	public abstract String getName();

	/**
	 * Given the <code>Tag</code> representation of this custom
	 * tag, return its string representation.
	 * 
	 * @param tag the <code>Tag</code> representation of this custom tag.
	 */
	public abstract String toString(Tag tag);

	/**
	 * @return true since this tag can be used in a field doc comment
	 */
	public boolean inField() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a constructor doc comment
	 */
	public boolean inConstructor() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a method doc comment
	 */
	public boolean inMethod() {
		return true;
	}

	/**
	 * @return true since this tag can be used in an overview doc comment
	 */
	public boolean inOverview() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a package doc comment
	 */
	public boolean inPackage() {
		return true;
	}

	/**
	 * @return true since this tag can be used in a type doc comment
	 */
	public boolean inType() {
		return true;
	}

	/**
	 * @return true since this is an inline tag.
	 */
	public boolean isInlineTag() {
		return true;
	}

	/**
	 * This method should not be called since arrays of inline tags do not
	 * exist. Method {@link #tostring(Tag)} should be used to convert this
	 * inline tag to a string.
	 * 
	 * @param tags the array of <code>Tag</code>s representing of this custom tag.
	 */
	public String toString(Tag[] tags) {
		return null;
	}
}