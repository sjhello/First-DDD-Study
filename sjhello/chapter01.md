# 도메인이란 무엇인가?
 - 현실세계에 있는 것을 소프트웨어로 표현 하고자 한 것
 - 도메인은 하위 도메인을 포함 하기도 하는데 반드시 고정된 하위 도메인이 존재하는 것은 아니다.

# 도메인 전문가와 개발자간의 지식 공유
 - 현실에 있는 도메인을 소프트웨어로 표현하려면 어떻게 해야하는가?
   - 가장 간단한 방법으로 도메인 전문가와 직접 대화하여 요구사항을 이끌어 내는 것이다.
 - 원하는 제품을 정확히 만들기 위해서는 개발자 또한 도메인 지식을 갖추는 것이 좋다.
 - 도메인 지식공유를 통해 도메인 전문가가 자신이 원하는 요구사항이 무엇인지 정확하게 이끌어 낼 수 있다.
   - Garbage in Garbage out

# 도메인 모델
 - 도메인 자체를 이해하기 위해 개념적으로 표현한 모델
 - 도메인 모델을 이용하여 여러 사람들이 도메인 지식을 공유할 수 있다.
 - 도메인 모델의 구성 요소
   - 기능(행위)
   - 데이터 구성(상태)
 - 도메인 마다 다루는 영역이 서로 다르기 때문에 용어가 같을 지라도 의미가 다를 수 있다.
   - 의미가 다르기 때문에 여러 의미에 맞게 도메인을 표현하는 도메인 모델을 따로 만들어야 한다

# 도메인 모델 패턴
  ![](../sjhello/image/chapter01/chapter01-architecture.png)
 - 흐름은 보통 위(Presentation) 에서 부터 아래(Infrastructure)로 흐르는 것을 기본으로 한다.
 - Presentation(표현)
   - 사용자의 요청을 받고(Request), 검증(Validation), 응답(Response) 하는 역할
 - Application(응용)
   - 도메인을 조합하여 기능을 실행하는 부분
 - Domain
   - 도메인 전문가와 협업하여 이끌어낸 요구사항이 담기는 부분
 - Infrastructure
   - 외부 시스템과의 연동을 담당하는 부분

# 엔티티와 벨류

## 엔티티(Entity)
각각의 엔티티는 **고유한 식별자**를 가지는데 식별자에 따라서 같은 객체인지 다른 객체인지 구분하고 equals()와 hasCode()를 이용하여 구현 할 수 있다.
```java
public class Member {

	private Long id;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Member member = (Member)o;
		return Objects.equals(id, member.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
```

## 식별자를 생성하는 방법
 - 특정 규칙에 따라 생성
   - 같은 식별자가 만들어지는 것을 주의 
   - 202212131944: 날짜 + 시간의 조합
 - UUID, Nano ID 생성
   - java.util.UUID
 - 값을 직접 입력
   - 회원의 id
   - 이메일 주소
 - 일련번호
   - DB의 Sequence Number

## 밸류 타입(Value Object)
 - 엔티티와는 다르게 식별자가 없는 도메인
 - 어떠한 개념에 대해서 그 개념을 하나로 표현할 수 있을때 밸류 타입을 사용 할 수 있다
```java
public class Member {
	private Long id;
	
    // private String address1;
    // private String address2;
    // private String zipCode;
   
    private Address address;
	
   ...
}

public class Address {
	private String address1;
	private String address2;
	private String zipCode;
}
```
 - 장점
   - 밸류 타입을 위한 기능을 추가 할 수 있다
   - 어떠한 데이터를 의미있는 객체로 표현하여 기본 자료형으로 표현된 코드보다 코드를 이해하는 것이 쉽다 
```java
public class OrderLine {
	private Product product;
	private Money price;
	private int quantity;
	private Money amount;   // 주문한 단일 상품에 대한 총가격
}

public class Money {
	private int price;

   public Money add(Money money) {
      return new Money(this.value + money.value);
   }

   public Money multiply(int multiplier) {
      return new Money(this.value * multiplier);
   }

   /* 생성자, Getter */
}
```

## 도메인, 밸류 타입에 외부에 공개되는 set 메서드 지양하기 - 불변객체
 - 객체가 불변하면? 서로 다른 참조를 갖기 때문에 변경에 대해 안전하다 
   - 같은 참조에 의한 예상하지 못한 변경을 피할 수 있다
   - Thread Safe
```java
public class SetTest {

	@Test
	void changeTest() {
		// given
		Money money = new Money(12);
		OrderLine orderLine = new OrderLine(money);

		// when
		money.setPrice(13);

		// then
		assertThat(orderLine.getOrderPrice().getPrice()).isEqualTo(12);
	}

}

class OrderLine {
	private Money orderPrice;

	public OrderLine(Money orderPrice) {
		this.orderPrice = orderPrice;
		
		/* 가변객체라면 새로운 참조를 생성해줘야 한다 */
		// this.orderPrice = new Money(orderPrice.getPrice());
	}

	public Money getOrderPrice() {
		return orderPrice;
	}
}

class Money {
	private int price;

	public Money(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
}
```

# 도메인 용어와 유비쿼터스 언어
 - 도메인에서 사용하는 용어를 코드에 반영함으로써 소통의 모호함과 용어를 해석하는 과정을 줄일 수 있다.
```java
public enum OrderState {
	/* STEP1, STEP2, STEP3, STEP4, STEP5 */
    PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED
}
```
 - 
