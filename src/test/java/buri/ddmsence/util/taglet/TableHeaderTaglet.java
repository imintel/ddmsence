package buri.ddmsence.util.taglet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import buri.ddmsence.util.Util;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A Javadoc Taglet which prints out a table header for DDMSence-specific information
 * 
 * <p>
 * <b>Inputs:</b> the title to display in the header. Some titles will generate additional header infrastructure.
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class TableHeaderTaglet extends AbstractInlineTaglet {

	private static final String NAME = "table.header";
	private static final Set<String> CHILD_INFO_TABLES = new HashSet<String>();
	private static final Set<String> RULE_TABLES = new HashSet<String>();
	static {
		CHILD_INFO_TABLES.add("Nested Elements");
		CHILD_INFO_TABLES.add("Attributes");
	}
	static {
		RULE_TABLES.add("Validation Rules");
	}

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
		String title = Util.getNonNullString(tag.text());
		StringBuffer b = new StringBuffer();
		b.append("<table class=\"info\" cellspacing=\"0\"><tr class=\"infoHeader\"><th>");
		b.append(title).append("</th></tr><tr><td>\n");
		for (String testTitle : CHILD_INFO_TABLES) {
			if (title.startsWith(testTitle)) {
				b.append("<table class=\"childInfo\" cellspacing=\"0\"><tr class=\"childInfoHeader\">");
				b.append("<td class=\"name\">Name</td><td class=\"cardinality\">Cardinality</td>");
				b.append("<td class=\"versions\">Availability</td></tr></table>\n");
			}
		}
		for (String testTitle : RULE_TABLES) {
			if (title.startsWith(testTitle)) {
				b.append("<table class=\"rules\" cellspacing=\"0\"><tr class=\"rulesHeader\">");
				b.append("<td class=\"rule\">Rule</td><td class=\"type\">Type</td>");
				b.append("<td class=\"versions\">Applicability</td></tr></table>\n");
			}
		}
		return (b.toString());
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		TableHeaderTaglet tag = new TableHeaderTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}