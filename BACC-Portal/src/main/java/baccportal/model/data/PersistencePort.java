package baccportal.model.data;

// Used to flush state to storage, persist the changes done during program execution.
public interface PersistencePort {
    void saveUsers();

    void saveCourses();

    void saveSections();
}
