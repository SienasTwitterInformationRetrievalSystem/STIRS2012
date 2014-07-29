package stirsx.util;

import java.util.Date;

/**
 * The <code>Log</code> class provides <code>static</code> methods for
 * convenient error logging.
 * 
 * Inspired by Kevin's DefaultLogSystem.java in Slick2d.
 *
 * @author David Purcell v1.0
 * @version 6/2011 v1.0
 */
public final class Log {
     //Whether or not logging is enabled.
     private static boolean enabled = true;
    
     //Whether or not the logging is currently verbose.
     private static boolean isVerbose = true;
	
     private Log(){}

     /**
      * Logs an error message.
      *
      * @param message The message to log.
      */
     public static void info(Object message){
          if(!isVerbose || !enabled){
               return;
          }
         
          System.out.println(new Date() + " INFO: " + message);
     }

     /**
      * Logs a caught exception.
      *
      * @param ex The exception to log.
      */
     public static void exception(Throwable ex){
		if(!enabled){
			return;
		}
		
		System.err.println(new Date() + " EXCEPTION: " + 
                       ex.getMessage());
		ex.printStackTrace(System.err);
     }
    
     /**
      * Logs a warning message.
      * 
      * @param message The message to log. 
      */
     public static void warn(Object message){
          if(!enabled){
			return;
          }
			
          System.err.println(new Date() + " WARN: " + message); 
     }
    
     /**
      * Logs an error message.
      * 
      * @param message The message to log. 
      */
     public static void error(Object message){
          if(!enabled){
			return;
          }
			
          System.err.println(new Date() + " ERROR: " + message);   
     }
    
     /**
      * Logs a debug message.
      * 
      * @param message The message to log. 
      */
     public static void debug(Object message){
          if(!isVerbose || !enabled){
               return;
          }
         
          System.out.println(new Date() + " DEBUG: " + message);
     }
    
     /**
      * Sets the verbosity of the logging.
      * A verbose log setting displays all messages.
      * A non-verbose log will not display info and debug messages.
      * 
      * @param verbose The verbosity of the log. 
      */
     public static void setVerbose(boolean verbose){
          isVerbose = verbose;
     }
    
	/**
	 * Sets the enabled status of the logger.
	 * A disabled logger will not display any messages.
	 *
	 * @param enabled Whether or not to enable logging.
	 */
     public static void setEnabled(boolean enabled){
		Log.enabled = enabled;
	}
}