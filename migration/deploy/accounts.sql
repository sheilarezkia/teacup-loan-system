-- Deploy teacup-loan:accounts to pg
-- requires: schema

BEGIN;

CREATE TABLE teacup_loan.accounts(
   id SERIAL PRIMARY KEY,
   holder_name text NOT NULL,
   phone_number text NOT NULL,
   current_limit bigint NOT NULL,
   max_limit bigint NOT NULL
);

COMMIT;
