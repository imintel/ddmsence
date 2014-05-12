<html>
<head>
	<title>DDMSence: Tutorial #3 (Escape)</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<h1>Tutorial #3: <u>Escape</u></h1>

<p>
<img src="./images/escape-flow.png" width="400" height="156" title="Escape Workflow" align="right" />
<u>Escape</u> is a tool that traverses multiple DDMS Resource files and then exposes various statistics about them through the 
<a href="http://code.google.com/apis/visualization/documentation/gallery.html" target="_new">Google Visualization API</a>. Charts in this sample
application are non-interactive, but provide the foundation for more complex cases: for example, it would
be possible to plot Temporal Coverage on an 
<a href="http://code.google.com/apis/visualization/documentation/gallery/annotatedtimeline.html" target="_new">Annotated Timeline</a>, or Geospatial Coverage on 
<a href="http://code.google.com/apis/visualization/documentation/gallery/map.html" target="_new">Google Maps</a>.</p>
	
<p>This application would be better suited as a web application, but I have implemented it with Swing, to minimize the amount of overhead required
to run it.</p>

<h2>Getting Started</h2>

<p>You do not need to have any experience with Google Visualization to use this application -- just be aware that it is a web-based tool which accepts 
a formatted URL and renders some sort of chart.</p>	

<p><u>Escape</u> can be run from the command line with the class, <code>buri.ddmsence.samples.Escape</code>. The application does not accept any
command line parameters. A network connection is needed to connect to Google (which also means that this application will not run in a SIPRNET environment).</p>

<p>Please see the "<a href="documentation.jsp#started">Getting Started</a>" section for classpath details and command line syntax.</p> 

<h3>Walkthrough</h3>

<p>When the application first opens, it will search the <code>data/sample/</code> directory for any XML files and try to convert them all into Resource
objects (DDMSence can currently handle DDMS 2.0 through 5.0). You might see an error message appear in your console, because I have included an invalid metacard file (aptly named <code>3.0-invalidResourceExample.xml</code>)
in the directory, which is used in the <u>Essentials</u> application. <u>Escape</u> will ignore any files it could not convert, so you can safely ignore this message.</p>

<p>With a collection of Resources in hand, <u>Escape</u> examines the data in each Resource and builds frequency distribution charts based on various metrics. The
chart that appears initially shows the distribution of MIME Types in the metacards (the <code>ddms:mimeType</code> element nested inside of <code>ddms:Media</code>).</p>

<img src="./images/escape-01.png" width="400" height="267" title="First Screen" />
<p class="figure">Figure 1. The MIME Types Distribution</p>

<p>The text field below the chart contains the URL used to generate it. You can paste this URL into your web browser to view the same graph.</p>

<p>Now, let's take a look at the source code in <code>/src/samples/buri/ddmsence/samples/Escape.java</code> to see how this was accomplished. 
The important lines are found in the <code>buildMimeTypeGraph()</code> method:</p>

<pre class="brush: java">Distribution distribution = new Distribution();
for (Resource resource : getResources()) {
   // Check any records that have a format (mimeType is required if format is present)
   if (resource.getFormat() != null) {
      String mimeType = resource.getFormat().getMimeType();
      distribution.incrementCount(mimeType);
   }			
}
return (buildPieGraphURL("DDMS%20MimeType%20Distribution", distribution, PIE_GRAPH_3D));</pre>
<p class="figure">Figure 2. MIME Type source code</p>

<p>The code traverses all of the Format components to maintain a count of each MIME Type. (A simple data class, called Distribution, is
used to encapsulate the counts). Once the counts are generated, the data is sent to the <code>buildPieGraphURL()</code> method to render as a
Pie Chart in Google-specific terms.</p>

<p>As you can see from the code, a DDMS Resource object can be traversed in a standard way. Each of the top-level components in a Resource record
(as well as its attributes) has a getter method, and deeper components can be extracted from those top-level components. If the DDMS schema allows
0 or 1 elements to appear, the getter will return a single DDMS component. If the DDMS schema allows 0 to many elements appear, the getter will return
a List of DDMS components. The name of the getter will be a capitalized version of the DDMS element name (such as <code>getVirtualCoverage</code> for <code>ddms:virtualCoverage</code>).</p>

<p>Next, let's click on the "Keywords" tab to see a pie graph of keyword usage.</p>

<img src="./images/escape-02.png" width="400" height="267" title="Second Screen" />
<p class="figure">Figure 3. The Keyword Distribution</p>
 
<pre class="brush: java">Distribution distribution = new Distribution();
for (Resource resource : getResources()) {
   // Check any records that have a keyword (subjectCoverage is required)
   for (SubjectCoverage subjectCoverage : resource.getSubjectCoverages()) {
      if (!subjectCoverage.getKeywords().isEmpty()) {
         List&lt;Keyword&gt; keywords = subjectCoverage.getKeywords();
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
return (buildPieGraphURL("DDMS%20Keyword%20Distribution", distribution, PIE_GRAPH));</pre>
<p class="figure">Figure 4. Keyword source code</p>

<p>The source code for this statistic is functionally identical to the source code for MIME Types. We are locating the Subject Coverage component
in the Resource and then traversing its complete list of Keywords. The only minor difference here is that multi-word Keyword values are being split
up into single words (to make the visualization more exciting).</p> 

<p>The next visualization can be seen in the "Dates" tab.</p>

<img src="./images/escape-03.png" width="400" height="267" title="Third Screen" />
<p class="figure">Figure 5. The Dates Distribution</p>

<p>The source code for this visualization can be found in the <code>buildDateGraph()</code> method. Dates can appear in a DDMS Resource in multiple locations:</p>

<ul>
	<li>There are six date attributes in the <code>ddms:dates</code> element, which apply to the resource being described.</li>
	<li>If any <code>ddms:temporalCoverage</code> elements are defined, each one may have a start date and an end date.</li>
	<li>The resource record itself has a createDate attribute in DDMS 3.0 through 4.1.</li>
</ul>

<pre class="brush: java">Distribution distribution = new Distribution();
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
   List&lt;TemporalCoverage&gt; timePeriods = resource.getTemporalCoverages();
   for (TemporalCoverage timePeriod : timePeriods) {
      if (timePeriod.getStart() != null)
         distribution.incrementCount(String.valueOf(timePeriod.getStart().getYear()));
      if (timePeriod.getEnd() != null)
         distribution.incrementCount(String.valueOf(timePeriod.getEnd().getYear()));
   }   
}
return (buildPieGraphURL("DDMS%20Date%20Distribution", distribution, PIE_GRAPH));</pre>
<p class="figure">Figure 6. Date source code</p>

<p>For this visualization, all date locations are checked, and then transformed into <code>xs:year</code> values (the Java pattern "<code>YYYY</code>"). The distribution
is then tracked as it was in the previous two examples. A more useful graph might show just expiration dates or time periods -- I added the additional
dates to provide more examples of traversing a Resource, and to make the visualization more exciting.</p>

<p>The final distribution in the "DDMS Versions" tab shows the DDMS versions of the loaded Resources, based on the XML namespaces in the files. Notice that DDMS 4.0.1 instances are grouped
together with DDMS 4.1 instances as "4.1", since both version share the same XML namespace.</p>
<img src="./images/escape-04.png" width="400" height="267" title="Fourth Screen" />
<p class="figure">Figure 6. The Version Distribution</p>

<p>If you would like to add new Resources to the dataset, simply copy your XML files into the <code>data/samples/</code> directory, or use the <u>Escort</u> wizard
application to generate Resources. When you open up <u>Escape</u> again, your new files will be included in the metrics.</p>

<h3>Conclusion</h3>

<p>In this tutorial, you have seen how to traverse a valid DDMS Resource in Java, and how the Java representations can be used as a gateway to apply
DDMS concepts in unexpected contexts.</p>

<p>This is the final tutorial. If you have any suggestions for additional tutorials or sample applications, please contact me in one of the ways mentioned
under <a href="documentation.jsp#feedback">Feedback</a>!</p> 

<p>
	<a href="tutorials-01.jsp">Tutorial #1: Essentials</a><br />
	<a href="tutorials-02.jsp">Tutorial #2: Escort</a><br />
	Tutorial #3: Escape <span class="notify">(you are here)</span><br />
	<a href="documentation.jsp#samples">Back to Samples Documentation</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>