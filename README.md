# Simple netty server

## Server
```
./gradlew run
```

## Test Curls
Refer to the Makefile for more examples

```
curl http://localhost:8080/

curl http://localhost:8080/todos

curl http://localhost:8080/todo/1
```


## Build Fat Jar
```
./gradlew buildFatJar
```

## Run from Fat Jar
```
java -jar build/libs/netty-hello-world.jar
```


