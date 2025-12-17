
<%@page import="java.lang.Integer"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, entity.TeamMember, entity.User, entity.Team, entity.Project" %>

<%
    //List<TeamMember> teamMembers = (List<TeamMember>) request.getAttribute("teamMembers");
    Team team = (Team) request.getAttribute("team");
    List<User> userAccounts = (List<User>) request.getAttribute("userAccounts");
    List<Project> projects = (List<Project>) request.getAttribute("projects");
    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) {
        activeTab = "members";
    }
    Map<Integer, String> roleMap = (Map<Integer, String>) request.getAttribute("roleMap");

%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Team Member Page</title>
        <style>
            body {
                margin: 0;
                font-family: Arial, sans-serif;
                background-color: #f5f7fb;
            }

            .page-wrapper {
                max-width: 1200px;
                margin: 20px auto;
                background-color: #ffffff;
                border-radius: 12px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
                padding: 20px 24px 32px;
                box-sizing: border-box;
            }

            .top-bar {
                display: flex;
                align-items: center;
                margin-bottom: 18px;
                gap: 10px;
            }

            .back-link {
                display: inline-flex;
                align-items: center;
                text-decoration: none;
                color: #333;
                font-size: 14px;
            }

            .back-arrow {
                display: inline-block;
                width: 26px;
                height: 26px;
                border-radius: 50%;
                border: 1px solid #ccc;
                text-align: center;
                line-height: 26px;
                margin-right: 6px;
                font-weight: bold;
            }

            .team-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 14px 0 10px;
                border-bottom: 1px solid #e1e4f0;
                margin-bottom: 14px;
            }

            .team-info-left {
                display: flex;
                align-items: center;
                gap: 14px;
            }

            .team-avatar {
                width: 44px;
                height: 44px;
                border-radius: 50%;
                background-color: #d4a015;
                color: #fff;
                font-weight: 600;
                font-size: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .team-name {
                font-size: 18px;
                font-weight: 600;
            }

            .team-actions-right {
                display: flex;
                gap: 10px;
            }

            .btn-outline {
                padding: 8px 16px;
                border-radius: 20px;
                border: 1px solid #ddd;
                background-color: #fff;
                font-size: 13px;
                cursor: pointer;
            }

            .btn-danger {
                border-color: #ff5a6b;
                color: #ff5a6b;
            }

            .tab-row {
                display: flex;
                align-items: center;
                margin-top: 14px;
                margin-bottom: 16px;
                gap: 8px;
            }

            .tab {
                padding: 8px 18px;
                border-radius: 20px;
                border: 1px solid #00bfa5;
                font-size: 13px;
                cursor: pointer;
                background-color: #fff;
                color: #00bfa5;
            }

            .tab.active {
                background-color: #00bfa5;
                color: #fff;
            }

            .search-wrapper {
                margin-left: auto;
                display: flex;
                align-items: center;
                gap: 6px;
            }

            .search-input {
                padding: 7px 10px;
                border-radius: 18px;
                border: 1px solid #ccd2e0;
                font-size: 13px;
                min-width: 230px;
            }

            .search-btn {
                width: 32px;
                height: 32px;
                border-radius: 50%;
                border: none;
                background-color: #00bfa5;
                color: #fff;
                font-size: 16px;
                cursor: pointer;
            }

            .cards-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
                gap: 16px;
                margin-top: 10px;
            }

            .member-card {
                background-color: #fff;
                border-radius: 12px;
                border: 1px solid #e0e4f0;
                padding: 18px 16px;
                text-align: center;
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
            }

            .member-avatar {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                margin: 0 auto 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 600;
                font-size: 18px;
                color: #fff;
                background-color: #4c6fff; /* you could randomize per user later */
            }

            .member-fullname {
                font-size: 13px;
                color: #555;
                margin-bottom: 2px;
            }

            .member-email {
                font-size: 12px;
                color: #888;
                margin-bottom: 8px;
            }

            .member-role {
                font-size: 12px;
                color: #00bfa5;
                font-weight: 600;
                margin-bottom: 10px;
            }

            .details-link {
                font-size: 13px;
                color: #00bfa5;
                text-decoration: none;
                font-weight: 600;
            }

            .details-link:hover {
                text-decoration: underline;
            }
        </style>

    </head>
    <body>
        <div class="page-wrapper">
            <!-- Back link -->
            <div class="top-bar">
                <a href="team" class="back-link">
                    <span class="back-arrow">&#8592;</span>
                    Back to all teams
                </a>
            </div>

            <!-- Fake team header for now (we can bind real team info later) -->
            <div class="team-header">
                <div class="team-info-left">
                    <div class="team-avatar">T</div>
                    <div>
                        <div class="team-name">
                            <%= (team != null && team.getTeamName() != null) ? team.getTeamName() : "Team"%>
                        </div>
                        <!-- later you can show Created On, etc. -->
                    </div>
                </div>
                <div class="team-actions-right">
                    <form method="post" action="teamMember" style="display:flex; gap:6px; align-items:center;">
                        <input type="hidden" name="action" value="updateTeam">
                        <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">

                        <input type="text" name="teamName"
                               value="<%= team.getTeamName()%>"
                               required
                               style="padding:6px 10px;border-radius:10px;border:1px solid #ccd2e0;">

                        <input type="text" name="description"
                               value="<%= team.getDescription() != null ? team.getDescription() : ""%>"
                               style="padding:6px 10px;border-radius:10px;border:1px solid #ccd2e0;">

                        <button class="btn-outline" type="submit">Update Team</button>
                    </form>

                    <form method="post" action="teamMember" onsubmit="return confirm('Are you sure you want to delete this team?');">
                        <input type="hidden" name="action" value="deleteTeam">
                        <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">
                        <button class="btn-outline btn-danger" type="submit">Delete Team</button>
                    </form>
                </div>
            </div>

            <!-- Tabs + Search -->
            <div class="tab-row">
                <a class="tab <%= "members".equals(activeTab) ? "active" : ""%>"
                   href="teamMember?teamId=<%= team.getTeamID()%>&tab=members">
                    Team Members
                </a>

                <a class="tab <%= "projects".equals(activeTab) ? "active" : ""%>"
                   href="teamMember?teamId=<%= team.getTeamID()%>&tab=projects">
                    Assigned Projects
                </a>

                <div class="search-wrapper">
                    <div class="search-wrapper">
                        <form method="get" action="teamMember" style="display:flex;align-items:center;gap:6px;">
                            <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">
                            <input type="hidden" name="tab" value="members">
                            <input type="text"
                                   class="search-input"
                                   name="q"
                                   value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : ""%>"
                                   placeholder="Search by member name..." />
                            <button class="search-btn" type="submit">&#128269;</button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Member cards -->
            <% if ("members".equals(activeTab)) { %>
            <div class="cards-grid">
                <%
                    if (userAccounts != null) {
                        for (User u : userAccounts) {

                            String userName = u.getUsername();
                            String fullName = u.getFullName();
                            String email = u.getEmail();
                            Integer roleId = u.getRoleId();
                            String roleName = roleMap != null ? roleMap.get(roleId) : null;

                            // simple initials: first 2 letters of username (fallback if null/short)
                            String initials = "";

                            if (userName != null && !userName.isEmpty()) {
                                initials = userName.substring(0, Math.min(2, userName.length())).toUpperCase();
                            }
                %>
                <div class="member-card">
                    <div class="member-avatar"><%= initials%></div>
                    <div class="member-username"><%= userName%></div>
                    <div class="member-fullname"><%= fullName%></div>
                    <div class="member-email"><%= email%></div>
                    <div class="member-role"><%= roleName != null ? roleName : ""%></div>

                    <form action="teamMember" method="post" style="margin-top:10px;">
                        <input type="hidden" name="action" value="changeRole"/>
                        <input type="hidden" name="teamId" value="<%= team.getTeamID()%>"/>
                        <input type="hidden" name="userId" value="<%= u.getUserId()%>"/>

                        <select name="roleId">
                            <option value="4" <%= (u.getRoleId() == 4) ? "selected" : ""%>>Team Member</option>
                            <option value="5" <%= (u.getRoleId() == 5) ? "selected" : ""%>>Team Leader</option>
                        </select>
                        <button type="submit" class="btn-outline">Change role</button>
                    </form>

                    <form action="teamMember" method="post" onsubmit="return confirm('Kick this member out of the team?');">
                        <input type="hidden" name="action" value="kick"/>
                        <input type="hidden" name="teamId" value="<%= team.getTeamID()%>"/>
                        <input type="hidden" name="userId" value="<%= u.getUserId()%>"/>
                        <button type="submit" class="btn-outline btn-danger">Kick Team Member</button>
                    </form>

                </div>
                <%
                        }
                    }
                %>


            </div>
            <% } %>   <!-- CLOSE members tab -->

            <!-- Project cards -->
            <% if ("projects".equals(activeTab)) { %>
            <div class="cards-grid">
                <%
                    if (projects != null) {
                        for (Project p : projects) {
                            String code = p.getProjectCode();
                            String name = p.getProjectName();
                            String desc = p.getDescription();
                            boolean active = p.isIsActive();
                            String initials = "";
                            if (code != null && !code.isEmpty()) {
                                initials = code.substring(0, Math.min(2, code.length())).toUpperCase();
                            }
                %>
                <div class="member-card">
                    <div class="member-avatar"><%= initials%></div>
                    <div class="member-username"><%= code%></div>
                    <div class="member-fullname"><%= name%></div>
                    <div class="member-email"><%= desc%></div>
                    <div class="member-role"><%= active ? "Active" : "Inactive"%></div>
                </div>
                <%
                        }
                    }
                %>
            </div>
            <% }%>   <!-- CLOSE projects tab -->


        </div>
    </body>
</html>
