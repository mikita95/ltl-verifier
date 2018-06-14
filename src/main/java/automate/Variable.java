package automate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class Variable {
    private String name;
    private boolean isVolatile;
}
