package chapter1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class Ch1Test {

    @Test
    @DisplayName("주문완료")
    public void order(){
        ArrayList orderLines = new ArrayList<OrderLine>();
        orderLines.add(new OrderLine(new Product("상품1","001"), new Money(100),1,2));
        orderLines.add(new OrderLine(new Product("상품2","002"), new Money(100),1,2));
        orderLines.add(new OrderLine(new Product("상품3","003"), new Money(100),1,2));
        Address address = new Address("1번지","2번지","123-333");
        ShippingInfo shippingInfo = new ShippingInfo("이름","01000000000",address);
        Order order = new Order(orderLines,shippingInfo);
    }
}
