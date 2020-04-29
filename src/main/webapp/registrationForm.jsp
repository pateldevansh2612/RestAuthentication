<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<h2>Registration Form</h2>
</head>
<body>
<br><br>
<form action="/sentOtp" method="post">
${invalidEmail}
<label for="username"><b>Username/Email</b></label>
<input type="email" placeholder="Enter Email-ID/Username" name="username" required>
<br><br>
<label for="firstName"><b>First Name:</b></label>
<input type="text" placeholder="Enter First/Given Name" name="firstName" >
<br><br>
<label for="lastName"><b>Last Name:</b></label>
<input type="text" placeholder="Enter Last/Family Name" name="lastName" >
<br><br>
<label for="psw"><b>Password</b></label>
<input type="password" placeholder="Enter Password" name="password" id="psw" minlength="8" required><br>
<input type="checkbox" onclick="show()">Show Password
<script>
function show() {
  var x = document.getElementById("psw");
  if (x.type === "password") {
    x.type = "text";
  } else {
    x.type = "password";
  }
}
</script>
<c:out value="${validate}"/>
<br><br>
<label for="phoneNo"><b>Phone Number:</b></label>
<input type="text" placeholder="Enter Phone Number: " name="phoneNumber" required>
<br><br>
<input type="submit" value="Submit Form"/>
</form>