package org.technikradio.jay_corp.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.technikradio.jay_corp.Protocol;
import org.technikradio.jay_corp.ui.helpers.ProgressIndicator;
import org.technikradio.jay_corp.user.Righttable;
import org.technikradio.universal_tools.Console;
import org.technikradio.universal_tools.Console.LogType;

public class UserWatchFrame extends JDialog {

	private static final long serialVersionUID = -1553588773452490924L;
	private JTable table;
	private final UserWatchFrame ownHandle = this;
	private String[][] originalData;
	private ArrayList<DataChangedNotifier> dcnotifiers;

	private void setup(String[] head, String[][] data) {
		dcnotifiers = new ArrayList<DataChangedNotifier>();
		originalData = data.clone();
		this.setLayout(new BorderLayout());
		//Initialize table
		{
			table = new JTable(data, head){
				private static final long serialVersionUID = -8502414623956768708L;
				
				@Override
				public boolean isCellEditable(int row, int col) {
				     switch (col) {
				         case 0:
				         case 1:
				         case 3:
				         case 5:
				             return true;
				         default:
				             return false;
				      }
				}
			};
			add(new JScrollPane(table.getTableHeader()), BorderLayout.PAGE_START);
			add(new JScrollPane(table), BorderLayout.CENTER);
		}
		//Add buttons
		{
			JPanel buttonPanel = new JPanel();
			JButton okButton = new JButton("OK");
			JButton abortButton = new JButton("Abbrechen");
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			buttonPanel.add(okButton);
			buttonPanel.add(abortButton);
			add(new JScrollPane(buttonPanel), BorderLayout.PAGE_END);
			okButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					Thread t = new Thread(new Runnable(){

						@Override
						public void run() {
							// Keep in mind that this dialog isn't designed to add users.
							//TODO implement data synchronization
							ownHandle.setEnabled(false);
							ownHandle.setTitle("Aktualisiere Daten... Bitte Warten...");
							ProgressIndicator e = new ProgressIndicator(ownHandle);
							e.setInfoLabelText("Datensatz wird analysiert.");
							ArrayList<String[]> temporaryData = new ArrayList<String[]>();
							for(int y = 0; y < originalData.length; y++){
								String[] cdata = new String[originalData[y].length];
								for(int x = 0; x < cdata.length; x++){
									cdata[x] = (String) table.getValueAt(y, x);
								}
								if(wasChanged(originalData[y], cdata)){
									temporaryData.add(cdata);
									System.out.println("hit");
								}
								e.setValv(0, originalData.length, y);
							}
							e.setInfoLabelText("Updates werden angewendet.");
							if(temporaryData.size() > 0){
								int i = 0;
								for(DataChangedNotifier dcn : dcnotifiers){
									dcn.correctData(temporaryData);
									i++;
									e.setValv(0, dcnotifiers.size(), i);
								}
							}
							e.dispose();
							try {
								ownHandle.dispose();
							} catch (Exception e1) {
								Console.log(LogType.Error, ownHandle, "This shouldn't happen.");
								e1.printStackTrace();
								JOptionPane.showMessageDialog(ownHandle, "An unknown exception occured: \n\n" + e1.getLocalizedMessage() + "\n" + e1.getStackTrace().toString());
								ownHandle.setEnabled(true);
							}
						}});
					t.setName("CHANGE_CHECKER_THREAD");
					t.setPriority(Thread.MAX_PRIORITY);
					t.start();
					
				}
				
			});
			abortButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						ownHandle.dispose();
					} catch (Exception e1) {
						Console.log(LogType.Error, ownHandle, "This shouldn't happen.");
						e1.printStackTrace();
						JOptionPane.showMessageDialog(ownHandle, "An unknown exception occured: \n\n" + e1.getLocalizedMessage() + "\n" + e1.getStackTrace().toString());
					}
				}
				
			});
			table.addMouseListener(new MouseAdapter() {
			    public void mousePressed(MouseEvent m) {
			    	final MouseEvent mf = m;
			    	Thread t = new Thread(new Runnable(){

						@Override
						public void run() {
							try {
								ownHandle.setEnabled(false);
								JTable table = (JTable) mf.getSource();
						        Point p = mf.getPoint();
						        int row = table.rowAtPoint(p);
						        int column = table.columnAtPoint(p);
						        if (mf.getClickCount() == 2 && column == 2) {
						            int id = Integer.parseInt((String)table.getValueAt(row, 4));
						            Righttable rt = Protocol.getRights(id);
						            String s = Byte.toString(RightEditFrame.getRight(RightEditFrame.showDialog(rt)));
						            table.setValueAt(s, row, column);
						            System.out.println(s);
						        }
							} catch(Exception e1){
								Console.log(LogType.Error, ownHandle, "This shouldn't happen.");
								e1.printStackTrace();
								JOptionPane.showMessageDialog(ownHandle, "An unknown exception occured: \n\n" + e1.getLocalizedMessage() + "\n" + e1.getStackTrace().toString());
							} finally {
								ownHandle.setEnabled(true);
							}
						}});
			        t.setName("RIGHT_EDIT_THREAD");
			        t.setPriority(Thread.MAX_PRIORITY);
			        t.start();
			    }
			});
		}
		{
			int posy, posx, width, height;
			Insets s = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
			posx = 0 + s.left;
			posy = 0 + s.top;
			width = (Toolkit.getDefaultToolkit().getScreenSize().width - s.right) / 3;
			height = (Toolkit.getDefaultToolkit().getScreenSize().height - s.bottom) / 3;
			this.setBounds(new Rectangle(posx, posy, width, height));
			this.setLocation(posx, posy);
		}
	}

	public UserWatchFrame(String[] head, String[][] data) {
		super();
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, boolean modal) {
		super(owner, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Dialog owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, JDialog owner) {
		super(owner);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner, boolean modal) {
		super(owner, modal);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame arg0, String arg1, boolean arg2, GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame arg0, String arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Frame owner) {
		super(owner);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner, String title) {
		super(owner, title);
		setup(head, data);
	}

	public UserWatchFrame(String[] head, String[][] data, Window owner) {
		super(owner);
		setup(head, data);
	}
	
	/**
	 * Use this method in order to add DataChangedNotifiers
	 * @param d The DataChangedNotifier to add.
	 */
	public void addDataChangedNotifier(DataChangedNotifier d){
		this.dcnotifiers.add(d);
	}
	
	private boolean wasChanged(String[] a, String[] b){
		if(a.length != b.length)
			return true;
		for(int i = 0; i < a.length; i++){
			if(!a[i].equals(b[i]))
				return true;
			else System.out.println(a[i] + " equals " + b[i]);
		}
		return false;
	}

}
