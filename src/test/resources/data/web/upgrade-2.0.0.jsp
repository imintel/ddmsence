<html>
<head>
	<title>DDMSence: Upgrade Guide: Version 1.x to 2.x</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Upgrade Guide: Version 1.x to 2.x</h1>

<p>In order to cleanly and comprehensively support DDMS 4.0.1, DDMSence 2.0.0 required multiple changes which
might break backwards compatibility with your existing code. Some of these changes are as simple as new package names
or changed method names, while others are more significant. This Upgrade Guide describes the changes and provides recommendations on how to migrate your code to use the new version
of DDMSence. If you have followed the instructions in this guide and are still encountering compiler errors or other
problems, I would be glad to assist you further.</p>

<ul>
	<li><b>Major Changes</b><ul>
		<li><a href="#major-01">Swappable CVE feature expanded</a></li>
		<li><a href="#major-02">Producer/Entity hierarchy changed</a></li>
		<li><a href="#major-03">Related Resources hierarchy compressed</a></li>
		<li><a href="#major-04">Non-XML Resources validated against DDMS schemas</a></li>
		<li><a href="#major-05">DDMS 4.0 skipped in favor of DDMS 4.0.1</a></li>
	</ul></li>
	<li><b>Minor Changes</b><ul>
		<li><a href="#minor-01">Validation for security rollup removed</a></li>
		<li><a href="#minor-02">Reusable components no longer track parentType</a></li>
		<li><a href="#minor-03">Method signatures have changed</a></li>
		<li><a href="#minor-04">XLink attributes encapsulated in a class</a></li>
		<li><a href="#minor-05">Public NAME constants removed</a></li>
		<li><a href="#minor-06">Non-DDMS classes moved into new packages</a></li>
		<li><a href="#minor-07">Configurable property names changed</a></li>
		<li><a href="#minor-08">Class names changed</a></li>
	</ul></li>
</ul>

<h3>Major Changes</h3>

<a name="major-01"></a><h4>Swappable CVE feature expanded (<a href="http://code.google.com/p/ddmsence/issues/detail?id=95">Issue #95</a>, <a href="http://code.google.com/p/ddmsence/issues/detail?id=169">Issue #169</a>, and <a href="http://code.google.com/p/ddmsence/issues/detail?id=154">Issue #154</a>)</h4> 

<div class="upgradeGuide">
<p>In previous versions of DDMSence, you could point to a custom set of ISM Controlled Vocabulary Enumeration files
with the "<code>icism.cve.customEnumLocation</code>" custom property or toggle CVEs to suppress errors with "<code>icism.cve.validationAsErrors</code>". 
Starting with V5 of ISM.XML (the version bundled with DDMS 3.1), these vocabularies were enforced in the schemas themselves. So, being able to change to a 
custom set of CVE files or suppress errors no longer made sense, because the CVEs would then present conflicting results when compared to the generated schema files,
which strictly enforce the vocabularies.</p>

<p>Both runtime configurable properties have now been removed. Support for alternate ISM schemas and CVEs is now handled as a configuration step to your
classpath.</p>

<p><b>How to Upgrade:</b></p>
<p>The default selection of ISM schemas and CVEs is now based upon the current DDMSVersion and uses bundled Public Release files all of the
time. It is now possible to swap out your entire set of ISM schemas and vocabularies by giving your local copy a higher priority in the Java classpath, 
as discussed in this <a href="documentation-differentIsm.jsp">Power Tip</a>.</p> 
</div>

<a name="major-02"></a><h4>Producer/Entity hierarchy changed (<a href="http://code.google.com/p/ddmsence/issues/detail?id=153">Issue #153</a>)</h4>

<div class="upgradeGuide">
<p>In previous versions of DDMSence, the producer hierarchy was flattened to be more object-oriented. Consider this example:

<pre class="brush: xml">&lt;ddms:creator&gt;
   &lt;ddms:Organization&gt;
      &lt;ddms:name&gt;DISA&lt;/ddms:name&gt;
   &lt;/ddms:Organization&gt;	
&lt;/ddms:creator&gt;</pre>

<p>This creator element was modeled in DDMSence 1.x as "An instance of Organization has the role of creator" 
and the Organization class contained this entire construct. With DDMS 4.0.1, Organizations and Persons 
can now appear in other roles besides the classic producers (such as an Addressee or RecordKeeper), and the 
old approach became too fragile to sustain. Producers and entities are now consistent with the schema -- 
the above construct is modeled as "An instance of Creator contains an instance of Organization".</p>

<p>Specifically, there is a new class for Creator, Contributor, Publisher, and PointOfContact, and the 
old entities, Organization Person, Service, and Unknown are no longer top-level components.</p>

<p><b>How to Upgrade:</b></p>
<p>To update your code, the four entity classes must now be wrapped into a producer role class. As an example, if you 
previously had code that worked with a "Service that was a creator":</p>
	
<pre class="brush: java">Service service = new Service("creator", names, phones, emails);</pre>

<p>You would now deal with a "Creator that contains a Service":</p>

<pre class="brush: java">Service service = new Service(names, phones, emails);
Creator creator = new Creator(service, null, securityAttributes);</pre>

<p>In places where you passed an Organization, Person, Service, or Unknown into a Resource constructor as a "top-level component", you will now need to pass
in the producer role (Contributor, Creator, PointOfContact, or Publisher) instead.</p>

</div>

<a name="major-03"></a><h4>Related Resources hierarchy compressed (<a href="http://code.google.com/p/ddmsence/issues/detail?id=130">Issue #130</a>)</h4>
<div class="upgradeGuide">
<p>The hierarchy for related resources used to require both a RelatedResources class and a RelatedResource class:</p>

<pre class="brush: xml">ddms:relatedResources (0-many in a ddms:resource)
   @ddms:relationship
   @ddms:direction
   @&lt;ismOptionalAttributes&gt;
   ddms:relatedResource (1-many)
      @ddms:qualifier
      @ddms:value
      ddms:link (1-many)</pre>
<p>In this model, the ddms:relatedResources element defined the relationship and direction, and a collection of ddms:relatedResource elements shared those values.
DDMS 4.0.1  has flattened this to one level:</p>

<pre class="brush: xml">ddms:relatedResource (0-many in a ddms:resource)
   @ddms:qualifier
   @ddms:value
   @ddms:relationship
   @ddms:direction
   @&lt;ismOptionalAttributes&gt;
   ddms:link (1-many)</pre>
   
<p>To support this in DDMSence, the RelatedResources class has been completely removed. The remaining RelatedResource class now 
contains all of the required information (including the relationship and direction attributes). The element-based
constructor for RelatedResource expects a construct that describes a single related resource. For example:</p>
	
<p><u>Pre-DDMS 4.0.1:</u></p>
<pre class="brush: xml">&lt;ddms:relatedResources ddms:relationship="http://purl.org/dc/terms/references" ddms:direction="outbound" 
   ISM:classification="U" ISM:ownerProducer="USA"&gt;
   &lt;ddms:RelatedResource ddms:qualifier="http://purl.org/dc/terms/URI" ddms:value="http://en.wikipedia.org/wiki/Tank"&gt;
      &lt;ddms:link [...] /&gt;
   &lt;/ddms:RelatedResource&gt;
&lt;/ddms:relatedResources&gt;</pre>
	
<p><u>DDMS 4.0.1</u></p>
<pre class="brush: xml">&lt;ddms:relatedResource ddms:relationship="http://purl.org/dc/terms/references" ddms:direction="outbound" 
   ddms:qualifier="http://purl.org/dc/terms/URI" ddms:value="http://en.wikipedia.org/wiki/Tank" 
   ISM:classification="U" ISM:ownerProducer="USA"&gt;
   &lt;ddms:link [...] /&gt;
&lt;/ddms:relatedResource&gt;</pre>
	
<p>In earlier versions of DDMS, having multiple resources with the same relationship and direction was allowed:</p>
	
<p><u>Pre-DDMS 4.0.1:</u></p>
<pre class="brush: xml">&lt;ddms:relatedResources ddms:relationship="http://purl.org/dc/terms/references" ddms:direction="outbound" 
   ISM:classification="U" ISM:ownerProducer="USA"&gt;
   &lt;ddms:RelatedResource ddms:qualifier="http://purl.org/dc/terms/URI" ddms:value="http://en.wikipedia.org/wiki/Tank"&gt;
      &lt;ddms:link [...] /&gt;
   &lt;/ddms:RelatedResource&gt;
   &lt;ddms:RelatedResource ddms:qualifier="http://purl.org/dc/terms/URI" ddms:value="http://en.wikipedia.org/wiki/Fish"&gt;
      &lt;ddms:link [...] /&gt;
   &lt;/ddms:RelatedResource&gt;
&lt;/ddms:relatedResources&gt;</pre>

<p>If the element-based constructor of RelatedResource encounters this case of multiples, it will only process the first
related resource and provide a validation warning message that it is skipping the remainder. However, the element-based constructor for the entire Resource
has been updated to mediate this case automatically. The result will be 2 RelatedResource instances, each with the same relation and direction attributes.</p>

<p><b>How to Upgrade:</b></p>
<p>If your code is working at the Resource level, you don't need to do a thing. The Resource constructors can handle all legal
ddms:relatedResources or ddms:relatedResource elements in all supported DDMS versions. However, if you were instantiating RelatedResources or 
RelatedResource classes directly via the data-driven constructors, you will need to update your code to stop using the removed class.</p>

<p>If you had old code that looked like this:</p>

<pre class="brush: java">List&lt;RelatedResource&gt; list = new ArrayList&lt;RelatedResource&gt;();
list.add(new RelatedResource(links, "qualifier", "resource1"));
list.add(new RelatedResource(links, "qualifier", "resource2"));
RelatedResources resources = new RelatedResources(list, "http://purl.org/dc/terms/references",
   "outbound", securityAttributes);
myTopLevelComponents.add(resources);</pre>
   
<p>The RelatedResources wrapper is now removed:</p>

<pre class="brush: java">
myTopLevelComponents.add(new RelatedResource(links, "http://purl.org/dc/terms/references", "outbound", "qualifier",
   "resource1"));
myTopLevelComponents.add(new RelatedResource(links, "http://purl.org/dc/terms/references", "outbound", "qualifier",
   "resource2"));</pre>
   
</div>

<a name="major-04"></a><h4>Non-XML Resources validated against DDMS schemas (<a href="http://code.google.com/p/ddmsence/issues/detail?id=28">Issue #28</a>)</h4>
<div class="upgradeGuide">
<p>DDMS metacards loaded from an XML file have always been validated against the DDMS schemas. However, DDMS metacards built from scratch (either with the data-driven constructors
or the Component Builder framework) were not. Starting in this release, a metacard built from scratch is converted into XML and validated against the schemas, to elminate
any remaining loopholes in data correctness. Individual components which are built from scratch are not validated against the schema until the enclosing Resource is instantiated.</p>

<p><b>How to Upgrade:</b></p>
<p>There are no upgrade steps required. However, DDMSence v2.0.0 may catch a few additional fringe cases which were mistakenly identified as valid in previous versions. For example,
the <code>gml:id</code> on GML points is supposed to be unique to the XML metacard. By using data-driven constructors in the past, it was possible to create multiple points with the same
ID value, which would result in an invalid XML record.</p>

</div>

<a name="major-05"></a><h4>DDMS 4.0 skipped in favor of DDMS 4.0.1 (<a href="http://code.google.com/p/ddmsence/issues/detail?id=174">Issue #174</a>)</h4>
<div class="upgradeGuide">

<p>DDMS 4.0 was released in September 2011 with an oversight on the technical implementation of the <code>pocType</code> attribute on producer roles. DDMS 4.0
contained a <code>ddms:POCType</code> attribute for this, but it was soon determined by the IC that this would break IRM instances. DDMS 4.0.1 was quickly released a month
later and employs <code>ISM:pocType</code> instead.</p>

<p>Although this change (removing the old attribute and adding a new one) breaks backwards compatibility, the decision was made to reuse the DDMS 4.0
XML namespace, given that the adoption of DDMS 4.0 was assumed to be relatively low. Because DDMS 4.0 is considered to be "broken", and because
DDMS 4.0.1 was released before I released DDMSence 2.0.0, I have elected not to support DDMS 4.0.</p>

<p><b>How to Upgrade:</b></p>
<p>There are no upgrade steps required. "4.0" is not a valid supported DDMS version in DDMSence. If your organization is upgrading from an earlier
version of DDMS, the DDMS team is strongly encouraging that you go directly to 4.0.1 anyhow.</p>

</div>

<h3>Minor Changes</h3>

<a name="minor-01"></a><h4>Validation for security rollup removed (<a href="http://code.google.com/p/ddmsence/issues/detail?id=165">Issue #165</a>)</h4>

<div class="upgradeGuide">
<p>Previously, DDMSence would do a basic check to see that security classifications of top-level components
were never more restrictive than the metacard itself, and also checked to see that all classifications used
a consistent system (US vs NATO). In DDMS 4.0.1, classification markings can go much deeper into the object
hierarchy, and NATO markings are now handled apart from classifications. Rather than reinvent the wheel
here, it would be better to use the ISM Schematron files to correctly and comprehensively manage your
security classifications.</p>
	
<p><b>How to Upgrade:</b></p>
<p>Validate rollup with the Schematron files that come with each version of ISM. There are instructions
in the Power Tip on <a href="documentation-schematron.jsp">Schematron Validation</a> which show how to use these files with DDMSence.
</div>

<a name="minor-02"></a><h4>Reusable components no longer track parentType (<a href="http://code.google.com/p/ddmsence/issues/detail?id=162">Issue #162</a>)</h4>

<div class="upgradeGuide">
<p>Person, Organization, Service, Unknown, and CountryCode previously maintained a reference to the enclosing component 
(such as "creator" for Person or "geographicIdentifier" for CountryCode). This made the hierarchy more fragile than necessary, and has been removed.</p>

<p><b>How to Upgrade:</b></p>
<p>Constructor calls which previously had the parentType:</p>
	
<pre class="brush: java">new CountryCode("postalAddress", element);</pre>
	
<p>Now no longer have a parentType:</p>
	
<pre class="brush: java">new CountryCode(element);</pre>
</div>

<a name="minor-03"></a><h4>Method signatures have changed (multiple issues)</h4>

<div class="upgradeGuide">
<p>Several methods have had their names changed for consistency, or have a different number of parameters.</p>

<ul>
<li><u>Category</u>: The constructor has additional parameters for optional attributes. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=155">Issue #155</a>)</li>
<li><u>Dates</u>: The constructor has additional parameters for optional dates. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=117">Issue #117</a>)</li>
<li><u>DDMSVersion</u>: getIcismNamespace() became getIsmNamespace() (<a href="http://code.google.com/p/ddmsence/issues/detail?id=95">Issue #95</a>).</li>
<li><u>GeographicIdentifier</u>: The constructor has an additional parameter for optional subDivisionCode. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=122">Issue #122</a>)</li>
<li><u>ISMVocabulary</u>: setIsmVersion() became setDDMSVersion() (<a href="http://code.google.com/p/ddmsence/issues/detail?id=95">Issue #95</a>).</li>
<li><u>Keyword</u>: The constructor has an additional parameter for optional attributes. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=156">Issue #156</a>)</li>
<li><u>Organization</u>: The constructor has additional parameters for optional subOrganization and acronym. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=113">Issue #113</a>)</li>
<li><u>Person</u>: The order of the constructor parameters has been reordered to match the DDMS 4.0.1 schema (<a href="http://code.google.com/p/ddmsence/issues/detail?id=114">Issue #114</a>)</li>
<li><u>RelatedResource</u>: The constructor has additional parameters for new attributes. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=130">Issue #130</a>)</li>
<li><u>Resource</u>: getSubjectCoverage() became getSubjectCoverages() and now returns a list of SubjectCoverage components (<a href="http://code.google.com/p/ddmsence/issues/detail?id=126">Issue #126</a>)</li>
<li><u>Resource</u>: getDESVersion() became getIsmDESVersion() to avoid conflict with ntk:DESVersion (<a href="http://code.google.com/p/ddmsence/issues/detail?id=131">Issue #131</a>)</li>
<li><u>Resource</u>: Constructor updated to include ISM:compliesWith, which used to be part of SecurityAttributes (<a href="http://code.google.com/p/ddmsence/issues/detail?id=168">Issue #168</a>)</li>
<li><u>Security</u>: The constructor has additional parametesr for access and noticeList. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=111">Issue #111</a>)</li>
<li><u>SubjectCoverage</u>: The constructor has additional parameters for productionMetrics and nonStateActors.  (<a href="http://code.google.com/p/ddmsence/issues/detail?id=126">Issue #126</a>)</li>
<li><u>Type</u>: The constructor has an additional parameter for optional description. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=118">Issue #118</a>)</li>
</ul>
<p><b>How to Upgrade:</b></p>
<p>If any method calls show compiler errors after upgrading to DDMSence 2.0.0, consult the API documentation and update the method signature. If a constructor
has additional parameters to support new data from DDMS 4.0.1 and you are using an older version of DDMS, you can probably get away with simply passing in a <code>null</code> value.</p>	
</div>

<a name="minor-04"></a><h4>XLink attributes encapsulated in a class (<a href="http://code.google.com/p/ddmsence/issues/detail?id=166">Issue #166</a>)</h4>

<div class="upgradeGuide">
<p>The XLink attributes which decorate a ddms:link element (and other new DDMS 4.0.1 elements) have been extracted into a standalone attribute group called
<a href="/docs/index.html?buri/ddmsence/ddms/summary/xlink/XLinkAttributes.html">XLinkAttributes</a>.</p>

<p><b>How to Upgrade:</b></p>
<p>If you had old Link code that looked like this:</p>

<pre class="brush: java">Link link = new Link(href, role, title, label);
String role = link.getRole();</pre>

<p>The XLink attributes would now be grouped together:</p>
<pre class="brush: java">XLinkAttributes attributes = new XLinkAttributes(href, role, title, label);
Link link = new Link(attributes);
String role = link.getXLinkAttributes().getRole();</pre>
</div>

<a name="minor-05"></a><h4>Public NAME constants removed (<a href="http://code.google.com/p/ddmsence/issues/detail?id=152">Issue #152</a>)</h4>
<div class="upgradeGuide">

<p>Previously, all components had a public NAME field containing the expected name of the component. Because
many elements have had their names changed in DDMS 4.0.1 (i.e. ddms:Person became ddms:person), component
classes now have a static method which returns a name for some DDMSVersion.</p>

<p><b>How to Upgrade:</b></p>
<p>If you had old Link code that looked like this:</p>
<pre class="brush: java">String extentName = Extent.NAME;</pre>
<p>You would now call a static method:</p>
<pre class="brush: java">String extentName = Extent.getName(ddmsVersion);</pre>
<p>The non-static <code>getName()</code> method on each component will still return the currently defined name for the component.</p>

</div>

<a name="minor-06"></a><h4>Non-DDMS classes moved into new packages (<a href="http://code.google.com/p/ddmsence/issues/detail?id=148">Issue #148</a>)</h4>

<div class="upgradeGuide">

<p>Classes are now organized into packages based first on the "Set" from the DDMS specification (Metacard, Resource, Format, etc.) and
then on the XML namespace (ISM, GML, etc.).</p>

<ul>
<li>Abstract classes have moved up to <code>buri.ddmsence</code>.</li>
<li>GML classes (Point, Polygon, Position, and SRSAttributes) have moved into <code>buri.ddmsence.ddms.summary.gml</code>.</li>
<li>ISM classes (ISMVocabulary, and SecurityAttributes) have moved into <code>buri.ddmsence.ddms.security.ism</code>.</li>
</ul>

<p><b>How to Upgrade:</b></p>
<p>Update any Java import statements that point to the old packages.</p>

</div>

<a name="minor-07"></a><h4>Configurable property name changed (<a href="http://code.google.com/p/ddmsence/issues/detail?id=95">Issue #95</a>)</h4>

<div class="upgradeGuide">
<ul>
<li><code>icism.prefix</code> became <code>ism.prefix</code>.</li>
</ul>
<p><b>How to Upgrade:</b></p>
<p>Update property names if you used these custom properties.</p>
</div>

<a name="minor-08"></a><h4>Class names changed (multiple issues)</h4>

<div class="upgradeGuide">

<ul>
<li>MediaExtent renamed to Extent: Because the Media wrapper was removed in DDMS 4.0.1, the old name no longer made sense. (<a href="http://code.google.com/p/ddmsence/issues/detail?id=127">Issue #127</a>).</li>
<li>IProducerEntity renamed to IRoleEntity: Because Entities can now fulfill non-producer roles, the old name no longer made sense (<a href="http://code.google.com/p/ddmsence/issues/detail?id=163">Issue #163</a>).</li>
</ul>

<p><b>How to Upgrade:</b></p>
<p>Change the class names of the affected classes.</p>	
</div>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="downloads.jsp">Back to Downloads</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>