import org.com.Node
import org.com.Container

def call(String name, Node node, String from_image, String base_dir) {
	Container container = new Container()
	container.construct(name, node, from_image, base_dir)
	return container
}