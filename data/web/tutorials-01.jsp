<html>
<head>
	<title>DDMSence: Tutorial #1 (Essentials)</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<h1>Tutorial #1: <u>Essentials</u></h1>

<p>
<img src="./images/essentials-flow.png" width="400" height="156" title="Essentials Workflow" align="right" />
<u>Essentials</u> is a simple reader application which loads an XML file containing a DDMS Resource and displays it in three different formats: the
original XML, HTML, and Text. The source code for this application provides an example of how to create DDMS components from an XML file.</p>

<h2>Getting Started</h2>

<p><u>Essentials</u> can be run from the command line with the class, <code>buri.ddmsence.samples.Essentials</code>. The application accepts a single 
optional parameter: the name of a file to load upon startup.</p>

<p>Please see the "<a href="documentation.jsp#started">Getting Started</a>" section for classpath details and command line syntax.</p> 

<h3>Walkthrough</h3>

<p>When the application first opens, go to the <i>File</i> menu and choose <i>Open...</i>. You will be able to select an XML file containing
a DDMS resource (also called a "metacard"). Sample files from various versions of DDMS can be found in the <code>data/sample/</code> directory. Let's start by
selecting the sample file, <code>3.1-identifierPostalAddressExample.xml</code> and clicking on the <i>Open</i> button.</p>

<img src="./images/essentials-02.png" width="300" height="212" title="Essentials File Chooser" />
<p class="figure">Figure 1. Selecting an existing DDMS Record from an XML file</p>

<p>The application will convert the XML file into a Java object model and then display the results in three separate panes.</p>

<img src="./images/essentials-01.png" width="800" height="535" title="Essentials Screenshot" />
<p class="figure">Figure 1. The three output formats</p>

<p>The top pane contains the result of calling <code>toXML()</code> on the Resource object. It should be identical to the data from the file.
The two lower panes contain the results of calling <code>toHTML()</code> and <code>toText()</code>, respectively, on the Resource object.</p> 

<p>Now, let's take a look at the source code in <code>/src/samples/buri/ddmsence/samples/Essentials.java</code> to see how this was accomplished. The important lines are found in the
<code>loadFile()</code> method. First, we try to guess the version of DDMS used in the file, based upon the XML namespace. Next, we create a DDMSReader instance that
will load the file. Finally, we store the loaded resource in the <code>_resource</code> variable:</p>

<pre class="brush: java">DDMSVersion fileVersion = guessVersion(file);
// The DDMS reader builds a Resource object from the XML in the file.
_resource = getReader(fileVersion).getDDMSResource(file);
			
// The three output formats
String xmlFormat = getResource().toXML();
String htmlFormat = getResource().toHTML();
String textFormat = getResource().toText();</pre>
<p class="figure">Figure 3. The main Essentials code</p>

<p>The remaining code in this method merely renders the data on the screen.</p>

<p>As you can see from the code, building an object model from an XML file only requires a single-line call to <code>DDMSReader.getDDMSResource()</code>. The conversion of
the Resource into XML, HTML, and Text is built-in to the Object. The primary purpose of the DDMSReader class is to load a metacard from an XML file. You can also use the <code>getElement()</code> methods of the DDMSReader to load XOM Elements representing any of the global DDMS components.</p>

<p>Now that you have seen a valid Resource, let's try opening an invalid one. Return to the <i>File</i> menu and select the sample file, <code>3.0-invalidResourceExample.xml</code> from the
samples directory. This XML file is invalid, because it tries to use an incorrect security classification (SuperSecret).</p>

<p>Opening this file should display the following error message:</p>

<pre class="brush: xml">Could not create the DDMS Resource: nu.xom.ValidityException: cvc-enumeration-valid: 
Value 'SuperSecret' is not facet-valid with respect to enumeration '[U, C, S, TS, R, CTS, 
CTS-B, CTS-BALK, NU, NR, NC, NS, CTSA, NSAT, NCA]'. It must be a value from the enumeration. 
at line 18, column 110 in file:///DDMSence/data/sample/3.0-invalidResourceExample.xml
</pre>
<p class="figure">Figure 4. A sample error message</p>

<p>This error comes from the underlying XML-parsing libraries which are attempting to validate the XML file against the DDMS schema
before building the object model. Objects are always validated upon instantiation, so it is impossible to have fully-formed, but invalid DDMS components
at any given time.</p>

<p>Validation from an XML file proceeds in the following order:</p>

<ul>
	<li>The XML file is initially validated by the underlying XML parsers to confirm that it is well-formed and adheres to the DDMS schema.</li>
	<li>As the objects are built in Java, the schema rules are revalidated in Java. This is not essential for file-based metacards, but
	becomes more important when we are building from scratch.</li>
	<li>Next, any rules mandated in the DDMS Specification but not implemented in the schema are validated (such as constraints on latitude/longitude values). This is done with
	custom Java code, and you can create your own rules with <a href="documentation-schematron.jsp">ISO Schematron</a>.</li>
	<li>Any warnings which do not actually result in an invalid component are stored on the component, and can be retrieved via <code>getValidationWarnings()</code>.</li>
</ul>

<p>The <u>Essentials</u> program can open metacards of any supported DDMS Version. An example is provided in the file, <code>2.0-earlierVersionExample.xml</code>. Working with
a other versions of DDMS is covered in the <a href="documentation.jsp#tips">Power Tips</a> section.</p>

<h3>Conclusion</h3>

<p>In this tutorial, you have seen how DDMS Resources can be built from an existing XML file and transformed into various outputs. You
have also had a small taste of the validation that occurs when a Resource is built.</p>

<p>The next tutorial, covering the <u>Escort</u> application, will show how a DDMS Resource can be constructed from scratch.</p>

<pre class="brush: xml">&lt;ddms:identifier
   ddms:qualifier="http://metadata.dod.mil/mdr/ns/MDR/0.1/MDR.owl#URI"
   ddms:value="http://www.whitehouse.gov/news/releases/2005/06/20050621.html" /&gt;</pre>
<p class="figure">Figure 5. A DDMS Identifier element in XML</p>
   
<pre class="brush: java">Identifier identifier1 = new Identifier("http://metadata.dod.mil/mdr/ns/MDR/0.1/MDR.owl#URI",
   "http://www.whitehouse.gov/news/releases/2005/06/20050621.html");
</pre>
<p class="figure">Figure 6. Building a DDMS Identifier from scratch</p>   

<p>
	Tutorial #1: Essentials <span class="notify">(you are here)</span><br />
	<a href="tutorials-02.jsp">Tutorial #2: Escort</a><br />
	<a href="tutorials-03.jsp">Tutorial #3: Escape</a><br />
	<a href="documentation.jsp#samples">Back to Samples Documentation</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>