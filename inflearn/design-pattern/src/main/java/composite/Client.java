package composite;

public class Client {
    public static void main(String[] args) {
        Item doranBlade = new Item("도란검", 450);
        Item healPotion = new Item("체력 물약", 50);

        Bag bag = new Bag();
        bag.add(doranBlade);
        bag.add(healPotion);

        Client client = new Client();
        client.printItem(doranBlade);
        System.out.println(client.printPrice(bag));

    }

    // 클라이언트의 책임이 올바른가?
    private int printPrice(Bag bag) {
        return bag.getItems()
                .stream()
                .mapToInt(Item::getPrice)
                .sum();
    }
    // 클라이언트의 책임이 올바른가?
    private void printItem(Item item) {
        System.out.println(item.getPrice());
    }
}
