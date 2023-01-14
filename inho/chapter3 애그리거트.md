# Chapter 3 애그리거트

![IMG_9016.HEIC](./images/IMG_9016.jpeg)

# 3.1 애그리거트

![IMG_9190.heic](./images/IMG_9190.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]
- 복잡도가 낮은 도메인 설계는 전반적인 관계를 이해하는 데 도움이 된다.

![IMG_9191.HEIC](./images/IMG_9191.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]
- 세부적인 객체 설계에서 전반적인 설계를 이해하려고 하면 복잡도가 높아져서 관계를 파악하기 어렵다.
    - 코드를 변경하거나 확장하는 것이 어렵다.
    - 변경 또는 확장할 작업에 대해 사이드 이펙트를 사전에 파악하기 어렵다.

![IMG_9192.HEIC](./images/IMG_9192.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]
- 애그리거트를 통해 비슷한 개념들끼리 묶으면 복잡도가 낮아져서
    - 전반적인 설계를 이해하기 쉽다.
    - 변경하거나 확장하는 작업과 사이드 이펙트를 파악하기 쉽다.
- 하나의 애그리거트 안에 있는 객체들은
    - 유사하거나 **동일한 라이프 사이클을 갖는다.**
    - **다른 애그리거트에 속하지 않는다.**
    - **동일한 도메인 규칙**을 따른다.

      ![IMG_9193.heic](./images/IMG_9193.jpeg)
       [출처: 도메인 주도개발 시작하기 / 최범균]
- 도메인에 대한 이해도가 높아질수록 애그리거트의 크기는 줄어든다 → 애그리거트가 늘어난다?
    - 애그리거트 = 1 엔티티 + N 밸류

## 3.2 애그리거트 루트

![IMG_9244.heic](./images/IMG_9244.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]
![IMG_9016.HEIC](./images/IMG_9016.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]

- 회원은 주문을 생성하고(주문하다), 주소 및 주문상태를 변경(결제완료 상태) 한다.
    - 회원은 주문 애그리거트 루트(Order) 를 통해 주문정보를 변경한다. (구매할 상품 갯수(`quantity`) / 배송지(`Shipping`))
        - 주문 애그리거트 루트(Order)는 주문정보 변경을 수행하기 위해 **ENTITY** 와 협력하고 도메인 규칙을 관리한다.
            - 구매할 상품 갯수(`quantity`) 를 변경하고, 총 주문금액(`totalAmounts`)를 함께 변경한다.
- 애그리거트는 여러 객체(ENTITY, VALUE) 로 [구성되어 협력한다.](https://www.notion.so/Chapter-2-7b51b8c2b5b34b02982ddd800eeead43)
- **애그리거트 루트**는
    - **일관된 상태를 유지**하는 책임을 갖는다. (**도메인 규칙** / **도메인의 항상성** / **애그리거트 일관성**)

        ```kotlin
        class Order {
        	// 애그리거트가 외부에 제공해야 하는 기능을 메소드로 구현
        	fun changeShipping(newShipping: Shipping) {
        		verifyNotYetShipped()
        		setShipping(newShipping)
        	}
        }
        ```

    - 애그리거트가 제공해야 하는 기능을 애그리거트에 속한 객체들(ENTITY / VALUE) 과 **협력을 통해 외부에 제공한다.**
- 애그리거트 안에 속한 모든 엔티티는 애그리거트 루트 엔티티와 협력한다.
- **애그리거트 루트가 아닌 다른 객체가 애그리거트에 속한 객체를 직접 변경하면 안된다.**

    ```kotlin
    val shipping = order.shipping
    // 주문상태가 배송전인 경우 까지만 주소변경이 가능한 도메인 규칙이 깨진다. -> 도메인 일관성 깨짐
    // 만약, 도메인 규칙이 Shipping(VALUE) 에 구현 된다면?
    // ENTITY 와 VALUE 의 경계가 무너지고, 동일한 도메인 규칙이 중복으로 구현된다.
    shipping.address = newAddress
    ```

    - 도메인 규칙의 중복구현

        ```kotlin
        val shipping = order.shipping
        // 도메인 규칙의 중복구현, 캡슐화 무너짐, 수동적 객체...
        if (order.state != OrderState.PAYMENT_WAITING && order.state != OrderState.WAITING) {
        	throw IllegalArgumentException()
        }
        shipping.address = newAddress
        ```

    - **getter / setter** 의 구현은 도메인의 일관성이 무너지는 Code Smell 로 볼 수 있다.
    - **getter / setter** 를 만들지 않고 도메인 메소드를 통해 관리하면 일관성을 유지하기 좋다.

        ```kotlin
        class Order(private val state: OrderState, private var shipping: Shipping) {
        	fun changeShipping(shipping: Shipping) {
        		// 도메인 규칙 검사를 통한 일관성 유지
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


### 애그리거트 루트의 기능 구현

- 애그리거트 루트는 **다른 애그리거트와 협력(직접구현 / 위임)을 통해 기능을 완성한다.**
    - 총 주문금액을 계산하는 내부기능을 협력을 통해 구현

        ```kotlin
        class Order(private var totalAmounts: Money, private val orders: OrderLines) {
        	private fun calculateTotalAmounts() {
        		// 위임을 통해 구현
        		totalAmounts = orders.sumOfAmounts()
        	}
        }
        
        class OrderLines(private val orders: List<OrderLine>) {
        	fun sumOfAmounts() = orders.sumOf { it.price }
        }
        
        ```

    - 회원의 비밀번호 변경하는 기능을 협력을 통해 구현

        ```kotlin
        class Member(private var password: Password) {
        	fun changePassword(currentPassword: String, newPassword: String) {
        		// 직접구현
        		if (!password.match(currentPassword)) {
        			throw PasswordNotMatchException()
        		}
        		password = Password(newPassword)
        	}
        }
        ```


### 트랜잭션 범위

- 트랜잭션 범위는 작을 수록 좋다.
    - 항상성 유지 **편의성**
    - 항상성 유지 **복잡도** 저하
    - 항상성 유지를 위한 작업의 **성능** 향상
- **한 트랜잭션 안에서는 한개의 애그리거트만 수정해야 한다. →**
    - **트랜잭션과 관련 없는 애그리거트는 변경하지 않는다.**
    - 애그리거트 간의 의존성 관리를 통해 결합도를 최소화 한다.

    ```kotlin
    // 배송지를 변경하면서 사용자의 기본 배송지를 동시에 변경하는 기능
    class Order {
    	// 애그리거트가 외부에 제공해야 하는 기능을 메소드로 구현
    	// 트랜잭션 범위
    	fun changeShipping(
    		newShipping: Shipping,
    		useNewShippingAddrAsMemberAddr) {
    			verifyNotYetShipped()
    			setShipping(newShipping)
    			if (useNewShippingAddrAsMemberAddr) {
    				// 다른 애그리거트의 상태를 변경하면 안된다.
    				order.customer.changeAddress(newShipping.address)
    			}
    	}
    }
    ```

    - 그렇다면 위의 기능을 구현하기 위해서 어떻게 해야 할까? → **Application Layer 에서 관리**

    ```kotlin
    class OrderChangeService(
    	private val customerRepository: CustomerRepository,
    	private val orderRepository: OrderRepository
    ) {
    	@Transactional
    	fun changeShippingWithMember(orderId: OrderNo, newShipping: Shipping) {
    		with(order(orderId)) { 
    			changeShipping(it, newShipping)
    			changeShipping(customer(it.customerId), newShipping)
    		}
    	} 
    
    	@Transactional
    	fun changeShipping(orderId: OrderNo, newShipping: Shipping) {
    		changeShipping(order(orderId), newShipping)
    	} 
    
    	private fun order(id: OrderNo): Order = orderRepository.findById(id)
    
    	private fun customer(id: CustomerNo): Customer 
    		= customerRepository.findById(id)
    
    	private fun changeShipping(order: Order, newShipping: Shipping) {
    		orderRepository.save(order.changeShipping(newShipping))
    	}
    
    	private fun changeShipping(customer: Customer, newShipping: Shipping) {
    		customerRepository.save(customer.changeShipping(newShipping))
    	}
    }
    ```

    ```kotlin
    class Order {
    	// 애그리거트가 외부에 제공해야 하는 기능을 메소드로 구현
    	// 트랜잭션 범위
    	fun changeShipping(newShipping: Shipping) {
    			verifyNotYetShipped()
    			setShipping(newShipping)
    	}
    }
    
    class Customer(private val address: Address) {
    	// 트랜잭션 범위
    	fun changeAddress(newAddress: Address) = Customer(newAddress)
    }
    ```

- 예외적인 상황의 경우는 **팀 표준(팀 내부 표준)**, **기술적 제약, UI 구현 편의성** 을 고려할 수 있다.

## 3.3 리포지터리와 애그리거트

- 도메인 객체의 **영속성을 유지하는 리포지터리는 애그리거트 단위로 존재한다.**
    - `save()` : 애그리거트 저장
    - `findById()`: ID 로 애그리거트를 조회
- 애그리거트는 개념적으로 하나의 도메인 모델을 표현하기 때문에 리포지터리는 **애그리거트 전체를 저장소에 영속화 해야 한다.**

    ```kotlin
    class OrderChageService(private val repository: OrderRepository) {
    	fun changeShipping(id: OrderNo, shipping: Shipping) {
    		val order = repository.findById(id)
    		repository.save(order.changeShipping(shipping))
    	}
    }
    
    class OrderRepository(
    	private val orderHistoryDAO: OrderHistoryDAO,
    	private val shippingDAO: ShippingDAO
    ) {
    	fun save(order: Order) {
    		orderHistoryDAO.insert(order.history)
    		shippingDAO.update(order.shipping.id, order.shipping.address)
    	}
    }
    ```


## 3.4 ID를 이용한 애그리거트 참조

- 애그리거트가 다른 애그리거트를 참조할 때에는 반드시 애그리거트 루트를 통해 참조(협력)한다.

![IMG_9245.heic](./images/IMG_9245.jpeg)
[출처: 도메인 주도개발 시작하기 / 최범균]

- 필드를 이용한 애그리거트 직접참조는 편의성을 제공하지만 [단점이 크다.](https://www.notion.so/Chapter-3-96c4e650bf8b44eba2e5a80feb59f05b)

    ```kotlin
    order.orderer.member.id
    ```

    - 편한 탐색을 이용한 **오용** → 의존성과 높은 결합도로 인해 트랜잭션 범위 넓어지고 복잡해짐 → **변경 어려움**
    - 성능이슈
    - **확장** 어려움
- ID 를 이용한 애그리거트 간접 참조는 직접적인 애그리거트와의 의존성을 관리한다.

  ![IMG_9246.heic](./images/IMG_9246.jpeg)
  [출처: 도메인 주도개발 시작하기 / 최범균]

    - **애그리거트 경계가 명확하다.**
    - **복잡도가 낮아진다. (의존성)**
    - 하나의 트랜잭션 안의 애그리거트에서 다른 애그리거트를 변경하는 것을 방지한다.
    - 애그리거트 별로 서로 다른 저장소(기술)를 사용할 수 있으면서 트랜잭션 관리가 용이해진다.

      ![IMG_9247.heic](./images/IMG_9247.jpeg)
      [출처: 도메인 주도개발 시작하기 / 최범균]


### ID를 이용한 참조와 조회 성능

- 애그리거트를 잘 나누었을때(경계가 명확하고 의존성 최소화) 다른 애그리거트를 ID 로 참조하면 여러 애그리거트를 읽어야 할 때 성능저하가 발생할 가능성이 낮아진다.

    ```kotlin
    val customer = customerRepository.findById(ordererId)
    val orders = orderRepository.findByOrderer(ordererId)
    val dtos = orders.map {
    	val prodId = it.orderLines[0].productId
    	// 각 주문마다 첫번째 주문 상품 정보 로딩을 위한 쿼리 실행
    	val product = productRepository.findById(prodId)
    	OrderView(order, customer, product)
    }
    ```

- N+1 문제
    - 주문(`Order`) 1번 조회하면 주문조회 쿼리 1번 + 상품조회 쿼리 1번 총 두 번이 실행된다. (성능이슈)
    - 동일한 저장소를 사용하는 경우
        - ID 참조방식을 객체 참조방식으로 바꾸고 즉시로딩을 사용(세타조인) 한다.

            ```kotlin
            @Repository
            class JpaOrderViewDao(
            	@PersistenceContext private val em: EntityManager
            ) : OrderViewDao {
            	override fun selectByOrderer(ordererId: String): List<OrderView> {
            		val selectQuery = """
            			SELECT new com.myshop.order.application.dto.OrderView(o, m, p)
            			FROM Order o JOIN o.orderLines o1, Member m, Product p
            			WHERE o.orderer.memberId.id = :ordererId
            			AND o.orderer.memberId = m.id
            			AND index(o1) = 0
            			AND o1.productId = p.id
            			ORDER BY o.number DESC  
            		"""
            		
            		val query = em.createQuery(selectQuery, OrderView::class.java)
            		query.setParameter("ordererId", ordererId)
            		return query.getResultList()
            	}	
            }
            ```

    - 물리적으로 다른 저장소를 사용하는 경우
        - Cache 또는 조회 전용 저장소(ES)를 사용한다.

# 3.5 애그리거트 간 집합 연관

- **1 : N (단방향)**
    - 한 카테고리에 여러가지 상품이 등록될 수 있다.
    - 1 (카테고리) : N (상품) == 상품 (N) : 카테고리 (1)

        ```kotlin
        class Category(private val products: Set<Product>)
        ```

    - 개념적인 애그리거트 간의 관계(1 (카테고리) : N (상품))가 실제 구현에 반영되지 않을 수 있다.
        - 특정 카테고리에 속한 상품 목록을 페이징 해서 보여준다.

        ```kotlin
        class Category(private val products: Set<Product>) {
        	fun getProducts(page: Int, size: Int): List<Product> = 
        		with(sortById(products)) {
        			this.subList((page - 1) * size, page * size)
        		}
        }
        ```

        - `products` 갯수가 많으면 많을수록 DB 에서 조회해올 때 심각한 성능문제가 발생할 수 있다.
    - 상품의 입장에서 카테고리와의 관계(상품 (N) : 카테고리 (1))를 실제 구현에 반영한다.

        ```kotlin
        class Product(private val categoryId: CategoryId)
        ```

        ```kotlin
        class ProductListService {
        	fun getProductOfCategory(
        		categoryId: Long, 
        		page: Int, 
        		size: Int
        	): Page<Product> {
        		val category = categoryRepository.findById(categoryId)
        		checkCategory(category)
        
        		val products = 
        			productRepository.findByCategoryId(category.id, page, size)
        		val totalCount = productRepository.countsByCategoryId(category.id)
        		return Page(page, size, totalCount, products)
        	}
        }
        ```

- **M : N (양방향)**
    - 여러가지 상품이 여러가지 카테고리에 등록될 수 있다.
    - 상품이 등록된 모든 카테고리는 상품 상세페이지에서 확인 가능하다. → 실제 구현에서는 1(상품) : N(카테고리)

        ```kotlin
        class Product(private val categoryIds: Set<CategoryId>)
        ```

      **RDBMS(**출처: <도메인 주도개발 시작하기> 최범균**)**

      ![IMG_9266.jpg](./images/IMG_9266.jpeg)
      [출처: 도메인 주도개발 시작하기 / 최범균]

      **JPA**

        ```kotlin
        @Entity
        @Table(name = "product")
        class Product(
            @EmbeddedId 
            val id: ProductId,
            @ElementCollection 
            @CollectionTable(
                name = "product_category", 
                joinColumns = @JoinColumn(name = "product_id")
            )
            val categoryIds: Set<CategoryId>
        )
        ```

        ```kotlin
        @Repository
        open class JpaProductRepository(
            @PersistenceContext private val entityManager: EntityManager
        ) : ProductRepository {
            override fun findByCategoryId(
                cateId: CategoryId, 
                page: Int, 
                size: Int
            ): List<Product> = entityManager.createQuery(
                    """
                        select p from Product p
                        where :cateId member of p.categoryIds 
                        order by p.id.id desc
                    """, 
                    Product::class.java
                ).apply {
                    setParameter("cateId", cateId)
                    setFirstResult((page - 1) * size)
                    setMaxResults(size)
                }.getResultList()
            }
        }
        ```

- 실제 요구사항을 고려하여 개념적인 애그리거트 간의 관계를 그대로 구현할지 판단한다.
- 목록 / 상세화면과 같은 조회 기능은 조회 전용 모델을 이용해서 구현하는 것이 좋다.

# 3.6 애그리거트를 팩토리로 사용하기

- 고객으로부터 여러차례 신고를 받은 상점은 상품을 등록할 수 없다. (도메인 규칙)
- **도메인 로직이 응용 서비스에 노출되면**
    - 응용 서비스 클래스가 도메인 로직을 가지면서 복잡해진다. → 도메인 로직과 결합도 높아짐
    - 도메인 규칙을 테스트 하기 위한 단위테스트가 복잡해진다.
    - 도메인 규칙이 중복으로 구현될 수 있다.

    ```kotlin
    class RegisterProductService(
    	private val storeRepository: StoreRepository,
    	private val productRepository: ProductRepository
    ) {
    	fun registerNewProduct(req: NewProductRequest): ProductId {
    		val store = storeRepository.findById(req.storeId)
    		checkNull(store)
    		// 도메인 로직
    		if (store.isBlocked()) 
    			throw new StoreBlockedException()
    		}
    		//
    		val id = productRepository.nextId()
    		val product = Product(id, store.id, ...)
    		productRepository.save(product)
    		return id
    	}
    }
    ```

- **도메인 로직을 응용 서비스로부터 분리하면**
    - 응용 서비스 클래스가 간단해진다. → 도메인 로직 응집도 높아지고 결합도 낮아짐
    - 도메인 규칙을 테스트 하기 위한 단위테스트가 간단해진다.
    - 도메인 규칙이 한 곳에서 관리된다.

    ```kotlin
    class Store {
    	fun createProduct(newProductId: ProductId, storeId: StoreId): Product {
    		if (isBlocked()) {
    			throw new StoreBlockedException()
    		}
    		return Product(newProductId, storeId, ...)
    	}
    }
    ```

    ```kotlin
    class RegisterProductService(
    	private val storeRepository: StoreRepository,
    	private val productRepository: ProductRepository
    ) {
    	fun registerNewProduct(req: NewProductRequest): ProductId {
    		val store = storeRepository.findById(req.storeId)
    		checkNull(store)
    		
    		val product = store.createProduct(productRepository.nextId(), store.id)
    		productRepository.save(product)
    		return product.id
    	}
    }
    ```

- 애그리거트 생성방법
    - 애그리거트의 일부 정보(Store)를 가지고 다른 애그리거트(Product)를 생성해야 한다면 애그리거트에 **팩토리 메소드**를 구현해보자
    - 애그리거트의 많은 정보(Store)를 가지고 다른 애그리거트(Product)를 생성해야 한다면 별도의 **팩토리 클래스**를 구현해보자

        ```kotlin
        class Store {
        	fun createProduct(newProductId: ProductId, storeId: StoreId): Product {
        		if (isBlocked()) {
        			throw new StoreBlockedException()
        		}
        		return ProductFactory.create(newProductId, storeId, ...)
        	}
        }
        ```

        ```kotlin
        class ProductFactory {
        	companion object {
        		@JvmStatic
        		fun create(id: productId, storeId: StoreId): Product = 
        			Product(id, storeId, ...)
        	}
        }
        ```
