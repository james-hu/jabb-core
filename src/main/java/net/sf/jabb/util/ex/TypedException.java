/**
 * 
 */
package net.sf.jabb.util.ex;

/**
 * Exception with a type field.
 * @author James Hu
 *
 */
public class TypedException extends Exception{
	private static final long serialVersionUID = 82771749194795505L;
	protected int type;
	
	public int getType(){
		return this.type;
	}
	
    /** Constructs a new exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     * @param	type type of the exception
     */
	public TypedException(int type){
		super();
		this.type = type;
	}

    /** Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param	type type of the exception
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public TypedException(int type, String message) {
        super(message);
		this.type = type;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param	type type of the exception
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public TypedException(int type, String message, Throwable cause) {
        super(message, cause);
		this.type = type;
    }

    /** Constructs a new exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param	type type of the exception
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public TypedException(int type, Throwable cause) {
        super(cause);
		this.type = type;
    }

}
