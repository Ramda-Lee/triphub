-- 결제 서비스 데이터베이스 스키마

CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT,
    amount DECIMAL(10, 2),
    payment_method VARCHAR(50),
    status VARCHAR(20),
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    type VARCHAR(20),
    provider VARCHAR(50),
    last_four_digits VARCHAR(4),
    expiry_date VARCHAR(5),
    is_default BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE refund (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(10, 2),
    reason VARCHAR(255),
    status VARCHAR(20),
    refund_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment(id)
);

CREATE TABLE payment_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    status VARCHAR(20),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment(id)
);

CREATE TABLE payment_failure (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    error_code VARCHAR(50),
    error_message TEXT,
    retry_count INT DEFAULT 0,
    last_retry_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment(id)
); 