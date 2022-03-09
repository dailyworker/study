package composite;

import java.util.ArrayList;
import java.util.List;

// Composite 객체이므로 Item을 참조하면 안된다.
public class Bag implements Component {
    private final List<Component> components = new ArrayList<>();

    public void add(Component item) {
        components.add(item);
    }

    public List<Component> getComponents() {
        return new ArrayList<>(components);
    }

    @Override
    public int getPrice() {
        return components.stream()
                .mapToInt(Component::getPrice)
                .sum();
    }
}
