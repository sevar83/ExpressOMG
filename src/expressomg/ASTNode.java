package expressomg;

import static expressomg.ASTIterable.it;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import antlr.collections.AST;
import antlr.collections.ASTEnumeration;

public class ASTNode implements Iterable<AST>
{
	private final AST ast;
	private final ASTIterable iter;
	
	public ASTNode(AST ast)
	{
		this.ast = ast;
		this.iter = new ASTIterable(ast);
	}
	
	public ASTNode getFirstChild()
	{
		AST firstChild = this.ast.getFirstChild();
		if (firstChild != null)
			return new ASTNode(firstChild);
		return null;
	}
	
	public ASTNode getNextSibling()
	{
		AST nextSibling = this.ast.getNextSibling();
		if (nextSibling != null)
			return new ASTNode(nextSibling);
		return null;
	}
	
	/**
	 * Get the first child of a given type
	 * 
	 * @param type
	 * @return
	 */
	public ASTNode getChild(int type)
	{
		for (AST child : it(this.ast))			
		{
			if (child.getType() == type)
				return new ASTNode(child);
		}
		return null;
	}
	
	public List<ASTNode> getChildren()
	{
		List<ASTNode> children = new ArrayList<ASTNode>();
		
		for (AST child : it(this.ast))
		{
			children.add(new ASTNode(child));
		}
		
		return children;
	}
	
	public List<ASTNode> getChildren(int type)
	{
		List<ASTNode> children = new ArrayList<ASTNode>();
		
		for (AST child : it(this.ast))
		{
			if (child.getType() == type)
				children.add(new ASTNode(child));
		}
		
		return children;
	}
	
	public int getType()
	{
		return this.ast.getType();
	}
	
	public String getText()
	{
		return this.ast.getText();
	}
	
	public ASTEnumeration findAll(ASTNode node)
	{
		return this.ast.findAll(node.ast);
	}

	@Override
	public Iterator<AST> iterator()
	{
		return this.iter.iterator();
	}
	
	@Override
	public String toString()
	{
		return this.ast.toString();
	}
}
