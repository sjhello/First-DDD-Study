package chapter3;

public class Store {
    private int storeId;
    private boolean block;

    public Store(int storeId, boolean block) {
        this.storeId = storeId;
        this.block = block;
    }

    public boolean isBlcoekd() {
        return this.block;
    }

    public Product createProduct(String name, String code) throws Exception {
        if(isBlcoekd()) throw new Exception();
        return ProductFactory.create(name,code);
    }
}
