.PHONY: help server client run clean

help:
	@echo "Alvos disponiveis:"
	@echo "  make server  - roda somente o servidor (API REST, porta 8081)"
	@echo "  make client  - roda somente o cliente (desktop Swing)"
	@echo "  make run     - roda servidor e cliente juntos (servidor em background)"
	@echo "  make clean   - para os containers do banco e apaga o volume (dados perdidos)"

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
