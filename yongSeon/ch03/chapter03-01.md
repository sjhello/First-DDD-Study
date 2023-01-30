## 애그리거트
- 복잡한 도메인의 관련된 관계를 나열해놓고 본다면 해당 도메인이 어떻게 동작하는지 이해하기 어렵다.
- 도메인 구조가 복잡하다는 것은 테스트하기도 어렵고 확장하기에도 어려움이 많다. 
![img.png](image%2Fimg.png)
- 그렇다면 쉽게 이해할 수 있는 구조로 풀어내야한다. 바로 상위 개념으로 묶어서 도메인의 관계를 풀어내는 것이다.
- 상위개념의 도메인을 통해 관계를 이해하고 상위 도메인의 내부릍 통해 도메인의 상세 구조를 이해하는 것이다.
![img_1.png](image%2Fimg_1.png)

### 애그리거트의 특징
- 복잡한 도메인의 구조를 단순화시켜준다.
- 복잡성이 낮아지는 만큼 도메인 기능을 확장하고 변경하는데 시간이 덜 들어간다.
- 애그리거트는 유사한 도메인을 하나로 묶었기 때문에 같은 생명주기를 가지거나 유사한 생명주기를 가진다.
- 애그리거트는 경계를 가지고 있으며 하나의 독립된 객체이다. 주문 에그리거트에서는 유저의 비밀번호를 수정하지 않는 것처럼
- 애그리거트의 경계는 서로 얼마나 간섭하여 변경하느냐에 따라 달린다. 예를 들어 주문이라는 애그리거트는 생성하는 동시에 주문 정보, 배송지, 상품 정보, 주문자 정보등은 함께 같이 생성되기 때문에
같은 애그리거트이다. 주문한 상품정보가 변경되면 주문 금액이 변경되고 배송지가 변경되면 배송정보가 변경되기도 한다.
- 상품리뷰와 상품은 리뷰가 변경된다고해도 상품은 변경되지 않기 때문에 다른 애그리거트로 분리를 한다.
- 도메인에 대한 경험이 생기고 지식이 쌓일수록 애그리거트의 크기는 점점 줄어든다.

## 애그리거트 루트 
- 주문 애그리거트는 아래와 같이 가지고 있다.
  - 총 금액인 totalAmounts 를 가지고 있는 Order 엔티티
  - 개별 구매 상품의 개수인 quantity 와 금액인 price를 가지고 있다.
- 애그리거트는 여러 객체로 구성되어 되어 있지만 하나의 도메인처럼 모든 객체가 정상이여야 한다.
- 예를 들어 상품개수가 변경되면 총금액도 그에맞게 변경되어야한다.
- 애그리거트의 전체를 관리해야되는 주체가 필요한데 이책임을 지는 것이 루트엔티티이다.
- 애그리거트에 속한 엔티티는 간접적으로 루트엔티티의 속해있다.

### 도메인 규칙과 일관성
- 루트 엔티티는 애그리거트에 속한 객체뿐만 관리할 뿐만 아니라 애그리거트의 일관성이 깨지지 않도록 해야한다.

```java
public class Order {
    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }
    
    private void verifyNotYetShipped() {
        if (state != OrderState.PAYMENT_WATING && state != OrderState.PREPARING) {
            throw new IllegalSateException("aleady shipped");
        }
    }
}
```
- 주의해야되는 것은 애그리거트 외부에서 애그리거트가 속한 객체를 외부에서 직접 변경하면 안된다. 애그리거트 루트가 강제하는 
규칙을 적용할 수 없어서 모델의 일관을 깨기 때문이다.
- 외부에서 객체를 변경할 경우 비지니스 요구사항이 변경되거나 추가될 경우 외부에서 객체 상태를 체크하게 되고 중복코드가 계속되어 생길 가능성이 크고
이러한 서비스는 유지보수에 어려움을 더한다.

**애그리거트 주의사항**
- 단순히 필드를 변경하는 set 메서드를 public 하게 만들지 않는다.
  - public set 메서드는 도메인의 의미나 의도를 표현하지 못하고 더 싱위 계층으로 비지니스 로직이 분산된다.
  - set이라는 메서드를 사용하지 않으면 자연스럽게 update, cancel, changePassword 같은 의미있는 네이밍을 붙이게 된다.
- 벨류타입은 불변으로 구현한다.
  - 밸류객체가 불변이면 벨류객체의 값을 바꾸지 못해 새로운 객체를 할당하게 된다.
  - 의존하고 있는 다른 객체가 일부만 변경 할 가능성이 없어지기 때문에 일관성이 깨질 가능성이 줄어든다.

### 애그리거트 루트의 기능 구현
- 애그리거트 루트는 내부의 다른 객체를 조합하여 기능을 완성한다.
```java
public class Order {
    private Money totalAmount;
    private List<OrderLine> orderLines;
    
    public void calculateTotalAmounts() {
        int sum = orderLines.stream()
                .mapToInt(ol -> ol.getPrice() * ol.getQuantity)
                .sum();
        this.totalAmount = new Money();
    }
}
```
```java
public class Member {
    private Password password;
    
    public void changePassword(String currentPassword, String newPassword) {
        if (!password.match(currentPassword)) {
            throw new PasswordNotMatchException();
        }
        this.password = new Password(newPassword);
    }
}
```
- 애그리거트만 구성요서의 상태만 참조하는 것은 아니다. 기능 실행을 다른 객체에게 위임하기도 한다.
- 아래와 같이 분리하고 기능구현을 위임하고 메서드만 가져다 쓰도록 구현하면 된다.
```java
public class OrderLines {
    private List<OrderLine> orderLines;
    
    public getTotalAmounts() {
        return orderLines.stream()
                .mapToInt(ol -> ol.getPrice() * ol.getQuantity)
                .sum();
    }
}

public class Order {
    private OrderLines orderLines;
    
    public void changeOrderLines(List<OrderLine> newLines) {
        orderLines.changeOirderLines(newLines);
        this.totalAmounts = orderLines.getTotalAmount();
    }
}
```
- 위에서의 코드를 문제점은 벨류를 set과 같은 형태로 변경하고 있다.

**외부에서 수정하는 값 실수 방지하는법** 
```java
orderLines lines = order.getOrderLines();
orderLines.changeOirderLines(newLines);
```
- 외부에서 상태값을 변경 할 경우 총합은 계산하지 못하느 버그를 유발할 수 있다.
- orderLines를 불변객체로 만들어서 내부에서만 변경하도록 해야한다.
- 불변으로 구현이 어렵다면 protected를 통해 하나의 패키지내에 사용하도록 접근을 제한하여 외부에서 상태값을 방지할 수 있다.
- 

### 트랜잭션 범위
- 트랜잭션의 범위는 작을수록 좋다.
  - 데이터베이스에서 사용할 수 있는 커넥션풀은 제한되어 있다.
  - 오랜시간 트랜잭션 락을 건다면 그만큼 대기하고 있는 트랜잭션이 많아지기 때문
  - 잠금 대상이 많아질수록 동시에 처리할 수 있는 트랜잭션이 줄어든다.
- 하나의 트랜잭션은 한가지의 애그리거트만 담당하여야 한다.
- 하나의 여러 개의 애그리거트를 수정한다면 트랜잭션이 충돌할 가능성이 크고 수정할 애그리거트가 많을수록 여러개의 테이블을 수정할 가능성이 크기때문에
처리량이 떨어지게 된다.
- 하지만 비지니스 요구사항에 따라 복수의 애그리거트를 수정해야 할 상황이 생길 수 있다.
```java
public class Order {
    public void shipTo(ShippingInfo newShippingInfo, boolean isNewAddress) {
        if (isNewAddress) {
            order.getMember().changeAddress(newShippingInfo.getAddress());
        }
    }
}
```
- 이렇게 애그리거트가 다른 애그리거트를 침범하여 의존하게 된다면 서로 다른 애그리거트끼리의 결합도 높아지게 된다.
- 결합도가 높아지면 높아질수록 수정했을 때 처리해야되는 비용이 높아지므로 좋은 설계라고는 하기 어렵다.
- 하지만 비지니스의 요구사항이나 외부 상황에 따라 두개의 애그리거트를 수정해야된다면 상위계층인 응용 서비스에서 수정하도록 하여야 한다.
- 도메인 이벤트를 활용하면 한개의 애그리거틀르 수정하면서도 동기나 비동이로 다른 애그리거트의 상태를 변경할 수 있다.

**두개 이상의 애그리거트가 변경될 수 있는 상황**
- 팀표준: 팀이나 조직의 표준에 따라 사용자 유스케이스와 관련된 서비스의 기능을 하나의 트랜잭션으로 실행할 경우가 있다.
- 기술제약: 기술적으로 이벤트 방식으로 처리하기 어려운 경우
- UI 구현의 편리: 운영자나 사용자의 편리함을 위해 일괄적으로 처리해야 될 경우

### 느낀점
- public 한 set 메서드는 사용을 최대한 자제해야 된다. 어쩔 수 없을 경우 같은 패키지에서만 사용하도록 제한한다.
- 트랜잭션은 애그리거트의 경계를 넘지 않아야 된다는 의미를 이해할 수 있었다.
- 자동으로 시간을 저장하는 어노테이션을 통해 이벤트 트리거가 있을 것이라는 생각은 하고 있었지만 그것이 무엇인지 알지 못했었다.
이제서야 그것이 도메인 이벤트를 통해 한다는 것을 알수 있었다. 
- 도메인 규칙과 일관성을 유지하기 위한 불변객체, 내부에서의 값 변경 등 많은 방법들을 알 수 있었다. 그렇다면 
다른 방법도 있다면 어떤 방법이 있을까?

## 리포지터리와 애그리거트
애그리거트는 개념상 완전한 한개의 도메인을 모델을 표현함으로 도메인 객체의 영속성을 처리하는데 리포지토리는 애그리거트 단위로
존재하게된다. OrderLine 은 애그리거트에 속하는 구성요소이므로 Order를 위한 리포지토리만 존재하게된다.

새로운 애그리거트를 만들면 기본적으로 저장소에 저장하고 조회해야 됨으로 두가지의 기능을 기본으로 제공한다.
- save 
- findById

### 리포지토리와 애그리거트의 관계
- 리포지토리는 애그리거트 전체를 저장소에 영속화 해야한다. 애그리거트와 관련된 테이블이 세개라면 애그리거트가 저장될 때 
애그리거트 루트와 매핑되는 테이블뿐만 아니라 애그리거트 루트가 구성하고 있는 요소까지 전부 저장해야한다.
- 리포지토리 메서드는 완전한 애그리거트를 제공해야 한다. 예시로 주문을 취소할 때 주문목록을 제공하지 않으면
NullPointException과 같은 문제를 발생 시킬 수 있다.
- RDMS는 트랜잭션을 통해 몽고 DB는 애그리거트를 한개의 문서에 저장함으로 손실 없이 저장소에 반영해야한다.

### 느낀점
- 리포지토리와 엔티티를 지금까지 난발하고 있던 시간들을 반성할 수 있었다. 
- 리포지토리와 애그리거트의 관계를 단순히 인프라스트럭처와 나눠주는 레이어로 생각하고 있었지만 해당 내용을 정리하면서 많이
생각이 바뀌었다.
- 도메인이 기능이나 요구사항이 추가될 수록 리포지토리에 대한 메서드가 많아지게 된다. 결국 몇십개의 메서드를 가지게 되면서 점점 유지보수하기가 
어려워진다. 이러한 문제를 해결해야하려면 어떻게 해야될까?
- 책에서는 완전한 애그리거트를 제공해야한다라고 했지만 약간 잘못 정의가 된 것 같다는 생각한다.
- 만약에 애그리거트에서 일부의 구성요소만 필요한 상황이여도 성능을 포기하고 가져오는 것이 옳은 것일까?
- 리포지토리는 도메인의 요구사항에 맞는 완전한 애그리거트를 제공해야된다.로 재정의해야되지 않을까?

## ID를 이용한 애그리거트 참조
- 애그리거트도 다른 애그리거트를 참조한다.
- 애그리거트 간 참조는 필드를 통해 쉽게 참고가 가능하다
```java
public class Order {
    private Member member;
}

...
        
order.getMember().getId();
```
- ORM 기술을 활용하여 애그리거트 루트에 대한 참조를 쉽게 구현할 수 있지만 그로인한 단점도 생기기 시작했다.
  - 편한 탐색 오용
  - 성능에 대한 고민
  - 확장 어려움

애그리거트 참조에 대한 문제점은 아래와 같이 정리할 수 있다.
1. 오용에 대한 고민: 다른 애그리거트에 대한 접근이 쉬워지면서 다른 애그리거트에 대한 수정하고 싶은 유혹에 빠지기 쉽다.
  - 결합도를 높이고 변경이 어렵게 만들어짐
2. 성능에 대한 고민: JPA를 활용한다면 즉시로딩 해야할 때 유리할 수 있고 지연 로딩이 유리할 때도 있다.
3. 확장에 대한 고민: 트래픽이 늘어나게 되면 분산되어 저장되는 경우가 많다. 

이러한 문제를 해결할 수 있는 방법이 ID를 활용하여 다른 애그리거트를 참조하는 것이다.
1. 오용에 대한 고민: 참조하지 않고 있기 때문에 다른 애그리거트를 수정하는 행동을 하려면 해당 애그리거트를 활용해야한다.
2. 성능에 대한 고민: ID를 통해 참조하고 있기 때문에 지연 로딩이나 즉시 로딩을 신경쓰지 않고 상황에 맞는 인프라와 연결하여 사용하면 된다.
3. 확장에 대한 고민: ID를 통해 서로에 대한 의존도를 끊어냈기 때문에 확장에도 유리해진다. 

### ID를 이용한 참조와 조회 성능
아래와 같은 구조로 한 멤버가 가지고 있는 주문을 조회하게 되면 id를 통해 조회하기 때문에 N+1문제가 발생하게 되고
데이터 베이스는 불필요한 I/O 반복하게 된다. 이러한 문제점은 조회에 대한 성능을 떨어뜨리는 원인이 된다.
``` java
public class Member {
  ...
}

public class Order {
  private Long memberId;    
}
```

**문제 해결 방법**

1. 이전처럼 객체를 참조하도록 변환한다.
```java
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;    
}
```
2. DAO를 생성하고 DAO에서 조회 메서드를 통해 조인을 사용한다.
```java
@Repository
public class JpaOrderViewDao implements OrderViewDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<Order> findOrderWithMemberByMemberId(Long memberId) {
        String selectQuery = "select new Order(order, memeber)" +
                "from Order order " +
                "inner join Member member" +
                "on order.memberId = member.id" +
                "where member.id = :memberId " +
                "order by order.id desc ";
        
        TypedQuery<Order> query = em.createQuery(selectQuery, Order.class);
        query.setParameter("memberId", memberId);
        return query.getResultList();
    }
}
```
위와같은 구조로 구현했을시에는 복잡한 관계 설정이나 로딩 설정할 필요없이 구현할 수 있게된다.
쿼리가 복잡하거나 SQL의 특정 기능을 활용해야할 시 Mybatis와 같은 기능을 활용해야할 수도 있다.
시스템의 처리량을 높여주고 비용을 아낄 수 있다는 장점을 가지고 있지만 코드 구현에 대한 복잡성이 높아진다는 단점도 가지고 있다.

그렇다면 서로 다른 애그리거트가 다른 저장소를 사용하고 있는 경우는 어떻게 해야될까? 
그럴경우에는 캐시나 조회전용 저장소를 만들어서 활용하면 된다.

### 느낀점
- 이러한 설계를 하는데에 있어서는 트레이드 오프가 가장 중요할 것 같다. 
- 여러개의 도메인이 복잡하게 얽혀있을 경우 유용하게 활용하기 좋을 것 같다. 
- 실무에서 조회전용 저장소를 두고 인프라 스트럭처를 구현해본 경험있으신 분이 공유하면 좋을 것 같다.
- 아이디를 위한 참조는 복잡하지 않은 상황이거나 개발팀의 규모가 작다면 오히려 역효과가 날 수 있을 것 같다.