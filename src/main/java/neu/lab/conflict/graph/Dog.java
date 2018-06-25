package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.util.MavenUtil;


public class Dog {
	private Graph graph;
	protected String pos;
	protected List<String> route;

	protected Map<String, Cross> graphMap = new HashMap<String, Cross>();

	protected Map<String, List<String>> circleMap = new HashMap<String, List<String>>();
																						
	protected Map<String, Book> books = new HashMap<String, Book>();


	protected Map<String, Book> tempBooks = new HashMap<String, Book>();

	public Dog(Graph graph) {
		this.graph = graph;
	}

	protected Book buyNodeBook(String nodeName) {
		return new Book(graph.getNode(nodeName));
	}

	public Map<String, Book> findRlt(Set<String> entrys) {
		long start = System.currentTimeMillis();
		for (String mthd : entrys) {
			route = new ArrayList<String>();
			if (books.containsKey(mthd))
				continue;
			else {
				forward(mthd);
				while (pos != null) {
					if (needChildBook()) {
						String frontNode = graphMap.get(pos).getBranch();
						getChildBook(frontNode);
					} else {
						back();
					}
				}
			}
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		MavenUtil.i().getLog().info("dog run time:" + runtime);
		return this.books;
	}

	public boolean needChildBook() {
		return graphMap.get(pos).hasBranch() && route.size() < Conf.PATH_DEP;
		// return graphMap.get(pos).hasBranch();
	}

	private void getChildBook(String frontNode) {
		if (books.containsKey(frontNode)) {
			addChildBook(frontNode, pos);
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
		Node node = graph.getNode(frontNode);
		if (node != null) {
			if (!route.contains(frontNode)) {
				pos = frontNode;
				route.add(pos);
				Book nodeRch = buyNodeBook(frontNode);
				this.tempBooks.put(frontNode, nodeRch);
				graphMap.put(pos, new Cross(node));
			} else {
				List<String> circle = new ArrayList<String>();
				int index = route.indexOf(frontNode) + 1;
				while (index < route.size()) {
					circle.add(route.get(index));
					index++;
				}
				this.circleMap.put(frontNode, circle);
			}
		}
	}

	private void back() {
		String donePos = route.get(route.size() - 1);
		graphMap.remove(donePos);


		Book book = this.tempBooks.get(donePos);
		book.addSelf();

		this.tempBooks.remove(donePos);
		this.books.put(donePos, book);

		if (circleMap.containsKey(donePos)) {

			dealLoopNd(donePos);
			circleMap.remove(donePos);
		}

		route.remove(route.size() - 1);

		if (route.size() == 0) {
			pos = null;
		} else {
			pos = route.get(route.size() - 1);
			addChildBook(donePos, pos);
		}
	}

	private void addChildBook(String donePos, String pos) {
		Book doneBook = this.books.get(donePos);
		Book doingBook = this.tempBooks.get(pos);
		doingBook.addChild(doneBook);
	}

	protected void dealLoopNd(String donePos) {
		// TODO
	}

}
