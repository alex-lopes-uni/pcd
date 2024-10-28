import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;


public class MiriaGUI {
	
	File[] files1;
	File[] files2;
	File[] files3;

	public File[]  getPasta(int n) {
		if (n == 1) 
			return files1;
		if (n == 2) 
			return files2;
		
		return files3;
	}
	
	public void lerConteudo() {
		
		files1 = new File("NoA").listFiles(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".mp3");
			}	
		});
		
		for(File f : files1)
		System.out.println("file 1: " + f);
		
		files2 = new File("NoB").listFiles(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".mp3");
			}	
		});
		
		for(File f : files2)
			System.out.println("file 2: " + f);
		
		files3 = new File("NoC").listFiles(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().endsWith(".mp3");
			}	
		});
		
		for(File f : files3)
			System.out.println("file 3: " + f);
		
	}
		
	
	
	
	public static void novaJanela() {
		
		JFrame frame2 = new JFrame("Ligacao");
		frame2.setLayout(new GridLayout(1, 4));
		
		
		JLabel label2 = new JLabel("Endereço");
		JTextField mostrador1 = new JTextField();
		mostrador1.setBackground(Color.WHITE);
		mostrador1.setEditable(true);
		
		JLabel label3 = new JLabel("Porta");
		JTextField mostrador2 = new JTextField();
		mostrador2.setBackground(Color.WHITE);
		mostrador2.setEditable(true);
		
		JButton cancelar = new JButton("Cancelar");
		JButton ok = new JButton("OK");
		
		frame2.add(label2);	
		frame2.add(mostrador1);	
		frame2.add(label3);	
		frame2.add(mostrador2);	
		frame2.add(cancelar);	
		frame2.add(ok);
		
		
		frame2.pack();
		frame2.setVisible(true);
		
		
	}
	
	public static void main(String[] args) {
		MiriaGUI g = new MiriaGUI();
		g.lerConteudo();
		
		File [] file = g.getPasta(2);
		for(File f : file)
			System.out.println(f);
	
		String [] string = {"Ola", "teste"};
		
		JFrame frame= new JFrame("Torrent");
		frame.setLayout(new BorderLayout());
		frame.setSize(300, 200);
		frame.setResizable(true);
		
		//criar paineis
		JPanel panel1= new JPanel(new GridLayout(1, 3));
		JPanel panel2= new JPanel(new GridLayout(2,1));
		
		//criar elementos
		JLabel label = new JLabel("Texto a procurar");
		JTextField mostrador = new JTextField();
		mostrador.setBackground(Color.WHITE);
		mostrador.setEditable(true);
		JButton procurar = new JButton("Procurar");
		
		JButton descarregar = new JButton("Descarregar");
		JButton no = new JButton("Ligar a Nó");
		
		JList lista = new JList(string);
		
		//adicionar elementos
		frame.add(panel1, BorderLayout.NORTH);
		panel1.add(label);
		panel1.add(mostrador);
		panel1.add(procurar);
		frame.add(panel2, BorderLayout.EAST);
		panel2.add(descarregar);
		panel2.add(no);
		frame.add(lista);

		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		no.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				novaJanela();
				
			}
			
		});
	
		
	}
	
}
