package edu.insight.finlaw.demo.grctc;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Query3ResultsWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private String queryResult;


	
	/**
	 * Create the frame.
	 */
	public Query3ResultsWindow(String queryRes) 
	{
		queryResult = queryRes;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Modality", "Provision Type", "Occurrence in Document"	}
		));

		scrollPane.setViewportView(table);
		
		PrintQueryResult();

	}

	void PrintQueryResult()
	{
	
		String Provision,Modality, instance;
		//subClass,instance;
		int startIndex,endIndex;
		DefaultTableModel model;
		
		model = (DefaultTableModel) table.getModel(); // get the window model to insert rows

		if(queryResult.contains("bindings"))
		{
			
			startIndex = queryResult.indexOf("bindings");
			queryResult = queryResult.substring(startIndex, queryResult.length());

			// Extracting Modality
			
			while(queryResult.indexOf("Modality") != -1)
			{
				
				startIndex = queryResult.indexOf("Modality");
				endIndex = queryResult.indexOf("}");
				
				Modality = queryResult.substring(startIndex, endIndex);
	
				queryResult = queryResult.substring(endIndex+1, queryResult.length());
	
				Modality = Extract(Modality);
				
				// Extracting Provision
				
				startIndex = queryResult.indexOf("Provision");
				endIndex = queryResult.indexOf("}");
				
				Provision = queryResult.substring(startIndex, endIndex);
	
				queryResult = queryResult.substring(endIndex+1, queryResult.length());
	
				Provision = Extract(Provision);
	
			
				// Extracting sub-section
				
				startIndex = queryResult.indexOf("subSection");
				endIndex = queryResult.indexOf("}");
				
				instance = queryResult.substring(startIndex, endIndex);

				queryResult = queryResult.substring(endIndex+1, queryResult.length());

				instance = Extract(instance);

				if(queryResult.indexOf("Modality") != -1)
				{
	
				endIndex = queryResult.indexOf("Modality");
				queryResult = queryResult.substring(endIndex, queryResult.length());
				}
				
				model.addRow(new Object[]{Modality,Provision,instance});
			}

		}
	}
	
	String Extract(String subQuery)
	{

	int startIndex = subQuery.indexOf("http");
	subQuery = subQuery.substring(startIndex, subQuery.length()-2);

	//System.out.print(subQuery);
	
	startIndex = subQuery.indexOf("#");
	subQuery = subQuery.substring(startIndex+1, subQuery.length());
	//System.out.print(subQuery);
	
	return subQuery;
	
	}

}
