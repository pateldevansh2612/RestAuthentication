<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<html>
<head>
    <h1>Password Reset Page</h1>
</head>
<body>
            <form action="/resetSuccessful" method="post">
    <p>Password:</p>
    <input type="password" name="password" id="password" minlength="8" required/>
    <p>Confirm Password:</p>
    <input type="password" name="password_confirm" id="password_confirm" minlength="8" oninput="check(this)" required/>
    <script language='javascript' type='text/javascript'>
        function check(input) {
            if (input.value != document.getElementById('password').value) {
                input.setCustomValidity('Password Must be Matching.');
            } else {
                input.setCustomValidity('');
            }
        }
    </script>
    <br><br>
    <input type="submit"/>
    </form>
    <c:out value="${length}"/>
</body>
</html>
