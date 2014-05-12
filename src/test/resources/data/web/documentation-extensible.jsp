<html>
<head>
	<title>DDMSence: Power Tip -The Extensible Layer</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: The Extensible Layer</h1>

<p><i><b>Note</b>: This Power Tip only applies to DDMS 2.0 through 4.1. DDMS 5.0 no longer has an Extensible Layer, and extensions are stored elsewhere in the IC Trusted Data Format (TDF).</i></p>

<p>DDMS is composed of five Core Sets (Metacard Info, Security, Resource, Summary Content, and Format) and the Extensible Layer. This layer supports extensibility
by providing space for custom attributes and elements within a <code>ddms:resource</code>. Specifically, custom attributes can be added to any producer
(Organization, Person, Service, and Unknown), a Keyword, a Category, and the Resource itself. A Resource can also have an unlimited number of custom
elements after the <code>ddms:security</code> element. These extensions are identified by the <code>xs:any</code> and <code>xs:anyAttribute</code>
definitions in the schema. The main restriction on content is that custom elements and attributes must belong to an XML namespace other than the
DDMS namespace.</p>

<p>Because any manner of content could appear in the Extensible Layer, DDMSence merely provides a consistent interface to access to the underlying 
XOM Elements and Attributes. Any business logic to be performed on this Layer is left up to the implementation, so some knowledge of 
<a href="http://xom.nu/">XOM</a> will be useful. </p>

<p>The relevant code can be found in the <code>buri.ddmsence.ddms.extensible</code> package. It may also be useful to load the sample file,  
<code>3.0-extensibleLayerExample.xml</code> into the <u>Essentials</u> application, because it has an example of each extension.</p>

<h2>ExtensibleElements</h2>

<p>An unlimited number of elements from any XML namespace other than the DDMS namespace can appear at the end of a <code>ddms:resource</code>. (In DDMS 2.0,
only 1 can appear). These elements are implemented with the <a href="/docs/index.html?buri/ddmsence/ddms/extensible/ExtensibleElement.html">ExtensibleElement</a> class,
which acts like any other IDDMSComponent and exposes <code>getXOMElementCopy()</code> to return a copy of the underlying XOM Element. Below is an
example of an extensible element as it might appear in an XML file.</p> 

<pre class="brush: xml">   [...]
   &lt;/ddms:subjectCoverage&gt;
   &lt;ddms:security ISM:ownerProducer="USA" ISM:classification="U" ISM:excludeFromRollup="true"/&gt;
   &lt;ddmsence:extension xmlns:ddmsence="http://ddmsence.urizone.net/"&gt;
      This is an extensible element.
   &lt;/ddmsence:extension&gt;
&lt;/ddms:resource&gt;</pre>
<p class="figure">Figure 1. An extensible element as it would appear in a ddms:resource</p>

<p>Unlike most DDMS components, which have a constructor for XOM elements and a constructor for raw data, ExtensibleElement only has one constructor
(since the raw data is, itself, a XOM element). If you are using a DDMSReader instance to load data from an XML file, the ExtensibleElements will be created automatically,
and can be accessed with <code>Resource.getExtensibleElements()</code>. Here is an example of how you might build a simple one from scratch:</p>

<pre class="brush: java">Element element = new Element("ddmsence:extension", "http://ddmsence.urizone.net/");
element.appendChild("This is an extensible element.");
ExtensibleElement component = new ExtensibleElement(element);</pre>
<p class="figure">Figure 2. Creating a simple ExtensibleElement from scratch</p>

<p>Once you have an ExtensibleElement, you can add it to a list of top-level components (like any other IDDMSComponent), and pass it into a Resource constructor.
Creating more complex Elements from scratch requires XOM knowledge, and is outside the scope of this documentation.</p>

<h2>ExtensibleAttributes</h2>

<p>The <a href="/docs/index.html?buri/ddmsence/ddms/extensible/ExtensibleAttributes.html">ExtensibleAttributes</a> class follows the same implementation
pattern as SecurityAttributes and SRSAttributes. The accessor, <code>getAttributes()</code> will return a read-only list of all the underlying XOM Attributes.
Below is an example of an extensible attribute as it might appear in an XML file, and how it could be created from scratch:</p>

<pre class="brush: xml">&lt;ddms:keyword xmlns:ddmsence="http://ddmsence.urizone.net/" ddms:value="XML" ddmsence:relevance="99" /&gt;</pre>
<p class="figure">Figure 3. An XML element with extensible attributes</p>

<pre class="brush: java">List&lt;Attribute&gt; extAttributes = new ArrayList&lt;Attribute&gt;();
extAttributes.add(new Attribute("ddmsence:relevance", "http://ddmsence.urizone.net/", "99"));
ExtensibleAttributes extensions = new ExtensibleAttributes(extAttributes);
Keyword keyword = new Keyword("XML", extensions);</pre>
<p class="figure">Figure 4. Creating the extensible attribute from scratch</p>

<h2>ExtensibleAttributes on a Resource</h2>

<p>The <code>ddms:resource</code> element has additional (ISM) attributes that might conflict with your extensible
attributes. The situation is trickier in DDMS 2.0, where the ISM attributes are not explicitly defined in the schema, but can exist nonetheless because
of the leeway provided with <code>xs:anyAttribute</code>.</p>

<p>When creating an ExtensibleAttributes instance based upon a <code>ddms:resource</code> XOM Element:</p>
<ul>
	<li>First, ISM resource attributes such as <code>ISM:DESVersion</code> will be "claimed", if present.</li>
	<li>Next, the ISM attributes such as <code>ISM:noticeType</code> will be converted into a NoticeAttributes instance.</li>
	<li>Next, the ISM attributes such as <code>ISM:classification</code> will be converted into a SecurityAttributes instance.</li>
	<li>Any remaining attributes are considered to be ExtensibleAttributes.</li>
</ul>

<p>When building an ExtensibleAttributes instance from scratch and placing it on a Resource:</p>
<ul>
	<li>ISM resource attributes which exist as constructor parameters, such as <code>ISM:DESVersion</code> are processed first, if present.</li>
	<li>Next, the NoticeAttributes, such as <code>ISM:noticeType</code> are processed.</li>
	<li>Next, the SecurityAttributes, such as <code>ISM:classification</code> are processed.</li>
	<li>Finally, the ExtensibleAttributes are processed. This means that these attributes cannot conflict with any attributes from the first two steps.</li>
</ul>
<p>In DDMS 2.0, it is perfectly legal to implement one of the resource attributes or security attributes as an extensible attribute:</p>

<pre class="brush: java">DDMSVersion.setCurrentVersion("2.0");

// DESVersion as a resource attribute
Resource resource1 = new Resource(myComponents, null, null, new Integer(2), null);

// DESVersion as an extensible attribute
String icNamespace = DDMSVersion.getCurrentVersion().getIcismNamespace();
List&lt;Attribute&gt; attributes = new ArrayList&lt;Attribute&gt;();
attributes.add(new Attribute("ISM:DESVersion", icNamespace, "2"));
ExtensibleAttributes extensions = new ExtensibleAttributes(attributes);
Resource resource2 = new Resource(myComponents, null, null, null, null, extensions);</pre>
<p class="figure">Figure 5. These two approaches for a resource attribute are both legal in DDMS 2.0</p>

<pre class="brush: java">DDMSVersion.setCurrentVersion("2.0");

// classification and ownerProducer as security attributes
List&lt;String&gt; ownerProducers = new ArrayList&lt;String&gt;();
ownerProducers.add("USA");
SecurityAttributes secAttributes = new SecurityAttributes("U", ownerProducers, null);
Resource resource = new Resource(myComponents, null, null, null, secAttributes);

// classification and ownerProducer as extensible attributes
String icNamespace = DDMSVersion.getCurrentVersion().getIcismNamespace();
List&lt;Attribute&gt; attributes = new ArrayList&lt;Attribute&gt;();
attributes.add(new Attribute("ISM:classification", icNamespace, "U"));
attributes.add(new Attribute("ISM:ownerProducer", icNamespace, "USA"));
ExtensibleAttributes extensions = new ExtensibleAttributes(attributes);
Resource resource = new Resource(myComponents, null, null, null, null, extensions);</pre>
<p class="figure">Figure 6. These two approaches for security attributes are both legal in DDMS 2.0</p>

<p>As a best practice, it is recommended that you create these attributes as explicitly as possible: if an attribute can be defined with constructor parameters or inside
of a SecurityAttributes instance, it should. This will make DDMS 2.0 metacards more consistent with their newer counterparts.</p>

<h2>ExtensibleAttributes on a Keyword or Category</h2>

<p>Starting with DDMS 4.0.1, <code>ddms:keyword</code> and <code>ddms:category</code> can have both extensible and security attributes, just like a Resource. The same guidelines apply in this
situation -- define your security attributes as explicitly as possible to avoid confusion.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>