package ru.clevertec.banking.deposit.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass

public class FileReaderUtil {
    private static final String CLASSPATH = "classpath";
    private static final String FILES = "__files";




    public static String readFile(String path) throws IOException {
        return Files.readString(
                Path.of(ResourceUtils
                        .getFile(CLASSPATH.concat(":").concat(FILES).concat(path))
                        .toURI()
                )
        );
    }
}
