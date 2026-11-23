-- Senhas de teste (todas): Senha@2026
 
BEGIN;

-- ---------------------------------------------------------------- ENDEREÇOS
-- 1-5 proprietários | 6-12 imóveis | 13-14 fiadores
INSERT INTO Endereco (cep, estado, cidade, bairro, numero, rua, complemento) VALUES
('89201000', 'SC', 'Joinville', 'Centro',          100, 'Rua Nove de Março',          'Apto 101'),
('89202000', 'SC', 'Joinville', 'Anita Garibaldi', 250, 'Rua Anita Garibaldi',         NULL),
('89203000', 'SC', 'Joinville', 'Costa e Silva',   310, 'Rua Otto Pfuetzenreuter',     NULL),
('89204000', 'SC', 'Joinville', 'América',         420, 'Rua João Colin',              'Sala 10'),
('89205000', 'SC', 'Joinville', 'Saguaçu',         505, 'Rua Dona Francisca',          'Casa'),
('89206000', 'SC', 'Joinville', 'Bom Retiro',      600, 'Rua Tenente Antônio João',    'Apto 302'),
('89207000', 'SC', 'Joinville', 'Santo Antônio',   715, 'Rua Prudente de Moraes',      'Apto 45'),
('89208000', 'SC', 'Joinville', 'Glória',          128, 'Rua Blumenau',                NULL),
('89209000', 'SC', 'Joinville', 'Atiradores',      900, 'Rua Visconde de Taunay',      'Cobertura'),
('89210000', 'SC', 'Joinville', 'Floresta',        233, 'Rua Quinze de Novembro',      'Casa 2'),
('89211000', 'SC', 'Joinville', 'Bucarein',        740, 'Rua Max Colin',               'Loft 12'),
('89212000', 'SC', 'Joinville', 'Boa Vista',       1010,'Avenida Santos Dumont',       'Sala 506'),
('89213000', 'SC', 'Joinville', 'Iririú',          66,  'Rua Itaiópolis',              NULL),
('89214000', 'SC', 'Joinville', 'Jardim Iririú',   312, 'Rua Ministro Calógeras',      NULL);

-- ------------------------------------------------------------ PROPRIETÁRIOS
INSERT INTO Proprietario (tipo, cpf, cnpj, email, senha, dados_bancario, nome, razao_social, id_endereco) VALUES
('F', '44771278202', NULL, 'joao.silva@email.com',      'Senha@2026', 'Bco:1 Ag:1234 Cc:56789-0',    'João da Silva',       NULL,                        1),
('F', '26197306948', NULL, 'maria.souza@email.com',     'Senha@2026', 'Bco:341 Ag:4321 Cc:98765-4',  'Maria Souza',         NULL,                        2),
('F', '52393849912', NULL, 'carlos.pereira@email.com',  'Senha@2026', 'Bco:237 Ag:567 Cc:123456-X',  'Carlos Pereira',      NULL,                        3),
('J', NULL, '22175294000125', 'contato@joinvilleimoveis.com', 'Senha@2026', 'Bco:104 Ag:2020 Cc:777888-1', NULL, 'Joinville Empreendimentos Ltda', 4),
('J', NULL, '83960310000185', 'financeiro@saguacupar.com',    'Senha@2026', 'Bco:260 Ag:1 Cc:4455667-8',   NULL, 'Saguaçu Participações S/A',       5);

INSERT INTO TelefoneProprietario (telefone, id_proprietario) VALUES
('47991110001', 1), ('4734220001', 1),
('47991110002', 2),
('47991110003', 3), ('47991110033', 3),
('4734220004', 4), ('47991110004', 4),
('4734220005', 5);

-- --------------------------------------------------------------------- IMÓVEIS
-- 1 Apto (loc) | 2 Apto (loc) | 3 Casa (venda) | 4 Cobertura (venda)
-- 5 Geminado (venda) | 6 Loft (loc) | 7 Sala comercial (loc)
INSERT INTO Imovel (id_tipo_imovel, id_endereco, id_proprietario, area_total, qtd_dormitorios, qtd_banheiros, qtd_vagas, ano_construcao, descricao, valor, status) VALUES
(5,  6, 1,  78.5, 2, 1, 1, 2018, 'Apartamento com 2 quartos, sacada com churrasqueira e vaga coberta.',   2800.00, 'disponível para locação'),
(5,  7, 2,  64.0, 2, 1, 1, 2015, 'Apartamento ensolarado, próximo ao centro, com armários planejados.',   2200.00, 'disponível para locação'),
(1,  8, 1, 180.0, 3, 2, 2, 2010, 'Casa térrea com quintal amplo, edícula e piscina.',                  520000.00, 'disponível para venda'),
(6,  9, 3, 210.0, 4, 3, 3, 2020, 'Cobertura duplex com terraço, vista panorâmica e 3 vagas.',           980000.00, 'disponível para venda'),
(2, 10, 2, 120.0, 3, 2, 2, 2023, 'Casa geminada recém-construída, ótimo acabamento.',                  395000.00, 'disponível para venda'),
(8, 11, 3,  42.0, 1, 1, 1, 2021, 'Loft moderno, mobiliado, com pé-direito duplo.',                       3100.00, 'disponível para locação'),
(10,12, 4,  55.0, 0, 1, 1, 2019, 'Sala comercial em prédio moderno, com recepção e ar-condicionado.',    4500.00, 'disponível para locação');

-- --------------------------------------------------------------------- FOTOS

INSERT INTO Foto (imagem, tipo, id_imovel) VALUES
(pg_read_binary_file('C:/imagensBanco/apartamento.jpg'), 'Sala', 1),
(pg_read_binary_file('C:/imagensBanco/apartamento2.jpg'), 'Entrada', 2),
(pg_read_binary_file('C:/imagensBanco/casa.jpg'), 'Fachada', 3),
(pg_read_binary_file('C:/imagensBanco/cobertura.jpg'), 'Terraço', 4),
(pg_read_binary_file('C:/imagensBanco/cobertura_cozinha.jpg'), 'Cozinha', 4),
(pg_read_binary_file('C:/imagensBanco/cobertura_quarto.jpg'), 'Quarto de Casal', 4),
(pg_read_binary_file('C:/imagensBanco/cobertura_banheiro.jpg'), 'Banheiro', 4),
(pg_read_binary_file('C:/imagensBanco/geminado.jpg'), 'Frente', 5),
(pg_read_binary_file('C:/imagensBanco/loft.jpg'), 'Entrada', 6),
(pg_read_binary_file('C:/imagensBanco/salacomercial.jpg'), 'Recepção', 7);

-- ---------------------------------------------------------------------- CLIENTES
INSERT INTO Cliente (renda_mensal, profissao, id_tipo_imovel, cpf, email, senha, nome) VALUES
( 9500.00, 'Engenheiro',             5, '67539892269', 'ana.lima@email.com',        'Senha@2026', 'Ana Lima'),
( 6200.00, 'Professora',             5, '20107136481', 'bruno.alves@email.com',     'Senha@2026', 'Bruno Alves'),
( 7800.00, 'Analista de sistemas',   5, '49265548606', 'carla.mendes@email.com',    'Senha@2026', 'Carla Mendes'),
(11200.00, 'Médico',                 8, '72576386326', 'diego.rocha@email.com',     'Senha@2026', 'Diego Rocha'),
(18000.00, 'Empresário',             1, '16227586927', 'elisa.martins@email.com',   'Senha@2026', 'Elisa Martins'),
(14500.00, 'Advogado',               2, '64485497305', 'fabio.nunes@email.com',     'Senha@2026', 'Fábio Nunes'),
(25000.00, 'Arquiteta',              6, '33053041979', 'gabriela.costa@email.com',  'Senha@2026', 'Gabriela Costa'),
( 8300.00, 'Contador',              10, '14026066215', 'henrique.dias@email.com',   'Senha@2026', 'Henrique Dias');

INSERT INTO TelefoneCliente (telefone, id_cliente) VALUES
('47992220001', 1), ('47992220002', 2), ('47992220003', 3), ('47992220033', 3),
('47992220004', 4), ('47992220005', 5), ('47992220006', 6), ('47992220007', 7),
('47992220077', 7), ('47992220008', 8);

-- --------------------------------------------------------------------- CORRETORES
INSERT INTO Corretor (nome, email, senha, telefone, cpf, percentual_comissao, data_admissao, creci) VALUES
('Rafael Teixeira', 'rafael.teixeira@imobiliaria.com', 'Senha@2026', '47993330001', '19228514264', 0.05, '2019-03-04', '12345-F/SC'),
('Patrícia Gomes',  'patricia.gomes@imobiliaria.com',  'Senha@2026', '47993330002', '56562351065', 0.055, '2020-08-17', '23456-F/SC'),
('Lucas Ferreira',  'lucas.ferreira@imobiliaria.com',  'Senha@2026', '47993330003', '54900893420', 0.045, '2021-02-01', '34567-F/SC'),
('Juliana Barros',  'juliana.barros@imobiliaria.com',  'Senha@2026', '47993330004', '17500669127', 0.06, '2022-06-13', '45678-F/SC');

-- ----------------------------------------------------------------------- FIADORES
INSERT INTO Fiador (cpf, nome, id_endereco, comprovante_renda) VALUES
('35840992933', 'Roberto Almeida', 13, convert_to('Comprovante de renda - Roberto Almeida', 'UTF8')),
('15184715606', 'Sandra Vieira',   14, convert_to('Comprovante de renda - Sandra Vieira',   'UTF8'));

INSERT INTO TelefoneFiador (telefone, id_fiador) VALUES
('47994440001', 1), ('47994440011', 1), ('47994440002', 2);

-- ------------------------------------------------------------------- PUBLICAÇÕES
-- (sem fotos: id_foto_capa fica nulo). Feitas enquanto os imóveis estão disponíveis.
INSERT INTO Publicacao (id_imovel, id_foto_capa, titulo, tipo_publicacao, data, acessos) VALUES
(1, 1, 'Apartamento 2 quartos no Bom Retiro',          'L', '2024-04-20', 134),
(2, 2, 'Apartamento ensolarado no Santo Antônio',      'L', '2025-01-05',  98),
(3, 3, 'Casa com piscina na Glória',                   'V', '2026-07-20', 215),
(4, 4, 'Cobertura duplex nos Atiradores',              'V', '2026-10-01', 187),
(5, 8, 'Geminado novo na Floresta',                    'V', '2026-09-25', 143),
(6, 9, 'Loft mobiliado no Bucarein',                   'L', '2026-01-30', 121),
(7, 10, 'Sala comercial na Boa Vista',                  'L', '2026-10-10',  76);

-- ----------------------------------------------------------------------- VISITAS
-- (corretor: no máx. uma visita por hora; imóvel idem). Visitas dos imóveis que
-- depois são alugados/vendidos são arquivadas pelos triggers dos contratos.
INSERT INTO Visita_Agendada (data, horario, id_corretor, id_cliente, id_imovel) VALUES
('2024-05-10', '10:00', 1, 1, 1),
('2025-01-15', '14:00', 2, 2, 2),
('2026-08-05', '10:00', 1, 5, 3),
('2026-08-12', '15:00', 1, 7, 3),
('2026-10-05', '11:00', 4, 6, 5),
('2026-02-10', '16:00', 3, 4, 6),
-- imóveis ainda disponíveis (potenciais contratos)
('2026-10-28', '10:00', 3, 7, 4),
('2026-11-14', '14:00', 3, 8, 4),
('2026-11-21', '10:00', 1, 1, 4),
('2026-11-10', '09:00', 2, 2, 7),
('2026-11-12', '16:00', 2, 6, 7),
('2026-11-17', '11:00', 4, 5, 7);

-- ------------------------------------------------------------ CONTRATOS DE LOCAÇÃO
-- L1: apto 1, garantia FIADOR, desde 2024-06 (36 meses)
INSERT INTO Contrato_Locacao (prazo_em_meses, garantia, valor_comissao, dia_vencimento, data_inicio, valor_aluguel, id_imovel, id_cliente, id_corretor, id_fiador) VALUES
(36, 'FIADOR', 2800.00, 5, '2024-06-01', 2800.00, 1, 1, 1, 1);

INSERT INTO Reajuste_Aluguel (data, novo_valor, indice_utilizado, id_locacao) VALUES
('2025-06-05', 2950.00, 'IPCA', 1),
('2026-06-05', 3050.00, 'IGPM', 1);
UPDATE Contrato_Locacao SET valor_aluguel = 3050.00 WHERE id_locacao = 1;

-- L2: apto 2, seguro fiança, rescindido em 2025-11 -> imóvel volta a ficar disponível
INSERT INTO Contrato_Locacao (prazo_em_meses, garantia, valor_comissao, dia_vencimento, data_inicio, valor_aluguel, id_imovel, id_cliente, id_corretor) VALUES
(12, 'SEGURO FIANCA', 2200.00, 10, '2025-02-01', 2200.00, 2, 2, 2);

UPDATE Contrato_Locacao
SET status_contrato = 'ENCERRADO', data_rescisao = '2025-11-20', motivo = 'Mudança de cidade', multa = 1100.00
WHERE id_locacao = 2;

-- nova publicação, nova visita e novo contrato (L3) para o mesmo apto 2
INSERT INTO Publicacao (id_imovel, titulo, tipo_publicacao, data, acessos) VALUES
(2, 'Apartamento reformado no Santo Antônio', 'L', '2025-12-01', 64);

INSERT INTO Visita_Agendada (data, horario, id_corretor, id_cliente, id_imovel) VALUES
('2025-12-20', '09:00', 2, 3, 2);

-- L3: apto 2, depósito caução
INSERT INTO Contrato_Locacao (prazo_em_meses, garantia, valor_caucao, valor_comissao, dia_vencimento, data_inicio, valor_aluguel, id_imovel, id_cliente, id_corretor) VALUES
(24, 'DEPOSITO CAUCAO', 4600.00, 2300.00, 10, '2026-01-01', 2300.00, 2, 3, 2);

-- L4: loft, garantia FIADOR
INSERT INTO Contrato_Locacao (prazo_em_meses, garantia, valor_comissao, dia_vencimento, data_inicio, valor_aluguel, id_imovel, id_cliente, id_corretor, id_fiador) VALUES
(30, 'FIADOR', 3100.00, 15, '2026-03-01', 3100.00, 6, 4, 3, 2);

-- ------------------------------------------------------------- CONTRATOS DE VENDA
INSERT INTO Contrato_Venda (data, valor_venda, valor_sinal, forma_pagamento, valor_financiado, valor_comissao, id_cliente, id_proprietario, id_imovel, id_corretor) VALUES
('2026-09-18', 515000.00, 50000.00, 'Financiamento', 300000.00, 25750.00, 5, 1, 3, 1),
('2026-10-20', 395000.00, NULL,     'À vista',       NULL,      19750.00, 6, 2, 5, 4);

COMMIT;