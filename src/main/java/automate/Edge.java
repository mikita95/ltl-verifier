package automate;

import diagram.Action;
import diagram.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class Edge {
    private Integer id;
    private List<Assignment> code;
    private String guard;
    private List<Action> actions;
    private Event event;
}
