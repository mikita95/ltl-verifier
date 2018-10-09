package buchi;

import lombok.Data;
import lombok.Setter;

import java.util.Objects;

@Data(staticConstructor = "of")
@Setter
public class BuchiState<E> {
    private E tag;

    public BuchiState(E tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuchiState<?> that = (BuchiState<?>) o;
        return Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tag);
    }
}
