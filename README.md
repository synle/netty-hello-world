# Simple netty server

## Server
```
./gradlew run
```

## Test Curls
```
curl http://localhost:8080/

curl http://localhost:8080/todos

curl http://localhost:8080/todo/1
```

```
curl --location --request PUT 'http://localhost:8080/todo' \
--header 'Content-Type: text/plain' \
--data-raw '{
    "message": "helloworld",
    "status": "incomplete"
}'
```

```
curl --location --request GET 'http://localhost:8080/todos' \
--header 'Content-Type: text/plain' \
--data-raw '{
    "message": "helloworld",
    "status": "incomplete"
}'
```


## Build Fat Jar
```
./gradlew buildFatJar
```

## Run from Fat Jar
```
java -jar build/libs/netty-hello-world.jar
```


