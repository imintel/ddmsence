<html>
<head>
	<title>DDMSence: Power Tip - Schematron Validation</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Schematron Validation</h1>

<p>It is expected that organizations and communities of interest may have additional constraints on the data in their DDMS Resources, besides the rules in the DDMS specification.
DDMSence provides support for these rules through the <a href="http://www.schematron.com/">ISO Schematron</a> standard. Using a combination of a configurable XSLT engine
and <a href="http://xom.nu/">XOM</a>, DDMSence can validate a Resource against a custom Schematron file (<code>.sch</code>) and return the results of validation as
a list of <a href="/docs/index.html?buri/ddmsence/ddms/ValidationMessage.html">ValidationMessage</a>s. The XSLT transformation makes use of Rick Jelliffe's <a href="http://www.schematron.com/implementation.html">mature implementation</a>
of ISO Schematron.</p> 

<p>Creating a custom Schematron file is outside the scope of this documentation, but there are plenty of Schematron tutorials available online, and I have also codified several
complex rules from the DDMS Specification for example's sake in the <a href="#explorations">Explorations</a> section. There are two very simple examples in the 
<code>/data/sample/schematron/</code> directory. The file, <code><a href="http://ddmsence.googlecode.com/svn/trunk/data/sample/schematron/testPublisherValueXslt1.sch">testPublisherValueXslt1.sch</a></code> examines the surname of person designated as a publisher and 
fails if the surname is "<b>Uri</b>".</p>

<pre class="brush: xml">&lt;iso:pattern title="Fixed Surname Value"&gt;
   &lt;iso:rule context="//ddms:publisher/ddms:person/ddms:surname"&gt;
      &lt;iso:report test="normalize-space(.) = 'Uri'"&gt;Members of the Uri family cannot be publishers.&lt;/iso:report&gt;
   &lt;/iso:rule&gt;
&lt;/iso:pattern&gt;</pre>
<p class="figure">Figure 1. The test from testPublisherValueXslt1.sch</p>

<p>The file, <code><a href="http://ddmsence.googlecode.com/svn/trunk/data/sample/schematron/testPositionValuesXslt2.sch">testPositionValuesXslt2.sch</a></code> forces any positions to match an exact location in Reston, Virginia. It makes use of the
XPath 2.0 function, <code>tokenize()</code>, so it must be handled with an XSLT2-compatible engine. DDMSence decides whether to use XSLT1 or XSLT2 based on the <code>queryBinding</code>
attribute on the root element of your Schematron file. The supported values are <code>xslt</code> or <code>xslt2</code>, and the former will be the default if this attribute does not exist.</p>

<pre class="brush: xml">&lt;iso:pattern id="FGM_Reston_Location"&gt;
   &lt;iso:rule context="//gml:pos"&gt;
      &lt;iso:let name="firstCoord" value="number(tokenize(text(), ' ')[1])"/&gt;
      &lt;iso:let name="secondCoord" value="number(tokenize(text(), ' ')[2])"/&gt;
      &lt;iso:assert test="$firstCoord = 38.95"&gt;The first coordinate in a gml:pos element must be 38.95 degrees.&lt;/iso:assert&gt;
      &lt;iso:assert test="$secondCoord = -77.36"&gt;The second coordinate in a gml:pos element must be -77.36 degrees.&lt;/iso:assert&gt;
   &lt;/iso:rule&gt;
&lt;/iso:pattern&gt;</pre>
<p class="figure">Figure 2. The test from testPositionValuesXslt2.sch</p>

<p>The following code sample will build a DDMS Resource from one of the sample XML files, and then validate it through Schematron:</p>

<pre class="brush: java">File resourceFile = new File("data/sample/5.0-ddmsenceExample.xml");
File schFile = new File("data/sample/schematron/testPublisherValueXslt1.sch");

DDMSReader reader = new DDMSReader();
Resource resource = reader.getDDMSResource(resourceFile);
List&lt;ValidationMessage&gt; schematronMessages = resource.validateWithSchematron(schFile);
for (ValidationMessage message : schematronMessages) {
   System.out.println("Location: " + message.getLocator());
   System.out.println("Message: " + message.getText());
}</pre>
<p class="figure">Figure 3. Sample code to validate 5.0-ddmsenceExample.xml with testPublisherValueXslt1.sch</p>

<pre class="brush: xml">Location: //*[local-name()='Resource' and namespace-uri()='urn:us:mil:ces:metadata:ddms:5']
   /*[local-name()='publisher' and namespace-uri()='urn:us:mil:ces:metadata:ddms:5']
   /*[local-name()='person' and namespace-uri()='urn:us:mil:ces:metadata:ddms:5']
   /*[local-name()='surname' and namespace-uri()='urn:us:mil:ces:metadata:ddms:5']
Message: Members of the Uri family cannot be publishers.</pre>
<p class="figure">Figure 4. Ouput of the code from Figure 3</p>

<p>Schematron files are made up of a series of patterns and rules which assert rules and report information. The raw output of Schematron validation
is a series of <code>failed-assert</code> and <code>successful-report</code> elements in Schematron Validation Report Language (SVRL). DDMSence converts
this output into ValidationMessages with a locator value taken from the <code>location</code> attribute in SVRL. The type returned is "warning" for 
"successful-report" messages and "error" for "failed-assert" messages. 
It is important to notice that 1) Schematron validation can only be performed on Resources which are already valid according to the DDMS specification and 
2) the results of Schematron validation will <b>never</b> invalidate the DDMSence object model. It is the responsibility of the Schematron user to react 
to any ValidationMessages.</p>

<p>Schematron files contain the XML namespaces of any elements you might traverse -- please make sure you use the correct namespaces for the version
of DDMS you are employing. The sample files described above are written only for DDMS 5.0.</p>

<h2>Validating with Intelligence Community Schematron Files</h2>

<p>The Intelligence Community specifications (ISM, NTK, and VIRT) include Schematron files for validating logical constraints on the IC attributes. 
DDMSence does not include these files, but you can download the Public Release versions from the 
<a href="http://www.dni.gov/index.php/about/organization/chief-information-officer/trusted-data-format">ODNI website</a>, and your organization might have
access to versions from higher classification levels as well. The top-level Schematron file for ISM, <code>ISM/Schematron/ISM/ISM_XML.sch</code> is the orchestration
point for each of the supporting files and the vocabularies needed for validation. A similar pattern can be seen in NTK and VIRT.</p>

<p>Here is an example which validates one of the sample DDMS metacards against the ISM.XML Schematron files. It assumes that the top-level file and all of
the files and subdirectories it depends on have been copied into the working directory.</p>

<pre class="brush: java">File schematronFile = new File("ISM_XML.sch");
Resource resource = new DDMSReader().getDDMSResource(new File("data/sample/4.1-ddmsenceExample.xml"));
List&lt;ValidationMessage&gt; messages = resource.validateWithSchematron(schematronFile);
for (ValidationMessage message : messages) {
   System.out.println("Location: " + message.getLocator());
   System.out.println("Message: " + message.getText());
}</pre>
<p class="figure">Figure 5. Sample code to validate with ISM.XML Schematron Files</p>

<p>Running this code will not display any errors or warnings, but we can make the output more exciting by intentionally breaking a rule. One of the rules described
in the DES states that <code>ISM:ownerProducer</code> token values must be in alphabetical order (ISM-ID-00100). If you edit this attribute on the root node
of the DDMS resource file so the value is <code>"USA AUS"</code> and then run the code again, you should get the following output.</p>

<pre class="brush: xml">Location: //*:resource[namespace-uri()='urn:us:mil:ces:metadata:ddms:4'][1]
   /*:title[namespace-uri()='urn:us:mil:ces:metadata:ddms:4'][1]
Message: [ISM-ID-00100][Error] If ISM-CAPCO-RESOURCE and attribute ownerProducer is specified, then each of its values must 
   be ordered in accordance with CVEnumISMOwnerProducer.xml. The following values are out of order [AUS] for [USA AUS]</pre>
<p class="figure">Figure 6. Schematron output when intentionally flaunting the rules</p>

<p>Be aware that a DDMS 5.0 assertion is not a complete record on its own -- it is intended for insertion into an IC Trusted Data Object. Because of this, using IC
Schematron files on the assertion alone may not be successful. You will need to validate the entire TDO record with the Schematron files. This operation is outside the scope
of what DDMSence offers.</p>

<h2>Supported XSLT Engines</h2>

<p>DDMSence comes bundled with Saxon Home Edition because it supports both XSLT1 and XSLT2 transformations. Support for alternate engines is provided through the 
<code>xml.transform.TransformerFactory</code> configurable property, which can be set to the class name of another processor. Please 
see the Power Tip on <a href="documentation-configuration.jsp">Configurable Properties</a> for details on how to set this property. The table below lists the engines I have tested with.
None of the engines listed will work with XSLT 2 Schema-Aware (SA) Schematron files.</p>

<table>
<tr><th>Name and Version</th><th>Class Name</th><th>XSLT1</th><th>XSLT2</th></tr>
<tr><td>Saxon HE 9.5.1.1</td><td><code>net.sf.saxon.TransformerFactoryImpl</code></td><td>supported</td><td>supported</td></tr>
<tr><td>Xalan interpretive, v2.7.1</td><td><code>org.apache.xalan.processor.TransformerFactoryImpl</code></td><td>supported</td><td>fails, doesn't support XSLT 2.0</td></tr>
<tr><td>Xalan XSLTC, v2.7.1</td><td><code>org.apache.xalan.xsltc.trax.TransformerFactoryImpl</code></td><td>fails, SVRL transformation doesn't seem to occur properly</td><td>fails, doesn't support XSLT 2.0</td></tr>
<tr><td>Xalan XSLTC, bundled with Java 1.5</td><td><code>com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl</code></td><td>fails, Xalan bug treats XSLT warning as an error</td><td>fails, doesn't support XSLT 2.0</td></tr>
<tr><td>Xalan XSLTC, bundled with Java 1.6</td><td><code>com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl</code></td><td>supported</td><td>fails, doesn't support XSLT 2.0</td></tr>
</table>
<p class="figure">Table 1. XSLT Engines for Schematron Validation</p>

<p>Be aware that DDMSence also uses the Saxon engine for some utility library functions. Even if you choose an alternate XSLT engine for Schematron validation, you will still need to include the Saxon JAR file in your classpath.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>