package ru.clevertec.banking.customer.testutil;

import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReaderUtil {
    private static final String CLASSPATH = "classpath";
    private static final String FILES = "__files";

    private FileReaderUtil() {
    }

    public static String readFile(String path) throws IOException {
        return Files.readString(
                Path.of(ResourceUtils
                                .getFile(CLASSPATH.concat(":").concat(FILES).concat(path))
                                .toURI()
                )
        );
    }
}
