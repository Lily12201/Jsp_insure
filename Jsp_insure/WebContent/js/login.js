/**
 * 登陆
 */
$(function(){
	$("#user_login").click(function(){
		//1、入力されたメールアドレスとパスワードを取得する
		var user_email = $("#email").val();
		var user_pass = $("#password").val();
		//2、ナル値かどうかを判定する
		var bo = false;
		if(user_email.length > 0){
			if(user_pass.length > 0){
				bo = true;
			}else{
				alert("パスワードを空にすることはできませんよ！");
			}
		}else{
			alert("メールボックスを空にすることはできませんよ！");
		}
		if(bo){
			//3、Ajaxを利用して、データをバックエンドに送信する
			$.ajax({
				//提出先
				url:"/Jsp_insure/UserServlet?opt=user_login",
				type:"post",
				data:{"userEmail":user_email,"userPass":user_pass},
				dataType:"json",
				//正常に送信されたかどうかを判定する
				success:function(date){
					if(date > 0){
						alert("ログイン成功おめでとうございます。もうすぐ画面をジャンプします！");
						window.location.href = "index.jsp";
					}else{
						alert("アカウントまたはパスワードが間違っている！");
					}
				},
				error:function(){
					alert("ネットワークにエラーがありました。後で再試行してください！");
				}
			});
		}
	});
});