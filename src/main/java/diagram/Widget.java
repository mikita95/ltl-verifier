package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class Widget extends DiagramElement {
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private int id;
    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;
    @JacksonXmlProperty(localName = "attributes")
    private Attributes attributes;
}
