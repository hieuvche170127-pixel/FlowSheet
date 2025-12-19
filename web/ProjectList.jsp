<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Projects List | FlowSheet</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        
        <style>
            body {
                background-color: #f8f9fa;
                padding: 20px;
            }
            .card {
                border: none;
                box-shadow: 0 0 15px rgba(0,0,0,0.05);
                border-radius: 8px;
            }
            .btn-green {
                background-color: #00bfa5;
                color: white;
                border: none;
            }
            .btn-green:hover {
                background-color: #009688;
                color: white;
            }
            .nav-pills .nav-link.active {
                background-color: #00bfa5 !important;
            }
            .nav-pills .nav-link {
                color: #6c757d;
                border-radius: 4px;
                margin-right: 5px;
                font-weight: 500;
            }
            .project-code {
                font-size: 0.85em;
                color: #888;
            }
            .project-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                background-color: #9fa8da;
                color: white;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: bold;
                margin-right: 15px;
            }
            /* Badge styles */
            .badge-open { background-color: #e3f2fd; color: #2196f3; }
            .badge-inprogress { background-color: #fff3cd; color: #ffc107; }
            .badge-complete { background-color: #e8f5e9; color: #4caf50; }
            
            .table > :not(caption) > * > * {
                padding: 1rem 0.5rem;
            }
        </style>
    </head>
    <body>
        
        <div class="container-fluid">
            <div class="card p-4">
                
                <h3 class="mb-4 font-weight-bold">Projects</h3>
                
                <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                    
                    <form action="projects" method="get" class="d-flex align-items-center flex-grow-1 flex-wrap gap-3">
                        
                        <div class="nav nav-pills">
                            <button type="submit" name="status" value="Active" 
                                    class="nav-link ${empty param.status || param.status == 'Active' ? 'active' : ''}">
                                Active
                            </button>
                            <button type="submit" name="status" value="All" 
                                    class="nav-link ${param.status == 'All' ? 'active' : ''}">
                                All Projects
                            </button>
                            </div>

                        <div class="input-group" style="max-width: 350px;">
                            <input type="text" name="search" class="form-control" 
                                   placeholder="Search by name & code..." 
                                   value="${param.search}">
                            <button class="btn btn-green" type="submit">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </form>

                    <div>
                        <a href="${pageContext.request.contextPath}/project/create" class="btn btn-green">
                            <i class="fas fa-plus"></i> Create Project
                        </a>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table align-middle table-hover">
                        <thead class="text-muted bg-light">
                            <tr>
                                <th style="width: 35%">Project Name</th>
                                <th style="width: 30%">Description</th> 
                                <th style="width: 15%">Deadline</th>
                                <th style="width: 10%">Status</th>
                                <th style="width: 10%">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:if test="${empty projectList}">
                                <tr>
                                    <td colspan="5" class="text-center py-5">
                                        <div class="text-muted">
                                            <i class="fas fa-folder-open fa-3x mb-3"></i><br>
                                            No projects found based on your search.
                                        </div>
                                    </td>
                                </tr>
                            </c:if>

                            <c:forEach var="p" items="${projectList}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="project-avatar">
                                                ${not empty p.projectName ? p.projectName.substring(0, 1).toUpperCase() : 'P'}
                                            </div> 
                                            <div>
                                                <div class="fw-bold text-dark">${p.projectName}</div>
                                                <div class="project-code">Code: ${p.projectCode}</div>
                                            </div>
                                        </div>
                                    </td>
                                    
                                    <td class="text-secondary small">
                                        <c:choose>
                                            <c:when test="${not empty p.description && p.description.length() > 50}">
                                                ${p.description.substring(0, 50)}...
                                            </c:when>
                                            <c:otherwise>${p.description}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty p.deadline}">
                                                <fmt:formatDate value="${p.deadline}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">None</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'COMPLETE'}">
                                                <span class="badge badge-complete px-2 py-1">Complete</span>
                                            </c:when>
                                            <c:when test="${p.status == 'IN_PROGRESS'}">
                                                <span class="badge badge-inprogress px-2 py-1 text-dark">In Progress</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-open px-2 py-1">Open</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>
                                        <a href="${pageContext.request.contextPath}/project/details?id=${p.projectID}" 
                                           class="btn btn-outline-secondary btn-sm">
                                            View Details
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <c:if test="${endPage > 0}">
                    <div class="d-flex justify-content-end mt-4">
                        <nav aria-label="Page navigation">
                            <ul class="pagination">
                                
                                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                    <a class="page-link" 
                                       href="projects?page=${currentPage - 1}&search=${param.search}&status=${param.status}" 
                                       aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>

                                <c:forEach begin="1" end="${endPage}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" 
                                           href="projects?page=${i}&search=${param.search}&status=${param.status}">
                                            ${i}
                                        </a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage >= endPage ? 'disabled' : ''}">
                                    <a class="page-link" 
                                       href="projects?page=${currentPage + 1}&search=${param.search}&status=${param.status}" 
                                       aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </c:if>
                </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
