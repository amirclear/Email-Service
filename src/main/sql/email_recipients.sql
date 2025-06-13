USE ap_db;
CREATE TABLE recipients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email_id INT NOT NULL,
    recipient_user_id INT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (email_id) REFERENCES emails(id),
    FOREIGN KEY (recipient_user_id) REFERENCES users(id)
);



