# 아키텍처 개요

### 네개의 영역
- 표현, 응용, 도메인, 인프라스트럭처
````JAVA
- 표현 : 사용자의 요청 및 응답을 처리한다.(외부시스템이 될 수도 있고, 실 사용자가 될 수도 있다.)
- 응용 : 표현계층에서 요청한 기능에 대해 구현하는 계층
 > 응용 서비스는 로직을 직접 수행하기보다는 도메인 모델에 로직 수행을 위임한다.
    public class CancelOrderService{
        public void cancelOrder(String orderId){
            Order order = findOrder(orderId);
            order.cancel();
        }
    }
- 도메인 : 도메인 모델을 구현하는 계층, 핵심 로직을 구현한다.
- 인프라스트럭처 : 구현 기술에 대한것
 > DBMS 연동, 큐 메세지 전송/수신, 레디스 등등...
 > 논리적인 개념을 표현하기보다는 실제 구현을 다룬다.
 > 도메인, 응용, 표현 계층은 인프라스트럭처에 의존해서는 안된다.
````
### 계층 구조 아키텍처
- 계층 구조 아키텍처에서는 표현/응용/도메인/인프라 스트럭처를 사용한다.
- 표현계층 과 응용계층은 도메인 영역을 사용
- 도메인 영역은 인프라스트럭처 영역을 사용
- 도메인 복잡도에 따라 응용/도메인을 합치기도 한다.
- 표현 -> 응용 -> 도메인 -> 인프라 스트럭처 (의존 방향)
- 계층 구조는 상위 계층에서 하위 계층으로의 의존 하는 것이 바람직하다.
- 편리함을 위해 응용계층에서 인프라 스트럭처를 의존하기도 한다.
- `하지만 각 계층의 의존 방향이 서로 얽혀있다면, 특정 영역에 해당 계층이 의존하게 된다.`
````JAVA
문제점
1) CalculateDiscountService 만 테스트 하기가 어렵다.
 > 해당 서비스를 테스트 하기 위해서는 외부 인프라 스트럭처(UserGrade)가 완벽하게 동작해야함.

class CalculateDiscountServiceTest {
    private UserGrade userGrade = new UserGrade();  //인프라 스트럭처 의존하게 된다.

    @Test
    @DisplayName("service가 dao에 의존하면 테스트가 어려워진다")
    void test(){
        Assertions.assertEquals(50L,calculateDiscount(100L,"123","VIP"));
    }
    public long calculateDiscount(Long amount, String userId, String grade){
        long newAmount = amount;
        if(userGrade.isOrderExist()){   //인프라 스트럭처 접근
            newAmount = userGrade.applyDiscount(amount, grade, userId);
        }
        return newAmount;
    }
}
-------------------------------------------------------------------------------
2) 구현 방식을 변경하기 어렵게 만든다. 또한, 의존되는 특정 소스가 추가 된다.
 > 새로운 할인 정책 예를들어, 앱 방문 고객만 할인을 적용.

public class CalculateDiscountService {
    private UserGrade userGrade;    //인프라 스트럭처 계층에 접근하는 객체

    public CalculateDiscountService(UserGrade userGrade) {
        this.userGrade = userGrade;
    }

    public long calculateDiscount(Long amount, String userId, String grade){
        
        // ------ 고객 할인금액에 특화된 소스 ----- //
        long newAmount = amount;
        boolean isOrderExist = userGrade.isOrderExist();
        if(isOrderExist){
            newAmount = userGrade.applyDiscount(amount, userGrade, userId);
        }
        /*앱 방문 고객만 할인 적용이라는 요건이 추가 될시, 해당 영역에 부근에 추가 될 것이다.*/
        //userGrade.isAppVisited(); -> newAmount/=2;
        // ------ 고객 할인금액에 특화된 소스 ----- //
        return newAmount;
    }
}
````
### DIP
- 의존성 역전 법칙 : 고수준 모듈은 저수준 모듈을 의존하지 않는다.
- 모듈 수준
  - 고수준 : CalculateDiscountService (가격 할인 계산 기능)
  - 저수준 : UserGrade (외부 인프라 - 고객 등급에 따른 가격할인 정책), 할인정책 적용

````JAVA
- 고수준 모듈
public class CalculateDiscountService {
  private OrderDisCounter orderDisCounter;

  public CalculateDiscountService(OrderDisCounter orderDisCounter) {
    this.orderDisCounter = orderDisCounter;
  }

  public long calculateDiscount(Long amount, String userId, String grade){
    return orderDisCounter.apply(100L);
  }
}


- 고수준/저수준 모듈을 이어주는 매개체
public interface OrderDisCounter {
  long apply(long amount);
}



- 저수준 모듈, 해당 인터페이스를 구현하는 구현체를 만든다.
public class UserOrderDiscounter implements OrderDisCounter{

  private UserGrade userGrade;
  public UserOrderDiscounter(UserGrade userGrade) {
    this.userGrade = userGrade;
  }
  @Override
  public long apply(long amount) {
    if(userGrade.isOrderExist()){
      return appVisitDiscount(userGrade.applyDiscount(amount, "123", "VIP"));
    }
    return amount;
  }
  //추가된 요구사항
  private long appVisitDiscount(long amount){
    return amount/2;
  }
}
````

````JAVA
1) 테스트 코드 작성시 인프라 스트럭처를 주입받는 소스가 없어짐.
class CalculateDiscountServiceTest {

  private OrderDisCounter orderDisCounter;
  @BeforeEach
  public void setup() {
    orderDisCounter = new OrderDisCounter() {
      .... 구현
  }

  @Test
  @DisplayName("service와 dao 의존성이 분리되면 테스트 하기가 쉬워진다.")
  void test(){
    CalculateDiscountService calculateDiscountService = new CalculateDiscountService(orderDisCounter);
    Assertions.assertEquals(25L,orderDisCounter.apply(100L));
  }
}
----------------------------------------------------------------------------
2) 요구사항이 추가되더라도, 고수준 서비스 객체는 변동이 없음.
public class CalculateDiscountService {
  public OrderDisCounter orderDisCounter;

  public CalculateDiscountService(OrderDisCounter orderDisCounter) {
    this.orderDisCounter = orderDisCounter;
  }

  public long calculateDiscount(Long amount, String userId, String grade){
    return orderDisCounter.apply(100L);
  }
}
````
#### DIP 적용시 주의사항
- 고수준 모듈이 저수준 모듈에 의존하지 않도록 하는 것이 목적이므로, 저수준 모듈에서 인터페이스를 추출해서는 안된다.
- 즉, 저수준 모듈을 추상화한 인터페이스는 `고수준 모듈`에 위치한다.

#### DIP와 아키텍처
- 아키텍처 전반적으로 DIP를 적용할시에, 인프라 스트럭처가 응용/도메인에 의존하는 구조가 된다.
- 이와 같이 응용/도메인 영역에서 인터페이스를 주입받는 형태로 할 경우 새로운 기능 추가 또는 외부 인프라 스트럭처 구조가 변경 되더라도 유연하게 대처가능하다.

### 도메인 영역의 주요 구성요소
1. 엔티티( ENTITY )
   1. 고유의 식별자를 갖는 객체로 자신의 라이프 사이클을 갖는다.
   2. 예를들어, 주문 이라는 객체
2. 밸류( VALUE )
   1. 개념적으로 완전한 하나를 표현할 때 사용
   2. 예를들어, 주문 이라는 도메인의 배송지
3. 애그리거트 ( AGGREGATE )
   1. 엔티티와 밸류 객체를 하나로 묶은 것이다. 즉, 엔티티+밸류
   2. 예를들어, 주문도메인 + 배송지
4. 리포지토리 ( REPOSITORY )
   1. 도메인 모델의 영속성을 처리
5. 도메인 서비스 ( DOMAIN SERVICE )
   1. 특정 엔티티에 속하지 않은 도메인 로직
   2. 예를들어, `할인 금액 계산`은 상품/쿠폰/회원등급 등 다양한 조건을 이용하여 구현하는데, 도메인 로직이 여러 `엔티티`와 `밸류`를 필요로 하는경우. 

#### 앤티티와 밸류
- 도메인 모델의 엔티티와 DB 관계형 모델의 엔티티는 같지 않다.

````JAVA
- 주문 도메인 엔티티는 주문만 처리하는 것이 아니라, 배송지 주소 변경 기능도 함께 제공한다.
public class Order {
  private OrderNo number;
  private Orderer orderer;

  public void changeSHippingInfo(ShippingInfo shippingInfo){
    ....
  }
}
````
````JAVA
- 도메인 모델의 엔티티는 두 개 이상의 데이터가 개념적으로 하나인 경우 
  밸류 타입을 이용해서 표현 할 수 있다.
  관계형 데이터 베이스에서는 Orderer 의미상으로 하나인 밸류 타입을 제대로 표현하기 힘들다.
public class Orderer {
   private String name;
   private String email;
    ....
}
````

#### 애그리거트
- 도메인이 커지면 많은 엔티티와 밸류가 생긴다.
- 상위 수준에서 도메인 모델을 볼 수 있어야 전체 모델의 관계, 개별 모델을 이해 할 수 있다.
- 애그리게이트란, 관련 객체를 하나로 묶은 군집이다.
  - 예를들어, `주문` 이라는 도메인은 `배송지, 주문상품, 주문자` 등의 하위 모델로 구성된다. 여기서 `주문`은 상위 개념이라고 표현할 수 있다.
- 애그리거트를 사용하면 객체 군집 단위로 모델을 바라볼수 있다. 또한, 이를 통해 큰 틀에서 도메인 모델을 관리할 수 있다.
````JAVA
- 애그리거트는 루트 엔티티를 갖는다.
- 애그리거트는 엔티티와 밸류 객체를 이용하여 필요한 기능을 실행한다.(구현을 숨겨 캡슐화 한다.)

//root 엔티티, 애그리거트의 상태를 관리한다.
public class Order {
   private OrderNo id;
   private OrderState state;
   private ShippingInfo shippingInfo;

    public void changeShippingInfo(ShippingInfo shippingInfo){
        // 애그리거트의 상태를 관리, 배송지 변경 등
        ....
    }
}
//밸류
public class ShippingInfo {
    private String receiverName;
    ...
}
//엔티티 
public class OrderNo {
    private String id;
}
````

#### 리포지터리
- 애그리거트 단위로 도메인 객체를 저장/조회 하는 기능 제공.
````JAVA
public interface OrderRepository{
    Order findByNumber(OrderNumber number); //애그리거트 단위로 삭제
    void save(Order Order); //애그리거트 단위로 저장
    void delete(Order Order); //애그리거트 단위로 삭제
}
````
- 도메인 모델을 사용하는 코드는 리포지터리를 통해`애그리거트를 조회후 도메인 객체의 기능을 실행`
````JAVA
public class CancelOrderService{
    private OrderRepository orderRepository;//고수준 모듈
    
    public void cancel(OrderNumber number){
        //레포지토리를 통해 애그리거트 조회
        //저수준 모듈인 인프라 계층에 접근.
        Order order = orderRepository.findByNumber(number);
        if(order == null) throw new NoOrderExcepion(number);
        order.cancel(); //객체의 기능을 실행
    }
}
````
### 요청 처리 흐름
````TEXT
HTTP 요청
   ↓
표현계층(Controller, 전송한 데이터 유효성 or 변환) 
   ↓
응용계층(도메인 모델을 이용하여 기능 구현)
   ↓
인프라(저장, 삭제, 조회 등을 처리)
````
### 인프라스트럭처 개요
````JAVA
- 표현, 응용, 도메인 영역을 지원한다.
- 도메인 객체의 영속성 처리, 트랙잭션 등등 보조 기능을 지원한다.
- 도메인/응용 영역에서 인프라 스트럭처를 인터페이스를 접근하면 더욱 유연한 어플리케이션이 된다.
- DIP가 주는 장점을 해치지 않는 범위에서 인프라스트럭처에 대한 의존은 나쁘지 않다.
@Entity
@Table(name = "ORDER")
public class Order{ //주문이라는 도메인, JPA 접근 기술에 의존한다.
    ...
}
````
### 모듈 구성
- 소프트웨어 아키텍처의 각 계층 영역별로 패키지가 구성되어 있다.
````JAVA
- 각 영역별로 패키지를 나눔
package com.myshop
        
            UI
            ↓
        APPLICATION
            ↓
          DOMAIN
            ↓
          INFRA
````
- 도메인이 크면 하위 도메인 별로 모듈을 나눈다.
````JAVA
- 도메인이 크면 하위 도메인으로 나눈다.
package com.myshop
package com.myshop.order   package com.myshop.member
        
            UI                       UI  
            ↓                        ↓
        APPLICATION              APPLICATION
            ↓                        ↓
          DOMAIN                   DOMAIN
            ↓                        ↓
          INFRA                    INFRA
````
- 도메인 모듈은 `애그리거트`를 기준으로 다시 패키지를 구성한다.
````JAVA
package com.myshop.order
- 애그리거트, 모델, 리포지터리는 같은 패키지에 위치힌다.
 > com.myshop.order.domain
                      |- OrderLines.java
                      |- Orderer.java
                      |- OrderRepository.java
                      |- OrderService.java

- 도메인이 복잡하면 도메인 모델,서비스를 다시 나눌수도 있다.
 > com.myshop.order.domain
                     |- order
                          |- Orderer.java
                          |- Order.java ...
                     |- cancel
                          |- Cancel.java ...
                     |- service
                          |- OrderService.java 
                          |- CancelService.java ... 
                     |- infra
                          |- OrderRepository.java
                          |- CancelRepository.java
````
- 한 패키지에 가능하면 10~15개 미만으로 타입 개수를 유지한다.