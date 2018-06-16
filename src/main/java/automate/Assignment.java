package automate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data(staticConstructor = "of")
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class Assignment {
    private Variable variable;
    private Integer value;

    public static List<Assignment> makeAssignments(@Nullable final String code, final Set<Variable> variableSet) {
        if (StringUtils.isEmpty(code))
            return Collections.emptyList();
        return Arrays.stream(code.split("\\r?\\n"))
                .filter(StringUtils::isEmpty)
                .flatMap(line -> {
                    final String[] assign = StringUtils.removeEnd(line.trim(), ";").split("\\s+=\\s+");
                    return variableSet.stream()
                            .filter(v -> v.getName().equals(assign[0]))
                            .map(v -> Assignment.of(v, Integer.valueOf(assign[1])));
                }).collect(Collectors.toList());
    }
}
