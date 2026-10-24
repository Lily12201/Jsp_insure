<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 没登录就踢回登录页 --%>
<c:if test="${empty sessionScope.adminUser}">
    <c:redirect url="/admin_login.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>管理者管理</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; flex: 1; }
    .my-row { background: #fff8e1; }
</style>

<!-- 三行顺序不能反：先 jQuery，再 ctx，最后本页的 admin_account.js -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_account.js"></script>
</head>
<body>
<div class="d-flex">
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list" class="on">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <div class="content">
        <h3>
            管理者管理
            <%-- ★ 权限徽章：看一眼就知道自己是什么角色 --%>
            <c:if test="${sessionScope.adminUser.admin_role == 1}">
                <span class="badge badge-danger">総管理者</span>
            </c:if>
            <c:if test="${sessionScope.adminUser.admin_role != 1}">
                <span class="badge badge-secondary">一般管理者</span>
            </c:if>
        </h3>

        <%-- 操作提示（Servlet 里存了 msgText 和 msgType 两个值）
             ★ c:remove：显示完马上从 session 里删掉。
               不删的话，以后每次刷新这个页面这行提示都会再冒出来一次。 --%>
        <c:if test="${not empty msgText}">
            <div class="alert alert-${msgType}">${msgText}</div>
            <c:remove var="msgText" scope="session"/>
            <c:remove var="msgType" scope="session"/>
        </c:if>

        <!-- ===== 1、改「自己」的密码（所有人都能用，包括总管理员）===== -->
        <div class="card mb-4 border-primary">
            <div class="card-header bg-primary text-white">
                <b>パスワード変更（自分のアカウント）</b>
            </div>
            <div class="card-body">
                <div class="form-inline">
                    <input type="password" id="oldPassword" class="form-control mr-2 mb-2"
                           placeholder="現在のパスワード">
                    <input type="password" id="newPassword" class="form-control mr-2 mb-2"
                           placeholder="新しいパスワード（6文字以上）">
                    <input type="password" id="newPassword2" class="form-control mr-2 mb-2"
                           placeholder="新しいパスワード（確認）">
                    <button type="button" id="btnChangePwd" class="btn btn-primary mb-2">変更する</button>
                </div>
                <small class="text-muted">
                    ※ パスワードが漏れた時は、ここから自分で変更できます。
                    現在のパスワードが分からない場合は、総管理者に「パスワード再設定」を頼んでください。
                </small>
                <div id="pwdMsg" class="mt-2"></div>
            </div>
        </div>

        <!-- ===== 2、新增管理员（只有总管理员能看到）===== -->
        <c:if test="${sessionScope.adminUser.admin_role == 1}">
            <div class="card mb-4">
                <div class="card-header"><b>管理者を追加</b></div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/AdminAccountServlet" method="post"
                          class="form-inline">
                        <input type="hidden" name="opt" value="admin_account_add">
                        <input type="text" name="account" class="form-control mr-2"
                               placeholder="アカウント（3～20文字）" required>
                        <input type="text" name="password" class="form-control mr-2"
                               placeholder="パスワード（6文字以上）" required>
                        <input type="text" name="name" class="form-control mr-2"
                               placeholder="氏名（省略可）">
                        <%-- ★ 新增时可以选「总管理员」还是「一般管理者」 --%>
                        <select name="role" class="form-control mr-2">
                            <option value="2">一般管理者</option>
                            <option value="1">総管理者</option>
                        </select>
                        <button type="submit" class="btn btn-primary">追加</button>
                    </form>
                    <small class="text-muted">
                        ※ 追加したアカウントは「有効」状態で作成されます。
                        人が交代したら：新しい人を追加 → 新しい人がログイン確認 → 古い人を「無効」にする
                    </small>
                </div>
            </div>
        </c:if>

        <!-- ===== 3、管理员列表 ===== -->
        <table class="table table-bordered table-hover bg-white">
            <thead class="thead-dark">
                <tr>
                    <th>ID</th><th>アカウント</th><th>パスワード</th><th>氏名</th>
                    <th>権限</th><th>状態</th><th>作成日時</th><th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${accountList}" var="a">
                    <tr class="${a.admin_id == sessionScope.adminUser.admin_id ? 'my-row' : ''}">
                        <td>${a.admin_id}</td>
                        <td>
                            ${a.admin_account}
                            <c:if test="${a.admin_id == sessionScope.adminUser.admin_id}">
                                <span class="badge badge-warning">自分</span>
                            </c:if>
                        </td>
                        <td>******</td>
                        <td>${a.admin_name}</td>
                        <td>
                            <c:if test="${a.admin_role == 1}">
                                <span class="badge badge-danger">総管理者</span>
                            </c:if>
                            <c:if test="${a.admin_role != 1}">
                                <span class="badge badge-secondary">一般管理者</span>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${a.admin_status == 1}">
                                <span class="badge badge-success">有効</span>
                            </c:if>
                            <c:if test="${a.admin_status != 1}">
                                <span class="badge badge-secondary">無効</span>
                            </c:if>
                        </td>
                        <td>${a.created_at}</td>
                        <td>

                            <%-- A、对「别人」：只有总管理员才能操作 --%>
                            <c:if test="${sessionScope.adminUser.admin_role == 1
                                          && a.admin_id != sessionScope.adminUser.admin_id}">

                                <!-- 启用 / 禁用 -->
                                <c:choose>
                                    <c:when test="${a.admin_status == 1}">
                                        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_status&adminId=${a.admin_id}&status=0"
                                           class="btn btn-sm btn-warning btn-status">無効にする</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_status&adminId=${a.admin_id}&status=1"
                                           class="btn btn-sm btn-info btn-status">有効にする</a>
                                    </c:otherwise>
                                </c:choose>

                                <!-- 升降级 -->
                                <c:choose>
                                    <c:when test="${a.admin_role == 1}">
                                        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_role&adminId=${a.admin_id}&role=2"
                                           class="btn btn-sm btn-outline-secondary btn-role">一般に下げる</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_role&adminId=${a.admin_id}&role=1"
                                           class="btn btn-sm btn-outline-danger btn-role">総管理に上げる</a>
                                    </c:otherwise>
                                </c:choose>

                                <!-- 重设密码：处理函数在 admin_account.js 第 3 项（.btn-resetpwd） -->
                                <button type="button" class="btn btn-sm btn-secondary btn-resetpwd"
                                        data-id="${a.admin_id}" data-name="${a.admin_account}">パスワード再設定</button>

                                <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_delete&adminId=${a.admin_id}"
                                   class="btn btn-sm btn-danger btn-del">削除</a>
                            </c:if>

                            <%-- B、对「自己」：什么都不给点，只提示去上面的表单改密码 --%>
                            <c:if test="${a.admin_id == sessionScope.adminUser.admin_id}">
                                <span class="text-muted small">（上のフォームから変更）</span>
                            </c:if>

                            <%-- C、普通管理员看别人：理论上不会出现（列表只返回自己），留个说明防万一 --%>
                            <c:if test="${sessionScope.adminUser.admin_role != 1
                                          && a.admin_id != sessionScope.adminUser.admin_id}">
                                <span class="text-muted small">権限なし</span>
                            </c:if>

                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty accountList}">
                    <tr><td colspan="8" class="text-center text-muted">データがありません</td></tr>
                </c:if>
            </tbody>
        </table>

        <p class="text-muted small">
            ※ 自分自身のアカウントは無効・削除・権限変更できません（誤って締め出すのを防ぐため）。<br>
            ※ 総管理者は最低1人必要です。最後の1人は無効・降格・削除できません。<br>
            ※ 一般管理者は自分のアカウントしか見えません。パスワードは上のフォームから自分で変更します。
        </p>
    </div>
</div>
</body>
</html>
