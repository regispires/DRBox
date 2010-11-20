/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Drbox.java
 *
 * Created on 17/11/2010, 21:15:36
 */

package dfs.application;

import dfs.client.ClientModuleImpl;
import dfs.server.ds.DirectoryServiceImpl;
import dfs.server.fs.FileAttributes.FileType;

import helper.RemoteUtils;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import ligador.Ligador;

/**
 *
 * @author diegovss
 */
public class Drbox extends javax.swing.JFrame {

    /** Creates new form Drbox */
    public Drbox() throws RemoteException{
    	initComponents();
    	lblStatus.setText("Connecting to remote services...");
    	cm = ClientModuleImpl.getInstance();
    	hostLigador = RemoteUtils.getHostAddress();
    	portLigador = 1099;
    	try {
    		cm.start(hostLigador);
    		updateJlDirectory(currentDir);
    		lblStatus.setText("Connection estabilished");
		} catch (Exception e) {
			lblStatus.setText("Could not establish connection to remote services.");
		}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtFile = new javax.swing.JTextField();
        btnChooser = new javax.swing.JButton();
        btnUpload = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlDirectory = new javax.swing.JList();
        btnCreateDir = new javax.swing.JButton();
        btnDownload = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCancel = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuConfig = new javax.swing.JMenu();
        menuItemConfig = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Drbox");

        lblStatus.setForeground(new java.awt.Color(217, 2, 38));
        lblStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));

        btnChooser.setText("Choose");
        btnChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooserActionPerformed(evt);
            }
        });

        btnUpload.setText("Upload");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFile, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChooser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpload)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooser)
                    .addComponent(btnUpload))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Current Directory"));

        jlDirectory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jlDirectoryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jlDirectory);

        btnCreateDir.setText("Create Dir");
        btnCreateDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDirActionPerformed(evt);
            }
        });

        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCreateDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDownload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreateDir)
                    .addComponent(btnDownload)
                    .addComponent(btnDelete))
                .addContainerGap())
        );

        btnCancel.setText("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        menuConfig.setText("File");

        menuItemConfig.setText("Config");
        menuItemConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemConfigActionPerformed(evt);
            }
        });
        menuConfig.add(menuItemConfig);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuConfig.add(menuExit);

        jMenuBar1.add(menuConfig);

        menuHelp.setText("Help");

        menuItemAbout.setText("About");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        jMenuBar1.add(menuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCancel)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-472)/2, (screenSize.height-524)/2, 472, 524);
    }// </editor-fold>//GEN-END:initComponents

    private void btnChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooserActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        try {
        	int o = fc.showOpenDialog(this);
        	if(o == JFileChooser.APPROVE_OPTION){
	        	File f = fc.getSelectedFile().getAbsoluteFile();
	        	txtFile.setText(f.getAbsolutePath());
        	}
		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
		}

    }//GEN-LAST:event_btnChooserActionPerformed

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
    	SwingWorker<List<String>, String> worker = new SwingWorker<List<String>, String>(){

			@Override
			protected List<String> doInBackground() throws Exception {
				try {
					lblStatus.setText("Uploading file ...");
					jlDirectory.setEnabled(false);
					btnCancel.setEnabled(true);
					cm.upload(txtFile.getText(), currentDir, 0);
					lblStatus.setText("Upload completed");
					updateJlDirectory(currentDir);
				} catch (Exception e) {
					lblStatus.setText(e.getMessage());
				}finally{
					btnCancel.setEnabled(false);
					jlDirectory.setEnabled(true);
				}
				return null;
			}
    		
    	};
    	worker.execute();
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnCreateDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDirActionPerformed
        // TODO add your handling code here:
    	try {
    		String dirName = JOptionPane.showInputDialog("Directory name");
    		cm.create(currentDir+"/"+dirName, FileType.DIRECTORY, 0);
    		lblStatus.setText("Directory created");
    		updateJlDirectory(currentDir);
    	} catch (Exception e) {
    		lblStatus.setText(e.getMessage());
    	}
    }//GEN-LAST:event_btnCreateDirActionPerformed

    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
    	SwingWorker<List<String>, String> worker = new SwingWorker<List<String>, String>(){
    		
    		@Override
    		protected List<String> doInBackground() throws Exception {
    			try {
    				lblStatus.setText("Downloading file ...");
    				btnCancel.setEnabled(true);
    				cm.download(lastSelectedFile.getAbsolutePath(), currentDir+"/"+jlDirectory.getSelectedValue().toString(), 0);
    				lblStatus.setText("Download completed.");
    			} catch (Exception e) {
    				lblStatus.setText(e.getMessage());
    			}finally{
    				btnCancel.setEnabled(false);
    			}
    			return null;
    		}
    		
    	}; 
    	
    	try {
    		JFileChooser fc = new JFileChooser();
    		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		int o = fc.showOpenDialog(this);
    		if(o == JFileChooser.APPROVE_OPTION)
    			lastSelectedFile = fc.getSelectedFile().getAbsoluteFile();
    			worker.execute();
		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
		}
        
    	
    }//GEN-LAST:event_btnDownloadActionPerformed

    private void jlDirectoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jlDirectoryMouseClicked
    	try {
    		if(evt.getClickCount() == 2){
    			int index = jlDirectory.locationToIndex(evt.getPoint());
    			ListModel lm = jlDirectory.getModel();
    			String fileName = (String)lm.getElementAt(index);
    			currentDir = currentDir+"/"+fileName;
    			updateJlDirectory(currentDir);
    		}
		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
		}
    }//GEN-LAST:event_jlDirectoryMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
    	try {
    		String deletedFile = (String)jlDirectory.getSelectedValue();
    		cm.rm(currentDir+"/"+deletedFile, 0);
    		updateJlDirectory(currentDir);
    	} catch (Exception e) {
    		lblStatus.setText(e.getMessage());
    	}
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    	try {
    		cm.cancelOperation();
		} catch (Exception e) {
			lblStatus.setText(e.getMessage());
		}
    }//GEN-LAST:event_btnCancelActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
    	System.exit(0);
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        JOptionPane.showMessageDialog(this, "Sistemas Distribuídos UFC - 2010 Copyright.\n" +
        		"Diego Victor and Regis Pires.");
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuItemConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemConfigActionPerformed
    	String host = JOptionPane.showInputDialog("Change Ligador address", hostLigador);
    	if(host == null)
    		return;
    	try {
    		RemoteUtils.getRemoteObject(host, portLigador, RemoteUtils.LIGADOR);
    		hostLigador = host;
    		cm.start(hostLigador);
    		lblStatus.setText("");
    		updateJlDirectory(currentDir);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Ligador could not be found on "+host);
		}
    	
    }//GEN-LAST:event_menuItemConfigActionPerformed
    
    public void updateJlDirectory(String dirPath){
    	
    	try {
			Vector<String> files = (Vector<String>)cm.ls(dirPath, "*", 0);
			jlDirectory.setListData(files);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    new Drbox().setVisible(true);
                } catch (Exception e){
                    log.log(Level.WARNING, e.getMessage(), e);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChooser;
    private javax.swing.JButton btnCreateDir;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnUpload;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList jlDirectory;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JMenu menuConfig;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemConfig;
    private javax.swing.JTextField txtFile;
    // End of variables declaration//GEN-END:variables
    private ClientModuleImpl cm;
    private static Logger log = Logger.getLogger(DirectoryServiceImpl.class.getName());
    private String currentDir = "/";
    private File lastSelectedFile;
    private String hostLigador;
    private int portLigador;
}
