/**
 * 后台登录 / 注册
 *   前端先判空 → alert 提示 → 通过了再 $.ajax 提交 → 按返回的数字判断结果
 *
 * 后台返回的数字含义：
 *   登录：1 成功 / 0 账号或密码错误 / 2 账号已被停用
 *   注册：1 成功 / 0 账号已存在 / 2 输入不合法 / 3 登録コード不对 / -1 数据库出错
 */
$(function () {

    /* ===== 登录框 / 注册框 互相切换 ===== */
    $("#toReg").click(function () {
        $("#loginBox").hide();
        $("#regBox").show();
    });
    $("#toLogin").click(function () {
        $("#regBox").hide();
        $("#loginBox").show();
    });

    /* ===== 1、登录 ===== */
    $("#admin_login").click(function () {
        // 1、取得输入的账号和密码
        var account  = $("#account").val();
        var password = $("#password").val();

        // 2、非空判断
        var bo = false;
        if (account.length > 0) {
            if (password.length > 0) {
                bo = true;
            } else {
                alert("パスワードを空にすることはできませんよ！");
            }
        } else {
            alert("アカウントを空にすることはできませんよ！");
        }

        // 3、通过了才发 Ajax
        if (bo) {
            $.ajax({
                url: ctx + "/AdminLoginServlet?opt=admin_login",
                type: "post",
                data: {"account": account, "password": password},
                dataType: "json",
                success: function (date) {
                    if (date == 1) {
                        alert("ログイン成功おめでとうございます。もうすぐ画面をジャンプします！");
                        window.location.href = ctx + "/admin_main.jsp";
                    } else if (date == 2) {
                        alert("このアカウントは無効です！管理者に連絡してください。");
                    } else {
                        alert("アカウントまたはパスワードが間違っている！");
                    }
                },
                error: function () {
                    alert("ネットワークにエラーがありました。後で再試行してください！");
                }
            });
        }
    });

    /* ===== 2、回车也能登录 ===== */
    $("#password").keydown(function (e) {
        if (e.keyCode == 13) {
            $("#admin_login").click();
        }
    });

    /* ===== 3、注册（新增管理员账号）===== */
    $("#admin_register").click(function () {
        // 1、取得输入
        var account  = $("#reg_account").val();
        var password = $("#reg_password").val();
        var name     = $("#reg_name").val();
        var regCode  = $("#reg_code").val();

        // 2、前端先判一遍（后端 Service 里还会再判一次，双保险）
        var bo = false;
        if (account.length < 3) {
            alert("アカウントは3文字以上で入力してください！");
        } else if (password.length < 6) {
            alert("パスワードは6文字以上で入力してください！");
        } else if (regCode.length == 0) {
            alert("登録コードを入力してください！");
        } else {
            bo = true;
        }

        // 3、发 Ajax
        if (bo) {
            $.ajax({
                url: ctx + "/AdminLoginServlet?opt=admin_register",
                type: "post",
                data: {"account": account, "password": password,
                       "name": name, "regCode": regCode},
                dataType: "json",
                success: function (date) {
                    if (date == 1) {
                        alert("登録しました！ログインしてください。");
                        // 切回登录框，并帮他把账号填好
                        $("#regBox").hide();
                        $("#loginBox").show();
                        $("#account").val(account);
                        $("#password").focus();
                    } else if (date == 0) {
                        alert("このアカウントは既に存在します！");
                    } else if (date == 2) {
                        alert("入力値が正しくありません！（アカウント3～20文字、パスワード6文字以上）");
                    } else if (date == 3) {
                        alert("登録コードが正しくありません！");
                    } else {
                        alert("登録に失敗しました！");
                    }
                },
                error: function () {
                    alert("ネットワークにエラーがありました。後で再試行してください！");
                }
            });
        }
    });

});
