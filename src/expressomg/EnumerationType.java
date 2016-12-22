package expressomg;

import java.util.Collections;
import java.util.List;

public final class EnumerationType extends Type
{
	private final List<String> ids;
	
	protected EnumerationType(Schema schema, List<String> ids)
	{
		super(schema, null, null);	// Enumerations are anonymous. Id is given by its declaring type
		
		this.ids = Collections.unmodifiableList(ids);
	}

	public List<String> getIds()
	{
		return this.ids;
	}
	
	@Override
	public String toString()
	{
		StringBuffer strBuffer = new StringBuffer("ENUMERATION OF \n\t(");
		int i = 0, n = this.ids.size();
		for (String id : this.ids)
		{			
			if (i > 0)
				strBuffer.append("\t,");
			strBuffer.append(id);
			
			if (i < n - 1)
				strBuffer.append("\n");
			i++;
		}		
		strBuffer.append(")");
		
		return strBuffer.toString();
	}
}
