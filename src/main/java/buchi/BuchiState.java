package buchi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class BuchiState<E> {
    private E tag;
}
