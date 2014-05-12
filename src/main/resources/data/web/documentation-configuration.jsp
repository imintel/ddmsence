<html>
<head>
	<title>DDMSence: Power Tip - Configurable Properties</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Configurable Properties</h1>

<p>DDMSence exposes some properties, such as the namespace prefixes used for each XML namespace, so they can be configured by the end user. For example, if you are 
building components from scratch, and you wish to use "ic" as a namespace prefix for the Intelligence Community namespace
instead of "ISM", you would set the "ism.prefix" property with a custom value of <code>ic</code>.</p>

<pre class="brush: java">PropertyReader.setProperty("ism.prefix", "ic");</pre>
<p class="figure">Figure 1. Command to change a configurable property.</p>

<p>Only the subset of properties listed below can be set programmatically. Attempts to change other DDMSence properties will result in an exception.</p>
			
<table>
<tr><th>Property Name</th><th>Description</th><th>Default Value</th></tr>
<tr><td>ddms.prefix</td><td>Default DDMS prefix used when generating components from scratch</td><td><code>ddms</code></td></tr>
<tr><td>gml.prefix</td><td>Default GML prefix used when generating components from scratch</td><td><code>gml</code></td></tr>
<tr><td>ism.prefix</td><td>Default ISM prefix used when generating components from scratch</td><td><code>ISM</code></td></tr>
<tr><td>ntk.prefix</td><td>Default NTK prefix used when generating components from scratch</td><td><code>ntk</code></td></tr>
<tr><td>output.indexLevel</td><td>Controls the placement of 1-based indices on the HTML/Text output of fields which are allowed to have multiples (0 = Never use, 1 = Use unless exactly 1 item exists, 2 = Always use)</td><td><code>0</code></td></tr>
<tr><td>sample.data</td><td>Default data directory used by sample applications</td><td><code>data/sample/</code></td></tr>
<tr><td>tspi.prefix</td><td>Default TSPI prefix used when generating components from scratch</td><td><code>tspi</code></td></tr>
<tr><td>virt.prefix</td><td>Default VIRT prefix used when generating components from scratch</td><td><code>virt</code></td></tr>
<tr><td>xlink.prefix</td><td>Default XLink prefix used when generating components from scratch</td><td><code>xlink</code></td></tr>
<tr><td>xml.transform.TransformerFactory</td><td>XSLT Engine class name, for Schematron validation<td><code>net.sf.saxon.TransformerFactoryImpl</code></td></tr>
</table>
<p class="figure">Table 1. Configurable Properties</p>

<h2>Controlling HTML/Text Output</h2>

<p>The DDMS specification has no convention for the HTML/Text output of fields which might have multiple values, such as the name and phone number fields on a person. DDMSence optionally adds
indices to the names of these values, based on the value of the <code>output.indexLevel</code> property. The example below shows a sample XML instance, and the expected output at each
indexLevel:</p>

<pre class="brush: xml">
&lt;ddms:creator&gt;
   &lt;ddms:person&gt;
      &lt;ddms:name&gt;Brian&lt;/ddms:name&gt;
      &lt;ddms:name&gt;BU&lt;/ddms:name&gt;
      &lt;ddms:surname&gt;Uri&lt;/ddms:surname&gt;
      &lt;ddms:phone&gt;703-885-1000&lt;/ddms:phone&gt;
   &lt;/ddms:person&gt;
&lt;/ddms:creator&gt;</pre>
<p class="figure">Figure 2. A sample XML instance representing a creator with 2 names and 1 phone number.</p>

<pre class="brush: xml">creator.name: Brian
creator.name: BU
creator.surname: Uri
creator.phone: 703-885-1000</pre> 
<p class="figure">Figure 3. The Text output of this creator when output.indexLevel is 0</p>

<pre class="brush: xml">creator.name[1]: Brian
creator.name[2]: BU
creator.surname: Uri
creator.phone: 703-885-1000</pre> 
<p class="figure">Figure 3. The Text output of this creator when output.indexLevel is 1</p>

<pre class="brush: xml">creator.name[1]: Brian
creator.name[2]: BU
creator.surname: Uri
creator.phone[1]: 703-885-1000</pre> 
<p class="figure">Figure 3. The Text output of this creator when output.indexLevel is 2</p>

<p>Indices are off by default, since this is not a part of the official specification.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>