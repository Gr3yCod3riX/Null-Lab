package com.simulator.protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Hex;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class VDigitalniPotpis {
	byte[] kljuc;
	
	KeyFactory keyFactory;
	public PublicKey javniKljuc;
	byte[] digitalniPotpis;
	Signature potpis;
	FileInputStream fis = null;
	FileChooser fc;
	
	FileInputStream poruka;
	File selectedFile;
	FileChooser fc2;
	public BufferedInputStream bufPoruka;
	
	File destFile;
	// MY PUBLIC KEY //  --> signatureLoad() will make errors if it's not correctly set
	Path pubKey;
	Path sig ;
	
	public void publicKeyLoad(Path dest) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        //Ucitavanje javnog kljuca
		pubKey = dest;
		kljuc = ucitaj(pubKey);
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(kljuc);
        keyFactory = KeyFactory.getInstance("RSA");
        javniKljuc = keyFactory.generatePublic(pubKeySpec); 
	}
	
	public void signatureLoad(Path sig) throws Exception{
		 //Loading of Alise signature
        this.sig = sig;
        digitalniPotpis = ucitaj(sig);
		//Initializing used algorithm for digital signature
        potpis = Signature.getInstance("SHA1withRSA");
        potpis.initVerify(javniKljuc);
        
	}
	 byte[] buffer;
	
    public void verification(Path fileVerify, Label labelAuth) throws Exception{
    	File tempF = new File(fileVerify.toString());
		 
        poruka = new FileInputStream(tempF);
        bufPoruka = new BufferedInputStream(poruka);
        buffer = new byte[1024];
        int len;
        while (bufPoruka.available() != 0) {
            len = bufPoruka.read(buffer);
            potpis.update(buffer, 0, len);
        }
    
        bufPoruka.close();
//        //Bob poredi heÅ¡ vrednosti
//       
//   
        boolean idi = potpis.verify(digitalniPotpis);
        System.out.println(idi);
           	 
           	 
   	 if(idi){
            System.out.println("Digitalni potpis: \n"
            		+"\n" //+ Hex.toHexString(potpis.sign())
                +Hex.toHexString(digitalniPotpis));
            System.out.println("Verifikovanje uspeÅ¡no!");
        }else{
            System.out.println("Verifikovanje nije uspesno!: \n"
            		 +Hex.toHexString(digitalniPotpis)
            		 + "\n"
            		
           		 );
            			
        }
   
        if (idi) {
			labelAuth.setText("Authentication Success!");
		}
        else{
        	labelAuth.setText("Authentication Failed!");
        }
   
    }

    public static byte[] ucitaj(Path fajl) throws IOException {
    	
        return Files.readAllBytes(fajl);
    }
    
}
