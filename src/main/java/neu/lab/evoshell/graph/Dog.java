package neu.lab.evoshell.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dog {
	private IGraph graph;
	protected String pos;
	protected List<String> route;

	protected Map<String, Cross> graphMap = new HashMap<String, Cross>();

	//one node may have multiple circle?
	protected Map<String, List<List<String>>> circleMap = new HashMap<String, List<List<String>>>();

	protected Map<String, IBook> books = new HashMap<String, IBook>();

	protected Map<String, IBook> tempBooks = new HashMap<String, IBook>();

	public Dog(IGraph graph) {
		this.graph = graph;
	}

	protected IBook buyNodeBook(String nodeName) {
		return graph.getNode(nodeName).getBook();
	}

	public Map<String, IBook> findRlt(Collection<String> entrys, int maxDep) {
		long start = System.currentTimeMillis();
		for (String mthd : entrys) {
			route = new ArrayList<String>();
			if (books.containsKey(mthd))
				continue;
			else {
				forward(mthd);
				while (pos != null) {
					if (needChildBook(maxDep)) {
						String frontNode = graphMap.get(pos).getBranch();
						getChildBook(frontNode);
					} else {
						back();
					}
				}
			}
		}
		return this.books;
	}

	public boolean needChildBook(int maxDep) {
		return graphMap.get(pos).hasBranch() && route.size() < maxDep;
		// return graphMap.get(pos).hasBranch();
	}

	private void getChildBook(String frontNode) {
		if (books.containsKey(frontNode)) {
			addChildBookInfo(frontNode, pos);
		} else {
			forward(frontNode);
		}

	}

	/**
	 * frontNode是一个手册没有完成的节点，需要为这个节点建立手册
	 * 
	 * @param frontNode
	 */
	private void forward(String frontNode) {
		// System.out.println("forward to " + frontNode);
		INode node = graph.getNode(frontNode);
		if (node != null) {
			if (!route.contains(frontNode)) {
				pos = frontNode;
				route.add(pos);
				IBook nodeRch = buyNodeBook(frontNode);
				this.tempBooks.put(frontNode, nodeRch);
				graphMap.put(pos, new Cross(node));
			} else {// have loop
				// nodes in circle don't include start and end.
				List<String> circle = new ArrayList<String>();
				int index = route.indexOf(frontNode) + 1;
				while (index < route.size()) {
					circle.add(route.get(index));
					index++;
				}
				addCircle(frontNode, circle);
			}
		}
	}

	private void addCircle(String node, List<String> circle) {
		List<List<String>> circles = this.circleMap.get(node);
		if (circles == null) {
			circles = new ArrayList<List<String>>();
		}
		circles.add(circle);
	}

	private void back() {
		String donePos = route.get(route.size() - 1);
		// System.out.println("back from " + donePos);
		graphMap.remove(donePos);

		IBook book = this.tempBooks.get(donePos);
		
		if (circleMap.containsKey(donePos)) {
			book.dealLoop(circleMap.get(donePos));
			circleMap.remove(donePos);
		}
		
		book.afterAddAllChildren();

		this.tempBooks.remove(donePos);
		this.books.put(donePos, book);



		route.remove(route.size() - 1);

		if (route.size() == 0) {
			pos = null;
		} else {
			pos = route.get(route.size() - 1);
			addChildBookInfo(donePos, pos);
		}
	}

	private void addChildBookInfo(String donePos, String pos) {
		IBook doneBook = this.books.get(donePos);
		IBook doingBook = this.tempBooks.get(pos);
		doingBook.addChild(doneBook);
	}

}
