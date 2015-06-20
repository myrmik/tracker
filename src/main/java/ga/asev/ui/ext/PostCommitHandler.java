package ga.asev.ui.ext;

import com.vaadin.data.fieldgroup.FieldGroup;

import java.util.function.Consumer;

public class PostCommitHandler implements FieldGroup.CommitHandler {

    Consumer<FieldGroup.CommitEvent> consumer;

    public PostCommitHandler(Consumer<FieldGroup.CommitEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        consumer.accept(commitEvent);
    }
}
