public class TestCase {

    private int secretID = 1337; // No one but this class should have access to this
    private String secretName; // Same as above
    private final long finalField = 12039L; // Should not be modifiable or accessible

    private TestCase(String secretName) { // Private constructor, should not be able to be accessed
        System.out.println("Instantiation of private class successful!");
        this.secretName = secretName;
    }

    private void setSecretID(int secretID) { // Private instance method
        this.secretID = secretID;
    }

    private class PrivateClass { // Private inner-class
        private PrivateClass() {
            System.out.println("Instantiation of private inner class successful!");
        }
    }
}
