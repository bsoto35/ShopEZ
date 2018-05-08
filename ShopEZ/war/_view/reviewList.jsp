<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>Review</title>
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
			<li><a href="${pageContext.servletContext.contextPath}/welcome">Home</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/help">Help</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/about">About</a></li>
		</ul>
	</div>
	<div id=content2>
		<div id=itemList>
			<c:forEach items="${list}" var="item" varStatus="iter">
				<div id="items">${item.itemName}-${item.itemPrice}</div>
				<form name="form1" method="Post"
					action="${pageContext.servletContext.contextPath}/review">
					<input class="button" type="submit" name="${item.itemID}"
						value="Remove">
				</form>
			</c:forEach>
		</div>
		
		<form name="form1" method="GET"
			action="${pageContext.servletContext.contextPath}/route">
			<input class="button" type="submit" name="create"
				value="Create Route">
		</form>
	</div>
</body>
</html>