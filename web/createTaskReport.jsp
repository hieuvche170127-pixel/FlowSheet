<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="entity.ProjectTask"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Task Report - LAB Timesheet</title>

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
            max-width: 700px;
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
        .task-info {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-md-10 col-sm-12">
            <div class="card create-card">
                <div class="card-header text-center bg-transparent">
                    <i class="fas fa-clipboard-list fa-4x text-primary mb-3"></i>
                    <h3 class="mb-1 fw-bold text-dark">Create Task Report</h3>
                    <p class="text-muted mb-0">Report your progress on assigned tasks</p>
                </div>
                <div class="card-body p-4 p-xl-5">

                    <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    </c:if>
                    <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/task-report/create" method="post">
                        <div class="form-floating mb-3">
                            <select class="form-select" id="taskId" name="taskId" required>
                                <option value="">-- Select a Task --</option>
                                <c:forEach var="task" items="${tasks}">
                                    <option value="${task.taskId}">
                                        ${task.taskName} 
                                        <c:if test="${not empty task.status}">
                                            (${task.status})
                                        </c:if>
                                    </option>
                                </c:forEach>
                            </select>
                            <label for="taskId"><i class="fas fa-tasks me-2"></i>Select Task *</label>
                        </div>

                        <c:if test="${empty tasks}">
                        <div class="alert alert-warning">
                            <i class="fas fa-info-circle me-2"></i>
                            You don't have any assigned tasks. Please contact your supervisor to get tasks assigned.
                        </div>
                        </c:if>

                        <div class="form-floating mb-3">
                            <textarea class="form-control" id="reportDescription" name="reportDescription"
                                      style="height: 150px" placeholder="Describe your work progress..."></textarea>
                            <label for="reportDescription"><i class="fas fa-file-alt me-2"></i>Report Description</label>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="number" class="form-control" id="estimateWorkPercentDone" 
                                           name="estimateWorkPercentDone" step="0.1" min="0" max="100" 
                                           placeholder="0.0">
                                    <label for="estimateWorkPercentDone">
                                        <i class="fas fa-percentage me-2"></i>Progress (%) (0-100)
                                    </label>
                                    <div class="form-text">Enter the percentage of work completed (0-100)</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="number" class="form-control" id="totalHourUsed" 
                                           name="totalHourUsed" step="0.1" min="0" 
                                           placeholder="0.0">
                                    <label for="totalHourUsed">
                                        <i class="fas fa-clock me-2"></i>Total Hours Used
                                    </label>
                                    <div class="form-text">Enter the total hours spent on this task</div>
                                </div>
                            </div>
                        </div>

                        <div class="form-floating mb-4">
                            <input type="number" class="form-control" id="timesheetEntryId" 
                                   name="timesheetEntryId" min="0" placeholder="Optional">
                            <label for="timesheetEntryId">
                                <i class="fas fa-link me-2"></i>Timesheet Entry ID (Optional)
                            </label>
                            <div class="form-text">Link this report to a specific timesheet entry if applicable</div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success btn-create flex-grow-1" 
                                    <c:if test="${empty tasks}">disabled</c:if>>
                                <i class="fas fa-save me-2"></i>Create Report
                            </button>
                            <a href="${pageContext.request.contextPath}/task/view" 
                               class="btn btn-secondary" 
                               style="border-radius: 50px; padding: 12px 24px; font-weight: 600;">
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
    // Add validation for percentage field
    document.getElementById('estimateWorkPercentDone')?.addEventListener('input', function(e) {
        const value = parseFloat(e.target.value);
        if (value > 100) {
            e.target.setCustomValidity('Percentage cannot exceed 100%');
        } else if (value < 0) {
            e.target.setCustomValidity('Percentage cannot be negative');
        } else {
            e.target.setCustomValidity('');
        }
    });

    // Add validation for hours field
    document.getElementById('totalHourUsed')?.addEventListener('input', function(e) {
        const value = parseFloat(e.target.value);
        if (value < 0) {
            e.target.setCustomValidity('Hours cannot be negative');
        } else {
            e.target.setCustomValidity('');
        }
    });
</script>
</body>
</html>

