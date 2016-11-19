/*
Copyright (c) 2016, Technikradio
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of Node2 nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * 
 */
package org.technikradio.jay_corp.ui;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

/**
 * @author leondietrich This class is designed to show message panels.
 */
public class MessagePanel extends JPanel {

	private MessageManager mm;
	private JLabel messageLabel;
	private JButton okButton;
	private boolean pressed = false;

	private static final long serialVersionUID = -9213215249506895496L;

	public MessagePanel() {
		super();
		setup();
	}

	public MessagePanel(LayoutManager layout) {
		super(layout);
		setup();
	}

	public MessagePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setup();
	}

	public MessagePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setup();
	}

	private void setup() {
		this.setVisible(false);
		this.setLayout(null);
		messageLabel = new JLabel();
		okButton = new JButton();
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pressed = true;
			}
		});
		okButton.setText("OK");
		this.add(messageLabel);
		this.add(okButton);
	}

	private String getEscapedString(String msg) {
		if (!msg.contains("\n"))
			return msg;
		return "<html>" + msg.replace("\n", "<br/>") + "</html>";
	}

	/**
	 * @return the messageManager
	 */
	public MessageManager getMessageManager() {
		return mm;
	}

	/**
	 * @param messageManager
	 *            the messageManager to set
	 */
	public void setMessageManager(MessageManager messageManager) {
		this.mm = messageManager;
	}

	public void message(String message) {
		messageLabel.setText(getEscapedString(message));
		pressed = false;
		if (mm != null) {
			mm.wrap();
		}
		this.setVisible(true);
		int oldPrio = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (!pressed) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Console.log(LogType.Warning, this, "Interrupted thread lock.");
				break;
			}
		}
		Thread.currentThread().setPriority(oldPrio);
		this.setVisible(false);
		if (mm != null) {
			mm.unwrap();
		}
	}
	
	public void setTextColor(Color cr){
		messageLabel.setForeground(cr);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		messageLabel.setBounds(5, 5, width - 10, height - 45);
		okButton.setBounds(width - 55, height - 35, 50, 30);
	}

	@Override
	public String toString() {
		return "MessagePanel";
	}

}
