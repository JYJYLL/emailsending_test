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
import java.util.ArrayList;
import java.util.List;

// 선관위 목록 불러오는 서블릿
@WebServlet("/getECusers")
public class GetECUsersServlet extends HttpServlet {
	
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
    	
    	String authHeader = request.getHeader("Authorization");

    	 // Authorization 헤더가 있고, Bearer 토큰으로 시작하는지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); 

            try {
                String email = JWTUtils.getEmailFromToken(token); // 토큰에서 이메일 추출

                if (email != null) { // 이메일이 존재하는 경우
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    
                    // 사용자 목록을 가져옴
                    List<User> users = getECUsers();

                    // 사용자 목록을 JSON 형식으로 응답
                    out.print("{\"users\":[");
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        out.print("{\"email\":\"" + user.getEmail() + "\","
                                + "\"name\":\"" + user.getName() + "\","
                                + "\"dob\":\"" + user.getDob().toString() + "\","
                                + "\"user_type\":\"" + user.getuser_type() + "\"}");
                        if (i < users.size() - 1) {
                            out.print(","); // 마지막 요소가 아닌 경우 쉼표 추가
                        }
                    }
                    out.print("]}"); // JSON 끝맺음
                    out.flush();
                } else { 
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 이메일이 null인 경우 401
                }
            } catch (Exception e) { // 예외 발생 시 처리
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Authorization 헤더가 없거나 Bearer 토큰이 아닌 경우 401
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // 선관위(EC) 목록 가져옴
    private List<User> getECUsers() throws SQLException {
        String sql = "SELECT email, name, dob, user_type FROM userdata WHERE user_type = 'ec'"; // EC 사용자만 조회
        List<User> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dburl, dbusername, dbpassword);
             PreparedStatement pstmt = conn.prepareStatement(sql); 
             ResultSet rs = pstmt.executeQuery()) { // 쿼리 실행 및 결과 가져오기

            while (rs.next()) { // 결과 집합을 순회하며 사용자 객체 생성
                User user = new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("user_type")
                );
                users.add(user); // 리스트에 사용자 추가
            }
        }
        return users; // 사용자 목록 반환
    }
}
