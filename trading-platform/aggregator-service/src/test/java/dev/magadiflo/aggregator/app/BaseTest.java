package dev.magadiflo.aggregator.app;

import org.junit.jupiter.api.BeforeAll;
import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@MockServerTest
@AutoConfigureWebTestClient
@SpringBootTest(properties = {
        "external.services.customer.base-url=http://localhost:${mockServerPort}",
        "external.services.stock.base-url=http://localhost:${mockServerPort}"
})
abstract class BaseTest {

    private static final Path TEST_RESOURCES_PATH = Paths.get("src/test/resources");

    @Autowired
    protected WebTestClient client;

    protected MockServerClient mockServerClient;

    @BeforeAll
    static void beforeAll() {
        ConfigurationProperties.disableLogging(true);
    }

    protected String resourceToString(String relativePath) {
        try {
            return Files.readString(TEST_RESOURCES_PATH.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
