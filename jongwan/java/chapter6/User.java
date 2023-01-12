package chapter6;

public class User{
    private String password;
    public void changePassword(String oldPw, String newPw) throws Exception {
        if(!matchPassword(oldPw)) throw new Exception();
        setPassword(newPw);
    }

    private void setPassword(String newPw) throws Exception{
        if(isEmpty(newPw)) throw new Exception();
    }

    private boolean isEmpty(String newPw) {
        return newPw == null || "".equals(newPw);
    }

    private boolean matchPassword(String oldPw) {
        return this.password.equals(oldPw);
    }
}
