package ru.clevertec.banking.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class FileReaderUtils {
    private final String CLASSPATH = "classpath";
    private final String FILES = "files";

    public String readFile(String path) throws IOException {
        return Files.readString(
                Path.of(ResourceUtils
                        .getFile(CLASSPATH.concat(":").concat(FILES).concat(path))
                        .toURI()
                )
        );
    }
}
