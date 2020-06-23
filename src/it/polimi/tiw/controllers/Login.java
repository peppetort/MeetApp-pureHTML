package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/Login")
public class Login extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));

		UserDAO userDAO = new UserDAO(connection);
		String path = getServletContext().getContextPath();
		HttpSession session = request.getSession(true);

		String error;
		User user;

		if (isWrong(username) || isWrong(password)) {
			error = "Invalid credentials";
			session.setAttribute("error", error);
			path = path + "/GetIndex";
			response.sendRedirect(path);
			return;
		}

		try {
			user = userDAO.checkExistence(username);

			if (user != null) {
				if (user.getPassword().equals(password)) {
					session.setAttribute("user", user);
					path = path + "/GetHome";
				} else {
					error = "Invalid Password";
					session.setAttribute("error", error);
					path = path + "/GetIndex";
				}
			} else {
				error = "Unknown user";
				session.setAttribute("error", error);
				path = path + "/GetIndex";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			path = path + "/error.html";
		}


		response.sendRedirect(path);
	}
}