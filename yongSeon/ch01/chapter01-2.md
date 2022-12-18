## 1.6.1 엔티티
- 도메인을 분리한 모델은 엔티티로 표현이 가능하다.
- 엔티티의 가장 큰 특징은 서로 다른 식별자를 통해 구별한다는 것
- 그렇기 때문에 아래와 같이 equals()를 정의할 수 있다.
```java 
public class Order {
    
    private String orderNumber;
    
    
    @Override
    public Boolean equals(Object obj) {
        if (obj.getClass() != Order.class) return false;
        if(this.orderNumber == null) return false;
        
        Order other = (Order) obj;
        return this.orderNumber.equals(other.getOrderNumber());
    }
    
    public String getOrderNumber() {
        return this.orderNumber;
    }
}
```
## 1.6.2 엔티티의 식별자 생성 방법
  - 특정 규칙에 따라 생성
  - UUID, Nano ID와 같은 고유 식별자 사용
  - 값을 직접 입력
  - 일련 ㅇ호 사용 
```java
    UUID uuid =  UUID.randomUUID()
    
    Article article = new Article(author, titile ...)
    articleRepository.save(article);
    Long articleId = article.getId();
```

## 1.6.3 벨류 타입
**shoppingInfo.class**
```java

public class shoppingInfo {
    // 받는 사람
    private String receiverName;
    private String receiverPhoneNumber;
    
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingZipcode;
    
}
```
- shippingInfo의 정보중 Name과 PhoneNumber는 받는 사람이라는 개념으로 묶을 수 있다.
- Address1, Address2, Zipcode는 주소라는 하나의 개념으로 묶을 수 있다.
- value타임은 이처럼 하나의 개념을 표현하는데 사용
```java
public class Receiver {
    private String name;
    private String phoneNumber;
}

public class Address {
    private String address1;
    private String address2;
    private String zipcode;
}

public shoppingInfo {
    private Receiver receiver;
    private Address address;
}
```
- 벨류 타입의 장점은 벨류 타입을 위한 기능을 만들 수 있다.
- 아래처럼 정수타입 연산이 아닌 돈계산을 위한 기능을 추가할 수 있다.
- 이처럼 값을 변경되지 않는 타입을 불변하다고 표현한다.
```java
public class Money {
    private int value;
    
    public Money add(Money money) {
        return new Money(this.value + money.getValue());
    }

    public Money multiply(Money money) {
      return new Money(this.value * money.getValue());
    }
}
```
**불변 객체를 사용하지 않을 시 생길 수 있는 문제점**
- 아래처럼 price가 변경 될때 참조하고 있는 객체도 같이 변경될 때 가능성이 있다.
```java 
Money money = new Money(1000);
OrderLine line = new OrderLine(product, price, 2);
price.set(2000)
```

## 1.6 엔티티 식별자와 벨류 타입
- 벨류타입으로 식별자를 표현하면 해당 id가 무슨 의미를 가지고 있는지 정확하게 알 수 있다.
```java
public class Order {
    private OrderNo id;
}
```

## 1.6.5 도메인 모델에 set메서드 넣지 않기
```java
public class User {
    private String id;
    private String name;
    
    public String getId(){
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
```
- 위와 같이 의미없는 getter/setter를 남용하는 것은 도메인의 핵심 개념과 의도를 사라지게 만든다.

```java
public class Order {
    public void changeOrderLines();
    public void payment();
}

public class Order {
    public void setOrderLines();
    public void setOrderState();
}
```
- setOrderState는 단순히 상태만 바뀐다는 것을 표현하고 어떤 정보가 바뀌는지 한눈에 알 수 없다.
- payment는 결제와 관련있다는 것을 알 수 있고 도메인 지식으로 코드를 구현하는데에도 자연스럽다.

```java 
Order order = new Order();
order.setOrderLines(lines);
```

```java
public class Order {
  private List<OrderLine> orderLines;
  private ShippingInfo shippingInfo;
  private Money totalAmounts;

  public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo) {
    setOrderLines(orderLines);
    setShippingInfo(shippingInfo);
  }

  private shippingInfo(ShippingInfo shippingInfo) {
    if (shippingInfo == null) {
      throw new IllegalArgumentException("no shippingInfo");
    }
    this.shippingInfo = shippingInfo;
  }
}
```
- set으로 구현하다보면 불완전한 객체가 위처럼 만들어 질 수 있다.
- 생성자를 통해 받고 처리하는 것이 더 안전하게 처리가 될 수 있다.

1.7 도메인 용어와 유비 쿼터스 용어
- 만약 주문의 상태를 아래처럼 정의하였을 경우
```java
public enum OrderSate {
    STEP1, STEP2, STEP3, STEP4 ...
}
```
- 배송지 변경이나 개발자가 전체 코드를 봐도 무엇을 정의하고 있는 것인지 어떤 비지니스 로직을 작성했는지 알 수 없다.
```java
public class Order {
    public void changeShippingInfo(ShippingInfo shippingInfo) {
        verifyStep1OrStep2();
        setShippingInfo(shippingInfo);
    }
}
```
- 최대한 도메인 언어를 사용하여 도메인 규칙을 정의하면 해석하는 시간이 그만큼 줄어들게 된다.
- 에릭 에반스는 유비쿼터스 언어를 사용하여 관계자, 개발자, 전문가가 공통된 언어를 활용하여 문서, 대화, 테스트에도 모두 같은 언어를 사용한다고 한다.
- 충분한 수준의 도메인 언어를 사용하지 않는다면 코드와 도메인은 멀어지기 때문에 충분히 고민해야한다.
```java
public enum OrderSate {
    PAYMENT_WAITING, PREPARING, SHIPPED
}
```
