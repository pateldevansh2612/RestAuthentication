<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<h2>Registration Form</h2>
</head>
<body>
<form action="/verifyOtpGoogle" method="get">
<label for="otp"><b>OTP:</b></label>
<input type="text" placeholder="Enter OTP" name="otp">
<br><br>
<input type="submit">
</form>

<button onClick="window.location.href = '/resendOtp';">Resend OTP</button>
<br>
<c:out value="${incorrectOtp}"/>
</body>
</html>