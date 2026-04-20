package games.cubi.locatables;

public interface StrictEquality {
    /**
     * For use with classes of interfaces such as {@link games.cubi.locatables.Locatable}, where the interface contract specifies that all implementations must use interface-defined <code>equals</code> and <code>hashcode</code> methods for interchangeability in data structures. In this case, the default {@link Object#equals(Object)} method is no longer able to confirm genuine equality, as it only checks for interface field equality.
     * <p>
     * This method should check for more typical equality, meaning that all fields of the object are equal, and the classes of the two objects are the same. This is useful for debugging and testing, as it allows us to confirm that two objects are genuinely equal, rather than just being interchangeable in data structures.
     * @param other The other object to compare to.
     * @return <code>True</code> if the two objects are
     * <p>- references to the same object, or
     * <p>- different objects of the same class with equal fields.
     * <p><code>False</code> otherwise.
     */
    boolean strictlyEquals(Object other);
}
