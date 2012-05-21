
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.ByteBuffer;
import java.util.List;
import javax.smartcardio.*;
import javax.swing.JApplet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class rfapp extends JApplet implements ActionListener  {

	private static boolean debug = false;
	
	private static final long serialVersionUID = -2840762027023649128L;
	private static CardTerminal terminal;
	private static CardTerminals terminalList;
	public String uid = "";	
	

	public static String GetUID() throws CardException {

		try {
		
			// list available terminals
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			if(debug) System.out.println("Terminals: " + terminals);
        	terminalList = factory.terminals();
        	
        	int t = 0;
			for (int i=0; i < terminalList.list().size(); i++)
        	{
        		String cardStatus = "No card in the reader";
        		if(terminalList.list().get(i).isCardPresent()) cardStatus = "Card Present";
        		if(debug) System.out.println("Reader Name : " + terminalList.list().get(i).getName()+ " : "+cardStatus ); 
        		t=i;
        	}
        
			// TODO: now simply grabs the highest numbered terminal  
			if(debug) System.out.println("Opening terminal: " + t);
			terminal = terminals.get(t);
            
    		// establish a connection with the card
			if(debug) System.out.println("Waiting for a card..");
        	terminal.waitForCardPresent(0);
        	Card card =  terminal.connect("*");
        	ATR atr = card.getATR();
        	if(debug) System.out.println(atr.toString()+" - "+atr.getBytes().toString());
        	if(debug) System.out.println("Card found!"); 	       
        
        	try {
        	
        		// read UID
        		byte[] GET_DATA = {(byte) 0xFF,(byte) 0xCA,(byte) 0x00,(byte) 0x00,(byte) 0x00};
            
        		CommandAPDU getData = new CommandAPDU(GET_DATA);
        		CardChannel channel = card.getBasicChannel();
        		ResponseAPDU resp = channel.transmit(getData);
        	
        		byte[] b = resp.getBytes();
        		byte[] buffer = {
        			(byte) (0xff&b[3]),
        			(byte) (0xff&b[2]),
        			(byte) (0xff&b[1]),
        			(byte) (0xff&b[0])
        		};
        	
        		long l = Long.decode("0x"+convertBinToASCII(buffer, 0, buffer.length)).longValue();
        		String uuuid = String.valueOf(l);
        	
        		/* 
        		 * Cantivo VDI Client don't pad so leaving out for now
        		 * 
        		if(uuuid.length()<10) uuuid = "0"+uuuid;
        		if(uuuid.length()<10) uuuid = "0"+uuuid;
        		if(uuuid.length()<10) uuuid = "0"+uuuid;
        		 
        		 */
        	
        		if(debug) System.out.println("UID:"+uuuid);
        		card.disconnect(true);
        		return uuuid;
        	
        } catch(Exception ex) {
        	if(debug) System.out.println("Exception : " + ex);
        }
        
		} catch (CardException ex) {
			ex.printStackTrace();
	    }
		return "";
		
	}

	public static String convertBinToASCII(byte[] bin, int offset, int length) {
		StringBuilder sb = new StringBuilder();
		for (int x = offset; x < offset + length; x++) {
			String s = Integer.toHexString(bin[x]);

			if (s.length() == 1)
				sb.append('0');
			else
				s = s.substring(s.length() - 2);
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}
	
	public static String send(byte[] cmd, CardChannel channel) {

	       String res = "";

	       byte[] baResp = new byte[258];
	       ByteBuffer bufCmd = ByteBuffer.wrap(cmd);
	       ByteBuffer bufResp = ByteBuffer.wrap(baResp);

	       // output = The length of the received response APDU
	       int output = 0;

	       try {
	           
	    	   output = channel.transmit(bufCmd, bufResp);
	           
	       } catch (CardException ex) {
	           
	    	   ex.printStackTrace();
	       }

	       for (int i = 0; i < output; i++) {
	           res += String.format("%02X", baResp[i]);
	       }

	       return res;
	}

	
	public void init() {
		  
		   String uid;
		   
		   try {
			   uid = GetUID();
			   System.out.println("Card found, UID : " + uid);
			   
			   
			   final URLStreamHandler streamHandler = new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL arg0)
						throws IOException {
					// TODO Auto-generated method stub
					return null;
				}
				   
				   
			   };
		  
			   try {
				   getAppletContext().showDocument(
						   new URL(null,"javascript:readrfid_callback("+uid+")", streamHandler) );
				   
				   
			   } catch (MalformedURLException me) { System.out.println(me.getMessage()); }
		  
		
			 
			   
		   } catch (CardException e) {
			// TODO Auto-generated catch block
		   }
		   
		   //System.exit(0);
		   
	}

	public static void run() {
		// TODO Auto-generated method stub		
	}
	
	public void main() {
		// TODO Auto-generated method stub		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub		
	}
	
}
