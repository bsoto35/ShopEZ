<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>InsertItem</title>
<link rel="stylesheet" href="webresources/style.css" />
</head>
<body>
	<div id="header">
		<div id="logo">
			SHOP-EZE <a href="https://imgur.com/wtOuyHi"> <img
				src="https://i.imgur.com/wtOuyHi.png" title="source: imgur.com"
				style="width: 50px; height: 50px;" /></a>
		</div>
		<div id="nav">
			<button class="button" onclick="myFunction()">Login</button>
			<button class="button" onclick="myFunction()">About</button>
			<button class="button" onclick="myFunction()">Help</button>
		</div>
	</div>
	<div id="content3">
		<c:if test="${! empty errorMessage}">
			<div class="error">${errorMessage}</div>
		</c:if>
		<c:if test="${! empty successMessage}">
			<div class="error">${successMessage}</div>
		</c:if>

		<form action="${pageContext.servletContext.contextPath}/insertItem"
			method="post">
			<div class="addItem">
				<div class="item">Item to Add:</div>
				<div>
					<input id="add" type="text" name="itemA" value="${app.ItemName}" />
				</div>

				<div class="qty">Quantity:</div>
				<div>
					<input id="qty" type="text" name="quantityA" value="${amount}" />
				</div>
				<input class= "button" type="submit" name="add" value="Add">
			</div>
			<div class="removeItem">
				<div class="item">Remove Item:</div>
				<div>
					<input id="remove" type="text" name="itemR" value="${app.ItemName}" />
				</div>

				<div class="qty">Quantity:</div>
				<div>
					<input id="qty" type="text" name="quantityR" value="${amount}" />
				</div>
				<input class= "button" type="submit" name="rem" value="Remove">
			</div>
		</form>

	</div>
</body>