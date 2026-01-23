package chapter6.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import chapter6.beans.User;
import chapter6.beans.UserComment;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/index.jsp", "/top" })
public class TopServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");
	final String DATE_FORMAT = "yyyy-MM-dd";

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public TopServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		boolean isShowMessageForm = false;
		User user = (User) request.getSession().getAttribute("loginUser");
		if (user != null) {
			isShowMessageForm = true;
		}

		String userId = request.getParameter("user_id");
		String startDate = request.getParameter("start_date");
		String endDate = request.getParameter("end_date");

		try {

			if (!StringUtils.isBlank(startDate)) {
				DateUtils.parseDateStrictly(startDate, new String[] { DATE_FORMAT });
			}

			if (!StringUtils.isBlank(endDate)) {
				DateUtils.parseDateStrictly(endDate, new String[] { DATE_FORMAT });
			}

		} catch (ParseException e) {
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		List<UserMessage> messages = new MessageService().select(userId, startDate, endDate);
		List<UserComment> comments = new CommentService().select();

		request.setAttribute("messages", messages);
		request.setAttribute("comments", comments);
		request.setAttribute("start_date", startDate);
		request.setAttribute("end_date", endDate);
		request.setAttribute("isShowMessageForm", isShowMessageForm);
		request.getRequestDispatcher("/top.jsp").forward(request, response);
	}
}
