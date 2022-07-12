package boundary;

import javax.swing.*;
import java.awt.*;

public class GUI {
	
	public static void main(String args[])
	 {
		/*
	   JFrame win;
	   win = new JFrame("Prima finestra");
	   Container c = win.getContentPane();
	   c.add(new JLabel("Buona Lezione"));
	   JLabel xField=new JLabel("x = ");
	   //c.add(xField);
	   win.setSize(200,200);
	   win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   win.setVisible(true);
	   */
		
	   JFrame win2;
	     win2 = new JFrame("Esempio␣di␣JComboBox");
	     String lista[]=new String[10];
	     for(int i=0;i<lista.length;i++)
	         lista[i]="Elemento␣numero␣"+i;
	     JComboBox cBox=new JComboBox(lista);
	     Container c2 = win2.getContentPane();
	     c2.add(cBox);
	     win2.setSize(200,200);
	     win2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     win2.setVisible(true);
	}

}
