/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.service;

/**
 * <p>MailCheckServiceFactory gets instances of MailCheckService implementations</p>
 *
 * @author borxa
 * @version $Id: $Id
 */
public class MailCheckServiceFactory {
    
    private static MailCheckService service = new MailCheckServiceImpl();
    
    /**
     * <p>Gets instance of MailCheckService implementation</p>
     *
     * @return a {@link net.borxa.mailcheck.service.MailCheckService} object.
     */
    public static MailCheckService getInstance() {
        
        return service;
    }
    
}
