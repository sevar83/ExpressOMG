package expressomg;

public final class NumberType extends SimpleType
{
	public static final NumberType NUMBER = new NumberType();
	public static final NumberType REAL = new NumberType("REAL");		// A NUMBER but with redefined id to be swapped with REAL
	
	private NumberType()
	{
		super("NUMBER");
	}
	
	private NumberType(String redefinedId)
	{
		super(redefinedId);
	}
}
