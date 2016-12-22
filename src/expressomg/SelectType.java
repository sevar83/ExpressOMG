package expressomg;

import java.util.Collections;
import java.util.List;

public final class SelectType extends Type
{
	private final List<String> possibleTypes;
	
	protected SelectType(Schema schema, List<String> possibleTypes)
	{
		super(schema, null, null);	// Select types are anonymous. Id is given by its declaring type
		this.possibleTypes = Collections.unmodifiableList(possibleTypes);
	}

	public List<String> getPossibleTypes()
	{
		return this.possibleTypes;
	}
	
	@Override
	public String toString()
	{
		StringBuffer strBuffer = new StringBuffer("SELECT OF \n\t(");
		int i = 0, n = this.possibleTypes.size();
		for (String possibleType : this.possibleTypes)
		{			
			if (i > 0)
				strBuffer.append("\t,");
			strBuffer.append(possibleType);
			
			if (i < n - 1)
				strBuffer.append("\n");
			i++;
		}		
		strBuffer.append(")");
		
		return strBuffer.toString();
	}
}