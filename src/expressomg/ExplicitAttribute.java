package expressomg;

public class ExplicitAttribute extends Attribute
{
	private final boolean isOptional;
	
	public ExplicitAttribute(Schema schema, String id, TypeRef type, boolean isOptional)
	{
		super(schema, id, type);
		this.isOptional = isOptional;
	}	

	public boolean isOptional()
	{
		return this.isOptional;
	}
	
	public boolean isDemanded()
	{
		return !this.isOptional;
	}
}
