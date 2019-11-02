package core;

public interface Exporter {
    void add(Entity entity, Entity parent);

    void done();
}
