/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.service;

import java.util.List;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import net.borxa.mailcheck.exception.MailCheckException;

/**
 *
 * @author borxa.varela
 */
public interface MailCheckService {
    
    public static String POP3s = "pop3s";
    public static String IMAPs = "imaps";
    public static String IMAPs_PORT = "993";
    public static final Logger LOG = Logger.getLogger("MailCheckService");
    
    public Store getStore(String host, String port, String user, String password, String type, boolean tls)
            throws MailCheckException;
    
    public Folder getFolder(Store store, String folderName, int permissions)
            throws MailCheckException;
    
    public List<Message> searchMessages(Folder folder, FlagTerm flagTerm)
            throws MailCheckException;
    
    public boolean closeConnection(Store store, Folder folder, boolean expunged);
    
}
