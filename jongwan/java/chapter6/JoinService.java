package chapter6;

public class JoinService {
    public User createUser(JoinRequest joinRequest) throws Exception {
        String existEmailData = "1234@naver.com";
        checkExistMember(existEmailData, joinRequest);
        return save(joinRequest);
    }

    private void checkExistMember(String existEmailData, JoinRequest joinRequest) throws Exception {
        if(existEmailData.equals(joinRequest.getEmail())){
            throw new Exception();
        }
    }
    private User save(JoinRequest joinRequest) {
        return new User();
    }
}
