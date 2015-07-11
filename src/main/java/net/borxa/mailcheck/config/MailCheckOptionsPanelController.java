
/**
 * Creative Commons License: Attribution-ShareAlike 4.0 International
 * https://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * @author borxa
 * @version $Id: $Id
 */
package net.borxa.mailcheck.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        displayName = "#AdvancedOption_DisplayName_MailCheck",
        keywords = "#AdvancedOption_Keywords_MailCheck",
        keywordsCategory = "Advanced/MailCheck"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_MailCheck=MailCheck", "AdvancedOption_Keywords_MailCheck=mailcheck"})
public final class MailCheckOptionsPanelController extends OptionsPanelController {

    private MailCheckPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     * <p>update.</p>
     */
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     * <p>applyChanges.</p>
     */
    public void applyChanges() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getPanel().store();
                changed = false;
            }
        });
    }

    /**
     * <p>cancel.</p>
     */
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     * <p>isChanged.</p>
     *
     * @return a boolean.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * <p>getHelpCtx.</p>
     *
     * @return a {@link org.openide.util.HelpCtx} object.
     */
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /** {@inheritDoc} */
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /** {@inheritDoc} */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /** {@inheritDoc} */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private MailCheckPanel getPanel() {
        if (panel == null) {
            panel = new MailCheckPanel();
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
