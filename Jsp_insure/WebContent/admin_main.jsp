<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--这一段每个后台页面都要有：没登录就踢回登录页 --%>
<c:if test="${empty sessionScope.adminUser}">
    <c:redirect url="/admin_login.jsp"/>
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>保険管理システム</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; }
</style>
</head>
<body>

<div class="d-flex">
    <!-- ===== 左侧菜单（每个后台页面都用这一段） ===== -->
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp" class="on">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <!-- ===== 右侧内容 ===== -->
    <div class="content flex-fill">
        <%-- EL 表达式会自动去 session 里找 adminUser，再调它的 getAdmin_name() --%>
        <h3>ようこそ、${sessionScope.adminUser.admin_name} さん</h3>
        <p class="text-muted">左のメニューから管理したい機能を選んでください。</p>

        <div class="row mt-4">
            <div class="col-md-3">
                <div class="card text-center p-3">
                    <h5>ユーザー管理</h5>
                    <p class="text-muted small">会員の検索 / 無効化 / チャージ</p>
                    <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list"
                       class="btn btn-sm btn-primary">開く</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center p-3">
                    <h5>商品管理</h5>
                    <p class="text-muted small">保険商品の登録 / 編集 / 削除</p>
                    <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list"
                       class="btn btn-sm btn-primary">開く</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center p-3">
                    <h5>注文管理</h5>
                    <p class="text-muted small">注文の検索 / 詳細 / 削除</p>
                    <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list"
                       class="btn btn-sm btn-primary">開く</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-center p-3">
                    <h5>統計レポート</h5>
                    <p class="text-muted small">性別 / 年齢 / 売上 ＋ Excel出力</p>
                    <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view"
                       class="btn btn-sm btn-success">開く</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
