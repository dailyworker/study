package bridge;

public class App {
    public static void main(String[] args) {
        Champion kda = new 아리(new KDA());
        kda.skillQ();
        kda.skillW();

        // 클라이언트는 Champion (추상적인) 클래스를 사용하고 있다.
        // new 아리(new PoolParty()) 이 부분은 의존성 주입하는 방식으로 감출 수 있다.
        // 따라서, 구체적인 클래스를 사용하는 것은 아니다.
        Champion poolParty = new 아리(new PoolParty());
        poolParty.skillW();
        poolParty.move();
    }
}
