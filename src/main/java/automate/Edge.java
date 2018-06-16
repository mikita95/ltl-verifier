package automate;

import diagram.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class Edge {
    private Integer id;
    private List<Assignment> code;
    private String guard;
    private List<Action> actions;
    private Event event;

    public static Map<Integer, Edge> fromDiagram(final Diagram d) {
        final Map<Variable, Integer> variables = Variable.fromDiagram(d);
        return d.getWidget()
                .stream()
                .filter(widget -> "Transition".equals(widget.getType()))
                .map(widget -> Edge.of(
                        widget.getId(),
                        Assignment.makeAssignments(widget.getAttributes().getCode(), variables.keySet()),
                        widget.getAttributes().getGuard(),
                        Optional.of(widget)
                                .map(Widget::getAttributes)
                                .map(Attributes::getAction)
                                .orElse(Collections.emptyList()),
                        widget.getAttributes().getEvent()
                ))
                .collect(Collectors.toMap(Edge::getId, Function.identity()));
    }
}
