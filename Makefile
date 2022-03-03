dev:
	./gradlew run

test:
	curl localhost:8080/

dist:
	./gradlew buildFatJar
