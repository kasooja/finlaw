package edu.insight.finlaw.demo.grctc;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;



public class MainWindow extends JFrame {

	private JPanel contentPane;
	private Demonstrator control = null;
	private JPanel jContentPane = null;
	private JButton jButtonSelectQuery = null;
	private JFrame thisWindow = this;


	/**
	 * Create the frame.
	 */
	public MainWindow(Demonstrator control) {


		this.control = control;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		initialize();
		
		final SparqlEndpointTest sparql = new SparqlEndpointTest();
		
		JLabel lblSelectQuery = new JLabel("Select Query");
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"None","List All Prohibitions, type and location in the document", "List All Prohibitions, type and location in the document with Text", 
				"List Obligations: Customer Due Diligence", "List All Provisions with Modalities","List All Sections with Modalities"}));
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				final int queryNumber;
				String queryResult;

				String selectedquery = (String)comboBox.getSelectedItem(); 
				System.out.print(selectedquery);
				
				
				// execute the selected query in the sparql endpoint
				if(selectedquery != "None")
				{
					queryNumber = GetSelection(selectedquery);

					queryResult = sparql.buildQuery(queryNumber);
					
					if(queryResult != null)
					{
					
						switch(queryNumber)
						{
						case 1:
							QueryResultsWindow queryresultsWindow = new QueryResultsWindow(queryResult);
							queryresultsWindow.setVisible(true);
							break;
						case 2:
							Query2ResultWindow query2resultsWindow = new Query2ResultWindow(queryResult);
							query2resultsWindow.setVisible(true);
							break;
						case 3:
							Query3ResultsWindow query3resultsWindow = new Query3ResultsWindow(queryResult);
							query3resultsWindow.setVisible(true);
							break;
						case 4:
							Query4ResultsWindow query4resultsWindow = new Query4ResultsWindow(queryResult);
							query4resultsWindow.setVisible(true);
							break;
						case 5:
							Query5ResultsWindow query5resultsWindow = new Query5ResultsWindow(queryResult);
							query5resultsWindow.setVisible(true);
							break;
						}
					}
					
				}
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() 
			{
			public void actionPerformed(ActionEvent arg0) 
			{
				dispose();
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(24)
					.addComponent(lblSelectQuery)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(36, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
							.addComponent(btnCancel)
							.addGap(108))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(22)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSelectQuery)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(123)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addContainerGap(57, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() 
	{
		this.setSize(483, 293);
		this.setTitle("Regulatory Change Management");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() 
	{
		//if (contentPane == null) {
			//contentPane = new JPanel();
			contentPane.setLayout(null);
			//contentPane.add(getJButtonSelectQuery(), null);
		//}
		return contentPane;
	}
	
	/**
	 * This method initializes jButtonAgregarRevista
	 *
	 * @return javax.swing.JButton
	 */
//	private JButton getJButtonSelectQuery() {
//		if (jButtonSelectQuery == null) {
//			jButtonSelectQuery = new JButton();
//			jButtonSelectQuery.setBounds(new Rectangle(30, 63, 182, 31));
//			jButtonSelectQuery.setText("Select Query");
//			jButtonSelectQuery.addActionListener(new java.awt.event.ActionListener() {
//				public void actionPerformed(java.awt.event.ActionEvent e) {
//					//control.agregarRevista();
//				}
//			});
//		}
//		return jButtonSelectQuery;
//	}
	
	int GetSelection(String selectedquery)
	{
		switch (selectedquery)
		{
			case "List All Prohibitions, type and location in the document": return 1; 
			case "List All Prohibitions, type and location in the document with Text" : return 2; 
			case "List Obligations: Customer Due Diligence": return 3;
			case "List All Provisions with Modalities": return 4;
			case "List All Sections with Modalities": return 5; 
		}
		return 0;
	}
}
