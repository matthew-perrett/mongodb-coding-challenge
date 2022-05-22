# MongoDB Coding Challenge

## Build

Requirements:

-   Java 17 or greater
-   Maven

Build with:

```
mvn clean package
```

This will create a jar file located at `target/FlattenJson.jar`

## Run

Json can be flattered by piping JSON into the executable:

```
cat src/test/resources/test.json | java -jar target/FlattenJson.jar
```
