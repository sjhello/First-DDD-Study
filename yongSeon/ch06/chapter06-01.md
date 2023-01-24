## 표현 영역과 응용영역
![img.png](images%2Fimg.png)
--
- 표현 영역은 사용자의 요청을 해석하여 요청에 해당하는 서비스를 실행한다.
- 사용자가 원하는 기능을 제공하는 것은 응용 영역에 존재하는 서비스다.
- 사용자와 소통하는 것은 표현계층이기 때문에 응용역에 있는 서비스는 표현계층을 의존하지 않고 무엇을 하는지 몰라야 한다.

## 응용 서비스와 역활
- 응용서비스의 주요 역활은 도메인 객체를 통해 사용자의 요청을 처리하는 것이다.
- 표현 계층에서 본다면 도메인 객체와 연결해주는 창구와 같은 역활을 한다.
- 응용서비스는 도메인 객체의 흐름을 제어하기 때문에 **단순한 형태**를 갖는다.

````java
@RestContoller
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    
    @PostMapping("/model")
    public ResponseEntity<Member> createMemberModel(@RequestBody MemberModel memberModel) {
        Member member = memberService.createMember(memberModel);
        return ResponseEntity.ok(member);
    }
}
````
- 위의 코드는 응용 서비스를 사용하는 코드이다.

## 응용 서비스의 역활
- 응용서비스는 사용자가 요청한 기능을 실행하기 위해 레파지토리에서 도메인 객체를 가져와 사용한다.
- 응용서비스는 표현영역과 연결해주는 창구와 같은 역활을 한다.
- 응용서비스가 단순한 형태를 가지는 이유는 도메인 객체의 흐름을 제어하기 때문이다.

```java
public class MemberService {
    public Member createMember(RegisterMemberRequest request) {
        Member member = new Member(request.getName(), request.getEmail());
        memberRepository.save(member);
        return member;
    }
}
```
- 응용 서비스가 복잡하다면 도메인 객체의 흐름을 제어하는 것이 아니라 도메인 로직을 구현할 가능성이 높다.
- 도메인 로직을 응용 서비스에서 구현하게 된다면 코드중복, 테스트의 어려움, 응용 서비스의 복잡도가 증가한다.

### 도메인 로직 넣지않기
- **도메인 로직을 응용 서비스에 넣지 않는다.**
```java
public class Member {
    public void changePassword(String oldPassword, String newPassword) {
        if(!matchPassword(oldPassword)) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        
        this.password = newPassword;
    }
    
    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
    
    private void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password must be provided.");
        }
        this.password = password;
    }
}
```
- 위의 코드는 도메인 객체의 비밀번호 변경 로직이다.
- 도메인 객체의 비밀번호 변경 로직을 응용 서비스에서 구현하면 아래와 같다.
```java
public class ChangePasswordService {
    
    public void changePassword(String email, String oldPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email);
        existsMember(member);
        
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new WrongPasswordException();
        }
        
        member.changePassword(newPassword);
        memberRepository.save(member);
    }
}
```
- 위의 코드는 도메인 로직을 응용 서비스에서 구현한 코드이다. 그로인해 아래와 같은 문제점을 가지게 된다.
  - 코드의 응집성이 떨어진다. 도메인 데이터와 그 데이터를 조작하는 로직이 분산되어 있기 때문에 여러 영역을 분석해야한다.
  - 같은 도메인의 로직을 여러 서비스에서 구현하게 될 가능성이 높아진다.
```java
public class DeactivationService {
    public void deactivateMember(String email) {
        Member member = memberRepository.findByEmail(email);
        existsMember(member);
        // 코드의 중복이 발생!!
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new WrongPasswordException();
        }
        
        member.deactivate();
        memberRepository.save(member);
    }
}
```
- 따라서 아래와 같이 도메인 로직을 도메인 외부의 서비스에서 구현하지 않으면 문제가 발생하지 않는다.
```java
public class ChangePasswordService {
    
    public void changePassword(String email, String oldPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email);
        existsMember(member);
        
        if (!member.matchPassword(oldPassword)) {
            throw new WrongPasswordException();
        }
        
        member.changePassword(oldPassword, newPassword);
        memberRepository.save(member);
    }
}
```

### 정리 및 느낀점
- 도메인 로직은 되도록이면 도메인 객체에 구현하는 것이 좋다.
- 응용서비스에 도메인 로직을 구현하게 되면 코드의 중복이 발생하고, 응용 서비스의 응집도가 떨어진다. 따라서 
코드에 대한 변경을 어렵게 만든다.
- 코드에서의 중요성은 결국 변경의 용이해야된다는 것 같다. 코드의 변경이 어렵다면 소프트웨어로서의 지속 가능함을 저해시키고 소프트웨어로서의 가치를 계속 떨어트릴 것이다.

## 응용 서비스의 구현
- 응용서비스는 표현 영역과 도메인 영역을 연결해주는 매개체 역활을 한다.

### 응용 서비스의 크기
- 응용서비스 크기는 보통 두가지 방법 중 하나로 구현된다. 
  - 한 응용 서비스 클래스에 도메인의 모든 기능 구현
  - 구분되는 기능별로 클래스를 분리하여 따로 구현

### 응용 서비스에 모든 기능 구현
```java
public class MemberService {
    private MemberRepository memberRepository;
    
    public void join(Member member) {
        memberRepository.save(member);
    }
    
    public viud changePassword(String email, String oldPassword, String newPassword) {
        Member member = findExistsMember(email);
        member.changePassword(oldPassword, newPassword);
        memberRepository.save(member);
    }
    
    public  void initializePassword(String email, String newPassword) {
        Member member = findExistsMember(email);
        member.initializePassword(newPassword);
        memberRepository.save(member);
    }
    
    public void leave(String email) {
        Member member = findExistsMember(email);
        member.deactivate();
        memberRepository.save(member);
    }
    
    private Member findExistsMember(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberNotFoundException();
        }
        return member;
    }
}
```
- 한 도메인과 관련된 기능이 한 클래스에 위치함으로 동일 로직에 대한 중복을 줄일 수 있다.
- 코드의 크기가 커지고, 클래스의 연관성 없는 코드가 한 클래스 내부의 위치할 가능성이 높다.
- 예를 들어 암호 초기화 기능을 사용하고 사용자에게 통지해줘야 한다면 해당 도메인과 관련이 없는 Notifier 기능이 의존하게 된다.
- 이러한 문제는 코드가 점점 얽히게 만들어 코드 품질을 낮추게 된다.

### 기능별로 클래스를 분리하여 구현
```java
public class ChangePasswordService {
    private MemberRepository memberRepository;
    
    public void changePassword(String email, String oldPassword, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
        member.changePassword(oldPassword, newPassword);
        memberRepository.save(member);
    }
}
```
- 이렇게 구현하게 된다면 클래스 개수는 많아지지만 코드 품잘을 일정 수준으로 유지하는데 도움을 준다.
- 각 클래스는 하나의 기능만을 담당하게 되므로 기존 코드에 영향을 줄 가능성이 줄어든다.
- 한 클래스가 여러가지가 아닌 하나의 기능만을 담당하게 되므로 코드의 가독성이 높아진다.

### 응용 서비스의 인터페이스와 구현 클래스
- 응용 서비스에서 인터페이스를 사용하는 방법 몇가지 존재한다.
  - 추상화된 기능이 여러 구현 클래스에 존재할 경우
  - 런타임 환경에서 구현 클래스를 선택해야 할 경우
- 응용 서비스가 구현되고 확실해질때까지 인터페이스의 사용을 미루는 것이 좋다. 
  DDD 방법론으로 설계한다면 클래스가 많아지게 되는데 거기에 추가로 불필요한 인터페이스를 만들 필요가 없다.
- 테스트의 용이성 또한 Mock 객체를 활용하여 대체가 가능하다.

### 응용 서비스의 파라미터와 값처리 
- 응용 서비스의 파라미터가 많아진다면 클래스를 통해 처리하는 것이 좋다. 응용 서비스에 파라미터가 많아지면 응용 서비스의 기능을 이해하기 어려워지기 때문이다.
- 응용 서비스의 애그리거트객체를 리턴하여 바로 표현계층에서 사용하는 것은 결합도르 높이는 행위이기 때문에 지양해야 한다.
```java
public class ChangePasswordService {

  public void changePassword(Long memberId, String oldPassword, String newPassword) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(MemberNotFoundException::new);
    member.changePassword(oldPassword, newPassword);
    memberRepository.save(member);
  }
}


public class ChangePasswordService {
    
    public void changePassword(ChangePasswordRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(MemberNotFoundException::new);
        member.changePassword(request.getOldPassword(), request.getNewPassword());
        memberRepository.save(member);
    }
}
```
### 트랜잭션의 처리
- 스프링내부에서는 트랜잭션의 롤백과 커밋을 어노테이션을 통해 손쉽게 제공하니 잘 활용하자.
```java
public class ChangePasswordService {
  @Transactional  
  public void changePassword(Long memberId, String oldPassword, String newPassword) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(MemberNotFoundException::new);
    member.changePassword(oldPassword, newPassword);
    memberRepository.save(member);
  }
}
```

## 표현 영역
- 사용자가 시스템을 사용할 수 있는 기능을 제공하고 흐름을 제어한다.
- 사용자의 요청을 알맞은 응용 서비스를 호출하고 응용 서비스가 리턴한 결과를 사용자에게 보여준다.
- 사용자의 세션을 관리한다.
```java
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/members")
  public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest request) {
    MemberResponse member = memberService.createMember(request);
    return ResponseEntity.created(URI.create("/members/" + member.getId()))
        .body(member);
  }
}
```

### 느낀점
- 객체지향적으로 설계하게 되면 클래스의 개수가 많아진다, 그 이유는 가독성과 응집성을 높이기 위해 기능또는 역할에 따라 클래스를 분리하기 때문이다.
- 스프링에 지원하는 기능들은 잘 활용하자. 트랜잭션, 테스트, 의존성 주입 등등
- 표현 영역은 사용자와 직접 연결되어 흐름을 관리하기 때문에 응용계층에 의존시키면 안된다.