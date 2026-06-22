-- Enable UUID support
create extension if not exists "uuid-ossp";
--------------------------------------------------
-- USERS TABLE
--------------------------------------------------
create table users (
                       id UUID primary key default uuid_generate_v4(),
                       name VARCHAR(255) not null,
                       email VARCHAR(255) not null unique,
                       mobile_number VARCHAR(20),
                       pwd TEXT,
-- Audit fields
                       created_at TIMESTAMP not null default NOW(),
                       updated_at TIMESTAMP default NOW(),
                       created_by VARCHAR(255) not null,
                       updated_by VARCHAR(255)
);
-- Search users by email efficiently
CREATE INDEX idx_users_email ON users(email);

--------------------------------------------------
-- TRANSACTIONS TABLE
--------------------------------------------------
create table transactions (
                              id UUID primary key default uuid_generate_v4(),
                              amount numeric(19, 2) not null check (amount >= 0),
                              currency VARCHAR(10) not null check (length(currency) > 0),
                              description TEXT,
                              counter_party_iban VARCHAR(34) not null,
                              user_id UUID not null,
-- Audit fields
                              created_at TIMESTAMP not null default NOW(),
                              updated_at TIMESTAMP default NOW(),
                              created_by VARCHAR(255) not null,
                              updated_by VARCHAR(255),

                              version BIGINT not null default 0,
-- Relationships
                              constraint fk_transaction_user
                                  foreign key (user_id)
                                      references users(id)
                                      on
                                          delete
                                          cascade
);
-- Indexes for performance optimization
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_amount ON transactions(amount);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);