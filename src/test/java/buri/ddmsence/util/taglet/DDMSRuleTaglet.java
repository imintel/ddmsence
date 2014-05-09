package buri.ddmsence.util.taglet;

import java.util.Map;

import buri.ddmsence.util.Util;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A Javadoc Taglet which prints out a single rule in a validate() method.
 * 
 * <p>
 * <b>Inputs:</b> A pipe-delimited string containing rule, type, and ddmsVersion info.
 * </p>
 * 
 * <p>
 * <b>Example:</b> <code>Something must exist.|Error|11111</code>
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class DDMSRuleTaglet extends AbstractInlineTaglet {

	private static final String NAME = "ddms.rule";
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
			throw new IllegalArgumentException("This taglet requires 3 pipe-delimited values.");
		StringBuffer b = new StringBuffer();
		b.append("<table class=\"rules\" cellspacing=\"0\"><tr class=\"rules\">");
		b.append("<td class=\"rule\">").append(input[0]).append("</td>");
		b.append("<td class=\"type\">").append(input[1]).append("</td>");
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
		DDMSRuleTaglet tag = new DDMSRuleTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}