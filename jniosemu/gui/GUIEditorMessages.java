package jniosemu.gui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;

import jniosemu.events.*;

/** 
 * Creates and manages the GUI component of the editor message view.
 */
public class GUIEditorMessages extends JPanel 
                       implements ActionListener, EventObserver {
	
	/**
	 * Reference to EventManager used to receive
	 * and send events.
	 */
	private EventManager eventManager;
	
	/**
	 * The text area used to display messages.
	 */
	private JTextArea editorMessages;
	
	/**
	 * Initiates the creation of GUI components and adds itself to
	 * the Event Manager as an observer.
	 *
	 * @post      eventManager reference is set for this object.
	 * @calledby  GUIManager.setup()
	 * @calls     setup(), EventManager.addEventObserver()
	 *
	 * @param  eventManager  The Event Manager object.
	 */
	public GUIEditorMessages(EventManager eventManager)
	{
		super();
		
		this.eventManager = eventManager;
		
		setup();
		
    // add events to listen to
    this.eventManager.addEventObserver("DEBUG", this);
	}

	/**
	 * Setup GUI components and attributes.
	 *
	 * @post      components created and added to panel
	 * @calledby  GUIEditorMessages
	 */
	private void setup()
	{
		editorMessages = new JTextArea("Editor messages...\n", 5, 60);
		editorMessages.setEditable(false);

		// put scrollbars around editor text area
		JScrollPane editorMessagesScrollPane =
		    new JScrollPane(editorMessages,
		                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// put everything into the editor panel
		this.setLayout(new BorderLayout());
		this.add(editorMessagesScrollPane, BorderLayout.CENTER);
	}

	public void update(String eventIdentifier, Object obj)
	{
		String debugMessage = (String) obj;
		
		editorMessages.append(eventIdentifier + ": " + debugMessage + "\n");
	}

	/**
	 * Invoked when a GUI action occurs, forwards it as
	 * an event to the EventManager object.
	 *
	 * @calls     EventManager.sendEvent()
	 *
	 * @param  e  action event object
	 */
  public void actionPerformed(ActionEvent e) {
  		eventManager.sendEvent(e.getActionCommand());
  }
	
}