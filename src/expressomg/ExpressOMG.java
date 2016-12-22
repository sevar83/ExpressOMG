package expressomg;

import java.io.BufferedInputStream;
import java.io.IOException;

import net.sourceforge.osexpress.apps.pretty.PrettyWalker;
import net.sourceforge.osexpress.parser.EasyParser;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;

/** 
 * EXPRESS Object Model Generator
 * 
 * @author Svetlozar Kostadinov
 * 
 */
public class ExpressOMG 
{
	private EasyParser parser;
	private PrettyWalker walker;
	private ExpressObjectModel objectModel;
	
	public ExpressOMG(BufferedInputStream inputStream)
	{		
		EasyParser parser;
		try
		{
			parser = new EasyParser(inputStream);
			CommonAST rootAst = parser.parse();
			this.objectModel = new ExpressObjectModel(rootAst);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (RecognitionException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		catch (TokenStreamException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}	    
	}
	
	public ExpressObjectModel getResult()
	{
		return this.objectModel;
	}
	
	public static ExpressObjectModel create(BufferedInputStream inputStream)
	{
		ExpressOMG exOMG = new ExpressOMG(inputStream);
		return exOMG.getResult();
	}
}
