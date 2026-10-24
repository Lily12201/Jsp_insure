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
<title>商品管理</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<style>
    body { background: #f4f6f9; }
    .sidebar { width: 200px; background: #343a40; min-height: 100vh; padding-top: 20px; }
    .sidebar a { display: block; color: #ccc; padding: 12px 20px; text-decoration: none; }
    .sidebar a:hover { background: #495057; color: #fff; }
    .sidebar a.on { background: #007bff; color: #fff; }
    .content { padding: 25px; flex: 1; }
    .form-box { background: #fff; padding: 18px; border-radius: 4px; margin-bottom: 20px; }
    .form-box .form-group { margin-bottom: 8px; }
</style>

<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_commodity.js"></script>
</head>
<body>
<div class="d-flex">
    <div class="sidebar">
        <div class="text-white text-center mb-4" style="font-weight:bold;">保険管理システム</div>
        <a href="${pageContext.request.contextPath}/admin_main.jsp">ダッシュボード</a>
        <a href="${pageContext.request.contextPath}/AdminUserServlet?opt=admin_user_list">ユーザー管理</a>
        <a href="${pageContext.request.contextPath}/AdminCommodityServlet?opt=admin_commodity_list" class="on">商品管理</a>
        <a href="${pageContext.request.contextPath}/AdminOrderServlet?opt=admin_order_list">注文管理</a>
        <a href="${pageContext.request.contextPath}/AdminReportServlet?opt=view">統計レポート</a>
        <a href="${pageContext.request.contextPath}/AdminAccountServlet?opt=admin_account_list">管理者管理</a>
        <a href="${pageContext.request.contextPath}/AdminLoginServlet?opt=admin_logout">ログアウト</a>
    </div>

    <div class="content">
        <h3>商品管理</h3>
        <p class="text-muted">商品の新規追加・編集・削除・上架／下架ができます。</p>

        <!-- ===== 新增 / 修改 共用的表单 ===== -->
        <div class="form-box">
            <h6 id="formTitle" class="mb-3">商品を新規追加</h6>
            <input type="hidden" id="commodity_id" value="">

            <div class="form-row">
                <div class="form-group col-md-3">
                    <label>商品名 <span class="text-danger">*</span></label>
                    <input type="text" id="c_name" class="form-control" placeholder="例：小児医療健康保険E金">
                </div>
                <div class="form-group col-md-2">
                    <label>価格（円）<span class="text-danger">*</span></label>
                    <input type="text" id="c_price" class="form-control numOnly" placeholder="3000">
                </div>
                <div class="form-group col-md-2">
                    <label>タイプ <span class="text-danger">*</span></label>
                    <select id="c_type" class="form-control">
                        <option value="">選択してください</option>
                        <option value="1">1 健康保険</option>
                        <option value="2">2 意外保険</option>
                        <option value="3">3 企業保険</option>
                        <option value="4">4 旅行保険</option>
                        <option value="5">5 家計保険</option>
                    </select>
                </div>
                <div class="form-group col-md-3">
                    <label>画像URL</label>
                    <input type="text" id="c_img" class="form-control" placeholder="images/et1.jpg">
                </div>
                <div class="form-group col-md-2">
                    <label>保険期間</label>
                    <input type="text" id="c_time_length" class="form-control" placeholder="1年">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group col-md-4">
                    <label>商品紹介</label>
                    <input type="text" id="c_introduce" class="form-control" placeholder="ひとことで説明">
                </div>
                <div class="form-group col-md-2">
                    <label>対象年齢（開始）</label>
                    <input type="text" id="c_age_start" class="form-control numOnly" placeholder="0">
                </div>
                <div class="form-group col-md-2">
                    <label>対象年齢（終了）</label>
                    <input type="text" id="c_age_end" class="form-control numOnly" placeholder="200">
                </div>
                <div class="form-group col-md-4 text-right">
                    <label>&nbsp;</label><br>
                    <button type="button" id="btnSave" class="btn btn-primary">新規追加</button>
                    <button type="button" id="btnClear" class="btn btn-secondary">クリア</button>
                </div>
            </div>
        </div>

        <!-- ===== 搜索 ===== -->
        <form action="${pageContext.request.contextPath}/AdminCommodityServlet" method="get"
              class="form-inline mb-3">
            <input type="hidden" name="opt" value="admin_commodity_list">
            <input type="text" id="keyword" name="keyword" class="form-control mr-2"
                   placeholder="商品ID または 商品名で検索（Enter可）" value="${keyword}">
            <select name="type" class="form-control mr-2">
                <option value="">全タイプ</option>
                <option value="1">健康保険</option>
                <option value="2">意外保険</option>
                <option value="3">企業保険</option>
                <option value="4">旅行保険</option>
                <option value="5">家計保険</option>
            </select>
            <button type="submit" class="btn btn-primary">検索</button>
        </form>

        <!-- ===== 商品列表 ===== -->
        <table class="table table-bordered table-hover bg-white">
            <thead class="thead-dark">
                <tr>
                    <th class="sort">ID</th><th>商品名</th><th class="sort">価格</th>
                    <th>タイプ</th><th>保険期間</th><th>対象年齢</th>
                    <th class="sort">販売数</th><th>状態</th><th>操作</th>
                </tr>
            </thead>
            <tbody>
            <!--每一行的数据都是 Servlet 查出来塞进 request 的 -->
                <c:forEach items="${commodityList}" var="c">
                    <tr>
                        <td>${c.commodity_id}</td>
                        <td>${c.commodity_name}</td>
                        <td>${c.commodity_price}</td>
                        <td>
                            <c:if test="${c.commodity_type == 1}">健康保険</c:if>
                            <c:if test="${c.commodity_type == 2}">意外保険</c:if>
                            <c:if test="${c.commodity_type == 3}">企業保険</c:if>
                            <c:if test="${c.commodity_type == 4}">旅行保険</c:if>
                            <c:if test="${c.commodity_type == 5}">家計保険</c:if>
                        </td>
                        <td>${c.commodity_time_length}</td>
                        <td>${c.commodity_age_start} 〜 ${c.commodity_age_end}</td>
                        <td>${c.order_count}</td>
                        <td>
                            <c:if test="${c.commodity_status == 1}">
                                <span class="badge badge-success">上架</span>
                            </c:if>
                            <c:if test="${c.commodity_status != 1}">
                                <span class="badge badge-secondary">下架</span>
                            </c:if>
                        </td>
                        <td>
                            <!-- 編集：把这一行的数据塞进 data-*，JS 再填进上面的表单 -->
                            <button type="button" class="btn btn-sm btn-info btn-edit"
                                    data-id="${c.commodity_id}"
                                    data-name="${c.commodity_name}"
                                    data-price="${c.commodity_price}"
                                    data-type="${c.commodity_type}"
                                    data-img="${c.commodity_img}"
                                    data-introduce="${c.commodity_introduce}"
                                    data-time="${c.commodity_time_length}"
                                    data-as="${c.commodity_age_start}"
                                    data-ae="${c.commodity_age_end}">編集</button>

                            <!-- 上架 / 下架 -->
                            <c:if test="${c.commodity_status == 1}">
                                <button type="button" class="btn btn-sm btn-secondary btn-shelf"
                                        data-id="${c.commodity_id}" data-status="0">下架</button>
                            </c:if>
                            <c:if test="${c.commodity_status != 1}">
                                <button type="button" class="btn btn-sm btn-success btn-shelf"
                                        data-id="${c.commodity_id}" data-status="1">上架</button>
                            </c:if>

                            <button type="button" class="btn btn-sm btn-danger btn-delete"
                                    data-id="${c.commodity_id}">削除</button>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty commodityList}">
                    <tr><td colspan="9" class="text-center text-muted">データがありません</td></tr>
                </c:if>
            </tbody>
        </table>

        <p class="text-muted small">
            ※ すでに購入された商品は削除できません（履歴が残るよう「下架」してください）。<br>
            ※ 下架にした商品を前台に出さないようにするには、前台 CommodityDaoImpl の SQL に
               「 AND commodity_status = 1 」を追加してください。
        </p>
    </div>
</div>
</body>
</html>
