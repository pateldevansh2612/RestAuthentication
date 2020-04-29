<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<h2>Forgot Password</h2>
</head>
<body>
<form action="/sendMessage" method="get">
<label for="email"><b>Enter valid Email-Id:</b></label><br>
<input type="email" placeholder="Enter Email-ID" name="emailid">
<br><br>
<input type="submit">
</form>
</body>
</html>