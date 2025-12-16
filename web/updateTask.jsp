<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.Project"%>
<%@page import="entity.ProjectTask"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Task - LAB Timesheet</title>

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
        .update-card {
            border: none;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(0,0,0,0.25);
            max-width: 600px;
            margin: 0 auto;
        }
        .card-header {
            background: rgba(255,255,255,0.95);
            border-bottom: none;
            padding: 2rem 1.5rem;
        }
        .btn-update {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 50px;
            padding: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
        }
        .btn-update:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }
        .btn-cancel {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
            border: none;
            border-radius: 50px;
            padding: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
        }
        .btn-cancel:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(220, 53, 69, 0.4);
        }
        .form-floating > label {
            color: #555;
        }
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.25rem rgba(102, 126, 234, 0.25);
        }
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            margin-top: 1rem;
        }
        .checkbox-group label {
            margin-bottom: 0;
            font-weight: 500;
            color: #333;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-6 col-md-8 col-sm-10">
            <div class="card update-card">
                <div class="card-header text-center bg-transparent">
                    <i class="fas fa-edit fa-4x text-primary mb-3"></i>
                    <h3 class="mb-1 fw-bold text-dark">Update Task</h3>
                    <p class="text-muted mb-0">Modify task details below</p>
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

                    <% ProjectTask task = (ProjectTask) request.getAttribute("task"); %>
                    <% if (task != null) { %>
                    <form action="update" method="post">
                        <input type="hidden" name="taskId" value="<%= task.getTaskId() %>">

                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="taskCode" name="taskCode"
                                   value="<%= task.getTaskCode() != null ? task.getTaskCode() : "" %>" required>
                            <label for="taskCode"><i class="fas fa-code me-2"></i>Task Code</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="taskName" name="taskName"
                                   value="<%= task.getTaskName() != null ? task.getTaskName() : "" %>" required>
                            <label for="taskName"><i class="fas fa-tasks me-2"></i>Task Name</label>
                        </div>

                        <div class="form-floating mb-3">
                                <textarea class="form-control" id="description" name="description"
                                          style="height: 120px"><%= task.getDescription() != null ? task.getDescription() : "" %></textarea>
                            <label for="description"><i class="fas fa-file-alt me-2"></i>Description</label>
                        </div>

                        <div class="form-floating mb-3">
                            <select class="form-select" id="projectId" name="projectId">
                                <option value="">None (Lab Default)</option>
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
                            <label for="projectId"><i class="fas fa-project-diagram me-2"></i>Assign to Project (Optional)</label>
                        </div>

                        <div class="form-floating mb-3">
                            <select class="form-select" id="status" name="status" required>
                                <option value="TO_DO" <%= "TO_DO".equals(task.getStatus()) ? "selected" : "" %>>TO_DO</option>
                                <option value="COMPLETE" <%= "COMPLETE".equals(task.getStatus()) ? "selected" : "" %>>COMPLETE</option>
                            </select>
                            <label for="status"><i class="fas fa-check-square me-2"></i>Status</label>
                        </div>

                        <div class="checkbox-group">
                            <input type="hidden" name="isActive" value="false">
                            <input type="checkbox" id="isActive" name="isActive" value="true"
                                <%= (task.getIsActive() != null && task.getIsActive()) ? "checked" : "" %>>
                            <label for="isActive">Active</label>
                        </div>

                        <div class="d-flex gap-3 mt-4">
                            <button type="submit" class="btn btn-primary btn-update flex-fill">
                                <i class="fas fa-save me-2"></i>Update Task
                            </button>
                            <button type="button" class="btn btn-danger btn-cancel flex-fill"
                                    onclick="window.location.href='view'">
                                <i class="fas fa-times me-2"></i>Cancel
                            </button>
                        </div>
                    </form>
                    <% } else { %>
                    <div class="alert alert-danger" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>Task not found.
                    </div>
                    <div class="text-center mt-3">
                        <a href="view" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to Task List
                        </a>
                    </div>
                    <% } %>

                    <div class="text-center mt-4">
                        <a href="view" class="text-decoration-none text-muted">
                            <i class="fas fa-arrow-left me-1"></i>Back to Task List
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