/**
 * Copyright (C) 2015  Vicente de Rivera III
 *
 * This file is part of poe-merchant.
 * 
 *    poe-merchant is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    poe-merchant is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with poe-merchant.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.poemerchant.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.poemerchant.Configuration;
import net.poemerchant.util.Utils;

/**
 * 
 *
 */
public class IndexerTab extends JPanel {
	private JTextField txtShopsSubFormUrl;
	public IndexerTab(final Configuration configuration) {
		setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		add(topPanel, BorderLayout.NORTH);
		
		JLabel lblShopsSubforumUrl = new JLabel("Shops Subforum URL:");
		topPanel.add(lblShopsSubforumUrl);
		
		final JButton btnStart = new JButton("Start");
		
		txtShopsSubFormUrl = new JTextField(150);
		txtShopsSubFormUrl.setText("http://www.pathofexile.com/forum/view-forum/561");
		topPanel.add(txtShopsSubFormUrl);
		txtShopsSubFormUrl.setColumns(40);
		topPanel.add(btnStart);
		
		JButton btnStop = new JButton("Halt at next page");
		topPanel.add(btnStop);
		
		JPanel centerPanel = new JPanel(null);
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		centerPanel.add(scrollPane);
		
		final JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		
		scrollPane.setViewportView(textArea);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnStart.setEnabled(false);
				String url = txtShopsSubFormUrl.getText();
				final PrintStream printStream = new PrintStream(new IndexerOutputStream(textArea));
				ScraperSwingWorker task = new ScraperSwingWorker(url, printStream, configuration) {
					@Override
					protected void done() {
						try {
							get();
							btnStart.setEnabled(true);
							printStream.close();
						} catch (ExecutionException e) {
							e.getCause().printStackTrace();
							String msg = String.format("Unexpected problem: %s", e.getCause()
									.toString());
							JOptionPane.showMessageDialog(Utils.getActiveFrame(IndexerTab.this), msg, "Error",
									JOptionPane.ERROR_MESSAGE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				task.execute();
			}
		});
	}

	/**
	 * This class extends from OutputStream to redirect output to a JTextArrea
	 * @author www.codejava.net
	 * Thanks to http://www.codejava.net/java-se/swing/redirect-standard-output-streams-to-jtextarea
	 */
	public static class IndexerOutputStream extends OutputStream {
	    private JTextArea textArea;
	     
	    public IndexerOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }
	     
	    @Override
	    public void write(int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(String.valueOf((char)b));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
	}
}
