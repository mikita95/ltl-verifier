package diagram;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    public Diagram parse(final Path xml) throws IOException {
        final JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        return new XmlMapper(xmlModule)
                .readValue(
                        StringUtils.toEncodedString(Files.readAllBytes(xml), StandardCharsets.UTF_8),
                        Diagram.class);
    }
}
