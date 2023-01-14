# Chapter 1 도메인 모델 시작하기

# 1.1 도메인이란?

- 소프트웨어로 해결하고자 하는 문제영역
- 도메인은 한 개 이상의 하위 도메인으로 구성된다.
    - 하위 도메인들은 서로 **연동**하여(협력) 상위 도메인의 **완전한 기능(역할과 책임)을 제공**한다.
    - 도메인의 일부 기능들은 직접 구현하지 않고 외부와 연동하여 기능을 제공할 수 있다. → 상황에 따라 달라짐

      ![IMG_9015.heic](./images/IMG_9015.jpeg)
      [출처: 도메인 주도개발 시작하기 / 최범균]

| 온라인 서점 | 도서 조회 |  | - 회원은 판매중인 도서를 조회할 수 있다. |
| --- | --- | --- | --- |
|  | 도서 구매 | 결제 | - 회원은 도서를 구매하기 위해 금액을 지불합니다. |
|  |  | |- 카드결제 / 계좌이체 / 상품권 |
|  |  | 혜택 | - 할인쿠폰 / 포인트 적립 |
|  | 도서 배송 | 조회 | - 회원은 결제가 완료된 도서의 배송상태를 조회합니다. |
|  |  | 배송 | - 결제가 완료된 도서를 배송지까지 배송합니다. |
|  |  | 반품 | - 회원은 결제가 완료된 도서에 대해서 수령 후 환불하기 위해 반품할 수 있습니다.  |
|  | 도서 리뷰 | 작성 | - 회원은 자신이 구매하고, 수령한 도서에 대해서 리뷰를 작성할 수 있습니다. |
|  |  | 수정 | - 회원은 자신이 구매하고, 수령한 도서에 대해서 리뷰를 수정할 수 있습니다. |
|  |  | 조회 | - 모든 회원은 도서 리뷰를 조회할 수 있습니다. |

# 1.2 도메인 전문가와 개발자 간 지식공유

## 요구사항을 올바르고 정확하게 이해하는 것이 중요하다.

- 잘못 개발한 코드는 수정해서 고치기 쉽지 않다.
- 요구사항을 제대로 이해하지 못하면 유용하지 못하거나 유연하지 못한 결과물을 만들기 쉽다.

## 도메인 전문가

- 해당 도메인에 대한 지식과 경험을 바탕으로 원하는 기능개발을 요구한다.

## 개발자

- 도메인 전문가의 요구사항을 분석하고, 설계하여, 코드를 작성하고 테스트하고 배포한다.
- 도메인 전문가 만큼의 해당 도메인에 대한 지식을 갖추어야 한다. → **요구사항에 대한 올바른 이해**
    - 도메인 전문가 / 이해관계자와 대화를 통해 요구사항을 정확하게 이해하는 노력을 기울여야 한다.

# 1.3 도메인 모델

- **도메인 모델**은 특정 도메인을 개념적으로 표현한 것이다.
    - 도메인 자체를 이해하기 위한 개념모델이다. → 바로 코드 작성은 어렵다.
    - 도메인을 이해하려면 도메인이 제공하는 기능(역할과 책임)과 도메인의 주요 데이터를 파악해야 한다.
- 도메인 모델을 사용하면 여러 관계자들이 **동일한 모습으로 도메인을 이해**하고 도메인 지식을 공유하는데 도움이 된다. → 이해도 통일
    - 도메인을 이해하는데 도움이 된다면 다이어그램도 좋고, 다양한 방법을 사용해도 좋다.
    - 단, 전체 도메인을 하나의 구조로 표현하는 것은 좋지 않다.
        - 이해하기 어렵다. → 도메인 용어의 충돌 가능성, 가독성

## **주문 도메인**

- **회원(Orderer)**은 **상품(Product)**의 가격(**price**)을 확인하고 몇개 살지(**quantity**) 입력하고 **지불방법(PaymentInfo)** 을 선택한다.
    - **주문정보(OrderLine)**
        - 상품의 가격(**price**) / 몇개 살지(**quantity**)
        - 총 지불금액(**amounts()**) 을 조회한다.
    - **회원(Orderer)**
        - 상품을 주문한다.
    - **상품(Product)**
        - 가격(**price**)
        - 가격을 조회한다.(**price()**)
- 주문한 뒤에도 **배송(ShippingInfo)** 전이면 배송지 주소(**address**)를 변경하거나 **주문(Order)**을 취소할 수 있다. → 도메인 규칙
    - **배송(ShippingInfo)**
        - 배송지 주소(**address**)
        - 배송 전이면 배송지 주소를 변경할 수 있다.(**change(address)**)
    - **주문(Order)**
        - 주문번호(**orderNumber**) / 총 결제금액(**totalAmounts**)
        - 주문한다. (**order()**)
        - 주문을 취소한다. (**cancel()**)

**예시) 주문 도메인 객체모델**

![IMG_9016.HEIC](./images/IMG_9016.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]

**예시) 주문 상태 다이어그램**

![IMG_9017.HEIC](./images/IMG_9017.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]

# 1.4 도메인 모델 패턴

![IMG_9024.HEIC](./images/IMG_9024.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]

| 영역 | 설명 |
| --- | --- |
| Presentation | - 사용자의 요청을 검증한다.
- 사용자의 요청을 처리한다. |
| Application | - 도메인 계층에 위임한다.(조합) |
| Domain | - 사용자의 요청을 실제 처리
- 도메인 규칙이 구현되는 레이어 |
| Infrastructure | - 데이터를 관리한다.
- 외부 시스템, DB, 메시징… |

- 주문상태(**OrderState**) 도메인 규칙
    - 주문 대기 중(**PAYMENT_WAITING**) 이거나 상품 준비중(**PREPARING**) 에만 배송지를 변경할 수 있다.
    - 주문 대기 중(**PAYMENT_WAITING**) 이거나 상품 준비중(**PREPARING**) 이 아니라면 배송지를 변경할 수 없다.

  **주문 가능여부를 OrderState 로 분리**

    ```kotlin
    class Order(private val state: OrderState, private var shipping: Shipping) {
    	fun changeShipping(shipping: Shipping) {
    		if (!state.isChangeable()) {
    			throw IllegalStateException("Can't change shipping in $state")
    		}
    		this.shipping = shipping
    	}
    }
    
    enum class OrderState {
        PAYMENT_WAITING {
            override fun isShippingChangeable(): Boolean = true
        },
        PREPARING {
            override fun isShippingChangeable(): Boolean = true
        },
        SHIPPED, DELIVERING, DELIVERY_COMPLETED;
        open fun isShippingChangeable(): Boolean = false
    }
    ```

    - 배송상태를 변경 가능한지 판단하기 위해서 **OrderState** 로 충분하다면 괜찮을 것 같다. ****
        - 배송 상태가 지금보다 더 많아지고, 배송 가능한 상태가 더 많이 추가된 이후에 요구사항 변경으로 **OrderState** 외에 다른 외부정보가 필요하다면 변경이 쉽지 않을 것 같다.
    - 배송가능여부에 대한 단위테스트가 용이해보인다.

**주문 가능 여부를 Order 에서 처리**

```kotlin
class Order(private val state: OrderState, private var shipping: Shipping) {
	fun changeShipping(shipping: Shipping) {
		if (!isShippingChangeable()) {
			throw IllegalStateException("Can't change shipping in $state")
		}
		this.shipping = shipping
	}
	
	private fun isShippingChangeable(): Boolean = 
		state == PAYMENT_WAITING || state == PREPARING
}
```

- 배송상태를 변경가능한지 판단하기 위해서 **OrderState** 외에 다른 정보가 필요하다면 **Order** 의 역할과 책임으로 가져가는 것이 좋을 것 같다.
    - 배송 상태를 변경 가능한지 판단하기 위해서 수많은 정보가 필요하고 판단하는 기준(도메인 규칙)이 복잡해진다면 Order 에 모두 구현하는 것이 좋진 않을 것 같다. → 도메인 구조를 세분화 해보면 좋을 것 같다.
    - 배송상태 변경가능여부 단위테스트가 상대적으로 복잡할 것 같다.

# 1.5 도메인 모델 도출

- **도메인에 대한 이해 없이 코드를 작성할 수 없다.**
- 이해관계자와의 대화를 통해 도메인을 점진적으로 이해하고, 구체화 할 수 있다.
    - 요구사항으로부터 핵심 구성요소 / 도메인 규칙 / 기능을 파악한다.
- 도메인에 대한 이해도는 관계자와의 대화를 통해 요구사항을 분석하면서 점진적으로 도메인에 대해서 이해할 수 있다. → 점진적으로 이해도가 높아진다.

### **주문(Order) 요구사항**

- 최소 한종류 이상의 **상품(Product)**을 **주문(Order)**해야 한다.
    - 주문 : 상품종류 = 1:N
- 한 **상품(Product)**을 한 개 이상 **주문(Order)**할 수 있다.
    - 주문 : 상품 = 1:N
    - 주문 가능한 최대 상품 종류의 갯수제한은 있는가?
- 총 **주문 금액(totalPrice)**은 각 **상품(Product)**의 구매가격(price) 합을 모두 더한 금액이다.
    - 주문 금액에 배송비는 포함되지 않는가?
        - 배송비는 언제 포함되며, 배송비가 포함되는 가격은 무엇인가?
        - 배송비는 상품과 상관없이 모두 항상 무료인가?
    - 주문금액(OrderPrice) = **주문한 종류 별 상품(Product) 의 구매가격**
        - 개당 1,000 원인 신발 10개와 개당 100원인 펜을 5개 주문하면 주문금액은 10,500 원이다.
            - 1000 * 10 + 100 * 5 = 10500
        - 상품의 구매가격은 0원일 수 있는가?
    - 주문 전 상품과 주문된 상품은 같은가?
        - 주문 전 상품의 수량은 판매 가능한 수량이고, 주문한 상품의 수량은 고객이 주문한 상품의 수량이다.
        - 판매 가능한 상품의 수량 ≥ 고객이 주문한 상품의 수량
    - 결제의 역할과 책임은 누구인가?
        - 주문? → 결제에 필요한 정보는 오직 총 주문금액 뿐이다 → 주문의 역할과 책임은 아닌것 같다.
        - 결제? → 총 주문금액만 알면 되고, 결제가 완료되면 주문의 상태는 출고 상태로 바뀐다.
- **각 상품의 구매 가격(price)** 합은 상품 가격에 구매 갯수를 곱한 값이다. (**도메인 규칙**)
- **주문(Order)**할 때 **배송지(Shipping)**를 반드시 지정해야 한다.(**도메인 규칙**)
- **배송지(Shipping)**는 **받는 사람 이름(name)**, **전화번호(phoneNumber)**, **주소(address)**로 구성된다.
    - 필수 정보와 아닌 정보는 무엇인가?
    - 각각의 정보는 변경될 수 있는가?
    - 각각의 정보는 항상 오직 한개씩만 존재하는가?
        - **배송지(Shipping)** : **받는 사람 이름(name)** : **전화번호(phoneNumber)** : **주소(address) =** 1:1:1:1
- **출고(Shipped)**를 하면 **배송지(Shipping)**를 변경(change())할 수 없다.(**도메인 규칙**)
- **출고 전(PREPARING)** 에 **주문(Order)**을 취소(cancel())할 수 있다. **(도메인 규칙)**
    - 출고 전에 주문을 취소 가능한 대상은 누구인가?
        - 구매고객, 관리자, 판매자…
            - 만약, 예외적인 상황(품절된 상품 주문 / 물량확보실패)으로 주문된 **상품(Product)**에 대해서 판매자가 **주문(Order)**을 취소할 필요는 없을까?
- **고객(Customer)**이 결제(pay())를 완료하기 전에는 **상품(Product)**을 준비(PREPARING)하지 않는다.(**도메인 규칙**)
    - 고객이 결제를 하지 않는다면, 주문 상태는 무엇인가?
        - **결제대기(PAYMENT_WATING)**
    - 고객이 결제를 완료하면 주문상태는 무엇인가?
        - **준비중(PREPARING)**
    - 주문은 결제가 완료된 상품에 대해서 가능한 것인가?
        - 아니오
    - 주문이 갖는 상태는 어떤 것들이 있는가?
        - **결제대기(PAYMENT_WATING) → 준비중(PREPARING) → 출고(SHIPPED) → 배송중(DELIVERING) → 배송완료(DELIVERY_COMPLETED)**
        - 주문 상태는 순서가 있다. (도메인 규칙)
            - 결제대기(PAYMENT_WATING) 에서 바로 배송완료(DELIVERY_COMPLETED) 로 변경될 수 없다.
        - 결제가 가능한 주문상태는 결제대기이다.
            - 준비중 / 출고 / 배송중 / 배송완료는 결제 불가

| 도메인 | 설명 | 규칙 | 데이터 | 역할 |
| --- | --- | --- | --- | --- |
| 주문(Order) | - 반드시 한개 이상의 상품을 주문한다.
- 배송지 / 주문상태 / 구매가격
  | - 출고 전이면 주문을 취소할 수 있다.
- 구매가격은 구매한 종류별 상품의 가격 * 수량의 합이다.
- 결제가 가능한 주문상태는 오직 결제대기 이다. | - 배송지
- 주문상태
- 주문한 상품 | - 취소하다.
  (고객 → 주문)
- 결제완료하다.
  (고객 → 주문)
- 배송지를 변경하다.
  (고객 → 주문) |
  | 주문한 상품(OrderedProduct) | - 판매 가능한 상품을 고객이 주문한다.
  | - 주문된 상품의 수량은 반드시 1개 이상이다. | - 판매가격
- 구매수량 | - 구매 가격을 계산한다.
  (주문 → 주문한 상품) |
  | 배송지(Shipping) |  | - 출고되면 배송지 변경할 수 없다. | - 받는 사람 이름
- 전화번호
- 주소 | - 변경하다.  |
  | 주문상태(OrderState) |  | - 결제대기 → 준비중 → 배송준비완료 → 출고 → 배송완료 |  |  |
  | 고객(Customer) |  |  | ** 고객이 아닌 다른 사람에게 배송할 수 있으므로 도메인의 필수 데이터 인지 판단하기 아직 어렵다. ** | - 주문을 결제한다. |
  | 결제(Payment) | - 고객의 주문에 대해서 총 주문금액을 결제한다. | - 결제가 완료되면 주문상태는 출고 상태가 된다. | - 결제금액 | - 총 주문금액을 결제한다.
  (고객 → 결제)
  |

```kotlin
class Order(
	private var shipping: Shipping,
	private val state: OrderState,
	private val products: List<OrderedProduct>
) {
	fun change(shipping: Shipping) {
		if (!state.isChangeable()) {
			// TODO 배송지 변경불가
		}
		this.shipping = shipping
	}

  fun cancel() {
		if (!state.isCancelable()) {
			// TODO 주문 취소 불가
		}
		// TODO 주문 취소 처리
	}

  fun paid() {
		if (!state.isPayable()) {
			// TODO 결제 불가
		}
		this.state = PREPARING
	}
}
```

```kotlin
class OrderedProduct(private val price: Int, private val amount: Int) {
	fun calculatePurchasePrice(): Int = price * amount
}
```

```kotlin
data class Shipping(
	val name: String, 
	val phoneNumber: String, 
	val address: String
)
```

```kotlin
enum class OrderState {
	PAYMENT_WATING {
		override fun isChangeable(): Boolean = true
		override fun isCancelable(): Boolean = true
		override fun isPayable(): Boolean = true
	}, 
	
	PREPARING {
		override fun isChangeable(): Boolean = true
		override fun isCancelable(): Boolean = true
	}, 

	SHIPPED, DELIVERING, DERIVERY_COMPLETED;

  open fun isChangeable(): Boolean = false
	open fun isCancelable(): Boolean = false
	open fun isPayable(): Boolean = false
}
```

```kotlin
class Payment {
	fun pay(order: Order) {
		// TODO 실제 결제처리(외부연동)
		order.paid()
	}
}
```

### 문서화의 목적

- 지식을 공유하기 위해 문서화 한다. (도메인 지식 / 시스템 지식 / 서비스 운영 지식…)
    - 코드로 지식을 습득하는 것은 상세하게 이해할 수 있는 장점이 있지만 시간이 오래 걸린다. → 비효율적
    - 코드를 통해서만 도메인 지식을 습득하는 것이 반복되면 자칫 도메인 지식(설계)와 코드가 불일치하는 문제가 발생할 수 있다.
        - 이해관계자와 소통의 어려움
        - 코드 가독성 저하
        - 이해관계자 간의 서로 다른 이해도
            - 기획은 코끼리, 개발은 뱀을 이해
- 전반적인 이해를 위한 목적으로는 문서를 참조하는 것이 효율적이고, 상세한 내용은 코드를 통해 이해하는 것이 더 깊이 이해할 수 있다.
    - 코드를 통해 이해하더라도 코드 한줄 한줄 읽는 것보다 단위 / 통합테스트를 통해 이해도를 가져가는 것이 좀 더 효율적이다.

# 1.6 엔티티와 밸류

- 도메인 모델은 크게 엔티티(**ENTITY**) 와 밸류(**VALUE**) 로 구분할 수 있다.
- 엔티티(**ENTITY**)와 밸류(**VALUE**) 는 반드시 잘 구분해야 한다.

  ![IMG_9049.HEIC](./images/IMG_9049.jpeg)
  [출처: 도메인 주도개발 시작하기 / 최범균]

## 1.6.1 엔티티(ENTITY)

- 엔티티는 반드시 식별자를 가진다.
- 엔티티의 식별자는 라이프 사이클과 상관없이 항상 동일하게 유지된다.
- 두 엔티티의 식별자가 같으면 속성이 달라도 동일한 엔티티이다.

    ```kotlin
    class Order(private val orderNumber: String) {
    	override fun equals(other: Any?): Boolean {
    		if (other == null) return false
    		if (javaClass != other?.javaClass) return false
    		
    		other as Order
    		if (orderNumber != other.orderNumber) return false
    		return true
    	}
    
    	override fun hashCode(): Int = orderNumber.hashCode()
    }
    ```


## 1.6.2 엔티티의 식별자 생성

- 엔티티의 식별자를 생성하는 시점은 도메인의 특징과 사용하는 기술에 따라 달라진다.


    | 방법 | 예시 | 주의사항 |
    | --- | --- | --- |
    | 특정 규칙에 따라 생성 | - 주문번호
    (2022121328371876) | - 날짜와 시간을 이용하면 중복이 생길 수 있다. |
    | 고유 식별자 생성기 사용 | - java.util.UUID 
    - Nano ID | - 고유 식별자 생성기는 중복이 거의 없지만 엔티티 갯수에 따라서 중복이 생길 가능성도 있으므로 사전 검토가 필요하다. |
    | 값을 직접 입력 | - 회원 아이디 | - 이미 사용중인 값을 입력할 수 있다. |
    | 일련번호 사용(DB) | - 게시글 번호 | - 엔티티를 생성하는 시점이 아닌 생성한 이후 엔티티를 저장한 이후에 식별자를 조회할 수 있는 기술도 있다. |

## 1.6.3 밸류 타입

- 밸류(**VALUE**) 타입은 개념적으로 완전한 하나를 표현할 때 사용한다.
- 밸류(**VALUE**) 타입은 식별자를 갖지 않는다.
    - 모든 속성이 같다면 같은 밸류로 판단한다.

    ```kotlin
    data class Shopping(
    	// 받는 사람
    	private val receiverName: String,
    	private val receiverPhoneNumber: String,
    	// 받는 주소
    	private val shippingAddress1: String,
    	private val shippingAddress2: String,
    	private val shippingZipcode: String,
    )
    ```

- 받는 사람과 주소는 동일한 의미를 갖는다.

    ```kotlin
    data class Shopping(
    	private val receiver: Receiver
    	private val address: Address
    )
    
    data class Receiver(
    	private val name: String,
    	private val phoneNumber: String
    )
    
    data class Address(
    	private val address1: String,
    	private val address2: String,
    	private val zipcode: String
    )
    ```

- 밸류(**VALUE**) 타입은 반드시 두 개 이상의 데이터를 갖지 않는다.
    - 한개 이상이면 가능하다.
    - **가독성이 높아진다.**

    ```kotlin
    data class OrderLine(
    	private val product: Product,
    	// 제품가격
    	private val price: Int,
    	private val quantity: Int,
    	// 총 주문금액
    	private val amount: Int
    )
    ```

    - 의미를 부여하여 가독성을 높이기 위해 밸류(**VALUE**) 타입을 만들 수 있다.

    ```kotlin
    data class OrderLine(
    	private val product: Product,
    	private val price: Money,
    	private val quantity: Int,
    	private val amount: Money
    )
    
    data class Money(private val value: Int)
    ```

- 밸류(**VALUE**) 타입은 밸류 타입을 위한 기능을 추가할 수 있다.

    ```kotlin
    data class Money(private val value: Int) {
    	operator fun plus(other: Money): Money = Money(value + other.value)
    	operator fun times(other: Int): Money = Money(value * other.value)
    }
    ```

    ```kotlin
    data class OrderLine(
    	private val product: Product,
    	private val price: Money,
    	private val quantity: Int,
    	private val amount: Money
    ) {
    	private fun calculateAmnounts(): Money = price * amount
    }
    ```

- 밸류(**VALUE**) 객체는 불변(**Immutable**)객체로 만드는 것이 바람직하다. → 안전한 코드(참조 투명성, 쓰레드 안전)

    ```kotlin
    data class Money(private val value: Int) {
    	// 현재 객체의 value 를 변경하지 않고, 변경된 값을 갖는 새로운 Money 객체를 생성하여 리턴
    	operator fun plus(other: Money): Money = Money(value + other.value)
    	operator fun times(other: Int): Money = Money(value * other.value)
    }
    ```

    - 참조 투명성이 깨지면 런타임에 문제가 발생할 수 있다.

    ```kotlin
    val price = Money(1000)
    // [price=1000, quantity=2, amounts=2000]
    val line = OrderLine(product, price, 2) 
    // [price=2000, quantity=2, amounts=2000] -> amounts 가 잘못된 데이터가 된다.
    price.value = 2000
    ```

    ```kotlin
    data class Money(var value: Int)
    ```


## 1.6.4 엔티티 식별자와 밸류타입

- 엔티티(**ENTITY**)의 식별자가 특별한 의미를 지닌다면, 밸류(**VALUE**) 타입을 사용한다.

```kotlin
// 주문번호라는 의미와 주문번호가 식별자라는 의미도 함께 드러난다.
class Order(private val id: OrderNumber) {
	override fun equals(other: Any?): Boolean {
		if (other == null) return false
		if (javaClass != other?.javaClass) return false
		
		other as Order
		return orderNumber == other.orderNumber
	}

	override fun hashCode(): Int = orderNumber.hashCode()
}
```

```kotlin
data class OrderNumber(private val number: String)
```

## 1.6.5 도메인 모델에 set 메서드 넣지 않기

- set 메서드는 도메인의 핵심 개념이나 의도를 코드에서 사라지게 한다.

    ```java
    public class Order {
    	public void setShipping(Shipping newShipping) {
    		this.shipping = newShipping;
    	}
    
    	public void setOrderState(OrderState state) {
    		this.state = state;
    	}
    }
    ```

- 도메인 객체를 생성할 때 온전하지 않은 상태로 생성될 수 있다.

    ```kotlin
    val order = Order()
    order.orderLine = lines
    order.shipping = shipping
    // 주문자가 없는데 주문상태는 배송준비중이 될 수 있다.
    order.state = OrderState.PREPARING
    ```

- 도메인 객체가 항상 온전한 상태로 생성되고 사용되도록 하려면 생성자를 통해 모든 것을 전달해주어야한다.

    ```kotlin
    class Order {
    	private val orderer: Orderer,
    	private val orderLines: List<OrderLine>,
    	private val shipping: Shipping,
    	private val state: OrderState,
    	private val totalAmounts: Money
    
    	constructor(
    		orderer: Orderer?,
    		orderLines: List<OrderLine>?,
    		shipping: Shipping,
    		state: OrderState
    	) {
    		setOrderer(orderer)
    		setOrderLines(orderLines)
    		...
    	}
    	// 외부에서 변경할 수 없다.
    	private fun setOrderer(orderer: Orderer?) {
    		orderer?.let { 
    			this.orderer = it 
    		} ?: throw IllegalArgumentException("no orderer")
    	}
    
    	private fun setOrderLines(orderLines: List<OrderLine>?) {
    		verifyAtLeastOneOrMoreOrderLines(orderLines)
    		this.orderLines = orderLines!!
    		calculateTotalAmounts()
    	}
    
    	private fun verifyAtLeastOneOrMoreOrderLines(orderLines: List<OrderLine>?){
    		if (orderLines.isNullOrEmpty()) 
    			throw IllegalArgumentException("no orderLine")
    	}
    
    	private fun calculateTotalAmounts() {
    		this.totalAmounts = orderLines.sumOf { it.amounts }
    	}
    }
    ```


# 1.7 도메인 용어와 유비쿼터스 언어

- 도메인에서 사용하는 용어를 **코드에 반영하지 않으면 코드의 의미를 개발자가 해석해야 한다.**

    ```kotlin
    enum class OrderState {
    	// 결제대기중, 상품준비중, 출고완료, 배송중, 배송완료, 주문취소
    	STEP1, STEP2, STEP3, STEP4, STEP5, STEP6;
    }
    ```

    ```kotlin
    class Order(
    	private val orderer: Orderer,
    	private val orderLines: List<OrderLine>,
    	private val shipping: Shipping,
    	private val state: OrderState,
    	private val totalAmounts: Money
    ) {
    	fun changeShipping(newShipping: Shipping) {
    		verifyStep1OrStep2()
    		setShipping(newShipping)
    	}
    
    	private fun verifyStep1OrStep2() {
    		// 출고완료전의 상태인지 검증하는 코드를 이해하기 어렵다.
    		if (state != OrderState.STEP1 && state != OrderState.STEP2) {
    			throw IllegalStateException("already shipped")
    		}
    	}
    }
    ```

    - 위의 코드를 이해하려면
        - STEP1 == 결제 대기 중, STEP2 == 상품 준비 중 이라는 지식을 사전에 알아야 한다.
        - 출고 전 == STEP1 또는 STEP2 라는 지식을 사전에 알아야 한다.

    ```kotlin
    enum class OrderState {
    	PAYMENT_WATING, PREPARING, SHIPPED, DELIVERING, DERIVERY_COMPLETED;
    	
    	companion object {
    		fun isBeforeShipped(state: OrderState) = 
    			state == PAYMENT_WATING || state == PREPARING
    	}
    }
    ```

    ```kotlin
    class Order(
    	private val orderer: Orderer,
    	private val orderLines: List<OrderLine>,
    	private val shipping: Shipping,
    	private val state: OrderState,
    	private val totalAmounts: Money
    ) {
    	fun changeShipping(newShipping: Shipping) {
    		verifyBeforeShipped()
    		setShipping(newShipping)
    	}
    
    	private fun verifyBeforeShipped() {
    		if (!OrderState.isBeforeShipped(state)) {
    			throw IllegalStateException("already shipped")
    		}
    	}
    }
    ```

    - 위의 코드는 이해하는데 필요한 사전지식이 없다.
        - 코드를 도메인 용어로 해석하거나 도메인 용어를 코드로 해석하는 과정이 없다. → 도메인 설계와 코드 구현의 일치
        - 가독성이 높아지고 이해하는 시간이 줄어든다.
        - 도메인 규칙이 코드로 이어지므로 버그가 발생할 가능성도 줄어든다.
- **유비쿼터스 언어(Ubiquitous Language)**
    - 도메인지식 / 문서 / 코드 / 테스트 모두 같은 언어를 사용한다.
    - 새로운 도메인을 찾아내면 관련 용어를 통일하고 같이 사용한다.
