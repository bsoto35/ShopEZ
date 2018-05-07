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
			SHOP-EZE <a href="https://imgur.com/wtOuyHi"> <img
				src="https://i.imgur.com/wtOuyHi.png" title="source: imgur.com"
				style="width: 50px; height: 50px;" /></a>
		</div>
		<ul id="nav">
			<li><a href="${pageContext.servletContext.contextPath}/welcome">Login</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/help">Help</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/about">About</a></li>
		</ul>
	</div>
	
	<div id=content>
		<a href="https://imgur.com/eLuk04R"><img src="https://i.imgur.com/eLuk04R.jpg" title="source: imgur.com" style="width: 600px; height: 400px; float:left;" /></a>
		<a href="https://imgur.com/LaLvWZX"><img src="https://i.imgur.com/LaLvWZX.jpg" title="source: imgur.com"  style="width: 600px; height: 400px; float:right;"/></a>
	</div>
</body>
</html>