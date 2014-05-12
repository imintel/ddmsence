<html>
<head>
	<title>DDMSence: What's New in DDMS 5.0 / DDMSence 2.2.0</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>What's New in DDMS 5.0 / DDMSence 2.2.0</h1>

<p>The table below describes the major changes in DDMS 5.0. For additional details, refer to the Release Notes bundled with DDMS 5.0.</p>

<table>
<tr><th>DDMS 4.1</th><th>DDMS 5.0</th></tr>
<tr>
	<td>The <code>ddms:resource</code> element is a "metacard". The metacard is a full-fledged, top-level element containing all of the details about a described asset.</td>
	<td>The <code>ddms:resource</code> element is an "assertion" in the IC's Trusted Data Object (TDO). The assertion by itself is insufficient for describing an asset, and 
		the entire TDO record is needed to fully understand what is being described.</td>
</tr>
<tr>
	<td>A metacard has five core sets (Metacard Info, Security, Resource, Summary Content, and Format) and an Extensible Layer.</td>
	<td>An assertion has four core sets (Metacard Info, Resource, Summary Content, and Format) and no Extensible Layer. Security information has moved to the Enterprise Data Header (EDH)
	of the TDO. Extensions would be modeled as standalone assertions in the TDO, separate from the DDMS discovery assertion.</td>
</tr>
<tr>
	<td>Cardinality of DDMS components is enforced in the XML schema.</td>
	<td>Cardinality of DDMS components have been removed from the XML schema, to allow the schema to be used in alternate contexts. The specification still identifies some components as Mandatory
	for discovery.</td>
</tr>
<tr>
	<td>Some Mandatory string components were allowed to have a nonsensical value of an "empty string".</td>
	<td>DDMS now requires all Mandatory string components to have a minimum length of 1.</td>
</tr>
<tr>
	<td>A GML profile and a custom <code>ddms:postalAddress</code> defintion are used for geolocations.</td>
	<td>Geolocations are handled with the <a href="docs/index.html?buri/ddmsence/ddms/summary/tspi/package-summary.html">Time-Space-Position Information</a> specification.
	<code>ddms:boundingBox</code>, <code>ddms:verticalExtent</code>, <code>gml:Point</code>, and <code>gml:Polygon</code> are replaced by comparable TSPI shapes.</td>
</tr>
</table>
<p class="figure">Table 1. Major Differences in DDMS 5.0</p>

<p>These changes have impacted DDMSence 2.2.0 in the following ways:</p>
<ul>
	<li>The <code>DDMSReader</code> class works on both metacards and assertions. The <code>DDMSReader</code> cannot interpret an entire Trusted Data Object -- you will need to extract
	the <code>ddms:resource</code> assertion before it can be loaded into DDMSence. Assertions created by DDMSence are incomplete on their own, and are intended for insertion into a TDO record.</li><br />
	<li>Because DDMSence was already enforcing that Mandatory components also be non-empty, the cardinality and empty string changes in DDMS 5.0 have no impact.</li><br />
	<li>DDMSence 2.2.0 contains a new package of classes for TSPI shapes and addresses. The TSPI specification is incredibly complex and multi-layered, and it is unclear how much 
	value a complete implementation in DDMSence would provide for discovery use cases. For this reason, shapes and addresses in the TSPI namespace are simple wrappers around the
	raw XML definition of the TSPI component. This is actually a step back from the GML shapes provided in older versions of DDMSence. As use cases refine and more organizations 
	adopt DDMS 5.0, I will revisit these components to determine whether a stronger solution would be useful.</li><br />
	<li>DDMSence 2.2.0 is backwards compatible with all versions since 2.0.0, with three minor exceptions:
		<ul>
			<li>The method, <code>BoundingGeometry.getPoints()</code> has been renamed as <code>BoundingGeometry.getGmlPoints()</code> to eliminate confusion between GML points and TSPI points.</li>
			<li>The method, <code>BoundingGeometry.getPolygons()</code> has been renamed as <code>BoundingGeometry.getGmlPolygons()</code> to eliminate confusion between GML polygons and TSPI polygons.</li>
			<li>The constructor, <code>DDMSReader()</code>, now requires a specific DDMSVersion as a parameter. Each reader instance is tied to a single version of DDMS. 
			You will need to create a separate DDMSReader for each version of DDMS XML file you intend to load. The sample applications have instructional code for this.</li><br />
		</ul>
	</li>
	<li>The <a href="docs/index.html?buri/ddmsence/ddms/Resource.html">API documentation</a> has been updated with helpful summary information on each class. The new standardized format should make it easier to understand how
	components have changed through various versions of DDMS.</li>
</ul>
<p>
	<a href="#top">Back to Top</a><br>
	<a href="downloads.jsp">Back to Downloads</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>