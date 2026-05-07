package data;

import java.io.IOException;

import storage.CourseStorage;
import storage.MajorStorage;
import storage.UserStorage;


public class FileHandlerFacade {
    private final CourseFileHandler courseFileHandler;
    private final SectionFileHandler sectionFileHandler;
    private final MajorFileHandler majorFileHandler;
    private final UserFileHandler userFileHandler;
    private final CourseStorage courseStorage;
    private final MajorStorage majorStorage;
    private final UserStorage userStorage;

    public FileHandlerFacade(CourseFileHandler courseFileHandler, SectionFileHandler sectionFileHandler, MajorFileHandler majorFileHandler,
         UserFileHandler userFileHandler, CourseStorage courseStorage, MajorStorage majorStorage, UserStorage userStorage) {
        this.courseFileHandler = courseFileHandler;
        this.sectionFileHandler = sectionFileHandler;
        this.majorFileHandler = majorFileHandler;
        this.userFileHandler = userFileHandler;
        this.courseStorage = courseStorage;
        this.majorStorage = majorStorage;
        this.userStorage = userStorage;
    }

    public void load() {
        try {
            courseFileHandler.readCoursesFromFile(courseStorage);
            sectionFileHandler.readSectionsFromFile(courseStorage);
            majorFileHandler.readMajorsFromFile(majorStorage, courseStorage);
            userFileHandler.readStudentUsersFromFile(userStorage, courseStorage);
            userFileHandler.readFacultyUsersFromFile(userStorage, courseStorage);
            userFileHandler.readAdminUsersFromFile(userStorage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            courseFileHandler.writeCoursesToFile(courseStorage);
            sectionFileHandler.writeSectionsToFile(courseStorage);
            majorFileHandler.writeMajorsToFile(majorStorage);
            userFileHandler.writeStudentUserToFile(userStorage);
            userFileHandler.writeFacultyUsersToFile(userStorage, courseStorage);
            userFileHandler.writeAdminUsersToFile(userStorage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
