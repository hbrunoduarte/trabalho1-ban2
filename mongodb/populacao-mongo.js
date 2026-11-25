// =====================================================================================
// População do MongoDB da imobiliária — esquema alinhado aos DAOs *Mongo
//
// Uso:   mongosh "mongodb://localhost:27017/imobiliaria" populacao-mongo.js
// Senha de teste (todos os usuários): Senha@2026
//
// Convenções (as mesmas de MongoDaoBase.java):
//   - _id inteiro; ids novos vêm da coleção "contadores" (substitui o SERIAL do Postgres)
//   - datas = string "yyyy-MM-dd"; horários = string "HH:mm"
//   - dinheiro = Decimal128 (NumberDecimal); foto e comprovante = BinData
//   - uma coleção por entidade que o servidor trata "por id" (endereço, telefone, foto...),
//     porque os controllers criam/buscam/removem essas entidades isoladamente
//   - o que no Postgres eram triggers (status do imóvel, arquivar visitas, desativar publicação,
//     gerar recibos) agora roda nos DAOs; aqui os dados já estão no estado final coerente
// =====================================================================================

const fs = require("fs");

// pasta das imagens (a mesma C:/imagensBanco/ do populacao.sql). Se o arquivo não existir,
// a foto fica com imagem = null (o FotoController responde 204 nesse caso).
const PASTA_IMAGENS = "C:/imagensBanco/";
function img(nome) {
  try {
    return BinData(0, fs.readFileSync(PASTA_IMAGENS + nome).toString("base64"));
  } catch (e) {
    return null;
  }
}
function texto(s) { return BinData(0, Buffer.from(s, "utf8").toString("base64")); }
const N = (s) => NumberDecimal(s);

const COLECOES = [
  "tipos_imovel", "enderecos", "proprietarios", "clientes", "corretores", "fiadores",
  "telefones", "imoveis", "fotos", "publicacoes", "visitas", "contratos_locacao",
  "contratos_venda", "reajustes", "recibos_aluguel", "contadores"
];
COLECOES.forEach(c => db.getCollection(c).drop());

// ------------------------------------------------------------------------ ÍNDICES
// (o TratadorErrosGlobais depende de DuplicateKeyException para avisar CPF/CNPJ/e-mail repetidos)
// cpf/cnpj de proprietário: índice parcial, pois PF não tem cnpj e PJ não tem cpf
db.proprietarios.createIndex({ cpf: 1 },  { unique: true, partialFilterExpression: { cpf:  { $type: "string" } } });
db.proprietarios.createIndex({ cnpj: 1 }, { unique: true, partialFilterExpression: { cnpj: { $type: "string" } } });
db.proprietarios.createIndex({ email: 1 }, { unique: true });
db.clientes.createIndex({ cpf: 1 },   { unique: true });
db.clientes.createIndex({ email: 1 }, { unique: true });
db.corretores.createIndex({ cpf: 1 },   { unique: true });
db.corretores.createIndex({ email: 1 }, { unique: true });
db.fiadores.createIndex({ cpf: 1 }, { unique: true });
db.telefones.createIndex({ tipoDono: 1, donoId: 1, telefone: 1 }, { unique: true });
db.imoveis.createIndex({ proprietarioId: 1 });
db.fotos.createIndex({ imovelId: 1 });
db.publicacoes.createIndex({ imovelId: 1, ativa: 1 });
db.visitas.createIndex({ corretorId: 1 });
db.visitas.createIndex({ imovelId: 1 });
db.visitas.createIndex({ clienteId: 1 });
db.contratos_locacao.createIndex({ clienteId: 1 });
db.contratos_locacao.createIndex({ imovelId: 1 });
db.reajustes.createIndex({ locacaoId: 1 });
db.recibos_aluguel.createIndex({ locacaoId: 1, ano: 1, mes: 1 }, { unique: true });

// ---------------------------------------------------------------- TIPOS DE IMÓVEL
db.tipos_imovel.insertMany([
  { _id: 1,  descricao: "Casa" },
  { _id: 2,  descricao: "Casa Geminada" },
  { _id: 3,  descricao: "Edícula" },
  { _id: 4,  descricao: "Bangalô" },
  { _id: 5,  descricao: "Apartamento Padrão" },
  { _id: 6,  descricao: "Cobertura" },
  { _id: 7,  descricao: "Studio" },
  { _id: 8,  descricao: "Loft" },
  { _id: 9,  descricao: "Giardino" },
  { _id: 10, descricao: "Sala Comercial" },
  { _id: 11, descricao: "Andar Corrido" },
  { _id: 12, descricao: "Galpão" },
  { _id: 13, descricao: "Chácara" },
  { _id: 14, descricao: "Terreno" }
]);

// -------------------------------------------------------------------- ENDEREÇOS
// 1-5 proprietários | 6-12 imóveis | 13-14 fiadores
db.enderecos.insertMany([
  { _id: 1,  cep: "89201000", estado: "SC", cidade: "Joinville", bairro: "Centro",          numero: 100,  rua: "Rua Nove de Março",       complemento: "Apto 101" },
  { _id: 2,  cep: "89202000", estado: "SC", cidade: "Joinville", bairro: "Anita Garibaldi", numero: 250,  rua: "Rua Anita Garibaldi",     complemento: null },
  { _id: 3,  cep: "89203000", estado: "SC", cidade: "Joinville", bairro: "Costa e Silva",   numero: 310,  rua: "Rua Otto Pfuetzenreuter", complemento: null },
  { _id: 4,  cep: "89204000", estado: "SC", cidade: "Joinville", bairro: "América",         numero: 420,  rua: "Rua João Colin",          complemento: "Sala 10" },
  { _id: 5,  cep: "89205000", estado: "SC", cidade: "Joinville", bairro: "Saguaçu",         numero: 505,  rua: "Rua Dona Francisca",      complemento: "Casa" },
  { _id: 6,  cep: "89206000", estado: "SC", cidade: "Joinville", bairro: "Bom Retiro",      numero: 600,  rua: "Rua Tenente Antônio João", complemento: "Apto 302" },
  { _id: 7,  cep: "89207000", estado: "SC", cidade: "Joinville", bairro: "Santo Antônio",   numero: 715,  rua: "Rua Prudente de Moraes",  complemento: "Apto 45" },
  { _id: 8,  cep: "89208000", estado: "SC", cidade: "Joinville", bairro: "Glória",          numero: 128,  rua: "Rua Blumenau",            complemento: null },
  { _id: 9,  cep: "89209000", estado: "SC", cidade: "Joinville", bairro: "Atiradores",      numero: 900,  rua: "Rua Visconde de Taunay",  complemento: "Cobertura" },
  { _id: 10, cep: "89210000", estado: "SC", cidade: "Joinville", bairro: "Floresta",        numero: 233,  rua: "Rua Quinze de Novembro",  complemento: "Casa 2" },
  { _id: 11, cep: "89211000", estado: "SC", cidade: "Joinville", bairro: "Bucarein",        numero: 740,  rua: "Rua Max Colin",           complemento: "Loft 12" },
  { _id: 12, cep: "89212000", estado: "SC", cidade: "Joinville", bairro: "Boa Vista",       numero: 1010, rua: "Avenida Santos Dumont",   complemento: "Sala 506" },
  { _id: 13, cep: "89213000", estado: "SC", cidade: "Joinville", bairro: "Iririú",          numero: 66,   rua: "Rua Itaiópolis",          complemento: null },
  { _id: 14, cep: "89214000", estado: "SC", cidade: "Joinville", bairro: "Jardim Iririú",   numero: 312,  rua: "Rua Ministro Calógeras",  complemento: null }
]);

// ---------------------------------------------------------------- PROPRIETÁRIOS
// PF não tem "cnpj"/"razaoSocial"; PJ não tem "cpf"/"nome" (campos ausentes, não null)
db.proprietarios.insertMany([
  { _id: 1, tipo: "F", cpf: "44771278202", nome: "João da Silva",   email: "joao.silva@email.com",     senha: "Senha@2026", dadosBancarios: "Bco:1 Ag:1234 Cc:56789-0",   enderecoId: 1 },
  { _id: 2, tipo: "F", cpf: "26197306948", nome: "Maria Souza",     email: "maria.souza@email.com",    senha: "Senha@2026", dadosBancarios: "Bco:341 Ag:4321 Cc:98765-4", enderecoId: 2 },
  { _id: 3, tipo: "F", cpf: "52393849912", nome: "Carlos Pereira",  email: "carlos.pereira@email.com", senha: "Senha@2026", dadosBancarios: "Bco:237 Ag:567 Cc:123456-X", enderecoId: 3 },
  { _id: 4, tipo: "J", cnpj: "22175294000125", razaoSocial: "Joinville Empreendimentos Ltda", email: "contato@joinvilleimoveis.com", senha: "Senha@2026", dadosBancarios: "Bco:104 Ag:2020 Cc:777888-1", enderecoId: 4 },
  { _id: 5, tipo: "J", cnpj: "83960310000185", razaoSocial: "Saguaçu Participações S/A",      email: "financeiro@saguacupar.com",    senha: "Senha@2026", dadosBancarios: "Bco:260 Ag:1 Cc:4455667-8",   enderecoId: 5 }
]);

// --------------------------------------------------------------------- CLIENTES
// tipoImovelInteresse = _id em tipos_imovel (o servidor trabalha com o id)
db.clientes.insertMany([
  { _id: 1, nome: "Ana Lima",       cpf: "67539892269", email: "ana.lima@email.com",       senha: "Senha@2026", rendaMensal: 9500,  profissao: "Engenheiro",           tipoImovelInteresse: 5 },
  { _id: 2, nome: "Bruno Alves",    cpf: "20107136481", email: "bruno.alves@email.com",    senha: "Senha@2026", rendaMensal: 6200,  profissao: "Professora",           tipoImovelInteresse: 5 },
  { _id: 3, nome: "Carla Mendes",   cpf: "49265548606", email: "carla.mendes@email.com",   senha: "Senha@2026", rendaMensal: 7800,  profissao: "Analista de sistemas", tipoImovelInteresse: 5 },
  { _id: 4, nome: "Diego Rocha",    cpf: "72576386326", email: "diego.rocha@email.com",    senha: "Senha@2026", rendaMensal: 11200, profissao: "Médico",               tipoImovelInteresse: 8 },
  { _id: 5, nome: "Elisa Martins",  cpf: "16227586927", email: "elisa.martins@email.com",  senha: "Senha@2026", rendaMensal: 18000, profissao: "Empresário",           tipoImovelInteresse: 1 },
  { _id: 6, nome: "Fábio Nunes",    cpf: "64485497305", email: "fabio.nunes@email.com",    senha: "Senha@2026", rendaMensal: 14500, profissao: "Advogado",             tipoImovelInteresse: 2 },
  { _id: 7, nome: "Gabriela Costa", cpf: "33053041979", email: "gabriela.costa@email.com", senha: "Senha@2026", rendaMensal: 25000, profissao: "Arquiteta",            tipoImovelInteresse: 6 },
  { _id: 8, nome: "Henrique Dias",  cpf: "14026066215", email: "henrique.dias@email.com",  senha: "Senha@2026", rendaMensal: 8300,  profissao: "Contador",             tipoImovelInteresse: 10 }
]);

// -------------------------------------------------------------------- CORRETORES
db.corretores.insertMany([
  { _id: 1, nome: "Rafael Teixeira", email: "rafael.teixeira@imobiliaria.com", senha: "Senha@2026", telefone: "47993330001", cpf: "19228514264", percentualComissao: 0.05,  dataAdmissao: "2019-03-04", creci: "12345-F/SC" },
  { _id: 2, nome: "Patrícia Gomes",  email: "patricia.gomes@imobiliaria.com",  senha: "Senha@2026", telefone: "47993330002", cpf: "56562351065", percentualComissao: 0.055, dataAdmissao: "2020-08-17", creci: "23456-F/SC" },
  { _id: 3, nome: "Lucas Ferreira",  email: "lucas.ferreira@imobiliaria.com",  senha: "Senha@2026", telefone: "47993330003", cpf: "54900893420", percentualComissao: 0.045, dataAdmissao: "2021-02-01", creci: "34567-F/SC" },
  { _id: 4, nome: "Juliana Barros",  email: "juliana.barros@imobiliaria.com",  senha: "Senha@2026", telefone: "47993330004", cpf: "17500669127", percentualComissao: 0.06,  dataAdmissao: "2022-06-13", creci: "45678-F/SC" }
]);

// --------------------------------------------------------------------- FIADORES
db.fiadores.insertMany([
  { _id: 1, cpf: "35840992933", nome: "Roberto Almeida", enderecoId: 13, comprovanteRenda: texto("Comprovante de renda - Roberto Almeida") },
  { _id: 2, cpf: "15184715606", nome: "Sandra Vieira",   enderecoId: 14, comprovanteRenda: texto("Comprovante de renda - Sandra Vieira") }
]);

// -------------------------------------------------------------------- TELEFONES
// uma coleção para os três tipos de dono: o servidor lista/remove telefone por id
const telefones = [];
function tel(tipoDono, donoId, ...numeros) {
  numeros.forEach(t => telefones.push({ _id: telefones.length + 1, telefone: t, tipoDono, donoId }));
}
tel("PROPRIETARIO", 1, "47991110001", "4734220001");
tel("PROPRIETARIO", 2, "47991110002");
tel("PROPRIETARIO", 3, "47991110003", "47991110033");
tel("PROPRIETARIO", 4, "4734220004", "47991110004");
tel("PROPRIETARIO", 5, "4734220005");
tel("CLIENTE", 1, "47992220001");
tel("CLIENTE", 2, "47992220002");
tel("CLIENTE", 3, "47992220003", "47992220033");
tel("CLIENTE", 4, "47992220004");
tel("CLIENTE", 5, "47992220005");
tel("CLIENTE", 6, "47992220006");
tel("CLIENTE", 7, "47992220007", "47992220077");
tel("CLIENTE", 8, "47992220008");
tel("FIADOR", 1, "47994440001", "47994440011");
tel("FIADOR", 2, "47994440002");
db.telefones.insertMany(telefones);

// ----------------------------------------------------------------------- IMÓVEIS
// 1 Apto (loc) | 2 Apto (loc) | 3 Casa (venda) | 4 Cobertura (venda)
// 5 Geminado (venda) | 6 Loft (loc) | 7 Sala comercial (loc)
// status = estado final após os contratos (o que os triggers faziam)
db.imoveis.insertMany([
  { _id: 1, idTipoImovel: 5,  enderecoId: 6,  proprietarioId: 1, areaTotal: 78.5,  qtdDormitorios: 2, qtdBanheiros: 1, qtdVagas: 1, anoConstrucao: 2018, descricao: "Apartamento com 2 quartos, sacada com churrasqueira e vaga coberta.", valor: N("2800.00"),   status: "alugado" },
  { _id: 2, idTipoImovel: 5,  enderecoId: 7,  proprietarioId: 2, areaTotal: 64.0,  qtdDormitorios: 2, qtdBanheiros: 1, qtdVagas: 1, anoConstrucao: 2015, descricao: "Apartamento ensolarado, próximo ao centro, com armários planejados.", valor: N("2200.00"),   status: "alugado" },
  { _id: 3, idTipoImovel: 1,  enderecoId: 8,  proprietarioId: 1, areaTotal: 180.0, qtdDormitorios: 3, qtdBanheiros: 2, qtdVagas: 2, anoConstrucao: 2010, descricao: "Casa térrea com quintal amplo, edícula e piscina.",                    valor: N("520000.00"), status: "vendido" },
  { _id: 4, idTipoImovel: 6,  enderecoId: 9,  proprietarioId: 3, areaTotal: 210.0, qtdDormitorios: 4, qtdBanheiros: 3, qtdVagas: 3, anoConstrucao: 2020, descricao: "Cobertura duplex com terraço, vista panorâmica e 3 vagas.",            valor: N("980000.00"), status: "disponível para venda" },
  { _id: 5, idTipoImovel: 2,  enderecoId: 10, proprietarioId: 2, areaTotal: 120.0, qtdDormitorios: 3, qtdBanheiros: 2, qtdVagas: 2, anoConstrucao: 2023, descricao: "Casa geminada recém-construída, ótimo acabamento.",                    valor: N("395000.00"), status: "vendido" },
  { _id: 6, idTipoImovel: 8,  enderecoId: 11, proprietarioId: 3, areaTotal: 42.0,  qtdDormitorios: 1, qtdBanheiros: 1, qtdVagas: 1, anoConstrucao: 2021, descricao: "Loft moderno, mobiliado, com pé-direito duplo.",                       valor: N("3100.00"),   status: "alugado" },
  { _id: 7, idTipoImovel: 10, enderecoId: 12, proprietarioId: 4, areaTotal: 55.0,  qtdDormitorios: 0, qtdBanheiros: 1, qtdVagas: 1, anoConstrucao: 2019, descricao: "Sala comercial em prédio moderno, com recepção e ar-condicionado.",    valor: N("4500.00"),   status: "disponível para locação" }
]);

// ------------------------------------------------------------------------- FOTOS
db.fotos.insertMany([
  { _id: 1,  imovelId: 1, tipo: "Sala",            imagem: img("apartamento.jpg") },
  { _id: 2,  imovelId: 2, tipo: "Entrada",         imagem: img("apartamento2.jpg") },
  { _id: 3,  imovelId: 3, tipo: "Fachada",         imagem: img("casa.jpg") },
  { _id: 4,  imovelId: 4, tipo: "Terraço",         imagem: img("cobertura.jpg") },
  { _id: 5,  imovelId: 4, tipo: "Cozinha",         imagem: img("cobertura_cozinha.jpg") },
  { _id: 6,  imovelId: 4, tipo: "Quarto de Casal", imagem: img("cobertura_quarto.jpg") },
  { _id: 7,  imovelId: 4, tipo: "Banheiro",        imagem: img("cobertura_banheiro.jpg") },
  { _id: 8,  imovelId: 5, tipo: "Frente",          imagem: img("geminado.jpg") },
  { _id: 9,  imovelId: 6, tipo: "Entrada",         imagem: img("loft.jpg") },
  { _id: 10, imovelId: 7, tipo: "Recepção",        imagem: img("salacomercial.jpg") }
]);

// ------------------------------------------------------------------- PUBLICAÇÕES
// só as dos imóveis ainda disponíveis (4 e 7) estão ativas
db.publicacoes.insertMany([
  { _id: 1, imovelId: 1, fotoCapaId: 1,    titulo: "Apartamento 2 quartos no Bom Retiro",     tipoPublicacao: "L", ativa: false, data: "2024-04-20", acessos: 134 },
  { _id: 2, imovelId: 2, fotoCapaId: 2,    titulo: "Apartamento ensolarado no Santo Antônio", tipoPublicacao: "L", ativa: false, data: "2025-01-05", acessos: 98 },
  { _id: 3, imovelId: 3, fotoCapaId: 3,    titulo: "Casa com piscina na Glória",              tipoPublicacao: "V", ativa: false, data: "2026-07-20", acessos: 215 },
  { _id: 4, imovelId: 4, fotoCapaId: 4,    titulo: "Cobertura duplex nos Atiradores",         tipoPublicacao: "V", ativa: true,  data: "2026-10-01", acessos: 187 },
  { _id: 5, imovelId: 5, fotoCapaId: 8,    titulo: "Geminado novo na Floresta",               tipoPublicacao: "V", ativa: false, data: "2026-09-25", acessos: 143 },
  { _id: 6, imovelId: 6, fotoCapaId: 9,    titulo: "Loft mobiliado no Bucarein",              tipoPublicacao: "L", ativa: false, data: "2026-01-30", acessos: 121 },
  { _id: 7, imovelId: 7, fotoCapaId: 10,   titulo: "Sala comercial na Boa Vista",             tipoPublicacao: "L", ativa: true,  data: "2026-10-10", acessos: 76 },
  { _id: 8, imovelId: 2, fotoCapaId: null, titulo: "Apartamento reformado no Santo Antônio",  tipoPublicacao: "L", ativa: false, data: "2025-12-01", acessos: 64 }
]);

// ---------------------------------------------------------------------- VISITAS
// regra: no máx. uma visita por hora por corretor e por imóvel.
// potencialContrato=false -> visitas arquivadas (imóvel já alugado/vendido).
// 7-12: visitas já realizadas dos imóveis 4 e 7 (ainda disponíveis) -> aparecem como
//       "potenciais contratos" no painel do corretor e permitem fechar contrato.
// 14-15: visitas futuras.
db.visitas.insertMany([
  { _id: 1,  data: "2024-05-10", horario: "10:00", corretorId: 1, clienteId: 1, imovelId: 1, potencialContrato: false },
  { _id: 2,  data: "2025-01-15", horario: "14:00", corretorId: 2, clienteId: 2, imovelId: 2, potencialContrato: false },
  { _id: 3,  data: "2026-08-05", horario: "10:00", corretorId: 1, clienteId: 5, imovelId: 3, potencialContrato: false },
  { _id: 4,  data: "2026-08-12", horario: "15:00", corretorId: 1, clienteId: 7, imovelId: 3, potencialContrato: false },
  { _id: 5,  data: "2026-10-05", horario: "11:00", corretorId: 4, clienteId: 6, imovelId: 5, potencialContrato: false },
  { _id: 6,  data: "2026-02-10", horario: "16:00", corretorId: 3, clienteId: 4, imovelId: 6, potencialContrato: false },
  { _id: 7,  data: "2026-10-28", horario: "10:00", corretorId: 3, clienteId: 7, imovelId: 4, potencialContrato: true },
  { _id: 8,  data: "2026-11-14", horario: "14:00", corretorId: 3, clienteId: 8, imovelId: 4, potencialContrato: true },
  { _id: 9,  data: "2026-11-21", horario: "10:00", corretorId: 1, clienteId: 1, imovelId: 4, potencialContrato: true },
  { _id: 10, data: "2026-11-10", horario: "09:00", corretorId: 2, clienteId: 2, imovelId: 7, potencialContrato: true },
  { _id: 11, data: "2026-11-12", horario: "16:00", corretorId: 2, clienteId: 6, imovelId: 7, potencialContrato: true },
  { _id: 12, data: "2026-11-17", horario: "11:00", corretorId: 4, clienteId: 5, imovelId: 7, potencialContrato: true },
  { _id: 13, data: "2025-12-20", horario: "09:00", corretorId: 2, clienteId: 3, imovelId: 2, potencialContrato: false },
  { _id: 14, data: "2026-12-03", horario: "10:00", corretorId: 3, clienteId: 2, imovelId: 4, potencialContrato: true },
  { _id: 15, data: "2026-12-05", horario: "15:00", corretorId: 2, clienteId: 3, imovelId: 7, potencialContrato: true }
]);

// ------------------------------------------------------------ CONTRATOS DE LOCAÇÃO
// valorInicial = aluguel na assinatura (os recibos usam valorInicial + reajustes);
// valorAluguel = valor vigente. rescisão (dataRescisao/multa/motivo) só existe se ENCERRADO.
db.contratos_locacao.insertMany([
  // L1: apto 1, FIADOR, desde 2024-06 (36 meses), com dois reajustes
  { _id: 1, imovelId: 1, clienteId: 1, corretorId: 1, fiadorId: 1,
    prazoEmMeses: 36, garantia: "FIADOR", valorComissao: N("2800.00"),
    statusContrato: "ATIVO", diaVencimento: 5, dataInicio: "2024-06-01",
    valorInicial: N("2800.00"), valorAluguel: N("3050.00") },

  // L2: apto 2, SEGURO FIANCA, rescindido em 2025-11
  { _id: 2, imovelId: 2, clienteId: 2, corretorId: 2,
    prazoEmMeses: 12, garantia: "SEGURO FIANCA", valorComissao: N("2200.00"),
    statusContrato: "ENCERRADO", diaVencimento: 10, dataInicio: "2025-02-01",
    valorInicial: N("2200.00"), valorAluguel: N("2200.00"),
    dataRescisao: "2025-11-20", multa: N("1100.00"), motivo: "Mudança de cidade" },

  // L3: apto 2, DEPOSITO CAUCAO
  { _id: 3, imovelId: 2, clienteId: 3, corretorId: 2,
    prazoEmMeses: 24, garantia: "DEPOSITO CAUCAO", valorCaucao: N("4600.00"), valorComissao: N("2300.00"),
    statusContrato: "ATIVO", diaVencimento: 10, dataInicio: "2026-01-01",
    valorInicial: N("2300.00"), valorAluguel: N("2300.00") },

  // L4: loft, FIADOR
  { _id: 4, imovelId: 6, clienteId: 4, corretorId: 3, fiadorId: 2,
    prazoEmMeses: 30, garantia: "FIADOR", valorComissao: N("3100.00"),
    statusContrato: "ATIVO", diaVencimento: 15, dataInicio: "2026-03-01",
    valorInicial: N("3100.00"), valorAluguel: N("3100.00") }
]);

db.reajustes.insertMany([
  { _id: 1, locacaoId: 1, data: "2025-06-05", novoValor: N("2950.00"), indice: "IPCA" },
  { _id: 2, locacaoId: 1, data: "2026-06-05", novoValor: N("3050.00"), indice: "IGPM" }
]);

// ------------------------------------------------------------- CONTRATOS DE VENDA
db.contratos_venda.insertMany([
  { _id: 1, data: "2026-09-18", valorVenda: N("515000.00"), valorSinal: N("50000.00"), formaPagamento: "Financiamento",
    valorFinanciado: N("300000.00"), valorComissao: N("25750.00"), clienteId: 5, proprietarioId: 1, imovelId: 3, corretorId: 1 },
  { _id: 2, data: "2026-10-20", valorVenda: N("395000.00"), valorSinal: null, formaPagamento: "À vista",
    valorFinanciado: null, valorComissao: N("19750.00"), clienteId: 6, proprietarioId: 2, imovelId: 5, corretorId: 4 }
]);

// ----------------------------------------------------------------------- RECIBOS
// vazia de propósito: o GeradorRecibosAgendados chama ReciboDAO.gerarRecibosPendentes()
// ao subir a aplicação e todo dia às 00:05, e os recibos dos contratos ATIVOS são criados.

// -------------------------------------------------------------------- CONTADORES
// último id usado em cada coleção (próximo = seq + 1)
db.contadores.insertMany([
  { _id: "enderecos",         seq: 14 },
  { _id: "proprietarios",     seq: 5 },
  { _id: "clientes",          seq: 8 },
  { _id: "corretores",        seq: 4 },
  { _id: "fiadores",          seq: 2 },
  { _id: "telefones",         seq: telefones.length },
  { _id: "imoveis",           seq: 7 },
  { _id: "fotos",             seq: 10 },
  { _id: "publicacoes",       seq: 8 },
  { _id: "visitas",           seq: 15 },
  { _id: "contratos_locacao", seq: 4 },
  { _id: "contratos_venda",   seq: 2 },
  { _id: "reajustes",         seq: 2 },
  { _id: "recibos_aluguel",   seq: 0 }
]);

print("População concluída: " + COLECOES.filter(c => c !== "contadores")
  .map(c => c + "=" + db.getCollection(c).countDocuments()).join(", "));
