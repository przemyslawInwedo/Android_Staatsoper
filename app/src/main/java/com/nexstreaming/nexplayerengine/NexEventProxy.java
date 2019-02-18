package com.nexstreaming.nexplayerengine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jake.you on 2015-04-01.
 */
class NexEventProxy {

	// Private member constant & variables
	private final static int DEFAULT_COLLECTION_CNT = 5;
	private HashMap< Integer, ArrayList<WeakReference<INexEventReceiver>>> mEventReceivers;

	// Constructor
	NexEventProxy() {
		mEventReceivers
				= new HashMap<Integer, ArrayList<WeakReference<INexEventReceiver>>> (DEFAULT_COLLECTION_CNT);
	}

	// Interface
	protected interface INexEventReceiver {
		NexPlayerEvent[] eventsAccepted();
		void onReceive(NexPlayer nexplayer, NexPlayerEvent event);
	}

	// Protected methods
	protected void registerReceiver(INexEventReceiver receiver) {
		if( receiver != null ) {
			NexPlayerEvent[] events = receiver.eventsAccepted();

			for (NexPlayerEvent event : events) {
				addEventReceiver(event, receiver);
			}
		}
	}

	private void addEventReceiver(NexPlayerEvent event, INexEventReceiver receiver) {
		ArrayList<WeakReference<INexEventReceiver>> receiverList = mEventReceivers.get(event.what);

		if( receiverList == null ) {
			receiverList = new ArrayList<WeakReference<INexEventReceiver>>();
			receiverList.add( new WeakReference<INexEventReceiver>(receiver) );
			mEventReceivers.put(event.what, receiverList);
		}
		else {
			receiverList.add(new WeakReference<INexEventReceiver>(receiver));
		}
	}

	protected void handleEvent(NexPlayer nexplayer, NexPlayerEvent event) {
		if ( nexplayer != null ) {
			if( mEventReceivers.size() > 0 ) {
				notifyEvent(nexplayer, event);
			}
		}
	}

	// Private methods
	private void notifyEvent( NexPlayer nexplayer, NexPlayerEvent event) {

		ArrayList<WeakReference<INexEventReceiver>> receivers = mEventReceivers.get( event.what );

		if( receivers != null ) {
			if( receivers.size() > 0 ) {
				ArrayList<WeakReference<INexEventReceiver>> removalList
						= new ArrayList<WeakReference<INexEventReceiver>>();

				for (WeakReference<INexEventReceiver> receiver : receivers) {
					INexEventReceiver _receiver = receiver.get();

					if (_receiver != null) {
						_receiver.onReceive(nexplayer, event);
					}
					else {
						removalList.add(receiver);
					}
				}
				receivers.removeAll(removalList);

				if ( receivers.size() == 0 ) {
					mEventReceivers.remove( event.what );
				}
			}
		}
	}
}