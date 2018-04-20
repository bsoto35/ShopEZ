<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>Login</title>
<style>
body {
	background-image: url("https://i.imgur.com/tnhC0DF.jpg");
	background-repeat: no-repeat;
	background-size: cover;
}

#header {
	background-color: white;
	border: 1px solid black;
	padding: 10px 30px;
	background-color: white;
	font-style: italic;
}

#logo {
	color: black;
	font-size: 70px;
	text-align: left;
}

#nav {
	text-align: right;
}

.button {
	float: right;
}

#content {
	float: top;
	text-align: center;
	font-size: 40px;
	color: black;
	width: 500;
	margin-left: auto;
	margin-right: auto;
	padding: 50px 200px;
}

#existing {
	width: 300px;
	height: 400px;
	background-color: lightblue;
	border-style: outset;
	border-width: 5px;
	float: left;
}

#existing .button {
	background-color: white;
	color: lightblue;
	margin-left: 25px;
	margin-right: 25px;
	margin-top: 10px;
	width: 50px;
	font-size: 15px;
	padding: 10px;
}

#existing .label {
	font-size: 25px;
}

#create {
	width: 300px;
	height: 400px;
	background-color: white;
	border-style: inset;
	border-width: 5px;
	border-color: lightblue;
	float: right;
}

#create .button {
	background-color: lightblue;
	color: white;
	margin-left: 25px;
	margin-right: 25px;
	margin-top: 10px;
	width: 5px;
	font-size: 25px;
	padding: 10px;
}

#create .label {
	font-size: 25px;
}

#footer {
	clear: both;
}
</style>
</head>
<body>
	<c:if test="${! empty errorMessage}">
		<div class="error">${errorMessage}</div>
	</c:if>
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
	<div id="content">
		<div id="existing">
		<form name="form1" method="POST" action="${pageContext.servletContext.contextPath}/login">
			<h3>Existing User</h3>
			<div class="label">Username:</div>
			<div>
				<input type="text" name="inUsername" value="${app.username}" />
			</div>

			<div class="label">Password:</div>
			<div>
				<input type="text" name="inPassword" value="${app.password}" />
			</div>

				<input type="submit" name="submit" value="Sign In">
			</form>

			<form name="form2" method="POST" action="login">
				<input type="submit" name="submit" value="Forgot Password">
			</form>

		</div>

		<div id="create">
		<form id="new" name="form1" method="POST" action="${pageContext.servletContext.contextPath}/login">
			<h3>Create an Account</h3>

			<div class="label">Username:</div>
			<div>
				<input type="text" name="upUsername" value="${app.username}" />
			</div>

			<div class="label">Password:</div>
			<div>
				<input type="text" name="upPassword" value="${app.password}" />
			</div>

			<div class="label">Confirm Password:</div>
			<div>
				<input type="text" name="ConfirmPassword" value="${confirm_password}" />
			</div>		
				<input type="submit" name="submit" value="Sign Up ">
			</form>
		</div>
	</div>
	<div id="footer"></div>
</body>
</html>
