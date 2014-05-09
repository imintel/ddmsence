<html>
<head>
	<title>DDMSence: Tutorial #2 (Escort)</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<h1>Tutorial #2: <u>Escort</u></h1>

<p>
<img src="./images/escort-flow.png" width="400" height="156" title="Escort Workflow" align="right" />
<u>Escort</u> is a step-by-step wizard for building a simple DDMS Resource from scratch, and then saving it to a file. The source code for this application
shows an example of how the Java object model can be built with basic data types (possibly mapped from a database table or some other pre-existing entity).</p>

<p>I have implemented this wizard as a series of textual prompts, to avoid the overhead of having a full-fledged MVC Swing application (or implementing it as a web application and
requiring a server to run). It is not as flashy, but this should make it easier to focus on the important sections of the source code.</p>

<p>This wizard only focuses on small representative subset of the DDMS components, such as Identifier and SubjectCoverage. It will not ask you for every single optional component
in the specification.</p>

<h2>Getting Started</h2>

<p><u>Escort</u> can be run from the command line with the class, <code>buri.ddmsence.samples.Escort</code>. The application does not accept any
command line parameters.</p>

<p>Please see the "<a href="documentation.jsp#started">Getting Started</a>" section for classpath details and command line syntax.</p> 

<h3>Walkthrough</h3>

<p>When you run <u>Escort</u> you will see a simple text screen:</p>

<pre class="brush: xml">Escort: a DDMSence Sample

This program allows you to build a DDMS 5.0 assertion from scratch using a
representative subset of possible components. Suggested valid answers are
provided in square brackets for each prompt. However, these are not default
values (hitting Enter will answer the prompt with an empty string).

Would you like to save time by using dummy security attributes, Unclassified/USA, throughout the assertion? [Y/N]:</pre>
<p class="figure">Figure 1. Starting the Wizard</p>

<p>The wizard will walk you through various components of a DDMS Resource in the order that they are found in the schema. 
Each component you create must be valid before you can proceed to the next one. You can optionally use dummy values
for the security classification and ownerProducer attributes to save time while walking through the wizard.</p>

<p>First, let's try creating an invalid Identifier. The DDMS specification states that the qualifier must be a valid URI. Type in "<code>:::::</code>" as
a qualifier and "<code>test</code>" as a value.</p>

<pre class="brush: xml">=== ddms:metacardInfo (exactly 1 required) ===
A minimal metacardInfo consist of an identifier, dates, and a publisher.
Please enter the qualifier [testQualifier]: :::::
Please enter the value [testValue]: test
[ERROR] /ddms:identifier: Invalid URI (Expected scheme name at index 0: :::::)
Please enter the qualifier [testQualifier]:</pre>
<p class="figure">Figure 2. Outsmarting the Wizard</p>

<p>Because the qualifier was an invalid URI, the wizard printed out the error message and then restarted the loop to get your input values. The value, 
"<code>/ddms:identifier</code>" tells you which component was causing a problem and can be retrieved via <code>getLocator()</code> on the <code>InvalidDDMSException</code>. The format
of the locator will be an XPath string, but should be enough to help you locate the offending component even if you have no XPath experience. (Skilled XPath
developers will notice that <code>ddms:identifier</code> seems to be the root node in the string -- this is because it had not been added to the parent Resource at the time
of the exception).</p>

<p>Now, let's take a look at the source code in <code>/src/samples/buri/ddmsence/samples/Escort.java</code> to see how this was accomplished. The <code>run()</code>
method defines the control flow for the program, and uses the helper method, <code>inputLoop()</code>, to ask for user input until a valid component can be created:</p>

<pre class="brush: java">printHead("ddms:metacardInfo (exactly 1 required)");
getTopLevelComponents().add(inputLoop(MetacardInfo.class));</pre>
<p class="figure">Figure 3. Excerpt from the run() method</p>
		
<pre class="brush: java">private IDDMSComponent inputLoop(Class theClass) throws IOException {
   IDDMSComponent component = null;
   while (component == null) {
      try {
         component = CONSTRUCTOR_BUILDERS.get(theClass).build();
      }
      catch (InvalidDDMSException e) {
         printError(e);
      }
   }
   return (component);
}</pre>
<p class="figure">Figure 4. Source code to continuously loop until a valid component is created</p>

<p>IDDMSComponent is the identifying interface for any DDMS components implemented as Java objects, and contains methods which are common to all components.
 An anonymous implementation of IConstructorBuilder is created (in the <u>Escort</u> constructor) for each DDMS Component class, and each Builder
is responsible for one top-level component. For example, here is the definition of the Builder that builds Identifiers:</p>

<pre class="brush: java">CONSTRUCTOR_BUILDERS.put(Identifier.class, new IConstructorBuilder() {
   public IDDMSComponent build() throws IOException, InvalidDDMSException {
      String qualifier = readString("the qualifier [testQualifier]");
      String value = readString("the value [testValue]");
      return (new Identifier(qualifier, value));
   }		
});</pre>
<p class="figure">Figure 5. An anonymous builder that can build Identifiers from user input</p>

<p>As soon as <code>inputLoop()</code> receives a valid component from a Builder, it returns that component to the main wizard method, <code>run()</code>. The main
wizard saves the component in a list and moves on to the next component type. You can examine the constructor to see the code needed to build each type of
component. Some Builders, like the one for a MetacardInfo will use other Builders to create child components.</p>

<p>Let's use the wizard to create a valid Resource. You should be able to follow the prompts to the end, but if not, the output below is one possible
road to a valid Resource.</p>

<pre class="brush: xml">Would you like to save time by using dummy security attributes, Unclassified/USA, throughout the assertion? [Y/N]: y

=== ddms:metacardInfo (exactly 1 required) ===
A minimal metacardInfo consist of an identifier, dates, and a publisher.
Please enter the qualifier [testQualifier]: testQualifier
Please enter the value [testValue]: testValue
Please enter the created date [2010]: 2010
Please enter the posted date [2010]: 
Please enter the validTil date [2010]: 
Please enter the infoCutOff date [2010]: 
Please enter the approvedOn date [2010]: 
Please enter the receivedOn date [2010]: 
Please enter the publisher entity type [organization]: organization
Please enter the number of names this organization has [1]: 1
Please enter the number of phone numbers this organization has [0]: 0
Please enter the number of email addresses this organization has [0]: 0
Please enter the number of suborganizations to include [0]: 0
Please enter entity name #1 [testName1]: Defense Information Systems Agency
Please enter the Organization acronym [testAcronym]: DISA

=== ddms:identifier (at least 1 required) ===
Please enter the qualifier [testQualifier]: testQualifier
Please enter the value [testValue]: testValue

=== ddms:title (at least 1 required) ===
Please enter the title text [testTitle]: testTitle

=== ddms:description (only 1 allowed) ===
Include this component? [Y/N]: n

=== ddms:dates (only 1 allowed) ===
Include this component? [Y/N]: n

=== Producers: creator, publisher, contributor, and pointOfContact (at least 1 required) ===
Please enter the producer type [creator]: creator
Please enter the entity type [organization]: organization
Please enter the pocType [DoD-Dist-B]: 
Please enter the number of names this organization has [1]: 1
Please enter the number of phone numbers this organization has [0]: 0
Please enter the number of email addresses this organization has [0]: 0
Please enter the number of suborganizations to include [0]: 0
Please enter entity name #1 [testName1]: Defense Information Systems Agency
Please enter the Organization acronym [testAcronym]: DISA

=== ddms:subjectCoverage (at least 1 required) ===
Please enter the number of keywords to include [1]: 1
Please enter the number of categories to include [0]: 0
Please enter the number of productionMetrics to include [0]: 0
Please enter the number of nonStateActors to include [0]: 0
* Keyword #1
Please enter the keyword value [testValue]: ddmsence

=== ddms:resource Attributes (all required) ===
Please enter the Resource compliesWith [DDMSRules]: DDMSRules
The DDMS Resource is valid!
No warnings were recorded.</pre>
<p class="figure">Figure 6. Successful run of the Escort Wizard</p>

<p>DDMSence stores warning messages on each component for conditions that aren't necessarily invalid. Calling <code>getValidationWarnings()</code> on any component will return
the messages of that component and any subcomponents. In this run-through, no warnings were recorded. We will try an example with warnings later.</p>

<p>The final step is to save your valid Resource as an XML file. Enter a filename, and the Resource will be saved in the <code>data/sample/</code> directory.</p>

<pre class="brush: xml">=== Saving the Resource ===
Would you like to save this file? [Y/N]: y
This Resource will be saved as XML in the data/sample/ directory.
Please enter a filename: test.xml
File saved at "C:\projects\ddmsence\data\sample\test.xml".

You can now open your saved file with the Essentials application.
The Escort wizard is now finished.</pre>
<p class="figure">Figure 7. Saving the File</p>

<p>Once the file is saved, you can open it with the <u>Essentials</u> application to view the Resource in different formats. You can also use the wizard
to generate additional data files for the <u>Escape</U> application.</p>

<p>If you were to run <u>Escort</u> again, and then create an empty <code>ddms:dates</code> component, you would see a warning message when the Resource
was generated:</p>

<pre class="brush: xml">=== ddms:dates (only 1 allowed) ===
Include this component? [Y/N]: y
Please enter the created date [2010]: 
Please enter the posted date [2010]: 
Please enter the validTil date [2010]: 
Please enter the infoCutOff date [2010]:
Please enter the approvedOn date [2010]:

[...]

The DDMS Resource is valid!
The following warnings were recorded:
   [WARNING] /ddms:resource/ddms:dates: A completely empty ddms:dates element was found.</pre>
<p class="figure">Figure 8. Triggering a Warning Condition</p>

<p>As you can see, the locator information on warnings is in the same format as the information on errors. Because parent components claim the warnings of their children,
a more detailed locator can be created. In this case, calling <code>getValidationWarnings()</code> on the Resource shows the full path of "<code>/ddms:resource/ddms:dates</code>". If 
you called <code>getValidationWarnings()</code> on the Dates component itself, the locator would be "<code>/ddms:dates</code>".</p>

<p>If a parent-child hierarchy has some DDMS elements which are not <a href="documentation.jsp#design">implemented as Java objects</a>, the locator string will
include every element in the hierarchy. For example, a warning in a <code>ddms:medium</code> element (in DDMS 2.0, 3.0, or 3.1) will have a locator value of "<code>/ddms:resource/ddms:format/ddms:Media/ddms:medium</code>"
even though <code>ddms:Media</code> is not an implemented component.</p>

<p>Validation for a Resource built from scratch proceeds in the following order:</p>

<ul>
	<li>As the objects are built in Java, the schema rules are validated in Java.</li>
	<li>Next, any rules mandated in the DDMS Specification but not implemented in the schema are validated (such as constraints on latitude/longitude values). This is done with
	custom Java code, and you can create your own rules with <a href="documentation-schematron.jsp">ISO Schematron</a>.</li>
	<li>Next, the Resource is converted into XML and validated against the DDMS schemas.</li>
	<li>Any warnings which do not actually result in an invalid component are stored on the component, and can be retrieved via <code>getValidationWarnings()</code>.</li>
</ul>

<h3>Conclusion</h3>

<p>In this tutorial, you have seen how DDMS Resources can be built from scratch. You have also seen further examples of component validation. In practice, the data-driven
constructor approach for building components is somewhat inflexible. The Component Builder framework offers more flexibility in the building process, and can also
be used to edit existing DDMS Resources. More about this framework can be found in the Power Tip on <a href="documentation-builders.jsp">Using Component Builders</a>.</p>

<p>The next tutorial, covering the Escape application, will show how a DDMS Resource can be traversed and used in other contexts.</p>

<p>
	<a href="tutorials-01.jsp">Tutorial #1: Essentials</a><br />
	Tutorial #2: Escort <span class="notify">(you are here)</span><br />
	<a href="tutorials-03.jsp">Tutorial #3: Escape</a><br />
	<a href="documentation.jsp#samples">Back to Samples Documentation</a>
</p>

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>