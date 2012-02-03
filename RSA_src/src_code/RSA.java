import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * RSA.java
 * RSA encryption and decryption implementation
 * 
 * @author Qiang Han
 * 
 * Created by Qiang Han on 12/3/2010
 * Copyright 2010 -. All rights reserved.
 *
 */
/* The great thing about computer science is that: there is always something to learn! */
public class RSA {
    private static final int P = 0;
    private static final int Q = 1;

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    //private static final BigInteger TWO = new BigInteger("2");
    private static final SecureRandom rnd = new SecureRandom();

    /**
     * function 1 - find large prime numbers, when given number of bits as an input
     * This function may take some time, but tolerable!
     * @param numbits
     * @return BigInteger[] stores p, q
     */
    private static BigInteger[] findLargePrime(int numbits){
        if(numbits < 95)
            throw new ArithmeticException("bitLength < 95, too small!");
        /**
         * generate p first, if success, generate q
         */
        BigInteger[] pq = new BigInteger[2];
        do{
            pq[P] = new BigInteger(1, getRandomBits(numbits/2, rnd));
            //System.out.println(".......");
        }while(!mr_test(pq[P], 10));
        do{
            pq[Q] = new BigInteger(1, getRandomBits(numbits/2, rnd));
            //System.out.println(".......");
        }while(!mr_test(pq[Q], 10) || pq[Q].compareTo(pq[P]) == 0);
        
        //pq[P] = BigInteger.probablePrime(numbits/2, rnd);
        //pq[Q] = BigInteger.probablePrime((numbits/2)-2, rnd);
        //if(pq[P].compareTo(pq[Q])==0)
        //    System.out.println("Equal!");
        //just double check, could be comment out
        if(pq[P].isProbablePrime(20)&&pq[Q].isProbablePrime(20)){
            System.out.println("Found p and q! Cheers!");
        } else {
            System.out.println("Did not find p and q, try again!");
            return null;
        }
        return pq;
    }
    private static byte[] getRandomBits(int numbits, SecureRandom rnd){
        int numBytes = (int)((numbits + 7)/8);
        byte[] rndBits = new byte[numBytes];
        int overflow = 8*numBytes - numbits;
        do{
            rnd.nextBytes(rndBits);
            rndBits[0] &= (1 << (8-overflow)) - 1;
        }while(rndBits[numBytes-1]%2 == 0);
        return rndBits;
    }
    
    /**
     * function 2 - compute GCD when given two large integers
     */
    private static BigInteger bigNumGcd(BigInteger a, BigInteger b){
        if(b.compareTo(ZERO)==0)
            return a;
        if(a.compareTo(a) == -1)
            return bigNumGcd(b, a);
        else
            return bigNumGcd(b, a.mod(b));
    }

    /**
     * function 3 - produce a random encryption key when given the two large prime numbers used for RSA
     */
    private static BigInteger findE(BigInteger p, BigInteger q){
        BigInteger fi = p.subtract(ONE).multiply(q.subtract(ONE));
        BigInteger e;
        do{
            e = new BigInteger(fi.bitLength()-1, rnd);
        }while( bigNumGcd(fi, e).compareTo(ONE) != 0 || e.compareTo(ONE) == 0);
        return e;
    }
    /**
     * function 4 - Find the encryption key d, when given p, q and the encryption key e
     * Use Euclid's Extended GCD Algorithm to find the inverse
     */
    private static BigInteger findD(BigInteger e, BigInteger p, BigInteger q){
        BigInteger fi = p.subtract(ONE).multiply(q.subtract(ONE));
        Stack<BigInteger> scalar = new Stack<BigInteger>();
        BigInteger a = fi;
        BigInteger b = e;
        BigInteger temp;
        System.out.println("a:" + a.toString());
        System.out.println("b:" + b.toString());
        do{
            temp = b;
            scalar.add( ( a.divide(b) ).negate() );
            //System.out.print(a.divide(b).negate()+",");
            b = a.mod(b);
            //System.out.println("<b:" + b.toString() + ">");
            a = temp;
        }while(b.compareTo(ZERO)!=0);
        /*
         * the matrix multiply pattern is:
         * k11  k12    0   1
         *           *
         * k21  k22    1 scalar
         */
        BigInteger k11 = ZERO;
        BigInteger k12 = ONE;
        BigInteger k21 = ONE;
        BigInteger k22 = scalar.pop();
        
        while(!scalar.isEmpty()){
            BigInteger newK22 = scalar.pop();
            BigInteger tempK11 = k11;
            BigInteger tempK21 = k21;
            k11 = k12;
            k12 = tempK11.add(k12.multiply(newK22));
            k21 = k22;
            k22 = tempK21.add(k22.multiply(newK22));
        }
        if(k12.compareTo(ZERO) == -1){
            k12 = fi.subtract(k12.abs());
        }
        return k12;
    }
    // function 5 - encryption when given the message and encryption key e and the modulo n
    private static BigInteger RSA_ENCRYPT(BigInteger msg, BigInteger e, BigInteger n){
        return myModPow(msg, e, n);
    }
    // funciton 6 - decryption when given the ciphertext and decryption key d and the modulo n
    private static BigInteger RSA_DECRYPT(BigInteger cipher, BigInteger d, BigInteger n){
        return myModPow(cipher, d, n);
    }
    /**
     * @param a - the base
     *        b - the exponential part in binary string format
     *        n - mod n
     * @return a^b mod n
     */
    private static BigInteger myModPow(BigInteger m, BigInteger e, BigInteger n){
        int length = e.bitLength();
        String b = new StringBuffer(e.toString(2)).reverse().toString();;
        //int length = b.length();
	BigInteger pre_cal[] = new BigInteger[length];
	//pre_cal[0] = 1;
	pre_cal[0] = m.mod(n);
	BigInteger temp = pre_cal[0]; 	
	for(int i = 1; i < length; i++){
            temp = (temp.multiply(temp)).mod(n);
            pre_cal[i] = temp;	
	}	
	BigInteger answer = null;
	int j = 0;
	//assign the first number to answer
	for(j = 0; j < length; j++){
            if(b.charAt(j) != '0'){
                answer = pre_cal[j];
		/*System.out.println("flag1: " + answer);*/
		j++;
                break;
            }
	}	
	//calculate the final answer
	for(int i = j; i < length; i++){
            if(b.charAt(i) != '0'){
		answer = (pre_cal[i].multiply(answer)).mod(n);
            }
	}
	return answer;
    }
    // prime testing
    private static boolean mr_test(BigInteger input, int preci){
        int precision = preci;
        /*randomly generate some BigInteger that are probably prime*/
        BigInteger likePrime = input;

    /*************** factor n-1 to (m * 2^a) ****************/
        BigInteger minusOne = likePrime.subtract(BigInteger.ONE);
        // A better approach
        BigInteger m = minusOne;
        int a = m.getLowestSetBit();
        m = m.shiftRight(a);
    /************** testing prime calculations **************/
        BigInteger witness;
        BigInteger tmp;
        int j;
        BigInteger odd_part = m;
        for(int i = 0; i < precision ; i++){

            do {
                witness = new BigInteger(likePrime.bitLength(), rnd);
            }while(witness.compareTo(BigInteger.ONE) <= 0 || witness.compareTo(likePrime) >= 0);

            tmp = witness.modPow(odd_part, likePrime);
            if(tmp.compareTo(BigInteger.ONE)!=0 && tmp.compareTo(minusOne) != 0){
                j = 1;
                while(j <= a - 1 && tmp != minusOne){
                    tmp = tmp.pow(2).mod(likePrime);
                    //tmp = do_bigNumberMod(tmp.pow(2), "01", likePrime);
                    if(tmp.compareTo(BigInteger.ONE)==0){
                        //System.out.println("here1");
                        return false;
                    }
                    /* else */
                    j++;
                }
                /* when the while loop finished, test the final result*/
                if(tmp.compareTo(minusOne) != 0){
                    return false;
                }
            }
            //System.out.println("One round done!");
        }
        return true;
    }
    public static void generateKeys(int numbits, String ekeyfile, String dkeyfile) throws IOException{
        FileWriter writer1 = new FileWriter(ekeyfile);
        FileWriter writer2 = new FileWriter(dkeyfile);
        /* generate encryption key and so on */
        BigInteger[] pq = RSA.findLargePrime(numbits);

        BigInteger e = RSA.findE(pq[P], pq[Q]);
        BigInteger d = RSA.findD(e, pq[P], pq[Q]);

        writer1.write(e.toString()+" "+pq[P].multiply(pq[Q]).toString());
        writer2.write(d.toString()+" "+pq[P].multiply(pq[Q]).toString());

        writer1.close();
        writer2.close();
    }
    /**
     * @param ..
     * @return 
     */
    public static void messageToCipher(String inputFile, String keyFile) throws FileNotFoundException, IOException{
        FileReader reader = new FileReader(inputFile);
        StringBuilder sb = new StringBuilder();
        // transform the file into numbers
        int flag = 0;
        while(reader.ready()){
            flag++;
            int x = reader.read();
            if( x > 96 && x <123)
                x = x - 32;
            if(x > 122)
                continue;
            //System.out.print(x+"/");
            sb.append(Integer.toString(x));
        }
        
        //padding the block size is 48
        if(sb.length() % 48 !=0){
            int rem = 48 - sb.length() % 48;
            //System.out.println("rem: " + rem);
            while(rem != 0){
                sb.append("0");
                rem--;
            }
        }
        System.out.println("now: " + sb.length() + " , blocks: "+sb.length()/48);
        /* read text in blocks, encrypt it, and put the cipher into a file */
        FileWriter writer = new FileWriter("rsa_ciphertext.txt");

        Scanner scan = new Scanner(new File(keyFile));
        String line = scan.nextLine();
        StringTokenizer st = new StringTokenizer(line," ");
        BigInteger e = new BigInteger(st.nextToken());
        //System.out.println(e.toString());
        BigInteger n = new BigInteger(st.nextToken());
        //System.out.println(n.toString());
        scan.close();
        int block_start = 0;
        int block_end = 48;

        for(int i = 0; i < sb.length()/48 ; i++){
            BigInteger block = new BigInteger(sb.substring(block_start, block_end));
            BigInteger cipherBlock = RSA_ENCRYPT(block, e ,n);
            writer.write(cipherBlock.toString()+" ");
            block_start += 48;
            block_end += 48;
        }
        reader.close();
        writer.close();

    }
    public static void cipherToMessage(String outputFile, String keyFile) throws FileNotFoundException, IOException{
        // read the decryption key
        Scanner scan = new Scanner(new File(keyFile));
        String line = scan.nextLine();
        StringTokenizer st = new StringTokenizer(line," ");
        BigInteger d = new BigInteger(st.nextToken());
        BigInteger n = new BigInteger(st.nextToken());
        scan.close();

        //read the cipher text and decrypt it
        scan = new Scanner(new File("rsa_ciphertext.txt"));
        line = scan.nextLine();
        st = new StringTokenizer(line, " ");

        FileWriter writer = new FileWriter(outputFile);

        while(st.hasMoreTokens()){
            BigInteger cipher = new BigInteger(st.nextToken());
            String recovered = RSA.RSA_DECRYPT(cipher, d, n).toString();
            int s = 0;
            int e = 2;
            for(int i = 0; i < recovered.length()/2; i++){
                writer.write(Integer.parseInt(recovered.substring(s,e)));
                s += 2;
                e += 2;
            }
        }
        writer.close();
        scan.close();
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Generate keys? ");
		System.out.println("hit any key you like to continue!");
        scan.next();
            RSA.generateKeys(500, "RSAEncryptionKey.txt", "RSADecryptionKey.txt");
        System.out.println("Encrypt the message? ");
        scan.next();
            RSA.messageToCipher("plaintext1.txt", "RSAEncryptionKey.txt");
        System.out.println("Decrypt the cipher? ");
        scan.next();
            RSA.cipherToMessage("rsa_recovered.txt" , "RSADecryptionKey.txt");

        /*
        BigInteger m = new BigInteger("234567898765432123456789876543211234567887654321");

        BigInteger[] pq = RSA.findLargePrime(500);
        System.out.println("p: " + pq[P].toString()+"  q: " + pq[Q].toString());

        BigInteger e = RSA.findE(pq[P], pq[Q]);
        
        BigInteger d = RSA.findD(e, pq[P], pq[Q]);
        BigInteger x = d.multiply(e).mod((pq[P].subtract(ONE)).multiply(pq[Q].subtract(ONE)));
        System.out.println("de mod fi: "+x.toString());

        BigInteger cipher = RSA.RSA_ENCRYPT(m, e, pq[P].multiply(pq[Q]));
        System.out.println("cipher: "+cipher.toString());
        
        BigInteger recovered = RSA.RSA_DECRYPT(cipher, d, pq[P].multiply(pq[Q]));
        System.out.println(recovered.toString());
        */
        //RSA.messageToCipher("plaintext3.txt", "rsa_encryptionkey.txt");
        
    }
}
