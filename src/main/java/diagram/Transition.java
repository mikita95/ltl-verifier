package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class Transition extends DiagramElement  {
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    private int id;
}
