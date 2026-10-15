/**
 * 主界面商品遍历
 */
$(function(){
	//首次进入，加载全部商品
	loadCommodity("/Jsp_insure/CommodityServlet?opt=selectAllCommodityList");
	
	//点击分类切换
	$(".type_li").click(function(){
		var type=$(this).attr("data-type");
		if(type==""){
			loadCommodity("CommodityServlet?opt=selectAllCommodityList");	
		}else{
			loadCommodity("CommodityServlet?opt=selectCommodityListByType&commodity_type="+type);
		}
	});
	});

//请求商品列表并渲染到#context_text
function loadCommodity(url){
	alert("要请求的URL = " + url);          // ← 加这行	
	$.ajax({
		url:url,
		type:"post",
		dataType:"json",
		success:function(data){
			alert("后端返回条数 = " + data.length);   // ← 加这行
			//先清空，再渲染
			$("#context_text").empty();
			var html="";
			for(var i=0;i<data.length;i++){
				var c=data[i];
			html+='<div class="div_context">'
				+'<div class="div_context_l">'
				+'<img src="'+c.commodity_img+'" width="350px" height="260px"/>' 
				+'</div>'
				+'<div class="div_context_r">'
					+'<div class="div_context_r_1">'
						+'<h2>'+c.commodity_name+'</h2>'
						+'<p class="p1">'+c.commodity_introduce+'</p>'
						+'<p class="p2">適用年齢:'+c.commodity_age_start+'~'+c.commodity_age_end+'歳</p>'
					+'</div>'
					+'<div class="div_context_r_2">'
						+'<div class="price">'
							+'<span class="fh"><b>¥</b></span>'
							+'<span class="price_a"><b>'+c.commodity_price+'</b></span>'
							+'<span class="fh"><b>/起</b></span>'
						+'</div>'
						//这里要跳转到page.jso页面展示保险详情（保险详情界面）
						+'<a href="CommodityServlet?opt=commodityQueryById&commodity_id='+c.commodity_id+'&page_info.jsp">'
							+'<div class="img_a">'
								+'<b>詳細はこちら</b>'
							+'</div>'
						+'</a>'
					+'</div>'
				+'</div>'
			+'</div> '
			}
		$("#context_text").append(html);
			
		},
		error:function(){
			alert("失败! status=" + status + " xhr.status=" + xhr.status);  // ← 改这行
			alert("ネットワークにエラーがありました。後で再試行してください！");
		}
	});
	}