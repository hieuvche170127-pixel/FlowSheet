<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 400px; margin: 100px auto; }
        .error { color: red; }
    </style>
</head>
<body>
<h2>Login to Timesheet System</h2>
<form action="login" method="post">
    <label>Username:</label><br>
    <input type="text" name="username" required autofocus><br><br>

    <label>Password:</label><br>
    <input type="password" name="password" required><br><br>

    <button type="submit">Login</button>
</form>

<p class="error">${error}</p>
<p>Don't have an account? <a href="/FlowSheet/register.jsp">Register here</a></p>

<hr>
</body>
</html>