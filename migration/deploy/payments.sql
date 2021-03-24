-- Deploy teacup-loan:payments to pg
-- requires: purchases

BEGIN;

CREATE TABLE teacup_loan.payments(
    id SERIAL PRIMARY KEY,
    purchase_id int NOT NULL,
    bill_amount bigint NOT NULL,
    status text NOT NULL,
    due_date timestamp with time zone NOT NULL,
    paid_at timestamp with time zone,
    CONSTRAINT fk_purchase FOREIGN KEY(purchase_id) REFERENCES teacup_loan.purchases(id)
);

COMMIT;
