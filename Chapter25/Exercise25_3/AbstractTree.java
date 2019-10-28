package exercise_25_3;

/* ADS Exercise 25-3: Implement inorder traversal of BST without using recursion. */

public abstract class AbstractTree<E> implements Tree<E> {
	
	@Override /** Inorder traversal from the root*/
	public void inorder() {
	}

	@Override /** Postorder traversal from the root */
	public void postorder() {
	}

	@Override /** Preorder traversal from the root */
	public void preorder() {
	}

	@Override /** Return true if the tree is empty */
	public boolean isEmpty() {
		return getSize() == 0;
	}
}