package buchi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BuchiAutomateCycleFinder<E> {
    private Deque<BuchiState<E>> nodesStack;
    private Map<BuchiState<E>, Color> colors;
    private Map<BuchiState<E>, Color> colors2;

    private enum Color {
        WHITE,
        GREY,
        BLACK
    }

    public BuchiAutomateCycleFinder() {

    }

    public <T> List<BuchiState<E>> findReachableCycle(BuchiAutomate<E, T> buchiAutomate) {
        final Set<BuchiState<E>> accepting = buchiAutomate.getAcceptingFamily().stream().findFirst().orElse(null);

        nodesStack = new ArrayDeque<>();
        colors = buchiAutomate.nodes().stream().collect(Collectors.toMap(
                Function.identity(),
                k -> Color.WHITE
        ));
        colors2 = new HashMap<>(colors);

        try {
            dfsOuter(buchiAutomate, accepting, buchiAutomate.getInitState());
        } catch (Exception e) {
            return new ArrayList<>(nodesStack);
        }

        return null;
    }

    private <U> void dfsInner(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, BuchiState<E> from) {
        if (colors.get(from) == Color.BLACK || colors.get(from) == Color.GREY) {
            return;
        }

        nodesStack.push(from);
        colors.put(from, Color.GREY);
        for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
            if (accepting.contains(entry.getValue()) && colors.get(entry.getValue()) == Color.GREY) {
                nodesStack.add(entry.getValue());
                throw new RuntimeException("");
            } else {
                dfsInner(automate, accepting, entry.getValue());
            }
        }
        colors.put(from, Color.BLACK);
        assert Objects.equals(nodesStack.pop(), from);
    }

    private <U> void dfsOuter(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, BuchiState<E> from) {
        if (colors.get(from) == Color.BLACK || colors.get(from) == Color.GREY) {
            return;
        }

        nodesStack.push(from);
        colors.put(from, Color.GREY);
        for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
            if (accepting.contains(from)) {
                final Map<BuchiState<E>, Color> colorsB = new HashMap<>(colors);
                colors.clear();
                colors.putAll(colors2);
                dfsInner(automate, accepting, entry.getValue());
                colors.clear();
                colors.putAll(colorsB);
            }
            dfsOuter(automate, accepting, entry.getValue());
        }
        colors.put(from, Color.BLACK);
        assert Objects.equals(nodesStack.pop(), from);
    }
}
