package org.com

class PropertiesNew {
	Map propertiesMap
	Map NODES
	Map NODE_LIST
	Map GIT_PROPERTIES
	PropertiesNew(propertiesMap) {
		this.propertiesMap = propertiesMap
		this.NODES = propertiesMap.NODES
		this.NODE_LIST = propertiesMap.NODE_LIST
		this.GIT_PROPERTIES = propertiesMap.GIT_PROPERTIES
	}
	
	Map getGitProperties(String projectName) {
		if (!this.GIT_PROPERTIES.get(projectName)) {
			error "Git project properties not found"
		}
		return this.GIT_PROPERTIES.get(projectName)                               
	}
	
	
	Map getNodeProperties(String nodeId) {
		for (entry in this.NODES) {
			for (node in entry.value) {
				if (nodeId == node.key) {
					return node.value
				}
			}
		}
		error "Node ${nodeId} not found"
	}
	
	
	List getNodeList(String nodeListId) {
		for (entry in this.NODE_LIST) {
			for (nodeList in entry.value) {
				if (nodeListId == nodeList.key) {
					return nodeList.value
				}
			}
		}
		error "Node list ${nodeListId} not found"
	}
}
