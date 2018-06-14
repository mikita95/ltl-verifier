package automate;

import diagram.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
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

    private static List<Assignment> makeAssignments(@Nullable final String code, final Set<Variable> variableSet) {
        if (StringUtils.isEmpty(code))
            return Collections.emptyList();
        return Arrays.stream(code.split("\\r?\\n"))
                .filter(StringUtils::isEmpty)
                .flatMap(line -> {
                    final String[] assign = StringUtils.removeEnd(line.trim(), ";").split("\\s+=\\s+");
                    return variableSet.stream()
                            .filter(v -> v.getName().equals(assign[0]))
                            .map(v -> Assignment.of(v, Integer.valueOf(assign[1])));
                }).collect(Collectors.toList());
    }

    private static SimpleEntry<Variable, Integer> parseDeclaration(@Nullable final String declaration) {
        final String[] decl = StringUtils.removeEnd(declaration
                .replaceFirst("volatile ", "")
                .replaceFirst("bool ", ""),
                ";")
                .split("\\s+=\\s+");
        return new SimpleEntry<>(
                Variable.of(decl[0], declaration.startsWith("volatile")),
                Integer.parseInt(decl[1]));
    }

    public static DiagramAutomate fromDiagram(final Diagram d) {
        final Map<Variable, Integer> variables = Optional.ofNullable(d)
                .map(Diagram::getData)
                .map(diagram.Data::getStateMachine)
                .map(StateMachine::getVariable)
                .orElse(Collections.emptyList())
                .stream()
                .map(v -> parseDeclaration(v.getDeclaration()))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

        final Map<String, Event> events = Optional.ofNullable(d)
                .map(Diagram::getData)
                .map(diagram.Data::getStateMachine)
                .map(StateMachine::getEvent)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(Event::getName, Function.identity()));

       final Map<Integer, Edge> edges = d.getWidget()
               .stream()
               .filter(widget -> "Transition".equals(widget.getType()))
               .map(widget -> Edge.of(
                       widget.getId(),
                       makeAssignments(widget.getAttributes().getCode(), variables.keySet()),
                       widget.getAttributes().getGuard(),
                       Optional.of(widget)
                               .map(Widget::getAttributes)
                               .map(Attributes::getAction)
                               .orElse(Collections.emptyList()),
                       widget.getAttributes().getEvent()
               ))
               .collect(Collectors.toMap(Edge::getId, Function.identity()));

       final Map<Integer, State> states = d.getWidget().stream()
               .filter(widget -> "State".equals(widget.getType()))
               .map(widget -> State.of(
                       widget.getId(),
                       widget.getAttributes().getName(),
                       Optional.of(widget)
                               .map(Widget::getAttributes)
                               .map(Attributes::getIncoming)
                               .orElse(Collections.emptyList())
                               .stream()
                               .map(transition -> edges.get(transition.getId()))
                               .collect(Collectors.toList()),
                       Optional.of(widget)
                               .map(Widget::getAttributes)
                               .map(Attributes::getOutgoing)
                               .orElse(Collections.emptyList())
                               .stream()
                               .map(transition -> edges.get(transition.getId()))
                               .collect(Collectors.toList())
               )).collect(Collectors.toMap(State::getId, Function.identity()));

       return DiagramAutomate.of(
               variables,
               events,
               states,
               states.get(0),
               edges
       );
    }
}
