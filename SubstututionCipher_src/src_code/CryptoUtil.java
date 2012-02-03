import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 *
 * @author Qiang Han
 * all rights reserved by the author
 */

public class CryptoUtil {

    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = new BigInteger("2");

     public static boolean elgamal_factor(BigInteger n, BigInteger d, BigInteger e){
         SecureRandom rnd = new SecureRandom();
         BigInteger w = new BigInteger(n.bitLength()-1, rnd);
         /*
          * random select number w
          * if coprime(w, n), keep testing
          * else w is a factor of n.
          */
         BigInteger x = n.gcd(w);
         if(x.compareTo(n) < 0 && x.compareTo(ONE) > 0){
             System.out.println("n = "+x+"*"+n.divide(x));
             return true;
         }
         //factor ed-1 to r * 2^s, r is odd
         BigInteger edminus = e.multiply(d).subtract(ONE);
         int s = edminus.getLowestSetBit();
         BigInteger r = edminus.shiftRight(s);

         BigInteger v = w.modPow(r, n);
         if(v.compareTo(ONE)==0)
             return false;
         BigInteger v0 = v;
         while(v.compareTo(ONE) != 0){
             v0 = v;
             v = v.modPow(TWO, n);
         }
         if(v0.compareTo(n.subtract(ONE))==0)
            return false;
         else{
             x = (v0.add(ONE)).gcd(n);
             System.out.println("n="+x+"*"+n.divide(x));
             return true;
         }
     }
     public static void decryptUtil(int method, String cipher, String plaintext, HashMap<Object, Object> keyTable) throws FileNotFoundException, IOException{
         FileReader reader = new FileReader(cipher);
         FileWriter writer = new FileWriter(plaintext);
         /* build the reverse key table */
         HashMap<Character, Character> reverseKey1;
         HashMap<Integer, Integer> reverseKey2;

         if(method==1){
             reverseKey1 = new HashMap<Character, Character>();
             Iterator keys = keyTable.keySet().iterator();
             while(keys.hasNext()){
                 Character c = (Character)keys.next();
                 //System.out.print("key: " + c + " value: " + (Character)keyTable.get(c)+"/");
                 reverseKey1.put((Character)keyTable.get(c), c);
             }
             int data = reader.read();
             while(data != -1){
                Character x = reverseKey1.get((char)data); //getCipher((char)data);
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
         } else {
             reverseKey2 = new HashMap<Integer, Integer>();
             Iterator keys = keyTable.keySet().iterator();
             while(keys.hasNext()){
                 Integer c = (Integer)keys.next();
                 reverseKey2.put((Integer)keyTable.get(c), c);
             }
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
                    System.out.print("("+i+"."+c+")");
                    i++;
                }
                for(int j = 0; j < 8 ; j++){
                    outbuf[reverseKey2.get(j)] = inbuf[j];
                }
                writer.write(outbuf);
            }
            reader.close();
            writer.close();
         }
     }
     /** to analyze the transposition cipher and build the private key
	 * The dictionary function is not implemented, it is too time consuming!
	 * I just use a random permutation on a buffer size of 8 characters and 
	 * check if there is a match, the match should comes from the dictionary looking up!
	 * @return int[] will contain the permutation sequence that would give the original 
	 * message back
	 * !!! This method needs a really long time to get a match(even after 100,000 roounds), 
	 * so pure luck! need to improve.
	 */
     public static int[] anagram(String inputFile, String dictionary) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(inputFile);
        //read in 16 characters
        char[] charbuf = new char[16];
        String charstr = new String(charbuf);
        reader.read(charbuf);
        /* fast switch position function */
        Random rnd = new Random();
        int count = 0;
        int[] sequence = null;
        while(count < 1000000){
            /** every time I generate 8 switches */
            char[] copy = charstr.toCharArray();
            /** every time I generate 8 switches */
            sequence = new int[16];
            int p = 0;
            for(int i = 0; i<8;i++){

                int sw1 = rnd.nextInt(16);
                int sw2 = rnd.nextInt(16);

                char temp = copy[sw1];
                copy[sw1] = copy[sw2];
                copy[sw2] = temp;

                sequence[p] = sw1;
                sequence[++p] = sw2;
                p++;
            }
            if(new String(copy).matches(".*(south|korea).*")){
                System.out.println("Found!" + new String(charbuf));
                for(int sw: sequence){
                    System.out.print(sw+",");
                }
                break;
            }
            //System.out.print(new String(charbuf) + "/");
            count++;
        }
        return sequence;
     }
     public static void frequencyAnalyzer(String inputFile, String outputFile, int flag) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(inputFile);
        FileWriter writer = new FileWriter(outputFile);
        PriorityQueue<Element> pq62 = new  PriorityQueue<Element>();

        int data;
        /** for substitution cipher */
        int[] f62 = new int[62];
        while(reader.ready()){
            data = reader.read();
            System.out.print(data+",");
            if( data > 47 && data < 58)
                f62[data - 48]++;
            if( data > 64 && data < 91)
                f62[data - 56]++;
            if( data > 96 && data < 123)
                f62[data - 62]++; 
        }
        for(int i = 0 ; i < 10 ; i++){
            pq62.add(new Element(f62[i] , i));
            //writer.write(i + "-" + f62[i] + ",");
        }
		for(int i  = 65 ; i < 91 ; i++){
            pq62.add(new Element(f62[i-56] , i));
            //writer.write((char)i + "-" + f62[i-56] + ",");
        }
        for(int i = 97 ; i < 123 ; i++){
			pq62.add(new Element(f62[i-62] , i));
            //writer.write((char)i + "-" + f62[i - 62] + ",");
        }
        
        /** print out the results*/
            Element e = pq62.poll();
            while(e!=null){
                writer.write((char)e.ele[1] + "-" + e.ele[0] +",");
                e = pq62.poll();
            }

        reader.close();
        writer.close();
    }

    static class Element implements Comparable{
        public int[] ele = new int[2];
        public Element(int value, int index){
            this.ele[0] = value;
            this.ele[1] = index;
        }
        public int compareTo(Object obj) {
            if (this.ele[0] > ((Element)obj).ele[0]){
                return 1;
            } else if (this.ele[0] < ((Element)obj).ele[0]){
                return -1;
            } else
                return 0;
        }

    }
    /*
    public static void main(String[] args) throws FileNotFoundException, IOException {
	
        //int[] seq = CryptoUtil.anagram("ciphertext2.txt", null);
			
        //CryptoUtil.frequencyAnalyzer("ciphertext1.txt", "frequencyOutput.txt", 0);
    }
    */
}
