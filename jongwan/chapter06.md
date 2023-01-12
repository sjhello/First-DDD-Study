# 응용 서비스와 표현 영역
q1 : 요청 재생성
q2 : service 에서 service 를 상송
q3 : 응용 서비스의 크기 기능별로 쪼갠다면, service 계층을 의존하는것이 더 많아 지지 않을까?
### 표현 영역과 응용 영역
- 표현 영역
  - 사용자의 요청을 해석하며, 응용 계층에게 기능을 위임한다.
  - 사용자의 요청(파라미터) 를 응용 서비스에 맞게 객체를 재 생성 한뒤, 서비스의 메서드를 호출한다.
- 응용 영역 : 사용자 요청에 대한 응답값을 리턴한다.
````JAVA
public class MemberController {
    public User join(String reqEmail, String reqPassword){
        String email = reqEmail;
        String password = reqPassword;
        //응용 계층에 전달할 객체 새로 생성
        JoinRequest joinRequest = new JoinRequest(email,password);
        //응용 계층 실행
        JoinService joinService = new JoinService();
        joinService.createUser(joinRequest);
        return new User();
    }
}
````
### 응용 서비스의 역할
- 사용자의 요청을 처리하기 위해 리포지터리에서 도메인 객체를 가져온다.
- `도메인 영역`과 `표현 영역`을 연결해주는 창구 역할을 한다.
- `도메인 객체 간의 흐름을 제어`한다.
- `트랜잭션 처리`를 담당한다.
- 응용 서비스가 복잡하면, 도메인 로직을 구현하고 있을 가능성이 있으므로 의심해보자.

#### 도메인 로직 넣지 않기
````JAVA
# AS-IS - bad
- 응용계층에 도메인 로직이 들어감
- 수정시, 도메인 로직을 파악하기 위해 여러 영역을 분석 해야 한다.
- 새로운 기능을 추가시, 코드 변경을 어렵게 만든다.
- 새로운 기능을 추가하기 위해, 새로운 응용계층을 생성하는 경우가 생길 수도 있다.
public class ChangePsswordService {
  public void changePassword(String userId, String oldPw, String newPw) throws Exception {
    User dbDataUser = new User();//db데이터 조회
    checkUserExists(dbDataUser);
    dbDataUser.changePassword(oldPw, newPw);
    
    //응용 계층에 도메인 로직이 들어간다.
    if(!matchPassword(oldPw)) throw new Exception();
    dbDataUser.setPassword(newPw);
  }
}

# TO-BE - good
- 비밀번호 변경을 도메인에서 처리한다.
- 코드의 응집도가 높아진다.
public class ChangePsswordService {
  public void changePassword(String userId, String oldPw, String newPw) throws Exception {
      User dbDataUser = new User();//db데이터 조회
      checkUserExists(dbDataUser);
      dbDataUser.changePassword(oldPw, newPw);
      //... save
  }
}
````
### 응용 서비스의 구현
#### 응용 서비스의 크기
- 응용 계층의 역할이 많아지면, 코드를 점점 얽히게 만들어 코드 품질을 낮춘다.
- 구분되는 기능별로 응용 계층을 나누는 것이 좋다.(`응용 계층에 기능별로 2~3개 기능`)
````JAVA
# AS-IS
- UserService 역할이 방대 해질 수 있다.
public class UserService {
    private UserRepository userRepository;
    private Notifier notifier;
    
    public void changePassword(){};
    public void initializePassword(){};
    public void findExistUser(){};
    public void sendNotify(){};
    public void findBadUser(){};
}

# TO-BE
- 기능별로 쪼개면, 코드 품질을 좋게 유지할 수 있다.
public class UserService {
  private UserRepository userRepository;
  private Notifier notifier;
  public void findExistUser(){};
  public void findBadUser(){};
}
public class UserPasswordService{
  public void changePassword(){};
  public void initializePassword(){};
}
public class notifierService{
  public void sendNotify(){};   
}
````
#### 응용 서비스의 인터페이스와 클래스
- 인터페이스가 필요한 상황은 구현클래스가 여러 개인 경우이므로, 인터페이스가 명확하게 필요하기 전까지는
  무조건 작성할 필요는 없다.
````JAVA
- 무조건 impl을 만드는 것은 잘못되었고, 꼭 필요할 경우에만 생성하자.
public interface ChangePasswordService{
    public void changePassword();
}
public class ChangePasswordServiceImpl implements ChangePasswordService{
  @Override
  public void changePassword();
}
````
#### 메서드 파라미터와 값 리턴
