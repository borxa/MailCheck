/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.details;

import java.io.IOException;
import javax.mail.Message;
import net.borxa.mailcheck.MailCheck;
import net.borxa.mailcheck.exception.MailCheckException;
import net.borxa.mailcheck.util.MailCheckUtil;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbPreferences;

/**
 * <p>DetailsJPanel class.</p>
 *
 * @author borxa
 * @version $Id: $Id
 */
public class DetailsJPanel extends javax.swing.JPanel {
    
    private static final long serialVersionUID = 1L;

    private final Message message;
    private final long messageId;

    /**
     * Creates new form DetailsJPanel
     *
     * @param message a {@link javax.mail.Message} object.
     */
    public DetailsJPanel(Message message) {
        this.message = message;
        this.messageId = MailCheckUtil.getMessageId(message);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();

        setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(MailCheck.class, "DetailsJPanel.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setText(MailCheckUtil.getContentMessage(message));
        jScrollPane2.setViewportView(jTextPane1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(MailCheck.class, "DetailsJPanel.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        try {
            String mailPath = NbPreferences.forModule(MailCheck.class).get("mail_path", "");
            new ProcessBuilder(mailPath).start();
        } catch (IOException ex) {
            StatusDisplayer.getDefault().setStatusText(ex.getMessage());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String host = NbPreferences.forModule(MailCheck.class).get("host", "");
                    String user = NbPreferences.forModule(MailCheck.class).get("user", "");
                    String password = NbPreferences.forModule(MailCheck.class).get("password", "");

                    MailCheckUtil.removeMessage(messageId, host, user, password);
                } catch (MailCheckException ex) {
                    StatusDisplayer.getDefault().setStatusText(ex.getMessage());
                }
            }
        });
        thread.start();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
