package jplot3dp;



import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import jplot3dp.Utils;

class HelpDialog extends JFrame
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private void enableButtons()
    {
        btnBack.setEnabled(historyIndex > 0);
        btnForward.setEnabled(historyIndex < urlHistory.size() - 1);
    }

    private void setURL(URL url)
    {
        try
        {
            ep.setPage(url);
            if(historyIndex < 0 || !url.equals((URL)urlHistory.get(historyIndex)))
            {
                urlHistory.add(url);
                for(historyIndex++; urlHistory.size() > historyIndex + 1; urlHistory.remove(historyIndex));
                enableButtons();
            }
        }
        catch(IOException ioexception) { }
    }

    private void goBack()
    {
        if(historyIndex > 0)
            setURL((URL)urlHistory.get(--historyIndex));
        enableButtons();
    }

    private void goForward()
    {
        if(historyIndex < urlHistory.size() - 1)
            setURL((URL)urlHistory.get(++historyIndex));
        enableButtons();
    }

    public HelpDialog(String s, String s1)
    {
        super(s);
        urlHistory = new ArrayList<URL>();
        historyIndex = -1;
        Container container = getContentPane();
        ep = new JEditorPane();
        ep.setEditable(false);
        ep.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent hyperlinkevent)
            {
                if(hyperlinkevent.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)
                    setURL(hyperlinkevent.getURL());
            }

        });
        ActionListener actionlistener = new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                String s2 = actionevent.getActionCommand();
                if(s2 == "Contents")
                    setURL(contents);
                else
                if(s2 == "Back")
                    goBack();
                else
                if(s2 == "Forward")
                    goForward();
            }

        };
        JToolBar jtoolbar = new JToolBar();
        btnContents = new JButton(Utils.loadIcon("home.gif"));
        Utils.makeHot(btnContents);
        jtoolbar.add(btnContents);
        btnContents.addActionListener(actionlistener);
        btnContents.setActionCommand("Contents");
        btnBack = new JButton(Utils.loadIcon("back.gif"));
        Utils.makeHot(btnBack);
        jtoolbar.add(btnBack);
        btnBack.addActionListener(actionlistener);
        btnBack.setActionCommand("Back");
        btnForward = new JButton(Utils.loadIcon("forward.gif"));
        Utils.makeHot(btnForward);
        jtoolbar.add(btnForward);
        btnForward.addActionListener(actionlistener);
        btnForward.setActionCommand("Forward");
        container.add(jtoolbar, "North");
        container.add(new JScrollPane(ep));
        setSize(480, 560);
        setLocationRelativeTo(null);
        setURL(contents = Utils.getURL(s1));
    }

    private URL contents;
    private JEditorPane ep;
    private ArrayList<URL> urlHistory;
    private int historyIndex;
    private JButton btnContents;
    private JButton btnBack;
    private JButton btnForward;




}
