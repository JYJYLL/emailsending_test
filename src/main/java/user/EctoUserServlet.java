package user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

// 선관위 권한 박탈 서블릿
@WebServlet("/EctoU")
public class EctoUserServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
    private login.db db = new login.db();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	// JSON 데이터 파싱
    	ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        EctoUserRequest ectoUserRequest = objectMapper.readValue(jsonData, EctoUserRequest.class);
        
        String email = ectoUserRequest.getEmail();
        //System.out.println("받은 이메일: " + email); 

        // 사용자 권한 업데이트
        boolean success = db.EctoUser(email);

        response.setContentType("application/json; charset=UTF-8");
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"권한이 변경되었습니다.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"권한 변경에 실패했습니다.\"}");
        }
    }

    private static class EctoUserRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

