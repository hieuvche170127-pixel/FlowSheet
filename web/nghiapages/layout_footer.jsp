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
    document.addEventListener('DOMContentLoaded', () => {
        const btn = document.getElementById('sidebar-toggle-btn');
        if (!btn)
            return;

        btn.addEventListener('click', function () {
            const body = document.body;

            if (body.classList.contains('sidebar-collapse')) {
                body.classList.remove('sidebar-collapse');
                localStorage.setItem('sidebarState', 'expanded');
            } else {
                body.classList.add('sidebar-collapse');
                localStorage.setItem('sidebarState', 'collapsed');
            }
        });

        const savedState = localStorage.getItem('sidebarState');
        document.body.classList.remove('sidebar-collapse');
        if (savedState === 'collapsed')
            document.body.classList.add('sidebar-collapse');
    });
</script>
</body>
</html>