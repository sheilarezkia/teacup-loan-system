-- Verify teacup-loan:payment_number on pg

BEGIN;

SELECT payment_number
FROM teacup_loan.payments
WHERE FALSE;

ROLLBACK;
