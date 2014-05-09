<html>
<head>
	<title>DDMSence: Power Tip - Working With Different DDMS Versions</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Working with Different DDMS Versions</h1>

<h2>Using Alternate Versions</h2>

<p>DDMSence currently supports six versions of DDMS: 2.0, 3.0, 3.1, 4.0.1, 4.1, and 5.0. The DDMSReader class must be initialized with the DDMS version
of files it will be loading.</p>

<pre class="brush: java">DDMSVersion version = DDMSVersion.getVersionFor("4.1");
Resource resource = (new DDMSReader(version)).getDDMSResource(my41resourceFile);
System.out.println("This metacard was created with DDMS "
   + DDMSVersion.getVersionForNamespace(resource.getNamespace()));</pre>
<p class="figure">Figure 1. Loading resources from XML files</p>

<pre class="brush: xml">This metacard was created with DDMS 4.1</pre>
<p class="figure">Figure 2. Output of the code in Figure 1</p>

<p>When building DDMS components from scratch, the <a href="/docs/index.html?buri/ddmsence/util/DDMSVersion.html">DDMSVersion</a>
class controls the version being used.</p>

<pre class="brush: java">DDMSVersion.setCurrentVersion("2.0");
System.out.println("The current version is " + DDMSVersion.getCurrentVersion());
Identifier identifier = new Identifier("http://metadata.dod.mil/mdr/ns/MDR/0.1/MDR.owl#URI",
   "http://www.whitehouse.gov/news/releases/2005/06/20050621.html");
System.out.println("This identifier was created with DDMS "
   + DDMSVersion.getVersionForNamespace(identifier.getNamespace()));
DDMSVersion.setCurrentVersion("3.0");
System.out.println("The current version is " + DDMSVersion.getCurrentVersion());
Identifier identifier2 = new Identifier("http://metadata.dod.mil/mdr/ns/MDR/0.1/MDR.owl#URI",
   "http://www.whitehouse.gov/news/releases/2005/06/20050621.html");
System.out.println("This identifier was created with DDMS "
   + DDMSVersion.getVersionForNamespace(identifier.getNamespace()));</pre>
<p class="figure">Figure 3. Creating Identifiers using different DDMS versions</p>
   
<pre class="brush: xml">The current version is 2.0
This identifier was created with DDMS 2.0
The current version is 3.0
This identifier was created with DDMS 3.0</pre>
<p class="figure">Figure 4. Output of the code in Figure 3</p>
  
<p>There is an instance of DDMSVersion for each supported version, and this instance contains any related XML namespaces. Some namespaces were introduced in later versions of DDMS,
and will be blank in earlier versions.</p>

<pre class="brush: java">DDMSVersion version = DDMSVersion.setCurrentVersion("4.1");
System.out.println("In DDMS " + version.getVersion() + ", the following namespaces are used: ");
System.out.println("ddms: " + version.getNamespace());
System.out.println("gml: " + version.getGmlNamespace());
System.out.println("ism: " + version.getIsmNamespace());
System.out.println("ntk: " + version.getNtkNamespace());
System.out.println("tspi: " + version.getTspiNamespace()); // Doesn't exist
System.out.println("xlink: " + version.getXlinkNamespace());
System.out.println("Are we using DDMS 4.1? " + DDMSVersion.isCurrentVersion("4.1"));
System.out.println("Can we use components that were introduced in DDMS 3.1? " + version.isAtLeast("3.1"));</pre>
<p class="figure">Figure 5. Learning details of the current DDMSVersion</p>

<pre class="brush: xml">In DDMS 4.1, the following namespaces are used: 
ddms: urn:us:mil:ces:metadata:ddms:4
gml: http://www.opengis.net/gml/3.2
ism: urn:us:gov:ic:ism
ntk: urn:us:gov:ic:ntk
tspi: 
xlink: http://www.w3.org/1999/xlink
Are we using DDMS 4.1? true
Can we use components that were introduced in DDMS 3.1? true</pre>
<p class="figure">Figure 6. Output of the code in Figure 5</p>

<p>Calling <code>DDMSVersion.setCurrentVersion("2.0")</code> will make any components you create from that point on obey DDMS 2.0 
validation rules. The default version if you never call this method is "5.0" (but you should always explicitly set the current version yourself,
because this default changes as new versions of DDMS are released). The version is maintained as a static variable, so this 
is not a thread-safe approach, but I believe that the most common use cases will deal with DDMS components of a single version at a time,
and I wanted the versioning mechanism to be as unobtrusive as possible.</p>

<h2>Differences Between Versions</h2>

<p>The validation rules between versions of DDMS are very similar, but there are a few major differences. The API documentation for each class
contains a helpful chart of validation rules that have changed over the years. For example, the 
<a href="docs/index.html?buri/ddmsence/ddms/resource/Unknown.html">Unknown</a>
entity for producers was not introduced until DDMS 3.0, so attempts to create one in DDMS 2.0 will fail.</p>

<pre class="brush: java">DDMSVersion.setCurrentVersion("2.0");
List&lt;String&gt; names = Util.getXsListAsList("UnknownEntity"); 
Unknown unknown = new Unknown(names, null, null);</pre>
<p class="figure">Figure 7. This code will throw an InvalidDDMSException</p>

<p>If you have a set of DDMS metacards from an older version of DDMS and wish to transform them to a newer version, you can do so with the <a href="documentation-builders.jsp">Component
Builder</a> framework. Builders allow you to load the old metacard, add any new fields that are required, and save it in the new version.</p>

<h2>DDMS 3.0.1</h2>

<p>DDMS release 3.0.1 was merely a documentation release which clarified some of the supporting documentation on geospatial elements. Because none of the 
schemas or components themselves were updated, 3.0.1 reuses all of the same technical information from 3.0 (including XML namespaces). DDMSence treats 3.0.1 as an alias 
for DDMS 3.0 -- you can set your DDMS version to 3.0.1, but DDMSence will continue to use DDMS 3.0 artifacts.</p>

<pre class="brush: java">DDMSVersion.setCurrentVersion("3.0.1");
System.out.println(DDMSVersion.getCurrentVersion().getVersion());
</pre>
<p class="figure">Figure 8. This code will print out "3.0".</p>

<h2>DDMS 4.0</h2>

<p>DDMS 4.0 was released in September 2011 with an oversight on the technical implementation of the <code>pocType</code> attribute on producer roles. DDMS 4.0
contained a <code>ddms:POCType</code> attribute for this, but it was soon determined by the IC that this would break IRM instances. DDMS 4.0.1 was quickly released a month
later and employs <code>ISM:pocType</code> instead.</p>

<p>Although this change (removing the old attribute and adding a new one) breaks backwards compatibility, the decision was made to reuse the DDMS 4.0
XML namespace, given that the adoption of DDMS 4.0 was assumed to be relatively low. Because DDMS 4.0 is considered to be "broken", I have elected not to
support it in DDMSence.</p>
 
<h2>DDMS 4.0.1</h2>

<p>DDMS 4.0.1 was released in November 2011. It shares the same XML namespace as DDMS 4.1, and unfortunately DDMS offers no mechanism to tell the difference
between 4.0.1 instances and 4.1 instances. Because of this, all instances with the shared XML namespace will be validated against 4.1 schemas. In cases where
new 4.1 elements or attributes are found in the XML instance, DDMSence will provide warnings that the instance might not be parseable by a DDMS 4.0.1 system.</p>
 
<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>