<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>InsertItem</title>
<style type="text/css">
.error {
	color: red;
	font-weight: bold;
}

.success {
	color: blue;
	font-weight: bold;
}

.success_title {
	color: darkblue;
	font-style: italic;
	font-weight: bold;
}

tr.label {
	text-align: right;
}
</style>
</head>

<body>
	<c:if test="${! empty errorMessage}">
		<div class="error">${errorMessage}</div>
	</c:if>

	<c:if test="${! empty successMessage}">
		<div class="success">
			Successfully added <span class="success_title">${successMessage}</span>
			to Library
		</div>
	</c:if>

	<form action="${pageContext.servletContext.contextPath}/insertItem"
		method="post">
		<div class="label">Add Item:</div>
		<div>
			<input type="text" name="Add" value="${newItem}" />
		</div>

		<div class="label">Quantity:</div>
		<div>
			<input type="text" name="quantityA" value="${amount}" />
		</div>
		<form id="Add" name="form1" method="POST" action="add">
			<input type="submit" name="submit" value="Add">
		</form>

		<div class="label">Remove Item:</div>
		<div>
			<input type="text" name="Remove" value="${remItem}" />
		</div>

		<div class="label">Quantity:</div>
		<div>
			<input type="text" name="quantity" value="${amount}" />
		</div>
		<form id="Remove" name="form1" method="POST" action="remove">
			<input type="submit" name="submit" value="Remove">
		</form>
		
		<form id="Submit" name="form1" method="POST" action="submit">
			<input type="submit" name="submit" value="Submit">
		</form>

		<input type="Submit" name="submitinsertitem"
			value="Add Item to Library">
	</form>
	<br>
	<form action="${pageContext.servletContext.contextPath}/index"
		method="post">
		<input type="Submit" name="submithome" value="Home">
	</form>
</body>
</html>