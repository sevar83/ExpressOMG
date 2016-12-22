package expressomg;

import static net.sourceforge.osexpress.parser.ExpressParserTokenTypes.SYNTAX;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.osexpress.parser.ExpressParser;
import antlr.CommonAST;

public class ExpressObjectModel
{
	private final Map<String, Schema> schemas;
	
	public ExpressObjectModel(CommonAST rootAst)
	{		
		if (rootAst == null)
			throw new NullPointerException();
		if (rootAst.getType() != SYNTAX)
			throw new IllegalArgumentException();
				
		Map<String, Schema> schemas = new HashMap<String, Schema>();
		
		ASTNode syntaxNode = new ASTNode(rootAst);
		for (ASTNode schemaNode : syntaxNode.getChildren())
		{
			if (schemaNode.getType() == ExpressParser.SCHEMA_DECL)
			{
				Schema schema = Extractor.extractSchema(schemaNode);
				schemas.put(schema.getId(), schema);
			}
		}
		
		this.schemas = Collections.unmodifiableMap(schemas);
	}
	
	public Map<String, Schema> getSchemas()
	{
		return this.schemas;
	}
}