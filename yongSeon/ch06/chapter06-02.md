## 6.5 값 검증
- 갑 검증은 응용 계층, 표현 계층 두곳 모두 검증할 수 있다.

### 표현계층에서의 값검증
```java
public class JoinService {
    public Member join(Member member) {
        checkEmptyEmail(member.getEmail());
        checkEmptyPassword(member.getPassword());
        checkEmptyName(member.getName());
        memberRepository.save(member);
        return member;
    }
}
```
```java
public class MemberController {
    
    @PostMapping("/members/new")
    public ResponseEntity<Member> create(@RequestBody Member member) {
        try {
            Member newMember = memberService.join(member);
            return ResponseEntity.ok(newMember);
        }catch (EmptyPropertyException e) {
            return ResponseEntity.badRequest().build();
        }catch (DuplicateMemberException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
```
- 응용 서비스에서 값 검증을 하게된다면 문제는 사용자가 값을 잘못해서 입력한 것과 중복되서 입력해서 잘못한 것인지 한번에 알 수 없다는 것이다.
- 이러한 예외처리 방법은 사용자가 여러번의 입력을 하게만들고 안좋은 경험을 만들어 낸다.

### 해결 방법
- 응용 서비스에서 에러코드르 한번에 모아서 익셉션을 발생시키는 방법
```java
public class JoinService {
    public Member join(Member member) {
        List<ValidationError> validationErrorList = new ArrayList<>();
        if (member.getEmail() == null || member.getEmail().isEmpty()) {
            validationErrorList.add(new ValidationError("email", "이메일은 필수 값입니다."));
        }else if(member.getPassword() == null || member.getPassword().isEmpty()) {
            validationErrorList.add(new ValidationError("password", "비밀번호는 필수 값입니다."));
        }
        
        if (!validationErrorList.isEmpty()) {
            throw new ValidationException(validationErrorList);
        }
        
        return memberRepository.save(member);
    }
}
```
- 표현 계층에서 필수 값을 검증하는 방법
```java
public class MemberController {
    
    @PostMapping("/members/new")
    public ResponseEntity<Member> create(@RequestBody Member member) {
        try {
            emptyEmail(member.getEmail());
            emptyPassword(member.getPassword());
            Member newMember = memberService.join(member);
            return ResponseEntity.ok(newMember);
        }catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }
}
```
- 스프링에서 제공하는 @Valid 어노테이션을 사용하는 방법
```java
public class JoinMemberRequestDto {
    @Email
    @NotEmpty(message = "이메일은 필수 값입니다.")
    private String email;
    
    @NotEmpty(message = "비밀번호는 필수 값입니다.")
    private String password;
    
    @NotEmpty(message = "이름은 필수 값입니다.")
    private String name;
}

public class MemberController {
    
    @PostMapping("/members/new")
    public ResponseEntity<Member> create(@RequestBody @Valid JoinMemberRequestDto joinMemberRequestDto) {
        Member member = new Member(joinMemberRequestDto.getEmail(), joinMemberRequestDto.getPassword(), joinMemberRequestDto.getName());
        Member newMember = memberService.join(member);
        return ResponseEntity.ok(newMember);
    }
}
```

### 느낀점
- 필자는 응용서비스의 완성도를 높이기 위해 응용서비스에서의 값검증을 선호한다고 한다는 것을 보고 새로운 견해를 느낄 수 있었다.
- 표현계층에서는 값이나 형식, 범위를 검증하고 응용계층은 주로 논리적인 오류를 검증을 하는데에 이점이 나는 더 크다고 생각한다.
- 그리고 응용서비스에서의 값검증을 하게되면 사용자가 여러번의 입력을 하게만들고 안좋은 경험을 만들어 낸다는 것이 매우 공감이 되었다.
- 응용서비스 계층은 비지니스로직이 들어가는 곳이기 때문에 단순하고 그 의미가 잘 담겨야된다고 생각한다.

## 6.6 권한 검사
