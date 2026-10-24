/*
 * 这个页面有两类操作：
 *   ① 启用/禁用、升降级、删除、重设别人密码 → 点链接跳 Servlet（页面刷新）
 *      这里只负责：点之前弹确认框
 *   ② 改「自己」的密码 → AJAX（不刷新页面），Servlet 返回一个数字
 */

$(function () {

    /*
     * 1、改「自己」的密码（AJAX）
     * Servlet 返回的数字含义：
     *   1 = 成功
     *   2 = 输入不合法（新密码少于 6 位）
     *   3 = 新密码和旧密码一样
     *   0 = 旧密码不对 / 数据库出错
    */
    $("#btnChangePwd").click(function () {

        // --- 1、取出三个输入框的值 ---
        var oldPwd = $.trim($("#oldPassword").val());
        var newPwd = $.trim($("#newPassword").val());
        var newPwd2 = $.trim($("#newPassword2").val());

        //  2、前端先检查一次（少一次白跑服务器的请求）
		//为了让提示信息一直显示在按钮下面，避免管理用户忘记，所以没有和admin_login.js一样用alert()来提示错误消息
        if (oldPwd == "" || newPwd == "" || newPwd2 == "") {
            $("#pwdMsg").html('<span class="text-danger">すべて入力してください。</span>');
            return;
        }
        if (newPwd.length < 6) {
            $("#pwdMsg").html('<span class="text-danger">新しいパスワードは6文字以上で入力してください。</span>');
            return;
        }
        if (newPwd != newPwd2) {
            $("#pwdMsg").html('<span class="text-danger">新しいパスワードが一致しません。</span>');
            return;
        }
        if (oldPwd == newPwd) {
            $("#pwdMsg").html('<span class="text-danger">現在のパスワードと同じです。別のパスワードを入力してください。</span>');
            return;
        }

        //3、发 AJAX 
        $.ajax({
            url: ctx + "/AdminAccountServlet?opt=admin_account_changepwd",
            type: "post",
            data: {
                oldPassword: oldPwd,
                newPassword: newPwd
            },
            success: function (res) {
                // Servlet 用 print 返回，可能带空格，先去掉
                res = $.trim(res);
				//这里我设置dataType: "json"，jQuery 原样给字符串 "1"，所以要写 res == "1"。
                if (res == "1") {
                    $("#pwdMsg").html('<span class="text-success">パスワードを変更しました。次回ログインから新しいパスワードを使ってください。</span>');
                    // 清空三个框
                    $("#oldPassword").val("");
                    $("#newPassword").val("");
                    $("#newPassword2").val("");
                } else if (res == "2") {
                    $("#pwdMsg").html('<span class="text-danger">入力値が正しくありません（6文字以上）。</span>');
                } else if (res == "3") {
                    $("#pwdMsg").html('<span class="text-danger">現在のパスワードと同じです。</span>');
                } else {
                    $("#pwdMsg").html('<span class="text-danger">現在のパスワードが違います。</span>');
                }
            },
            error: function () {
                $("#pwdMsg").html('<span class="text-danger">通信エラーが発生しました。</span>');
            }
        });
    });

    /*
     * 2、启用 / 禁用：点之前先确认
     * 注意：这里绑的是 <a href="...">，不是按钮。
     * confirm 点「キャンセル」时 return false → 阻止它跳转。
     * （按钮要用 return;，链接必须 return false;）
     * */
    $(document).on("click", ".btn-status", function () {
        var word = $(this).hasClass("btn-warning") ? "無効" : "有効";
        if (!confirm("このアカウントを「" + word + "」に変更しますか？")) {
            return false;
        }
    });

    /* 
     * 3、升降级：点之前先确认
     */
    $(document).on("click", ".btn-role", function () {
        var word = $(this).hasClass("btn-outline-danger") ? "総管理者" : "一般管理者";
        if (!confirm("このアカウントを「" + word + "」に変更しますか？")) {
            return false;
        }
    });

    /* 
     * 4、删除：点之前先确认
    */
    $(document).on("click", ".btn-del", function () {
        if (!confirm("この管理者を削除しますか？（元に戻せません）")) {
            return false;
        }
    });

    /* 
     * 5、重设「别人」的密码
     * data-id / data-name 是 HTML5 的自定义属性，
     * 这个是「别人忘了密码，总管理员强制改」，不用旧密码验证。
     */
    $(document).on("click", ".btn-resetpwd", function () {
        var id   = $(this).attr("data-id");
        var name = $(this).attr("data-name");

        // 1、弹出输入框要新密码
        var p = prompt(name + " の新しいパスワードを入力してください（6文字以上）");
        if (p == null || p == "") {
            return;                     
        }
        if (p.length < 6) {
            alert("6文字以上で入力してください");
            return;
        }

        // 2、跳 Servlet（页面会刷新，Servlet 里存了 msgText 会显示提示）
        location.href = ctx + "/AdminAccountServlet?opt=admin_account_resetpwd"
                      + "&adminId=" + id + "&password=" + p;
    });

});
