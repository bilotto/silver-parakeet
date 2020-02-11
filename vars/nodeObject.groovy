import org.com.NodeNew
import org.com.PropertiesNew

def call(String user, String hostname, String homeDir, NodeNew jpNode){
	return new NodeNew(user, hostname, homeDir, jpNode)
}

NodeNew createNodeObject(String nodeId, PropertiesNew properties){
	def jpNode = null
	nodeProperties = properties.getNodeProperties(nodeId)
	if (nodeProperties.get('JUMP_SERVER')) {
		jpId = nodeProperties.get('JUMP_SERVER')
		jpNode = this.createNodeObject(jpId, properties)
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
	nodeObjects = [ : ]
	nodeIdList.each { nodeId ->
		nodeObjects.put(nodeId, this.createNodeObject(nodeId, properties))
	}
	return nodeObjects
}


return this