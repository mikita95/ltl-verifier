package buchi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data(staticConstructor = "of")
@AllArgsConstructor
@Setter
public class BuchiState<E> {
    private E tag;
}
