<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile - LAB Timesheet</title>

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
            padding: 40px 20px;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .profile-container {
            max-width: 800px;
            margin: 0 auto;
        }

        .profile-header {
            background: white;
            border-radius: 15px 15px 0 0;
            padding: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            text-align: center;
        }

        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
            margin: 0 auto 20px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.2);
        }

        .profile-name {
            font-size: 28px;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 10px;
        }

        .profile-role {
            display: inline-block;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 500;
            margin-top: 10px;
        }

        .role-student {
            background-color: #e3f2fd;
            color: #1976d2;
        }

        .role-supervisor {
            background-color: #fff3e0;
            color: #f57c00;
        }

        .role-admin {
            background-color: #f3e5f5;
            color: #7b1fa2;
        }

        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            margin-bottom: 25px;
            overflow: hidden;
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 25px;
            font-weight: 600;
            font-size: 18px;
            border: none;
        }

        .card-header i {
            margin-right: 10px;
        }

        .card-body {
            padding: 30px;
            background: white;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
            display: block;
        }

        .form-control {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 12px 15px;
            font-size: 15px;
            transition: all 0.3s;
        }

        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .input-group-text {
            background-color: #f8f9fa;
            border: 2px solid #e9ecef;
            border-right: none;
            border-radius: 8px 0 0 8px;
            color: #6c757d;
        }

        .input-group .form-control {
            border-left: none;
            border-radius: 0 8px 8px 0;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 12px 30px;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s;
            width: 100%;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(102, 126, 234, 0.4);
        }

        .btn-outline-secondary {
            border: 2px solid #6c757d;
            color: #6c757d;
            padding: 10px 25px;
            font-weight: 500;
            border-radius: 8px;
            transition: all 0.3s;
        }

        .btn-outline-secondary:hover {
            background-color: #6c757d;
            color: white;
        }

        .alert {
            border-radius: 10px;
            border: none;
            padding: 15px 20px;
            margin-bottom: 25px;
            font-weight: 500;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
        }

        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
        }

        .back-link {
            text-align: center;
            margin-top: 20px;
        }

        .back-link a {
            color: white;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
        }

        .back-link a:hover {
            text-decoration: underline;
        }

        .section-divider {
            height: 1px;
            background: linear-gradient(to right, transparent, #e9ecef, transparent);
            margin: 30px 0;
        }
    </style>
</head>
<body>
<div class="profile-container">
    <!-- Profile Header -->
    <div class="profile-header">
        <div class="profile-avatar">
            <i class="fas fa-user"></i>
        </div>
        <div class="profile-name">${user.fullName}</div>
        <c:choose>
            <c:when test="${user.roleID == 1}">
                    <span class="profile-role role-student">
                        <i class="fas fa-graduation-cap me-1"></i>Student
                    </span>
            </c:when>
            <c:when test="${user.roleID == 2}">
                    <span class="profile-role role-supervisor">
                        <i class="fas fa-user-tie me-1"></i>Supervisor
                    </span>
            </c:when>
            <c:when test="${user.roleID == 3}">
                    <span class="profile-role role-admin">
                        <i class="fas fa-user-shield me-1"></i>Admin
                    </span>
            </c:when>
            <c:otherwise>
                <span class="profile-role">Unknown</span>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Alert Messages -->
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>
        <%= request.getAttribute("error") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-2"></i>
        <%= request.getAttribute("success") %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% } %>

    <!-- Personal Information Card -->
    <div class="card">
        <div class="card-header">
            <i class="fas fa-user-edit"></i>Personal Information
        </div>
        <div class="card-body">
            <form action="profile" method="post">
                <input type="hidden" name="action" value="updateProfile">

                <div class="mb-4">
                    <label for="username" class="form-label">
                        <i class="fas fa-user me-2"></i>Username
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-at"></i>
                            </span>
                        <input type="text"
                               class="form-control"
                               id="username"
                               name="username"
                               value="${user.username}"
                               required>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="fullName" class="form-label">
                        <i class="fas fa-id-card me-2"></i>Full Name
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-signature"></i>
                            </span>
                        <input type="text"
                               class="form-control"
                               id="fullName"
                               name="fullName"
                               value="${user.fullName}"
                               required>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="email" class="form-label">
                        <i class="fas fa-envelope me-2"></i>Email
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-at"></i>
                            </span>
                        <input type="email"
                               class="form-control"
                               id="email"
                               name="email"
                               value="${user.email}"
                               required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save me-2"></i>Update Profile
                </button>
            </form>
        </div>
    </div>

    <!-- Change Password Card -->
    <div class="card">
        <div class="card-header">
            <i class="fas fa-lock"></i>Change Password
        </div>
        <div class="card-body">
            <form action="profile" method="post">
                <input type="hidden" name="action" value="changePassword">

                <div class="mb-4">
                    <label for="currentPassword" class="form-label">
                        <i class="fas fa-key me-2"></i>Current Password
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-lock"></i>
                            </span>
                        <input type="password"
                               class="form-control"
                               id="currentPassword"
                               name="currentPassword"
                               required>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="newPassword" class="form-label">
                        <i class="fas fa-key me-2"></i>New Password
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-lock-open"></i>
                            </span>
                        <input type="password"
                               class="form-control"
                               id="newPassword"
                               name="newPassword"
                               required
                               minlength="4"
                               placeholder="Minimum 4 characters">
                    </div>
                </div>

                <div class="mb-4">
                    <label for="confirmPassword" class="form-label">
                        <i class="fas fa-key me-2"></i>Confirm New Password
                    </label>
                    <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-check-double"></i>
                            </span>
                        <input type="password"
                               class="form-control"
                               id="confirmPassword"
                               name="confirmPassword"
                               required
                               minlength="4">
                    </div>
                </div>

                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-shield-alt me-2"></i>Change Password
                </button>
            </form>
        </div>
    </div>

    <!-- Back Link -->
    <div class="back-link">
        <c:choose>
            <c:when test="${user.roleID == 1}">
                <a href="${pageContext.request.contextPath}/studentHomePage.jsp">
                    <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                </a>
            </c:when>
            <c:when test="${user.roleID == 2}">
                <a href="${pageContext.request.contextPath}/supervisor/dashboard">
                    <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                </a>
            </c:when>
            <c:when test="${user.roleID == 3}">
                <a href="${pageContext.request.contextPath}/supervisor/dashboard">
                    <i class="fas fa-arrow-left me-2"></i>Back to Dashboard
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp">
                    <i class="fas fa-arrow-left me-2"></i>Back to Login
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>