package diagram;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@JacksonXmlRootElement(localName = "diagram")
public final class Diagram extends DiagramElement  {
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "data")
    private Data data;
    @JacksonXmlElementWrapper(localName = "widget", useWrapping = false)
    private List<Widget> widget;
}
