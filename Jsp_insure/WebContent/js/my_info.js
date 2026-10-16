/**
 * 信息提交修改:修改密码、手机号，余额充值
 */
console.log("my_info.jsp文件加载成功");
$(function(){
	$("#updateuserinfo").click(function(){
		console.log("按钮点击事件已经触发！");
			//1、取值
			var userPass = $("#userPass").val();
			var userPhone = $("#userPhone").val();
			var chargeMoney = $("#chargeMoney").val();

			//2、密码校验：6~16位，\w 代表字母、数字或下划线,可以纯数字
			var passReg = /^(\w){6,16}$/;;
			if(!passReg.test(userPass)){
				alert("パスワードには6桁以上15桁未満の長さが必要です！");
				return;
			}

			//3、手机号校验：11位数字，1开头
			var phoneReg = /^1\d{10}$/;
			if(!phoneReg.test(userPhone)){
				alert("携帯番号の形式が正しくありません（11桁の数字）！");
				return;
			}

			//4、充值金额校验：必须是数字且大于0
			var money = Number(chargeMoney);
			if(isNaN(money) || money <= 0){
				alert("チャージ金額は0より大きい数字を入力してください！");
				return;
			}
			console.log("准备发送Ajax请求！");
			//5、校验都通过，提交后端
			$.ajax({
				url:"/Jsp_insure/UserServlet",
				type:"post",
				data:{
					opt:"user_update",
					userPass:userPass,
					userPhone:userPhone,
					chargeMoney:chargeMoney
				},
				dataType:"text",
				success:function(result){
					if(result == "1"){
						alert("変更が成功しました！");
						//刷新页面，让新的余额和session数据生效
						location.href = "my_info.jsp";
					}else{
						alert("変更に失敗しました。もう一度お試しください！");
					}
				},
				error:function(){
					alert("ネットワークにエラーがありました。後で再試行してください！");
				}
			});
		});
	});