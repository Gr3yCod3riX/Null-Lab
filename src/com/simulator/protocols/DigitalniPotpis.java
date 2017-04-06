package com.simulator.protocols;

import java.io.BufferedInputStream;
import java.io.File;
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

import com.simulator.networking.NetworkConnection;

import javafx.stage.FileChooser;

public class DigitalniPotpis {
	
	Thread t1,t2;
    FileInputStream fis = null;

    PublicKey javni;
    PrivateKey privatni;
    Signature dsa;
    boolean sel = false;
    public static volatile File selectedFile;
    File destFile;
    
    Path selected;
    Path dest; 
    
    BufferedInputStream bufporuka;

    FileChooser fc;
	
	byte[] key;
	byte[] digitalniPotpis;
	
	public void selectFile() throws FileNotFoundException{
	
	}
	
    public void inic() throws Exception{
    	
		//Alisa generise par kljuceva
	
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom CSPRNG = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(1024, CSPRNG);
        
        KeyPair pair = keyGen.generateKeyPair();
        privatni = pair.getPrivate();
        javni = pair.getPublic();
        //Alisa bira algoritme - izabrala je RSA sa SHA-1
        dsa = Signature.getInstance("SHA1withRSA");
        dsa.initSign(privatni);
        //Alisa ucitava poruku/fajl koju/i ce digitalno da potpise
 
    	fc = new FileChooser();
		selectedFile = fc.showOpenDialog(null);
		selected = selectedFile.toPath();
		
		fis = new FileInputStream(selectedFile);
        bufporuka = new BufferedInputStream(fis);
		
		
		
        byte[] buffer = new byte[1024];
        int len;
        while (bufporuka.available() != 0) {
            len = bufporuka.read(buffer);
            dsa.update(buffer, 0, len);
        }
        bufporuka.close();
        fis.close();
        
        System.out.println("Buffer done #1");
        
    	
      //Alisa generiÅ¡e digitalni potpis pozivajuÄ‡i metod sign()
				System.out.println("Odmah van try catch-a");
				
				
				
				 digitalniPotpis = dsa.sign();
//Alisa is sending the signature to Bob
		        
		        fc.setTitle("Save Signature");
    			destFile = fc.showSaveDialog(null);
    			dest = destFile.toPath();
		        save(digitalniPotpis, dest.toString()+".sig");
		        System.out.println(dest.toString());
		        
//Alisa sending public key to Bob
		        fc.setTitle("Save Public key");
    			destFile = fc.showSaveDialog(null);
    			dest = destFile.toPath();
		        key = javni.getEncoded();
		        save(key, dest.toString()+".JK");
		        
		        
		        System.out.println("Public and Signature DONE");
	        
		        System.out.println("Digitalni potpis: "
		                +Hex.toHexString(digitalniPotpis));
		        
		        System.out.println("Operacija uspesna!");
		        System.out.println("OP uspesna");
		        System.out.println("Posle try-a");
				
    }

    public static void save(byte podaci[], String selected) throws IOException {
    	Path lokacija = Paths.get(selected);
    	Files.write(lokacija, podaci);
    }
}
