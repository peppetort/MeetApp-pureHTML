package it.polimi.tiw.controllers;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/GetDeleting")
public class GetDeleting extends HttpServletGetter {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ServletContext servletContext = getServletContext();
		WebContext ctx = new WebContext(request, response, servletContext);
		HttpSession session = request.getSession();

		String page = "deleting";

		String error = (String) session.getAttribute("error");

		if(error != null){
			ctx.setVariable("error", error);
			session.removeAttribute("error");
		}

		templateEngine.process(page, ctx, response.getWriter());

		session.removeAttribute("meeting");
		session.removeAttribute("times");
		session.removeAttribute("difference");
		session.removeAttribute("selected");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}
