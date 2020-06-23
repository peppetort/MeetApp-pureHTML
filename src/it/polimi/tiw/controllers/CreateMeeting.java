package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingDAO;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("user")).getId();

		String title = StringEscapeUtils.escapeJava(request.getParameter("title"));
		String sDate = StringEscapeUtils.escapeJava(request.getParameter("date"));
		String sStartTime = StringEscapeUtils.escapeJava(request.getParameter("start"));
		String sHours = StringEscapeUtils.escapeJava(request.getParameter("hours"));
		String sMinutes = StringEscapeUtils.escapeJava(request.getParameter("minutes"));
		String sMaxParticipants = StringEscapeUtils.escapeJava(request.getParameter("participants"));

		MeetingDAO meetingDAO = new MeetingDAO(connection);

		String path = getServletContext().getContextPath();

		String error;

		if (isWrong(title) || isWrong(sDate) || isWrong(sStartTime) || isWrong(sHours)
				|| isWrong(sMinutes) || isWrong(sMaxParticipants)) {
			error = "Invalid data";
			session.setAttribute("error", error);
			path = path + "/GetHome";
			response.sendRedirect(path);
			return;
		}


		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat timeFormat = new SimpleDateFormat("HH:mm");

			int hours = Integer.parseInt(sHours);
			int minutes = Integer.parseInt(sMinutes);

			if (hours < 0 || hours > 24 || minutes < 0 || minutes > 60) {
				error = "Hour interval must be between 1 and 24 hours";
				session.setAttribute("error", error);
				path = path + "/GetHome";
				response.sendRedirect(path);
				return;
			}

			Time startTime = new Time(timeFormat.parse(sStartTime).getTime());
			LocalTime localtime = startTime.toLocalTime();
			localtime = localtime.plusHours(hours);
			localtime = localtime.plusMinutes(minutes);
			Time endTime = Time.valueOf(localtime);

			int maxParticipants = Integer.parseInt(sMaxParticipants);

			if (maxParticipants < 1) {
				error = "There must be at least one participant";
				session.setAttribute("error", error);
				path = path + "/GetHome";
				response.sendRedirect(path);
				return;
			}

			Date meetingDate = new Date(dateFormat.parse(sDate).getTime());
			Date actualDate = new Date(new java.util.Date().getTime());
			Time actualTime = Time.valueOf(LocalTime.now());

			if (actualDate.toLocalDate().isAfter(meetingDate.toLocalDate())
					|| (actualDate.toLocalDate().equals(meetingDate.toLocalDate())
					&& actualTime.toLocalTime().isAfter(startTime.toLocalTime()))) {
				error = "The meeting must be later";
				session.setAttribute("error", error);
				path = path + "/GetHome";
				response.sendRedirect(path);
				return;
			}

			Meeting meeting = meetingDAO.checkExistence(title);

			if (meeting == null) {
				meeting = new Meeting(userId, title, meetingDate, startTime, endTime, maxParticipants);
				session.setAttribute("meeting", meeting);
				session.setAttribute("times", 0);
				path = path + "/GetRegistry";
			} else {
				error = meeting.getTitle() + " already exists";
				session.setAttribute("error", error);
				path = path + "/GetHome";
			}
		} catch (IllegalArgumentException | ParseException e1) {
			error = "Invalid data format";
			session.setAttribute("error", error);
			path = path + "/GetHome";
		} catch (SQLException e2) {
			e2.printStackTrace();
			path = path + "/error.html";
		}

		response.sendRedirect(path);
	}
}
