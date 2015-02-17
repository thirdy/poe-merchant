package net.poemerchant.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.BoxLayout;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import net.poemerchant.Configuration;
import net.poemerchant.search.ElasticSearch;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JScrollPane;
import java.awt.Font;

public class MainJFrame extends JFrame {

	private JPanel contentPane;
	private JTextField queryTextField;
	private Configuration configuration;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ElasticSearch.start();
					MainJFrame frame = new MainJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainJFrame() {
		// TODO, provide UI for this, and file persist
		configuration = new Configuration();
		
		setTitle("PoE Merchant - 0.0.1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, 697, 629);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				ElasticSearch.shutdown();
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel searchTab = new JPanel();
		
		tabbedPane.addTab("Search", null, searchTab, null);
		searchTab.setLayout(new BorderLayout(0, 0));
		
		JPanel statusPanel = new JPanel();
		searchTab.add(statusPanel, BorderLayout.SOUTH);
		
		final JLabel lblDuration = new JLabel("");
		lblDuration.setFont(new Font("Verdana", Font.PLAIN, 9));
		statusPanel.add(lblDuration);
		
		final JLabel lblHits = new JLabel("");
		lblHits.setFont(new Font("Verdana", Font.PLAIN, 9));
		statusPanel.add(lblHits);
		
		final JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
		resultsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		resultsPanel.setAutoscrolls(true);
		searchTab.add(new JScrollPane(resultsPanel), BorderLayout.CENTER);
		
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		searchTab.add(searchPanel, BorderLayout.NORTH);
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
		
		JLabel lblQuery = new JLabel("Query:");
		searchPanel.add(lblQuery);
		
		queryTextField = new JTextField();
		queryTextField.setText("amulet");
		searchPanel.add(queryTextField);
		queryTextField.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String query = queryTextField.getText();
				SearchResponse searchResponse = ElasticSearch.query(
						configuration.getIndex(), configuration.getType(),query);
				
				SearchHits searchHits = searchResponse.getHits();
				
				lblDuration.setText("Search took " + searchResponse.getTookInMillis() + "ms to complete.");
				lblHits.setText(searchHits.getTotalHits() + " Hits.");
				
				resultsPanel.removeAll();
				
				for (SearchHit searchHit : searchHits) {
					Map<String, Object> item = searchHit.getSource();
					ItemViewPanel itemViewPanel = new ItemViewPanel(item);
					resultsPanel.add(itemViewPanel);
				}
				resultsPanel.revalidate();
				MainJFrame.this.validate();
			}
		});
		searchPanel.add(btnSearch);
		
		JPanel indexerTab = new JPanel();
		tabbedPane.addTab("Indexer", null, indexerTab, null);
		indexerTab.setLayout(new BorderLayout(0, 0));
		
		IndexerTab indexerPanel = new IndexerTab(configuration);
		indexerTab.add(indexerPanel);
	}

}
