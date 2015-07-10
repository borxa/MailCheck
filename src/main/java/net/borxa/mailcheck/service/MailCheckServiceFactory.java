/*
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 */
package net.borxa.mailcheck.service;

/**
 *
 * @author borxa
 */
public class MailCheckServiceFactory {
    
    private static MailCheckService service = new MailCheckServiceImpl();
    
    public static MailCheckService getInstance() {
        
        return service;
    }
    
}
