<%-- 
    Document   : layout_header.jsp
    Created on : Dec 14, 2025, 9:24:38 AM
    Author     : Admin
--%>

<%-- layout_header.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin Dashboard Layout (Header & Sidebar Toggle)</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

        <style>
            /* =======================================
               ĐỊNH NGHĨA CHUNG & BIẾN CSS
               ======================================= */
            :root {
                --header-height: 50px;
                --sidebar-width: 230px;
                --main-bg-color: #3c8dbc;
                --dark-bg-color: #367fa9;
                --text-color: #fff;
                --sidebar-bg: #222d32;
                --sidebar-text: #b8c7ce;
                --notification-color: #f56954;
            }

            body {
                font-family: 'Arial', sans-serif;
                margin: 0;
                background-color: #f4f6f9;
            }

            /* Đảm bảo Body và HTML chiếm toàn bộ chiều cao màn hình */
            html, body {
                height: 100%;
            }

            /* =======================================
               HEADER (Thanh điều hướng trên cùng)
               ======================================= */
            .main-header {
                position: fixed; /* Header cố định ở trên cùng */
                width: 100%;
                top: 0;
                left: 0;
                background-color: var(--main-bg-color);
                color: var(--text-color);
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                z-index: 1040; /* Cao hơn Sidebar */
            }

            .navbar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                height: var(--header-height);
                padding: 0 15px;
            }

            /* --- Cột Trái: Logo và Toggle --- */
            .navbar-left {
                display: flex;
                align-items: center;
            }

            .logo {
                background-color: var(--dark-bg-color);
                color: var(--text-color);
                padding: 0 15px;
                height: var(--header-height);
                line-height: var(--header-height);
                font-size: 18px;
                font-weight: bold;
                text-decoration: none;
                text-align: center;
            }

            .sidebar-toggle {
                background: none;
                border: none;
                color: var(--text-color);
                font-size: 20px;
                cursor: pointer;
                padding: 0 15px;
                height: var(--header-height);
                line-height: var(--header-height);
                margin-left: 15px;
                transition: background-color 0.3s;
            }

            .sidebar-toggle:hover {
                background-color: var(--dark-bg-color);
            }

            /* --- Cột Phải: Icons và User --- */
            .navbar-right {
                display: flex;
                align-items: center;
                list-style: none;
                margin: 0;
                padding: 0;
            }

            .nav-item {
                position: relative;
                cursor: pointer;
            }

            .nav-link {
                display: block;
                height: var(--header-height);
                line-height: var(--header-height);
                color: var(--text-color);
                text-decoration: none;
                padding: 0 15px;
                transition: background-color 0.3s;
            }

            .nav-link:hover {
                background-color: var(--dark-bg-color);
            }

            .badge {
                position: absolute;
                top: 5px;
                right: 5px;
                padding: 2px 5px;
                font-size: 10px;
                line-height: 1;
                color: var(--text-color);
                background-color: var(--notification-color);
                border-radius: 50%;
                min-width: 18px;
                text-align: center;
            }

            .user-menu {
                display: flex;
                align-items: center;
                padding: 0 15px 0 20px;
            }

            .user-icon {
                font-size: 20px;
                margin-right: 5px;
            }

            /* =======================================
               SIDEBAR (Thanh bên trái)
               ======================================= */
            .main-sidebar {
                position: fixed;
                top: var(--header-height); /* Bắt đầu ngay dưới Header */
                left: 0;
                width: var(--sidebar-width);
                height: calc(100% - var(--header-height));
                background-color: var(--sidebar-bg);
                color: var(--sidebar-text);
                z-index: 1030;
                overflow-y: auto;
                /* Key cho hiệu ứng trượt */
                transition: margin-left 0.3s ease-in-out;
            }

            .sidebar-content ul {
                list-style: none;
                padding: 20px 0;
                margin: 0;
            }

            .sidebar-content ul li a {
                display: block;
                padding: 10px 15px;
                color: var(--sidebar-text);
                text-decoration: none;
                font-size: 14px;
            }

            .sidebar-content ul li a:hover {
                background-color: #1e282c;
                color: #fff;
            }

            .sidebar-content ul li a i {
                margin-right: 10px;
                width: 20px;
            }

            /* =======================================
               CONTENT WRAPPER (Nội dung chính)
               ======================================= */
            .content-wrapper {
                /* Mặc định, Nội dung bắt đầu sau Sidebar */
                margin-left: var(--sidebar-width);
                padding-top: var(--header-height); /* Đẩy nội dung xuống dưới Header */
                transition: margin-left 0.3s ease-in-out;
                min-height: 100vh;
                background-color: #f4f6f9;
            }

            .content {
                padding: 15px;
            }

            /* =======================================
               HIỆU ỨNG SIDEBAR COLLAPSE (THỤT VÀO)
               ======================================= */
            /* Class này được thêm vào thẻ <body> bằng JavaScript */
            .sidebar-collapse .main-sidebar {
                /* Ẩn Sidebar bằng cách đẩy ra ngoài */
                margin-left: calc(-1 * var(--sidebar-width));
            }

            .sidebar-collapse .content-wrapper {
                /* Mở rộng nội dung ra toàn màn hình */
                margin-left: 0;
            }

            /* <>   UPDATE CHO TASK REVIEW    */
            .has-submenu .submenu {
                list-style: none;
                margin: 6px 0 0 0;
                padding-left: 18px;
                display: none;
            }

            .has-submenu.open .submenu {
                display: block;
            }

            .submenu-toggle {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 10px;
            }

            .has-submenu .caret {
                font-size: 12px;
                opacity: 0.85;
            }

            .has-submenu.open .caret {
                transform: rotate(180deg);
            }
            /* </>  UPDATE CHO TASK REVIEW    */


        </style>
    </head>
    <body class="sidebar-collapse"> <header class="main-header">

            <c:if test="${empty sessionScope.user}">
                <%-- 
                    pageContext.request.contextPath sẽ trả về đường dẫn gốc của ứng dụng (ví dụ: /FlowSheet)
                    và nối với /login.jsp
                --%>
                <c:redirect url="${pageContext.request.contextPath}/login.jsp" />
            </c:if>

            <nav class="navbar">
                <div class="navbar-left">
                    <a href="#" class="logo">Flowsheet</a>

                    <button type="button" class="sidebar-toggle" id="sidebar-toggle-btn" title="Toggle Sidebar">
                        <i class="fas fa-bars"></i>
                    </button>
                </div>

                <ul class="navbar-right">
                    <li class="nav-item">
                        <a href="#" class="nav-link"><i class="far fa-envelope"></i> <span class="badge">1</span></a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link"><i class="far fa-bell"></i> <span class="badge">2</span></a>
                    </li>
                    <li class="nav-item user-menu">
                        <a href="#" class="nav-link" style="padding: 0 10px;">
                            <i class="far fa-user-circle user-icon"></i> 
                            <span><span>${sessionScope.user.fullName}</span></span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="#" class="nav-link" title="Sign out"><i class="fas fa-power-off"></i></a>
                    </li>
                </ul>
            </nav>
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    document.querySelectorAll(".submenu-toggle").forEach(function (btn) {
                        btn.addEventListener("click", function (e) {
                            e.preventDefault();
                            const li = btn.closest(".has-submenu");
                            if (li)
                                li.classList.toggle("open");
                        });
                    });
                });
            </script>
        </header>

        <aside class="main-sidebar" id="sidebar">
            <div class="sidebar-content">
                <ul>   
                    <li><a href="${pageContext.request.contextPath}/nghiapages/my_project_list.jsp"><i class="fas fa-graduation-cap"></i>My Project</a></li>
                    <li><a href="${pageContext.request.contextPath}/MyTeamList"><i class="fas fa-users"></i>My Teams</a></li>
                    <li><a href="${pageContext.request.contextPath}/student/tasks"><i class="fas fa-shield-alt"></i> My tasks</a></li>
                    <li><a href="${pageContext.request.contextPath}/nghiapages/my_timesheet.jsp"><i class="fas fa-list-ol"></i> My Timesheet</a></li>

                    <li style="margin-top: 15px;"><a href="#"><i class="fas fa-home"></i>HomePage</a></li>
                    <li><a href="${pageContext.request.contextPath}/team"><i class="fas fa-users"></i> All Teams</a></li>
                    <c:if test="${not empty sessionScope.user && sessionScope.user.roleID == 2}">
                        <li>
                            <a href="${pageContext.request.contextPath}/task-review?action=list">
                                <i class="fas fa-list-ol"></i>
                                Task Review
                            </a>
                        </li>
                    </c:if>
                    <li><a href="${pageContext.request.contextPath}/task-report/list"><i class="fas fa-calendar-alt"></i>My Report</a></li>
                    <li><a href="#"><i class="fas fa-file-alt"></i> Private files</a></li>
                </ul>
            </div>
        </aside>

        <div class="content-wrapper" id="content-wrapper">
            <section class="content">
