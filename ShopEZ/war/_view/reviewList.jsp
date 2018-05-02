<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>Login</title>
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
	<div id=content4>
	<form action="${pageContext.servletContext.contextPath}/guessingGame"
		method="post">
		<c:if test="${empty game}">
			<input name="startGame" type="submit" value="Start game" />
		</c:if>
		<c:if test="${! empty game}">
			<c:if test="${game.done}">
				<div>The number you are thinking of is ${game.guess}</div>
				<div>
					<input name="startGame" type="submit" value="Play again" />
				</div>
			</c:if>
			<c:if test="${!game.done}">
				<div>Are you thinking of ${game.guess}?</div>
				<div>
					<input name="gotIt" type="submit" value="Yes, that's it!" /> <input
						name="less" type="submit" value="No, that's too big" /> <input
						name="more" type="submit" value="No, that's too small" /> <input
						name="min" type="hidden" value="${game.min}" /> <input name="max"
						type="hidden" value="${game.max}" />
				</div>
			</c:if>
		</c:if>
	</form>
	</div>
</body>
</html>