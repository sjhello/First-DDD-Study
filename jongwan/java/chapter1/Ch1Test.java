package chapter1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.UUID;

public class Ch1Test {

    @Test
    @DisplayName("주문완료")
    public void order(){
        ArrayList orderLines = new ArrayList<OrderLine>();
        orderLines.add(new OrderLine(new Product("상품1","001"), new Money(100),1,new Money(200)));
        orderLines.add(new OrderLine(new Product("상품2","002"), new Money(100),1,new Money(200)));
        orderLines.add(new OrderLine(new Product("상품3","003"), new Money(100),1,new Money(200)));
        Address address = new Address("1번지","2번지","123-333");
        ShippingInfo shippingInfo = new ShippingInfo("이름","01000000000",address);
        Order order = new Order(orderLines,shippingInfo);
    }
    
    @Test
    @DisplayName("orderNumber가 같으면 값은 엔티티로 판단을 한다.")
    public void orderNumber같으면동일엔티티(){
        Order order = Order.builder().id(new OrderNo("1")).build();
        Order order2 = Order.builder().id(new OrderNo("1")).build();
        Assertions.assertEquals(order, order2);
    }

    @Test
    @DisplayName("java uuid")
    public void javauuid(){
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Assertions.assertNotEquals(uuid, uuid2);
    }

    @Test
    @DisplayName("MoneyTest")
    public void MoneyImmutable(){
        Money price = new Money(1000);
        OrderLine line = new OrderLine(new Product("상품1","001"), price,1,new Money(20000));
        price.setValue(2000);
        Assertions.assertEquals(line.getPrice().getValue(), 1000);
    }
}
