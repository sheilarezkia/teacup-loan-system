-- Deploy teacup-loan:purchases to pg
-- requires: accounts

BEGIN;

CREATE TABLE teacup_loan.purchases(
     id SERIAL PRIMARY KEY,
     account_id int NOT NULL,
     description text NOT NULL,
     amount bigint NOT NULL,
     installment_period_month int NOT NULL,
     status text,
     created_at timestamp with time zone DEFAULT NOW(),
     CONSTRAINT fk_account FOREIGN KEY(account_id) REFERENCES teacup_loan.accounts(id)
);

COMMIT;
