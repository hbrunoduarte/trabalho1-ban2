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

CREATE TABLE TelefoneProprietario (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_proprietario INT NOT NULL,
    PRIMARY KEY (id_telefone),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario)
);

CREATE TABLE Proprietario (
    id_proprietario SERIAL,
    tipo CHAR(1) NOT NULL, -- 'F' ou 'J'
    cpf TIPO_CPF,
    cnpj TIPO_CNPJ,
    email VARCHAR(50),
    dados_bancario VARCHAR(50) NOT NULL,
    nome VARCHAR(50),
    razao_social VARCHAR(50),
    id_endereco INT NOT NULL,
    PRIMARY KEY (id_proprietario),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco)
    CHECK (
        (tipo = 'F' AND cpf IS NOT NULL AND nome IS NOT NULL AND cnpj IS NULL)
        OR
        (tipo = 'J' AND cnpj IS NOT NULL AND razao_social IS NOT NULL AND cpf IS NULL)
    )
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
    status VARCHAR(30) NOT NULL, -- disponível para venda, disponível para locação, vendido, alugado ou indisponível
    PRIMARY KEY (id_imovel),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco),
    FOREIGN KEY (id_proprietario) REFERENCES Proprietario (id_proprietario)
);

CREATE TABLE Foto (
    id_foto SERIAL,
    tipo VARCHAR(20) NOT NULL,
    id_imovel INT,
    PRIMARY KEY (id_foto),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel)
);

CREATE TABLE Corretor (
    id_corretor SERIAL,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    cpf TIPO_CPF NOT NULL,
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
    cpf TIPO_CPF NOT NULL,
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
    valor_sinal TIPO_VALOR NOT NULL,
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
    prazo_mes INT NOT NULL,
    garantia VARCHAR(30), -- "FIADOR", "SEGURO FIANCA" ou "DEPOSITO CAUCAO"
    valor_caucao TIPO_VALOR,
    valor_comissao TIPO_VALOR NOT NULL,
    status_contrato VARCHAR(15) NOT NULL, -- "ATIVO" ou "ENCERRADO"
    dia_vencimento INT NOT NULL,
    data_inicio DATE NOT NULL,
    valor_aluguel TIPO_VALOR NOT NULL,
    -- No caso de "ENCERRADO"
    data_recisao DATE,
    multa TIPO_VALOR,
    motivo VARCHAR(50),
    -------------------------
    id_imovel INT NOT NULL,
    id_cliente INT NOT NULL,
    PRIMARY KEY (id_locacao),
    FOREIGN KEY (id_imovel) REFERENCES Imovel (id_imovel),
    FOREIGN KEY (id_cliente) REFERENCES Cliente (id_cliente)
);

CREATE TABLE Fiador (
    id_fiador SERIAL,
    cpf TIPO_CPF NOT NULL,
    nome VARCHAR(50) NOT NULL,
    comprovante_renda VARCHAR(50) NOT NULL,
    id_endereco INT NOT NULL,
    id_locacao INT NOT NULL,
    PRIMARY KEY (id_fiador),
    FOREIGN KEY (id_endereco) REFERENCES Endereco (id_endereco),
    FOREIGN KEY (id_locacao) REFERENCES Contrato_Locacao (id_locacao)
);

CREATE TABLE TelefoneFiador (
    id_telefone SERIAL NOT NULL,
    telefone VARCHAR(13) NOT NULL,
    id_fiador INT NOT NULL,
    PRIMARY KEY (id_telefone),
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

-- OBS: no documento pede-se período, mas diz que o recibo é emitido mensalmente.
--      portanto, período foi representado como o atributo "mes"
CREATE TABLE Recibo_Aluguel (
    id_recibo SERIAL,
    desconto TIPO_VALOR DEFAULT 0.00,
    acrescimo TIPO_VALOR DEFAULT 0.00,
    valor_liquido TIPO_VALOR NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor_aluguel TIPO_VALOR NOT NULL,
    mes INT NOT NULL,
    id_locacao INT NOT NULL,
    PRIMARY KEY (id_recibo),
    FOREIGN KEY (id_locacao) REFERENCES Contrato_Locacao (id_locacao)
);