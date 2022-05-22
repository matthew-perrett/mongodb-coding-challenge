package com.github.matthewperrett;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import java.io.InputStreamReader;
import java.util.Map;

public class FlattenJson {

    private static final String KEY_SEPARATOR = ".";
    private static final Map<String, Boolean> WRITER_OPTIONS = Map.of(JsonGenerator.PRETTY_PRINTING, true);
    private static final JsonWriterFactory WRITER_FACTORY = Json.createWriterFactory(WRITER_OPTIONS);

    public static void main(String[] args) {
        JsonReader reader = Json.createReader(new InputStreamReader(System.in));
        JsonObject json = reader.readObject();

        FlattenJson flattenJson = new FlattenJson();
        JsonObject flatten = flattenJson.flatten(json);
        flattenJson.print(flatten);
    }

    public JsonObject flatten(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        for (String key : json.keySet()) {
            JsonValue value = json.get(key);

            if (value instanceof JsonArray) {
                throw new IllegalArgumentException("JSON Arrays not supported");
            } else if (value instanceof JsonObject child) {
                JsonObject flattenedChild = flatten(child);
                flattenedChild.forEach((k, v) -> builder.add(key + KEY_SEPARATOR + k, v));
            } else {
                builder.add(key, value);
            }
        }

        return builder.build();
    }

    private void print(JsonObject json) {
        JsonWriter writer = WRITER_FACTORY.createWriter(System.out);
        writer.write(json);
        writer.close();
    }
}
