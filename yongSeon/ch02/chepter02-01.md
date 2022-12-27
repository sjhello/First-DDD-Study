## 2.1 4개의 영역
- 표현, 응용, 도메인, 인프라스트럭쳐는 아키텍처를 설계할 때 출현하는 전형적인 구조이다.  
![img.png](image/img.png)

### 표현 계층
- 사용자의 요청을 받아 응용 영역에 전달한다. 그리고 응용 계층에 처리한 결과를 사용자에게 전달합니다.  
![img.png](image/controllerImage.png)

### 응용 계층
- 사용자에게 제공할 기능을 구현. 주문 등록, 주문 취소, 상품 상세 조회 같은 기능을 구현한다. 응용 영역에서 기능을 구현하기위해서는 도메인 모델을 사용하여 정의한다.
- 응용 서비스의 로직을 직접 수행한다기보다는 도메인 모델에 로직수행을 위임하여 처리한다.  
![img.png](image/serviceLayer.png)

### 인프라스트럭처 계층
- 구현 기술에 대한 것을 다룬다. RDMS 연동을 처리하거나 메시징 큐에 메시지를 전송하거나 수신하는 기능을 구현
- 몽고디비나 레디스의 데이터 연동을 구현하기도함.
- 논리적인 개념보다는 실제 구현을 다룬다.
![img.png](image/infrastructure.png)

### 2.2 계층구조 아키텍처
- 계층 구조는 특성상 상위계층에서 하위 계층으로의 의존만 존재한다. 하위 계층이 상위 계층을 의존하지 않는다.
- 구조를 엄격하게 하여 아래 계층에만 의존하도록 하지만 상황에 따라 유연하게 적용하기도 한다.
![img.png](image/layer.png)
- 위 그림처럼 더 아래 계층에 의존한 구조로 가져가기도 한다. 
- 주의점은 표현, 응용, 도메인 계층이 상세한 구현 기술을 가진 인프라스트럭쳐 계층에 의존하게 된다.

```java
public class DroolsRuleEngine {
    private KieContainer kContainer;
    
    public DroolsRuleEngine() {
        KieService ks = KieServices.Factory.get();
        kContainer = ks.getKieContainer();
    }
    public void evalute(String sessionName, List<?> facts) {
        KieSession kieSession = kContainer.newSession(sessionName);
        
        try {
            facts.forEach(x->kieSession.insert(x));
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
    }
}
```

```java
public class CalculateDiscountService {
    private DroolsRuleEngine ruleEngine;
    
    public CalculateDiscountService() {
        ruleEngine = new DroolsRuleEngine();
    }
    
    public Money calculateDisCount(List<OrderLine> orderLines, String customerId) {
        Customer customer = findCustomer(customerId);
        
        MutableMoney money = new MutableMoney(0); // Drools에 특화된 코드(얀신 결과를 받기 위한 타입)
        List<?> facts = Arrays.asList(customer, money); // Drools에 특화된 코드(룰에 필요한 데이터)
        facts.addAll(orderLines);
        ruleEngine.evalute("discountCalculation",facts);// Drools에 특화된 코드(룰의 이름)
        
        return money.toImmutableMoney();
    }
}
```
- 세션 이름을 변경하면 코드도 변경해야한다.
- MutableMoney는 룰 적용 결과값을 위해 존재하는 타입이다.
- 겉으로는 의존하지 않지만 완전하게 의존하고 있다.
- 기능 확장의 어려움과 테스트의 어려움을 가지고 있다.

## 2.3  DIP
>프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.”

![module.png](image%2Fmodule.png)
- 고수준 모듈 : CalculateDiscountService
- 저수준 모듈 : DroolsRuleEngine 
- 여기서 CalculateDiscountService는 의미 있는 단일 기능을 제공하는 고수준 모듈이다
  고수준 모듈의 기능을 구현하려면 여러 하위 기능이 필요하다
- 고수준 모듈이 저수준 모듈을 사용하면 구현 변경과 테스트가 어려운 문제가 발생

### 비밀은 추상화된 인터페이스
```java 
public interface RuleDiscounter { 
    Money applyRules(Customer customer, List<OrderLine> orderLines);
}
```
- 추상화된 인터페이스에 의존한 서비스로직 
```java
public class CalculateDiscountService {
    private RuleDiscounter ruleDiscounter;

    public CalculateDiscountService(RuleDiscounter ruleDiscounter) {
        this.ruleDiscounter = ruleDiscounter;
    }

    public Money calculateDisCount(List<OrderLine> orderLines, String customerId) {
        Customer customer = findCustomer(customerId);

        return ruleDiscounter.applyRules(customer, orderLines);
    }
}
```
- 서비스레이어는 Drools에 의존하는 코드가 없어짐
- RuleDiscounter의 구현 객체는 생성자를 통해서 전달 받음

### DIP 적용
>고수준 모듈: 어떤 의미 있는 단일 기능을 제공하는 모듈  
>저수준 모듈: 고수준 모듈의 기능을 구현하기 위해 필요한 하위 기능의 실제 구현

![classDiagram.png](image%2FclassDiagram.png)
- DIP는 이 문제를 해결하기 위해 저수준 모듈이 고수준 모듈에 의존하도록 바꾸며, 이는 추상화한 인터페이스를 통해 이루어진다
  - 고수준 모듈을 인터페이스로 추상화한다
  - 고수준 모듈은 이 인터페이스를 의존하고, 저수준 모듈은 이 인터페이스를 구현한다
  - 실제 사용할 저수준 모듈은 의존 주입을 통해서 전달 받는다
  - 이미지 반대로 되어 있다.
### 정리
- 저수준 모듈이 고수준 모듈에 의존한다고 해서 DIP(Dependency Inversion Principle), 의존역전 원칙이라고 부른다
- 의존성이 역전됨에 따라 어려움을 해결할 수 있다
- (컴파일 타임에) 고수준 모듈은 실제 구현체가 어떤 것인지 모른 채 주입받는다

```java
// 1. 사용할 저수준 객체 생성 (RuleDiscounter는 인터페이스)
RuleDiscounter ruleDiscounter = new DroolsRuleDiscounter();

// 2. 생성자 방식으로 주입
CalculateDiscountService disService = new CalculateDisocuntService(ruleDiscounter);

// 3. 사용할 저수준 구현 객체 변경
RuleDiscounter ruleDiscounter = new SimpleRuleDiscounter();

// 4. 사용할 저수준 모듈을 변경해도 고수준 모듈을 수정할 필요가 없음
CalculateDiscountService disService = new CalculateDisocuntService(ruleDiscounter);
```
테스트의 어려움 또한 해결할 수 있다
CustomerRepository와 RuleDicounter 등의 고수준의 인터페이스를 사용하면 대역 객체를 사용해서 테스트를 진행할 수 있다

### DIP 주의사항
- DIP를 적용할 때 하위 기능을 추상화한 인터페이스는 고수준 모듈 관점에서 도출한다
  - CalculateDiscountService에서는 어떤 인프라스트럭처를 이용해서 계산하는 것이 중요하지 않다.
  - 규칙에 따라 할인 금액을 계산한다는 것이 중요하다. 따라서 '할인 금액 계산'을 추상화한 인터페이스는 고수준 모듈에 위치한다
![dip_warning.png](image%2Fdip_warning.png)

### DIP와 아키텍처
- 아키텍처의 관점으로 봤을 때에는 고수준의 모듈을 실제 구현하는 것은 인프라스트럭처이다.
- 응용계층, 도메인은 인프라스트럭처를 이용하여 기능을 제공하는 저수준 모듈이다.
- 이러한 아키텍처에서 DIP를 적용한다면 아래와 같이 구현하여 다른 구현 클래스로도 변경해도 다른 레이어에 지장없는 아키텍처를 구성할 수 있다.
![img_2.png](image%2Fimg_2.png)

## 도메인 영역의 구성 요소
### 도메인의 주요 구성 요소
- 엔티티: 고유의 식별자를 가진 객체, 데이터와 행동을 가지고 자신의 라이프 사이클을 가지고 있다.
- 벨류: 식별자를 갖지 않는 객체로 하나의 값을 표현 할때 사용
- 애그리거트: 연관된 엔티티와 밸류 객체를 하나로 묶은 것
- 리포지토리: 도메인의 영속성을 철히하는 모듈, 예시로 DBMS 테이블에서 엔티티 객체를 가져오거나 저장
- 도메인 서비스: 엔티티에 속하지 않는 기능을 제공하는 도메인 로직을 제공, 예시로 할인할 때 쿠폰, 회원 등급 등
여러 도메인과 로직이 필요할 때 사용되는 경우.

### 엔티티와 벨류 
- DB와 엔티티는 다르다.
- 도메인 엔티티는 데이터와 기능을 함께 제공한다.
- 도메인 관점에서 기능을 구현하고 캡슐화해서 데이터가 임의로 변경되는 것을 막는다.
- RDBMS와 같은 관계형 데이터 베이스는 벨류 타입을 표현하기 힘들다.

```java
public class Order {
    private Long id;
    private Orderer orderer;
    private ShippingInfo shippingInfo;
    
    public void changeShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
}
```
### 느낀점
- 이전에는 JPA를 학습하면서 엔티티는 테이블과 일대일로 매핑된 객체라고 생각했지만 지금은 좀 더 도메인 관점에서 바라보게 되었다.
- 관계형 데이터베이스에서는 벨류 객체를 표현하는 것은 쉽지 않다. 테이블을 분리하여 엔티티로 분리하는 것이 좋다고 하는데
해당 예시로는 이해하기가 어렵다. 좀 더 좋은 예시가 없을까?

## 애그리거트
- 도메인이 많아질수록 모델 관계도는 점점 복잡해진다.
- 복잡한 도메인 구조에서는 전체적으로 보지못하고 개별적으로 초점을 맞추게 된다.
- 배송지, 배송인, 배송업체, 배송상품 등 개별적으로 보게되면 이해하기 어렵게 복잡한 구조로 서로 엮여있다. 이것을 배송이라는 하나의 객체로 묶는다면
개념적으로 좀 더 이해하기 쉬운 구조를 가져갈 수 있다.
![img_3.png](image%2Fimg_3.png)
- 개별 객체의 관계가 아니라 군집단위로 보게 되면서 개별 도메인 관계의 구조를 이해하기 쉬워진다.
- 애그리거트는 루트 엔티티를 통헤 내부 개별 객체를 관리하는 기능을 제공한다.
- 내부 엔티티나 벨류를 숨기고 루트 엔티티를 통하여 기능을 제공하기 때문에 외부의 접근으로서 안전하다.
![img_4.png](image%2Fimg_4.png)
```java
public class Order {
    
    public void changeShippingInfo(ShippingInfo shippingInfo) {
        checkShippingInfoChangeable(); // 배송지 변경 여부 체크
        this.shippingInfo = shippingInfo;
    }
}
```
- 주문이라는 애그리거트는 무조건 도메인 규칙을 따라서 배송지를 변경 가능하다.
- 루트 엔티티를 통하지 않고서는 개별 도메인을 접근하지 못하기 때문이다.
- 애그리거트를 어떻게 구성했냐에 따라서 구현이 복잡해지거나 트랜잭션에 대한 제약이 생기기도 한다.

### 느낀점
- 잘못 구성한다면 하나의 객체에 너무 많은 책임을 가질 수 있게 될 것 같다.
- 애그리거트안에 있는 개별 엔티티의 대한 기능이 계속 추가되면서 분리해야한다면 상당히 많은 부분을 수정해야되는 문제가 발생할 수 있을 것 같다.

## 리포지터리
- 리포지토리는 도메인의 구현을 위한 모델이다.
- 리포지토리는 애그리거트 단위로 저장하고 관리한다.
- JPA 리포지토리를 보면 추상화된 인터페이스에 실제 기능을 구현한다. 이것은 리포지토리가 고수준 모듈이고 구현체는 저수준 모듈이다

```java
public interface ProductRepository {
  List<Product> findAllByKeyword(String keyword);
}

public interface JdbcProductRepository extends ProductRepository {
  @Override
  List<Product> findAllByKeyword(String keyword) {
    ...
  };
}

public interface ElasticProductRepository extends ProducstRepository {
  @Override
  List<Product> findAllByKeyword(String keyword) {
    ...
  }; 
}
```
### 리포지터리와 서비스가 관련이 높은 이유
- 응용 서비스는 도메인 객체를 실제로 가져오거나 저장할 때 리포지토리를 활용한다.
- 트랜잭션을 리포지터리 구현 기술에 영향을 받는다.
![img_5.png](image%2Fimg_5.png)

### 느낀점
- 애그리거트를 만들지 않았을 때 엔티티와 레파지토리가 너무 많은 메서드가 생기는 경우가 잦았다.
- 좀 더 도메인 관점에서 바라보고 애그리거트를 잘 활용하였다면 리포지토리를 효율적으로 분리할 수 있을 것 같다. 