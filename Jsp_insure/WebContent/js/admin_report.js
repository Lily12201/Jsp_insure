/*
 * 统计报表 JS
 * 这个页面没有增删改，只做一件事：点导出按钮时给个提示。
 * 导出本身是 <a href="..."> 直接跳 Servlet 下载文件，不需要 AJAX。
 */

$(function () {

    /* ===== 导出时给个提示 =====
     *
     * 为什么不 return false？
     *   这个提示只是告诉用户「正在下载」，必须让它继续跳转，
     *   浏览器才会真的去请求 Servlet、把文件下载下来。
     *
     * 导出的是 CSV 文件（Excel 双击就能打开）。
     * 项目里没装 Apache POI（生成 .xlsx 要用的 jar），
     * 所以用了零依赖的 CSV 方案，效果一样。
     */
    $(document).on("click", ".btn-export", function () {
        alert("CSV ファイルを出力しています。しばらくお待ちください。（Excel で開けます）");
    });

});
