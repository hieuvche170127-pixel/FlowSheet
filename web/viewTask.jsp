<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Tasks - LAB Timesheet</title>

    <!-- Bootstrap 5.3 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">

    <!-- Font Awesome 6 -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
          integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />

    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
            padding: 3rem 0;
        }
        .view-card {
            border: none;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(0,0,0,0.25);
            background: white;
        }
        .card-header {
            background: rgba(255,255,255,0.95);
            border-bottom: none;
            padding: 2rem 1.5rem;
        }
        .btn-add {
            background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            border: none;
            border-radius: 50px;
            padding: 10px 20px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-add:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(40, 167, 69, 0.4);
        }
        .btn-filter {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 50px;
            padding: 8px 16px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-filter:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }
        .btn-clear {
            background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
            border: none;
            border-radius: 50px;
            padding: 8px 16px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-clear:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(108, 117, 125, 0.4);
        }
        .btn-edit {
            background: #0066cc;
            border: none;
            border-radius: 20px;
            padding: 6px 12px;
        }
        .btn-delete {
            background: #dc3545;
            border: none;
            border-radius: 20px;
            padding: 6px 12px;
        }
        .table {
            border-radius: 10px;
            overflow: hidden;
        }
        .table thead th {
            background: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
        }
        .filter-form {
            background: rgba(255,255,255,0.9);
            border-radius: 15px;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.25rem rgba(102, 126, 234, 0.25);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-11 col-md-12">
            <div class="card view-card">
                <div class="card-header text-center bg-transparent">
                    <i class="fas fa-list fa-4x text-primary mb-3"></i>
                    <h3 class="mb-1 fw-bold text-dark">All Tasks</h3>
                    <p class="text-muted mb-0">View and manage your tasks</p>
                </div>
                <div class="card-body p-4 p-xl-5">

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i><%= request.getAttribute("error") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>
                    <% if (request.getAttribute("success") != null) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i><%= request.getAttribute("success") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <div class="filter-form">
                        <form action="view" method="get" class="row g-3 align-items-end">
                            <div class="col-lg-5">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="search" name="search"
                                           value="<%= request.getAttribute("searchFilter") != null ? request.getAttribute("searchFilter") : "" %>"
                                           placeholder="Search by Task ID, Task Name, or Project Name...">
                                    <label for="search"><i class="fas fa-search me-2"></i>Search</label>
                                </div>
                            </div>
                            <div class="col-lg-4">
                                <div class="form-floating">
                                    <select class="form-select" id="projectId" name="projectId">
                                        <option value="" ${empty projectIdFilter ? 'selected' : ''}>All Projects</option>
                                        <option value="lab" ${labSelected ? 'selected' : ''}>Lab (Unassigned)</option>
                                        <c:forEach var="p" items="${projects}">
                                            <option value="${p.projectID}" ${projectIdFilter == p.projectID ? 'selected' : ''}>
                                                ${p.projectName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <label for="projectId"><i class="fas fa-diagram-project me-2"></i>Project</label>
                                </div>
                            </div>
                            <div class="col-lg-3 d-flex gap-2">
                                <button type="submit" class="btn btn-primary btn-filter flex-grow-1">
                                    <i class="fas fa-filter me-2"></i>Filter
                                </button>
                                <button type="button" class="btn btn-secondary btn-clear"
                                        onclick="window.location.href='view'">
                                    <i class="fas fa-times me-2"></i>Clear
                                </button>
                            </div>
                        </form>
                    </div>

                    <div class="text-end mb-3">
                        <a href="create" class="btn btn-success btn-add">
                            <i class="fas fa-plus me-2"></i>Add New Task
                        </a>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover table-striped">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Project</th>
                                <th>Status</th>
                                <th>Deadline</th>
                                <th>Estimate Hours</th>
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
                                            <td>${task.taskName}</td>
                                            <td>${task.description}</td>
                                            <td>${not empty task.projectName ? task.projectName : 'Lab (Unassigned)'}</td>
                                            <td>
                                                <span class="badge bg-${task.status == 'TO_DO' ? 'secondary' : task.status == 'IN_PROGRESS' ? 'primary' : task.status == 'SUSPENDED' ? 'warning' : 'success'}">
                                                    ${task.status}
                                                </span>
                                            </td>
                                            <td>${task.deadline != null ? task.deadline : 'N/A'}</td>
                                            <td>${task.estimateHourToDo != null ? task.estimateHourToDo : 'N/A'}</td>
                                            <td>${task.createdAt != null ? task.createdAt : 'N/A'}</td>
                                            <td class="d-flex gap-2">
                                                <a href="update?taskId=${task.taskId}" class="btn btn-primary btn-edit btn-sm">
                                                    <i class="fas fa-edit"></i> Edit
                                                </a>
                                                <form action="view" method="post" class="mb-0">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="taskId" value="${task.taskId}">
                                                    <button type="submit" class="btn btn-danger btn-delete btn-sm"
                                                            onclick="return confirm('Are you sure you want to delete this task?');">
                                                        <i class="fas fa-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="text-center py-4">
                                            <i class="fas fa-exclamation-circle text-muted fa-2x mb-2 d-block"></i>
                                            No tasks available.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <div class="text-center mt-4">
                        <a href="dashboard" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>