-- Deploy teacup-loan:payment_number to pg
-- requires: payments

BEGIN;

ALTER TABLE teacup_loan.payments
    ADD COLUMN payment_number int NOT NULL;

COMMIT;
