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
<title>統計レポート</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; flex: 1; }
</style>

<!-- 三行顺序不能反：先 jQuery，再 ctx，最后本页的 admin_report.js -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_report.js"></script>
</head>
<body>
<div class="d-flex">
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view" class="on">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <div class="content">
        <h3>統計レポート</h3>
        <p class="text-muted">各レポート右上のボタンで Excel に出力できます。</p>

        <!-- ===== 报表1 性别分布 ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>① 性別分布</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=sex"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body">
                <c:forEach items="${sexList}" var="s">
                    <div class="mb-2">
                        <span>${s.label}：${s.value} 人（${s.percent}%）</span>
                        <div class="progress">
                            <div class="progress-bar bg-primary" style="width:${s.percent}%"></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- ===== 报表2 年龄分布 ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>② 年齢分布</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=age"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body">
                <c:forEach items="${ageList}" var="s">
                    <div class="mb-2">
                        <span>${s.label}：${s.value} 人（${s.percent}%）</span>
                        <div class="progress">
                            <div class="progress-bar bg-success" style="width:${s.percent}%"></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- ===== 报表3 地区分布 ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>③ 被保険者地域分布</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=city"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body">
                <c:forEach items="${cityList}" var="s">
                    <div class="mb-2">
                        <span>${s.label}：${s.value} 件（${s.percent}%）</span>
                        <div class="progress">
                            <div class="progress-bar bg-warning" style="width:${s.percent}%"></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- ===== 报表4 商品类型（有金额，用表格） ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>④ 商品タイプ別売上</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=type"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body">
                <table class="table table-bordered table-sm bg-white">
                    <thead class="thead-light">

                        <tr><th>商品タイプ</th><th>注文数</th><th>売上金額</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${typeList}" var="s">
                            <tr>
                                <td>${s.label}</td>
                                <td>${s.value}</td>
                                <td>${s.amount} 円</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- ===== 报表5 月度趋势 ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>⑤ 月次売上推移</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=month"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body">
                <table class="table table-bordered table-sm bg-white">
                    <thead class="thead-light">

                        <tr><th>年月</th><th>注文数</th><th>売上金額</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${monthList}" var="s">
                            <tr>
                                <td>${s.label}</td>
                                <td>${s.value}</td>
                                <td>${s.amount} 円</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- ===== 会员导出 ===== -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>⑥ 会員一覧</b>
                <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=user"
                   class="btn btn-sm btn-success btn-export">Excel出力</a>
            </div>
            <div class="card-body text-muted small">
                登録されている全会員を Excel に出力します。
            </div>
        </div>

    </div>
</div>
</body>
</html>
