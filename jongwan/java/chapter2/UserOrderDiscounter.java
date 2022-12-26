package chapter2;


public class UserOrderDiscounter implements OrderDisCounter{

    private UserGrade userGrade;
    public UserOrderDiscounter(UserGrade userGrade) {
        this.userGrade = userGrade;
    }
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
}
