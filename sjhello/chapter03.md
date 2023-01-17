# 3.1 애그리거트
![](../sjhello/image/chapter03/aggregate.jpeg)

개별 객체 수준에서 모델을 바라보았을때에는 전반적인 구조, 큰 수준에서 도메인 간의 관계를 파악하기 어렵다.
코드를 작성하기 전에 상위 수준에서 모델이 어떻게 엮이는지 알아야 기존 모델을 깨트리지 않으면서 요구사항을 반영할 수 있는데 세부적인 모델 수준만을 이해한 상태로는 코드 수정이 쉽지않다.
그래서 세부적으로 이해한 관계를 쉽게 만들기 위해 상위 수준에서 모델을 바라볼 수 있어야 하는데 **애그리거트**가 이때 도움이 된다

![](../sjhello/image/chapter03/aggregate2.jpeg)

애그리거트는 도메인 모델에서 관련된 객체들을 하나의 군으로 묶어주는 역할을 한다.
애그리거트로 묶인 모델들의 특징은

 - 모델을 애그리거트 단위로 묶었기 때문에 개별적으로 모델을 바라보는 것보다 이해하기 쉬워진다.
   - 이해하는데에 도움을 주고 일관성을 관리하는 기준이 된다
 - 한 애그리거트에 속한 객체는 유사하거나 동일한 라이프 사이클을 갖는다.
 - 한 애그리거트에 속한 객체는 다른 애그리거트에 속하지 않는다

## 애그리거트 경계 설정 시 주의사항
애그리거트간 경계를 설정할 때 기본이 되는 것은 **도메인 규칙과 요구사항**이다. 도메인 규칙에 따라 함께 생성되는 구성요소는 한 애그리거트에 속할 가능성이 높다
> 예를들어 주문을 생성할때 상품 개수, 배송지 정보, 주문자 정보는 주문시점에 생성되므로 한 애그리거트에 속하게 된다

```java
public class Order {
	private List<OrderLine> orderLineList;
	private ShippingInfo shippingInfo;
	private Orderer orderer;
	private Address address;
    
    ...
}

public class Address {
	private String address1;
	private String address2;
	private String zipCode;
	
    ...
}
```

---

'A가 B를 갖는다'로 설계할 수 있는 요구사항의 경우 두 모델을 한 애그리거트로 묶어서 생각하기 쉽지만 그럴수도 있고 아닐수도 있다.
예를 들어 상품(Product)과 리뷰(Review)는 위에서 설명한 애그리거트로 묶인 모델의 특징을 갖을 수 없다. 상품과 리뷰가 같은 애그리거트에 묶인다 할지라도 이 두 객체는 같이 생성되지 않고, 함께 변경되지 않는다.
또한 상품(상품 담당자)과 리뷰(상품을 구입한 고객)에 접근하는 주체가 서로 다르다. 따라서 **상품의 정보가 바뀌었다고 해서 리뷰의 정보가 바뀌지 않고 그 반대의 경우도 마찬가지**이다

그래서 이 둘은 서로 다른 애그리거트에 속한다.

![](../sjhello/image/chapter03/aggregate3.jpeg)

# 3.2 애그리거트 루트
애그리거트는 여러 객체로 구성되기 때문에 한 객체만 상태가 정상이면 안된다. 여기서 말하는 정상은 일관성을 갖는다는 말이다.

![](../sjhello/image/chapter03/aggregateRoot.jpeg)

 - 애그리거트에 속한 모든 객체가 일관된 상태를 가져야 한다
 - 애그리거트 전체를 관리할 주체가 바로 **애그리거트 루트**
 - 애그리거트에 속한 모델은 애그리거트 루트(Order)에 직접 또는 간접적으로 속하게 된다

## 3.2.1 도메인 규칙과 일관성
애그리거트 루트는 단순히 애그리거트에 속한 객체를 포함하는것으로 끝나지 않는다. **애그리거트의 일관성이 깨지지 않게 하는것이 핵심이다**. 도메인 규칙에 따라 애그리거트에 속한 객체의 일관성이 깨지지 않도록 구현해야한다.

일관성을 지키기 위해 애그리거트 외부에서 애그리거트에 속한 객체를 직접 변경해선 안된다. 외부에서 변경을 허용한다면 애그리거트 루트가 강제하는 규칙(도메인 규칙)을 적용할 수 없어 일관성을 깨는 원인이 된다. 일관성을 지키기 위해 애그리거트 루트를 통해서만 기능을 사용하게 해야한다.

애그리거트 루트를 통해서만 도메인 로직을 구현하게 하려면 도메인 모델에 두가지 습관을 적용해야 한다.
 - 단순히 필드를 변경하는 set 메서드를 public으로 만들지 않는다.
 - 밸류 타입은 불변으로 구현한다.

### 1. 필드를 변경하는 set 메서드를 public으로 두는 경우
```java
ShippingInfo si = order.getShippingInfo();
si.setAddress(newAddress);
```
배송지 정보를 바꾸기 위해서는 배송이 시작되기전 상품만 배송지 정보 변경이 가능한 도메인 규칙이 있다.
이 코드는 도메인 규칙을 무시하면서 ShippingInfo의 상태를 수정하는 결과를 만들게 되고 일관성이 깨지게 된다.

```java
// in Service..
ShippingInfo si = order.getShippingInfo();

if (state != OrderState.PAYMENT_WAITING && state != OrderState.PREPARING) {
	throw new IllegalException("배송지 정보를 변경 할 수 없습니다");
}

si.setAddress(newAddress);
```
또 다른 예시로 상태를 확인하는 로직을 서비스에 구현한 경우인데, 이렇게 되면 상태를 변경하기 위한 로직이 여러 서비스에 중복으로 구현되고 코드의 응집도(기능적 응집도)를 해쳐 유지보수에 도움이 되지 않는다

### 2. 밸류 타입은 불변으로 구현
밸류 객체를 불변으로 구현하면 애그리거트 루트에서 밸류 객체를 구해도 외부에서 밸류 객체의 상태를 변경 할 수 없다.
외부에서 밸류 객체를 변경하려면 새로운 밸류 객체를 생성하는 것 뿐이다.

```java
public class Order {
	private ShippingInfo shippingInfo;

	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		verifyNotYetShipped();
		setShippingInfo(newShippingInfo);
	}

	private void setShippingInfo(ShippingInfo newShippingInfo) {
		this.shippingInfo = newShippingInfo;
	}
}
```
애그리거트 루트가 제공하는 메서드를 통해서만 배송지 정보를 변경하게 하였다. 도메인 규칙을 적용할 수 있게 되었고 외부에서 public setXXX 메서드를 사용하지 못하게 함으로써 애그리거트의 속한 객체의 일관성을 지키게 할 수 있다.


## 3.2.2 애그리거트 루트의 기능 구현
애그리거트 루트는 애그리거트 내부의 다른 객체를 조합해서 기능을 완성한다.

 - 구성요소의 상태를 참조
 - 애그리거트 내부 객체에게 기능을 위임

### 구성요소의 상태를 참조
```java
public class Order {
	private Money totalAmounts;
	private List<OrderLine> orderLines;

	private void calculateTotalAmounts() {
		int sum = orderLines.stream()
					.mapToInt(ol -> ol.getPrice() * ol.getQuantity())
					.sum();
		this.totalAmounts = new Money(sum);
	}
}
```
Order는 OrderLine 목록을 참조하여 하나의 OrderLine의 가격과 주문량을 곱하여 주문의 총 가격을 계산한다

### 애그리거트 내부 객체에게 기능을 위힘
```java
public class Order {
	private OrderLines orderLines;
	private Money totalAmounts;

	public void changeOrderLines(List<OrderLine> newLines) {
		orderLines.changeOrderLines(newLines);
		this.totalAmounts = orderLines.getTotalAmounts();
	}

	// getter
}

public class OrderLines {
	private List<OrderLine> lines;

	public Money getTotalAmounts() {...}
	public void changeOrderLines(List<OrderLine> newLines) {
		this.lines = newLines;
	}
}
```
기술적 제약이나 내부 모델링 규칙 때문에 OrderLine 목록을 별도의 클래스로 분리했다고 하자.
OrderLines는 getTotalAmounts()와 changeOrderLines() 상태를 변경하는 기능을 public 하게 공개하고 있다.

```java
OrderLines lines = order.getOrderLines();

lines.changeOrderLines(newOrderLines);
```
Order로 부터 OrderLines 객체를 가져오고 OrderLines에 새로운 List<OrderLine>을 끼워넣는다.
이렇게 되면 총 가격을 변경하는 도메인 규칙은 적용하지 않은채 주문한 상품 목록을 바꾸게 되어 애그리거트 내부 객체의 상태의 일관성이 깨지게 된다.

이러한 일이 생기지 않도록 하려면 외부에서 List<OrderLine>을 변경할 수 없도록 OrderLines를 불변으로 구현하면 된다.

**불변으로 구현할 수 없다면 OrderLines의 변경 기능을 package나 protected로 한정해서 외부에서 실행할 수 없도록 제한하는 방법도 있다.**

보통 한 애그리거트에 속하는 모델을 한 패키지에 속하기 때문에 package나 protected 범위를 사용하면 애그리거트 외부에서 상태 변경 기능을 실행하는 것을 방지 할 수 있다.

## 3.2.3 트랜잭션 범위
한 트랜잭션에서는 한 개의 애그리거트만 수정해야한다. 한 트랜잭션에서 두개 이상의 애그리거트를 수정하는 것은 자신의 책임 범위를 넘어 다른 애그리거트의 상태까지 관리하는 꼴이 된다.

```java
 public class Order {
	private Orderer orderer;

	public void ShipTo(ShippingInfo newShippingInfo, boolean useNewShippingAddrAsMemberAddr) {
		verifyNotYetShipped();
		setShippingInfo(newShippingInfo);

		// Order 애그리거트에서 Member 애그리거트의 상태를 변경하고 있음.
		// Order와 Member간에 결합도가 생기게 되어 코드 유지보수 비용이 증가함.
		if (useNewShippingAddrAsMemberAddr) {
			orderer.getMember().changeAddress(newShippingInfo);
		}
	}
 }
 ```
 - 하나의 애그리거트가 다른 애그리거트의 상태를 변경하는 것은 두 애그리거트간에 결합도가 생기는 것을 의미한다.
 - 애그리거트간에 결합도가 생기면 향후 수정 비용이 증가하므로 애그리거트에서 다른 애그리거트의 상태를 변경하지 말아야 한다.

부득이하게 한 트랜잭션으로 두 개 이상의 애그리거트를 수정해야 한다면 Service에서 두 애그리거트를 수정하도록 구현한다.

```java
public class ChangeOrderService {

	@Transactional
	public void changeShippingInfo(OrderId id, ShippingInfo newShippingInfo, boolean useNewShippingAddrAsMemberAddr) {
		Order order = orderRepository.findById(id);
		if (order == null) {
			throw new OrderNotFoundException();
		}

		order.shipTo(newShippingInfo);

		if (useNewShippingAddrAsMemberAddr) {
			Member member = findMember(order.getOrderer());
			member.changeAddress(newShippingInfo);
		}
	}
}
```
