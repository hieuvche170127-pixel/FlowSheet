<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.Task"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View Tasks - LAB Timesheet</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 50px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .error { color: red; font-weight: bold; }
        .success { color: green; font-weight: bold; }
        button.delete { background: #ff0000; color: white; border: none; padding: 5px 10px; cursor: pointer; }
        a.add { display: inline-block; margin-bottom: 20px; padding: 10px 20px; background: #0066cc; color: white; text-decoration: none; }
        .filter-form { background: #f9f9f9; padding: 15px; margin-bottom: 20px; border: 1px solid #ddd; border-radius: 5px; }
        .filter-form label { display: inline-block; margin-right: 10px; font-weight: bold; }
        .filter-form input { padding: 5px; margin-right: 10px; width: 200px; }
        .filter-form button { padding: 5px 15px; background: #0066cc; color: white; border: none; cursor: pointer; }
        .filter-form button.clear { background: #666; }
    </style>
</head>
<body>
<h2>All Tasks</h2>

<% if (request.getAttribute("error") != null) { %>
<div class="error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
<div class="success"><%= request.getAttribute("success") %></div>
<% } %>

<div class="filter-form">
    <form action="view" method="get" style="display: inline-block;">
        <label for="taskName">Task Name:</label>
        <input type="text" id="taskName" name="taskName" 
               value="<%= request.getAttribute("taskNameFilter") != null ? request.getAttribute("taskNameFilter") : "" %>" 
               placeholder="Filter by task name...">
        
        <label for="projectName">Project Name:</label>
        <input type="text" id="projectName" name="projectName" 
               value="<%= request.getAttribute("projectNameFilter") != null ? request.getAttribute("projectNameFilter") : "" %>" 
               placeholder="Filter by project name...">
        
        <button type="submit">Filter</button>
        <button type="button" class="clear" onclick="window.location.href='view'">Clear</button>
    </form>
</div>

<a href="createTask.jsp" class="add">Add New Task</a>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Code</th>
        <th>Name</th>
        <th>Description</th>
        <th>Project</th>
        <th>Status</th>
        <th>Active</th>
        <th>Created At</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty tasks}">
            <c:forEach var="task" items="${tasks}">
                <tr>
                    <td>${task.taskId}</td>
                    <td>${task.taskCode}</td>
                    <td>${task.taskName}</td>
                    <td>${task.description}</td>
                    <td>${not empty task.projectName ? task.projectName : 'Lab (Unassigned)'}</td>
                    <td>${task.status}</td>
                    <td>${task.isActive ? 'True' : 'False'}</td>
                    <td>${task.createdAt}</td>
                    <td>
                        <a href="update?taskId=${task.taskId}" style="display:inline-block; padding: 5px 10px; background: #0066cc; color: white; text-decoration: none; margin-right: 5px;">Edit</a>
                        <form action="view" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="taskId" value="${task.taskId}">
                            <button type="submit" class="delete" onclick="return confirm('Are you sure you want to delete this task?');">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr><td colspan="9">No tasks available.</td></tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>
<p><a href="dashboard">Back to Dashboard</a></p>
</body>
</html>