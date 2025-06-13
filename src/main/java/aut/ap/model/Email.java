package aut.ap.model;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToMany
    @JoinTable(
            name = "email_recipient",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "recipient_id")
    )
    private List<User> recipients = new ArrayList<>();

    private String subject;

    @Basic(optional = false)
    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    @Basic(optional = false)
    @Column(name = "code", length = 6, unique = true)
    private String emailCode;

    public Email() {
        // Default constructor required by JPA
    }

    public Email(User sender, String subject, String body, LocalDateTime sentAt) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
        this.emailCode = generateEmailCode();
    }

    public Integer getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }

    private static String generateEmailCode() {
        final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    @Override
    public String toString() {
        return "+ " + (sender != null ? sender.getEmail() : "unknown sender") + " - " + subject + " - " + emailCode;
    }
}
