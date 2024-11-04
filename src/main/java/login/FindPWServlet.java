package login;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;

// 비번 찾기위해 가입 유무 확인하는 서블릿
@WebServlet("/finduser")
public class FindPWServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	String name = request.getParameter("name");
        String email = request.getParameter("email");

        try {
            boolean userExists = db.checkUserExists(name, email);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            if (userExists) {
                response.getWriter().write("User exists");
            } else {
                response.getWriter().write("User does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }
}