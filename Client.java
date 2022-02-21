import utils.MessageIDGenerator;

import java.util.*;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Client {
    public static final int MESSAGE_ID_LENGTH = 16;     // set the length of the message ID
    public static MessageIDGenerator gen = new MessageIDGenerator(MESSAGE_ID_LENGTH);   // create a new MessageIDGenerator

    public static void main(String[] args) {
        double balance;
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================");
        System.out.println("Welcome to bank ABC");
        System.out.println("Select the service you want to do:");
        System.out.println("1. Open new account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");

        System.out.print("Your choice: ");
        String message = scanner.nextLine();
        int serviceType = Integer.parseInt(message);
        System.out.println();

        switch(serviceType){
            case 1: //Create acc
            System.out.println("You have selected open account");
            int bankAcc = createAccount("John Smith", Currency.NZD, "P@ssword", "1000.00");
            System.out.println("Acc number: " + bankAcc);
            break;

            case 2: // Deposit money
            /*
            System.out.println("You have selected deposit money");
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your account number: ");
            int accNumber = scanner.nextInt();
            scanner.nextLine();  
            System.out.print("Enter the password: ");
            String password = scanner.nextLine();
            System.out.print("Enter the currency type: ");
            String temp = scanner.nextLine();
            Currency currency=Currency.valueOf(temp);
            System.out.print("Enter the amount to deposit: ");
            double deposit = scanner.nextDouble();
            scanner.nextLine();  

            double balance = depositMoney(name,accNumber,password,currency,deposit);
            */
            balance = depositMoney("John Smith",1243065256,"P@ssword", Currency.NZD, 1000);
            System.out.print("The updated balance is: "+balance);
            break;

            case 3: //Withdraw Money
            /*
            System.out.println("You have selected withdraw money");
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.print("Enter your account number: ");
            int accNumber = scanner.nextInt();
            scanner.nextLine();  
            System.out.print("Enter the password: ");
            String password = scanner.nextLine();
            System.out.print("Enter the currency type: ");
            String temp = scanner.nextLine();
            Currency currency=Currency.valueOf(temp);
            System.out.print("Enter the amount to withdraw: ");
            double withdraw = scanner.nextDouble();
            scanner.nextLine();  

            double balance = depositMoney(name,accNumber,password,currency,withdraw);
            */
            balance = depositMoney("John Smith",1243065256,"P@ssword", Currency.NZD, 1000);
            System.out.print("The updated balance is: "+balance);
            break;
        }
        
    }
    public static double withdrawMoney(String name, int accNumber,String password,Currency currency, double withdraw) {
        int nameLength = name.length();
        int accNumberLength = Integer.toString(accNumber).length();
        int passwordLength = password.length();
        int currencyLength = currency.toString().length();
        int withdrawLength = String.valueOf(withdraw).length();
        
        byte[] withdrawMoneyByteArray = ByteBuffer.allocate(4).putInt(3).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] accNumberLengthByteArray = ByteBuffer.allocate(4).putInt(accNumberLength).array();
        byte[] accNumberByteArray = convertStringToByteArray(Integer.toString(accNumber));
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] withdrawLengthByteArray = ByteBuffer.allocate(4).putInt(withdrawLength).array();
        byte[] withdrawByteArray = convertStringToByteArray(String.valueOf(withdraw));
        
        String messageID = gen.nextString();
        System.out.println("MessageId: "+messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);

        byte[] marshall = concatWithCopy(messageIDArray, withdrawMoneyByteArray, nameLengthByteArray, nameByteArray,accNumberLengthByteArray,accNumberByteArray,passwordLengthByteArray, passwordByteArray,currencyLengthByteArray, currencyByteArray,withdrawLengthByteArray,withdrawByteArray);

        System.out.print("Marshalled data:");
        for (byte c : marshall) {
            System.out.printf("%02X ", c);      // printing to show the marshalled data on console
        }
        System.out.println();

        byte[] reply = sendRequest(marshall);
        ByteBuffer choiceBuffer = ByteBuffer.allocate(1000);
        choiceBuffer.put(reply);
        choiceBuffer.rewind();
        return choiceBuffer.getDouble();
    }

    public static double depositMoney(String name, int accNumber,String password,Currency currency, double deposit) {
        int nameLength = name.length();
        int accNumberLength = Integer.toString(accNumber).length();
        int passwordLength = password.length();
        int currencyLength = currency.toString().length();
        int depositLength = String.valueOf(deposit).length();
        
        byte[] depositMoneyByteArray = ByteBuffer.allocate(4).putInt(2).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] accNumberLengthByteArray = ByteBuffer.allocate(4).putInt(accNumberLength).array();
        byte[] accNumberByteArray = convertStringToByteArray(Integer.toString(accNumber));
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] depositLengthByteArray = ByteBuffer.allocate(4).putInt(depositLength).array();
        byte[] depositByteArray = convertStringToByteArray(String.valueOf(deposit));
        
        String messageID = gen.nextString();
        System.out.println("MessageId: "+messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);

        byte[] marshall = concatWithCopy(messageIDArray, depositMoneyByteArray, nameLengthByteArray, nameByteArray,accNumberLengthByteArray,accNumberByteArray,passwordLengthByteArray, passwordByteArray,currencyLengthByteArray, currencyByteArray,depositLengthByteArray,depositByteArray);

        System.out.print("Marshalled data:");
        for (byte c : marshall) {
            System.out.printf("%02X ", c);      // printing to show the marshalled data on console
        }
        System.out.println();

        byte[] reply = sendRequest(marshall);
        ByteBuffer choiceBuffer = ByteBuffer.allocate(1000);
        choiceBuffer.put(reply);
        choiceBuffer.rewind();
        return choiceBuffer.getDouble();
    }

    public enum Currency {
        NZD,
        SGD,
        USD
    }

    /**
     * Function to process account creation
     *
     * @param name              String containing name of the customer
     * @param currency          Enum to represent the currency of the bank account
     * @param password          String to represent password of the bank account
     * @param initialAccBalance String to represent inital account balance
     * @return the bank account number generated by the server
     */
    public static int createAccount(String name, Currency currency, String password, String initialAccBalance) {
        int nameLength = name.length();
        int currencyLength = currency.toString().length();
        int accBalanceLength = initialAccBalance.length();
        int passwordLength = password.length();

        byte[] accCreationByteArray = ByteBuffer.allocate(4).putInt(1).array();
        byte[] nameLengthByteArray = ByteBuffer.allocate(4).putInt(nameLength).array();
        byte[] nameByteArray = convertStringToByteArray(name);
        byte[] currencyLengthByteArray = ByteBuffer.allocate(4).putInt(currencyLength).array();
        byte[] currencyByteArray = convertStringToByteArray(currency.toString());
        byte[] passwordLengthByteArray = ByteBuffer.allocate(4).putInt(passwordLength).array();
        byte[] passwordByteArray = convertStringToByteArray(password);
        byte[] accBalanceLengthByteArray = ByteBuffer.allocate(4).putInt(accBalanceLength).array();
        byte[] accBalanceArray = convertStringToByteArray(initialAccBalance);

        String messageID = gen.nextString();
        System.out.println(messageID);
        byte[] messageIDArray = convertStringToByteArray(messageID);

        byte[] marshall = concatWithCopy(messageIDArray, accCreationByteArray, nameLengthByteArray, nameByteArray, currencyLengthByteArray, currencyByteArray, passwordLengthByteArray, passwordByteArray, accBalanceLengthByteArray, accBalanceArray);
        for (byte c : marshall) {
            System.out.printf("%02X ", c);      // printing to show the marshalled data on console
        }
        System.out.println();
        byte[] reply = sendRequest(marshall);
        //TLH: To add timeout, add in this while loop below
        while(reply==null){
            reply = sendRequest(marshall);
        }

        ByteBuffer choiceBuffer = ByteBuffer.allocate(1000);
        choiceBuffer.put(reply);
        choiceBuffer.rewind();
        return choiceBuffer.getInt();
    }

    /**
     * Function to send data in the form of a byte array to the server for processing
     *
     * @param marshall the byte array to be sent over
     * @return the reply message from the server
     */
    private static byte[] sendRequest(byte[] marshall) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            InetAddress aHost = InetAddress.getByName("localhost");     // translate user-specified hostname to Internet address
            int serverPort = 6789;                                      // a port number to construct a packet

            DatagramPacket request = new DatagramPacket(marshall, marshall.length, aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];     // a buffer for receive
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.setSoTimeout(1000);
            boolean received=false;
            while(received==false){
                    try{
                        aSocket.receive(reply);
                        received=true;
                        return reply.getData();
                    }
                    catch(SocketException e){
                    }
            }
        } catch (Exception e) {
            System.out.println();
        }
        System.out.println("Timeout! Resending...");
        return null;
    }

    /**
     * Function to concatenate byte arrays
     *
     * @param arrays The byte arrays to be concatenated
     * @return the concatenated byte array
     */
    private static byte[] concatWithCopy(byte[]... arrays) {

        Class<?> compType1 = arrays[0].getClass().getComponentType();
        int totalLength = 0;

        for (byte[] array : arrays) {
            for (int j = 0; j < array.length; j++)
                totalLength++;
        }
        byte[] result = (byte[]) Array.newInstance(compType1, totalLength);

        int startingPosition = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, startingPosition, array.length);
            startingPosition += array.length;
        }

        return result;
    }

    /**
     * Convert a String into a byte array whose size is a multiple of 4
     * If length of String is not a multiple of 4, '_' (ASCII code 95) is added as padding to make up the numbers
     *
     * @param str the String to be converted into byte array
     * @return the converted byte array
     */
    private static byte[] convertStringToByteArray(String str) {
        byte[] array = str.getBytes();
        if (array.length % 4 != 0) {
            int extra = 4 - (array.length % 4);
            byte[] newArray = new byte[array.length + extra];

            for (int i = 0; i < newArray.length; i++) {
                if (i < array.length) {
                    newArray[i] = array[i];
                } else {
                    newArray[i] = 95;
                }
            }
            return newArray;
        }
        return array;
    }
}
