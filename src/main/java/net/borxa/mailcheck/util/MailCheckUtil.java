/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import javax.swing.ImageIcon;
import net.borxa.mailcheck.MailCheck;
import net.borxa.mailcheck.balloon.BalloonJPanel;
import net.borxa.mailcheck.details.DetailsJPanel;
import net.borxa.mailcheck.exception.MailCheckException;
import net.borxa.mailcheck.service.MailCheckService;
import net.borxa.mailcheck.service.MailCheckServiceFactory;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.NotificationDisplayer.Priority;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

/**
 * <p>MailCheckUtil is a set of utils for encapsulate common tasks</p>
 *
 * @author borxa.varela
 * @version $Id: $Id
 */
public class MailCheckUtil {

    private static final String IMAPs = "imaps";
    private static final String IMAPs_PORT = "993";
    private static final MailCheckService service = MailCheckServiceFactory.getInstance();
    private static final Logger LOG = Logger.getLogger("MailCheckUtil");

    /**
     * <p>Gets the string content of message, first try for txt version and if
     * no result getted then try with html version</p>
     *
     * @param message a {@link javax.mail.Message} object.
     * @return a {@link java.lang.String} object with txt or html content
     */
    public static String getContentMessage(Message message) {

        String content = new String();

        try {

            Object obj = message.getContent();
            if (obj instanceof MimeMultipart) {
                MimeMultipart multipart = (MimeMultipart) obj;
                content = mimeMultiparttoTxt(multipart);
                if (content.length() < 1) {
                    content = mimeMultiparttoHtml(multipart);
                }
            } else if (obj instanceof String) {
                content = (String) obj;
            } else if (obj instanceof InputStream) {
                content = inputStreamtoStream((InputStream) obj);
            }

        } catch (IOException | MessagingException ex) {
            LOG.log(Level.INFO, "Email content not found: {0}", ex.getMessage());
        }

        return content;
    }

    /**
     * <p>Check for new unread messages in INBOX folder and make notifications
     * for them</p>
     *
     * @param host an IMAPs hostname
     * @param user an user in IMAPs server
     * @param password password for user in IMAPs server
     * @throws net.borxa.mailcheck.exception.MailCheckException if any.
     */
    public static void notifyNewUnreadMessages(
            final String host, final String user, final String password)
            throws MailCheckException {

        Store store = null;
        Folder folder = null;

        try {
            
            String inboxFolderName = NbPreferences.forModule(MailCheck.class).get("mail_inbox_folder", "INBOX");

            store = service.getStore(host, IMAPs_PORT, user, password, IMAPs, true);
            folder = service.getFolder(store, inboxFolderName, Folder.READ_ONLY);

            Flags flags = new Flags(Flags.Flag.SEEN);
            FlagTerm fterm = new FlagTerm(flags, false);
            List<Message>messages = service.searchMessages(folder, fterm);

            Set<Long> notifiesToRemove = new HashSet<>(MailCheck.getNotificationHistory().keySet());
            for (Message message : messages) {
                long messageId = getMessageId(message);
                if (!MailCheck.getNotificationHistory().containsKey(messageId)) {
                    Notification notification = newMailNotification(message);
                    MailCheck.getNotificationHistory().put(messageId, notification);
                } else {
                    notifiesToRemove.remove(messageId);
                }
            }
            
            for(long notifyToRemove : notifiesToRemove) {
                Notification notification = MailCheck.getNotificationHistory().remove(notifyToRemove);
                notification.clear();
            }

        } catch (MailCheckException | MessagingException | UnsupportedEncodingException ex) {
            throw new MailCheckException(ex);
        } finally {
            service.closeConnection(store, folder, false);
        }
    }

    /**
     * <p>Copy message to Trash folder and remove it in INBOX folder.</p>
     *
     * @param messageId a long.
     * @param host an IMAPs hostname
     * @param user an user in IMAPs server
     * @param password password for user in IMAPs server
     * @return true or false if all operations in remove are complete
     * @throws net.borxa.mailcheck.exception.MailCheckException if any.
     */
    public static boolean removeMessage(
            long messageId, final String host, final String user, final String password) throws MailCheckException {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(com.sun.mail.imap.IMAPSSLStore.class.getClassLoader());

        boolean removed = false;
        Store store = null;
        Folder folder = null;
        Notification notification = MailCheck.getNotificationHistory().get(messageId);

        try {
            
            notification.clear();
            
            String inboxFolderName = NbPreferences.forModule(MailCheck.class).get("mail_inbox_folder", "INBOX");

            store = service.getStore(host, IMAPs_PORT, user, password, IMAPs, true);
            folder = service.getFolder(store, inboxFolderName, Folder.READ_WRITE);
            
            UIDFolder uf = (UIDFolder)folder;
            Message[] messageDeleted = new Message[1];
            messageDeleted[0] = uf.getMessageByUID(messageId);
            messageDeleted[0].setFlag(Flags.Flag.SEEN, true);
            
            String trashFolderName = NbPreferences.forModule(MailCheck.class).get("mail_trash_folder", "Trash");

            Folder trash = service.getFolder(store, trashFolderName, Folder.READ_WRITE);
            folder.copyMessages(messageDeleted, trash);
            trash.close(true);
            messageDeleted[0].setFlag(Flags.Flag.DELETED, true);
            
            MailCheck.getNotificationHistory().remove(messageId);
            
            removed = true;
            
        } catch (MailCheckException | MessagingException ex) {
            notification.notify();
            throw new MailCheckException(ex);
        } finally {
            service.closeConnection(store, folder, true);
            Thread.currentThread().setContextClassLoader(orig);
        }

        return removed;
    }
    
    /**
     * <p>Get messageId for reference a message</p>
     *
     * @param message a {@link javax.mail.Message} object.
     * @return a messageId number
     */
    public static long getMessageId(Message message) {
        
        long messageId;
        
        try {
            UIDFolder uf = (UIDFolder)message.getFolder();
            
            messageId = uf.getUID(message);
        } catch (MessagingException ex) {
            messageId = -1;
        }
    
        return messageId;
    }

    private static String notifyText(Message message) throws MessagingException, UnsupportedEncodingException {

        final String subject = org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.subject");
        final String remitent = org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.remitent");

        StringBuilder notifyText = new StringBuilder(subject).append(message.getSubject());
        String from = MimeUtility.decodeText(message.getFrom()[0].toString());
        from = from.replaceAll("<", "(");
        from = from.replaceAll(">", ")");
        notifyText.append(remitent).append(from);

        return notifyText.toString();
    }

    private static Priority getMessagePriority(Message message) throws MessagingException {

        Priority priority = NotificationDisplayer.Priority.NORMAL;

        String[] header = message.getHeader("X-Priority");
        if (header != null && header.length > 0) {
            String prty = header[0];
            switch (prty) {
                case "1":
                    priority = NotificationDisplayer.Priority.HIGH;
                    break;
                case "5":
                    priority = NotificationDisplayer.Priority.LOW;
                    break;
                default:
                    priority = NotificationDisplayer.Priority.NORMAL;
                    break;
            }
        }

        return priority;
    }

    private static Notification newMailNotification(Message message) throws MessagingException, UnsupportedEncodingException {

        final String title = notifyText(message);
        final ImageIcon icon = ImageUtilities.loadImageIcon("icon.png", false);
        final Priority priority = getMessagePriority(message);
        final NotificationDisplayer.Category category = NotificationDisplayer.Category.INFO;

        Notification notification = NotificationDisplayer.getDefault().notify(
                title,
                icon,
                new BalloonJPanel(),
                new DetailsJPanel(message),
                priority,
                category);
        
        return notification;

    }
    
    private static String inputStreamtoStream(InputStream in)
            throws UnsupportedEncodingException, IOException {

        StringBuilder sb = new StringBuilder();

        InputStreamReader is = new InputStreamReader(in, "UTF-8");
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }

        return sb.toString();
    }

    private static String mimeMultiparttoTxt(MimeMultipart multipart)
            throws MessagingException, IOException {

        String result = new String();

        for (int j = 0; j < multipart.getCount(); j++) {

            BodyPart bodyPart = multipart.getBodyPart(j);

            String disposition = bodyPart.getDisposition();
            if (disposition == null || !disposition.equalsIgnoreCase("ATTACHMENT")) {
                Object obj = bodyPart.getContent();
                if (bodyPart.isMimeType("text/plain")) {
                    result = (String) obj;
                    break;
                } else if (obj instanceof MimeMultipart) {
                    result = mimeMultiparttoTxt((MimeMultipart) obj);
                    break;
                }
            }
        }

        return result;
    }

    private static String mimeMultiparttoHtml(MimeMultipart multipart)
            throws MessagingException, IOException {

        String result = new String();

        for (int j = 0; j < multipart.getCount(); j++) {

            BodyPart bodyPart = multipart.getBodyPart(j);

            String disposition = bodyPart.getDisposition();
            if (disposition == null || !disposition.equalsIgnoreCase("ATTACHMENT")) {
                Object obj = bodyPart.getContent();
                if (bodyPart.isMimeType("text/html")) {
                    result = (String) obj;
                    break;
                } else if (obj instanceof MimeMultipart) {
                    result = mimeMultiparttoHtml((MimeMultipart) obj);
                    break;
                }
            }
        }

        return result;
    }

}
