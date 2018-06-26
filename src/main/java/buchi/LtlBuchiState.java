package buchi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ltl.Formula;

import java.util.Set;

@ToString
@Getter
@Setter
public class LtlBuchiState extends BuchiState<Integer> {
    public int id;
    public Set<LtlBuchiState> incoming;
    public Set<Formula> now;
    public Set<Formula> next;

    public LtlBuchiState(int id, Set<LtlBuchiState> incoming, Set<Formula> now, Set<Formula> next) {
        super(id);
        this.id = id;
        this.incoming = incoming;
        this.now = now;
        this.next = next;
    }
}
