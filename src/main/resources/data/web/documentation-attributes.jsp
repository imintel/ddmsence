<html>
<head>
	<title>DDMSence: Power Tip - Common Attribute Groups</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Common Attribute Groups</h1>

<p>The DDMS specification identifies several groups of attributes which always tend to be used together: ISM security attributes (such as classification), ISM
notice attributes, XLink attributes, GML SRS attributes, and attributes used as part of the Extensible Layer.</p>

<p>ExtensibleAttributes are discussed in the Power Tip on <a href="documentation-extensible.jsp">The Extensible Layer</a>.</p>

<h2>ISM Security Attributes</h2>

<p>
ISM security attributes are defined in the Intelligence Community's "XML Data Encoding Specification for Information Security Marking Metadata" document (DES) and
implemented in the <a href="/docs/index.html?buri/ddmsence/ddms/security/ism/SecurityAttributes.html">SecurityAttributes</a> class. This class encapsulates
the ISM attributes which can decorate various DDMS components, such as <code>ddms:resource</code> or <code>ddms:security</code>. The constructor which builds
the attributes from a XOM element will simply load these attributes from the element itself. The constructor which builds the attributes from raw data is defined as:

<pre class="brush: java">public SecurityAttributes(String classification, List&lt;String&gt; ownerProducers, Map&lt;String,String&gt; otherAttributes)</pre>
<p class="figure">Figure 1. SecurityAttributes constructor</p>

<p>Because the <code>classification</code> and <code>ownerProducers</code> are the most commonly referenced attributes, they are explicit parameters. Any other
attribute can be added in the String-based map called <code>otherAttributes</code>. Here is an example which creates Confidential markings and puts them on a <code>ddms:title</code> element:</p>

<pre class="brush: java">List&lt;String&gt; ownerProducers = Util.getXsListAsList("AUS USA");
Map&lt;String, String&gt; otherAttributes = new HashMap&lt;String, String&gt;();
otherAttributes.put("SCIcontrols", "SI");
otherAttributes.put("SARIdentifier", "SAR-USA");
SecurityAttributes security = new SecurityAttributes("C", ownerProducers, otherAttributes);
Title title = new Title("My Confidential Notes", security);
System.out.println(title.toXML());</pre>
<p class="figure">Figure 2. Code to generate SecurityAttributes</p>

<p>Note: The actual values assigned to each attribute in Figure 2 are for example's sake only, and might be illogical in real-world metadata.</p>

<pre class="brush: xml">&lt;ddms:title xmlns:ddms="http://metadata.dod.mil/mdr/ns/DDMS/3.0/" xmlns:ISM="urn:us:gov:ic:ism" 
   ISM:classification="C" ISM:ownerProducer="AUS USA" ISM:SCIcontrols="SI"
   ISM:SARIdentifier="SAR-USA"&gt;
   My Confidential Notes
&lt;/ddms:title&gt;</pre>
<p class="figure">Figure 3. The resultant XML element with security attributes</p>

<p>If an attribute in the <code>otherAttributes</code> map does not have one of the expected ISM attribute names, it is ignored. In addition, the parameter versions
of <code>classification</code> and <code>ownerProducer</code> always take precedence if you try to override them with <code>otherAttributes</code>:

<pre class="brush: java">List&lt;String&gt; ownerProducers = Util.getXsListAsList("AUS USA");
Map&lt;String, String&gt; otherAttributes = new HashMap&lt;String, String&gt;();
// The next line will be ignored, because there is no ISM attribute with this name.
otherAttributes.put("favoriteColor", "blue");
// The next line will be ignored, because the "classification" parameter takes precedence.
otherAttributes.put("classification", "U");
// The next line will be ignored, because the "ownerProducers" parameter takes precedence.
otherAttributes.put("ownerProducer", "FRA"); 
SecurityAttributes security = new SecurityAttributes("C", ownerProducers, otherAttributes);
Title title = new Title("My Confidential Notes", security);
System.out.println(title.toXML());</pre>
<p class="figure">Figure 4. Code to generate SecurityAttributes</p>

<pre class="brush: xml">&lt;ddms:title xmlns:ddms="http://metadata.dod.mil/mdr/ns/DDMS/3.0/" xmlns:ISM="urn:us:gov:ic:ism" 
   ISM:classification="C" ISM:ownerProducer="AUS USA"&gt;
   My Confidential Notes
&lt;/ddms:title&gt;</pre>
<p class="figure">Figure 5. The resultant XML element with security attributes</p>

<p>The DES also defines many logical constraints on these attributes, but DDMSence does not validate these rules today. A set of Schematron files is 
bundled with ISM.XML V5, and V9 (which are used by DDMS 3.1 and 4.1, respectively),
and sample code for using DDMSence with these files can be found in the <a href="documentation-schematron.jsp">Schematron Validation</a> Power Tip.</p>

<p>The values assigned to some attributes must come from the Controlled Vocabulary Enumerations (CVEs) defined by the Intelligence Community. The 
enumerations used by DDMSence are taken from Public Release versions of ISM.XML, so DDMSence will not be able to recognize enumeration values from 
higher classification levels. This restriction will be addressed in a future release.</p>

<h2>ISM Notice Attributes</h2>

<p><a href="/docs/index.html?buri/ddmsence/ddms/security/ism/NoticeAttributes.html">NoticeAttributes</a> are only applicable in DDMS 4.0.1 and 4.1, and follow the same patterns used 
by the SecurityAttributes. The <code>ISM:noticeType</code> attribute is validated against a CVE when present.</p>

<pre class="brush: java">// Assume that a list of noticeTexts, and the security attributes were previously created.
NoticeAttributes noticeAttributes = new NoticeAttributes("POC", "This is a reason.", "2011-09-15", null, Boolean.TRUE);
Notice notice = new Notice(noticeTexts, securityAttributes, noticeAttributes);
System.out.println(notice.toXML());</pre>
<p class="figure">Figure 6. Code to generate NoticeAttributes</p>

<pre class="brush: xml">&lt;ISM:Notice ISM:noticeType="POC" ISM:noticeReason="This is a reason." ISM:noticeDate="2011-09-15" ISM:externalNotice="true"
   ISM:classification="U" ISM:ownerProducer="USA"&gt;
   [...]
&lt;/ISM:Notice&gt;</pre>
<p class="figure">Figure 7. The resultant XML element with notice attributes</p>

<h2>XLink Attributes</h2>

<p><a href="/docs/index.html?buri/ddmsence/ddms/summary/xlink/XLinkAttributes.html">XLinkAttributes</a> support the various components
which provide external link attributes. An XLinkAttributes instance can function as 3 different types of XLink attribute groups: locator, simple, and resource, based on
the value of the <code>xlink:type</code> attribute, or the constructor used to build the instance.</p>

<pre class="brush: java">
// Constructor for attributes with type="locator", used with ddms:link
public XLinkAttributes(String href, String role, String title, String label);

// Constructor for attributes with type="resource", used with ddms:revisionRecall
public XLinkAttributes(String role, String title, String label);

// Constructor for attributes with type="simple", used with ddms:taskID
public XLinkAttributes(String href, String role, String title, String arcrole, String show, String actuate);</pre>
<p class="figure">Figure 8. Constructors to generate XLinkAttributes for various purposes</p>

<h2>GML SRS Attributes</h2>

<p>Spatial Reference System (SRS) attributes are defined in the GML specification and implemented as an <a href="/docs/index.html?buri/ddmsence/ddms/summary/gml/SRSAttributes.html">SRSAttributes</a> class.
They can be applied to the various GML and TSPI shapes inside of a <code>ddms:boundingGeometry</code> element.</p>

<p>Here is an example which creates SRS attributes on a <code>gml:pos</code> element in DDMS 4.1:</p>

<pre class="brush: java">List&lt;String&gt; axisLabels = Util.getXsListAsList("X Y");
List&lt;String&gt; uomLabels = Util.getXsListAsList("Meter Meter");
SRSAttributes srsAttributes = new SRSAttributes("http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D",
   new Integer(10), axisLabels, uomLabels);
List&lt;Double&gt; coordinates = new ArrayList&lt;Double&gt;();
coordinates.add(new Double(32.1));
coordinates.add(new Double(40.1));
Position position = new Position(coordinates, srsAttributes);
System.out.println(position.toXML());</pre>
<p class="figure">Figure 9. Code to generate SRSAttributes in DDMS 4.1</p>

<pre class="brush: xml">&lt;gml:pos srsName="http://metadata.dod.mil/mdr/ns/GSIP/crs/WGS84E_2D" srsDimension="10" 
   axisLabels="X Y" uomLabels="Meter Meter"&gt;32.1 40.1&lt;/gml:pos&gt;</pre>
<p class="figure">Figure 10. The resultant XML element with SRS attributes</p>
  
<p>Please note that the SRSAttributes do not belong in any XML namespace.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>