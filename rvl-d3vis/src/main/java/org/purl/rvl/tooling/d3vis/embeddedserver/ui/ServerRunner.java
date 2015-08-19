package org.purl.rvl.tooling.d3vis.embeddedserver.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.purl.rvl.tooling.d3vis.embeddedserver.server.JettyServer;
import org.purl.rvl.tooling.d3vis.embeddedserver.ui.listener.ServerStartStopActionListner;


public class ServerRunner extends JFrame {
	private static final long serialVersionUID = 8261022096695034L;

	private JButton btnStartStop;

	public ServerRunner(final JettyServer jettyServer) {
		super("Start/Stop Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(new ServerStartStopActionListner(jettyServer));
		add(btnStartStop, BorderLayout.CENTER);
		setSize(300, 300);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (jettyServer.isStarted()) {
					try {
						jettyServer.stop();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}, "Stop Jetty Hook"));
		setVisible(true);
	}
}