.PHONY: help server client run clean

help:
	@echo "Alvos disponiveis:"
	@echo "  make server  - roda somente o servidor"
	@echo "  make client  - roda somente o cliente"
	@echo "  make run     - roda servidor e cliente juntos"
	@echo "  make clean   - para os containers do banco e apaga o volume"

server:
	cd server && mvn spring-boot:run

client:
	cd client && mvn compile exec:java

run:
	cd server && mvn spring-boot:run & \
	SERVER_PID=$$!; \
	trap "kill $$SERVER_PID 2>/dev/null" EXIT INT TERM; \
	sleep 5; \
	cd client && mvn compile exec:java; \
	kill $$SERVER_PID 2>/dev/null

clean:
	docker-compose down -v
