<!DOCTYPE html>
<html>
  <head>
    <title> Shopeze </title>   
  	<link rel="stylesheet" href="webresources/style.css" />
    
  </head>
  <body>
    <div id="container">
      <div id="header">
        <div id="logo">
        SHOP-EZE
        <img src="https://i.imgur.com/wtOuyHi.png" alt="SHOPEZE.png" style="width:50px;height:50px;">
        </div>
        <div id="nav">
          <button class="button" onclick="myFunction()">Login</button>
          <button class="button" onclick="myFunction()">About</button>
          <button class="button" onclick="myFunction()">Help</button>
        </div>
      </div>
      <div id="content">
          <p>Find your Fastest Route!</p>
          <form name ="form1" method="POST" action="login">
           <input id="login" type="submit"name = "submit" value = "Login">
          </form>
           <form name ="form2" method="POST" action="guest">
           <input id="guest" type="submit"name = "submit" value = "Continue as Guest">
          </form>
      </div>
      <div id="footer">
      </div>
    </div> 
    <script>
    </script>
  </body>
</html>
