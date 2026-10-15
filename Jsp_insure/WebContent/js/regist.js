/**
 * 注册JS模块
 */
$(function(){
	$("#userRegister").click(function(){
		//获取界面信息
		var userName1 = $("#userX").val() + $("#userM").val();
		var userName2 = $("#userXq").val() + $("#userMq").val();
		var userBirthday = $("#year").val() + $("#month").val() + $("#day").val();
		var userSex = $("#userSex").val();
		var userPhone = $("#userPhone5").val() + $("#userPhone4").val() + $("#userPhone42").val();
		var userEmail = $("#userEmail1").val() + "@" + $("#userEmail2").val();
		var userPass = $("#userPass").val();
		var bo = false;
		if(userName1.length > 0){
			if(userName2.length > 0){
				//正则表达式正确判断生日
				//年份必须是19或20开头，后面跟2位数字（如1995, 2023）；格式必须是年月日连写，例如 20230515
				var regB=/^((19|20)[0-9]{2})((0[1-9])|(1[0-2]))((0[1-9])|((1|2)[0-9])|(3[0-1]))$/;
				if(regB.test(userBirthday)){
					//正则表达式正确判断手机号
					//必须以1开头，第二位是3到9之间的数字，后面跟着9位任意数字
					var regP = /^1[3,4,5,6,7,8,9][0-9]{9}$/;
					if(regP.test(userPhone)){
						//正则表达式正确判断邮箱
						//允许邮箱前缀是字母、数字、下划线或横杠。
						var regE = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
						if(regE.test(userEmail)){
							//正则表达式正确判断密码
							//\w 代表字母、数字或下划线。{6,16} 代表长度必须在6到16位之间。
							var regPa=/^(\w){6,16}$/;
							if(regPa.test(userPass)){
								bo = true;
							}else{
								alert("パスワードには6桁以上15桁未満の数字が必要です！");
							}
						}else{
							alert("入力したメールボックスに問題がありますよ！");
						}
					}else{
						alert("入力した携帯番号に問題がありますよ！");
					}
				}else{
					alert("あなたが入力した誕生日情報に問題がありますよ！");
				}
			}else{
				alert("仮名は空にしてはいけませんよ！");
			}
		}else{
			alert("名前は空にしてはいけませんよ！");
		}
		if(bo){
			//使用ajax传递数据判断
			$.ajax({
				url:"/Jsp_insure/UserServlet?opt=user_regist",
				type:"post",
				data:{"userName":userName1 + "(" + userName2 + ")","userBirthday":userBirthday,"userSex":userSex,"userPhone":userPhone,"userEmail":userEmail,"userPass":userPass},
				dataType:"json",
				success:function(date){
					if(date > 0){
						alert("ログインインタフェースをジャンプしてご登録おめでとうございます！");
						window.location.href = "login.jsp";
					}else{
						alert("登録に失敗しました！");
					}
				},
				error:function(){
					alert("ネットワークにエラーがありました。後で再試行してください！");
				}
			});
		}
	});
});