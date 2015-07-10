/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.service;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.search.FlagTerm;
import net.borxa.mailcheck.exception.MailCheckException;

/**
 *
 * @author borxa.varela
 */
public class MailCheckServiceImpl implements MailCheckService {

    @Override
    public Store getStore(String host, String port, String user, String password, String type, boolean tls)
            throws MailCheckException {

        Store store = null;

        try {
            Properties properties = new Properties();
            properties.put("mail.imaps.host", host);
            properties.put("mail.imaps.port", port);
            properties.put("mail.imaps.starttls.enable", tls);
            Session emailSession = Session.getDefaultInstance(properties);

            store = emailSession.getStore(type);
            store.connect(host, user, password);
        } catch (NoSuchProviderException ex) {
            LOG.severe(ex.getMessage());
            throw new MailCheckException(ex);
        } catch (MessagingException ex) {
            LOG.severe(ex.getMessage());
            throw new MailCheckException(ex);
        }

        return store;
    }

    @Override
    public Folder getFolder(Store store, String folderName, int permissions)
            throws MailCheckException {

        Folder folder = null;
        
        try {
            folder = store.getFolder(folderName);
            folder.open(permissions);
        } catch (MessagingException ex) {
            LOG.severe(ex.getMessage());
            throw new MailCheckException(ex);
        }

        return folder;
    }

    @Override
    public List<Message> searchMessages(Folder folder, FlagTerm flagTerm) throws MailCheckException {
        
        try {
            
            Message[] messages = folder.search(flagTerm);      
            return Arrays.asList(messages);
            
        } catch (MessagingException ex) {
            LOG.severe(ex.getMessage());
            throw new MailCheckException(ex);
        }
    }

    @Override
    public boolean closeConnection(Store store, Folder folder, boolean expunged) {

        boolean closed = true;

        try {
            if (folder != null) {
                folder.close(expunged);
            }
        } catch (MessagingException ex) {
            closed = false;
            LOG.severe(ex.getMessage());
        }

        try {
            if (store != null) {
                store.close();
            }
        } catch (MessagingException ex) {
            closed = false;
            LOG.severe(ex.getMessage());
        }

        return closed;
    }

}
