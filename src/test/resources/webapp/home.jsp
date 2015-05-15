<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <meta charset='utf-8' >
  <title>Home</title>
</head>
<body>
<h1>Hello world! This is a JSP. 了就好了</h1>

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
