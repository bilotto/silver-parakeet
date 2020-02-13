import org.com.File
import org.com.FileNew
import org.com.Node
import org.com.NodeNew
import org.com.GitNode

def call(String name, Node node, String directory) {
	file = new File()
	file.construct(name, node, directory)
	return file
}

def call(String name, String directory, NodeNew node) {
	FileNew file = new FileNew(name, directory, node)
	def cksumCommand = "cd ${file.directory}; cksum ${file.name}"
	file.cksum = node.executeAndGetOutput(cksumCommand)
	println file.cksum
	return file
}

def call(String name, String directory, GitNode gitNode) {
	node = nodeObject(gitNode.user, gitNode.hostname, gitNode.homeDir, gitNode.jumpServer)
	return this.call(name, directory, node)
}


def dump_map_to_file(map, filename){
	content = ''
	for (element in map) {
		content = content + "${element.key} -> ${element.value}\n"
	}
	writeFile(file: filename, text: content, encoding: "UTF-8")
	current_dir = sh (returnStdout: true, script: 'pwd').trim()
}