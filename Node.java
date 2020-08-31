package editortrees;

import java.util.Stack;

import editortrees.EditTree.MyWrapper;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {
	public final static Node NULL_NODE = new Node();
	public Node parent;
	public DisplayableNodeWrapper dnw;

	public Node() {
		this.left = null;
		this.right = null;
		this.dnw = new DisplayableNodeWrapper(this);
		this.rank = 0;
	}

	public Node(Character ch, Node parent) {
		this.element = ch;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.parent = parent;
		this.balance = Code.SAME;
		this.dnw = new DisplayableNodeWrapper(this);
	}

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects

	char element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	// Node parent; // You may want this field.
	// Feel free to add other fields that you find useful

	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they work
	// correctly

	public Node add(Character ch, Node parent, MyWrapper wrapper) {
		if (this == NULL_NODE)
			return new Node(ch, parent);
		this.right = this.right.add(ch, this, wrapper);
		if (wrapper.neededUpdate()) {
			String str = updateBalance(this, wrapper, true);
			if (neededRotation(wrapper)) {
				if (str.equals("RightFirst")) {
					this.right = handleRotation(this.right, this.right.left, wrapper, str);
					return handleRotation(this, this.right, wrapper, "SL");
				} else {
					return handleRotation(this, this.right, wrapper, str);
				}
			}
		}
		return this;
	}

	public Node add(Character ch, int pos, Node parent, MyWrapper wrapper) {
		if (this == NULL_NODE) {
			this.rank = 0;
			return new Node(ch, parent);
		}
		if (pos > this.rank) {// if needs to go right
			this.right = this.right.add(ch, pos - this.rank - 1, this, wrapper);
			if (wrapper.neededUpdate()) {// update balance codes and handle rotation
				String str = updateBalance(this, wrapper, true);
				if (neededRotation(wrapper)) {
					if (str.equals("RightFirst")) {
						this.right = handleRotation(this.right, this.right.left, wrapper, str);
						return handleRotation(this, this.right, wrapper, "SL");//return the rotated subtree
					} else {
						return handleRotation(this, this.right, wrapper, str);//return the rotated subtree
					}
				}
			}
		} else if (pos < this.rank) {// if needs to go left
			this.rank++;
			this.left = this.left.add(ch, pos, this, wrapper);
			if (wrapper.neededUpdate()) {// update balance codes and handle rotation
				String str = updateBalance(this, wrapper, false);
				if (neededRotation(wrapper)) {
					if (str.equals("LeftFirst")) {
						this.left = handleRotation(this.left, this.left.right, wrapper, str);
						return handleRotation(this, this.left, wrapper, "SR");//return the rotated subtree
					} else {
						return handleRotation(this, this.left, wrapper, str);//return the rotated subtree
					}
				}
			}
		} else if (pos == this.rank) {// if is the right spot for adding
			this.rank++;
			this.left = this.left.add(ch, pos, this, wrapper);
			if (wrapper.neededUpdate()) {// update balance codes and handle rotation
				String str = updateBalance(this, wrapper, false);
				if (neededRotation(wrapper)) {
					if (str.equals("LeftFirst")) {
						this.left = handleRotation(this.left, this.left.right, wrapper, str);
						return handleRotation(this, this.left, wrapper, "SR");//return the rotated subtree
					} else {
						return handleRotation(this, this.left, wrapper, str);//return the rotated subtree
					}
				}
			}
		}
		return this;
	}

	/**
	 * 
	 * @param wrapper
	 * @return true if (a) rotation(s) is needed
	 */
	public boolean neededRotation(MyWrapper wrapper) {
		return wrapper.rotationCount == 0 && wrapper.doubleRotation
				|| wrapper.rotationCount == 1 && wrapper.doubleRotation
				|| wrapper.rotationCount == 0 && !wrapper.doubleRotation;
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @param wrapper
	 * @param str
	 * @return direct each tree into appropriate rotation method
	 */
	public Node handleRotation(Node parent, Node child, MyWrapper wrapper, String str) {
		if (str.equals("SR")) {
			wrapper.rotationCount++;
			wrapper.setFalse();//stop updating the balance code
			return singleRightRotate(parent, child, false, wrapper);
		} else if (str.equals("RightFirst")) {
			wrapper.rotationCount++;
			wrapper.doubleRotation = true;
			return singleRightRotate(parent, child, true, wrapper);
		} else if (str.equals("SL")) {
			wrapper.rotationCount++;
			wrapper.setFalse();//stop updating the balance code
			return singleLeftRotate(parent, child, false, wrapper);
		} else if (str.equals("LeftFirst")) {
			wrapper.rotationCount++;
			wrapper.doubleRotation = true;
			return singleLeftRotate(parent, child, true, wrapper);
		}
		return parent;
	}

	/**
	 * 
	 * @param parent
	 * @param child
	 * @param isDouble
	 * @param wrapper
	 * @return the root of a (left)rotated tree
	 */
	public Node singleLeftRotate(Node parent, Node child, boolean isDouble, MyWrapper wrapper) {
		// handle single rotation
		if (!isDouble) {
			if (wrapper.gcDirection == ' ') {
				parent.balance = Code.SAME;
				child.balance = Code.SAME;
			} else if (wrapper.gcDirection == 'R') {
				parent.balance = Code.LEFT;
			} else if (wrapper.gcDirection == 'L') {
				parent.balance = Code.SAME;
			}
			child.balance = Code.SAME;
		} else if (isDouble) { // handle double rotation
			if (child.balance == Code.LEFT) {
				wrapper.gcDirection = 'L';
				parent.balance = Code.SAME;
				child.balance = Code.LEFT;
			} else if (child.balance == Code.RIGHT) {
				wrapper.gcDirection = 'R';
				parent.balance = Code.LEFT;
				child.balance = Code.LEFT;
			} else if (child.balance == Code.SAME) {
				// if child is a leaf
				parent.balance = Code.SAME;
				child.balance = Code.LEFT;
			}

		}
		parent.right = child.left;
		child.left = parent;
		if (parent.parent == null) {
			child.parent = null;// update the parent of child after rotation
		} else {
			child.parent = parent.parent;// update the parent of child after rotation
		}
		parent.parent = child;// update the parent after rotation
		child.rank = child.rank + parent.rank + 1;
		return child;
	}

	/**
	 * 
	 * @param parent
	 * @param child
	 * @param isDouble
	 * @param wrapper
	 * @return the root of a (right)rotated tree
	 */
	public Node singleRightRotate(Node parent, Node child, boolean isDouble, MyWrapper wrapper) {
		if (!isDouble) { // handle single rotation
			if (wrapper.specialCase) {
				parent.balance = Code.LEFT;
				child.balance = Code.RIGHT;
				wrapper.setFalse();
			} else if (wrapper.gcDirection == ' ') {
				parent.balance = Code.SAME;
				child.balance = Code.SAME;
			} else if (wrapper.gcDirection == 'R') {
				parent.balance = Code.SAME;
				child.balance = Code.SAME;
			} else if (wrapper.gcDirection == 'L') {
				parent.balance = Code.RIGHT;
				child.balance = Code.SAME;
			}
		} else if (isDouble) { // handle double rotation
			if (child.balance == Code.LEFT) {
				wrapper.gcDirection = 'L';
				parent.balance = Code.RIGHT;
				child.balance = Code.RIGHT;
			} else if (child.balance == Code.RIGHT) {
				wrapper.gcDirection = 'R';
				parent.balance = Code.SAME;
				child.balance = Code.RIGHT;
			} else if (child.balance == Code.SAME) {
				parent.balance = Code.SAME;
				child.balance = Code.RIGHT;
			}
		}
		parent.left = child.right;
		child.right = parent;
		if (parent.parent == null) {
			child.parent = null;// update the parent of child after rotation
		} else {
			child.parent = parent.parent;// update the parent of child after rotation
		}
		parent.parent = child;// update the parent after rotation
		parent.rank = parent.rank - child.rank - 1;
		return child;
	}

	public String updataBalanceForCOn(Node node, MyWrapper w, boolean toTheRight) {
		if (toTheRight) {
			// was balanced
			if (node.balance == Code.SAME) {
				node.balance = Code.RIGHT;
			}
			// was tipped right
			else if (node.right.balance == Code.LEFT && node.balance == Code.RIGHT) {
				return "RightFirst";
			} else if (node.balance == Code.RIGHT) {
				return "SL";
			}
		}
		return "";
	}

	/**
	 * 
	 * @param node
	 * @param wrapper
	 * @param toTheRight
	 * @return a string which suggests which type of rotation should take place
	 */
	public String updateBalance(Node node, MyWrapper w, boolean toTheRight) {
		if (toTheRight) {
			// was balanced
			if (node.balance == Code.SAME) {
				node.balance = Code.RIGHT;
			}
			// was tipped right
			else if (node.balance == Code.RIGHT) {
				if (node.right.balance == Code.RIGHT) {
					return "SL";
				} else if (node.right.balance == Code.LEFT) {
					return "RightFirst";
				}
			}
			// was tipped left
			else if (node.balance == Code.LEFT) {
				node.balance = Code.SAME;
				w.setFalse();
			}
		} else {
			// was balanced
			if (node.balance == Code.SAME) {
				node.balance = Code.LEFT;
			}
			// was tipped right
			else if (node.balance == Code.RIGHT) {
				node.balance = Code.SAME;
				w.setFalse();
			}
			// was tipped left
			else if (node.balance == Code.LEFT) {
				if (node.left.balance == Code.LEFT) {
					return "SR";
				} else if (node.left.balance == Code.RIGHT) {
					return "LeftFirst";
				}
			}
		}
		return "";
	}

	public Node delete(int toBeFound, MyWrapper wrapper) {
		if (this.rank > toBeFound) {// if needs to find the target in the left subtree
			this.rank--;
			this.left = this.left.delete(toBeFound, wrapper);
			// update balance codes and handle rotation
			if (wrapper.neededUpdate() || wrapper.rotationCount > 0) {
				if (!wrapper.specialCase) {
					String str = updateBalanceWhenDeleting(this, wrapper, false);
					if (str.equals("RightFirst")) {
						this.right = handleRotation(this.right, this.right.left, wrapper, str);
						return handleRotation(this, this.right, wrapper, "SL");
					} else {
						return handleRotation(this, this.right, wrapper, str);
					}
				}
			}
		} else if (this.rank < toBeFound) {// if needs to find the target in the right subtree
			this.right = this.right.delete(toBeFound - this.rank - 1, wrapper);
			// update balance codes and handle rotation
			if (wrapper.neededUpdate() || wrapper.rotationCount > 0) {
				if (!wrapper.specialCase) {
					String str = updateBalanceWhenDeleting(this, wrapper, true);
					// new handle rotation
					if (str.equals("LeftFirst")) {
						this.left = handleRotation(this.left, this.left.right, wrapper, str);
						return handleRotation(this, this.left, wrapper, "SR");
					} else {
						return handleRotation(this, this.left, wrapper, str);
					}
				}
			}
		} else {// if is the target
			if (!this.hasLeft() && !this.hasRight()) {
				return NULL_NODE;
			} else if (this.hasLeft() && !this.hasRight()) {
				return this.left;
			} else if (!this.hasLeft() && this.hasRight()) {
				return this.right;
			} else {//if needs to find the successor of the node
				if (!this.right.hasLeft()) {
					this.element = this.right.element;
					if (this.right.right != NULL_NODE) {
						this.right = this.right.right;
						this.right.parent = this;
					} else {
						this.right = NULL_NODE;
					}
				} else {
					this.right = replaceBy(this.right, wrapper);
					this.element = wrapper.replaceBy;
				}
				// update balance codes and handle rotation
				if (wrapper.neededUpdate() || wrapper.rotationCount > 0) {
					if (!wrapper.specialCase) {
						String str = updateBalanceWhenDeleting(this, wrapper, true);
						if (str.equals("LeftFirst")) {
							this.left = handleRotation(this.left, this.left.right, wrapper, str);
							return handleRotation(this, this.left, wrapper, "SR");
						} else {
							return handleRotation(this, this.left, wrapper, str);
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 
	 * @param node
	 * @param wrapper
	 * @return the left bottom most node of the right subtree of the given node
	 */
	public Node replaceBy(Node node, MyWrapper wrapper) {
		if (!node.left.hasLeft()) {// if is the one
			char result = node.left.element;
			node.left = NULL_NODE;
			node.rank--;
			wrapper.replaceBy = result;
			String str = updateBalanceWhenDeleting(node, wrapper, false);
			if (!str.equals("")) {
				if (str.equals("RightFirst")) {
					node.right = handleRotation(node.right, node.right.left, wrapper, str);
					return handleRotation(node, node.right, wrapper, "SL");
				} else {
					return handleRotation(node, node.right, wrapper, str);
				}
			} else {
				return node;
			}
		} else {// if needs to proceed down the tree
			node.rank--;
			node.left = replaceBy(node.left, wrapper);
			if (wrapper.neededUpdate() || wrapper.rotationCount > 0) {
				String str = updateBalanceWhenDeleting(node, wrapper, false);
				handleRotation(node, node.left, wrapper, str);
			}
			return node;
		}
	}

	public String updateBalanceWhenDeleting(Node node, MyWrapper b, boolean isRight) {
		Code originalBalance = node.balance;
		if (isRight) {
			if (originalBalance == Code.SAME) {
				node.balance = Code.LEFT;
				b.setFalse();
			} else if (originalBalance == Code.LEFT) {
				if (node.left.balance == Code.LEFT) {
					return "SR";
				} else if (node.left.balance == Code.SAME) {
					b.specialCase = true;
					return "SR";
				} else if (node.left.balance == Code.RIGHT) {
					return "LeftFirst";
				}
			} else {// tipped right at first and delete the right
				node.balance = Code.SAME;
			}

		} else if (!isRight) {
			if (originalBalance == Code.SAME) {
				node.balance = Code.RIGHT;
				b.setFalse();
			} else if (originalBalance == Code.RIGHT) {
				if (node.right.balance == Code.RIGHT) {
					return "SL";
				} else if (node.right.balance == Code.LEFT) {
					return "RightFirst";

				}
			} else {// tipped left at first and delete the left
				node.balance = Code.SAME;
			}
		}
		return "";
	}

	public int height(int i) {
		int currentHeight = i;
		int leftHeight = 0, rightHeight = 0;
		// This happens when you get to a leaf node.
		if (this == NULL_NODE) {
			return currentHeight - 1;
		}
		// Get height of left sub tree
		if (this.balance == Code.LEFT) {
			return leftHeight = this.left.height(currentHeight + 1);
		}
		return rightHeight = this.right.height(currentHeight + 1);
	}

	public void debugString(StringBuilder sb) {
		if (this == NULL_NODE)
			return;
		sb.append(this.element);
		sb.append(this.rank);
		sb.append(this.balance); // TODO Need to handle balance codes
		sb.append(", ");
		this.left.debugString(sb);
		this.right.debugString(sb);
	}

	public DisplayableNodeWrapper getDisplayableNodePart() {
		return this.dnw;
	}

	public Node getParent() {
		return this.parent;
	}

	public String getRank() {
		return this.rank + "";
	}

	public Object getBalance() {
		return this.balance;
	}

	public char[] getElement() {
		char[] c = new char[1];
		c[0] = this.element;
		return c;
	}

	public Node getRight() {
		return this.right;
	}

	public Node getLeft() {
		return this.left;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public boolean hasRight() {
		return this.right != NULL_NODE;
	}

	public boolean hasLeft() {
		return this.left != NULL_NODE;
	}

	public int slowSize() {
		if (this == NULL_NODE) {
			return 0;
		}
		return 1 + this.left.slowSize() + this.right.slowSize();
	}

	public int slowHeight() {
		return this.height(0);
	}

	public Node copy(Node oldroot, Node parent) {
		if (oldroot == NULL_NODE) {
			return NULL_NODE;
		}
		Node result = new Node(oldroot.element, parent);
		result.balance = oldroot.balance;
		result.rank = oldroot.rank;
		if (oldroot.hasLeft()) {
			result.left = copy(oldroot.left, result);
		}
		if (oldroot.hasRight()) {
			result.right = copy(oldroot.right, result);
		}
		return result;
	}

	public Node buildFromStr(Node root, String s) {
		if (s.isEmpty()) {
			return NULL_NODE;
		} else if (s.length() == 1) {
			return new Node(s.charAt(0), root);
		}
		int mid = s.length() / 2;//divide the current string into two part and build the left and right respectly 
		Node result = new Node(s.charAt(mid), root);
		result.rank = mid - 1;// assign rank
		result.left = buildFromStr(result, s.substring(0, mid));
		result.right = buildFromStr(result, s.substring(mid + 1));
		return result;
	}

	//since we know that rank of node is the size of its left subtree
	//we can use this knowledge to recursively calculate the size of the whole tree
	public int size(Node n) {
		if (n == NULL_NODE) {
			return 0;
		}
		if (n.hasRight()) {
			return n.rank + size(n.right) + 1;
		}
		return n.rank + 1;
	}
	
	/**
	 * 
	 * @param s
	 * @param pos
	 * @param node
	 * @return a stack that contains all the nodes which are in the path of finding the target node
	 */
	public Stack<Node> fillStack(Stack<Node> s, int pos, Node node) {
		if (node.rank > pos) {
			s.push(node);
			return fillStack(s, pos, node.left);
		} else if (node.rank < pos) {
			s.push(node);
			return fillStack(s, pos - node.rank - 1, node.right);
		} else {
			s.push(node);
			return s;
		}
	}

	public Node concatenate(Node node, EditTree other, int height, MyWrapper wrap) {
		if (wrap.direction) {//if we concatenate the other tree to the right the this tree
			if (height == other.height()) {
				char q = other.delete(0);//we delete the left most element first and get the element
				Node nodeQ = new Node(q, node.parent);//create a new node Q
				nodeQ.rank = size(node);
				nodeQ.left = node;
				nodeQ.right = other.getRoot();
				node.parent = nodeQ;
				other.getRoot().parent = nodeQ;
				wrap.neededUpdate = true;
				if (nodeQ.left.height(0) > nodeQ.right.height(0)) {//update the balance Code of Q
					nodeQ.balance = Code.LEFT;
				} else if (nodeQ.left.height(0) < nodeQ.right.height(0)) {
					nodeQ.balance = Code.RIGHT;
				} else if (nodeQ.left.height(0) == nodeQ.right.height(0)) {
					nodeQ.balance = Code.SAME;
				}
				return nodeQ;
			}//handle rotation after we concatenate 
			if (node.balance == Code.LEFT) {
				node.right = node.right.concatenate(node.right, other, height - 2, wrap);
			} else if (node.balance == Code.RIGHT || node.balance == Code.SAME) {
				node.right = node.right.concatenate(node.right, other, height - 1, wrap);
				if (wrap.neededUpdate()) {
					String str = updataBalanceForCOn(node, wrap, true);
					if (neededRotation(wrap)) {
						if (str.equals("RightFirst")) {
							node.right = handleRotation(node.right, node.right.left, wrap, str);
							Node result = handleRotation(node, node.right, wrap, "SL");
							return result;
						} else {
							Node result = handleRotation(node, node.right, wrap, str);
							return result;
						}
					}
				}
			}
		} else if (!wrap.direction) {//if we concatenate the other tree to the left the this tree
			if (height == other.height()) {
				char q = other.delete(other.size() - 1);//we delete the right most element of the other tree
				Node nodeQ = new Node(q, node.parent);//create a new node Q
				nodeQ.rank = size(other.getRoot());
				nodeQ.right = node;
				nodeQ.left = other.getRoot();
				node.parent = nodeQ;
				// need update balance
				other.getRoot().parent = nodeQ;
				return nodeQ;
			}
			if (node.balance == Code.RIGHT) {//update the balance Code of Q
				node.left = node.left.concatenate(node.left, other, height - 2, wrap);
			} else if (node.balance == Code.LEFT || node.balance == Code.SAME) {
				node.left = node.left.concatenate(node.left, other, height - 1, wrap);
			}
		}
		return node;
	}
	
	//similar to size() in that we use the rank to determine the position in the tree to get
	public char get(Node n, int pos) {
		if (n.rank > pos) {
			return get(n.left, pos);
		} else if (n.rank < pos) {
			return get(n.right, pos - n.rank - 1);
		} else {
			return n.element;
		}

	}
}