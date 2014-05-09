package buri.ddmsence.util.taglet;

import java.util.Map;

import buri.ddmsence.util.Util;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A Javadoc Taglet which prints out a standard table for nested elements and attributes.
 * 
 * <p>
 * <b>Inputs:</b> A pipe-delimited string containing XML name, cardinality, and ddmsVersion info.
 * </p>
 * 
 * <p>
 * <b>Example:</b> <code>ntk:AccessSystemName|1|11111</code>
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class DDMSChildInfoTaglet extends AbstractInlineTaglet {

	private static final String NAME = "child.info";
	private static final String DELIMITER = "\\|";

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
		String[] input = Util.getNonNullString(tag.text()).split(DELIMITER);
		if (input.length != 3)
			throw new IllegalArgumentException("This taglet requires 4 pipe-delimited values.");
		StringBuffer b = new StringBuffer();
		b.append("<table class=\"childInfo\" cellspacing=\"0\"><tr class=\"childInfo\">");
		b.append("<td class=\"name\">").append(input[0]).append("</td>");
		b.append("<td class=\"cardinality\">").append(input[1]).append("</td>");
		b.append("<td class=\"versions\">").append(DDMSVersionTaglet.getTableFor(input[2])).append("</td>");
		b.append("</tr></table>\n");
		return (b.toString());
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		DDMSChildInfoTaglet tag = new DDMSChildInfoTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}