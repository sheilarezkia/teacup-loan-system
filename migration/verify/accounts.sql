-- Verify teacup-loan:accounts on pg

BEGIN;

SELECT id, holder_name, phone_number, current_limit, max_limit
  FROM teacup_loan.accounts
 WHERE FALSE;

ROLLBACK;
