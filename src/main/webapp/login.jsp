<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<html>
<head>
    <h1>Login Page</h1>
</head>
<body>
    ${SPRING_SECURITY_LAST_EXCEPTION.message}
    ${invalidEmail}
    ${registered}
    ${isExists}
    <form action="loginPage" method="post">
    <p>Username:</p>
    <input type="text" name="username"/>
    <p>Password:</p>
    <input type="password" name="password"/><br><br>
    <input type="submit" value="Login"/>
    </form>
    <button onClick="window.location.href = '/forgotPassword';">Forgot Password?</button>
    <br><br>
    <form  action="http://localhost:8080/oauth2/authorization/google">
    <input type="submit" value="Google Login"/>
    </form>
    <a href="/addUserForm">Register</a>
</body>
</html>
