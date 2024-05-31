package abstract_factorymethod;

// 추상 팩토리
public interface ShipPartsFactory {
    Anchor createAnchor();
    Wheel createWheel();
}
