-- Revert teacup-loan:schema from pg

BEGIN;

DROP SCHEMA teacup_loan;

COMMIT;
