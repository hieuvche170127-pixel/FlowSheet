<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Profile - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }
        input, button { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { background: #0066cc; color: white; border: none; cursor: pointer; }
        .error { color: red; font-weight: bold; }
        .success { color: green; font-weight: bold; }
        .section { margin-bottom: 30px; }
    </style>
</head>
<body>
<h2>User Profile</h2>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
<div class="success"><%= request.getAttribute("success") %></div>
<% } %>

<div class="section">
    <h3>Personal Information</h3>
    <p><strong>Username:</strong> ${user.username}</p>
    <p><strong>Role:</strong> ${user.role}</p>

    <form action="profile" method="post">
        <input type="hidden" name="action" value="updateProfile">
        <label>Full Name</label>
        <input type="text" name="fullName" value="${user.fullName}" required>

        <label>Email</label>
        <input type="email" name="email" value="${user.email}" required>

        <button type="submit">Update Profile</button>
    </form>
</div>

<div class="section">
    <h3>Change Password</h3>
    <form action="profile" method="post">
        <input type="hidden" name="action" value="changePassword">
        <label>Current Password</label>
        <input type="password" name="currentPassword" required>

        <label>New Password</label>
        <input type="password" name="newPassword" required minlength="4">

        <label>Confirm New Password</label>
        <input type="password" name="confirmPassword" required minlength="4">

        <button type="submit">Change Password</button>
    </form>
</div>

<p><a href="logout">Logout</a> | <a href="${user.role == 'STUDENT' ? 'student/timesheet.jsp' : (user.role == 'SUPERVISOR' ? 'supervisor/dashboard.jsp' : 'admin/dashboard.jsp')}">Back to Dashboard</a></p>
</body>
</html>