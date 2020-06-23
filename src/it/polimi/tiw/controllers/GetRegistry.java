package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetRegistry")
public class GetRegistry extends HttpServletGetter {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletContext servletContext = getServletContext();
		WebContext ctx = new WebContext(request, response, servletContext);

		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("user")).getId();
		Integer times = (Integer) session.getAttribute("times");
		Meeting meeting = (Meeting) session.getAttribute("meeting");
		Integer difference = (Integer) session.getAttribute("difference");
		List<Integer> selected = (List<Integer>) session.getAttribute("selected");

		UserDAO userDAO = new UserDAO(connection);

		String path = getServletContext().getContextPath();
		String page = "registry";
		String error;

		try{
			List<User> allUsers = userDAO.getAllUsersBut(userId);

			if(allUsers.isEmpty()){
				error = "You are the only user!";
				session.setAttribute("error", error);
				path = path + "/GetDeleting";
				response.sendRedirect(path);
			}else {
				ctx.setVariable("users", allUsers);
				ctx.setVariable("meeting", meeting);
				ctx.setVariable("times", times+1);

				if(difference != null){
					ctx.setVariable("difference", difference);
				}

				if(selected != null){
					ctx.setVariable("selected", selected);
				}

				session.setAttribute("times", times+1);
				templateEngine.process(page, ctx, response.getWriter());
			}
		} catch (SQLException e){
			e.printStackTrace();
			path = path + "/error.html";
			response.sendRedirect(path);
		}


	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
