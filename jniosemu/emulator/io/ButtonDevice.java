package jniosemu.emulator.io;

import java.util.Vector;
import jniosemu.emulator.memory.MemoryManager;
import jniosemu.events.Events;
import jniosemu.events.EventManager;
import jniosemu.events.EventObserver;
/**
 * Handle the dipswitches
 */
public class ButtonDevice extends IODevice implements EventObserver
{
	/**
	 * Address to memory where this is placed
	 */
	private static final int MEMORYADDR = 0x840;
	/**
	 * Length of memory that is used
	 */
	private static final int MEMORYLENGTH = 16;
	/**
	 * Name of memoryblock
	 */
	private static final String MEMORYNAME = "Buttons";
	/**
	 * Number of buttons
	 */
	private static final int COUNT = 4;
	/**
	 * Containing the states of each dipswitch
	 */
	private Vector<Boolean> state;
	/**
	 * Used MemoryManager
	 */
	private MemoryManager memory;
	/**
	 * Used EventManager
	 */
	private EventManager eventManager;

	/**
	 * Init ButtonDevice
	 *
	 * @post Add events. Init states.
	 * @calledby IOManager()
	 *
	 * @param memory  current MemoryManager
	 * @param eventManager current EventManager
	 */
	public ButtonDevice(MemoryManager memory, EventManager eventManager) {
		this.eventManager = eventManager;

		String[] events = {
			Events.EVENTID_GUI_BUTTON_RELEASED,
			Events.EVENTID_GUI_BUTTON_PRESSED,
			Events.EVENTID_GUI_BUTTON_TOGGLE};
		this.eventManager.addEventObserver(events, this);

		this.reset(memory);
	}

	/**
	 * Reset
	 *
	 * @calledby  IOManager.reset()
	 *
	 * @param memory current MemoryManager
	 */
	public void reset(MemoryManager memory) {
		this.memory = memory;
		this.memory.register(MEMORYNAME, MEMORYADDR, MEMORYLENGTH, this);

		this.state = new Vector<Boolean>(COUNT);
		for (int i = 0; i < COUNT; i++)
			this.state.add(i, false);

		this.memoryChange();
		this.sendEvent();
	}

	/**
	 * When memory change in in this region this method is called. And then we
	 * want to restore the memory.
	 *
	 * @calledby MemoryManager.memoryChange()
	 */
	public void memoryChange() {
		this.memory.writeInt(MEMORYADDR     , this.vectorToInt(this.state), false);
		this.memory.writeInt(MEMORYADDR +  4, 0, false);
		this.memory.writeInt(MEMORYADDR +  8, 0, false);
		this.memory.writeInt(MEMORYADDR + 12, 0, false);
	}

	/**
	 * Send states to eventManager
	 *
	 * @calledby ButtonDevice(), reset(), update()
	 */
	private void sendEvent() {
		this.eventManager.sendEvent(Events.EVENTID_UPDATE_BUTTONS, this.state);
	}

	/**
	 * Set state of a button
	 *
	 * @calledby update()
	 *
	 * @param index button index
	 * @param state new state
	 */
	private void setState(int index, boolean state) {
		this.state.set(index, state);
		this.memory.writeInt(MEMORYADDR, this.vectorToInt(this.state), false);

		this.sendEvent();
	}

	public void update(String eventIdentifier, Object obj) {
		if (eventIdentifier.equals(Events.EVENTID_GUI_BUTTON_RELEASED)) {
			this.setState(((Integer)obj).intValue(), false);
		} else if (eventIdentifier.equals(Events.EVENTID_GUI_BUTTON_PRESSED)) {
			this.setState(((Integer)obj).intValue(), true);
		} else if (eventIdentifier.equals(Events.EVENTID_GUI_BUTTON_TOGGLE)) {
			int index = ((Integer)obj).intValue();
			this.setState(index, !this.state.get(index));
		}
	}
}
