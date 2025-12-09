<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Projects List</title>
        
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        
        <style>
            /* CSS tùy chỉnh để giống hình ảnh mẫu */
            body {
                background-color: #f8f9fa; /* Màu nền xám nhạt */
                padding: 20px;
            }
            .card {
                border: none;
                box-shadow: 0 0 15px rgba(0,0,0,0.05); /* Đổ bóng nhẹ cho khung trắng */
                border-radius: 8px;
            }
            .btn-green {
                background-color: #00bfa5; /* Màu xanh ngọc giống trong ảnh */
                color: white;
                border: none;
            }
            .btn-green:hover {
                background-color: #009688;
                color: white;
            }
            .nav-pills .nav-link.active {
                background-color: #00bfa5 !important; /* Màu tab Active */
            }
            .nav-pills .nav-link {
                color: #6c757d;
                border-radius: 4px;
                margin-right: 5px;
            }
            .project-code {
                font-size: 0.85em;
                color: #888;
            }
            .badge-open {
                background-color: #e3f2fd; /* Nền xanh nhạt */
                color: #2196f3; /* Chữ xanh dương */
            }
            .badge-complete {
                background-color: #e8f5e9; /* Nền xanh lá nhạt */
                color: #4caf50; /* Chữ xanh lá */
            }
            /* Tạo hình tròn Avatar cho tên dự án */
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
                margin-right: 10px;
            }
        </style>
    </head>
    <body>
        
        <div class="container-fluid">
            <div class="card p-4">
                
                <h3 class="mb-4 font-weight-bold">Projects</h3>
                
                <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap">
                    
                    <form action="projects" method="get" class="d-flex align-items-center flex-grow-1">
                        
                        <div class="nav nav-pills me-4">
                            <button type="submit" name="status" value="Active" 
                                    class="nav-link ${empty param.status || param.status == 'Active' ? 'active' : ''}">
                                Active
                            </button>
                            <button type="submit" name="status" value="Archived" 
                                    class="nav-link ${param.status == 'Archived' ? 'active' : ''}">
                                Archived
                            </button>
                        </div>

                        <div class="input-group" style="max-width: 300px;">
                            <input type="text" name="search" class="form-control" 
                                   placeholder="Search by name & code..." 
                                   value="${param.search}"> <button class="btn btn-green" type="submit">
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
                    <table class="table align-middle">
                        <thead class="text-muted">
                            <tr>
                                <th style="width: 40%">Project Name</th>
                                <th>Description</th> <th>Deadline</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${projectList}">
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="project-avatar">P</div> 
                                            <div>
                                                <div class="fw-bold">${p.projectName}</div>
                                                <div class="project-code">Code: ${p.projectCode}</div>
                                            </div>
                                        </div>
                                    </td>
                                    
                                    <td class="text-secondary">
                                        <c:choose>
                                            <c:when test="${p.description.length() > 30}">
                                                ${p.description.substring(0, 30)}...
                                            </c:when>
                                            <c:otherwise>${p.description}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>${p.deadline}</td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.status == 'COMPLETE'}">
                                                <span class="badge badge-complete px-3 py-2">Complete</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-open px-3 py-2">Open</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td>
                                        <a href="${pageContext.request.contextPath}/project/details?id=${p.projectID}" class="btn btn-outline-secondary btn-sm">
                                            View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            
                            <c:if test="${empty projectList}">
                                <tr>
                                    <td colspan="5" class="text-center py-4">
                                        No projects found based on your search.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                
            </div>
        </div>

    </body>
</html>
