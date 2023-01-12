package chapter3;

public class ProductFactory {
    public static Product create(String name, String code){
        return new Product(name,code);
    }
}
