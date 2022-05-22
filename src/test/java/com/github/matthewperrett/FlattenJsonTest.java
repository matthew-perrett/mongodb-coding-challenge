package com.github.matthewperrett;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FlattenJsonTest {

    private FlattenJson flattenJson;

    @BeforeEach
    public void setup() {
        flattenJson = new FlattenJson();
    }

    @AfterEach
    public void cleanup() {
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void flatten_should_handle_empty_json_object() {
        String input = "{}";
        String output = "{}";

        JsonObject json = createJsonObject(input);

        JsonObject flatJson = flattenJson.flatten(json);

        assertEquals(flatJson, createJsonObject(output));
    }

    @Test
    public void flatten_should_handle_all_primitive_types() {
        String input = """
                { "a": "string", "b": 1.1, "c": true, "d": false, "e": null }""";
        String output = """
                { "a": "string", "b": 1.1, "c": true, "d": false, "e": null }""";

        JsonObject json = createJsonObject(input);

        JsonObject flatJson = flattenJson.flatten(json);

        assertEquals(flatJson, createJsonObject(output));
    }

    @Test
    public void flatten_should_flatten_nested_json_objects() {
        String input =
            """
            {
                "l1-number": 1,
                "l1": {
                    "l2-number": 2,
                    "l2": {
                    "l3-number": 3
                    }
                }
            }""";
        String output =
            """
            {
                "l1-number":1,
                "l1.l2-number":2,
                "l1.l2.l3-number":3
            }""";

        JsonObject json = createJsonObject(input);

        JsonObject flatJson = flattenJson.flatten(json);

        assertEquals(flatJson, createJsonObject(output));
    }

    @Test
    public void flatten_should_throw_IllegalArgumentException_for_json_arrays() {
        String input = """
                { "array": [1, 2, 3] }""";

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                JsonObject json = createJsonObject(input);
                flattenJson.flatten(json);
            }
        );

        assertEquals("JSON Arrays not supported", exception.getMessage());
    }

    @Test
    public void main_method_should_except_input_from_standard_in_and_format_correctly() {
        String input =
            """
            {
                "a": 1,
                "b": true,
                "c": {
                    "d": 3,
                    "e": "test"
                }
            }""";
        String output =
            """
            {
                "a": 1,
                "b": true,
                "c.d": 3,
                "c.e": "test"
            }""";

        ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        System.setOut(new PrintStream(systemOut));

        FlattenJson.main(null);

        assertEquals(output, systemOut.toString());
    }

    private JsonObject createJsonObject(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        return jsonReader.readObject();
    }
}
