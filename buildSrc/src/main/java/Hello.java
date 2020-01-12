import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class Hello extends DefaultTask {
    @TaskAction
    public void run() {
        System.out.println("Hello from task " + getPath() + "!");
    }
}
