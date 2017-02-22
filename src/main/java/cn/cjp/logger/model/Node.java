package cn.cjp.logger.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * bean节点
 * 
 * @author Jinpeng Cui
 *
 */
public class Node extends BaseModel implements AbstractModel {

	private static final Logger logger = Logger.getLogger(Node.class);

	public static final String CLASS = "bean.clazz";
	public static final String METHOD = "bean.method";
	public static final String PERIOD = "bean.period";
	public static final String AVGPERIOD = "bean.avgPeriod";
	public static final String CALLEDTIMES = "bean.calledTimes";
	public static final String RETURNLINENUM = "bean.returnLineNum";
	public static final String TIME = "bean.time";
	public static final String REMARKS = "bean.remarks";

	/**
	 * 父节点
	 */
	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Node parent;

	/**
	 * 子节点
	 */
	private List<Node> childs = new ArrayList<>();

	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Node source;

	private BeanInspectorModel bean;

	public Node() {
	}

	public Node(Node parent) {
		this.parent = parent;
		if (parent == null) {
			this.setSource(this);
		} else {
			this.setSource(parent.getSource());
		}
	}

	public String toTreeString() {
		return this.toTreeString(true, 1);
	}

	private String toTreeString(boolean isRoot, int deep) {
		StringBuilder s = new StringBuilder();
		if (isRoot) {
			s.append(this.getBean().getClazz());
		}
		List<Node> childs = this.getChilds();
		int length = childs.size();
		if (length > 0) {
			// 横向
			s.append("\t" + childs.get(0).getBean().getClazz());
			deep++;
			s.append(childs.get(0).toTreeString(false, deep));
			deep--;
			for (int i = 1; i < length; i++) {
				s.append("\r\n");
				s.append(repeat("|\t", deep));
				Node child = childs.get(i);
				s.append("-\t" + child.getBean().getClazz());
				deep++;
				s.append(child.toTreeString(false, deep));
				deep--;
			}
		}
		return s.toString();
	}

	private static String repeat(String s, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public void setBean(BeanInspectorModel bean) {
		this.bean = bean;
	}

	public BeanInspectorModel getBean() {
		return this.bean;
	}

	public List<Node> getChilds() {
		return this.childs;
	}

	public Node getParent() {
		return this.parent;
	}

	/**
	 * <pre>
	 * a -> b -> b -> b -> c
	 * +
	 * a -> b -> b -> c
	 * =
	 * a -> b -> b -> b -> c
	 *           | -> c
	 * </pre>
	 * 
	 * @param node
	 * @return the branch merged
	 */
	public Node addChild(Node branch) {
		Node mergedBranch = null;
		// 递归寻找相同节点，若找到，则调用次数+1，时间累加，若没找到，则视为新节点
		boolean found = false;
		for (Node rootChild : childs) {
			if (rootChild.getBean().getClazz().equals(branch.getBean().getClazz())) {
				found = true;
				rootChild.getBean().merge(branch.getBean());
				for (Node branchChild : branch.getChilds()) {
					mergedBranch = rootChild.addChild(branchChild);
				}
			}
		}
		if (!found) {
			childs.add(branch);
			branch.parent = this;
			mergedBranch = branch;
//			this.getBean().merge(branch.bean);
		}
		return mergedBranch;
	}

	/**
	 * 递归查找目标节点
	 * 
	 * @param destNode
	 *            目标节点实例
	 * @return null if not found
	 */
	@SuppressWarnings("unused")
	private static Node deepFind(Node sourceNode, Node destNode) {
		for (Node childNode : sourceNode.getChilds()) {
			if (childNode.equals(destNode)) {
				return childNode;
			}
			Node tempNode = deepFind(childNode, destNode);
			if (tempNode != null) {
				return tempNode;
			}
		}
		return null;
	}

	public DBObject toDBObject() {
		BasicDBObject dbo = new BasicDBObject();
		// add childs
		try {
			addChilds(this, dbo);
		} catch (Exception e) {
			logger.error(this, e);
		}
		return dbo;
	}

	public static Node fromDBObject(DBObject dbo) {
		Node node = new Node(null);
		DBObject beanDbo = (DBObject) dbo.get("bean");
		BeanInspectorModel beanModel = BeanInspectorModel.fromDBObject(beanDbo);
		node.setBean(beanModel);
		@SuppressWarnings("unchecked")
		List<DBObject> childDbos = (List<DBObject>) dbo.get("childs");
		if (childDbos != null) {
			for (DBObject childDbo : childDbos) {
				Node child = fromDBObject(childDbo);
				node.getChilds().add(child);
				child.parent = node;
			}
		}
		return node;
	}

	public static Node fromDoc(Document dbo) {
		Node node = new Node(null);
		if (dbo.get(ID) != null) {
			node.setId(dbo.getObjectId(ID).toString());
		}
		Document beanDbo = (Document) dbo.get("bean");
		BeanInspectorModel beanModel = BeanInspectorModel.fromDoc(beanDbo);
		node.setBean(beanModel);
		@SuppressWarnings("unchecked")
		List<Document> childDbos = (List<Document>) dbo.get("childs");
		if (childDbos != null) {
			for (Document childDbo : childDbos) {
				Node child = fromDoc(childDbo);
				node.getChilds().add(child);
				child.parent = node;
			}
		}
		return node;
	}

	private static void addChilds(Node node, BasicDBObject dbo) {
		dbo.append("bean", node.getBean().toDBObject());
		List<DBObject> childDbos = new ArrayList<>();
		for (Node childNode : node.getChilds()) {
			BasicDBObject childDbo = new BasicDBObject();
			childDbo.append("bean", childNode.getBean().toDBObject());
			if (!childNode.getChilds().isEmpty()) {
				try {
					addChilds(childNode, childDbo);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			childDbos.add(childDbo);
		}
		dbo.append("childs", childDbos);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"_id\":");
		sb.append(String.format("{\"%s\"", this.getId()));
		sb.append(",\"bean\":");
		sb.append(bean);
		sb.append(",\"childs\":[");
		for (int i = 0; i < this.childs.size(); i++) {
			if (i == 0) {
				sb.append(this.childs.get(i));
			} else {
				sb.append("," + this.childs.get(i));
			}
		}
		sb.append("]}");
		return sb.toString();
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof Node) {
			Node anotherNode = (Node) anObject;
			if (anotherNode == null || anotherNode.bean == null) {
				return false;
			}
			if (this.bean.getClazz().equals(anotherNode.bean.getClazz())
					&& this.bean.getMethod().equals(anotherNode.bean.getMethod())) {
				return true;
			}
		}

		return false;
	}

	public Document toDoc() {
		Document dbo = new Document();
		// add childs
		try {
			addChilds(this, dbo);
		} catch (Exception e) {
			logger.error(this, e);
		}
		return dbo;
	}

	private void addChilds(Node node, Document dbo) {
		if (!StringUtils.isEmpty(node.getId())) {
			dbo.put(ID, new ObjectId(node.getId()));
		}
		dbo.append("bean", node.getBean().toDoc());
		List<Document> childDbos = new ArrayList<>();
		for (Node childNode : node.getChilds()) {
			Document childDbo = new Document();
			childDbo.append("bean", childNode.getBean().toDBObject());
			if (!childNode.getChilds().isEmpty()) {
				try {
					addChilds(childNode, childDbo);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			childDbos.add(childDbo);
		}
		dbo.append("childs", childDbos);
	}

}
