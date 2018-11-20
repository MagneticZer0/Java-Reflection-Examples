public class TrulyUninstantiable { // Everything here will be public or package public just to make things easier
    public String test; // Instance variable to test some things

    TrulyUninstantiable() { // You can try and instantiate this, you will get an exception though
        throw new UnsupportedOperationException();
    }
}
