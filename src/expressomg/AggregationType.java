package expressomg;


public abstract class AggregationType extends Type
{
	private final TypeRef baseType;
	
	protected AggregationType(Schema schema, String id, TypeRef baseType)
	{
		super(null, id, null);

		this.baseType = baseType;
	}
	
	public TypeRef getBaseType()
	{
		return this.baseType;
	}
	
	public String getBaseTypeId()
	{
		return this.baseType.getId();
	}
	
	@Override
	public boolean isAggregation()
	{
		return true;
	}	
}
