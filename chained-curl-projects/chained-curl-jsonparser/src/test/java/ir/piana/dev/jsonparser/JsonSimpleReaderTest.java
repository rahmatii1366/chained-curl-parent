package ir.piana.dev.jsonparser;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import ir.piana.dev.TestConfig;
import ir.piana.dev.jsonparser.json.JsonParser;
import ir.piana.dev.jsonparser.json.JsonTarget;
import ir.piana.dev.jsonparser.json.JsonTargetBuilder;
import ir.piana.dev.jsonparser.json.validators.JsonValidationRuntimeException;
import ir.piana.dev.jsonparser.json.validators.ValidatorProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@Import(TestConfig.class)
class JsonSimpleReaderTest {

    @Autowired
    private JsonParser jsonParser;

    @Autowired
    private ValidatorProvider validatorProvider;

    @Autowired
    @Qualifier("messageSource")
    MessageSource messageSource;

    @Test
    void jsonTargetTest(@Value("classpath:test.json") Resource resource) throws IOException {
        try {
            JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
                            """
                            [0].addresses[0].title2 string !nn
                            [0].addresses[0].title string nn equal-to#ali
                            [0].addresses[0].plaque
                            [0].addresses[1].title string
                            [0].addresses[1].plaque number
                            [0].family string
                            """, true);

            assertThat(jsonTarget.asInteger("[0].addresses[0].title2")).isNull();
            assertThat(jsonTarget.asString("[0].addresses[0].title")).isEqualTo("home");
            assertThat(jsonTarget.asString("[0].addresses[1].title")).isEqualTo("work");
            assertThat(jsonTarget.asInteger("[0].addresses[1].plaque")).isEqualTo(2);
            assertThat(jsonTarget.asString("[0].family")).isEqualTo("Doi");
            assertThat(jsonTarget.toStringOnDemand()).isEqualTo("[" +
                    /*  */"{\"addresses\":[" +
                    /*      */"{\"title2\":null,\"title\":\"home\",\"plaque\":1}," +
                    /*      */"{\"title\":\"work\",\"plaque\":2}]," +
                    /*  */"\"family\":\"Doi\"}" +
                    "]");
        } catch (JsonValidationRuntimeException e) {
//            messageSource.getMessage(e.getMessageKey(), null, Locale.ENGLISH);
            System.out.println(e.getMessage());
        }

    }

    @Test
    void jsonCrawlerTest(@Value("classpath:test.json") Resource resource) throws IOException {

        JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
                null, true);

        assertThat(jsonTarget.asInteger("")).isNull();
        assertThat(jsonTarget.asInteger("[0].addresses[0].title2")).isNull();
        assertThat(jsonTarget.asString("[0].addresses[0].title")).isEqualTo("home");
        assertThat(jsonTarget.asString("[0].addresses[1].title")).isEqualTo("work");
        assertThat(jsonTarget.asInteger("[0].addresses[1].plaque")).isEqualTo(2);
        assertThat(jsonTarget.asString("[0].family")).isEqualTo("Doi");
        assertThat(jsonTarget.toStringOnDemand()).isEqualTo("[" +
                /*  */"{\"addresses\":[" +
                /*      */"{\"title2\":null,\"title\":\"home\"}," +
                /*      */"{\"title\":\"work\",\"plaque\":2}]," +
                /*  */"\"family\":\"Doi\"}" +
                "]");
    }

    @Test
    void emptyJsonCrawlerTest(@Value("classpath:test.json") Resource resource) throws IOException {
        Buffer buffer = Buffer.buffer(resource.getContentAsByteArray());
        JsonArray json = (JsonArray) buffer.toJson();
        JsonObject copy = json.getJsonObject(0).copy();


        JsonTarget jsonTarget = jsonParser.fromBytes(resource.getInputStream().readAllBytes(),
                null, true);

        JsonTargetBuilder builder = JsonTargetBuilder.edit(jsonTarget);
        builder.replace("[0].parent[0].father", "ali");
        JsonTarget build = builder.build();


        assertThat(jsonTarget.toString()).isEqualTo("[" +
                "{\"name\":\"John\",\"family\":\"Doi\",\"addresses\":[" +
                "{\"title\":\"home\",\"plaque\":1}," +
                "{\"title\":\"work\",\"plaque\":2}]," +
                "\"parent\":{\"is-legal\":false,\"man\":null,\"name\":\"hasan\"," +
                "\"national-code\":\"0082315310\",\"work\":{\"title\":\"ibm\",\"responsibility\":\"cto\"}}}]");
    }
}
