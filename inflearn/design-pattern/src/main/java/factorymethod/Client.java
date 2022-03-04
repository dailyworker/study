package factorymethod;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();

        Ship whiteShip = ShipFactory.orderShip("WhiteShip", "workingssu@gmail.com");
        System.out.println(whiteShip.toString());

        Ship blackShip = ShipFactory.orderShip("blackShip", "workingssu@gmail.com");
        System.out.println(blackShip.toString());

    }
}
