package buri.ddmsence.util.taglet;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A Javadoc Taglet which prints out a table footer for DDMSence-specific information
 * 
 * <p>
 * <b>Inputs:</b> None.
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class TableFooterTaglet extends AbstractInlineTaglet {

	private static final String NAME = "table.footer";

	/**
	 * Return the name of this custom tag.
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Given the <code>Tag</code> representation of this custom
	 * tag, return its string representation.
	 * 
	 * @param tag the <code>Tag</code> representation of this custom tag.
	 */
	public String toString(Tag tag) {
		StringBuffer b = new StringBuffer();
		b.append("</td></tr></table>\n");
		return (b.toString());
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		TableFooterTaglet tag = new TableFooterTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}