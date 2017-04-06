import java.util.Random;

public class Conversion_Module {
	Random r = new Random();
	int noiseSet = 0;


	public void coder() {

	}

	public void decoder() {

	}

	public String noiser(int noise, int bitsLength) {
		int nSet = 0;
		int a = 0;
		Random r = new Random();

		
		StringBuilder noiseGen = new StringBuilder();
		// Empty noise
		for (int i = 0; i < bitsLength; i++) {
		
			noiseGen.append('0');
		}

		// Filled noise

		a = r.nextInt(bitsLength);

		while (nSet != noise) {
			if (noiseGen.charAt(a) != '1') {
				noiseGen.setCharAt(a, '1'); 
				nSet++;
			} else {
				a = r.nextInt(bitsLength);

			}
		}
		nSet = 0;
		
		String n = noiseGen.toString();
		return n;

	}

	public String messagePlusNoise(String binMessage, String noise) {
		int[] messageWithNoise = new int[binMessage.length()];

		int cond = 0;
		
		StringBuilder binMsgBuilder = new StringBuilder();
		String binMsgNoise = "";

		for (int i = 0; i < binMessage.length(); i++) {
			if ((binMessage.charAt(i)=='1') || (binMessage.charAt(i)=='0')) {
				binMsgBuilder.append((int)binMessage.charAt(i)^(int)noise.charAt(i));
				cond = 0;
			}
			else if(cond != 2){
				cond ++;
				i--;
			}
		}
		binMsgNoise = binMsgBuilder.toString();
		return binMsgNoise;
	}
	
	public String toBin(String message){
		StringBuilder binMessage = new StringBuilder();
		
		byte[] bytes = message.getBytes();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binMessage.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		String bM = binMessage.toString();
		
		return bM;
		
	}
	
	public String binToText(String input){
		String output = "";
		for(int i = 0; i <= input.length() - 8; i+=8)
		{
		    int k = Integer.parseInt(input.substring(i, i+8), 2);
		    output += (char) k;
		}   
		return output;
	}
	
}
