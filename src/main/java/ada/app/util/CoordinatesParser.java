package ada.app.util;

import ada.app.model.Box;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CoordinatesParser {
    private final Logger logger = LoggerFactory.getLogger(CoordinatesParser.class);

    private final ObjectMapper objectMapper;

    public CoordinatesParser() {
        this.objectMapper = new ObjectMapper();
    }

    public List<Box> parseJson(String filePath) {
        Resource resourceFile = new FileSystemResource(filePath);
        try {
            logger.info("event=parsing_input, source={}", resourceFile.getFile().getPath());
            return objectMapper.readValue(resourceFile.getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, Box.class));
        } catch (IOException e) {
            logger.error("event=parsing_input_failure, reason=io_exception, [{}]", e.getMessage());
        }
        logger.info("event=parsing_input_failure, source={} [Returning empty list]", resourceFile);
        return List.of();
    }
}
