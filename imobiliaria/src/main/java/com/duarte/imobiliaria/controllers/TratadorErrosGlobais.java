package com.duarte.imobiliaria.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorErrosGlobais {

    public static final String CHAVE_GERAL = "geral";

    private static final Logger log = LoggerFactory.getLogger(TratadorErrosGlobais.class);

    // SQLSTATE do PostgreSQL
    private static final String SQLSTATE_NOT_NULL = "23502";
    private static final String SQLSTATE_FOREIGN_KEY = "23503";
    private static final String SQLSTATE_UNIQUE = "23505";
    private static final String SQLSTATE_VALOR_MUITO_LONGO = "22001";
    private static final String SQLSTATE_RAISE_EXCEPTION = "P0001";

    private final MessageSource messageSource;

    public TratadorErrosGlobais(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // validação

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String campo = (error instanceof FieldError fe) ? fe.getField() : CHAVE_GERAL;
            erros.putIfAbsent(campo, error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(erros);
    }

    // requisição ruim

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        log.debug("Corpo da requisição ilegível: {}", ex.getMessage());
        return erro(HttpStatus.BAD_REQUEST, "erro.requisicao.invalida");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tratarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return erro(HttpStatus.BAD_REQUEST, "erro.parametro.invalido", ex.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        return erro(HttpStatus.METHOD_NOT_ALLOWED, "erro.metodo.nao.suportado", ex.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> tratarTipoNaoSuportado(HttpMediaTypeNotSupportedException ex) {
        return erro(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "erro.conteudo.nao.suportado");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(NoResourceFoundException ex) {
        return erro(HttpStatus.NOT_FOUND, "erro.recurso.nao.encontrado");
    }

    // banco

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(DataIntegrityViolationException ex) {
        Throwable causa = ex.getMostSpecificCause();
        log.warn("Violação de integridade no banco: {}", causa.getMessage());

        String sqlState = causa instanceof SQLException sql ? sql.getSQLState() : null;
        String detalhe = causa.getMessage() == null ? "" : causa.getMessage().toLowerCase();

        if (ex instanceof DuplicateKeyException || SQLSTATE_UNIQUE.equals(sqlState)) {
            if (detalhe.contains("cnpj")) {
                return campo(HttpStatus.CONFLICT, "cnpj", "cadastro.cnpj.duplicado");
            }
            if (detalhe.contains("cpf")) {
                return campo(HttpStatus.CONFLICT, "cpf", "cadastro.cpf.duplicado");
            }
            if (detalhe.contains("email")) {
                return campo(HttpStatus.CONFLICT, "email", "cadastro.email.duplicado");
            }
            return erro(HttpStatus.CONFLICT, "erro.dados.duplicados");
        }
        if (SQLSTATE_VALOR_MUITO_LONGO.equals(sqlState)) {
            return erro(HttpStatus.BAD_REQUEST, "erro.dados.muito.longos");
        }
        if (SQLSTATE_FOREIGN_KEY.equals(sqlState)) {
            return erro(HttpStatus.CONFLICT, "erro.dados.referencia.invalida");
        }
        if (SQLSTATE_NOT_NULL.equals(sqlState)) {
            return erro(HttpStatus.BAD_REQUEST, "erro.dados.incompletos");
        }
        return erro(HttpStatus.CONFLICT, "erro.dados.conflito");
    }

    @ExceptionHandler({CannotGetJdbcConnectionException.class, DataAccessResourceFailureException.class})
    public ResponseEntity<Map<String, String>> tratarBancoIndisponivel(Exception ex) {
        log.error("Banco de dados indisponível", ex);
        return erro(HttpStatus.SERVICE_UNAVAILABLE, "erro.banco.indisponivel");
    }

    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Map<String, String>> tratarErroDoBanco(UncategorizedSQLException ex) {
        SQLException sql = ex.getSQLException();
        if (sql != null && SQLSTATE_RAISE_EXCEPTION.equals(sql.getSQLState())) {
            log.warn("Regra rejeitada por trigger do banco: {}", sql.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(CHAVE_GERAL, mensagemDoTrigger(sql)));
        }
        return tratarInesperado(ex);
    }

    private static String mensagemDoTrigger(SQLException sql) {
        String texto = sql.getMessage() == null ? "" : sql.getMessage().strip();
        int fim = texto.indexOf('\n');
        String linha = (fim >= 0 ? texto.substring(0, fim) : texto).strip();
        return linha.regionMatches(true, 0, "ERROR:", 0, 6) ? linha.substring(6).strip() : linha;
    }

    // inesperado

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarInesperado(Exception ex) {
        if (ex instanceof ErrorResponse er && er.getStatusCode().is4xxClientError()) {
            return campo(er.getStatusCode(), CHAVE_GERAL, "erro.requisicao.invalida");
        }
        log.error("Erro inesperado", ex);
        return erro(HttpStatus.INTERNAL_SERVER_ERROR, "erro.servidor");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String chaveMensagem, Object... args) {
        return campo(status, CHAVE_GERAL, chaveMensagem, args);
    }

    private ResponseEntity<Map<String, String>> campo(HttpStatusCode status, String campo, String chaveMensagem, Object... args) {
        String mensagem = messageSource.getMessage(chaveMensagem, args, chaveMensagem, LocaleContextHolder.getLocale());
        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put(campo, mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}