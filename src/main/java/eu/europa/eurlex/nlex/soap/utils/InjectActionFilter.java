package eu.europa.eurlex.nlex.soap.utils;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A filter that injects a proper SOAPAction header if it was not given.
 * @author Mariusz Jakubowski
 *
 */
@WebFilter("/*")
public class InjectActionFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String action = req.getHeader("SOAPAction");
        if (action == null || action.isEmpty()) {
            HeaderMapRequestWrapper wrapper = new HeaderMapRequestWrapper(req);
            String body = wrapper.getBody();
            if (body.contains("<about_connector")) {
                wrapper.addHeader("SOAPAction", "#about_connector");
            } else if (body.contains("<VERSION")) {
                wrapper.addHeader("SOAPAction", "#VERSION");
            } else if (body.contains("<test_query")) {
                wrapper.addHeader("SOAPAction", "#test_query");
            } else if (body.contains("<request")) {
                wrapper.addHeader("SOAPAction", "#request");
            }
            super.doFilter(wrapper, res, chain);
        } else {
            super.doFilter(req, res, chain);
        }
    }
    
    
    @Override
    public void destroy() {
    }


}
