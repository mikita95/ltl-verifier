package kripke;

import automate.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class StateKripke {
    public Set<Variable> variables;
}
