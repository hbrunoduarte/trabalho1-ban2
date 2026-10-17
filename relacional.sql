CREATE DOMAIN TIPO_VALOR AS NUMERIC(15,2);
CREATE DOMAIN TIPO_CPF AS CHAR(11);
CREATE DOMAIN TIPO_CNPJ AS CHAR(14);

CREATE TABLE Endereco (
    id_endereco SERIAL,
    cep CHAR(8) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    cidade VARCHAR(30) NOT NULL,
    bairro VARCHAR(30) NOT NULL,
    numero INT NOT NULL,
    rua VARCHAR(50) NOT NULL,
    complemento VARCHAR(10),
    PRIMARY KEY (id_endereco)
);

CREATE TABLE Proprietario (
    id_proprietario SERIAL,
    tipo CHAR(1) NOT NULL, -- 'F' ou 'J'
    cpf TIPO_CPF UNIQUE,
    cnpj TIPO_CNPJ UNIQUE,
    email VARCHAR(50),
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
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario)
);

CREATE TABLE Imovel (
    id_imovel SERIAL,
    tipo VARCHAR(20) NOT NULL,
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
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario)
);

CREATE TABLE Foto (
    id_foto SERIAL,
    imagem BYTEA,
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
    ativa BOOLEAN NOT NULL,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    acessos INT NOT NULL DEFAULT 0 CHECK (acessos >= 0),
    PRIMARY KEY (id_publicacao),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    -- Para garantir que a foto de capa é do imóvel
    FOREIGN KEY (id_foto_capa, id_imovel) REFERENCES Foto (id_foto, id_imovel),
    -- Para cada imovel ter só uma publicação
    UNIQUE (id_imovel)
);

CREATE TABLE Corretor (
    id_corretor SERIAL,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    cpf TIPO_CPF UNIQUE NOT NULL,
    percentual_comissao INT NOT NULL,
    data_admissao DATE NOT NULL,
    creci VARCHAR(10) NOT NULL,
    PRIMARY KEY (id_corretor)
);

CREATE TABLE Cliente (
    id_cliente SERIAL,
    renda_mensal INT NOT NULL,
    profissao VARCHAR(30) NOT NULL,
    tipo_interessado VARCHAR(50) NOT NULL,
    cpf TIPO_CPF UNIQUE NOT NULL,
    email VARCHAR(50) NOT NULL,
    nome VARCHAR(50),
    PRIMARY KEY (id_cliente)
);

CREATE TABLE TelefoneCliente (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_cliente INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente)
);

CREATE TABLE Visita_Agendada (
    id_visita SERIAL,
    data DATE NOT NULL,
    horario TIME NOT NULL,
    id_corretor INT NOT NULL,
    id_cliente INT NOT NULL,
    id_imovel INT NOT NULL,
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
    PRIMARY KEY (id_venda),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel)
);

CREATE TABLE Contrato_Locacao (
    id_locacao SERIAL,
    prazo_em_meses INT NOT NULL,
    garantia VARCHAR(30) CHECK (
        garantia IN ('FIADOR', 'SEGURO FIANCA', 'DEPOSITO CAUCAO')
    ), -- "FIADOR", "SEGURO FIANCA" ou "DEPOSITO CAUCAO"
    valor_caucao TIPO_VALOR,
    valor_comissao TIPO_VALOR NOT NULL,
    status_contrato VARCHAR(15) NOT NULL CHECK (
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
    PRIMARY KEY (id_locacao),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente)
);

CREATE TABLE Fiador (
    id_fiador SERIAL,
    cpf TIPO_CPF NOT NULL UNIQUE,
    nome VARCHAR(50) NOT NULL,
    id_endereco INT NOT NULL,
    PRIMARY KEY (id_fiador),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco)
);

CREATE TABLE TelefoneFiador (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_fiador INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_fiador) REFERENCES Fiador (id_fiador)
);

CREATE TABLE Fiador_Contrato (
    id_locacao INT NOT NULL,
    id_fiador INT NOT NULL,
    comprovante_renda VARCHAR(50) NOT NULL,
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