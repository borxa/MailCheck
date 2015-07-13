/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.borxa.mailcheck.exception.MailCheckException;
import net.borxa.mailcheck.util.MailCheckUtil;
import org.openide.awt.Notification;
import org.openide.awt.StatusDisplayer;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.windows.OnShowing;

/**
 * <p>MailCheck checks for new unread messages in INBOX folder of IMAPs 
 * server host account and notify them in Netbeans notifications window.</p>
 *
 * @author borxa.varela
 * @version $Id: $Id
 */
@OnShowing
public class MailCheck extends ModuleInstall implements Runnable {

    private static boolean secondRun = false;
    private static final RequestProcessor RP = new RequestProcessor(MailCheck.class);
    private static final Logger LOG = Logger.getLogger("MailCheck");
    
    private static final Map<Long, Notification> notifications = new HashMap<>();
    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public void run() {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(com.sun.mail.imap.IMAPSSLStore.class.getClassLoader());

        try {

            int delay = Integer.parseInt(NbPreferences.forModule(MailCheck.class).get("delay", org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.delay-default")));
            if (secondRun && delay > 0) {

                Date time = new Date();
                StatusDisplayer.getDefault().setStatusText(org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.searching-messages"));

                String host = NbPreferences.forModule(MailCheck.class).get("host", "");
                String user = NbPreferences.forModule(MailCheck.class).get("user", "");
                String password = NbPreferences.forModule(MailCheck.class).get("password", "");

                MailCheckUtil.notifyNewUnreadMessages(host, user, password);

                long seconds = (new Date().getTime() - time.getTime()) / 1000;
                StringBuilder sb = new StringBuilder(org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.emails-searched-in"));
                sb.append(Long.toString(seconds)).append(" ").append(org.openide.util.NbBundle.getMessage(MailCheck.class, "MailCheck.seconds"));
                StatusDisplayer.getDefault().setStatusText(sb.toString());

                RP.schedule(this, delay, TimeUnit.SECONDS);
            } else {
                secondRun = true;
                StatusDisplayer.getDefault().setStatusText("MailCheck: Searching emails in " + delay + " seconds.");
                RP.schedule(this, delay, TimeUnit.SECONDS);
            }
        } catch (MissingResourceException | MailCheckException | NumberFormatException e) {
            StatusDisplayer.getDefault().setStatusText(new StringBuilder("MailCheck: ")
                    .append(e.getMessage()).append(". New run in 5 minutes.").toString());
            LOG.log(Level.INFO, "{0}. New run in 5 minutes.", e.getMessage());
            RP.schedule(this, 20, TimeUnit.SECONDS);
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
    
    /**
     * <p>Gets a list of all pairs of messageId and notification done</p>
     *
     * @return a {@link java.util.Map} object with messageId and his email 
     * notification asociated
     */
    public static Map<Long, Notification> getNotificationHistory() {
        
        return notifications;
    }
}
