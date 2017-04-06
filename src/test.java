import java.util.Random;

public class test {

	public static void main(String[] args) {
		String a = "Hello D";
		int br = 0;
		String porukaBin = toBin(a);
		
		System.out.println("Poruka bin:      " + porukaBin);
		String sum = noiser(1, porukaBin.length());
		
		System.out.println("ssum:            " + sum);
	//	String appliedNoise = messagePlusNoise(porukaBin, ssum);
		
		//System.out.println("Poruka sa sumom: " + appliedNoise);

	}
	
	public static String messagePlusNoise(String binMessage, String noise) {
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
	
	public static String toBin(String message){
		StringBuilder binMessage = new StringBuilder();
		
		byte[] bytes = message.getBytes();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binMessage.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
			//binMessage.append(' ');
		}
		
		
		
		String bM = binMessage.toString();
		
		
		
		
		return bM;
		
		
	}
	
	public static String noiser(int noise, int bitsLength) {
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


}
