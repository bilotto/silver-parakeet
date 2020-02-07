import org.foo.Cluster

def call(Map node_objects, String role){

	if (role != 'ACTIVE' && role != 'PASSIVE') {
		error "Invalid cluster role: ${role}"
	}

	node_list = []
	node_objects.each{ k, v -> node_list.add(v) }

	Cluster cluster = new Cluster()
	cluster.construct(node_list, role)
	return cluster
}