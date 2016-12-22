package expressomg;

public abstract class Attribute extends ExpressRoot
{
	private final String id;
	private String overridenId;
	private TypeRef owner;
	private final TypeRef type;
	
	protected Attribute(Schema schema, String id, TypeRef type)
	{
		super(schema);
		this.id = id;
		this.type = type;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public String getOverridenId()
	{
		return this.overridenId;
	}
	
	public void setOverridenId(String overridenId)
	{
		this.overridenId = overridenId;
	}
	
	public TypeRef getType()
	{
		return this.type;
	}
	
	public TypeRef getOwner()
	{
		return this.owner;
	}
	
	void setOwner(TypeRef owner)
	{
		this.owner = owner;
	}
	
	@Override
	public String toString()
	{
		return getId();
	}
}