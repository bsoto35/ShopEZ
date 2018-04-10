<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
	<head>
		<title>CS320 Add Book</title>
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
			<div class="success">Successfully added <span class="success_title">${successMessage}</span> to Library</div>
		</c:if>
			
		<form action="${pageContext.servletContext.contextPath}/insertItem" method="post">
			<table>
				<tr>
					<td class="label">Item Name:</td>
					<td><input type="text" name="item_name" size="20" value="${item_name}" /></td>
				</tr>
				<tr>
					<td class="label">Item Price:</td>
					<td><input type="text" name="item_price" size="20" value="${item_price}" /></td>
				</tr>
				<tr>
					<td class="label">Item Location X:</td>
					<td><input type="text" name="item_locationX" size="50" value="${item_locationX}" /></td>
				</tr>
				<tr>
					<td class="label">Item Location Y:</td>
					<td><input type="text" name="item_locationY" size="20" value="${item_locationY}" /></td>
				</tr>
				<tr>
					<td class="label">Quantity:</td>
					<td><input type="text" name="item_quantity" size="20" value="${item_quantity}" /></td>
				</tr>							
			</table>
			
			<input type="Submit" name="submitinsertitem" value="Add Item to Library">
		</form>
		<br>
		<form action="${pageContext.servletContext.contextPath}/index" method="post">
			<input type="Submit" name="submithome" value="Home">
		</form>		
	</body>
</html>