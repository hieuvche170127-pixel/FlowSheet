<%-- 
    Document   : studentHomePage
    Created on : Dec 13, 2025, 4:43:04 PM
    Author     : Admin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Student Homepage - LAB Timesheet</title>
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
                padding: 2rem 0;
            }
            .welcome-card {
                border: none;
                border-radius: 20px;
                overflow: hidden;
                box-shadow: 0 20px 40px rgba(0,0,0,0.25);
                background: white;
                margin-bottom: 2rem;
            }
            .card-header {
                background: rgba(255,255,255,0.95);
                border-bottom: none;
                padding: 2rem 1.5rem;
            }
            .action-card {
                border: none;
                border-radius: 15px;
                overflow: hidden;
                box-shadow: 0 10px 20px rgba(0,0,0,0.15);
                transition: all 0.3s ease;
                height: 100%;
            }
            .action-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 30px rgba(0,0,0,0.25);
            }
            .btn-action {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border: none;
                border-radius: 50px;
                padding: 15px 30px;
                font-weight: 600;
                letter-spacing: 0.5px;
                transition: all 0.3s ease;
                width: 100%;
            }
            .btn-action:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
            }
            .btn-report {
                background: linear-gradient(135deg, #28a745 0%, #218838 100%);
            }
            .btn-report:hover {
                box-shadow: 0 10px 20px rgba(40, 167, 69, 0.4);
            }
            .icon-wrapper {
                width: 80px;
                height: 80px;
                border-radius: 50%;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 1rem;
            }
            .icon-wrapper i {
                font-size: 2.5rem;
                color: white;
            }
        </style>
    </head>
    <body>
        <%@ include file="/nghiapages/layout_header.jsp" %>
        
        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <!-- Welcome Card -->
                    <div class="card welcome-card">
                        <div class="card-header text-center bg-transparent">
                            <i class="fas fa-user-graduate fa-4x text-primary mb-3"></i>
                            <h2 class="mb-1 fw-bold text-dark">Welcome, ${user.fullName}!</h2>
                            <p class="text-muted mb-0">Student Dashboard - LAB Timesheet</p>
                        </div>
                    </div>

                    <!-- Action Cards -->
                    <div class="row g-4">
                        <div class="col-md-6 col-lg-4">
                            <div class="card action-card">
                                <div class="card-body text-center p-4">
                                    <div class="icon-wrapper">
                                        <i class="fas fa-clipboard-list"></i>
                                    </div>
                                    <h5 class="card-title mb-3">My Reports</h5>
                                    <p class="card-text text-muted mb-4">View and manage all your task reports</p>
                                    <a href="${pageContext.request.contextPath}/task-report/list" class="btn btn-success btn-action btn-report">
                                        <i class="fas fa-eye me-2"></i>View My Reports
                                    </a>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6 col-lg-4">
                            <div class="card action-card">
                                <div class="card-body text-center p-4">
                                    <div class="icon-wrapper">
                                        <i class="fas fa-tasks"></i>
                                    </div>
                                    <h5 class="card-title mb-3">My Tasks</h5>
                                    <p class="card-text text-muted mb-4">View all tasks assigned to you</p>
                                    <a href="${pageContext.request.contextPath}/student/tasks" class="btn btn-primary btn-action">
                                        <i class="fas fa-list me-2"></i>View My Tasks
                                    </a>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6 col-lg-4">
                            <div class="card action-card">
                                <div class="card-body text-center p-4">
                                    <div class="icon-wrapper">
                                        <i class="fas fa-calendar-alt"></i>
                                    </div>
                                    <h5 class="card-title mb-3">My Timesheet</h5>
                                    <p class="card-text text-muted mb-4">Manage your timesheet entries</p>
                                    <a href="${pageContext.request.contextPath}/ViewAndSearchTimesheet" class="btn btn-info btn-action">
                                        <i class="fas fa-clock me-2"></i>View Timesheet
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%@ include file="/nghiapages/layout_footer.jsp" %>
        
        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
