<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Change Password - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }
        input, button { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { background: #0066cc; color: white; border: none; cursor: pointer; }
        .error { color: red; font-weight: bold; }
        .success { color: green; font-weight: bold; }
    </style>
</head>
<body>
<h2>Change Password</h2>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
<div class="success"><%= request.getAttribute("success") %></div>
<% } %>

<form action="profile/change-password" method="post">
    <label>Current Password</label>
    <input type="password" name="currentPassword" required>

    <label>New Password</label>
    <input type="password" name="newPassword" required minlength="4">

    <label>Confirm New Password</label>
    <input type="password" name="confirmPassword" required minlength="4">

    <button type="submit">Change Password</button>
</form>

<p><a href="profile/view">Back to Profile View</a></p>
</body>
</html>