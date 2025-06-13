package aut.ap;

import java.util.Scanner;
import aut.ap.service.UserService;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choice;
        do {
            System.out.println("[L]ogin, [S]ign up, [E]xit: ");
            choice = sc.nextLine().trim().toLowerCase();
            switch (choice) {
                case "s", "sign up":
                    UserService.SignUp();
                    break;
                case "l", "login":
                    UserService.Login();
                    break;
                case "e", "exit":
                    System.out.println("--Exiting...");
                    break;
                default:
                    System.out.println("Invalid Choice");
            }

        } while (!(choice.equals("e") || choice.equals("exit")));
    }
}
