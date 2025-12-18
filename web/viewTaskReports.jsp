<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Task Reports - LAB Timesheet</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
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
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-xl-10 col-lg-11 col-md-12">
            <div class="card view-card">
                <div class="card-header text-center bg-transparent">
                    <i class="fas fa-clipboard-list fa-4x text-primary mb-3"></i>
                    <h3 class="mb-1 fw-bold text-dark">My Task Reports</h3>
                    <p class="text-muted mb-0">Review, update, or remove your task reports</p>
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

                    <div class="text-end mb-3">
                        <a href="${pageContext.request.contextPath}/task-report/create" class="btn btn-success btn-add">
                            <i class="fas fa-plus me-2"></i>Create New Report
                        </a>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-hover table-striped align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Task</th>
                                <th>Progress (%)</th>
                                <th>Total Hours</th>
                                <th>Timesheet Entry</th>
                                <th>Created At</th>
                                <th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty reports}">
                                    <c:forEach var="report" items="${reports}">
                                        <tr>
                                            <td>${report.reportId}</td>
                                            <td>
                                                <c:set var="task" value="${taskMap[report.taskId]}"/>
                                                <c:choose>
                                                    <c:when test="${not empty task}">
                                                        ${task.taskName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Task #${report.taskId}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${report.estimateWorkPercentDone != null ? report.estimateWorkPercentDone : '0'}</td>
                                            <td>${report.totalHourUsed != null ? report.totalHourUsed : '0'}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty report.timesheetEntryId}">
                                                        Entry #${report.timesheetEntryId}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">Not linked</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty report.createdAt}">
                                                        <fmt:formatDate value="${report.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                                                    </c:when>
                                                    <c:otherwise>N/A</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/task-report/update?reportId=${report.reportId}"
                                                   class="btn btn-primary btn-edit btn-sm">
                                                    <i class="fas fa-edit"></i> Edit
                                                </a>
                                                <form action="${pageContext.request.contextPath}/task-report/list" method="post" class="mb-0">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="reportId" value="${report.reportId}">
                                                    <button type="submit" class="btn btn-danger btn-delete btn-sm"
                                                            onclick="return confirm('Are you sure you want to delete this report?');">
                                                        <i class="fas fa-trash"></i> Delete
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="text-center py-4">
                                            <i class="fas fa-exclamation-circle text-muted fa-2x mb-2 d-block"></i>
                                            No task reports yet. Create one to get started.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Back to Home
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>




