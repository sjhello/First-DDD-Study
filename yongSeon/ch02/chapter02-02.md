## 요청 처리의 흐름
 
![img_6.png](image%2Fimg_6.png)
- 클라이언트에 받은 요청을 컨트롤러가 처리할 수 있는 객체나 값으로 변환
- 컨트롤러에 등록된 서비스 로직을 호출한다.
- 서비스에서 도메인객체를 받아서 처리하거나 도메인 객체를 가져와서 비지니스 로직을 실행한다. 

### 서비스 레이어에서 트랜잭션 처리
```java
public class CancelOrderService {
    private OrderRepository orderRepository; 
    
    @Transactional
    public void cancel(OrderNumber orderNumber) {
        Order order = orderRepository.findByNumber(orderNumber);
        if (order == null) throw new NoOrderException(orderNumber);
        order.cancel();
    }
}
```

## 인프라스트럭처
- 표현영역, 응용영역, 도메인 영역, 지원한다.
- 영속성 처리, 트랜잭션, SMTP 클라이언트, REST 클라이언트 등 다른 영역에서 필요하는 구현 기술, 보조 기능을 지원한다.
- 구현의 중요함은 DIP(변경의 유연함, 테스트가 쉬움)의 중요한 만큼 중요하다.
- DIP의 장점을 해치지 않는 선에서 구현 기술에 의존하는 것은 괜찮다.
- 예시로 스프링에 대한 의존을 없애기 위해 `@Transactional`을 대체하려 한다면 복잡한 스프링을 설정하고 그것을 위한 테스트를 더 작성해야한다.

## 모듈 구조
- 아키텍처의 각 영역은 별도 패키지에 위치한다.
- 패키지 구성 규칙에 한 개의 정답만 존재하는 것은 아니다.
  - 하위 도메인별로 모듈 구성
- 10개에서 15개 미만 정도의 모듈 개수를 유지하는 것이 유지보수하는 데에 어려움이 없다.
![img_7.png](image%2Fimg_7.png)
![img_8.png](image%2Fimg_8.png)