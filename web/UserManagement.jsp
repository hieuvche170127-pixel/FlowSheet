<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin - Quản lý người dùng</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <style>
            body {
                background-color: #f8f9fa;
            }
            .table-action-btn {
                margin: 0 2px;
            }
            .status-badge {
                font-size: 0.85em;
                padding: 5px 10px;
                border-radius: 20px;
            }
        </style>
    </head>
    <body>

        <div class="container mt-5">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="text-primary fw-bold"><i class="fas fa-users-cog"></i> Quản Lý Người Dùng</h2>
                <a href="admin/dashboard" class="btn btn-outline-secondary"><i class="fas fa-arrow-left"></i> Quay lại Dashboard</a>
            </div>

            <div class="card shadow-sm mb-4">
                <div class="card-body bg-white">
                    <form action="users" method="GET" class="row g-3 align-items-center">

                        <div class="col-md-5">
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fas fa-search text-muted"></i></span>
                                <input type="text" name="search" class="form-control" 
                                       value="${param.search}" placeholder="Nhập tên hoặc email để tìm...">
                            </div>
                        </div>

                        <div class="col-md-3">
                            <select name="roleFilter" class="form-select" onchange="this.form.submit()">
                                <option value="" ${empty param.roleFilter ? 'selected' : ''}>-- Tất cả vai trò --</option>
                                <option value="1" ${param.roleFilter == '1' ? 'selected' : ''}>Sinh viên (Student)</option>
                                <option value="2" ${param.roleFilter == '2' ? 'selected' : ''}>Giảng viên (Supervisor)</option>
                            </select>
                        </div>

                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100">Tìm kiếm</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card shadow">
                <div class="card-body p-0">
                    <table class="table table-hover table-striped align-middle mb-0">
                        <thead class="table-dark text-center">
                            <tr>
                                <th>ID</th>
                                <th class="text-start">Họ và Tên</th>
                                <th class="text-start">Email</th>
                                <th>SĐT</th>
                                <th>Vai trò</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody id="userTableBody">
                            <c:forEach items="${listUsers}" var="u" varStatus="loop">
                                <tr class="text-center">

                                    <td class="fw-bold text-secondary">
                                        ${(currentPage - 1) * 20 + loop.count}
                                    </td>

                                    <td class="text-start fw-bold text-primary">${u.fullName}</td>
                                    <td class="text-start">
                                        <c:choose>
                                            <c:when test="${not empty u.email}">${u.email}</c:when>
                                            <c:otherwise><span class="text-danger fst-italic">Đã xóa email</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${u.phone}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.roleId == 1}"><span class="badge bg-info text-dark">Student</span></c:when>
                                            <c:when test="${u.roleId == 2}"><span class="badge bg-warning text-dark">Supervisor</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.isActive}">
                                                <span class="badge bg-success status-badge"><i class="fas fa-check-circle"></i> Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary status-badge"><i class="fas fa-minus-circle"></i> Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${u.isActive}">
                                                <button class="btn btn-sm btn-outline-warning table-action-btn" 
                                                        onclick="submitAction('deactivate', ${u.userId}, '${u.fullName}')" 
                                                        title="Khóa tài khoản">
                                                    <i class="fas fa-user-lock"></i>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="btn btn-sm btn-outline-success table-action-btn" 
                                                        onclick="submitAction('activate', ${u.userId}, '${u.fullName}')" 
                                                        title="Mở khóa tài khoản">
                                                    <i class="fas fa-user-check"></i>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>

                                        <button class="btn btn-sm btn-outline-danger table-action-btn" 
                                                onclick="submitAction('delete', ${u.userId}, '${u.fullName}')"
                                                ${empty u.email ? 'disabled' : ''} 
                                                title="Xóa Email">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
                    <div class="text-muted small">
                        Hiển thị trang <strong>${currentPage}</strong> trên tổng số <strong>${totalPages}</strong>
                    </div>

                    <nav aria-label="Page navigation">
                        <ul class="pagination mb-0">
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="users?page=${currentPage - 1}&search=${param.search}&roleFilter=${param.roleFilter}">
                                        <i class="fas fa-chevron-left"></i>
                                    </a>
                                </li>
                            </c:if>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="users?page=${i}&search=${param.search}&roleFilter=${param.roleFilter}">${i}</a>
                                </li>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <a class="page-link" href="users?page=${currentPage + 1}&search=${param.search}&roleFilter=${param.roleFilter}">
                                        <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>

        <script>
            function submitAction(actionType, userId, userName) {
                let msg = "";
                let confirmBtnColor = "";

                if (actionType === 'deactivate') {
                    msg = "Bạn có chắc muốn KHÓA tài khoản của [" + userName + "] không?";
                } else if (actionType === 'activate') {
                    msg = "Bạn có chắc muốn KÍCH HOẠT lại tài khoản của [" + userName + "] không?";
                } else if (actionType === 'delete') {
                    msg = "CẢNH BÁO: Bạn sắp XÓA EMAIL của [" + userName + "].\nHành động này không thể hoàn tác!\nBạn có chắc chắn không?";
                }

                if (confirm(msg)) {
                    // Tạo form ẩn để gửi POST request về Servlet
                    let form = document.createElement("form");
                    form.method = "POST";
                    form.action = "users"; // Mapping URL của Servlet

                    // Input Action
                    let inputAction = document.createElement("input");
                    inputAction.type = "hidden";
                    inputAction.name = "action";
                    inputAction.value = actionType;

                    // Input UserID
                    let inputId = document.createElement("input");
                    inputId.type = "hidden";
                    inputId.name = "userId";
                    inputId.value = userId;

                    form.appendChild(inputAction);
                    form.appendChild(inputId);

                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>