<!DOCTYPE html>
<html>
<head>
<title>Shopeze</title>
<link rel="stylesheet" href="webresources/style.css" />

</head>
<body>
	<div id="container">
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
		<div id="content">
			<h4>Find your Fastest Route!</h4>
			<form name="form1" method="GET"
				action="${pageContext.servletContext.contextPath}/login">
				<input id="login" type="submit" name="login" value="Login">
			</form>
			<form name="form2" method="POST"
				action="${pageContext.servletContext.contextPath}/insertItem">
				<input id="guest" type="submit" name="guest"
					value="Continue as Guest">
			</form>
		</div>
		<div id="footer"></div>
	</div>
	<script>
		
	</script>
</body>
</html>
