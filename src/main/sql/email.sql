USE ap_db;
CREATE TABLE emails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    code VARCHAR(6) NOT NULL UNIQUE,
    sent_at DATETIME,
    FOREIGN KEY (sender_id) REFERENCES users(id)
);




