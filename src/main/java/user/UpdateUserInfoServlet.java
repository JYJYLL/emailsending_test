/* 수정은 되는데 
개선사항1. 토큰을 이메일을 주체로 발급해서 토큰이 회수됨 -> 다시 로그인 해야 됨. 
개선사항2. 투표생성자 이메일 정보는 바뀌지 않음.*/

package user;

import login.Config;
import login.PasswordUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// 비밀번호, 이메일 변경 서블릿 (이메일은 막아둠..)
@WebServlet("/updateUserInfo")
public class UpdateUserInfoServlet extends HttpServlet {
	
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	String authHeader = request.getHeader("Authorization");

    	// 인증 헤더가 있고 "Bearer "로 시작하는지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
            String email = JWTUtils.getEmailFromToken(token); // 토큰에서 이메일 추출

            if (email != null) {
            	 // 새 이메일과 새 비밀번호를 가져옴
                String newEmail = request.getParameter("email");
                String newPassword = request.getParameter("password");

                try {
                	// 이메일이 변경된 경우 이메일 업데이트
                    if (newEmail != null && !newEmail.isEmpty()) {
                        updateEmail(email, newEmail);
                    }

                    // 비밀번호가 변경된 경우 비밀번호 업데이트
                    if (newPassword != null && !newPassword.isEmpty()) {
                        updatePassword(email, newPassword);
                    }

                    response.setStatus(HttpServletResponse.SC_OK); // 성공 응답
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 서버 오류 응답
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패 응답
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증 헤더가 없거나 형식이 잘못된 경우
        }
    }

    // 이메일 업데이트
    private void updateEmail(String oldEmail, String newEmail) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword)) {
            //System.out.println("Updating email from " + oldEmail + " to " + newEmail); // 테스트 로그
            String updateEmailSQL = "UPDATE userdata SET email = ? WHERE email = ?"; // 이메일 업데이트
            try (PreparedStatement updateStmt = connection.prepareStatement(updateEmailSQL)) {
                updateStmt.setString(1, newEmail);
                updateStmt.setString(2, oldEmail);
                int rowsAffected = updateStmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected); // 변경된 행 수 출력
            }
        }
    }
    
    // 비밀번호 업데이트
    private void updatePassword(String email, String newPassword) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword)) {
            String hashedPassword = PasswordUtils.hashPassword(newPassword); // 비밀번호를 해시로 변환
            //System.out.println("Updating password for email: " + email); // 테스트 로그
            String updatePasswordSQL = "UPDATE userdata SET password = ? WHERE email = ?"; // 비밀번호 업데이트
            try (PreparedStatement updateStmt = connection.prepareStatement(updatePasswordSQL)) {
                updateStmt.setString(1, hashedPassword);
                updateStmt.setString(2, email);
                int rowsAffected = updateStmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected); // 변경된 행 수 출력
            }
        }
    }
}
