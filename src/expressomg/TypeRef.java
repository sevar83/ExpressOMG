package expressomg;

public class TypeRef extends Reference
{
	private final String typeId;
	private Type type;
	
	public TypeRef(Schema schema, String typeId)
	{
		super(schema);
		this.typeId = typeId;
	}
	
	public TypeRef(Schema schema, Type type)
	{
		super(schema);
		this.type = type;
		this.typeId = type.getId();
	}
	
	public Type getType()
	{
		if (this.type == null)
			this.type = this.schema.getTypes().get(this.typeId);		// Cache the type
		
		return this.type;
	}
	
	public String getId()
	{
		return this.typeId;
	}
	
	@Override
	public String toString()
	{
		return this.typeId;
	}
}