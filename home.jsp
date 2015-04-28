<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
    <title>Home</title>
</head>
<body>
<h1>Hello world! This is a JSP.</h1>

<P>The time on the server is ${serverTime}.</P>

<p>Here are some items:</p>
<ul>
    <c:forEach var="item" items="${someItems}">
        <li>${item}</li>
    </c:forEach>
</ul>

<p>${person.name}</p>
<p>${person.age}</p>

<p><a href="static.txt">A static file.</a></p>

</body>
</html>
