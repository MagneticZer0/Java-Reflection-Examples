import java.lang.reflect.*;

import sun.misc.Unsafe;
import java.util.Arrays;

public class Reflector {
    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
        Constructor<TestCase> testCaseConstructor = TestCase.class.getDeclaredConstructor(String.class); // Throws NoSuchMethodException
        testCaseConstructor.setAccessible(true); // This makes it ignore private/public/protected flags
        TestCase testCase = testCaseConstructor.newInstance("Super secret name"); // Just instantiated even though it is private
        // Throws InstantiationException, IllegalAccessException, and InvocationTargetException

        Field privateID = TestCase.class.getDeclaredField("secretID"); // Throws NoSuchFieldException
        privateID.setAccessible(true); // Same as Constructor's setAccessible
        int privateIDValue = (int) privateID.get(testCase); // Gets the value of the private field
        System.out.println(String.format("Value of private field privateID: %d", privateIDValue));

        Field secretName = TestCase.class.getDeclaredField("secretName"); // Throws NoSuchFieldException
        secretName.setAccessible(true); // Same as above
        String secretNameValue = (String) secretName.get(testCase); // Gets the value of the private field
        System.out.println(String.format("Value of private field secretName: %s", secretNameValue));

        Method setSecretID = TestCase.class.getDeclaredMethod("setSecretID", int.class); // Throws NoSuchMethodException
        setSecretID.setAccessible(true); // Same as above
        setSecretID.invoke(testCase, 1338); // Invokes the private method

        // Make sure it worked by reprinting out the secretID field;
        System.out.println(String.format("Value of private field privateID: %d", privateID.get(testCase)));

        Class privateClass = TestCase.class.getDeclaredClasses()[0]; // We know that it only has 1 private inner class so, it's the first one.
        Constructor privateInnerClassConstructor = privateClass.getDeclaredConstructor(TestCase.class); // Constructors take superclass as param
        privateInnerClassConstructor.setAccessible(true); // Same as above
        privateInnerClassConstructor.newInstance(testCase); // Instantiate private inner-class

        Field finalField = TestCase.class.getDeclaredField("finalField");
        finalField.setAccessible(true); // Same as above
        System.out.println("Final field finalField is: " + finalField.get(testCase)); // Null because it is static

        // The next 3 lines essentially negates the final keyword, so that we can modify them.
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(finalField, finalField.getModifiers() & ~Modifier.FINAL);

        finalField.set(testCase, 1L); // This sets the value of a final field
        System.out.println("Final field finalField is: " + finalField.get(testCase));

        /**
         * Now that's cool and dandy, but what if we didn't know the names
         * of every method or field or params of the constructors?
         * Sure you can decompile the class and look that way, but there's other ways too
         */
        Constructor[] constructors = TestCase.class.getDeclaredConstructors();
        System.out.println(Arrays.toString(constructors)); // Prints access flags and constructors with params.

        Field[] fields = TestCase.class.getDeclaredFields();
        System.out.println(Arrays.toString(fields)); // Prints access flag, type, and name of all fields.

        Method[] methods = TestCase.class.getDeclaredMethods();
        System.out.println(Arrays.toString(methods)); // Prints access flag, return type, and method signatures of all methods.

        Class[] classes = TestCase.class.getDeclaredClasses();
        System.out.println(Arrays.toString(classes)); // Prints any inner-classes

        /**
         * But wait, let's get funky now. We've modified only instance variables
         * What about modifying static variables?
         * What if... we modify the Boolean class static variables...
         */
        Field falseField = Boolean.class.getField("FALSE"); // Gets the FALSE field
        falseField.setAccessible(true); // Makes it accessible

        // Again, next 3 negate finalness
        modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(falseField, falseField.getModifiers() & ~Modifier.FINAL);

        falseField.set(null, true); // Sets false to true

        System.out.println(String.format("false is %s", false)); // Because of autoboxing behavior this prints false is true

        // Integers are funny because they use a cache for various things
        Integer[] newCache = new Integer[256]; // Let's create a new cache
        Arrays.fill(newCache, 420); // And fill it with what we want
        Field cache = Class.forName("java.lang.Integer$IntegerCache").getDeclaredField("cache"); // Let's get the Integer cache field
        cache.setAccessible(true);
        
        // Again, next 3 negate finalness
        modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(cache, cache.getModifiers() & ~Modifier.FINAL);
        
        cache.set(null, newCache); // Let's set the value
        System.out.printf("6 * 9 = %d", 6*9); // You would expect this to return 54, although by now you know better, it'll return 420.
        
        /**
         * Lastly what if a programmer purposely makes the constructor throw an Exception?
         * It's hard to instantiate that object.. But Java's sun.music.Unsafe allows this to happen
         * Although it's not supported, so we have to access it via Reflection!
         */
        // The next 3 lines are to create an Unsafe object
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null); // You can use Unsafe.getUnsafe(), that may throw a SecurityException though

        TrulyUninstantiable isItReally = (TrulyUninstantiable) unsafe.allocateInstance(TrulyUninstantiable.class); // Creates an instance of TrulyUninstantiable
        isItReally.test = "As we see, it worked"; // Instance variables are null
        System.out.println(isItReally.test); // See if it worked
    }
}
