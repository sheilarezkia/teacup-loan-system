-- Verify teacup-loan:payments on pg

BEGIN;

SELECT id, purchase_id, bill_amount, status, due_date, paid_at
FROM teacup_loan.payments
WHERE FALSE;

ROLLBACK;
