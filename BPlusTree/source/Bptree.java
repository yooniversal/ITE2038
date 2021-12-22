import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Tree {
	int m;		// # of keys
	Node root;	// root node
	
	Tree(int m) {
		this.m = m;
		root = new Node(true, m, 0, null, null, null, null);
	}
	
	/*
	 * �˻�
	 * mode == true  : single key search
	 * mode == false : range search
	 * 
	 * ��ܸ� ��� : ���� key�� ��� ���
	 * �ܸ� ��� : ���� ã�Ҵٸ� value��, �ƴϸ� "NOT FOUND" ���
	 */
	void search(boolean mode, long key, long key2, Node cur) {
		
		// single key search
		if(mode) {
			
			// �ܸ� ���
			if(cur.isLeaf) {
				
				// �ܸ� ��忡 key ���� �����ϴ��� Ȯ��
				for(int i=0; i<cur.size_(); i++) {
					Pair_ pair = cur.getPair_(i);
					if(pair.key == key) {
						System.out.println(pair.valuePointer.node.value);
						return;
					}
				}
				
				// "NOT FOUND"
				System.out.println("NOT FOUND");
				return;
			}
			// ��ܸ� ���
			else {
				
				// ��� ���� �����͸� �����ϸ鼭 ���ǿ� ���� ������ ����
				// ���İ��� ��忡 ���� ��� key�� print
				boolean alreadySearched = false;
				int nextSearchIndex = -1;
				for(int i=0; i<=cur.size(); i++) {
					
					if(i == cur.size()) {
						
						if(!alreadySearched) nextSearchIndex = i;
						continue;
					}
					
					// ���� ��忡 ���� ��� key ���
					Pair pair = cur.getPair(i);
					if(i < cur.size()-1) System.out.print(pair.key + ",");
					else System.out.println(pair.key);
					
					if(key < cur.getPair(i).key) {
						
						if(!alreadySearched) nextSearchIndex = i;
						alreadySearched = true;
					}
				}
				
				// �ܸ� ���� �������鼭 Ž��
				if(nextSearchIndex == cur.size()) search(mode, key, key2, cur.rightChild);
				else search(mode, key, key2, cur.getPair(nextSearchIndex).left_child_node);
			}
			
		}
		// range search
		else {
			
			// �ܸ� ���
			if(cur.isLeaf) {
				
				Node current = cur;
				boolean start = false;
				
				while(true) {
					
					// ���� ��忡�� [key, key2]�� ���ϴ� �� ���
					for(Pair_ pair : current.p_) {
						
						if(key <= pair.key && pair.key <= key2) {
							start = true;
							System.out.println(pair.key + "," + pair.valuePointer.node.value);
						}
						
						// Ž�� ����
						if(pair.key >= key2) return;
					}
					
					// Ž�� ����
					if(current.rightSibling == null) return;
					
					current = current.rightSibling;
				}
			}
			// ��ܸ� ���
			else {
				
				// ��� ���� �����͸� �����ϸ鼭 ���ǿ� ���� ������ ����
				// ���ǿ� ������ �ܸ���忡 ������ ������ ���
				for(int i=0; i<=cur.size(); i++) {
					
					if(i == cur.size()) {
						search(mode, key, key2, cur.rightChild);
						return;
					}
					
					if(key < cur.getPair(i).key) {
						Pair pair = cur.getPair(i);
						search(mode, key, key2, pair.left_child_node);
						return;
					}
					
				}
				
				// Ž�� ����
				return;
			}
			
		}
	}
	
	/* 
	 * ����
	 */
	SplitChildren insert(Node parent, Node cur, Pair_ pair, int childIndex, int depth) {
		
		// �ܸ� ���
		if(cur.isLeaf) {
			
			// root : key�� ��� �� ����
			if(cur.size_() == 0) {
				cur.insertPair_(pair);
				return new SplitChildren(-1, null, null);
			}
			
			for(int i=0; i<=cur.size_(); i++) {
				
				long key = -1;
				
				// ������ ��ġ�� �ƴ϶��
				if(i != cur.size_()) {
					
					key = cur.getPair_(i).key;
					
					// ���� key���� ������ ��� ����ó��
					if(pair.key == key) 
						return new SplitChildren(-1, null, null);
				}
				
				// �������� key���� Ž���� key������ �۰ų�
				// ������ �ڸ��� ���������� ��ã�� ���
				if(i == cur.size_() || (i != cur.size_() && pair.key < key)) {
					
					cur.insertPair_(i, pair);
					
					// overflow �߻� ��				
					if(cur.size_() >= m) {
						
						// ���� Sibling�� ������ ���� �ִٸ� key pair ������
						if(cur.leftSiblingHasSpace(true, new Node())) {
							
							cur.giveToLeft(true, true, false, cur.leftSibling);

							// �θ� key ������Ʈ
							if(childIndex > 0) 
								cur.updateKey(childIndex-1, cur.parent, true);
						}
						// ������ Sibling�� ������ ���� �ִٸ� key pair ������
						else if(cur.rightSiblingHasSpace(true, new Node())) {
						
							cur.giveToRight(true, true, false, cur.rightSibling);
							
							// �θ� key ������Ʈ
							if(childIndex < cur.parent.size()) 
								cur.updateKey(childIndex, cur.parent, true);
						}
						// ���� Sibling�� ������ ���ٸ� split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);
							
							// ���� �� �� ��带 ���� ����
							Node right = splitedChildren.right;
							cur.rightSibling = right;
							right.leftSibling = cur;
							
							return splitedChildren;
						}
					}
					
					return new SplitChildren(-1, null, null);
				}
			}
			
			return new SplitChildren(-1, null, null);
		}
		
		// ��ܸ� ���
		for(int i=0; i<=cur.size(); ++i) {
			
			// ������ �ڸ��� ���������� ��ã�� ���
			if(i == cur.size()) {
				
				// �ܸ� ��忡 �����ؼ� ������ ������ ��� ������
				// ��ȯ�� != null : child���� overflow �߻� -> �θ�� �߰��� �̵�
				SplitChildren children = insert(cur, cur.rightChild, pair, i, depth+1);
				
				// �ڽĿ��� overflow�� �߰��� ó��
				if(children.left != null) {
					
					// ���� ��忡 ���� �� �ڽİ� ����
					cur.insertPair(new Pair(children.addKey, children.left));
					cur.rightChild = children.right;
					children.left.parent = cur;
					children.right.parent = cur;
					
					// overflow �߻� ��
					if(cur.size() >= m) {
						
						Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
						Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
						
						// ���� Sibling�� ���� ������ �ִٸ� key pair ������
						if(cur.leftSiblingHasSpace(false, leftSibling)) {
							cur.giveToLeft(false, true, false, leftSibling);
						}
						// ������ Sibling�� ���� ������ �ִٸ� key pair ������
						else if(cur.rightSiblingHasSpace(false, rightSibling)) {
							cur.giveToRight(false, true, false, rightSibling);
						}
						// ���� Sibling�� ������ ���ٸ� split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);				
							
							// ���� ��尡 split �ȴٸ� �̹� �и��� 2���� �ڽ� ���� �θ��� ������ ���� ���εž� ��
							children.left.parent = splitedChildren.right;
							children.right.parent = splitedChildren.right;
							
							return splitedChildren;
						}
						
						return new SplitChildren(-1, null, null);
					}
				}
				
				// key �� ������Ʈ
				if(children.left != null) cur.updateKey(i, cur, true);
				else cur.updateKey(i-1, cur, true);
				
				return new SplitChildren(-1, null, null);
			}
			
			long key = cur.getPair(i).key;
			
			// ������ key�� < �̹� �����ϴ� key��
			if(pair.key < key) {
				
				Pair currentPair = cur.getPair(i);
				
				// �ܸ� ��忡 �����ؼ� ������ ������ ��� ������
				// ��ȯ�� != null : child���� overflow �߻� -> �θ�� �߰��� �̵�
				SplitChildren children = insert(cur, currentPair.left_child_node, pair, i, depth+1);
				
				// �ڽĿ��� overflow�� �߰��� ó��
				if(children.left != null) {
					
					// ���� ��忡 ���� �� �ڽİ� ����
					cur.insertPair(i, new Pair(children.addKey, children.left));
					cur.setChild(i+1, children.right);
					children.left.parent = cur;
					children.right.parent = cur;
					
					// overflow �߻� ��
					if(cur.size() >= m) {
						
						Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
						Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
						
						// ���� Sibling�� ���� ������ �ִٸ� key pair ������
						if(cur.leftSiblingHasSpace(false, leftSibling)) {
							cur.giveToLeft(false, true, false, leftSibling);
						}
						// ������ Sibling�� ���� ������ �ִٸ� key pair ������
						else if(cur.rightSiblingHasSpace(false, rightSibling)) {
							cur.giveToRight(false, true, false, rightSibling);
						}
						// ���� Sibling�� ������ ���ٸ� split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);
							return splitedChildren;
						}
						
						return new SplitChildren(-1, null, null);
					}
				}
				
				// key �� ������Ʈ
				if(i > 0) cur.updateKey(i-1, cur, true); 
				return new SplitChildren(-1, null, null);
			}
			
		}
		
		return new SplitChildren(-1, null, null);
	}
	
	/*
	 * ����
	 * true  : �ڽĳ�尡 ������ (������ ��ġ �ʿ�)
	 * false : �ڽĳ�尡 �������� �ʾ���
	 * 
	 * main���� true�� ��ȯ�ϰ� root�� �ܸ� �����
	 * Ʈ���� ��� ��尡 ���� ���̹Ƿ� root = null ����
	 */
	boolean delete(Node cur, long target, int depth) {
		
		// �ܸ� ���
		if(cur.isLeaf) {

			for(int i=0; i<cur.size_(); i++) {
				
				// ������ key ã��
				if(cur.getPair_(i).key == target) {
					
					cur.p_.remove(i); // �ش� key ����
					
					// ���� ��尡 root�� �ƴ� ��
					if(depth != 0) {
						
						if(cur.size_() < Math.ceil(m/2.0)-1) {
							
							Node left = cur.leftSibling;
							Node right = cur.rightSibling;
							
							// ���ʿ� ���� ������ ������
							if(cur.leftHasEnough(left)) {
								
								Pair_ pair = left.p_.remove(left.size_()-1);
								cur.insertPair_(0, pair);
								return false;
							}
							// �����ʿ� ���� ������ ������
							else if(cur.rightHasEnough(right)) {
								
								Pair_ pair = right.p_.remove(0);
								cur.insertPair_(pair);
								return false;
							}
							// merge
							else {
								
								// left merge
								if(left != null) {
									
									for(Pair_ pair : cur.p_)
										left.insertPair_(pair);
  								}
								// right merge
								else {
									
									for(int j=cur.size_()-1; j>=0; j--) {
										
										Pair_ pair = cur.getPair_(j);
										right.insertPair_(0, pair);
									}
								}
								
								// �Ҹ� �� ���� �ܸ� ��� ����
								cur.connectFromDeletedNode();
							}
							
							return true;
						}
					}
					// ���� ��尡 root
					else {
						
						if(cur.size_() < 1)
							return true;
					}
					
					return false;
				}
			}
			
			return false;
		}
		
		// ��ܸ� ���
		for(int i=0; i<=cur.size(); i++) {
			
			// ������ key���� ũ�ų� ���� ��
			if(i == cur.size()) {
				
				if(cur.getPair(i-1).key <= target) {
					
					// �ڽ��� �������� �� ���� ����� key ������ ���� ���̽� �з�
					boolean deleted = delete(cur.rightChild, target, depth+1);
					if(deleted) {
						
						// root�� �ڽ��� merge�Ǹ� �ڽĳ�带 root�� ����
						if(depth == 0 && cur.size() <= 1) {
							
							// ��Ʈ�� ������ �ڽ� ��尡 ���ٸ�
							if(cur.rightChild == null) {
								Node left = cur.getPair(0).left_child_node;
								
								// ���� ���� �Ҹ�ǹǷ� �պ��� ���� �ڽ��� root�� ����
								root = left;
								root.setParent(null);
								root.setChildIndex(0);
								
								return true;	
							}
							
							// ��Ʈ�� �ڽ� ��尡 �ܸ� ���
							if(cur.rightChild.isLeaf) {
								
								Node left = cur.getPair(0).left_child_node;
								
								// ���� ���� �Ҹ�ǹǷ� �պ��� ���� �ڽ��� root�� ����
								root = left;
								root.setParent(null);
								root.setChildIndex(0);
							}

							return true;
						}
						
						// ���� ����� key ������ �ּ� ������ ��
						if(depth > 0 && cur.size() <= Math.ceil(m/2.0)-1) {
							
							Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
							
							// ���� Sibling�� �����ϸ�
							if(leftSibling.m != -1) {
								
								// ���� Sibling���� ���� ���� ������ -> borrow
								// borrow �߻� �� �� �̻� �θ���� ���� ��ȭ�� ���� ���� ����
								if(cur.leftHasEnough(leftSibling)) {
									
									leftSibling.giveToRight(false, false, true, cur);
									return false;
								}
								// ���� Sibling�� key pair�� �ּ� ������� -> merge
								else {
									
									cur.leftMerge(leftSibling, i);
								}
									
								return true;
							}
							// ������ Sibling���� �̵�
							else {
								
								Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
								
								if(rightSibling != null) {
								
									// ������ Sibling���� ���� ���� ������ -> borrow
									// borrow �߻� �� �� �̻� �θ���� ���� ��ȭ�� ���� ���� ����
									if(cur.rightHasEnough(rightSibling)) {
										
										rightSibling.giveToLeft(false, false, true, cur);
										return false;
									}
									// ������ Sibling�� key pair�� �ּ� ������� -> merge
									else {
										
										cur.rightMerge(rightSibling, i);
										
										// �̵��� key pair�� �θ� key pair ������Ʈ
										if(rightSibling.parent != null && rightSibling.childIndex > 0)
											rightSibling.updateKey(rightSibling.childIndex-1, rightSibling.parent, true);
									}
									
									return true;
								}
							}
						}
						
						// ���� ��� ���� �Ŀ��� ������ �����Ǵ� ���
						// �ּ� �������� �����Ƿ� ����
						// ���� ������ �ڽ� �缳�� �� ����
						cur.rightChild = cur.getPair(i-1).left_child_node;
						cur.p.remove(i-1);
						
						// key ������Ʈ
						if(i > 0) cur.updateKey(cur.size()-1, cur, true);
						
						return false;
					}
					
					// key ������Ʈ
					if(i > 0) cur.updateKey(i-1, cur, true);
					
					return false;
				}
			}
			// [0, size()-1]
			else {

				if(target < cur.getPair(i).key) {
					
					// �ڽ��� �������� �� ���� ����� key ������ ���� ���̽� �з�
					boolean deleted = delete(cur.getPair(i).left_child_node, target, depth+1);
					if(deleted) {
						
						// root�� �ڽ��� merge�Ǹ� �ڽĳ�带 root�� ����
						if(depth == 0 && cur.size() <= 1) {
							
							// root�� ���� �ڽ��� �Ҹ����� ��
							if(cur.getPair(0).left_child_node == null) {
								
								Node right = cur.rightChild;
								
								// ���� ���� �Ҹ�ǹǷ� �պ��� ������ �ڽ��� root�� ����
								root = right;
								root.setParent(null);
								root.setChildIndex(0);
								
								return true;
							}
							
							// ��Ʈ�� �ڽ� ��尡 �ܸ� ���
							
							if(cur.getPair(0).left_child_node.isLeaf) {
								
								Node right = cur.rightChild;
								
								// ���� ���� �Ҹ�ǹǷ� ������ �ڽ��� root�� ����
								root = right;
								root.setParent(null);
								root.setChildIndex(0);
							}
							
							return true;
						}
						
						if(depth > 0 && cur.size() <= Math.ceil(m/2.0)-1) {
							
							Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
							
							// ���� Sibling�� �����ϸ�
							if(leftSibling.m != -1) {
								
								// ���� Sibling���� ���� ���� ������ -> borrow
								// borrow �߻� �� �� �̻� �θ���� ���� ��ȭ�� ���� ���� ����
								if(cur.leftHasEnough(leftSibling)) {
									
									cur.p.remove(i);
									leftSibling.giveToRight(false, false, false, cur);
									return false;
								}
								// ���� Sibling�� key pair�� �ּ� ������� -> merge
								else {
		
									cur.leftMerge(leftSibling, i);
								}
								
								return true;
							}
							// ������ Sibling���� �̵�
							else {
								
								Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
								
								if(rightSibling != null) {
									
									// ������ Sibling���� ���� ���� ������ -> borrow
									// borrow �߻� �� �� �̻� �θ���� ���� ��ȭ�� ���� ���� ����
									if(cur.rightHasEnough(rightSibling)) {
										
										// ������ ���� �ڽ� ��ġ �� �ڽ� ��ȣ �缳��
										cur.p.remove(i);
										for(int j=0; j<=cur.size(); ++j)
											if(j == cur.size()) cur.rightChild.setChildIndex(j);
											else cur.getPair(j).left_child_node.setChildIndex(j);
										
										rightSibling.giveToLeft(false, false, false, cur);
										
										return false;
									}
									// ������ Sibling�� key pair�� �ּ� ������� -> merge
									else {
										
										cur.rightMerge(rightSibling, i);
										
										// ������ Sibling�� �θ� key pair ������Ʈ
										if(rightSibling.parent != null && rightSibling.childIndex > 0)
											rightSibling.updateKey(rightSibling.childIndex-1, rightSibling.parent, true);
									}
									
									return true;
								}
							}
						}
						
						// ���� ��� ���� �Ŀ��� ������ �����Ǵ� ���
						// �ּ� �������� �����Ƿ� ����
						// key pair ������Ʈ
						cur.p.remove(i);
						if(i > 0) cur.updateKey(i, cur, true);
						
						return false;
					}
					
					// key ������Ʈ
					cur.updateKey(i, cur, true);
					return false;
				}
			}
		}
		
		return false;
	}

	void writeIndexFile(BufferedWriter bw) throws IOException {
		
		// b ���� ���
		bw.write("b " + root.m + "\r\n");
		bw.flush();
		
		// ��ϵ� ���� ����
		if(root.isLeaf && root.size_() == 0) return;
		if(!root.isLeaf && root.size() == 0) return;
		
		// breadth-first search
		// ��带 ���İ��鼭 key pairs �ٷ� ���
		// ���� ��尡 �ܸ� ��尡 �ƴ϶�� queue�� �ڽ��� push
		Queue<Node> q = new LinkedList<>();
		root.setChildIndex(-1);
		q.offer(root);
		
		while(q.size() > 0) {
			
			Node curNode = q.poll();
			
			// �ܸ� ���
			if(curNode.isLeaf) {
				
				bw.write("# ");
				for(int i=0; i<curNode.size_(); i++) {
					
					Pair_ pair = curNode.getPair_(i);
					
					// Ʈ�� ���� �ۼ�
					bw.write(pair.key + " " + pair.valuePointer.node.value + " ");
				}
			}
			// ��ܸ� ���
			else {
				
				bw.write("/ ");
				for(int i=0; i<curNode.size(); i++) {
					
					Pair pair = curNode.getPair(i);
					
					// Ʈ�� ���� �ۼ�
					bw.write(pair.key + " ");
					
					// queue�� �ڽ� ��� ����
					q.offer(pair.left_child_node);
				}
				
				// queue�� ������ �ڽ� ��� ����
				// depth�� ����� ���� �� ����
				if(curNode.childIndex == -1) 
					curNode.rightChild.setChildIndex(-1);
				q.offer(curNode.rightChild);
			}
			
			// depth���� ���
			if(curNode.childIndex == -1) {
				
				bw.write("\r\n");
				bw.flush();
			}
		}
	}
	
}

class SplitChildren {
	long addKey;
	Node left, right;
	
	SplitChildren(long addKey, Node left, Node right) {
		this.addKey = addKey;
		this.left = left;
		this.right = right;
	}
}

class Pair {
	long key;
	Node left_child_node;
	
	Pair(long key, Node left_child_node) {
		this.key = key;
		this.left_child_node = left_child_node;
	}
}

class Pair_ {
	long key;
	ValuePointer valuePointer;
	
	Pair_(long key, ValuePointer valuePointer) {
		this.key = key;
		this.valuePointer = valuePointer;
	}
}

class ValuePointer {
	ValueNode node;
	
	ValuePointer (long value) {
		ValueNode newNode = new ValueNode(value);
		node = newNode;
	}
}

class ValueNode {
	long value;
	
	ValueNode(long value) {
		this.value = value;
	}
}

class Node {
	boolean isLeaf; 	 // true : �ܸ� ��� false : ��ܸ� ���
	int m;	  			 // # of keys
	int childIndex; 	 // (��ܸ� ����) �θ� ����� childIndex��° �ڽ�
	ArrayList<Pair> p;	 // (�ܸ� ����) an array of<key, left_child_node> pairs
	ArrayList<Pair_> p_; // (��ܸ� ����) an array of<key, value(or pointer to the value)> pairs
	Node parent;		 // ���� �θ� ���
	Node rightChild;	 // (��ܸ� ����) ���� ����� ���� ������ child
	Node leftSibling;	 // (�ܸ� ����) ���� sibling node
	Node rightSibling;	 // (�ܸ� ����) ������ sibling node
	
	Node() {
		this(false, -1, 0, null, null, null, null);
	}
	
	Node(boolean isLeaf, int m, int childIndex, Node parent, Node rightChild, Node leftSibling, Node rightSibling) {
		this.isLeaf = isLeaf;
		this.m = m;
		this.childIndex = childIndex;
		p = new ArrayList<Pair>();
		p_ = new ArrayList<Pair_>();
		this.parent = parent;
		this.rightChild = rightChild;
		this.leftSibling = leftSibling;
		this.rightSibling = rightSibling;
	}
	
	void insertPair(Pair pair) {
		p.add(pair);
	}
	
	void insertPair(int index, Pair pair) {
		p.add(index, pair);
	}
	
	void insertPair_(Pair_ pair) {
		p_.add(pair);
	}
	
	void insertPair_(int index, Pair_ pair) {
		p_.add(index, pair);
	}
	
	Pair getPair(int index) {
		return p.get(index);
	}
	
	Pair_ getPair_(int index) {
		return p_.get(index);
	}
	
	void setChild(int index, Node Child) {
		Pair pair = p.get(index);
		pair.left_child_node = Child;
		p.set(index, pair);
	}
	
	void setParent(Node parent) {
		this.parent = parent;
	}
	
	void setChildIndex(int childIndex) {
		this.childIndex = childIndex;
	}
	
	// ��ܸ� ����� key pair ����
	int size() { 
		return p.size();
	}
	
	// �ܸ� ����� key pair ����
	int size_() {
		return p_.size();
	}
	
	// overflow �� ���� ��带 �� ���� ����
	// ���ҵǴ� ���� ���� �߰����� ��ȯ
	SplitChildren split(int dividedIndex) {
		
		// �ܸ� ���
		if(isLeaf) {

			Node rightNode = new Node(true, m, 0, null, null, this, rightSibling);
			
			// �и��� ���� �̿� ��� ���� �缳��
			if(rightSibling != null) {
				rightSibling.leftSibling = rightNode;
				rightSibling = rightNode;
			}
			
			// ���ҵǴ� ��忡 <key, value> ����
			for(int i=dividedIndex; i<p_.size(); i++)
				rightNode.insertPair_(p_.get(i));
			
			// ����� <key, value> ����
			while(p_.size() > dividedIndex) 
				p_.remove(dividedIndex);
			
			return new SplitChildren(rightNode.getPair_(0).key, this, rightNode);
		}
		
		// ��ܸ� ���
		Node rightNode = new Node(false, m, childIndex+1, parent, rightChild, null, null);
		
		// ���ҵǴ� ��忡 <key, left_child_node> ����
		// ���� �ڽ� ��尡 ��ܸ� ����� �ڽ� ��ȣ �缳��
		int cnt = 0;
		for(int i=dividedIndex; i<p.size(); i++) {
			if(!rightChild.isLeaf && i>dividedIndex) p.get(i).left_child_node.childIndex = cnt++;
			rightNode.insertPair(p.get(i));
		}
		if(!rightChild.isLeaf) rightChild.childIndex = cnt;
		
		// ���� ���(left)�� rightChild �缳��
		rightChild = p.get(dividedIndex).left_child_node;
		
		// ����� <key, left_child_node> ����
		while(p.size() > dividedIndex)
			p.remove(dividedIndex);
		
		// �߰� key pair�� �θ�� �̵�
		// ��ܸ� ��忡���� �߰� key pair�� ������
		Pair pair = rightNode.getPair(0);
		rightNode.p.remove(0);
		
		// �ڽ�-�θ� ����
		for(int i=0; i<=p.size(); i++) {
			if(i == p.size()) rightChild.parent = this;
			else p.get(i).left_child_node.parent = this;
		}
		
		for(int i=0; i<=rightNode.size(); i++) {
			if(i == rightNode.size()) rightNode.rightChild.parent = rightNode;
			else rightNode.getPair(i).left_child_node.parent = rightNode;
		}
		
		return new SplitChildren(pair.key, this, rightNode);
	}
	
	// ���� Sibling ����
	// turn      : ��ǥ ��尡 ���� subtree ���� ����
	// cur       : ���� ���
	// curDepth  : ���� ����� depth
	// goalDepth : ��ǥ ����� depth
	Node searchLeftSibling(boolean turn, Node cur, int curDepth, int goalDepth) {

		// ��ǥ ��尡 ���� subtree�� �������� ���ߴٸ�
		if(!turn) {
			
			// root�� ���ϸ鼭 ���� subtree�� �ִ��� Ȯ��
			if(curDepth > 0) {
				if(cur.childIndex == 0) 
					return searchLeftSibling(false, cur.parent, curDepth-1, goalDepth);
				
				// ���� subtree �߰�
				Node left = cur.parent.getPair(cur.childIndex-1).left_child_node;
				return searchLeftSibling(true, left, curDepth, goalDepth);
			}
			
			// ���� Sibling ����
			return new Node();
		}
		
		// ��ǥ ��尡 ���� subtree�� �����ߴٸ� -> �� �ݵ�� ����
		// Ž�� ����
		if(curDepth == goalDepth) return cur;
				
		// depth�� ������ ������ Ž��
		return searchLeftSibling(true, cur.rightChild, curDepth+1, goalDepth);
	}
	
	// ������ Sibling ����
	// turn      : ��ǥ ��尡 ���� subtree ���� ����
	// cur       : ���� ���
	// curDepth  : ���� ����� depth
	// goalDepth : ��ǥ ����� depth
	Node searchRightSibling(boolean turn, Node cur, int curDepth, int goalDepth) {
		
		// ��ǥ ��尡 ���� subtree�� �������� ���ߴٸ�
		if(!turn) {
			
			// root�� ���ϸ鼭 ������ subtree�� �ִ��� Ȯ��
			if(curDepth > 0) {
				
				// ���� ��尡 root
				if(cur.parent == null) return new Node();
				
				// ���� ��尡 root�� �ƴ϶��
				if(cur.childIndex == cur.parent.size()) 
					return searchRightSibling(false, cur.parent, curDepth-1, goalDepth);
				
				// ������ subtree �߰�
				Node right = cur.childIndex == cur.parent.size()-1 ?
						parent.rightChild : parent.getPair(cur.childIndex+1).left_child_node;
				return searchRightSibling(true, right, curDepth, goalDepth);
			}
			
			// ������ Sibling ����
			return new Node();
		}
		
		// ��ǥ ��尡 ���� subtree�� �����ߴٸ� -> �� �ݵ�� ����
		// Ž�� ����
		if(curDepth == goalDepth) return cur;
				
		// depth�� ������ ������ Ž��
		return searchLeftSibling(true, cur.getPair(0).left_child_node, curDepth+1, goalDepth);
	}
	
	// ���� Sibling�� ������ �ִ��� üũ
	boolean leftSiblingHasSpace(boolean isLeaf, Node leftSibling) {
		if(isLeaf) {
			if(this.leftSibling == null) return false; // �� ���� LeafNode
			return this.leftSibling.size_() < m-1;
		}
		
		// ���� Sibling ����x
		if(leftSibling.m == -1) return false;
		
		return leftSibling.size() < m-1;
	}
	
	// ������ Sibling�� ������ �ִ��� üũ
	boolean rightSiblingHasSpace(boolean isLeaf, Node rightSibling) {
		if(isLeaf) {
			if(this.rightSibling == null) return false; // �� ������ LeafNode
			return this.rightSibling.size_() < m-1;
		}
		
		// ������ Sibling ����X
		if(rightSibling.m == -1) return false;
		
		return rightSibling.size() < m-1;
	}
	
	// ���� �տ� �ִ� key pair�� ���� Sibling�� ����
	void giveToLeft(boolean isLeaf, boolean isInsert, boolean isEnd, Node leftSibling) {
		if(isLeaf) {
			
			Pair_ pair = new Pair_(p_.get(0).key, p_.get(0).valuePointer);
			p_.remove(0); // ���� ��忡�� �̵��Ϸ��� pair ����
			
			// ���� Sibling�� �� ���� key�� ����
			leftSibling.insertPair_(pair);
			
			return;
		}
		
		Pair pair = new Pair(p.get(0).key, p.get(0).left_child_node);
		p.remove(0); // ���� ��忡�� �̵��Ϸ��� pair ����
		
		// �ڽ� �缳��
		Node tmp = leftSibling.rightChild;
		leftSibling.rightChild = pair.left_child_node;
		pair.left_child_node = tmp;
		
		// �������� pair �ѱ��
		leftSibling.insertPair(pair);
		
		// �ű� pair�� �ڽ� �θ� ����
		leftSibling.getPair(leftSibling.size()-1).left_child_node.setParent(leftSibling);
		
		// delete�ε� ������ �ڽ��� ������ �ڽ��̶��
		if(!isInsert && isEnd) {
			
			Pair lastPair = leftSibling.p.remove(leftSibling.size()-1);
			leftSibling.rightChild = lastPair.left_child_node;
			updateKey(leftSibling.size()-1, leftSibling, true);
		}
		
		// ���� ����� �ڽ� ��ȣ �缳��
		for(int i=0; i<=p.size(); i++) {
			if(i == p.size()) rightChild.setChildIndex(i);
			else p.get(i).left_child_node.setChildIndex(i);
		}
		
		// ���� Sibling�� �ڽ� ��ȣ �缳��
		for(int i=0; i<=leftSibling.size(); i++) {
			if(i == leftSibling.size()) leftSibling.rightChild.setChildIndex(i);
			else leftSibling.getPair(i).left_child_node.setChildIndex(i);
		}
		
		// ���� Sibling�� �� �� key pair�� �θ� key pair �缳��
		updateKey(leftSibling.size()-1, leftSibling, true);
		if(childIndex > 0) updateKey(childIndex-1, parent, true);
	}
	
	// ���� �ڿ� �ִ� key pair�� ������ Sibling�� ����
	void giveToRight(boolean isLeaf, boolean isInsert, boolean isEnd, Node rightSibling) {
		if(isLeaf) {
			
			Pair_ pair = new Pair_(p_.get(p_.size()-1).key, p_.get(p_.size()-1).valuePointer);
			p_.remove(p_.size()-1); // ���� ��忡�� �̵��Ϸ��� pair ����
			
			// ������ Sibling�� �� ���� key�� ����
			rightSibling.insertPair_(0, pair);
			
			return;
		}
		
		Pair pair = p.get(p.size()-1);
		p.remove(p.size()-1); // ���� ��忡�� �̵��Ϸ��� pair ����
		
		// �ڽ� �缳��
		Node tmp = rightChild;
		rightChild = pair.left_child_node;
		pair.left_child_node = tmp;

		// ���������� pair �ѱ��
		rightSibling.insertPair(0, pair);
		
		// ������ Sibling�� ù ��° �ڽ� �θ� ����
		rightSibling.getPair(0).left_child_node.setParent(rightSibling);
		
		// (delete ��) ������ Sibling�� ����� �ڽ� rightmost ���ο� ���� ���̽� �з�
		if(!isInsert && isEnd) {
			
			pair = rightSibling.p.remove(rightSibling.size()-1);
			rightSibling.rightChild = pair.left_child_node;
			updateKey(0, rightSibling, true);
		}
		
		// ������ Sibling�� �ڽ� ��ȣ �缳��
		// ��� �ڽ� ��ȣ�� 1�� �ð� �ǹǷ� �缳��
		for(int i=0; i<=rightSibling.size(); i++) {
			if(i == rightSibling.size()) rightSibling.rightChild.setChildIndex(i);
			else rightSibling.getPair(i).left_child_node.setChildIndex(i);
		}
		
		// ������ Sibling�� �� �� key pair�� �θ� key pair �缳��
		updateKey(0, rightSibling, true);
		if(childIndex > 0) updateKey(childIndex-1, parent, true);
	}
		
	// (��ܸ� ����) index��° Pair�� key�� ������ ����Ʈ���� ���� ���� ������ ����
	// start : ������Ʈ �� ��忡���� true, �׷��� ������ false
	// ���ϰ� : �ܸ� ����� key��
	long updateKey(int index, Node cur, boolean start) {
		
		// �ܸ� ���
		if(cur.isLeaf) return cur.getPair_(0).key;
		
		// ��ܸ� ���
		long key;
		
		// update�� ���� ������ subtree�� �̵��ؾ� ��
		if(start) {
			
			// ������ pair
			if(index == cur.size()-1) 
				key = updateKey(0, cur.rightChild, false);
			// ������ pair�� �ƴϸ�
			else 
				key = updateKey(0, cur.getPair(index+1).left_child_node, false);
			
			// ���� �Ϸ�
			Pair pair = cur.getPair(index);
			pair.key = key;
			cur.p.set(index, pair);
			
			return key;
		}
		
		// update�� ��带 �����ٸ� ���� ���� ������ �̵�
		Pair pair = cur.getPair(0);
		return updateKey(0, pair.left_child_node, false);
	}
	
	// (��ܸ� ����) ���� Sibling�� borrow �ϱ⿡ ����� key ������ ���� �ִ��� Ȯ��
	boolean leftHasEnough(Node leftSibling) {

		// ���� Sibling ����x
		if(leftSibling == null) return false;
		if(leftSibling.m == -1) return false;
		
		if(leftSibling.isLeaf) return leftSibling.size_() > Math.ceil(m/2.0)-1;
		return leftSibling.size() > Math.ceil(m/2.0)-1;
	}
	
	// (��ܸ� ����) ���� Sibling�� borrow �ϱ⿡ ����� key ������ ���� �ִ��� Ȯ��
	boolean rightHasEnough(Node rightSibling) {
		
		// ������ Sibling ����x
		if(rightSibling == null) return false;
		if(rightSibling.m == -1) return false;
		
		if(rightSibling.isLeaf) return rightSibling.size_() > Math.ceil(m/2.0)-1;
		return rightSibling.size() > Math.ceil(m/2.0)-1;
	}
	
	// (��ܸ� ����) ���� ��忡�� except�� �ڽ��� ������ ��� �ڽ��� ���� Sibling�� ��ġ��
	void leftMerge(Node left, int except) {
		
		Node leftLastChild = left.rightChild; // ���� Sibling�� ������ �ڽ�
		
		// �� pair�� left_child_node �缳��
		Node prev = leftLastChild;
		for(int i=0; i<p.size(); i++) {
			
			Pair pair = p.get(i);
			if(i <= except) {
				
				left.insertPair(new Pair(pair.key, prev));
				prev = pair.left_child_node;
				left.rightChild = pair.left_child_node;
			}
			else {
				
				left.insertPair(pair);
			}
			
			// key ������Ʈ
			updateKey(left.size()-1, left, true);
		}
		// �� ������ �ڽ��� �� ����ó��
		if(except != p.size()) left.rightChild = rightChild;
		else left.rightChild = prev;
		
		Node root = parent;
		
		// �θ���� ���� ����
		if(childIndex == parent.size()) parent.rightChild = null;
		else {
			Pair newPair = parent.getPair(childIndex);
			newPair.left_child_node = null;
			parent.p.set(childIndex, newPair);
		}
		// ���� �θ��� �ڽĵ� (���� ���� ���� depth) �ڽ� ��ȣ �缳��
		int cnt = 0;
		for(int i=0; i<=parent.size(); i++) {
			
			if(i == childIndex) continue;
			if(i == parent.size()) parent.rightChild.setChildIndex(cnt);
			else parent.getPair(i).left_child_node.setChildIndex(cnt++);
		}
		parent = null;
		
		// parent, childIndex �缳��
		for(int i=0; i<=left.size(); i++) {
			
			Node child;
			if(i == left.size()) child = left.rightChild;
			else child = left.getPair(i).left_child_node;
			
			child.setParent(left);
			child.setChildIndex(i);
		}
	}
	
	// (��ܸ� ����) ���� ��忡�� except���� ������ ��� �ڽ��� ������ Sibling�� ��ġ��
	void rightMerge(Node right, int except) {
		
		// ���� ��忡�� ������������ ���� ����
		// �� pair�� left_child_node �缳��
		// ������ �ڽ��� �� ��ȣ������ ���� ���̽� �з�
		if(except == p.size()) {
			
			for(int i=p.size()-1; i>=0; i--) {
				Pair pair = p.get(i);
				right.insertPair(0, pair);
				
				// key ������Ʈ
				updateKey(0, right, true);
			}
		}
		else {
			
			Node prev = rightChild;
			for(int i=p.size()-1; i>=0; i--) {
				
				Pair pair = p.get(i);
				if(i >= except) {
					
					right.insertPair(0, new Pair(pair.key, prev));
					prev = pair.left_child_node;
				}
				else {
					
					right.insertPair(0, pair);
				}
				
				// key ������Ʈ
				updateKey(0, right, true);
			}
		}
		
		for(int i=0; i<=right.size(); i++) {
			
			Node child;
			if(i == right.size()) child = right.rightChild;
			else child = right.getPair(i).left_child_node;
		}
		
		// �θ���� ���� ����
		if(childIndex == parent.size()) parent.rightChild = null;
		else {
			Pair newPair = parent.getPair(childIndex);
			newPair.left_child_node = null;
			parent.p.set(childIndex, newPair);
		}
		// ���� �θ��� �ڽĵ� (���� ���� ���� depth) �ڽ� ��ȣ �缳��
		int cnt = 0;
		for(int i=0; i<=parent.size(); i++) {
			
			if(i == childIndex) continue;
			if(i == parent.size()) parent.rightChild.setChildIndex(cnt);
			else parent.getPair(i).left_child_node.setChildIndex(cnt++);
		}
		parent = null;
		
		// parent, childIndex �缳��
		for(int i=0; i<=right.size(); i++) {
			
			Node child;
			if(i == right.size()) child = right.rightChild;
			else child = right.getPair(i).left_child_node;
			
			child.setParent(right);
			child.setChildIndex(i);
		}
	}
	
	// (�ܸ� ����) �����Ǵ� �ܸ� ��忡�� ���� Sibling�� ����
	void connectFromDeletedNode() {
		if(!isLeaf) return;
		
		if(leftSibling != null) leftSibling.rightSibling = rightSibling;
		if(rightSibling != null) rightSibling.leftSibling = leftSibling;
	}
	
}

public class Bptree {
	public static void main(String[] args) throws IOException {	
		
		BufferedWriter bw = null;
		BufferedReader br = null;
		
		try {
			
			Tree bPlusTree = null;
			int b = -1;
			
			String mode = args[0];
			
			// Data File Creation 
			if(mode.equals("-c")) {
				
				String index_file = args[1];
				bw = new BufferedWriter(new FileWriter(index_file));
				b = Integer.parseInt(args[2]);
				
				bPlusTree = new Tree(b);
				bPlusTree.writeIndexFile(bw);
			}
			else {

				String index_file = args[1];
				br = new BufferedReader(new FileReader(index_file));
				
				String s = br.readLine();
				
				// b ���
				if(s.charAt(0) == 'b') {
					
					StringTokenizer st = new StringTokenizer(s);
					st.nextToken(); // 'b' ����
					b = Integer.parseInt(st.nextToken());
					
					bPlusTree = new Tree(b);
				}
				
				// index.dat ���
				Queue<Node> parentQueue = new LinkedList<>();	// �θ� ��带 ���� ť
				Queue<Node> currentQueue = new LinkedList<>();	// ���� ��带 ���� ť
				
				while((s = br.readLine()) != null) {
					
					Queue<Node> tmp = new LinkedList<>();		// swap ��
					StringTokenizer st = new StringTokenizer(s);
					String type = st.nextToken();
					
					// ��ܸ� ��� ó��
					if(type.equals("/")) {
						
						// root ���� ��ܸ� ���
						bPlusTree.root.isLeaf = false;
						
						// root ���
						if(parentQueue.size() == 0) {
							
							// root�� key pairs ���
							while(st.hasMoreTokens()) {
								
								String ttmp = st.nextToken();
								
								Long key = Long.parseLong(ttmp);
								bPlusTree.root.insertPair(new Pair(key, null));
							}
							parentQueue.offer(bPlusTree.root); // root ����
							
						}
						// ��ܸ� ��� ���
						else {
							
							// currentQueue�� ���� ���� ����
							Node curNode = new Node(false, b, 0, null, null, null, null);
							while(st.hasMoreTokens()) {
								
								String cur = st.nextToken();
								
								// ��� ������
								if(cur.equals("/")) {
									
									// ������ ť ����
									currentQueue.offer(curNode);
									tmp.offer(curNode);
									curNode = new Node(false, b, 0, null, null, null, null);
								}
								// ���� ��忡 key pair ����
								else {
									
									Long key = Long.parseLong(cur);
									curNode.insertPair(new Pair(key, null));
								}
							}
							currentQueue.offer(curNode); // ������ ��� ����
							tmp.offer(curNode);
							
							// ��� �θ� ���� ��Ī�� ������ ����
							while(parentQueue.size() > 0) {
								
								Node parent = parentQueue.poll();
								for(int childIndex=0; childIndex<=parent.size(); childIndex++) {
									
									curNode = currentQueue.poll();
									curNode.setParent(parent);
									curNode.setChildIndex(childIndex);
									
									// rightmost �ڽ� ���
									if(parent.size() == childIndex) 
										parent.rightChild = curNode;
									// left_child_node ���
									else {
										
										Pair pair = parent.getPair(childIndex);
										pair.left_child_node = curNode;
										parent.p.set(childIndex, pair);
									}
								}
							}
							
							// ���� ������ ���� �������� �θ� ��尡 ��
							parentQueue = tmp;
						}
					}
					// �ܸ� ��� ó��
					else {
						
						// root ���
						if(parentQueue.size() == 0) {
							
							// root�� key pairs ���
							while(st.hasMoreTokens()) {
								
								Long key = Long.parseLong(st.nextToken());
								Long value = Long.parseLong(st.nextToken());
								ValuePointer valuePointer = new ValuePointer(value);
								bPlusTree.root.insertPair_(new Pair_(key, null));
							}
						}
						// �ܸ� ��尡 root�� �ƴ϶��
						else {
						
							// currentQueue�� ���� ���� ����
							Node curNode = new Node(true, b, 0, null, null, null, null);
							Node prev = null;
							while(st.hasMoreTokens()) {
								
								String cur = st.nextToken();
								
								// ��� ������
								if(cur.equals("#")) {
									
									// ������ ť ����
									currentQueue.offer(curNode);
									
									// ���� �ܸ� ���� ����
									if(prev != null) {
										
										prev.rightSibling = curNode;
										curNode.leftSibling = prev;
									}
									
									prev = curNode;
									curNode = new Node(true, b, 0, null, null, null, null);
								}
								// ���� ��忡 key pair ����
								else {
									
									Long key = Long.parseLong(cur);
									Long value = Long.parseLong(st.nextToken());
									ValuePointer valuePointer = new ValuePointer(value);
									curNode.insertPair_(new Pair_(key, valuePointer));
								}
							}
							currentQueue.offer(curNode); // ������ ��� ����
							
							// ���� �ܸ� ���� ����
							prev.rightSibling = curNode;
							curNode.leftSibling = prev;
							
							// ��� �θ� ���� ��Ī�� ������ ����
							while(parentQueue.size() > 0) {
								
								Node parent = parentQueue.poll();
								for(int childIndex=0; childIndex<=parent.size(); childIndex++) {
									
									curNode = currentQueue.poll();
									curNode.setParent(parent);
									curNode.setChildIndex(childIndex);
									
									// rightmost �ڽ� ���
									if(parent.size() == childIndex) 
										parent.rightChild = curNode;
									// left_child_node ���
									else {
										
										Pair pair = parent.getPair(childIndex);
										pair.left_child_node = curNode;
										parent.p.set(childIndex, pair);
									}
								}
							}
						}
					}
				}
				
				// Insertion
				if(mode.equals("-i")) {
					
					String data_file = args[2];
					br = new BufferedReader(new FileReader(data_file));
					
					String string;
					while((string = br.readLine()) != null) {
						
						// input data ó��
						StringTokenizer st = new StringTokenizer(string, ",", true);
						long key = Long.parseLong(st.nextToken());
						String tmp = st.nextToken();
						long value = Long.parseLong(st.nextToken());
						ValuePointer valuePointer = new ValuePointer(value);
						
						Pair_ newPair = new Pair_(key, valuePointer);
						
						SplitChildren ret = bPlusTree.insert(null, bPlusTree.root, newPair, -1, 0);
						
						// root���� overflow �߻�
						if(ret.left != null) {
							
							// root �缳�� �� �ڽ� ����
							Node newRoot = new Node(false, b, 0, null, ret.right, null, null);
							newRoot.insertPair(new Pair(ret.addKey, ret.left));
							ret.left.parent = newRoot;
							ret.right.parent = newRoot;
							bPlusTree.root = newRoot;
						}
					}
					
					// index.dat �ٽ� ����
					bw = new BufferedWriter(new FileWriter(index_file));
					bPlusTree.writeIndexFile(bw);
				}
				// Deletion
				else if(mode.equals("-d")) {
					
					String data_file = args[2];
					br = new BufferedReader(new FileReader(data_file));
					
					String string;
					while((string = br.readLine()) != null) {
						
						StringTokenizer st = new StringTokenizer(string);
						long deleteKey = Long.parseLong(st.nextToken());
						
						bPlusTree.delete(bPlusTree.root, deleteKey, 0);
						
					}
					
					// index.dat �ٽ� ����
					bw = new BufferedWriter(new FileWriter(index_file));
					bPlusTree.writeIndexFile(bw);
				}
				// Single Key Search
				else if(mode.equals("-s")) {
					
					long searchKey = Long.parseLong(args[2]);
					
					bPlusTree.search(true, searchKey, -1, bPlusTree.root);
					
				}
				// Ranged Search
				else if(mode.equals("-r")) {
					
					long start_key = Long.parseLong(args[2]);
					long end_key = Long.parseLong(args[3]);
					
					bPlusTree.search(false, start_key, end_key, bPlusTree.root);
				}
			}
			
		} finally {
			if(br != null) br.close();
			if(bw != null) bw.close();
		}
		
	}
}
