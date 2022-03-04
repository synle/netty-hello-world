dev:
	./gradlew run

test:
	curl localhost:8080/

dist:
	./gradlew buildFatJar

start:
	java -jar build/libs/netty-hello-world.jar
