package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class StateMachine extends DiagramElement  {
    @JacksonXmlElementWrapper(localName = "event", useWrapping = false)
    private List<Event> event;
    @JacksonXmlElementWrapper(localName = "variable", useWrapping = false)
    private List<Variable> variable;
    @JacksonXmlProperty(localName = "autoreject")
    private boolean autoReject;
}
