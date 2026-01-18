package com.guicedee.services.jsonrepresentation;

/**
 * Runtime exception for JSON serialization/deserialization failures in this module.
 */
public class JsonRenderException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an empty JSON render exception.
	 */
	public JsonRenderException()
	{
		//No config required
	}
	
	/**
	 * Creates an exception with a message.
	 *
	 * @param message the error message
	 */
	public JsonRenderException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates an exception with a message and cause.
	 *
	 * @param message the error message
	 * @param cause   the root cause
	 */
	public JsonRenderException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates an exception with a cause.
	 *
	 * @param cause the root cause
	 */
	public JsonRenderException(Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * Creates an exception with full control over suppression and stack trace.
	 *
	 * @param message            the error message
	 * @param cause              the root cause
	 * @param enableSuppression  whether suppression is enabled
	 * @param writableStackTrace whether the stack trace should be writable
	 */
	public JsonRenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
