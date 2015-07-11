/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.service;

import java.util.List;
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
    
    Store getStore(String host, String port, String user, String password, String type, boolean tls)
            throws MailCheckException;
    
    Folder getFolder(Store store, String folderName, int permissions)
            throws MailCheckException;
    
    List<Message> searchMessages(Folder folder, FlagTerm flagTerm)
            throws MailCheckException;
    
    boolean closeConnection(Store store, Folder folder, boolean expunged);
    
}
