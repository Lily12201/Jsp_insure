/*
 * 订单管理 JS
 *   1 成功 / 2 输入不合法 / 0 出错
 */

$(function () {

    /* ===== 1、搜索框按回车就查询 ===== */
    $("#keyword").keydown(function (e) {
        if (e.keyCode == 13) {
            $(this).closest("form").submit();
        }
    });

   
    /* ===== 2、导出 Excel 时给个提示 ===== */
    $(document).on("click", ".btn-export", function () {
        alert("Excel を出力しています。しばらくお待ちください。");
    });

    /* ===== 3、删除订单 =====
     */
    $(document).on("click", ".btn-delete", function () {
        var orderId = $(this).attr("data-id");

        if (!confirm("注文番号 " + orderId + " を削除しますか？（元に戻せません）")) {
            return;
        }

        $.ajax({
            url: ctx + "/AdminOrderServlet?opt=admin_order_delete",
            type: "post",
            data: {"orderId": orderId},
            success: function (date) {
                if (date == 1) {
                    alert("削除しました！");
                    location.reload();
                } else if (date == 2) {
                    alert("入力値が正しくありません！");
                } else {
                    alert("削除に失敗しました！");
                }
            },
            error: function () {
                alert("ネットワークにエラーがありました。後で再試行してください！");
            }
        });
    });

    /* ===== 4、点「詳細」：把这一行的隐藏信息填进下面的详情卡片 =====
     * 点击商品行按钮 → 读取按钮上7个保障字段 → 只把有内容的保障拼成表格。
     * 点详细时才展开看。数据用 data-* 存在按钮上，不用再查一次数据库。
     */
    $(document).on("click", ".btn-detail", function () {
        var b = $(this);
		//b.attr("data-id")'：读取按钮上 'data-id' 的值
		//'$("#xxx").val(值)'：给页面上方表单里对应的输入框赋值、回填数据
        $("#d_order_id").text(b.attr("data-id"));
        $("#d_user").text(b.attr("data-user"));
        $("#d_commodity").text(b.attr("data-commodity"));
        $("#d_beneficiary").text(b.attr("data-beneficiary"));
        $("#d_city").text(b.attr("data-city"));
        $("#d_phone").text(b.attr("data-phone"));
        $("#d_period").text(b.attr("data-start") + " 〜 " + b.attr("data-end"));
        $("#d_years").text(b.attr("data-length"));
        $("#d_price").text(b.attr("data-price"));

        // 7 个保险责任，空的就不显示
		//准备一个字符串变量，用来拼接表格的 html 代码
        var html = "";
		//循环，一共循环 7 次，对应 7 条保险保障责任。
        for (var i = 1; i <= 7; i++) {
			//拼接属性名
            var v = b.attr("data-l" + i);
			//判断规则：有内容才展示
            if (v != null && v != "" && v != "0") {
				//满足条件，就拼一行表格
                html += "<tr><td>保障項目 " + i + "</td><td>" + v + "</td></tr>";
            }
        }
        if (html == "") {
			//单元格跨2列显示提示
            html = "<tr><td colspan='2' class='text-muted'>追加の保障はありません</td></tr>";
        }
		//把拼好的整个表格内容，放到页面上 id = `d_liability` 的容器里面渲染出来
        $("#d_liability").html(html);

        $("#detailBox").show();
		//平滑滚动页面，滚动到这个详情盒子的位置，动画耗时200毫秒，方便用户直接看到保障详情。
        $("html, body").animate({ scrollTop: $("#detailBox").offset().top }, 200);
    });

    /* ===== 5、关闭详情卡片 ===== */
    $("#btnCloseDetail").click(function () {
        $("#detailBox").hide();
    });

});
