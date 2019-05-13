package pickapath.model;

public interface ModelListener {
	void updateModel(Model.Event event, Element object, boolean undoOrRedo);
}
