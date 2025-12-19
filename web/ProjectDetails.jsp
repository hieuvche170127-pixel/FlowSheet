<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Chi Tiết Dự Án | FlowSheet</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">

        <style>
            body {
                background-color: #f8f9fa;
            }

            /* Avatar Project */
            .project-avatar {
                width: 60px;
                height: 60px;
                background-color: #d4a373;
                color: white;
                font-size: 24px;
                font-weight: bold;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 15px;
            }

            /* Tabs Navigation */
            .nav-tabs {
                border-bottom: 2px solid #e9ecef;
            }
            .nav-tabs .nav-link {
                border: none;
                color: #6c757d;
                font-weight: 500;
                padding-bottom: 10px;
                margin-right: 20px;
            }
            .nav-tabs .nav-link.active {
                color: #20c997;
                border-bottom: 3px solid #20c997;
                background: transparent;
            }
            .nav-tabs .nav-link:hover {
                color: #20c997;
            }

            /* Badges */
            .badge-soft-success {
                background-color: #d1e7dd;
                color: #0f5132;
            }
            .badge-soft-secondary {
                background-color: #e2e3e5;
                color: #41464b;
            }
            .badge-soft-primary {
                background-color: #cfe2ff;
                color: #084298;
            }

            /* Pagination & Links */
            .back-link {
                text-decoration: none;
                color: #343a40;
                font-weight: 500;
                display: inline-flex;
                align-items: center;
                margin-bottom: 20px;
            }
            .back-link:hover {
                color: #20c997;
            }

            .page-link {
                color: #20c997;
            }
            .page-item.active .page-link {
                background-color: #20c997;
                border-color: #20c997;
            }
        </style>
    </head>
    <body>

        <div class="container mt-4">
            <a href="${pageContext.request.contextPath}/projects" class="back-link">
                <i class="bi bi-arrow-left me-2"></i> Back to all projects
            </a>

            <div class="card shadow-sm border-0 mb-4 bg-white">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-start">
                        <div class="d-flex">
                            <div class="project-avatar">
                                ${not empty project.projectName ? project.projectName.substring(0, 1).toUpperCase() : 'P'}
                            </div>
                            <div>
                                <h3 class="fw-bold mb-1">${project.projectName}</h3>
                                <div class="text-muted small mb-3">
                                    <span class="badge badge-soft-success me-2 text-uppercase">${project.status}</span>
                                    <span class="me-3"><i class="bi bi-barcode"></i> ${project.projectCode}</span>
                                    <span><i class="bi bi-calendar-event"></i> Deadline: 
                                        <c:choose>
                                            <c:when test="${not empty project.deadline}">
                                                <fmt:formatDate value="${project.deadline}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>none</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div>
                            <%-- KIỂM TRA QUYỀN: Chỉ hiện nút nếu KHÔNG PHẢI là Sinh viên (RoleID != 1) --%>
                            <c:if test="${sessionScope.LOGIN_USER.roleId != 1}">
                                
                                <%-- Nút Deactive --%>
                                <c:if test="${project.isActive}">
                                    <a href="${pageContext.request.contextPath}/project/details?id=${project.projectID}&action=deactive"
                                       class="btn btn-secondary text-white btn-sm me-1" 
                                       onclick="return confirm('Bạn có chắc chắn muốn ngưng hoạt động dự án này không?');">
                                        <i class="bi bi-x-circle"></i> Deactive
                                    </a>
                                </c:if>
                                
                                <%-- Nút Edit --%>
                                <a href="${pageContext.request.contextPath}/project/edit?id=${project.projectID}" class="btn btn-outline-secondary btn-sm me-1">
                                    <i class="bi bi-pencil"></i> Edit
                                </a>
                                
                            </c:if>
                            
                            <%-- (Tùy chọn) Nếu là sinh viên thì hiện nút View hoặc không hiện gì --%>
                            <c:if test="${sessionScope.LOGIN_USER.roleId == 1}">
                                <span class="badge bg-light text-dark border p-2">Read-only View</span>
                            </c:if>
                        </div>
                    </div>

                    <hr class="my-3 text-muted" style="opacity: 0.1;">

                    <p class="text-muted mb-0">
                        ${not empty project.description ? project.description : 'No description available for this project.'}
                    </p>
                </div>
            </div>

            <div class="card shadow-sm border-0">
                <div class="card-header bg-white border-0 pb-0 pt-3">
                    <ul class="nav nav-tabs" id="projectTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="tasks-tab" data-bs-toggle="tab" data-bs-target="#tasks" type="button">Tasks</button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="members-tab" data-bs-toggle="tab" data-bs-target="#members" type="button">Members</button>
                        </li>
                    </ul>
                </div>

                <div class="card-body">
                    <div class="tab-content" id="projectTabsContent">

                        <div class="tab-pane fade show active" id="tasks" role="tabpanel">

                            <table class="table table-hover align-middle mb-4">
                                <thead class="table-light text-muted small">
                                    <tr>
                                        <th>Task Code</th>
                                        <th>Task Name</th>
                                        <th>Assignee</th>
                                        <th>Status</th>
                                        <th class="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty tasks}">
                                        <tr><td colspan="5" class="text-center py-4 text-muted">No tasks found in this project.</td></tr>
                                    </c:if>

                                    <c:forEach var="task" items="${tasks}">
                                        <tr>
                                            <td class="fw-bold text-muted">${task.taskCode}</td>
                                            <td>${task.taskName}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty task.assigneeNames}">
                                                        <div class="d-flex align-items-center">
                                                            <div class="rounded-circle bg-secondary text-white d-flex justify-content-center align-items-center me-2" style="width:24px; height:24px; font-size:10px;">
                                                                <i class="bi bi-person"></i>
                                                            </div>
                                                            <span class="small">${task.assigneeNames}</span>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted small">Unassigned</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${task.status == 'COMPLETE'}">
                                                        <span class="badge badge-soft-success">Complete</span>
                                                    </c:when>
                                                    <c:when test="${task.status == 'IN_PROGRESS'}">
                                                        <span class="badge badge-soft-primary">In Progress</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-soft-secondary">To Do</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end">
                                                <a href="#" class="text-muted"><i class="bi bi-three-dots-vertical"></i></a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <div class="d-flex justify-content-between align-items-center">

                                <a href="task/create?projectId=${project.projectID}" class="text-decoration-none text-success fw-bold">
                                    <i class="bi bi-plus-lg"></i> Create New Task
                                </a>

                                <c:if test="${endPage > 0}">
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination pagination-sm mb-0">
                                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                                <a class="page-link" 
                                                   href="${pageContext.request.contextPath}/project/details?id=${project.projectID}&page=${currentPage - 1}" 
                                                   aria-label="Previous">
                                                    <span aria-hidden="true">&laquo;</span>
                                                </a>
                                            </li>

                                            <c:forEach begin="1" end="${endPage}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" 
                                                       href="${pageContext.request.contextPath}/project/details?id=${project.projectID}&page=${i}">
                                                        ${i}
                                                    </a>
                                                </li>
                                            </c:forEach>

                                            <li class="page-item ${currentPage >= endPage ? 'disabled' : ''}">
                                                <a class="page-link" 
                                                   href="${pageContext.request.contextPath}/project/details?id=${project.projectID}&page=${currentPage + 1}" 
                                                   aria-label="Next">
                                                    <span aria-hidden="true">&raquo;</span>
                                                </a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </div>

                        <div class="tab-pane fade" id="members" role="tabpanel">
                            <table class="table table-hover align-middle mt-3">
                                <thead class="table-light text-muted small">
                                    <tr>
                                        <th>Member</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="member" items="${members}">
                                        <tr>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="rounded-circle bg-success text-white d-flex justify-content-center align-items-center me-3" style="width:35px; height:35px;">
                                                        ${member.username.substring(0, 1).toUpperCase()}
                                                    </div>
                                                    <span class="fw-bold">${member.fullName}</span>
                                                </div>
                                            </td>
                                            <td class="text-muted">${member.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${member.roleInProject == 'Leader'}">
                                                        <span class="badge bg-warning text-dark">Leader</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-light text-dark border">Member</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>