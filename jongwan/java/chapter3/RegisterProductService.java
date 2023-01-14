package chapter3;

public class RegisterProductService {

    public Product registerNewProdut(String req) throws Exception{
        Store store = new Store(1, false);
        return store.createProduct("테스트상품","123");
    }
}
