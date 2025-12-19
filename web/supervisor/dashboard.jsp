<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Supervisor Dashboard - LAB Timesheet</title>
    
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
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css_files/supervisor-dashboard.css">
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/supervisor/dashboard">
                <i class="fas fa-chart-line me-2"></i>Supervisor Dashboard
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/supervisor/dashboard">
                            <i class="fas fa-home me-1"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/supervisor/projects">
                            <i class="fas fa-project-diagram me-1"></i>Projects
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/teams">
                            <i class="fas fa-users me-1"></i>Teams
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile.jsp">
                            <i class="fas fa-user me-1"></i>Profile
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-1"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">
        <!-- Welcome Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="welcome-card p-4">
                    <h2 class="mb-1">
                        <i class="fas fa-user-tie me-2"></i>Welcome, ${supervisor.fullName}!
                    </h2>
                    <p class="text-muted mb-0">Monitor and manage timesheets, projects, and teams</p>
                </div>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-3 col-sm-6">
                <div class="stat-card stat-card-pending">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <h3 class="stat-number">${pendingCount}</h3>
                        <p class="stat-label">Pending Approvals</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card stat-card-projects">
                    <div class="stat-icon">
                        <i class="fas fa-project-diagram"></i>
                    </div>
                    <div class="stat-content">
                        <h3 class="stat-number">${totalProjects}</h3>
                        <p class="stat-label">Active Projects</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card stat-card-teams">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-content">
                        <h3 class="stat-number">${totalTeams}</h3>
                        <p class="stat-label">Teams</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6">
                <div class="stat-card stat-card-students">
                    <div class="stat-icon">
                        <i class="fas fa-user-graduate"></i>
                    </div>
                    <div class="stat-content">
                        <h3 class="stat-number">${totalStudents}</h3>
                        <p class="stat-label">Students</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Content Row -->
        <div class="row g-4">
            <!-- Pending Timesheet Entries -->
            <div class="col-lg-8">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-list-check me-2"></i>Pending Timesheet Approvals
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty pendingEntries}">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Date</th>
                                                <th>Time</th>
                                                <th>Duration</th>
                                                <th>Project</th>
                                                <th>Note</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="entry" items="${pendingEntries}">
                                                <tr>
                                                    <td>
                                                        <fmt:formatDate value="${entry.workDate}" pattern="MMM dd, yyyy" />
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty entry.startTime}">
                                                            <fmt:formatDate value="${entry.startTime}" pattern="HH:mm" type="time" />
                                                            -
                                                            <fmt:formatDate value="${entry.endTime}" pattern="HH:mm" type="time" />
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-info">
                                                            ${entry.minutesWorked} min
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty entry.projectId}">
                                                                Project #${entry.projectId}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">N/A</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty entry.note && entry.note.length() > 30}">
                                                                ${entry.note.substring(0, 30)}...
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${entry.note != null ? entry.note : '-'}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <button class="btn btn-sm btn-success me-1" 
                                                                onclick="approveEntry(${entry.entryId})"
                                                                title="Approve">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger" 
                                                                onclick="rejectEntry(${entry.entryId})"
                                                                title="Reject">
                                                            <i class="fas fa-times"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <c:if test="${pendingCount > 10}">
                                    <div class="text-center mt-3">
                                        <a href="#" class="btn btn-outline-primary">
                                            View All ${pendingCount} Pending Entries
                                        </a>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                                    <p class="text-muted">No pending timesheet entries to review.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Quick Actions & Recent Projects -->
            <div class="col-lg-4">
                <!-- Quick Actions -->
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-bolt me-2"></i>Quick Actions
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/supervisor/projects" 
                               class="btn btn-outline-primary">
                                <i class="fas fa-project-diagram me-2"></i>View All Projects
                            </a>
                            <a href="${pageContext.request.contextPath}/teams" 
                               class="btn btn-outline-primary">
                                <i class="fas fa-users me-2"></i>Manage Teams
                            </a>
                            <a href="${pageContext.request.contextPath}/task/view"
                               class="btn btn-outline-primary">
                                <i class="fas fa-tasks me-2"></i>Tasks
                            </a>
                            <a href="${pageContext.request.contextPath}/project/create" 
                               class="btn btn-outline-success">
                                <i class="fas fa-plus me-2"></i>Create New Project
                            </a>
                            <a href="${pageContext.request.contextPath}/team/create" 
                               class="btn btn-outline-success">
                                <i class="fas fa-user-plus me-2"></i>Create New Team
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Recent Projects -->
                <div class="card shadow-sm">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-folder-open me-2"></i>Recent Projects
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty recentProjects}">
                                <div class="list-group list-group-flush">
                                    <c:forEach var="project" items="${recentProjects}">
                                        <a href="${pageContext.request.contextPath}/project/details?id=${project.projectID}" 
                                           class="list-group-item list-group-item-action">
                                            <div class="d-flex w-100 justify-content-between">
                                                <h6 class="mb-1">${project.projectName}</h6>
                                                <small>
                                                    <span class="badge bg-${project.status == 'COMPLETE' ? 'success' : 'primary'}">
                                                        ${project.status != null ? project.status : 'OPEN'}
                                                    </span>
                                                </small>
                                            </div>
                                            <p class="mb-1 text-muted small">${project.projectCode}</p>
                                        </a>
                                    </c:forEach>
                                </div>
                                <div class="text-center mt-3">
                                    <a href="${pageContext.request.contextPath}/supervisor/projects" 
                                       class="btn btn-sm btn-outline-info">
                                        View All Projects
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted text-center py-3">No projects available</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" 
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" 
            crossorigin="anonymous"></script>
    
    <script>
        function approveEntry(entryId) {
            if (confirm('Are you sure you want to approve this timesheet entry?')) {
                // TODO: Implement approval logic
                fetch('${pageContext.request.contextPath}/supervisor/approve-timesheet?id=' + entryId, {
                    method: 'POST'
                })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error approving entry');
                    }
                });
            }
        }
        
        function rejectEntry(entryId) {
            const reason = prompt('Please provide a reason for rejection:');
            if (reason) {
                // TODO: Implement rejection logic
                fetch('${pageContext.request.contextPath}/supervisor/reject-timesheet?id=' + entryId, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'reason=' + encodeURIComponent(reason)
                })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error rejecting entry');
                    }
                });
            }
        }
    </script>
</body>
</html>

