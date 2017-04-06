

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.util.encoders.Hex;

public class DigitalniPotpisORIGINAL {
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, FileNotFoundException, IOException, SignatureException {
        //Alice generates a pair of keys
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom CSPRNG = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(1024, CSPRNG);
        
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privatni = pair.getPrivate();
        PublicKey javni = pair.getPublic();
        //Alice chooses algorithms - Chosed RSA with SHA-1
        Signature dsa = Signature.getInstance("SHA1withRSA");
        dsa.initSign(privatni);
        //Alice loading message that she will sign digitaly
        FileInputStream poruka = new FileInputStream("video.avi");
        BufferedInputStream bufporuka = new BufferedInputStream(poruka);
        byte[] buffer = new byte[1024];
        int len;
        while (bufporuka.available() != 0) {
            len = bufporuka.read(buffer);
            dsa.update(buffer, 0, len);
        }
        bufporuka.close();
        poruka.close();
        //Alice generates digital signature calling method sign()
        byte[] digitalniPotpis = dsa.sign();
        //Alice is sending signature to Bob
        sacuvaj(digitalniPotpis, "potpis.sig");
        //Alice is sending the public key to Bob
        byte[] key = javni.getEncoded();
        sacuvaj(key, "javniKljuc.JK");
        System.out.println("Digitalni potpis: "
                +Hex.toHexString(digitalniPotpis));
        System.out.println("Operacija uspešna!");
    }

    public static void sacuvaj(byte podaci[], String nazivFajla) throws IOException {
        Path lokacija = Paths.get(nazivFajla);
        Files.write(lokacija, podaci, StandardOpenOption.CREATE_NEW);
    }
}
