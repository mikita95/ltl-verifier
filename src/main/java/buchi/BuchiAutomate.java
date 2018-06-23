package buchi;

import automate.Variable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import kripke.ModelKripke;
import kripke.StateKripke;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class BuchiAutomate<S, T> {
    private BuchiState<S> initState;
    private Map<BuchiState<S>, Multimap<T, BuchiState<S>>> transitions;
    private Set<Set<BuchiState<S>>> acceptingFamily;

    public Set<BuchiState<S>> nodes() {
        return transitions.keySet();
    }

    public boolean isGeneralized() {
        return acceptingFamily.size() > 1;
    }

    public BuchiAutomate<StateKripke, Set<Variable>> of(final ModelKripke modelKripke) {
        Map<StateKripke, BuchiState<StateKripke>> buchiStateByKripkeState = modelKripke.getStateKripkes().stream()
                .collect(Collectors.toMap(Function.identity(), BuchiState::of));


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

  /*  public <E> BuchiAutomate<E, Set<Prop>> variableAutomatonToPropAutomaton(BuchiAutomate<E, Set<Variable>> variableAutomate) {
        for (val entry: variableAutomate.getTransitions().entrySet()) {
            val state = entry.getKey();
            val map = entry.getValue();

            val builder = ImmutableMultimap.builder();
            map.entries().stream()
                    .forEach(edge -> {
                        builder.put(
                                edge.getKey().stream().map(v -> new Prop(v.getName())).collect(Collectors.toSet()),
                                edge.getValue()
                        );
                    });


        }

        variableAutomate.getTransitions().replaceAll(
                (k, trs) -> {
                    final Set<Prop> props = trs.entries().stream().map((entry) -> entry.getKey().stream().map(v -> new Prop(v.getName())).collect(Collectors.toSet()));
                }
        );
    }*/

    /*
    fun <E> variableAutomatonToPropAutomaton(variableAutomaton: BuchiAutomaton<E, Set<Variable>>)
    : BuchiAutomaton<E, Set<Prop>> {
    val transitions = variableAutomaton.transitions.mapValues { (_, trs) ->
        trs.map { (vars, toState) -> vars.map { Prop(it.name) }.toSet() to toState }
    }
    return BuchiAutomaton(variableAutomaton.initialState, transitions, variableAutomaton.acceptingFamily)
}

fun <A, B> crossIgnoringProps(aa: BuchiAutomaton<A, Set<Prop>>,
                              xx: BuchiAutomaton<B, Set<Prop>>,
                              propsIgnoredInXx: Set<Prop>)
    : BuchiAutomaton<Pair<BuchiState<A>, BuchiState<B>>, Set<Prop>> {

    val nodes = aa.nodes.flatMap { a ->
        xx.nodes.map { x ->
            BuchiState(a to x)
        }
    }

    val transitions = nodes.associate { ax ->
        val (a, x) = ax.tag
        ax to nodes.flatMap { by ->
            val (b, y) = by.tag
            val abTransitionLabels = aa.transitions[a]!!.filter { (_, to) -> to == b }.map { (label, _) -> label }
            val xyTransitionLabels = xx.transitions[x]!!.filter { (_, to) -> to == y }.map { (label, _) -> label }
            abTransitionLabels.filter { it - propsIgnoredInXx in xyTransitionLabels }.map { tag -> tag to by }
        }
    }

    val initialState = nodes.single { node ->
        val (a, x) = node.tag
        aa.initialState == a && xx.initialState == x
    }

    return BuchiAutomaton(
        initialState,
        transitions,
        xx.acceptingFamily.map { f -> nodes.filter { n -> n.tag.second in f }.toSet() }.toSet())
}
     */
}
