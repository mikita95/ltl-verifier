package buchi;

import automate.Variable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import kripke.ModelKripke;
import kripke.StateKripke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import ltl.Prop;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class BuchiAutomate<S, T> {
    private BuchiState<S> initState;
    private Map<BuchiState<S>, Multimap<T, BuchiState<S>>> transitions;
    private Set<Set<BuchiState<S>>> acceptingFamily;

    public BuchiAutomate() {

    }

    public Set<BuchiState<S>> nodes() {
        return transitions.keySet();
    }

    public boolean isGeneralized() {
        return acceptingFamily.size() > 1;
    }

    public BuchiAutomate<StateKripke, Set<Variable>> of(final ModelKripke modelKripke) {
        Map<StateKripke, BuchiState<StateKripke>> buchiStateByKripkeState = modelKripke.getStateKripkes().stream()
                .collect(Collectors.toMap(Function.identity(), BuchiState::new));


        Map<BuchiState, Multimap> transitions = modelKripke.getStateKripkes().stream().collect(Collectors.toMap(
                buchiStateByKripkeState::get,
                k -> makeTransition(modelKripke.getTransitions().get(k), buchiStateByKripkeState)
        ));


        return BuchiAutomate.of(
                buchiStateByKripkeState.getOrDefault(modelKripke.getInitStateKripke(), null),
                transitions,
                ImmutableSet.of(ImmutableSet.copyOf(buchiStateByKripkeState.values()))
        );
    }

    private Multimap<Set<Variable>, BuchiState<StateKripke>> makeTransition(final Set<StateKripke> states, final Map<StateKripke, BuchiState<StateKripke>> buchiStateByKripkeState) {
        ImmutableMultimap.Builder<Set<Variable>, BuchiState<StateKripke>> builder = ImmutableMultimap.builder();
        states.forEach(toState ->
                builder.put(
                        toState.getVariables(),
                        buchiStateByKripkeState.get(toState)
                )
        );
        return builder.build();
    }

    public <E> BuchiAutomate<E, Set<Prop>> variableAutomatonToPropAutomaton(BuchiAutomate<E, Set<Variable>> variableAutomate) {
        final ImmutableMap.Builder<BuchiState<E>, Multimap<Set<Prop>, BuchiState<E>>> transBuilder = ImmutableMap.builder();

        for (Map.Entry<BuchiState<E>, Multimap<Set<Variable>, BuchiState<E>>> entry : variableAutomate.getTransitions().entrySet()) {
            final BuchiState<E> state = entry.getKey();
            final Multimap<Set<Variable>, BuchiState<E>> map = entry.getValue();

            final ImmutableMultimap.Builder<Set<Prop>, BuchiState<E>> builder = ImmutableMultimap.builder();
            map.entries()
                    .forEach(edge -> builder.put(
                            edge.getKey().stream().map(v -> new Prop(v.getName())).collect(Collectors.toSet()),
                            edge.getValue()
                    ));

            transBuilder.put(state, builder.build());
        }

        final BuchiAutomate<E, Set<Prop>> result = new BuchiAutomate<>();
        result.setInitState(variableAutomate.getInitState());
        result.setAcceptingFamily(variableAutomate.getAcceptingFamily());
        result.setTransitions(transBuilder.build());

        return result;
    }

    public static <S, T> BuchiAutomate<S, T> degeneralize(BuchiAutomate<S, T> buchiAutomate) {
        if (!buchiAutomate.isGeneralized()) {
            return buchiAutomate;
        }

        List<Set<BuchiState<S>>> fs = new ArrayList<>(buchiAutomate.getAcceptingFamily());
        List<LayerBuchiState<S>> newStates = IntStream.of(fs.size()).boxed().flatMap(layer ->
            buchiAutomate.nodes().stream().map(state -> new LayerBuchiState<>(layer, state))
        ).collect(Collectors.toList());

        Map<BuchiState<S>, Multimap<T, BuchiState<S>>> transitions = newStates.stream().collect(Collectors.toMap(
                Function.identity(),
                state -> {
                    final int nextLayer = (state.getLayer() + 1) % fs.size();
                    ImmutableMultimap.Builder<T, BuchiState<S>> builder = ImmutableMultimap.builder();
                    buchiAutomate.getTransitions().get(state.getOrigin()).entries().stream().map(
                            entry -> {
                                LayerBuchiState<S> resultState;
                                if (fs.get(state.getLayer()).contains(state.getOrigin())) {
                                    resultState = newStates.stream().filter(lb -> lb.getLayer() == nextLayer && Objects.equals(lb.getOrigin(), entry.getValue())).findFirst().orElse(null);
                                } else {
                                    resultState = newStates.stream().filter(lb -> Objects.equals(lb.getLayer(), state.getLayer()) && Objects.equals(lb.getOrigin(), entry.getValue())).findFirst().orElse(null);
                                }

                                return new AbstractMap.SimpleEntry<>(
                                        entry.getKey(),
                                        resultState
                                );
                            }).forEach(entry -> builder.put(entry.getKey(), entry.getValue())
                    );
                    return builder.build();
                }
        ));

        BuchiAutomate<S, T> result = new BuchiAutomate<>();
        result.setInitState(newStates.stream().filter(s -> s.getLayer() == 0 && Objects.equals(s.getOrigin(), buchiAutomate.getInitState())).findFirst().orElse(null));
        result.setTransitions(transitions);
        result.setAcceptingFamily(ImmutableSet.of(newStates.stream().filter(s -> fs.get(0).contains(s)).collect(Collectors.toSet())));
        return result;
    }
}
