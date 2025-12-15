<%-- 
    Document   : my_team_lít
    Created on : Dec 14, 2025, 10:29:17 AM
    Author     : Admin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <style>
            /* 1. Đặt font chữ hiện đại hơn và tăng kích thước chữ */
            .table {
                font-family: 'Arial', sans-serif;
                font-size: 14px;
                width: 100%;
                margin-top: 20px; /* Thêm khoảng cách phía trên bảng */
                border-collapse: collapse; /* Đảm bảo các đường kẻ không bị kép */
            }

            /* 2. Cải thiện Header */
            .table thead th {
                background-color: #3f51b5; /* Màu nền xanh tím đậm cho header */
                color: #ffffff; /* Chữ trắng */
                font-weight: 600; /* Làm chữ đậm vừa phải */
                padding: 12px 15px; /* Tăng khoảng cách đệm */
                text-align: left;
                border-bottom: 2px solid #283593; /* Đường kẻ dưới đậm hơn */
            }

            /* 3. Cải thiện Body (Các dòng dữ liệu) */
            .table tbody tr td {
                padding: 10px 15px; /* Khoảng cách đệm thoải mái */
                vertical-align: middle;
                border-color: #e0e0e0; /* Đường kẻ mờ hơn */
            }

            /* Nếu dùng table-striped, làm cho dòng xen kẽ rõ ràng hơn */
            .table-striped tbody tr:nth-of-type(odd) {
                background-color: #f9f9f9;
            }

            /* 4. Hiệu ứng Hover (Làm nổi bật dòng khi di chuột) */
            .table tbody tr:hover {
                background-color: #e3f2fd !important; /* Màu xanh nhạt khi di chuột */
                cursor: default; /* Thêm con trỏ mặc định */
            }

            /* 5. Định dạng cột Tên Team */
            .table tbody tr td strong {
                color: #007bff; /* Làm nổi bật tên team */
            }

            /* 6. Định dạng cột ID và Ngày Tạo (cho nhỏ hơn chút) */
            .table tbody tr td:first-child, /* Cột TeamID */
            .table tbody tr td:last-child { /* Cột Ngày tạo */
                font-size: 13px;
                color: #616161;
            }
        </style>

    </head>
    <body>
        <h1>Hello World!</h1>
        <%@ include file="layout_header.jsp" %>
        <h1>
            danh sách các ĐỘi của tôi
        </h1>
        trả về danh sách các team của mình - tức các team mà team member có mình
        <c:if test="${not empty requestScope.myTeamList}">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>ID Team</th>
                        <th>Tên Team</th>
                        <th>Mô tả</th>
                        <th>Người tạo</th>
                        <th>Ngày tạo</th>
                        <th>action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="team" items="${requestScope.myTeamList}">
                        <tr>
                            <td>${team.teamID}</td>
                            <td><strong>${team.teamName}</strong></td>
                            <td>${team.description}</td>
                            <td>${team.createdBy}</td>
                            <td>${team.createdAt}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/nghia/TeamDetail" method="GET" id="form-${team.teamID}">
                                    <!--form ẩn, khi người dùng ấn vào thì nó gửi 1 param sang cho servlet.-->
                                    <input type="hidden" name="teamId" value="${team.teamID}">
                                    <button type="submit" class="btn btn-info btn-sm">
                                        <i class="fas fa-search"></i> Xem Detail
                                    </button>
                                </form>
                            </td>
                        </tr>

                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty requestScope.myTeamList}">
            <p>Bạn hiện chưa là thành viên của Team nào.</p>
        </c:if>
        <%@ include file="layout_footer.jsp" %>

    </body>
</html>
