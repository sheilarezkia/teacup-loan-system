-- Revert teacup-loan:purchases from pg

BEGIN;

DROP TABLE IF EXISTS teacup_loan.purchases;

COMMIT;
