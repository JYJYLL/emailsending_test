package user;

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

import java.io.BufferedReader;
import org.json.JSONObject;

import login.Config;

// 회원 탈퇴 서블릿
@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {

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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 데이터 읽기
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        
        JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
        String email = jsonObject.optString("email");

        // 이메일이 없거나 빈 경우 오류 반환
        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Email is required");
            return;
        }

        // 유저 삭제 시도
        try {
            deleteUser(email); // 유저 삭제 메서드 호출
            response.setStatus(HttpServletResponse.SC_OK); // 성공 상태 설정
            response.getWriter().write("User deleted successfully");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 서버 오류 상태 설정
            response.getWriter().write("Error deleting user");
            e.printStackTrace();
        }
    }

    // 해당 이메일 유저 삭제
    private void deleteUser(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement deleteStmt = null;
        try {
            connection = DriverManager.getConnection(dburl, dbusername, dbpassword); // DB 연결
            String deleteUserSQL = "DELETE FROM userdata WHERE email = ?";
            deleteStmt = connection.prepareStatement(deleteUserSQL); // 삭제 실행
            deleteStmt.setString(1, email);

            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted > 0) { // 삭제 성공
                System.out.println("User deleted successfully!");
            } else { // 삭제 실패
                System.out.println("Failed to delete user.");
                throw new SQLException("Failed to delete user with email: " + email);
            }
        } finally {
            if (deleteStmt != null) {
                deleteStmt.close();
            }
            if (connection != null) {
                connection.close(); // DB 연결 해제
            }
        }
    }
}
