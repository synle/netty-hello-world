# Simple netty server

## Server
```
./gradlew run
```

## Test Curls
```
curl localhost:8080/json

curl localhost:8080/plaintext

curl localhost:8080/
```


## Build Fat Jar
```
./gradlew buildFatJar
```

## Run from Fat Jar
```
java -jar build/libs/netty-hello-world.jar
```
