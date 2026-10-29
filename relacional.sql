CREATE DOMAIN TIPO_VALOR AS NUMERIC(15,2);
CREATE DOMAIN TIPO_CPF AS CHAR(11);
CREATE DOMAIN TIPO_CNPJ AS CHAR(14);
CREATE DOMAIN TIPO_SENHA AS VARCHAR(72);

CREATE TABLE TipoImovel (
    id_tipo_imovel SERIAL,
    descricao VARCHAR(20),
    PRIMARY KEY (id_tipo_imovel)
);

INSERT INTO TipoImovel(descricao) VALUES 
    ('Casa'),
    ('Casa Geminada'),
    ('Edícula'),
    ('Bangalô'),
    ('Apartamento Padrão'),
    ('Cobertura'),
    ('Studio'),
    ('Loft'),
    ('Giardino'),
    ('Sala Comercial'),
    ('Andar Corrido'),
    ('Galpão'),
    ('Chácara'),
    ('Terreno');
    
CREATE TABLE Endereco (
    id_endereco SERIAL,
    cep CHAR(8) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    cidade VARCHAR(30) NOT NULL,
    bairro VARCHAR(30) NOT NULL,
    numero INT NOT NULL,
    rua VARCHAR(50) NOT NULL,
    complemento VARCHAR(30),
    PRIMARY KEY (id_endereco)
);

CREATE TABLE Proprietario (
    id_proprietario SERIAL,
    tipo CHAR(1) NOT NULL, -- 'F' ou 'J'
    cpf TIPO_CPF UNIQUE,
    cnpj TIPO_CNPJ UNIQUE,
    email VARCHAR(50) UNIQUE NOT NULL,
    senha TIPO_SENHA NOT NULL,
    dados_bancario VARCHAR(50) NOT NULL,
    nome VARCHAR(50),
    razao_social VARCHAR(50),
    id_endereco INT NOT NULL,
    PRIMARY KEY (id_proprietario),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco),
    CHECK (
        (tipo = 'F' AND cpf IS NOT NULL AND nome IS NOT NULL AND cnpj IS NULL)
        OR
        (tipo = 'J' AND cnpj IS NOT NULL AND razao_social IS NOT NULL AND cpf IS NULL)
    )
);

CREATE TABLE TelefoneProprietario (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_proprietario INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario),
    UNIQUE (telefone, id_proprietario)
);

CREATE TABLE Imovel (
    id_imovel SERIAL,
    id_tipo_imovel INT NOT NULL,
    id_endereco INT NOT NULL,
    id_proprietario INT NOT NULL,
    area_total FLOAT NOT NULL,
    qtd_dormitorios INT NOT NULL,
    qtd_banheiros INT NOT NULL,
    qtd_vagas INT NOT NULL, 
    ano_construcao INT NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    valor TIPO_VALOR NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (
        status IN ('disponível para venda', 'disponível para locação',
                   'vendido', 'alugado', 'indisponível')
    ),
    PRIMARY KEY (id_imovel),
    FOREIGN KEY (id_tipo_imovel) REFERENCES TipoImovel (id_tipo_imovel),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario)
);

CREATE TABLE Foto (
    id_foto SERIAL,
    imagem BYTEA NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    id_imovel INT NOT NULL,
    PRIMARY KEY (id_foto),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    UNIQUE (id_foto, id_imovel)
);

CREATE TABLE Publicacao (
    id_publicacao SERIAL,
    id_imovel INT NOT NULL,
    id_foto_capa INT,
    titulo VARCHAR(50) NOT NULL,
    tipo_publicacao CHAR(1) NOT NULL, -- 'V'enda ou 'L'ocacao
    ativa BOOLEAN NOT NULL,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    acessos INT NOT NULL DEFAULT 0 CHECK (acessos >= 0),
    PRIMARY KEY (id_publicacao),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    -- Para garantir que a foto de capa é do imóvel
    FOREIGN KEY (id_foto_capa, id_imovel) REFERENCES Foto (id_foto, id_imovel)
);

CREATE TABLE Corretor (
    id_corretor SERIAL,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    senha TIPO_SENHA NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    cpf TIPO_CPF UNIQUE NOT NULL,
    percentual_comissao FLOAT NOT NULL,
    data_admissao DATE NOT NULL,
    creci VARCHAR(12) NOT NULL,
    PRIMARY KEY (id_corretor)
);

CREATE TABLE Cliente (
    id_cliente SERIAL,
    renda_mensal FLOAT NOT NULL,
    profissao VARCHAR(30) NOT NULL,
    id_tipo_imovel INT NOT NULL,
    cpf TIPO_CPF UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(50) NOT NULL,
    nome VARCHAR(50),
    PRIMARY KEY (id_cliente),
    FOREIGN KEY (id_tipo_imovel) REFERENCES TipoImovel (id_tipo_imovel)
);

CREATE TABLE TelefoneCliente (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_cliente INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    UNIQUE(telefone, id_cliente)
);

CREATE TABLE Visita_Agendada (
    id_visita SERIAL,
    data DATE NOT NULL,
    horario TIME NOT NULL,
    id_corretor INT NOT NULL,
    id_cliente INT NOT NULL,
    id_imovel INT NOT NULL,
    potencial_contrato BOOLEAN NOT NULL DEFAULT,
    PRIMARY KEY (id_visita),
    FOREIGN KEY (id_corretor) REFERENCES Corretor (id_corretor),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel)
);

CREATE TABLE Contrato_Venda (
    id_venda SERIAL,
    data DATE NOT NULL,
    valor_venda TIPO_VALOR NOT NULL,
    valor_sinal TIPO_VALOR,
    forma_pagamento VARCHAR(20) NOT NULL,
    valor_financiado TIPO_VALOR,
    valor_comissao TIPO_VALOR NOT NULL,
    id_cliente INT NOT NULL,
    id_proprietario INT NOT NULL,
    id_imovel INT NOT NULL,
    id_corretor INT NOT NULL,
    PRIMARY KEY (id_venda),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    FOREIGN KEY (id_corretor) REFERENCES Correotr (id_corretor)
);

CREATE TABLE Contrato_Locacao (
    id_locacao SERIAL,
    prazo_em_meses INT NOT NULL,
    garantia VARCHAR(30) CHECK (
        garantia IN ('FIADOR', 'SEGURO FIANCA', 'DEPOSITO CAUCAO')
    ), -- "FIADOR", "SEGURO FIANCA" ou "DEPOSITO CAUCAO"
    valor_caucao TIPO_VALOR,
    valor_comissao TIPO_VALOR NOT NULL,
    status_contrato VARCHAR(15) NOT NULL DEFAULT 'ATIVO' CHECK (
        status_contrato IN ('ATIVO', 'ENCERRADO')
    ), -- "ATIVO" ou "ENCERRADO"
    dia_vencimento INT NOT NULL,
    data_inicio DATE NOT NULL,
    valor_aluguel TIPO_VALOR NOT NULL,
    -- No caso de "ENCERRADO"
    data_rescisao DATE,
    multa TIPO_VALOR,
    motivo VARCHAR(50),
    CHECK (
        (status_contrato = 'ENCERRADO' AND data_rescisao IS NOT NULL AND motivo IS NOT NULL)
        OR
        (status_contrato = 'ATIVO' AND data_rescisao IS NULL AND multa IS NULL AND motivo IS NULL)
    ),
    CHECK (multa IS NULL OR multa >= 0),
    CHECK (
        (garantia = 'DEPOSITO CAUCAO' AND valor_caucao IS NOT NULL)
        OR
        (garantia IS DISTINCT FROM 'DEPOSITO CAUCAO' AND valor_caucao IS NULL)
    ),
    CHECK (
        dia_vencimento BETWEEN 1 AND 28
    ),
    CHECK (
        data_recisao IS NULL OR data_rescisao >= data_inicio
    ),
    CHECK (
        prazo_em_meses > 0
    ),
    -------------------------
    id_imovel INT NOT NULL,
    id_cliente INT NOT NULL,
    id_corretor INT NOT NULL,
    PRIMARY KEY (id_locacao),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    FOREIGN KEY (id_corretor) REFERENCES Corretor (id_corretor)
);

CREATE TABLE Fiador (
    id_fiador SERIAL,
    cpf TIPO_CPF NOT NULL UNIQUE,
    nome VARCHAR(50) NOT NULL,
    id_endereco INT NOT NULL,
    comprovante_renda BYTEA NOT NULL,
    PRIMARY KEY (id_fiador),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco)
);

CREATE TABLE TelefoneFiador (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_fiador INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_fiador) REFERENCES Fiador (id_fiador),
    UNIQUE (telefone, id_fiador)
);

CREATE TABLE Fiador_Contrato (
    id_locacao INT NOT NULL,
    id_fiador INT NOT NULL,
    PRIMARY KEY (id_locacao, id_fiador),
    FOREIGN KEY (id_locacao) REFERENCES Contrato_Locacao (id_locacao),
    FOREIGN KEY (id_fiador) REFERENCES Fiador (id_fiador)
);

CREATE TABLE Reajuste_Aluguel (
    id_reajuste SERIAL,
    data DATE NOT NULL,
    novo_valor TIPO_VALOR NOT NULL,
    indice_utilizado VARCHAR(10) NOT NULL, -- como "IGPM" ou "IPCA"
    id_locacao INT NOT NULL,
    PRIMARY KEY (id_reajuste),
    FOREIGN KEY (id_locacao) REFERENCES Contrato_Locacao (id_locacao)
);

CREATE TABLE Recibo_Aluguel (
    id_recibo SERIAL,
    desconto TIPO_VALOR DEFAULT 0.00,
    acrescimo TIPO_VALOR DEFAULT 0.00,
    valor_liquido TIPO_VALOR NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor_aluguel TIPO_VALOR NOT NULL,
    mes INT NOT NULL CHECK (mes BETWEEN 1 AND 12),
    ano INT NOT NULL,
    id_locacao INT NOT NULL,
    PRIMARY KEY (id_recibo),
    FOREIGN KEY (id_locacao) REFERENCES Contrato_Locacao (id_locacao),
    -- período
    UNIQUE (id_locacao, ano, mes)
);

/*
------ LOG DE MUDANCAS ------

- CPFs e CNPJs marcados com UNIQUE

- Na tabela Contrato_Locacao:

    - prazo_mes -> prazo_em_meses 
    - data_recisao -> data_rescisao
    - dia_vencimento agora vai de 1 a 28
    - adicionada verificacao para prazo_mes
    - data_rescisao deve ser maior que data_inicio
    - um contrato encerrado não necessariamente tem multa
    - a multa deve sempre ser NULL ou maior ou igual que zero 

- Na tabela Recibo_Aluguel:

    - adicionado check do mês
    - adicionado ano
    - todo recibo tem uma combinacao unica de id_locacao, ano e mes

- Tabela associativa Fiador_Contrato foi criada

- Comprovante_renda e id_locacao foram para a tabela associativa

- Adicionada tabela Publicacao

TRIGGERS:

- Para ser fiador, o contrato precisar estar ativo

- O CPF do locatário e fiador devem ser diferentes

*/

/*

-- 1. Inserir Endereços (Serão usados pelos proprietários e pelos imóveis)
INSERT INTO Endereco (cep, estado, cidade, bairro, numero, rua, complemento) VALUES
('89201000', 'Santa Catarina', 'Joinville', 'Centro', 100, 'Rua Nove de Março', 'Apto 101'),
('89202000', 'Santa Catarina', 'Joinville', 'Anita Garibaldi', 200, 'Rua Anita Garibaldi', NULL),
('89203000', 'Santa Catarina', 'Joinville', 'Costa e Silva', 300, 'Rua Otto Pfuetzenreuter', NULL),
('89204000', 'Santa Catarina', 'Joinville', 'América', 400, 'Rua João Colin', 'Sala 10'),
('89205000', 'Santa Catarina', 'Joinville', 'Saguaçu', 500, 'Rua Dona Francisca', 'Casa'),
('89206000', 'Santa Catarina', 'Joinville', 'Bom Retiro', 600, 'Rua Tenente Antônio João', NULL),
('89207000', 'Santa Catarina', 'Joinville', 'Santo Antônio', 700, 'Rua Prudente de Moraes', 'Cobertura');

SELECT * FROM ENDERECO;

-- 2. Inserir Proprietários (Um Pessoa Física e um Pessoa Jurídica)
-- Utilizam os endereços de ID 1 e 2
INSERT INTO Proprietario (tipo, cpf, cnpj, email, senha, dados_bancario, nome, razao_social, id_endereco) VALUES
('F', '81404462899', NULL, 'joao.silva@email.com', 'Abacate&2026', 'Banco do Brasil Ag 1234 CC 5678-9', 'João da Silva', NULL, 15),
('J', NULL, '85947080000140', 'contato@imobiliaria.com', 'Alface&2026', 'Itaú Ag 4321 CC 9876-5', NULL, 'Imobiliária S/A', 16);

INSERT INTO TelefoneProprietario(id_proprietario, telefone) VALUES
	(11, '9724678832'),
	(12, '2737559484'),
	(12, '7927144866');


-- 3. Inserir Imóveis
-- Utilizam os IDs de Endereço de 3 a 7, IDs de Proprietários 1 e 2, e os IDs de TipoImovel (1=Casa, 5=Apto, 10=Sala, 2=Geminada, 6=Cobertura)
INSERT INTO Imovel (id_tipo_imovel, id_endereco, id_proprietario, area_total, qtd_dormitorios, qtd_banheiros, qtd_vagas, ano_construcao, descricao, valor, status) VALUES
(1, 17, 11, 150.00, 3, 2, 2, 2015, 'Linda casa espaçosa no Costa e Silva.', 450000.00, 'disponível para venda'),
(5, 18, 11, 80.00, 2, 1, 1, 2020, 'Apartamento padrão bem localizado no América.', 2500.00, 'disponível para locação'),
(10, 19, 12, 45.00, 0, 1, 1, 2018, 'Sala comercial em prédio moderno.', 150000.00, 'disponível para venda'),
(2, 20, 12, 120.00, 3, 2, 2, 2023, 'Casa geminada recém construída, ótimo acabamento.', 350000.00, 'disponível para venda'),
(6, 21, 11, 200.00, 4, 3, 3, 2010, 'Cobertura com vista incrível para a cidade.', 850000.00, 'disponível para venda');

SELECT * FROM Imovel;

-- 4. Inserir Fotos (Capa para cada imóvel)
-- Utilizamos uma string hexadecimal fictícia (E'\\x...') para popular o campo BYTEA
INSERT INTO Foto (imagem, tipo, id_imovel) VALUES
(pg_read_binary_file('C:/imagensBanco/casa.jpg'), 'Fachada', 21),
(pg_read_binary_file('C:/imagensBanco/apartamento.jpg'), 'Sala', 22),
(pg_read_binary_file('C:/imagensBanco/salacomercial.jpg'), 'Recepção', 23),
(pg_read_binary_file('C:/imagensBanco/geminado.jpg'), 'Frente', 24),
(pg_read_binary_file('C:/imagensBanco/cobertura.jpg'), 'Terraço', 25),
(pg_read_binary_file('C:/imagensBanco/casa_cozinha.jpg'), 'Cozinha', 21),
(pg_read_binary_file('C:/imagensBanco/casa_quarto.jpg'), 'Quarto de Casal', 21),
(pg_read_binary_file('C:/imagensBanco/casa_banheiro.jpg'), 'Banheiro', 21);

SELECT * FROM FOTO;

-- 5. Inserir Publicações
-- A chave estrangeira composta de id_foto_capa exige que a foto pertença exatamente ao imóvel indicado
INSERT INTO Publicacao (id_imovel, id_foto_capa, titulo, tipo_publicacao, ativa, data, acessos) VALUES
(21, 1, 'Casa aconchegante no Costa e Silva', 'V', TRUE, '2023-10-01', 125),
(22, 2, 'Lindo Apartamento no América', 'L', TRUE, '2023-10-05', 45),
(23, 3, 'Oportunidade Comercial no Saguaçu', 'V', TRUE, '2023-10-10', 300),
(24, 4, 'Geminado novo no Bom Retiro', 'V', TRUE, '2023-10-15', 78),
(25, 5, 'Cobertura Espetacular no Santo Antônio', 'V', TRUE, '2023-09-20', 540);

SELECT * FROM PUBLICACAO;

*/