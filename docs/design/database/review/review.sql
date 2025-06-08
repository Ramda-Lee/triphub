-- 리뷰 서비스 데이터베이스 스키마

CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    accommodation_id BIGINT,
    reservation_id BIGINT,
    content TEXT,
    rating DECIMAL(3, 2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE review_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    image_url VARCHAR(255),
    image_order INT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES review(id)
);

CREATE TABLE rating (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accommodation_id BIGINT,
    average_rating DECIMAL(3, 2),
    total_reviews INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE review_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accommodation_id BIGINT,
    days_after_checkout INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
); 