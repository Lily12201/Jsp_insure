package com.seiryo.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seiryo.entity.AdminUser;
import com.seiryo.service.AdminAccountService;
import com.seiryo.service.impl.AdminAccountServiceImpl;

/**
 * 权限规则（两条）：
 *   ① 管理人＝「総管理者」(admin_role=1)：能看全部账号、能增删改别人
 *   ② 一般管理者 (admin_role=2)：只能看到自己，只能改自己的密码
 *   另外所有人都不许停用/删除/改自己，也不许把最后一个总管理员弄没
 *
 * 访问地址：
 *   列表      ?opt=admin_account_list
 *   新增      ?opt=admin_account_add
 *   停用启用  ?opt=admin_account_status&adminId=2&status=0
 *   重置密码  ?opt=admin_account_resetpwd&adminId=2&password=xxx
 *   升降级    ?opt=admin_account_role&adminId=2&role=1
 *   删除      ?opt=admin_account_delete&adminId=2
 *   改自己密码 ?opt=admin_account_changepwd  （AJAX，返回数字）
 */
@WebServlet("/AdminAccountServlet")
public class AdminAccountServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private AdminAccountService adminAccountService = new AdminAccountServiceImpl();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 编码格式（也可以通过过滤器设置）
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		// 没登录不让用
		HttpSession session = req.getSession();
		AdminUser me = (AdminUser) session.getAttribute(AdminLoginServlet.SESSION_ADMIN);
		if (me == null) {
			resp.sendRedirect(req.getContextPath() + "/admin_login.jsp");
			return;
		}

		// 通过判断进行设置调用方法
		String opt = req.getParameter("opt");
		if (opt == null || opt.equals("")) {
			opt = "admin_account_list";
		}

		if ("admin_account_add".equals(opt)) {
			accountAdd(req, resp, me);
		} else if ("admin_account_status".equals(opt)) {
			accountStatus(req, resp, me);
		} else if ("admin_account_resetpwd".equals(opt)) {
			accountResetPwd(req, resp, me);
		} else if ("admin_account_role".equals(opt)) {
			accountRole(req, resp, me);
		} else if ("admin_account_delete".equals(opt)) {
			accountDelete(req, resp, me);
		} else if ("admin_account_changepwd".equals(opt)) {
			changeOwnPassword(req, resp, me);
		} else {
			accountList(req, resp, me);
		}
	}

	/**
	 * 管理员列表
	 * @Title: accountList
	 * @Description: 总管理员看全部账号，普通管理员只看自己那一行
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountList(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		List<AdminUser> accountList =
				adminAccountService.getAccountList(myRole(me), myId(me));

		req.setAttribute("accountList", accountList);
		req.getRequestDispatcher("/admin_account_list.jsp").forward(req, resp);
	}

	/**
	 * 新增管理员（总管理员给新人开账号）
	 * @Title: accountAdd
	 * @Description: 只有总管理员能用。role 传 1 或 2，决定新人是总管理员还是普通
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountAdd(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.addAccount(
				req.getParameter("account"),
				req.getParameter("password"),
				req.getParameter("name"),
				req.getParameter("role"),
				myRole(me));
		backToList(req, resp, code, "管理者を追加しました！", "追加に失敗しました！");
	}

	/**
	 * 启用 / 停用管理员
	 * @Title: accountStatus
	 * @Description: 不能停用自己；也不能把最后一个总管理员停掉
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountStatus(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.changeStatus(
				req.getParameter("adminId"), req.getParameter("status"),
				myRole(me), myId(me));
		backToList(req, resp, code, "状態を更新しました！", "状態の更新に失敗しました！");
	}

	/**
	 * 重设「别人」的密码
	 * @Title: accountResetPwd
	 * @Description: 用于「管理员忘了密码」这类情况。改自己的密码请走 changeOwnPassword
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountResetPwd(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.resetPassword(
				req.getParameter("adminId"), req.getParameter("password"),
				myRole(me), myId(me));
		backToList(req, resp, code, "パスワードを再設定しました！", "再設定に失敗しました！");
	}

	/**
	 * 升降级：把某个账号改成总管理员 / 普通管理员
	 * @Title: accountRole
	 * @Description: 不能改自己；也不能把最后一个总管理员降成普通
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountRole(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.changeRole(
				req.getParameter("adminId"), req.getParameter("role"),
				myRole(me), myId(me));
		backToList(req, resp, code, "権限を変更しました！", "権限の変更に失敗しました！");
	}

	/**
	 * 删除管理员账号
	 * @Title: accountDelete
	 * @Description: 一般建议用「停用」而不是「删除」
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void accountDelete(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.deleteAccount(
				req.getParameter("adminId"), myRole(me), myId(me));
		backToList(req, resp, code, "削除しました！", "削除に失敗しました！");
	}

	/**
	 * 改「自己」的密码（所有人都能用，包括总管理员）
	 * @Title: changeOwnPassword
	 * @Description: 必须输对旧密码才能改。总管理员密码泄露时就用这个改。
	 *               走 AJAX，返回数字给前端弹提示（用 print，不能用 println）
	 * @param @param req
	 * @param @param resp
	 * @param @param me 当前登录的管理员
	 * @param @throws ServletException
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void changeOwnPassword(HttpServletRequest req, HttpServletResponse resp, AdminUser me)
			throws ServletException, IOException {

		String code = adminAccountService.changeOwnPassword(
				myId(me),
				req.getParameter("oldPassword"),
				req.getParameter("newPassword"));

		// 注意：必须用 print 不能用 println（println 会多一个换行符，JS 判断会失败）
		resp.getWriter().print(code);
	}

	/**
	 * 取当前登录者的角色（1 总管理员 / 2 普通）
	 * @Title: myRole
	 * @param @param me 当前登录的管理员
	 * @return int    返回类型
	 * @throws
	 */
	private int myRole(AdminUser me) {
		if (me == null || me.getAdmin_role() == null) {
			return 2;                 // 取不到就当普通，权限宁可给小
		}
		return me.getAdmin_role().intValue();
	}

	/**
	 * 取当前登录者的 id
	 * @Title: myId
	 * @param @param me 当前登录的管理员
	 * @return int    返回类型
	 * @throws
	 */
	private int myId(AdminUser me) {
		if (me == null || me.getAdmin_id() == null) {
			return 0;
		}
		return me.getAdmin_id().intValue();
	}

	/**
	 * 操作完跳回列表页，并用 session 带一条提示消息
	 * @Title: backToList
	 * @Description: 存两个值（msgType / msgText），JSP 里用 EL 取，不用写 <% %>
	 * @param @param req
	 * @param @param resp
	 * @param @param code 1成功 2输入不合法 3账号已存在 4権限なし 5自分は不可 6最後の総管理者 0失败
	 * @param @param okMsg 成功时显示的话
	 * @param @param errMsg 失败时显示的话
	 * @param @throws IOException    参数
	 * @return void    返回类型
	 * @throws
	 */
	private void backToList(HttpServletRequest req, HttpServletResponse resp,
			String code, String okMsg, String errMsg) throws IOException {

		HttpSession session = req.getSession();

		if ("1".equals(code)) {
			session.setAttribute("msgType", "success");
			session.setAttribute("msgText", okMsg);
		} else {
			session.setAttribute("msgType", "danger");
			if ("2".equals(code)) {
				session.setAttribute("msgText",
						"入力値が正しくありません！（アカウント3～20文字、パスワード6文字以上）");
			} else if ("3".equals(code)) {
				session.setAttribute("msgText", "このアカウントは既に存在します！");
			} else if ("4".equals(code)) {
				session.setAttribute("msgText",
						"権限がありません。アカウントの管理は総管理者のみ可能です。");
			} else if ("5".equals(code)) {
				session.setAttribute("msgText",
						"自分自身のアカウントは変更できません。パスワードは下の「パスワード変更」から変更してください。");
			} else if ("6".equals(code)) {
				session.setAttribute("msgText",
						"これ以上総管理者を減らせません（最低1人必要です）。");
			} else {
				session.setAttribute("msgText", errMsg);
			}
		}

		resp.sendRedirect(req.getContextPath() + "/AdminAccountServlet?opt=admin_account_list");
	}
}
