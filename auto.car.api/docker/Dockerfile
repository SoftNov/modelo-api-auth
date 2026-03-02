# Dockerfile para MySQL (ponto de partida)
# Instruções para subir o banco com docker-compose:
# 1) Usando o docker-compose (v1):
#    docker-compose -f docker/docker-compose.yml up -d
# 2) Usando o plugin docker compose (v2):
#    docker compose -f docker/docker-compose.yml up -d
# 3) Ver logs do serviço DB:
#    docker-compose -f docker/docker-compose.yml logs -f db
#    ou
#    docker compose -f docker/docker-compose.yml logs -f db
# 4) Entrar no container (bash):
#    docker exec -it auto-car-db bash
# 5) Acessar o cliente MySQL dentro do container:
#    mysql -u auto_car_user -p auto_car
#    (use a senha definida em docker-compose.yml: auto_car_pass)
# 6) Para aplicar scripts SQL na primeira inicialização, coloque os arquivos
#    .sql na pasta script-db/ — o docker-compose já monta essa pasta em
#    /docker-entrypoint-initdb.d e o MySQL executará os scripts automaticamente
#    na criação do volume de dados.
# 7) Para reconstruir o serviço se alterar o Dockerfile (quando usar imagem custom):
#    docker-compose -f docker/docker-compose.yml build db && docker-compose -f docker/docker-compose.yml up -d
#    ou
#    docker compose -f docker/docker-compose.yml build db && docker compose -f docker/docker-compose.yml up -d

FROM mysql:8.0
LABEL maintainer="auto.car"

# Se preferir construir uma imagem com os scripts SQL inclusos,
# descomente a linha abaixo e ajuste o contexto de build para a raiz do repositório
# COPY ../script-db/*.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
