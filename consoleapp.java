package mycompany.project1;

import java.util.List;
import java.util.Scanner;

public class consoleapp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Login login = new Login();

        System.out.println("================================");
        System.out.println("      USER REGISTRATION/LOGIN");
        System.out.println("================================");

        if (!authenticate(input, login)) {
            input.close();
            return;
        }

        User user = login.getRegisteredUser();
        Message.setSender(user.getFirstName() + " " + user.getLastName());

       
        int loaded = Message.loadStoredMessages();

        System.out.println();
        System.out.println("Welcome to QuickChat.");
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from file.");
        }

        int maxMessages = readInt(input, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int entered = 0;
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Please choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("> ");

            String choice = input.nextLine().trim();

            switch (choice) {
                case "1":
                    if (entered >= maxMessages) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                    } else {
                        sendFlow(input);
                        entered++;
                        if (entered == maxMessages) {
                            System.out.println();
                            System.out.println("All " + maxMessages + " messages have been entered.");
                            System.out.println("Total messages sent: " + Message.returnTotalMessages());
                        }
                    }
                    break;
                case "2":
                    System.out.println(Message.printMessages());
                    break;
                case "3":
                    System.out.println();
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    System.out.println(Message.printMessages());
                    running = false;
                    break;
                case "4":
                    storedMessagesMenu(input);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3 or 4.");
            }
        }

        input.close();
    }



    private static void storedMessagesMenu(Scanner input) {

      
        System.out.println();
        System.out.println("You have " + Message.getStoredMessageTexts().size() + " stored message(s).");
        System.out.println(Message.storedMessagesReport());

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- STORED MESSAGES -----");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a particular recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to main menu");
            int option = readInt(input, "> ", 1, 7);
            System.out.println();

            switch (option) {
                case 1:
                    System.out.println(Message.displaySenderAndRecipient());
                    break;
                case 2:
                    System.out.println("Longest stored message: " + Message.longestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    System.out.println(Message.searchByMessageID(input.nextLine().trim()));
                    break;
                case 4:
                    System.out.print("Enter the recipient cell number: ");
                    String cell = input.nextLine().trim();
                    List<String> found = Message.searchByRecipient(cell);
                    if (found.isEmpty()) {
                        System.out.println("No messages found for " + cell + ".");
                    } else {
                        for (String text : found) {
                            System.out.println("\"" + text + "\"");
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteByHash(input.nextLine().trim()));
                    break;
                case 6:
                    System.out.println(Message.storedMessagesReport());
                    break;
                default:
                    back = true;
            }
        }
    }


    private static void sendFlow(Scanner input) {

        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            recipient = input.nextLine().trim();
            System.out.println(Message.validateRecipient(recipient));
            if (Message.isValidRecipient(recipient)) {
                break;
            }
        }

        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = input.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
            } else if (!Message.isValidLength(text)) {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println(Message.validateLength(text));
            } else {
                System.out.println("Message sent");
                break;
            }
        }

        Message message = new Message(recipient, text);

        System.out.println();
        System.out.println("What would you like to do with this message?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int option = readInt(input, "> ", 1, 3);

        System.out.println(message.SentMessage(option));

        if (option == 2) {
            String confirm = input.nextLine().trim();
            System.out.println(confirm.equals("0") ? "Message deleted." : "Message disregarded.");
        } else if (option == 1) {
            System.out.println();
            System.out.println(message.details());
        }
    }


    private static boolean authenticate(Scanner input, Login login) {
        while (true) {
            System.out.println();
            System.out.println("Are you registering or logging in? (R/L, Q to quit)");
            String choice = input.nextLine().trim();

            if (choice.equalsIgnoreCase("R")) {
                register(input, login);
            } else if (choice.equalsIgnoreCase("L")) {
                if (!login.isRegistered()) {
                    System.out.println("No registered user yet. Please register first.");
                } else if (signIn(input, login)) {
                    return true;
                }
            } else if (choice.equalsIgnoreCase("Q")) {
                return false;
            } else {
                System.out.println("NO ENTRY");
            }
        }
    }

    private static void register(Scanner input, Login login) {

        System.out.println();
        System.out.println("----- REGISTRATION -----");

        System.out.print("Enter first name: ");
        login.setFirstName(input.nextLine());

        System.out.print("Enter last name: ");
        login.setLastName(input.nextLine());

        System.out.print("Enter username: ");
        login.setUsername(input.nextLine());

        System.out.print("Enter password: ");
        login.setPassword(input.nextLine());

        System.out.print("Enter South African cell number (example +27838968976): ");
        login.setCellPhoneNumber(input.nextLine());

        System.out.println();
        System.out.println("----- VALIDATION -----");
        System.out.println(login.getUsernameMessage());
        System.out.println(login.getPasswordMessage());
        System.out.println(login.getCellPhoneMessage());
        System.out.println();
        System.out.println(login.registerUser());

        if (login.isRegistered()) {
            System.out.println("Registered successfully!");
        } else {
            System.out.println("Registration was unsuccessful.");
        }
    }

    private static boolean signIn(Scanner input, Login login) {

        System.out.println();
        System.out.println("----- LOGIN -----");

        System.out.print("Enter username: ");
        login.setEnteredUsername(input.nextLine());

        System.out.print("Enter password: ");
        login.setEnteredPassword(input.nextLine());

        System.out.println();
        System.out.println(login.returnLoginStatus());

        boolean ok = login.loginUser();
        System.out.println(ok ? "Logged in successfully!" : "Login unsuccessful.");
        return ok;
    }


    private static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Please enter a valid number.");
        }
    }
}