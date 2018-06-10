package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class Event extends DiagramElement  {
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;
    @JacksonXmlProperty(localName = "comment", isAttribute = true)
    private String comment;
}
