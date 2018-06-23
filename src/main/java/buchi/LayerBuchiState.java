package buchi;

import lombok.ToString;

import java.util.Objects;

@ToString
public class LayerBuchiState<E> extends BuchiState<E> {

    private BuchiState<E> origin;
    private Integer layer;

    public LayerBuchiState(Integer layer, BuchiState<E> origin) {
        super(origin.getTag());
        this.layer = layer;
        this.origin = origin;
    }

    public Integer getLayer() {
        return layer;
    }

    public BuchiState<E> getOrigin() {
        return origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LayerBuchiState<?> that = (LayerBuchiState<?>) o;
        return Objects.equals(origin, that.origin) &&
                Objects.equals(layer, that.layer);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), origin, layer);
    }
}
