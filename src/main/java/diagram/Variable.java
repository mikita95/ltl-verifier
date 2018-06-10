package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public final class Variable extends DiagramElement  {
    @JacksonXmlProperty(localName = "decl", isAttribute = true)
    private String declaration;
}
