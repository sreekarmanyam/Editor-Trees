package editortrees;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import editortrees.Node.Code;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	private int rotationCount;
	private DisplayableBinaryTree display = null;

	/**
	 * MILESTONE 1 Construct an empty tree
	 */
	public EditTree() {
		root = Node.NULL_NODE;
		rotationCount = 0;
	}

	/**
	 * MILESTONE 1 Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		root = new Node(ch, null);
		rotationCount = 0;
	}

	/**
	 * MILESTONE 2 Make this tree be a copy of e, with all new nodes, but the same
	 * shape and contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		root = e.root.copy(e.root, null);
	}

	/**
	 * MILESTONE 3 Create an EditTree whose toString is s. This can be done in O(N)
	 * time, where N is the size of the tree (note that repeatedly calling insert()
	 * would be O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		if (s.isEmpty()) {
			return;
		}
		int mid = s.length() / 2;
		char c = s.charAt(mid);
		root = new Node(c, null);
		root.rank = mid - 1;
		root.right = root.buildFromStr(root, s.substring(mid + 1));
		root.left = root.buildFromStr(root, s.substring(0, mid));
	}

	/**
	 * MILESTONE 1 returns the total number of rotations done in this tree since it
	 * was created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.rotationCount; // replace by a real calculation.
	}

	/**
	 * MILESTONE 1 return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		Iterator<Character> iter = this.iterator(); //this is an in-order iterator 
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			sb.append(iter.next().toString());
		}
		return sb.toString();

	}

	/**
	 * MILESTONE 1 This one asks for more info from each node. You can write it like
	 * the arraylist-based toString() method from the BinarySearchTree assignment.
	 * However, the output isn't just the elements, but the elements, ranks, and
	 * balance codes. Former CSSE230 students recommended that this method, while
	 * making it harder to pass tests initially, saves them time later since it
	 * catches weird errors that occur when you don't update ranks and balance codes
	 * correctly. For the tree with root b and children a and c, it should return
	 * the string: [b1=, a0=, c0=] There are many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		this.root.debugString(sb);
		if (sb.length() > 2) { //get rid of the last comma and space before closing bracket
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.
		if (root == Node.NULL_NODE) {
			root = new Node(ch, null);
			return;
		}
		MyWrapper wrapper = new MyWrapper();
		root = root.add(ch, null, wrapper);
		rotationCount += wrapper.rotationCount;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch  character to add
	 * @param pos character added in this inorder position
	 * @throws IndexOutOfBoundsException if pos is negative or too large for this
	 *                                   tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos > this.size()) {
			throw new IndexOutOfBoundsException();
		}
		if (root == Node.NULL_NODE) {
			root = new Node(ch, null);
			return;
		}
		MyWrapper wrapper = new MyWrapper();
		root = root.add(ch, pos, null, wrapper);
		rotationCount += wrapper.rotationCount;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param pos position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		return this.root.get(this.root, pos);

	}

	/**
	 * MILESTONE 1
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return root.height(0);
	}

	/**
	 * MILESTONE 2
	 * 
	 * @return the number of nodes in this tree, not counting the Node.NULL_NODE if
	 *         you have one.
	 */
	public int size() {
		return root.size(root);
	}

	/**
	 * MILESTONE 2
	 * 
	 * @param pos position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		if (pos < 0 || pos >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		char toBeFound = get(pos);
		MyWrapper wrapper = new MyWrapper();
		root = root.delete(pos, wrapper);
		rotationCount += wrapper.rotationCount;
		return toBeFound;
	}

	/**
	 * MILESTONE 3, EASY This method operates in O(length*log N), where N is the
	 * size of this tree.
	 * 
	 * @param pos    location of the beginning of the string to retrieve
	 * @param length length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException unless both pos and pos+length-1 are
	 *                                   legitimate indexes within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		int size = this.size();
		if (pos > size || pos + length - 1 > size - 1 || pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(this.root.get(this.root, pos + i));
		}
		return sb.toString();
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE THE PAPER REFERENCED IN THE SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree) the
	 * contents of the other tree to this one. Other should be made empty after this
	 * operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		int size = this.size();
		int otherSize = other.size();
		if (this == other) {
			throw new IllegalArgumentException();
		} else if (otherSize == 0) {
			return;
		} else if (size == 0) {
			this.root = other.root;
			other.root = Node.NULL_NODE;
			return;
		}
		if (this.height() < other.height()) {
			MyWrapper wrap = new MyWrapper();
			wrap.direction = false;
			other.root = other.root.concatenate(other.root, this, other.height(), wrap);
			this.root = other.root;
			other.root = Node.NULL_NODE;
			return;
		}
		MyWrapper wrap = new MyWrapper();
		wrap.direction = true;
		this.root = this.root.concatenate(this.root, other, this.height(), wrap);
		other.root = Node.NULL_NODE;
	}

	/**
	 * MILESTONE 3: DIFFICULT This operation must be done in time proportional to
	 * the height of this tree.
	 * 
	 * @param pos where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		// use a stack to keep track of the path of finding the target node
		Stack<Node> s = new Stack<Node>();
		s = root.fillStack(s, pos, root);
		Node target = s.pop();
		Node left = target.left;
		Node right = constructTree(Node.NULL_NODE, target, target.right);
		// poping nodes out from the stack for modification
		while (!s.isEmpty()) {
			Node temp = target;
			target = s.pop();
			if (temp == target.right) {
				left = constructTree(target.left, target, left);
			} else {
				right = constructTree(right, target, target.right);
			}
		}
		this.root = left;
		if (this.size() > 1) {
			this.root = balance(root);
		}
		EditTree result = new EditTree();
		result.root = right;
		return result;
	}

	/**
	 * handle update balance and rotation after split
	 * 
	 * @param node
	 * @return updated or rotated node
	 */
	public Node balance(Node node) {
		// if is unbalanced and left subtree is bigger
		if (node.left.height(0) - node.right.height(0) > 1) {
			String str = node.updateBalance(node, new MyWrapper(), false);
			if (str.equals("LeftFirst")) {
				node.left = node.handleRotation(node.left, node.left.right, new MyWrapper(), str);
				return node.handleRotation(node, node.left, new MyWrapper(), "SR");
			} else {
				return node.handleRotation(node, node.left, new MyWrapper(), str);
			}
		} else {// if is unbalanced and right subtree is bigger
			String str = node.updateBalance(node, new MyWrapper(), true);
			if (str.equals("RightFirst")) {
				node.right = node.handleRotation(node.right, node.right.left, new MyWrapper(), str);
				return node.handleRotation(node, node.right, new MyWrapper(), "SL");
			} else {
				return node.handleRotation(node, node.right, new MyWrapper(), str);
			}
		}
	}

	/**
	 * construct subtrees this method was implemented based on the method pasteHB in
	 * "Data structures in Pascal" by Edward M.Reingold & Wilfred J.Hansen which was
	 * provided in the EditorTree assignment
	 * 
	 * @param left subtree, sub root, right subtree
	 * @return a node with its subtrees constructed
	 */
	public Node constructTree(Node left, Node subRoot, Node right) {
		int leftHeight = left.height(0);
		int rightHeight = right.height(0);
		Node newNode = Node.NULL_NODE, parent = Node.NULL_NODE;
		int newNodeHeight;
		// if left subtree is bigger than the right subtree
		if (leftHeight >= rightHeight) {
			newNode = left;
			newNodeHeight = leftHeight;
			while (newNodeHeight - rightHeight > 1) {
				if (newNode.balance == Code.LEFT) {
					newNodeHeight -= 2;
				} else {
					newNodeHeight -= 1;
				}
				parent = newNode;
				newNode = newNode.right;
			}
			// modify the subtrees
			subRoot.left = newNode;
			subRoot.right = right;
			subRoot.rank = newNode.size(newNode);
			if (newNodeHeight == rightHeight) {
				subRoot.balance = Code.SAME;
			} else {
				subRoot.balance = Code.LEFT;
			}
			if (parent != Node.NULL_NODE) {
				parent.right = subRoot;
			} else {
				left = subRoot;
			}
			return balance(left);
		}
		// same concepts with the part above but in the opposite direction
		newNode = right;
		newNodeHeight = rightHeight;
		while (newNodeHeight - leftHeight > 1) {
			if (newNode.balance == Code.RIGHT) {
				newNodeHeight -= 2;
			} else {
				newNodeHeight -= 1;
			}
			parent = newNode;
			newNode = newNode.left;
		}
		subRoot.left = left;
		subRoot.right = newNode;
		subRoot.rank = left.size(left);
		if (newNodeHeight == leftHeight) {
			subRoot.balance = Code.SAME;
		} else {
			subRoot.balance = Code.RIGHT;
		}
		if (parent != Node.NULL_NODE) {
			parent.left = subRoot;
		} else {
			right = subRoot;
		}
		left = right;
		return balance(left);
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE This method is
	 * provided for you, and should not need to be changed. If split() and
	 * concatenate() are O(log N) operations as required, delete should also be
	 * O(log N)
	 * 
	 * @param start  position of beginning of string to delete
	 * 
	 * @param length length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException unless both start and start+length-1 are in
	 *                                   range for this tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3 Don't worry if you can't do this one efficiently.
	 * 
	 * @param s the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s does
	 *         not occur
	 */
	public int find(String s) {
		if (s.isEmpty()) {
			return 0;
		}
		if (root == Node.NULL_NODE) {
			return -1;
		}
		String str = this.toString();
		int result = str.indexOf(s);
		return result;
	}

	/**
	 * MILESTONE 3
	 * 
	 * @param s   the string to search for
	 * @param pos the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does not
	 *         occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		if (s.isEmpty()) {
			return 0;
		}
		if (root == Node.NULL_NODE) {
			return -1;
		}
		String str = this.toString();
		int result = str.indexOf(s, pos);
		return result;
	}

	/**
	 * @return the root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	/**
	 * 
	 * @return an inOrder iterator
	 */
	public Iterator<Character> iterator() {
		return new InOrderIterator();
	}

	/**
	 * 
	 * the inOrder iterator class
	 *
	 */
	public class InOrderIterator implements Iterator<Character> {
		Stack<Node> s;
		boolean canRemove;
		Node node;
		Character element;
		int treeSize;

		public InOrderIterator() {
			s = new Stack<Node>();
			node = root;
			canRemove = false;
			element = node.element;
			treeSize = size();
			while (this.node != Node.NULL_NODE) {
				s.push(this.node);
				this.node = this.node.left;
			}
		}

		@Override
		public boolean hasNext() {
			return !s.isEmpty();
		}

		public void remove() {
			if (!canRemove)
				throw new IllegalStateException();
			MyWrapper b = new MyWrapper();
			if (b.node != Node.NULL_NODE)
				s.push(b.node);
			canRemove = false;
		}

		@Override
		public Character next() {
			if (treeSize != size()) {
				throw new ConcurrentModificationException();
			}
			if (s.isEmpty()) {
				throw new NoSuchElementException();
			}
			node = s.pop();
			element = node.element;
			canRemove = true;
			if (node.right != Node.NULL_NODE) {
				node = node.right;
				while (node != Node.NULL_NODE) {
					s.push(node);
					node = node.left;
				}
			}
			return element;
		}
	}

	/**
	 * inner class holds multiple fields which are useful for add, delete and
	 * concatenate
	 * 
	 */
	public class MyWrapper {
		public int rotationCount = 0;
		public boolean neededUpdate = true, // true if needed update balance code
				neededRotation = false, // true if needed rotation
				doubleRotation = false, // true if a double rotation should take place
				specialCase = false, // true if is a special deletion case
				direction;
		public char gcDirection = ' ', // the position of the grand child(left or right).
										// useful for handling double rotation
				replaceBy; // the node which should replace the one that has been deleted
		private Node node = Node.NULL_NODE;

		public void setFalse() {
			neededUpdate = false;
		}

		public void setTrue() {
			neededUpdate = true;
		}

		public boolean neededUpdate() {
			return neededUpdate;
		}
	}

	// methods below are for the visualizer's use
	public int slowSize() {
		return this.root.slowSize();
	}

	public int slowHeight() {
		return this.root.slowHeight();
	}

	public void show() {
		if (this.display == null) {
			this.display = new DisplayableBinaryTree(this, 960, 1080, true);
		} else {
			this.display.show(true);
		}
	}

	public void close() {
		if (this.display != null) {
			this.display.close();
		}
	}
}
