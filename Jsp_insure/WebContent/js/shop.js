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

$(function(){
	$("#buyCommodity").click(function(){
		
	});
});