CREATE TABLE product (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255),
                         price DECIMAL(10,2),
                         brand VARCHAR(255),
                         category VARCHAR(255),
                         quantity INT,
                         image_name VARCHAR(255),
                         image_type VARCHAR(100),
                         image_data BYTEA
);