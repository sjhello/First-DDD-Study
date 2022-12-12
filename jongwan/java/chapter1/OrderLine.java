package chapter1;

public class OrderLine {
    private Product product;
    private Money price;
    private int quantity;
    private int amounts;

    public OrderLine(Product product, Money price, int quantity, int amounts) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amounts = amounts;
    }

    private Money calculateAmounts() {
        return price.multiply(quantity);
    }

    public int getAmounts() {
        return 0;
    }
}
