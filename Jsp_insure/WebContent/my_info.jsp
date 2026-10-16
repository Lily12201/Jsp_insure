<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="login.jsp" />
</c:if>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>健康保険のホームページ</title>
		<link rel="stylesheet" type="text/css" href="css/index.css" />
		<link rel="stylesheet" href="https://at.alicdn.com/t/c/font_3881267_wfv3iyzbijg.css">
		<link rel="stylesheet" href="https://cdn.staticfile.org/font-awesome/4.7.0/css/font-awesome.css">
		<script type="text/javascript" src="js/banner.js"></script> 
		
		<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
		<script type="text/javascript" src="js/my_info.js"></script>
	</head>
	<body>
		<div align="center" id="div_all">
			<div id="top1">
				<div id="top1_text">
					<div id="top1_text1">こんにちは、健康保険モールへようこそ</div>
					<div id="top1_text2">
						<span id="span1">最新情報：</span>
						<marquee id="lb" direction="left">
							ログインしてサインするとプレゼントがあります
						</marquee>
						<%-- 这里和index.jsp的逻辑一样，没登录显示login/regist;登录了显示こんにちは！--%>
						<%--从后端收到的数据：服务器内部的内存读取操作 --%>				
						<c:if test="${not empty sessionScope.user}">
							<button type="button" id="userlogin">${sessionScope.user.user_name} 
					            ${sessionScope.user.user_sex == '女' ? '女士' : '先生'}，こんにちは！
					         </button>
						</c:if>
						  <%-- 未登录：显示 login/regist --%>
						<c:if test="${empty sessionScope.user}">
						<a href="login.jsp">
							<button type="button" id="userlogin">login/regist</button>
						</a>
						</c:if>
					</div>
				</div>
			</div>
			<div id="top2">
				<div id="top2_text">
					<ul id="top2_text_ul">
						<li><b>すべての保険</b></li>
						<li><b>健康保険</b></li>
						<li><b>意外保険</b></li>
						<li><b>企業保険</b></li>
						<li><b>旅行保険</b></li>
						<li><b>家計保険</b></li>
					</ul>
				</div>
			</div>
			<div id="context_all2">
				<div id="banner_img">
					<div id="banner_img_text">
					    <img src="images/my_img.jpg" width="100%" />
					</div>
				</div>
				<div id="context" class="context" style="height: auto;">
					<div id="context_text_1" style="height: 70px;">
						<a href="my_info.jsp"><div id="myinfo" style="background-color:#FF7F50;color: white;"><b>マイメッセージ</b></div></a>
						<a href="/Jsp_insure/OrderServlet?opt=select_order_by_user_id"><div id="myorder"><b>私の注文</b></div></a>
					</div>
					<div id="context_text_2">
						<div id="context_text_2_1">
						<!-- 补齐前端的EL标签，接收后端数据 -->
							<b>${sessionScope.user.user_name}さん、あなたの情報は次の通りです。</b>
							<table id="table_myinfo">
								<tr>
									<th>メールアドレス：</th>
									<td>${sessionScope.user.user_email}</td>
								</tr>
								<tr>
									<th>パスワード：</th>
									<td><input type="password" name="userPass" id="userPass" value="${sessionScope.user.user_password}"/></td>
								</tr>
								<tr>
									<th>あなたの誕生日：</th>
									<td>${sessionScope.user.user_birthday}</td>
								</tr>
								<tr>
									<th>携帯番号：</th>
									<td>
										<input type="text" maxlength="11" name="userPhone" id="userPhone" value="${sessionScope.user.user_phone}" />
									</td>
								</tr>
								<tr>
									<th>現在の残高:</th>
									<td>
										${sessionScope.user.user_money} 円
						               　チャージ金額の入力:
						               <input type="text" name="chargeMoney" id="chargeMoney" value="0" />
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<a href="#"><div id="updateuserinfo">変更</div></a>
									</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				<div id="foot">
					<h5 class="h51">カスタマーサービスホットライン：888-888-8888 （9:00~21:00）</h5>
					<h5 class="h52">○○健康保険株式会社 027-666666 &nbsp;&nbsp; ICP認証：UP9999999 &nbsp;&nbsp; Copyright © 2023 All Rights Reserved</h5>
				</div>
			</div>
		</div>
	</body>
</html>