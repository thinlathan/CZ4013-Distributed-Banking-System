import objects.Currency;
import java.util.Scanner;

import static functionalities.ClientInterface.*;
import static utils.Constants.*;
import static utils.ReadingInputs.readCurrencyInput;
import static utils.ReadingInputs.readNameInput;

public class Client {
    public static void main(String[] args) {
        boolean end = false;
        //If atLeastonce schematic is used, set atLeaseOnce as true
        boolean atLeastOnce=true;

        System.out.printf("%20s\n","Welcome to CZ4013 Bank!");
        while (!end) {
            try {
                System.out.printf("%s\n", "What would you like to do? (Key in the number for your choice)");
                System.out.printf("%s\n", "1. Open a new account");
                System.out.printf("%s\n", "2. Deposit money");
                System.out.printf("%s\n", "3. Withdraw money");
                System.out.printf("%s\n", "4. Close an existing account");
                System.out.printf("%s\n", "5. Transfer money");
                System.out.printf("%s\n", "8. Show current account balance");
                System.out.printf("%s\n", "0. Exit");

                Scanner scanner = new Scanner(System.in);
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case ACC_OPENING_CODE: { 
                        System.out.println("Opening a new account...");
                        String name = readNameInput();
                        Currency chosenCurrency = readCurrencyInput();
                        break;
                    }
                    case DEPOSIT_MONEY_CODE:{
                        System.out.println("Depositing money...");                    
                        String name = readNameInput();
                        System.out.println("Enter your account number: ");
                        int accNumber = scanner.nextInt();
                        scanner.nextLine();  
                        System.out.println("Enter the password: ");
                        String password = scanner.nextLine();
                        Currency currency = readCurrencyInput();
                        System.out.println("Enter the amount to deposit: ");
                        double deposit = scanner.nextDouble();
                        scanner.nextLine(); 
                        double balance = depositMoney(name,accNumber,password,currency,deposit,atLeastOnce);  
                        //double balance = depositMoney("John", 123, "123", Currency.SGD, 1000, atLeastOnce);                   
                        break;
                    }
                    case WITHDRAW_MONEY_CODE:{
                        System.out.println("Withdrawing money...");                  
                        String name = readNameInput();
                        System.out.println("Enter your account number: ");
                        int accNumber = scanner.nextInt();
                        scanner.nextLine();  
                        System.out.println("Enter the password: ");
                        String password = scanner.nextLine();
                        Currency currency = readCurrencyInput();
                        System.out.println("Enter the amount to withdraw: ");
                        double withdraw = scanner.nextDouble();
                        scanner.nextLine(); 
                        double balance = withdrawMoney(name,accNumber,password,currency,withdraw,atLeastOnce);                        
                        break;
                    }
                    case ACC_CLOSING_CODE:
                        System.out.println("close acc");
                        break;

                    case TRANSFER_MONEY_CODE:{
                        System.out.println("Transfering money...");                
                        String name = readNameInput();
                        System.out.println("Enter your account number: ");
                        int accNumber = scanner.nextInt();
                        scanner.nextLine();  
                        System.out.println("Enter the password: ");
                        String password = scanner.nextLine();
                        System.out.println("Enter the account number to transfer to: ");
                        int toAccNumber = scanner.nextInt();
                        scanner.nextLine();  
                        Currency currency = readCurrencyInput();
                        System.out.println("Enter the amount to transfer: ");
                        double transfer = scanner.nextDouble();
                        scanner.nextLine(); 
                        double balance = transferMoney(name,accNumber,password,toAccNumber,currency,transfer,atLeastOnce);                        
                        break;
                    }
                    case ACC_BALANCE_CODE:
                        System.out.println("query acc balance");
                        break;
                    
                    case 0:
                        System.out.println("Thank you for banking with us, goodbye!");
                        end = true;
                        break;
                    default:
                        System.out.printf("%s\n", "Unavailable choice entered");
                        break;
                }
            } catch (NumberFormatException invalidFormat) {
                System.out.println("Error: Invalid input entered");
            } catch (Exception e) {
                throw new IllegalArgumentException("Unknown error\n");
            }
        }
    }
}
