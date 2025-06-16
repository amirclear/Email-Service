package aut.ap.service;

import aut.ap.framework.SingletonSessionFactory;
import aut.ap.model.Email;
import aut.ap.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserService {
    static Scanner sc = new Scanner(System.in);
    static final String DOMAIN = "@milou.com";

    public static void SignUp() {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine().toLowerCase();
        if (!email.endsWith(DOMAIN)) {
            email += DOMAIN;
        }
        String password;
        while (true) {
            System.out.println("Password (at least 8 characters): ");
            password = sc.nextLine().trim();
            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters long. Please try again.");
            } else {
                break;
            }
        }

        User userExist = findByEmail(email);
        if (userExist != null) {
            throw new RuntimeException("Email already exists");
        }

        persist(name, email, password);
        System.out.println("Your new account is created. ");
        System.out.println("--Please login Again--");
    }

    public static void Login() {
        System.out.print("Email: ");
        String email = sc.nextLine().toLowerCase();
        if (!email.contains("@")) {
            email += DOMAIN;
        } else if (!email.endsWith(DOMAIN)) {

        }
        System.out.print("Password: ");
        String password = sc.nextLine();
        User user = findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Welcome back, " + user.getName());
            System.out.println();
            EmailService.showUnreadEmails(user);
            LoginPage(user);
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public static User findByEmail(String email) {
        return SingletonSessionFactory.get()
                .fromTransaction(session ->
                        session.createQuery("FROM User WHERE email = :email", User.class)
                                .setParameter("email", email)
                                .uniqueResult()
                );
    }

    public static void persist(String name, String email, String password) {
        User user = new User(name, email, password);
        SingletonSessionFactory.get()
                .inTransaction(session -> {
                    session.persist(user);
                });
    }

    public static void LoginPage(User user) {
        boolean stayLoggedIn = true;
        while (stayLoggedIn) {
            System.out.println("[S]end, [V]iew, [R]eply, [F]orward, [U]pdate Account, [Q]uit: ");
            String choice = sc.nextLine().trim().toLowerCase();
            switch (choice) {
                case "send", "s":
                    List<User> recipientUsers = new ArrayList<>();
                    while (true) {
                        System.out.println("Recipients (separated by ','): ");
                        String recipients = sc.nextLine();
                        String[] allRecipients = recipients.split(",");
                        boolean allValid = true;
                        recipientUsers.clear();

                        for (int i = 0; i < allRecipients.length; i++) {
                            String recipientEmail = allRecipients[i].trim().toLowerCase();
                            if (!recipientEmail.endsWith(DOMAIN)) {
                                recipientEmail += DOMAIN;
                            }
                            User recipientUser = findByEmail(recipientEmail);
                            if (recipientUser != null) {
                                recipientUsers.add(recipientUser);
                            } else {
                                System.out.println("User with email '" + recipientEmail + "' not found.");
                                System.out.println("Options: [1] Exit, [2] Re-enter recipients");
                                String option = sc.nextLine().trim();

                                if (option.equals("1")) {
                                    System.out.println("Exiting send email.");
                                    return;
                                } else if (option.equals("2")) {
                                    System.out.println("Please re-enter recipients, " + user.getName() + ":");
                                    allValid = false;
                                    break;
                                } else {
                                    System.out.println("Invalid option, exiting send email.");
                                    return;
                                }
                            }
                        }
                        if (allValid) {
                            break;
                        }
                    }
                    System.out.println("Subject: ");
                    String subject = sc.nextLine();
                    System.out.println("Body: ");
                    String body = sc.nextLine();

                    EmailService.sendEmail(user, recipientUsers, subject, body);
                    break;

                case "v", "view":
                    System.out.println("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode: ");
                    String view = sc.nextLine().trim().toUpperCase();
                    if (view.equals("A")) {
                        EmailService.showAllEmails(user);
                    } else if (view.equals("U")) {
                        EmailService.showUnreadEmails(user);
                    } else if (view.equals("S")) {
                        EmailService.showSentEmails(user);
                    } else if (view.equals("C")) {
                        System.out.println("Enter the code: ");
                        String code = sc.nextLine();
                        EmailService.showEmailByCode(code, user);
                    } else {
                        System.out.println("Invalid view choice");
                    }
                    break;

                case "reply", "r":
                    replyEmailFlow(user);
                    break;

                case "f", "forward":
                    forwardEmailFlow(user);
                    break;

                case "l", "logout":
                    System.out.println("Logging out. Goodbye " + user.getName());
                    stayLoggedIn = false;
                    break;

                case "u", "update", "update account":
                    updateAccountSettings(user);
                    break;

                case "q", "quit":
                    System.out.println("Logging out. Bye!");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }


    public static void updateAccountSettings(User user) {
        while (true) {
            System.out.println("Update [N]ame, [P]assword, [B]ack: ");
            String updateChoice = sc.nextLine().trim().toLowerCase();

            switch (updateChoice) {
                case "n", "name":
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine().trim();
                    if (!newName.isEmpty()) {
                        user.setName(newName);
                        saveOrUpdateUser(user);
                        System.out.println("Name updated successfully.");
                    } else {
                        System.out.println("Name cannot be empty.");
                    }
                    break;
                case "p", "password":
                    while (true) {
                        System.out.print("Enter new password (min 8 chars): ");
                        String newPassword = sc.nextLine().trim();
                        if (newPassword.length() >= 8) {
                            user.setPassword(newPassword);
                            saveOrUpdateUser(user);
                            System.out.println("Password updated successfully.");
                            break;
                        } else {
                            System.out.println("Password too short. Try again.");
                        }
                    }
                    break;
                case "b", "back":
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void saveOrUpdateUser(User user) {
        SingletonSessionFactory.get()
                .inTransaction(session -> {
                    session.merge(user);
                });
    }

    private static void replyEmailFlow(User user) {
        System.out.println("Enter the code of the email you want to reply to: ");
        String code = sc.nextLine().trim();
        Email original = EmailService.getEmailByCodeAndUser(code, user);

        if (original == null) {
            System.out.println("Email not found or you don't have access.");
            return;
        }

        System.out.println("Original Subject: " + original.getSubject());
        System.out.println("Enter your reply: ");
        String body = sc.nextLine();

        String replySubject = original.getSubject().startsWith("RE:") ?
                original.getSubject() : "RE: " + original.getSubject();

        EmailService.sendEmail(user, List.of(original.getSender()), replySubject, body);
        System.out.println("Reply sent.");
    }

    private static void forwardEmailFlow(User user) {
        System.out.println("Enter the code of the email you want to forward: ");
        String code = sc.nextLine().trim();
        Email original = EmailService.getEmailByCodeAndUser(code, user);

        if (original == null) {
            System.out.println("Email not found or access denied.");
            return;
        }

        List<User> recipientUsers = new ArrayList<>();
        while (true) {
            System.out.println("Recipients for forwarding (separated by ','): ");
            String recipients = sc.nextLine();
            String[] allRecipients = recipients.split(",");
            boolean allValid = true;
            recipientUsers.clear();

            for (String email : allRecipients) {
                email = email.trim().toLowerCase();
                if (!email.endsWith(DOMAIN)) {
                    email += DOMAIN;
                }
                User recipient = findByEmail(email);
                if (recipient != null) {
                    recipientUsers.add(recipient);
                } else {
                    System.out.println("User with email '" + email + "' not found.");
                    System.out.println("Options: [1] Exit, [2] Re-enter recipients");
                    String option = sc.nextLine().trim();
                    if (option.equals("1")) {
                        return;
                    } else if (option.equals("2")) {
                        allValid = false;
                        break;
                    } else {
                        return;
                    }
                }
            }
            if (allValid) break;
        }

        System.out.println("Add a message (optional): ");
        String message = sc.nextLine().trim();

        String forwardSubject = original.getSubject().startsWith("FWD:") ?
                original.getSubject() : "FWD: " + original.getSubject();

        String forwardedHeader = "--- Forwarded message ---\nFrom: " +
                original.getSender().getEmail() + "\nSubject: " + original.getSubject() +
                "\n\n" + original.getBody();

        String forwardBody = message.isBlank()
                ? forwardedHeader
                : message + "\n\n" + forwardedHeader;

        EmailService.sendEmail(user, recipientUsers, forwardSubject, forwardBody);
        System.out.println("Email forwarded.");
    }


}
