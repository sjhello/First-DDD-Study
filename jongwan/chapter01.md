
도메인
> 해결하고자 하는 문제영역
> 도메인은 다수의 하위 도메인으로 구성 된다.
ex) 쇼핑몰 주문은 배송, 상품 등등의 하위 도메인으로 구성된다.

도메인 전문가와 개발자간 지식 공유
> 코딩에 앞서 요구사항을 올바르게 이해하자. 그렇지 않으면 개발 비용이 높다.
개발자와 전문가(도메인에 정통한)가 직접 대화를 통해 문제에 대해 의논하는게 가장 좋다.

도메인 모델
> 해결하고자 하는 문제를 이해하기 위한 개념 모델이다.
> 개념 모델, 구현 모델은 서로 다르지만 최대한 개념 모델을 따르도록 할 수 있다.
ex) 객체 지향 언어를 활용하여 개념 모델에 가깝게 만든다.

도메인 모델 패턴
> 아키텍처를 객체 지향 기법으로 구현하는 패턴을 말한다.

도메인 모델 도출
> 기획서, 유스케이스, 사용자 스토리 등 요구사항을 관련자와 대화를 통해 도메인을 이해해야 소스로 작성할 수 있다.
> 이를통해 도메인과 관련된 구성요소, 규칙, 기능 을 찾는다.

## 1.6 앤티티와 밸류
- 도출한 모델은 크게 엔티티, value로 구분할 수 있다.
- 엔티티와 value를 제대로 구분해야 도메인을 올바르게 설계/구현 할 수 있다.


### 엔티티 : 식별자를 갖는다 
```text
 - 주문 엔티티에서 주문번호는 식별자이다.
 - 배송지 변경, 주문 상태가 변경되더라도 주문의 식별자는 변경 되지 않는다.
```

```JAVA
- 식별자를 이용해서 equals, hashCode 구현
- orderNumber가 같으면 값은 엔티티로 판단을 한다.

public class Order {
    String orderNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderNumber, order.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber);
    }
}
```
### 엔티티의 식별자 생성
- 동일한 엔티티의 식별자가 만들어져서는 안된다.
```text
1. 특정 규칙에 따라 생성
 - 흔히 사용하는 규칙은 현재시간 + 날짜 시간을 이용한다.
 
2. UUID, Nano ID 같은 고유 식별자 생성기 사용
 - java.util.UUID 식별자 클래스

3. 값을 직접 입력
 - 회원 아이디나, 이메일과 같은 식별자값 입력

4. 일련번호 사용(시퀀스, DB 자동 증가 칼럼)
 - mysql 자동증가 칼럼
```

### 밸류타입
 - 개념적으로 완전한 하나를 표현할 때 사용
````JAVA
- 고객명/휴대폰번호는 다른 데이터를 담지만 개념적으로는 '받는사람' 을 표현한다.

public class Order {
    String orderNumber;
    private OrderState state;
    private ShippingInfo shippingInfo;  //배송정보, 하나의 엔티티로 생각
    ...
}
public class ShippingInfo {
    private String receiverName;        //받는사람
    private String receiverPhoneNumber; //받는사람
    private Address address;            //주소, 하나나의 엔티티로 생각

    public ShippingInfo(String receiverName, String receiverPhoneNumber, Address address) {
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.address = address;
    }
}
````
- 의미를 명확하게 표현하기 위해 밸류타입을 사용하는 경우도 있다.
````JAVA
- 상품가격(price), 구매금액(amount)는 int 타입으로 표현할 수 있지만
  Money라는 밸류타입을 만들어 사용하면 조금 더 이해하기 쉬운 코드를 작성할 수 있다.  

public class OrderLine {
    private Product product;
    private Money price;
    private int quantity;
    private Money amounts;
}
````
- 밸류 객체의 데이터를 변경할 때는 기존 데이터를 변경하는것 보다는 ''새로운 밸류 객체''를 생성하는 방식을 선호한다.
````JAVA
- AS-IS : line의 가격자체가 2000으로 변경되 버린다.

public OrderLine(Product product, Money price, int quantity, Money amounts) {
    this.product = product;
    this.price = price;
    this.quantity = quantity;
    this.amounts = amounts;
}

@Test
@DisplayName("MoneyTest")
public void MoneyImmutable(){
    Money price = new Money(1000);
    OrderLine line = new OrderLine(new Product("상품1","001"), price,1,new Money(20000));
    price.setValue(2000);
    //line.getPrice().getValue() : 2000원
    //price.getValue() : 1000원
    Assertions.assertNotEquals(line.getPrice().getValue(), price.getValue());
}

######################################################################
- TO-BE : 생성자가 money 를 새로 만들면, line의 가격자체가 1000으로 동일하다.

public OrderLine(Product product, Money price, int quantity, Money amounts) {
    this.product = product;
    this.price = new Money(price.getValue());
    this.quantity = quantity;
    this.amounts = calculateAmounts();
}

@Test
@DisplayName("MoneyTest")
public void MoneyImmutable(){
    Money price = new Money(1000);
    OrderLine line = new OrderLine(new Product("상품1","001"), price,1,new Money(20000));
    price.setValue(2000);
    //setvalue 를 넣어도 Money객체를 생성했으므로 line은 1000원이 새로 주입된다.
    Assertions.assertEquals(line.getPrice().getValue(), 1000);
}
````
- 두 밸류 객체를 비교할 때는 모든 속성이 같은지 비교한다.
````JAVA
public class ShippingInfo {
    private String receiverName;
    private String receiverPhoneNumber;
    private Address address;
    
    //생성자 ...
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingInfo that = (ShippingInfo) o;
        return Objects.equals(receiverName, that.receiverName) 
                && Objects.equals(receiverPhoneNumber, that.receiverPhoneNumber);
    }
    @Override
    public int hashCode() {
        return Objects.hash(receiverName, receiverPhoneNumber);
    }
}
````
- 엔티티 식별자와 밸류 타입
  - 엔티티 식별자는, 밸류 타입을 사용해서 의미가 잘 드러나도록 할 수 있다.
````JAVA
public class Order {
    private OrderNo id;
    ...
}

public class OrderNo {
    private String id;

    public OrderNo(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderNo orderNo = (OrderNo) o;
        return Objects.equals(id, orderNo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
````
- 도메인 모델에 set 메서드 넣지 않기
````JAVA
- 도메인 모델에 get/set 메서드를 무조건 추가하는 것은 좋지 않은 버릇이다.
- setShippingInfo() 보다는 changeShippingInfo() 가 의미가 조금 더 명확하다.
- set 메서드를 남발하면, 객체를 생성할 때 온전하지 않은 상태가 될 수 있다.
 > 실수할 여지가 많다.(필수적으로 set 해야할 부분을 누락할 수 있음).
  //생성자에서 필수 값인지 여부를 캐치할 수 없다.
  Order order = new Order();
  order.setOrderLine(lines);
  order.setShippingInfo(shippinfo);
  
  //생성자에서 필수로 받으므로, 누락할 가능성이 없다.
  Order order = new Order(orderLines,shippingInfo);
  
- 만약 객체를 변경해야하는 set 메서드를 사용해야하면 객체안에서 private 메서드로 사용한다.
  public class Order {
    public void changeShippingInfo(){
        setShippingInfo();
    }
    private void setShippingInfo(){
        //private 메서드 이므로, 외부에서 사용불가 메서드
    }
  }
````
### 도메인 용어와 유비쿼터스 언어
- 도메인에서 사용하는 용어는 매우 중요하다. 도메인에서 사용하는 용어를 코드에 반영하지 않으면 코드 의미 해석이 어려워 질 수 있다.
````JAVA
- 해석 어려움
public enum OrderState {
  step1, step2, step3.... 
}
- 해석하기 편해짐
public enum OrderState {
    PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELLED;
}
````