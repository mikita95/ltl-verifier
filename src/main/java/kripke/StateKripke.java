package kripke;

import automate.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class StateKripke {
    public Set<Variable> variables;

    public String toString() {
        return variables.stream().map(Variable::toString).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateKripke that = (StateKripke) o;
        return Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables);
    }
}
