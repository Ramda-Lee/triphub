-- 숙소 서비스 데이터베이스 스키마

CREATE TABLE accommodation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description TEXT,
    address VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE accommodation_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accommodation_id BIGINT NOT NULL,
    image_url VARCHAR(255),
    image_order INT,
    type VARCHAR(20),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id)
);

CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    accommodation_id BIGINT NOT NULL,
    name VARCHAR(255),
    type VARCHAR(50),
    capacity INT,
    base_price DECIMAL(10, 2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id)
);

CREATE TABLE room_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    image_url VARCHAR(255),
    image_order INT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    date DATE,
    is_available BOOLEAN DEFAULT TRUE,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE pricing_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    price_multiplier DECIMAL(4, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE wishlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    accommodation_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (accommodation_id) REFERENCES accommodation(id)
); 