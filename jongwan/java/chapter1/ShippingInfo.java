package chapter1;

public class ShippingInfo {
    private String receiverName;
    private String receiverPhoneNumber;
    private Address address;

    public ShippingInfo(String receiverName, String receiverPhoneNumber, Address address) {
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.address = address;
    }
}
