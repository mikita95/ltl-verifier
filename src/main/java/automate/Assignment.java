package automate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Setter
public class Assignment {
    private Variable variable;
    private Integer value;

    public static List<Assignment> makeAssignments(final String code, final Set<Variable> variableSet) {
        if (StringUtils.isEmpty(code))
            return Collections.emptyList();
        return Arrays.stream(code.split("\\r?\\n"))
                .filter(StringUtils::isNoneEmpty)
                .flatMap(line -> {
                    final String[] assign = StringUtils.removeEnd(line.trim(), ";").split("\\s+=\\s+");
                    return variableSet.stream()
                            .filter(v -> v.getName().equals(assign[0]))
                            .map(v -> Assignment.of(v, Integer.valueOf(assign[1])));
                }).collect(Collectors.toList());
    }
}
