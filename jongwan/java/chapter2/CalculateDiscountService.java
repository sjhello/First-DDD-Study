package chapter2;

public class CalculateDiscountService {
    private OrderDisCounter orderDisCounter;

    public CalculateDiscountService(OrderDisCounter orderDisCounter) {
        this.orderDisCounter = orderDisCounter;
    }

    public long calculateDiscount(Long amount, String userId, String grade){
        return orderDisCounter.apply(100L);
    }
}
