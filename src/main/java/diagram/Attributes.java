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
public final class Attributes extends DiagramElement {
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "event")
    private Event event;
    @JacksonXmlProperty(localName = "type")
    private String type;
    @JacksonXmlElementWrapper(localName = "action", useWrapping = false)
    private List<Action> action;
    @JacksonXmlProperty(localName = "code")
    private String code;
    @JacksonXmlProperty(localName = "guard")
    private String guard;
    @JacksonXmlElementWrapper(localName = "incoming", useWrapping = false)
    private List<Transition> incoming;
    @JacksonXmlElementWrapper(localName = "outgoing", useWrapping = false)
    private List<Transition> outgoing;
}
