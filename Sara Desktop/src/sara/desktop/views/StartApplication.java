package sara.desktop.views;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class StartApplication
{

	public static void main(String[] args)
	{	
		MainWindow mainWindow = new MainWindow();

		JFrame window = new JFrame();
		window.setSize(800, 600);
		window.getContentPane().add(mainWindow);
		window.setVisible(true);
		window.setTitle("Sara Device");
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent we)
			{
				// TODO: Save the configurations
				System.exit(0);
			}
		});
	}
}
