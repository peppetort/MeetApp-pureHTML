package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.dao.ParticipationDAO;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AddParticipants")
public class AddParticipants extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Meeting meeting = (Meeting) session.getAttribute("meeting");
		Integer times = (Integer) session.getAttribute("times");

		String path = getServletContext().getContextPath();

		String[] checkedIds = request.getParameterValues("checked");

		MeetingDAO meetingDAO = new MeetingDAO(connection);
		ParticipationDAO participationDAO = new ParticipationDAO(connection);

		List<Integer> selected = new ArrayList<>();

		if (checkedIds != null) {
			for (String sId : checkedIds) {
				sId = StringEscapeUtils.escapeJava(sId);
				if (isWrong(sId)) {
					path = path + "/error.html";
					response.sendRedirect(path);
					return;
				}
				try {
					selected.add(Integer.parseInt(sId));
				}catch (NumberFormatException e){
					path = path + "/error.html";
					response.sendRedirect(path);
					return;
				}
			}
		}

		if (selected.isEmpty() || selected.size() > meeting.getMaxParticipants()) {
			session.setAttribute("difference", selected.size() - meeting.getMaxParticipants());
			if (times < 3) {
				session.setAttribute("selected", selected);
				path = path + "/GetRegistry";
			} else {
				path = path + "/GetDeleting";
			}
			response.sendRedirect(path);
			return;
		}

		try {
			meetingDAO.createMeeting(meeting);
			meeting = meetingDAO.checkExistence(meeting.getTitle());
			participationDAO.addParticipants(meeting, selected);
			path = path + "/GetHome";
		} catch (SQLException e) {
			try {
				meeting = meetingDAO.checkExistence(meeting.getTitle());
				if (meeting != null) {
					meetingDAO.deleteMeeting(meeting);
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			e.printStackTrace();
			path = path + "/error.html";
		}

		response.sendRedirect(path);

	}
}
