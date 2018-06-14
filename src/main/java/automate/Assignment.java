package automate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class Assignment {
    private Variable variable;
    private Integer value;
}
