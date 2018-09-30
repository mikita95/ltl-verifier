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
        final Set<StateKripke> resultStates = new HashSet<>();
        resultStates.add(initState);
        final Map<StateKripke, Set<StateKripke>> resultTransitions = new HashMap<>();

        boolean changed = false;

        do {
            changed = false;
            Set<StateKripke> tempSet = new HashSet<>();
            for (StateKripke b : resultStates) {
                Set<Variable> variables = b.getVariables();
                if (Objects.isNull(variables)) {
                    continue;
                }
                final State s = variables.stream()
                        .map(q -> automate.getStates().values().stream().filter(st -> StringUtils.equals(st.getName(), q.getName())).findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);

                if (Objects.isNull(s)) {
                    continue;
                }

                final List<StateEvent> possibleTransitions = transitions.getOrDefault(s, null);

                for (StateEvent stateEvent : possibleTransitions) {
                    StateKripke lastState = b;

                    final List<Set<Variable>> innerWorlds = stateEvent.getActions().stream()
                            .map(action -> ImmutableSet.of(
                                    Variable.of(s.getName()),
                                    Variable.of(stateEvent.getEvent().getName()),
                                    Variable.of(action.getName())
                                    )
                            ).collect(Collectors.toList());

                    innerWorlds.add(ImmutableSet.of(
                            Variable.of(s.getName()),
                            Variable.of(stateEvent.getEvent().getName())));


                    for (Set<Variable> innerWorld : innerWorlds) {
                        final StateKripke innerState = StateKripke.of(innerWorld);

                        if (new HashSet<>(resultStates).stream().noneMatch(stateKripke -> stateKripke.getVariables().equals(innerWorld))) {
                            tempSet.add(innerState);
                            changed = true;
                        }

                        resultTransitions.putIfAbsent(lastState, new HashSet<>());
                        resultTransitions.get(lastState).add(initState);

                        lastState = initState;
                    }

                    final Set<Variable> toWorld = ImmutableSet.of(Variable.of(stateEvent.getState().getName()));
                    final StateKripke toState = StateKripke.of(toWorld);

                    if (new HashSet<>(resultStates).stream().noneMatch(stateKripke -> stateKripke.getVariables().equals(toWorld))) {
                        tempSet.add(toState);
                        changed = true;
                    }

                    resultTransitions.putIfAbsent(lastState, new HashSet<>());
                    resultTransitions.get(lastState).add(toState);
                }


            }
            resultStates.addAll(tempSet);
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
