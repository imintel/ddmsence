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
package buri.ddmsence.samples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;

import org.xml.sax.SAXException;

import buri.ddmsence.ddms.Resource;
import buri.ddmsence.ddms.resource.Dates;
import buri.ddmsence.ddms.summary.Keyword;
import buri.ddmsence.ddms.summary.SubjectCoverage;
import buri.ddmsence.ddms.summary.TemporalCoverage;
import buri.ddmsence.samples.util.AbstractSample;
import buri.ddmsence.samples.util.Distribution;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.PropertyReader;

/**
 * DDMScape is a tool that loads multiple DDMS Resource files and then exposes various statistics about them through the
 * Google Visualization API.
 * 
 * <p>
 * Because some Google Visualizations require Flash, or have interactive components, I limited the output of this
 * application to the static image-based visualizations like pie graphs. This allows the results to display in a Swing
 * UI rather than requiring a browser with addons.
 * </p>
 * 
 * <p>
 * However, the same concepts displayed in this application could be done in a webapp to allow for more complex
 * visualizations, like Temporal Coverage on a timeline, or Geospatial Coverage on a map.
 * </p>
 * 
 * <p>
 * While the first two sample applications were designed to teach developers how DDMSence works, the intent of this one
 * is to provide brainstorming ideas for leveraging DDMS in other contexts.
 * </p>
 * 
 * <p>
 * For additional details about this application, please see the tutorial on the Documentation page of the DDMSence
 * website.
 * </p>
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public class Escape extends AbstractSample {

	private List<Resource> _resources;
	private static final String GOOGLE_URL = "http://chart.apis.google.com/chart?";

	private static final String PIE_GRAPH = "p";
	private static final String PIE_GRAPH_3D = "p3";

	/**
	 * Entry point
	 * 
	 * @param args no parameters are required
	 */
	public static void main(String[] args) {
		try {
			Escape app = new Escape();
			app.setVisible(true);
		}
		catch (SAXException e) {
			System.err.println("Could not initialize the application.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (IOException e) {
			System.err.println("Could not render the visualizations.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * This application loads immediately and has no default instructions.
	 * 
	 * @see buri.ddmsence.samples.util.AbstractSample#getDefaultInstructions()
	 */
	protected String getDefaultInstructions() {
		return ("");
	}

	/**
	 * Sets up the UI and DDMSReader (which is base functionality of all sample apps)
	 */
	public Escape() throws SAXException, IOException {
		super("Escape", new Dimension(600, 400), false);

		_resources = new ArrayList<Resource>();
		loadValidResources();

		// Create the URLs for the data
		URL mimeTypeUrl = buildMimeTypeGraph();
		URL keywordUrl = buildKeywordGraph();
		URL dateUrl = buildDateGraph();
		URL versionUrl = buildVersionGraph();

		// Render the data in the Swing UI
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		pane.add("Mime Types", buildVisualizationPanel(mimeTypeUrl));
		pane.add("Keywords", buildVisualizationPanel(keywordUrl));
		pane.add("Dates", buildVisualizationPanel(dateUrl));
		pane.add("DDMS Versions", buildVisualizationPanel(versionUrl));
		getFrame().getContentPane().add(pane, BorderLayout.CENTER);
	}

	/**
	 * Searches the sample directory for valid DDMS Resources and builds
	 * object models for them.
	 */
	private void loadValidResources() {
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return (pathname.getName().endsWith(".xml"));
			}
		};
		File[] fileList = new File(PropertyReader.getProperty("sample.data")).listFiles(filter);
		for (int i = 0; i < fileList.length; i++) {
			try {
				// Try to parse each file.
				DDMSVersion version = guessVersion(fileList[i]);
				getResources().add(getReader(version).getDDMSResource(fileList[i]));
			}
			catch (Exception e) {
				// Skip any that cannot be parsed and continue.
				System.err.println("Skipping file, " + fileList[i].getName() + ": " + e.getMessage());
			}
		}
	}

	/**
	 * Traverses the mimeTypes of any loaded records and creates a
	 * Google Pie Graph of the distribution.
	 * 
	 * @return URL representing the Google URL
	 */
	private URL buildMimeTypeGraph() throws IOException {
		Distribution distribution = new Distribution();
		for (Resource resource : getResources()) {
			// Check any records that have a format (mimeType is required if format is present)
			if (resource.getFormat() != null) {
				String mimeType = resource.getFormat().getMimeType();
				distribution.incrementCount(mimeType);
			}
		}
		return (buildPieGraphURL("DDMS%20MimeType%20Distribution", distribution, PIE_GRAPH_3D));
	}

	/**
	 * Traverses the keywords of any loaded records and creates a Google Pie Graph of the distribution.
	 * 
	 * @return URL representing the Google URL
	 */
	private URL buildKeywordGraph() throws IOException {
		Distribution distribution = new Distribution();
		for (Resource resource : getResources()) {
			// Check any records that have a keyword (subjectCoverage is required)
			for (SubjectCoverage subjectCoverage : resource.getSubjectCoverages()) {
				if (!subjectCoverage.getKeywords().isEmpty()) {
					List<Keyword> keywords = subjectCoverage.getKeywords();
					// Record the counts for each keyword's usage
					for (Keyword keyword : keywords) {
						// Split multiword keywords.
						String[] splitValues = keyword.getValue().split(" ");
						for (int i = 0; i < splitValues.length; i++) {
							distribution.incrementCount(splitValues[i]);
						}
					}
				}
			}
		}
		return (buildPieGraphURL("DDMS%20Keyword%20Distribution", distribution, PIE_GRAPH));
	}

	/**
	 * Examines every date field in a Resource and creates a distribution of years.
	 * 
	 * @return URL representing the Google URL
	 */
	private URL buildDateGraph() throws IOException {
		Distribution distribution = new Distribution();
		for (Resource resource : getResources()) {
			// Examine the ddms:dates element (optional field with optional attributes)
			// Ignores ddms:DateHourMinType dates, which were introduced in DDMS 4.1, to simplify example
			Dates dates = resource.getDates();
			if (dates != null) {
				if (dates.getCreated() != null)
					distribution.incrementCount(String.valueOf(dates.getCreated().getYear()));
				if (dates.getPosted() != null)
					distribution.incrementCount(String.valueOf(dates.getPosted().getYear()));
				if (dates.getValidTil() != null)
					distribution.incrementCount(String.valueOf(dates.getValidTil().getYear()));
				if (dates.getInfoCutOff() != null)
					distribution.incrementCount(String.valueOf(dates.getInfoCutOff().getYear()));
				if (dates.getApprovedOn() != null)
					distribution.incrementCount(String.valueOf(dates.getApprovedOn().getYear()));
				if (dates.getReceivedOn() != null)
					distribution.incrementCount(String.valueOf(dates.getReceivedOn().getYear()));
			}

			// Resource createDate (required field in 3.0, 4.0.1, and 4.1, optional in 2.0)
			if (resource.getCreateDate() != null)
				distribution.incrementCount(String.valueOf(resource.getCreateDate().getYear()));

			// ddms:temporalCoverage (optional field)
			// getStart() returns the date if present. getStartString() returns the XML format or
			// the two allowed strings, Not Applicable, and Unknown.
			List<TemporalCoverage> timePeriods = resource.getTemporalCoverages();
			for (TemporalCoverage timePeriod : timePeriods) {
				if (timePeriod.getStart() != null)
					distribution.incrementCount(String.valueOf(timePeriod.getStart().getYear()));
				if (timePeriod.getEnd() != null)
					distribution.incrementCount(String.valueOf(timePeriod.getEnd().getYear()));
			}
		}
		return (buildPieGraphURL("DDMS%20Date%20Distribution", distribution, PIE_GRAPH));
	}

	/**
	 * Examines every Resource and creates a distribution of DDMS Versions
	 * 
	 * @return URL representing the Google URL
	 */
	private URL buildVersionGraph() throws IOException {
		Distribution distribution = new Distribution();
		for (Resource resource : getResources()) {
			distribution.incrementCount(DDMSVersion.getVersionForNamespace(resource.getNamespace()).getVersion());
		}
		return (buildPieGraphURL("DDMS%20Version%20Distribution", distribution, PIE_GRAPH));
	}

	/**
	 * Helper method to convert a map of keys to counts into a Google Pie Graph URL
	 * 
	 * @param title the title of this chart
	 * @param distribution the data to render
	 * @param show3D whether to show a 3d chart or a 2d chart
	 * @return a URL which can be loaded to see the visualization
	 */
	private URL buildPieGraphURL(String title, Distribution distribution, String type) throws IOException {
		StringBuffer url = new StringBuffer(GOOGLE_URL);
		url.append("cht=").append(type).append("&chof=gif&chs=500x200&chtt=").append(title);
		url.append("&chco=0000ff,2222ff,4444ff,6666ff,8888ff,aaaaff,ccccff,eeeeff&chp=4&chl=");
		for (Iterator<String> iterator = distribution.getKeys().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			url.append(key);
			if (iterator.hasNext())
				url.append("|");
		}
		url.append("&chd=t:");
		for (Iterator<String> iterator = distribution.getKeys().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			url.append(distribution.getCount(key));
			if (iterator.hasNext())
				url.append(",");
		}
		return (new URL(url.toString()));
	}

	/**
	 * Accessor for the resources
	 */
	private List<Resource> getResources() {
		return (_resources);
	}
}
