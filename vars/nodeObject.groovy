import org.com.NodeNew
import org.com.PropertiesNew

NodeNew call(String user, String hostname, String homeDir, NodeNew jpNode){
	return new NodeNew(user, hostname, homeDir, jpNode, pipelineTools())
}

def getNodeProperty(nodeProperties, propertyName){
	if (nodeProperties.get(propertyName.toLowerCase())) {
		return nodeProperties.get(propertyName.toLowerCase())
	} else if (nodeProperties.get(propertyName.toUpperCase())) {
		return nodeProperties.get(propertyName.toUpperCase())
	} else if (nodeProperties.get(propertyName)) {
		return nodeProperties.get(propertyName)
	}
	return null
}


NodeNew createNodeObject(String nodeId, PropertiesNew properties){
	def jpNode = null
	def nodeProperties = properties.getNodeProperties(nodeId)
	log("DEBUG", "nodeId: ${nodeId} - nodeProperties: ${nodeProperties}")
	if (this.getNodeProperty(nodeProperties, 'jump_server')) {
		jpId = this.getNodeProperty(nodeProperties, 'jump_server')
		log("DEBUG", "jpId: ${jpId}")
		//todo: the variable jumpServerObjects below should be defined in the upper context
	    try {
	    	var = {jumpServerObjects}
	        var()
	    } catch (exc) {
	    	log("DEBUG", "I don't have a jumpServerObjects in the scope. Creating it")
	    	jumpServerObjects = [ : ]
	    }
		if (jumpServerObjects) {
			if (!jumpServerObjects.jpId) {
				jpNode = this.createNodeObject(jpId, properties)
				jumpServerObjects.put(jpId, jpNode)
			}
		} else {
			jpNode = this.createNodeObject(jpId, properties)
			jumpServerObjects.put(jpId, jpNode)
		}
		log("DEBUG", "jumpServerObjects: ${jumpServerObjects}")
		jpNode = jumpServerObjects.get(jpId)
		log("DEBUG", "jpId: ${jpId}")
		log("DEBUG", "jpNode: ${jpNode}")
	}
	def user = this.getNodeProperty(nodeProperties, 'user')
	def hostname = this.getNodeProperty(nodeProperties, 'hostname')
	def homeDir = this.getNodeProperty(nodeProperties, 'home_dir')
	node = this.call(user, hostname, homeDir, jpNode)
	if (this.getNodeProperty(nodeProperties, 'release_base_dir')) {
		node.releaseBaseDir = this.getNodeProperty(nodeProperties, 'release_base_dir')
	}
	return node
}

Map createNodeObjectsFromList(nodeListId, properties){
	def nodeObjects = [ : ]
	jumpServerObjects = [ : ]
	nodeIdList = properties.getNodeList(nodeListId)
	if (!nodeIdList.size()){
		log "${nodeListId} is probably a standalone node"
		nodeId = nodeListId
	    nodeIdList.add(nodeId)
	}
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

def defaultIfInexistent(varNameExpr, defaultValue) {
    try {
        varNameExpr()
    } catch (exc) {
    	log("DEBUG", "Returning default value ${defaultValue} to ${varNameExpr}")
        defaultValue
    }
}


return this