<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>Route</title>
<link rel="stylesheet" href="webresources/style.css" />
</head>

<body>
	<div id="header">
		<div id="logo">
			<a href="https://imgur.com/6aJkmKH"><img
				src="https://i.imgur.com/6aJkmKH.png" title="source: imgur.com"
				style="width: 400px; height: 70px; padding-top: 10px;" /></a>
		</div>
		<ul id="nav">
			<li><a href="${pageContext.servletContext.contextPath}/welcome">Sign
					Out</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/help">Help</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/about">About</a></li>
		</ul>
	</div>

	<div id=content>
		<a href="https://imgur.com/eLuk04R"><img
			src="https://i.imgur.com/eLuk04R.jpg" title="source: imgur.com"
			style="width: 720px; height: 540px; float: center;" /></a>

	</div>
</body>
</html>