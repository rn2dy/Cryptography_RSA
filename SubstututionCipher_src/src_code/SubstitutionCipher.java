import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Qiang Han
 * All rights reserved.
 */

public class SubstitutionCipher {
    private static HashMap<Character, Character> stable = null;
    private static HashMap<Integer, Integer> ttable = null;
    private static final String PIPE = "pipe.txt";
    
    public static void substitute(String msgFile, String ciphFile) throws FileNotFoundException, IOException{
        /* load the message and check if the inputs are valid input */
        FileReader reader = new FileReader(msgFile);
        FileWriter writer = new FileWriter(ciphFile);

        int data = reader.read();
        while(data != -1){
            Character x = stable.get((char)data); //getCipher((char)data);
            if (x == null){
                /*if there is undefined character just keep it! */
                writer.append((char)data);
            } else {
                writer.append(x);
            }
            data = reader.read();
        }
        writer.close();
        reader.close();
    }
    public static void loadSubstitutionTable(String keyFile) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(keyFile);
        int data;
        stable = new HashMap<Character, Character>(62);
        int lower = 97;
        int capital = 65;
        int number = 48;
        int k = 0;
        while(reader.ready()){
            /*if there some undefined character*/
            data = reader.read();
            if(data < 48 || data > 57 &&
                    data < 65 || data > 90 &&
                        data < 97 || data > 122){
                //System.out.println("!!"+(char)data);
                        break;
            }
            if(lower <= 122){
                //System.out.print((char)lower + ",");
                stable.put((char) lower, (char)data);
                lower++;
            }
            else if (capital <= 90){
                //System.out.print((char)capital + ",");
                stable.put((char)capital, (char)data);
                capital++;
            }
            else if (number <= 57){
                //System.out.print((char)number + ",");
                stable.put((char)number, (char)data);
                number++;
            }
            /*
            if(k==62){
                stable.put(' ', ' ');
                //System.out.print(stable.get(' '));
                reader.close();
                break;
            }
             * 
             */
            k++;
        }
        if(k!=62){
            System.err.print("Invalid substitution keys");
            reader.close();
        }
        reader.close();
    }
    public static void loadTranspositonTable(String keyFile) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(keyFile);
        int data;
        int k = 0;
        ttable = new HashMap<Integer, Integer>(8);
        while(reader.ready()){
            data  = reader.read() - 48;
            if(data > 0  && data < 9){
                ttable.put(k, data-1);
                k++;
            }
        }
        //System.out.println(ttable.toString());
        if(k != 8){
            reader.close();
            System.err.println("Incorrect transpositon key size!");
        }
        reader.close();
    }
    public static void transposition(String msgFile, String ciphFile) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(msgFile);
        FileWriter writer = new FileWriter(ciphFile);
        boolean done = false;
        char[] inbuf = new char[8];
        char[] outbuf = new char[8];
        if(!reader.ready()){
            System.err.println("There is no message!");
        }
        while(!done){
            for(int i = 0; i < 8 ; i++){
                if(!reader.ready()){
                    inbuf[i] = ' ';
                    done = true;
                } else {
                    inbuf[i] = (char)reader.read();
                }
            }
            int i = 1;
            for(char c : inbuf){
                //System.out.print("("+i+"."+c+")");
                i++;
            }
            for(int j = 0; j < 8 ; j++){
                outbuf[ttable.get(j)] = inbuf[j];
            }

            writer.write(outbuf);
        }
        reader.close();
        writer.close();
    }
    public static void cipherMix(String msgFile,String ciphFile) throws FileNotFoundException, IOException{
        substitute(msgFile, PIPE);
        transposition(PIPE, ciphFile);
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Scanner input = new Scanner(System.in);
        boolean done = false;
        String tablePath, tablePath1, tablePath2;
        String msgPath;
        String ciphMsg;
        while(!done){
            System.out.println("Enter \"1\" to Test Substitution Cipher");
            System.out.println("Enter \"2\" to Test Transposition Cipher");
            System.out.println("Enter \"3\" to Test Mix of the previous two Cipher method");
            System.out.println("Enter \"4\" to decrypt the substitution cipher");
            System.out.println("Enter \"5\" to decrypt the transposition cipher");
            System.out.println("Enter \"0\" to Quit");
            System.out.print("Enter here:");
            int choice = input.nextInt();
            input.nextLine();
           switch(choice){
            case 1:
                System.out.print("Enter the substitution key file path: ");
                tablePath = input.nextLine();
                System.out.println();
                System.out.print("Enter the plaintext file path: ");
                msgPath = input.nextLine();
                System.out.println();
                System.out.print("Enter the ciphertext file path: ");
                ciphMsg = input.nextLine();
                SubstitutionCipher.loadSubstitutionTable(tablePath);
                SubstitutionCipher.substitute(msgPath, ciphMsg);
                break;
            case 2:
                System.out.print("Enter the transiposition key file path: ");
                tablePath = input.nextLine();
                System.out.println();
                System.out.print("Enter the plaintext file path: ");
                msgPath = input.nextLine();
                System.out.println();
                System.out.print("Enter the ciphertext file path: ");
                ciphMsg = input.nextLine();
                SubstitutionCipher.loadTranspositonTable(tablePath);
                SubstitutionCipher.transposition(msgPath, ciphMsg);
                break;
            case 3:
                System.out.print("Enter the substitution key file path: ");
                tablePath1 = input.nextLine();
                System.out.print("Enter the transiposition key file path: ");
                tablePath2 = input.nextLine();
                System.out.println();
                System.out.print("Enter the plaintext file path: ");
                msgPath = input.nextLine();
                System.out.println();
                System.out.print("Enter the ciphertext file path: ");
                ciphMsg = input.nextLine();
                SubstitutionCipher.loadSubstitutionTable(tablePath1);
                SubstitutionCipher.loadTranspositonTable(tablePath2);
                SubstitutionCipher.cipherMix(msgPath, ciphMsg);
                break;
            case 4:
                System.out.println("Decrypting ... ");
                SubstitutionCipher.loadSubstitutionTable("keyTable1.txt");
                HashMap hm1 = stable;
                CryptoUtil.decryptUtil(1, "ciphertext1.txt", "decryptedtext1.txt", hm1);
                
                break;
            case 5:
                System.out.println("Decrypting ... ");
                SubstitutionCipher.loadTranspositonTable("keyTable2.txt");
                HashMap hm2 = ttable;
                CryptoUtil.decryptUtil(0, "ciphertext2.txt", "decryptedtext2.txt", hm2);
                //HashMap hm = ttable;
                //CryptoUtil.decryptUtil(0, ciphMsg, msgPath, hm);

                break;
            case 0:
                done = true;
                break;
            default:
                done = false;
                System.out.println("try again?");
           }
        }
    }
}
