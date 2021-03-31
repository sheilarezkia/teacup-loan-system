-- Revert teacup-loan:payment_number from pg

BEGIN;

ALTER TABLE teacup_loan.payments
    DROP COLUMN payment_number;

COMMIT;
