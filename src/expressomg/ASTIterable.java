package expressomg;

import java.util.Iterator;

import antlr.collections.AST;

public class ASTIterable implements Iterable<AST>
{
	private final AST ast;
	
	public ASTIterable(AST ast)
	{
		this.ast = ast;
	}
	
	@Override
	public Iterator<AST> iterator()
	{
		return new ASTIterator(this.ast);
	}
	
	public static <T extends AST> Iterable<AST> it(T ast)
	{
		return new ASTIterable(ast);
	}
	
	private static class ASTIterator<T extends AST> implements Iterator<T>
	{
		private final AST ast;
		//private int i = 0;
		private AST current;
		
		public ASTIterator(AST ast)
		{
			this.ast = ast;
		}

		@Override
		public boolean hasNext()
		{
			return this.current == null ? 
					this.ast.getFirstChild() != null : 
					this.current.getNextSibling() != null;
		}

		@Override
		public T next()
		{			
			T next;
			if (this.current == null)
			{
				next = (T) this.ast.getFirstChild();
			}
			else
			{
				next = (T) this.current.getNextSibling();				
			}
			this.current = next;
			return next;
		}
		
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
