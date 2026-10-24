/*
 * 用户管理 JS
 * 放这里：WebContent/js/admin_user.js
 * 用在：WebContent/admin/admin_user_list.jsp
 *
 * 全部用 $.ajax 的写法（和你前台 js/login.js 一模一样）：
 *   前端先判断 → confirm 确认 → $.ajax 提交 → 按返回的数字弹提示
 *
 * 后台返回的数字含义：
 *   1 成功 / 2 输入不合法 / 0 出错（用户管理没有返回 3 的情况）
 *
 * 页面上按钮只要加 class 和 data-*，这里就自动接管，不用写 onclick：
 *   <button class="btn btn-sm btn-warning btn-status"
 *           data-id="${u.user_info_id}" data-status="0">無効</button>
 */

$(function () {

    /* ===== 1、搜索框按回车就查询 =====
     * keyCode 13 = 回车键。$(this).closest("form") = 找到它外面包着的表单
     */
    $("#keyword").keydown(function (e) {
        if (e.keyCode == 13) {
            $(this).closest("form").submit();
        }
    });
	
    /* ===== 2、导出 Excel 时给个提示 =====
     * 用法：<a href="..." class="btn btn-success btn-export">Excel出力</a>
     */
    $(document).on("click", ".btn-export", function () {
        alert("Excel を出力しています。しばらくお待ちください。");
    });

    /* ===== 3、禁用 / 启用 ===== */
    $(document).on("click", ".btn-status", function () {
        var id     = $(this).attr("data-id");
        var status = $(this).attr("data-status");
        var word   = (status == "0") ? "無効" : "有効";

        // 1、先确认
        if (!confirm("この会員を「" + word + "」に変更しますか？")) {
            return;
        }

        // 2、发 Ajax
        $.ajax({
            url: ctx + "/AdminUserServlet?opt=admin_user_status",
            type: "post",
            data: {"userInfoId": id, "status": status},
            success: function (date) {
                if (date == 1) {
                    alert("状態を更新しました！");
					// 刷新页面，看到最新状态
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

    /* ===== 4、充值 ===== */
    $(document).on("click", ".btn-charge", function () {
        var id = $(this).attr("data-id");

        // 1、弹出输入框要充多少
        var m = prompt("チャージ金額を入力してください（正の整数）");
        if (m == null || m == "") {
            return;                            
        }
        if (isNaN(m) || parseInt(m) <= 0) {
            alert("正の整数を入力してください");
            return;
        }

        // 2、发 Ajax
        $.ajax({
            url: ctx + "/AdminUserServlet?opt=admin_user_charge",
            type: "post",
            data: {"userInfoId": id, "addMoney": m},
            success: function (date) {
                if (date == 1) {
                    alert("チャージしました！");
                    location.reload();
                } else if (date == 2) {
                    alert("入力値が正しくありません！");
                } else {
                    alert("チャージに失敗しました！");
                }
            },
            error: function () {
                alert("ネットワークにエラーがありました。後で再試行してください！");
            }
        });
    });

    /* ===== 5、删除单个会员 =====
     * 为什么传两个 id？因为会员的数据存在两张表里：
     */
    $(document).on("click", ".btn-delete", function () {
        var userId     = $(this).attr("data-id");
        var userInfoId = $(this).attr("data-infoid");

        if (!confirm("本当に削除しますか？（元に戻せません）")) {
            return;
        }

        $.ajax({
            url: ctx + "/AdminUserServlet?opt=admin_user_delete",
            type: "post",
            data: {"userId": userId, "userInfoId": userInfoId},
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

});
