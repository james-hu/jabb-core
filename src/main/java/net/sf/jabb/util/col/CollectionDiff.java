/**
 * 
 */
package net.sf.jabb.util.col;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Compare and find out differences between collections
 * @author James Hu
 *
 */
public abstract class CollectionDiff {
	public static class Result<T>{
		private Set<T> onlyInLhs;
		private Set<T> onlyInRhs;
		private Set<T> inBoth;
		
		private Result(Set<T> onlyInLhs, Set<T> onlyInRhs, Set<T> inBoth){
			this.onlyInLhs = onlyInLhs;
			this.onlyInRhs = onlyInRhs;
			this.inBoth = inBoth;
		}
		
		public Set<T> getElementsOnlyInLhs() {
			return onlyInLhs;
		}
		public Set<T> getElementsOnlyInRhs() {
			return onlyInRhs;
		}
		public Set<T> getElementsInBoth() {
			return inBoth;
		}
		
	}
	
	/**
	 * Compare and find out the difference between two collections.
	 * The comparison depends on hashCode(...) and equals(...) methods of the elements.
	 * @param lhs	left hand side
	 * @param rhs	right hand side
	 * @return	the result with information about elements only in lhs, only in rhs, and in both.
	 */
	public static <T> Result<T> compare(Collection<T> lhs, Collection<T> rhs){
		Set<T> onlyInLhs = new HashSet<T>(lhs);
		onlyInLhs.removeAll(rhs);
		
		Set<T> inBoth = new HashSet<T>(lhs);
		inBoth.removeAll(onlyInLhs);
		
		Set<T> onlyInRhs = new HashSet<T>(rhs);
		onlyInRhs.removeAll(inBoth);
		
		return new Result<T>(onlyInLhs, onlyInRhs, inBoth);
	}

}
