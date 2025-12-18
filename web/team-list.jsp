
<jsp:include page="/nghiapages/layout_header.jsp" />

<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, entity.Team"%>
<%@page import="entity.UserAccount"%>
<%@page import="java.time.format.DateTimeFormatter"%>

<%
    List<Team> teams = (List<Team>) request.getAttribute("teams");
    Map<Integer, List<String>> projectCodesByTeam
            = (Map<Integer, List<String>>) request.getAttribute("projectCodesByTeam");
    Map<Integer, List<UserAccount>> membersByTeam
            = (Map<Integer, List<UserAccount>>) request.getAttribute("membersByTeam");

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
%>



<style>
    /* IMPORTANT: do NOT style body/html here, layout_header already controls the page */
    .page-wrapper {
        max-width: 1100px;
        margin: 30px auto;
        background-color: #ffffff;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 24px 28px 40px;
        box-sizing: border-box;
    }

    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
        color: blue;
    }

    .page-header h1 {
        margin: 0;
        font-size: 26px;
        font-weight: 600;
    }

    .btn-primary {
        padding: 10px 18px;
        border-radius: 20px;
        border: 1px solid #333;
        background-color: #fff;
        cursor: pointer;
        font-size: 14px;
    }

    .btn-primary:hover {
        background-color: #f0f0f0;
    }

    .search-section {
        margin-bottom: 24px;
    }

    .search-label {
        display: block;
        font-size: 13px;
        margin-bottom: 4px;
    }

    .search-input {
        width: 260px;
        padding: 8px 10px;
        border-radius: 6px;
        border: 1px solid #aaa;
        font-size: 14px;
    }

    .table-wrapper {
        border: 1px solid #ccc;
        border-radius: 6px;
        padding: 12px 12px 20px;
        background-color: #fff;
    }

    table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }

    th, td {
        padding: 10px 8px;
        text-align: left;
        border-bottom: 1px solid #eee;
        vertical-align: middle;
    }

    th {
        text-transform: lowercase;
        font-weight: 600;
    }

    .action-btn {
        padding: 6px 14px;
        border-radius: 16px;
        border: 1px solid #333;
        background-color: #fff;
        cursor: pointer;
        font-size: 13px;
        text-decoration: none;
        display: inline-block;
    }

    .action-btn:hover {
        background-color: #f3f3f3;
    }

    .back-link {
        margin-top: 18px;
        display: inline-block;
        font-size: 13px;
    }

    .member-avatars {
        display: flex;
        align-items: center;
        gap: 4px;
    }

    .avatar-circle {
        width: 26px;
        height: 26px;
        border-radius: 50%;
        background-color: #4c6fff;
        color: #fff;
        font-size: 11px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
    }

    .avatar-more {
        font-size: 11px;
        color: #555;
    }
</style>
<div class="container-fluid">
    <div class="page-wrapper">

        <div class="page-header">
            <h1>Team</h1>

            <!-- Your update: link direct to /FlowSheet/CreateTeam.jsp -->
            <form method="get" action="<%= request.getContextPath()%>/CreateTeam.jsp">
                <a class="btn-create-team"
                   href="${pageContext.request.contextPath}/team/create">
                    Create Team
                </a>
            </form>
        </div>

        <div class="search-section">
            <form method="get" action="<%= request.getContextPath()%>/team">
                <label class="search-label">Search team</label>
                <input type="text"
                       class="search-input"
                       name="q"
                       value="<%= (request.getAttribute("q") != null ? request.getAttribute("q") : "")%>"
                       placeholder="Enter team or member name">
                <button type="submit" class="btn-primary" style="margin-left:8px;">Search</button>
            </form>
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>team name</th>
                        <th>projects</th>
                        <th>members</th>
                        <th>create on</th>
                        <th>action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (teams != null) {
                            for (Team t : teams) {
                    %>
                    <tr>
                        <td><%= t.getTeamName()%></td>

                        <td>
                            <%
                                List<String> codes = (projectCodesByTeam != null)
                                        ? projectCodesByTeam.get(t.getTeamID())
                                        : null;
                                if (codes != null && !codes.isEmpty()) {
                            %>
                            <%= String.join(", ", codes)%>
                            <%
                            } else {
                            %>
                            N/A
                            <%
                                }
                            %>
                        </td>

                        <td>
                            <div class="member-avatars">
                                <%
                                    List<UserAccount> members = (membersByTeam != null)
                                            ? membersByTeam.get(t.getTeamID())
                                            : null;

                                    if (members != null && !members.isEmpty()) {
                                        int count = 0;
                                        for (UserAccount ua : members) {
                                            if (count >= 3) {
                                                break;
                                            }

                                            String uname = ua.getUsername();
                                            String initials = "";
                                            if (uname != null && !uname.isEmpty()) {
                                                initials = uname.substring(0, Math.min(2, uname.length())).toUpperCase();
                                            }
                                %>
                                <span class="avatar-circle"><%= initials%></span>
                                <%
                                        count++;
                                    }
                                    if (members.size() > 3) {
                                %>
                                <span class="avatar-more">+<%= members.size() - 3%></span>
                                <%
                                    }
                                } else {
                                %>
                                N/A
                                <%
                                    }
                                %>
                            </div>
                        </td>

                        <td><%= t.getCreatedAt()%></td>

                        <td>
                            <a class="action-btn"
                               href="<%= request.getContextPath()%>/teamMember?teamId=<%= t.getTeamID()%>">
                                View
                            </a>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>

        <a class="back-link" href="<%= request.getContextPath()%>/nghiapages/my_all_task.jsp">Back to main</a>
    </div>
</div>
<jsp:include page="/nghiapages/layout_footer.jsp" />
