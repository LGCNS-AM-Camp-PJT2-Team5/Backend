CREATE TABLE company (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         website_url VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE recruitment (
                             id BIGINT PRIMARY KEY,
                             title VARCHAR(255) NOT NULL,
                             requirements VARCHAR(255),
                             description TEXT,
                             deadline TIMESTAMP,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
