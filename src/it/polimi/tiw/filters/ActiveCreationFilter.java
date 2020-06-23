package it.polimi.tiw.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "ActiveCreationFilter")
public class ActiveCreationFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpSession session = request.getSession();

		session.removeAttribute("times");
		session.removeAttribute("meeting");
		session.removeAttribute("selected");
		session.removeAttribute("difference");

		chain.doFilter(req, resp);
	}

}
