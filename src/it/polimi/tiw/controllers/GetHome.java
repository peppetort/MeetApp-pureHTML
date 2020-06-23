package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.dao.ParticipationDAO;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetHome")
public class GetHome extends HttpServletGetter {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletContext servletContext = getServletContext();
		WebContext ctx = new WebContext(request, response, servletContext);

		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("user")).getId();
		String error = (String) session.getAttribute("error");

		String path = getServletContext().getContextPath();
		String page = "home";

		MeetingDAO meetingDAO = new MeetingDAO(connection);
		ParticipationDAO participationDAO = new ParticipationDAO(connection);


		try {
			List<Meeting> createdMeetings = meetingDAO.getMeetingsByUser(userId);
			List<Meeting> invitations = participationDAO.getMeetingsByUser(userId);

			ctx.setVariable("myMeetings", createdMeetings);
			ctx.setVariable("invitations", invitations);

			if(error != null){
				ctx.setVariable("error", error);
				session.removeAttribute("error");
			}

			templateEngine.process(page, ctx, response.getWriter());

		} catch (SQLException e) {
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
