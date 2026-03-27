up:
	docker compose -p kafka-ui up -d

down:
	docker compose -p kafka-ui down

build:
	docker compose -p kafka-ui up --build -d

# Hapus semua container + volume (fresh start)
clean:
	docker compose -p kafka-ui down -v --remove-orphans
