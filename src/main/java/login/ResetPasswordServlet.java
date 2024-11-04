package login;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 비밀번호 재설정 서블릿
@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String useremail = request.getParameter("useremail");
        String newhashedPassword  = request.getParameter("newhashedPassword");
        
        Config config = new Config();
        String dburl = config.getDbUrl();
        String dbusername = config.getDbUser();
        String dbpassword = config.getDbPassword();
        
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dburl, dbusername, dbpassword);
            
            // 비밀번호 업데이트
            String updateSQL = "UPDATE userdata SET password = ? WHERE email = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateSQL);
            updateStmt.setString(1, newhashedPassword ); // 새 비밀번호 설정
            updateStmt.setString(2, useremail); // 이메일 설정

            int rowsUpdated = updateStmt.executeUpdate(); // 쿼리 실행
            if (rowsUpdated > 0) {
                response.getWriter().write("Password updated successfully");
            } else {
                response.getWriter().write("User not found");
            }
        } catch (SQLException e) {
            response.getWriter().write("Database error: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // 연결 종료
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}