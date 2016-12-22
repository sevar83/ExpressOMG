package expressomg;


public final class DeclaredType extends Type implements NamedType
{
	private final TypeRef underlyingType;
	
	public DeclaredType(Schema schema, String id, TypeRef underlyingType)
	{
		super(schema, id, underlyingType);
		this.underlyingType = underlyingType;
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}
	
	public Type getUnderlyingType()	
	{
		return this.underlyingType.getType();
	}
	
	@Override
	public TypeRef getSuperType()
	{
		return new TypeRef(getSchema(), getUnderlyingType());
	}
	
	@Override
	public String toString()
	{
		return "TYPE " + this.id + " = " + this.underlyingType;
	}
}
