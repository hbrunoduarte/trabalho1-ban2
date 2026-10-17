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

-------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_valida_fiador() RETURNS TRIGGER AS
$$
DECLARE
	v_garantia VARCHAR(30);
	v_status VARCHAR(15);
	v_cpf_locatario TIPO_CPF;
	v_cpf_fiador TIPO_CPF;
BEGIN
	SELECT c.garantia, c.status_contrato, cl.cpf
	INTO v_garantia, v_status, v_cpf_locatario
	FROM Contrato_Locacao c
	JOIN Cliente cl ON cl.id_cliente = c.id_cliente
	WHERE c.id_locacao = NEW.id_locacao;

	SELECT cpf INTO v_cpf_fiador
	FROM Fiador
	WHERE id_fiador = NEW.id_fiador;

	IF v_garantia IS DISTINCT FROM 'FIADOR' THEN
		RAISE EXCEPTION 'Contrato % não tem garantia do tipo FIADOR', NEW.id_locacao;
	END IF;

	IF (TG_OP = 'INSERT' OR NEW.id_locacao IS DISTINCT FROM OLD.id_locacao)
	   AND v_status IS DISTINCT FROM 'ATIVO' THEN
		RAISE EXCEPTION 'Contrato % não está ativo (status: %)', NEW.id_locacao, v_status;
	END IF;
	
	IF v_cpf_fiador = v_cpf_locatario THEN
		RAISE EXCEPTION 'O fiador não pode ser locatário do contrato';
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_valida_fiador
BEFORE INSERT OR UPDATE ON Fiador_Contrato
FOR EACH ROW EXECUTE FUNCTION fn_valida_fiador();

-------------------------------------------------------------------------------------------

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

-------------------------------------------------------------------------------------------

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

-------------------------------------------------------------------------------------------

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

-------------------------------------------------------------------------------------------

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

-------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION fn_garantia_com_fiador() RETURNS TRIGGER AS
$$
BEGIN
	IF OLD.garantia = 'FIADOR' AND NEW.garantia IS DISTINCT FROM 'FIADOR'
	AND EXISTS (SELECT 1 FROM Fiador_Contrato WHERE id_locacao = NEW.id_locacao) THEN
		RAISE EXCEPTION 'Contrato % possui fiador cadastrado', NEW.id_locacao;
	END IF;

	RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER tgr_garantia_com_fiador
BEFORE UPDATE OF garantia ON Contrato_Locacao
FOR EACH ROW EXECUTE FUNCTION fn_garantia_com_fiador();