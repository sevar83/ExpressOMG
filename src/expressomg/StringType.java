package expressomg;

public final class StringType extends SimpleType
{
	public static final String ID = "STRING";
	public static final StringType STRING = new StringType();
	
	protected StringType()
	{
		super(ID);
	}
}
