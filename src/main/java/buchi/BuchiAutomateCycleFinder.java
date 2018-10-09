package buchi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BuchiAutomateCycleFinder<E> {
    private Deque<BuchiState<E>> nodesStack;
    private Map<BuchiState<E>, Mark> marks;
    private Map<BuchiState<E>, Mark> nodesMarks;

    private enum Mark {
        WHITE,
        GREY,
        BLACK
    }

    public BuchiAutomateCycleFinder() {

    }

    public <T> List<BuchiState<E>> cycle(BuchiAutomate<E, T> buchiAutomate) {
        final Set<BuchiState<E>> accepting = buchiAutomate.getAcceptingFamily().stream().findFirst().orElse(null);

        nodesStack = new ArrayDeque<>();
        marks = buchiAutomate.nodes().stream().collect(Collectors.toMap(
                Function.identity(),
                k -> Mark.WHITE
        ));
        nodesMarks = new HashMap<>(marks);

        try {
            findAccepted(buchiAutomate, accepting, buchiAutomate.getInitState());
        } catch (Exception e) {
            return new ArrayList<>(nodesStack);
        }

        return null;
    }

    private <U> void dfsCycle(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, BuchiState<E> from) {
        if (marks.get(from) == Mark.BLACK || marks.get(from) == Mark.GREY) {
            return;
        }
        nodesStack.push(from);
        marks.put(from, Mark.GREY);
        for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
            if (accepting.contains(entry.getValue()) && marks.get(entry.getValue()) == Mark.GREY) {
                nodesStack.add(entry.getValue());
                throw new RuntimeException("");
            } else {
                dfsCycle(automate, accepting, entry.getValue());
            }
        }
        marks.put(from, Mark.BLACK);
        assert Objects.equals(nodesStack.pop(), from);
    }

    private <U> void findAccepted(BuchiAutomate<E, U> automate, Set<BuchiState<E>> accepting, BuchiState<E> from) {
        if (marks.get(from) == Mark.BLACK || marks.get(from) == Mark.GREY) {
            return;
        }

        nodesStack.push(from);
        marks.put(from, Mark.GREY);
        for (Map.Entry<U, BuchiState<E>> entry : automate.getTransitions().get(from).entries()) {
            if (accepting.contains(from)) {
                final Map<BuchiState<E>, Mark> colorsB = new HashMap<>(marks);
                marks.clear();
                marks.putAll(nodesMarks);
                dfsCycle(automate, accepting, entry.getValue());
                marks.clear();
                marks.putAll(colorsB);
            }
            findAccepted(automate, accepting, entry.getValue());
        }
        marks.put(from, Mark.BLACK);
        assert Objects.equals(nodesStack.pop(), from);
    }
}
