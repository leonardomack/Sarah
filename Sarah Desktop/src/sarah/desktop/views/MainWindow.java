package sarah.desktop.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
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

import sarah.api.Sarah;
import sarah.api.handler.LogEventArgs;
import sarah.api.handler.SarahEventArgs;
import sarah.api.interfaces.ILogEvent;
import sarah.api.interfaces.ISarahEvent;
import sarah.api.tools.SarahLog;
import sarah.api.tools.SmartScroller;

public class MainWindow extends JPanel implements ISarahEvent, ILogEvent
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
	private Sarah sarah;
	private SarahLog sarahLog;

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
		sarah = new Sarah(this);
		sarahLog = sarah.getSaraLog();
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
		sarah.addSarahEventsListener(this);
		sarahLog.addLogEventsListener(this);
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
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		sarah.start();
		sarah.tryToFindSaraCentral();

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	private void btnSendHandShake_Clicked()
	{
		sarah.sendHandShakeConfirmation();
	}

	private void btnSendUrl_Clicked()
	{
		sarah.sendThingOperationsUrl();
	}

	private void btnSendSignal_Clicked()
	{
		sarah.sendSignal();
	}

	// Behavior events
	@Override
	public void onCentralFound(EventObject sender, SarahEventArgs e)
	{
		sarahLog.add("Sara Central found");
	}

	@Override
	public void onHandShakeConfirmationRequested(EventObject sender, SarahEventArgs e)
	{
		sarahLog.add("The handshake confirmation was requested by Sara Central");
	}

	@Override
	public void onThingIdRequested(EventObject sender, SarahEventArgs e)
	{
		sarahLog.add("The Thing ID was requested by Sara Central");
	}

	@Override
	public void onThingOperationsUrlRequested(EventObject sender, SarahEventArgs e)
	{
		sarahLog.add("The Operations URL was requested by Sara Central");
	}

	@Override
	public void onSignalReceived(EventObject sender, SarahEventArgs e)
	{
		sarahLog.add("Sara Central sent a signal");
	}

	@Override
	public void onNewLogMessageAdded(EventObject sender, LogEventArgs e)
	{
		txtLog.setText(((SarahLog) sender).getLog());
	}
}
