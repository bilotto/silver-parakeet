import org.com.NodeNew
import org.com.PropertiesNew

def call(String user, String hostname, String base_dir, Node jump_server){
	return new NodeNew(user, hostname, base_dir, jump_server)
}

Node createNodeObject(String nodeId, PropertiesNew properties){
	def jpNode = null
	nodeProperties = properties.getNodeProperties(nodeId)
	if (nodeProperties.get('JUMP_SERVER')) {
		jpId = nodeProperties.get('JUMP_SERVER')
		jpNode = this.createNodeObject(jpId, properties)
	}
	def user = nodeProperties.get('USER')
	def hostname = nodeProperties.get('HOSTNAME')
	def homeDir = nodeProperties.get('HOME_DIR')
	node = this.call(user, hostname, base_dir, jpNode)
	if (nodeProperties.get('RELEASE_BASE_DIR')) {
		node.releaseBaseDir = nodeProperties.get('RELEASE_BASE_DIR')
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