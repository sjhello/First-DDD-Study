package chapter6;

public class ChangePsswordService {

    public void changePassword(String userId, String oldPw, String newPw) throws Exception {
        User dbDataUser = new User();//db데이터
        checkUserExists(dbDataUser);
        dbDataUser.changePassword(oldPw, newPw);
        //... save
    }

    private void checkUserExists(User dbDataUser) throws Exception {
        if(dbDataUser != null){
            throw new Exception();
        }
    }
}
