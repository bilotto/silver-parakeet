import org.com.Node
import org.com.Properties

def call(String user, String hostname, String base_dir, Node jump_server){
	Node node = new Node()
	node.construct(user, hostname, base_dir, jump_server)
	return node
}

def create_node_obj(Map node_properties, Properties properties){
	jp_node = null
	if (node_properties.get('JUMP_SERVER')) {
		jp_id = node_properties.get('JUMP_SERVER')
		jp_properties = properties.get_node_properties(jp_id)
		jp_node = this.create_node_obj(jp_properties, properties)
	}
	user = node_properties.get('USER')
	hostname = node_properties.get('HOSTNAME')
	base_dir = node_properties.get('RELEASE_BASE_DIR')
	node = this.call(user, hostname, base_dir, jp_node)
	return node
}

def create_node_object(node_properties, jump_server_objects){
	def user = node_properties.USER
	def hostname = node_properties.HOSTNAME
	def base_dir = node_properties.RELEASE_BASE_DIR
	def jp_id = node_properties.JUMP_SERVER
	if (jp_id) {
		jump_server = jump_server_objects.get(jp_id)
	} else {
		jump_server = null
	}
	return this.call(user, hostname, base_dir, jump_server)
}


def createNodeObjectsFromList(properties, nodeListId){
	nodeIdList = properties.get_list_of_nodes(nodeListId)

	jpObjects = [:]
	nodeObjects = [:]
	
	for (node_id in nodeIdList) {
		node_properties = properties.get_node_properties(node_id)
		println node_properties
		if (node_properties.get('JUMP_SERVER')) {
			jp_id = node_properties.JUMP_SERVER.toString()
			jp_properties = properties.get_node_properties(jp_id)
			if (!jpObjects.get(jp_id)) {
				jp_node = this.create_node_object(jp_properties, jpObjects)
				jpObjects.put(jp_id, jp_node)
			}
		}
		println jpObjects
		node = this.create_node_object(node_properties, jpObjects)
		nodeObjects.put(node_id, node)
	}
	return nodeObjects
}



return this