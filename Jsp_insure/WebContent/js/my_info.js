/**
 * 信息提交修改:修改密码、手机号，余额充值
 */
console.log("my_info.jsp文件加载成功");
$(function(){
	$("#updateuserinfo").click(function(){
		console.log("按钮点击事件已经触发！");
		      //1、取值（trim 防空格）
				var userPass    = $.trim($("#userPass").val());
				var userPhone   = $.trim($("#userPhone").val());
				var chargeMoney = $.trim($("#chargeMoney").val());

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
			//充值金额：空值/0等于不充值，放行，可以修改密码和手机号
			var money = 0;
					if(chargeMoney !== "" && chargeMoney !== "0"){
						if(!/^\d+$/.test(chargeMoney) || Number(chargeMoney) <= 0){
							alert("チャージ金額は0より大きい整数を入力してください！");
							return;
						}
						money = Number(chargeMoney);
					}
			console.log("准备发送Ajax请求！chargeMoney = " + money);
			//5、校验都通过，提交后端
			$.ajax({
				url:"/Jsp_insure/UserServlet",
				type:"post",
				data:{
					opt:"user_update",
					userPass:userPass,
					userPhone:userPhone,
					// 不充值就是 0
					chargeMoney:money   
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