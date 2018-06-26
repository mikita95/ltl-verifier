package automate;

import diagram.Diagram;
import diagram.StateMachine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class Variable {
    public String name;
    private boolean isVolatile;

    public static Variable of(final String name) {
        return Variable.of(name, false);
    }

    public static AbstractMap.SimpleEntry<Variable, Integer> parseDeclaration(@Nullable final String declaration) {
        final String[] decl = StringUtils.removeEnd(declaration
                        .replaceFirst("volatile ", "")
                        .replaceFirst("bool ", ""),
                ";")
                .split("\\s+=\\s+");
        return new AbstractMap.SimpleEntry<>(
                Variable.of(decl[0], declaration.startsWith("volatile")),
                Integer.parseInt(decl[1]));
    }

    public static Map<Variable, Integer> fromDiagram(final Diagram d) {
        return Optional.ofNullable(d)
                .map(Diagram::getData)
                .map(diagram.Data::getStateMachine)
                .map(StateMachine::getVariable)
                .orElse(Collections.emptyList())
                .stream()
                .map(diagram.Variable::getDeclaration)
                .map(Variable::parseDeclaration)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
