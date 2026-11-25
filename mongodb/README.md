# Instruções para rodar a aplicação com banco de dados MongoDB

1. Rode *mangod* no terminal para subir o servidor do MongoDB (o executável deve estar nas variáveis de ambiente do sistema).

2. Rode *mongosh --eval "db.runCommand({ping:1})"* em outro terminal para verificar se o server está operando corretamente (mangosh também deve estar nas variáveis de ambiente). A resposta do server deve ser *{ ok: 1 }*.

3. As imagens utilizadas devem estar em *C:/imagensBanco/*.

4. Navegue até o diretório em que está o arquivo *populacao-mongo.js* e rode o comando *mongosh "mongodb://localhost:27017/imobiliaria" populacao-mongo.js* para criar e popular a base de dados. Ao final, deve aparecer no terminal uma listagem das coleções com a quantidade de elementos inseridos em cada coleção. Pode rodar de novo quando quiser: o script apaga as coleções e recria tudo.

5. No projeto, o *src/main/resources/application.properties* já está com *spring.profiles.active=mongo* e *spring.mongodb.uri=mongodb://localhost:27017/imobiliaria*. Não precisa mudar nada se o MongoDB está na sua máquina na porta padrão.

6. Agora é só subir a aplicação.
