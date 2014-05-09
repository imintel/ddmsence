package buri.ddmsence.util.taglet;

import java.util.List;
import java.util.Map;

import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.Util;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * A Javadoc Taglet which prints out a table row of supported DDMSVersions.
 * 
 * <p>
 * <b>Inputs:</b> a series of 0s and 1s, where 1 is "supported" and 0 is "unsupported". The inputs are read from
 * left-to-right and mapped to the versions of DDMS in DDMSence (e.g. 2.0, 3.0, 3.1, 4.1, 5.0, etc.).
 * </p>
 * 
 * <p>
 * If there are fewer inputs than supported versions, the default for remaining slots will be the most recent value.
 * </p>
 * 
 * @author Brian Uri!
 * @since 2.2.0
 */
public class DDMSVersionTaglet extends AbstractInlineTaglet {

	private static final String NAME = "ddms.versions";
	private static final char DEFAULT_VALUE = '1';

	/**
	 * Return the name of this custom tag.
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Helper method to build the version table. Extracted so it can be called from another tag.
	 * 
	 * @param input the input, as specified in the header description of this class.
	 */
	public static String getTableFor(String input) {
		input = Util.getNonNullString(input);
		StringBuffer b = new StringBuffer();
		b.append("<table cellspacing=\"0\" class=\"versionChart\"><tr>\n");
		b.append("<td class=\"versionChartHeader\">In DDMS</td>");
		List<String> versions = DDMSVersion.getSupportedVersions();
		char value = DEFAULT_VALUE;
		for (int i = 0; i < versions.size(); i++) {
			if (i < input.length())
				value = input.charAt(i);
			b.append("<td class=\"versionChart").append(value).append("\">");
			String version = versions.get(i);
			if ("4.1".equals(version))
				version = "4.0.1/4.1";
			b.append(version).append("</td>");
		}
		b.append("</tr></table>\n");
		return (b.toString());
	}

	/**
	 * Given the <code>Tag</code> representation of this custom
	 * tag, return its string representation.
	 * 
	 * @param tag the <code>Tag</code> representation of this custom tag.
	 */
	public String toString(Tag tag) {
		return (getTableFor(tag.text()));
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		DDMSVersionTaglet tag = new DDMSVersionTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
}