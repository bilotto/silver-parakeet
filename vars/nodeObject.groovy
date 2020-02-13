import org.com.NodeNew
import org.com.PropertiesNew

NodeNew call(String user, String hostname, String homeDir, NodeNew jpNode){
	return new NodeNew(user, hostname, homeDir, jpNode, tools)
}

NodeNew createNodeObject(String nodeId, PropertiesNew properties){
	def jpNode = null
	def nodeProperties = properties.getNodeProperties(nodeId)
	if (nodeProperties.get('JUMP_SERVER')) {
		jpId = nodeProperties.get('JUMP_SERVER')
		if (env.jpObjects) {
			if (!env.jpObjects.jpId) {
				jpNode = this.createNodeObject(jpId, properties)
				env.jpObjects.put(jpId, jpNode)
			}
			jpNode = env.jpObjects.jpId
		} else {
			jpNode = this.createNodeObject(jpId, properties)
		}
	}
	def user = nodeProperties.get('USER')
	def hostname = nodeProperties.get('HOSTNAME')
	def homeDir = nodeProperties.get('HOME_DIR')
	node = this.call(user, hostname, homeDir, jpNode)
	if (nodeProperties.get('RELEASE_BASE_DIR')) {
		node.releaseBaseDir = nodeProperties.get('RELEASE_BASE_DIR')
	}
	return node
}

Map createNodeObjectsFromList(nodeListId, properties){
	nodeIdList = properties.getNodeList(nodeListId)
	println nodeIdList
	nodeObjects = [ : ]
	nodeIdList.each { nodeId ->
		println nodeId
		node = this.createNodeObject(nodeId, properties)
		println node
		nodeObjects.put(nodeId, node)
	}
	return nodeObjects
}

Boolean isTheSameNode(NodeNew node1, NodeNew node2) {
	if (node1 != node2) {
		if ( node1.user != node2.user || node1.hostname != node2.hostname ) {
			return false
		}
	}
	return true
}


return this