## 애그리거트 간 집합 연관

### 1:N 관계 상품과 카테고리와의 관계로 예시
- 카테고리(1) : 상품(N)의 관계
```java
public class Category {
    private Set<Product> products;
}
```
- 1-N 연관을 구현하는 것이 요구사항을 충족하는 것과 상관 없는 경우가 많음
- 예시로 모든 상품을 가져온다기보다 페이징을 통해 가져온다.
```java
public class Category {
    private Set<Product> products;
    
    public List<Product> getProducts(Pageable pageable) {
        List<Product> sortedProducts = sortById(products);
        return sortedProducts.subList((page-1) * size, page * size);
    }
}
```
- 위와같은 구조로 가져올시 DBMS에서 모든 데이터를 가져오기 때문에 처리하는 성능에 크게 지장을 준다.
- 1-N이 아닌 N-1의 구조로 데이터를 가져오는 것이 좀 더 효율적으로 사용할 수 있다.

```java
public class Product {
    private Long categoryId;
}
```
```java
public class ProductListService {
    
    public Page<Product> getProductOfCategory(Long categoryId, Pageable pageable){
        Category category = categoryRepository.findById(categoryId); 
        checkCategory(category);
        List<Product> products = productRepository.findByCategoryId(category.getId(), pageable);
        
        int totalCount = productRepository.countByCategoryId(category.getId());
        
        return new Page(pageable, toalCount, products);
    }
}
```
### M:N 관계 상품과 카테고리와의 관계로 예시
- 개념적으로 양쪽 애그리거트에 컬렉션으로 연관 관계를 두거나 데이터베이스처럼 중간의 엔티티를 두고 연관을 맺는다.
- 요구사항을 생각해보면 재품의 모든 카테고리(N)를 보여주는 것은 보통 제품의 상세 화면(1)인 경우가 많다.
- 따라서 이러한 요구사항을 봤을 때 구현할 때에는 집합연관을 필요하지 않기 때문에 단방향으로만 연관관계를 적용하면 된다.

```java
public class Product {
    
    private Set<Long> categoryIds;
}

public interface JpaProductRepository extends JpaRepository<Product, Long> {
    
    @Query(value = "select p " +
            "from Product p " +
            "where p.categoryIds = :categoryId order by b.id desc ")
    public List<Procut> findByCategoryId(Long categoryId, Pageable pageable);
        
}
```

## 애그리거트를 팩토리로 활용하기
- 특정 상점이 차단되어 더 이상 물건을 등록하지 못할경우 구현해야한다.
```java
public class RegisterProductService {
    
    public Product registerNewProduct (NewProductRequest req) {
        Store store = storeRepository.findById(req.getId())
                .oeElseThrow(() -> new NullPointException());
        
        if (isBlocked(store)) {
            throw new StoreBlockedException();
        }
        
        Long productId  = productRepository.nextId();
        Product product = new Product(productId, store);
        return productRepository.save(product);
    }
}
```
- 스토어가 상품을 생성할 수 있는지 물어보고 상품을 생성하는 것은 논리적으로 하나의 도메인의 기능인데 응용 서비스에서 상품 처리가 노출되었다.
- 해당 도메인 기능을 넣기 위해 별도의 서비스나 팩토리 클래스를 만들 수 있지만 에그리거트에 구현할 수도 있다.

```java
public class Store {
    
    public Product createProduct(Long productId) {
        if (isBlocked()) throw new StoreBlockedException();
        
        return new Product(productId, storeId);
    } 
}
```

### 느낀점
- 구현해야되는 요구상황에 따라 유연하게 도메인 기능을 구현해야한다. 
- 도메인 기능이 인프라 스트럭처를 통해 처리해야되는 요구사항이라도 에그리거트를 사용하여 처리하도록 해야될까?
- 요구사항이 N:M 관계에서 둘 다 필요한 상황이라면 1:N, N:1 관계를 가져가야 할까?
