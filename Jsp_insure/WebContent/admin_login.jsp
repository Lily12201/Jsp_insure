<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>管理者ログイン</title>
<!-- Bootstrap 4 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<!-- Font Awesome（图标） -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.min.css">
<style>
    body { background: #f0f2f5; }
    .login-box { width: 400px; margin: 80px auto; background: #fff;
                 padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px #ccc; }
    .reg-box   { width: 400px; margin: 80px auto; background: #fff;
                 padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px #ccc;
                 display: none; }
</style>

<!-- 顺序：先 jQuery，再 ctx，最后 admin_login.js -->
<script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/js/admin_login.js"></script>
</head>
<body>

<!-- ========== 登录 ========== -->
<div class="login-box" id="loginBox">
    <h4 class="text-center mb-4">
        <i class="fas fa-user-shield"></i> 保険管理システム
    </h4>

    <!-- 提示信息用 EL 取 -->
    <c:if test="${not empty msgText}">
        <div class="alert alert-${msgType}">${msgText}</div>
    </c:if>

    <div class="form-group">
        <label><i class="fas fa-user"></i> アカウント</label>
        <input type="text" id="account" class="form-control" placeholder="admin">
    </div>
    <div class="form-group">
        <label><i class="fas fa-lock"></i> パスワード</label>
        <input type="password" id="password" class="form-control" placeholder="パスワード">
    </div>

    <button type="button" id="admin_login" class="btn btn-primary btn-block">
        ログイン
    </button>

    <p class="mt-3 text-center">
        <a href="javascript:void(0)" id="toReg">新規登録はこちら</a>
    </p>
    <p class="text-center">
        <a href="${pageContext.request.contextPath}/index.jsp">← フロントページへ</a>
    </p>
</div>

<!-- ========== 注册 ========== -->
<div class="reg-box" id="regBox">
    <h4 class="text-center mb-4">
        <i class="fas fa-user-plus"></i> 管理者登録
    </h4>

    <div class="form-group">
        <label><i class="fas fa-user"></i> アカウント（3～20文字）</label>
        <input type="text" id="reg_account" class="form-control">
    </div>
    <div class="form-group">
        <label><i class="fas fa-lock"></i> パスワード（6文字以上）</label>
        <input type="password" id="reg_password" class="form-control">
    </div>
    <div class="form-group">
        <label><i class="fas fa-id-card"></i> 氏名（省略可）</label>
        <input type="text" id="reg_name" class="form-control">
    </div>
    <div class="form-group">
        <label><i class="fas fa-key"></i> 登録コード</label>
        <input type="text" id="reg_code" class="form-control" placeholder="管理者だけが知る文字列">
        <small class="text-muted">※ この欄が正しくないと登録できません</small>
    </div>

    <button type="button" id="admin_register" class="btn btn-success btn-block">
        登録する
    </button>

    <p class="mt-3 text-center">
        <a href="javascript:void(0)" id="toLogin">← ログイン画面へ戻る</a>
    </p>
</div>

</body>
</html>
