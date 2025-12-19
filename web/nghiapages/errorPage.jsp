<%-- 
    Document   : errorPage
    Created on : Dec 18, 2025, 10:35:15 PM
    Author     : Admin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 

    </head>
    <%@ include file="/nghiapages/layout_header.jsp" %>
    <c:if test="${not empty sessionScope.sessionError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lỗi!</strong> ${sessionScope.sessionError}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <!-- Xóa ngay attribute này khỏi session để F5 không hiện lại -->
        <c:remove var="sessionError" scope="session" />
    </c:if>
    <h1>Trang loi</h1>
    <%@ include file="/nghiapages/layout_footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>


</html>
