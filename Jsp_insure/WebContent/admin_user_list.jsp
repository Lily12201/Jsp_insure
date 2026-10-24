<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- 没登录就踢回登录页（每个后台页面都要有这三行）--%>
<c:if test="${empty sessionScope.adminUser}">
    <c:redirect url="/admin_login.jsp"/>
</c:if>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー管理</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; flex: 1; }
    th.sort { cursor: pointer; }
    th.sort:after { content: " ↕"; color: #999; }
    th.asc:after  { content: " ↑"; color: #007bff; }
    th.desc:after { content: " ↓"; color: #007bff; }
</style>

<!-- 三行顺序不能反：先 jQuery，再 ctx，最后本页的 admin_user.js -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_user.js"></script>
</head>
<body>
<div class="d-flex">
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list" class="on">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <div class="content">
        <h3>ユーザー管理</h3>
        <p class="text-muted">会員の一覧確認・利用停止・チャージができます。</p>

        <!-- 搜索框：id="keyword" → 按回车就能查（处理函数在 admin_user.js 第 1 项） -->
        <form action="${pageContext.request.contextPath}/AdminUserServlet" method="get"
              class="form-inline mb-3">
            <input type="hidden" name="opt" value="admin_user_list">
            <input type="text" id="keyword" name="keyword" class="form-control mr-2"
                   placeholder="ID または 氏名で検索（Enter可）" value="${keyword}">
            <button type="submit" class="btn btn-primary mr-2">検索</button>
            <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=user"
               class="btn btn-success btn-export">Excel出力</a>
        </form>

        <table class="table table-bordered table-hover bg-white">
            <thead class="thead-dark">
                <tr>
                 <%-- 排序在 SQL 里做，页面打开就是按余额降序 --%>             
                    <th>ID</th><th>氏名</th><th>メール</th><th>電話番号</th>
                    <th>性別</th><th>生年月日</th><th>残高（多い順）</th><th>状態</th><th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${userList}" var="u">
                    <tr>
                        <td>${u.user_id}</td>
                        <td>${u.user_name}</td>
                        <td>${u.user_email}</td>
                        <td>${u.user_phone}</td>
                       <td>${u.user_sex}</td>
                        <td>${u.user_birthday}</td>
                        <td>${u.user_money}</td>
                        <td>
                            <c:if test="${u.user_status == 1}">
                                <span class="badge badge-success">有効</span>
                            </c:if>
                            <c:if test="${u.user_status != 1}">
                                <span class="badge badge-secondary">無効</span>
                            </c:if>
                        </td>
                        <td>
                            <!-- 禁用 / 启用：class="btn-status"，data-status 是要改成的值 -->
                            <c:if test="${u.user_status == 1}">
                                <button type="button" class="btn btn-sm btn-warning btn-status"
                                        data-id="${u.user_info_id}" data-status="0">無効にする</button>
                            </c:if>
                            <c:if test="${u.user_status != 1}">
                                <button type="button" class="btn btn-sm btn-info btn-status"
                                        data-id="${u.user_info_id}" data-status="1">有効にする</button>
                            </c:if>

                            <!-- 充值：金额在 JS 里用 prompt 弹框输入 -->
                            <button type="button" class="btn btn-sm btn-success btn-charge"
                                    data-id="${u.user_info_id}">チャージ</button>

                            <!-- 删除：要传两个 id（主表 user_id + 明细表 user_info_id） -->
                            <button type="button" class="btn btn-sm btn-danger btn-delete"
                                    data-id="${u.user_id}" data-infoid="${u.user_info_id}">削除</button>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty userList}">
                    <tr><td colspan="9" class="text-center text-muted">データがありません</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
