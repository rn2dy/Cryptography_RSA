How to run my RSA implementation:

	- javac RSA.java

	- java RSA


	* It is recommendated that you check the generated *.txt file after each step.
	* please use my test *.txt files, because the file name are hard coded into the code.
	* please copy the test .txt files in the right directory.


Information about the input and output files and arguments of some function:

1) In my implementation I use the default key size 500 bits, to use a larger key size, you can modify it in the main method:

	- RSA.generateKeys(500, "RSAEncryptionKey.txt", "RSADecryptionKey.txt");

2) The generated Encryption key goes to "RSAEncryptionKey.txt" file; The generated Decryption key goes to "RSADecryptionKey.txt" file.

3) The default message to encrypt is from "plaintext1.txt", you can modify it through
	
	- RSA.messageToCipher("plaintext1.txt", "RSAEncryptionKey.txt");

4) The default recovered message goes to "rsa_recovered.txt", you can modify it through
	
	- RSA.cipherToMessage("rsa_recovered.txt" , "RSADecryptionKey.txt");

5) The default ciphertext that is encrypted goes to "rsa_ciphertext.txt". Better not change it, but just view it. This file is also used to do decryption.