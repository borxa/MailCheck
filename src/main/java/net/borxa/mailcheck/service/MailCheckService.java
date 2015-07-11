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
 * <p>MailCheckService interface define a set of operations over IMAPs server.</p>
 *
 * @author borxa.varela
 * @version $Id: $Id
 */
public interface MailCheckService {
    
    /**
     * <p>Gets store object for an IMAPs connection</p>
     *
     * @param host a hostname
     * @param port server port
     * @param user an user in email server
     * @param password password for user in email server
     * @param type type of email server (imaps, pops, ...)
     * @param tls true or false if secure connection
     * @return a {@link javax.mail.Store} object.
     * @throws net.borxa.mailcheck.exception.MailCheckException if any.
     */
    Store getStore(String host, String port, String user, String password, String type, boolean tls)
            throws MailCheckException;
    
    /**
     * <p>Gets a folder in a store connection to an IMAPs server</p>
     *
     * @param store a {@link javax.mail.Store} object of email server
     * @param folderName a folder name in email server account
     * @param permissions read only or read write
     * @return a {@link javax.mail.Folder} object.
     * @throws net.borxa.mailcheck.exception.MailCheckException if any.
     */
    Folder getFolder(Store store, String folderName, int permissions)
            throws MailCheckException;
    
    /**
     * <p>Search messages in a folder</p>
     *
     * @param folder a {@link javax.mail.Folder} object in email server
     * @param flagTerm a {@link javax.mail.search.FlagTerm} object with search filter
     * @return a {@link java.util.List} object of messages
     * @throws net.borxa.mailcheck.exception.MailCheckException if any.
     */
    List<Message> searchMessages(Folder folder, FlagTerm flagTerm)
            throws MailCheckException;
    
    /**
     * <p>Close connection of a folder and store to an IMAPs server</p>
     *
     * @param store a {@link javax.mail.Store} object of email server
     * @param folder a {@link javax.mail.Folder} object in email server
     * @param expunged true or false if remove messages flaged whit it
     * @return true or false if all operations done
     */
    boolean closeConnection(Store store, Folder folder, boolean expunged);
    
}
