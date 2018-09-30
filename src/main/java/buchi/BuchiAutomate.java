package buchi;

import automate.Variable;
import com.google.common.collect.*;
import kripke.ModelKripke;
import kripke.StateKripke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.val;
import ltl.*;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public boolean isMultiAccepting() {
        return acceptingFamily.size() > 1;
    }

    public static BuchiAutomate<StateKripke, Set<Variable>> of(final ModelKripke modelKripke) {
        Map<StateKripke, BuchiState<StateKripke>> buchiStateByKripkeState = modelKripke.getStates().stream()
                .collect(Collectors.toMap(Function.identity(), BuchiState::new));


        Map<BuchiState<StateKripke>, Multimap<Set<Variable>, BuchiState<StateKripke>>> transitions = modelKripke.getStates().stream().collect(Collectors.toMap(
                buchiStateByKripkeState::get,
                k -> makeTransition(modelKripke.getTransitions().get(k), buchiStateByKripkeState)
        ));

        BuchiAutomate<StateKripke, Set<Variable>> result = new BuchiAutomate<>();
        result.transitions = transitions;
        result.initState = buchiStateByKripkeState.getOrDefault(modelKripke.getInitStateKripke(), null);
        result.acceptingFamily = ImmutableSet.of(ImmutableSet.copyOf(buchiStateByKripkeState.values()));
        return result;
    }

    private static Multimap<Set<Variable>, BuchiState<StateKripke>> makeTransition(final Set<StateKripke> states, final Map<StateKripke, BuchiState<StateKripke>> buchiStateByKripkeState) {
        ImmutableMultimap.Builder<Set<Variable>, BuchiState<StateKripke>> builder = ImmutableMultimap.builder();
        states.forEach(toState ->
                builder.put(
                        toState.getVariables(),
                        buchiStateByKripkeState.get(toState)
                )
        );
        return builder.build();
    }

    public static <E> BuchiAutomate<E, Set<Prop>> variableAutomatonToPropAutomaton(BuchiAutomate<E, Set<Variable>> variableAutomate) {
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
        if (!buchiAutomate.isMultiAccepting()) {
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

    private static <K> List<Set<K>> allSubsets(Set<K> set) {
        return StreamEx.of(set).foldLeft(Collections.singletonList(new HashSet<>()),
                (p, c) -> BuchiAutomate.<Set<K>>plusL(p, p.stream().map(n -> plus(n, c)).collect(Collectors.toList())));
    }

   /* fun <T> allSubsets(set: Set<T>): List<Set<T>> =
            set.fold(listOf(emptySet<T>())) { prevSets, item -> prevSets + prevSets.map { it + item } }*/

    private static <U, E> void dfsInner(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, Map<BuchiState<E>, Color> colors, BuchiState<E> from, Deque<BuchiState<E>> nodesStack) {
        if (colors.get(from) == Color.BLACK || colors.get(from) == Color.GREY) {
            return;
        }

        dfsInner(colors, from, () -> {
            for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
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
            for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
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
        Set<LtlBuchiState> nodes = new HashSet<>();

        int nextTag = 1;
        LtlBuchiState init = new LtlBuchiState(0, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        expand(nextTag, nodes, ImmutableSet.of(ltlFormula), Collections.emptySet(), Collections.emptySet(), ImmutableSet.of(init));
        Set<Prop> allVariables = ltlFormula.varsJava();
        Map<LtlBuchiState, Set<Prop>> apInNow = nodes.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        n -> n.getNow().stream()
                                .filter(t -> t instanceof Prop)
                                .map(t -> (Prop) t)
                                .collect(Collectors.toSet())));
        Map<LtlBuchiState, Set<Prop>> notNegatedInNow = nodes.stream().collect(Collectors.toMap(
           Function.identity(),
           n -> minus(
                   allVariables,
                   n.getNow().stream()
                           .filter(t -> t instanceof Prop)
                           .map(t -> (Prop) t)
                           .collect(Collectors.toSet())
                   )
        ));

        Map<LtlBuchiState, List<Set<Prop>>> l = nodes.stream().collect(Collectors.toMap(
                Function.identity(),
                n -> {
                    Set<Prop> boundsDiff = minus(notNegatedInNow.get(n), apInNow.get(n));
                    return allSubsets(boundsDiff).stream().map(d -> plus(d, apInNow.get(n))).collect(Collectors.toList());
                }
        ));

        Map<LtlBuchiState, Set<LtlBuchiState>> transitions = plus(nodes, init).stream().collect(Collectors.toMap(
                Function.identity(),
                n -> nodes.stream().filter(t -> t.getIncoming().contains(n)).collect(Collectors.toSet())
        ));

        Set<Set<BuchiState<Integer>>> acceptingFamily = ltlFormula.subformulasJava().stream()
                .filter(t -> t instanceof Until)
                .map(Until.class::cast)
                .map(u -> nodes.stream()
                        .filter(n -> !n.getNow().contains(u) || n.getNow().contains(u.right()))
                        .map(r -> new BuchiState<>(r.getTag()))
                        .collect(Collectors.toSet())).collect(Collectors.toSet());
        Map<BuchiState<Integer>, Multimap<Set<Prop>, BuchiState<Integer>>> bTrans = transitions.entrySet().stream().collect(Collectors.toMap(e -> new BuchiState<>(e.getKey().getTag()), e -> {
            Set<AbstractMap.SimpleEntry<Set<Prop>, BuchiState<Integer>>> t = e.getValue().stream().flatMap(s ->
                    l.getOrDefault(s, Collections.emptyList())
                            .stream()
                            .map(b -> new AbstractMap.SimpleEntry<>(b, new BuchiState<>(s.getTag())))).collect(Collectors.toSet());
            ImmutableMultimap.Builder<Set<Prop>, BuchiState<Integer>> builder = new ImmutableListMultimap.Builder<>();
            t.forEach(h -> builder.put(h.getKey(), h.getValue()));
            return builder.build();
        }));

        BuchiAutomate<Integer, Set<Prop>> result = new BuchiAutomate<>();
        BuchiState<Integer> initTemp = new BuchiState<>(init.getTag());
        result.setInitState(initTemp);
        result.setTransitions(bTrans);
        Set<Set<BuchiState<Integer>>> temp = new HashSet<>();
        temp.add(nodes.stream().map(s -> new BuchiState<>(s.getTag())).collect(Collectors.toSet()));
        result.setAcceptingFamily(
                acceptingFamily.isEmpty() ? temp : acceptingFamily
        );

        return result;
    }

    private static <X> Set<X> add(Set<X> set, X element) {
        return Streams.concat(
                set.stream(),
                Stream.of(element)
        ).collect(Collectors.toSet());
    }

    private static <X> Set<X> remove(Set<X> set, X element) {
        return set.stream().filter(element::equals).collect(Collectors.toSet());
    }

    private static Set<Formula> next1Rule(Formula f) {
        if (f instanceof Until || f instanceof Release) {
            return Stream.of(f).collect(Collectors.toSet());
        }
        if (f instanceof Or) {
            return Collections.emptySet();
        }
        throw new IllegalArgumentException();
    }


    private static Set<Formula> curr1Rule(Formula f) {
        if (f instanceof Until) {
            return Stream.of(((Until) f).left()).collect(Collectors.toSet());
        }
        if (f instanceof Release) {
            return Stream.of(((Release) f).right()).collect(Collectors.toSet());
        }
        if (f instanceof Or) {
            return Stream.of(((Or) f).right()).collect(Collectors.toSet());
        }
        throw new IllegalArgumentException();
    }

    private static Set<Formula> curr2Rule(Formula f) {
        if (f instanceof Until) {
            return Stream.of(((Until) f).right()).collect(Collectors.toSet());
        }
        if (f instanceof Release) {
            return Stream.of(((Release) f).left(), ((Release) f).right()).collect(Collectors.toSet());
        }
        if (f instanceof Or) {
            return Stream.of(((Or) f).left()).collect(Collectors.toSet());
        }
        throw new IllegalArgumentException();
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
            Set<Formula> toNew = remove(newFormulas, f);
            Set<Formula> toOld = add(oldFormulas, f);
            if (f instanceof TRUE || f instanceof FALSE || f instanceof Prop || f instanceof Not && f.getBody() instanceof Prop) {
                if (!(f instanceof FALSE) && !oldFormulas.contains(f.negation())) {
                    expand(nextTag, nodes, toNew, toOld, next, incoming);
                }
            } else if (f instanceof And) {
                Set<Formula> lrSet = Stream.of(((And) f).left(), ((And) f).right()).collect(Collectors.toSet());
                lrSet.removeAll(toOld);
                lrSet.addAll(toNew);
                expand(nextTag, nodes, lrSet, toOld, next, incoming);
            } else if (f instanceof Next) {
                expand(nextTag, nodes, toNew, toOld, add(next, ((Next) f).body()), incoming);
            } else if (f instanceof Or || f instanceof Until || f instanceof Release) {
                Set<Formula> set1 = curr1Rule(f);
                set1.removeAll(oldFormulas);
                set1.addAll(toNew);

                Set<Formula> set2 = curr2Rule(f);
                set1.removeAll(oldFormulas);
                set1.addAll(toNew);

                expand(nextTag, nodes, set1, toOld, Stream.concat(next.stream(), next1Rule(f).stream()).collect(Collectors.toSet()), incoming);
                expand(nextTag, nodes, set2, toOld, next, incoming);
            }
        }
    }

    public static <A, B> BuchiAutomate<Pair<BuchiState<A>, BuchiState<B>>, Set<Prop>> cross(BuchiAutomate<A, Set<Prop>> aa, BuchiAutomate<B, Set<Prop>> xx, Set<Prop> props) {
        val nodes = aa.nodes().stream().flatMap(a -> xx.nodes().stream().map(x -> new BuchiState<>(Pair.of(a, x)))).collect(Collectors.toSet());
        Map<BuchiState<Pair<BuchiState<A>, BuchiState<B>>>, Multimap<Set<Prop>, BuchiState<Pair<BuchiState<A>, BuchiState<B>>>>> transitions = new HashMap<>();

        for (val ax: nodes) {
            Pair<BuchiState<A>, BuchiState<B>> pair = ax.getTag();
            Set<Pair<Set<Prop>, BuchiState<Pair<BuchiState<A>, BuchiState<B>>>>> to = nodes.stream().flatMap(
                    by -> {
                        Pair<BuchiState<A>, BuchiState<B>> t = by.getTag();
                        val abL = aa.getTransitions().get(pair.getKey()).entries().stream().filter(e -> e.getValue().equals(t.getKey())).map(Map.Entry::getKey).collect(Collectors.toSet());
                        val xyL = xx.getTransitions().get(pair.getValue()).entries().stream().filter(e -> e.getValue().equals(t.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
                        return abL.stream().filter(sp -> xyL.containsAll(minus(sp, props))).map(tag -> Pair.of(tag, by));
                    }
            ).collect(Collectors.toSet());

            ImmutableMultimap.Builder<Set<Prop>, BuchiState<Pair<BuchiState<A>, BuchiState<B>>>> builder = new ImmutableListMultimap.Builder<>();
            to.forEach(p -> builder.put(p.getLeft(), p.getRight()));
            transitions.put(ax, builder.build());
        }

        val init = nodes.stream().filter(
                node -> {
                    val pair = node.getTag();
                    return aa.getInitState().equals(pair.getKey()) && xx.getInitState().equals(pair.getValue());
                }
        ).findFirst().orElse(null);
        Set<Set<BuchiState<Pair<BuchiState<A>, BuchiState<B>>>>> acc = xx.getAcceptingFamily().stream().map(
                f -> nodes.stream().filter(n -> f.contains(n.getTag().getRight())).collect(Collectors.toSet())
        ).collect(Collectors.toSet());

        BuchiAutomate<Pair<BuchiState<A>, BuchiState<B>>, Set<Prop>> result = new BuchiAutomate<>();
        result.initState = init;
        result.transitions = transitions;

        return result;
    }

    private static <A> Set<A> minus(Set<A> x, Set<A> y) {
        if (x == null || y == null) {
            return new HashSet<>();
        }
        Set<A> result = new HashSet<>(x);
        result.removeAll(y);
        return result;
    }

    private static <A> Set<A> plus(Set<A> x, Set<A> y) {
        if (x == null || y == null) {
            return new HashSet<>();
        }
        Set<A> result = new HashSet<>(x);
        result.addAll(y);
        return result;
    }

    private static <A> Set<A> plus(Set<A> x, A y) {
        if (x == null || y == null) {
            return new HashSet<>();
        }
        Set<A> result = new HashSet<>(x);
        result.add(y);
        return result;
    }

    private static <A> List<A> plusL(List<A> x, List<A> y) {
        if (x == null || y == null) {
            return new ArrayList<>();
        }
        List<A> result = new ArrayList<>(x);
        result.addAll(y);
        return result;
    }
}
