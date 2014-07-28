package sara.desktop.views;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainWindow extends JPanel
{
	private static final long serialVersionUID = -2857668062854192621L;

	// Panels
	private JPanel pnFirstLine;

	// Controls
	private JButton btnConnect;

	public MainWindow()
	{
		pnFirstLine = new JPanel();
		btnConnect = new JButton("Connect");

		loadLayout();
		loadEventHandlers();
	}

	private void loadLayout()
	{
		// The main grid with 3 horizontal lines
		GridLayout mainGridLayout = new GridLayout(3, 1);
		this.setLayout(mainGridLayout);

		// First line layout
		FlowLayout firstLine = new FlowLayout();
		pnFirstLine.setLayout(firstLine);
		pnFirstLine.add(btnConnect);

		// Add to the main window
		this.add(pnFirstLine);
	}

	private void loadEventHandlers()
	{
		btnConnect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnConnect_Clicked();
			}
		});
	}

	public void paintComponent(Graphics g)
	{

	}

	// Events methods
	private void btnConnect_Clicked()
	{
		JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");
	}
}
