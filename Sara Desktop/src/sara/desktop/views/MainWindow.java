package sara.desktop.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import sara.api.Sara;
import sara.api.handler.LogEventArgs;
import sara.api.handler.SaraEventArgs;
import sara.api.interfaces.ILogEvent;
import sara.api.interfaces.ISaraEvent;
import sara.api.tools.SaraLog;
import sara.api.tools.SmartScroller;

public class MainWindow extends JPanel implements ISaraEvent, ILogEvent
{
	private static final long serialVersionUID = -2857668062854192621L;

	// Panels
	private JPanel pnFirstLine;
	private JPanel pnSecondLine;

	// Controls
	private JTextField txtBrokerAddress;
	private JButton btnConnect;
	private JButton btnSendHandShake;
	private JButton btnSendUrl;
	private JButton btnSendSignal;
	private JScrollPane logScrollPane;
	private JTextArea txtLog;

	// Device
	private Sara sara;
	private SaraLog saraLog;

	public MainWindow()
	{
		loadInstances();
		loadEventHandlers();
		loadLayout();
	}

	private void loadInstances()
	{
		// Window object controls
		txtBrokerAddress = new JTextField("192.168.0.104");
		btnConnect = new JButton("Connect to broker");
		btnSendHandShake = new JButton("Send handshake");
		btnSendUrl = new JButton("Send url");
		btnSendSignal = new JButton("Send signal");
		txtLog = new JTextArea();
		logScrollPane = new JScrollPane(txtLog);
		new SmartScroller(logScrollPane, SmartScroller.HORIZONTAL, SmartScroller.END);

		// Sara control
		sara = new Sara(this);
		saraLog = sara.getSaraLog();
	}

	private void loadEventHandlers()
	{
		// Window object controls
		btnConnect.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnConnect_Clicked();
			}
		});

		btnSendHandShake.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnSendHandShake_Clicked();
			}
		});

		btnSendUrl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnSendUrl_Clicked();
			}
		});

		btnSendSignal.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnSendSignal_Clicked();
			}
		});

		// Sara control
		sara.addSaraEventsListener(this);
		saraLog.addLogEventsListener(this);
	}

	private void loadLayout()
	{
		// The main grid with 3 horizontal lines
		GridLayout mainGridLayout = new GridLayout(3, 1);
		this.setLayout(mainGridLayout);

		// First line layout
		FlowLayout firstLine = new FlowLayout();
		pnFirstLine = new JPanel();
		pnFirstLine.setLayout(firstLine);
		pnFirstLine.add(txtBrokerAddress);
		pnFirstLine.add(btnConnect);
		pnFirstLine.add(btnSendHandShake);
		pnFirstLine.add(btnSendUrl);
		pnFirstLine.add(btnSendSignal);

		// Second line layout
		BorderLayout secondLine = new BorderLayout();
		pnSecondLine = new JPanel();
		pnSecondLine.setLayout(secondLine);
		pnSecondLine.add(logScrollPane);

		// Add to the main window
		this.add(pnFirstLine);
		this.add(pnSecondLine);
	}

	public void paintComponent(Graphics g)
	{

	}

	// Events methods
	private void btnConnect_Clicked()
	{
		sara.start();
		sara.tryToFindSaraCentral();
	}

	private void btnSendHandShake_Clicked()
	{
		sara.sendHandShakeConfirmation();
	}

	private void btnSendUrl_Clicked()
	{
		sara.sendThingOperationsUrl();
	}

	private void btnSendSignal_Clicked()
	{
		sara.sendSignal();
	}

	// Behavior events
	@Override
	public void onCentralFound(EventObject sender, SaraEventArgs e)
	{
		saraLog.add("Sara Central found");
	}

	@Override
	public void onHandShakeConfirmationRequested(EventObject sender, SaraEventArgs e)
	{
		saraLog.add("The handshake confirmation was requested by Sara Central");
		sara.sendHandShakeConfirmation();
	}

	@Override
	public void onThingIdRequested(EventObject sender, SaraEventArgs e)
	{
		saraLog.add("The Thing ID was requested by Sara Central");
		sara.sendThingId();
	}

	@Override
	public void onThingOperationsUrlRequested(EventObject sender, SaraEventArgs e)
	{
		saraLog.add("The Operations URL was requested by Sara Central");
		sara.sendThingOperationsUrl();
	}

	@Override
	public void onSignalReceived(EventObject sender, SaraEventArgs e)
	{
		saraLog.add("Sara Central sent a signal");
	}

	@Override
	public void onNewLogMessageAdded(EventObject sender, LogEventArgs e)
	{
		SaraLog saraLog = (SaraLog) sender;
		txtLog.setText(saraLog.getLog());
		txtLog.setCaretPosition(txtLog.getDocument().getLength());
	}
}
