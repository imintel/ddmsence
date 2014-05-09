<html>
<head>
	<title>DDMSence: Power Tip - Using Alternate Versions of Intelligence Community Specifications</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<a name="top"></a><h1>Power Tip: Using Alternate Versions of Intelligence Community Specifications</h1>

<p>DDMSence comes bundled with the Public Release versions of the IC Information Security Marking (ISM), Need-to-Know Metadata (NTK), and Virtual (VIRT) schemas, 
which allows them to be distributed without caveat on the public Internet. For many developers, this should be sufficient. However, some developers may need to make 
use of values from higher classification versions of the specifications (such as the FOUO version). The instructions below show you how to replace the bundled files 
from one of the supported versions of DDMS with your own copies.</p> 

<h3>Information Security Marking (ISM)</h3>

<h4>Restrictions</h4>
<img src="./images/ismDirectory.png" width="344" height="327" title="ISM Directory Structure" align="right" border="1"/>
<ol>
<li>These instructions assume that you are updating from a Public Release to a more restricted release, and are NOT changing ISM versions altogether. Please 
remember that each DDMS version identifies one specific supported version of ISM. For example, in DDMS 4.1, replacing ISM.XML V9 Public Release 
with ISM.XML V9 FOUO is supported, but replacing it with ISM.XML V4 FOUO may have unexpected side effects or simply fail to work.</li>
<li>Out of the box, DDMSence only contains files cleared for Public Release. Using restricted files with DDMSence does not absolve you of your responsibility for
obeying all handling and dissemination instructions on those files. Do not proceed unless you are comfortable with this responsibility.</li>
</ol>

<h4>What You Need</h4>

<ol>
<li>The top-level ISM schema files, <code>IC-ISM.xsd</code> and <code>CVEGeneratedTypes.xsd</code> (the latter schema only exists in earlier versions). 
These files might be found in your ISM archive at <code>ISM9/Schema/ISM/</code>.</li>
<li>The supporting generated CVE schemas. These files might be found at <code>ISM9/Schema/ISM/CVEGenerated/</code>.</li>
<li>The raw vocabulary files used to create the schemas. These files might be found at <code>ISM9/CVE/ISM/</code>.</li>
</ol>
 
<h4>Instructions</h4>

<ol>
<li>First, decide on a classpath-accessible directory where your files will be stored.</li>
<li>DDMSence will expect ISM schema files to be available at <code>schemas/&lt;ddmsVersionNumber&gt;/ISM/</code>, relative to the library. 
In your custom directory, set up the appropriate <code>schemas/&lt;ddmsVersionNumber&gt;/ISM/</code> directory structure, according to the diagram
on the upper right. It should contain the top-level ISM schemas, a subdirectory (<code>CVEGenerated</code>) for the generated schemas,
and a subdirectory (<code>CVE</code>) for the raw vocabulary files.</li>
<li>Next, you will need to edit the classpath of your JVM to ensure that this directory structure has a higher priority than the DDMSence JAR file by
loading it before the JAR file.</li>
<li>When you run your program, DDMSence will search your custom directory first, and only turn to the bundled files in the DDMSence JAR 
if it cannot find the custom directory.</li>
<li>To confirm your success, try creating a DDMS XML instance that uses vocabulary values not found in the bundled ISM files. Normally if you tried to open this
instance with the <u>Essentials</u> sample program, it would complain about the vocabulary value. If you have followed these instructions properly, the
instance should load correctly.</li>
</ol>

<h4>Example</h4>

<ol>
<li>This example shows how you would swap ISM files for DDMS 5.0.</li>
<li>Let's use the directory, <code>C:\tomcat\shared\classes</code>, as the location for our schemas.</li>
<li>After following the instructions for creating the directory structure, you should now have a file at 
<code>C:\tomcat\shared\classes\schemas\5.0\ISM\CVE\CVEnumISMClassificationAll.xml</code>.</li>
<li>The commands below show how you would add this custom directory into your classpath. It assumes that your classpath was original stored 
in an the environment variable (<code>DDMSENCE_CLASSPATH</code>), and places the custom directory first. Afterwards, it runs the <u>Essentials</u>
sample program.</li>

<pre class="brush: xml">set DDMSENCE_CLASSPATH=C:\tomcat\shared\classes;%DDMSENCE_CLASSPATH%
java -cp %DDMSENCE_CLASSPATH% buri.ddmsence.samples.Essentials</pre>
<p class="figure">Figure 1. Putting your custom directory at the beginning of your classpath, in Windows/DOS</p>

</ol>

<h3>Need-to-Know Metadata (NTK)</h3>

<p>The same instructions can be used to swap NTK versions. There is only a single NTK schema file, <code>IC-NTK.xsd</code> which DDMSence expects to find at
<code>schemas\&lt;ddmsVersionNumber&gt;/NTK/</code>.</p>

<h3>Virtual Coverage (VIRT)</h3>

<p>The same instructions can be used to swap VIRT versions. You will need two files (DDMSence does not make use of the CVE file for VIRT):</p>
<ul>
<li><code>VIRT.xsd</code></li>
<li><code>CVEGenerated/CVEnumVIRTNetworkName.xsd</code></li>
</ul>
<p>DDMSence expects to find these files at <code>schemas\&lt;ddmsVersionNumber&gt;/VIRT/</code>.</p>

<p>
	<a href="#top">Back to Top</a><br>
	<a href="documentation.jsp#tips">Back to Power Tips</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>