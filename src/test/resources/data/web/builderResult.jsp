<html>
<head>
	<%@page session="true"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<title>DDMSence: DDMS Builder Result</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="The open-source Java library for the DoD Discovery Metadata Specification (DDMS)">
</head>
<body>
<%@ include file="../shared/header.jspf" %>

<h1>DDMS Builder Results</h1>

<c:forEach items="${warnings}" var="warning" varStatus="rowInfo">
	<li><b>${warning.type}</b> at <code><c:out value="${warning.locator}" /></code>: <c:out value="${warning.text}" /></li>
</c:forEach>

<c:if test="${not empty xml}">
	<pre class="brush: xml">
<c:out value="${xml}" escapeXml="true" />
	</pre>
</c:if>	

<div class="clear"></div>
<%@ include file="../shared/footer.jspf" %>
</body>
</html>