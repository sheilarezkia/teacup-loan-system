-- Verify teacup-loan:purchases on pg

BEGIN;

SELECT id, account_id, description, amount, installment_period_month, status, created_at
FROM teacup_loan.purchases
WHERE FALSE;

ROLLBACK;
