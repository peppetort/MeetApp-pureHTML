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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/Registration")
public class Registration extends HttpServletChecker {
	public final Pattern VALID_EMAIL_ADDRESS_REGEX =
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String password1 = StringEscapeUtils.escapeJava(request.getParameter("password1"));
		String password2 = StringEscapeUtils.escapeJava(request.getParameter("password2"));
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));

		UserDAO userDAO = new UserDAO(connection);
		String path = getServletContext().getContextPath();
		HttpSession session = request.getSession(true);

		String error;
		User user;

		if (isWrong(username) || isWrong(password1) || isWrong(password2)
				|| isWrong(name) || isWrong(surname) || isWrong(email)) {
			error = "Invalid credentials";
			session.setAttribute("error", error);
			path = path + "/GetIndex";
			response.sendRedirect(path);
			return;
		}

		if (!password1.equals(password2)) {
			error = "Unmatched passwords";
			session.setAttribute("error", error);
			path = path + "/GetIndex";
			response.sendRedirect(path);
			return;
		}

		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

		if (!matcher.find()) {
			error = "Invalid email address";
			session.setAttribute("error", error);
			path = path + "/GetIndex";
			response.sendRedirect(path);
			return;
		}

		try {
			user = userDAO.checkExistence(username);

			if (user == null) {
				user = new User(username, name, surname, email, password1);
				userDAO.insertUser(user);
				user = userDAO.checkExistence(username);
				session.setAttribute("user", user);
				path = path + "/GetHome";
			} else {
				error = username + " already exists";
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
