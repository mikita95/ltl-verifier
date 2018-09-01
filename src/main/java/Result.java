import buchi.BuchiState;
import kripke.StateKripke;
import lombok.val;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Result {
    private List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>> result;

    private Result(List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>> result) {
        this.result = result;
    }

    public static Result correct() {
        return new Result(null);
    }

    public static Result fail(List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>> result) {
        return new Result(result);
    }

    public String print() {
        if (result == null) {
            return "correct";
        }
        val last = result.get(result.size() - 1);
        result.remove(result.size() - 1);
        val prefix = StreamEx.of(result).dropWhile(p -> p != last).toList();
        List<BuchiState<Pair<BuchiState<StateKripke>, BuchiState<Integer>>>> cycle = StreamEx.of(result).takeWhile(p -> p != last).toList();
        if (cycle.size() == result.size()) {
            cycle = Collections.emptyList();
        } else {
            cycle.add(last);
        }
        final String path = prefix.stream().map(s -> s.getTag().getLeft().toString()).collect(Collectors.joining("\n"));
        final String answer = cycle.stream().map(s -> s.getTag().getLeft().toString()).collect(Collectors.joining("\n"));
        return "path:\n" + path + "\n cycle:\n" + answer;
    }
}
