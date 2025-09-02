-- =====================================================
-- CURRENCY EXCHANGE API
-- =====================================================

-- Drop existing tables in reverse dependency order
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS system_metrics CASCADE;
DROP TABLE IF EXISTS data_quality_metrics CASCADE;
DROP TABLE IF EXISTS api_request_logs CASCADE;
DROP TABLE IF EXISTS api_rate_limits CASCADE;
DROP TABLE IF EXISTS api_clients CASCADE;
DROP TABLE IF EXISTS exchange_rates_historical CASCADE;
DROP TABLE IF EXISTS exchange_rates CASCADE;
DROP TABLE IF EXISTS provider_currency_pairs CASCADE;
DROP TABLE IF EXISTS data_providers CASCADE;
DROP TABLE IF EXISTS currency_pairs CASCADE;
DROP TABLE IF EXISTS currencies CASCADE;

-- =====================================================
-- 1. CURRENCY MANAGEMENT
-- =====================================================

-- Currencies table: Master list of supported currencies
CREATE TABLE currencies
(
    code         VARCHAR(3) PRIMARY KEY,
    name         VARCHAR(100)             NOT NULL,
    numeric_code SMALLINT UNIQUE          NOT NULL,
    minor_unit   SMALLINT                 NOT NULL DEFAULT 2,
    is_active    BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_valid_currency_code CHECK (code REGEXP '^[A-Z]{3}$'),
    CONSTRAINT chk_valid_numeric_code CHECK (numeric_code BETWEEN 1 AND 999)
);

-- Create index on active currencies
CREATE INDEX idx_currencies_active ON currencies (is_active, code);

-- Currency pairs: Tradeable currency combinations
CREATE TABLE currency_pairs
(
    id               IDENTITY PRIMARY KEY,
    base_currency    VARCHAR(3)               NOT NULL,
    quote_currency   VARCHAR(3)               NOT NULL,
    is_active        BOOLEAN                  NOT NULL DEFAULT TRUE,
    min_spread       DECIMAL(10, 5),
    max_spread       DECIMAL(10, 5),
    precision_digits SMALLINT                 NOT NULL DEFAULT 5,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_base_currency FOREIGN KEY (base_currency) REFERENCES currencies (code),
    CONSTRAINT fk_quote_currency FOREIGN KEY (quote_currency) REFERENCES currencies (code),
    CONSTRAINT chk_different_currencies CHECK (base_currency != quote_currency),
    CONSTRAINT uk_currency_pair UNIQUE (base_currency, quote_currency)
);

-- Indexes for currency pairs
CREATE INDEX idx_currency_pairs_base ON currency_pairs (base_currency);
CREATE INDEX idx_currency_pairs_quote ON currency_pairs (quote_currency);
CREATE INDEX idx_currency_pairs_active ON currency_pairs (is_active, base_currency, quote_currency);

-- =====================================================
-- 2. DATA SOURCES & PROVIDERS
-- =====================================================

-- Data providers: External rate data sources
CREATE TABLE data_providers
(
    id                  IDENTITY PRIMARY KEY,
    name                VARCHAR(100)             NOT NULL UNIQUE,
    api_endpoint        VARCHAR(500),
    priority            SMALLINT                 NOT NULL DEFAULT 100,
    is_active           BOOLEAN                  NOT NULL DEFAULT TRUE,
    rate_limit_per_hour INTEGER                           DEFAULT 1000,
    last_sync_at        TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP()
);

CREATE INDEX idx_data_providers_active ON data_providers (is_active, priority);

-- Provider currency pair mapping
CREATE TABLE provider_currency_pairs
(
    id               IDENTITY PRIMARY KEY,
    provider_id      INTEGER NOT NULL,
    currency_pair_id INTEGER NOT NULL,
    external_symbol  VARCHAR(20),
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_provider FOREIGN KEY (provider_id) REFERENCES data_providers (id),
    CONSTRAINT fk_currency_pair FOREIGN KEY (currency_pair_id) REFERENCES currency_pairs (id),
    CONSTRAINT uk_provider_pair UNIQUE (provider_id, currency_pair_id)
);

CREATE INDEX idx_provider_pairs_active ON provider_currency_pairs (is_active, provider_id);

-- =====================================================
-- 3. EXCHANGE RATE DATA (HOT DATA)
-- =====================================================

-- Main exchange rates table (hot data - last 3 months)
CREATE TABLE exchange_rates
(
    id                 IDENTITY PRIMARY KEY,
    currency_pair_id   INTEGER                  NOT NULL,
    rate_timestamp     TIMESTAMP WITH TIME ZONE NOT NULL,

    -- OHLC data for bid prices
    bid_open           DECIMAL(15, 8)           NOT NULL,
    bid_high           DECIMAL(15, 8)           NOT NULL,
    bid_low            DECIMAL(15, 8)           NOT NULL,
    bid_close          DECIMAL(15, 8)           NOT NULL,
    bid_average        DECIMAL(15, 8)           NOT NULL,

    -- OHLC data for ask prices
    ask_open           DECIMAL(15, 8)           NOT NULL,
    ask_high           DECIMAL(15, 8)           NOT NULL,
    ask_low            DECIMAL(15, 8)           NOT NULL,
    ask_close          DECIMAL(15, 8)           NOT NULL,
    ask_average        DECIMAL(15, 8)           NOT NULL,

    -- Derived values (computed in application layer for H2 compatibility)
    mid_rate           DECIMAL(15, 8),
    spread             DECIMAL(15, 8),

    -- Volume and metadata
    volume             DECIMAL(20, 2)                    DEFAULT 0,
    trade_count        INTEGER                           DEFAULT 0,
    data_quality_score SMALLINT                          DEFAULT 100,

    -- Audit fields
    provider_id        INTEGER,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_exchange_rates_pair FOREIGN KEY (currency_pair_id) REFERENCES currency_pairs (id),
    CONSTRAINT fk_exchange_rates_provider FOREIGN KEY (provider_id) REFERENCES data_providers (id),

    -- Data validation constraints
    CONSTRAINT chk_positive_bid_rates CHECK (
        bid_open > 0 AND bid_high > 0 AND bid_low > 0 AND bid_close > 0
        ),
    CONSTRAINT chk_positive_ask_rates CHECK (
        ask_open > 0 AND ask_high > 0 AND ask_low > 0 AND ask_close > 0
        ),
    CONSTRAINT chk_valid_ohlc_bid CHECK (
        bid_low <= bid_open AND bid_low <= bid_close AND
        bid_high >= bid_open AND bid_high >= bid_close
        ),
    CONSTRAINT chk_valid_ohlc_ask CHECK (
        ask_low <= ask_open AND ask_low <= ask_close AND
        ask_high >= ask_open AND ask_high >= ask_close
        ),
    CONSTRAINT chk_bid_ask_spread CHECK (ask_close >= bid_close),
    CONSTRAINT chk_quality_score CHECK (data_quality_score BETWEEN 0 AND 100)
);

-- Critical indexes for exchange_rates performance
CREATE INDEX IF NOT EXISTS idx_exchange_rates_lookup
    ON exchange_rates (currency_pair_id, rate_timestamp);

CREATE INDEX IF NOT EXISTS idx_exchange_rates_timestamp
    ON exchange_rates (rate_timestamp);

-- No partial index support, so we just index the same columns
CREATE INDEX IF NOT EXISTS idx_exchange_rates_recent
    ON exchange_rates (currency_pair_id, rate_timestamp);

-- =====================================================
-- 5. API MANAGEMENT & RATE LIMITING
-- =====================================================

-- API keys and clients
CREATE TABLE api_clients
(
    id                  IDENTITY PRIMARY KEY,
    client_id           UUID                     NOT NULL DEFAULT RANDOM_UUID(),
    api_key_hash        VARCHAR(64)              NOT NULL UNIQUE,
    client_name         VARCHAR(100)             NOT NULL,
    tier                VARCHAR(20)              NOT NULL DEFAULT 'basic' CHECK (tier IN ('basic', 'premium', 'enterprise')),
    rate_limit_per_hour INTEGER                  NOT NULL DEFAULT 1000,
    is_active           BOOLEAN                  NOT NULL DEFAULT TRUE,
    expires_at          TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    last_used_at        TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uk_client_id UNIQUE (client_id)
);

CREATE INDEX IF NOT EXISTS idx_api_clients_active
    ON api_clients (is_active, tier);

-- H2 cannot do partial indexes, so I just index the column
CREATE INDEX IF NOT EXISTS idx_api_clients_key_hash
    ON api_clients (api_key_hash);

-- Rate limiting tracking
CREATE TABLE api_rate_limits
(
    id            IDENTITY PRIMARY KEY,
    client_id     INTEGER                  NOT NULL,
    hour_bucket   TIMESTAMP WITH TIME ZONE NOT NULL,
    request_count INTEGER                  NOT NULL DEFAULT 0,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),

    CONSTRAINT fk_rate_limit_client FOREIGN KEY (client_id) REFERENCES api_clients (id),
    CONSTRAINT uk_client_hour_bucket UNIQUE (client_id, hour_bucket)
);

CREATE INDEX IF NOT EXISTS idx_rate_limits_lookup
    ON api_rate_limits (client_id, hour_bucket);

-- H2: no partial index support, so just index the column
CREATE INDEX IF NOT EXISTS idx_rate_limits_cleanup
    ON api_rate_limits (hour_bucket);

-- API request logs (for analytics and debugging)
CREATE TABLE api_request_logs
(
    id               IDENTITY PRIMARY KEY,
    request_id       UUID                     NOT NULL DEFAULT RANDOM_UUID(),
    client_id        INTEGER,
    endpoint         VARCHAR(200)             NOT NULL,
    method           VARCHAR(10)              NOT NULL,
    query_params     VARCHAR(1000), -- H2 doesn't have native JSON, using VARCHAR
    response_status  SMALLINT                 NOT NULL,
    response_time_ms INTEGER,
    ip_address       VARCHAR(45),   -- IPv6 compatible
    user_agent       TEXT,
    error_code       VARCHAR(50),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),

    CONSTRAINT fk_request_logs_client FOREIGN KEY (client_id) REFERENCES api_clients (id),
    CONSTRAINT uk_request_id UNIQUE (request_id)
);

CREATE INDEX idx_request_logs_client_time ON api_request_logs (client_id, created_at DESC);
CREATE INDEX idx_request_logs_endpoint ON api_request_logs (endpoint, created_at DESC);
CREATE INDEX idx_request_logs_status ON api_request_logs (response_status, created_at DESC);

-- =====================================================
-- 6. DATA QUALITY & MONITORING
-- =====================================================

-- Data quality metrics
CREATE TABLE data_quality_metrics
(
    id                 IDENTITY PRIMARY KEY,
    provider_id        INTEGER                  NOT NULL,
    currency_pair_id   INTEGER                  NOT NULL,
    date_checked       DATE                     NOT NULL,

    -- Quality indicators (0-100 scale)
    completeness_score SMALLINT,
    accuracy_score     SMALLINT,
    timeliness_score   SMALLINT,
    consistency_score  SMALLINT,

    -- Anomaly detection counts
    outlier_count      INTEGER                           DEFAULT 0,
    gap_count          INTEGER                           DEFAULT 0,
    spike_count        INTEGER                           DEFAULT 0,

    created_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),

    CONSTRAINT fk_quality_provider FOREIGN KEY (provider_id) REFERENCES data_providers (id),
    CONSTRAINT fk_quality_pair FOREIGN KEY (currency_pair_id) REFERENCES currency_pairs (id),
    CONSTRAINT uk_quality_provider_pair_date UNIQUE (provider_id, currency_pair_id, date_checked),
    CONSTRAINT chk_quality_scores CHECK (
        (completeness_score IS NULL OR completeness_score BETWEEN 0 AND 100) AND
        (accuracy_score IS NULL OR accuracy_score BETWEEN 0 AND 100) AND
        (timeliness_score IS NULL OR timeliness_score BETWEEN 0 AND 100) AND
        (consistency_score IS NULL OR consistency_score BETWEEN 0 AND 100)
        )
);

CREATE INDEX idx_quality_provider_date ON data_quality_metrics (provider_id, date_checked DESC);
CREATE INDEX idx_quality_pair_date ON data_quality_metrics (currency_pair_id, date_checked DESC);

-- System health monitoring
CREATE TABLE system_metrics
(
    id           IDENTITY PRIMARY KEY,
    metric_name  VARCHAR(50)              NOT NULL,
    metric_value DECIMAL(15, 4)           NOT NULL,
    metric_unit  VARCHAR(20),
    tags         VARCHAR(500), -- JSON-like string for H2
    recorded_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP()
);

CREATE INDEX idx_system_metrics_name_time ON system_metrics (metric_name, recorded_at DESC);

-- =====================================================
-- 7. AUDIT TRAIL
-- =====================================================

-- Audit trail for sensitive operations
CREATE TABLE audit_log
(
    id         IDENTITY PRIMARY KEY,
    table_name VARCHAR(50)              NOT NULL,
    operation  VARCHAR(10)              NOT NULL,
    record_id  VARCHAR(50),
    old_values TEXT, -- JSON string representation
    new_values TEXT, -- JSON string representation
    user_id    INTEGER,
    client_id  INTEGER,
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP(),

    CONSTRAINT fk_audit_client FOREIGN KEY (client_id) REFERENCES api_clients (id)
);

CREATE INDEX idx_audit_table_time ON audit_log (table_name, created_at DESC);
CREATE INDEX idx_audit_user_time ON audit_log (user_id, created_at DESC);
CREATE INDEX idx_audit_client_time ON audit_log (client_id, created_at DESC);

-- =====================================================
-- 8. SAMPLE DATA INSERTION
-- =====================================================

-- Insert sample currencies
INSERT INTO currencies (code, name, numeric_code, minor_unit)
VALUES ('USD', 'United States Dollar', 840, 2),
       ('EUR', 'Euro', 978, 2),
       ('GBP', 'British Pound Sterling', 826, 2),
       ('JPY', 'Japanese Yen', 392, 0),
       ('CHF', 'Swiss Franc', 756, 2),
       ('CAD', 'Canadian Dollar', 124, 2),
       ('AUD', 'Australian Dollar', 36, 2),
       ('CNY', 'Chinese Yuan', 156, 2);

-- Insert sample currency pairs
INSERT INTO currency_pairs (base_currency, quote_currency, min_spread, max_spread)
VALUES ('EUR', 'USD', 0.00010, 0.00050),
       ('GBP', 'USD', 0.00015, 0.00060),
       ('USD', 'JPY', 0.01000, 0.05000),
       ('USD', 'CHF', 0.00012, 0.00055),
       ('EUR', 'GBP', 0.00015, 0.00065),
       ('AUD', 'USD', 0.00020, 0.00070),
       ('USD', 'CAD', 0.00015, 0.00060),
       ('USD', 'CNY', 0.00100, 0.00500);

-- Insert sample data provider
INSERT INTO data_providers (name, api_endpoint, priority, rate_limit_per_hour)
VALUES ('Central Bank Feed', 'https://api.centralbank.com/rates', 10, 10000),
       ('Market Data Pro', 'https://api.marketdatapro.com/forex', 20, 5000),
       ('Financial Times Feed', 'https://api.ft.com/rates', 30, 2000);

-- Insert sample API client
INSERT INTO api_clients (api_key_hash, client_name, tier, rate_limit_per_hour)
VALUES ('e1faffb3e614e6c2fba74296962386b7c6c8bbd022b3bfb8f7f85e0b96d7c6a5', 'Demo', 'basic', 1000);

-- =====================================================
-- 9. PERFORMANCE VIEWS
-- =====================================================

-- View for recent exchange rates with currency names
CREATE VIEW v_recent_exchange_rates AS
SELECT er.id,
       c1.code as base_currency,
       c1.name as base_currency_name,
       c2.code as quote_currency,
       c2.name as quote_currency_name,
       er.rate_timestamp,
       er.bid_close,
       er.ask_close,
       er.mid_rate,
       er.spread,
       er.volume,
       dp.name as provider_name
FROM exchange_rates er
         JOIN currency_pairs cp ON er.currency_pair_id = cp.id
         JOIN currencies c1 ON cp.base_currency = c1.code
         JOIN currencies c2 ON cp.quote_currency = c2.code
         LEFT JOIN data_providers dp ON er.provider_id = dp.id
WHERE er.rate_timestamp > DATEADD('DAY', -7, CURRENT_TIMESTAMP())
ORDER BY er.rate_timestamp DESC;

-- View for API performance monitoring
CREATE VIEW v_api_performance_hourly AS
SELECT ac.client_name,
       ac.tier,
       FORMATDATETIME(arl.created_at, 'yyyy-MM-dd HH')        as hour_bucket,
       COUNT(*)                                               as total_requests,
       AVG(arl.response_time_ms)                              as avg_response_time_ms,
       COUNT(CASE WHEN arl.response_status >= 400 THEN 1 END) as error_count,
       COUNT(CASE WHEN arl.response_status = 200 THEN 1 END)  as success_count
FROM api_request_logs arl
         JOIN api_clients ac ON arl.client_id = ac.id
WHERE arl.created_at > DATEADD('DAY', -1, CURRENT_TIMESTAMP())
GROUP BY ac.client_name, ac.tier, FORMATDATETIME(arl.created_at, 'yyyy-MM-dd HH')
ORDER BY hour_bucket DESC;

-- =====================================================
-- END OF DDL SCRIPT
-- =====================================================

-- Final verification query to check all tables are created
SELECT TABLE_NAME, TABLE_TYPE
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'PUBLIC'
ORDER BY TABLE_NAME;