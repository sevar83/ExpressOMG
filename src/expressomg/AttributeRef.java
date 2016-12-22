package expressomg;

public class AttributeRef extends Reference
{
	private final String attributeId;	
	private Attribute attribute;
	private TypeRef entityRef;	// defines scope
	
	public AttributeRef(Schema schema, TypeRef entityRef, String attributeId)
	{
		super(schema);
		this.entityRef = entityRef;
		this.attributeId = attributeId;
	}
	
	public AttributeRef(Schema schema, TypeRef entityRef, Attribute attribute)
	{
		super(schema);
		this.entityRef = entityRef;
		this.attribute = attribute;
		this.attributeId = attribute.getId();
	}
	
	public Attribute getAttribute()
	{
		if (this.attribute == null)
			this.attribute = ((EntityType) entityRef.getType()).getAttribute(this.attributeId);
		
		return this.attribute;
	}
	
	public String getId()
	{
		return this.attributeId;
	}
	
	@Override
	public String toString()
	{
		return this.attributeId;
	}
}
