dev:
	./gradlew run

test_get_all:
	curl localhost:8080/todos

test_get_by_id:
	curl localhost:8080/todo/1

test_create:
	curl --location --request PUT 'http://localhost:8080/todo' \
        --header 'Content-Type: text/plain' \
        --data-raw '{
            "message": "helloworld",
            "status": "incomplete"
        }'

test_update:
    curl --location --request POST 'http://sy-g15:8080/todo/1' \
        --header 'Content-Type: text/plain' \
        --data-raw '{
            "message": "updated message 123",
            "status": "incomplete"
        }'

dist:
	./gradlew buildFatJar

start:
	java -jar build/libs/netty-hello-world.jar
