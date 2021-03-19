-- Verify teacup-loan:schema on pg

BEGIN;

SELECT pg_catalog.has_schema_privilege('teacup_loan', 'usage');

ROLLBACK;
