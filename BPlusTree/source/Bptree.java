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
	 * 검색
	 * mode == true  : single key search
	 * mode == false : range search
	 * 
	 * 비단말 노드 : 속한 key를 모두 출력
	 * 단말 노드 : 답을 찾았다면 value를, 아니면 "NOT FOUND" 출력
	 */
	void search(boolean mode, long key, long key2, Node cur) {
		
		// single key search
		if(mode) {
			
			// 단말 노드
			if(cur.isLeaf) {
				
				// 단말 노드에 key 값이 존재하는지 확인
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
			// 비단말 노드
			else {
				
				// 노드 내의 데이터를 조사하면서 조건에 맞을 때까지 진행
				// 거쳐가는 노드에 속한 모든 key를 print
				boolean alreadySearched = false;
				int nextSearchIndex = -1;
				for(int i=0; i<=cur.size(); i++) {
					
					if(i == cur.size()) {
						
						if(!alreadySearched) nextSearchIndex = i;
						continue;
					}
					
					// 현재 노드에 속한 모든 key 출력
					Pair pair = cur.getPair(i);
					if(i < cur.size()-1) System.out.print(pair.key + ",");
					else System.out.println(pair.key);
					
					if(key < cur.getPair(i).key) {
						
						if(!alreadySearched) nextSearchIndex = i;
						alreadySearched = true;
					}
				}
				
				// 단말 노드로 내려가면서 탐색
				if(nextSearchIndex == cur.size()) search(mode, key, key2, cur.rightChild);
				else search(mode, key, key2, cur.getPair(nextSearchIndex).left_child_node);
			}
			
		}
		// range search
		else {
			
			// 단말 노드
			if(cur.isLeaf) {
				
				Node current = cur;
				boolean start = false;
				
				while(true) {
					
					// 현재 노드에서 [key, key2]에 속하는 값 출력
					for(Pair_ pair : current.p_) {
						
						if(key <= pair.key && pair.key <= key2) {
							start = true;
							System.out.println(pair.key + "," + pair.valuePointer.node.value);
						}
						
						// 탐색 중지
						if(pair.key >= key2) return;
					}
					
					// 탐색 중지
					if(current.rightSibling == null) return;
					
					current = current.rightSibling;
				}
			}
			// 비단말 노드
			else {
				
				// 노드 내의 데이터를 조사하면서 조건에 맞을 때까지 진행
				// 조건에 맞으면 단말노드에 접근할 때까지 재귀
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
				
				// 탐색 실패
				return;
			}
			
		}
	}
	
	/* 
	 * 삽입
	 */
	SplitChildren insert(Node parent, Node cur, Pair_ pair, int childIndex, int depth) {
		
		// 단말 노드
		if(cur.isLeaf) {
			
			// root : key값 등록 후 종료
			if(cur.size_() == 0) {
				cur.insertPair_(pair);
				return new SplitChildren(-1, null, null);
			}
			
			for(int i=0; i<=cur.size_(); i++) {
				
				long key = -1;
				
				// 마지막 위치가 아니라면
				if(i != cur.size_()) {
					
					key = cur.getPair_(i).key;
					
					// 같은 key값이 존재할 경우 예외처리
					if(pair.key == key) 
						return new SplitChildren(-1, null, null);
				}
				
				// 넣으려는 key값이 탐색된 key값보다 작거나
				// 삽입할 자리를 마지막까지 못찾은 경우
				if(i == cur.size_() || (i != cur.size_() && pair.key < key)) {
					
					cur.insertPair_(i, pair);
					
					// overflow 발생 시				
					if(cur.size_() >= m) {
						
						// 왼쪽 Sibling이 공간을 갖고 있다면 key pair 보내기
						if(cur.leftSiblingHasSpace(true, new Node())) {
							
							cur.giveToLeft(true, true, false, cur.leftSibling);

							// 부모 key 업데이트
							if(childIndex > 0) 
								cur.updateKey(childIndex-1, cur.parent, true);
						}
						// 오른쪽 Sibling이 공간을 갖고 있다면 key pair 보내기
						else if(cur.rightSiblingHasSpace(true, new Node())) {
						
							cur.giveToRight(true, true, false, cur.rightSibling);
							
							// 부모 key 업데이트
							if(childIndex < cur.parent.size()) 
								cur.updateKey(childIndex, cur.parent, true);
						}
						// 양쪽 Sibling이 공간이 없다면 split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);
							
							// 분할 된 두 노드를 서로 연결
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
		
		// 비단말 노드
		for(int i=0; i<=cur.size(); ++i) {
			
			// 삽입할 자리를 마지막까지 못찾은 경우
			if(i == cur.size()) {
				
				// 단말 노드에 도달해서 삽입할 때까지 재귀 돌리기
				// 반환값 != null : child에서 overflow 발생 -> 부모로 중간값 이동
				SplitChildren children = insert(cur, cur.rightChild, pair, i, depth+1);
				
				// 자식에서 overflow된 중간값 처리
				if(children.left != null) {
					
					// 현재 노드에 삽입 및 자식과 연결
					cur.insertPair(new Pair(children.addKey, children.left));
					cur.rightChild = children.right;
					children.left.parent = cur;
					children.right.parent = cur;
					
					// overflow 발생 시
					if(cur.size() >= m) {
						
						Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
						Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
						
						// 왼쪽 Sibling에 남는 공간이 있다면 key pair 보내기
						if(cur.leftSiblingHasSpace(false, leftSibling)) {
							cur.giveToLeft(false, true, false, leftSibling);
						}
						// 오른쪽 Sibling에 남는 공간이 있다면 key pair 보내기
						else if(cur.rightSiblingHasSpace(false, rightSibling)) {
							cur.giveToRight(false, true, false, rightSibling);
						}
						// 양쪽 Sibling에 공간이 없다면 split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);				
							
							// 현재 노드가 split 된다면 이미 분리된 2개의 자식 노드는 부모의 오른쪽 노드와 매핑돼야 함
							children.left.parent = splitedChildren.right;
							children.right.parent = splitedChildren.right;
							
							return splitedChildren;
						}
						
						return new SplitChildren(-1, null, null);
					}
				}
				
				// key 값 업데이트
				if(children.left != null) cur.updateKey(i, cur, true);
				else cur.updateKey(i-1, cur, true);
				
				return new SplitChildren(-1, null, null);
			}
			
			long key = cur.getPair(i).key;
			
			// 삽입할 key값 < 이미 존재하는 key값
			if(pair.key < key) {
				
				Pair currentPair = cur.getPair(i);
				
				// 단말 노드에 도달해서 삽입할 때까지 재귀 돌리기
				// 반환값 != null : child에서 overflow 발생 -> 부모로 중간값 이동
				SplitChildren children = insert(cur, currentPair.left_child_node, pair, i, depth+1);
				
				// 자식에서 overflow된 중간값 처리
				if(children.left != null) {
					
					// 현재 노드에 삽입 및 자식과 연결
					cur.insertPair(i, new Pair(children.addKey, children.left));
					cur.setChild(i+1, children.right);
					children.left.parent = cur;
					children.right.parent = cur;
					
					// overflow 발생 시
					if(cur.size() >= m) {
						
						Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
						Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
						
						// 왼쪽 Sibling에 남는 공간이 있다면 key pair 보내기
						if(cur.leftSiblingHasSpace(false, leftSibling)) {
							cur.giveToLeft(false, true, false, leftSibling);
						}
						// 오른쪽 Sibling에 남는 공간이 있다면 key pair 보내기
						else if(cur.rightSiblingHasSpace(false, rightSibling)) {
							cur.giveToRight(false, true, false, rightSibling);
						}
						// 양쪽 Sibling에 공간이 없다면 split
						else {
							
							SplitChildren splitedChildren = cur.split(m/2);
							return splitedChildren;
						}
						
						return new SplitChildren(-1, null, null);
					}
				}
				
				// key 값 업데이트
				if(i > 0) cur.updateKey(i-1, cur, true); 
				return new SplitChildren(-1, null, null);
			}
			
		}
		
		return new SplitChildren(-1, null, null);
	}
	
	/*
	 * 삭제
	 * true  : 자식노드가 삭제됨 (별도의 조치 필요)
	 * false : 자식노드가 삭제되지 않았음
	 * 
	 * main에서 true를 반환하고 root가 단말 노드라면
	 * 트리에 모든 노드가 없는 것이므로 root = null 설정
	 */
	boolean delete(Node cur, long target, int depth) {
		
		// 단말 노드
		if(cur.isLeaf) {

			for(int i=0; i<cur.size_(); i++) {
				
				// 삭제할 key 찾음
				if(cur.getPair_(i).key == target) {
					
					cur.p_.remove(i); // 해당 key 삭제
					
					// 현재 노드가 root가 아닐 때
					if(depth != 0) {
						
						if(cur.size_() < Math.ceil(m/2.0)-1) {
							
							Node left = cur.leftSibling;
							Node right = cur.rightSibling;
							
							// 왼쪽에 빌릴 여유가 있으면
							if(cur.leftHasEnough(left)) {
								
								Pair_ pair = left.p_.remove(left.size_()-1);
								cur.insertPair_(0, pair);
								return false;
							}
							// 오른쪽에 빌릴 여유가 있으면
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
								
								// 소멸 및 양쪽 단말 노드 연결
								cur.connectFromDeletedNode();
							}
							
							return true;
						}
					}
					// 현재 노드가 root
					else {
						
						if(cur.size_() < 1)
							return true;
					}
					
					return false;
				}
			}
			
			return false;
		}
		
		// 비단말 노드
		for(int i=0; i<=cur.size(); i++) {
			
			// 마지막 key보다 크거나 같을 때
			if(i == cur.size()) {
				
				if(cur.getPair(i-1).key <= target) {
					
					// 자식이 없어졌을 때 현재 노드의 key 개수에 따라 케이스 분류
					boolean deleted = delete(cur.rightChild, target, depth+1);
					if(deleted) {
						
						// root의 자식이 merge되면 자식노드를 root로 설정
						if(depth == 0 && cur.size() <= 1) {
							
							// 루트의 오른쪽 자식 노드가 없다면
							if(cur.rightChild == null) {
								Node left = cur.getPair(0).left_child_node;
								
								// 현재 노드는 소멸되므로 합병된 왼쪽 자식을 root로 설정
								root = left;
								root.setParent(null);
								root.setChildIndex(0);
								
								return true;	
							}
							
							// 루트의 자식 노드가 단말 노드
							if(cur.rightChild.isLeaf) {
								
								Node left = cur.getPair(0).left_child_node;
								
								// 현재 노드는 소멸되므로 합병된 왼쪽 자식을 root로 설정
								root = left;
								root.setParent(null);
								root.setChildIndex(0);
							}

							return true;
						}
						
						// 현재 노드의 key 개수가 최소 개수일 때
						if(depth > 0 && cur.size() <= Math.ceil(m/2.0)-1) {
							
							Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
							
							// 왼쪽 Sibling이 존재하면
							if(leftSibling.m != -1) {
								
								// 왼쪽 Sibling에서 빌릴 여유 있으면 -> borrow
								// borrow 발생 시 더 이상 부모로의 구조 변화에 대한 영향 없음
								if(cur.leftHasEnough(leftSibling)) {
									
									leftSibling.giveToRight(false, false, true, cur);
									return false;
								}
								// 왼쪽 Sibling도 key pair가 최소 개수라면 -> merge
								else {
									
									cur.leftMerge(leftSibling, i);
								}
									
								return true;
							}
							// 오른쪽 Sibling으로 이동
							else {
								
								Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
								
								if(rightSibling != null) {
								
									// 오른쪽 Sibling에서 빌릴 여유 있으면 -> borrow
									// borrow 발생 시 더 이상 부모로의 구조 변화에 대한 영향 없음
									if(cur.rightHasEnough(rightSibling)) {
										
										rightSibling.giveToLeft(false, false, true, cur);
										return false;
									}
									// 오른쪽 Sibling도 key pair가 최소 개수라면 -> merge
									else {
										
										cur.rightMerge(rightSibling, i);
										
										// 이동한 key pair와 부모 key pair 업데이트
										if(rightSibling.parent != null && rightSibling.childIndex > 0)
											rightSibling.updateKey(rightSibling.childIndex-1, rightSibling.parent, true);
									}
									
									return true;
								}
							}
						}
						
						// 현재 노드 삭제 후에도 구조가 유지되는 경우
						// 최소 개수보다 많으므로 삭제
						// 가장 오른쪽 자식 재설정 후 삭제
						cur.rightChild = cur.getPair(i-1).left_child_node;
						cur.p.remove(i-1);
						
						// key 업데이트
						if(i > 0) cur.updateKey(cur.size()-1, cur, true);
						
						return false;
					}
					
					// key 업데이트
					if(i > 0) cur.updateKey(i-1, cur, true);
					
					return false;
				}
			}
			// [0, size()-1]
			else {

				if(target < cur.getPair(i).key) {
					
					// 자식이 없어졌을 때 현재 노드의 key 개수에 따라 케이스 분류
					boolean deleted = delete(cur.getPair(i).left_child_node, target, depth+1);
					if(deleted) {
						
						// root의 자식이 merge되면 자식노드를 root로 설정
						if(depth == 0 && cur.size() <= 1) {
							
							// root의 왼쪽 자식이 소멸했을 때
							if(cur.getPair(0).left_child_node == null) {
								
								Node right = cur.rightChild;
								
								// 현재 노드는 소멸되므로 합병된 오른쪽 자식을 root로 설정
								root = right;
								root.setParent(null);
								root.setChildIndex(0);
								
								return true;
							}
							
							// 루트의 자식 노드가 단말 노드
							
							if(cur.getPair(0).left_child_node.isLeaf) {
								
								Node right = cur.rightChild;
								
								// 현재 노드는 소멸되므로 오른쪽 자식을 root로 설정
								root = right;
								root.setParent(null);
								root.setChildIndex(0);
							}
							
							return true;
						}
						
						if(depth > 0 && cur.size() <= Math.ceil(m/2.0)-1) {
							
							Node leftSibling = cur.searchLeftSibling(false, cur, depth, depth);
							
							// 왼쪽 Sibling이 존재하면
							if(leftSibling.m != -1) {
								
								// 왼쪽 Sibling에서 빌릴 여유 있으면 -> borrow
								// borrow 발생 시 더 이상 부모로의 구조 변화에 대한 영향 없음
								if(cur.leftHasEnough(leftSibling)) {
									
									cur.p.remove(i);
									leftSibling.giveToRight(false, false, false, cur);
									return false;
								}
								// 왼쪽 Sibling도 key pair가 최소 개수라면 -> merge
								else {
		
									cur.leftMerge(leftSibling, i);
								}
								
								return true;
							}
							// 오른쪽 Sibling으로 이동
							else {
								
								Node rightSibling = cur.searchRightSibling(false, cur, depth, depth);
								
								if(rightSibling != null) {
									
									// 오른쪽 Sibling에서 빌릴 여유 있으면 -> borrow
									// borrow 발생 시 더 이상 부모로의 구조 변화에 대한 영향 없음
									if(cur.rightHasEnough(rightSibling)) {
										
										// 빌리기 전에 자식 위치 및 자식 번호 재설정
										cur.p.remove(i);
										for(int j=0; j<=cur.size(); ++j)
											if(j == cur.size()) cur.rightChild.setChildIndex(j);
											else cur.getPair(j).left_child_node.setChildIndex(j);
										
										rightSibling.giveToLeft(false, false, false, cur);
										
										return false;
									}
									// 오른쪽 Sibling도 key pair가 최소 개수라면 -> merge
									else {
										
										cur.rightMerge(rightSibling, i);
										
										// 오른쪽 Sibling의 부모 key pair 업데이트
										if(rightSibling.parent != null && rightSibling.childIndex > 0)
											rightSibling.updateKey(rightSibling.childIndex-1, rightSibling.parent, true);
									}
									
									return true;
								}
							}
						}
						
						// 현재 노드 삭제 후에도 구조가 유지되는 경우
						// 최소 개수보다 많으므로 삭제
						// key pair 업데이트
						cur.p.remove(i);
						if(i > 0) cur.updateKey(i, cur, true);
						
						return false;
					}
					
					// key 업데이트
					cur.updateKey(i, cur, true);
					return false;
				}
			}
		}
		
		return false;
	}

	void writeIndexFile(BufferedWriter bw) throws IOException {
		
		// b 정보 기록
		bw.write("b " + root.m + "\r\n");
		bw.flush();
		
		// 기록된 내용 없음
		if(root.isLeaf && root.size_() == 0) return;
		if(!root.isLeaf && root.size() == 0) return;
		
		// breadth-first search
		// 노드를 거쳐가면서 key pairs 바로 기록
		// 현재 노드가 단말 노드가 아니라면 queue에 자식을 push
		Queue<Node> q = new LinkedList<>();
		root.setChildIndex(-1);
		q.offer(root);
		
		while(q.size() > 0) {
			
			Node curNode = q.poll();
			
			// 단말 노드
			if(curNode.isLeaf) {
				
				bw.write("# ");
				for(int i=0; i<curNode.size_(); i++) {
					
					Pair_ pair = curNode.getPair_(i);
					
					// 트리 구조 작성
					bw.write(pair.key + " " + pair.valuePointer.node.value + " ");
				}
			}
			// 비단말 노드
			else {
				
				bw.write("/ ");
				for(int i=0; i<curNode.size(); i++) {
					
					Pair pair = curNode.getPair(i);
					
					// 트리 구조 작성
					bw.write(pair.key + " ");
					
					// queue에 자식 노드 삽입
					q.offer(pair.left_child_node);
				}
				
				// queue에 마지막 자식 노드 삽입
				// depth별 기록을 위해 값 수정
				if(curNode.childIndex == -1) 
					curNode.rightChild.setChildIndex(-1);
				q.offer(curNode.rightChild);
			}
			
			// depth별로 기록
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
	boolean isLeaf; 	 // true : 단말 노드 false : 비단말 노드
	int m;	  			 // # of keys
	int childIndex; 	 // (비단말 노드용) 부모 노드의 childIndex번째 자식
	ArrayList<Pair> p;	 // (단말 노드용) an array of<key, left_child_node> pairs
	ArrayList<Pair_> p_; // (비단말 노드용) an array of<key, value(or pointer to the value)> pairs
	Node parent;		 // 직계 부모 노드
	Node rightChild;	 // (비단말 노드용) 현재 노드의 가장 오른쪽 child
	Node leftSibling;	 // (단말 노드용) 왼쪽 sibling node
	Node rightSibling;	 // (단말 노드용) 오른쪽 sibling node
	
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
	
	// 비단말 노드의 key pair 개수
	int size() { 
		return p.size();
	}
	
	// 단말 노드의 key pair 개수
	int size_() {
		return p_.size();
	}
	
	// overflow 시 현재 노드를 두 노드로 분할
	// 분할되는 양쪽 노드와 중간값을 반환
	SplitChildren split(int dividedIndex) {
		
		// 단말 노드
		if(isLeaf) {

			Node rightNode = new Node(true, m, 0, null, null, this, rightSibling);
			
			// 분리된 노드와 이웃 노드 관계 재설정
			if(rightSibling != null) {
				rightSibling.leftSibling = rightNode;
				rightSibling = rightNode;
			}
			
			// 분할되는 노드에 <key, value> 복사
			for(int i=dividedIndex; i<p_.size(); i++)
				rightNode.insertPair_(p_.get(i));
			
			// 복사된 <key, value> 삭제
			while(p_.size() > dividedIndex) 
				p_.remove(dividedIndex);
			
			return new SplitChildren(rightNode.getPair_(0).key, this, rightNode);
		}
		
		// 비단말 노드
		Node rightNode = new Node(false, m, childIndex+1, parent, rightChild, null, null);
		
		// 분할되는 노드에 <key, left_child_node> 복사
		// 만약 자식 노드가 비단말 노드라면 자식 번호 재설정
		int cnt = 0;
		for(int i=dividedIndex; i<p.size(); i++) {
			if(!rightChild.isLeaf && i>dividedIndex) p.get(i).left_child_node.childIndex = cnt++;
			rightNode.insertPair(p.get(i));
		}
		if(!rightChild.isLeaf) rightChild.childIndex = cnt;
		
		// 현재 노드(left)의 rightChild 재설정
		rightChild = p.get(dividedIndex).left_child_node;
		
		// 복사된 <key, left_child_node> 삭제
		while(p.size() > dividedIndex)
			p.remove(dividedIndex);
		
		// 중간 key pair는 부모로 이동
		// 비단말 노드에서는 중간 key pair가 삭제됨
		Pair pair = rightNode.getPair(0);
		rightNode.p.remove(0);
		
		// 자식-부모 연결
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
	
	// 왼쪽 Sibling 리턴
	// turn      : 목표 노드가 속한 subtree 진입 여부
	// cur       : 현재 노드
	// curDepth  : 현재 노드의 depth
	// goalDepth : 목표 노드의 depth
	Node searchLeftSibling(boolean turn, Node cur, int curDepth, int goalDepth) {

		// 목표 노드가 속한 subtree에 진입하지 못했다면
		if(!turn) {
			
			// root로 향하면서 왼쪽 subtree가 있는지 확인
			if(curDepth > 0) {
				if(cur.childIndex == 0) 
					return searchLeftSibling(false, cur.parent, curDepth-1, goalDepth);
				
				// 왼쪽 subtree 발견
				Node left = cur.parent.getPair(cur.childIndex-1).left_child_node;
				return searchLeftSibling(true, left, curDepth, goalDepth);
			}
			
			// 왼쪽 Sibling 없음
			return new Node();
		}
		
		// 목표 노드가 속한 subtree에 진입했다면 -> 답 반드시 존재
		// 탐색 성공
		if(curDepth == goalDepth) return cur;
				
		// depth가 같아질 때까지 탐색
		return searchLeftSibling(true, cur.rightChild, curDepth+1, goalDepth);
	}
	
	// 오른쪽 Sibling 리턴
	// turn      : 목표 노드가 속한 subtree 진입 여부
	// cur       : 현재 노드
	// curDepth  : 현재 노드의 depth
	// goalDepth : 목표 노드의 depth
	Node searchRightSibling(boolean turn, Node cur, int curDepth, int goalDepth) {
		
		// 목표 노드가 속한 subtree에 진입하지 못했다면
		if(!turn) {
			
			// root로 향하면서 오른쪽 subtree가 있는지 확인
			if(curDepth > 0) {
				
				// 현재 노드가 root
				if(cur.parent == null) return new Node();
				
				// 현대 노드가 root가 아니라면
				if(cur.childIndex == cur.parent.size()) 
					return searchRightSibling(false, cur.parent, curDepth-1, goalDepth);
				
				// 오른쪽 subtree 발견
				Node right = cur.childIndex == cur.parent.size()-1 ?
						parent.rightChild : parent.getPair(cur.childIndex+1).left_child_node;
				return searchRightSibling(true, right, curDepth, goalDepth);
			}
			
			// 오른쪽 Sibling 없음
			return new Node();
		}
		
		// 목표 노드가 속한 subtree에 진입했다면 -> 답 반드시 존재
		// 탐색 성공
		if(curDepth == goalDepth) return cur;
				
		// depth가 같아질 때까지 탐색
		return searchLeftSibling(true, cur.getPair(0).left_child_node, curDepth+1, goalDepth);
	}
	
	// 왼쪽 Sibling이 공간이 있는지 체크
	boolean leftSiblingHasSpace(boolean isLeaf, Node leftSibling) {
		if(isLeaf) {
			if(this.leftSibling == null) return false; // 맨 왼쪽 LeafNode
			return this.leftSibling.size_() < m-1;
		}
		
		// 왼쪽 Sibling 존재x
		if(leftSibling.m == -1) return false;
		
		return leftSibling.size() < m-1;
	}
	
	// 오른쪽 Sibling이 공간이 있는지 체크
	boolean rightSiblingHasSpace(boolean isLeaf, Node rightSibling) {
		if(isLeaf) {
			if(this.rightSibling == null) return false; // 맨 오른쪽 LeafNode
			return this.rightSibling.size_() < m-1;
		}
		
		// 오른쪽 Sibling 존재X
		if(rightSibling.m == -1) return false;
		
		return rightSibling.size() < m-1;
	}
	
	// 제일 앞에 있는 key pair을 왼쪽 Sibling에 삽입
	void giveToLeft(boolean isLeaf, boolean isInsert, boolean isEnd, Node leftSibling) {
		if(isLeaf) {
			
			Pair_ pair = new Pair_(p_.get(0).key, p_.get(0).valuePointer);
			p_.remove(0); // 현재 노드에서 이동하려는 pair 제거
			
			// 왼쪽 Sibling의 맨 뒤의 key로 설정
			leftSibling.insertPair_(pair);
			
			return;
		}
		
		Pair pair = new Pair(p.get(0).key, p.get(0).left_child_node);
		p.remove(0); // 현재 노드에서 이동하려는 pair 제거
		
		// 자식 재설정
		Node tmp = leftSibling.rightChild;
		leftSibling.rightChild = pair.left_child_node;
		pair.left_child_node = tmp;
		
		// 왼쪽으로 pair 넘기기
		leftSibling.insertPair(pair);
		
		// 옮긴 pair의 자식 부모 설정
		leftSibling.getPair(leftSibling.size()-1).left_child_node.setParent(leftSibling);
		
		// delete인데 삭제된 자식이 마지막 자식이라면
		if(!isInsert && isEnd) {
			
			Pair lastPair = leftSibling.p.remove(leftSibling.size()-1);
			leftSibling.rightChild = lastPair.left_child_node;
			updateKey(leftSibling.size()-1, leftSibling, true);
		}
		
		// 현재 노드의 자식 번호 재설정
		for(int i=0; i<=p.size(); i++) {
			if(i == p.size()) rightChild.setChildIndex(i);
			else p.get(i).left_child_node.setChildIndex(i);
		}
		
		// 왼쪽 Sibling의 자식 번호 재설정
		for(int i=0; i<=leftSibling.size(); i++) {
			if(i == leftSibling.size()) leftSibling.rightChild.setChildIndex(i);
			else leftSibling.getPair(i).left_child_node.setChildIndex(i);
		}
		
		// 왼쪽 Sibling의 맨 뒤 key pair와 부모 key pair 재설정
		updateKey(leftSibling.size()-1, leftSibling, true);
		if(childIndex > 0) updateKey(childIndex-1, parent, true);
	}
	
	// 제일 뒤에 있는 key pair을 오른쪽 Sibling에 삽입
	void giveToRight(boolean isLeaf, boolean isInsert, boolean isEnd, Node rightSibling) {
		if(isLeaf) {
			
			Pair_ pair = new Pair_(p_.get(p_.size()-1).key, p_.get(p_.size()-1).valuePointer);
			p_.remove(p_.size()-1); // 현재 노드에서 이동하려는 pair 제거
			
			// 오른쪽 Sibling의 맨 앞의 key로 설정
			rightSibling.insertPair_(0, pair);
			
			return;
		}
		
		Pair pair = p.get(p.size()-1);
		p.remove(p.size()-1); // 현재 노드에서 이동하려는 pair 제거
		
		// 자식 재설정
		Node tmp = rightChild;
		rightChild = pair.left_child_node;
		pair.left_child_node = tmp;

		// 오른쪽으로 pair 넘기기
		rightSibling.insertPair(0, pair);
		
		// 오른쪽 Sibling의 첫 번째 자식 부모 설정
		rightSibling.getPair(0).left_child_node.setParent(rightSibling);
		
		// (delete 시) 오른쪽 Sibling의 사라진 자식 rightmost 여부에 따라 케이스 분류
		if(!isInsert && isEnd) {
			
			pair = rightSibling.p.remove(rightSibling.size()-1);
			rightSibling.rightChild = pair.left_child_node;
			updateKey(0, rightSibling, true);
		}
		
		// 오른쪽 Sibling의 자식 번호 재설정
		// 모든 자식 번호가 1씩 늘게 되므로 재설정
		for(int i=0; i<=rightSibling.size(); i++) {
			if(i == rightSibling.size()) rightSibling.rightChild.setChildIndex(i);
			else rightSibling.getPair(i).left_child_node.setChildIndex(i);
		}
		
		// 오른쪽 Sibling의 맨 앞 key pair와 부모 key pair 재설정
		updateKey(0, rightSibling, true);
		if(childIndex > 0) updateKey(childIndex-1, parent, true);
	}
		
	// (비단말 노드용) index번째 Pair의 key를 오른쪽 서브트리의 제일 작은 값으로 갱신
	// start : 업데이트 할 노드에서만 true, 그렇지 않으면 false
	// 리턴값 : 단말 노드의 key값
	long updateKey(int index, Node cur, boolean start) {
		
		// 단말 노드
		if(cur.isLeaf) return cur.getPair_(0).key;
		
		// 비단말 노드
		long key;
		
		// update할 노드는 오른쪽 subtree로 이동해야 함
		if(start) {
			
			// 마지막 pair
			if(index == cur.size()-1) 
				key = updateKey(0, cur.rightChild, false);
			// 마지막 pair가 아니면
			else 
				key = updateKey(0, cur.getPair(index+1).left_child_node, false);
			
			// 갱신 완료
			Pair pair = cur.getPair(index);
			pair.key = key;
			cur.p.set(index, pair);
			
			return key;
		}
		
		// update할 노드를 떠났다면 가장 작은 값으로 이동
		Pair pair = cur.getPair(0);
		return updateKey(0, pair.left_child_node, false);
	}
	
	// (비단말 노드용) 왼쪽 Sibling이 borrow 하기에 충분한 key 개수를 갖고 있는지 확인
	boolean leftHasEnough(Node leftSibling) {

		// 왼쪽 Sibling 존재x
		if(leftSibling == null) return false;
		if(leftSibling.m == -1) return false;
		
		if(leftSibling.isLeaf) return leftSibling.size_() > Math.ceil(m/2.0)-1;
		return leftSibling.size() > Math.ceil(m/2.0)-1;
	}
	
	// (비단말 노드용) 오른 Sibling이 borrow 하기에 충분한 key 개수를 갖고 있는지 확인
	boolean rightHasEnough(Node rightSibling) {
		
		// 오른쪽 Sibling 존재x
		if(rightSibling == null) return false;
		if(rightSibling.m == -1) return false;
		
		if(rightSibling.isLeaf) return rightSibling.size_() > Math.ceil(m/2.0)-1;
		return rightSibling.size() > Math.ceil(m/2.0)-1;
	}
	
	// (비단말 노드용) 현재 노드에서 except번 자식을 제외한 모든 자식을 왼쪽 Sibling에 합치기
	void leftMerge(Node left, int except) {
		
		Node leftLastChild = left.rightChild; // 왼쪽 Sibling의 마지막 자식
		
		// 각 pair의 left_child_node 재설정
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
			
			// key 업데이트
			updateKey(left.size()-1, left, true);
		}
		// 맨 오른쪽 자식일 때 예외처리
		if(except != p.size()) left.rightChild = rightChild;
		else left.rightChild = prev;
		
		Node root = parent;
		
		// 부모와의 연결 끊기
		if(childIndex == parent.size()) parent.rightChild = null;
		else {
			Pair newPair = parent.getPair(childIndex);
			newPair.left_child_node = null;
			parent.p.set(childIndex, newPair);
		}
		// 같은 부모의 자식들 (현재 노드와 같은 depth) 자식 번호 재설정
		int cnt = 0;
		for(int i=0; i<=parent.size(); i++) {
			
			if(i == childIndex) continue;
			if(i == parent.size()) parent.rightChild.setChildIndex(cnt);
			else parent.getPair(i).left_child_node.setChildIndex(cnt++);
		}
		parent = null;
		
		// parent, childIndex 재설정
		for(int i=0; i<=left.size(); i++) {
			
			Node child;
			if(i == left.size()) child = left.rightChild;
			else child = left.getPair(i).left_child_node;
			
			child.setParent(left);
			child.setChildIndex(i);
		}
	}
	
	// (비단말 노드용) 현재 노드에서 except번을 제외한 모든 자식을 오른쪽 Sibling에 합치기
	void rightMerge(Node right, int except) {
		
		// 현재 노드에서 내림차순으로 병합 진행
		// 각 pair의 left_child_node 재설정
		// 제외할 자식이 끝 번호인지에 따라 케이스 분류
		if(except == p.size()) {
			
			for(int i=p.size()-1; i>=0; i--) {
				Pair pair = p.get(i);
				right.insertPair(0, pair);
				
				// key 업데이트
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
				
				// key 업데이트
				updateKey(0, right, true);
			}
		}
		
		for(int i=0; i<=right.size(); i++) {
			
			Node child;
			if(i == right.size()) child = right.rightChild;
			else child = right.getPair(i).left_child_node;
		}
		
		// 부모와의 연결 끊기
		if(childIndex == parent.size()) parent.rightChild = null;
		else {
			Pair newPair = parent.getPair(childIndex);
			newPair.left_child_node = null;
			parent.p.set(childIndex, newPair);
		}
		// 같은 부모의 자식들 (현재 노드와 같은 depth) 자식 번호 재설정
		int cnt = 0;
		for(int i=0; i<=parent.size(); i++) {
			
			if(i == childIndex) continue;
			if(i == parent.size()) parent.rightChild.setChildIndex(cnt);
			else parent.getPair(i).left_child_node.setChildIndex(cnt++);
		}
		parent = null;
		
		// parent, childIndex 재설정
		for(int i=0; i<=right.size(); i++) {
			
			Node child;
			if(i == right.size()) child = right.rightChild;
			else child = right.getPair(i).left_child_node;
			
			child.setParent(right);
			child.setChildIndex(i);
		}
	}
	
	// (단말 노드용) 삭제되는 단말 노드에서 양쪽 Sibling을 연결
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
				
				// b 등록
				if(s.charAt(0) == 'b') {
					
					StringTokenizer st = new StringTokenizer(s);
					st.nextToken(); // 'b' 버림
					b = Integer.parseInt(st.nextToken());
					
					bPlusTree = new Tree(b);
				}
				
				// index.dat 등록
				Queue<Node> parentQueue = new LinkedList<>();	// 부모 노드를 담은 큐
				Queue<Node> currentQueue = new LinkedList<>();	// 현재 노드를 담은 큐
				
				while((s = br.readLine()) != null) {
					
					Queue<Node> tmp = new LinkedList<>();		// swap 용
					StringTokenizer st = new StringTokenizer(s);
					String type = st.nextToken();
					
					// 비단말 노드 처리
					if(type.equals("/")) {
						
						// root 노드는 비단말 노드
						bPlusTree.root.isLeaf = false;
						
						// root 등록
						if(parentQueue.size() == 0) {
							
							// root에 key pairs 등록
							while(st.hasMoreTokens()) {
								
								String ttmp = st.nextToken();
								
								Long key = Long.parseLong(ttmp);
								bPlusTree.root.insertPair(new Pair(key, null));
							}
							parentQueue.offer(bPlusTree.root); // root 삽입
							
						}
						// 비단말 노드 등록
						else {
							
							// currentQueue에 현재 노드들 관리
							Node curNode = new Node(false, b, 0, null, null, null, null);
							while(st.hasMoreTokens()) {
								
								String cur = st.nextToken();
								
								// 노드 구분자
								if(cur.equals("/")) {
									
									// 현재노드 큐 삽입
									currentQueue.offer(curNode);
									tmp.offer(curNode);
									curNode = new Node(false, b, 0, null, null, null, null);
								}
								// 현재 노드에 key pair 삽입
								else {
									
									Long key = Long.parseLong(cur);
									curNode.insertPair(new Pair(key, null));
								}
							}
							currentQueue.offer(curNode); // 마지막 노드 삽입
							tmp.offer(curNode);
							
							// 모든 부모 노드와 매칭될 때까지 진행
							while(parentQueue.size() > 0) {
								
								Node parent = parentQueue.poll();
								for(int childIndex=0; childIndex<=parent.size(); childIndex++) {
									
									curNode = currentQueue.poll();
									curNode.setParent(parent);
									curNode.setChildIndex(childIndex);
									
									// rightmost 자식 등록
									if(parent.size() == childIndex) 
										parent.rightChild = curNode;
									// left_child_node 등록
									else {
										
										Pair pair = parent.getPair(childIndex);
										pair.left_child_node = curNode;
										parent.p.set(childIndex, pair);
									}
								}
							}
							
							// 현재 노드들은 다음 순서에서 부모 노드가 됨
							parentQueue = tmp;
						}
					}
					// 단말 노드 처리
					else {
						
						// root 등록
						if(parentQueue.size() == 0) {
							
							// root에 key pairs 등록
							while(st.hasMoreTokens()) {
								
								Long key = Long.parseLong(st.nextToken());
								Long value = Long.parseLong(st.nextToken());
								ValuePointer valuePointer = new ValuePointer(value);
								bPlusTree.root.insertPair_(new Pair_(key, null));
							}
						}
						// 단말 노드가 root가 아니라면
						else {
						
							// currentQueue에 현재 노드들 관리
							Node curNode = new Node(true, b, 0, null, null, null, null);
							Node prev = null;
							while(st.hasMoreTokens()) {
								
								String cur = st.nextToken();
								
								// 노드 구분자
								if(cur.equals("#")) {
									
									// 현재노드 큐 삽입
									currentQueue.offer(curNode);
									
									// 이전 단말 노드와 연결
									if(prev != null) {
										
										prev.rightSibling = curNode;
										curNode.leftSibling = prev;
									}
									
									prev = curNode;
									curNode = new Node(true, b, 0, null, null, null, null);
								}
								// 현재 노드에 key pair 삽입
								else {
									
									Long key = Long.parseLong(cur);
									Long value = Long.parseLong(st.nextToken());
									ValuePointer valuePointer = new ValuePointer(value);
									curNode.insertPair_(new Pair_(key, valuePointer));
								}
							}
							currentQueue.offer(curNode); // 마지막 노드 삽입
							
							// 이전 단말 노드와 연결
							prev.rightSibling = curNode;
							curNode.leftSibling = prev;
							
							// 모든 부모 노드와 매칭될 때까지 진행
							while(parentQueue.size() > 0) {
								
								Node parent = parentQueue.poll();
								for(int childIndex=0; childIndex<=parent.size(); childIndex++) {
									
									curNode = currentQueue.poll();
									curNode.setParent(parent);
									curNode.setChildIndex(childIndex);
									
									// rightmost 자식 등록
									if(parent.size() == childIndex) 
										parent.rightChild = curNode;
									// left_child_node 등록
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
						
						// input data 처리
						StringTokenizer st = new StringTokenizer(string, ",", true);
						long key = Long.parseLong(st.nextToken());
						String tmp = st.nextToken();
						long value = Long.parseLong(st.nextToken());
						ValuePointer valuePointer = new ValuePointer(value);
						
						Pair_ newPair = new Pair_(key, valuePointer);
						
						SplitChildren ret = bPlusTree.insert(null, bPlusTree.root, newPair, -1, 0);
						
						// root에서 overflow 발생
						if(ret.left != null) {
							
							// root 재설정 및 자식 설정
							Node newRoot = new Node(false, b, 0, null, ret.right, null, null);
							newRoot.insertPair(new Pair(ret.addKey, ret.left));
							ret.left.parent = newRoot;
							ret.right.parent = newRoot;
							bPlusTree.root = newRoot;
						}
					}
					
					// index.dat 다시 쓰기
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
					
					// index.dat 다시 쓰기
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
