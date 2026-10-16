/**
 * shop.js文件
 */
//设置js方法来操作界面下拉框，动态设置值
function js(obj,v){
	var sum = parseFloat(obj.value) * 10;
	sessionStorage.setItem(v,sum + "");
	var sum_value = 0;
	var bo = true;
	if(getData() != ""){
		var $value = getData().split(",");
		for(var i = 0; i < $value.length; i++){
			if(v == $value[i]){
				bo = false;
				break;
			}
		}
	}
	if(bo){
		sessionStorage.setItem("dataValue",getData() + v);
	}
	if(getData() != ""){
		var $value = getData().split(",");
		for(var i = 0; i < $value.length -1; i++){
			sum_value = sum_value + parseFloat(sessionStorage.getItem($value[i]));
		}
	}
	sum_value = sum_value + parseFloat(document.getElementById("price_value").value);
	document.getElementById("price_view").innerHTML = sum_value;
}

//获取内容
function getData(){
	var dataValue = sessionStorage.getItem("dataValue");
	if(dataValue != null){
		return dataValue + ","
	}
	return "";
}

//设置当前时间
function setDate(){
	var date = new Date();
	document.getElementById("staDateTime").value = date.getFullYear();
}

//每次打开此网页进行清空sessionStorage避免出现金额增多情况
window.onload = function(){
	sessionStorage.clear();
	//运行
	setDate();
}
//补齐购买
$(function(){
	$("#buyCommodity").click(function(){
		//1、获取前端数据
		var userName2=$("#userName2").val();
		var beneficiarylocation=$("#commodity_location").val();
		var userPhone2=$("#userPhone2").val();
		var endDataTime = $("#endDataTime").val();
		var staDateTime=$("#staDateTime").val();
		//勾选保险责任后的加价
		var price = $("#price_view").text();
		var commodity_id = $("#commodity_id").val(); 
		//商品适用范围
		var locationRange = $("#commodity_location_range").val();

		//2、非空验证
			if(userName2 == "" || beneficiarylocation == "" || userPhone2 == ""){
				alert("被保険者の情報を入力してください！");
				return;
			}

		//3、国家验证：只能输入 中国大陆 或 日本
			if(beneficiarylocation != "中国大陆" && beneficiarylocation != "日本"){
				alert("地域は「中国大陆」または「日本」で入力してください！");
				return;
			}

		//4、和商品适用范围匹配验证
		//商品表里可能存"中国大陆,日本"这类多值，用indexOf判断包含
			if(locationRange.indexOf(beneficiarylocation) == -1){
				alert("入力された地域は本商品の販売範囲外です！購入ができません！");
				return;
			}

		//5、手机号格式验证：11位数字
		//我控制台验证长度多了 2 个隐藏字符，所以要先清洗再验证，避免发给后端的长度还是多两个字符
			userPhone2 = userPhone2.replace(/\s+/g, "");
			var phoneReg = /^1\d{10}$/;
			if(!phoneReg.test(userPhone2)){
				alert("携帯番号の形式が正しくありません（11桁の数字）！");
				return;
			}

		//6、校验都通过，提交后端
					$.ajax({
						url:"/Jsp_insure/OrderServlet",
						type:"post",
						data:{
						opt:"buyCommodity",
							commodity_id:commodity_id,
							beneficiaryName:userName2,
							beneficiarylocation:beneficiarylocation,
							beneficiaryPhone:userPhone2,
							timeLength:endDataTime,
							startDate:staDateTime,
							sumPrice:price
								},
						dataType:"text",
						success:function(result){
							if(result=="1"){
								alert("購入が成功しました！");
								location.href="/Jsp_insure/OrderServlet?opt="
							}else if(result=="2"){
								//余额不足，去我的信息界面充值
								alert("残高が足りません。チャージページへ移動します！");
								location.href="my_info.jsp"
							}else if(result=="3"){
								//被保人国家不在商品适用范围
								alert("入力された地域は本商品の販売範囲外です！購入ができません！");
							}else if(result=="4"){
								//被保人的姓名和手机号与注册时不一致
								alert("入力された名前や携帯電話番号が登録情報と一致しません！");
							}else if(result=="5"){
								//被保人年龄/性别不符合本商品投保条件
								alert("被保険者は本商品の加入条件（年齢・性別）を満たしていません！");
							}else if(result=="6"){
								//被保人未登录
								alert("この被保険者は登録されていません。先にご登録ください！");
							}														
						},
						error:function(){
								alert("ネットワークにエラーがありました。後で再試行してください！");
						}
			});			
			});
			});