package buchi;

import lombok.ToString;

import java.util.Objects;

@ToString
public class TagedBuchiState<E> extends BuchiState<E> {

    private BuchiState<E> origin;
    private Integer number;

    public TagedBuchiState(Integer layer, BuchiState<E> origin) {
        super(origin.getTag());
        this.number = layer;
        this.origin = origin;
    }

    public Integer getNumber() {
        return number;
    }

    public BuchiState<E> getOrigin() {
        return origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TagedBuchiState<?> that = (TagedBuchiState<?>) o;
        return Objects.equals(origin, that.origin) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), origin, number);
    }
}
