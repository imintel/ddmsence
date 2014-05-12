<html>
<head>
	<title>DDMSence: Power Tip - Using Component Builders</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Using Component Builders</h1>

<p>Every DDMS component has an associated <a href="/docs/buri/ddmsence/ddms/IBuilder.html">Builder</a> class
which offers a mutable way to build components. A Builder class can be the form bean behind an HTML form on a website, allowing someone to fill in the details page
by page (this is discussed more below). A Builder class can also be initialized with an existing component to allow for editing after it has already been saved. 
Properties on a Builder class can be set or re-set, and the strict DDMSence validation does not occur until
you are ready to <code>commit()</code> the changes.</p>

<p>Builder classes for components that have child components flexibly handle any nested Builders, so you do not have to make your edits from the lowest level component. This differs
from the approach described in <a href="tutorials-02.jsp">Tutorial #2: Escort</a>, where all child components must be complete and valid before proceeding up the hierarchy.
The following three figures provide an example of this difference, using SubjectCoverage as a representative component.</p>

<pre class="brush: xml">&lt;ddms:subjectCoverage ISM:classification="U"&gt;
   &lt;ddms:keyword ddms:value="DDMSence" /&gt;
&lt;/ddms:subjectCoverage&gt;</pre>

<p class="figure">Figure 1. An XML fragment containing Subject Coverage information</p>

<pre class="brush: java">List&lt;Keyword&gt; keywords = new ArrayList&lt;Keyword&gt;();
keywords.add(new Keyword("DDMSence", null)); // Keyword validated here
SecurityAttributes securityAttributes = new SecurityAttributes("U", null, null); // SecurityAttributes validated here
SubjectCoverage subjectCoverage = new SubjectCoverage(keywords, null, securityAttributes); // SubjectCoverage validated here</pre>

<p class="figure">Figure 2. The "bottoms-up" approach for building a SubjectCoverage component</p>

<pre class="brush: java">SubjectCoverage.Builder builder = new SubjectCoverage.Builder();
builder.getKeywords().get(0).setValue("DDMSence");
builder.getSecurityAttributes().setClassification("U");
SubjectCoverage subjectCoverage = builder.commit(); // All validation occurs here</pre>

<p class="figure">Figure 3. The Builder approach for building a SubjectCoverage component</p>

<p>As you can see, the Builder approach treats the building process from a "top-down" perspective. By using 
a <a href="http://ddmsence.urizone.net/docs/buri/ddmsence/ddms/Resource.Builder.html">Resource.Builder</a> instance, you can edit and traverse
a complete DDMS Resource, without necessarily needing to understand the intricacies of the components you aren't worried about. The code sample below
takes a List of pre-existing DDMS Resources, uses the Builder framework to edit a <code>ddms:dates</code> attribute on each one, and saves the results in a new List.</p>
 
<pre class="brush: java">List&lt;Resource&gt; updatedResources = new ArrayList&lt;Resource&gt;();
for (Resource resource : myExistingResources) {
   Resource.Builder builder = new Resource.Builder(resource);
   builder.getDates().setPosted("2011-05-13");
   updatedResources.add(builder.commit());
}</pre>

<p class="figure">Figure 4. Programatically editing a batch of Resources</p>

<p>There are a few implementation details to keep in mind when working with Builders:</p>
<ol>
<li>Calling a <code>get()</code> method that returns a child Builder instance (or a List of them) will never return <code>null</code>. A new Builder will be lazily instantiated if 
one does not already exist. An example of this can be seen on line 2 of Figure 3. When the value of the Keyword is set to "DDMSence", the List of Keyword.Builders
returned by <code>getKeywords()</code>, and the first Keyword.Builder returned by <code>get(0)</code> are both instantiated upon request.
This should make it easier for Component Builders to be used in a web-based form, where the number of child components might grow dynamically with JavaScript.</li>
<li>The <code>commit()</code> method on any given Builder will only return a component if the Builder was actually used, according its
implementation of <code>IBuilder.isEmpty()</code>. This decision was made to handle form beans more flexibly: if a user has not filled 
in any of the fields on a <code>ddms:dates</code> form, it is presumed that their intent is NO <code>ddms:dates</code> element, and not an empty, useless one. The exception to this
are the builders for any attribute group, such as SecurityAttributes. If the builder is empty, an empty attributes instance will be returned.</li>
<li>The <code>commit()</code> method will use the version of DDMS defined in <code>DDMSVersion.getCurrentVersion()</code> for validation and XML namespaces. Changing the current version during
the building process has no effect up until the moment that <code>commit()</code> is called. In addition, initializing a Builder with an existing resource will not change the current DDMSVersion value.</li>
</ol>

<p>The third detail is important, because it allows you to load a metacard from an old version of DDMS and transform it into a newer version.</p>

<h2>Transforming Between DDMS Versions</h2>

<p>The code sample below takes a DDMS 2.0 metacard and uses builders to transform it into newer versions of DDMS.</p>

<pre class="brush: java">
// Start from a 2.0 metacard
Resource.Builder builder = new Resource.Builder(my20Resource);
builder.setResourceElement(Boolean.TRUE);
builder.setCreateDate("2011-07-05");
builder.setIsmDESVersion(new Integer(2));
builder.getSecurityAttributes().setClassification("U");
builder.getSecurityAttributes().getOwnerProducers().add("USA");

// Save as a 3.0 metacard
DDMSVersion.setCurrentVersion("3.0");
Resource my30Resource = builder.commit();

// Now start from a 3.0 metacard
builder = new Resource.Builder(my30Resource);
builder.setIsmDESVersion(new Integer(5));

// Save as a 3.1 metacard
DDMSVersion.setCurrentVersion("3.1");
Resource my31Resource = builder.commit();

// Now start from a 3.1 metacard
builder = new Resource.Builder(my31Resource);
builder.setNtkDESVersion(new Integer(7));
builder.setIsmDESVersion(new Integer(9));
builder.getMetacardInfo().getIdentifiers().get(0).setQualifier("qualifier");
builder.getMetacardInfo().getIdentifiers().get(0).setValue("value");
builder.getMetacardInfo().getDates().setCreated("2011-09-25");
builder.getMetacardInfo().getPublishers().get(0).setEntityType("organization");
builder.getMetacardInfo().getPublishers().get(0).getOrganization().setNames(Util.getXsListAsList("DISA"));

// Skip 4.0.1, because it has the same XML namespace as 4.1

// Save as a 4.1 metacard
DDMSVersion.setCurrentVersion("4.1");
Resource my41Resource = builder.commit();

// Now start from a 4.1 metacard
builder = new Resource.Builder(my41Resource);
builder.setResourceElement(null);
builder.setCreateDate(null);
builder.setIsmDESVersion(null);
builder.setNtkDESVersion(null);
builder.setCompliesWiths("DDMSRules");
builder.setSecurityAttributes(null);
builder.setNoticeAttributes(null);
builder.setSecurity(null);
// If you used GML shapes or a postalAddress, your geospatialCoverages will be incompatible.
// Converting shapes and addresses into TSPI is not covered here.
builder.getGeospatialCoverages().clear();

// Save as a 5.0 assertion
DDMSVersion.setCurrentVersion("5.0");
Resource my50Resource = builder.commit();</pre>
<p class="figure">Figure 5. Transforming a DDMS 2.0 metacard with the Builder Framework</p>

<h2>Builders as Form Beans</h2>

<p>Because every Builder adheres to the JavaBean standard for having conventional get() and set() accessors, the Resource.Builder
or any sub-builders can easily be used in a web context. The next two figures show a sample form input field, and the way a
web library like Spring MVC might resolve that field into Java:

<pre class="brush: xml">&lt;input name="metacardInfo.publishers[0].entityType" type="hidden" value="person" /&gt;
&lt;input name="metacardInfo.publishers[0].person.surname" type="text" value="Uri" /&gt;</pre>
<p class="figure">Figure 6. Form fields identifying a publisher as a person, and setting his surname</p>

<pre class="brush: java">Resource.Builder builder = new Resource.Builder();
builder.getMetacardInfo().getPublishers().get(0).setEntityType("person");
builder.getMetacardInfo().getPublishers().get(0).getPerson().setSurname("Uri");</pre>
<p class="figure">Figure 7. Comparable Builder code to the form fields in Figure 6</p>

<p>I have created a sample <a href="builder.uri">DDMS Builder</a> web application which provides an example of this behavior.</li></p> 

<h2>Builders for TSPI Components</h2>

<p>DDMS 5.0 introduced the Time-Space-Position Information specification for geospatial shapes and addresses. The TSPI specification is incredibly complex and 
multi-layered, and it is unclear how much value a complete implementation in DDMSence would provide for discovery use cases. For this reason,
shapes and addresses in the TSPI namespace have very simple Builder classes which require the raw XML for a component.</p>

<pre class="brush: java">Point.Builder builder = new Point.Builder();
builder.setXml("&lt;tspi:Point xmlns:tspi=\"http://metadata.ces.mil/mdr/ns/GSIP/tspi/2.0\" "
   + "xmlns:gml=\"http://www.opengis.net/gml/3.2\" gml:id=\"PointMinimalExample\" srsDimension=\"3\" "
   + "srsName=\"http://metadata.ces.mil/mdr/ns/GSIP/crs/WGS84E_MSL_H\"&gt;"
   + "&lt;gml:pos&gt;53.8093938 -2.12955 4572&lt;/gml:pos&gt;&lt;/tspi:Point&gt;");
Point tspiPoint = builder.commit();</pre>
<p class="figure">Figure 8. Creating a TSPI Point with a Builder</p>

<p>This is actually a step back from the GML shape builders provided in older versions of DDMSence. As use cases refine and more organizations adopt DDMS 5.0, I will
revisit these components to determine whether a stronger solution would be useful.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>