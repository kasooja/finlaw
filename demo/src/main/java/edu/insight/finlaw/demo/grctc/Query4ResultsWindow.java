package edu.insight.finlaw.demo.grctc;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Query4ResultsWindow extends JFrame {

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
	public Query4ResultsWindow(String queryRes)
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
				"Modality", "Provision Type", "Section", "Subsection"
			}
		));
		scrollPane.setViewportView(table);
		
		PrintQueryResult();

	}
	
	void PrintQueryResult()
	{
	
		String Provision,Modality,section,subSection;
		int startIndex,endIndex;
		DefaultTableModel model;
		
		model = (DefaultTableModel) table.getModel(); // get the window model to insert rows

		if(queryResult.contains("bindings"))
		{
			
			startIndex = queryResult.indexOf("bindings");
			queryResult = queryResult.substring(startIndex, queryResult.length());

			// Extracting Provision
			
			while(queryResult.indexOf("Provision") != -1)
			{
				
				startIndex = queryResult.indexOf("Provision");
				endIndex = queryResult.indexOf("}");
				
				Provision = queryResult.substring(startIndex, endIndex);
	
				queryResult = queryResult.substring(endIndex+1, queryResult.length());
				System.out.print(queryResult);

	
				Provision = Extract(Provision);
				
				// Extracting Modality
				
				startIndex = queryResult.indexOf("Modality");
				endIndex = queryResult.indexOf("}");
				
				Modality = queryResult.substring(startIndex, endIndex);
	
				queryResult = queryResult.substring(endIndex+1, queryResult.length());
				System.out.print(queryResult);

				Modality = Extract(Modality);
	
				// Extracting section
				
				startIndex = queryResult.indexOf("section");
				endIndex = queryResult.indexOf("}");
				
				section = queryResult.substring(startIndex, endIndex);
	
				queryResult = queryResult.substring(endIndex+1, queryResult.length());
				System.out.print(queryResult);
	
				section = Extract(section);
	
				// Extracting subsection
				
				startIndex = queryResult.indexOf("subSection");
				endIndex = queryResult.indexOf("}");
				
				subSection = queryResult.substring(startIndex, endIndex);
				
				if(queryResult.indexOf("Provision") != -1)
				{
	
				endIndex = queryResult.indexOf("Provision");
				queryResult = queryResult.substring(endIndex, queryResult.length());
				System.out.print(queryResult);
				}
				
				subSection = Extract(subSection);

				model.addRow(new Object[]{Modality,Provision,section,subSection});
			
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
