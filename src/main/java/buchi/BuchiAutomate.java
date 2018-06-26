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
import ltl.Formula;
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
        List<TagedBuchiState<S>> newStates = IntStream.of(fs.size()).boxed().flatMap(layer ->
            buchiAutomate.nodes().stream().map(state -> new TagedBuchiState<>(layer, state))
        ).collect(Collectors.toList());

        Map<BuchiState<S>, Multimap<T, BuchiState<S>>> transitions = newStates.stream().collect(Collectors.toMap(
                Function.identity(),
                state -> {
                    final int nextLayer = (state.getNumber() + 1) % fs.size();
                    ImmutableMultimap.Builder<T, BuchiState<S>> builder = ImmutableMultimap.builder();
                    buchiAutomate.getTransitions().get(state.getOrigin()).entries().stream().map(
                            entry -> {
                                TagedBuchiState<S> resultState;
                                if (fs.get(state.getNumber()).contains(state.getOrigin())) {
                                    resultState = newStates.stream().filter(lb -> lb.getNumber() == nextLayer && Objects.equals(lb.getOrigin(), entry.getValue())).findFirst().orElse(null);
                                } else {
                                    resultState = newStates.stream().filter(lb -> Objects.equals(lb.getNumber(), state.getNumber()) && Objects.equals(lb.getOrigin(), entry.getValue())).findFirst().orElse(null);
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
        result.setInitState(newStates.stream().filter(s -> s.getNumber() == 0 && Objects.equals(s.getOrigin(), buchiAutomate.getInitState())).findFirst().orElse(null));
        result.setTransitions(transitions);
        result.setAcceptingFamily(ImmutableSet.of(newStates.stream().filter(s -> fs.get(0).contains(s)).collect(Collectors.toSet())));
        return result;
    }

    private enum Color {
        WHITE,
        GREY,
        BLACK
    }

    public static <T, E> List<BuchiState<E>> findReachableCycle(BuchiAutomate<E, T> buchiAutomate) {
        final Set<BuchiState<E>> accepting = buchiAutomate.getAcceptingFamily().stream().findFirst().orElse(null);

        Deque<BuchiState<E>> nodesStack = new ArrayDeque<>();
        Map<BuchiState<E>, Color> colors = buchiAutomate.nodes().stream().collect(Collectors.toMap(
                Function.identity(),
                k -> Color.WHITE
        ));
        Map<BuchiState<E>, Color> colors2 = new HashMap<>(colors);

        try {
            dfsOuter(buchiAutomate, accepting, colors, colors2, buchiAutomate.getInitState(), nodesStack);
        } catch (Exception e) {
            return new ArrayList<>(nodesStack);
        }

        return null;
    }

    private static <U, E> void dfsInner(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, Map<BuchiState<E>, Color> colors, BuchiState<E> from, Deque<BuchiState<E>> nodesStack) {
        if (colors.get(from) == Color.BLACK || colors.get(from) == Color.GREY) {
            return;
        }

        dfsInner(colors, from, () -> {
            for (Map.Entry<U, BuchiState<E>> entry: automate.getTransitions().get(from).entries()) {
                if (accepting.contains(entry.getValue()) && colors.get(entry.getValue()) == Color.GREY) {
                    nodesStack.add(entry.getValue());
                    throw new RuntimeException("");
                } else {
                    dfsInner(automate, accepting, colors, entry.getValue(), nodesStack);
                }
            }
        }, nodesStack);
    }

    private static <U, E> void dfsOuter(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, Map<BuchiState<E>, Color> colors, Map<BuchiState<E>, Color> colors2, BuchiState<E> from, Deque<BuchiState<E>> nodesStack) {
        if (colors.get(from) == Color.BLACK || colors.get(from) == Color.GREY) {
            return;
        }

        dfsInner(colors, from, () -> {
            for (Map.Entry<U, BuchiState<E>> entry: automate.getTransitions().get(from).entries()) {
                if (accepting.contains(from)) {
                    final Map<BuchiState<E>, Color> colorsB = new HashMap<>(colors);
                    colors.clear();
                    colors.putAll(colors2);
                    dfsInner(automate, accepting, colors, entry.getValue(), nodesStack);
                    colors.putAll(colorsB);
                }
                if (accepting.contains(entry.getValue()) && colors.get(entry.getValue()) == Color.GREY) {
                    nodesStack.add(entry.getValue());
                    throw new RuntimeException("");
                } else {
                    dfsInner(automate, accepting, colors, entry.getValue(), nodesStack);
                }
            }
        }, nodesStack);
    }

    private static <E> void dfsInner(Map<BuchiState<E>, Color> colors, BuchiState<E> item, Runnable body, Deque<BuchiState<E>> nodesStack) {
        nodesStack.add(item);
        colors.put(item, Color.GREY);
        body.run();
        colors.put(item, Color.BLACK);
        assert Objects.equals(nodesStack.pop(), item);
    }

    public static BuchiAutomate<Integer, Set<Prop>> of(Formula ltlFormula) {
        // TODO: implement

        return null;
    }

    private static void expand(Integer nextTag,
                               Set<LtlBuchiState> nodes,
                               Set<Formula> newFormulas,
                               Set<Formula> oldFormulas,
                               Set<Formula> next,
                               Set<LtlBuchiState> incoming) {
        if (newFormulas.isEmpty()) {
            LtlBuchiState q = nodes.stream().filter(s -> Objects.equals(s.getNext(), next) && Objects.equals(s.getNow(), oldFormulas)).findFirst().orElse(null);
            if (Objects.nonNull(q)) {
                q.getIncoming().addAll(incoming);
            } else {
                LtlBuchiState n = new LtlBuchiState(nextTag++, incoming, oldFormulas, next);
                nodes.add(n);
                expand(nextTag, nodes, next, Collections.emptySet(), Collections.emptySet(), Collections.singleton(n));
            }
        } else {
            Formula f = newFormulas.stream().findFirst().orElse(null);

        }
    }
}
