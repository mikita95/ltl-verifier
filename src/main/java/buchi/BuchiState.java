package buchi;

import lombok.Data;
import lombok.Setter;

@Data(staticConstructor = "of")
@Setter
public class BuchiState<E> {
    private E tag;

    public BuchiState(E tag) {
        this.tag = tag;
    }
}
