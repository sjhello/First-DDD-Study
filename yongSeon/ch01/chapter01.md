# 도메인 시작하기
## 1.1 도메인이란

### 도메인 예시(영화 예매 시스템)

- 예매
  - 할인
  - 결제
- 영화
  - 상영 기간
  - 배우
  - 감독
- 상영관
    - 자리
    - 상영시간

### 개발자 관점으로 도메인
 
- 소프트웨어를 통해 문제를 해결해야 되는 범위
- 비지니스에 근거하여 모델링을 하게 됨.
- 비즈니스 문제에 맞게 코드를 구성하고 동일한 비즈니스 용어(유비쿼터스 언어)를 사용하는 것입니다.
- 도메인은 서로 고정된 관계가 아니다. 도메인에 대한 모든 것을 구현하지 않는다.
  - 엑셀, 외부결제, 수작업 등으로 작업되어 질 수 있다.
- 도메인은 복잡한 비지니스의 관계를 알기 쉽게 풀어낸 것이라고 생각한다.

[참고자료](https://learn.microsoft.com/ko-kr/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice)

## 1.2 도메인 전문가와의 지식 공유

- 개발에서 중요한 것은 요구사항에 맞게 개발하는 것
- 도메인 전문가는 기획자를 말하는 것이 아니다. 회계사, AS기사, 의사등 해당 분야에서 근무하고 있는 사람들이 
도메인에 지식과 경험을 바탕으로 요청하는 사람들이 전부 도메인 전문가다.
- 지식 공유가 필요한 이유? 
  - 공통된 비지니스 용어(유비쿼터스 언어)로 풀어내어 협업하기 쉬운 환경을 만들기 위함
  - 요구한 사항에 맞춰서 정확하게 개발하기 위함.
  - 요구사항에 맞춰 개발하지 않은 소프트웨어는 쓸모없거나 유용함이 떨어진다.
- 지식 공유를 하는 방법
  - 개발자와 전문가가 직접 대화하는 것
  - 전달자를 줄여야 한다. 전달자가 많을수록 정보는 왜곡되어 전달된다.
  - 전문가라고 모든 것이 명확하진 않기 때문에 해당 요구사항이 나오게 된 배경이나 이유를 들어야 한다.
  - 예시: "로딩 속도를 높여주세요"라고 요청이 들어오면 캐싱 기능, 쿼리 튜닝, 비동기 처리 등을 할 수도 있지만 로딩화면에 
간단한 애니메이션을 넣음으로써 상대적으로 빠르다고 느끼게 할 수 있다. 이처럼 요구사항보다 문제를 인식함으로써 좀 더 쉽게 요구사항을 해결할 수 있다.

## 1.3 도메인 모델
- 도메인 모델이란 특정 도메인에 대해 개념적으로 표현한 것
- 도메인 모델링은 클래스나 상태같은 UML 표기법만 사용되어지는 것아 아니다. 수학 공식, 그래프 등 도메인을 모델을 만들 수 있다.
- 도메인 모델은 도메인 자체를 이해하기 위한 개념 모델이다. 개념 모델을 이용하여 코드를 바로 짤 수 없기에 구현 모델을 따로 필요하다.
- 하위 도메인을 여러개로 묶으면 안된다. 해당 도메인을 이해하는데 오히려 방해가 되기 때문이다. 예를 들어 카탈로그와 배송 상품을 하나의 도메인으로 묶으면 카탈로그 상품과
배송상품에 대한 이해를 떨어트린다. 하위 도메인은 특정되어 질 때 완전해진다.  

## 1.4 도메인 모델 패턴
| 계층                 | 설명                                                          |
|--------------------|-------------------------------------------------------------|
| 표현 또는 사용자인터페이스(UI) | 사용자의 요청을 처리하고 사용자에게 정보를 보여준다.                               |
| 응용 | 사용자가 요청한 기능을 실행한다. 업무 로직을 직접 구현하지 않으며 도메인 계층을 조합해서 기능을 실행한다 |
| 도메인 | 시스템이 제공할 도메인의 규칙을 구현한다.                                     |
| 인프라스트럭처 |  데이터베이스나 메시징 시스템과 같은 외부 시스템과의 연동을 처리한다.                     |

### 도메인 모델 패턴 예시
- 출고 전에 배송지를 변경할 수 있다. 주문 취소는 배송 전에만 할 수 있다. 
``` JAVA
public class Order {
	private OrderState state;
	private ShippingInfo shippingInfo;

	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		if (!state.isShippingChangeable()) {
			throw new IllegalStateException("can't change shipping in " + state);
		}
		this.shippingInfo = newShippingInfo;
	}
	...
}
```
``` JAVA
public enum OrderState {
	PAYMENT_WAITING {
		public boolean isShippingChangeable() {
			return true;
		}
	},
	PREPARING {
		public boolean isShippingChangeable() {
			return true;
		}
	},
	SHIPPED, DELIVERING, DELIVERY_COMPLETED;

	public boolean isShippingChangeable() {
		return false;
	}
}
```
1. OrderState는 배송지를 변경할 수 있는지 체크하는 isShippingChangeable() 메서드를 제공한다.
2. 주문 대기중(PAYMENT_WAITING), 준비중(PREPARING) 상태는 true를 리턴한다.
3. 주문 대기 중이거나 상품 준비중에는 배송지를 변경하는 도메인 규칙을 구현

### 확장을 고려한 설계

```JAVA
public class Order {
	private OrderState state;
	private ShippingInfo shippingInfo;

	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		if (!state.isShippingChangeable()) {
			throw new IllegalStateException("can't change shipping in " + state);
		}
		this.shippingInfo = newShippingInfo;
	}
	
    private boolean isShippingChangeable() {
        return state == OrderState.PAYMENT_WATING || state == OrderState.PREPARING;
    }
}

public enum OrderState {
  PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED;
}
```
- 배송지 변경을 주문 상태와 다른 정보와 함께 판단한다고 한다면 Order에서 로직을 구현해야한다.

### 개념 모델과 구현 모델
- 개념 모델은 순수한 문제를 분석한 결과이다.
- 개념 모델을 처음부터 완벽하게 설계하는 것은 불가능하다. 데이터베이스나 트랜잭션, 구현 기술등을 고려하지 않았기 때문
- 완벽한 도메인 모델을 설계하는 것보다 전반적인 개요를 알 수 있는 것이 중요

## 도메인 모델 도출
- 기획서, 유스케이스 사용자 스토리와 같은 요구사항과 관리자와의 대화를 통해 도메인을 이해해야 코드를 작성할 수 있다.
- 구현을 시작하려면 도메인에 대한 초기 모델이 필요하다.
### 도메인 모델링 예시
**요구사항**
- 최소 한 종류 이상의 상품을 주문해야 한다.
- 한 상품을 한 개 이상 주문할 수 있다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.
- 각 상품의 구매 가격 합은 상품 가격에 구매 개수를 곱한 값이다.
- 주문할 때 배송지 정보를 반드시 지정해야 한다.
- 배송지 정보는 받는 사람 이름, 전화번호, 주소로 구성된다.
- 출고를 하면 배송지 정보를 변경할 수 없다.
- 출고 전에 주문을 취소할 수 있다.
- 고객이 결제를 완료하기 전에는 상품을 준비하지 않는다.

위의 요구사항을 봤을 때 주문은 출고상태변경, 배송지 정보변경, 주문 취소, 결제 완료까지 제공한다.
```JAVA
public class Order {
	public void changeShipped() { ... }
	public void changeShippingInfo(ShippingInfo newShipping) { ... }
	public void cancel() { ... }
	public void completePayment() { ... }
}
```
- 한 상품을 한 개 이상 주문할 수 있다.
- 각 상품의 구매 가격 합은 상품 가격에 구매 개수를 곱한 값이다.
  적어도 OrderLine은 주문할 상품, 상품의 가격, 구매 개수를 포함해야하고 각 구매 항목의 가격도 제공해야한다.
```JAVA
public class OrderLine {
  private Product product;
  private int price;
  private int quantity;
  private int amount;
  
  private int calculateAmounts() {
      return price * quantity;
  }
}
```

- 최소 한 종류 이상의 상품을 주문해야 한다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.
위와 같은 요구사항을 봤을 때 Order와 OrderLine의 관계를 정의하여 구현할 수 있다.
1. 한개 이상의 OrderLine을 가짐으로써 List형태로 전달한다.
2. 한개 이상 존재해야 함으로 verifyAtLeastOneOrMoreOrderLines를 통해 확인한다.
3. 총 주문 금액을 계산해야 함으로 calculateTotalAmounts를 통해 제공한다.
```JAVA
public class Order {
    private List<OrderLine> orderLines;
    private Monty totalAmounts;
    
    public Order(List<OrderLine> orderLines) {
        setOrderLines(orderLines);
    }
    
    private void setOrderLines(List<OrderLine> orderLines) {
      verifyAtLeastOneOrMoreOrderLines(orderLines);
      this.orderLines = orderLines;
      calculateTotalAmounts();
    }
    
    private verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("no OrderLine");
        }
    }
    
    private calculateTotalAmounts() {
        int sum = orderLines.stream
                .mapToInt(x -> x.getAmounts())
                .sum();
        
        this.totalAmounts = new Money(sum);
    }
}
```

배송지 정보는 이름, 전화번호, 주소 데이터를 가짐으로 ShippingInfo클래스를 아래와 같이 정의한다.
```JAVA
public class ShippingInfo {
    private String receiverName;
    private Stirng receiverPhoneNumber;
    private Address address;
}
```

주문할 때 반드시 주소지를 추가해야 함으로  Order의 생성차에 추가해준다.
```JAVA
public class Order {
  private List<OrderLine> orderLines;
  private ShippingInfo shippingInfo;
  private Monty totalAmounts;

  public Order(List<OrderLine> orderLines) {
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

- 출고를 하면 배송지 정보를 변경할 수 없다.
- 출고 전에 주문을 취소할 수 있다.
- 고객이 결제를 완료하기 전에는 상품을 준비하지 않는다.
위와같은 요구사항은 출고 상태에 따라서 다른 제약을 가지기 때문에 최소한 출고 상태를 표현해야한다. 세번째도 마찬가지로 상태를 가지고 있어야 한다. 

```JAVA
public enum OrderState {
	PAYMENT_WAITING, 
	PREPARING, 
	SHIPPED, 
	DELIVERING, 
	DELIVERY_COMPLETED, 
	CANCELED 
}
```

배송지 변경이나 주문 취소 기능은 출고 전에만 가능한 제약 조건이 있기 때문에 verifyNotYetShipped를 항상 실행해야한다.
``` JAVA
public class Order {
	private OrderState state;
	
	
    public void changeShippingInfo(ShippingInfo newShippingInfo) {
      verifyNotYetShipped();
      setShippingInfo(newShippingInfo);
    }
    
	public void cancel() { 
	  verifyNotYetShipped();
	  this.state = OrderState.CANCELED;
	}
	
	public void verifyNotYetShipped() { 
	  if (state != OrderState.PAYMENT_WAITING && state != OrderState.PREPARING) {
	    throw new IllegalSateException("aleady shipped");
	  }
	}
}
```

### 문서화
- 문서화는 지식을 공유하기 위함
- 직접 이해하는 것보다 상위 수준에서 정리한 문서를 참조하는 것이 소프트웨어 전반을 빠르게 이해하는 데 도움이 된다. 
- 코드를 보면서 도메인을 깊게 이해하게 되므로 코드 자체도 문서화의 대상이 된다.
- 단순히 코드를 보기 좋게 작성하는 것뿐만 아니라 도메인 관점에서 코드가 도메인을 잘 표현해야 비로소 코드의 가독성이 높아지며 문서로서 코드가 의미를 갖는다.