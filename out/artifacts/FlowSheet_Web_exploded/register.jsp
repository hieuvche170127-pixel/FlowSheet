<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 500px; margin: 80px auto; padding: 20px; }
        input, button { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { background: #0066cc; color: white; border: none; cursor: pointer; }
        .error { color: red; font-weight: bold; }
    </style>
</head>
<body>
<h2>Create Your Account</h2>

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>

<form action="register" method="post">
    <label>Full Name</label>
    <input type="text" name="fullName" required>

    <label>Username</label>
    <input type="text" name="username" required>

    <label>Email</label>
    <input type="email" name="email" required>

    <label>Password</label>
    <input type="password" name="password" required minlength="4">

    <label>Confirm Password</label>
    <input type="password" name="confirmPassword" required minlength="4">

    <p><small>You will be registered as a <strong>Student</strong>.</small></p>

    <button type="submit">Register</button>
</form>

<p><a href="/FlowSheet/login.jsp">Already have an account? Login here</a></p>
</body>
</html>