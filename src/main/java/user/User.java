package user;

import java.sql.Date;

public class User {
    private String email;
    private String name;
    private Date dob;
    private String user_type;

    // 생성자: User 객체를 초기화
    public User(String email, String name, Date dob, String user_type) {
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.user_type = user_type;
    }

    // 이메일을 반환
    public String getEmail() {
        return email;
    }

    // 이름을 반환
    public String getName() {
        return name;
    }

    // 생년월일을 반환
    public Date getDob() {
        return dob;
    }
    
    // 사용자 유형을 반환
    public String getuser_type() {
        return user_type;
    }
}

