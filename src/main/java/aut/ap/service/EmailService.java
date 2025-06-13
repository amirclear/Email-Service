package aut.ap.service;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.Recipient;
import aut.ap.model.User;
import com.mysql.cj.Session;

import java.time.LocalDateTime;
import java.util.List;

public class EmailService {

    public static void sendEmail(User sender, List<User> recipients, String subject, String body) {
        SingletonSessionFactory.get().inTransaction(session -> {

            Email email = new Email(sender, subject, body, LocalDateTime.now());
            session.persist(email);

            for (User recipientUser : recipients) {
                Recipient recipient = new Recipient(email, recipientUser);
                session.persist(recipient);
            }

            System.out.println("Successfully sent email");
            System.out.println("Code: " + email.getEmailCode());
        });
    }

    public static void showUnreadEmails(User user) {
        List<Email> list = SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery(
                                        "SELECT r.email FROM Recipient r " +
                                                "JOIN FETCH r.email.sender " +
                                                "WHERE r.recipientUser.id = :userId AND r.isRead = false", Email.class)
                                .setParameter("userId", user.getId())
                                .getResultList());

        System.out.println("Unread emails: ");
        for (Email email : list) {
            System.out.println(email);
        }
    }




    public static void showAllEmails(User user) {
        List<Email> emails = SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery(
                                        "SELECT DISTINCT e FROM Email e " +
                                                "JOIN FETCH e.sender " +
                                                "JOIN e.recipients r " +
                                                "WHERE r.recipientUser.id = :userId " +
                                                "ORDER BY e.sentAt DESC",
                                        Email.class)
                                .setParameter("userId", user.getId())
                                .getResultList()
                );

        System.out.println("All received emails: ");
        for (Email email : emails) {
            System.out.println(email);
        }
    }


    public static void showSentEmails(User user) {
        List<Email> emails = SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery(
                                        "FROM Email e " +
                                                "WHERE e.sender.id = :userId " +
                                                "ORDER BY e.sentAt DESC",
                                        Email.class)
                                .setParameter("userId", user.getId())
                                .getResultList()
                );

        System.out.println("Sent Emails:");
        for (Email email : emails) {
            System.out.println(email);
        }
    }

    public static void showEmailByCode(String emailCode, User user) {
        Email email = SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery(
                                        "FROM Email e " +
                                                "WHERE e.emailCode = :code " +
                                                "AND (e.sender.id = :userId OR EXISTS (" +
                                                "SELECT 1 FROM Recipient r " +
                                                "WHERE r.email.id = e.id AND r.recipientUser.id = :userId))",
                                        Email.class)
                                .setParameter("code", emailCode)
                                .setParameter("userId", user.getId())
                                .uniqueResult()
                );

        if (email == null) {
            System.out.println("You cannot read this email.");
            return;
        }

        List<String> recipients = SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery(
                                        "SELECT r.recipientUser.email FROM Recipient r WHERE r.email.id = :emailId",
                                        String.class)
                                .setParameter("emailId", email.getId())
                                .getResultList()
                );

        System.out.println("Code: " + email.getEmailCode());
        System.out.println("Recipient(s): " + String.join(", ", recipients));
        System.out.println("Subject: " + email.getSubject());
        System.out.println("Date: " + email.getSentAt().toLocalDate());
        System.out.println();
        System.out.println(email.getBody());
    }

    public static Email getEmailByCodeAndUser(String code, User user) {
        try (Session session = HibernateUtil.getSession()) {
            return session.createQuery("""
                select e from Email e
                join Recipient r on e.id = r.email.id
                where e.emailCode = :code and r.recipientUser = :user
                """, Email.class)
                    .setParameter("code", code)
                    .setParameter("user", user)
                    .uniqueResult();
        }
    }

}
