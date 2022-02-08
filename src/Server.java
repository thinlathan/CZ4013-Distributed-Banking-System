import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server started on port 6789");
        byte[] buffer = new byte[1000];
        byte[] reply = new byte[1000];
        byte[] data;

        while (true) {
            DatagramPacket request = receiveRequest(buffer);                // listen for requests from clients
            data = request.getData();                                       // get the data from the request DatagramPacket
            int action = byteArrayToInt(Arrays.copyOfRange(data, 0, 4));    // get the action to be taken by the server
            byte[] info = Arrays.copyOfRange(data, 4, data.length);         // get the information from the client

            /* switch statement to select the action to be taken by the server */
            switch (action) {
                case 1:
                    int accNumber = processAccCreation(info);                   // call the function to process the account creation
                    reply = ByteBuffer.allocate(4).putInt(accNumber).array();   // convert the account number into a byte[] array to be sent to client
            }

            sendReply(request, reply);      // send to client the reply message
            reply = new byte[1000];         // reset buffers
            buffer = new byte[1000];
        }
    }

    /**
     * Function to receive requests from the clients
     *
     * @param buffer byte array to be used in the DatagramPacket
     * @return DatagramPacket with data from client
     */
    public static DatagramPacket receiveRequest(byte[] buffer) {
        try (DatagramSocket aSocket = new DatagramSocket(6789)) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            return request;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    /**
     * Function to send reply from server to client
     *
     * @param request Original DatagramPacket from client
     * @param reply   byte array to for the reply DatagramPacket
     */
    public static void sendReply(DatagramPacket request, byte[] reply) {
        try (DatagramSocket aSocket = new DatagramSocket(6789)) {
            DatagramPacket replyPacket = new DatagramPacket(reply, reply.length,
                    request.getAddress(), request.getPort());
            aSocket.send(replyPacket);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static int choiceSelection(byte[] m) {
        ByteBuffer choiceBuffer = ByteBuffer.allocate(Integer.BYTES);
        byte[] slice = Arrays.copyOfRange(m, 0, 4);
        choiceBuffer.put(slice);
        choiceBuffer.rewind();
        return choiceBuffer.getInt();
    }

    /**
     * Utility function to convert byte array to an integer
     *
     * @param array byte array to be converted
     * @return integer converted from the given byte array
     */
    public static int byteArrayToInt(byte[] array) {
        ByteBuffer choiceBuffer = ByteBuffer.allocate(Integer.BYTES);
        choiceBuffer.put(array);
        choiceBuffer.rewind();
        return choiceBuffer.getInt();
    }

    //
    static class Pointer {
        int val;

        Pointer(int val) {
            this.val = val;
        }
    }

    /**
     * Function to process the account creation, which will unmarshall the data from the client
     *
     * @param request byte array containing data from the client
     * @return the account number of the newly opened bank account
     */
    public static int processAccCreation(byte[] request) {
        /*
            I'm basing my design off CORBA's Common Data Representation where it is assumed that sender and recipient have
            common knowledge of the order and types of the data items in a message.

            For e.g. if name: "John Smith", currency: "NZD", password: "P@ssword", initial acc balance: "1000.00"
            the byte array will be:

            00 00 00 01 00 00 00 0A 4A 6F 68 6E 20 53 6D 69 74 68 5F 5F 00 00 00 03 4E 5A 44 5F 00 00 00 08 50 40 73 73 77 6F 72 64 00 00 00 07 31 30 30 30 2E 30 30 5F

            00 00 00 01 = 1 (4 bytes to decide what action server will take in the switch statement shown above)

            00 00 00 0A = 10 (4 bytes to show the length of name)
            4A 6F 68 6E 20 53 6D 69 74 68 5F 5F = "John Smith__" (12 bytes to represent the name, with last 2 bytes as padding)

            00 00 00 03 = 3 (4 bytes to show length of currency code)
            4E 5A 44 5F = "NZD_" (4 bytes to represent the currency code with 1 byte as padding)

            00 00 00 08 = 8 (4 bytes to show length of password)
            50 40 73 73 77 6F 72 64 = "P@ssword" (8 bytes to represent the password)

            00 00 00 07 = 7 (4 bytes to show length of initial bank amount)
            31 30 30 30 2E 30 30 5F = "1000.00_" (7 bytes to represent the initial bank amount with 1 byte as padding)

         */

        Pointer val = new Pointer(0);

        String name = unmarshall(val, request);
        System.out.printf("name: %s\n", name);

        String currency = unmarshall(val, request);
        System.out.printf("currency: %s\n", currency);

        String password = unmarshall(val, request);
        System.out.printf("password: %s\n", password);

        String amtString = unmarshall(val, request);
        Double amt = round(Double.parseDouble(amtString), 2);
        System.out.printf("amt: $%.2f\n", amt);

        return (int) ((Math.random() * (Integer.MAX_VALUE - 1000000000)) + 1000000000);
    }

    /**
     * Function to unmarshall data
     *
     * @param point   Pointer class to keep track of pointer value so that the correct range can be selected from the request byte array
     * @param request byte array
     * @return String value
     */
    public static String unmarshall(Pointer point, byte[] request) {
        int start = point.val;
        int end = point.val + 4;

        int dataLength = byteArrayToInt(Arrays.copyOfRange(request, start, end));
        start = end;
        end += dataLength;

        String data = new String(Arrays.copyOfRange(request, start, end), StandardCharsets.UTF_8);
        if (dataLength % 4 != 0)
            end += (4 - (dataLength % 4));
        point.val = end;

        return data;
    }

    /**
     * Function for rounding when dealing with monetary amounts
     *
     * @param value  the double value
     * @param places the number of decimal places to round
     * @return the rounded value
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
