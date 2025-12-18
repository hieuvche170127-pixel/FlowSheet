<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Tasks</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-0">My Tasks</h3>
            <p class="text-muted mb-0">Tasks assigned to you</p>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">Back</a>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty tasks}">
                    <div class="table-responsive">
                        <table class="table table-striped align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Project</th>
                                <th>Status</th>
                                <th>Deadline</th>
                                <th>Estimate Hours</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="task" items="${tasks}">
                                <tr>
                                    <td>${task.taskId}</td>
                                    <td>${task.taskName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty task.projectName}">
                                                ${task.projectName}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Lab / Unassigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge bg-${task.status == 'IN_PROGRESS' ? 'primary' : task.status == 'SUSPENDED' ? 'warning' : 'secondary'}">
                                            ${task.status}
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty task.deadline}">
                                                <fmt:formatDate value="${task.deadline}" pattern="yyyy-MM-dd HH:mm"/>
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${task.estimateHourToDo != null ? task.estimateHourToDo : 'N/A'}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center text-muted py-4">
                        <i class="fas fa-clipboard-list fa-2x mb-2"></i>
                        <div>No tasks assigned to you yet.</div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>
</body>
</html>


