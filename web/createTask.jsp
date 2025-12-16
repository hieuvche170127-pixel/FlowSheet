<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.Project"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Task - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }
        input, select, textarea, button { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { background: #0066cc; color: white; border: none; cursor: pointer; }
        .error { color: red; font-weight: bold; }
        .success { color: green; font-weight: bold; }
    </style>
</head>
<body>
<h2>Create New Task</h2>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
<div class="success"><%= request.getAttribute("success") %></div>
<% } %>

<form action="create" method="post">
    <label>Task Code</label>
    <input type="text" name="taskCode" required>

    <label>Task Name</label>
    <input type="text" name="taskName" required>

    <label>Description</label>
    <textarea name="description"></textarea>

    <label>Assign to Project (Optional)</label>
    <select name="projectId">
        <option value="">None (Lab Default)</option>
        <% List<Project> projects = (List<Project>) request.getAttribute("projects");
            if (projects != null) {
                for (Project p : projects) { %>
        <option value="<%= p.getProjectID() %>"><%= p.getProjectName() %></option>
        <%      }
        } %>
    </select>

    <button type="submit">Create Task</button>
</form>

<p><a href="dashboard">Back to Dashboard</a></p>
</body>
</html>