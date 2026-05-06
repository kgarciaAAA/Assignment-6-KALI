package academics;

public enum Department {
    COMPUTER_SCIENCE, DATA_SCIENCE, MATHEMATICS;

    public static Department stringToDepartment(String department) {
        switch (department) {
            case "COMPUTER_SCIENCE" -> {
                return COMPUTER_SCIENCE;
            } case "DATA_SCIENCE" -> {
                return DATA_SCIENCE;
            } case "MATHEMATICS" -> {
                return MATHEMATICS;
            }
        
            default -> {
                throw new IllegalArgumentException("Cannot convert String to Department enum.");
            }
        }
    }
}
