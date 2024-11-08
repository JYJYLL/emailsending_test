package user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

import login.Config;

// 권한신청 목록 가져오는 서블릿
@WebServlet("/getRequestRole") 
public class GetRoleRequestsServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

	private static String dburl;
    private static String dbusername;
    private static String dbpassword; 
    
    static {
        Config config = new Config();
        dburl = config.getDbUrl();
        dbusername = config.getDbUser();
        dbpassword = config.getDbPassword();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DriverManager.getConnection(dburl, dbusername, dbpassword)) {
            
        	// role_requests 테이블에서 모든 신청자 정보 가져옴
            String query = "SELECT name, email, dob FROM role_requests";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();

                // JSON 배열에 결과 추가
                JSONArray requestsArray = new JSONArray();

                while (rs.next()) {
                    JSONObject requestJson = new JSONObject();
                    requestJson.put("name", rs.getString("name"));
                    requestJson.put("email", rs.getString("email"));
                    requestJson.put("dob", rs.getDate("dob").toString());
                    requestsArray.put(requestJson);
                }

                // JSON 응답 객체 생성
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("requests", requestsArray);

                // 응답 전송
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toString());
                out.flush();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 오류");
        }
    }
}