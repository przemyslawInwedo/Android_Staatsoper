package app.nexstreaming.nexplayerengine;

/**
 * API for sending log output.
 * Generally, use the NexLog.v() NexLog.d() NexLog.i() NexLog.w() and NexLog.e() methods.
 *
 * \since version 6.0.6
 */
 
 /** \deprecated  For internal use only.  Please do not use. */
public class NexLog {

    /**
     * Whether or not a log message should be sent to log output. 
     * 
     * If this value is \c TRUE, the log message will be sent to log output. 
     */
	public static boolean Debug = false;
	
	/**
	 * This method sends a DEBUG log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message to be logged.
	 */
	public static void d(String tag, String msg)
	{
		if(Debug)
		{
			android.util.Log.d(tag, msg);
		}
	}
	
	/**
	 * \brief  This method sends an ERROR log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message to be logged.
	 */
	public static void e(String tag, String msg)
	{
		if(Debug)
		{
			android.util.Log.e(tag, msg);
		}
	}	

	/**
	 * \brief  This method sends an INFO log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message to be logged.
	 */
	public static void i(String tag, String msg)
	{
		if(Debug)
		{
			android.util.Log.i(tag, msg);
		}
	}	
	
	/**
	 * \brief  This message sends a VERBOSE log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message to be logged.
	 */
	public static void v(String tag, String msg)
	{
		if(Debug)
		{
			android.util.Log.v(tag, msg);
		}
	}	
	
	/**
	 * \brief  This message sends a WARN log message.
	 * 
	 * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg The message to be logged.
	 */
	public static void w(String tag, String msg)
	{
		if(Debug)
		{
			android.util.Log.w(tag, msg);
		}
	}	
}
