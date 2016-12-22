package expressomg;

public abstract class ExpressRoot
{
	private final Schema schema;
	
	protected ExpressRoot(Schema schema)
	{
		this.schema = schema;
	}
	
	public Schema getSchema()
	{
		return this.schema;
	}
}
