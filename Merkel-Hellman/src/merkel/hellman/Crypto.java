package merkel.hellman;

import java.io.*;

/**
 *
 * @author simon
 */
public class Crypto {

    // private key:
    // q: prime larger than sum of superincreasing sequence
    // r: multiplier which is in [1,q)
    // w: superincreasing sequence
    // public key:
    // B: hard sequence
    private final int keylength = 16;
    private int[] privateKey = new int[keylength];;
    private int[] publicKey = new int[keylength];
    private int modulo; //has to bigger than the sum of private key
    private int multiplier = 22588; //this number can't be too small
    private int modularInverse;// = U.getModInverse(multiplier, modulo);
    private int blocksize = keylength / 8; // 8bits in a byte ;) and will always be 2

    public Crypto(){ /* Exists for testing methods */ }
    
    // encrypt
    public Crypto(String keyString) {
        String[] key = keyString.split(",");
        for (int i = 0; i < keylength; ++i) {
            this.publicKey[i] = Integer.parseInt(key[i]);
        }
        
//        U.p(this.publicKey);
//        System.exit(0);
    }
    
    // decrypt
    public Crypto(String modulostring, String multiplierString, String[] keystring) {
       this.modulo = Integer.parseInt(modulostring);
       this.multiplier = Integer.parseInt(multiplierString);
       for (int i = 0; i < keylength; ++i) {
           this.privateKey[i] = Integer.parseInt(keystring[i]);
       }
       this.modularInverse = U.getModInverse(this.multiplier, this.modulo);
       
//       U.p(this.modulo);
//       U.p(this.multiplier);
//       U.p(this.privateKey);
//       U.p(U.calculate(privateKey));
//       System.exit(0);
    }

    public int[] encrypt(byte[] data) {
        //U.p("\nIn encrypt()\n===");
        // encrypt for every block and get a number
        int[] result = new int[data.length / blocksize];
        for (int i = 0, j = 0; i < data.length; i += blocksize, ++j) { //i increments in blocks of 2
            byte[] piece = new byte[blocksize];
            for (int k = 0; k < blocksize; ++k) {
                piece[k] = data[i + k];
            }
            result[j] = encryptBlock(piece); //encrypt the byte array containing 2 elements
        }

        // return the encrypted bytes as int[]
        return result;
    }

    public int encryptBlock(byte[] block) {
        //U.p("= encrypt block");
        //U.p(U.toCharArr(block));

        char[] chardata = U.toCharArr(block);
        int encryptedNum = 0;
        //loop through chardata and multiply each bit by its corresponding number in B[]
        for (int i = 0; i < chardata.length; ++i) {
            if (chardata[i] == '1') {
                encryptedNum += publicKey[i];
            }
        }
        //U.p(encryptedNum);

        return encryptedNum;
    }

    public byte[] decrypt(int[] encrypted) {
        //U.p("\nIn decrypt()\n===");
        byte[] decrypted = new byte[encrypted.length * blocksize];

        // multiply encrypted number by modular inverse and mod by q      
        for (int i = 0; i < encrypted.length; ++i) {
            byte[] temp = new byte[blocksize];
            temp = decryptBlock(encrypted[i]);
            for (int j = 0; j < blocksize; ++j) {
                int v = (i * blocksize) + j;
                decrypted[(i * blocksize) + j] = temp[j];
            }
        }

        //U.p("\n(in decrypt(...)) Decrypted: ");
        //U.p(U.toCharArr(decrypted));

        // decrypt result with w and return
        return decrypted;
    }

    public byte[] decryptBlock(int block) {
        //U.p("= decrypt block");
        //U.p(block);

        String res = "";
        int decryptedNum = block * modularInverse % modulo;
        byte[] decryptedBlock = new byte[blocksize];

        // build string representation of decrypted block
        int temp = decryptedNum;
        for (int i = privateKey.length - 1; i >= 0; --i) {
            if (temp - privateKey[i] >= 0) {
                temp = temp - privateKey[i];
                res = "1" + res;
            } else {
                res = "0" + res;
            }
        }

        // for each block in the binary String, convert into a byte
        for (int i = 0, j = 0; i < res.length(); i += 8, ++j) {
            String tempString = res.substring(i, i + 8);
            decryptedBlock[j] = U.toByte(tempString);
        }

        //U.p(U.toCharArr(decryptedBlock));

        return decryptedBlock;
    }
    
    public void keygen() {
        // gen private key
        this.privateKey = U.createSuperincreasing();
        System.out.println("Is superincreasing: " + U.isSuperIncreasing(this.privateKey));
        
        // find modulo
        int sum = 0;
        for (int i : privateKey) { sum += i; }
        this.modulo = U.findNextPrime(sum);
        //this.multiplier = U.getCoPrime(this.modulo);
        System.out.println("mod: "+modulo+" mul: "+multiplier);
        
        for (int i = 0; i < privateKey.length; ++i) {
            publicKey[i] = privateKey[i] * multiplier % modulo;
        }
        
        // write private key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("private.key"));
            //print modulo
            pf.write(Integer.toString(this.modulo) + ";");
            //print multiplier
            pf.write(Integer.toString(this.multiplier) + ";");
            //print superincreasing sequence
            for (int i = 0; i < privateKey.length; ++i){
                pf.write(Integer.toString(privateKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (Exception e){
            System.exit(2);
        }
        // write public key to file
        try {
            BufferedWriter pf = new BufferedWriter(new FileWriter("public.key"));
            for (int i = 0; i < privateKey.length; ++i){
                pf.write(Integer.toString(publicKey[i]));
                if (i != keylength - 1) {
                    pf.write(",");
                }
            }
            pf.close();
        } catch (Exception e){
            System.exit(2);
        }
    }

    public boolean test() {
        return test16bit() && test16bit2() /*&& test16bitpadding()*/;
    }

    public boolean test16bit() {
        Crypto crypto = new Crypto();
        String data = "ab";
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);

        String decryptedStr = new String(decryptedData);

        return decryptedStr.equals(data);
    }

    public boolean test16bit2() {
        Crypto crypto = new Crypto();
        String data = "abcdef";
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);

        String decryptedStr = new String(decryptedData);
        return decryptedStr.equals(data);
    }

    //No padding is being done in this test -> we need to add padding to the encrypt method above
    //to padd the blocks that don't take up a full two bytes
    public boolean test16bitpadding() {
        Crypto crypto = new Crypto();
        String data = "abcde";
        int[] encryptedData = crypto.encrypt(data.getBytes());
        byte[] decryptedData = crypto.decrypt(encryptedData);

        String decryptedStr = new String(decryptedData);

        return decryptedStr.equals(data);
    }
}
