<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.Project"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Task - LAB Timesheet</title>

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
        .create-card {
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
        .btn-create {
            background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            border: none;
            border-radius: 50px;
            padding: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
        }
        .btn-create:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(40, 167, 69, 0.4);
        }
        .form-floating > label {
            color: #555;
        }
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.25rem rgba(102, 126, 234, 0.25);
        }
        .form-select:disabled {
            background-color: #e9ecef;
            cursor: not-allowed;
            opacity: 0.7;
        }
        .project-locked-info {
            font-size: 0.875rem;
            color: #6c757d;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-6 col-md-8 col-sm-10">
            <div class="card create-card">
                <div class="card-header text-center bg-transparent">
                    <i class="fas fa-plus-circle fa-4x text-success mb-3"></i>
                    <h3 class="mb-1 fw-bold text-dark">Create New Task</h3>
                    <p class="text-muted mb-0">Add a new task to the system</p>
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

                    <form action="create" method="post">
                        <div class="form-floating mb-3">
                            <input type="text" class="form-control" id="taskName" name="taskName" required>
                            <label for="taskName"><i class="fas fa-tasks me-2"></i>Task Name</label>
                        </div>

                        <div class="form-floating mb-3">
                                <textarea class="form-control" id="description" name="description"
                                          style="height: 120px"></textarea>
                            <label for="description"><i class="fas fa-file-alt me-2"></i>Description</label>
                        </div>

                        <div class="form-floating mb-3">
                            <% 
                                Integer selectedProjectId = (Integer) request.getAttribute("selectedProjectId");
                                boolean isProjectLocked = (selectedProjectId != null);
                            %>
                            <% if (isProjectLocked) { %>
                                <!-- Hidden input to ensure projectId is submitted when dropdown is disabled -->
                                <input type="hidden" name="projectId" value="<%= selectedProjectId %>">
                            <% } %>
                            <select class="form-select" id="projectId" name="projectId" <%= isProjectLocked ? "disabled" : "" %> required>
                                <option value="">-- Select Project --</option>
                                <% 
                                    List<Project> projects = (List<Project>) request.getAttribute("projects");
                                    if (projects != null) {
                                        for (Project p : projects) { 
                                            boolean isSelected = (selectedProjectId != null && selectedProjectId.equals(p.getProjectID()));
                                %>
                                <option value="<%= p.getProjectID() %>" <%= isSelected ? "selected" : "" %>><%= p.getProjectName() %></option>
                                <%      }
                                } %>
                            </select>
                            <label for="projectId"><i class="fas fa-project-diagram me-2"></i>Assign to Project *</label>
                            <% if (isProjectLocked) { %>
                                <div class="project-locked-info">
                                    <i class="fas fa-lock me-1"></i>Project is locked for this task
                                </div>
                            <% } %>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="datetime-local" class="form-control" id="deadline" name="deadline" min="">
                            <label for="deadline"><i class="fas fa-calendar-times me-2"></i>Deadline (Optional)</label>
                            <div class="invalid-feedback" id="deadline-error" style="display: none;">
                                Deadline cannot be in the past.
                            </div>
                        </div>

                        <div class="form-floating mb-4">
                            <input type="number" class="form-control" id="estimateHourToDo" name="estimateHourToDo"
                                   step="0.1" min="0">
                            <label for="estimateHourToDo"><i class="fas fa-clock me-2"></i>Estimate Hours (Optional)</label>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success btn-create flex-grow-1">
                                <i class="fas fa-plus me-2"></i>Create Task
                            </button>
                            <% 
                                String projectIdParam = request.getParameter("projectId");
                                String cancelUrl;
                                if (projectIdParam != null && !projectIdParam.trim().isEmpty()) {
                                    cancelUrl = request.getContextPath() + "/project/details?id=" + projectIdParam;
                                } else {
                                    cancelUrl = "view";
                                }
                            %>
                            <a href="<%= cancelUrl %>" class="btn btn-secondary" style="border-radius: 50px; padding: 12px 24px; font-weight: 600;">
                                <i class="fas fa-times me-2"></i>Cancel
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Set minimum date to today
    document.addEventListener('DOMContentLoaded', function() {
        const deadlineInput = document.getElementById('deadline');
        const now = new Date();
        // Format: YYYY-MM-DDTHH:mm
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
        deadlineInput.setAttribute('min', minDateTime);
        
        // Validate on change
        deadlineInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            const currentDate = new Date();
            
            if (selectedDate < currentDate) {
                this.setCustomValidity('Deadline cannot be in the past.');
                this.classList.add('is-invalid');
                document.getElementById('deadline-error').style.display = 'block';
            } else {
                this.setCustomValidity('');
                this.classList.remove('is-invalid');
                document.getElementById('deadline-error').style.display = 'none';
            }
        });
        
        // Validate on form submit
        document.querySelector('form').addEventListener('submit', function(e) {
            const deadlineInput = document.getElementById('deadline');
            if (deadlineInput.value) {
                const selectedDate = new Date(deadlineInput.value);
                const currentDate = new Date();
                
                if (selectedDate < currentDate) {
                    e.preventDefault();
                    deadlineInput.classList.add('is-invalid');
                    document.getElementById('deadline-error').style.display = 'block';
                    deadlineInput.focus();
                    return false;
                }
            }
        });
    });
</script>
</body>
</html>