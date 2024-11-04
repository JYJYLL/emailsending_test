package vote;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

// 후보자 등록 서블릿
@WebServlet("/addcandidates")
public class AddCandidatesServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		request.setCharacterEncoding("UTF-8"); // 요청의 문자 인코딩을 UTF-8로 설정

	    // 클라이언트로부터 전송된 JSON 데이터를 받아옴
	    StringBuilder jsonBuilder = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        jsonBuilder.append(line); // 라인을 읽어서 JSON 문자열 빌드
	    }

	    try {
	        // JSON 데이터 파싱
	        JSONObject json = new JSONObject(jsonBuilder.toString());
	        JSONArray candidates = json.getJSONArray("candidates"); // 후보자 배열을 가져옴

	        // 헤더에서 vote-id 추출
	        String voteId = request.getHeader("vote-id");
	        if (voteId == null || voteId.isEmpty()) {
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 요청이 잘못된 경우
	            response.getWriter().write("{\"message\": \"Vote ID is required.\"}");
	            return;
	        }

	        // db에 저장
	        Candidatesdb.saveCandidates(voteId, candidates);

	        // 성공
	        response.setStatus(HttpServletResponse.SC_OK);
	        response.getWriter().write("{\"message\": \"Candidates added successfully.\"}");

	    } catch (Exception e) {
	        // 오류
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("{\"message\": \"Failed to save candidates.\"}");
	        e.printStackTrace();
	    }
	}

}