package net.modularmods.protogl.model;

import net.modularmods.protogl.gl.Mesh;
import net.modularmods.protogl.loader.data.ModelData;
import net.modularmods.protogl.loader.data.NodeData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class PModel extends Node {

    private ModelData modelData;

    public PModel(String nodeName, ModelData modelData) {
        super(nodeName);
        this.modelData = modelData;
        Map<String, Node> nodesMap = new HashMap<>();

        // Create all nodes
        createNodes(modelData, nodesMap);

        // Establish parent-child relationships
        establishHierarchy(modelData, nodesMap);
    }

    public Node getNode(String nodeName) {
        if (this.nodeName.equals(nodeName)) {
            return this;
        }
        return this.getAllChildren().stream().filter(node -> node.nodeName.equals(nodeName)).findFirst().orElse(null);
    }

    private void createNodes(ModelData modelData, Map<String, Node> nodesMap) {
        for(NodeData nodeData : modelData.getNodeData()) {
            Node node = createNodeFromData(nodeData);
            nodesMap.put(nodeData.getNodeName(), node);
        }
    }

    private Node createNodeFromData(NodeData nodeData) {
        Node node;
        if (nodeData.getMeshData() != null) {
            node = new MeshNode(nodeData.getNodeName());
            ((MeshNode) node).mesh = Mesh.load(nodeData.getMeshData());
        } else {
            node = new Node(nodeData.getNodeName());
        }
        node.position = new Vector3f(nodeData.getTranslation());
        node.rotation = new Quaternionf(nodeData.getRotation()[0], nodeData.getRotation()[1], nodeData.getRotation()[2], nodeData.getRotation()[3]);
        node.scale = new Vector3f(nodeData.getScale());
        return node;
    }

    private void establishHierarchy(ModelData modelData, Map<String, Node> nodesMap) {
        for (NodeData nodeData : modelData.getNodeData()) {
            Node node = nodesMap.get(nodeData.getNodeName());
            String parentName = nodeData.getParentNodeName();
            if (parentName != null && nodesMap.containsKey(parentName)) {
                nodesMap.get(parentName).addChild(node);
            } else {
                // If no parent specified or parent not found, add to root
                this.addChild(node);
            }
        }
    }
}
