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
<title>注文管理</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; flex: 1; }
    .sum-box { background: #fff; padding: 15px 20px; border-radius: 4px; margin-bottom: 18px; }
    .sum-box b { font-size: 22px; }
</style>

<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_order.js"></script>
</head>
<body>
<div class="d-flex">
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list" class="on">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <div class="content">
        <h3>注文管理</h3>
        <p class="text-muted">すべての会員の注文を一覧できます。</p>

        <!-- 顶部汇总 -->
        <div class="sum-box">
            総注文数：<b class="text-primary">${totalCount}</b> 件
            &nbsp;&nbsp;｜&nbsp;&nbsp;
            売上合計：<b class="text-danger">${totalPrice}</b> 円
        </div>

        <!-- 三条件查询 -->
        <form action="${pageContext.request.contextPath}/AdminOrderServlet" method="get"
              class="form-inline mb-3">
            <input type="hidden" name="opt" value="admin_order_list">
            <input type="text" id="keyword" name="orderId" class="form-control mr-2"
                   placeholder="注文番号" value="${orderId}">
            <input type="text" name="beneficiaryName" class="form-control mr-2"
                   placeholder="被保険者氏名" value="${beneficiaryName}">
            <input type="text" name="phone" class="form-control mr-2"
                   placeholder="電話番号" value="${phone}">
            <button type="submit" class="btn btn-primary mr-2">検索</button>
            <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list"
               class="btn btn-secondary mr-2">リセット</a>
            <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=export&type=order"
               class="btn btn-success btn-export">Excel出力</a>
        </form>

        <!-- 订单列表 -->
        <table class="table table-bordered table-hover bg-white">
            <thead class="thead-dark">
                <tr>

                    <th>注文番号</th><th>契約者</th><th>商品名</th><th>被保険者</th>
                    <th>投保地</th><th>電話番号</th><th>保険期間</th>
                    <th>年数</th><th>金額</th><th>操作</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${orderList}" var="o">
                    <tr>
                        <td>${o.order_id}</td>
                        <td>${o.user_name}</td>
                        <td>${o.commodity_name}</td>
                        <td>${o.order_beneficiary_name}</td>
                        <td>${o.order_beneficiary_city}</td>
                        <td>${o.order_beneficiary_phone}</td>
                        <td>${o.order_commodity_time_start} 〜 ${o.order_commodity_time_end}</td>
                        <td>${o.order_commodity_time_length}</td>
                        <td>${o.order_sum_price}</td>
                        <td>
                            <!-- 詳細：把这一行的明细塞进 data-*，JS 再填进下面的卡片 -->
                            <button type="button" class="btn btn-sm btn-info btn-detail"
                                    data-id="${o.order_id}"
                                    data-user="${o.user_name}"
                                    data-commodity="${o.commodity_name}"
                                    data-beneficiary="${o.order_beneficiary_name}"
                                    data-city="${o.order_beneficiary_city}"
                                    data-phone="${o.order_beneficiary_phone}"
                                    data-start="${o.order_commodity_time_start}"
                                    data-end="${o.order_commodity_time_end}"
                                    data-length="${o.order_commodity_time_length}"
                                    data-price="${o.order_sum_price}"
                                    data-l1="${o.order_commodity_liability1}"
                                    data-l2="${o.order_commodity_liability2}"
                                    data-l3="${o.order_commodity_liability3}"
                                    data-l4="${o.order_commodity_liability4}"
                                    data-l5="${o.order_commodity_liability5}"
                                    data-l6="${o.order_commodity_liability6}"
                                    data-l7="${o.order_commodity_liability7}">詳細</button>

                            <button type="button" class="btn btn-sm btn-danger btn-delete"
                                    data-id="${o.order_id}">削除</button>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty orderList}">
                    <tr><td colspan="10" class="text-center text-muted">データがありません</td></tr>
                </c:if>
            </tbody>
        </table>

        <!-- 详情卡片：默认隐藏，点「詳細」才显示 -->
        <div class="card" id="detailBox" style="display:none;">
            <div class="card-header d-flex justify-content-between align-items-center">
                <b>注文詳細</b>
                <button type="button" class="btn btn-sm btn-secondary" id="btnCloseDetail">閉じる</button>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p>注文番号：<span id="d_order_id"></span></p>
                        <p>契約者：<span id="d_user"></span></p>
                        <p>商品名：<span id="d_commodity"></span></p>
                        <p>被保険者：<span id="d_beneficiary"></span></p>
                    </div>
                    <div class="col-md-6">
                        <p>投保地：<span id="d_city"></span></p>
                        <p>電話番号：<span id="d_phone"></span></p>
                        <p>保険期間：<span id="d_period"></span></p>
                        <p>年数：<span id="d_years"></span></p>
                        <p>合計金額：<b class="text-danger"><span id="d_price"></span></b> 円</p>
                    </div>
                </div>
                <h6 class="mt-3">追加した保障</h6>
                <table class="table table-sm table-bordered">
                    <thead class="thead-light"><tr><th>項目</th><th>金額</th></tr></thead>
                    <tbody id="d_liability"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
