package automate;

import diagram.Attributes;
import diagram.Diagram;
import diagram.Transition;
import diagram.Widget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "of")
@Data(staticConstructor = "of")
@Setter
public class State {
    private Integer id;
    private String name;
    private List<Edge> incoming;
    private List<Edge> outcoming;

    public static Map<Integer, State> fromDiagram(final Diagram d) {
        final Map<Integer, Edge> edges = Edge.fromDiagram(d);

        return d.getWidget().stream()
                .filter(widget -> "StateKripke".equals(widget.getType()))
                .map(widget -> State.of(widget, edges))
                .collect(Collectors.toMap(State::getId, Function.identity()));
    }

    private static List<Transition> getTransitions(final Widget widget, Function<Attributes, List<Transition>> getter) {
        return Optional.ofNullable(widget)
                .map(Widget::getAttributes)
                .map(getter)
                .orElse(Collections.emptyList());
    }

    private static List<Edge> getEdges(final Widget widget,
                                       final Map<Integer, Edge> edges,
                                       Function<Attributes, List<Transition>> getter) {
        return getTransitions(widget, getter)
                .stream()
                .map(Transition::getId)
                .map(edges::get)
                .collect(Collectors.toList());
    }

    public static State of(final Widget widget, final Map<Integer, Edge> edges) {
        if (Objects.isNull(widget))
            return null;

        return State.of(
                widget.getId(),
                widget.getAttributes().getName(),
                getEdges(widget, edges, Attributes::getIncoming),
                getEdges(widget, edges, Attributes::getOutgoing));
    }
}
