package expressomg;

public final class ListType extends AggregationType
{
	protected ListType(Schema schema, TypeRef baseType)
	{
		super(schema, "LIST", baseType);
	}
}
