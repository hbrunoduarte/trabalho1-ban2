CREATE OR REPLACE FUNCTION fn_valida_visita() RETURNS TRIGGER AS 
$$
DECLARE
	v_status VARCHAR(30);
BEGIN
	SELECT status INTO v_status
	FROM Imovel
	WHERE id_imovel = NEW.id_imovel;

	IF v_status IN ('vendido', 'alugado', 'indisponível')
		AND (TG_OP = 'INSERT'
			OR NEW.data IS DISTINCT FROM OLD.data
            OR NEW.horario IS DISTINCT FROM OLD.horario
            OR NEW.id_imovel IS DISTINCT FROM OLD.id_imovel) THEN
		RAISE EXCEPTION 'Imóvel não aceita visitas (status: %)', v_status;
	END IF;

	-- Corretor nao pode estar em duas visitas ao mesmo tempo
	IF EXISTS (
		SELECT 1
		FROM Visita_Agendada
		WHERE id_corretor = NEW.id_corretor
		  AND potencial_contrato
		  AND data = NEW.data
		  AND id_visita IS DISTINCT FROM NEW.id_visita
		  AND horario > NEW.horario - INTERVAL '1 hour'
		  AND horario < NEW.horario + INTERVAL '1 hour'
	) THEN 
		RAISE EXCEPTION 'Corretor já possui visita nesse horário';
	END IF;

	-- Nao pode ter mais de uma visita em um imovel ao mesmo tempo
	IF EXISTS (
		SELECT 1
		FROM Visita_Agendada
		WHERE id_imovel = NEW.id_imovel
		  AND data = NEW.data
		  AND id_visita IS DISTINCT FROM NEW.id_visita
		  AND horario > NEW.horario - INTERVAL '1 hour'
		  AND horario < NEW.horario + INTERVAL '1 hour'
	) THEN
		RAISE EXCEPTION 'Já existe visita agendada para esse imóvel nesse horário';
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_valida_visita
BEFORE INSERT OR UPDATE ON Visita_Agendada
FOR EACH ROW EXECUTE FUNCTION fn_valida_visita();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_valida_fiador() RETURNS TRIGGER AS
$$
DECLARE
	v_cpf_locatario TIPO_CPF;
	v_cpf_fiador TIPO_CPF;
BEGIN

	IF NEW.id_fiador IS NULL THEN
		IF NEW.garantia = 'FIADOR' THEN
			RAISE EXCEPTION 'Contrato com garantia do tipo FIADOR exige um fiador';
		END IF;
		RETURN NEW;
	END IF;

	IF NEW.garantia IS DISTINCT FROM 'FIADOR' THEN
		RAISE EXCEPTION 'Somente contratos com garantia do tipo FIADOR podem ter fiador';
	END IF;

	IF (TG_OP = 'INSERT' OR NEW.id_fiador IS DISTINCT FROM OLD.id_fiador)
	   AND NEW.status_contrato IS DISTINCT FROM 'ATIVO' THEN
		RAISE EXCEPTION 'O contrato não está ativo (status: %)', NEW.status_contrato;
	END IF;

	SELECT cpf INTO v_cpf_locatario FROM Cliente WHERE id_cliente = NEW.id_cliente;
	SELECT cpf INTO v_cpf_fiador    FROM Fiador  WHERE id_fiador  = NEW.id_fiador;

	IF v_cpf_fiador = v_cpf_locatario THEN
		RAISE EXCEPTION 'O fiador não pode ser locatário do contrato';
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_valida_fiador
BEFORE INSERT OR UPDATE OF garantia, id_fiador, id_cliente, status_contrato ON Contrato_Locacao
FOR EACH ROW EXECUTE FUNCTION fn_valida_fiador();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_valida_venda() RETURNS TRIGGER AS 
$$
DECLARE
	v_status VARCHAR(30);
	v_id_proprietario INT;
BEGIN
	SELECT status, id_proprietario INTO v_status, v_id_proprietario
	FROM Imovel
	WHERE id_imovel = NEW.id_imovel;

	IF TG_OP = 'INSERT' AND v_status IS DISTINCT FROM 'disponível para venda' THEN
		RAISE EXCEPTION 'Imóvel % não está disponível para venda (status: %)', NEW.id_imovel, v_status;
	END IF;

	IF v_id_proprietario != NEW.id_proprietario THEN
		RAISE EXCEPTION 'Proprietário do contrato não é o proprietário do imóvel';
	END IF;

	IF COALESCE(NEW.valor_sinal, 0) + COALESCE(NEW.valor_financiado, 0) > NEW.valor_venda THEN
		RAISE EXCEPTION 'Sinal mais valor financiado excede o valor de venda';
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_valida_venda
BEFORE INSERT OR UPDATE ON Contrato_Venda
FOR EACH ROW EXECUTE FUNCTION fn_valida_venda();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_imovel_vendido() RETURNS TRIGGER AS
$$
BEGIN
	UPDATE Imovel
	SET status = 'vendido'
	WHERE id_imovel = NEW.id_imovel;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_imovel_vendido
AFTER INSERT ON Contrato_Venda
FOR EACH ROW EXECUTE FUNCTION fn_imovel_vendido();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_valida_locacao() RETURNS TRIGGER AS
$$
DECLARE
	v_status VARCHAR(30);
BEGIN
	SELECT status INTO v_status
	FROM Imovel
	WHERE id_imovel = NEW.id_imovel;

	IF NEW.status_contrato = 'ATIVO' AND v_status IS DISTINCT FROM 'disponível para locação' THEN
		RAISE EXCEPTION 'Imóvel % não está disponível para locação (status: %)', NEW.id_imovel, v_status;
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_valida_locacao
BEFORE INSERT ON Contrato_Locacao
FOR EACH ROW EXECUTE FUNCTION fn_valida_locacao();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_status_imovel_locacao() RETURNS TRIGGER AS
$$
BEGIN
	IF TG_OP = 'INSERT' AND NEW.status_contrato = 'ATIVO' THEN
		UPDATE Imovel 
		SET status = 'alugado'
		WHERE id_imovel = NEW.id_imovel; 
	ELSIF TG_OP = 'UPDATE' AND OLD.status_contrato = 'ATIVO' AND NEW.status_contrato = 'ENCERRADO' THEN
		UPDATE Imovel
		SET status = 'disponível para locação'
		WHERE id_imovel = NEW.id_imovel;
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_status_imovel_locacao
AFTER INSERT OR UPDATE OF status_contrato ON Contrato_Locacao
FOR EACH ROW EXECUTE FUNCTION fn_status_imovel_locacao();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_contrato_fechado() RETURNS TRIGGER AS
$$
BEGIN
	UPDATE Visita_Agendada
	SET potencial_contrato = FALSE
	WHERE id_imovel = NEW.id_imovel;

	DELETE FROM Publicacao WHERE id_imovel = NEW.id_imovel;
	DELETE FROM Foto       WHERE id_imovel = NEW.id_imovel;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_contrato_locacao_fechado
AFTER INSERT ON Contrato_Locacao
FOR EACH ROW EXECUTE FUNCTION fn_contrato_fechado();

CREATE TRIGGER tgr_contrato_venda_fechado
AFTER INSERT ON Contrato_Venda
FOR EACH ROW EXECUTE FUNCTION fn_contrato_fechado();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_imovel_publicado() RETURNS TRIGGER AS
$$
DECLARE
	v_status_imovel VARCHAR(30);
BEGIN
	IF EXISTS (SELECT 1 FROM Publicacao WHERE id_imovel=NEW.id_imovel AND ativa) THEN
		RAISE EXCEPTION 'Imóvel já está publicado';
	END IF;

	SELECT status INTO v_status_imovel FROM Imovel WHERE id_imovel=NEW.id_imovel;

	IF NEW.tipo_publicacao = 'V' AND v_status_imovel <> 'disponível para venda' THEN
		RAISE EXCEPTION 'Imóvel não está disponível para venda';
	ELSIF NEW.tipo_publicacao = 'L' AND v_status_imovel <> 'disponível para locação' THEN
		RAISE EXCEPTION 'Imóvel não está disponível para locação';
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_imovel_publicado
BEFORE INSERT ON Publicacao
FOR EACH ROW EXECUTE FUNCTION fn_imovel_publicado();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_calcula_recibo() RETURNS TRIGGER AS
$$
BEGIN
	NEW.desconto  := COALESCE(NEW.desconto, 0);
	NEW.acrescimo := COALESCE(NEW.acrescimo, 0);
	NEW.valor_liquido := NEW.valor_aluguel - NEW.desconto + NEW.acrescimo;

	IF NEW.desconto < 0 OR NEW.acrescimo < 0 THEN
		RAISE EXCEPTION 'Desconto e acréscimo não podem ser negativos';
	END IF;

	IF NEW.valor_liquido < 0 THEN
		RAISE EXCEPTION 'O desconto não pode ser maior que o valor do aluguel';
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_calcula_recibo
BEFORE INSERT OR UPDATE ON Recibo_Aluguel
FOR EACH ROW EXECUTE FUNCTION fn_calcula_recibo();

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_gerar_recibos(p_referencia DATE DEFAULT CURRENT_DATE)
RETURNS INT AS
$$
DECLARE
	v_qtd INT;
BEGIN
	INSERT INTO Recibo_Aluguel (valor_aluguel, data_vencimento, mes, ano, id_locacao)
		SELECT c.valor_aluguel,
			   make_date(EXTRACT(YEAR FROM m.mes_ref)::INT,
						 EXTRACT(MONTH FROM m.mes_ref)::INT,
						 LEAST(c.dia_vencimento,
							   EXTRACT(DAY FROM (m.mes_ref + INTERVAL '1 month' - INTERVAL '1 day'))::INT)),
			   EXTRACT(MONTH FROM m.mes_ref)::INT,
			   EXTRACT(YEAR FROM m.mes_ref)::INT,
			   c.id_locacao
		FROM Contrato_Locacao c CROSS JOIN
			 LATERAL generate_series(
				date_trunc('month', c.data_inicio),
				LEAST(date_trunc('month', p_referencia),
						date_trunc('month', c.data_inicio) + make_interval(months => c.prazo_em_meses - 1)),
				INTERVAL '1 month'
			 ) AS m(mes_ref)
		WHERE c.status_contrato = 'ATIVO'
	ON CONFLICT (id_locacao, ano, mes) DO NOTHING;

	GET DIAGNOSTICS v_qtd = ROW_COUNT;

	RETURN v_qtd;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_pagar_recibo(p_id_recibo INT, p_forma VARCHAR,
                                           p_data DATE DEFAULT CURRENT_DATE,
                                           p_desconto TIPO_VALOR DEFAULT NULL,
                                           p_acrescimo TIPO_VALOR DEFAULT NULL)
RETURNS VOID AS
$$
BEGIN

	UPDATE Recibo_Aluguel
		SET forma_pagamento = p_forma,
			data_pagamento  = p_data,
			desconto = COALESCE(p_desconto, desconto),
			acrescimo = COALESCE(p_acrescimo, acrescimo)
	WHERE id_recibo = p_id_recibo AND data_pagamento IS NULL;

	IF NOT FOUND THEN
		RAISE EXCEPTION 'Recibo inexistente ou já pago';
	END IF;

END;
$$ LANGUAGE plpgsql;

/*-------------------------------------------------------------------------------------------*/

CREATE OR REPLACE FUNCTION fn_limpa_recibos_rescisao() RETURNS TRIGGER AS
$$
BEGIN

	DELETE FROM Recibo_Aluguel
		WHERE id_locacao = NEW.id_locacao
				AND data_pagamento IS NULL
				AND make_date(ano, mes, 1) > date_trunc('month', NEW.data_rescisao)::DATE;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tgr_limpa_recibos_rescisao
AFTER UPDATE ON Contrato_Locacao
FOR EACH ROW
WHEN (OLD.status_contrato = 'ATIVO' AND NEW.status_contrato = 'ENCERRADO')
EXECUTE FUNCTION fn_limpa_recibos_rescisao();

/*-------------------------------------------------------------------------------------------*/
