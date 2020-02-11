import org.com.NodeNew
import org.com.PropertiesNew

def call(String user, String hostname, String base_dir, Node jump_server){
	return new NodeNew(user, hostname, base_dir, jump_server)
}

Node createNodeObject(String nodeId, PropertiesNew properties){
	def jpNode = null
	nodeProperties = properties.getNodeProperties(nodeId)
	if (nodeProperties.get('JUMP_SERVER')) {
		jpId = node_properties.get('JUMP_SERVER')
		jpNode = this.createNodeObject(jpId, properties)
	}
	def user = node_properties.get('USER')
	def hostname = node_properties.get('HOSTNAME')
	def homeDir = node_properties.get('HOME_DIR')
	node = this.call(user, hostname, base_dir, jpNode)
	if (node_properties.get('RELEASE_BASE_DIR')) {
		node.releaseBaseDir = node_properties.get('RELEASE_BASE_DIR')
	}
	return node
}

Map createNodeObjectsFromList(nodeListId, properties){
	nodeIdList = properties.getNodeList(nodeListId)
	nodeObjects = [ : ]
	nodeIdList.each { nodeId ->
		node = this.createNodeObject(nodeId, properties)
		nodeObjects.put(nodeId, node)
	}
	return nodeObjects
}


return this