package com.duarte.imobiliaria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@Configuration
@Profile("mongo")
public class MongoConfig {

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(MongoDatabaseFactory fabrica,
                                                         @Value("${app.mongo.transacoes:false}") boolean transacoes) {
        return transacoes ? new MongoTransactionManager(fabrica) : new SemTransacao();
    }

    static class SemTransacao extends AbstractPlatformTransactionManager {
        @Override protected Object doGetTransaction() { return new Object(); }
        @Override protected void doBegin(Object transacao, TransactionDefinition definicao) { }
        @Override protected void doCommit(DefaultTransactionStatus status) { }
        @Override protected void doRollback(DefaultTransactionStatus status) { }
    }
}
