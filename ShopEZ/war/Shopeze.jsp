<!DOCTYPE html>
<html>
  <head>
    <title> Shopeze </title>
    <style>
    body{
      background-image: url("https://i.imgur.com/tnhC0DF.jpg");
      background-repeat: no-repeat;
      background-size: cover;
    }
    #header{
      background-color: white;
      border: 1px solid black;
      padding: 10px 30px;
      background-color: white;
      font-style: italic;
    }
    p{
    float:top
    margin-top:50px;
    }
    #logo{
      color: black;
      font-size: 70px;
      text-align: left;
    }
    #nav{
      float: right;
    }
    .button{
      float: right;
    }

    #content{
      padding-top: 5px;
      text-align: center;
      font-size: 85px;
      text-shadow: 2px 2px lightblue;
      color: black;
      height: 200px;

    }

    #login, #guest {
    padding: 12px 20px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 20px;
    
    -webkit-transition-duration: 0.4s;
    transition-duration: 0.4s;
    cursor: pointer;
    float: bottom;
    }

    #login{
      background-color: white;
      border:2px solid lightblue;
      color:lightblue;
    }
    #guest{
      background-color: lightblue;
      border:2px solid white;
      color: white;
    }

    #login:hover{
    background-color: lightblue;
    border:2px solid white;
    color: white;
    }
     #guest:hover{
       background-color: white;
       border:2px solid lightblue;
       color: lightblue;
     }
    </style>
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
          <button id="login" onclick=href=login.jsp>Login</button>
          <button id="guest" onclick="guest.jsp">Continue as Guest</button>
      </div>
      <div id="footer">

      </div>
    </div>
    <script>

    </script>
  </body>
</html>