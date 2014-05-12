<?xml version="1.0" encoding="utf-8"?>
<iso:schema   
	xmlns="http://purl.oclc.org/dsdl/schematron"
	xmlns:iso="http://purl.oclc.org/dsdl/schematron">
	
	<iso:title>Test ISO Schematron File for DDMSence (DDMS 5.0)</iso:title>
	<iso:ns prefix='ddms' uri='urn:us:mil:ces:metadata:ddms:5' />
	<iso:ns prefix='ISM' uri='urn:us:gov:ic:ism' />
	<iso:ns prefix='gml' uri='http://www.opengis.net/gml/3.2' />
	<iso:ns prefix='xlink' uri='http://www.w3.org/1999/xlink' />
	
	<iso:pattern title="Sample Failure">
		<iso:rule context="ddms:resource">
			<iso:assert test="unknownElement">A DDMS Resource must have an unknownElement child. This will always fail.</iso:assert>
		</iso:rule>
	</iso:pattern>

	<iso:pattern title="Fixed Surname Value">
		<iso:rule context="//ddms:publisher/ddms:person/ddms:surname">
			<iso:report test="normalize-space(.) = 'Uri'">Members of the Uri family cannot be publishers.</iso:report>
		</iso:rule>
	</iso:pattern>

</iso:schema>
