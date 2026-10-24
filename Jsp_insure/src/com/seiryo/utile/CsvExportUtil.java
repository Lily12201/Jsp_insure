package com.seiryo.utile;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CSV 导出工具（不需要装任何 jar）
 * 1、为什么不用 Excel（POI）？
 *   真正的 .xlsx 需要 Apache POI 的三个 jar（poi、poi-ooxml、xmlbeans），
 *   CSV 也是表格文件，Excel 双击就能打开，
 * 2、CSV 是什么？
 *   就是一个纯文本文件，每行一条数据，字段之间用逗号隔开：
 *       性別,人数,割合(%)
 *       男,12,35.3
 *       女,22,64.7
 * 3、两个必须注意的坑：
 *   ① Excel 打开 UTF-8 的 CSV 会显示乱码 → 在文件最开头写 3 个字节的 BOM 就正常了
 *   ② 字段里如果本身有逗号或双引号，会破坏格式 → 用双引号把整个字段包起来
 */
public class CsvExportUtil {

    /**
     * 导出 CSV
     * @param req       请求（用来读浏览器类型，处理文件名编码）
     * @param resp      响应（往里写文件内容）
     * @param title     第一行的大标题，比如「会員性別分布統計」
     * @param headers   表头数组
     * @param rows      数据：每一行是一个 Object[]
     * @param fileName  下载下来的文件名（不要带 .csv，这里会自动加）
     */
    public static void export(HttpServletRequest req, HttpServletResponse resp,
                              String title, String[] headers, List<Object[]> rows,
                              String fileName) throws Exception {

        // ---------- 1、处理下载文件名（中文文件名要编码，否则下载下来是乱码）----------
        String name = fileName + ".csv";
        // 中文文件名必须先编码，不编码下载下来就是乱码或一堆下划线
        String encodedName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");

        // ---------- 2、告诉浏览器：这不是网页，是个要下载的附件 ----------
        resp.reset();
        resp.setContentType("application/x-download;charset=UTF-8");
        // 写两个 filename：老浏览器认前面那个，新浏览器认 filename*（Chrome / Edge / Firefox 都支持）
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName);

        // ---------- 3、写文件内容 ----------
        OutputStream os = resp.getOutputStream();

        // BOM：这 3 个字节让 Excel 知道「这个文件是 UTF-8」，不加中文就乱码
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));

        // 第一行：大标题
        out.println(escape(title));

        // 第二行：表头
        out.println(join(headers));

        // 后面：数据行
        if (rows != null) {
            for (Object[] row : rows) {
                out.println(join(row));
            }
        }

        out.flush();
        out.close();

        // 注意：导出分支到此结束，不要再 getWriter() 或转发页面，会报错
    }

    /** 把一行数据拼成 "值1,值2,值3" */
    private static String join(Object[] cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(escape(cells[i]));
        }
        return sb.toString();
    }

    /** 单个值的安全处理：null 变空，含逗号/引号/换行的用双引号包起来 */
    private static String escape(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value);
        boolean needQuote = s.indexOf(',') >= 0 || s.indexOf('"') >= 0
                         || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
        if (needQuote) {
            // CSV 里表示一个双引号要写两个双引号
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
