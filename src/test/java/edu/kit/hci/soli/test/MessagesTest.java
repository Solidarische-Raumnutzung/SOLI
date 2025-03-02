package edu.kit.hci.soli.test;

import edu.kit.hci.soli.SoliApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = {"dev", "test"})
public class MessagesTest {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\d+)}");

    @Test
    public void testMatchingKeys() {
        // We deliberately avoid ResurceBundle and MessageSource,
        // since those provide fallbacks to english,
        // and we want to ensure that all languages have the same keys.
        var bundles = Stream.of("", "_de")
                .map(s -> "/messages" + s + ".properties")
                .map(SoliApplication.class::getResourceAsStream)
                .map(Objects::requireNonNull)
                .map(MessagesTest::readProperties)
                .toList();
        var reference = bundles.getFirst();
        for (Properties bundle : bundles) {
            if (bundle == reference) continue;
            assertEquals(reference.keySet(), bundle.keySet(), "Mismatched keys");
            for (String key : reference.stringPropertyNames()) {
                var otherValue = bundle.getProperty(key);
                assertEquals(findPlaceholders(reference.getProperty(key)), findPlaceholders(otherValue), "Mismatched placeholders for key " + key);
            }
        }
    }

    @SneakyThrows
    private static Properties readProperties(InputStream is) {
        Properties props = new Properties();
        try (InputStream i = is; InputStreamReader r = new InputStreamReader(i, UTF_8)) {
            props.load(r);
        }
        return props;
    }

    private static Set<String> findPlaceholders(String value) {
        return PLACEHOLDER.matcher(value).results().map(m -> m.group(1)).collect(Collectors.toSet());
    }
}
