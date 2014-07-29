package edu.insight.finlaw.demo.grctc;

import javax.swing.UIManager;


public class Demonstrator {

	static Demonstrator demo;
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

		demo = new Demonstrator();
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception exc){
			exc.printStackTrace();
		}

		Demonstrator.Start();
		
		
		System.out.println("Demonstrator initiated...");
	
	
	}

		public Demonstrator() {} // constructor method
        
		
		public static void Start() {

			try {
				MainWindow mainwindow = new MainWindow(demo);
				mainwindow.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	


	
