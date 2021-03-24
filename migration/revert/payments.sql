-- Revert teacup-loan:payments from pg

BEGIN;

DROP TABLE IF EXISTS teacup_loan.payments;

COMMIT;
