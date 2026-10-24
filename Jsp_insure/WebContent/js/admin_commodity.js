/*
 * 商品管理 JS
 * 页面上方是一个「新增 / 修改」共用的表单，
 * 点列表里的「編集」会把那一行的数据填进表单，再点「更新」保存。
 * 全部用 $.ajax 的写法。后台返回的数字含义：
 *   1 成功 / 2 输入不合法 / 3 已经有人买过，不能删（要改用下架） / 0 出错
 */

$(function () {

    /* ===== 1、价格、年龄这几个框只能输数字 =====
     * 用法：<input class="form-control numOnly" id="c_price">
     * keyup = 松开键盘时触发；[^0-9] = 「不是数字的都替换成空」
     */
    $(document).on("keyup", ".numOnly", function () {
        this.value = this.value.replace(/[^0-9]/g, "");
    });

    /* ===== 2、搜索框按回车就查询 ===== */
    $("#keyword").keydown(function (e) {
        if (e.keyCode == 13) {
            $(this).closest("form").submit();
        }
    });


    /* ===== 3、点「編集」：把这一行的数据填进上面的表单 =====
     * 数据都存在按钮的 data-* 里，不用再查一次数据库
	 *	'b.attr("data-id")'：读取按钮上 'data-id' 的值
	 *	'$("#xxx").val(值)'：给页面上方表单里对应的输入框赋值、回填数据
     */
    $(document).on("click", ".btn-edit", function () {
        var b = $(this);

        $("#commodity_id").val(b.attr("data-id"));
        $("#c_name").val(b.attr("data-name"));
        $("#c_price").val(b.attr("data-price"));
        $("#c_type").val(b.attr("data-type"));
        $("#c_img").val(b.attr("data-img"));
        $("#c_introduce").val(b.attr("data-introduce"));
        $("#c_time_length").val(b.attr("data-time"));
        $("#c_age_start").val(b.attr("data-as"));
        $("#c_age_end").val(b.attr("data-ae"));
     //修改保存按钮文字：从'追加'改成'更新する'；样式从蓝色主按钮换成橙色警告按钮
        $("#btnSave").text("更新する").removeClass("btn-primary").addClass("btn-warning");
		//修改表单标题，提示现在是编辑哪一条商品
        $("#formTitle").text("商品情報を編集（ID：" + b.attr("data-id") + "）");

        // 平滑滚动到页面最顶部，让用户一眼看到表单
        $("html, body").animate({ scrollTop: 0 }, 200);
    });

    /* ===== 4、点「クリア」：清空表单，回到「新增」模式 ===== */
    $("#btnClear").click(function () {
        $("#commodity_id").val("");
        $("#c_name").val("");
        $("#c_price").val("");
        $("#c_type").val("");
        $("#c_img").val("");
        $("#c_introduce").val("");
        $("#c_time_length").val("");
        $("#c_age_start").val("");
        $("#c_age_end").val("");

        $("#btnSave").text("新規追加").removeClass("btn-warning").addClass("btn-primary");
        $("#formTitle").text("商品を新規追加");
    });

    /* ===== 5、点「新規追加 / 更新する」=====
     * 看隐藏域 commodity_id 有没有值：
     *   空  → 新增（opt=admin_commodity_add）
     *   有值 → 修改（opt=admin_commodity_update）
     */
    $("#btnSave").click(function () {

        //1、前端先校验一遍，不对就 alert
        var name  = $.trim($("#c_name").val());
        var price = $.trim($("#c_price").val());
        var type  = $.trim($("#c_type").val());

        if (name == "") {
            alert("商品名を入力してください");
            return;
        }
        if (price == "" || isNaN(price) || parseFloat(price) <= 0) {
            alert("価格は0より大きい数値で入力してください");
            return;
        }
        if (type == "") {
            alert("商品タイプを選択してください");
            return;
        }

        //2、收集表单要提交的数据 
        var data = {
            name       : name,
            price      : price,
            type       : type,
            img        : $("#c_img").val(),
            introduce  : $("#c_introduce").val(),
            timeLength : $("#c_time_length").val(),
            ageStart   : $("#c_age_start").val(),
            ageEnd     : $("#c_age_end").val()
        };
		//同时准备根据有没有商品 ID，来决定是走【新增】还是【修改】接口
        var id = $("#commodity_id").val();
        var url;
        var okMsg;

        if (id == "") {
            url   = ctx + "/AdminCommodityServlet?opt=admin_commodity_add";
            okMsg = "商品を追加しました！";
        } else {
            url   = ctx + "/AdminCommodityServlet?opt=admin_commodity_update";
            okMsg = "商品を更新しました！";
            data.commodityId = id;
        }

        // 3、发 Ajax
        $.ajax({
            url: url,
            type: "post",
            data: data,
            success: function (date) {
                if (date == 1) {
                    alert(okMsg);
                    location.reload();
                } else if (date == 2) {
                    alert("入力値が正しくありません！");
                } else {
                    alert("保存に失敗しました！");
                }
            },
            error: function () {
                alert("ネットワークにエラーがありました。後で再試行してください！");
            }
        });
    });

    /* ===== 6、上架 / 下架 ===== */
    $(document).on("click", ".btn-shelf", function () {
		//拿到输入框里面，用户填的值
        var id     = $(this).attr("data-id");
        var status = $(this).attr("data-status");
        var word   = (status == "0") ? "下架（販売停止）" : "上架（販売再開）";

        if (!confirm("この商品を「" + word + "」にしますか？")) {
            return;
        }

        $.ajax({
            url: ctx + "/AdminCommodityServlet?opt=admin_commodity_status",
            type: "post",
            data: {"commodityId": id, "status": status},
            success: function (date) {
                if (date == 1) {
                    alert("状態を更新しました！");
                    location.reload();
                } else if (date == 2) {
                    alert("入力値が正しくありません！");
                } else {
                    alert("状態の更新に失敗しました！");
                }
            },
            error: function () {
                alert("ネットワークにエラーがありました。後で再試行してください！");
            }
        });
    });

    /* ===== 7、删除商品 =====
     * 返回 3 = 已经有人买过这个商品，不能删（删了历史订单就查不到商品名了）
     */
    $(document).on("click", ".btn-delete", function () {
        var id = $(this).attr("data-id");

        if (!confirm("本当に削除しますか？（元に戻せません）")) {
            return;
        }

        $.ajax({
            url: ctx + "/AdminCommodityServlet?opt=admin_commodity_delete",
            type: "post",
            data: {"commodityId": id},
            success: function (date) {
                if (date == 1) {
                    alert("削除しました！");
                    location.reload();
                } else if (date == 2) {
                    alert("入力値が正しくありません！");
                } else if (date == 3) {
                    alert("この商品は既に購入されているため削除できません。「下架」を使用してください！");
                } else {
                    alert("削除に失敗しました！");
                }
            },
            error: function () {
                alert("ネットワークにエラーがありました。後で再試行してください！");
            }
        });
    });

});
