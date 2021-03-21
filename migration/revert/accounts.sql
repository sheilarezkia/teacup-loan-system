-- Revert teacup-loan:accounts from pg

BEGIN;

DROP TABLE teacup_loan.accounts;

COMMIT;
