package chapter6;


public class MemberController {
    public User join(String reqEmail, String reqPassword) throws Exception {
        String email = reqEmail;
        String password = reqPassword;
        //응용 계층에 전달할 객체 새로 생성
        JoinRequest joinRequest = new JoinRequest(email,password);
        JoinService joinService = new JoinService();
        joinService.createUser(joinRequest);
        return new User();
    }
}
