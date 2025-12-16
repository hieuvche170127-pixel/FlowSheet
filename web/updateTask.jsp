<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.Project"%>
<%@page import="entity.Task"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Update Task - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }
        input, select, textarea, button { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { background: #0066cc; color: white; border: none; cursor: pointer; }
        button.cancel { background: #666; }
        .error { color: red; font-weight: bold; }
        .success { color: green; font-weight: bold; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        .checkbox-group { display: flex; align-items: center; }
        .checkbox-group input[type="checkbox"] { width: auto; margin-right: 10px; }
    </style>
</head>
<body>
<h2>Update Task</h2>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
<div class="success"><%= request.getAttribute("success") %></div>
<% } %>

<% Task task = (Task) request.getAttribute("task"); %>
<% if (task != null) { %>
<form action="update" method="post">
    <input type="hidden" name="taskId" value="<%= task.getTaskId() %>">
    
    <div class="form-group">
        <label>Task Code</label>
        <input type="text" name="taskCode" value="<%= task.getTaskCode() != null ? task.getTaskCode() : "" %>" required>
    </div>

    <div class="form-group">
        <label>Task Name</label>
        <input type="text" name="taskName" value="<%= task.getTaskName() != null ? task.getTaskName() : "" %>" required>
    </div>

    <div class="form-group">
        <label>Description</label>
        <textarea name="description"><%= task.getDescription() != null ? task.getDescription() : "" %></textarea>
    </div>

    <div class="form-group">
        <label>Assign to Project</label>
        <select name="projectId" required>
            <option value="">Select Project</option>
            <% List<Project> projects = (List<Project>) request.getAttribute("projects");
                if (projects != null) {
                    for (Project p : projects) { %>
            <option value="<%= p.getProjectID() %>" 
                    <%= (task.getProjectId() != null && task.getProjectId().equals(p.getProjectID())) ? "selected" : "" %>>
                <%= p.getProjectName() %>
            </option>
            <%      }
            } %>
        </select>
    </div>

    <div class="form-group">
        <label>Status</label>
        <select name="status" required>
            <option value="TO_DO" <%= "TO_DO".equals(task.getStatus()) ? "selected" : "" %>>TO_DO</option>
            <option value="COMPLETE" <%= "COMPLETE".equals(task.getStatus()) ? "selected" : "" %>>COMPLETE</option>
        </select>
    </div>

    <div class="form-group">
        <div class="checkbox-group">
            <input type="checkbox" name="isActive" value="true" 
                   <%= task.isActive() ? "checked" : "" %>>
            <label style="display: inline; font-weight: normal;">Active</label>
        </div>
    </div>

    <button type="submit">Update Task</button>
    <button type="button" class="cancel" onclick="window.location.href='view'">Cancel</button>
</form>
<% } else { %>
<div class="error">Task not found.</div>
<p><a href="view">Back to Task List</a></p>
<% } %>

<p><a href="view">Back to Task List</a></p>
</body>
</html>

