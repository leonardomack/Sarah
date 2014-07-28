package sara.desktop.views;

import javax.swing.JFrame;

public class StartApplication
{

	public static void main(String[] args)
	{
		System.out.println("=== Inicializando a aplicação ===");
		// new Device();

		MainWindow mainWindow = new MainWindow();

		JFrame fenetre = new JFrame();
		fenetre.setSize(800, 600);
		fenetre.getContentPane().add(mainWindow);
		fenetre.setVisible(true);
		fenetre.setTitle("Sara Device");
		fenetre.setResizable(false);
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
