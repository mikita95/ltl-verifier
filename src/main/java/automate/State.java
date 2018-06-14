package automate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor(staticName = "of")
@Data(staticConstructor = "of")
@Setter
public class State {
    private Integer id;
    private String name;
    private List<Edge> incoming;
    private List<Edge> outcoming;
}
