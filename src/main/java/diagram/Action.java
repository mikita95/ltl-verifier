package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class Action extends DiagramElement {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;
    @JacksonXmlProperty(localName = "comment", isAttribute = true)
    private String comment;
    @JacksonXmlProperty(localName = "synchro", isAttribute = true)
    private String synchro;
}
