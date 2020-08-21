import java.net.*;
import java.io.*;
import java.util.*; 

public class Main {
    private static final String TERMINATE = "Sair"; 
    static String nome; 
    static volatile boolean finished = false; 
    public static void main(String[] args) 
    { 
	try { 
		InetAddress sala = InetAddress.getByName(args[0]); 
		int porta = Integer.parseInt(args[1]); 
		Scanner sc = new Scanner(System.in); 
		System.out.print("Entre com seu nome do chat: "); 
		nome = sc.nextLine(); 
		MulticastSocket socket = new MulticastSocket(porta); 
		socket.setTimeToLive(0); 
		socket.joinGroup(sala); 
		Thread t = new Thread(new ReadThread(socket,sala,porta)); 
		t.start();  
		System.out.println(nome + " entrou na sala...\n"); 
		while(true) 
		{ 
			String mensagem; 
			mensagem = sc.nextLine(); 
			if(mensagem.equalsIgnoreCase(Main.TERMINATE)) 
			{ 
				finished = true; 
				socket.leaveGroup(sala); 
				socket.close(); 
				break; 
			} 
			mensagem = nome + ": " + mensagem; 
			byte[] buffer = mensagem.getBytes(); 
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, sala, porta); 
			socket.send(datagram); 
		} 
	} 
	catch(Exception e) 
	{ 
	   e.printStackTrace(); 
	}
    } 
} 


class ReadThread implements Runnable { 
    private MulticastSocket socket; 
    private InetAddress sala; 
    private int porta; 
    private static final int MAX_LEN = 1000; 
    ReadThread(MulticastSocket socket,InetAddress sala,int porta) 
    { 
        this.socket = socket; 
        this.sala = sala; 
        this.porta = porta; 
    } 
      
    @Override
    public void run() 
    { 
        while(!Main.finished) { 
	byte[] buffer = new byte[ReadThread.MAX_LEN]; 
	DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, sala, porta); 
	String mensagem; 
            try { 
                socket.receive(datagram); 
                mensagem = new
                String(buffer, 0, datagram.getLength(), "UTF-8"); 
                if(!mensagem.startsWith(Main.nome)) 
                    System.out.println(mensagem); 
            } catch(Exception e) { 
                e.printStackTrace(); 
            } 
        } 
    } 
}
