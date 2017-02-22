package cn.cjp.logger.model;

public class NodeTest {

	static BeanInspectorModel genBean(String clazz) {
		BeanInspectorModel model = new BeanInspectorModel();
		model.setClazz(clazz);
		return model;
	}

	static Node genNode(String clazz) {
		Node node = new Node(null);
		node.setBean(genBean(clazz));
		return node;
	}

	static Node genRoot() {
		Node root = genNode("0");

		Node node1 = genNode("1");
		node1.getChilds().add(genNode("4"));
		root.getChilds().add(node1);

		Node node2 = genNode("2");
		node2.getChilds().add(genNode("5"));
		root.getChilds().add(node2);

		Node node3 = genNode("3");
		node3.getChilds().add(genNode("6"));
		root.getChilds().add(node3);

		return root;
	}

	static Node genBranch() {
		Node root = genNode("0");

		Node node1 = genNode("1");
		node1.getChilds().add(genNode("5"));
		root.getChilds().add(node1);

		Node node4 = genNode("4");
		root.getChilds().add(node4);

		Node node3 = genNode("3");
		Node node6 = genNode("6");
		node6.getChilds().add(genNode("7"));
		node3.getChilds().add(node6);
		root.getChilds().add(node3);

		return root;
	}

	/**
	 * 构建一个树形结构
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Node root = new Node(null);
		root.setBean(new BeanInspectorModel());
		root.getBean().setClazz("-1");
		root.getChilds().add(genRoot());
		System.out.println(root.toTreeString());
		System.out.println(root);

		System.out.println("");
		System.out.println("+");
		System.out.println("");
		Node branch = genBranch();
		System.out.println(branch.toTreeString());
		System.out.println(branch);

		root.addChild(branch);

		System.out.println("");
		System.out.println("=");
		System.out.println("");
		System.out.println(root.toTreeString());
		System.out.println(root);
	}
}
