up:
	docker compose -p microkafva down
	docker compose -p microkafva up -d

down:
	docker compose -p microkafva down

build:
	docker compose -p microkafva up --build -d

# Hapus semua container + volume (fresh start)
clean:
	docker compose -p microkafva down -v --remove-orphans
