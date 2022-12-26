package chapter2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CalculateDiscountServiceTest {

    private OrderDisCounter orderDisCounter;
    @BeforeEach
    public void setup() {
        orderDisCounter = new OrderDisCounter() {
            private UserGrade userGrade = new UserGrade();
            @Override
            public long apply(long amount) {
                if(userGrade.isOrderExist()){
                    return appVisitDiscount(userGrade.applyDiscount(amount, "123", "VIP"));
                }
                return amount;
            }
            //추가된 요구사항
            private long appVisitDiscount(long amount){
                return amount/2;
            }
        };
    }

    @Test
    @DisplayName("service와 dao 의존성이 분리되면 테스트 하기가 쉬워진다.")
    void test(){
        CalculateDiscountService calculateDiscountService = new CalculateDiscountService(orderDisCounter);
        Assertions.assertEquals(25L,orderDisCounter.apply(100L));
    }
}