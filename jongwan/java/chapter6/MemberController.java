package chapter6;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberController {
    private JoinService joinService = new JoinService();

    public User join(String reqEmail, String reqPassword) throws Exception {
        String email = reqEmail;
        String password = reqPassword;
        //응용 계층에 전달할 객체 새로 생성
        JoinRequest joinRequest = new JoinRequest(email,password);
        JoinService joinService = new JoinService();
        joinService.createUser(joinRequest);
        return new User();
    }

    public Map<String,String> join(JoinRequest joinRequest){
        Map<String,String> result = new HashMap<>();
        try {
            joinService.join(joinRequest);
        }catch (EmptyPropertyException emptyEx){
            result.put("error",emptyEx.getMessage());
            return result;
        }catch (DuplicateIdException duplEx){
            result.put("error",duplEx.getMessage());
            return result;
        }catch (Exception e){
            result.put("error",e.getMessage());
            return result;
        }
        return result;
    }

    public Map<String,Object> joinUsingValidator(JoinRequest joinRequest){
        Map<String, Object> result = new HashMap<>();
        try {
            joinService.join(joinRequest);
        }catch (ValidationErrorException ve){
            result.put("error", ve.getErrors());
            return result;
        }catch (Exception e){
            result.put("error",e.getMessage());
            return result;
        }
        return result;
    }

    public String joinUsingPresentationValidator(JoinRequest joinRequest){
        List<String> erros = new ArrayList<>();
        if(joinRequest == null) erros.add("request값 없음");
        if(joinRequest.getId() == null) erros.add("아이디 없음");
        if(!erros.isEmpty()) return erros.toString();
        return "view";
    }
}
