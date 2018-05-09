<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>InsertItem</title>
<link rel="stylesheet" href="webresources/style.css" />
<script>
	function addGroceryItems() {
		var itemList = document.getElementById("itemList");
		document.getElementById("add").value = itemList.options[itemList.selectedIndex].text;
	}
	function removeGroceryItems() {
		var removeList = document.getElementById("removeList");
		document.getElementById("remove").value = removeList.options[removeList.selectedIndex].text;
	}
</script>

</head>
<body>
	<div id="header">
		<div id="logo">
				<a href="https://imgur.com/6aJkmKH"><img
					src="https://i.imgur.com/6aJkmKH.png" title="source: imgur.com"
					style="width: 400px; height: 70px; padding-top: 10px;" /></a>
			</div>
		<ul id="nav">
			<li><a href="${pageContext.servletContext.contextPath}/login">Login</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/help">Help</a></li>
			<li><a href="${pageContext.servletContext.contextPath}/about">About</a></li>
		</ul>
	</div>
	<div id="content3">
		<form action="${pageContext.servletContext.contextPath}/insertItem"
			method="post">
			<div class="addItem">
				<div class="item">Item to Add:</div>
				<select id="itemList" onChange="addGroceryItems()">
					<c:forEach items="${entireList}" var="item" varStatus="iter">
						<option>${item.itemName}</option>
					</c:forEach>
				</select>


				<div>
					<input id="add" type="text" name="itemA" value="${app.ItemName}" />
				</div>

				<div class="qty">Quantity:</div>
				<div>
					<input id="qty" type="text" name="quantityA"
						value="${app.quantity}" />
				</div>
				<input class="button" type="submit" name="add" value="Add">
			</div>
			<div class="removeItem">
				<div class="item">Remove Item:</div>
				<select id="removeList" onChange="removeGroceryItems()">
					<c:forEach items="${list}" var="item" varStatus="iter">
						<option>${item.itemName}</option>
					</c:forEach>
				</select>
				<div>
					<input id="remove" type="text" name="itemR" value="${app.ItemName}" />
				</div>

				<div class="qty">Quantity:</div>
				<div>
					<input id="qty" type="text" name="quantityR"
						value="${app.Quantity}" />
				</div>

				<input class="button" type="submit" name="rem" value="Remove">

			</div>
			<c:if test="${! empty errorMessage}">
				<div class="error">${errorMessage}</div>
			</c:if>
			<c:if test="${! empty successMessage}">
				<div class="success">${successMessage}</div>
			</c:if>
		</form>

		<form name="form1" method="GET"
			action="${pageContext.servletContext.contextPath}/review">
			<input class="button" type="submit" name="submit" value="Submit List">
		</form>


		<div id=groceryList>
			<c:forEach items="${list}" var="item" varStatus="iter">
				<div id="items">${item.itemName}-${item.itemPrice}</div>
			</c:forEach>
		</div>

	</div>

</body>