<%-- 
    Document   : layout_footer.jsp
    Created on : Dec 14, 2025, 9:26:14 AM
    Author     : Admin
--%>

<%-- layout_footer.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


</section>
</div>
<script>
    document.getElementById('sidebar-toggle-btn').addEventListener('click', function () {
        // Thao tác với thẻ <body> để thêm/xóa class 'sidebar-collapse'
        const body = document.body;

        // Toggle class 'sidebar-collapse'
        if (body.classList.contains('sidebar-collapse')) {
            body.classList.remove('sidebar-collapse');
            // Lưu trạng thái vào localStorage (Tùy chọn)
            localStorage.setItem('sidebarState', 'expanded');
        } else {
            body.classList.add('sidebar-collapse');
            localStorage.setItem('sidebarState', 'collapsed');
        }
    });

    // Tải trạng thái Sidebar khi trang được load (giúp giữ trạng thái khi F5)
    document.addEventListener('DOMContentLoaded', (event) => {
        const savedState = localStorage.getItem('sidebarState');

        // Xóa class mặc định nếu có và áp dụng trạng thái đã lưu
        document.body.classList.remove('sidebar-collapse');

        if (savedState === 'collapsed') {
            document.body.classList.add('sidebar-collapse');
        } else if (!savedState || savedState === 'expanded') {
            // Mặc định mở nếu không có trạng thái lưu hoặc là 'expanded'
            // Không làm gì cả vì body đã không có class 'sidebar-collapse'
        }
    });
</script>
</body>
</html>