package factorymethod;

public interface ShipFactory {
    default Ship orderShip(String name, String email) {
        validate(name, email);
        prepareFor(name);
        Ship ship = createShip();
        sendEmailTo(email, ship);
        return ship;
    }

    Ship createShip();

    private void sendEmailTo(String email, Ship ship) {
        System.out.println(ship.getName() + "만들어졌습니다.");
        System.out.println(email + "에게 " + ship.getName() + "이 완성됐음을 이메일로 알림을 보냈습니다.");
    }

    private void validate(String name, String email) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("배 이름을 지어주세요.");
        }

        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("연락처를 남겨주세요.");
        }
    }

    private static void prepareFor(String name) {
        System.out.println(name + "만들 준비 중..");
    }
}
