# Chapter 2 아키텍처 개요

# 2.1 네 개의 영역

![IMG_9056.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9056.heic)

![IMG_9024.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9024.heic)

| 영역 | 역할과 책임 |  |
| --- | --- | --- |
| Presentation | - 사용자의 요청을 검증한다.
- 사용자의 요청을 Application Layer 가 처리할 수 있는 객체로 변환해서 위임한다.
- Application Layer 에서 처리한 결과를 사용자가 요구한 형식에 맞게 변환해서 전송한다. | - Controller
- Response Object
- JSON 변환 |
  | Application | - 직접 처리하지 않고 도메인 로직을 통해 처리한다.
- 도메인 계층에 위임한다.(조합) | - Service |
  | Domain | - 사용자의 요청을 실제 처리
- 도메인 규칙이 구현되는 레이어 | - Domain Object |
  | Infrastructure | - 실제 구현기술을 통해 데이터를 관리한다.
- 외부 시스템, DB, 메시징… | - DAO, Repository |

![IMG_9057.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9057.heic)

```kotlin
class CancelOrderService {
	@Transactional
	fun cancelOrder(orderId: String) {
		val order = findOrderBy(orderId)
		cancel(order)
	}

	private fun findOrderBy(orderId: String): Order {
		val order = findOrderById(orderId)
		if (order.notExist())
			throw OrderNotFoundException(orderId)
		return order
	}

	private fun cancel(order: Order) = save(order.cancel())
}
```

![IMG_9058.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9058.heic)

- **Presentation / Application / Domain** Layer 에서는 구현 기술을 사용한 코드를 직접 만들지 않는다.
    - 구현 기술과 관련된 객체에 대한 의존성이 없다.

# 2.2 계층 구조 아키텍처

- 도메인의 복잡도에 따라서 계층구조를 분리하기도 하고 합치기도 한다.
- 상위 계층에서 하위 계층을 의존하고 하위 계층이 상위 계층을 의존하지 않는다.
- 구현의 편리함을 위해 구조를 유연하게 적용하기도 하지만 고민이 필요하다.
    - 상위 계층이 하위 계층에 의존한다는 것은 상위계층이 하위계층에 종속된다.

  ![IMG_9064.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9064.heic)

  **Infrastructure**

    ```kotlin
    class DroolsRuleEngine {
    	private val kContainer: KieContainer
    	
    	constructor() {
    		val ks = KieServices.Factory.get()
    		kContainer = ks.getKieClasspathContainer()
    	}
    
    	fun evaluate(sessionName: String, facts: List<?>) {
    		val kSession = kContainer.newKieSession(sessionName)
    		try {
    			facts.forEach { kSession.insert(it) }
    			kSession.fireAllRules()
    		} finally {
    			kSession.dispose()
    		}
    	}
    }
    ```

  **Application**

    ```kotlin
    class CalculateDiscountService {
    	private val ruleEngine: DroolsRuleEngine
    	
    	constructor() {
    		ruleEngine = DroolsRuleEngine()
    	}
    
    	fun calculateDiscount(orderLines: List<OrderLine>, customerId: String): Money {
    		val customer = findCustomer(customerId)
    		
    		val money = MutableMoney(0)
    		val facts = listOf(customer, money).apply {
    			addAll(orderLines)
    		}
    		ruleEngine.evalute("discountCalculation", facts)
    		return money.toImmutableMoney()
    	}
    }
    ```

    - 할인된 금액을 계산(`calculateDiscount()`)하는 역할과 책임을 테스트 하기 어렵다.
        - 할인된 금액을 계산하는 객체는 도메인 객체가 아닌 Infrastructure 객체이다.
            - 현재 구조에서는 mocking 어렵다.
    - 구현방식(**Infrastructure)** 변경이 어렵다.
        - `Drools` 방식이 아닌 다른 방식을 사용하도록 변경하려면 `CalculateDiscountService`(**Application)** 에 큰 변경이 일어난다.
        - `CalculateDiscountService`(**Application**) 이 변경되면 **Presentation** Layer 까지 영향을 받는다.

      ![IMG_9065.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9065.heic)


# 2.3 DIP

- 가격할인을 계산하려면 고객정보(`Customer`)와 할인정책(`DiscountRule`)이 필요하다.

  ![IMG_9121.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9121.heic)

  | 고수준 모듈 | 의미있는 단일 기능을 제공한다. | - 할인 금액을 구한다.
    - CalculatDiscountService |
      | --- | --- | --- |
      | 저수준 모듈 | 의미있는 단일 기능을 구현하기 위해 필요한 세부 기능을 구현한다. | - 고객의 정보를 조회한다.(CustomerRepository)
    - 룰(할인정책)을 적용한다.
      (DroolsRuleDiscounter) |
- 고수준 모듈이 저수준 모듈에 의존하면
    - 저수준 모듈이 변경될 때마다 고수준 모듈이 영향을 받는다.
    - 고수준 모듈 테스트시 구체화된 저수준 모듈을 주입해야 하므로 어렵다.
- **저수준 모듈이 고수준 모듈에 의존하도록 해야 한다. → DIP(D**ependency **I**njection **P**rinciple)
    - `CalculateDiscountService` 가 원하는 것은 할인금액이다.

      ![IMG_9123.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9123.heic)

        ```kotlin
        interface RuleDiscounter {
        	fun applyRules(customer: Customer, orderLines: List<OrderLine>): Money?
        }
        ```

        ```kotlin
        class CalculateDiscountService(
        	private val ruleDiscounter: RuleDiscounter
        ) {
        	// Rule 이 세부적으로 어떻게 구현되는지 정보 또는 지식이 없다 -> 세부구현에 의존하지 않음
        	fun calculateDiscount(
        		orderLines: List<OrderLine>, 
        		customerId: String
        	): Money 
        	= ruleDiscounter.applyRules(findCustomer(customerId), orderLines)
        }
        ```

        ```kotlin
        class DroolsRuleDiscounter(
        	private val kContainer: KieContainer
        ) : RuleDiscounter {
        	
        	init {
        		kContainer = KieServices.Factory.get().kieClasspathContainer
        	}
        
        	override fun applyRules(
        		customer: Customer, 
        		orderLines: List<OrderLine>
        	): Money? {
        		val kSession = kContainer.newKieSession("discountSession")
        		kSession.use { it.fireAllRules() }
        		return money.toImmutableMoney()
        	}
        }
        ```

    - 저수준 모듈(룰을 적용하여 할인 금액을 계산하는) 의 구현이 바뀌어도 영향을 받지 않는다.

        ```kotlin
        // 저수준 객체
        val ruleDiscounter = SimpleRuleDiscounter()
        // 저수준 객체를 주입
        val discountService = CalculateDiscountService(ruleDiscounter)
        ```

    - 테스트 하기 용이하다.

        ```kotlin
        @ExtendWith(MockKExtension::class)
        class CalculateDiscountServiceTest {	
        	@Test
        	fun noCustomer_thenExceptionShouldBeThrown() {
        		// 테스트를 위한 대역객체
        		val stubRepo = mockk<CustomerRepository>() {
        			every { findById("noCustId") } returns null
        		}
        		val stubRule = object: RuleDiscounter {
                override fun applyRules(
        					customer: Customer, 
        					orderLines: List<OrderLine>
        				): Money? = null
            }
        
        		// 대역객체 주입을 통한 테스트
        		val calDisSvc = CalculateDiscountService(stubRepo, stubRule)
        		assertThatThrownBy {
        			calDisSvc.calculateDiscount(someLines, "noCustId")
        		}
        	}
        }
        ```


### 2.3.1 DIP 주의사항

- DIP 는 **단순히 Strategy Pattern 이 아니다. →** 저수준 모듈이 고수준 모듈을 의존하도록 의존방향 역전
- **도메인 관점에서 고/저 수준을 판단**하고, 고수준 모듈을 도출한다.

  ![IMG_9124.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9124.heic)


### 2.3.2 DIP 와 아키텍처

- **도메인 관점에서** 인프라스트럭처 영역은 저수준 모듈이고, 응용영역과 도메인 영역은 고수준 모듈이다.

  ![IMG_9125.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9125.heic)

    - 추상화 대상이 모호하다면, 무조건 DIP 를 적용하지 않고, 적용범위를 검토 후 적용하자

## 2.4 도메인 영역의 주요 구성요소

![IMG_9126.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9126.heic)

### 2.4.1 엔티티와 밸류

- 도메인 모델의 **ENTITY** 와 DB 테이블의 **ENTITY** 는 다르다.
    - DB 테이블의 ENTITY 는 데이터만 제공한다.

      ![IMG_9127.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9127.heic)

    - 도메인 모델의 엔티티는 데이터와 기능을 제공한다.

        ```kotlin
        class Order(
        	private val number: OrderNo,
        	private val orderer: Orderer,
        	private val shipping: Shipping
        ) {
        	fun changeShipping(newShipping: Shipping) {
        		// 도메인 규칙 및 기능구현
        	}
        }
        ```

        - 두 개 이상의 데이터가 하나의 개념을 표현한다면 **VALUE** 타입을 이용할 수 있다.

            ```kotlin
            data class Orderer(
            	private val name: String, 
            	private val email: String
            )
            ```

- **VALUE** 는 가급적 **불변**으로 구현하는 것이 좋다.
    - **VALUE** 의 데이터를 변경할 때에는, 변경할 데이터를 갖는 새로운 객체를 생성한다.

### 2.4.2 애그리거트

- **AGGREGATE** 는
    - 연관된 **ENTITY** 와 **VALUE** 를 개념적으로 하나로 묶은 것이다.

      ![IMG_9129.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9129.heic)

    - 반드시 ROOT **ENTITY** 를 갖는다.
        - ROOT **ENTITY** 는
            - **ENTITY**, **VALUE** 를 이용하여 외부에 기능을 제공한다.
            - 내부 구현을 숨기고, 외부의 변경으로부터 내부 **ENTITY**, **VALUE** 를 보호한다.

              ![IMG_9130.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9130.heic)

- **ENTITY** 와 **VALUE** 가 많아질수록 도메인 모델은 점점 더 복잡해진다.

  ![IMG_9128 2.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9128_2.heic)

    - 도메인 모델이 복잡해지면
        - 전체 설계를 보기 어렵다. → 전체를 보지 못하고 개별적인 세부 도메인에 집중하게 된다.
- **AGGREGATE** 를 사용하면
    - 개별 **ENTITY**, **VALUE** 가 아닌 **AGGREGATE** 단위로 도메인 모델을 이해할 수 있다.
- **AGGREGATE** 를 사용할 때에는
    - **구성**에 따라 더복잡해지기도 한다.
    - **트랜잭션 범위**
    - **구현제약**

### 2.4.3 리포지터리

- 도메인 객체를 보관 및 관리하기 위한 도메인 모델
- **AGGREGATE** 단위로 도메인 객체를 **저장(필수)** / **삭제** / **수정** / **조회(필수)** 한다.

    ```kotlin
    interface OrderRepository {
    	fun findBy(number: OrderNumber): Order
    	fun save(order: Order)
    	fun delete(order: Order)
    }
    ```

- 도메인 모델 관점에서 **REPOSITORY** 는 도메인 객체를 영속화 하는데 필요한 기능을 추상하였으므로 고수준 모듈이다.

  ![IMG_9131.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9131.heic)

    - **APPLICATION** 은 도메인 객체를 조회 / 저장 할 때 **REPOSITORY** 를 사용한다.

        ```kotlin
        interface SomeRepository {
        	fun save(some: Some)
        	fun findBy(id: SomeId)
        }
        ```

        - 필요에 따라서 삭제(`delete(id)`) / 갯수 조회(`counts()`) 등의 메소드를 제공한다.
    - **APPLICATION** 은 트랜잭션을 관리하는데, 트랜잭션 처리는 **REPOSITORY** 구현 기술의 영향을 받는다.

## 2.5 요청 처리 흐름

| Layer | Role |
| --- | --- |
| Presentation Layer | - 사용자의 요청을 검증한다.
- 사용자의 요청을 처리하기 위해 기능실행을 Application Layer 에게 위임한다.
- 사용자가 요구하는 데이터 형식으로 응답한다. |
  | Application Layer | - Domain Model 을 이용해서 기능을 실행한다.(위임) |
  | Infrastructure Layer | - Presentation / Application / Domain layer 지원
- Domain Model 의 영속성 관리
  ㄴ SMTP / Rest API / Transaction / Framework 구현기술에 대한 영역 |

![IMG_9169.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9169.heic)

```kotlin
class CancelOrderService(private val orderRepository: OrderRepository) {
	@Transactional
	fun cancel(number: OrderNumber) {
		val order = orderRepository.findByNumber(number)
		order?.let { it.cancel() } ?: throw NoOrderException(number)
	}
}
```

## 2.6 인프라스트럭처 개요

- 구현의 편리함은 **DIP**(변경의 유연함 / 테스트 쉬움) 만큼 중요하다.
- DIP 의 장점을 해치지 않는 선에서 **Application** / **Domain** Layer 에서 구현 기술에 대한 의존성을 가져가는 것은 나쁘지 않다고 저자는 생각한다.

## 2.7 모듈 구성

- 도메인이 작은 경우 영역별로 분리한다.

  ![IMG_9187.heic](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9187.heic)

- 도메인이 크다면 하위 도메인 별로 분리한다.

  ![IMG_9188.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9188.heic)

- 하위 도메인이 크다면 애그리거트를 기준으로 분리한다.

  ![IMG_9189.HEIC](Chapter%202%20%E1%84%8B%E1%85%A1%E1%84%8F%E1%85%B5%E1%84%90%E1%85%A6%E1%86%A8%E1%84%8E%E1%85%A5%20%E1%84%80%E1%85%A2%E1%84%8B%E1%85%AD%207b51b8c2b5b34b02982ddd800eeead43/IMG_9189.heic)

- 단, 애그리거트, 모델, 리포지터리는 같은 패키지에 존재한다.
- 한 패키지에 코드를 찾기 불편할 정도로 많은 타입이 몰려있지 않도록 관리해야 한다.
