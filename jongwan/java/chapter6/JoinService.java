package chapter6;

import java.util.ArrayList;
import java.util.List;

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

    public void join(JoinRequest joinRequest){
        checkEmpty(joinRequest.getId(), "id");
        checkEmpty(joinRequest.getName(), "name");
        checkEmpty(joinRequest.getPassword(), "password");
        checkDuplicateId(joinRequest.getId());
    }

    private void checkDuplicateId(String id) {
        int duplicateIdCount = 1;//동일한 아이디가 존재
        if(duplicateIdCount > 0){
            throw new DuplicateIdException("동일한 아이디가 존재합니다.");
        }
    }

    private void checkEmpty(String value, String propertyName){
        if(value == null || value.isEmpty()){
            throw new EmptyPropertyException(propertyName);
        }
    }

    public void joinUsingValidator(JoinRequest joinRequest){
        List<ValidationError> errors = new ArrayList<>();
        if(joinRequest.getId() == null)
            errors.add(ValidationError.of("아이디 없음","empty"));
        if(joinRequest.getPassword() == null)
            errors.add(ValidationError.of("비밀번호 없음","empty"));
        if(joinRequest.getName() == null)
            errors.add(ValidationError.of("이메일 없음","empty"));
        if(isDuplicateIdExist(joinRequest.getId()))
            errors.add(ValidationError.of("동일한id 존재", "duplidexist"));

        if(!errors.isEmpty())
            throw new ValidationErrorException(errors);
    }

    private boolean isDuplicateIdExist(String id) {
        int duplicateIdCount = 1;
        return duplicateIdCount > 0 ? true : false;
    }
}
