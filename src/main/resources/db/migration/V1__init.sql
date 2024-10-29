CREATE TABLE IF NOT EXISTS account (
    number SERIAL PRIMARY KEY,
    balance DECIMAL(20,2), 
    currency VARCHAR(3),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transfer (
    id SERIAL PRIMARY KEY, 
    source_account_no LONG,
    target_account_no LONG,
    amount DECIMAL(20,2),
    currency VARCHAR(3),
    source_currency VARCHAR(3),
    target_currency VARCHAR(3),
    time TIMESTAMP,
    FOREIGN KEY (source_account_no) references account(number),
    FOREIGN KEY (target_account_no) references account(number)
)
