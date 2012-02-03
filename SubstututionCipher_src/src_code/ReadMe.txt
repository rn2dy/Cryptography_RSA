How to run my substitutionCipher implementation:

	- javac SubstitutionCipher.java
/* This file contains Substitution cipher and Transposition cipher method */

	- javac CryptioUtil.java 
/* This file contains all the Decryption and Cryptoanalysis method */

	- java SubstitutionCipher

	- follow the instructions from prompt.
		- for Substitution cipher(choice 1) the input should be:

			/> 1

			/> keyTable1.txt //contain the substitution key table
			
			/> plaintext1.txt
			
			/> ciphertext1.txt
		- for Transposition cipher the input should be:	
			
			/> 2			
			
			/> keyTable2.txt //contain the Transposition key table
			
			/> plaintext2.txt
			
			/> ciphertext2.txt	
	
		- for decrypt substitution cipher
			
			/> 4 //(option 4)

		- for decrypt Transposition cipher

			/> 5 //(option 5)

	* It is recommended that you check the output file after each run.

	* Please use my test *.txt files and copy them to the right place.

CryptoAnalysis
To run the code: please uncomment the main method of CryptoUtil.java

	- java CryptoUtil

For this job, I wrote two method in the CryptoUtil.java file
	
	- for analyzing Transposition cipher, I implement a method called anagram() which will return the key table of the Transposition cipher.

	- for analyzing Substitution cipher, I implement a method called frequencyAnalyzer(), which will do frequencyAnalyze and output file of all the letters' frequency

* However the anagram may not give a result if the permutation does not match the original text