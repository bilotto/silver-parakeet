import org.com.File
import org.com.FileNew
import org.com.Node
import org.com.NodeNew

def call(String name, Node node, String directory) {
	file = new File()
	file.construct(name, node, directory)
	return file
}

def call(String name, String directory, NodeNew node) {
	file = FileNew(name, directory, node)
	def cksumCommand = "cd ${file.directory}; cksum ${file.name}"
	file.cksum = node.executeAndGetOutput(cksumCommand)
	return file
}


def dump_map_to_file(map, filename){
	content = ''
	for (element in map) {
		content = content + "${element.key} -> ${element.value}\n"
	}
	writeFile(file: filename, text: content, encoding: "UTF-8")
	current_dir = sh (returnStdout: true, script: 'pwd').trim()
}