package user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import login.Config;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 사용자 정보 반환하는 서블릿
@WebServlet("/userinfo")
public class UserInfoServlet extends HttpServlet {
	
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
        
    	// 헤더에서 토큰 가져옴
    	String authHeader = request.getHeader("Authorization");

    	// 인증 헤더가 있고 "Bearer "로 시작하는지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = JWTUtils.getEmailFromToken(token); // 토큰에서 이메일 추출

                if (email != null) {
                    User user = getUserByEmail(email); // 이메일을 이용해 사용자 정보를 DB에서 조회

                    if (user != null) {
                    	// 응답을 JSON 형식으로 설정
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        PrintWriter out = response.getWriter();

                        // 사용자 정보를 JSON 형식으로 응답
                        out.print("{\"email\":\"" + user.getEmail() + "\","
                                + "\"name\":\"" + user.getName() + "\","
                                + "\"dob\":\"" + user.getDob().toString() + "\","
                                + "\"user_type\":\"" + user.getuser_type() + "\"}");
                        out.flush();
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 사용자가 없으면 인증 실패 상태 반환
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 이메일을 추출하지 못했을 경우 인증 실패 상태 반환
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 오류 발생 시 인증 실패 상태 반환
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 헤더가 없거나 형식이 잘못된 경우 인증 실패 상태 반환
        }
    }

    // 이메일을 이용해 DB에서 사용자 정보를 조회
    private User getUserByEmail(String email) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DriverManager.getConnection(dburl, dbusername, dbpassword); // DB 연결

            String sql = "SELECT email, name, dob, user_type FROM userdata WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            // 쿼리 실행
            rs = pstmt.executeQuery();

            // 사용자 정보가 존재하면 User 객체를 생성
            if (rs.next()) {
                user = new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("user_type")
                );
            }
        } finally { // 리소스 해제
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return user; // 조회된 사용자 정보 반환
    }
}
