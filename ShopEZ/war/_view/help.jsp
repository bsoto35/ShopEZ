<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>Help</title>
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
	<div id="content2">
		<div id="existing">
			<form name="form1" method="POST"
				action="${pageContext.servletContext.contextPath}/login">
				<h3>Existing User</h3>
				<div class="label">Username:</div>
				<div>
					<input type="text" name="inUsername" value="${app.username}" />
				</div>

				<div class="label">Password:</div>
				<div>
					<input type="password" name="inPassword" value="${app.password}" />
				</div>

				<input type="submit" name="SignIn" value="Sign In">
			</form>

			<form name="form2" method="POST"
				action="${pageContext.servletContext.contextPath}/login">
				<input type="submit" name="forgot" value="Forgot Password">
			</form>

		</div>

		<div id="create">
			<form id="new" name="form1" method="POST"
				action="${pageContext.servletContext.contextPath}/login">
				
				<h3>Create an Account</h3>

				<div class="label">Username:</div>
				<div>
					<input type="text" name="upUsername" value="${app.username}" />
				</div>

				<div class="label">Password:</div>
				<div>
					<input type="password" name="upPassword" value="${app.password}" />
				</div>

				<div class="label">Confirm Password:</div>
				<div>
					<input type="password" name="ConfirmPassword"
						value="${confirm_password}" />
				</div>
				<input type="submit" name="SignUp" value="Sign Up ">
				<c:if test="${! empty errorMessage}">
					<div class="error">${errorMessage}</div>
				</c:if>
				<c:if test="${! empty successMessage}">
					<div class="error">${successMessage}</div>
				</c:if>
			</form>
		</div>
	</div>
	<div id="footer">
				</div>


</body>
</html>
