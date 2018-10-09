package kripke;

import automate.DiagramAutomate;
import automate.Edge;
import automate.State;
import automate.Variable;
import com.google.common.collect.ImmutableSet;
import diagram.Action;
import diagram.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class ModelKripke {
    public Set<StateKripke> states;
    public StateKripke initStateKripke;
    public Map<StateKripke, Set<StateKripke>> transitions;

    public static ModelKripke fromDiagramAutomate(final DiagramAutomate automate) {
        // TODO: need to be refactored
        final Map<State, List<StateEvent>> transitions = automate.getStates()
                .values()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        state -> state.getOutcoming().stream().map(edge -> StateEvent.of(automate, edge)).collect(Collectors.toList())
                ));

        final StateKripke initState = StateKripke.of(Collections.singleton(Variable.of(automate.getInitState().getName(), false)));
        final Set<StateKripke> resultStates = new LinkedHashSet<>();
        resultStates.add(initState);
        final Map<StateKripke, Set<StateKripke>> resultTransitions = new HashMap<>();

        boolean changed;

        do {
            changed = false;
            for (StateKripke b : new LinkedHashSet<>(resultStates)) {
                Set<Variable> variables = b.getVariables();
                if (Objects.isNull(variables)) {
                    continue;
                }
                final List<State> sList = variables.stream()
                        .map(q -> automate.getStates().values().stream().filter(st -> StringUtils.equals(st.getName(), q.getName())).findFirst().orElse(null))
                        .collect(Collectors.toList());

                if (sList.size() != 1) {
                    continue;
                }
                final State s = sList.get(0);

                final List<StateEvent> possibleTransitions = transitions.getOrDefault(s, Collections.emptyList());

                for (StateEvent stateEvent : possibleTransitions) {
                    StateKripke lastState = b;

                    final List<Set<Variable>> innerWorlds = new ArrayList<>();
                    innerWorlds.add(ImmutableSet.of(
                            Variable.of(s.getName()),
                            Variable.of(stateEvent.getEvent().getName())));

                    innerWorlds.addAll(stateEvent.getActions().stream()
                            .map(action -> ImmutableSet.of(
                                    Variable.of(s.getName()),
                                    Variable.of(stateEvent.getEvent().getName()),
                                    Variable.of(action.getName())
                                    )
                            ).collect(Collectors.toList()));

                    for (Set<Variable> innerWorld : innerWorlds) {
                        final StateKripke innerState = StateKripke.of(innerWorld);

                        if (resultStates.stream().noneMatch(stateKripke -> stateKripke.getVariables().equals(innerWorld))) {
                            resultStates.add(innerState);
                            changed = true;
                        }

                        resultTransitions.putIfAbsent(lastState, new LinkedHashSet<>());
                        resultTransitions.get(lastState).add(innerState);

                        lastState = innerState;
                    }

                    final Set<Variable> toWorld = ImmutableSet.of(Variable.of(stateEvent.getState().getName()));
                    final StateKripke toState = StateKripke.of(toWorld);

                    if (resultStates.stream().noneMatch(stateKripke -> stateKripke.getVariables().equals(toWorld))) {
                        resultStates.add(toState);
                        changed = true;
                    }

                    resultTransitions.putIfAbsent(lastState, new LinkedHashSet<>());
                    resultTransitions.get(lastState).add(toState);
                }


            }
        } while (changed);
        return ModelKripke.of(
                resultStates,
                initState,
                resultTransitions
        );
    }

    @Data(staticConstructor = "of")
    @AllArgsConstructor(staticName = "of")
    @Setter
    private static class StateEvent {
        private State state;
        private List<Action> actions;
        private Event event;

        private static StateEvent of(final DiagramAutomate automate, final Edge edge) {
            return automate.getStates().values().stream().filter(s -> s.getIncoming().contains(edge)).findFirst()
                    .map(dest -> StateEvent.of(
                            dest,
                            edge.getActions(),
                            edge.getEvent()
                    )).orElseThrow(() -> new RuntimeException("Unable to construct kripke state event"));
        }
    }


}
