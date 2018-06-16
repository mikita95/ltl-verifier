package automate;

import diagram.Diagram;
import diagram.Event;
import diagram.StateMachine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class DiagramAutomate {
    private Map<Variable, Integer> variables;
    private Map<String, Event> events;
    private Map<Integer, State> states;
    private State initState;
    private Map<Integer, Edge> edges;

    public static DiagramAutomate fromDiagram(final Diagram d) {
        final Map<String, Event> events = Optional.ofNullable(d)
                .map(Diagram::getData)
                .map(diagram.Data::getStateMachine)
                .map(StateMachine::getEvent)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(Event::getName, Function.identity()));

       final Map<Integer, State> states = State.fromDiagram(d);

       return DiagramAutomate.of(
               Variable.fromDiagram(d),
               events,
               states,
               states.get(0),
               Edge.fromDiagram(d)
       );
    }
}
